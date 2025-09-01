package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Activity untuk menampilkan detail blog post
 * Menampilkan konten lengkap dengan fungsi like, save, dan share
 */
class BlogDetailActivity : AppCompatActivity() {

    // UI Components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var ivAuthorProfile: CircleImageView
    private lateinit var tvAuthorName: TextView
    private lateinit var tvPostTime: TextView
    private lateinit var tvBlogTitle: TextView
    private lateinit var tvBlogContent: TextView
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var btnLike: ImageButton
    private lateinit var tvLikeCount: TextView
    private lateinit var btnSave: ImageButton
    private lateinit var btnShare: ImageButton

    // Firebase
    private val auth = Firebase.auth

    // Blog post data
    private lateinit var blogPost: BlogPost

    companion object {
        const val EXTRA_BLOG_POST = "extra_blog_post"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_detail)

        // Get blog post data from intent
        blogPost = intent.getSerializableExtra(EXTRA_BLOG_POST) as? BlogPost
            ?: run {
                Toast.makeText(this, "Error loading blog post", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        // Initialize UI components
        initViews()
        
        // Setup toolbar
        setupToolbar()
        
        // Setup data
        setupData()
        
        // Setup click listeners
        setupClickListeners()
    }

    /**
     * Inisialisasi view components
     */
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        ivAuthorProfile = findViewById(R.id.ivAuthorProfile)
        tvAuthorName = findViewById(R.id.tvAuthorName)
        tvPostTime = findViewById(R.id.tvPostTime)
        tvBlogTitle = findViewById(R.id.tvBlogTitle)
        tvBlogContent = findViewById(R.id.tvBlogContent)
        chipGroupTags = findViewById(R.id.chipGroupTags)
        btnLike = findViewById(R.id.btnLike)
        tvLikeCount = findViewById(R.id.tvLikeCount)
        btnSave = findViewById(R.id.btnSave)
        btnShare = findViewById(R.id.btnShare)
    }

    /**
     * Setup toolbar dengan navigation
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Setup data ke views
     */
    private fun setupData() {
        // Set basic information
        tvAuthorName.text = blogPost.authorName
        tvPostTime.text = blogPost.getFormattedTime()
        tvBlogTitle.text = blogPost.title
        tvBlogContent.text = blogPost.content
        tvLikeCount.text = blogPost.likeCount.toString()

        // Set like button state
        updateLikeButton()

        // Set save button state
        updateSaveButton()

        // Load author profile image
        ProfileImageHelper.loadProfileImage(
            ivAuthorProfile,
            blogPost.authorProfileUrl,
            R.drawable.ic_launcher_foreground
        )

        // Setup tags
        setupTags()

        // Load user-specific data (like/save status)
        loadUserInteractions()
    }

    /**
     * Setup tags display
     */
    private fun setupTags() {
        chipGroupTags.removeAllViews()
        blogPost.tags.forEach { tag ->
            val chip = Chip(this)
            chip.text = tag
            chip.isClickable = false
            chipGroupTags.addView(chip)
        }
    }

    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        btnLike.setOnClickListener {
            handleLikeClick()
        }

        btnSave.setOnClickListener {
            handleSaveClick()
        }

        btnShare.setOnClickListener {
            handleShareClick()
        }

        // Author profile click
        ivAuthorProfile.setOnClickListener {
            // TODO: Navigate to author profile
            Toast.makeText(this, "Author profile: ${blogPost.authorName}", Toast.LENGTH_SHORT).show()
        }

        tvAuthorName.setOnClickListener {
            // TODO: Navigate to author profile
            Toast.makeText(this, "Author profile: ${blogPost.authorName}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Load user interactions (like/save status)
     */
    private fun loadUserInteractions() {
        if (auth.currentUser == null) return

        // Check if liked
        BlogInteractionHelper.checkIfLiked(blogPost.id) { isLiked ->
            blogPost = blogPost.copy(isLiked = isLiked)
            updateLikeButton()
        }

        // Check if saved
        BlogInteractionHelper.checkIfSaved(blogPost.id) { isSaved ->
            blogPost = blogPost.copy(isSaved = isSaved)
            updateSaveButton()
        }
    }

    /**
     * Handle like button click
     */
    private fun handleLikeClick() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "Please login to like posts", Toast.LENGTH_SHORT).show()
            return
        }

        BlogInteractionHelper.toggleLike(blogPost.id, blogPost.isLiked) { success, newLikeCount ->
            if (success) {
                blogPost = blogPost.copy(
                    isLiked = !blogPost.isLiked,
                    likeCount = newLikeCount
                )
                updateLikeButton()
                tvLikeCount.text = newLikeCount.toString()
                
                val message = if (blogPost.isLiked) "Liked!" else "Unliked!"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update like", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Handle save button click
     */
    private fun handleSaveClick() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "Please login to save posts", Toast.LENGTH_SHORT).show()
            return
        }

        BlogInteractionHelper.toggleSave(blogPost.id, blogPost.isSaved) { success ->
            if (success) {
                blogPost = blogPost.copy(isSaved = !blogPost.isSaved)
                updateSaveButton()
                
                val message = if (blogPost.isSaved) "Saved!" else "Removed from saved!"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Handle share button click
     */
    private fun handleShareClick() {
        val shareText = "${blogPost.title}\n\nBy ${blogPost.authorName}\n\n${blogPost.content}"
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, blogPost.title)
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share Blog Post"))
    }

    /**
     * Update like button appearance
     */
    private fun updateLikeButton() {
        if (blogPost.isLiked) {
            btnLike.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            btnLike.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }

    /**
     * Update save button appearance
     */
    private fun updateSaveButton() {
        if (blogPost.isSaved) {
            btnSave.setImageResource(android.R.drawable.ic_menu_save)
            // TODO: Use different icon for saved state
        } else {
            btnSave.setImageResource(android.R.drawable.ic_menu_save)
        }
    }
}
