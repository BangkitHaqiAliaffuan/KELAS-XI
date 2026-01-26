<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Laravel\Sanctum\HasApiTokens;

class Societies extends Model
{
    use HasApiTokens;
    protected $table = 'societies';

    protected $fillable = [
        'login_tokens'
    ];

    protected $hidden = [
        'password'
    ];
}
