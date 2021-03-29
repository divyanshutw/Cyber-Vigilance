package com.practice.cybervigilance.homepage.passwordmanager

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.cybervigilance.AppNetworkStatus
import com.practice.cybervigilance.R
import kotlinx.android.synthetic.main.password_layout.view.*

class PasswordsActivity : AppCompatActivity() {

    private lateinit var firestore:FirebaseFirestore

    private lateinit var viewModel: PasswordsActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passwords)

        //supportActionBar!!.title=getString(R.string.your_passwords)

        viewModel=ViewModelProvider(this).get(PasswordsActivityViewModel::class.java)

        firestore= FirebaseFirestore.getInstance()

        fetch()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            inflateView()
        }
    }

    private fun fetch() {
        if(AppNetworkStatus.getInstance(this).isOnline)
        {
            viewModel.isLoadingDialogVisible.value=true
            showLoadingDialog()
            firestore.collection("Users").document(getSharedPreferences("LOGIN_INFO",Context.MODE_PRIVATE)
                .getString("uid","").toString()).collection("Passwords").get()
                .addOnSuccessListener {
                    val layout=findViewById<LinearLayout>(R.id.layout_pass)
                    for(snap: DocumentSnapshot in it.documents)
                    {
                        val layout=findViewById<LinearLayout>(R.id.layout_pass)
                        var childView: View
                        val inflater =getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        childView = inflater.inflate(R.layout.password_layout,null)

                        childView.editText_passwordName.setText(snap.getString("name"))
                        childView.editText_passwordName.setText(snap.getString("password"))

                        childView.button_submit.visibility=View.GONE
                        convertFields(childView.editText_passwordName,false)
                        convertFields(childView.editText_password,false)
                        layout.addView(childView)
                    }
                    viewModel.isLoadingDialogVisible.value=false
                }
                .addOnFailureListener {
                    viewModel.isLoadingDialogVisible.value=false
                    Toast.makeText(this,getString(R.string.unable_to_load),Toast.LENGTH_LONG).show()
                }
        }
        else
            Toast.makeText(this,getString(R.string.no_internet_connection),Toast.LENGTH_LONG).show()
    }

    private fun inflateView() {
        val layout=findViewById<LinearLayout>(R.id.layout_pass)
        var childView: View
        val inflater =getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        childView = inflater.inflate(R.layout.password_layout,null)

        childView.button_submit.setOnClickListener {
            savePassword(childView.editText_passwordName.text.toString(),childView.editText_password.text.toString(),childView)
        }

        layout.addView(childView)

    }
    private fun convertFields(editText: EditText, mode: Boolean) {
        editText.isEnabled=mode
        editText.isFocusable=mode
        editText.isFocusableInTouchMode=mode
    }

    private fun savePassword(name: String, password: String, childView: View) {
        var map=HashMap<String,String>()
        map.put("name",name)
        map.put("password",password)
        if(AppNetworkStatus.getInstance(this).isOnline)
        {
            viewModel.isLoadingDialogVisible.value=true
            showLoadingDialog()
            firestore.collection("Users").document(getSharedPreferences("LOGIN_INFO",Context.MODE_PRIVATE)
                .getString("uid","").toString()).collection("Passwords").document().set(map)
                .addOnSuccessListener {
                    childView.button_submit.visibility=View.GONE
                    convertFields(childView.editText_passwordName,false)
                    convertFields(childView.editText_password,false)
                    viewModel.isLoadingDialogVisible.value=false
                }
                .addOnFailureListener {
                    Toast.makeText(this,getString(R.string.unable_to_load),Toast.LENGTH_LONG).show()
                    viewModel.isLoadingDialogVisible.value=false
                }
        }
        else
            Toast.makeText(this,getString(R.string.no_internet_connection),Toast.LENGTH_LONG).show()
    }

    private fun showLoadingDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        viewModel.isLoadingDialogVisible.observe(this, Observer {
            if(!it)
            {
                dialog.dismiss()
            }
        })
        val handler= Handler()
        handler.postDelayed(Runnable{
            dialog.dismiss()
        },R.string.loadingDuarationInMillis.toLong())

    }
}