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
        Schema::create('pickup_requests', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade'); // requesting user
            $table->foreignId('collector_id')->nullable()->constrained('users')->onDelete('set null'); // assigned collector
            $table->text('pickup_address'); // pickup address
            $table->decimal('pickup_lat', 10, 8); // pickup latitude
            $table->decimal('pickup_lng', 11, 8); // pickup longitude
            $table->dateTime('scheduled_date'); // scheduled pickup date/time
            $table->enum('status', ['pending', 'accepted', 'on_the_way', 'picked_up', 'completed', 'cancelled'])->default('pending'); // status
            $table->decimal('total_weight', 8, 2)->nullable(); // total actual weight
            $table->decimal('total_price', 10, 2)->nullable(); // total price
            $table->text('notes')->nullable(); // additional notes
            $table->integer('rating')->nullable(); // rating 1-5
            $table->text('review')->nullable(); // review text
            $table->timestamps();
            
            // Indexes for performance
            $table->index(['pickup_lat', 'pickup_lng']);
            $table->index('status');
            $table->index('scheduled_date');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('pickup_requests');
    }
};
