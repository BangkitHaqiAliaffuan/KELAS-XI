<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Validation extends Model
{
    protected $table = 'validations';

    protected $fillable = [
        'society_id',
        'work_experience',
        'job_category_id',
        'job_position',
        'reason_accepted',
    ];
}
