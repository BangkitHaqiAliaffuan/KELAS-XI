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
        Schema::table('users', function (Blueprint $table) {
            // Add phone column if it doesn't exist
            if (!Schema::hasColumn('users', 'phone')) {
                $table->string('phone')->unique()->nullable(); // unique phone number
            }
            
            // Add role column if it doesn't exist
            if (!Schema::hasColumn('users', 'role')) {
                $table->enum('role', ['user', 'collector', 'industry', 'admin'])->default('user'); // user role
            }
            
            // Add avatar column if it doesn't exist
            if (!Schema::hasColumn('users', 'avatar')) {
                $table->string('avatar')->nullable(); // user avatar
            }
            
            // Add address column if it doesn't exist
            if (!Schema::hasColumn('users', 'address')) {
                $table->text('address')->nullable(); // user address
            }
            
            // Add lat column if it doesn't exist
            if (!Schema::hasColumn('users', 'lat')) {
                $table->decimal('lat', 10, 8)->nullable(); // latitude
            }
            
            // Add lng column if it doesn't exist
            if (!Schema::hasColumn('users', 'lng')) {
                $table->decimal('lng', 11, 8)->nullable(); // longitude
            }
            
            // Add points column if it doesn't exist
            if (!Schema::hasColumn('users', 'points')) {
                $table->integer('points')->default(0); // reward points
            }
            
            // Add is_verified column if it doesn't exist
            if (!Schema::hasColumn('users', 'is_verified')) {
                $table->boolean('is_verified')->default(false); // verification status
            }
            
            // Add fcm_token column if it doesn't exist
            if (!Schema::hasColumn('users', 'fcm_token')) {
                $table->string('fcm_token')->nullable(); // Firebase Cloud Messaging token
            }
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            // Remove columns only if they exist
            $columnsToRemove = [
                'phone', 'role', 'avatar', 'address', 'lat', 'lng', 
                'points', 'is_verified', 'fcm_token'
            ];
            
            $existingColumns = array_filter($columnsToRemove, function ($column) {
                return Schema::hasColumn('users', $column);
            });
            
            if (!empty($existingColumns)) {
                $table->dropColumn($existingColumns);
            }
        });
    }
};
