<?php

namespace Database\Seeders;

use App\Models\PickupRequest;
use App\Models\PickupItem;
use App\Models\User;
use App\Models\WasteCategory;
use Illuminate\Database\Seeder;

class PickupRequestSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $user = User::where('role', 'user')->first();
        $collector = User::where('role', 'collector')->first();
        $categories = WasteCategory::all();

        if ($user && $collector && $categories->count() > 0) {
            // Create a pickup request
            $pickup = PickupRequest::create([
                'user_id' => $user->id,
                'collector_id' => $collector->id,
                'pickup_address' => 'Jl. Contoh No. 123, Jakarta',
                'pickup_lat' => -6.2146,
                'pickup_lng' => 106.8451,
                'scheduled_date' => now()->addDay(),
                'status' => 'completed',
                'total_weight' => 5.5,
                'total_price' => 22000.00,
                'notes' => 'Silakan datang sesuai jadwal',
            ]);

            // Add pickup items
            PickupItem::create([
                'pickup_request_id' => $pickup->id,
                'waste_category_id' => $categories[0]->id, // Kain Perca
                'estimated_weight' => 2.0,
                'actual_weight' => 2.0,
                'price_per_unit' => 3000.00,
                'subtotal' => 6000.00,
            ]);

            PickupItem::create([
                'pickup_request_id' => $pickup->id,
                'waste_category_id' => $categories[1]->id, // Plastik PET
                'estimated_weight' => 3.5,
                'actual_weight' => 3.5,
                'price_per_unit' => 4000.00,
                'subtotal' => 14000.00,
            ]);

            // Create another pickup request (pending)
            $pickup2 = PickupRequest::create([
                'user_id' => $user->id,
                'pickup_address' => 'Jl. Contoh 2 No. 456, Jakarta',
                'pickup_lat' => -6.2156,
                'pickup_lng' => 106.8461,
                'scheduled_date' => now()->addDays(2),
                'status' => 'pending',
                'notes' => 'Mohon datang sore hari',
            ]);

            PickupItem::create([
                'pickup_request_id' => $pickup2->id,
                'waste_category_id' => $categories[2]->id, // Kardus/Kertas
                'estimated_weight' => 1.0,
                'price_per_unit' => 2000.00,
                'subtotal' => 2000.00,
            ]);
        }
    }
}
