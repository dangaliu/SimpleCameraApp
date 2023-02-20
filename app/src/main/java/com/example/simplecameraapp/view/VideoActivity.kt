package com.example.simplecameraapp.view

import android.os.Bundle
import android.view.KeyEvent
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.simplecameraapp.databinding.ActivityVideoBinding


class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setVideo()
    }

    private fun setVideo() {
        binding.videoView.setMediaController(object : MediaController(this) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                if (event.keyCode == KeyEvent.KEYCODE_BACK) finish()
                return super.dispatchKeyEvent(event)
            }
        })
        binding.videoView.setVideoURI(intent?.getStringExtra("videoUri")!!.toUri())
    }
}