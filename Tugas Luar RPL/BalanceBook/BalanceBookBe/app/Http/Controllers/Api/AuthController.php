<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;

class AuthController extends Controller
{
    public function register(Request $request): JsonResponse
    {
        $payload = $request->validate([
            'name' => ['required', 'string', 'max:255'],
            'email' => ['required', 'email', 'max:255', 'unique:users,email'],
            'password' => ['required', 'string', 'min:8', 'confirmed'],
        ]);

        $user = User::query()->create([
            'name' => $payload['name'],
            'email' => $payload['email'],
            'password' => $payload['password'],
        ]);

        $token = $this->issueToken($user);

        return response()->json([
            'message' => 'Register berhasil',
            'token' => $token,
            'user' => $this->userPayload($user),
        ], 201);
    }

    public function login(Request $request): JsonResponse
    {
        $payload = $request->validate([
            'email' => ['required', 'email'],
            'password' => ['required', 'string'],
        ]);

        $user = User::query()->where('email', $payload['email'])->first();

        if (! $user || ! Hash::check($payload['password'], $user->password)) {
            return response()->json([
                'message' => 'Email atau kata sandi salah',
            ], 422);
        }

        $token = $this->issueToken($user);

        return response()->json([
            'message' => 'Login berhasil',
            'token' => $token,
            'user' => $this->userPayload($user),
        ]);
    }

    public function googleLogin(Request $request): JsonResponse
    {
        $payload = $request->validate([
            'id_token' => ['required', 'string'],
        ]);

        $tokenInfoResponse = Http::timeout(15)->get('https://oauth2.googleapis.com/tokeninfo', [
            'id_token' => $payload['id_token'],
        ]);

        if (! $tokenInfoResponse->ok()) {
            return response()->json([
                'message' => 'Token Google tidak valid',
            ], 422);
        }

        $tokenInfo = $tokenInfoResponse->json();
        $googleClientId = config('services.google.client_id');

        if (! empty($googleClientId) && ($tokenInfo['aud'] ?? null) !== $googleClientId) {
            return response()->json([
                'message' => 'Client Google tidak sesuai',
            ], 422);
        }

        if (($tokenInfo['email_verified'] ?? 'false') !== 'true') {
            return response()->json([
                'message' => 'Email Google belum terverifikasi',
            ], 422);
        }

        $email = $tokenInfo['email'] ?? null;
        if (! $email) {
            return response()->json([
                'message' => 'Email dari Google tidak ditemukan',
            ], 422);
        }

        $name = trim((string) ($tokenInfo['name'] ?? ''));

        $user = User::query()->firstOrCreate(
            ['email' => $email],
            [
                'name' => $name !== '' ? $name : Str::before($email, '@'),
                'password' => Str::random(40),
            ]
        );

        if ($name !== '' && $user->name !== $name) {
            $user->name = $name;
            $user->save();
        }

        $token = $this->issueToken($user);

        return response()->json([
            'message' => 'Login Google berhasil',
            'token' => $token,
            'user' => $this->userPayload($user),
        ]);
    }

    public function me(Request $request): JsonResponse
    {
        return response()->json([
            'user' => $this->userPayload($request->user()),
        ]);
    }

    public function logout(Request $request): JsonResponse
    {
        $user = $request->user();
        $user->api_token = null;
        $user->save();

        return response()->json([
            'message' => 'Logout berhasil',
        ]);
    }

    private function issueToken(User $user): string
    {
        $plainToken = Str::random(64);
        $user->api_token = hash('sha256', $plainToken);
        $user->save();

        return $plainToken;
    }

    private function userPayload(User $user): array
    {
        return [
            'id' => $user->id,
            'name' => $user->name,
            'email' => $user->email,
            'currency' => $user->currency,
            'dark_mode' => (bool) $user->dark_mode,
        ];
    }
}
