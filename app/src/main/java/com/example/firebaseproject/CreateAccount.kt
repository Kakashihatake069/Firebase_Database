package com.example.firebaseproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseproject.databinding.ActivityCreateAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CreateAccount : AppCompatActivity() {
    lateinit var signinbinding: ActivityCreateAccountBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signinbinding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(signinbinding.root)

        initview()
    }

    private fun initview() {
        auth = Firebase.auth
        val sharedPreferences = getSharedPreferences("MysharedPreference", MODE_PRIVATE)
        signinbinding.btncreateacc.setOnClickListener {
            var email = signinbinding.edtemail.text.toString()
            var password = signinbinding.edtpassword.text.toString()
            var username = signinbinding.edtusername.text.toString()
            var phone = signinbinding.edtphone.text.toString()
            if (username.isEmpty()) {
                Toast.makeText(this, "username is Empty", Toast.LENGTH_LONG).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Email ID is Empty", Toast.LENGTH_LONG).show()
            } else if (phone.isEmpty()) {
                    Toast.makeText(this, "Phone number is Empty", Toast.LENGTH_LONG).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Password is Empty", Toast.LENGTH_LONG).show()
            }

            else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Created Account successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
            var i = Intent(this,DashboardActivity::class.java)
            val myedit: SharedPreferences.Editor = sharedPreferences.edit()

            myedit.putBoolean("isLogin", true)
            myedit.putString("username",username)
            myedit.putString("phone",phone)
            myedit.commit()

            startActivity(i)
        }

        signinbinding.btnCback.setOnClickListener {
            var goback = Intent(this,MainActivity::class.java)
            startActivity(goback)
        }
    }
}