package com.example.demosih11

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.demosih11.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        // Handle direct exclusive secure launch routing targeting admin profile views
        val launchMode = intent.getStringExtra("LAUNCH_MODE")

        if (launchMode == "ADMIN_PANEL") {
            navController.navigate(R.id.adminPanelFragment)
        }
        // Listen for navigation destination changes to manage UI visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.adminPanelFragment, 
                R.id.recyclerDashboardFragment, R.id.recyclerProfileFragment, R.id.recyclerChatFragment -> {
                    // Hide the bottom navigation tabs but keep the language shortcut bar visible
                    binding.bottomNav.visibility = View.GONE
                    binding.layoutBottomBar.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.layoutBottomBar.visibility = View.VISIBLE
                }
            }
        }

        // Set an active click listener on the bottom layout strip shortcut to re-trigger the language pop-up options explicitly
        binding.textChangeLangShortcut.setOnClickListener {
            showLanguageSelectionDialog()
        }

        // Show language selection pop-up upon opening the application
        if (savedInstanceState == null && launchMode != "ADMIN_PANEL") {
            showLanguageSelectionDialog()
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "हिंदी (Hindi)", "मराठी (Marathi)")
        AlertDialog.Builder(this)
            .setTitle("Select Preferred Language / भाषा चुनें")
            .setCancelable(false)
            .setItems(languages) { _, which ->
                val localeCode = when (which) {
                    1 -> "hi"
                    2 -> "mr"
                    else -> "en"
                }
                setAppLocale(localeCode)
            }
            .show()
    }

    private fun setAppLocale(localeCode: String) {
        val locale = Locale(localeCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        
        // Recreate activity to apply chosen language translations across all views
        recreate()
    }
}