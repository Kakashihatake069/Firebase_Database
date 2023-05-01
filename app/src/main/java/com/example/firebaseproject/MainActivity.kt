package com.example.firebaseproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebaseproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth

        initview()
    }

    private fun initview() {
        auth = Firebase.auth
        sharedPreferences = getSharedPreferences("MysharedPreference", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLogin", false) == true) {

            var i = Intent(this, DashboardActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnlogin.setOnClickListener {
            var email = binding.edtusername.text.toString()
            var password = binding.edtpassword.text.toString()


            if (email.isEmpty()) {
                Toast.makeText(this, "Enter Your Email ID", Toast.LENGTH_LONG).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Password is Empty", Toast.LENGTH_LONG).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Created Account successfully", Toast.LENGTH_SHORT)
                            .show()

                        var sharedPreferences = getSharedPreferences("MysharedPreference", MODE_PRIVATE)
                        val myedit: SharedPreferences.Editor = sharedPreferences.edit()

                        myedit.putBoolean("isLogin", true)
                        myedit.putString("email",email)
                        myedit.putString("password",password)
                        myedit.commit()
                        var i = Intent(this,DashboardActivity::class.java)
                        startActivity(i)

                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnnewaccount.setOnClickListener {
            var acc = Intent(this,CreateAccount::class.java)
            startActivity(acc)
        }
    }
}


