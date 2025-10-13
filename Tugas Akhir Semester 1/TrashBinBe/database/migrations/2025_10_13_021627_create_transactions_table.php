<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('transactions', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade'); // user who made transaction
            $table->enum('type', ['pickup_earning', 'marketplace_sale', 'marketplace_purchase', 'points_redeem']); // transaction type
            $table->integer('reference_id'); // reference ID (pickup_request_id or order_id)
            $table->string('reference_type'); // reference type (PickupRequest, Order, etc.)
            $table->decimal('amount', 10, 2); // transaction amount
            $table->integer('points_earned')->default(0); // points earned from transaction
            $table->text('description'); // description of transaction
            $table->timestamps();
            
            // Indexes for performance
            $table->index(['user_id', 'type']);
            $table->index('created_at');
            $table->index(['reference_id', 'reference_type']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('transactions');
    }
};
