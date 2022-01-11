package com.example.chatapp.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btnSend_forgotPassword.setOnClickListener {
            forgotPassword()
        }
    }

    private fun forgotPassword() {
        val email = email_editText_forgotPassword.text.toString()

        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent", Toast.LENGTH_LONG).show()
                    }
                else {
                    Toast.makeText(this, "Wrong", Toast.LENGTH_LONG).show()
                }
            }
    }
}