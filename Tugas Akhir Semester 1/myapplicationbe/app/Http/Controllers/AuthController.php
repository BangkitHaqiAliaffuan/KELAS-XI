<?php

namespace App\Http\Controllers;

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
    // ─────────────────────────────────────────────────────────────
    public function login(Request $request): JsonResponse
    {
        $request->validate([
            'email'    => ['required', 'email'],
            'password' => ['required', 'string'],
        ]);

        $user = User::where('email', $request->email)->first();

        if (! $user || ! Hash::check($request->password, $user->password)) {
            throw ValidationException::withMessages([
                'email' => ['Email atau password salah.'],
            ]);
        }

        // Revoke previous tokens from this device (optional — keeps it clean)
        $user->tokens()->where('name', 'mobile')->delete();

        $token = $user->createToken('mobile')->plainTextToken;

        return response()->json([
            'message' => 'Login berhasil.',
            'token'   => $token,
            'user'    => $this->userResource($user),
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
}
