package com.practice.cybervigilance.homepage.news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.ActivityFullNewsVideoBinding

class FullNewsVideoActivity :  YouTubeBaseActivity() {

    private lateinit var binding:ActivityFullNewsVideoBinding
    private lateinit var mOnInitializedListener:com.google.android.youtube.player.YouTubePlayer.OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_full_news_video)

        val intent: Intent = intent
        val VIDEO_SAMPLE= intent.getStringExtra("videoUrl")
        Log.d("div","FullWebinarActivity L93 $VIDEO_SAMPLE")
        title=intent.getStringExtra("title")!!

        binding.title.text=title


        mOnInitializedListener= object: YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean, ) {
                p1?.loadVideo(VIDEO_SAMPLE)
            }
            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?, ) {}
        }

        binding.videoView.initialize(YouTubeConfig().getApiKey(),mOnInitializedListener)
    }
}