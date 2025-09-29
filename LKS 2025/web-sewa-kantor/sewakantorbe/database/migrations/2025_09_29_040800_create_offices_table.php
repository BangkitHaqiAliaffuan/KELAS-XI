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
        Schema::create('offices', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('slug')->unique();
            $table->text('description');
            $table->string('address');
            $table->string('latitude')->nullable();
            $table->string('longitude')->nullable();
            $table->integer('capacity');
            $table->decimal('price_per_day', 10, 2);
            $table->decimal('price_per_week', 10, 2);
            $table->decimal('price_per_month', 10, 2);
            $table->json('photos')->nullable();
            $table->string('status')->default('available'); // available, unavailable, maintenance
            $table->string('operating_hours')->nullable();
            $table->decimal('rating', 3, 2)->default(0);
            $table->integer('total_reviews')->default(0);
            $table->foreignId('city_id')->constrained()->onDelete('cascade');
            $table->timestamps();

            $table->index(['city_id', 'status']);
            $table->index(['capacity', 'status']);
            $table->index(['price_per_day', 'status']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('offices');
    }
};
