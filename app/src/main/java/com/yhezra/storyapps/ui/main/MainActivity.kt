package com.yhezra.storyapps.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.yhezra.storyapps.databinding.ActivityMainBinding
import com.yhezra.storyapps.ui.welcome.WelcomeActivity
import com.yhezra.storyapps.data.Result
import com.yhezra.storyapps.ui.story.addstory.AddStoryActivity
import com.yhezra.storyapps.ui.story.detailstory.DetailActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(application, dataStore)
    }

    private val launcherAddStoryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                mainViewModel.getAllStories()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.listStoryResponse.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvUserStory.apply {
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        adapter = ListStoryAdapter(result.data) {
                            val intent = Intent(this@MainActivity, DetailActivity::class.java)
                            intent.putExtra(DetailActivity.EXTRA_ID, it.id)
                            startActivity(intent)
                        }
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, result.error, Toast.LENGTH_SHORT).show()
                }
            }
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

        binding.fabAddStory.setOnClickListener {
            launcherAddStoryActivity.launch(Intent(this, AddStoryActivity::class.java))
        }

        setupAction()
    }

    private fun setupAction() {
        binding.toolbar.actionLogout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.toolbar.actionLogout -> {
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
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.logout_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val RESULT_OK = 110
    }
}