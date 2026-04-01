package com.kelas.balancebook.ui.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelas.balancebook.R
import com.kelas.balancebook.adapter.TransactionAdapter
import com.kelas.balancebook.databinding.FragmentHistoryBinding
import com.kelas.balancebook.model.Transaction

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val allTransactions = listOf(
        Transaction(1,  "Food & Dining",  "Makan siang",       "Hari ini",   85_000,    false, R.drawable.ic_restaurant,    R.drawable.bg_rounded_orange,  R.color.colorOrange),
        Transaction(2,  "Transportasi",   "Grab ride",          "Hari ini",   35_000,    false, R.drawable.ic_directions_car, R.drawable.bg_rounded_blue,    R.color.colorBlue),
        Transaction(3,  "Gaji",           "Gaji bulanan",       "Kemarin",    5_000_000, true,  R.drawable.ic_payments,      R.drawable.bg_rounded_emerald, R.color.colorEmerald),
        Transaction(4,  "Belanja",        "Belanja bulanan",    "10 Mar",     250_000,   false, R.drawable.ic_shopping_cart, R.drawable.bg_rounded_indigo,  R.color.colorIndigo),
        Transaction(5,  "Hiburan",        "Netflix",            "9 Mar",      54_000,    false, R.drawable.ic_theater,       R.drawable.bg_rounded_purple,  R.color.colorPurple),
        Transaction(6,  "Kopi",           "Kopi pagi",          "8 Mar",      45_000,    false, R.drawable.ic_coffee,        R.drawable.bg_rounded_orange,  R.color.colorOrange),
        Transaction(7,  "Transportasi",   "Bus TransJakarta",   "7 Mar",      10_000,    false, R.drawable.ic_directions_car, R.drawable.bg_rounded_blue,   R.color.colorBlue),
        Transaction(8,  "Food & Dining",  "Makan malam",        "7 Mar",      120_000,   false, R.drawable.ic_restaurant,    R.drawable.bg_rounded_orange,  R.color.colorOrange),
        Transaction(9,  "Belanja",        "Alat tulis",         "6 Mar",      75_000,    false, R.drawable.ic_shopping_bag,  R.drawable.bg_rounded_indigo,  R.color.colorIndigo),
        Transaction(10, "Pemasukan",      "Freelance project",  "5 Mar",      1_500_000, true,  R.drawable.ic_payments,      R.drawable.bg_rounded_emerald, R.color.colorEmerald)
    )

    private lateinit var adapter: TransactionAdapter

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

        adapter = TransactionAdapter(allTransactions)
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter

        binding.layoutEmpty.visibility = View.GONE
        binding.rvTransactions.visibility = View.VISIBLE

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()?.lowercase() ?: ""
                val filtered = if (query.isEmpty()) {
                    allTransactions
                } else {
                    allTransactions.filter {
                        it.category.lowercase().contains(query) ||
                        it.note.lowercase().contains(query)
                    }
                }
                adapter.updateData(filtered)
                binding.layoutEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
                binding.rvTransactions.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipDate.setOnClickListener {
            Toast.makeText(requireContext(), "Date filter", Toast.LENGTH_SHORT).show()
        }
        binding.chipCategory.setOnClickListener {
            Toast.makeText(requireContext(), "Category filter", Toast.LENGTH_SHORT).show()
        }
        binding.chipType.setOnClickListener {
            Toast.makeText(requireContext(), "Type filter", Toast.LENGTH_SHORT).show()
        }
        binding.chipMore.setOnClickListener {
            Toast.makeText(requireContext(), "More filters", Toast.LENGTH_SHORT).show()
        }
        binding.ivMoreOptions.setOnClickListener {
            Toast.makeText(requireContext(), "Options", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
