package com.example.simplecameraapp.view

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplecameraapp.databinding.ActivityPhotoListBinding
import com.example.simplecameraapp.model.dto.GalleryItem
import com.example.simplecameraapp.model.dto.GalleryItemType

class PhotoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.rvGallery.apply {
            layoutManager = GridLayoutManager(this@PhotoListActivity, 2)
            adapter = GalleryAdapter(this@PhotoListActivity, getGalleryItems())
        }
    }

    private fun getGalleryItems(): List<GalleryItem> {
        val contextWrapper = ContextWrapper(this)
        val directoryImages = contextWrapper.getDir("imagesDir", Context.MODE_PRIVATE)
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
        return galleryItems.reversed()
    }
}