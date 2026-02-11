<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TeacherAttendance extends Model
{
    use HasFactory;

    protected $fillable = [
        'schedule_id',
        'guru_id',
        'guru_asli_id',
        'tanggal',
        'jam_masuk',
        'status',
        'keterangan',
        'created_by',
        'assigned_by'
    ];

    protected $casts = [
        'tanggal' => 'date',
        'jam_masuk' => 'datetime:H:i',
    ];

    public function schedule()
    {
        return $this->belongsTo(Schedule::class);
    }

    /**
     * Relasi ke guru (pengganti jika status diganti, atau guru asli jika belum diganti)
     * Menggunakan Teacher model dari tabel teachers
     */
    public function guru()
    {
        return $this->belongsTo(Teacher::class, 'guru_id');
    }

    /**
     * Relasi ke guru asli (jika sudah diganti)
     * Menggunakan Teacher model dari tabel teachers
     */
    public function guruAsli()
    {
        return $this->belongsTo(Teacher::class, 'guru_asli_id');
    }

    public function createdBy()
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function assignedBy()
    {
        return $this->belongsTo(User::class, 'assigned_by');
    }
}
