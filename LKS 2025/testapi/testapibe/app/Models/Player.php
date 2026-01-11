<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Player extends Model
{
    public $table = 'players';

    protected $fillable = [
        'posisi',
        'name',
        'nomor_punggung',
        'createdBy',
        'modifiedBy',
    ];
}
