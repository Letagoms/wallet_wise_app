// screens/LoginActivity.kt
package com.example.wallet_wise_app.screens

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.services.GamificationService

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper.getInstance(this)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)

        btnLogin.setOnClickListener {
            loginUser()
        }

        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val usernameOrEmail = etUsername.text.toString().trim()
        val password = etPassword.text.toString()

        if (usernameOrEmail.isEmpty()) {
            Toast.makeText(this, "Username or Email required", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password required", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            var user = dbHelper.getUserByUsername(usernameOrEmail)
            if (user == null) {
                user = dbHelper.getUserByEmail(usernameOrEmail)
            }

            val finalUser = user
            runOnUiThread {
                if (finalUser != null && finalUser.password == password) {
                    // Initialize gamification for user
                    val gamificationService = GamificationService(this)
                    gamificationService.initializeAchievements(finalUser.id)
                    gamificationService.initializeUserStats(finalUser.id)

                    // Record login and check for achievement
                    val unlockedAchievement = gamificationService.recordLogin(finalUser.id)

                    // Show popup if achievement unlocked
                    if (unlockedAchievement != null) {
                        showAchievementPopup(unlockedAchievement)
                    }

                    Toast.makeText(this, "Welcome ${finalUser.username}!", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, SetGoalsActivity::class.java)
                    intent.putExtra("USER_ID", finalUser.id)
                    intent.putExtra("USERNAME", finalUser.username)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid username/email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun showAchievementPopup(achievementName: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("🏆 Achievement Unlocked!")
            .setMessage("Congratulations! You've earned: $achievementName")
            .setPositiveButton("Awesome!") { _, _ -> }
            .create()
        dialog.show()
    }
}