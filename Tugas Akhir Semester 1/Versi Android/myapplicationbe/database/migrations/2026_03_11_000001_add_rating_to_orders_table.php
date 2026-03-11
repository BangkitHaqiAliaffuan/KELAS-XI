<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('orders', function (Blueprint $table) {
            // Courier rating (1–5) given by buyer after order completed
            $table->unsignedTinyInteger('courier_rating')->nullable()->after('completed_at');
            $table->text('courier_review')->nullable()->after('courier_rating');

            // Listing/product rating (1–5) given by buyer after order completed
            $table->unsignedTinyInteger('listing_rating')->nullable()->after('courier_review');
            $table->text('listing_review')->nullable()->after('listing_rating');

            // Timestamp when buyer submitted the rating
            $table->timestamp('rated_at')->nullable()->after('listing_review');
        });
    }

    public function down(): void
    {
        Schema::table('orders', function (Blueprint $table) {
            $table->dropColumn([
                'courier_rating',
                'courier_review',
                'listing_rating',
                'listing_review',
                'rated_at',
            ]);
        });
    }
};
