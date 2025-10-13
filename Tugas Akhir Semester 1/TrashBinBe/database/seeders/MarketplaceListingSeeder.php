<?php

namespace Database\Seeders;

use App\Models\MarketplaceListing;
use App\Models\User;
use App\Models\WasteCategory;
use Illuminate\Database\Seeder;

class MarketplaceListingSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $seller = User::where('role', 'user')->orWhere('role', 'collector')->first();
        $categories = WasteCategory::all();

        if ($seller && $categories->count() > 0) {
            // Create a few marketplace listings
            MarketplaceListing::create([
                'seller_id' => $seller->id,
                'waste_category_id' => $categories[0]->id, // Kain Perca
                'title' => 'Kain Perca Bekas Jumlah Banyak',
                'description' => 'Kain perca bekas dengan berbagai motif dan warna. Cocok untuk kerajinan tangan.',
                'quantity' => 3.5,
                'unit' => 'kg',
                'price_per_unit' => 3200.00,
                'total_price' => 11200.00,
                'condition' => 'clean',
                'location' => 'Jl. Kerajinan No. 15, Jakarta',
                'lat' => -6.2136,
                'lng' => 106.8441,
                'status' => 'available',
                'photos' => [
                    'https://example.com/photos/kain-perca-1.jpg',
                    'https://example.com/photos/kain-perca-2.jpg'
                ],
                'expires_at' => now()->addDays(25),
            ]);

            MarketplaceListing::create([
                'seller_id' => $seller->id,
                'waste_category_id' => $categories[1]->id, // Plastik PET
                'title' => 'Botol Plastik PET Bersih Banyak',
                'description' => 'Botol plastik PET yang sudah dibersihkan, siap didaur ulang.',
                'quantity' => 5.0,
                'unit' => 'kg',
                'price_per_unit' => 4200.00,
                'total_price' => 21000.00,
                'condition' => 'clean',
                'location' => 'Jl. Daur Ulang No. 8, Jakarta',
                'lat' => -6.2146,
                'lng' => 106.8451,
                'status' => 'available',
                'photos' => [
                    'https://example.com/photos/plastik-pet-1.jpg'
                ],
                'expires_at' => now()->addDays(30),
            ]);

            MarketplaceListing::create([
                'seller_id' => $seller->id,
                'waste_category_id' => $categories[2]->id, // Kardus/Kertas
                'title' => 'Kardus Bekas dalam Jumlah Besar',
                'description' => 'Kardus bekas dalam jumlah besar, masih bagus untuk didaur ulang.',
                'quantity' => 8.0,
                'unit' => 'kg',
                'price_per_unit' => 2200.00,
                'total_price' => 17600.00,
                'condition' => 'needs_cleaning',
                'location' => 'Jl. Pengolahan No. 22, Jakarta',
                'lat' => -6.2156,
                'lng' => 106.8461,
                'status' => 'available',
                'photos' => [
                    'https://example.com/photos/kardus-1.jpg',
                    'https://example.com/photos/kardus-2.jpg',
                    'https://example.com/photos/kardus-3.jpg'
                ],
                'expires_at' => now()->addDays(20),
            ]);
        }
    }
}
