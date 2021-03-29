package com.practice.cybervigilance.login

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.cybervigilance.R
import com.practice.cybervigilance.homepage.HomePageActivity


class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN=1001

    private lateinit var auth:FirebaseAuth
    private lateinit var listener:FirebaseAuth.AuthStateListener

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences=getSharedPreferences("LOGIN_INFO", MODE_PRIVATE)
        editor=preferences.edit()

        supportActionBar!!.hide()

        auth=FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),)

        listener=FirebaseAuth.AuthStateListener {
            val user=it.currentUser
            if(user!=null)
//                Toast.makeText(this,getString(R.string.you_are_already_logged_in_with_a_different_account),
//                    Toast.LENGTH_LONG).show()
            else
            {
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo) // Set logo drawable
                        .setTheme(R.style.Theme_CyberVigilance) // Set theme
                        .build(),
                    RC_SIGN_IN)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                val email=user!!.email
                val name=user.displayName
                val uid=user.uid
                editor.putString("email",email)
                editor.putString("name",name)
                editor.putString("uid",uid)

                editor.commit()

                putToDb(email,name,uid)
                displayNotification("Daily Tip","Strengthen your home network")

                Toast.makeText(this,getString(R.string.successfully_logged_in),Toast.LENGTH_LONG).show()
                startActivity(Intent(this, HomePageActivity::class.java))
                finish()

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if(response!=null)
                    Toast.makeText(this,getString(R.string.login_failed),Toast.LENGTH_LONG).show()
                Log.d("div","LoginActivity L89 Login failed $response")
                if(response!=null)
                    Log.d("div","LoginActivity L89 Login failed ${response.error}")
            }
        }
    }

    private fun putToDb(email: String?, name: String?, uid: String) {
        val map=HashMap<String,String>()
        if (email != null)
            map["email"] = email
        if(name!=null)
            map["name"] = name
        map["uid"]=uid;
        firestore.collection("Users").document(uid).set(map)
            .addOnSuccessListener {
                editor.putBoolean("isLoggedIn",true)
                editor.commit()
                Toast.makeText(this,getString(R.string.successfully_logged_in),Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,getString(R.string.login_failed),Toast.LENGTH_LONG).show()
            }
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

        manager.notify(1,builder.build())
    }


    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(listener)
    }

    override fun onStop() {
        if(listener!=null)
            auth.removeAuthStateListener(listener)
        super.onStop()
    }
}