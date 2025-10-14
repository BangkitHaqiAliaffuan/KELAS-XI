package com.trashbin.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trashbin.app.R

class PhotoPagerAdapter(
    private val photoUrls: List<String>
) : RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_fullscreen, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photoUrls[position])
    }

    override fun getItemCount() = photoUrls.size

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_photo_fullscreen)

        fun bind(photoUrl: String) {
            Glide.with(itemView.context)
                .load(photoUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(imageView)
        }
    }
}