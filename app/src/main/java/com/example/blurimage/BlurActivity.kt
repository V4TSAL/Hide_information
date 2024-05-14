package com.example.blurimage

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.example.blurimage.databinding.BlurImageViewBinding
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

var globalBitmap: Bitmap? = null
var globalUpdatedBitmap: Bitmap?= null
var globalUri : Uri? = null
class BlurActivity : ComponentActivity() {
    private lateinit var binding: BlurImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BlurImageViewBinding.inflate(layoutInflater)
        binding.blurTouchImageView.setBitmap(globalBitmap!!)
        binding.viewUpdatedPhotoButton.setOnClickListener {
            globalUpdatedBitmap =  binding.blurTouchImageView.getBitmap()
            val intent = Intent(this,ViewUpdatedImageActivity::class.java)
            startActivity(intent)
            saveBitmapToGallery(this, globalUpdatedBitmap!!, generateRandomFilename("blurredImage","jpg"),"")
        }
        setContentView(binding.root)
    }
}
fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String, description: String): Boolean {
    // Prepare values for inserting into the MediaStore
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, title)
        put(MediaStore.Images.Media.DESCRIPTION, description)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.WIDTH, bitmap.width)
        put(MediaStore.Images.Media.HEIGHT, bitmap.height)
    }

    // Get the content resolver
    val contentResolver = context.contentResolver

    // Insert the image into the MediaStore
    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    Log.d("IMAGE URI", "saveBitmapToGallery:${uri} ")
    return uri?.let { imageUri ->
        // Get output stream from the content resolver
        val outputStream: OutputStream? = contentResolver.openOutputStream(imageUri)

        // Write the bitmap to the output stream
        val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)

        outputStream?.close()

        if (success) {
            Log.d("SaveBitmapToGallery", "Bitmap saved successfully")
        } else {
            Log.e("SaveBitmapToGallery", "Failed to save bitmap")
            Toast.makeText(context,"Failed to save to gallery", Toast.LENGTH_SHORT).show()
        }

        success
    } ?: run {
        Log.e("SaveBitmapToGallery", "Failed to create media entry")
        false
    }
}
fun loadBitmapFromView(v: View): Bitmap? {
    val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.draw(c)
    return b
}
fun generateRandomFilename(prefix: String, extension: String): String {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val randomString = (1..6).map { Random.nextInt(0, 10) }.joinToString("") // Generate a random 6-digit number
    return "$prefix$timestamp$randomString.$extension"
}
