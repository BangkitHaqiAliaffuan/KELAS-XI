<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Installment extends Model
{
    protected $table = 'installment';
    public function available_month(){
        return $this->hasMany(AvailableMonth::class, 'installment_id');
    }
}
