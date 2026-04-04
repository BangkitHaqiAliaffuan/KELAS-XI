package com.kelas.balancebook.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kelas.balancebook.R
import com.kelas.balancebook.data.local.SessionManager
import com.kelas.balancebook.data.remote.ApiClient
import com.kelas.balancebook.data.remote.ReportDto
import com.kelas.balancebook.data.remote.TopCategoryDto
import com.kelas.balancebook.databinding.FragmentReportsBinding
import com.kelas.balancebook.util.CurrencyUtils
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

class ReportsFragment : Fragment() {
    private var currentPeriod: String = "monthly"
    private var latestReport: ReportDto? = null


    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPeriodToggle()
        setupClickListeners()
        loadReport()
    }

    private fun setupPeriodToggle() {
        val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip_period_active)
        val activeColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.colorTextSecondary)

        fun selectPeriod(selected: Int) {
            listOf(binding.btnMonthly, binding.btnQuarterly, binding.btnYearly).forEachIndexed { idx, btn ->
                if (idx == selected) {
                    btn.background = activeDrawable
                    btn.setTextColor(activeColor)
                    btn.elevation = 2f
                } else {
                    btn.background = null
                    btn.setTextColor(inactiveColor)
                    btn.elevation = 0f
                }
            }

            currentPeriod = when (selected) {
                1 -> "quarterly"
                2 -> "yearly"
                else -> "monthly"
            }

            loadReport()
        }

        binding.btnMonthly.setOnClickListener { selectPeriod(0) }
        binding.btnQuarterly.setOnClickListener { selectPeriod(1) }
        binding.btnYearly.setOnClickListener { selectPeriod(2) }

        // Default: Monthly active
        selectPeriod(0)
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCalendar.setOnClickListener {
            Toast.makeText(requireContext(), "Periode: ${currentPeriod.replaceFirstChar { it.uppercase() }}", Toast.LENGTH_SHORT).show()
        }

        binding.tvViewAllCategories.setOnClickListener {
            val categories = latestReport?.topCategories.orEmpty()
            if (categories.isEmpty()) {
                Toast.makeText(requireContext(), "Belum ada kategori", Toast.LENGTH_SHORT).show()
            } else {
                val currency = SessionManager.getUser(requireContext())?.currency
                val message = categories.joinToString("\n") {
                    "${it.category}: ${CurrencyUtils.format(it.amount, currency)} (${it.percent.roundToInt()}%)"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setProgressWidth(progressView: View, percent: Int) {
        progressView.post {
            val parent = progressView.parent as? View ?: return@post
            val params = progressView.layoutParams
            params.width = (parent.width * percent / 100)
            progressView.layoutParams = params
        }
    }

    private fun loadReport() {
        lifecycleScope.launch {
            runCatching {
                val report = ApiClient.service(requireContext()).reportOverview(currentPeriod).report
                latestReport = report
                val currency = SessionManager.getUser(requireContext())?.currency

                binding.tvMonthlyExpensesAmount.text = CurrencyUtils.format(report.monthlyExpenses, currency)
                binding.tvExpenseChange.text = "${report.expenseChangePercent}%"

                binding.tvIncomeAmount.text = CurrencyUtils.format(report.income, currency)
                binding.tvExpensesBarAmount.text = CurrencyUtils.format(report.expenses, currency)
                binding.tvSavingsAmount.text = CurrencyUtils.format(report.savings, currency)
                binding.tvNetCashFlow.text = CurrencyUtils.format(report.netCashFlow, currency)
                binding.tvNetCashFlow.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (report.netCashFlow >= 0) R.color.colorIncome else R.color.colorExpense
                    )
                )

                bindTopCategories(report, currency)

                bindMonthlyChart(report)
                bindIncomeExpenseBars(report)
            }.onFailure {
                Toast.makeText(requireContext(), "Gagal memuat laporan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindMonthlyChart(report: ReportDto) {
        val monthMap = report.timeline.associate { it.monthKey to it.amount }
        val cal = Calendar.getInstance()

        val keys = mutableListOf<String>()
        val labels = mutableListOf<String>()

        for (offset in 5 downTo 0) {
            val c = cal.clone() as Calendar
            c.add(Calendar.MONTH, -offset)
            val key = String.format(Locale.US, "%04d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1)
            keys.add(key)
            labels.add(DateFormatSymbols(Locale.US).shortMonths[c.get(Calendar.MONTH)].uppercase(Locale.US).take(3))
        }

        val points = keys.map { monthMap[it] ?: 0.0 }
        binding.viewLineChart.setData(points)

        val labelViews = listOf(
            binding.tvMonthLabel1,
            binding.tvMonthLabel2,
            binding.tvMonthLabel3,
            binding.tvMonthLabel4,
            binding.tvMonthLabel5,
            binding.tvMonthLabel6,
        )
        labelViews.zip(labels).forEach { (view, text) -> view.text = text }
    }

    private fun bindIncomeExpenseBars(report: ReportDto) {
        val maxValue = max(max(report.income, report.expenses), max(report.savings, 1.0))

        val minDp = 24f
        val maxDp = 144f

        fun scaledHeight(value: Double): Int {
            val ratio = (value / maxValue).toFloat().coerceIn(0f, 1f)
            val dp = minDp + ((maxDp - minDp) * ratio)
            return dpToPx(dp)
        }

        setViewHeight(binding.barIncome, scaledHeight(report.income))
        setViewHeight(binding.barExpenses, scaledHeight(report.expenses))
        setViewHeight(binding.barSavings, scaledHeight(report.savings))

        val fixedGap = dpToPx(12f)
        setViewHeight(binding.spaceIncome, fixedGap)
        setViewHeight(binding.spaceExpenses, fixedGap)
        setViewHeight(binding.spaceSavings, fixedGap)
    }

    private fun bindTopCategories(report: ReportDto, currency: String?) {
        val sorted = report.topCategories.sortedByDescending { it.amount }
        val topThree = sorted.take(3)

        val topThreeSum = topThree.sumOf { it.amount }
        val totalExpense = report.expenses.coerceAtLeast(0.0)
        val otherAmount = (totalExpense - topThreeSum).coerceAtLeast(0.0)
        val otherPercent = if (totalExpense > 0) (otherAmount / totalExpense) * 100.0 else 0.0

        val normalized = buildList {
            addAll(topThree)
            while (size < 3) {
                add(TopCategoryDto("-", 0.0, 0.0))
            }
            add(TopCategoryDto("Other", otherAmount, otherPercent))
        }

        binding.tvHousingCategory.text = normalized[0].category
        binding.tvFoodCategory.text = normalized[1].category
        binding.tvTransportCategory.text = normalized[2].category
        binding.tvEntertainmentCategory.text = "Other"

        applyCategoryIcon(binding.ivHousingCategoryIcon, normalized[0].category)
        applyCategoryIcon(binding.ivFoodCategoryIcon, normalized[1].category)
        applyCategoryIcon(binding.ivTransportCategoryIcon, normalized[2].category)
        applyCategoryIcon(binding.ivEntertainmentCategoryIcon, "Other")

        binding.tvHousingAmount.text = CurrencyUtils.format(normalized[0].amount, currency)
        binding.tvFoodAmount.text = CurrencyUtils.format(normalized[1].amount, currency)
        binding.tvTransportAmount.text = CurrencyUtils.format(normalized[2].amount, currency)
        binding.tvEntertainmentCatAmount.text = CurrencyUtils.format(normalized[3].amount, currency)

        binding.tvHousingPercent.text = "${normalized[0].percent.roundToInt()}% of total expenses"
        binding.tvFoodPercent.text = "${normalized[1].percent.roundToInt()}% of total expenses"
        binding.tvTransportPercent.text = "${normalized[2].percent.roundToInt()}% of total expenses"
        binding.tvEntertainmentPercent.text = "${normalized[3].percent.roundToInt()}% of total expenses"

        setProgressWidth(binding.progressHousing, normalized[0].percent.roundToInt().coerceIn(0, 100))
        setProgressWidth(binding.progressFood, normalized[1].percent.roundToInt().coerceIn(0, 100))
        setProgressWidth(binding.progressTransport, normalized[2].percent.roundToInt().coerceIn(0, 100))
        setProgressWidth(binding.progressEntertainmentCat, normalized[3].percent.roundToInt().coerceIn(0, 100))
    }

    private fun applyCategoryIcon(imageView: android.widget.ImageView, category: String) {
        val lower = category.lowercase(Locale.getDefault())

        val iconRes = when {
            lower.contains("food") || lower.contains("dining") || lower.contains("makan") -> R.drawable.ic_restaurant
            lower.contains("transport") || lower.contains("travel") || lower.contains("perjalanan") -> R.drawable.ic_directions_car
            lower.contains("bill") || lower.contains("utility") || lower.contains("housing") || lower.contains("sewa") -> R.drawable.ic_home_fill
            lower.contains("entertain") || lower.contains("movie") || lower.contains("theater") || lower.contains("hiburan") -> R.drawable.ic_theater
            lower.contains("shop") || lower.contains("belanja") -> R.drawable.ic_shopping_cart
            lower.contains("health") -> R.drawable.ic_redeem
            lower.contains("other") || lower == "-" -> R.drawable.ic_redeem
            else -> R.drawable.ic_category
        }

        val (bgRes, tintRes) = when {
            lower.contains("food") || lower.contains("dining") || lower.contains("makan") -> R.drawable.bg_rounded_orange to R.color.colorOrange
            lower.contains("transport") || lower.contains("travel") || lower.contains("perjalanan") -> R.drawable.bg_rounded_blue to R.color.colorBlue
            lower.contains("bill") || lower.contains("utility") || lower.contains("housing") || lower.contains("sewa") -> R.drawable.bg_rounded_indigo to R.color.colorIndigo
            lower.contains("entertain") || lower.contains("movie") || lower.contains("theater") || lower.contains("hiburan") -> R.drawable.bg_rounded_purple to R.color.colorPurple
            lower.contains("other") || lower == "-" -> R.drawable.bg_rounded_purple to R.color.colorPurple
            else -> R.drawable.bg_rounded_primary_light to R.color.colorPrimary
        }

        imageView.setBackgroundResource(bgRes)
        imageView.setImageResource(iconRes)
        ImageViewCompat.setImageTintList(
            imageView,
            ContextCompat.getColorStateList(requireContext(), tintRes)
        )
    }

    private fun setViewHeight(view: View, heightPx: Int) {
        val params = view.layoutParams
        params.height = heightPx
        view.layoutParams = params
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).roundToInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
