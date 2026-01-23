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
}
