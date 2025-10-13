<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create admin user
        User::create([
            'name' => 'Admin User',
            'email' => 'admin@trashbin.com',
            'phone' => '081234567890',
            'password' => Hash::make('password'),
            'role' => 'admin',
            'address' => 'Jl. Admin No. 1, Jakarta',
            'lat' => -6.2088,
            'lng' => 106.8456,
            'points' => 0,
            'is_verified' => true,
        ]);

        // Create regular user
        User::create([
            'name' => 'John Doe',
            'email' => 'john@trashbin.com',
            'phone' => '081234567891',
            'password' => Hash::make('password'),
            'role' => 'user',
            'address' => 'Jl. Mangga No. 10, Jakarta',
            'lat' => -6.2146,
            'lng' => 106.8451,
            'points' => 150,
            'is_verified' => true,
        ]);

        // Create another regular user
        User::create([
            'name' => 'Jane Smith',
            'email' => 'jane@trashbin.com',
            'phone' => '081234567892',
            'password' => Hash::make('password'),
            'role' => 'user',
            'address' => 'Jl. Durian No. 5, Jakarta',
            'lat' => -6.2156,
            'lng' => 106.8461,
            'points' => 75,
            'is_verified' => true,
        ]);

        // Create collector
        User::create([
            'name' => 'Budi Collector',
            'email' => 'budi@trashbin.com',
            'phone' => '081234567893',
            'password' => Hash::make('password'),
            'role' => 'collector',
            'address' => 'Jl. Penjemput No. 20, Jakarta',
            'lat' => -6.2166,
            'lng' => 106.8471,
            'points' => 0,
            'is_verified' => true,
        ]);

        // Create industry/merchant user
        User::create([
            'name' => 'PT. Daur Ulang Jaya',
            'email' => 'info@daurulang.com',
            'phone' => '081234567894',
            'password' => Hash::make('password'),
            'role' => 'industry',
            'address' => 'Jl. Industri No. 100, Jakarta',
            'lat' => -6.2176,
            'lng' => 106.8481,
            'points' => 0,
            'is_verified' => true,
        ]);
    }
}
