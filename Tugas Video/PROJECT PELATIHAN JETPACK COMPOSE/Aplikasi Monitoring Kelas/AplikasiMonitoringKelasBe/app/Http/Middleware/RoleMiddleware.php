<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class RoleMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     * @param  string  $roles
     */
    public function handle(Request $request, Closure $next, string $roles): Response
    {
        if (!$request->user()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized'
            ], 401);
        }

        $allowedRoles = explode(',', $roles);

        if (!in_array($request->user()->role, $allowedRoles)) {
            return response()->json([
                'success' => false,
                'message' => 'Forbidden. Insufficient role permissions.'
            ], 403);
        }

        return $next($request);
    }
}
