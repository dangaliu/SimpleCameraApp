package com.example.simplecameraapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplecameraapp.databinding.ActivityPhotoListBinding

class PhotoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}