<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Transaction;
use App\Models\User;
use App\Models\Office;
use Carbon\Carbon;

class TransactionSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Get first user or create one
        $user = User::first();
        if (!$user) {
            $user = User::create([
                'name' => 'Test User',
                'email' => 'user@example.com',
                'password' => bcrypt('password'),
            ]);
        }

        // Get offices
        $offices = Office::limit(5)->get();

        if ($offices->isEmpty()) {
            $this->command->info('No offices found. Please run OfficeSeeder first.');
            return;
        }

        $statuses = ['confirmed', 'pending', 'completed', 'cancelled'];
        $paymentStatuses = ['paid', 'pending', 'paid', 'refunded'];

        foreach ($offices as $index => $office) {
            $startDate = Carbon::now()->addDays($index * 7);
            $endDate = $startDate->copy()->addDays(rand(1, 14));
            $durationDays = $startDate->diffInDays($endDate);

            $pricePerUnit = $office->price_per_day;
            $totalAmount = $pricePerUnit * $durationDays;
            $taxAmount = $totalAmount * 0.11;
            $finalAmount = $totalAmount + $taxAmount;

            Transaction::create([
                'booking_code' => 'BK' . strtoupper(uniqid()),
                'customer_name' => $user->name,
                'customer_email' => $user->email,
                'customer_phone' => '+62' . rand(8000000000, 8999999999),
                'office_id' => $office->id,
                'user_id' => $user->id,
                'start_date' => $startDate,
                'end_date' => $endDate,
                'duration_days' => $durationDays,
                'rental_type' => 'daily',
                'price_per_unit' => $pricePerUnit,
                'total_amount' => $totalAmount,
                'tax_amount' => $taxAmount,
                'discount_amount' => 0,
                'final_amount' => $finalAmount,
                'payment_status' => $paymentStatuses[$index % 4],
                'payment_method' => 'credit_card',
                'payment_reference' => 'PAY' . strtoupper(uniqid()),
                'payment_date' => $paymentStatuses[$index % 4] === 'paid' ? now() : null,
                'notes' => 'Sample booking #' . ($index + 1),
                'status' => $statuses[$index % 4],
            ]);
        }

        $this->command->info('Sample transactions created successfully!');
    }
}
