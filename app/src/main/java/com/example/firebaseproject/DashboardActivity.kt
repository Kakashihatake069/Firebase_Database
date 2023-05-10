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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class DashboardActivity : AppCompatActivity() {
    lateinit var dashboardBinding: ActivityDashboardBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    private var filePath: Uri? = null
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private val PICK_IMAGE_REQUEST = 22
    lateinit var googleSignInClient: GoogleSignInClient
    var image = " "
    lateinit var progressDialog: ProgressDialog
    var studentList = ArrayList<StudentModelClass>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding.root)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
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


            var name = dashboardBinding.edtRname.text.toString()
            var course = dashboardBinding.edtRcourse.text.toString()
            var address =  dashboardBinding.edtRadd.text.toString()
            var fees =  dashboardBinding.edtRfees.text.toString()

            if (name.isEmpty()){
                Toast.makeText(this, "please enter name", Toast.LENGTH_SHORT).show()
            }
            else if (course.isEmpty()){
                Toast.makeText(this, "please enter your course", Toast.LENGTH_SHORT).show()
            }
            else if (address.isEmpty()){
                Toast.makeText(this, "please enter your address", Toast.LENGTH_SHORT).show()
            }
            else if (fees.isEmpty()){
                Toast.makeText(this, "please enter fees", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val key = firebaseDatabase.reference.child("studentTb").push().key ?: ""
                val data = StudentModelClass(key,name, course, address, fees, image)
                firebaseDatabase.reference.child("studentTb").child(key).setValue(data)
                    .addOnCompleteListener {


                            dashboardBinding.edtRname.text.clear()
                            dashboardBinding.edtRcourse.text.clear()
                            dashboardBinding.edtRadd.text.clear()
                            dashboardBinding.edtRfees.text.clear()
                            dashboardBinding.edtRid.text.clear()
                            image

                    }.addOnFailureListener {
                        Toast.makeText(this, "Invalid details", Toast.LENGTH_SHORT).show()
                        Log.e("TAG", "initviewgdgg: "+it.message )
                    }
            }
        }

        //display all details
        dashboardBinding.btndisplay.setOnClickListener {
            var displayrecord = Intent(this@DashboardActivity, DisplayRecordActivity::class.java)
            startActivity(displayrecord)
        }

        //pick image
        dashboardBinding.btnpick.setOnClickListener {
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

        //upload photo
        dashboardBinding.btnupload.setOnClickListener {
            uploadimage()
        }

         // google logout
        // Initialize sign in client
        googleSignInClient =
            GoogleSignIn.getClient(this@DashboardActivity, GoogleSignInOptions.DEFAULT_SIGN_IN)
        dashboardBinding.txtlogoutGoogle.setOnClickListener {
            // Sign out from google
            googleSignInClient.signOut().addOnCompleteListener { task ->
                // Check condition
                if (task.isSuccessful) {
                    // When task is successful sign out from firebase
                    auth.signOut()
                    // Display Toast
                    Toast.makeText(applicationContext, "Logout successful", Toast.LENGTH_SHORT)
                        .show()
                    // Finish activity
                    finish()
                }
            }
        }
    }
    // Override onActivityResult method
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
            var ref = storageReference.child("images/" + UUID.randomUUID().toString())


            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath!!)
                .addOnCompleteListener {

                    ref.downloadUrl.addOnSuccessListener {
                        image=it.toString()
                    }

                }
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