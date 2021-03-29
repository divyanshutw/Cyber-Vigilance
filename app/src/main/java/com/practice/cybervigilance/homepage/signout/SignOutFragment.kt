package com.practice.cybervigilance.homepage.signout

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.practice.cybervigilance.AppNetworkStatus
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.DialogLogoutBinding
import com.practice.cybervigilance.databinding.FragmentSignOutBinding
import com.practice.cybervigilance.login.LoginActivity


class SignOutFragment : Fragment() {

    private lateinit var binding:FragmentSignOutBinding

    private lateinit var auth:FirebaseAuth;

    private lateinit var editor:SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_sign_out,container,false)

        auth= FirebaseAuth.getInstance()

        editor=requireActivity().getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE).edit()

        binding.buttonSignOut.setOnClickListener { onClickSignOut() }
        binding.buttonDelete.setOnClickListener { onClickDelete() }

        return binding.root;
    }

    private fun onClickDelete() {
        if(AppNetworkStatus.getInstance(requireContext()).isOnline)
        {
            val dialogBinding=DataBindingUtil.inflate<DialogLogoutBinding>(
                layoutInflater, R.layout.dialog_logout, null, false)
            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBinding.textView.text=getString(R.string.are_you_sure_you_want_to_delete)
            dialogBinding.buttonYes.setOnClickListener {

                auth.currentUser.delete()
                    .addOnSuccessListener {
                        editor.putBoolean("loggedIn", false)
                        editor.commit()
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                        requireActivity().finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireActivity(),"Unable to delete account",Toast.LENGTH_LONG).show()
                    }

            }
            dialogBinding.buttonNo.setOnClickListener { dialog.dismiss() }
            dialog.setCancelable(false)
            dialog.show()
        }
        else {
            //showInternetNotConnectedDialog()
            val snackbar=
                Snackbar.make(binding.layout,getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
            snackbar.show()
        }

    }

    private fun onClickSignOut() {
        if(AppNetworkStatus.getInstance(requireContext()).isOnline) {
            val dialogBinding = DataBindingUtil.inflate<DialogLogoutBinding>(
                layoutInflater, R.layout.dialog_logout, null, false
            )
            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBinding.textView.text = getString(R.string.are_you_sure_you_want_to_logout)
            dialogBinding.buttonYes.setOnClickListener {
                editor.putBoolean("loggedIn", false)
                editor.commit()

                FirebaseAuth.getInstance().signOut()

                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }
            dialogBinding.buttonNo.setOnClickListener { dialog.dismiss() }
            dialog.setCancelable(false)
            dialog.show()
        }
        else {
            //showInternetNotConnectedDialog()
            val snackbar=
                Snackbar.make(binding.layout,getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
            snackbar.show()
        }

    }
}