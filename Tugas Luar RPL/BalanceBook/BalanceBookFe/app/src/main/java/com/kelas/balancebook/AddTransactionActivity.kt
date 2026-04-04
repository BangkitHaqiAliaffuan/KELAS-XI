package com.kelas.balancebook

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kelas.balancebook.data.remote.ApiClient
import com.kelas.balancebook.data.remote.CreateTransactionRequest
import com.kelas.balancebook.databinding.ActivityAddTransactionBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private var isIncome = false
    private var selectedDateApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    private val incomeCategories = listOf(
        "Select category", "Salary", "Allowance", "Bonus", "Gift", "Freelance", "Investment", "Other"
    )
    private val expenseCategories = listOf(
        "Select category", "Food & Dining", "Transportation", "Shopping", "Entertainment", "Bills & Utilities", "Health", "Education", "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }

        // Set default date to today
        updateDateField(Calendar.getInstance())

        // Default from caller
        val type = intent.getStringExtra("type") ?: "expense"
        selectType(isIncome = type == "income")

        binding.btnTypeIncome.setOnClickListener { selectType(isIncome = true) }
        binding.btnTypeExpense.setOnClickListener { selectType(isIncome = false) }

        binding.etDate.setOnClickListener { showDatePicker() }
        binding.containerDate.setOnClickListener { showDatePicker() }

        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                saveTransaction()
            }
        }
    }

    private fun selectType(isIncome: Boolean) {
        this.isIncome = isIncome
        val activeDrawable = ContextCompat.getDrawable(this, R.drawable.bg_chip_period_active)
        val activeColor = ContextCompat.getColor(this, R.color.colorPrimary)
        val inactiveColor = ContextCompat.getColor(this, R.color.colorTextSecondary)

        if (isIncome) {
            binding.btnTypeIncome.background = activeDrawable
            binding.btnTypeIncome.setTextColor(activeColor)
            binding.btnTypeExpense.background = null
            binding.btnTypeExpense.setTextColor(inactiveColor)
        } else {
            binding.btnTypeExpense.background = activeDrawable
            binding.btnTypeExpense.setTextColor(activeColor)
            binding.btnTypeIncome.background = null
            binding.btnTypeIncome.setTextColor(inactiveColor)
        }
        updateCategoryAdapter(isIncome)
    }

    private fun updateCategoryAdapter(isIncome: Boolean) {
        val categories = if (isIncome) incomeCategories else expenseCategories
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                updateDateField(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateField(calendar: Calendar) {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.etDate.setText(formatter.format(calendar.time))
        selectedDateApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    private fun validateInputs(): Boolean {
        val amount = binding.etAmount.text.toString().trim()
        val category = binding.spinnerCategory.selectedItemPosition

        if (amount.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            binding.etAmount.requestFocus()
            return false
        }
        if (category == 0) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveTransaction() {
        val amount = binding.etAmount.text.toString().trim().replace(",", "").toDoubleOrNull() ?: 0.0
        val category = binding.spinnerCategory.selectedItem?.toString().orEmpty()
        val note = binding.etNote.text.toString().trim().ifBlank { null }

        binding.btnSave.isEnabled = false

        lifecycleScope.launch {
            runCatching {
                ApiClient.service(this@AddTransactionActivity).createTransaction(
                    CreateTransactionRequest(
                        type = if (isIncome) "income" else "expense",
                        category = category,
                        note = note,
                        amount = amount,
                        transactionDate = selectedDateApi
                    )
                )
            }.onSuccess {
                Toast.makeText(this@AddTransactionActivity, "Transaction saved", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }.onFailure {
                Toast.makeText(
                    this@AddTransactionActivity,
                    it.message ?: "Gagal menyimpan transaksi",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.btnSave.isEnabled = true
        }
    }
}
