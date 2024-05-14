package com.example.blurimage

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.example.blurimage.databinding.ViewUpdatedImageBinding

class ViewUpdatedImageActivity : ComponentActivity() {
    private lateinit var binding: ViewUpdatedImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewUpdatedImageBinding.inflate(layoutInflater)
        binding.blurTouchImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        binding.blurTouchImageView.setImageBitmap(globalUpdatedBitmap)
        setContentView(binding.root)
    }
}
