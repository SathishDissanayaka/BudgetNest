package com.example.imilipocket.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.imilipocket.R
import com.example.imilipocket.data.PreferenceManager
import com.example.imilipocket.data.Result
import com.example.imilipocket.data.Transaction
import com.example.imilipocket.databinding.FragmentAddTransactionBinding
import java.util.UUID

class AddTransactionFragment : Fragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddTransactionViewModel

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
        
        val preferenceManager = PreferenceManager(requireContext())
        val factory = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddTransactionViewModel(preferenceManager) as T
            }
        }
        viewModel = ViewModelProvider(this, factory).get(AddTransactionViewModel::class.java)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            // Set up category spinner
            val categories = listOf(
                "Salary",
                "Bonus",
                "Investment",
                "Food",
                "Transport",
                "Entertainment",
                "Bills",
                "Shopping",
                "Other"
            )
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter

            // Set default selection
            spinnerCategory.setSelection(0)

            // Set default radio button selection
            radioIncome.isChecked = true

            buttonSave.setOnClickListener {
                saveTransaction()
            }

            buttonCancel.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun saveTransaction() {
        binding.apply {
            val title = editTextTitle.text.toString()
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val category = spinnerCategory.selectedItem?.toString() ?: ""
            val type = if (radioIncome.isChecked) Transaction.Type.INCOME else Transaction.Type.EXPENSE

            if (title.isBlank()) {
                editTextTitle.error = "Title is required"
                return
            }
            if (amount == null) {
                editTextAmount.error = "Amount is required"
                return
            }
            if (category.isBlank()) {
                Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
                return
            }

            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                title = title,
                amount = amount,
                category = category,
                type = type,
                date = System.currentTimeMillis()
            )

            viewModel.addTransaction(transaction)
        }
    }

    private fun observeViewModel() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(requireContext(), "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Error saving transaction: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 