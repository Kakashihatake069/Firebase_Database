package com.example.firebaseproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseproject.databinding.ActivityMainBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var callbackManager : CallbackManager
    private val EMAIL = "email"
    var saveLogin: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        var sharedPreferences = getSharedPreferences("MysharedPreference", MODE_PRIVATE)
        val myedit: SharedPreferences.Editor = sharedPreferences.edit()

        saveLogin = sharedPreferences.getBoolean("isLogin", false);
        if (saveLogin == true) {
            binding.edtusername.setText(sharedPreferences.getString("username", ""));
            binding.edtusername.setText(sharedPreferences.getString("password", ""));
            binding.saveLoginCheckBox.setChecked(true);
        }

//        try {
//            val info = packageManager.getPackageInfo(
//                "com.facebook.samples.hellofacebook",
//                PackageManager.GET_SIGNATURES
//            )
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.e("TAG", "onCreate: "+md )
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//        } catch (e: NoSuchAlgorithmException) {
//        }
        // Initialize Firebase Auth

        initview()
    }

    private fun initview() {
//        FacebookSdk.sdkInitialize(this)
//        println("Facebook hash key: ${FacebookSdk.getApplicationSignature(this)}")
        auth = Firebase.auth
        sharedPreferences = getSharedPreferences("MysharedPreference", MODE_PRIVATE)


        // Initialize sign in options the client-id is copied form google-services.json file
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("90995660761-efsciv5db3takhr8c34ijqoq9nt6p4mh.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        // google login
        binding.btngooglesignin.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            // Start activity for result
            startActivityForResult(intent, 100)
        }

//        if (sharedPreferences.getBoolean("isLogin", false) == true) {
//
//            var i = Intent(this, DashboardActivity::class.java)
//            startActivity(i)
//            finish()
//        }

        // if already have an account
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
                        myedit.apply()
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

        binding.loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        callbackManager = CallbackManager.Factory.create();


        binding.loginButton.registerCallback(callbackManager,object: FacebookCallback<LoginResult>{
            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {

            }

            override fun onSuccess(result: LoginResult) {

                var request = GraphRequest.newMeRequest(result.accessToken,object : GraphRequest.GraphJSONObjectCallback{
                    override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
                        try {
                            var email = obj?.getString(EMAIL)


                            Log.e("TAG", "onCompleted: "  + email +" "+obj)

                        } catch ( e : JSONException){

                        }

                    }

                })

                val parameter = Bundle()
                parameter.putString("fields","name,course,address,fees,id")
                request.parameters=parameter
                request.executeAsync()

                val credential: AuthCredential = FacebookAuthProvider.getCredential(result.accessToken.token)
                auth = FirebaseAuth.getInstance()
                Log.e("TAG", "onSuccess: " + result.accessToken.token )
                // Check credential
                auth.signInWithCredential(credential)
                    .addOnCompleteListener{ task ->
                            // Check condition
                            if (task.isSuccessful) {
                                // When task is successful redirect to profile activity display Toast
                                startActivity(
                                    Intent(
                                        this@MainActivity,DashboardActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                                Toast.makeText(this@MainActivity, "Firebase authentication on Facebook is successful", Toast.LENGTH_SHORT).show()
                            } else {
                                // When task is unsuccessful display Toast

                                Toast.makeText(this@MainActivity, "Authentication Failed :", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        })
    }

    private fun doSomethingElse() {
        var i = Intent(this,DashboardActivity::class.java)
        startActivity(i)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === 100) {
            // When request code is equal to 100 initialize task
            val signInAccountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful initialize string
                val s = "Google sign in successful"
                // Display Toast
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    val googleSignInAccount: GoogleSignInAccount =
                        signInAccountTask.getResult(ApiException::class.java)
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
                        // Check credential
                        auth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this,
                                OnCompleteListener<AuthResult?> { task ->
                                    // Check condition
                                    if (task.isSuccessful) {
                                        // When task is successful redirect to profile activity display Toast
                                        startActivity(
                                            Intent(
                                                this@MainActivity,DashboardActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        Toast.makeText(this, "Firebase authentication successful", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // When task is unsuccessful display Toast
                                        Toast.makeText(this, "Authentication Failed :", Toast.LENGTH_SHORT).show()
                                    }
                                })
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }
}


