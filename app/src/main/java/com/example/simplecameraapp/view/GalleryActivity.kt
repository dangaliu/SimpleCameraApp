package com.example.simplecameraapp.view

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplecameraapp.databinding.ActivityPhotoListBinding
import com.example.simplecameraapp.model.dto.GalleryItem
import com.example.simplecameraapp.model.dto.GalleryItemType

class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.rvGallery.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity, 2)
            adapter = GalleryAdapter(
                context = this@GalleryActivity,
                galleryItems = getGalleryItems(),
                onImageClick = {
                    startActivity(
                        Intent(
                            this@GalleryActivity,
                            ImageActivity::class.java
                        ).apply { putExtra("imageUri", it.toString()) })
                },
                onVideoClick = {
                    startActivity(
                        Intent(
                            this@GalleryActivity,
                            VideoActivity::class.java
                        ).apply { putExtra("videoUri", it.toString()) })
                }
            )
        }
    }

    private fun getGalleryItems(): List<GalleryItem> {
        val contextWrapper = ContextWrapper(this)
        val directoryImages = contextWrapper.getDir("imagesDir", Context.MODE_PRIVATE)
        val directoryVideos = contextWrapper.getDir("videosDir", Context.MODE_PRIVATE)
        val galleryItems = mutableListOf<GalleryItem>()
        if (directoryImages.listFiles() != null) {
            for (file in directoryImages.listFiles()!!) {
                galleryItems.add(
                    GalleryItem(
                        fileUri = file.toUri(),
                        type = GalleryItemType.IMAGE
                    )
                )
            }
        }
        if (directoryVideos.listFiles() != null) {
            for (file in directoryVideos.listFiles()!!) {
                galleryItems.add(
                    GalleryItem(
                        fileUri = file.toUri(),
                        type = GalleryItemType.VIDEO
                    )
                )
            }
        }
        return galleryItems.reversed()
    }
}