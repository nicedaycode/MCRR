package com.example.mcrmedicinereminder.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.databinding.ActivitySignUpBinding
import com.example.mcrmedicinereminder.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: ActivitySignUpBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.signIn.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }


        binding.signUpBtn.setOnClickListener {
            val name = binding.nameEdt.text.toString()
            val email = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()
            val confirmPassword = binding.confirmPasswordEdt.text.toString()
            if (name.isEmpty()) binding.emailEdt.error = "Please enter name"
            if (email.isEmpty()) binding.emailEdt.error = "Please enter email"
            if (password.isEmpty()) binding.passwordEdt.error = "Please enter password"
            if (password != confirmPassword) binding.passwordEdt.error =
                "Please enter same password"
            if (isValidPassword(password)) {
                val progressDialog = ProgressDialog(this@SignUp)
                progressDialog.setTitle("Please Wait..")
                progressDialog.setMessage("Application is loading, please wait")
                progressDialog.show()
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            progressDialog.dismiss()
                            val userId = firebaseAuth.currentUser?.uid.toString()
                            database.reference.child("users")
                                .child(userId)
                                .setValue(User(userId, name, email, "null"))
                            // Shared Preference
                            val pref = getSharedPreferences("login", MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.putBoolean("flag", true)
                            editor.apply()
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.putExtra("User_id", userId)
                            intent.putExtra("User_name", name)
                            intent.putExtra("User_email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            } else {
                binding.passwordEdt.error = "Please enter valid password "
            }
        }

        // Google Sign Option
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSignUpBtn.setOnClickListener {
            signInGoogle()
        }
    }


    // Google SignUp Code
    private fun signInGoogle() {
        val signInIntent = this.googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val progressDialog = ProgressDialog(this@SignUp)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                progressDialog.dismiss()
                val userId = firebaseAuth.currentUser?.uid.toString()
                val name = account.displayName.toString()
                val email = account.email.toString()
                val imageURL = account.photoUrl.toString()
                Log.d("RAHUL", "$name \n $email \n $imageURL")
                database.reference.child("users")
                    .child(userId)
                    .setValue(User(userId, name, email, imageURL))
                // Shared Preference
                val pref = getSharedPreferences("login", MODE_PRIVATE)
                val editor = pref.edit()
                editor.putBoolean("flag", true)
                editor.apply()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Password Validation Code
    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.firstOrNull { it.isDigit() } == null) return false
        if (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) return false
        if (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) return false
        if (password.firstOrNull { !it.isLetterOrDigit() } == null) return false
        return true
    }


}