<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class JobApplyPositions extends Model
{
    protected $fillable = [
        'job_vacancy_id',
        'position',
        'society_id'
    ];

    public function applySociety()
    {
        return $this->belongsTo(JobApplySocieties::class, 'job_apply_societies_id');
    }

    public function vacancy()
    {
        return $this->belongsTo(JobVacancies::class, 'job_vacancy_id');
    }
}
