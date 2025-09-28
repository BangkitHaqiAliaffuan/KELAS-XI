<?php

namespace App\Http\Controllers;

use App\Models\Post;
use App\Http\Resources\PostResource;
use App\Http\Resources\PostDetailResource;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Resources\Json\AnonymousResourceCollection;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Validator;

class PostController extends Controller
{
    /**
     * Display a listing of all posts.
     *
     * @return AnonymousResourceCollection
     */
    public function index(): AnonymousResourceCollection
    {
        // Ambil semua post dari database
        $posts = Post::all();

        // Gunakan PostResource::collection untuk hasil berupa array/list
        return PostResource::collection($posts);
    }

    /**
     * Display the specified post with writer relationship.
     *
     * @param string $id
     * @return PostDetailResource
     */
    public function show(string $id): PostDetailResource
    {
        // Ambil post dengan Eager Loading relationship 'writer' dan 'comments' dengan 'user'
        $post = Post::with(['writer', 'comments.user'])->findOrFail($id);

        // Gunakan new PostDetailResource untuk hasil berupa objek tunggal
        return new PostDetailResource($post);
    }

    /**
     * Store a newly created post in storage.
     *
     * @param Request $request
     * @return JsonResponse
     */
    public function store(Request $request): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'title' => 'required|string|max:255',
            'news_content' => 'required|string',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $post = Post::create([
            'title' => $request->title,
            'news_content' => $request->news_content,
            'author' => Auth::user()->id, // Otomatis mengisi dengan ID user yang login
        ]);

        // Load writer relationship untuk response
        $post->load('writer');

        return response()->json([
            'message' => 'Post created successfully',
            'data' => new PostDetailResource($post)
        ], 201);
    }

    /**
     * Update the specified post in storage.
     *
     * @param Request $request
     * @param string $id
     * @return JsonResponse
     */
    public function update(Request $request, string $id): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'title' => 'required|string|max:255',
            'news_content' => 'required|string',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $post = Post::findOrFail($id);

        $post->update([
            'title' => $request->title,
            'news_content' => $request->news_content,
        ]);

        // Load writer relationship untuk response
        $post->load('writer');

        return response()->json([
            'message' => 'Post updated successfully',
            'data' => new PostDetailResource($post)
        ], 200);
    }

    /**
     * Remove the specified post from storage (Soft Delete).
     *
     * @param string $id
     * @return JsonResponse
     */
    public function destroy(string $id): JsonResponse
    {
        $post = Post::findOrFail($id);

        // Soft delete the post
        $post->delete();

        return response()->json([
            'message' => 'Post deleted successfully'
        ], 200);
    }
}
