package com.example.imilipocket.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.imilipocket.R
import com.example.imilipocket.data.PreferenceManager
import com.example.imilipocket.data.Transaction
import com.example.imilipocket.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentDashboardBinding.inflate(inflater, container, false)
            return binding.root
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error in onCreateView: ${e.message}")
            throw e
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            setupViewModel()
            setupUI()
            observeViewModel()
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error in onViewCreated: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun setupViewModel() {
        val preferenceManager = PreferenceManager(requireContext())
        val factory = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(preferenceManager) as T
            }
        }
        viewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
    }

    private fun setupUI() {
        setupPieChart()
        setupLineCharts()
    }

    private fun observeViewModel() {
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        try {
            viewModel.loadDashboardData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            try {
                binding.tvTotalBalance.text = formatCurrency(balance ?: 0.0)
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvTotalBalance.text = formatCurrency(0.0)
            }
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            try {
                binding.tvTotalIncome.text = formatCurrency(income ?: 0.0)
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvTotalIncome.text = formatCurrency(0.0)
            }
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            try {
                binding.tvTotalExpense.text = formatCurrency(expense ?: 0.0)
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvTotalExpense.text = formatCurrency(0.0)
            }
        }

        viewModel.categorySpending.observe(viewLifecycleOwner) { spending ->
            try {
                updatePieChart(spending ?: emptyMap())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            try {
                updateLineCharts(transactions ?: emptyList())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupPieChart() {
        try {
            binding.pieChart.apply {
                description.isEnabled = false
                legend.isEnabled = true
                setHoleColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                setTransparentCircleColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                setEntryLabelColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                setEntryLabelTextSize(12f)
                setUsePercentValues(true)
                setDrawEntryLabels(true)
                setDrawHoleEnabled(true)
                setHoleRadius(50f)
                setTransparentCircleRadius(55f)
                setRotationEnabled(true)
                setHighlightPerTapEnabled(true)
                animateY(1000)
                setNoDataText("No transactions yet")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupLineCharts() {
        try {
            val commonSetup: com.github.mikephil.charting.charts.LineChart.() -> Unit = {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return try {
                            dateFormat.format(Date(value.toLong()))
                        } catch (e: Exception) {
                            ""
                        }
                    }
                }
                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return formatCurrency(value.toDouble())
                    }
                }
                axisRight.isEnabled = false
                setNoDataText("No data available")
            }

            binding.incomeChart.apply(commonSetup)
            binding.expenseChart.apply(commonSetup)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updatePieChart(spending: Map<String, Double>) {
        try {
            if (spending.isEmpty()) {
                binding.pieChart.setNoDataText("No transactions yet")
                binding.pieChart.invalidate()
                return
            }

            val entries = spending.map { (category, amount) ->
                PieEntry(amount.toFloat(), category)
            }

            val dataSet = PieDataSet(entries, "Categories").apply {
                colors = entries.map { entry ->
                    if (entry.label.startsWith("Income:")) {
                        ContextCompat.getColor(requireContext(), R.color.green_500)
                    } else {
                        ContextCompat.getColor(requireContext(), R.color.red_500)
                    }
                }
                valueFormatter = PercentFormatter(binding.pieChart)
                valueTextSize = 12f
                valueTextColor = ContextCompat.getColor(requireContext(), android.R.color.black)
            }

            binding.pieChart.data = PieData(dataSet)
            binding.pieChart.invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
            binding.pieChart.setNoDataText("Error loading data")
            binding.pieChart.invalidate()
        }
    }

    private fun updateLineCharts(transactions: List<Transaction>) {
        try {
            val incomeEntries = transactions
                .filter { it.type == Transaction.Type.INCOME }
                .groupBy { it.date }
                .map { (date, transactions) ->
                    Entry(date.toFloat(), transactions.sumOf { it.amount }.toFloat())
                }
                .sortedBy { it.x }

            val expenseEntries = transactions
                .filter { it.type == Transaction.Type.EXPENSE }
                .groupBy { it.date }
                .map { (date, transactions) ->
                    Entry(date.toFloat(), transactions.sumOf { it.amount }.toFloat())
                }
                .sortedBy { it.x }

            // Update Income Chart
            if (incomeEntries.isNotEmpty()) {
                val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
                    color = ContextCompat.getColor(requireContext(), R.color.green_500)
                    setDrawCircles(true)
                    setDrawValues(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return formatCurrency(value.toDouble())
                        }
                    }
                }
                binding.incomeChart.data = LineData(incomeDataSet)
            } else {
                binding.incomeChart.data = null
                binding.incomeChart.setNoDataText("No income transactions yet")
            }
            binding.incomeChart.invalidate()

            // Update Expense Chart
            if (expenseEntries.isNotEmpty()) {
                val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
                    color = ContextCompat.getColor(requireContext(), R.color.red_500)
                    setDrawCircles(true)
                    setDrawValues(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return formatCurrency(value.toDouble())
                        }
                    }
                }
                binding.expenseChart.data = LineData(expenseDataSet)
            } else {
                binding.expenseChart.data = null
                binding.expenseChart.setNoDataText("No expense transactions yet")
            }
            binding.expenseChart.invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
            binding.incomeChart.setNoDataText("Error loading data")
            binding.expenseChart.setNoDataText("Error loading data")
            binding.incomeChart.invalidate()
            binding.expenseChart.invalidate()
        }
    }

    private fun formatCurrency(amount: Double): String {
        return try {
            NumberFormat.getCurrencyInstance().format(amount)
        } catch (e: Exception) {
            e.printStackTrace()
            "$0.00"
        }
    }
} 