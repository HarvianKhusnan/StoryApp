package com.example.storyapp.ui

import android.Manifest;
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.storyapp.R
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.ApiService
import com.example.storyapp.data.RepositoryStory
import com.example.storyapp.databinding.ActivityStoryAddBinding
import com.example.storyapp.utils.*
import com.example.storyapp.viewmodel.StoryAddViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryAddActivity : AppCompatActivity(){

    private lateinit var binding: ActivityStoryAddBinding
    private lateinit var photoPath: String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var loct : Location? = null
    private var file : File? = null
    private val viewModel: StoryAddViewModel by viewModels {
        ViewModelFactory.instance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            btnCameraAdd.setOnClickListener { takePhoto() }
            btnGalleryAdd.setOnClickListener { accesGallery() }
            btnUpload.setOnClickListener { imageUpload() }
            addLocation.setOnClickListener { userLocationSet()}
        }

        if(!accPermission()){
            ActivityCompat.requestPermissions(this, permission_req, code_permissions_req)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == code_permissions_req){
            if(!accPermission()){
                Toast.makeText(this, "Permissions is not accessed", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun imageUpload(){
        when{
            binding.editDesc.text.toString().isEmpty() -> {
                Toast.makeText(this, "Add Description Before Please!", Toast.LENGTH_SHORT).show()
            }
            file != null -> {
                val token = "${UserPreferences(this).userGet().token}"
                val getFile = reduceImageSize(file as File)
                val descr = binding.editDesc.text.toString()
                    .toRequestBody("application/json;charset=utf-8".toMediaType())
                val reqImg = getFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imgMltPart = MultipartBody.Part.createFormData("photo",getFile.name, reqImg)
                var lati: RequestBody? = null
                var longi: RequestBody? = null
                if(loct != null){
                    lati = loct!!.latitude.toString()
                        .toRequestBody("application/json;charset=utf-8".toMediaType())
                    longi = loct!!.longitude.toString()
                        .toRequestBody("application/json;charset=utf-8".toMediaType())
                }

                viewModel.imageUpload(imgMltPart,descr,lati,longi,token).observe(this){
                    if (it != null){
                        when(it){
                           is Result.Loading -> {
                               Toast.makeText(this, "Wait for a minute..", Toast.LENGTH_SHORT).show()
                           }
                           is Result.onSuccess -> {
                               Toast.makeText(this, "Yoshhh Upload Succes!", Toast.LENGTH_SHORT).show()
                               startActivity(Intent(this, MainActivity::class.java))
                               finish()
                           }
                           is Result.onError -> {
                               Toast.makeText(this, "Something gone wrong :(", Toast.LENGTH_SHORT).show()
                           }
                        }
                    }

                }
            }
            else -> {
                Toast.makeText(this, "Image must be added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun accesGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Select your Image please!")
        galleryAccesLaunch.launch(chooser)
    }

    private fun takePhoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTemp(application).also {
            val uriImg: Uri = FileProvider.getUriForFile(this, "com.example.storyapp", it)
            photoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImg)
            cameraLaunchAccessed.launch(intent)
        }
    }


    private fun userLocationSet(){
        if(ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if(it != null){
                    loct = it
                }else{
                    Toast.makeText(this, "Failed get Location", Toast.LENGTH_SHORT).show()
                    binding.addLocation.isClickable = false
                }
            }
        }else{
            permissionLauncherReq.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun accPermission() = permission_req.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val cameraLaunchAccessed  =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK) {
                val getFile = File(photoPath)
                file = getFile

                val result = BitmapFactory.decodeFile(file?.absolutePath)
                binding.imgStory.setImageBitmap(result)
            }
        }

    private val permissionLauncherReq =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){per ->
            if(per[Manifest.permission.ACCESS_COARSE_LOCATION] == true){
                userLocationSet()
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                binding.addLocation.isClickable = false
            }
        }

    private val galleryAccesLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
            val imgSelect: Uri = result.data?.data as Uri
            val getFile = uriFile(imgSelect, this)
            file = getFile
            binding.imgStory.setImageURI(imgSelect)
        }
    }


    companion object{
        private val permission_req = arrayOf(Manifest.permission.CAMERA)
        private const val code_permissions_req = 100
    }

}