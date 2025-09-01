package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Activity utama yang menampilkan feed blog posts
 * Menggunakan RecyclerView untuk menampilkan daftar blog
 */
class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvBlogPosts: RecyclerView
    private lateinit var fabWriteBlog: ExtendedFloatingActionButton
    private lateinit var blogPostAdapter: BlogPostAdapter
    
    // Firebase
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        initViews()
        
        // Setup toolbar
        setupToolbar()
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Setup FAB click listener
        setupFabClickListener()
        
        // Load blog posts from Firestore
        loadBlogPosts()
        
        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Inisialisasi view components
     */
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        rvBlogPosts = findViewById(R.id.rvBlogPosts)
        fabWriteBlog = findViewById(R.id.fabWriteBlog)
    }

    /**
     * Setup toolbar
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    /**
     * Setup RecyclerView dengan adapter
     */
    private fun setupRecyclerView() {
        rvBlogPosts.layoutManager = LinearLayoutManager(this)
        
        // Initialize adapter with empty list
        blogPostAdapter = BlogPostAdapter(emptyList())
        rvBlogPosts.adapter = blogPostAdapter
        
        // Setup click listeners for blog post interactions
        setupBlogPostClickListeners()
    }

    /**
     * Setup FAB click listener untuk navigasi ke write blog activity
     */
    private fun setupFabClickListener() {
        fabWriteBlog.setOnClickListener {
            // Check if user is authenticated
            if (auth.currentUser != null) {
                val intent = Intent(this, WriteBlogActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please login to write a blog", Toast.LENGTH_SHORT).show()
                // Navigate to login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    /**
     * Setup click listeners untuk blog post adapter
     */
    private fun setupBlogPostClickListeners() {
        blogPostAdapter.setOnBlogPostClickListener(object : BlogPostAdapter.OnBlogPostClickListener {
            override fun onBlogPostClick(blogPost: BlogPost) {
                // Navigate to blog detail activity
                val intent = Intent(this@MainActivity, BlogDetailActivity::class.java)
                intent.putExtra(BlogDetailActivity.EXTRA_BLOG_POST, blogPost)
                startActivity(intent)
            }

            override fun onLikeClick(blogPost: BlogPost, position: Int) {
                handleLikeClick(blogPost, position)
            }

            override fun onSaveClick(blogPost: BlogPost, position: Int) {
                handleSaveClick(blogPost, position)
            }

            override fun onMoreClick(blogPost: BlogPost, position: Int) {
                // TODO: Show options menu (edit/delete for owner)
                Toast.makeText(this@MainActivity, "More options for: ${blogPost.title}", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthorClick(blogPost: BlogPost) {
                // TODO: Navigate to author profile
                Toast.makeText(this@MainActivity, "Author: ${blogPost.authorName}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Load blog posts dari Firestore dengan realtime listener
     */
    private fun loadBlogPosts() {
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MainActivity", "Listen failed.", e)
                    Toast.makeText(this, "Failed to load posts", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val blogPosts = mutableListOf<BlogPost>()
                snapshots?.forEach { document ->
                    try {
                        val blogPost = BlogPost(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            content = document.getString("content") ?: "",
                            authorName = document.getString("authorName") ?: "",
                            authorId = document.getString("authorId") ?: "",
                            authorProfileUrl = document.getString("authorProfileUrl") ?: "",
                            timestamp = document.getLong("timestamp") ?: 0L,
                            likeCount = document.getLong("likeCount")?.toInt() ?: 0,
                            isLiked = false, // Will be updated after checking user's likes
                            isSaved = false, // Will be updated after checking user's saves
                            tags = (document.get("tags") as? List<String>) ?: emptyList()
                        )
                        blogPosts.add(blogPost)
                    } catch (ex: Exception) {
                        Log.e("MainActivity", "Error parsing blog post: ${document.id}", ex)
                    }
                }
                
                // Update adapter with new data
                blogPostAdapter.updateData(blogPosts)
                
                // Check like/save status for each post
                checkUserInteractions(blogPosts)
                
                Log.d("MainActivity", "Loaded ${blogPosts.size} blog posts")
            }
    }

    /**
     * Handle like button click
     */
    private fun handleLikeClick(blogPost: BlogPost, position: Int) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to like posts", Toast.LENGTH_SHORT).show()
            return
        }

        BlogInteractionHelper.toggleLike(blogPost.id, blogPost.isLiked) { success, newLikeCount ->
            if (success) {
                // Update the blog post data
                val updatedPost = blogPost.copy(
                    isLiked = !blogPost.isLiked,
                    likeCount = newLikeCount
                )
                blogPostAdapter.updateSingleItem(position, updatedPost)
                
                val message = if (updatedPost.isLiked) "Liked!" else "Unliked!"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update like", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Handle save button click
     */
    private fun handleSaveClick(blogPost: BlogPost, position: Int) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to save posts", Toast.LENGTH_SHORT).show()
            return
        }

        BlogInteractionHelper.toggleSave(blogPost.id, blogPost.isSaved) { success ->
            if (success) {
                // Update the blog post data
                val updatedPost = blogPost.copy(isSaved = !blogPost.isSaved)
                blogPostAdapter.updateSingleItem(position, updatedPost)
                
                val message = if (updatedPost.isSaved) "Saved!" else "Removed from saved!"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Check user interactions (likes/saves) untuk semua posts
     */
    private fun checkUserInteractions(blogPosts: List<BlogPost>) {
        if (auth.currentUser == null) return

        blogPosts.forEachIndexed { index, blogPost ->
            // Check if liked
            BlogInteractionHelper.checkIfLiked(blogPost.id) { isLiked ->
                if (isLiked != blogPost.isLiked) {
                    val updatedPost = blogPost.copy(isLiked = isLiked)
                    blogPostAdapter.updateSingleItem(index, updatedPost)
                }
            }

            // Check if saved
            BlogInteractionHelper.checkIfSaved(blogPost.id) { isSaved ->
                if (isSaved != blogPost.isSaved) {
                    val updatedPost = blogPost.copy(isSaved = isSaved)
                    blogPostAdapter.updateSingleItem(index, updatedPost)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh posts when returning to this activity
        // Firestore listener handles this automatically
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_saved_articles -> {
                // Navigate to saved articles activity
                val intent = Intent(this, SavedArticlesActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_profile -> {
                // Navigate to profile activity
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                handleLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Handle logout
     */
    private fun handleLogout() {
        auth.signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        
        // Navigate to login activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}