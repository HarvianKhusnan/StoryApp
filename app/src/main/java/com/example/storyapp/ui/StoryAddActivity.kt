package com.example.storyapp.ui

import android.Manifest;
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityStoryAddBinding
import com.example.storyapp.utils.*
import com.example.storyapp.viewmodel.StoryAddViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryAddActivity : AppCompatActivity(), View.OnClickListener {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name="user_key")
    private var binding: ActivityStoryAddBinding? = null
    private val getBinding get() = binding!!
    private var zoomImage = true
    private var fileGet: File? = null
    private lateinit var viewModelAddStory: StoryAddViewModel

     companion object {
         const val CAMERA_X_RESULT = 200
         private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
         private const val REQUEST_CODE_PERMISSIONS = 10
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryAddBinding.inflate(layoutInflater)
        setContentView(getBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    ActivityCompat.requestPermissions(
                        this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                    )
                }
            }

        if(!allPermissionsGranted()){
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS[0])
        }

        view()
        viewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun view(){
        with(getBinding){
            btnCameraAdd.setOnClickListener(this@StoryAddActivity)
            btnGalleryAdd.setOnClickListener(this@StoryAddActivity)
            btnUpload.setOnClickListener(this@StoryAddActivity)
            btnUploadGuest.setOnClickListener(this@StoryAddActivity)
            imgStory.setOnClickListener(this@StoryAddActivity)
        }
    }

    private fun viewModel() {
        val preferences = UserPreferences.getInstances(dataStore)
        viewModelAddStory =
            ViewModelProvider(this, ViewModelFactory(preferences))[StoryAddViewModel::class.java]

        viewModelAddStory.uploadInfo.observe(this) {
            when (it) {
                is Resource.forSucces -> {
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    finish()
                    loading(false)
                }
                is Resource.Loading -> loading(true)
                is Resource.forError -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    loading(false)
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            getBinding.imgStory -> {
                zoomImage = !zoomImage
                getBinding.imgStory.scaleType = if(zoomImage) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
            }
            getBinding.btnCameraAdd -> cameraX()
            getBinding.btnGalleryAdd -> gallery()
            getBinding.btnUpload -> imageUpload(asGuest = false)
            getBinding.btnUploadGuest -> imageUpload(asGuest = true)
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(!allPermissionsGranted()){
                Toast.makeText(this, resources.getString(R.string.request_denied), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launchIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == CAMERA_X_RESULT) {
            val file = it.data?.getSerializableExtra("picture") as File
            val backCam = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBit(BitmapFactory.decodeFile(file.path),backCam)
            getBinding.imgStory.setImageBitmap(result)
            fileGet = file
        }
    }

    private val launchIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == RESULT_OK){
            val selectImg: Uri = result.data?.data as Uri
            val file = uriFile(selectImg, this@StoryAddActivity)
            getBinding.imgStory.setImageURI(selectImg)
            fileGet = file
        }
    }

    private fun cameraX(){
        val intent = Intent(this, CameraActivity::class.java)
        launchIntentCameraX.launch(intent)
    }

    private fun gallery(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val choose = Intent.createChooser(intent, "pilih gambar")
        launchIntentGallery.launch(choose)
    }

    private fun imageUpload(asGuest: Boolean){
        if(fileGet!=null){
            val file = reduceImageSize(fileGet as File)
            val desc =
                getBinding.editDesc.text.toString()
                    .toRequestBody("text/plain".toMediaType())
            val requestImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMulti: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImage)
            CoroutineScope(Dispatchers.IO).launch {
                viewModelAddStory.forUpload(imageMulti, desc, asGuest) }
        }else{
            Toast.makeText(this, resources.getString(R.string.picture_firs), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loading(state: Boolean) {
        //
    }
}