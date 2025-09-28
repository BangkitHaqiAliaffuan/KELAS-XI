<?php

namespace App\Http\Middleware;

use App\Models\Post;
use Closure;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Symfony\Component\HttpFoundation\Response;

class PemilikPostingan
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        // Get post ID from route parameter
        $postId = $request->route('id');

        // Find the post
        $post = Post::findOrFail($postId);

        // Check if the authenticated user is the author of the post
        if ($post->author !== Auth::id()) {
            return response()->json([
                'message' => 'Data not found'
            ], 404);
        }

        return $next($request);
    }
}
