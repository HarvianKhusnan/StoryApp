package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.util.Pair
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.adapter.AdapterStory
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.databinding.ItemListBinding
import com.example.storyapp.response.Story
import com.example.storyapp.utils.Resource
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_LIST_MAP = "MAPS"
        const val LIST_MAP_NAME = "NAME"
    }
    private var storyList: ArrayList<LatLng>? = null
    private var mapNameList: ArrayList<String>? = null
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name= "user_key")
    private var binding: ActivityMainBinding? = null
    private val getBinding get() = binding!!
    private val storyAdapter = AdapterStory(this)
    private lateinit var viewModel: MainViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(getBinding.root)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val addStory = Intent(this, StoryAddActivity::class.java)
            startActivity(addStory)
        }

        viewModel()
        setup()
    }

    private fun setup(){
        recyclerView()
    }

    private fun viewModel(){
        val preferences = UserPreferences.getInstances(dataStore)
        val viewModelFac = ViewModelFactory(preferences)
        viewModelFac.setApp(application)

        viewModel = ViewModelProvider(this, viewModelFac)[MainViewModel::class.java]
        viewModel.story.observe(this){
            when(it){
                is Resource.forSucces -> {
                    it.data?.let { stories -> storyAdapter.dataSet(stories)  }
                    loading(false)
                }
                is Resource.Loading -> loading(true)
                is Resource.forError -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    loading(false)
                }
            }
        }
        viewModel.getStory().observe(this) {
            if (it != null) {
                for (stori in it.indices) {
                    storyList!!.add(LatLng(it[stori].lat, it[stori].lon))
                    mapNameList!!.add(it[stori].name.toString())
                }
            }
        }
        getData()
    }

    private fun recyclerView(){
        with(getBinding.storyRv){
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }
    }

    private fun loading(isloading : Boolean){
        getBinding.progressBarMain.visibility = if( isloading) View.VISIBLE else View.GONE
    }

    private fun getData(){
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.stories()
            viewModel.getStory()
        }
    }

    override fun onResume(){
        super.onResume()
        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.settings_menu -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.location_activity -> {
                Intent(this, MapsActivity::class.java).also {
                    it.putExtra(KEY_LIST_MAP, storyList)
                    it.putExtra(LIST_MAP_NAME, mapNameList)
                    startActivity(it)
                }
                true
            }
            else -> false
        }
    }


    fun onStoryClick(story: Story, itemListBinding: ItemListBinding) {
        val compat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(itemListBinding.userNameStory, "userName"),
                Pair(itemListBinding.usersProfpic,"userProfPic")
            )
        val intentDetail = Intent(this, DetailActivity::class.java)
        intentDetail.putExtra(DetailActivity.STORY_EXTRA, story)
        startActivity(intentDetail, compat.toBundle())
    }
}