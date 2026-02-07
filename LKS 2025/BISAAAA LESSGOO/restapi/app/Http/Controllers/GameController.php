<?php

namespace App\Http\Controllers;

use App\Models\Game;
use Illuminate\Http\Request;
use Illuminate\Support\Str;

class GameController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {

        $sortDir = $request->get('sortDir', 'asc');
        $sortBy = $request->get('sortBy', 'title');
        $size = $request->get('size', 10);

        $sortColumn = match ($sortBy) {
            'popular' => 'views',
            'uploaddate' => 'created_at',
            default => 'title'
        };

        $game = Game::orderBy($sortColumn, $sortDir)->paginate($size);

        return response()->json([
            'total_elements' => $game->count(),
            'size' => $size,
            'page' => $game->currentPage() - 1,
            'data' => $game,
        ]);

    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $validator = Validator::make($request->all(), [
            'title' => 'required|min:3',
            'description' => 'required|max:200'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => $validator->errors()
            ]);
        }

        $game = Game::create([
            'title' => $request->title,
            'description' => $request->description,
            'slug' => Str::slug($request->title)
        ]);

        return response()->json([
            'message' => 'done min',
            'data' => $game
        ]);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     */
    public function show(Game $game, $slug)
    {
        $game = Game::where('slug', $slug)->first();

        return response()->json([
            'data' => $game
        ]);
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Game $game)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Game $game)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Game $game)
    {
        //
    }
}
