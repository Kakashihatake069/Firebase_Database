package com.example.firebaseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseproject.databinding.ActivityDisplayRecordBinding
import com.example.firebaseproject.databinding.ActivityEditRegistrationBinding
import com.google.firebase.database.FirebaseDatabase

class EditRegistrationActivity : AppCompatActivity() {
    lateinit var editBinding: ActivityEditRegistrationBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var image: String
    var studentList = ArrayList<StudentModelClass>()
    lateinit var adapter: DisplayAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editBinding = ActivityEditRegistrationBinding.inflate(layoutInflater)
        setContentView(editBinding.root)

        initview()

    }

    private fun initview() {
        editBinding.txtnewname.setText(intent.getStringExtra("name"))
        editBinding.txtnewcourse.setText(intent.getStringExtra("course"))
        editBinding.txtnewaddress.setText(intent.getStringExtra("address"))
        editBinding.txtnewfees.setText(intent.getStringExtra("fees"))
        var id = intent.getStringExtra("id").toString()
        firebaseDatabase= FirebaseDatabase.getInstance()

        editBinding.btnEdit.setOnClickListener {

            var data = StudentModelClass(
                id,
                editBinding.txtnewname.text.toString(),
                editBinding.txtnewcourse.text.toString(),
                editBinding.txtnewaddress.text.toString(),
                editBinding.txtnewfees.text.toString(),
                image

            )
            firebaseDatabase.reference.child("studentTb").child(id).setValue(data).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Record Updated Successfully", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Log.e("TAG", "initview: " +it.message )
                Toast.makeText(this, "something went wroung", Toast.LENGTH_SHORT).show()
            }
            var editdone = Intent(this,DisplayRecordActivity::class.java)
            startActivity(editdone)
        }
    }
}