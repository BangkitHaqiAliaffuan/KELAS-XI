<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // Add the new payment_method column first
        Schema::table('transactions', function (Blueprint $table) {
            $table->enum('payment_method', ['cod', 'trashpay'])->default('cod')->after('description');
        });
        
        // For modifying enum values in Laravel, we need to use a raw query approach
        // as direct enum changes might not work across all database systems
        DB::statement("ALTER TABLE transactions MODIFY COLUMN type ENUM(
            'pickup_earning', 
            'pickup_payment', 
            'marketplace_sale', 
            'marketplace_purchase',
            'points_redeem',
            'trashpay_deposit',
            'trashpay_withdrawal'
        )");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Drop the payment_method column
        Schema::table('transactions', function (Blueprint $table) {
            $table->dropColumn('payment_method');
        });
        
        // Revert the enum type to original values
        DB::statement("ALTER TABLE transactions MODIFY COLUMN type ENUM(
            'pickup_earning', 
            'marketplace_sale', 
            'marketplace_purchase', 
            'points_redeem'
        )");
    }
};
