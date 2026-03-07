package com.kelasxi.myapplication.data.network

import android.content.Context
import android.net.Uri
import com.kelasxi.myapplication.model.Order
import com.kelasxi.myapplication.model.OrderStatus
import com.kelasxi.myapplication.model.Product
import com.kelasxi.myapplication.model.ProductCategory
import com.kelasxi.myapplication.model.ProductCondition
import com.kelasxi.myapplication.model.SalesSummary
import com.kelasxi.myapplication.model.SalesTransaction
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class MarketplaceRepository(private val context: Context) {

    private val api = RetrofitClient.api

    // ─────────────────────────────────────────────────────────────
    // GET /api/marketplace  →  list of Product (domain model)
    // ─────────────────────────────────────────────────────────────
    suspend fun getListings(
        category: String? = null,
        search: String? = null
    ): AuthResult<List<Product>> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val response = api.getListings(
                bearer   = "Bearer $token",
                category = category?.lowercase()?.takeIf { it != "all" },
                search   = search?.ifBlank { null }
            )
            if (response.isSuccessful) {
                val listings = response.body()!!.data.map { it.toDomain() }
                AuthResult.Success(listings)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // GET /api/marketplace/{id}  →  single Product
    // ─────────────────────────────────────────────────────────────
    suspend fun getListing(id: Long): AuthResult<Product> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val response = api.getListing("Bearer $token", id)
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/marketplace  →  create a new listing
    // ─────────────────────────────────────────────────────────────
    suspend fun createListing(
        name: String,
        description: String,
        price: Long,
        category: String,
        condition: String,
        imageUri: Uri? = null
    ): AuthResult<Product> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val textType = "text/plain".toMediaTypeOrNull()
            val imagePart = imageUri?.let { uri ->
                val stream = context.contentResolver.openInputStream(uri)
                    ?: return AuthResult.Error("Tidak bisa membaca file gambar.")
                val bytes = stream.readBytes()
                stream.close()
                val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
                MultipartBody.Part.createFormData(
                    "image", "listing.jpg",
                    bytes.toRequestBody(mime.toMediaTypeOrNull())
                )
            }

            val response = api.createListing(
                bearer      = "Bearer $token",
                name        = name.toRequestBody(textType),
                description = description.toRequestBody(textType),
                price       = price.toString().toRequestBody(textType),
                category    = category.toRequestBody(textType),
                condition   = condition.toRequestBody(textType),
                image       = imagePart
            )
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // GET /api/orders  →  list of Order (domain model)
    // ─────────────────────────────────────────────────────────────
    suspend fun getOrders(): AuthResult<List<Order>> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val response = api.getOrders("Bearer $token")
            if (response.isSuccessful) {
                val orders = response.body()!!.data.map { it.toDomain() }
                AuthResult.Success(orders)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/orders  →  single Order
    // ─────────────────────────────────────────────────────────────
    suspend fun createOrder(
        listingId: Long,
        quantity: Int = 1,
        shippingAddress: String,
        notes: String? = null
    ): AuthResult<Order> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val body = CreateOrderRequest(
                listing_id       = listingId,
                quantity         = quantity,
                notes            = notes?.ifBlank { null },
                shipping_address = shippingAddress
            )

            val response = api.createOrder("Bearer $token", body)
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/orders/{id}/pay  →  Mayar payment link
    // ─────────────────────────────────────────────────────────────
    suspend fun payOrder(id: Long): AuthResult<PayOrderResponse> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")
            val response = api.payOrder(
                bearer = "Bearer $token",
                id     = id
            )
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // GET /api/orders/{id}/payment-status  →  poll Mayar status
    // ─────────────────────────────────────────────────────────────
    suspend fun pollPaymentStatus(id: Long): AuthResult<PaymentStatusResponse> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")
            val response = api.getPaymentStatus("Bearer $token", id)
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/orders/{id}/cancel
    // ─────────────────────────────────────────────────────────────
    suspend fun cancelOrder(id: Long, reason: String? = null): AuthResult<Order> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val response = api.cancelOrder("Bearer $token", id, CancelOrderRequest(reason))
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // GET /api/wishlist  →  list of Product
    // ─────────────────────────────────────────────────────────────
    suspend fun getWishlist(): AuthResult<List<Product>> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val response = api.getWishlist("Bearer $token")
            if (response.isSuccessful) {
                val products = response.body()!!.data.map { it.toDomain() }
                AuthResult.Success(products)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /api/wishlist/toggle  →  wishlisted: Boolean
    // ─────────────────────────────────────────────────────────────
    suspend fun toggleWishlist(listingId: Long): AuthResult<Boolean> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val response = api.toggleWishlist(
                bearer = "Bearer $token",
                body   = ToggleWishlistRequest(listing_id = listingId)
            )
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.wishlisted)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Mappers: DTO  →  Domain
    // ─────────────────────────────────────────────────────────────

    private fun ListingDto.toDomain(): Product = Product(
        id           = id,
        name         = name,
        price        = price,
        sellerName   = seller_name,
        sellerRating = seller_rating,
        description  = description,
        category     = category.toProductCategory(),
        condition    = condition.toProductCondition(),
        imageUrl     = image_url ?: "",
        isWishlisted = is_wishlisted,
        isSold       = is_sold
    )

    private fun OrderDto.toDomain(): Order {
        val product = listing?.toDomain() ?: Product(
            id          = "unknown",
            name        = "Produk tidak tersedia",
            price       = total_price,
            sellerName  = "-",
            sellerRating = 0f,
            description = "",
            category    = ProductCategory.OTHERS,
            condition   = ProductCondition.FAIR
        )
        return Order(
            id               = id,
            product          = product,
            quantity         = quantity,
            totalPrice       = total_price,
            status           = status.toOrderStatus(),
            orderedAt        = ordered_at,
            estimatedArrival = estimated_arrival ?: "",
            shippingAddress  = shipping_address,
            paymentStatus    = payment_status ?: "unpaid",
            mayarPaymentLink = mayar_payment_link,
            mayarPaymentId   = mayar_payment_id,
            paidAt           = paid_at
        )
    }

    private fun String.toProductCategory(): ProductCategory = when (this.lowercase()) {
        "furniture"   -> ProductCategory.FURNITURE
        "electronics" -> ProductCategory.ELECTRONICS
        "clothing"    -> ProductCategory.CLOTHING
        "books"       -> ProductCategory.BOOKS
        else          -> ProductCategory.OTHERS
    }

    private fun String.toProductCondition(): ProductCondition = when (this.lowercase()) {
        "like_new" -> ProductCondition.LIKE_NEW
        "good"     -> ProductCondition.GOOD
        else       -> ProductCondition.FAIR
    }

    private fun String.toOrderStatus(): OrderStatus = when (this.lowercase()) {
        "confirmed"  -> OrderStatus.PROCESSING
        "shipped"    -> OrderStatus.SHIPPED
        "completed"  -> OrderStatus.DELIVERED
        "cancelled"  -> OrderStatus.CANCELLED
        else         -> OrderStatus.WAITING_PAYMENT   // pending
    }

    // ─────────────────────────────────────────────────────────────
    // GET /api/marketplace/mine  →  seller's own listings
    // ─────────────────────────────────────────────────────────────
    suspend fun getMyListings(): AuthResult<List<Product>> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")
            val response = api.getMyListings("Bearer $token")
            if (response.isSuccessful) {
                val listings = response.body()!!.data.map { it.toDomain() }
                AuthResult.Success(listings)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE /api/marketplace/{id}  →  deactivate a listing
    // ─────────────────────────────────────────────────────────────
    suspend fun deleteListing(id: Long): AuthResult<String> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")
            val response = api.deleteListing("Bearer $token", id)
            if (response.isSuccessful) {
                val message = response.body()?.get("message") ?: "Listing dihapus."
                AuthResult.Success(message)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // PUT /api/marketplace/{id}  →  update a listing
    // ─────────────────────────────────────────────────────────────
    suspend fun updateListing(
        id: Long,
        name: String,
        description: String,
        price: Long,
        category: String,
        condition: String,
        imageUri: Uri? = null
    ): AuthResult<Product> {
        return try {
            val token = TokenStore.getToken(context)
                ?: return AuthResult.Error("Belum login. Silakan login kembali.")

            val textType = "text/plain".toMediaTypeOrNull()
            val imagePart = imageUri?.let { uri ->
                val stream = context.contentResolver.openInputStream(uri)
                    ?: return AuthResult.Error("Tidak bisa membaca file gambar.")
                val bytes = stream.readBytes()
                stream.close()
                val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
                MultipartBody.Part.createFormData(
                    "image", "listing.jpg",
                    bytes.toRequestBody(mime.toMediaTypeOrNull())
                )
            }

            val response = api.updateListing(
                bearer      = "Bearer $token",
                id          = id,
                method      = "PUT".toRequestBody(textType),
                name        = name.toRequestBody(textType),
                description = description.toRequestBody(textType),
                price       = price.toString().toRequestBody(textType),
                category    = category.toRequestBody(textType),
                condition   = condition.toRequestBody(textType),
                image       = imagePart
            )
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!.data.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Shared error parser — parses Laravel JSON error responses
    // ─────────────────────────────────────────────────────────────
    private fun parseError(raw: String?): String {
        if (raw.isNullOrBlank()) return "Terjadi kesalahan."
        return try {
            val json = org.json.JSONObject(raw)
            val errors = json.optJSONObject("errors")
            if (errors != null) {
                val firstKey = errors.keys().next()
                errors.getJSONArray(firstKey).getString(0)
            } else {
                json.optString("message", "Terjadi kesalahan.")
            }
        } catch (_: Exception) {
            "Terjadi kesalahan."
        }
    }

    // ─────────────────────────────────────────────────────────────
    // GET /api/orders/sales-transactions
    // Returns Pair<List<SalesTransaction>, SalesSummary>
    // ─────────────────────────────────────────────────────────────
    suspend fun getSalesTransactions(): AuthResult<Pair<List<SalesTransaction>, SalesSummary>> {
        val token = TokenStore.getToken(context)
            ?: return AuthResult.Error("Belum login. Silakan login kembali.")
        return try {
            val response = api.getSalesTransactions("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()!!
                val list = body.data.map { dto ->
                    SalesTransaction(
                        id            = dto.id,
                        transactionId = dto.transactionId ?: dto.id,
                        status        = dto.status,
                        mayarStatus   = dto.mayarStatus,
                        amount        = dto.amount,
                        customerName  = dto.customerName,
                        customerEmail = dto.customerEmail ?: "",
                        description   = dto.description ?: "",
                        createdAt     = dto.createdAt ?: ""
                    )
                }
                val s = body.summary
                val summary = SalesSummary(
                    totalTransactions = s.totalTransactions,
                    totalPaid         = s.totalPaid,
                    totalUnpaid       = s.totalUnpaid,
                    totalRevenue      = s.totalRevenue
                )
                AuthResult.Success(Pair(list, summary))
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Tidak dapat terhubung ke server.")
        }
    }
}
