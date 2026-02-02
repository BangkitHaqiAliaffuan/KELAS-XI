<?php

namespace App\Http\Controllers;

use App\Models\Installment;
use App\Models\Society;
use Illuminate\Http\Request;

class InstallmentController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $installments = Installment::with('available_month')->get();
        return response()->json(
            [
                'message' => 'done',
                'data' => $installments,
            ]
        );
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
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
    public function show(Installment $installment, $id)
    {
        $installments = Installment::where('id', $id)->with('available_month')->get();
        return response()->json(
            [
                'message' => 'done',
                'data' => $installments,
            ]
        );
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Installment $installment)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Installment $installment)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Installment $installment)
    {
        //
    }
}
