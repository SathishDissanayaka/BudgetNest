package com.example.imilipocket.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imilipocket.data.PreferenceManager
import com.example.imilipocket.data.Transaction
import java.util.UUID

class AddTransactionViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
    private val _saveResult = MutableLiveData<SaveResult>()
    val saveResult: LiveData<SaveResult> = _saveResult

    fun addTransaction(title: String, amount: Double, category: String, type: Transaction.Type) {
        try {
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                title = title,
                amount = amount,
                category = category,
                type = type,
                date = System.currentTimeMillis()
            )
            preferenceManager.addTransaction(transaction)
            _saveResult.value = SaveResult.Success
        } catch (e: Exception) {
            _saveResult.value = SaveResult.Error(e.message ?: "Failed to save transaction")
        }
    }

    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }
}

class AddTransactionViewModelFactory(private val preferenceManager: PreferenceManager) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTransactionViewModel(preferenceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 