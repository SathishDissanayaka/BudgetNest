package com.example.imilipocket.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imilipocket.data.PreferenceManager

class SettingsViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String> = _currency

    fun initialize() {
        loadCurrency()
    }

    private fun loadCurrency() {
        _currency.value = preferenceManager.getSelectedCurrency()
    }

    fun updateCurrency(newCurrency: String) {
        preferenceManager.setSelectedCurrency(newCurrency)
        _currency.value = newCurrency
    }

    fun setSelectedCurrency(currency: String) {
        val currencyCode = currency.substring(0, 3)
        preferenceManager.setSelectedCurrency(currencyCode)
    }
} 