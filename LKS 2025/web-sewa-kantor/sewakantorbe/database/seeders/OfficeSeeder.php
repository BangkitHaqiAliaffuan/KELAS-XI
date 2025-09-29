<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class OfficeSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $offices = [
            // Jakarta
            [
                'name' => 'Jakarta Corporate Center',
                'slug' => 'jakarta-corporate-center',
                'description' => 'Kantor modern di pusat bisnis Jakarta dengan fasilitas lengkap dan akses mudah ke transportasi umum.',
                'address' => 'Jl. Sudirman No. 123, Jakarta Pusat, DKI Jakarta',
                'latitude' => '-6.208763',
                'longitude' => '106.845599',
                'capacity' => 50,
                'price_per_day' => 150000,
                'price_per_week' => 900000,
                'price_per_month' => 3500000,
                'photos' => ['offices/jakarta-1-1.jpg', 'offices/jakarta-1-2.jpg', 'offices/jakarta-1-3.jpg'],
                'status' => 'available',
                'operating_hours' => '08:00-18:00',
                'city_id' => 1,
                'rating' => 4.5,
            ],
            [
                'name' => 'South Jakarta Business Hub',
                'slug' => 'south-jakarta-business-hub',
                'description' => 'Ruang kerja premium di area Jakarta Selatan dengan pemandangan kota yang menakjubkan.',
                'address' => 'Jl. Gatot Subroto No. 456, Jakarta Selatan, DKI Jakarta',
                'latitude' => '-6.2297',
                'longitude' => '106.8261',
                'capacity' => 30,
                'price_per_day' => 120000,
                'price_per_week' => 750000,
                'price_per_month' => 2800000,
                'photos' => ['offices/jakarta-2-1.jpg', 'offices/jakarta-2-2.jpg'],
                'status' => 'available',
                'operating_hours' => '07:00-19:00',
                'city_id' => 1,
                'rating' => 4.2,
            ],
            // Surabaya
            [
                'name' => 'Surabaya Tech Park',
                'slug' => 'surabaya-tech-park',
                'description' => 'Kantor teknologi modern dengan fasilitas co-working space yang nyaman.',
                'address' => 'Jl. Pemuda No. 789, Surabaya, Jawa Timur',
                'latitude' => '-7.2575',
                'longitude' => '112.7521',
                'capacity' => 40,
                'price_per_day' => 100000,
                'price_per_week' => 600000,
                'price_per_month' => 2200000,
                'photos' => ['offices/surabaya-1-1.jpg', 'offices/surabaya-1-2.jpg'],
                'status' => 'available',
                'operating_hours' => '08:00-17:00',
                'city_id' => 2,
                'rating' => 4.3,
            ],
            // Bandung
            [
                'name' => 'Bandung Creative Space',
                'slug' => 'bandung-creative-space',
                'description' => 'Ruang kerja kreatif di jantung kota Bandung dengan atmosfer yang inspiratif.',
                'address' => 'Jl. Asia Afrika No. 321, Bandung, Jawa Barat',
                'latitude' => '-6.9175',
                'longitude' => '107.6191',
                'capacity' => 25,
                'price_per_day' => 80000,
                'price_per_week' => 500000,
                'price_per_month' => 1800000,
                'photos' => ['offices/bandung-1-1.jpg', 'offices/bandung-1-2.jpg'],
                'status' => 'available',
                'operating_hours' => '09:00-18:00',
                'city_id' => 3,
                'rating' => 4.1,
            ],
            // Yogyakarta
            [
                'name' => 'Jogja Student Hub',
                'slug' => 'jogja-student-hub',
                'description' => 'Co-working space yang terjangkau untuk mahasiswa dan startup muda.',
                'address' => 'Jl. Malioboro No. 654, Yogyakarta, DIY',
                'latitude' => '-7.7956',
                'longitude' => '110.3695',
                'capacity' => 20,
                'price_per_day' => 60000,
                'price_per_week' => 350000,
                'price_per_month' => 1200000,
                'photos' => ['offices/yogya-1-1.jpg', 'offices/yogya-1-2.jpg'],
                'status' => 'available',
                'operating_hours' => '08:00-20:00',
                'city_id' => 4,
                'rating' => 4.0,
            ],
        ];

        foreach ($offices as $office) {
            $createdOffice = \App\Models\Office::create($office);

            // Attach random facilities to each office
            $facilityIds = \App\Models\Facility::pluck('id')->random(rand(5, 8));
            $createdOffice->facilities()->attach($facilityIds);
        }
    }
}
