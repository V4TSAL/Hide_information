package com.example.blurimage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.blurimage.databinding.MainActivityBinding
import com.example.blurimage.ui.theme.BlurImageTheme


class MainActivity : ComponentActivity() {
    lateinit var binding: MainActivityBinding
    private var permissionCallback: ((Boolean) -> Unit)? = null
    private val permissionContract = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        permissionCallback?.invoke(isGranted)
    }
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            globalUri = uri
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            globalBitmap = bitmap
            val intent = Intent(this,BlurActivity::class.java)
            startActivity(intent)
        } else {
            Log.d("PhotoPicker", "No media selected")
            Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestStoragePermission { isGranted->
                when(isGranted){
                    true ->{
//                    Toast.makeText(this,"Please allow the permission",Toast.LENGTH_SHORT).show()
                    }
                    false -> {
//                        Toast.makeText(this,"Please allow the permission",Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }

        binding = MainActivityBinding.inflate(layoutInflater)
        binding.selectPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        setContentView(binding.root)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestStoragePermission(listener: (Boolean) -> Unit) {
        permissionCallback = listener
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
        permissionContract.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            listener.invoke(true)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BlurImageTheme {
        Greeting("Android")
    }
}