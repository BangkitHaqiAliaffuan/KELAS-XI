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
            // Tambahkan kolom untuk nama guru pengganti (manual input)
            $table->string('nama_guru_pengganti')->nullable()->after('keterangan');
            
            // Tambahkan kolom untuk ID guru pengganti (jika terhubung ke tabel teachers)
            $table->foreignId('guru_pengganti_id')->nullable()->after('nama_guru_pengganti')
                  ->constrained('teachers')->nullOnDelete();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('teacher_attendances', function (Blueprint $table) {
            $table->dropForeign(['guru_pengganti_id']);
            $table->dropColumn(['nama_guru_pengganti', 'guru_pengganti_id']);
        });
    }
};
