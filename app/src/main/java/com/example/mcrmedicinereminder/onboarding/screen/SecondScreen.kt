package com.example.mcrmedicinereminder.onboarding.screen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.databinding.FragmentSecondScreenBinding

class SecondScreen : Fragment() {
    private lateinit var binding : FragmentSecondScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_second_screen, container, false)

        binding.skipBtn.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_signIn)
            onBoardingFinished()
        }

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        binding.nextBtn.setOnClickListener {
            viewPager?.currentItem = 2
        }

        return binding.root

    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("flag",true)
        editor.apply()
    }
}