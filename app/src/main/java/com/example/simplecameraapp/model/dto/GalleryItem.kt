package com.example.simplecameraapp.model.dto

import android.net.Uri

data class GalleryItem(
    val fileUri: Uri,
    val type: GalleryItemType
)
