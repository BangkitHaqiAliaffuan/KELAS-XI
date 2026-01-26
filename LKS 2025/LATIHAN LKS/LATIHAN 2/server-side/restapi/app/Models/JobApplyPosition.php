<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class JobApplyPosition extends Model
{
    protected $fillable = [
        'society_id',
        'job_vacancy_id',
        'job_apply_societies_id',
        'position_id',
    ];

    public function society(){
        return $this->belongsTo(JobApplySocieties::class, 'job_apply_societies_id');
    }
}
