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
        Schema::create('marketplace_listings', function (Blueprint $table) {
            $table->id();
            $table->foreignId('seller_id')->constrained('users', 'id')->onDelete('cascade'); // seller user
            $table->foreignId('waste_category_id')->constrained('waste_categories')->onDelete('cascade'); // waste category
            $table->string('title'); // listing title
            $table->text('description'); // listing description
            $table->decimal('quantity', 8, 2); // available quantity
            $table->string('unit'); // unit (kg, pcs, etc.)
            $table->decimal('price_per_unit', 10, 2); // price per unit
            $table->decimal('total_price', 10, 2); // total price
            $table->enum('condition', ['clean', 'needs_cleaning', 'mixed']); // condition of waste
            $table->string('location'); // location of waste
            $table->decimal('lat', 10, 8); // latitude
            $table->decimal('lng', 11, 8); // longitude
            $table->enum('status', ['available', 'reserved', 'sold', 'expired'])->default('available'); // listing status
            $table->json('photos'); // array of photo URLs
            $table->integer('views_count')->default(0); // number of views
            $table->timestamp('expires_at')->nullable(); // when listing expires
            $table->timestamps();
            
            // Indexes for performance
            $table->index(['lat', 'lng']);
            $table->index('status');
            $table->index('expires_at');
            $table->index('created_at');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('marketplace_listings');
    }
};
