package com.example.storyapp.viewPager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.storyapp.ui.MapsFragment
import com.example.storyapp.ui.StoryFragment

class AdapterMainPager(activity: AppCompatActivity) : FragmentStateAdapter(activity){

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment?  = null
        when(position){
            0 -> fragment = StoryFragment()
            1 -> fragment = MapsFragment()
        }
        return fragment as Fragment
    }
}