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
        Schema::create('waste_categories', function (Blueprint $table) {
            $table->id();
            $table->string('name'); // category name (e.g., Kain Perca, Plastik PET)
            $table->string('slug')->unique(); // URL-friendly slug
            $table->text('description')->nullable(); // category description
            $table->enum('unit', ['kg', 'pcs', 'liter']); // unit of measurement
            $table->decimal('base_price_per_unit', 10, 2); // base price per unit
            $table->string('icon_url')->nullable(); // icon for the category
            $table->boolean('is_active')->default(true); // whether category is active
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('waste_categories');
    }
};
