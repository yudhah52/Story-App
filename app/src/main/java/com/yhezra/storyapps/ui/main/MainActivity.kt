package com.yhezra.storyapps.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhezra.storyapps.R
import com.yhezra.storyapps.ui.adapter.ListStoryAdapter
import com.yhezra.storyapps.data.remote.utils.story.Result
import com.yhezra.storyapps.databinding.ActivityMainBinding
import com.yhezra.storyapps.ui.adapter.LoadingStateAdapter
import com.yhezra.storyapps.ui.maps.MapsActivity
import com.yhezra.storyapps.ui.welcome.WelcomeActivity
import com.yhezra.storyapps.ui.story.addstory.AddStoryActivity
import com.yhezra.storyapps.ui.story.detailstory.DetailActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(application, dataStore)
    }

    private val launcherAddStoryActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            mainViewModel.getAllStories()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyAdapter = ListStoryAdapter { story, optionsCompat ->
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, story.id)
            startActivity(intent, optionsCompat.toBundle())
        }

        mainViewModel.listStory.observe(this) {
            storyAdapter.submitData(lifecycle, it)
            if (it == null) Toast.makeText(
                this@MainActivity,
                getString(R.string.empty_text),
                Toast.LENGTH_SHORT
            ).show()
        }

        mainViewModel.isLogin().observe(this) {
            if (it.isNullOrEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.logout),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        binding.rvUserStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter.withLoadStateFooter(footer = LoadingStateAdapter { storyAdapter.retry()})
        }

        binding.fabAddStory.setOnClickListener {
            launcherAddStoryActivity.launch(Intent(this, AddStoryActivity::class.java))
        }

        binding.toolbar.actionMaps.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        setupAction()
    }

    private fun setupAction() {
        binding.toolbar.actionLogout.setOnClickListener {
            mainViewModel.logout().observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, getString(R.string.logout_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object{
        const val RESULT_OK = 110
    }
}