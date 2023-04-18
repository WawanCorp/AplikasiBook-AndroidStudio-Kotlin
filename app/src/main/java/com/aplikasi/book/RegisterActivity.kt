package com.aplikasi.book

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.aplikasi.book.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var  binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        txtRegisterListener()


        binding.btnRegister.setOnClickListener {
            register()
        }
    }

    private fun txtRegisterListener() {
            binding.gotoLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }


    private fun register() {
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()
        val confirPassword = binding.ConfirmPassword.text.toString()

        if ( email.isEmpty() || password.isEmpty() || confirPassword.isEmpty()) {

            binding.inputEmail.error = "email tidak boleh kosong"
            binding.inputPassword.error = "Konfirmasi password tidak boleh kosong"
            binding.ConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.error = "Email tidak valid"
        }
        if (password.length < 6) {
            binding.inputPassword.error = "Password minimal 6 karakter"
        }
        if (password != confirPassword) {
            binding.ConfirmPassword.error = "Password tidak sama"

        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Register Berhasil", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null)
            Intent(this@RegisterActivity, ProfileActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
    }
}


