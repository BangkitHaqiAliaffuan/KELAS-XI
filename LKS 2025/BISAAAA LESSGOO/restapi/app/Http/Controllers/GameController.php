<?php

namespace App\Http\Controllers;

use App\Models\Game;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
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
    public function store(Request $request, $slug)
    {

        $game = Game::where('slug', $slug)->first();

        $path = $request->file('zipFile')->store('uploads');

    }

    /**
     * Display the specified resource.
     */
    public function show(Game $game, $slug)
    {
        $game = Game::where('slug', $slug)->with('latestVersion', 'user')->withSum('scores', 'score')->first();

        return response()->json([
            'data' => [
                'slug' => $game->slug,
                'title' => $game->title,
                'description' => $game->description,
                'thumbnail' => $game->description ? $game->description : null,
                'uploadTimeStamp' => $game->created_at,
                'author' => $game->user->username,
                'scoreCount' => $game->scores_sum_score,
                'gamePath' =>$game->latestVersion[0]->storage_path,
            ],
            'real-data' => $game
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
