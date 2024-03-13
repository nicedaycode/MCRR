package com.example.mcrmedicinereminder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.mcrmedicinereminder.activity.HomeActivity
import com.example.mcrmedicinereminder.activity.SignIn
import com.example.mcrmedicinereminder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
   private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()
        onBoardingFinished()


    }
    private fun onBoardingFinished(): Boolean{
        val sharedPref = application.getSharedPreferences("login", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("flag",false)
    }
}