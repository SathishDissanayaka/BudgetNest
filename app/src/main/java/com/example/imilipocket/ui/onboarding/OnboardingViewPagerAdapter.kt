package com.example.imilipocket.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imilipocket.R
import com.example.imilipocket.databinding.ItemOnboardingBinding

class OnboardingViewPagerAdapter : RecyclerView.Adapter<OnboardingViewPagerAdapter.OnboardingViewHolder>() {

    private val onboardingItems = listOf(
        OnboardingItem(
            "Welcome to ImiliPocket",
            "Track your expenses and manage your budget easily",
            R.drawable.ic_wallet
        ),
        OnboardingItem(
            "Smart Budgeting",
            "Set budgets and get insights into your spending habits",
            R.drawable.ic_budget
        ),
        OnboardingItem(
            "Secure & Private",
            "Your financial data is safe with us",
            R.drawable.ic_security
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount() = onboardingItems.size

    class OnboardingViewHolder(private val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OnboardingItem) {
            binding.textTitle.text = item.title
            binding.textDescription.text = item.description
            binding.imageOnboarding.setImageResource(item.imageResId)
        }
    }

    data class OnboardingItem(
        val title: String,
        val description: String,
        val imageResId: Int
    )
} 