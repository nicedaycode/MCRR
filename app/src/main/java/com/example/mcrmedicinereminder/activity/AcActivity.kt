package com.example.mcrmedicinereminder.activity

import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.databinding.ActivityAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AcActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountBinding
    private var uri: Uri? = null
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storageRef: FirebaseStorage
    private lateinit var imageUrl: String
    var click: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storageRef = FirebaseStorage.getInstance()

        // Back Button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()

        database.reference.child("users").child(firebaseAuth.currentUser?.uid.toString())
            .get().addOnSuccessListener {
                if (it.exists()) {
                    progressDialog.dismiss()
                    val userId = it.child("userId").value
                    val name = it.child("name").value.toString()
                    val email = it.child("email").value.toString()
                    val phoneNumber = it.child("phoneNumber").value.toString()
                    val address = it.child("address").value.toString()
                    val imageURL = it.child("imageURL").value

                    binding.userNameTxt.text = name.toString()
                    binding.nameEdt.text = Editable.Factory.getInstance().newEditable(name)
                    binding.userEmailTxt.text = email.toString()
                    binding.emailEdt.text = Editable.Factory.getInstance().newEditable(email)
                    binding.phoneNoEdt.text =
                        Editable.Factory.getInstance().newEditable(phoneNumber)
                    binding.addressEdt.text = Editable.Factory.getInstance().newEditable(address)

                    if (imageURL != "null") {
                        Glide.with(this).load(imageURL).into(binding.profileImg)
                    }
                }
            }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(), ActivityResultCallback {
                binding.profileImg.setImageURI(it)
                if (it != null) {
                    uri = it
                }
            })
        binding.profileImg.setOnClickListener {
            click = true
            galleryImage.launch("image/*")
        }

        binding.updateBtn.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please Wait..")
            progressDialog.setMessage("Profile is update, please wait")
            progressDialog.show()
                val reference = storageRef.reference.child("Profile")
                    .child(firebaseAuth.currentUser?.uid.toString())
                uri?.let { it1 ->
                    reference.putFile(it1).addOnCompleteListener {
                        progressDialog.dismiss()
                        if (it.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener {
                                imageUrl = it.toString()
                                updateProfile(progressDialog)
                            }
                        }
                    }
                }
        }
    }

    private fun updateProfile(progressDialog: ProgressDialog) {
//        progressDialog.dismiss()
        val userId = firebaseAuth.currentUser?.uid.toString()
        val name = binding.nameEdt.text.toString()
        val image = imageUrl
        val email = binding.emailEdt.text.toString()
        val phoneNumber = binding.phoneNoEdt.text.toString()
        val address = binding.addressEdt.text.toString()
//        Log.d("ACCOUNT", "$userId \n $name \n $image \n $email \n $phoneNumber \n $address ")

        val user = mapOf<String, String>(
            "userId" to userId,
            "name" to name,
            "imageURL" to image,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "address" to address
        )
        database.reference.child("users").child(userId)
            .updateChildren(user).addOnSuccessListener {
                Toast.makeText(this, "Profile is Updated", Toast.LENGTH_SHORT).show()
            }
    }

}