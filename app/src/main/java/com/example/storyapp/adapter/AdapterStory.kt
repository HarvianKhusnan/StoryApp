package com.example.storyapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ScrollCaptureCallback
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ItemListBinding
import com.example.storyapp.response.Story
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.utils.loadImage

class AdapterStory(private val context: Context) : PagingDataAdapter<Story, AdapterStory.ListViewHolder>(DIFF_CALLBACK){
    private val data = ArrayList<Story>()
    private lateinit var itemCallback: OnItemClickCallback


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) :ListViewHolder{
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterStory.ListViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null){
            holder.bind(item)
            holder.itemView.setOnClickListener {
                itemCallback.onItemClicked(item)
            }
        }
    }

    override fun getItemCount() = data.size
    inner class ListViewHolder(private val binding: ItemListBinding) :
            RecyclerView.ViewHolder(binding.root){
                fun bind(story: Story){
                    binding.apply {
                        Glide.with(this.root.context)
                            .load(story.photoUrl)
                            .centerCrop()
                            .into(usersProfpic)
                        userNameStory.text = story.name
                    }
                }
            }

    interface OnItemClickCallback{
        fun onItemClicked(story: Story)
    }

    fun clickCallback(onItemClickCallback: OnItemClickCallback){
        this.itemCallback = onItemClickCallback
    }
    companion object{
        val DIFF_CALLBACK = object :DiffUtil.ItemCallback<Story>(){
            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}