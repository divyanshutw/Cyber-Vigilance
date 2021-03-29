package com.practice.cybervigilance.homepage.news

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.ActivityFullNewsArticleBinding

class FullNewsArticleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFullNewsArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_full_news_article)

        if(intent!=null)
            inflateViews(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun inflateViews(args: Intent?) {
        val imgUri = args!!.getStringExtra("imageUrl")!!.toUri().buildUpon().scheme("https").build()
        Glide.with(binding.imageView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_baseline_play_circle_24)
            )
            .into(binding.imageView)

        binding.textViewTitle.text=args.getStringExtra("title")
        binding.textViewContributedBy.text=args.getStringExtra("contributedBy")
//        val s=args.getLongExtra("",0).toString()
//        binding.textViewDate.text="${s[6]}${s[7]}-${s[4]}${s[5]}-${s[0]}${s[1]}${s[2]}${s[3]}"
        binding.textViewDescription.text=args.getStringExtra("description")
    }
}