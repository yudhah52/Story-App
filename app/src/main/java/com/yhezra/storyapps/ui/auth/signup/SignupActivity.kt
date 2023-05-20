package com.yhezra.storyapps.ui.auth.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
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
import com.yhezra.storyapps.databinding.ActivitySignupBinding
import com.yhezra.storyapps.ui.auth.AuthViewModel
import com.yhezra.storyapps.ui.auth.AuthViewModelFactory

class SignupActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignupBinding

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory.getInstance(dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener(this)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvTitle =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(1000)
        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(1000)
        val etlName =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val tvEmail =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val etlEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val tvPassword =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(1000)
        val etlPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val btnSignUp =
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playTogether(
                tvTitle,
                tvName,
                etlName,
                tvEmail,
                etlEmail,
                tvPassword,
                etlPassword,
                btnSignUp
            )
            start()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.signupButton -> {
                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                when {
                    name.isEmpty() -> {
                        binding.nameEditTextLayout.error = "Masukkan email"
                    }

                    email.isEmpty() -> {
                        binding.emailEditTextLayout.error = "Masukkan email"
                    }

                    password.isEmpty() -> {
                        binding.passwordEditTextLayout.error = "Masukkan password"
                    }

                    Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8 -> {
                        binding.apply {
                            nameEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                            emailEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                            passwordEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                        }
                        authViewModel.register(name, email, password).observe(this) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is Result.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Yeayy!!!")
                                        setMessage("Akun kamu berhasil terdaftar! Segera bagikan cerita menarikmu!")
                                        setPositiveButton("Selanjutnya") { _, _ ->
                                            finish()
                                        }
                                        create()
                                        show()
                                    }
                                }
                                is Result.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@SignupActivity,
                                        result.error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(
                            this@SignupActivity,
                            getString(R.string.register_invalid),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}