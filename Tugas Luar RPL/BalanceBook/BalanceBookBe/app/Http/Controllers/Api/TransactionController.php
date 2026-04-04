<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class TransactionController extends Controller
{
    public function index(Request $request): JsonResponse
    {
        $payload = $request->validate([
            'search' => ['nullable', 'string', 'max:255'],
            'type' => ['nullable', 'in:income,expense'],
            'category' => ['nullable', 'string', 'max:120'],
            'from' => ['nullable', 'date'],
            'to' => ['nullable', 'date'],
            'limit' => ['nullable', 'integer', 'min:1', 'max:200'],
        ]);

        $query = $request->user()
            ->transactions()
            ->latest('transaction_date')
            ->latest('id');

        if (! empty($payload['search'])) {
            $search = $payload['search'];
            $query->where(function ($builder) use ($search) {
                $builder->where('category', 'like', "%{$search}%")
                    ->orWhere('note', 'like', "%{$search}%");
            });
        }

        if (! empty($payload['type'])) {
            $query->where('type', $payload['type']);
        }

        if (! empty($payload['category'])) {
            $query->where('category', $payload['category']);
        }

        if (! empty($payload['from'])) {
            $query->whereDate('transaction_date', '>=', $payload['from']);
        }

        if (! empty($payload['to'])) {
            $query->whereDate('transaction_date', '<=', $payload['to']);
        }

        $limit = $payload['limit'] ?? 100;
        $transactions = $query->limit($limit)->get();

        return response()->json([
            'transactions' => $transactions,
        ]);
    }

    public function recent(Request $request): JsonResponse
    {
        $payload = $request->validate([
            'limit' => ['nullable', 'integer', 'min:1', 'max:20'],
        ]);

        $limit = $payload['limit'] ?? 5;

        $transactions = $request->user()
            ->transactions()
            ->latest('transaction_date')
            ->latest('id')
            ->limit($limit)
            ->get();

        return response()->json([
            'transactions' => $transactions,
        ]);
    }

    public function store(Request $request): JsonResponse
    {
        $payload = $request->validate([
            'type' => ['required', 'in:income,expense'],
            'category' => ['required', 'string', 'max:120'],
            'note' => ['nullable', 'string', 'max:1000'],
            'amount' => ['required', 'numeric', 'gt:0'],
            'transaction_date' => ['required', 'date'],
        ]);

        $transaction = $request->user()->transactions()->create($payload);

        return response()->json([
            'message' => 'Transaksi berhasil disimpan',
            'transaction' => $transaction,
        ], 201);
    }
}
