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
        Schema::create('orders', function (Blueprint $table) {
            $table->id();
            $table->foreignId('listing_id')->constrained('marketplace_listings')->onDelete('cascade'); // related listing
            $table->foreignId('buyer_id')->constrained('users', 'id')->onDelete('cascade'); // buyer user
            $table->foreignId('seller_id')->constrained('users', 'id')->onDelete('cascade'); // seller user
            $table->decimal('quantity', 8, 2); // purchased quantity
            $table->decimal('total_price', 10, 2); // total price
            $table->enum('status', ['pending', 'confirmed', 'shipping', 'completed', 'cancelled'])->default('pending'); // order status
            $table->enum('payment_status', ['unpaid', 'paid', 'refunded'])->default('unpaid'); // payment status
            $table->text('shipping_address')->nullable(); // shipping address
            $table->text('notes')->nullable(); // additional notes
            $table->integer('rating')->nullable(); // rating 1-5
            $table->text('review')->nullable(); // review text
            $table->timestamps();
            
            // Indexes for performance
            $table->index('status');
            $table->index('payment_status');
            $table->index(['buyer_id', 'status']);
            $table->index(['seller_id', 'status']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('orders');
    }
};
