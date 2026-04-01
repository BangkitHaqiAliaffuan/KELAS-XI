package com.kelas.balancebook.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelas.balancebook.AddTransactionActivity
import com.kelas.balancebook.R
import com.kelas.balancebook.adapter.TransactionAdapter
import com.kelas.balancebook.databinding.FragmentDashboardBinding
import com.kelas.balancebook.model.Transaction
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val sampleTransactions = listOf(
        Transaction(1, "Food & Dining", "Makan siang", "Hari ini",
            85_000, false, R.drawable.ic_restaurant, R.drawable.bg_rounded_orange, R.color.colorOrange),
        Transaction(2, "Transportasi", "Grab ride", "Hari ini",
            35_000, false, R.drawable.ic_directions_car, R.drawable.bg_rounded_blue, R.color.colorBlue),
        Transaction(3, "Gaji", "Gaji bulanan", "Kemarin",
            5_000_000, true, R.drawable.ic_payments, R.drawable.bg_rounded_emerald, R.color.colorEmerald),
        Transaction(4, "Belanja", "Belanja bulanan", "10 Mar",
            250_000, false, R.drawable.ic_shopping_cart, R.drawable.bg_rounded_indigo, R.color.colorIndigo),
        Transaction(5, "Hiburan", "Netflix", "9 Mar",
            54_000, false, R.drawable.ic_theater, R.drawable.bg_rounded_purple, R.color.colorPurple)
    )

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

        val fmt = NumberFormat.getInstance(Locale("id", "ID"))
        binding.tvBalance.text = "Rp ${fmt.format(4_576_000)}"
        binding.tvMonthIncome.text = "Rp ${fmt.format(5_000_000)}"
        binding.tvMonthExpense.text = "Rp ${fmt.format(424_000)}"
        binding.tvMonthSavings.text = "Rp ${fmt.format(4_576_000)}"
        binding.tvChartTotal.text = "Rp ${fmt.format(424_000)}"

        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentTransactions.adapter = TransactionAdapter(sampleTransactions)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
