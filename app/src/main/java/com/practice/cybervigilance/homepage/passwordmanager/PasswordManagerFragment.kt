package com.practice.cybervigilance.homepage.passwordmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.FragmentPasswordManagerBinding


class PasswordManagerFragment : Fragment() {

    private lateinit var binding:FragmentPasswordManagerBinding

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_password_manager, container, false)

        (requireActivity() as AppCompatActivity)!!.supportActionBar!!.title=getString(R.string.your_passwords)

        preferences=requireActivity().getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE)
        editor=preferences.edit()

        if(!preferences.contains("password") || preferences.getString("password","")==null)
        {
            binding.textViewHeading.text=getString(R.string.create_password)
            binding.editTextReenterPassword.visibility=View.VISIBLE
            binding.buttonSubmit.setOnClickListener { onClickCreatePassword() }
        }
        else
            binding.buttonSubmit.setOnClickListener{ onClickSubmitPassword() }

        return binding.root
    }

    private fun onClickSubmitPassword() {
        val password=preferences.getString("password","")
        if(password!=binding.editTextPassword.text.toString())
            Toast.makeText(requireActivity(),getString(R.string.wrong_password),Toast.LENGTH_LONG).show()
        else
        {
            startActivity(Intent(requireActivity(),PasswordsActivity::class.java))
        }
    }

    private fun onClickCreatePassword() {
        if(binding.editTextPassword.text.length<4)
            Toast.makeText(requireActivity(),getString(R.string.password_too_short),Toast.LENGTH_LONG).show()
        else if(binding.editTextPassword.text.toString()!=binding.editTextReenterPassword.text.toString())
            Toast.makeText(requireActivity(),getString(R.string.password_dont_match),Toast.LENGTH_LONG).show()
        else
        {
            editor.putString("password",binding.editTextPassword.text.toString())
            editor.commit()
            startActivity(Intent(requireActivity(),PasswordsActivity::class.java))
        }
    }


}