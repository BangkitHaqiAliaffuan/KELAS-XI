<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Resources\TransactionResource;
use App\Models\Transaction;
use App\Models\Office;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Carbon\Carbon;

class TransactionController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request): JsonResponse
    {
        $query = Transaction::with(['office.city', 'user']);

        // Filter by status
        if ($request->has('status')) {
            $query->where('status', $request->status);
        }

        // Filter by payment status
        if ($request->has('payment_status')) {
            $query->where('payment_status', $request->payment_status);
        }

        // Filter by date range
        if ($request->has('start_date')) {
            $query->where('start_date', '>=', $request->start_date);
        }
        if ($request->has('end_date')) {
            $query->where('end_date', '<=', $request->end_date);
        }

        // Search by booking code or customer
        if ($request->has('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('booking_code', 'like', "%{$search}%")
                  ->orWhere('customer_name', 'like', "%{$search}%")
                  ->orWhere('customer_email', 'like', "%{$search}%");
            });
        }

        $perPage = $request->input('per_page', 15);
        $transactions = $query->orderBy('created_at', 'desc')->paginate($perPage);

        return response()->json([
            'success' => true,
            'data' => TransactionResource::collection($transactions->items()),
            'pagination' => [
                'current_page' => $transactions->currentPage(),
                'last_page' => $transactions->lastPage(),
                'per_page' => $transactions->perPage(),
                'total' => $transactions->total(),
            ],
            'message' => 'Transactions retrieved successfully'
        ]);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'customer_name' => 'required|string|max:255',
            'customer_email' => 'required|email',
            'customer_phone' => 'required|string|max:20',
            'office_id' => 'required|exists:offices,id',
            'start_date' => 'required|date|after_or_equal:today',
            'end_date' => 'required|date|after:start_date',
            'rental_type' => 'required|in:daily,weekly,monthly',
            'notes' => 'nullable|string',
        ]);

        $office = Office::findOrFail($validated['office_id']);

        // Check if office is available
        if ($office->status !== 'available') {
            return response()->json([
                'success' => false,
                'message' => 'Office is not available for booking'
            ], 422);
        }

        // Calculate duration and pricing
        $startDate = Carbon::parse($validated['start_date']);
        $endDate = Carbon::parse($validated['end_date']);
        $durationDays = $startDate->diffInDays($endDate) + 1;

        $pricePerUnit = match($validated['rental_type']) {
            'daily' => $office->price_per_day,
            'weekly' => $office->price_per_week,
            'monthly' => $office->price_per_month,
        };

        $totalAmount = $pricePerUnit * ($validated['rental_type'] === 'daily' ? $durationDays : 1);
        $taxAmount = $totalAmount * 0.11; // 11% tax
        $finalAmount = $totalAmount + $taxAmount;

        $validated['user_id'] = auth()->id();
        $validated['duration_days'] = $durationDays;
        $validated['price_per_unit'] = $pricePerUnit;
        $validated['total_amount'] = $totalAmount;
        $validated['tax_amount'] = $taxAmount;
        $validated['discount_amount'] = 0;
        $validated['final_amount'] = $finalAmount;

        $transaction = Transaction::create($validated);
        $transaction->load(['office.city']);

        return response()->json([
            'success' => true,
            'data' => new TransactionResource($transaction),
            'message' => 'Booking created successfully'
        ], 201);
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id): JsonResponse
    {
        $transaction = Transaction::with(['office.city', 'user'])
            ->findOrFail($id);

        return response()->json([
            'success' => true,
            'data' => new TransactionResource($transaction),
            'message' => 'Transaction retrieved successfully'
        ]);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id): JsonResponse
    {
        $transaction = Transaction::findOrFail($id);

        $validated = $request->validate([
            'customer_name' => 'sometimes|required|string|max:255',
            'customer_email' => 'sometimes|required|email',
            'customer_phone' => 'sometimes|required|string|max:20',
            'notes' => 'nullable|string',
            'status' => 'sometimes|in:confirmed,cancelled,completed',
            'payment_status' => 'sometimes|in:pending,paid,failed,cancelled,refunded',
        ]);

        $transaction->update($validated);
        $transaction->load(['office.city']);

        return response()->json([
            'success' => true,
            'data' => new TransactionResource($transaction),
            'message' => 'Transaction updated successfully'
        ]);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id): JsonResponse
    {
        $transaction = Transaction::findOrFail($id);
        $transaction->delete();

        return response()->json([
            'success' => true,
            'message' => 'Transaction deleted successfully'
        ]);
    }

    /**
     * Get user's transactions
     */
    public function userTransactions(Request $request): JsonResponse
    {
        $transactions = Transaction::with(['office.city'])
            ->where('user_id', auth()->id())
            ->orderBy('created_at', 'desc')
            ->paginate($request->input('per_page', 15));

        return response()->json([
            'success' => true,
            'data' => TransactionResource::collection($transactions->items()),
            'pagination' => [
                'current_page' => $transactions->currentPage(),
                'last_page' => $transactions->lastPage(),
                'per_page' => $transactions->perPage(),
                'total' => $transactions->total(),
            ],
            'message' => 'User transactions retrieved successfully'
        ]);
    }

    /**
     * Update transaction status
     */
    public function updateStatus(Request $request, string $id): JsonResponse
    {
        $transaction = Transaction::findOrFail($id);

        $validated = $request->validate([
            'status' => 'required|in:confirmed,cancelled,completed',
        ]);

        $transaction->update($validated);

        return response()->json([
            'success' => true,
            'data' => new TransactionResource($transaction->load(['office.city'])),
            'message' => 'Transaction status updated successfully'
        ]);
    }

    /**
     * Update payment status
     */
    public function updatePaymentStatus(Request $request, string $id): JsonResponse
    {
        $transaction = Transaction::findOrFail($id);

        $validated = $request->validate([
            'payment_status' => 'required|in:pending,paid,failed,cancelled,refunded',
            'payment_method' => 'nullable|string',
            'payment_reference' => 'nullable|string',
        ]);

        if ($validated['payment_status'] === 'paid') {
            $validated['payment_date'] = now();
        }

        $transaction->update($validated);

        return response()->json([
            'success' => true,
            'data' => new TransactionResource($transaction->load(['office.city'])),
            'message' => 'Payment status updated successfully'
        ]);
    }
}
