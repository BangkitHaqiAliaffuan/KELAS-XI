<?php

namespace App\Http\Controllers;

use App\Models\JobApplyPositions;
use App\Models\JobApplySocieties;
use App\Models\JobVacancies;
use App\Models\Society;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

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
        $token = $request->bearerToken();

        $society = Society::where('login_tokens', $token)->first();


        $exists = JobApplyPositions::where('society_id', $society->id)->where('job_vacancy_id', $request->vacancy_id)->exists();

        if ($exists) {
            return response()->json([
                'message' => 'wes ada',
            ]);
        }

        DB::beginTransaction();

        try {
            $apply = $job = JobApplySocieties::create([
                'job_vacancy_id' => $request->vacancy_id,
                'notes' => $request->note,
                'society_id' => $society->id
            ]);
            foreach ($request->positions as $pos) {
                JobApplyPositions::create([
                    'society_id' => $society->id,
                    'job_vacancy_id' => $request->vacancy_id,
                    'job_apply_societies_id' => $apply->id,
                    'position' => $pos,
                    'status' => 'pending'
                ]);
            }

            DB::commit();

             return response()->json([
            'message' => 'berhasil',
            'data' => $job,
            'apply' => $apply,
        ]);

        } catch (error) {

        }


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
    public function alljobvacan(JobVacancies $jobVacancies)
    {
        $job = JobVacancies::with('category','positions')->get();

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
}
