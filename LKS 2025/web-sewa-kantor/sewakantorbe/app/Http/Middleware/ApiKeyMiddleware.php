<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class ApiKeyMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        $apiKey = $request->header('X-API-Key') ?? $request->query('api_key');

        if (!$apiKey) {
            return response()->json([
                'success' => false,
                'message' => 'API Key is required'
            ], 401);
        }

        $validApiKey = \App\Models\ApiKey::where('key', $apiKey)
            ->where('is_active', true)
            ->first();

        if (!$validApiKey || !$validApiKey->isActive()) {
            return response()->json([
                'success' => false,
                'message' => 'Invalid or expired API Key'
            ], 401);
        }

        // Mark API key as used
        $validApiKey->markAsUsed();

        return $next($request);
    }
}
