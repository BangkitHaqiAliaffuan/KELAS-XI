<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class JobVacancies extends Model
{
    protected $fillable = [
        'job_category_id',
        'company',
        'address',
        'description',
    ];

    public function category(){
        return $this->belongsTo(JobCategory::class, 'job_category_id');
    }
    public function positions(){
        return $this->hasMany(AvailablePosition::class, 'job_vacancy_id');
    }
    
}
