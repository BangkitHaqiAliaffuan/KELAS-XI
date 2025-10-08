<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Monitoring extends Model
{
    use HasFactory;

    protected $table = 'monitoring';

    protected $fillable = [
        'guru_id',
        'pelapor_id',
        'status_hadir',
        'catatan',
        'kelas',
        'mata_pelajaran',
        'tanggal',
        'jam_laporan'
    ];

    protected $casts = [
        'tanggal' => 'date',
        'jam_laporan' => 'datetime:H:i',
    ];

    /**
     * Relasi ke model User (guru)
     */
    public function guru()
    {
        return $this->belongsTo(User::class, 'guru_id');
    }

    /**
     * Relasi ke model User (pelapor)
     */
    public function pelapor()
    {
        return $this->belongsTo(User::class, 'pelapor_id');
    }
}
