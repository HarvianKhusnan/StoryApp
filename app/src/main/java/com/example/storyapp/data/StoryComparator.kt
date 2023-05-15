package com.example.storyapp.data

import androidx.recyclerview.widget.DiffUtil
import com.example.storyapp.response.Story

object StoryComparator : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean{
        return oldItem == newItem
    }

}