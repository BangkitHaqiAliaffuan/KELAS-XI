<?php

namespace App\Http\Middleware;

use App\Models\Admin;
use App\Models\User;
use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class RoleMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     */
    public function handle(Request $request, Closure $next, $roles): Response
    {
        $token = $request->bearerToken();

        $user = User::where('remember_token', $token)->first();

        if($user && $user->role === $roles){
            return $next($request);
        } else{
            return response()->json([
                'message' => "you're not allowed"
            ]);
        }

    }
}
