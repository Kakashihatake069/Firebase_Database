package com.example.firebaseproject

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseproject.databinding.ActivityCreateAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class CreateAccount : AppCompatActivity() {
    lateinit var signinbinding: ActivityCreateAccountBinding
    private lateinit var auth: FirebaseAuth
    private var filePath: Uri? = null
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private val PICK_IMAGE_REQUEST = 22
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signinbinding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(signinbinding.root)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()
        initview()
//        pickimage()
//        uploadimage()
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
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Created Account successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

            }
            var i = Intent(this, DashboardActivity::class.java)
            val myedit: SharedPreferences.Editor = sharedPreferences.edit()

            myedit.putBoolean("isLogin", true)
            myedit.putString("username", username)
            myedit.putString("phone", phone)
            myedit.commit()

            startActivity(i)
        }

        signinbinding.btnCback.setOnClickListener {
            var goback = Intent(this, MainActivity::class.java)
            startActivity(goback)
        }

//        signinbinding.btnpick.setOnClickListener {
//            pickimage()
//        }
//        signinbinding.btnupload.setOnClickListener {
//            uploadimage()
//        }
    }

//    private fun pickimage() {
//
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
//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?
//    ) {
//        super.onActivityResult(
//            requestCode,
//            resultCode,
//            data
//        )
//
//        // checking request code and result code
//        // if request code is PICK_IMAGE_REQUEST and
//        // resultCode is RESULT_OK
//        // then set image in the image view
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
//
//            // Get the Uri of data
//            filePath = data.data
//            try {
//
//                // Setting image on image view using Bitmap
//                val bitmap = MediaStore.Images.Media
//                    .getBitmap(
//                        contentResolver,
//                        filePath
//                    )
//                signinbinding.imgimagespace.setImageBitmap(bitmap)
//            } catch (e: IOException) {
//                // Log the exception
//                e.printStackTrace()
//            }
//        }
//    }

//    private fun uploadimage() {
//        if (filePath != null) {
//
//            // Code for showing progressDialog while uploading
//            val progressDialog = ProgressDialog(this)
//            progressDialog.setTitle("Uploading...")
//            progressDialog.show()
//
//            // Defining the child of storageReference
//            val ref = storageReference
//                .child(
//                    "images/"
//                            + UUID.randomUUID().toString()
//                )
//
//            // adding listeners on upload
//            // or failure of image
//            ref.putFile(filePath!!)
//                .addOnSuccessListener { // Image uploaded successfully
//                    // Dismiss dialog
//                    progressDialog.dismiss()
//                    Toast
//                        .makeText(
//                            this@CreateAccount,
//                            "Image Uploaded!!",
//                            Toast.LENGTH_SHORT
//                        )
//                        .show()
//                }
//                .addOnFailureListener { e -> // Error, Image not uploaded
//                    progressDialog.dismiss()
//                    Toast
//                        .makeText(
//                            this@CreateAccount,
//                            "Failed " + e.message,
//                            Toast.LENGTH_SHORT
//                        )
//                        .show()
//                }
//                .addOnProgressListener { taskSnapshot ->
//
//                    // Progress Listener for loading
//                    // percentage on the dialog box
//                    val progress = (100.0
//                            * taskSnapshot.bytesTransferred
//                            / taskSnapshot.totalByteCount)
//                    progressDialog.setMessage(
//                        "Uploaded "
//                                + progress.toInt() + "%"
//                    )
//                }
//        }
//    }
}
