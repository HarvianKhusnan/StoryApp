package com.example.storyapp.ui

import android.content.Intent
import android.media.session.MediaSession.Token
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.R
import com.example.storyapp.data.TokenUser
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.viewPager.AdapterMainPager
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            val pagerAdapter = AdapterMainPager(this@MainActivity)
            viewPager.adapter = pagerAdapter
            viewPager.isUserInputEnabled = false
            TabLayoutMediator(authTabLayout, viewPager){
                tab, pos ->
                tab.text = resources.getString(title_tab[pos])
            }.attach()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_menu -> {
                logoutFunc()
            }
        }
        return true
    }

    private fun logoutFunc(){
        val userPreferences = UserPreferences(this)
        userPreferences.setUser(TokenUser(""))
        startActivity(Intent(this, LoginActivity::class.java ).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }

    companion object{
        @StringRes
        private val title_tab = intArrayOf(
            R.string.story_activity,
            R.string.Maps_activity
        )
    }
}

