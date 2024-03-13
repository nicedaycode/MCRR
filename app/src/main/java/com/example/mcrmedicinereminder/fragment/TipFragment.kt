package com.example.mcrmedicinereminder.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.databinding.FragmentTipsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [Tips.newInstance] factory method to
 * create an instance of this fragment.
 */
class TipFragment : Fragment() {
    private lateinit var binding : FragmentTipsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_tips, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}