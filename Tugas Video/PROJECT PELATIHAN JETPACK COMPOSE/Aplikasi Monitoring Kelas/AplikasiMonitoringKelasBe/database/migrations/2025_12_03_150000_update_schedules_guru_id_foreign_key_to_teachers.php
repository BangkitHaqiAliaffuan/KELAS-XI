<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations to update schedules table to reference teachers table instead of users table.
     */
    public function up(): void
    {
        // Get the foreign key constraint name for guru_id
        $guruIdFk = DB::selectOne("
            SELECT CONSTRAINT_NAME
            FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE()
            AND TABLE_NAME = 'schedules'
            AND COLUMN_NAME = 'guru_id'
            AND REFERENCED_TABLE_NAME IS NOT NULL
        ");

        // Drop existing foreign key if exists
        if ($guruIdFk) {
            DB::statement("ALTER TABLE schedules DROP FOREIGN KEY `{$guruIdFk->CONSTRAINT_NAME}`");
        }

        // Add new foreign key referencing teachers table with CASCADE delete
        DB::statement("ALTER TABLE schedules
            ADD CONSTRAINT schedules_guru_id_foreign
            FOREIGN KEY (guru_id) REFERENCES teachers(id) ON DELETE CASCADE ON UPDATE CASCADE");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Get the foreign key constraint name for guru_id
        $guruIdFk = DB::selectOne("
            SELECT CONSTRAINT_NAME
            FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE()
            AND TABLE_NAME = 'schedules'
            AND COLUMN_NAME = 'guru_id'
            AND REFERENCED_TABLE_NAME IS NOT NULL
        ");

        // Drop foreign key to teachers if exists
        if ($guruIdFk) {
            DB::statement("ALTER TABLE schedules DROP FOREIGN KEY `{$guruIdFk->CONSTRAINT_NAME}`");
        }

        // Add foreign key back to users table
        DB::statement("ALTER TABLE schedules
            ADD CONSTRAINT schedules_guru_id_foreign
            FOREIGN KEY (guru_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE");
    }
};
