<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Jobs\AutoCompleteOrder;
use App\Models\MarketplaceListing;
use App\Models\Order;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class OrderController extends Controller
{
    // ─────────────────────────────────────────────────────────────
    // GET /api/orders
    // Returns all orders for the authenticated buyer, newest first
    // ─────────────────────────────────────────────────────────────
    public function index(Request $request): JsonResponse
    {
        $orders = $request->user()
            ->orders()
            ->with('listing')
            ->latest()
            ->get()
            ->map(fn ($order) => $this->formatOrder($order));

        return response()->json(['data' => $orders]);
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/orders
    // Create an order (buy a listing)
    // Body: { listing_id, quantity?, notes?, shipping_address }
    // ─────────────────────────────────────────────────────────────
    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'listing_id'       => ['required', 'integer', 'exists:marketplace_listings,id'],
            'quantity'         => ['nullable', 'integer', 'min:1', 'max:10'],
            'notes'            => ['nullable', 'string', 'max:500'],
            'shipping_address' => ['required', 'string', 'max:500'],
        ]);

        $listing = MarketplaceListing::active()->findOrFail($validated['listing_id']);

        // Prevent buyer from buying their own listing
        if ($listing->seller_id === $request->user()->id) {
            return response()->json([
                'message' => 'Kamu tidak bisa membeli barangmu sendiri.',
            ], 422);
        }

        $quantity = $validated['quantity'] ?? 1;

        DB::beginTransaction();
        try {
            $order = Order::create([
                'buyer_id'         => $request->user()->id,
                'listing_id'       => $listing->id,
                'status'           => 'pending',
                'total_price'      => $listing->price * $quantity,
                'quantity'         => $quantity,
                'notes'            => $validated['notes'] ?? null,
                'shipping_address' => $validated['shipping_address'],
            ]);

            // Mark listing as sold after purchase
            $listing->update(['is_sold' => true]);

            DB::commit();

            $order->load('listing');

            return response()->json([
                'message' => 'Pesanan berhasil dibuat.',
                'data'    => $this->formatOrder($order),
            ], 201);
        } catch (\Throwable $e) {
            DB::rollBack();
            return response()->json(['message' => 'Terjadi kesalahan. Silakan coba lagi.'], 500);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // GET /api/orders/{id}
    // Returns a single order (buyer only)
    // ─────────────────────────────────────────────────────────────
    public function show(Request $request, int $id): JsonResponse
    {
        $order = $request->user()
            ->orders()
            ->with('listing')
            ->findOrFail($id);

        return response()->json(['data' => $this->formatOrder($order)]);
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/orders/{id}/pay
    // Simulate payment: moves order from pending → confirmed
    // Body: { payment_method, payment_proof? }
    // ─────────────────────────────────────────────────────────────
    public function pay(Request $request, int $id): JsonResponse
    {
        $order = $request->user()->orders()->with('listing')->findOrFail($id);

        if (!$order->isPending()) {
            return response()->json([
                'message' => 'Hanya pesanan dengan status pending yang bisa dibayar.',
            ], 422);
        }

        $validated = $request->validate([
            'payment_method' => ['required', 'in:transfer,ewallet,cod'],
            'payment_proof'  => ['nullable', 'string', 'max:500'],
        ]);

        $order->update([
            'status'       => 'confirmed',
            'confirmed_at' => now(),
        ]);

        // Otomatis ubah status ke "completed" setelah 1 menit
        AutoCompleteOrder::dispatch($order)->delay(now()->addMinute());

        $order->refresh()->load('listing');

        return response()->json([
            'message' => 'Pembayaran berhasil! Pesanan sedang diproses.',
            'data'    => $this->formatOrder($order),
        ]);
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/orders/{id}/cancel
    // Cancel a pending order
    // Body: { reason? }
    // ─────────────────────────────────────────────────────────────
    public function cancel(Request $request, int $id): JsonResponse
    {
        $order = $request->user()->orders()->findOrFail($id);

        if (!$order->isPending()) {
            return response()->json([
                'message' => 'Hanya pesanan dengan status pending yang bisa dibatalkan.',
            ], 422);
        }

        $validated = $request->validate([
            'reason' => ['nullable', 'string', 'max:500'],
        ]);

        DB::beginTransaction();
        try {
            $order->update([
                'status'              => 'cancelled',
                'cancelled_at'        => now(),
                'cancellation_reason' => $validated['reason'] ?? null,
            ]);

            // Re-activate listing so it can be bought again
            $order->listing()->update(['is_sold' => false]);

            DB::commit();

            $order->load('listing');

            return response()->json([
                'message' => 'Pesanan berhasil dibatalkan.',
                'data'    => $this->formatOrder($order),
            ]);
        } catch (\Throwable $e) {
            DB::rollBack();
            return response()->json(['message' => 'Terjadi kesalahan. Silakan coba lagi.'], 500);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Helper: format order into consistent array shape
    // ─────────────────────────────────────────────────────────────
    private function formatOrder(Order $order): array
    {
        $listing = $order->listing;

        return [
            'id'               => (string) $order->id,
            'status'           => $order->status,
            'total_price'      => $order->total_price,
            'quantity'         => $order->quantity,
            'notes'            => $order->notes,
            'shipping_address' => $order->shipping_address,
            'cancellation_reason' => $order->cancellation_reason,
            'ordered_at'       => $order->created_at?->format('d M Y'),
            'confirmed_at'     => $order->confirmed_at?->format('d M Y'),
            'shipped_at'       => $order->shipped_at?->format('d M Y'),
            'completed_at'     => $order->completed_at?->format('d M Y'),
            'cancelled_at'     => $order->cancelled_at?->format('d M Y'),
            'estimated_arrival' => $order->shipped_at
                ? $order->shipped_at->addDays(3)->format('d M Y')
                : null,
            'listing' => $listing ? [
                'id'            => (string) $listing->id,
                'name'          => $listing->name,
                'price'         => $listing->price,
                'seller_name'   => $listing->seller_name,
                'seller_rating' => $listing->seller_rating,
                'description'   => $listing->description,
                'category'      => $listing->category,
                'condition'     => $listing->condition,
                'image_url'     => $listing->image_path
                    ? asset('storage/' . $listing->image_path)
                    : null,
                'is_wishlisted' => false,
                'is_sold'       => $listing->is_sold,
            ] : null,
        ];
    }
}
