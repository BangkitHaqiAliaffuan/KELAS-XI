<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     * Mengubah foreign key guru_pengganti dan guru_asli dari tabel users ke tabel teachers
     */
    public function up(): void
    {
        Schema::table('guru_pengganti', function (Blueprint $table) {
            // Drop old foreign keys
            $table->dropForeign(['guru_pengganti_id']);
            $table->dropForeign(['guru_asli_id']);
        });

        Schema::table('guru_pengganti', function (Blueprint $table) {
            // Add new foreign keys to teachers table
            $table->foreign('guru_pengganti_id')
                ->references('id')
                ->on('teachers')
                ->onDelete('cascade');
            
            $table->foreign('guru_asli_id')
                ->references('id')
                ->on('teachers')
                ->onDelete('set null');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('guru_pengganti', function (Blueprint $table) {
            // Drop new foreign keys
            $table->dropForeign(['guru_pengganti_id']);
            $table->dropForeign(['guru_asli_id']);
        });

        Schema::table('guru_pengganti', function (Blueprint $table) {
            // Restore old foreign keys to users table
            $table->foreign('guru_pengganti_id')
                ->references('id')
                ->on('users')
                ->onDelete('cascade');
            
            $table->foreign('guru_asli_id')
                ->references('id')
                ->on('users')
                ->onDelete('set null');
        });
    }
};
