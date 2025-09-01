package com.kelasxi.blogapp

import java.io.Serializable

/**
 * Data class untuk menyimpan informasi blog post
 * Digunakan untuk transfer data dan display di UI
 */
data class BlogPost(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorName: String = "",
    val authorId: String = "",
    val authorProfileUrl: String = "",
    val timestamp: Long = 0L,
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val tags: List<String> = emptyList()
) : Serializable {
    /**
     * Convert timestamp to readable time format
     */
    fun getFormattedTime(): String {
        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - timestamp
        
        return when {
            timeDiff < 60000 -> "Just now" // Less than 1 minute
            timeDiff < 3600000 -> "${timeDiff / 60000} minutes ago" // Less than 1 hour
            timeDiff < 86400000 -> "${timeDiff / 3600000} hours ago" // Less than 1 day
            timeDiff < 604800000 -> "${timeDiff / 86400000} days ago" // Less than 1 week
            else -> "${timeDiff / 604800000} weeks ago"
        }
    }

    /**
     * Get preview content (first 150 characters)
     */
    fun getPreviewContent(): String {
        return if (content.length > 150) {
            content.substring(0, 150) + "..."
        } else {
            content
        }
    }
}
