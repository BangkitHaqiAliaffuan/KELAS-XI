package com.trashbin.app.ui.marketplace

import android.Manifest
import android.R.attr.gravity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trashbin.app.R
import com.trashbin.app.data.model.WasteCategory
import com.trashbin.app.ui.adapters.CategoryAdapter
import com.trashbin.app.ui.bottomsheet.CategoryBottomSheet
import com.trashbin.app.ui.viewmodel.MarketplaceViewModel
import com.trashbin.app.utils.ImageHelper
import com.trashbin.app.data.repository.Result
import java.io.File

class CreateListingActivity : AppCompatActivity() {
    private val viewModel: MarketplaceViewModel by viewModels()
    private var selectedCategory: WasteCategory? = null
    private var selectedLat = 0.0
    private var selectedLng = 0.0
    private var selectedAddress = ""
    private val selectedPhotos = mutableListOf<File>()
    
    // UI Components
    private lateinit var scrollView: ScrollView
    private lateinit var mainLayout: LinearLayout
    private lateinit var titleText: TextView
    private lateinit var categoryInputLayout: TextInputLayout
    private lateinit var etCategory: TextInputEditText
    private lateinit var titleInputLayout: TextInputLayout
    private lateinit var etTitle: TextInputEditText
    private lateinit var descriptionInputLayout: TextInputLayout
    private lateinit var etDescription: TextInputEditText
    private lateinit var quantityPriceLayout: LinearLayout
    private lateinit var quantityInputLayout: TextInputLayout
    private lateinit var etQuantity: TextInputEditText
    private lateinit var priceInputLayout: TextInputLayout
    private lateinit var etPrice: TextInputEditText
    private lateinit var conditionLabel: TextView
    private lateinit var rgCondition: RadioGroup
    private lateinit var rbClean: MaterialRadioButton
    private lateinit var rbNeedsCleaning: MaterialRadioButton
    private lateinit var rbMixed: MaterialRadioButton
    private lateinit var locationInputLayout: TextInputLayout
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
        
        setupUI()
        setupListeners()
        loadCategories()
    }
    
    private fun setupUI() {
        scrollView = ScrollView(this)
        mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt()
            )
        }

        // Title
        titleText = TextView(this).apply {
            text = "Buat Listing Baru"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, (24 * resources.displayMetrics.density).toInt())
        }
        mainLayout.addView(titleText)

        // Category Selection
        categoryInputLayout = TextInputLayout(this).apply {
            hint = "Kategori Sampah"
        }
        etCategory = TextInputEditText(this).apply {
            id = View.generateViewId()
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0)
            inputType = android.text.InputType.TYPE_NULL
        }
        categoryInputLayout.addView(etCategory)
        mainLayout.addView(categoryInputLayout)

        // Title
        titleInputLayout = TextInputLayout(this).apply {
            hint = "Judul Listing"
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        etTitle = TextInputEditText(this).apply {
            id = View.generateViewId()
            filters = arrayOf(InputFilter.LengthFilter(100))
        }
        titleInputLayout.addView(etTitle)
        mainLayout.addView(titleInputLayout)

        // Description
        descriptionInputLayout = TextInputLayout(this).apply {
            hint = "Deskripsi"
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        etDescription = TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
            gravity = Gravity.TOP
        }
        descriptionInputLayout.addView(etDescription)
        mainLayout.addView(descriptionInputLayout)

        // Quantity and Price (horizontal layout)
        quantityPriceLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }

        val quantityContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = (8 * resources.displayMetrics.density).toInt()
            }
        }
        quantityInputLayout = TextInputLayout(this).apply {
            hint = "Jumlah"
        }
        etQuantity = TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "0.0"
        }
        quantityInputLayout.addView(etQuantity)
        quantityContainer.addView(quantityInputLayout)
        quantityPriceLayout.addView(quantityContainer)

        val priceContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (8 * resources.displayMetrics.density).toInt()
            }
        }
        priceInputLayout = TextInputLayout(this).apply {
            hint = "Harga per Unit"
        }
        etPrice = TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Rp 0"
        }
        priceInputLayout.addView(etPrice)
        priceContainer.addView(priceInputLayout)
        quantityPriceLayout.addView(priceContainer)

        mainLayout.addView(quantityPriceLayout)

        // Condition Selection
        conditionLabel = TextView(this).apply {
            text = "Kondisi Barang"
            textSize = 16f
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(conditionLabel)

        rgCondition = RadioGroup(this).apply {
            id = View.generateViewId()
            orientation = RadioGroup.VERTICAL
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }

        rbClean = MaterialRadioButton(this).apply {
            id = View.generateViewId()
            text = "Bersih"
        }
        rgCondition.addView(rbClean)

        rbNeedsCleaning = MaterialRadioButton(this).apply {
            id = View.generateViewId()
            text = "Perlu Dibersihkan"
        }
        rgCondition.addView(rbNeedsCleaning)

        rbMixed = MaterialRadioButton(this).apply {
            id = View.generateViewId()
            text = "Campur"
        }
        rgCondition.addView(rbMixed)

        mainLayout.addView(rgCondition)

        // Location Selection
        locationInputLayout = TextInputLayout(this).apply {
            hint = "Lokasi"
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        etLocation = TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_location, 0)
        }
        locationInputLayout.addView(etLocation)
        mainLayout.addView(locationInputLayout)

        // Photos Layout
        llPhotos = LinearLayout(this).apply {
            id = View.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(llPhotos)

        // Add Photo Button
        btnAddPhoto = MaterialButton(this).apply {
            id = View.generateViewId()
            text = "Tambah Foto"
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(btnAddPhoto)

        // Pick Location Button
        btnPickLocation = MaterialButton(this).apply {
            id = View.generateViewId()
            text = "Pilih Lokasi dari Peta"
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(btnPickLocation)

        // Submit Button
        btnSubmit = MaterialButton(this).apply {
            id = View.generateViewId()
            text = "Buat Listing"
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(btnSubmit)

        // Progress Bar
        progressBar = ProgressBar(this).apply {
            id = View.generateViewId()
            visibility = View.GONE
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(progressBar)

        scrollView.addView(mainLayout)
        setContentView(scrollView)
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
            
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                == PackageManager.PERMISSION_GRANTED) {
                photoPicker.launch("image/*")
            } else {
                // Request permission
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1001)
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
        val photoContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                (80 * resources.displayMetrics.density).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMarginEnd((4 * resources.displayMetrics.density).toInt())
            }
        }
        
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (80 * resources.displayMetrics.density).toInt()
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            // setBackgroundResource(R.color.gray_200) // Assuming this color exists
            contentDescription = "Foto barang"
            
            Glide.with(this@CreateListingActivity)
                .load(photoFile)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(this)
        }
        
        val removeButton = ImageButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                (24 * resources.displayMetrics.density).toInt(),
                (24 * resources.displayMetrics.density).toInt()
            ).apply {
                gravity = Gravity.END or Gravity.TOP
                setMargins(0, -(24 * resources.displayMetrics.density).toInt(), 
                    -(4 * resources.displayMetrics.density).toInt(), 0)
            }
            // setBackgroundResource(R.drawable.bg_circle_remove) // Assuming this drawable exists
            // setImageResource(R.drawable.ic_close) // Assuming this drawable exists
            contentDescription = "Hapus foto"
            
            setOnClickListener {
                photoContainer.removeAllViews()
                llPhotos.removeView(photoContainer)
                selectedPhotos.remove(photoFile)
            }
        }
        
        photoContainer.addView(imageView)
        photoContainer.addView(removeButton)
        llPhotos.addView(photoContainer)
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
            rbClean.id -> "clean"
            rbNeedsCleaning.id -> "needs_cleaning"
            rbMixed.id -> "mixed"
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