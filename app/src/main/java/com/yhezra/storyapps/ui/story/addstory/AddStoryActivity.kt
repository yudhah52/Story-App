package com.yhezra.storyapps.ui.story.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yhezra.storyapps.R
import com.yhezra.storyapps.data.remote.utils.rotateFile
import com.yhezra.storyapps.data.remote.utils.uriToFile
import com.yhezra.storyapps.databinding.ActivityAddStoryBinding
import com.yhezra.storyapps.ui.main.MainActivity
import com.yhezra.storyapps.ui.story.StoryViewModel
import com.yhezra.storyapps.ui.story.StoryViewModelFactory
import com.yhezra.storyapps.ui.story.addstory.camera.CameraActivity
import java.io.File
import com.yhezra.storyapps.data.Result

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    private var getFile: File? = null

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val addStoryViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(application, dataStore)
    }

    private val launcherIntentCameraX =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == CAMERA_X_RESULT) {
                val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.data?.getSerializableExtra("picture", File::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    it.data?.getSerializableExtra("picture")
                } as? File

                val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
                myFile?.let { file ->
                    rotateFile(file, isBackCamera)
                    getFile = file
                    binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
                }
            }
        }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg = result.data?.data as Uri
                selectedImg.let { uri ->
                    val myFile = uriToFile(uri, this@AddStoryActivity)
                    getFile = myFile
                    binding.previewImageView.setImageURI(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setToolbar()
    }

    private fun setupAction() {
        binding.apply {
            btnCamera.setOnClickListener {
                if (!allPermissionsGranted()) ActivityCompat.requestPermissions(
                    this@AddStoryActivity,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
                else startCameraX()
            }
            btnGallery.setOnClickListener { startGallery() }
            btnAdd.setOnClickListener { uploadImage() }
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
                actionLogout.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_settings,
                        null
                    )
                )
                toolbarTitle.text = resources.getString(R.string.addStory)

                toolbarLogo.setOnClickListener {
                    val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                    startActivity(intent)
                }

                actionLogout.setOnClickListener {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.fromParts("package", packageName, null)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.allow_permission), Toast.LENGTH_SHORT)
                    .show()
            } else {
                startCameraX()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        val description = binding.storyDescriptionEditText.text.toString()
        if (description.isNotBlank() && getFile != null) {
            val file = getFile as File

            addStoryViewModel.addStory(description, file).observe(this) {
                when (it) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val intent = Intent()
                        setResult(MainActivity.RESULT_OK, intent)
                        finish()
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@AddStoryActivity, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(
                this@AddStoryActivity,
                getString(R.string.story_invalid),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}