<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Laravel\Sanctum\HasApiTokens;

class Administrator extends Model
{
    use HasApiTokens;

    protected $fillable = [
        'api_token',
        'password',
        'username',
    ];
    protected $hidden = [
        'password'
    ];
}
