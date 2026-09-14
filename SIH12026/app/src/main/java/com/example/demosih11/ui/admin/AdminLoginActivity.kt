package com.example.demosih11.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.demosih11.MainActivity
import com.example.demosih11.databinding.ActivityAdminLoginBinding

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdminLoginSubmit.setOnClickListener {
            val user = binding.editAdminUser.text.toString().trim()
            val pass = binding.editAdminPass.text.toString()

            if (user == "admin2026sih" && pass == "sih2026") {
                Toast.makeText(this, "Root Operator Access Granted", Toast.LENGTH_SHORT).show()
                
                // Route safely into the main app ecosystem configuration targeting admin access profile variables exclusively
                val resultIntent = Intent().apply {
                    putExtra("LAUNCH_MODE", "ADMIN_PANEL")
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Security Violation: Unauthorized Admin Entry Attempt", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnAdminGoBack.setOnClickListener {
            // Terminate admin login activity and return to the main entry login fragment
            finish()
        }
    }
}