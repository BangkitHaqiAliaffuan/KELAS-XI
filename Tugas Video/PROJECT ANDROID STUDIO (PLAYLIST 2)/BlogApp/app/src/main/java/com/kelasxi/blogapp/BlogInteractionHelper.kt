package com.kelasxi.blogapp

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Helper class untuk mengelola likes dan saves pada blog posts
 */
object BlogInteractionHelper {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    /**
     * Toggle like status untuk sebuah blog post
     */
    fun toggleLike(postId: String, isCurrentlyLiked: Boolean, callback: (Boolean, Int) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, -1)
            return
        }

        val postRef = firestore.collection("posts").document(postId)
        val userLikesRef = firestore.collection("users")
            .document(currentUser.uid)
            .collection("likes")
            .document(postId)

        firestore.runTransaction { transaction ->
            val postSnapshot = transaction.get(postRef)
            val currentLikeCount = postSnapshot.getLong("likeCount")?.toInt() ?: 0

            if (isCurrentlyLiked) {
                // Remove like
                transaction.update(postRef, "likeCount", currentLikeCount - 1)
                transaction.delete(userLikesRef)
                currentLikeCount - 1
            } else {
                // Add like
                transaction.update(postRef, "likeCount", currentLikeCount + 1)
                transaction.set(userLikesRef, mapOf(
                    "postId" to postId,
                    "timestamp" to System.currentTimeMillis()
                ))
                currentLikeCount + 1
            }
        }.addOnSuccessListener { newLikeCount ->
            callback(true, newLikeCount)
            Log.d("BlogInteractionHelper", "Like toggled successfully for post $postId")
        }.addOnFailureListener { e ->
            Log.e("BlogInteractionHelper", "Error toggling like for post $postId", e)
            callback(false, -1)
        }
    }

    /**
     * Toggle save status untuk sebuah blog post
     */
    fun toggleSave(postId: String, isCurrentlySaved: Boolean, callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false)
            return
        }

        val userSavesRef = firestore.collection("users")
            .document(currentUser.uid)
            .collection("savedPosts")
            .document(postId)

        if (isCurrentlySaved) {
            // Remove from saved
            userSavesRef.delete()
                .addOnSuccessListener {
                    callback(true)
                    Log.d("BlogInteractionHelper", "Post $postId removed from saved")
                }
                .addOnFailureListener { e ->
                    Log.e("BlogInteractionHelper", "Error removing post $postId from saved", e)
                    callback(false)
                }
        } else {
            // Add to saved
            val saveData = mapOf(
                "postId" to postId,
                "timestamp" to System.currentTimeMillis()
            )
            userSavesRef.set(saveData)
                .addOnSuccessListener {
                    callback(true)
                    Log.d("BlogInteractionHelper", "Post $postId added to saved")
                }
                .addOnFailureListener { e ->
                    Log.e("BlogInteractionHelper", "Error saving post $postId", e)
                    callback(false)
                }
        }
    }

    /**
     * Check apakah user sudah like post tertentu
     */
    fun checkIfLiked(postId: String, callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false)
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("likes")
            .document(postId)
            .get()
            .addOnSuccessListener { document ->
                callback(document.exists())
            }
            .addOnFailureListener { e ->
                Log.e("BlogInteractionHelper", "Error checking like status for post $postId", e)
                callback(false)
            }
    }

    /**
     * Check apakah user sudah save post tertentu
     */
    fun checkIfSaved(postId: String, callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false)
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("savedPosts")
            .document(postId)
            .get()
            .addOnSuccessListener { document ->
                callback(document.exists())
            }
            .addOnFailureListener { e ->
                Log.e("BlogInteractionHelper", "Error checking save status for post $postId", e)
                callback(false)
            }
    }
}
