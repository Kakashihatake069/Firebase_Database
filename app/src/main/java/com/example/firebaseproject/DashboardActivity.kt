package com.example.firebaseproject

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseproject.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.*


class DashboardActivity : AppCompatActivity() {
    lateinit var dashboardBinding: ActivityDashboardBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var filePath: Uri? = null
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private val PICK_IMAGE_REQUEST = 22

    lateinit var progressDialog: ProgressDialog
    var studentList = ArrayList<StudentModelClass>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding.root)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()

        initview()
//        pickimage()
        uploadimage()
    }


    private fun initview() {
        firebaseDatabase = FirebaseDatabase.getInstance()

        dashboardBinding.imgbackbtn.setOnClickListener {
            var backcreateacc = Intent(this, CreateAccount::class.java)
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

        dashboardBinding.txtuser.text = sharedPreferences.getString("username", " ")


        dashboardBinding.btnsubmit.setOnClickListener {

            val key = firebaseDatabase.reference.child("studentTb").push().key ?: ""
            val data = StudentModelClass(
                key,
                dashboardBinding.edtRname.text.toString(),
                dashboardBinding.edtRcourse.text.toString(),
                dashboardBinding.edtRadd.text.toString(),
                dashboardBinding.edtRfees.text.toString()

            )
            firebaseDatabase.reference.child("studentTb").child(key).setValue(data)
                .addOnCompleteListener {

                    if (it.isSuccessful) {
                        dashboardBinding.edtRname.text.clear()
                        dashboardBinding.edtRcourse.text.clear()
                        dashboardBinding.edtRadd.text.clear()
                        dashboardBinding.edtRfees.text.clear()
                        dashboardBinding.edtRid.text.clear()
                    }


                }.addOnFailureListener {
                    Toast.makeText(this, "Invalid details", Toast.LENGTH_SHORT).show()
                }


            val getImage: DatabaseReference = firebaseDatabase.reference.child("studentTb")
            getImage.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(
                        dataSnapshot: DataSnapshot) {
                        // getting a DataSnapshot for the
                        // location at the specified relative
                        // path and getting in the link variable
//                        val link = dataSnapshot.getValue(String::class.java)
                        val map = dataSnapshot.value as Map<*, *>?
                        Log.d("TAG", "Value is: $map")
                        // loading that data into rImage
                        // variable which is ImageView
//                        Picasso.get().load(map).into(dashboardBinding.imgimagespace)
                    }

                    // this will called when any problem
                    // occurs in getting data
                    override fun onCancelled(
                        databaseError: DatabaseError
                    ) {
                        // we are showing that error message in
                        // toast
                        Toast
                            .makeText(
                                this@DashboardActivity,
                                "Error Loading Image",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                })

            Toast.makeText(
                    this@DashboardActivity,
                    "Details submitted",
                    Toast.LENGTH_SHORT).show()
        }

        dashboardBinding.btndisplay.setOnClickListener {
            var displayrecord = Intent(this@DashboardActivity, DisplayRecordActivity::class.java)
            startActivity(displayrecord)
        }

        dashboardBinding.btnpick.setOnClickListener {
//            pickimage()
            // Defining Implicit Intent to mobile gallery
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    "Select Image from here..."
                ), PICK_IMAGE_REQUEST
            )
        }
        dashboardBinding.btnupload.setOnClickListener {
            uploadimage()
        }
    }


//    data class details(
//        var name: String,
//        var course: String,
//        var address: String,
//        var fees: String,
//        var id: String
//    )


//    private fun pickimage() {
//        // Defining Implicit Intent to mobile gallery
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(
//            Intent.createChooser(
//                intent,
//                "Select Image from here..."
//            ), PICK_IMAGE_REQUEST
//        )
//    }

    // Override onActivityResult method
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {

            // Get the Uri of data
            filePath = data.data
            try {

                // Setting image on image view using Bitmap
                val bitmap = MediaStore.Images.Media
                    .getBitmap(
                        contentResolver,
                        filePath
                    )
                dashboardBinding.imgimagespace.setImageBitmap(bitmap)
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }


    private fun uploadimage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref = storageReference
                .child(
                    "images/"
                            + UUID.randomUUID().toString()
                )

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath!!)
                .addOnSuccessListener { // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            this@DashboardActivity,
                            "Image Uploaded!!",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            this@DashboardActivity,
                            "Failed " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->

                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress = (100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded "
                                + progress.toInt() + "%"
                    )
                }
        }
    }
}