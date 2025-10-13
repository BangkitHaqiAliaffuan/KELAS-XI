<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\MarketplaceListing;
use App\Models\Order;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class MarketplaceOrderController extends Controller
{
    /**
     * Display a listing of orders for the authenticated user
     */
    public function index(Request $request)
    {
        $asBuyer = Order::where('buyer_id', $request->user()->id)
            ->with(['listing:id,title,price_per_unit,photos,seller_id', 'listing.seller:id,name,avatar', 'seller:id,name,avatar'])
            ->orderBy('created_at', 'desc')
            ->get();

        $asSeller = Order::where('seller_id', $request->user()->id)
            ->with(['listing:id,title,price_per_unit,photos,buyer_id', 'listing.buyer:id,name,avatar', 'buyer:id,name,avatar'])
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json([
            'as_buyer' => $asBuyer,
            'as_seller' => $asSeller
        ]);
    }

    /**
     * Store a newly created order
     */
    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'listing_id' => 'required|exists:marketplace_listings,id',
            'quantity' => 'required|numeric|min:0.01',
            'shipping_address' => 'required_if:delivery_method,shipping|string',
            'notes' => 'nullable|string|max:500',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $listing = MarketplaceListing::where('id', $request->listing_id)
            ->where('status', 'available')
            ->where('expires_at', '>', now())
            ->firstOrFail();

        if ($listing->quantity < $request->quantity) {
            return response()->json([
                'message' => 'Insufficient quantity available'
            ], 400);
        }

        DB::beginTransaction();

        try {
            // Create the order
            $order = Order::create([
                'listing_id' => $request->listing_id,
                'buyer_id' => $request->user()->id,
                'seller_id' => $listing->seller_id,
                'quantity' => $request->quantity,
                'total_price' => $request->quantity * $listing->price_per_unit,
                'shipping_address' => $request->shipping_address,
                'notes' => $request->notes,
                'status' => 'pending',
                'payment_status' => 'unpaid',
            ]);

            // Update listing quantity and mark as reserved
            $newQuantity = $listing->quantity - $request->quantity;
            if ($newQuantity <= 0) {
                $listing->update([
                    'quantity' => 0,
                    'status' => 'reserved'
                ]);
            } else {
                $listing->update([
                    'quantity' => $newQuantity
                ]);
            }

            $order->load(['listing', 'buyer', 'seller']);

            DB::commit();

            return response()->json([
                'message' => 'Order created successfully',
                'data' => $order
            ], 201);
        } catch (\Exception $e) {
            DB::rollback();
            return response()->json([
                'message' => 'Failed to create order',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Display the specified order
     */
    public function show($id, Request $request)
    {
        $order = Order::where('id', $id)
            ->where(function($query) use ($request) {
                $query->where('buyer_id', $request->user()->id)
                      ->orWhere('seller_id', $request->user()->id);
            })
            ->with(['listing', 'buyer', 'seller'])
            ->firstOrFail();

        return response()->json([
            'data' => $order
        ]);
    }

    /**
     * Confirm the order (seller action)
     */
    public function confirmOrder($id, Request $request)
    {
        $order = Order::where('id', $id)
            ->where('seller_id', $request->user()->id)
            ->where('status', 'pending')
            ->firstOrFail();

        $order->update([
            'status' => 'confirmed'
        ]);

        $order->refresh();
        $order->load(['listing', 'buyer', 'seller']);

        return response()->json([
            'message' => 'Order confirmed successfully',
            'data' => $order
        ]);
    }

    /**
     * Update order to shipping status
     */
    public function shipOrder($id, Request $request)
    {
        $order = Order::where('id', $id)
            ->where('seller_id', $request->user()->id)
            ->where('status', 'confirmed')
            ->firstOrFail();

        $order->update([
            'status' => 'shipping'
        ]);

        $order->refresh();
        $order->load(['listing', 'buyer', 'seller']);

        return response()->json([
            'message' => 'Order shipped successfully',
            'data' => $order
        ]);
    }

    /**
     * Complete the order
     */
    public function completeOrder($id, Request $request)
    {
        $order = Order::where('id', $id)
            ->where('buyer_id', $request->user()->id)
            ->where('status', 'shipping')
            ->firstOrFail();

        $order->update([
            'status' => 'completed'
        ]);

        // Update the listing status if all quantity was sold
        $listing = $order->listing;
        if ($listing->quantity <= 0) {
            $listing->update(['status' => 'sold']);
        }

        $order->refresh();
        $order->load(['listing', 'buyer', 'seller']);

        return response()->json([
            'message' => 'Order completed successfully',
            'data' => $order
        ]);
    }

    /**
     * Cancel the order
     */
    public function cancelOrder($id, Request $request)
    {
        $order = Order::where('id', $id)
            ->where(function($query) use ($request) {
                $query->where('buyer_id', $request->user()->id)
                      ->orWhere('seller_id', $request->user()->id);
            })
            ->whereIn('status', ['pending', 'confirmed'])
            ->firstOrFail();

        if ($order->buyer_id == $request->user()->id && $order->status !== 'pending') {
            return response()->json([
                'message' => 'Cannot cancel order that is already confirmed'
            ], 400);
        }

        DB::beginTransaction();

        try {
            // Update order status to cancelled
            $order->update([
                'status' => 'cancelled',
                'payment_status' => 'refunded'
            ]);

            // Return quantity back to listing
            $listing = $order->listing;
            $listing->increment('quantity', $order->quantity);
            
            // If listing was fully reserved, change back to available
            if ($listing->status === 'reserved' && $listing->quantity > 0) {
                $listing->update(['status' => 'available']);
            }

            $order->refresh();
            $order->load(['listing', 'buyer', 'seller']);

            DB::commit();

            return response()->json([
                'message' => 'Order cancelled successfully',
                'data' => $order
            ]);
        } catch (\Exception $e) {
            DB::rollback();
            return response()->json([
                'message' => 'Failed to cancel order',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Add a review to the order
     */
    public function review($id, Request $request)
    {
        $validator = Validator::make($request->all(), [
            'rating' => 'required|integer|min:1|max:5',
            'review' => 'required|string|max:500'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $order = Order::where('id', $id)
            ->where('buyer_id', $request->user()->id)
            ->where('status', 'completed')
            ->whereNull('rating') // Only allow one review
            ->firstOrFail();

        $order->update([
            'rating' => $request->rating,
            'review' => $request->review
        ]);

        $order->refresh();
        $order->load(['listing', 'buyer', 'seller']);

        return response()->json([
            'message' => 'Review added successfully',
            'data' => $order
        ]);
    }
}
