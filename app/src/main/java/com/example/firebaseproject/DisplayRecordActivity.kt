package com.example.firebaseproject

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.firebaseproject.databinding.ActivityDashboardBinding
import com.example.firebaseproject.databinding.ActivityDisplayRecordBinding
import com.example.firebaseproject.databinding.DeleteDialogboxBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DisplayRecordActivity : AppCompatActivity() {
    lateinit var displayRecordBinding: ActivityDisplayRecordBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    var studentList = ArrayList<StudentModelClass>()
    lateinit var rcvdisplayrecords: RecyclerView
    lateinit var adapter: DisplayAdapter
//    var imagelist = ArrayList<ImageModelClass>()
    var id = " "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayRecordBinding = ActivityDisplayRecordBinding.inflate(layoutInflater)
        setContentView(displayRecordBinding.root)

        initview()
        setadapter()
    }


    private fun initview() {
        firebaseDatabase = FirebaseDatabase.getInstance()

        firebaseDatabase.reference.child("studentTb")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    studentList.clear()

                    for (i in snapshot.children) {
                        var data = i.getValue(StudentModelClass::class.java)
                        Log.e(
                            "TAG",
                            "onDataChange:" + data?.name + " " + data?.course + " " + data?.address + " " +data?.fees + "  "+ data?.image
                        )
                        data?.let { d -> studentList.add(d) }
//
//                        adapter = UserAdapter(list)
//                        var LayoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
//                        binding.rcvmain.layoutManager=LayoutManager
//                        binding.rcvmain.adapter=adapter

                    }
                    adapter.updatelist(studentList)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun setadapter() {

        adapter = DisplayAdapter(this,{
            val editintent =
                Intent(this@DisplayRecordActivity, EditRegistrationActivity::class.java)
            editintent.putExtra("name", it.name)
            editintent.putExtra("course", it.course)
            editintent.putExtra("address", it.address)
            editintent.putExtra("fees", it.fees)
            editintent.putExtra("id", it.id)
            editintent.putExtra("image", it.image)
            startActivity(editintent)

        }, {
            id = it
            val dialog = Dialog(this)
            val dialogBinding: DeleteDialogboxBinding = DeleteDialogboxBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogBinding.btndeletecancel.setOnClickListener {
                Toast.makeText(this, "Your Transaction has been Canceled", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
            dialogBinding.btndelete.setOnClickListener {
                deleterecord()
                Toast.makeText(this, "Your Transaction has been Deleted", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            dialog.show()
        })

        var LayoutManager =
            LinearLayoutManager(this@DisplayRecordActivity, LinearLayoutManager.VERTICAL, false)
        displayRecordBinding.rcvdisplayrecords.layoutManager = LayoutManager
        displayRecordBinding.rcvdisplayrecords.adapter = adapter
    }


    private fun deleterecord() {
        firebaseDatabase.reference.child("studentTb").child(id).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Record Deleted successfully", Toast.LENGTH_SHORT).show()
                    Log.e("TAG", "deleterecordgkj: " +id)
                }
            }.addOnFailureListener {
            Log.e("TAG", "deleterecord: " + it.message)
        }
    }

}