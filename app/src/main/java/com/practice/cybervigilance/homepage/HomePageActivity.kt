package com.practice.cybervigilance.homepage

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.practice.cybervigilance.AppNetworkStatus
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.DialogLogoutBinding
import com.practice.cybervigilance.homepage.contribute.ContributeFragment
import com.practice.cybervigilance.homepage.laws.LawsAndTipsFragment
import com.practice.cybervigilance.homepage.news.NewsFragment
import com.practice.cybervigilance.login.LoginActivity

class HomePageActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)


        drawerLayout = findViewById(R.id.home_drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view_home)

        val headerView=navView.getHeaderView(0)
        val s:String?=getSharedPreferences("LOGIN_INFO",
            Context.MODE_PRIVATE).getString("name","")
        if(s!=null)
        headerView.findViewById<TextView>(R.id.textView_name).text="Welcome $s"
        headerView.findViewById<TextView>(R.id.textView_quote).text="Today's tip:\nStrengthen your home network"

        navController = findNavController(R.id.news_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.newsFragment,
                R.id.contributeFragment,
                R.id.lawsAndTipsFragment,
                R.id.passwordManagerFragment2,
                R.id.vpnFragment2,
                R.id.signOutFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //displayNotification("Daily Tip","Strengthen your home network")

        //setupBottomNav()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        //findViewById<NavigationView>(R.id.nav_view_home).menu.getItem(0).isChecked = true
        //bottomNavigationView.menu.getItem(0).isChecked = true
    }

    private fun setupBottomNav() {
        //bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.itemIconTintList = null;
        val bottomNavController = Navigation.findNavController(this, R.id.news_fragment)
        bottomNavigationView.setupWithNavController(bottomNavController)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.newsFragment->setCurrentFragment(NewsFragment())
                R.id.contributeFragment->setCurrentFragment(ContributeFragment())
                R.id.lawsAndTipsFragment->setCurrentFragment(LawsAndTipsFragment())

            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    private fun displayNotification(title:String, description:String)
    {
        val manager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.O)
        {
            val channel= NotificationChannel("cyberVigilance","Cyber Vigilance", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val builder= NotificationCompat.Builder(applicationContext,"cyberVigilance")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.logo))


        manager.notify(1,builder.build())
    }




}