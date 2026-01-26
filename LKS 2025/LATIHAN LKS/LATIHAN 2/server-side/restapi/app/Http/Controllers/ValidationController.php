<?php

namespace App\Http\Controllers;

use App\Models\Societies;
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
        $token = $request->bearerToken();


        if (!$token) {
            return response()->json([
                'pesan' => 'no token'
            ]);
        }

        $societies = Societies::where('login_tokens', $token)->first();

        if (!$societies) {
            return response()->json([
                'pesan' => 'no user'
            ]);
        }

        $data = Validation::create([
            'society_id' => $societies->id,
            'job_category_id' => $request->job_category_id,
            'work_experience' => $request->work_experience,
            'reason_accepted' => $request->reason_accepted,
            'job_position' => $request->job_position,
        ]);

        return response()->json([
            'data' => $data,
            'pesan' => 'bisa'
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
    public function show(Request $request)
    {
        $token = $request->bearerToken();

        if (!$token) {
            return response()->json([
                'pesan' => 'no token'
            ]);
        }
        $societies = Societies::where('login_tokens', $token)->first();

        if (!$societies) {
            return response()->json([
                'pesan' => 'no user'
            ]);
        }

        $validation = Validation::where('society_id', $societies->id)->with('category')->orderBy('created_at', 'desc')->limit(2)->get();

        // $result = [];

        // foreach($validation as $val){
        //     $result[] = [
        //         'work_experience' => $validation->work_experience,
        //         'reason_accepted'=>$validation->reason_accepted,
        //         'job_category'=>$validation->category,
        //         'job_position'=>$validation->job_position,
        //         'status'=>$validation->status,
        //     ];
        // }

        return response()->json([
            'pesam'=> 'bisa jir',
            'data'=> $validation
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
