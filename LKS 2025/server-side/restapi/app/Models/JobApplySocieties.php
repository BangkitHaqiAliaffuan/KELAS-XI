<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class JobApplySocieties extends Model
{
    protected $fillable = [
        'society_id',
        'note',
        'job_vacancy_id',
    ];

    public function positions()
    {
        return $this->hasMany(JobApplyPositions::class, 'job_apply_societies_id');
    }

    public function vacancy()
    {
        return $this->belongsTo(JobVacancies::class, 'job_vacancy_id');
    }
}
