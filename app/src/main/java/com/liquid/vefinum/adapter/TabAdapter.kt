package com.liquid.vefinum.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.liquid.vefinum.fragment.CommunitiesFragment
import com.liquid.vefinum.fragment.RunFragment

class TabAdapter(
    fragmentManage: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManage, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RunFragment()
            1 -> CommunitiesFragment()
            else -> Fragment()
        }
    }
}