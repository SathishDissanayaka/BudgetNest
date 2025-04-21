package com.example.imilipocket.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.imilipocket.R
import com.example.imilipocket.databinding.FragmentSettingsBinding
import com.example.imilipocket.data.PreferenceManager

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())
        viewModel = ViewModelProvider(this, SettingsViewModelFactory(preferenceManager))
            .get(SettingsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
        observeViewModel()
        
        // Set initial dark mode state
        binding.switchDarkMode.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun setupUI() {
        // Setup currency spinner
        val currencies = listOf(
            getString(R.string.currency_usd),
            getString(R.string.currency_eur),
            getString(R.string.currency_gbp),
            getString(R.string.currency_jpy),
            getString(R.string.currency_inr),
            getString(R.string.currency_aud),
            getString(R.string.currency_cad),
            getString(R.string.currency_lkr),
            getString(R.string.currency_cny),
            getString(R.string.currency_sgd),
            getString(R.string.currency_myr),
            getString(R.string.currency_thb),
            getString(R.string.currency_idr),
            getString(R.string.currency_php),
            getString(R.string.currency_vnd),
            getString(R.string.currency_krw),
            getString(R.string.currency_aed),
            getString(R.string.currency_sar),
            getString(R.string.currency_qar)
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            currencies
        )
        
        binding.spinnerCurrency.apply {
            setAdapter(adapter)
            threshold = 1
            
            // Set current currency
            val currentCurrency = preferenceManager.getSelectedCurrency()
            val currencyIndex = currencies.indexOfFirst { it.startsWith(currentCurrency) }
            if (currencyIndex != -1) {
                setText(currencies[currencyIndex], false)
            }
            
            setOnItemClickListener { _, _, position, _ ->
                val selectedCurrency = adapter.getItem(position).toString()
                val currencyCode = selectedCurrency.substring(0, 3)
                preferenceManager.setSelectedCurrency(currencyCode)
                Toast.makeText(requireContext(), "Currency updated to $selectedCurrency", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSaveCurrency.setOnClickListener {
                val selectedCurrency = spinnerCurrency.text.toString()
                viewModel.setSelectedCurrency(selectedCurrency)
                Toast.makeText(requireContext(), "Currency saved", Toast.LENGTH_SHORT).show()
            }

            switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                val mode = if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }

    private fun observeViewModel() {
        // Implement the logic to observe the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 