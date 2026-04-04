<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class ExportController extends Controller
{
    public function csv(Request $request)
    {
        $transactions = $request->user()
            ->transactions()
            ->latest('transaction_date')
            ->get(['transaction_date', 'type', 'category', 'note', 'amount']);

        $rows = [
            ['Date', 'Type', 'Category', 'Note', 'Amount'],
        ];

        foreach ($transactions as $transaction) {
            $rows[] = [
                $transaction->transaction_date->toDateString(),
                $transaction->type,
                $transaction->category,
                $transaction->note ?? '',
                $transaction->amount,
            ];
        }

        $handle = fopen('php://temp', 'r+');
        foreach ($rows as $row) {
            fputcsv($handle, $row);
        }

        rewind($handle);
        $csv = stream_get_contents($handle);
        fclose($handle);

        return response($csv, 200, [
            'Content-Type' => 'text/csv; charset=UTF-8',
            'Content-Disposition' => 'attachment; filename="balancebook-transactions.csv"',
        ]);
    }
}
