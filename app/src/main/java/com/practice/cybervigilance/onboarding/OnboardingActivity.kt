package com.practice.cybervigilance.onboarding

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.ActivityOnboardingBinding
import com.practice.cybervigilance.login.LoginActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var adapter:OnboardingViewPagerAdapter
    private lateinit var binding:ActivityOnboardingBinding

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var list=ArrayList<Onboarding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_onboarding)

        preferences=getSharedPreferences("LOGIN_INFO", MODE_PRIVATE)
        editor=preferences.edit()

        supportActionBar!!.hide()

        list.add(Onboarding("Read news and laws",R.drawable.logo))
        list.add(Onboarding("Get notified about latest cyber crimes",R.drawable.logo))
        list.add(Onboarding("VPN, Password manager and many more handy tools",R.drawable.logo))

        adapter= OnboardingViewPagerAdapter(this,list)
        binding.viewPager.adapter=adapter
        pageChangeListener()
        binding.tabLayout.setupWithViewPager(binding.viewPager, true);

        binding.buttonNext.setOnClickListener { onClickNext() }
        binding.buttonSkip.setOnClickListener { onClickNext() }

    }

    private fun onClickNext() {
        editor.putBoolean("isOnboardingCompleted",true)
        editor.commit()
        startActivity(Intent(this@OnboardingActivity,LoginActivity::class.java))
        finish()
    }

    private fun pageChangeListener() {
        binding.viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                if (position == list.size-1) {
                    binding.buttonNext.visibility= View.VISIBLE
                    binding.buttonSkip.visibility=View.INVISIBLE
                    binding.tabLayout.visibility=View.INVISIBLE
                }
                else{
                    binding.buttonNext.visibility=View.INVISIBLE
                    binding.buttonSkip.visibility=View.VISIBLE
                    binding.tabLayout.visibility=View.VISIBLE
                }
            }
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
}