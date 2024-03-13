package com.example.mcrmedicinereminder.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mcrmedicinereminder.*
import com.example.mcrmedicinereminder.activity.AcActivity
import com.example.mcrmedicinereminder.activity.RestockActivity
import com.example.mcrmedicinereminder.activity.SignIn
import com.example.mcrmedicinereminder.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DashboardFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var storageref: FirebaseStorage
    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storageref = FirebaseStorage.getInstance()
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please Wait..")
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()

        database.reference.child("users").child(firebaseAuth.currentUser?.uid.toString())
            .get().addOnSuccessListener {
                if (it.exists()) {
                    progressDialog.dismiss()
                    val name = it.child("name").value
                    val imageURL = it.child("imageURL").value
                    binding.userNameTxt.text = "Hello, $name!"
                    if (imageURL != "null") {
                        Glide.with(requireContext()).load(imageURL).into(binding.profileImg)
                    }
                }
            }


        binding.logout.setOnClickListener {
            firebaseAuth.signOut()
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Log out ?")
            builder.setMessage("Are you sure you want to Logout ?")
            builder.apply {
                setPositiveButton("YES", DialogInterface.OnClickListener { dialog, id ->
                    // Shared Preference
                    val pref = builder.context.getSharedPreferences(
                        "login",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    val editor = pref?.edit()
                    editor?.putBoolean("flag", false)
                    editor?.apply()
                    startActivity(Intent(builder.context, SignIn::class.java))
                    activity?.finish()

                })
            }
                .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
            builder.show()
        }

//        binding.accountTxt.setOnClickListener {
//            val intent = Intent(it.context, AcActivity::class.java)
//            startActivity(intent)
//        }

        binding.restoreMedicine.setOnClickListener {
            startActivity(Intent(it.context, RestockActivity::class.java))
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(), ActivityResultCallback {
                binding.profileImg.setImageURI(it)
                if (it != null) {
                    this.uri = it
                }
            })
        binding.profileImg.setOnClickListener {
            galleryImage.launch("image/*")
            updateProfile()

        }
    }

    private fun updateProfile() {
        val reference = storageref.reference.child("Profile")
            .child(firebaseAuth.currentUser?.uid.toString())
        uri?.let {
            reference.putFile(it).addOnCompleteListener{
                if (it.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                        val user = mapOf<String, String>("imageURL" to imageUrl)
                        database.reference.child("users").child(firebaseAuth.currentUser?.uid.toString())
                            .updateChildren(user).addOnSuccessListener {
                                Toast.makeText(requireContext(), "Profile is Updated", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }
    }

}