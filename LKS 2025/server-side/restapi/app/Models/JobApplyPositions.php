<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class JobApplyPositions extends Model
{
    protected $fillable = [
        'job_vacancy_id',
        'positions',
        'society_id'
    ];
}
