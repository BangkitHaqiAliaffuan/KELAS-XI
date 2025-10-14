package com.trashbin.app.ui.marketplace

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputEditText
import com.trashbin.app.R
import com.trashbin.app.data.model.WasteCategory
import com.trashbin.app.ui.adapters.CategoryAdapter
import com.trashbin.app.ui.bottomsheet.CategoryBottomSheet
import com.trashbin.app.ui.viewmodel.MarketplaceViewModel
import com.trashbin.app.utils.ImageHelper
import com.trashbin.app.utils.Result
import java.io.File

class CreateListingActivity : AppCompatActivity() {
    private val viewModel: MarketplaceViewModel by viewModels()
    private var selectedCategory: WasteCategory? = null
    private var selectedLat = 0.0
    private var selectedLng = 0.0
    private var selectedAddress = ""
    private val selectedPhotos = mutableListOf<File>()
    
    private lateinit var etCategory: TextInputEditText
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etQuantity: TextInputEditText
    private lateinit var etPrice: TextInputEditText
    private lateinit var rgCondition: RadioGroup
    private lateinit var etLocation: TextInputEditText
    private lateinit var llPhotos: LinearLayout
    private lateinit var btnAddPhoto: MaterialButton
    private lateinit var btnPickLocation: MaterialButton
    private lateinit var btnSubmit: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val photoPicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            for (uri in uris) {
                val compressedFile = ImageHelper.compressImage(this, uri)
                if (compressedFile != null && selectedPhotos.size < 5) {
                    selectedPhotos.add(compressedFile)
                    addPhotoToLayout(compressedFile)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_listing)
        
        initViews()
        setupListeners()
        loadCategories()
    }
    
    private fun initViews() {
        etCategory = findViewById(R.id.et_category)
        etTitle = findViewById(R.id.et_title)
        etDescription = findViewById(R.id.et_description)
        etQuantity = findViewById(R.id.et_quantity)
        etPrice = findViewById(R.id.et_price)
        rgCondition = findViewById(R.id.rg_condition)
        etLocation = findViewById(R.id.et_location)
        llPhotos = findViewById(R.id.ll_photos)
        btnAddPhoto = findViewById(R.id.btn_add_photo)
        btnPickLocation = findViewById(R.id.btn_pick_location)
        btnSubmit = findViewById(R.id.btn_submit)
        progressBar = findViewById(R.id.progress_bar)
    }
    
    private fun setupListeners() {
        etCategory.setOnClickListener {
            showCategoryDialog()
        }
        
        btnAddPhoto.setOnClickListener {
            if (selectedPhotos.size >= 5) {
                Toast.makeText(this, "Maksimal 5 foto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                == PackageManager.PERMISSION_GRANTED) {
                photoPicker.launch("image/*")
            } else {
                // Request permission
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
            }
        }
        
        btnPickLocation.setOnClickListener {
            Toast.makeText(this, "Fitur pilih lokasi dari peta akan diimplementasikan", Toast.LENGTH_SHORT).show()
            // TODO: Implement location picking
        }
        
        btnSubmit.setOnClickListener {
            if (validateInput()) {
                createListing()
            }
        }
    }
    
    private fun loadCategories() {
        // Load categories to populate the selection
        viewModel.loadListings()
    }
    
    private fun showCategoryDialog() {
        // For now, we'll just make a simple category selection
        // In a real implementation, you would load the actual categories
        val categories = listOf(
            WasteCategory(1, "Kain Perca", "kain-perca", "kg", 3000.0, null),
            WasteCategory(2, "Plastik PET", "plastik-pet", "kg", 4000.0, null),
            WasteCategory(3, "Kardus", "kardus", "kg", 2000.0, null),
            WasteCategory(4, "Kaleng", "kaleng", "kg", 15000.0, null),
            WasteCategory(5, "Botol Kaca", "botol-kaca", "kg", 1500.0, null)
        )
        
        val dialog = CategoryBottomSheet(categories) { category ->
            selectedCategory = category
            etCategory.setText(category.name)
        }
        dialog.show(supportFragmentManager, "category")
    }
    
    private fun addPhotoToLayout(photoFile: File) {
        val photoView = layoutInflater.inflate(R.layout.item_photo, llPhotos, false)
        val ivPhoto: ImageView = photoView.findViewById(R.id.iv_photo)
        val btnRemove: ImageButton = photoView.findViewById(R.id.btn_remove)
        
        Glide.with(this)
            .load(photoFile)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(ivPhoto)
        
        btnRemove.setOnClickListener {
            llPhotos.removeView(photoView)
            selectedPhotos.remove(photoFile)
        }
        
        llPhotos.addView(photoView)
    }
    
    private fun validateInput(): Boolean {
        if (selectedCategory == null) {
            Toast.makeText(this, "Pilih kategori terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (etTitle.text.isNullOrEmpty()) {
            etTitle.error = "Judul harus diisi"
            return false
        }
        
        if (etDescription.text.isNullOrEmpty()) {
            etDescription.error = "Deskripsi harus diisi"
            return false
        }
        
        if (etQuantity.text.isNullOrEmpty() || etQuantity.text.toString().toDoubleOrNull() ?: 0.0 <= 0) {
            etQuantity.error = "Jumlah harus lebih dari 0"
            return false
        }
        
        if (etPrice.text.isNullOrEmpty() || etPrice.text.toString().toDoubleOrNull() ?: 0.0 <= 0) {
            etPrice.error = "Harga harus lebih dari 0"
            return false
        }
        
        if (selectedLat == 0.0 || selectedLng == 0.0 || selectedAddress.isEmpty()) {
            Toast.makeText(this, "Pilih lokasi terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (selectedPhotos.isEmpty()) {
            Toast.makeText(this, "Tambahkan minimal 1 foto", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun createListing() {
        val categoryId = selectedCategory!!.id
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()
        val quantity = etQuantity.text.toString().toInt()
        val pricePerUnit = etPrice.text.toString().toDouble()
        
        // Get selected condition
        val selectedConditionId = rgCondition.checkedRadioButtonId
        val condition = when (selectedConditionId) {
            R.id.rb_clean -> "clean"
            R.id.rb_needs_cleaning -> "needs_cleaning"
            R.id.rb_mixed -> "mixed"
            else -> "clean" // default
        }
        
        val location = selectedAddress
        
        viewModel.createListing(
            categoryId, title, description, quantity, pricePerUnit,
            condition, location, selectedLat, selectedLng, selectedPhotos
        )
        
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.createListingState.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Listing berhasil dibuat", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSubmit.isEnabled = !show
    }
}