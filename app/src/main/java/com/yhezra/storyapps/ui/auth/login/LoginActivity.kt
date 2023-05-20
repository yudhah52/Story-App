package com.yhezra.storyapps.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yhezra.storyapps.R
import com.yhezra.storyapps.data.Result
import com.yhezra.storyapps.databinding.ActivityLoginBinding
import com.yhezra.storyapps.ui.auth.AuthViewModel
import com.yhezra.storyapps.ui.auth.AuthViewModelFactory
import com.yhezra.storyapps.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory.getInstance(dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener(this)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvTitle = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(1000)
        val tvMessage = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(1000)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val etlEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val tvPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(1000)
        val etlPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val btnLogin = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playTogether(
                tvTitle,
                tvMessage,
                tvEmail,
                etlEmail,
                tvPassword,
                etlPassword,
                btnLogin
            )
            start()
        }
    }

    override fun onClick(v: View?) {
        when(v){
            binding.loginButton->{
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                when {
                    email.isEmpty() -> {
                        binding.emailEditTextLayout.error = "Masukkan email"
                    }

                    password.isEmpty() -> {
                        binding.passwordEditTextLayout.error = "Masukkan password"
                    }

                    Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8 -> {
                        binding.apply {
                            emailEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                            passwordEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                        }
                        authViewModel.login(email, password).observe(this) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }

                                is Result.Success -> {
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Yeayy!!!")
                                        setMessage("Kamu berhasil login! Segera bagikan cerita menarikmu!")
                                        setPositiveButton("Selanjutnya") { _, _ ->
                                            val intent = Intent(context, MainActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(intent)
                                            finish()
                                        }
                                        create()
                                        show()
                                    }
                                    binding.progressBar.visibility = View.GONE
                                }

                                is Result.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    Snackbar.make(binding.root, result.error, Snackbar.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.register_invalid),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}