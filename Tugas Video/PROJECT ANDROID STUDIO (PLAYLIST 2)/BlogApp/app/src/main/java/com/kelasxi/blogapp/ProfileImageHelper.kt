package com.kelasxi.blogapp

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Helper class untuk mengelola profile images
 */
object ProfileImageHelper {

    // Sample profile image URLs (menggunakan placeholder images)
    private val sampleProfileImages = listOf(
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
        "https://images.unsplash.com/photo-1494790108755-2616b612b5bc?w=150&h=150&fit=crop&crop=face",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&h=150&fit=crop&crop=face",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f60?w=150&h=150&fit=crop&crop=face",
        "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face",
        "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&h=150&fit=crop&crop=face"
    )

    /**
     * Get random sample profile image URL
     */
    fun getRandomSampleProfileImage(): String {
        return sampleProfileImages.random()
    }

    /**
     * Load profile image menggunakan Glide dengan fallback ke default
     */
    fun loadProfileImage(
        imageView: ImageView,
        profileUrl: String?,
        placeholderRes: Int = R.drawable.ic_launcher_foreground
    ) {
        val context = imageView.context
        
        if (!profileUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(profileUrl)
                .transform(CircleCrop())
                .placeholder(placeholderRes)
                .error(placeholderRes)
                .into(imageView)
        } else {
            // Use default profile image
            imageView.setImageResource(placeholderRes)
        }
    }

    /**
     * Load profile image untuk CircleImageView
     */
    fun loadProfileImage(
        circleImageView: CircleImageView,
        profileUrl: String?,
        placeholderRes: Int = R.drawable.ic_launcher_foreground
    ) {
        val context = circleImageView.context
        
        if (!profileUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(profileUrl)
                .placeholder(placeholderRes)
                .error(placeholderRes)
                .into(circleImageView)
        } else {
            // Use default profile image
            circleImageView.setImageResource(placeholderRes)
        }
    }

    /**
     * Get profile image berdasarkan nama user (untuk konsistensi)
     */
    fun getProfileImageForUser(userName: String): String {
        // Menggunakan hash dari nama untuk konsistensi profile image
        val hash = userName.hashCode()
        val index = kotlin.math.abs(hash) % sampleProfileImages.size
        return sampleProfileImages[index]
    }
}
