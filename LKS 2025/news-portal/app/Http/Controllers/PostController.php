<?php

namespace App\Http\Controllers;

use App\Models\Post;
use App\Http\Resources\PostResource;
use App\Http\Resources\PostDetailResource;
use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\AnonymousResourceCollection;

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
        // Ambil post dengan Eager Loading relationship 'writer'
        $post = Post::with('writer')->findOrFail($id);

        // Gunakan new PostDetailResource untuk hasil berupa objek tunggal
        return new PostDetailResource($post);
    }
}
