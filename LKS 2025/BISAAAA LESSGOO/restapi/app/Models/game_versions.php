<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class game_versions extends Model
{

    public function game(){
        return $this->belongsTo(Game::class, 'game_id');
    }

    public function scores(){
        return $this->hasMany(Score::class, 'game_version_id');
    }
}
