package com.example.imilipocket.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.imilipocket.data.PreferenceManager

class BudgetViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
    private val _budget = MutableLiveData<Double>(0.0)
    val budget: LiveData<Double> = _budget

    init {
        loadBudget()
    }

    private fun loadBudget() {
        try {
            _budget.value = preferenceManager.getMonthlyBudget()
        } catch (e: Exception) {
            e.printStackTrace()
            _budget.value = 0.0
        }
    }

    fun updateBudget(newBudget: Double) {
        try {
            preferenceManager.saveMonthlyBudget(newBudget)
            _budget.value = newBudget
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMonthlyExpenses(): Double {
        return try {
            preferenceManager.getMonthlyExpenses()
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    class Factory(private val preferenceManager: PreferenceManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BudgetViewModel(preferenceManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 