<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class AvailableMonth extends Model
{
    protected $table = 'available_month';

    

    public function installment(){
        return $this->belongsTo(Installment::class, 'installment_id');
    }
}
