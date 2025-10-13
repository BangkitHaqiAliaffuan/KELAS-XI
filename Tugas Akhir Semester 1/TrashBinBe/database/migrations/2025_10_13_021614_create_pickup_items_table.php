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
        Schema::create('pickup_items', function (Blueprint $table) {
            $table->id();
            $table->foreignId('pickup_request_id')->constrained('pickup_requests')->onDelete('cascade'); // related pickup request
            $table->foreignId('waste_category_id')->constrained('waste_categories')->onDelete('cascade'); // waste category
            $table->decimal('estimated_weight', 8, 2); // estimated weight
            $table->decimal('actual_weight', 8, 2)->nullable(); // actual weight after pickup
            $table->string('photo_url')->nullable(); // photo of the waste
            $table->decimal('price_per_unit', 10, 2); // price per unit at time of pickup
            $table->decimal('subtotal', 10, 2)->nullable(); // calculated subtotal
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('pickup_items');
    }
};
