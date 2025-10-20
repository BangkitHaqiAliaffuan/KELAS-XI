package com.trashbin.app.ui.marketplace

import android.app.AlertDialog
import android.view.Gravity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.trashbin.app.R
import com.trashbin.app.data.model.MarketplaceListing
import com.trashbin.app.ui.adapters.PhotoPagerAdapter
import com.trashbin.app.ui.viewmodel.MarketplaceViewModel
import com.trashbin.app.utils.CurrencyHelper

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
data class OrderDialogComponents(
    val dialogView: View,
    val etQuantity: EditText,
    val etShippingAddress: EditText,
    val etNotes: EditText,
    val btnOrder: Button,
    val btnCancel: Button
)

class ListingDetailActivity : AppCompatActivity() {
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var toolbar: MaterialToolbar
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var mainContentLayout: LinearLayout
    private lateinit var tvTitle: TextView
    private lateinit var tvPrice: TextView
    private lateinit var ivSellerAvatar: ImageView
    private lateinit var tvSellerName: TextView
    private lateinit var tvSellerRating: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnBuy: com.google.android.material.floatingactionbutton.FloatingActionButton

    private val viewModel: MarketplaceViewModel by viewModels()
    private var listing: MarketplaceListing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupUI()
        loadListingDetail()
        setupListeners()
        observeOrderCreation()
        observeListingDetail()
    }
    
    private fun setupUI() {
        coordinatorLayout = CoordinatorLayout(this)

        appBarLayout = AppBarLayout(this).apply {
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )
        }

        collapsingToolbarLayout = CollapsingToolbarLayout(this).apply {
            layoutParams = AppBarLayout.LayoutParams(
                AppBarLayout.LayoutParams.MATCH_PARENT,
                (300 * resources.displayMetrics.density).toInt()
            ).apply {
                scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
            }
        }

        viewPager = ViewPager2(this).apply {
            id = View.generateViewId()
            layoutParams = CollapsingToolbarLayout.LayoutParams(
                CollapsingToolbarLayout.LayoutParams.MATCH_PARENT,
                CollapsingToolbarLayout.LayoutParams.MATCH_PARENT
            )
        }
        collapsingToolbarLayout.addView(viewPager)

        toolbar = MaterialToolbar(this).apply {
            id = View.generateViewId()
            layoutParams = CollapsingToolbarLayout.LayoutParams(
                CollapsingToolbarLayout.LayoutParams.MATCH_PARENT,
                CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
            }
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                finish()
            }
        }
        collapsingToolbarLayout.addView(toolbar)

        appBarLayout.addView(collapsingToolbarLayout)

        nestedScrollView = NestedScrollView(this).apply {
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
            )
        }

        mainContentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt()
            )
        }

        // Title and Price
        tvTitle = TextView(this).apply {
            id = View.generateViewId()
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        mainContentLayout.addView(tvTitle)

        tvPrice = TextView(this).apply {
            id = View.generateViewId()
            setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
        }
        mainContentLayout.addView(tvPrice)

        // Seller Info
        val sellerInfoLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }

        ivSellerAvatar = ImageView(this).apply {
            id = View.generateViewId()
            // setImageResource(R.drawable.ic_user) // Assuming this drawable exists
            contentDescription = "Avatar penjual"
            val size = (40 * resources.displayMetrics.density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size)
        }
        sellerInfoLayout.addView(ivSellerAvatar)

        val sellerDetailsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMarginStart((12 * resources.displayMetrics.density).toInt())
            }
        }

        tvSellerName = TextView(this).apply {
            id = View.generateViewId()
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        sellerDetailsLayout.addView(tvSellerName)

        tvSellerRating = TextView(this).apply {
            id = View.generateViewId()
            setPadding(0, (2 * resources.displayMetrics.density).toInt(), 0, 0)
            textSize = 12f
        }
        sellerDetailsLayout.addView(tvSellerRating)

        sellerInfoLayout.addView(sellerDetailsLayout)
        mainContentLayout.addView(sellerInfoLayout)

        // Category and Condition
        val categoryConditionLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }

        val categoryLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val categoryLabel = TextView(this).apply {
            text = "Kategori"
            textSize = 12f
            setTextColor(resources.getColor(R.color.gray_600))
        }
        categoryLayout.addView(categoryLabel)

        tvCategory = TextView(this).apply {
            id = View.generateViewId()
            setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            textSize = 14f
        }
        categoryLayout.addView(tvCategory)

        categoryConditionLayout.addView(categoryLayout)

        val conditionLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val conditionLabel = TextView(this).apply {
            text = "Kondisi"
            textSize = 12f
            setTextColor(resources.getColor(R.color.gray_600))
        }
        conditionLayout.addView(conditionLabel)

        tvCondition = TextView(this).apply {
            id = View.generateViewId()
            setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            textSize = 14f
        }
        conditionLayout.addView(tvCondition)

        categoryConditionLayout.addView(conditionLayout)
        mainContentLayout.addView(categoryConditionLayout)

        // Quantity and Availability
        val quantityStatusLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }

        val quantityLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val quantityLabel = TextView(this).apply {
            text = "Jumlah"
            textSize = 12f
            setTextColor(resources.getColor(R.color.gray_600))
        }
        quantityLayout.addView(quantityLabel)

        tvQuantity = TextView(this).apply {
            id = View.generateViewId()
            setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            textSize = 14f
        }
        quantityLayout.addView(tvQuantity)

        quantityStatusLayout.addView(quantityLayout)

        val statusLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val statusLabel = TextView(this).apply {
            text = "Status"
            textSize = 12f
            setTextColor(resources.getColor(R.color.gray_600))
        }
        statusLayout.addView(statusLabel)

        tvStatus = TextView(this).apply {
            id = View.generateViewId()
            setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            textSize = 14f
        }
        statusLayout.addView(tvStatus)

        quantityStatusLayout.addView(statusLayout)

        mainContentLayout.addView(quantityStatusLayout)

        // Location
        val locationLabel = TextView(this).apply {
            text = "Lokasi"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainContentLayout.addView(locationLabel)

        tvLocation = TextView(this).apply {
            id = View.generateViewId()
            setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            textSize = 14f
        }
        mainContentLayout.addView(tvLocation)

        // Description
        val descriptionLabel = TextView(this).apply {
            text = "Deskripsi"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainContentLayout.addView(descriptionLabel)

        tvDescription = TextView(this).apply {
            id = View.generateViewId()
            setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            textSize = 14f
        }
        mainContentLayout.addView(tvDescription)

        nestedScrollView.addView(mainContentLayout)
        coordinatorLayout.addView(appBarLayout)
        coordinatorLayout.addView(nestedScrollView)

        // Floating action button
        btnBuy = com.google.android.material.floatingactionbutton.FloatingActionButton(this).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.ic_shopping_cart) // Assuming this drawable exists
            contentDescription = "Beli barang"
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END or Gravity.BOTTOM
                setMargins(0, 0, (16 * resources.displayMetrics.density).toInt(), 
                    (16 * resources.displayMetrics.density).toInt())
            }
        }
        coordinatorLayout.addView(btnBuy)

        setContentView(coordinatorLayout)
    }
    
    private fun loadListingDetail() {
        val listingId = intent.extras?.getInt("listing_id", -1) ?: -1
        android.util.Log.d("ListingDetailActivity", "Loading listing with ID: $listingId")
        if (listingId != -1) {
            // Load actual listing from API
            viewModel.loadListingDetail(listingId)
        } else {
            Toast.makeText(this, "Listing ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun bindListingData() {
        listing?.let { listing ->
            android.util.Log.d("ListingDetailActivity", "Binding data for: ${listing.title}")
            
            // Set up photo pager
            val photos = if (listing.photos.isNotEmpty()) {
                listing.photos
            } else {
                // Provide a placeholder if no photos
                listOf("https://via.placeholder.com/400x300?text=No+Image")
            }
            val photoAdapter = PhotoPagerAdapter(photos)
            viewPager.adapter = photoAdapter
            
            // Bind text data
            tvTitle.text = listing.title
            tvPrice.text = CurrencyHelper.formatRupiah(listing.totalPrice)
            tvSellerName.text = listing.seller.name
            tvSellerRating.text = "⭐ ${listing.seller.points}" // Changed from rating to points
            tvCategory.text = listing.category.name
            tvCondition.text = when (listing.condition) {
                "clean" -> "Bersih"
                "needs_cleaning" -> "Perlu Dibersihkan"
                "mixed" -> "Campur"
                else -> listing.condition
            }
            tvQuantity.text = "${listing.quantity} ${listing.unit}"
            tvStatus.text = when (listing.status) {
                "available" -> "Tersedia"
                "sold" -> "Terjual"
                "expired" -> "Kadaluarsa"
                else -> listing.status
            }
            tvLocation.text = listing.location
            tvDescription.text = listing.description
            
            // Load seller avatar
            listing.seller.avatar?.let { avatarUrl ->
                Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_person)
                    .into(ivSellerAvatar)
            } ?: run {
                ivSellerAvatar.setImageResource(R.drawable.ic_person)
            }
            
            android.util.Log.d("ListingDetailActivity", "Data binding completed")
        }
    }
    
    private fun setupListeners() {
        btnBuy.setOnClickListener {
            showOrderDialog()
        }
    }
    
    private fun observeListingDetail() {
        viewModel.listingDetail.observe(this) { result ->
            when (result) {
                is com.trashbin.app.data.repository.Result.Loading -> {
                    android.util.Log.d("ListingDetailActivity", "Loading listing detail...")
                    // Show loading if needed
                }
                is com.trashbin.app.data.repository.Result.Success -> {
                    android.util.Log.d("ListingDetailActivity", "Listing loaded successfully: ${result.data.title}")
                    listing = result.data
                    bindListingData()
                }
                is com.trashbin.app.data.repository.Result.Error -> {
                    android.util.Log.e("ListingDetailActivity", "Error loading listing: ${result.message}")
                    Toast.makeText(this, "Gagal memuat detail listing: ${result.message}", Toast.LENGTH_LONG).show()
                    finish() // Close the activity if we can't load the listing
                }
            }
        }
    }
    
    private fun observeOrderCreation() {
        viewModel.createOrderState.observe(this) { result ->
            when (result) {
                is com.trashbin.app.data.repository.Result.Loading -> {
                    // Show loading if needed
                }
                is com.trashbin.app.data.repository.Result.Success -> {
                    Toast.makeText(this, "Order berhasil dibuat", Toast.LENGTH_SHORT).show()
                }
                is com.trashbin.app.data.repository.Result.Error -> {
                    Toast.makeText(this, "Gagal membuat order: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun showOrderDialog() {
        listing?.let { listing ->
            val builder = AlertDialog.Builder(this)
            val orderDialogComponents = createOrderDialogView()
            builder.setView(orderDialogComponents.dialogView)
            
            val dialog = builder.create()
            
            orderDialogComponents.btnOrder.setOnClickListener {
                val quantityStr = orderDialogComponents.etQuantity.text.toString()
                val quantity = quantityStr.toDoubleOrNull()
                val shippingAddress = orderDialogComponents.etShippingAddress.text.toString().trim()
                val notes = orderDialogComponents.etNotes.text.toString().trim().takeIf { it.isNotEmpty() }
                
                android.util.Log.d("ListingDetailActivity", "Order attempt - quantity: $quantity, address: $shippingAddress")
                
                if (quantity != null && quantity > 0 && quantity <= listing.quantity && shippingAddress.isNotEmpty()) {
                    // Create order with API call - success will be handled by observer
                    viewModel.createOrder(
                        listing.id,
                        quantity,  // Send as Double, not Int
                        shippingAddress,
                        notes
                    )
                    dialog.dismiss()
                } else {
                    if (shippingAddress.isEmpty()) {
                        Toast.makeText(this, "Alamat pengiriman harus diisi", Toast.LENGTH_SHORT).show()
                    } else if (quantity == null || quantity <= 0) {
                        Toast.makeText(this, "Jumlah harus lebih dari 0", Toast.LENGTH_SHORT).show()
                    } else if (quantity > listing.quantity) {
                        Toast.makeText(this, "Jumlah melebihi stok tersedia (${listing.quantity})", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Data tidak valid", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            orderDialogComponents.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            
            dialog.show()
        } ?: run {
            Toast.makeText(this, "Data listing tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun createOrderDialogView(): OrderDialogComponents {
        val dialogLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (24 * resources.displayMetrics.density).toInt(),
                (24 * resources.displayMetrics.density).toInt(),
                (24 * resources.displayMetrics.density).toInt(),
                (24 * resources.displayMetrics.density).toInt()
            )
        }

        val title = TextView(this).apply {
            text = "Buat Pesanan"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, (16 * resources.displayMetrics.density).toInt())
        }
        dialogLayout.addView(title)

        val quantityInputLayout = com.google.android.material.textfield.TextInputLayout(this).apply {
            hint = "Jumlah"
        }
        val etQuantity = com.google.android.material.textfield.TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            hint = "1"
        }
        quantityInputLayout.addView(etQuantity)
        dialogLayout.addView(quantityInputLayout)

        val shippingAddressInputLayout = com.google.android.material.textfield.TextInputLayout(this).apply {
            hint = "Alamat Pengiriman"
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        val etShippingAddress = com.google.android.material.textfield.TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            hint = "Masukkan alamat pengiriman"
        }
        shippingAddressInputLayout.addView(etShippingAddress)
        dialogLayout.addView(shippingAddressInputLayout)

        val notesInputLayout = com.google.android.material.textfield.TextInputLayout(this).apply {
            hint = "Catatan (Opsional)"
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        val etNotes = com.google.android.material.textfield.TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            hint = "Catatan tambahan untuk penjual"
        }
        notesInputLayout.addView(etNotes)
        dialogLayout.addView(notesInputLayout)

        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }

        val btnCancel = Button(this).apply {
            id = View.generateViewId()
            text = "Batal"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = (8 * resources.displayMetrics.density).toInt()
            }
        }
        buttonLayout.addView(btnCancel)

        val btnOrder = Button(this).apply {
            id = View.generateViewId()
            text = "Pesan"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (8 * resources.displayMetrics.density).toInt()
            }
        }
        buttonLayout.addView(btnOrder)

        dialogLayout.addView(buttonLayout)

        return OrderDialogComponents(dialogLayout, etQuantity, etShippingAddress, etNotes, btnOrder, btnCancel)
    }
}