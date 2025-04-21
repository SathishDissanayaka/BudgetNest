package com.example.imilipocket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: String,
    val date: Date,
    val type: TransactionType
) : Parcelable {
    companion object {
        const val FOOD = "Food"
        const val TRANSPORT = "Transport"
        const val BILLS = "Bills"
        const val ENTERTAINMENT = "Entertainment"
        const val OTHER = "Other"
    }
}

enum class TransactionType {
    INCOME, EXPENSE
} 