<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class AvailablePosition extends Model
{
     protected $table = 'available_positions';
     public function vacancy()
    {
        return $this->belongsTo(JobVacancies::class, 'job_vacancy_id');
    }

}
