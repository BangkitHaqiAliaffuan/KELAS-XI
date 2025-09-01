package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Activity untuk menulis blog baru atau mengedit blog yang sudah ada
 * Menyediakan editor teks untuk judul dan konten blog
 */
class WriteBlogActivity : AppCompatActivity() {

    // UI Components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var etBlogTitle: TextInputEditText
    private lateinit var etBlogContent: TextInputEditText
    private lateinit var btnSaveDraft: MaterialButton
    private lateinit var btnPublish: MaterialButton

    // Firebase
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    // Edit mode
    private var isEditMode = false
    private var editingBlogPost: BlogPost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_blog)

        // Initialize UI components
        initViews()
        
        // Check if this is edit mode
        checkEditMode()
        
        // Setup toolbar
        setupToolbar()
        
        // Setup click listeners
        setupClickListeners()
    }

    /**
     * Check apakah ini edit mode dan load data jika iya
     */
    private fun checkEditMode() {
        isEditMode = intent.getBooleanExtra("EDIT_MODE", false)
        if (isEditMode) {
            editingBlogPost = intent.getSerializableExtra("BLOG_POST") as? BlogPost
            editingBlogPost?.let { blogPost ->
                // Populate fields dengan data yang ada
                etBlogTitle.setText(blogPost.title)
                etBlogContent.setText(blogPost.content)
                
                // Update UI untuk edit mode
                toolbar.title = "Edit Blog"
                btnPublish.text = "Update"
            }
        }
    }

    /**
     * Inisialisasi view components
     */
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        etBlogTitle = findViewById(R.id.etBlogTitle)
        etBlogContent = findViewById(R.id.etBlogContent)
        btnSaveDraft = findViewById(R.id.btnSaveDraft)
        btnPublish = findViewById(R.id.btnPublish)
    }

    /**
     * Setup toolbar dengan navigation
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        toolbar.setNavigationOnClickListener {
            finish() // Close this activity
        }
    }

    /**
     * Setup click listeners untuk buttons
     */
    private fun setupClickListeners() {
        btnSaveDraft.setOnClickListener {
            // TODO: Implement save draft functionality
            saveDraft()
        }

        btnPublish.setOnClickListener {
            // TODO: Implement publish functionality
            publishBlog()
        }
    }

    /**
     * Save blog as draft (placeholder for now)
     */
    private fun saveDraft() {
        val title = etBlogTitle.text.toString().trim()
        val content = etBlogContent.text.toString().trim()

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to save drafts", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Implement draft functionality
        // For now, just show success message
        Toast.makeText(this, "Draft saved locally (feature coming soon)", Toast.LENGTH_SHORT).show()
    }

    /**
     * Publish blog to Firestore
     */
    private fun publishBlog() {
        val title = etBlogTitle.text.toString().trim()
        val content = etBlogContent.text.toString().trim()

        // Basic validation
        if (title.isEmpty()) {
            etBlogTitle.error = "Title is required"
            return
        }

        if (content.isEmpty()) {
            etBlogContent.error = "Content is required"
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to publish", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable publish button to prevent double submission
        btnPublish.isEnabled = false
        btnPublish.text = if (isEditMode) "Updating..." else "Publishing..."

        if (isEditMode && editingBlogPost != null) {
            // Update existing blog post
            updateBlogPost(title, content)
        } else {
            // Create new blog post
            createNewBlogPost(title, content, currentUser.uid)
        }
    }

    /**
     * Create new blog post
     */
    private fun createNewBlogPost(title: String, content: String, userId: String) {
        // Get user profile for author information
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc ->
                val authorName = userDoc.getString("name") ?: auth.currentUser?.email ?: "Anonymous"
                
                // Create blog post data
                val blogPostData = mapOf(
                    "title" to title,
                    "content" to content,
                    "authorName" to authorName,
                    "authorId" to userId,
                    "authorProfileUrl" to "", // TODO: Add profile image support
                    "timestamp" to System.currentTimeMillis(),
                    "likeCount" to 0,
                    "tags" to emptyList<String>() // TODO: Add tag support
                )

                // Save to Firestore
                firestore.collection("posts")
                    .add(blogPostData)
                    .addOnSuccessListener { documentReference ->
                        Log.d("WriteBlogActivity", "Blog post published with ID: ${documentReference.id}")
                        Toast.makeText(this, "Blog published successfully!", Toast.LENGTH_SHORT).show()
                        
                        // Navigate back
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w("WriteBlogActivity", "Error publishing blog post", e)
                        Toast.makeText(this, "Failed to publish blog: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        
                        // Re-enable publish button
                        btnPublish.isEnabled = true
                        btnPublish.text = "Publish"
                    }
            }
            .addOnFailureListener { e ->
                Log.w("WriteBlogActivity", "Error getting user profile", e)
                Toast.makeText(this, "Failed to get user profile: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                
                // Re-enable publish button
                btnPublish.isEnabled = true
                btnPublish.text = "Publish"
            }
    }

    /**
     * Update existing blog post
     */
    private fun updateBlogPost(title: String, content: String) {
        val blogPost = editingBlogPost ?: return
        
        val updateData = mapOf(
            "title" to title,
            "content" to content,
            "timestamp" to System.currentTimeMillis() // Update timestamp
        )

        firestore.collection("posts").document(blogPost.id)
            .update(updateData)
            .addOnSuccessListener {
                Log.d("WriteBlogActivity", "Blog post updated: ${blogPost.id}")
                Toast.makeText(this, "Blog updated successfully!", Toast.LENGTH_SHORT).show()
                
                // Navigate back
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("WriteBlogActivity", "Error updating blog post", e)
                Toast.makeText(this, "Failed to update blog: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                
                // Re-enable publish button
                btnPublish.isEnabled = true
                btnPublish.text = "Update"
            }
    }
}
