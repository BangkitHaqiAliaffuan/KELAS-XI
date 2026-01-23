<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class JobVacancies extends Model
{
    public function category(){
        return $this->belongsTo(JobCategory::class, 'job_category_id');
    }
}
