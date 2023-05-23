package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.AdapterStory
import com.example.storyapp.data.TokenUser
import com.example.storyapp.databinding.FragmentStoryBinding
import com.example.storyapp.response.Story
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.viewmodel.StoryViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class StoryFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentStoryBinding? = null
    private val _binding get() = binding!!
    private lateinit var tokenUser: TokenUser
    private lateinit var userPreferences: UserPreferences
    private var token: String = ""
    private val viewModel: StoryViewModel by viewModels{
        ViewModelFactory.instance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      binding = FragmentStoryBinding.inflate(layoutInflater, container, false)
      return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())
        tokenUser =userPreferences.userGet()
        token = tokenUser.token.toString()

        _binding.fab.setOnClickListener(this@StoryFragment)
        getData()
    }

    private fun getData(){
        val storyAdapter = AdapterStory(requireContext())
        viewModel.stories(token).observe(viewLifecycleOwner){
            if(it != null){
                storyAdapter.submitData(lifecycle, it)
            }
        }
        lifecycleScope.launch {
            storyAdapter.loadStateFlow.collectLatest {
                if(it.append is LoadState.Loading){
                    _binding.loadingBar.visibility = View.VISIBLE
                }else{
                    _binding.loadingBar.visibility = View.GONE
                }
            }
        }
        _binding.apply {
            storyRv.layoutManager = LinearLayoutManager(context)
            storyRv.setHasFixedSize(true)
            storyRv.adapter = storyAdapter
            storyRv.layoutManager = LinearLayoutManager(requireContext())
            storyAdapter.clickCallback(object : AdapterStory.OnItemClickCallback{
                override fun onItemClicked(story: Story) {
                    Intent(this@StoryFragment.context, DetailActivity::class.java).also {it ->
                        it.putExtra(DetailActivity.NAME, story.name)
                        it.putExtra(DetailActivity.DESC, story.descript)
                        it.putExtra(DetailActivity.PHOTO_URL, story.photoUrl)
                        startActivity(it)
                    }
                }
            })
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            _binding.fab.id -> startActivity(Intent(requireContext(),StoryAddActivity::class.java))
        }
    }

}