package com.example.storyapp.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.util.Pair
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.adapter.AdapterStory
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.ApiService
import com.example.storyapp.data.RepositoryStory
import com.example.storyapp.data.StoryComparator
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.databinding.ItemListBinding
import com.example.storyapp.response.Story
import com.example.storyapp.utils.Resource
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.StoryAddViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AdapterStory.CallbackStory{

    companion object {
        const val KEY_LIST_MAP = "MAPS"
        const val LIST_MAP_NAME = "NAME"
    }
    private var storyList: ArrayList<LatLng>? = null
    private var mapNameList: ArrayList<String>? = null
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name= "user_key")
    private var binding: ActivityMainBinding? = null
    private val getBinding get() = binding!!
    private var storyAdapter = AdapterStory(StoryComparator, this)
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(getBinding.root)

        storyList = ArrayList<LatLng>()
        mapNameList = ArrayList<String>()


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val addStory = Intent(this, StoryAddActivity::class.java)
            startActivity(addStory)
        }

        viewModel()
        setup()
    }

    private fun setup(){
        storyAdapter = AdapterStory(StoryComparator, this)
        recyclerView()
    }

    private fun viewModel(){
        val preferences = UserPreferences.getInstances(dataStore)
        val storyRepo = RepositoryStory(ApiConfig.apiInstance)
        val viewModelFac = ViewModelFactory(preferences, storyRepo)
        viewModelFac.setApp(application)

        viewModel = ViewModelProvider(this, viewModelFac)[MainViewModel::class.java]
        viewModel.story.observe(this){
            when(it){
                is Resource.forSucces -> {
                  it.data?.let { stories -> storyAdapter.dataSet(stories)  }
                    loading(false)
                    if(it.data != null) {
                        storyList!!.addAll(it.data.map { story -> LatLng(story.lat, story.lon) })
                        mapNameList!!.addAll(it.data.map { story -> story.name })
                    }
                    lifecycleScope.launch {
                        viewModel.pagingStory().observe(this@MainActivity){story ->
                            storyAdapter.submitData(lifecycle,story)
                        }
                    }
                }
                is Resource.Loading -> loading(true)
                is Resource.forError -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    loading(false)
                }
            }
        }
        getData()
    }

    private fun recyclerView(){
        with(getBinding.storyRv){
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }
        lifecycleScope.launch {
            viewModel.pagingStory().observe(this@MainActivity){story ->
                storyAdapter.submitData(lifecycle,story)
            }
        }
    }

    private fun loading(isloading : Boolean){
        getBinding.progressBarMain.visibility = if( isloading) View.VISIBLE else View.GONE
    }

    private fun getData(){
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.stories()
            viewModel.getStory()
            viewModel.pagingStory()
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


    override fun onStoryClick(story: Story, itemListBinding: ItemListBinding) {
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

