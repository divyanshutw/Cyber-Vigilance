package com.practice.cybervigilance.homepage.news

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.cybervigilance.AppNetworkStatus
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.DialogLogoutBinding
import com.practice.cybervigilance.databinding.FragmentNewsBinding
import com.practice.cybervigilance.login.LoginActivity


class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var viewModel:NewsViewModel

    private var categoriesList=ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_news,container,false)
        binding.lifecycleOwner=this

        viewModel=ViewModelProvider(this).get(NewsViewModel::class.java)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.news)

        binding.buttonLogout.setOnClickListener { onClickSignOut() }

        loadCategories()

        return binding.root
    }

    private fun loadCategories() {
        if(AppNetworkStatus.getInstance(requireContext()).isOnline)
        {
            showLoadingDialog()
            getAllCategories()
        }
        else {
            //showInternetNotConnectedDialog()
            val snackbar = Snackbar.make(binding.layout, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry)) {
                    loadCategories()
                }
            snackbar.show()
        }
    }

    private fun getAllCategories() {
        viewModel.isLoadingDialogVisible.value=true
        val firestore=FirebaseFirestore.getInstance()
        firestore.collection("Data").document("News").get()
            .addOnSuccessListener {
                categoriesList=it.get("categories") as ArrayList<String>
                Log.d("div","NewsFragment L70 $categoriesList")
                viewModel.isLoadingDialogVisible.value=false
                addCategories()
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(),getString(R.string.unable_to_load),Toast.LENGTH_LONG).show()
                viewModel.isLoadingDialogVisible.value=false
            }
    }

    private fun addCategories()
    {
        val viewPagerAdapter=ViewPagerAdapter(this)

        binding.viewPager.adapter=viewPagerAdapter

        var i:Int=0
        while(i<categoriesList.size)
        {
            val bundle = Bundle()
            bundle.putString("category", categoriesList[i])
            val fragment= NewsCategoryFragment()
            fragment.arguments=bundle
            viewPagerAdapter.addFragment(fragment)
            i++
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = categoriesList[position]
        }.attach()
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

    private fun onClickSignOut() {

        val dialogBinding = DataBindingUtil.inflate<DialogLogoutBinding>(
            layoutInflater, R.layout.dialog_logout, null, false
        )
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.textView.text = getString(R.string.are_you_sure_you_want_to_logout)
        dialogBinding.buttonYes.setOnClickListener {
            val editor=requireActivity().getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE).edit()
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

    class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        private val fragmentList = ArrayList<Fragment>()
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
        fun addFragment(fragment: Fragment) {
            fragmentList.add(fragment)
        }
    }




}