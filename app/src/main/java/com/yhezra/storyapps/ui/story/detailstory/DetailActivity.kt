package com.yhezra.storyapps.ui.story.detailstory

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yhezra.storyapps.R
import com.yhezra.storyapps.databinding.ActivityDetailBinding
import com.yhezra.storyapps.ui.main.MainActivity
import com.yhezra.storyapps.ui.story.StoryViewModel
import com.yhezra.storyapps.ui.story.StoryViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.yhezra.storyapps.data.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(application, dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        val id = intent.getStringExtra(EXTRA_ID)

        if (id != null && storyViewModel.storyResponse.value == null) {
            storyViewModel.getDetailStory(id)
        }

        storyViewModel.storyResponse.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Glide.with(this)
                        .load(result.data.photoUrl)
                        .into(binding.ivItemStory)
                    binding.tvItemName.text = result.data.name
                    binding.tvItemDescription.text = result.data.description
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, result.error, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setToolbar() {
        binding.apply {
            with(toolbar) {
                toolbarLogo.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_arrow_back,
                        null
                    )
                )
                actionLogout.visibility = View.GONE
                toolbarTitle.text = resources.getString(R.string.detail_story)

                toolbarLogo.setOnClickListener {
                    val intent = Intent(this@DetailActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}