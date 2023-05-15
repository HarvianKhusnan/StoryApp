package com.example.storyapp.ui

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityCameraBinding
import com.example.storyapp.utils.createFile

class CameraActivity : AppCompatActivity(), View.OnClickListener {
    private var binding: ActivityCameraBinding? = null
    private val getBinding get() = binding!!
    private var getImage: ImageCapture? = null
    private var selectorCamera: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(getBinding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        camera()
        view()
    }

    private fun view(){
        with(getBinding){
            imgCapture.setOnClickListener(this@CameraActivity)
            changeCamera.setOnClickListener(this@CameraActivity)
            btnBackCam.setOnClickListener(this@CameraActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun camera(){
        val cameraSettingProvider = ProcessCameraProvider.getInstance(this)

        cameraSettingProvider.addListener({
            val cameraProv: ProcessCameraProvider = cameraSettingProvider.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(getBinding.cameraScreen.surfaceProvider)
                }

            getImage = ImageCapture.Builder().build()
            try {
                cameraProv.unbindAll()
                cameraProv.bindToLifecycle(this, selectorCamera, preview, getImage)
            }catch (exc: Exception){
                Toast.makeText(this@CameraActivity, "Kesalahan dalam memunculkan kamera.", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getPhoto(){
        getBinding.imgCapture.visibility = View.VISIBLE
        val imgCapture = getImage?: return
        val filePhoto = createFile(application)
        val optionForOption = ImageCapture.OutputFileOptions.Builder(filePhoto).build()
        imgCapture.takePicture(
            optionForOption, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity, "Error", Toast.LENGTH_SHORT).show()
                }
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent().apply {
                        putExtra("picture",filePhoto)
                        putExtra("isBackCamera", selectorCamera == CameraSelector.DEFAULT_BACK_CAMERA)
                    }

                    setResult(StoryAddActivity.CAMERA_X_RESULT, intent)
                    finish()
                }
            }
        )
    }

    override fun onClick(p0: View?) {
        when(p0){
            getBinding.imgCapture -> getPhoto()
            getBinding.changeCamera -> {
                selectorCamera = if(selectorCamera == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                camera()
            }
            getBinding.btnBackCam -> onBackPressed()
        }
    }
}