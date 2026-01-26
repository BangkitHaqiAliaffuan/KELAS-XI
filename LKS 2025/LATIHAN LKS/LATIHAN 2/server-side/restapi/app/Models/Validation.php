<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Validation extends Model
{
  protected $fillable = [
    'job_category_id',
    'society_id',
    'status',
    'work_experience',
    'job_position',
    'reason_accepted',
  ];

  public function category(){
    return $this->belongsTo(JobCategory::class, 'job_category_id');
  }

}
