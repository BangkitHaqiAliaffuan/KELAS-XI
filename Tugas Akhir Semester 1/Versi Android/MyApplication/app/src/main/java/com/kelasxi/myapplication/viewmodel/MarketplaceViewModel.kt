package com.kelasxi.myapplication.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.myapplication.data.network.AuthResult
import com.kelasxi.myapplication.data.network.CartCheckoutItemRequest
import com.kelasxi.myapplication.data.network.CartCheckoutResponse
import com.kelasxi.myapplication.data.network.MarketplaceRepository
import com.kelasxi.myapplication.model.CartCheckoutGroup
import com.kelasxi.myapplication.model.CartItem
import com.kelasxi.myapplication.model.Order
import com.kelasxi.myapplication.model.Product
import com.kelasxi.myapplication.model.ProductCategory
import com.kelasxi.myapplication.model.SalesSummary
import com.kelasxi.myapplication.model.SalesTransaction
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MarketplaceRepository(application.applicationContext)

    // ── Product list ──────────────────────────────────────────────
    private val _allProducts     = MutableStateFlow<List<Product>>(emptyList())
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    private val _isLoadingProducts = MutableStateFlow(false)
    val isLoadingProducts: StateFlow<Boolean> = _isLoadingProducts.asStateFlow()

    private val _productsError = MutableStateFlow<String?>(null)
    val productsError: StateFlow<String?> = _productsError.asStateFlow()

    // ── Filters ───────────────────────────────────────────────────
    private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ── Selected product (detail screen) ─────────────────────────
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // ── Wishlist (set of product IDs) ─────────────────────────────
    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    private val _isTogglingWishlist = MutableStateFlow(false)
    val isTogglingWishlist: StateFlow<Boolean> = _isTogglingWishlist.asStateFlow()

    private val _wishlistError = MutableStateFlow<String?>(null)
    val wishlistError: StateFlow<String?> = _wishlistError.asStateFlow()

    // Wishlist product list (for WishlistScreen)
    private val _wishlistProducts = MutableStateFlow<List<Product>>(emptyList())
    val wishlistProducts: StateFlow<List<Product>> = _wishlistProducts.asStateFlow()

    private val _isLoadingWishlist = MutableStateFlow(false)
    val isLoadingWishlist: StateFlow<Boolean> = _isLoadingWishlist.asStateFlow()

    // ── Orders ────────────────────────────────────────────────────
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoadingOrders = MutableStateFlow(false)
    val isLoadingOrders: StateFlow<Boolean> = _isLoadingOrders.asStateFlow()

    private val _ordersError = MutableStateFlow<String?>(null)
    val ordersError: StateFlow<String?> = _ordersError.asStateFlow()

    private val _isSubmittingOrder = MutableStateFlow(false)
    val isSubmittingOrder: StateFlow<Boolean> = _isSubmittingOrder.asStateFlow()

    private val _orderSuccess = MutableStateFlow<Order?>(null)
    val orderSuccess: StateFlow<Order?> = _orderSuccess.asStateFlow()

    private val _orderError = MutableStateFlow<String?>(null)
    val orderError: StateFlow<String?> = _orderError.asStateFlow()

    private val _isPayingOrder = MutableStateFlow(false)
    val isPayingOrder: StateFlow<Boolean> = _isPayingOrder.asStateFlow()

    // Holds payment link + id after successful payOrder() call
    data class PendingPayment(
        val orderId: Long,
        val paymentLink: String,
        val paymentId: String
    )
    private val _pendingPayment = MutableStateFlow<PendingPayment?>(null)
    val pendingPayment: StateFlow<PendingPayment?> = _pendingPayment.asStateFlow()

    // Polling result: "paid" | "closed" | "unpaid" | null
    private val _polledPaymentStatus = MutableStateFlow<String?>(null)
    val polledPaymentStatus: StateFlow<String?> = _polledPaymentStatus.asStateFlow()

    private val _paySuccess = MutableStateFlow<String?>(null)
    val paySuccess: StateFlow<String?> = _paySuccess.asStateFlow()

    // ── My Shop (seller listings) ─────────────────────────────────
    private val _myListings = MutableStateFlow<List<Product>>(emptyList())
    val myListings: StateFlow<List<Product>> = _myListings.asStateFlow()

    private val _isLoadingMyListings = MutableStateFlow(false)
    val isLoadingMyListings: StateFlow<Boolean> = _isLoadingMyListings.asStateFlow()

    private val _myListingsError = MutableStateFlow<String?>(null)
    val myListingsError: StateFlow<String?> = _myListingsError.asStateFlow()

    private val _isDeletingListing = MutableStateFlow(false)
    val isDeletingListing: StateFlow<Boolean> = _isDeletingListing.asStateFlow()

    private val _deleteSuccess = MutableStateFlow<String?>(null)
    val deleteSuccess: StateFlow<String?> = _deleteSuccess.asStateFlow()

    // ── Add Listing (seller form) ─────────────────────────────────
    private val _isCreatingListing = MutableStateFlow(false)
    val isCreatingListing: StateFlow<Boolean> = _isCreatingListing.asStateFlow()

    private val _createListingSuccess = MutableStateFlow<String?>(null)
    val createListingSuccess: StateFlow<String?> = _createListingSuccess.asStateFlow()

    private val _createListingError = MutableStateFlow<String?>(null)
    val createListingError: StateFlow<String?> = _createListingError.asStateFlow()

    // ── Edit Listing (seller update form) ────────────────────────
    private val _isUpdatingListing = MutableStateFlow(false)
    val isUpdatingListing: StateFlow<Boolean> = _isUpdatingListing.asStateFlow()

    private val _updateListingSuccess = MutableStateFlow<String?>(null)
    val updateListingSuccess: StateFlow<String?> = _updateListingSuccess.asStateFlow()

    private val _updateListingError = MutableStateFlow<String?>(null)
    val updateListingError: StateFlow<String?> = _updateListingError.asStateFlow()

    // ── Cart (local, in-memory) ───────────────────────────────────
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()

    private val _cartTotal = MutableStateFlow(0L)
    val cartTotal: StateFlow<Long> = _cartTotal.asStateFlow()

    private val _cartAddedMessage = MutableStateFlow<String?>(null)
    val cartAddedMessage: StateFlow<String?> = _cartAddedMessage.asStateFlow()

    // ── Cart Checkout ─────────────────────────────────────────────
    private val _isCheckingOut = MutableStateFlow(false)
    val isCheckingOut: StateFlow<Boolean> = _isCheckingOut.asStateFlow()

    private val _checkoutResult = MutableStateFlow<CartCheckoutResponse?>(null)
    val checkoutResult: StateFlow<CartCheckoutResponse?> = _checkoutResult.asStateFlow()

    private val _checkoutError = MutableStateFlow<String?>(null)
    val checkoutError: StateFlow<String?> = _checkoutError.asStateFlow()

    private val _cartCheckoutGroups = MutableStateFlow<List<CartCheckoutGroup>>(emptyList())
    val cartCheckoutGroups: StateFlow<List<CartCheckoutGroup>> = _cartCheckoutGroups.asStateFlow()

    private val _isLoadingCartCheckouts = MutableStateFlow(false)
    val isLoadingCartCheckouts: StateFlow<Boolean> = _isLoadingCartCheckouts.asStateFlow()

    private val _cartCheckoutPolledStatus = MutableStateFlow<String?>(null)
    val cartCheckoutPolledStatus: StateFlow<String?> = _cartCheckoutPolledStatus.asStateFlow()

    // ── Sales Transactions (Mayar) ───────────────────────────────
    private val _salesTransactions = MutableStateFlow<List<SalesTransaction>>(emptyList())
    val salesTransactions: StateFlow<List<SalesTransaction>> = _salesTransactions.asStateFlow()

    private val _salesSummary = MutableStateFlow(SalesSummary())
    val salesSummary: StateFlow<SalesSummary> = _salesSummary.asStateFlow()

    private val _isLoadingSales = MutableStateFlow(false)
    val isLoadingSales: StateFlow<Boolean> = _isLoadingSales.asStateFlow()

    private val _salesError = MutableStateFlow<String?>(null)
    val salesError: StateFlow<String?> = _salesError.asStateFlow()

    // ─────────────────────────────────────────────────────────────
    // Init — load products on ViewModel creation
    // ─────────────────────────────────────────────────────────────
    init {
        loadProducts()
    }

    // ───────────────────────────────────────────────────────────────
    // Load sales transactions from Mayar (via Laravel proxy)
    // ───────────────────────────────────────────────────────────────
    fun loadSalesTransactions() {
        viewModelScope.launch {
            _isLoadingSales.value = true
            _salesError.value = null
            when (val result = repository.getSalesTransactions()) {
                is AuthResult.Success -> {
                    _salesTransactions.value = result.data.first
                    _salesSummary.value      = result.data.second
                }
                is AuthResult.Error   -> _salesError.value = result.message
                else -> {}
            }
            _isLoadingSales.value = false
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Load all listings from API
    // ───────────────────────────────────────────────────────────────
    fun loadProducts() {
        viewModelScope.launch {
            _isLoadingProducts.value = true
            _productsError.value = null

            val category = _selectedCategory.value
                .name.lowercase().takeIf { it != "all" }
            val search = _searchQuery.value.ifBlank { null }

            when (val result = repository.getListings(category, search)) {
                is AuthResult.Success -> {
                    _allProducts.value      = result.data
                    _filteredProducts.value = result.data
                }
                is AuthResult.Error -> _productsError.value = result.message
                else -> {}
            }
            _isLoadingProducts.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Filter controls
    // ─────────────────────────────────────────────────────────────
    fun selectCategory(category: ProductCategory) {
        _selectedCategory.value = category
        loadProducts()
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
        applyLocalFilters()
    }

    /** Fast local filter on already-loaded products (no API call) */
    private fun applyLocalFilters() {
        val query    = _searchQuery.value.lowercase()
        val category = _selectedCategory.value
        _filteredProducts.value = _allProducts.value.filter { p ->
            val matchCat = category == ProductCategory.ALL || p.category == category
            val matchSearch = query.isEmpty() || p.name.lowercase().contains(query)
            matchCat && matchSearch
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Product detail
    // ─────────────────────────────────────────────────────────────
    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }

    fun clearProduct() {
        _selectedProduct.value = null
        clearOrderSuccess()
    }

    // ─────────────────────────────────────────────────────────────
    // Wishlist toggle — calls API, updates local Set & product list
    // ─────────────────────────────────────────────────────────────
    fun toggleWishlist(productId: String) {
        val listingId = productId.toLongOrNull() ?: return

        viewModelScope.launch {
            _isTogglingWishlist.value = true
            _wishlistError.value = null

            when (val result = repository.toggleWishlist(listingId)) {
                is AuthResult.Success -> {
                    val isNowWishlisted = result.data
                    val current = _wishlist.value.toMutableSet()
                    if (isNowWishlisted) current.add(productId) else current.remove(productId)
                    _wishlist.value = current

                    // Update isWishlisted flag on selectedProduct
                    _selectedProduct.value = _selectedProduct.value?.copy(
                        isWishlisted = isNowWishlisted
                    )

                    // Update in the filtered product list
                    _filteredProducts.value = _filteredProducts.value.map { p ->
                        if (p.id == productId) p.copy(isWishlisted = isNowWishlisted) else p
                    }

                    // Update wishlist product list
                    if (!isNowWishlisted) {
                        _wishlistProducts.value = _wishlistProducts.value.filter { it.id != productId }
                    }
                }
                is AuthResult.Error -> _wishlistError.value = result.message
                else -> {}
            }
            _isTogglingWishlist.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Load wishlist products (for WishlistScreen)
    // ─────────────────────────────────────────────────────────────
    fun loadWishlist() {
        viewModelScope.launch {
            _isLoadingWishlist.value = true
            when (val result = repository.getWishlist()) {
                is AuthResult.Success -> {
                    _wishlistProducts.value = result.data
                    // Rebuild wishlist Set from server response
                    _wishlist.value = result.data.map { it.id }.toSet()
                }
                is AuthResult.Error -> _wishlistError.value = result.message
                else -> {}
            }
            _isLoadingWishlist.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Load orders (for MyOrdersScreen)
    // ─────────────────────────────────────────────────────────────
    fun loadOrders() {
        viewModelScope.launch {
            _isLoadingOrders.value = true
            _ordersError.value = null
            when (val result = repository.getOrders()) {
                is AuthResult.Success -> _orders.value = result.data
                is AuthResult.Error   -> _ordersError.value = result.message
                else -> {}
            }
            _isLoadingOrders.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Create order — buy a product from ProductDetailScreen
    // ─────────────────────────────────────────────────────────────
    fun createOrder(
        productId: String,
        quantity: Int = 1,
        shippingAddress: String,
        notes: String? = null
    ) {
        val listingId = productId.toLongOrNull() ?: run {
            _orderError.value = "ID produk tidak valid."
            return
        }
        if (shippingAddress.isBlank()) {
            _orderError.value = "Alamat pengiriman tidak boleh kosong."
            return
        }

        viewModelScope.launch {
            _isSubmittingOrder.value = true
            _orderError.value = null

            when (val result = repository.createOrder(listingId, quantity, shippingAddress, notes)) {
                is AuthResult.Success -> {
                    _orderSuccess.value = result.data
                    // Clear ordered product from cart if present
                    removeFromCart(productId)

                    // Mark product as sold in local list
                    _filteredProducts.value = _filteredProducts.value.map { p ->
                        if (p.id == productId) p.copy(isWishlisted = p.isWishlisted) else p
                    }
                }
                is AuthResult.Error -> _orderError.value = result.message
                else -> {}
            }
            _isSubmittingOrder.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Pay order — calls Mayar, returns payment link for Chrome Tab
    // ─────────────────────────────────────────────────────────────
    fun payOrder(orderId: String, onPaymentReady: (Long, String, String) -> Unit = { _, _, _ -> }) {
        val id = orderId.toLongOrNull() ?: return
        viewModelScope.launch {
            _isPayingOrder.value = true
            _ordersError.value = null
            when (val result = repository.payOrder(id)) {
                is AuthResult.Success -> {
                    val resp = result.data
                    // Store pending payment info
                    _pendingPayment.value = PendingPayment(id, resp.payment_link, resp.payment_id)
                    // Update the order in the local list with fresh data
                    _orders.value = _orders.value.map { o ->
                        if (o.id == orderId) resp.data.let {
                            o.copy(
                                mayarPaymentLink = resp.payment_link,
                                mayarPaymentId   = resp.payment_id,
                                paymentStatus    = "unpaid"
                            )
                        } else o
                    }
                    onPaymentReady(id, resp.payment_link, resp.payment_id)
                }
                is AuthResult.Error -> _ordersError.value = result.message
                else -> {}
            }
            _isPayingOrder.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Poll payment status — called from PaymentScreen every 3s
    // ─────────────────────────────────────────────────────────────
    fun pollPaymentStatus(orderId: Long) {
        viewModelScope.launch {
            when (val result = repository.pollPaymentStatus(orderId)) {
                is AuthResult.Success -> {
                    val status = result.data.payment_status
                    _polledPaymentStatus.value = status
                    if (status == "paid") {
                        // Refresh orders list to show updated status
                        loadOrders()
                        _paySuccess.value = "Pembayaran berhasil dikonfirmasi! 🎉"
                    }
                }
                is AuthResult.Error -> { /* silently ignore polling errors */ }
                else -> {}
            }
        }
    }

    fun clearPendingPayment()       { _pendingPayment.value = null }
    fun clearPolledPaymentStatus()  { _polledPaymentStatus.value = null }

    // ─────────────────────────────────────────────────────────────
    // Cancel order
    // ─────────────────────────────────────────────────────────────
    fun cancelOrder(orderId: String, reason: String? = null) {
        val id = orderId.toLongOrNull() ?: return
        viewModelScope.launch {
            when (val result = repository.cancelOrder(id, reason)) {
                is AuthResult.Success -> {
                    _orders.value = _orders.value.map { o ->
                        if (o.id == orderId) result.data else o
                    }
                }
                is AuthResult.Error -> _ordersError.value = result.message
                else -> {}
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // My Shop — load seller's own listings
    // ─────────────────────────────────────────────────────────────
    fun loadMyListings() {
        viewModelScope.launch {
            _isLoadingMyListings.value = true
            _myListingsError.value = null
            when (val result = repository.getMyListings()) {
                is AuthResult.Success -> _myListings.value = result.data
                is AuthResult.Error   -> _myListingsError.value = result.message
                else -> {}
            }
            _isLoadingMyListings.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // My Shop — delete (deactivate) a listing
    // ─────────────────────────────────────────────────────────────
    fun deleteListing(productId: String) {
        val id = productId.toLongOrNull() ?: return
        viewModelScope.launch {
            _isDeletingListing.value = true
            when (val result = repository.deleteListing(id)) {
                is AuthResult.Success -> {
                    // Remove from local list immediately
                    _myListings.value = _myListings.value.filter { it.id != productId }
                    _deleteSuccess.value = "Listing berhasil dihapus. 🗑️"
                }
                is AuthResult.Error -> _myListingsError.value = result.message
                else -> {}
            }
            _isDeletingListing.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Add Listing — POST /api/marketplace
    // category: furniture|electronics|clothing|books|others
    // condition: like_new|good|fair
    // ─────────────────────────────────────────────────────────────
    fun createListing(
        name: String,
        description: String,
        price: Long,
        category: String,
        condition: String,
        imageUri: Uri? = null,
        stock: Int = 1,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isCreatingListing.value = true
            _createListingError.value = null
            when (val result = repository.createListing(name, description, price, category, condition, imageUri, stock)) {
                is AuthResult.Success -> {
                    // Prepend new listing to myListings list
                    _myListings.value = listOf(result.data) + _myListings.value
                    _createListingSuccess.value = "Barang berhasil dipasang! 🎉"
                    onSuccess()
                }
                is AuthResult.Error -> _createListingError.value = result.message
                else -> {}
            }
            _isCreatingListing.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Edit Listing — PUT /api/marketplace/{id}
    // category: furniture|electronics|clothing|books|others
    // condition: like_new|good|fair
    // ─────────────────────────────────────────────────────────────
    fun updateListing(
        productId: String,
        name: String,
        description: String,
        price: Long,
        category: String,
        condition: String,
        imageUri: Uri? = null,
        stock: Int = 1,
        onSuccess: () -> Unit = {}
    ) {
        val id = productId.toLongOrNull() ?: run {
            _updateListingError.value = "ID produk tidak valid."
            return
        }
        viewModelScope.launch {
            _isUpdatingListing.value = true
            _updateListingError.value = null
            when (val result = repository.updateListing(id, name, description, price, category, condition, imageUri, stock)) {
                is AuthResult.Success -> {
                    // Replace the updated listing in myListings
                    _myListings.value = _myListings.value.map { p ->
                        if (p.id == productId) result.data else p
                    }
                    _updateListingSuccess.value = "Listing berhasil diperbarui! ✅"
                    onSuccess()
                }
                is AuthResult.Error -> _updateListingError.value = result.message
                else -> {}
            }
            _isUpdatingListing.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Dismiss helpers
    // ─────────────────────────────────────────────────────────────
    fun dismissOrderError()          { _orderError.value = null }
    fun dismissWishlistError()       { _wishlistError.value = null }
    fun dismissProductsError()       { _productsError.value = null }
    fun clearOrderSuccess()          { _orderSuccess.value = null }
    fun dismissPaySuccess()          { _paySuccess.value = null }
    fun dismissDeleteSuccess()       { _deleteSuccess.value = null }
    fun dismissMyListingsError()     { _myListingsError.value = null }
    fun dismissCreateListingSuccess(){ _createListingSuccess.value = null }
    fun dismissCreateListingError()  { _createListingError.value = null }
    fun dismissUpdateListingSuccess(){ _updateListingSuccess.value = null }
    fun dismissUpdateListingError()  { _updateListingError.value = null }

    // Legacy compatibility — kept so MarketplaceScreen compiles
    // ─────────────────────────────────────────────────────────────
    // Cart operations
    // ─────────────────────────────────────────────────────────────
    fun addToCart(product: Product) {
        val current = _cartItems.value.toMutableList()
        val idx = current.indexOfFirst { it.product.id == product.id }
        if (idx >= 0) {
            current[idx] = current[idx].copy(quantity = current[idx].quantity + 1)
        } else {
            current.add(CartItem(product = product, quantity = 1))
        }
        _cartItems.value = current
        recalcCart(current)
        _cartAddedMessage.value = "${product.name} ditambahkan ke keranjang"
    }

    fun removeFromCart(productId: String) {
        val current = _cartItems.value.filter { it.product.id != productId }
        _cartItems.value = current
        recalcCart(current)
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) { removeFromCart(productId); return }
        val current = _cartItems.value.toMutableList()
        val idx = current.indexOfFirst { it.product.id == productId }
        if (idx >= 0) {
            current[idx] = current[idx].copy(quantity = quantity)
            _cartItems.value = current
            recalcCart(current)
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _cartCount.value = 0
        _cartTotal.value = 0L
    }

    fun dismissCartMessage() { _cartAddedMessage.value = null }

    private fun recalcCart(items: List<CartItem>) {
        _cartCount.value = items.sumOf { it.quantity }
        _cartTotal.value = items.sumOf { it.subtotal }
    }

    // Legacy no-arg kept for backward compat (ProductCard uses this via old lambda)
    fun addToCart() { /* no-op – use addToCart(product) */ }

    // ─────────────────────────────────────────────────────────────
    // Cart Checkout — POST /api/orders/checkout-cart
    // ─────────────────────────────────────────────────────────────
    fun checkoutCart(
        shippingAddress: String,
        notes: String?,
        onSuccess: (paymentLink: String, cartCheckoutId: String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            _isCheckingOut.value = true
            _checkoutError.value = null

            val items = _cartItems.value.mapNotNull { ci ->
                val id = ci.product.id.toLongOrNull() ?: return@mapNotNull null
                CartCheckoutItemRequest(listing_id = id, quantity = ci.quantity)
            }
            if (items.isEmpty()) {
                _checkoutError.value = "Keranjang kosong."
                _isCheckingOut.value = false
                return@launch
            }

            when (val result = repository.cartCheckout(shippingAddress, notes, items)) {
                is AuthResult.Success -> {
                    _checkoutResult.value = result.data
                    // Clear cart after successful checkout
                    clearCart()
                    onSuccess(result.data.payment_link, result.data.cart_checkout_id)
                }
                is AuthResult.Error -> _checkoutError.value = result.message
                else -> {}
            }
            _isCheckingOut.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Load cart checkout groups (MyOrders tab)
    // ─────────────────────────────────────────────────────────────
    fun loadCartCheckouts() {
        viewModelScope.launch {
            _isLoadingCartCheckouts.value = true
            when (val result = repository.getCartCheckouts()) {
                is AuthResult.Success -> _cartCheckoutGroups.value = result.data
                is AuthResult.Error   -> { /* silently fail, keep old data */ }
                else -> {}
            }
            _isLoadingCartCheckouts.value = false
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Poll cart checkout payment status
    // ─────────────────────────────────────────────────────────────
    fun pollCartCheckoutStatus(cartCheckoutId: String) {
        viewModelScope.launch {
            when (val result = repository.pollCartCheckoutStatus(cartCheckoutId)) {
                is AuthResult.Success -> {
                    val status = result.data.payment_status
                    _cartCheckoutPolledStatus.value = status
                    if (status == "paid") {
                        loadCartCheckouts()
                        _paySuccess.value = "Pembayaran cart berhasil dikonfirmasi! 🎉"
                    }
                }
                is AuthResult.Error -> { /* ignore polling errors */ }
                else -> {}
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Cancel cart checkout
    // ─────────────────────────────────────────────────────────────
    fun cancelCartCheckout(cartCheckoutId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            when (val result = repository.cancelCartCheckout(cartCheckoutId)) {
                is AuthResult.Success -> {
                    loadCartCheckouts()
                    onDone()
                }
                is AuthResult.Error -> _checkoutError.value = result.message
                else -> {}
            }
        }
    }

    fun dismissCheckoutError()  { _checkoutError.value = null }
    fun clearCheckoutResult()   { _checkoutResult.value = null }
    fun clearCartCheckoutPolledStatus() { _cartCheckoutPolledStatus.value = null }
}


