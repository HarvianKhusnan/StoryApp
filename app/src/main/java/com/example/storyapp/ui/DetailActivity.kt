package com.example.storyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.response.Story
import com.example.storyapp.utils.loadImage

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra(NAME)
        val description = intent.getStringExtra(DESC)
        val img = intent.getStringExtra(PHOTO_URL)

        Glide.with(this)
            .load(img)
            .into(binding.storyDetail)

        binding.apply {
            storyTitle.text = name
            detailDesc.text = description
        }
    }

    companion object{
        const val NAME = "name"
        const val DESC = "description"
        const val PHOTO_URL = "photo_url"
    }

}