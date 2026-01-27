<?php

namespace App\Http\Controllers;

use App\Models\JobApplyPosition;
use App\Models\JobApplySocieties;
use App\Models\JobVacancies;
use App\Models\Societies;
use DB;
use Illuminate\Http\Request;

class JobVacanciesController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index($id)
    {
        $data = JobVacancies::where('job_category_id', $id)->with('positions')->get();

        if (!$data) {
            return response()->json([
                'message' => 'gabisa'
            ]);
        }
        return response()->json([
            'message' => 'bisa',
            'data' => $data
        ]);
    }
    public function showOwnJob(Request $request)
    {
        $token = $request->bearerToken();
        $society = Societies::where('login_tokens', $token)->first();

        $job = JobApplySocieties::with('positions', 'vacancy')->where('society_id', $society->id)->orderBy('created_at', 'desc')->get();

        if (!$job) {
            return response()->json([
                'message' => 'gabisa'
            ]);
        }

        // $result = [];

        // foreach($job as $j){
        //     $app = $job->vacancy;
        // }

        // for

        return response()->json(data: [
            'message' => 'bisa',
            'data' => $job
        ]);
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create(Request $request)
    {
        $token = $request->bearerToken();
        $society = Societies::where('login_tokens', $token)->first();

        $exists = JobApplyPosition::where('society_id', $society->id)->where('job_vacancy_id', $request->vacancy_id)->exists();

        if ($exists) {
            return response()->json([
                'message' => 'lu sudah bos',
            ]);
        }

        DB::beginTransaction();
        $apply = $job = JobApplySocieties::create([
            'job_vacancy_id' => $request->vacancy_id,
            'notes' => $request->notes,
            'society_id' => $society->id,
        ]);

        foreach ($request->position as $pos) {
            JobApplyPosition::create([
                'position_id' => $pos,
                'job_apply_societies_id' => $apply->id,
                'job_vacancy_id' => $request->vacancy_id,
                'society_id' => $society->id,
            ]);
        }

        DB::commit();

        return response()->json([
            'message' => 'done',
            'jon' => $apply,
            'position_id' => $request->position,

            'joni' => $job,
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
    public function show($id)
    {
        $data = JobVacancies::where('id', $id)->with('category', 'positions')->first();

        return response()->json([
            'message' => 'done',
            'data' => $data
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
