package com.example.mcrmedicinereminder.activity

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.databinding.ActivityHomeBinding
import com.example.mcrmedicinereminder.fragment.DashboardFragment
import com.example.mcrmedicinereminder.fragment.DonateFragment
import com.example.mcrmedicinereminder.fragment.ReminderFragment
import com.example.mcrmedicinereminder.fragment.TipFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var selectedTab = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        // set default fragment
        replaceFragment(ReminderFragment())

        binding.reminderLayout.setOnClickListener {

            if (selectedTab != 1) {

                // change fragment
                replaceFragment(ReminderFragment())

                // set layout and image visibility
                binding.sellLayout.background = null
                binding.tipLayout.background = null
                binding.dashboardLayout.background = null
                binding.reminderLayout.setBackgroundResource(R.drawable.nav_round_bg)
                binding.reminderImgSelected.visibility = View.VISIBLE
                binding.reminderImgDefault.visibility = View.GONE
                binding.dashboardImg.setBackgroundResource(R.drawable.dashboard_selected)
                binding.sellImg.setBackgroundResource(R.drawable.donate_default)
                binding.tipImg.setBackgroundResource(R.drawable.tip_selected)


                // set other nav item text visibility
                binding.reminderText.visibility = View.VISIBLE
                binding.sellText.visibility = View.GONE
                binding.tipText.visibility = View.GONE
                binding.dashboardText.visibility = View.GONE

                // set animation
                val scaleAnimation = ScaleAnimation(
                    0.8f,
                    1.0f,
                    1f,
                    1f,
                    Animation.RELATIVE_TO_SELF,
                    0.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.0f
                )
                scaleAnimation.duration = 200
                scaleAnimation.fillAfter = true
                binding.reminderLayout.startAnimation(scaleAnimation)

                selectedTab = 1
            }

        }

        binding.sellLayout.setOnClickListener {
            if (selectedTab != 2) {
                // change fragment
                replaceFragment(DonateFragment())

                // set layout and image visibility
                binding.reminderLayout.background = null
                binding.tipLayout.background = null
                binding.dashboardLayout.background = null
                binding.sellLayout.setBackgroundResource(R.drawable.nav_round_bg)
                binding.reminderImgDefault.visibility = View.VISIBLE
                binding.reminderImgSelected.visibility = View.GONE
                binding.dashboardImg.setBackgroundResource(R.drawable.dashboard_selected)
                binding.sellImg.setBackgroundResource(R.drawable.donate_selected)
                binding.tipImg.setBackgroundResource(R.drawable.tip_selected)

                // set other nav item text visibility
                binding.sellText.visibility = View.VISIBLE
                binding.reminderText.visibility = View.GONE
                binding.tipText.visibility = View.GONE
                binding.dashboardText.visibility = View.GONE

                // set animation
                val scaleAnimation = ScaleAnimation(
                    0.8f,
                    1.0f,
                    1f,
                    1f,
                    Animation.RELATIVE_TO_SELF,
                    1.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.0f
                )
                scaleAnimation.duration = 200
                scaleAnimation.fillAfter = true
                binding.sellLayout.startAnimation(scaleAnimation)

                selectedTab = 2
            }
        }

        binding.tipLayout.setOnClickListener {
            if (selectedTab != 3) {

                // change fragment
                replaceFragment(TipFragment())
                // set layout and image visibility
                binding.sellLayout.background = null
                binding.reminderText.background = null
                binding.dashboardLayout.background = null
                binding.tipLayout.setBackgroundResource(R.drawable.nav_round_bg)
                binding.dashboardImg.setBackgroundResource(R.drawable.dashboard_selected)
                binding.reminderImgDefault.visibility = View.VISIBLE
                binding.reminderImgSelected.visibility = View.GONE
                binding.sellImg.setBackgroundResource(R.drawable.donate_default)
                binding.tipImg.setBackgroundResource(R.drawable.tips)

                // set other nav item text visibility
                binding.sellText.visibility = View.GONE
                binding.tipText.visibility = View.VISIBLE
                binding.reminderText.visibility = View.GONE
                binding.dashboardText.visibility = View.GONE

                // set animation
                val scaleAnimation = ScaleAnimation(
                    0.8f,
                    1.0f,
                    1f,
                    1f,
                    Animation.RELATIVE_TO_SELF,
                    1.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.0f
                )
                scaleAnimation.duration = 200
                scaleAnimation.fillAfter = true
                binding.tipLayout.startAnimation(scaleAnimation)

                selectedTab = 3
            }
        }

        binding.dashboardLayout.setOnClickListener {
            if (selectedTab != 4) {
                // change fragment
                replaceFragment(DashboardFragment())
                // set layout and image visibility
                binding.sellLayout.background = null
                binding.tipLayout.background = null
                binding.reminderLayout.background = null
                binding.dashboardLayout.setBackgroundResource(R.drawable.nav_round_bg)
                binding.sellImg.setBackgroundResource(R.drawable.donate_default)
                binding.reminderImgDefault.visibility = View.VISIBLE
                binding.reminderImgSelected.visibility = View.GONE
                binding.dashboardImg.setBackgroundResource(R.drawable.dashboard)
                binding.tipImg.setBackgroundResource(R.drawable.tip_selected)

                // set other nav item text visibility
                binding.sellText.visibility = View.GONE
                binding.dashboardText.visibility = View.VISIBLE
                binding.tipText.visibility = View.GONE
                binding.reminderText.visibility = View.GONE

                // set animation
                val scaleAnimation = ScaleAnimation(
                    0.8f,
                    1.0f,
                    1f,
                    1f,
                    Animation.RELATIVE_TO_SELF,
                    1.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.0f
                )
                scaleAnimation.duration = 200
                scaleAnimation.fillAfter = true
                binding.dashboardLayout.startAnimation(scaleAnimation)

                selectedTab = 4
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}

