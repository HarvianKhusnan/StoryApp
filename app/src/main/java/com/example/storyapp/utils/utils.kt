package com.example.storyapp.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.storyapp.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

private const val FORMAT = "dd-MMM-yyyy"

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .apply(RequestOptions.placeholderOf(R.drawable.placeholder_img))
        .into(this)
}
fun String.validEmail(): Boolean{
    val pattern: Pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(this).matches()
}
fun keyboard(activity: AppCompatActivity){
    val v: View? = activity.currentFocus
    if(v != null){
        val methodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        methodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }
}
val timestamp:String = SimpleDateFormat(FORMAT, Locale.US).format(System.currentTimeMillis())

fun createTemp(context: Context) : File{
    val storage: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timestamp,".jpg", storage)
}

fun createFile(app : Application): File{
    val media = app.externalMediaDirs.firstOrNull()?.let{
        File(it,app.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDir = if(
        media != null && media.exists()
    )media else app.filesDir
    return File(outputDir, "$timestamp.jpg")
}

fun rotateBit(bitmap: Bitmap, backCam: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if(backCam){
        matrix.postRotate(90f)
        Bitmap.createBitmap(bitmap, 0,0,bitmap.width,bitmap.height,matrix,true)
    }else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f,1f,bitmap.width/2f,bitmap.height/2f)
        Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
    }
}

fun uriFile(selectImg: Uri, context: Context):File{
    val contentResolv: ContentResolver = context.contentResolver
    val file = createTemp(context)

    val streamInput = contentResolv.openInputStream(selectImg) as InputStream
    val streamOutput: OutputStream = FileOutputStream(file)
    val byte = ByteArray(1024)
    var len: Int
    while (streamInput.read(byte).also {
            len = it
        } > 0 ) {
        streamOutput.write(byte,0,len)
    }
    streamOutput.close()
    streamInput.close()

    return file
}

fun reduceImageSize(file: File) : File{
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressSize = 100
    var lengthStream : Int
    do {
        val bmp = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressSize, bmp)
        val bmpPic = bmp.toByteArray()
        lengthStream = bmpPic.size
        compressSize -= 5
    }while (lengthStream > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressSize, FileOutputStream(file))
    return file
}
