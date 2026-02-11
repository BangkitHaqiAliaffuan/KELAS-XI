<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Game extends Model
{
    public function user(){
        return $this->belongsTo(User::class, 'created_by');
    }
    public function latestVersion(){
        return $this->hasMany(game_versions::class, 'game_id');
    }
    public function scores(){
        return $this->hasManyThrough(Score::class,game_versions::class, 'game_id', 'game_version_id');
    }
}
