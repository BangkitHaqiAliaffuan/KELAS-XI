package com.kelas.balancebook.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelas.balancebook.AddTransactionActivity
import com.kelas.balancebook.R
import com.kelas.balancebook.adapter.TransactionAdapter
import com.kelas.balancebook.data.local.SessionManager
import com.kelas.balancebook.data.remote.ApiClient
import com.kelas.balancebook.data.remote.TopCategoryDto
import com.kelas.balancebook.databinding.FragmentDashboardBinding
import com.kelas.balancebook.util.CurrencyUtils
import com.kelas.balancebook.util.TransactionUiMapper
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        adapter = TransactionAdapter(emptyList())
        binding.rvRecentTransactions.adapter = adapter

        binding.tvSeeAll.setOnClickListener {
            findNavController().navigate(R.id.historyFragment)
        }

        binding.tvSeeDetails.setOnClickListener {
            findNavController().navigate(R.id.reportsFragment)
        }

        binding.btnAddIncome.setOnClickListener {
            val intent = Intent(requireContext(), AddTransactionActivity::class.java)
            intent.putExtra("type", "income")
            startActivity(intent)
        }

        binding.btnAddExpense.setOnClickListener {
            val intent = Intent(requireContext(), AddTransactionActivity::class.java)
            intent.putExtra("type", "expense")
            startActivity(intent)
        }

        loadDashboard()
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    private fun loadDashboard() {
        lifecycleScope.launch {
            runCatching {
                val api = ApiClient.service(requireContext())
                val summary = api.dashboardSummary().summary
                val recent = api.recentTransactions(5).transactions
                val currency = SessionManager.getUser(requireContext())?.currency

                binding.tvBalance.text = CurrencyUtils.format(summary.currentBalance, currency)
                binding.tvMonthIncome.text = CurrencyUtils.format(summary.monthIncome, currency)
                binding.tvMonthExpense.text = CurrencyUtils.format(summary.monthExpense, currency)
                binding.tvMonthSavings.text = CurrencyUtils.format(summary.monthSavings, currency)
                binding.tvChartTotal.text = CurrencyUtils.format(summary.chartTotal, currency)
                renderExpenseBreakdown(summary.topCategories)

                adapter.updateData(recent.map { TransactionUiMapper.map(it) })
            }.onFailure {
                Toast.makeText(requireContext(), "Gagal memuat dashboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderExpenseBreakdown(topCategories: List<TopCategoryDto>) {
        val safe = topCategories
            .filter { it.amount > 0 }
            .sortedByDescending { it.amount }

        val padded = safe.take(4).toMutableList()
        while (padded.size < 4) {
            padded.add(TopCategoryDto(category = "-", amount = 0.0, percent = 0.0))
        }

        val percentages = normalizePercentages(padded)

        val categoryViews = listOf(
            binding.tvLegendCategory1,
            binding.tvLegendCategory2,
            binding.tvLegendCategory3,
            binding.tvLegendCategory4,
        )
        val percentViews = listOf(
            binding.tvLegendPercent1,
            binding.tvLegendPercent2,
            binding.tvLegendPercent3,
            binding.tvLegendPercent4,
        )

        padded.forEachIndexed { index, item ->
            categoryViews[index].text = item.category
            percentViews[index].text = "${percentages[index]}%"
        }

        binding.chartSegment1.progress = percentages[0]
        binding.chartSegment2.progress = percentages[1]
        binding.chartSegment3.progress = percentages[2]
        binding.chartSegment4.progress = percentages[3]

        val firstRotation = percentages[0] * 3.6f
        val secondRotation = (percentages[0] + percentages[1]) * 3.6f
        val thirdRotation = (percentages[0] + percentages[1] + percentages[2]) * 3.6f

        binding.chartSegment1.rotation = 0f
        binding.chartSegment2.rotation = firstRotation
        binding.chartSegment3.rotation = secondRotation
        binding.chartSegment4.rotation = thirdRotation
    }

    private fun normalizePercentages(items: List<TopCategoryDto>): List<Int> {
        if (items.isEmpty()) return listOf(0, 0, 0, 0)

        val total = items.sumOf { it.amount }
        if (total <= 0.0) return listOf(0, 0, 0, 0)

        val raw = items.map { (it.amount / total) * 100.0 }
        val rounded = raw.map { it.roundToInt() }.toMutableList()
        val diff = 100 - rounded.sum()

        if (diff != 0 && rounded.isNotEmpty()) {
            val targetIndex = raw.indices.maxByOrNull { raw[it] - rounded[it] } ?: 0
            rounded[targetIndex] = (rounded[targetIndex] + diff).coerceIn(0, 100)
        }

        return rounded
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
