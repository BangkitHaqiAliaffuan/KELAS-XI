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
        Schema::table('teacher_attendances', function (Blueprint $table) {
            if (!Schema::hasColumn('teacher_attendances', 'guru_asli_id')) {
                $table->foreignId('guru_asli_id')->nullable()->constrained('teachers')->onDelete('set null');
            }
            if (!Schema::hasColumn('teacher_attendances', 'assigned_by')) {
                $table->foreignId('assigned_by')->nullable()->constrained('users')->onDelete('set null');
            }
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('teacher_attendances', function (Blueprint $table) {
            $table->dropForeign(['guru_asli_id']);
            $table->dropForeign(['assigned_by']);
            $table->dropColumn(['guru_asli_id', 'assigned_by']);
        });
    }
};
