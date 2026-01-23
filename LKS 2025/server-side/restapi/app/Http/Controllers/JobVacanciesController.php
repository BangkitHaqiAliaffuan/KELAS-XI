<?php

namespace App\Http\Controllers;

use App\Models\JobVacancies;
use Illuminate\Http\Request;

class JobVacanciesController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index($id)
    {
        $job = JobVacancies::with('category')->where('job_category_id', $id)
            ->get();

        return response()->json([
            'message' => 'berhasil',
            'data' => $job,

        ]);
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {

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
    public function show($id)
    {

        $job = JobVacancies::with('category')->find($id);

        if (!$job) {
            return response()->json([
                'message' => 'Job not found'
            ], 404);
        }

        return response()->json([
            'message' => 'success',
            'data' => $job,
        ]);

    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(JobVacancies $jobVacancies)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, JobVacancies $jobVacancies)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(JobVacancies $jobVacancies)
    {
        //
    }
}
