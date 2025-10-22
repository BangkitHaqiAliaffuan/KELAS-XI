package com.trashbin.app.ui.pickup

import android.app.DatePickerDialog
import android.view.Gravity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trashbin.app.R
import com.trashbin.app.data.api.TokenManager
import com.trashbin.app.data.model.PickupItemRequest
import com.trashbin.app.data.model.WasteCategory
import com.trashbin.app.ui.adapters.PickupItemAdapter
import com.trashbin.app.ui.viewmodel.PickupViewModel
import com.trashbin.app.utils.CurrencyHelper
import com.trashbin.app.data.repository.RepositoryResult
import java.util.*

class PickupRequestActivity : AppCompatActivity() {
    private val viewModel: PickupViewModel by viewModels()
    
    // UI Components
    private lateinit var scrollView: ScrollView
    private lateinit var mainLayout: LinearLayout
    private lateinit var addressInputLayout: TextInputLayout
    private lateinit var etAddress: TextInputEditText
    private lateinit var btnPickLocation: Button
    private lateinit var scheduleLabel: TextView
    private lateinit var scheduledDateInputLayout: TextInputLayout
    private lateinit var etScheduledDate: TextInputEditText
    private lateinit var itemsLabel: TextView
    private lateinit var categorySpinner: Spinner
    private lateinit var etWeight: TextInputEditText
    private lateinit var btnAddItem: Button
    private lateinit var itemsReviewLayout: LinearLayout
    private lateinit var tvItemsReview: TextView
    private lateinit var rvItems: RecyclerView
    private lateinit var notesLabel: TextView
    private lateinit var notesInputLayout: TextInputLayout
    private lateinit var etNotes: TextInputEditText
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar
    
    private lateinit var adapter: PickupItemAdapter
    private val pickupItems = mutableListOf<PickupItemRequest>()
    private val categories = mutableListOf<WasteCategory>()
    private var selectedDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupUI()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        // Initialize UI state
        updateItemsReview()
        
        // Check authentication before loading categories
        val token = TokenManager.getToken()
        android.util.Log.d("PickupRequestActivity", "=== AUTHENTICATION CHECK ===")
        android.util.Log.d("PickupRequestActivity", "Token exists: ${token != null}")
        android.util.Log.d("PickupRequestActivity", "Token not empty: ${!token.isNullOrEmpty()}")
        if (token != null) {
            android.util.Log.d("PickupRequestActivity", "Token length: ${token.length}")
            android.util.Log.d("PickupRequestActivity", "Token: $token") // Log full token for Postman debugging
            android.util.Log.d("PickupRequestActivity", "Token format: Bearer ${token.take(20)}...")
        }
        
        // Check if user data is available
        val userData = TokenManager.getInstance().getUser()
        android.util.Log.d("PickupRequestActivity", "User data available: ${userData != null}")
        if (userData != null) {
            android.util.Log.d("PickupRequestActivity", "User ID: ${userData.id}, Name: ${userData.name}")
        }
        
        if (token.isNullOrEmpty()) {
            android.util.Log.e("PickupRequestActivity", "NO TOKEN AVAILABLE - redirecting to login")
            Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        
        // Load categories
        android.util.Log.d("PickupRequestActivity", "=== LOADING CATEGORIES ===")
        android.util.Log.d("PickupRequestActivity", "Calling viewModel.loadCategories()...")
        viewModel.loadCategories()
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

        // Address Input
        addressInputLayout = TextInputLayout(this).apply {
            hint = "Alamat Penjemputan"
        }
        etAddress = TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
        addressInputLayout.addView(etAddress)
        mainLayout.addView(addressInputLayout)

        // Pick Location Button
        btnPickLocation = Button(this).apply {
            text = "Pilih Lokasi dari Peta"
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(btnPickLocation)

        // Schedule Label
        scheduleLabel = TextView(this).apply {
            text = "Jadwal Penjemputan"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(scheduleLabel)

        // Scheduled Date Input
        scheduledDateInputLayout = TextInputLayout(this).apply {
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        etScheduledDate = TextInputEditText(this).apply {
            id = View.generateViewId()
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
            hint = "Pilih Tanggal"
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_calendar, 0)
            inputType = android.text.InputType.TYPE_NULL
        }
        scheduledDateInputLayout.addView(etScheduledDate)
        mainLayout.addView(scheduledDateInputLayout)

        // Items Label
        itemsLabel = TextView(this).apply {
            text = "Daftar Sampah"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(itemsLabel)

        // Add Item Input Section
        val addItemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }

        // Category Spinner
        categorySpinner = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f).apply {
                marginEnd = (8 * resources.displayMetrics.density).toInt()
            }
            setPadding(
                (12 * resources.displayMetrics.density).toInt(),
                (8 * resources.displayMetrics.density).toInt(),
                (12 * resources.displayMetrics.density).toInt(),
                (8 * resources.displayMetrics.density).toInt()
            )
        }
        addItemLayout.addView(categorySpinner)

        // Weight Input
        etWeight = TextInputEditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (8 * resources.displayMetrics.density).toInt()
                marginEnd = (8 * resources.displayMetrics.density).toInt()
            }
            hint = "Berat (kg)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        addItemLayout.addView(etWeight)

        // Add Item Button
        btnAddItem = Button(this).apply {
            text = "Tambah"
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        addItemLayout.addView(btnAddItem)

        mainLayout.addView(addItemLayout)

        // Items Review Section
        itemsReviewLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (8 * resources.displayMetrics.density).toInt(),
                (12 * resources.displayMetrics.density).toInt(),
                (8 * resources.displayMetrics.density).toInt(),
                (8 * resources.displayMetrics.density).toInt()
            )
            setBackgroundColor(resources.getColor(R.color.gray_200))
            visibility = View.GONE
        }
        
        val reviewTitle = TextView(this).apply {
            text = "Ringkasan Items"
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
        }
        itemsReviewLayout.addView(reviewTitle)
        
        tvItemsReview = TextView(this).apply {
            id = View.generateViewId()
            textSize = 12f
            setTextColor(resources.getColor(R.color.gray_600))
        }
        itemsReviewLayout.addView(tvItemsReview)
        
        mainLayout.addView(itemsReviewLayout)

        // Items RecyclerView
        rvItems = RecyclerView(this).apply {
            id = View.generateViewId()
            layoutManager = LinearLayoutManager(this@PickupRequestActivity)
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
            isNestedScrollingEnabled = false
        }
        mainLayout.addView(rvItems)

        // Notes Label
        notesLabel = TextView(this).apply {
            text = "Catatan (Opsional)"
            textSize = 14f
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(notesLabel)

        // Notes Input
        notesInputLayout = TextInputLayout(this).apply {
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        etNotes = TextInputEditText(this).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            hint = "Tambahkan catatan tambahan..."
        }
        notesInputLayout.addView(etNotes)
        mainLayout.addView(notesInputLayout)

        // Total Price
        tvTotalPrice = TextView(this).apply {
            id = View.generateViewId()
            text = "Total: Rp 0"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.END
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(tvTotalPrice)

        // Submit Button
        btnSubmit = Button(this).apply {
            text = "Buat Permintaan Penjemputan"
            isAllCaps = false
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
    
    private fun setupRecyclerView() {
        adapter = PickupItemAdapter(categories) { position ->
            pickupItems.removeAt(position)
            adapter.notifyItemRemoved(position)
            updateTotalPrice()
        }
        rvItems.adapter = adapter
    }
    
    private fun setupListeners() {
        etScheduledDate.setOnClickListener {
            showDatePicker()
        }
        
        btnPickLocation.setOnClickListener {
            // Open map for location selection
            Toast.makeText(this, "Fitur pilih lokasi dari peta akan diimplementasikan", Toast.LENGTH_SHORT).show()
        }
        
        btnAddItem.setOnClickListener {
            addNewItem()
        }
        
        btnSubmit.setOnClickListener {
            if (validateInput()) {
                createPickupRequest()
            }
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                etScheduledDate.setText("${selectedDay}/${selectedMonth + 1}/$selectedYear")
            },
            year, month, day
        ).apply {
            // Set minimum date to tomorrow
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_MONTH, 1)
            datePicker.minDate = tomorrow.timeInMillis
        }
        datePickerDialog.show()
    }
    
    private fun validateInput(): Boolean {
        if (etAddress.text.isNullOrEmpty()) {
            etAddress.error = "Alamat harus diisi"
            return false
        }
        
        if (selectedDate == null) {
            Toast.makeText(this, "Pilih tanggal penjemputan", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (pickupItems.isEmpty()) {
            Toast.makeText(this, "Tambahkan minimal 1 item", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun createPickupRequest() {
        val address = etAddress.text.toString()
        val notes = etNotes.text.toString()
        
        // Show loading
        showLoading(true)
        
        // Convert Calendar to string format
        val dateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val dateString = dateFormatter.format(selectedDate!!.time)
        
        // Set items in ViewModel
        viewModel.selectedItems.value = pickupItems.toMutableList()
        
        // Create pickup request (need coordinates - using default for now)
        viewModel.createPickup(address, -6.200000, 106.816666, dateString, notes)
        
        // Observe result
        viewModel.createState.observe(this) { result ->
            when (result) {
                is RepositoryResult.Loading -> showLoading(true)
                is RepositoryResult.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Permintaan penjemputan berhasil dibuat", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is RepositoryResult.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {} // Handle any other cases if needed
            }
        }
    }
    
    private fun updateTotalPrice() {
        // Calculate total based on categories and estimated weight
        val total = pickupItems.sumOf { item ->
            val category = categories.find { it.id == item.categoryId }
            (category?.basePricePerUnit ?: 0.0) * item.estimatedWeight
        }
        tvTotalPrice.text = "Total: ${CurrencyHelper.formatRupiah(total)}"
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSubmit.isEnabled = !show
    }
    
    private fun setupObservers() {
        viewModel.categories.observe(this) { result ->
            android.util.Log.d("PickupRequestActivity", "Categories result: $result")
            when (result) {
                is RepositoryResult.Success -> {
                    android.util.Log.d("PickupRequestActivity", "Categories loaded: ${result.data.size} items")
                    result.data.forEach { category ->
                        android.util.Log.d("PickupRequestActivity", "Category: ${category.name} (ID: ${category.id})")
                    }
                    
                    categories.clear()
                    categories.addAll(result.data)
                    
                    // Setup category spinner
                    setupCategorySpinner()
                    
                    // Recreate adapter with categories
                    adapter = PickupItemAdapter(categories) { position ->
                        pickupItems.removeAt(position)
                        adapter.submitList(pickupItems.toList())
                        updateTotalPrice()
                        updateItemsReview()
                    }
                    rvItems.adapter = adapter
                    
                    android.util.Log.d("PickupRequestActivity", "Spinner setup complete")
                }
                is RepositoryResult.Error -> {
                    android.util.Log.e("PickupRequestActivity", "Failed to load categories: ${result.message}")
                    Toast.makeText(this, "Failed to load categories: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is RepositoryResult.Loading -> {
                    android.util.Log.d("PickupRequestActivity", "Loading categories...")
                    // Show loading if needed
                }
                else -> {} // Handle any other cases if needed
            }
        }
    }
    
    private fun setupCategorySpinner() {
        android.util.Log.d("PickupRequestActivity", "Setting up spinner with ${categories.size} categories")
        
        if (categories.isEmpty()) {
            android.util.Log.w("PickupRequestActivity", "No categories available for spinner")
            return
        }
        
        val categoryNames = mutableListOf<String>()
        categoryNames.add("Pilih Kategori Sampah") // Add placeholder
        categoryNames.addAll(categories.map { it.name })
        
        android.util.Log.d("PickupRequestActivity", "Spinner items: $categoryNames")
        
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        categorySpinner.adapter = spinnerAdapter
        categorySpinner.setSelection(0) // Select placeholder by default
        
        android.util.Log.d("PickupRequestActivity", "Spinner adapter set successfully")
    }
    
    private fun addNewItem() {
        val selectedPosition = categorySpinner.selectedItemPosition
        val weightText = etWeight.text.toString().trim()
        
        android.util.Log.d("PickupRequestActivity", "Adding new item - Position: $selectedPosition, Weight: $weightText")
        
        // Validation - account for placeholder at position 0
        if (selectedPosition <= 0 || selectedPosition > categories.size) {
            Toast.makeText(this, "Pilih kategori sampah", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (weightText.isEmpty()) {
            etWeight.error = "Masukkan berat sampah"
            return
        }
        
        val weight = weightText.toDoubleOrNull()
        if (weight == null || weight <= 0) {
            etWeight.error = "Berat harus berupa angka positif"
            return
        }
        
        // Add item - subtract 1 from position because of placeholder
        val selectedCategory = categories[selectedPosition - 1]
        android.util.Log.d("PickupRequestActivity", "Selected category: ${selectedCategory.name} (ID: ${selectedCategory.id})")
        
        val newItem = PickupItemRequest(
            categoryId = selectedCategory.id,
            estimatedWeight = weight,
            photoUrl = null
        )
        
        pickupItems.add(newItem)
        adapter.submitList(pickupItems.toList())
        updateTotalPrice()
        updateItemsReview()
        
        // Clear input
        etWeight.setText("")
        categorySpinner.setSelection(0)
        
        Toast.makeText(this, "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateItemsReview() {
        // Update items count in items label
        itemsLabel.text = if (pickupItems.isEmpty()) {
            "Daftar Sampah"
        } else {
            "Daftar Sampah (${pickupItems.size} item)"
        }
        
        // Show/hide review section and update content
        if (pickupItems.isEmpty()) {
            itemsReviewLayout.visibility = View.GONE
        } else {
            itemsReviewLayout.visibility = View.VISIBLE
            
            // Create summary text
            val reviewText = StringBuilder()
            val groupedItems = pickupItems.groupBy { it.categoryId }
            
            groupedItems.forEach { (categoryId, items) ->
                val category = categories.find { it.id == categoryId }
                val totalWeight = items.sumOf { it.estimatedWeight }
                val totalPrice = totalWeight * (category?.basePricePerUnit ?: 0.0)
                
                reviewText.append("• ${category?.name ?: "Unknown"}: ")
                reviewText.append("${totalWeight}kg ")
                reviewText.append("(${CurrencyHelper.formatRupiah(totalPrice)})\n")
            }
            
            // Add total summary
            val totalWeight = pickupItems.sumOf { it.estimatedWeight }
            val totalPrice = pickupItems.sumOf { item ->
                val category = categories.find { it.id == item.categoryId }
                (category?.basePricePerUnit ?: 0.0) * item.estimatedWeight
            }
            
            reviewText.append("\nTotal: ${totalWeight}kg - ${CurrencyHelper.formatRupiah(totalPrice)}")
            
            tvItemsReview.text = reviewText.toString()
        }
    }
}