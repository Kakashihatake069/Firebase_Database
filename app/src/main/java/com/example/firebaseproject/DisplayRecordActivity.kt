package com.example.firebaseproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.firebaseproject.databinding.ActivityDashboardBinding
import com.example.firebaseproject.databinding.ActivityDisplayRecordBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DisplayRecordActivity : AppCompatActivity() {
    lateinit var displayRecordBinding: ActivityDisplayRecordBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    var studentList = ArrayList<StudentModelClass>()
    lateinit var rcvdisplayrecords : RecyclerView
    lateinit var adapter: DisplayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayRecordBinding = ActivityDisplayRecordBinding.inflate(layoutInflater)
        setContentView(displayRecordBinding.root)

        initview()
    }

    private fun initview() {
        firebaseDatabase = FirebaseDatabase.getInstance()

        firebaseDatabase.reference.child("studentTb")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        var data = i.getValue(StudentModelClass::class.java)
                        Log.e(
                            "TAG",
                            "onDataChange: " + data?.name + " " + data?.course + " " + data?.address + data?.fees
                        )
                        data?.let { d -> studentList.add(d) }

//                        adapter = UserAdapter(list)
//                        var LayoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
//                        binding.rcvmain.layoutManager=LayoutManager
//                        binding.rcvmain.adapter=adapter

                        adapter = DisplayAdapter(studentList)
                        var LayoutManager = LinearLayoutManager(this@DisplayRecordActivity,LinearLayoutManager.VERTICAL,false)
                        displayRecordBinding.rcvdisplayrecords.layoutManager=LayoutManager
                        displayRecordBinding.rcvdisplayrecords.adapter=adapter

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}