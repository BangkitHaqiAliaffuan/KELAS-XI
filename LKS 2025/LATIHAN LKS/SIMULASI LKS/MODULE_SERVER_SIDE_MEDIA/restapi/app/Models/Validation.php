<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Validation extends Model
{

    protected $table = 'validations';
    protected $fillable = [
        'job_description',
        'society_id',
        'income',
        'reason_accepted',
    ];
}
