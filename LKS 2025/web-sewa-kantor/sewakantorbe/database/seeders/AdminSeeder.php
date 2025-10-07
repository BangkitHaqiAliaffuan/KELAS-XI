<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Admin;
use Illuminate\Support\Facades\Hash;

class AdminSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create Super Admin
        Admin::create([
            'name' => 'Super Admin',
            'email' => 'superadmin@sewakantor.com',
            'password' => Hash::make('password123'),
            'role' => 'super_admin',
            'phone' => '+6281234567890',
            'is_active' => true,
        ]);

        // Create Regular Admin
        Admin::create([
            'name' => 'Admin User',
            'email' => 'admin@sewakantor.com',
            'password' => Hash::make('password123'),
            'role' => 'admin',
            'phone' => '+6281234567891',
            'is_active' => true,
        ]);

        // Create Moderator
        Admin::create([
            'name' => 'Moderator User',
            'email' => 'moderator@sewakantor.com',
            'password' => Hash::make('password123'),
            'role' => 'moderator',
            'phone' => '+6281234567892',
            'is_active' => true,
        ]);

        $this->command->info('Admins created successfully!');
        $this->command->info('Super Admin: superadmin@sewakantor.com / password123');
        $this->command->info('Admin: admin@sewakantor.com / password123');
        $this->command->info('Moderator: moderator@sewakantor.com / password123');
    }
}
