package com.practice.cybervigilance.homepage.laws

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.FragmentLawsAndTipsBinding
import com.practice.cybervigilance.homepage.news.NewsCategoryFragment
import com.practice.cybervigilance.homepage.news.NewsFragment

class LawsAndTipsFragment : Fragment() {

    private lateinit var binding:FragmentLawsAndTipsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_laws_and_tips, container, false)

        setTabs()

        return binding.root
    }

    private fun setTabs() {
        val viewPagerAdapter= NewsFragment.ViewPagerAdapter(this)
        binding.viewPager.adapter=viewPagerAdapter

        viewPagerAdapter.addFragment(LawsFragment())
        viewPagerAdapter.addFragment(TipsFragment())

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if(position==0)
                tab.text=getString(R.string.cyber_laws)
            else
                tab.text=getString(R.string.best_practices)
        }.attach()
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

