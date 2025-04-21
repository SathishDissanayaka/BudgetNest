package com.example.imilipocket.data

import android.content.Context
import android.content.SharedPreferences
import com.example.imilipocket.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val context: Context = context

    companion object {
        private const val PREFS_NAME = "ImiliPocketPrefs"
        private const val KEY_TRANSACTIONS = "transactions"
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
        private const val KEY_SELECTED_CURRENCY = "selected_currency"
        private const val DEFAULT_CURRENCY = "USD"
    }

    fun saveMonthlyBudget(budget: Double) {
        try {
            sharedPreferences.edit().putFloat(KEY_MONTHLY_BUDGET, budget.toFloat()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMonthlyBudget(): Double {
        return try {
            sharedPreferences.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    fun getMonthlyExpenses(): Double {
        return try {
            val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            
            getTransactions()
                .filter { 
                    val transactionDate = java.util.Calendar.getInstance().apply { 
                        timeInMillis = it.date 
                    }
                    transactionDate.get(java.util.Calendar.MONTH) == currentMonth &&
                    transactionDate.get(java.util.Calendar.YEAR) == currentYear &&
                    it.type == Transaction.Type.EXPENSE
                }
                .sumOf { it.amount }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    fun saveTransactions(transactions: List<Transaction>) {
        try {
            val json = gson.toJson(transactions)
            sharedPreferences.edit().putString(KEY_TRANSACTIONS, json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
            // If saving fails, try to save an empty list as fallback
            try {
                sharedPreferences.edit().putString(KEY_TRANSACTIONS, "[]").apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getTransactions(): List<Transaction> {
        return try {
            val json = sharedPreferences.getString(KEY_TRANSACTIONS, "[]")
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun addTransaction(transaction: Transaction) {
        try {
            val transactions = getTransactions().toMutableList()
            transactions.add(transaction)
            saveTransactions(transactions)
        } catch (e: Exception) {
            e.printStackTrace()
            // If adding fails, try to initialize with just this transaction
            try {
                saveTransactions(listOf(transaction))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        try {
            val transactions = getTransactions().toMutableList()
            val index = transactions.indexOfFirst { it.id == transaction.id }
            if (index != -1) {
                transactions[index] = transaction
                saveTransactions(transactions)
            } else {
                throw Exception("Transaction not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If update fails, try to preserve existing data
            try {
                val currentTransactions = getTransactions()
                saveTransactions(currentTransactions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        val transactions = getTransactions().toMutableList()
        transactions.removeIf { it.id == transaction.id }
        saveTransactions(transactions)
    }

    fun getSelectedCurrency(): String {
        return try {
            sharedPreferences.getString(KEY_SELECTED_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
        } catch (e: Exception) {
            e.printStackTrace()
            DEFAULT_CURRENCY
        }
    }

    fun setSelectedCurrency(currency: String) {
        try {
            sharedPreferences.edit().putString(KEY_SELECTED_CURRENCY, currency).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCategories(): List<String> {
        return try {
            context.resources.getStringArray(R.array.transaction_categories).toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
} 