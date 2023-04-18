package com.aplikasi.book



import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aplikasi.book.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()



        txtRegisterListener()

        binding.btnLogin.setOnClickListener {
            login()

        }

    }

    private fun txtRegisterListener() {
        binding.gotoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }


    private fun login() {
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()


        if (email.isEmpty() || password.isEmpty()) {
            binding.inputEmail.error = "Email tidak boleh kosong"
            binding.inputPassword.error = "Password tidak boleh kosong"

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputPassword.error = "Email tidak valid"
        }
        if (password.length < 6) {
            binding.inputPassword.error = "Password minimal 6 karakter"

        } else {
            auth.signInWithEmailAndPassword (email, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Selamat Datang", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

}


