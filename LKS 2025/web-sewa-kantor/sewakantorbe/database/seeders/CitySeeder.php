<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class CitySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $cities = [
            [
                'name' => 'Jakarta',
                'slug' => 'jakarta',
                'description' => 'Ibu kota Indonesia dengan berbagai pilihan kantor modern',
                'photo' => 'cities/jakarta.jpg',
            ],
            [
                'name' => 'Surabaya',
                'slug' => 'surabaya',
                'description' => 'Kota terbesar kedua di Indonesia dengan pusat bisnis yang berkembang',
                'photo' => 'cities/surabaya.jpg',
            ],
            [
                'name' => 'Bandung',
                'slug' => 'bandung',
                'description' => 'Kota kreatif dengan suasana yang sejuk dan nyaman untuk bekerja',
                'photo' => 'cities/bandung.jpg',
            ],
            [
                'name' => 'Yogyakarta',
                'slug' => 'yogyakarta',
                'description' => 'Kota pelajar dengan biaya sewa yang terjangkau',
                'photo' => 'cities/yogyakarta.jpg',
            ],
            [
                'name' => 'Semarang',
                'slug' => 'semarang',
                'description' => 'Kota pelabuhan dengan akses transportasi yang strategis',
                'photo' => 'cities/semarang.jpg',
            ],
        ];

        foreach ($cities as $city) {
            \App\Models\City::create($city);
        }
    }
}
