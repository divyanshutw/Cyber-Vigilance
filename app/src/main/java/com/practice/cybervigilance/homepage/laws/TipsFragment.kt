package com.practice.cybervigilance.homepage.laws

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.practice.cybervigilance.AppNetworkStatus
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.FragmentTipsBinding
import com.practice.cybervigilance.databinding.FragmentTipsBindingImpl


class TipsFragment : Fragment() {

    private lateinit var binding: FragmentTipsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_tips, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar!!.title=getString(R.string.laws_tips)

//        if(AppNetworkStatus.getInstance(requireContext()).isOnline) {
//            load()
//        }
//        else{
//            val snackbar = Snackbar.make(binding.webView, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
//                .setAction(getString(R.string.retry)) {
//                    load()
//                }
//            snackbar.show()
//        }

        return binding.root
    }

//    private fun load() {
//        binding.webView.webChromeClient = object : WebChromeClient() {
//            override fun onProgressChanged(view: WebView, progress: Int) {
//                //Make the bar disappear after URL is loaded, and changes string to Loading...
//                binding.progressBar.progress = progress
//                if (progress == 100)
//                {
//                    binding.progressBar.visibility= View.INVISIBLE
//                }
//            }
//        }
//        binding.webView.loadUrl("https://us.norton.com/internetsecurity-how-to-how-to-recognize-and-protect-yourself-from-cybercrime.html")
//        binding.webView.settings.javaScriptEnabled=true
//    }


}