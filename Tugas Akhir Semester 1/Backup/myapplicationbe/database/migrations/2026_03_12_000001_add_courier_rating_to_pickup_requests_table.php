<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('pickup_requests', function (Blueprint $table) {
            $table->unsignedTinyInteger('courier_rating')->nullable()->after('completed_at');
            $table->text('courier_review')->nullable()->after('courier_rating');
            $table->timestamp('rated_at')->nullable()->after('courier_review');
        });
    }

    public function down(): void
    {
        Schema::table('pickup_requests', function (Blueprint $table) {
            $table->dropColumn(['courier_rating', 'courier_review', 'rated_at']);
        });
    }
};
