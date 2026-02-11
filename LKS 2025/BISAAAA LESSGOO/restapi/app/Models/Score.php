<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Score extends Model
{
    public function gameVersion(){
        return $this->belongsTo(game_versions::class, 'game_version_id');
    }
}
