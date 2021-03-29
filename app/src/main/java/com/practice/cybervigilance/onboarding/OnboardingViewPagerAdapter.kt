package com.practice.cybervigilance.onboarding

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.practice.cybervigilance.R

class OnboardingViewPagerAdapter(private val context:Context, private val list:ArrayList<Onboarding>):PagerAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater= LayoutInflater.from(context)
        val view:View=layoutInflater.inflate(R.layout.onboarding_view_pager_item,container,false)

        val imageView: ImageView =view.findViewById(R.id.imageView)
        imageView.setImageResource(list[position].imageRes)
        val textView:TextView=view.findViewById<TextView>(R.id.textView)
        textView.text = list[position].text


        container.addView(view,0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}