package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_blog)

        // Initialize UI components
        initViews()
        
        // Setup toolbar
        setupToolbar()
        
        // Setup click listeners
        setupClickListeners()
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

        // TODO: Save to Firebase as draft
        if (title.isNotEmpty() || content.isNotEmpty()) {
            // Show success message
            // For now, just finish activity
            finish()
        }
    }

    /**
     * Publish blog (placeholder for now)
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

        // TODO: Save to Firebase and publish
        // For now, just finish activity
        finish()
    }
}
