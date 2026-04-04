<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class SettingsController extends Controller
{
    public function show(Request $request): JsonResponse
    {
        $user = $request->user();

        return response()->json([
            'settings' => [
                'currency' => $user->currency,
                'dark_mode' => (bool) $user->dark_mode,
            ],
        ]);
    }

    public function update(Request $request): JsonResponse
    {
        $payload = $request->validate([
            'name' => ['nullable', 'string', 'max:255'],
            'currency' => ['nullable', 'string', 'max:10'],
            'dark_mode' => ['nullable', 'boolean'],
        ]);

        $user = $request->user();

        if (array_key_exists('name', $payload)) {
            $user->name = $payload['name'];
        }

        if (array_key_exists('currency', $payload)) {
            $user->currency = strtoupper($payload['currency']);
        }

        if (array_key_exists('dark_mode', $payload)) {
            $user->dark_mode = (bool) $payload['dark_mode'];
        }

        $user->save();

        return response()->json([
            'message' => 'Pengaturan diperbarui',
            'user' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
                'currency' => $user->currency,
                'dark_mode' => (bool) $user->dark_mode,
            ],
        ]);
    }
}
