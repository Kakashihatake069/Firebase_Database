package com.example.firebaseproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseproject.databinding.ActivityDashboardBinding
import com.google.firebase.database.FirebaseDatabase

class DashboardActivity : AppCompatActivity() {
    lateinit var dashboardBinding: ActivityDashboardBinding
    lateinit var firebaseDatabase : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding.root)

        initview()
    }

    private fun initview() {
        firebaseDatabase = FirebaseDatabase.getInstance()

        dashboardBinding.imgbackbtn.setOnClickListener {
            var backcreateacc = Intent(this,CreateAccount::class.java)
            startActivity(backcreateacc)
        }

        dashboardBinding.btnlogout.setOnClickListener {
            var sharedPreferences = getSharedPreferences("MysharedPreference", MODE_PRIVATE)
            var myEdit: SharedPreferences.Editor = sharedPreferences.edit()
            myEdit.remove("isLogin")
            myEdit.commit()
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }


        var sharedPreferences = getSharedPreferences("MysharedPreference", MODE_PRIVATE)

        dashboardBinding.txtuser.text = sharedPreferences.getString("username"," ")


        dashboardBinding.btnsubmit.setOnClickListener {

            var name = dashboardBinding.edtRname.text.toString()
            var course = dashboardBinding.edtRcourse.text.toString()
            var address = dashboardBinding.edtRadd.text.toString()
            var fees = dashboardBinding.edtRfees.text.toString()




            val key = firebaseDatabase.reference.child("studentTb").push().key ?: ""
            val data = details(name,course,address,fees,key)
            firebaseDatabase.reference.child("studentTb").child(key).setValue(data).addOnCompleteListener {

                if (it.isSuccessful){
                    dashboardBinding.edtRname.text.clear()
                    dashboardBinding.edtRcourse.text.clear()
                    dashboardBinding.edtRadd.text.clear()
                    dashboardBinding.edtRfees.text.clear()
                    dashboardBinding.edtRid.text.clear()
                }


            }.addOnFailureListener {
                Toast.makeText(this, "Invalid details", Toast.LENGTH_SHORT).show()
            }
        }


    }

    data class details(
        var name: String,
        var course: String,
        var address: String,
        var fees: String,
        var id: String
    )


}