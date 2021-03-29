package com.practice.cybervigilance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.practice.cybervigilance.homepage.HomePageActivity
import com.practice.cybervigilance.login.LoginActivity
import com.practice.cybervigilance.onboarding.OnboardingActivity

class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenTime:Long=1500
    private lateinit var preferences:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar!!.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        preferences=this.getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        editor=preferences.edit()

        val animationImage = AnimationUtils.loadAnimation(this@SplashScreenActivity, R.anim.splash_screen__image_animation)
        val animationText=AnimationUtils.loadAnimation(this@SplashScreenActivity,R.anim.splash_screen_text_animation)
        val imageView_icon = findViewById<ImageView>(R.id.imageView_icon)
        val imageView_text=findViewById<ImageView>(R.id.imageView_text)

        imageView_icon.animation = animationImage
        imageView_text.animation=animationText

        Handler().postDelayed({
            if(!preferences.getBoolean("isOnboardingCompleted",false))
                startActivity(Intent(this@SplashScreenActivity, OnboardingActivity::class.java))
            else if(!preferences.getBoolean("isLoggedIn",false))
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            else
                startActivity(Intent(this@SplashScreenActivity, HomePageActivity::class.java))
            finish()
        }, splashScreenTime)
    }
}