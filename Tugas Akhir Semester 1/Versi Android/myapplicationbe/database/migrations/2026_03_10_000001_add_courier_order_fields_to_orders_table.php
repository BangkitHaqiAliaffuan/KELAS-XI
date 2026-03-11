<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        // 1. Add new columns to orders table
        Schema::table('orders', function (Blueprint $table) {
            $table->double('latitude')->nullable()->after('shipping_address');
            $table->double('longitude')->nullable()->after('latitude');
            $table->foreignId('courier_id')
                ->nullable()
                ->after('longitude')
                ->constrained('couriers')
                ->nullOnDelete();
            $table->timestamp('searching_at')->nullable()->after('cancelled_at');
        });

        // 2. Add 'searching' to the status ENUM
        DB::statement("
            ALTER TABLE orders
            MODIFY COLUMN status ENUM(
                'pending',
                'confirmed',
                'searching',
                'shipped',
                'completed',
                'cancelled'
            ) NOT NULL DEFAULT 'pending'
        ");
    }

    public function down(): void
    {
        // Remove 'searching' from ENUM first
        DB::statement("
            ALTER TABLE orders
            MODIFY COLUMN status ENUM(
                'pending',
                'confirmed',
                'shipped',
                'completed',
                'cancelled'
            ) NOT NULL DEFAULT 'pending'
        ");

        Schema::table('orders', function (Blueprint $table) {
            $table->dropForeign(['courier_id']);
            $table->dropColumn(['latitude', 'longitude', 'courier_id', 'searching_at']);
        });
    }
};
