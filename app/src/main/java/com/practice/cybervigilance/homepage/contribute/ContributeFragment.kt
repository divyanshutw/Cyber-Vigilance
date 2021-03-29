package com.practice.cybervigilance.homepage.contribute

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.FragmentContributeBinding
import com.practice.cybervigilance.homepage.HomePageActivity
import kotlinx.android.synthetic.main.fragment_contribute.*


class ContributeFragment : Fragment() {

    private lateinit var binding:FragmentContributeBinding
    private lateinit var viewModel:ContributeViewModel

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_contribute,container,false)

        viewModel=ViewModelProvider(this).get(ContributeViewModel::class.java)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contribute)

        firestore= FirebaseFirestore.getInstance()

        setSpinner()
        binding.buttonSubmit.setOnClickListener { onClickSubmit() }

        return binding.root;

    }

    private fun setSpinner() {
        var list:ArrayList<String> = ArrayList<String>()
        viewModel.isLoadingDialogVisible.value=true
        showLoadingDialog()
        firestore.collection("Data").document("News").get()
            .addOnSuccessListener {
                list=it.get("categories") as ArrayList<String>
                list.removeAt(0)
                viewModel.isLoadingDialogVisible.value=false
                val adapter: ArrayAdapter<String> = ArrayAdapter(binding.root.context, R.layout.support_simple_spinner_dropdown_item, list)
                binding.spinner.adapter=adapter
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(),getString(R.string.unable_to_load), Toast.LENGTH_LONG).show()
                viewModel.isLoadingDialogVisible.value=false
            }

    }

    private fun onClickSubmit() {
        viewModel.isLoadingDialogVisible.value=true
        showLoadingDialog()
        var map=HashMap<String,String>()
        map["type"] = binding.spinner.selectedItem.toString()
        map["title"]=binding.editTextTitle.text.toString()
        map["description"]=binding.editTextDescription.text.toString()
        map["contributer"]=requireActivity().getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE).getString("uid","").toString()

        firestore.collection("Contributions").document().set(map)
            .addOnSuccessListener {
                viewModel.isLoadingDialogVisible.value=false
                Toast.makeText(requireActivity(),"Request received",Toast.LENGTH_LONG).show()
                startActivity(Intent(requireActivity(),HomePageActivity::class.java))
                requireActivity().finish()
            }
            .addOnFailureListener {
                viewModel.isLoadingDialogVisible.value=false
                Toast.makeText(requireActivity(),"Request failed",Toast.LENGTH_LONG).show()
            }
    }

    private fun showLoadingDialog() {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        viewModel.isLoadingDialogVisible.observe(viewLifecycleOwner, Observer {
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