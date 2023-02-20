package com.example.simplecameraapp.view

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.simplecameraapp.R
import com.example.simplecameraapp.model.dto.GalleryItem
import com.example.simplecameraapp.model.dto.GalleryItemType
import java.io.File

class GalleryAdapter(
    private val context: Context,
    private val galleryItems: List<GalleryItem>,
    private val onImageClick: (Uri) -> Unit = {},
    private val onVideoClick: (Uri) -> Unit = {}
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    companion object {
        val VIEW_TYPE_IMAGE = 1
        val VIEW_TYPE_VIDEO = 2
    }

    sealed class GalleryViewHolder(itemView: View) : ViewHolder(itemView) {
        val cvRoot = itemView.findViewById<CardView>(R.id.cvRoot)
        val ivImage = itemView.findViewById<ImageView>(R.id.ivImage)

        class GalleryImageViewHolder(itemView: View) : GalleryViewHolder(itemView)
        class GalleryVideoViewHolder(itemView: View) : GalleryViewHolder(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE -> GalleryViewHolder.GalleryImageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_image_type, parent, false)
            )
            VIEW_TYPE_VIDEO -> GalleryViewHolder.GalleryVideoViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_video_type, parent, false)
            )
            else -> throw RuntimeException("Unknown View Type")
        }
    }

    override fun getItemCount(): Int {
        return galleryItems.size
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val galleryItem = galleryItems[position]
        when (holder) {
            is GalleryViewHolder.GalleryImageViewHolder -> {
                holder.ivImage.setOnClickListener {
                    onImageClick(galleryItem.fileUri)
                }
                Glide.with(context).load(galleryItem.fileUri).into(holder.ivImage)
            }
            is GalleryViewHolder.GalleryVideoViewHolder -> {
                holder.ivImage.setOnClickListener {
                    onVideoClick(galleryItem.fileUri)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (galleryItems[position].type) {
            GalleryItemType.IMAGE -> VIEW_TYPE_IMAGE
            GalleryItemType.VIDEO -> VIEW_TYPE_VIDEO
        }
    }
}