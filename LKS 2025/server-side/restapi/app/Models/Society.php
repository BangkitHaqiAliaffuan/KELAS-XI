<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Laravel\Sanctum\HasApiTokens;

class Society extends Model
{
    use HasApiTokens;

    protected $table = 'societies';

    protected $fillable = [
        'login_tokens',
        'id_card_number',
        'password',
        'name',
    ];
}
