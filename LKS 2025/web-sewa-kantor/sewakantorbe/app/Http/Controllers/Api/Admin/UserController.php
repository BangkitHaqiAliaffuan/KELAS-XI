<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Http\Resources\UserResource;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\Rule;

class UserController extends Controller
{
    /**
     * Display a listing of users.
     */
    public function index(Request $request): JsonResponse
    {
        try {
            // Validate input parameters
            $request->validate([
                'search' => 'nullable|string|max:255',
                'status' => 'nullable|in:active,inactive',
                'per_page' => 'nullable|integer|min:1|max:100'
            ]);

            $query = User::query();

            // Search by name or email
            if ($request->filled('search')) {
                $search = $request->search;
                $query->where(function ($q) use ($search) {
                    $q->where('name', 'like', "%{$search}%")
                      ->orWhere('email', 'like', "%{$search}%");
                });
            }

            // Filter by email verification status
            if ($request->filled('status')) {
                if ($request->status === 'active') {
                    $query->whereNotNull('email_verified_at');
                } else {
                    $query->whereNull('email_verified_at');
                }
            }

            $perPage = $request->input('per_page', 15);
            $users = $query->orderBy('created_at', 'desc')->paginate($perPage);

            return response()->json([
                'success' => true,
                'data' => UserResource::collection($users->items()),
                'pagination' => [
                    'current_page' => $users->currentPage(),
                    'last_page' => $users->lastPage(),
                    'per_page' => $users->perPage(),
                    'total' => $users->total(),
                ],
                'message' => 'Users retrieved successfully'
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to retrieve users',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Display the specified user.
     */
    public function show(User $user): JsonResponse
    {
        try {
            return response()->json([
                'success' => true,
                'data' => new UserResource($user->load('transactions')),
                'message' => 'User retrieved successfully'
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'User not found',
                'error' => $e->getMessage()
            ], 404);
        }
    }

    /**
     * Update the specified user.
     */
    public function update(Request $request, User $user): JsonResponse
    {
        try {
            $validatedData = $request->validate([
                'name' => 'required|string|max:255',
                'email' => [
                    'required',
                    'email',
                    Rule::unique('users')->ignore($user->id)
                ],
                'password' => 'nullable|string|min:8',
                'phone' => 'nullable|string|max:20',
                'address' => 'nullable|string|max:500',
                'is_active' => 'boolean'
            ]);

            // Update user data
            $user->name = $validatedData['name'];
            $user->email = $validatedData['email'];
            
            if (!empty($validatedData['password'])) {
                $user->password = Hash::make($validatedData['password']);
            }

            if (isset($validatedData['phone'])) {
                $user->phone = $validatedData['phone'];
            }

            if (isset($validatedData['address'])) {
                $user->address = $validatedData['address'];
            }

            // Handle email verification status
            if (isset($validatedData['is_active'])) {
                if ($validatedData['is_active']) {
                    $user->email_verified_at = $user->email_verified_at ?? now();
                } else {
                    $user->email_verified_at = null;
                }
            }

            $user->save();

            return response()->json([
                'success' => true,
                'data' => new UserResource($user),
                'message' => 'User updated successfully'
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to update user',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Remove the specified user.
     */
    public function destroy(User $user): JsonResponse
    {
        try {
            // Check if user has active transactions
            $activeTransactions = $user->transactions()
                ->whereIn('status', ['confirmed', 'pending'])
                ->count();

            if ($activeTransactions > 0) {
                return response()->json([
                    'success' => false,
                    'message' => 'Cannot delete user with active transactions'
                ], 400);
            }

            $user->delete();

            return response()->json([
                'success' => true,
                'message' => 'User deleted successfully'
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to delete user',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Toggle user status (active/inactive)
     */
    public function toggleStatus(User $user): JsonResponse
    {
        try {
            if ($user->email_verified_at) {
                $user->email_verified_at = null;
                $message = 'User deactivated successfully';
            } else {
                $user->email_verified_at = now();
                $message = 'User activated successfully';
            }

            $user->save();

            return response()->json([
                'success' => true,
                'data' => new UserResource($user),
                'message' => $message
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to toggle user status',
                'error' => $e->getMessage()
            ], 500);
        }
    }
}