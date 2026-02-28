<?php

namespace App\Http\Controllers;

use App\Models\Courier;
use App\Models\User;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\Rules\Password;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    // ─────────────────────────────────────────────────────────────
    // POST /api/auth/register
    // Body: { name, email, phone?, password, password_confirmation }
    // ─────────────────────────────────────────────────────────────
    public function register(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'name'     => ['required', 'string', 'max:255'],
            'email'    => ['required', 'string', 'email', 'max:255', 'unique:users'],
            'phone'    => ['nullable', 'string', 'max:20'],
            'password' => ['required', 'confirmed', Password::min(8)],
        ]);

        $user = User::create([
            'name'     => $validated['name'],
            'email'    => $validated['email'],
            'phone'    => $validated['phone'] ?? null,
            'password' => Hash::make($validated['password']),
        ]);

        $token = $user->createToken('mobile')->plainTextToken;

        return response()->json([
            'message' => 'Registrasi berhasil.',
            'token'   => $token,
            'user'    => $this->userResource($user),
        ], 201);
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/auth/login
    // Body: { email, password }
    // Tries User table first; if not found, tries Courier table.
    // Response includes `role` field: "user" | "courier"
    // ─────────────────────────────────────────────────────────────
    public function login(Request $request): JsonResponse
    {
        $request->validate([
            'email'    => ['required', 'email'],
            'password' => ['required', 'string'],
        ]);

        // ── 1. Try user table ─────────────────────────────────────
        $user = User::where('email', $request->email)->first();

        if ($user && Hash::check($request->password, $user->password)) {
            $user->tokens()->where('name', 'mobile')->delete();
            $token = $user->createToken('mobile')->plainTextToken;

            return response()->json([
                'message' => 'Login berhasil.',
                'role'    => 'user',
                'token'   => $token,
                'user'    => $this->userResource($user),
            ]);
        }

        // ── 2. Fallback: try courier table ────────────────────────
        $courier = Courier::where('email', $request->email)->first();

        if ($courier && Hash::check($request->password, $courier->password)) {
            // Couriers use Laravel Sanctum via HasApiTokens too — add the trait to Courier model
            $token = $courier->createToken('mobile')->plainTextToken;

            return response()->json([
                'message' => 'Login berhasil.',
                'role'    => 'courier',
                'token'   => $token,
                'courier' => $this->courierResource($courier),
            ]);
        }

        // ── 3. Neither matched ────────────────────────────────────
        throw ValidationException::withMessages([
            'email' => ['Email atau password salah.'],
        ]);
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/auth/logout   (requires Bearer token)
    // ─────────────────────────────────────────────────────────────
    public function logout(Request $request): JsonResponse
    {
        // Revoke only the token used for this request
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Logout berhasil.',
        ]);
    }

    // ─────────────────────────────────────────────────────────────
    // GET /api/auth/me   (requires Bearer token)
    // ─────────────────────────────────────────────────────────────
    public function me(Request $request): JsonResponse
    {
        return response()->json([
            'user' => $this->userResource($request->user()),
        ]);
    }

    // ─────────────────────────────────────────────────────────────
    // Private helper — normalise user shape for the mobile app
    // ─────────────────────────────────────────────────────────────
    private function userResource(User $user): array
    {
        return [
            'id'            => $user->id,
            'name'          => $user->name,
            'email'         => $user->email,
            'phone'         => $user->phone,
            'avatar_path'   => $user->avatar_path,
            'total_pickups' => $user->total_pickups,
            'items_sold'    => $user->items_sold,
            'co2_saved'     => $user->co2_saved,
            'points_balance'=> $user->points_balance,
            'member_since'  => $user->created_at?->translatedFormat('F Y') ?? '-',
        ];
    }

    // ─────────────────────────────────────────────────────────────
    // Private helper — normalise courier shape for the mobile app
    // ─────────────────────────────────────────────────────────────
    private function courierResource(Courier $courier): array
    {
        return [
            'id'               => $courier->id,
            'name'             => $courier->name,
            'email'            => $courier->email,
            'phone'            => $courier->phone,
            'avatar_path'      => $courier->avatar_path,
            'vehicle_type'     => $courier->vehicle_type,
            'vehicle_plate'    => $courier->vehicle_plate,
            'status'           => $courier->status,
            'is_available'     => (bool) $courier->is_available,
            'rating'           => (float) $courier->rating,
            'total_deliveries' => (int) $courier->total_deliveries,
        ];
    }
}
