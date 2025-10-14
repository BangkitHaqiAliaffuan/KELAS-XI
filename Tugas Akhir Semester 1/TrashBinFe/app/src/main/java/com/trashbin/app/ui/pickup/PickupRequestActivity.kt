package com.trashbin.app.ui.pickup

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.trashbin.app.R
import com.trashbin.app.data.model.WasteCategory
import com.trashbin.app.ui.adapters.PickupItemAdapter
import com.trashbin.app.ui.bottomsheet.CategoryBottomSheet
import com.trashbin.app.ui.viewmodel.PickupViewModel
import com.trashbin.app.utils.CurrencyHelper
import com.trashbin.app.utils.DateHelper
import com.trashbin.app.utils.Result
import java.util.*

class PickupRequestActivity : AppCompatActivity() {
    private val viewModel: PickupViewModel by viewModels()
    private lateinit var adapter: PickupItemAdapter
    
    private lateinit var etAddress: EditText
    private lateinit var etScheduledDate: EditText
    private lateinit var etNotes: EditText
    private lateinit var rvItems: RecyclerView
    private lateinit var btnPickLocation: MaterialButton
    private lateinit var btnAddItem: MaterialButton
    private lateinit var btnSubmit: MaterialButton
    private lateinit var tvTotalPrice: View
    private lateinit var progressBar: CircularProgressIndicator
    
    private var selectedLat = 0.0
    private var selectedLng = 0.0
    private var selectedAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pickup_request)
        
        initViews()
        setupRecyclerView()
        observeViewModel()
        setupListeners()
        
        viewModel.loadCategories()
    }
    
    private fun initViews() {
        etAddress = findViewById(R.id.et_address)
        etScheduledDate = findViewById(R.id.et_scheduled_date)
        etNotes = findViewById(R.id.et_notes)
        rvItems = findViewById(R.id.rv_items)
        btnPickLocation = findViewById(R.id.btn_pick_location)
        btnAddItem = findViewById(R.id.btn_add_item)
        btnSubmit = findViewById(R.id.btn_submit)
        tvTotalPrice = findViewById(R.id.tv_total_price)
        progressBar = findViewById(R.id.progress_bar)
    }
    
    private fun setupRecyclerView() {
        adapter = PickupItemAdapter(emptyList()) { index -> 
            viewModel.removeItem(index) 
        }
        rvItems.adapter = adapter
        rvItems.layoutManager = LinearLayoutManager(this)
    }
    
    private fun observeViewModel() {
        viewModel.selectedItems.observe(this) { items ->
            adapter.submitList(items)
            calculateTotal(items)
        }
        
        viewModel.categories.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // Show loading if needed
                }
                is Result.Success -> {
                    adapter = PickupItemAdapter(result.data) { index -> 
                        viewModel.removeItem(index) 
                    }
                    rvItems.adapter = adapter
                    adapter.submitList(viewModel.selectedItems.value ?: emptyList())
                }
                is Result.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        viewModel.createState.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Pickup berhasil dibuat", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupListeners() {
        btnPickLocation.setOnClickListener {
            // TODO: Open map picker activity
            Toast.makeText(this, "Fitur pilih lokasi dari peta akan diimplementasikan", Toast.LENGTH_SHORT).show()
        }
        
        btnAddItem.setOnClickListener {
            showCategoryDialog()
        }
        
        etScheduledDate.setOnClickListener {
            showDatePicker()
        }
        
        btnSubmit.setOnClickListener {
            if (validate()) {
                val address = etAddress.text.toString()
                val date = etScheduledDate.text.toString()
                val notes = if (etNotes.text.toString().isEmpty()) null else etNotes.text.toString()
                
                viewModel.createPickup(
                    address, 
                    selectedLat, 
                    selectedLng,
                    "$date 09:00:00", // Format to match backend expectation
                    notes
                )
            }
        }
    }
    
    private fun showCategoryDialog() {
        val categories = when (val result = viewModel.categories.value) {
            is Result.Success -> result.data
            else -> emptyList()
        }
        
        if (categories.isNotEmpty()) {
            val dialog = CategoryBottomSheet(categories) { category ->
                showWeightDialog(category)
            }
            dialog.show(supportFragmentManager, "category")
        } else {
            Toast.makeText(this, "Memuat kategori sampah...", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showWeightDialog(category: WasteCategory) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.hint = "Berat (${category.unit})"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        
        builder.setTitle("Tambah ${category.name}")
            .setView(input)
            .setPositiveButton("Tambah") { _, _ ->
                val weightText = input.text.toString()
                if (weightText.isNotEmpty()) {
                    val weight = weightText.toDoubleOrNull()
                    if (weight != null && weight > 0) {
                        viewModel.addItem(category.id, weight)
                    } else {
                        Toast.makeText(this, "Berat harus lebih dari 0", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Masukkan berat yang valid", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                etScheduledDate.setText(DateHelper.formatDate("$selectedDate 00:00:00"))
            },
            year, month, day
        )
        
        datePickerDialog.show()
    }
    
    private fun calculateTotal(items: List<com.trashbin.app.data.model.PickupItemRequest>) {
        val categories = when (val result = viewModel.categories.value) {
            is Result.Success -> result.data
            else -> emptyList()
        }
        
        var total = 0.0
        for (item in items) {
            val category = categories.find { it.id == item.categoryId }
            if (category != null) {
                total += category.basePricePerUnit * item.estimatedWeight
            }
        }
        
        findViewById<TextView>(R.id.tv_total_price).text = "Total: ${CurrencyHelper.formatRupiah(total)}"
    }
    
    private fun validate(): Boolean {
        if (etAddress.text.isNullOrEmpty()) {
            etAddress.error = "Alamat penjemputan harus diisi"
            return false
        }
        if (viewModel.selectedItems.value.isNullOrEmpty()) {
            Toast.makeText(this, "Tambahkan minimal 1 sampah", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etScheduledDate.text.isNullOrEmpty()) {
            Toast.makeText(this, "Pilih jadwal penjemputan", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSubmit.isEnabled = !show
    }
}