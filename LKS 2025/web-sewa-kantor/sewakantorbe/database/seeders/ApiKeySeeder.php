<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class ApiKeySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $admin = \App\Models\User::where('email', 'admin@sewakantorbe.com')->first();

        if ($admin) {
            \App\Models\ApiKey::create([
                'name' => 'Frontend API Key',
                'key' => 'ak_frontend_' . \Illuminate\Support\Str::random(24),
                'description' => 'API Key untuk akses frontend React',
                'created_by' => $admin->id,
            ]);

            \App\Models\ApiKey::create([
                'name' => 'Mobile API Key',
                'key' => 'ak_mobile_' . \Illuminate\Support\Str::random(24),
                'description' => 'API Key untuk aplikasi mobile',
                'created_by' => $admin->id,
            ]);
        }
    }
}
