package com.practice.cybervigilance.homepage.vpn

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.practice.cybervigilance.AppNetworkStatus
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.FragmentVpnBinding


class VpnFragment : Fragment() {

    private lateinit var binding:FragmentVpnBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(layoutInflater, R.layout.fragment_vpn, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar!!.title=getString(R.string.report_a_crime)

        if(AppNetworkStatus.getInstance(requireContext()).isOnline) {
            val url = "https://cybercrime.gov.in/Accept.aspx"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        else{
            val snackbar = Snackbar.make(
                binding.webView,
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(getString(R.string.retry)) {
                    val url = "https://cybercrime.gov.in/Accept.aspx"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
            snackbar.show()
        }

        return binding.root
    }

    private fun load() {
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                binding.progressBar.progress = progress
                if (progress == 100)
                {
                    binding.progressBar.visibility= View.INVISIBLE
                }
            }
        }
        binding.webView.loadUrl("https://cybercrime.gov.in/Accept.aspx")
        binding.webView.settings.javaScriptEnabled=true
    }


}