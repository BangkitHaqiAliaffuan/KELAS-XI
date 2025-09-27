<?php

namespace App\Http\Controllers;

use App\Models\Post;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class PostController extends Controller
{
    /**
     * Display a listing of all posts.
     *
     * @return JsonResponse
     */
    public function index(): JsonResponse
    {
        $posts = Post::all();

        return response()->json($posts, 200);
    }
}
