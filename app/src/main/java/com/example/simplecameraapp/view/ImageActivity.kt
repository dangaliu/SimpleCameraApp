package com.example.simplecameraapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.example.simplecameraapp.R
import com.example.simplecameraapp.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setImage()
    }

    private fun setImage() {
        binding.ivImage.setImageURI(intent?.getStringExtra("imageUri")!!.toUri())
    }
}