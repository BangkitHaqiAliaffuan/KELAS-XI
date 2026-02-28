<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Courier;
use App\Models\PickupRequest;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Validation\Rule;

class CourierController extends Controller
{
    // ─────────────────────────────────────────────────────────────
    // GET /api/courier/me
    // Returns the authenticated courier's profile.
    // ─────────────────────────────────────────────────────────────
    public function me(Request $request): JsonResponse
    {
        /** @var Courier $courier */
        $courier = $request->user();

        return response()->json([
            'courier' => $this->courierResource($courier),
        ]);
    }

    // ─────────────────────────────────────────────────────────────
    // GET /api/courier/pickups
    // Returns all pickup requests assigned to this courier,
    // ordered by pickup_date asc (upcoming first).
    // ─────────────────────────────────────────────────────────────
    public function pickups(Request $request): JsonResponse
    {
        /** @var Courier $courier */
        $courier = $request->user();

        $pickups = PickupRequest::with(['items.wasteCategory', 'user'])
            ->where('courier_id', $courier->id)
            ->orderByRaw("FIELD(status, 'on_the_way', 'pending', 'done', 'cancelled')")
            ->orderBy('pickup_date', 'asc')
            ->orderBy('pickup_time', 'asc')
            ->get()
            ->map(fn ($p) => $this->formatPickup($p));

        return response()->json(['data' => $pickups]);
    }

    // ─────────────────────────────────────────────────────────────
    // PATCH /api/courier/pickups/{id}/status
    // Body: { status: "on_the_way" | "done" }
    // Courier can only advance status forward; cannot cancel.
    // ─────────────────────────────────────────────────────────────
    public function updateStatus(Request $request, int $id): JsonResponse
    {
        /** @var Courier $courier */
        $courier = $request->user();

        $pickup = PickupRequest::where('courier_id', $courier->id)
            ->findOrFail($id);

        $validated = $request->validate([
            'status' => ['required', Rule::in(['on_the_way', 'done'])],
        ]);

        $newStatus = $validated['status'];

        // Guard against going backwards
        $order = ['pending' => 0, 'on_the_way' => 1, 'done' => 2, 'cancelled' => 3];
        if (($order[$newStatus] ?? 0) <= ($order[$pickup->status] ?? 0)) {
            return response()->json([
                'message' => "Tidak bisa mengubah status dari '{$pickup->status}' ke '{$newStatus}'.",
            ], 422);
        }

        $updates = ['status' => $newStatus];

        if ($newStatus === 'done') {
            $updates['completed_at'] = now();
            $updates['points_awarded'] = 10; // base reward points for user

            // Award points to the customer
            $pickup->user()->increment('points_balance', $updates['points_awarded']);

            // Free up courier availability when all their pickups are done
            $remainingActive = PickupRequest::where('courier_id', $courier->id)
                ->whereIn('status', ['pending', 'on_the_way'])
                ->where('id', '!=', $pickup->id)
                ->count();

            if ($remainingActive === 0) {
                $courier->update(['status' => 'active', 'is_available' => true]);
            }
        }

        $pickup->update($updates);

        return response()->json([
            'message' => 'Status pickup berhasil diperbarui.',
            'data'    => $this->formatPickup($pickup->fresh(['items.wasteCategory', 'user'])),
        ]);
    }

    // ─────────────────────────────────────────────────────────────
    // PATCH /api/courier/availability
    // Body: { is_available: true | false }
    // Toggle courier availability (e.g. going offline)
    // ─────────────────────────────────────────────────────────────
    public function availability(Request $request): JsonResponse
    {
        /** @var Courier $courier */
        $courier = $request->user();

        $validated = $request->validate([
            'is_available' => ['required', 'boolean'],
        ]);

        $courier->update(['is_available' => $validated['is_available']]);

        return response()->json([
            'message'      => $validated['is_available'] ? 'Kamu sekarang online.' : 'Kamu sekarang offline.',
            'is_available' => (bool) $courier->is_available,
        ]);
    }

    // ─────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────

    private function courierResource(Courier $courier): array
    {
        return [
            'id'               => $courier->id,
            'name'             => $courier->name,
            'email'            => $courier->email,
            'phone'            => $courier->phone,
            'avatar_path'      => $courier->avatar_path,
            'vehicle_type'     => $courier->vehicle_type,
            'vehicle_plate'    => $courier->vehicle_plate,
            'status'           => $courier->status,
            'is_available'     => (bool) $courier->is_available,
            'rating'           => (float) $courier->rating,
            'total_deliveries' => (int) $courier->total_deliveries,
        ];
    }

    private function formatPickup(PickupRequest $pickup): array
    {
        return [
            'id'                  => $pickup->id,
            'status'              => $pickup->status,
            'address'             => $pickup->address,
            'latitude'            => $pickup->latitude,
            'longitude'           => $pickup->longitude,
            'pickup_date'         => $pickup->pickup_date?->format('Y-m-d'),
            'pickup_time'         => $pickup->pickup_time,
            'notes'               => $pickup->notes,
            'estimated_weight_kg' => $pickup->estimated_weight_kg,
            'points_awarded'      => $pickup->points_awarded,
            'completed_at'        => $pickup->completed_at?->toIso8601String(),
            'cancelled_at'        => $pickup->cancelled_at?->toIso8601String(),
            'cancellation_reason' => $pickup->cancellation_reason,
            'created_at'          => $pickup->created_at?->toIso8601String(),
            'customer'            => $pickup->user ? [
                'id'    => $pickup->user->id,
                'name'  => $pickup->user->name,
                'phone' => $pickup->user->phone,
            ] : null,
            'trash_types' => $pickup->items->map(fn ($item) => [
                'id'    => $item->wasteCategory->id,
                'type'  => $item->wasteCategory->type,
                'label' => $item->wasteCategory->label,
                'emoji' => $item->wasteCategory->emoji,
            ])->values()->toArray(),
        ];
    }
}
