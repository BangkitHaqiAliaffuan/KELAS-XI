package com.kelas.balancebook.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kelas.balancebook.R
import com.kelas.balancebook.databinding.FragmentReportsBinding

class ReportsFragment : Fragment() {

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
        setupPlaceholderData()
        setupClickListeners()
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
        }

        binding.btnMonthly.setOnClickListener { selectPeriod(0) }
        binding.btnQuarterly.setOnClickListener { selectPeriod(1) }
        binding.btnYearly.setOnClickListener { selectPeriod(2) }

        // Default: Monthly active
        selectPeriod(0)
    }

    private fun setupPlaceholderData() {
        // Monthly expenses
        binding.tvMonthlyExpensesAmount.text = "Rp 4.250.000"
        binding.tvExpenseChange.text = "12.5%"

        // Bar chart amounts
        binding.tvIncomeAmount.text = "Rp 8.5jt"
        binding.tvExpensesBarAmount.text = "Rp 4.2jt"
        binding.tvSavingsAmount.text = "Rp 4.3jt"
        binding.tvNetCashFlow.text = "+Rp 4.250.000"

        // Category amounts
        binding.tvHousingAmount.text = "Rp 1.870.000"
        binding.tvFoodAmount.text = "Rp 807.500"
        binding.tvTransportAmount.text = "Rp 467.500"
        binding.tvEntertainmentCatAmount.text = "Rp 318.750"

        // Category progress bars (set after layout pass)
        setProgressWidth(binding.progressHousing, 44)
        setProgressWidth(binding.progressFood, 19)
        setProgressWidth(binding.progressTransport, 11)
        setProgressWidth(binding.progressEntertainmentCat, 8)
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCalendar.setOnClickListener {
            Toast.makeText(requireContext(), "Select period", Toast.LENGTH_SHORT).show()
        }

        binding.tvViewAllCategories.setOnClickListener {
            Toast.makeText(requireContext(), "View all categories", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
