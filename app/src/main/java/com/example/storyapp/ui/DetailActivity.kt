package com.example.storyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.response.Story
import com.example.storyapp.utils.loadImage

class DetailActivity : AppCompatActivity(), View.OnClickListener {
    private var binding: ActivityDetailBinding? = null
    private val getBinding get() = binding!!
    private var zoomImage = true

    companion object {
        const val STORY_EXTRA ="extra_story"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(getBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Story"

        val story = intent.getParcelableExtra<Story>(STORY_EXTRA)
        view(story)
    }

    private fun view(story: Story?){
        with(getBinding){
            storyDetail.loadImage(story?.photoUrl)
            storyTitle.text = story?.name
            detailDesc.text= story?.descript

            storyDetail.setOnClickListener(this@DetailActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> true
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            getBinding.storyDetail -> {
                zoomImage = !zoomImage
                getBinding.storyDetail.scaleType = if (zoomImage) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
            }
        }
    }
}