<?php

namespace App\Http\Middleware;

use App\Models\Comment;
use Closure;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Symfony\Component\HttpFoundation\Response;

class PemilikKomentar
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        // Get comment ID from route parameter
        $commentId = $request->route('id');

        // Find the comment
        $comment = Comment::findOrFail($commentId);

        // Check if the authenticated user is the owner of the comment
        if ($comment->user_id !== Auth::id()) {
            return response()->json([
                'message' => 'Data not found'
            ], 404);
        }

        return $next($request);
    }
}
