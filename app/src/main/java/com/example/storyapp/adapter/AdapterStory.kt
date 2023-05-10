package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.databinding.ItemListBinding
import com.example.storyapp.response.Story
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.utils.loadImage

class AdapterStory(private val callback: MainActivity) : RecyclerView.Adapter<AdapterStory.StoryViewHolder>() {
    private val data = ArrayList<Story>()

    fun dataSet(story : ArrayList<Story>){
        data.clear()
        data.addAll(story)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = StoryViewHolder (
        ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )

    override fun onBindViewHolder(holder: AdapterStory.StoryViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount() = data.size

    interface CallbackStory {
        fun onStoryClick(story: Story, bindingItem: ItemListBinding)
    }

    inner class StoryViewHolder(private val binding: ItemListBinding) :
            RecyclerView.ViewHolder(binding.root){
                fun bind(story: Story){
                    with(binding){
                        usersProfpic.loadImage(story.photoUrl)
                        userNameStory.text = story.name
                        root.setOnClickListener {callback.onStoryClick(story,this)}
                    }
                }
            }

}