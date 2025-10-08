<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Schedule;
use Illuminate\Support\Facades\Hash;

class DummyDataSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Membuat users dummy
        $admin = User::create([
            'name' => 'Admin Sekolah',
            'email' => 'admin@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'admin'
        ]);

        $guru1 = User::create([
            'name' => 'Dr. Siti Nurhaliza',
            'email' => 'siti.guru@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'guru'
        ]);

        $guru2 = User::create([
            'name' => 'Bapak Ahmad Sudrajat',
            'email' => 'ahmad.guru@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'guru'
        ]);

        $siswa = User::create([
            'name' => 'Andi Pratama',
            'email' => 'andi.siswa@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'siswa'
        ]);

        // Membuat jadwal dummy
        Schedule::create([
            'hari' => 'Senin',
            'kelas' => 'X IPA 1',
            'mata_pelajaran' => 'Matematika',
            'guru_id' => $guru1->id,
            'jam_mulai' => '07:30',
            'jam_selesai' => '09:00',
            'ruang' => 'Lab Matematika'
        ]);

        Schedule::create([
            'hari' => 'Senin',
            'kelas' => 'X IPA 1',
            'mata_pelajaran' => 'Fisika',
            'guru_id' => $guru2->id,
            'jam_mulai' => '09:15',
            'jam_selesai' => '10:45',
            'ruang' => 'Lab Fisika'
        ]);

        Schedule::create([
            'hari' => 'Selasa',
            'kelas' => 'XI IPS 2',
            'mata_pelajaran' => 'Sejarah',
            'guru_id' => $guru1->id,
            'jam_mulai' => '10:30',
            'jam_selesai' => '12:00',
            'ruang' => 'Ruang 201'
        ]);
    }
}
