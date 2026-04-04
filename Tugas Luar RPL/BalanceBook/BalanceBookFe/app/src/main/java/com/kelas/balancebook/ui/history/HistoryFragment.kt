package com.kelas.balancebook.ui.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelas.balancebook.adapter.TransactionAdapter
import com.kelas.balancebook.data.remote.ApiClient
import com.kelas.balancebook.databinding.FragmentHistoryBinding
import com.kelas.balancebook.model.Transaction
import com.kelas.balancebook.util.TransactionUiMapper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TransactionAdapter
    private var allTransactions: List<Transaction> = emptyList()
    private var typeFilter: String? = null
    private var categoryFilter: String? = null
    private var fromDateFilter: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter(emptyList())
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter

        updateUi(allTransactions)

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyLocalSearchAndRender(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipDate.setOnClickListener {
            val cal = Calendar.getInstance()
            android.app.DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val picked = Calendar.getInstance()
                    picked.set(year, month, day)
                    fromDateFilter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(picked.time)
                    loadTransactions()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.chipCategory.setOnClickListener {
            showCategoryMenu()
        }
        binding.chipType.setOnClickListener {
            val popup = PopupMenu(requireContext(), binding.chipType)
            popup.menu.add("Semua")
            popup.menu.add("Income")
            popup.menu.add("Expense")
            popup.setOnMenuItemClickListener {
                typeFilter = when (it.title.toString()) {
                    "Income" -> "income"
                    "Expense" -> "expense"
                    else -> null
                }
                loadTransactions()
                true
            }
            popup.show()
        }
        binding.chipMore.setOnClickListener {
            typeFilter = null
            categoryFilter = null
            fromDateFilter = null
            binding.etSearch.setText("")
            loadTransactions()
            Toast.makeText(requireContext(), "Filter direset", Toast.LENGTH_SHORT).show()
        }
        binding.ivMoreOptions.setOnClickListener {
            Toast.makeText(requireContext(), "Options", Toast.LENGTH_SHORT).show()
        }

        loadTransactions()
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {
        lifecycleScope.launch {
            runCatching {
                val options = linkedMapOf<String, String>()
                typeFilter?.let { options["type"] = it }
                categoryFilter?.let { options["category"] = it }
                fromDateFilter?.let { options["from"] = it }
                options["limit"] = "200"

                val response = ApiClient.service(requireContext()).transactions(options)
                allTransactions = response.transactions.map { TransactionUiMapper.map(it) }
                applyLocalSearchAndRender(binding.etSearch.text?.toString().orEmpty())
            }.onFailure {
                Toast.makeText(requireContext(), "Gagal memuat transaksi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyLocalSearchAndRender(searchText: String) {
        val query = searchText.trim().lowercase()
        val filtered = if (query.isEmpty()) {
            allTransactions
        } else {
            allTransactions.filter {
                it.category.lowercase().contains(query) ||
                    it.note.lowercase().contains(query)
            }
        }
        updateUi(filtered)
    }

    private fun updateUi(items: List<Transaction>) {
        adapter.updateData(items)
        binding.layoutEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        binding.rvTransactions.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showCategoryMenu() {
        val categories = allTransactions.map { it.category }.distinct().sorted()
        val popup = PopupMenu(requireContext(), binding.chipCategory)
        popup.menu.add("Semua")
        categories.forEach { popup.menu.add(it) }
        popup.setOnMenuItemClickListener {
            categoryFilter = if (it.title == "Semua") null else it.title.toString()
            loadTransactions()
            true
        }
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
