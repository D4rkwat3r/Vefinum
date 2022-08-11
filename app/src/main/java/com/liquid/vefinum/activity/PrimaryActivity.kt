package com.liquid.vefinum.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.liquid.vefinum.R
import com.liquid.vefinum.adapter.TabAdapter
import com.liquid.vefinum.databinding.ActivityPrimaryBinding

class PrimaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrimaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_primary)
        configureNavigation()
    }

    private fun configureNavigation() {
        binding.viewPager2.adapter = TabAdapter(supportFragmentManager, lifecycle)
        binding.viewPager2.isUserInputEnabled = false
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.run -> binding.viewPager2.currentItem = 0
                R.id.communities -> binding.viewPager2.currentItem = 1
            }
            true
        }
    }
}