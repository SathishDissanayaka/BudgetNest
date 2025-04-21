package com.example.imilipocket.ui.dashboard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.imilipocket.R
import com.example.imilipocket.databinding.ActivityDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityDashboardBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setupNavigation()
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in onCreate: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun setupNavigation() {
        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView?.setupWithNavController(navController)
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in setupNavigation: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
} 