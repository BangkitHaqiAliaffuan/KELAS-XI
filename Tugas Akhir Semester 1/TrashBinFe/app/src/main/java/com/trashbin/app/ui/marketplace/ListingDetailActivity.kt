package com.trashbin.app.ui.marketplace

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trashbin.app.R
import com.trashbin.app.data.model.MarketplaceListing
import com.trashbin.app.ui.adapters.PhotoPagerAdapter
import com.trashbin.app.ui.viewmodel.MarketplaceViewModel
import com.trashbin.app.utils.CurrencyHelper

class ListingDetailActivity : AppCompatActivity() {
    private lateinit var viewPager: androidx.viewpager2.widget.ViewPager2
    private lateinit var tvTitle: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvSellerName: TextView
    private lateinit var tvSellerRating: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnBuy: FloatingActionButton
    private lateinit var ivSellerAvatar: ImageView
    
    private val viewModel: MarketplaceViewModel by viewModels()
    private var listing: MarketplaceListing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_detail)
        
        initViews()
        loadListingDetail()
        setupListeners()
    }
    
    private fun initViews() {
        viewPager = findViewById(R.id.view_pager_photos)
        tvTitle = findViewById(R.id.tv_title)
        tvPrice = findViewById(R.id.tv_price)
        tvSellerName = findViewById(R.id.tv_seller_name)
        tvSellerRating = findViewById(R.id.tv_seller_rating)
        tvCategory = findViewById(R.id.tv_category)
        tvCondition = findViewById(R.id.tv_condition)
        tvQuantity = findViewById(R.id.tv_quantity)
        tvStatus = findViewById(R.id.tv_status)
        tvLocation = findViewById(R.id.tv_location)
        tvDescription = findViewById(R.id.tv_description)
        btnBuy = findViewById(R.id.btn_buy)
        ivSellerAvatar = findViewById(R.id.iv_seller_avatar)
    }
    
    private fun loadListingDetail() {
        // This is a placeholder - in a real app, you would load the listing detail from the API
        // For now, I'll create a mock listing based on the passed ID
        val listingId = intent.extras?.getInt("listing_id", -1) ?: -1
        if (listingId != -1) {
            // TODO: Load actual listing from API
            // For now, I'll create a mock listing
            listing = MarketplaceListing(
                id = listingId,
                sellerId = 1,
                categoryId = 1,
                title = "Kertas Bekas",
                description = "Kertas bekas yang bisa didaur ulang",
                quantity = 5.0,
                pricePerUnit = 2000.0,
                condition = "clean",
                location = "Jakarta, Indonesia",
                lat = -6.200000,
                lng = 106.816666,
                photos = listOf("https://example.com/photo.jpg"),  // Placeholder
                views = 10,
                isActive = true,
                status = "available",
                seller = com.trashbin.app.data.model.User(
                    id = 1,
                    name = "Contoh Penjual",
                    email = "seller@example.com",
                    phone = null,
                    role = "user",
                    avatar = null,
                    address = "Jakarta",
                    lat = -6.200000,
                    lng = 106.816666,
                    points = 0,
                    isVerified = true,
                    rating = 4.5
                ),
                category = com.trashbin.app.data.model.WasteCategory(
                    id = 1,
                    name = "Kertas",
                    slug = "kertas",
                    unit = "kg",
                    basePricePerUnit = 2000.0,
                    iconUrl = null
                ),
                createdAt = "2023-01-01 00:00:00",
                updatedAt = "2023-01-01 00:00:00"
            )
            
            bindListingData()
        } else {
            Toast.makeText(this, "Listing tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun bindListingData() {
        listing?.let { listing ->
            // Set up photo pager
            val photoAdapter = PhotoPagerAdapter(listing.photos)
            viewPager.adapter = photoAdapter
            
            // Bind text data
            tvTitle.text = listing.title
            tvPrice.text = CurrencyHelper.formatRupiah(listing.totalPrice)
            tvSellerName.text = listing.seller.name
            tvSellerRating.text = "⭐ ${listing.seller.rating ?: 0.0}"
            tvCategory.text = listing.category.name
            tvCondition.text = when (listing.condition) {
                "clean" -> "Bersih"
                "needs_cleaning" -> "Perlu Dibersihkan"
                "mixed" -> "Campur"
                else -> listing.condition
            }
            tvQuantity.text = "${listing.quantity.toInt()}"
            tvStatus.text = when (listing.status) {
                "available" -> "Tersedia"
                "reserved" -> "Dipesan"
                "sold" -> "Terjual"
                else -> listing.status
            }
            tvLocation.text = listing.location
            tvDescription.text = listing.description
            
            // Load seller avatar
            listing.seller.avatar?.let { avatarUrl ->
                Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_user)
                    .into(ivSellerAvatar)
            } ?: run {
                ivSellerAvatar.setImageResource(R.drawable.ic_user)
            }
        }
    }
    
    private fun setupListeners() {
        btnBuy.setOnClickListener {
            showOrderDialog()
        }
    }
    
    private fun showOrderDialog() {
        listing?.let { listing ->
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_order, null)
            builder.setView(dialogView)
            
            val etQuantity: EditText = dialogView.findViewById(R.id.et_quantity)
            val btnOrder: Button = dialogView.findViewById(R.id.btn_order)
            val btnCancel: Button = dialogView.findViewById(R.id.btn_cancel)
            
            val dialog = builder.create()
            
            btnOrder.setOnClickListener {
                val quantity = etQuantity.text.toString().toIntOrNull()
                if (quantity != null && quantity > 0 && quantity <= listing.quantity.toInt()) {
                    // TODO: Create order with API call
                    viewModel.createOrder(
                        listing.id,
                        quantity,
                        "Alamat pengiriman",  // This should come from user input
                        "Catatan pesanan"     // This should come from user input
                    )
                    
                    Toast.makeText(this, "Order berhasil dibuat", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Jumlah tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
            
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            
            dialog.show()
        } ?: run {
            Toast.makeText(this, "Data listing tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }
}