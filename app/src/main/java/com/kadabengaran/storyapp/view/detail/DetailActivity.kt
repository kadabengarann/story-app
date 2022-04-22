package com.kadabengaran.storyapp.view.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.kadabengaran.storyapp.R
import com.kadabengaran.storyapp.databinding.ActivityDetailBinding
import com.kadabengaran.storyapp.service.database.StoryEntity
import com.kadabengaran.storyapp.service.model.StoryItem
import com.kadabengaran.storyapp.utils.loadImage
import com.kadabengaran.storyapp.utils.withDateFormat

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_detail_story)


        val dataStory = intent.getParcelableExtra<StoryEntity>(EXTRA_STORY) as StoryEntity

        binding.apply {
            tvUsername.text = dataStory.name
            tvItemDate.text = getString(R.string.dateFormat, dataStory.createdAt.withDateFormat())
            tvStoryDescription.text = dataStory.description
            imgStory.loadImage(dataStory.photoUrl)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        const val EXTRA_STORY = "extra_object"
    }
}