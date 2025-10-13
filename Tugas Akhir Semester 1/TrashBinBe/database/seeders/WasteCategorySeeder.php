<?php

namespace Database\Seeders;

use App\Models\WasteCategory;
use Illuminate\Database\Seeder;

class WasteCategorySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $categories = [
            [
                'name' => 'Kain Perca',
                'slug' => 'kain-perca',
                'description' => 'Sisa kain dari proses jahit menjahit atau produksi tekstil',
                'unit' => 'kg',
                'base_price_per_unit' => 3000.00,
                'icon_url' => 'https://example.com/icons/fabric.png',
                'is_active' => true,
            ],
            [
                'name' => 'Plastik PET',
                'slug' => 'plastik-pet',
                'description' => 'Botol plastik PET yang bisa didaur ulang',
                'unit' => 'kg',
                'base_price_per_unit' => 4000.00,
                'icon_url' => 'https://example.com/icons/plastic.png',
                'is_active' => true,
            ],
            [
                'name' => 'Kardus/Kertas',
                'slug' => 'kardus-kertas',
                'description' => 'Kardus bekas dan kertas yang bisa didaur ulang',
                'unit' => 'kg',
                'base_price_per_unit' => 2000.00,
                'icon_url' => 'https://example.com/icons/paper.png',
                'is_active' => true,
            ],
            [
                'name' => 'Kaleng Aluminium',
                'slug' => 'kaleng-aluminium',
                'description' => 'Kaleng minuman dan bahan makanan dari aluminium',
                'unit' => 'kg',
                'base_price_per_unit' => 15000.00,
                'icon_url' => 'https://example.com/icons/metal.png',
                'is_active' => true,
            ],
            [
                'name' => 'Botol Kaca',
                'slug' => 'botol-kaca',
                'description' => 'Botol kaca bekas yang bisa didaur ulang',
                'unit' => 'kg',
                'base_price_per_unit' => 1500.00,
                'icon_url' => 'https://example.com/icons/glass.png',
                'is_active' => true,
            ],
            [
                'name' => 'Elektronik Bekas',
                'slug' => 'elektronik-bekas',
                'description' => 'Perangkat elektronik bekas yang bisa didaur ulang',
                'unit' => 'pcs',
                'base_price_per_unit' => 10000.00,
                'icon_url' => 'https://example.com/icons/electronic.png',
                'is_active' => true,
            ],
            [
                'name' => 'Besi/Logam',
                'slug' => 'besi-logam',
                'description' => 'Sisa logam besi yang bisa didaur ulang',
                'unit' => 'kg',
                'base_price_per_unit' => 5000.00,
                'icon_url' => 'https://example.com/icons/metal_scrap.png',
                'is_active' => true,
            ],
        ];

        foreach ($categories as $category) {
            WasteCategory::create($category);
        }
    }
}
