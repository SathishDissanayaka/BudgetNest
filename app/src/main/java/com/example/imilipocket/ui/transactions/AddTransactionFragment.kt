package com.example.imilipocket.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.imilipocket.R
import com.example.imilipocket.data.PreferenceManager
import com.example.imilipocket.data.Transaction
import com.example.imilipocket.databinding.FragmentAddTransactionBinding
import com.google.android.material.snackbar.Snackbar

class AddTransactionFragment : Fragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTransactionViewModel by viewModels {
        AddTransactionViewModelFactory(PreferenceManager(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupUI() {
        // Setup category spinner
        val categories = resources.getStringArray(R.array.transaction_categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        // Setup type radio group
        binding.radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioIncome -> {
                    binding.spinnerCategory.setSelection(0) // Reset to first category
                }
                R.id.radioExpense -> {
                    binding.spinnerCategory.setSelection(0) // Reset to first category
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.buttonSave.setOnClickListener {
            saveTransaction()
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddTransactionViewModel.SaveResult.Success -> {
                    Toast.makeText(requireContext(), "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                    if (isAdded && !isDetached) {
                        findNavController().navigateUp()
                    }
                }
                is AddTransactionViewModel.SaveResult.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveTransaction() {
        val title = binding.editTextTitle.text.toString()
        val amountText = binding.editTextAmount.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()
        val type = when (binding.radioGroupType.checkedRadioButtonId) {
            R.id.radioIncome -> Transaction.Type.INCOME
            R.id.radioExpense -> Transaction.Type.EXPENSE
            else -> {
                Snackbar.make(binding.root, "Please select transaction type", Snackbar.LENGTH_LONG).show()
                return
            }
        }

        if (title.isBlank()) {
            binding.editTextTitle.error = "Title is required"
            return
        }

        if (amountText.isBlank()) {
            binding.editTextAmount.error = "Amount is required"
            return
        }

        try {
            val amount = amountText.toDouble()
            if (amount <= 0) {
                binding.editTextAmount.error = "Amount must be greater than 0"
                return
            }
            viewModel.addTransaction(title, amount, category, type)
        } catch (e: NumberFormatException) {
            binding.editTextAmount.error = "Invalid amount"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 