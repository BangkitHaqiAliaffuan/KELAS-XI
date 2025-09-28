<?php

namespace App\Http\Controllers;

use App\Models\Comment;
use App\Http\Resources\CommentResource;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Validator;

class CommentController extends Controller
{
    /**
     * Store a newly created comment in storage.
     *
     * @param Request $request
     * @return JsonResponse
     */
    public function store(Request $request): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'post_id' => 'required|exists:posts,id',
            'comment_content' => 'required|string',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $comment = Comment::create([
            'post_id' => $request->post_id,
            'user_id' => Auth::user()->id, // Otomatis mengisi dengan ID user yang login
            'comment_content' => $request->comment_content,
        ]);

        // Load user relationship untuk response
        $comment->load('user');

        return response()->json([
            'message' => 'Comment created successfully',
            'data' => new CommentResource($comment)
        ], 201);
    }

    /**
     * Update the specified comment in storage.
     *
     * @param Request $request
     * @param string $id
     * @return JsonResponse
     */
    public function update(Request $request, string $id): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'comment_content' => 'required|string',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $comment = Comment::findOrFail($id);

        $comment->update([
            'comment_content' => $request->comment_content,
        ]);

        // Load user relationship untuk response
        $comment->load('user');

        return response()->json([
            'message' => 'Comment updated successfully',
            'data' => new CommentResource($comment)
        ], 200);
    }

    /**
     * Remove the specified comment from storage (Soft Delete).
     *
     * @param string $id
     * @return JsonResponse
     */
    public function destroy(string $id): JsonResponse
    {
        $comment = Comment::findOrFail($id);

        // Soft delete the comment
        $comment->delete();

        return response()->json([
            'message' => 'Comment deleted successfully'
        ], 200);
    }
}
