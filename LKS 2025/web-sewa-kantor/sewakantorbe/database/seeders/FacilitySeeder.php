<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class FacilitySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $facilities = [
            [
                'name' => 'Wi-Fi',
                'icon' => 'wifi',
                'description' => 'Koneksi internet berkecepatan tinggi',
            ],
            [
                'name' => 'AC',
                'icon' => 'snowflake',
                'description' => 'Pendingin ruangan untuk kenyamanan bekerja',
            ],
            [
                'name' => 'Proyektor',
                'icon' => 'projector',
                'description' => 'Proyektor untuk presentasi dan meeting',
            ],
            [
                'name' => 'Sound System',
                'icon' => 'volume-high',
                'description' => 'Sistem audio untuk kebutuhan presentasi',
            ],
            [
                'name' => 'Whiteboard',
                'icon' => 'presentation-chart',
                'description' => 'Papan tulis untuk brainstorming dan diskusi',
            ],
            [
                'name' => 'Printer',
                'icon' => 'printer',
                'description' => 'Fasilitas cetak dokumen',
            ],
            [
                'name' => 'Coffee Corner',
                'icon' => 'coffee',
                'description' => 'Area kopi dan minuman gratis',
            ],
            [
                'name' => 'Meeting Room',
                'icon' => 'users',
                'description' => 'Ruang meeting terpisah untuk diskusi privat',
            ],
            [
                'name' => 'Parking Area',
                'icon' => 'car',
                'description' => 'Area parkir untuk kendaraan',
            ],
            [
                'name' => '24/7 Access',
                'icon' => 'clock',
                'description' => 'Akses 24 jam untuk fleksibilitas kerja',
            ],
        ];

        foreach ($facilities as $facility) {
            \App\Models\Facility::create($facility);
        }
    }
}
