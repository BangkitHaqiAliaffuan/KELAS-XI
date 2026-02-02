<?php

namespace App\Http\Controllers;

use App\Models\Society;
use App\Models\Validation;
use Illuminate\Http\Request;

class ValidationController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        //
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create(Request $request)
    {
        $society = Society::where('login_tokens', $request->bearerToken())->first();

        $exist = $society->where('society_id', $society->id)->exists();

        if ($society || $exist) {
            return response()->json([
                'message' => 'unauthorized'
            ]);
        }

        $validation = Validation::create([
            'society_id' => $society->id,
            'job_description' => $request->job_description,
            'income' => $request->income,
            'reason_accepted' => $request->reason_accepted,
        ]);

        return response()->json([
            'message' => 'done',
            'data' => $validation
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
    public function show(Validation $validation, Request $request)
    {
        $token = $request->bearerToken();

        $society = Society::where('login_tokens', $token)->first();
        
        return response()->json([
            'data' => $society,
            'messagee' => 'done'
        ]);


    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Validation $validation)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Validation $validation)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Validation $validation)
    {
        //
    }
}
