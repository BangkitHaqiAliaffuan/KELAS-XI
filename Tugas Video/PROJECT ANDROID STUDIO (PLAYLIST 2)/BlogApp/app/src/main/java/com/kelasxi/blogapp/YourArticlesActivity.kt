package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Activity untuk menampilkan artikel-artikel milik user
 * Dengan fungsi view, edit, dan delete
 */
class YourArticlesActivity : AppCompatActivity() {

    // UI Components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvUserArticles: RecyclerView
    private lateinit var llEmptyState: LinearLayout
    private lateinit var fabWriteBlog: FloatingActionButton
    private lateinit var userBlogPostAdapter: UserBlogPostAdapter

    // Firebase
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_articles)

        // Initialize UI components
        initViews()
        
        // Setup toolbar
        setupToolbar()
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Setup FAB click listener
        setupFabClickListener()
        
        // Load user articles
        loadUserArticles()
    }

    /**
     * Inisialisasi view components
     */
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        rvUserArticles = findViewById(R.id.rvUserArticles)
        llEmptyState = findViewById(R.id.llEmptyState)
        fabWriteBlog = findViewById(R.id.fabWriteBlog)
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
     * Setup RecyclerView dengan adapter
     */
    private fun setupRecyclerView() {
        rvUserArticles.layoutManager = LinearLayoutManager(this)
        
        // Initialize adapter with empty list
        userBlogPostAdapter = UserBlogPostAdapter(emptyList())
        rvUserArticles.adapter = userBlogPostAdapter
        
        // Setup click listeners
        setupBlogPostClickListeners()
    }

    /**
     * Setup FAB click listener
     */
    private fun setupFabClickListener() {
        fabWriteBlog.setOnClickListener {
            val intent = Intent(this, WriteBlogActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Setup click listeners untuk user blog post adapter
     */
    private fun setupBlogPostClickListeners() {
        userBlogPostAdapter.setOnUserBlogPostClickListener(object : UserBlogPostAdapter.OnUserBlogPostClickListener {
            override fun onBlogPostClick(blogPost: BlogPost) {
                // Navigate to blog detail activity
                val intent = Intent(this@YourArticlesActivity, BlogDetailActivity::class.java)
                intent.putExtra(BlogDetailActivity.EXTRA_BLOG_POST, blogPost)
                startActivity(intent)
            }

            override fun onEditClick(blogPost: BlogPost) {
                // Navigate to edit blog activity
                val intent = Intent(this@YourArticlesActivity, WriteBlogActivity::class.java)
                intent.putExtra("EDIT_MODE", true)
                intent.putExtra("BLOG_POST", blogPost)
                startActivity(intent)
            }

            override fun onDeleteClick(blogPost: BlogPost) {
                showDeleteConfirmationDialog(blogPost)
            }

            override fun onMoreClick(blogPost: BlogPost) {
                // Show more options
                Toast.makeText(this@YourArticlesActivity, "More options for: ${blogPost.title}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Load artikel-artikel milik user dari Firestore
     */
    private fun loadUserArticles() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to view your articles", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        firestore.collection("posts")
            .whereEqualTo("authorId", currentUser.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("YourArticlesActivity", "Listen failed.", e)
                    Toast.makeText(this, "Failed to load your articles", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val userBlogPosts = mutableListOf<BlogPost>()
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
                            isLiked = false,
                            isSaved = false,
                            tags = (document.get("tags") as? List<String>) ?: emptyList()
                        )
                        userBlogPosts.add(blogPost)
                    } catch (ex: Exception) {
                        Log.e("YourArticlesActivity", "Error parsing blog post: ${document.id}", ex)
                    }
                }
                
                // Update UI based on data
                if (userBlogPosts.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                    userBlogPostAdapter.updateData(userBlogPosts)
                }
                
                Log.d("YourArticlesActivity", "Loaded ${userBlogPosts.size} user articles")
            }
    }

    /**
     * Show empty state
     */
    private fun showEmptyState() {
        llEmptyState.visibility = View.VISIBLE
        rvUserArticles.visibility = View.GONE
    }

    /**
     * Hide empty state
     */
    private fun hideEmptyState() {
        llEmptyState.visibility = View.GONE
        rvUserArticles.visibility = View.VISIBLE
    }

    /**
     * Show delete confirmation dialog
     */
    private fun showDeleteConfirmationDialog(blogPost: BlogPost) {
        AlertDialog.Builder(this)
            .setTitle("Delete Article")
            .setMessage("Are you sure you want to delete \"${blogPost.title}\"? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteBlogPost(blogPost)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Delete blog post dari Firestore
     */
    private fun deleteBlogPost(blogPost: BlogPost) {
        firestore.collection("posts").document(blogPost.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Article deleted successfully", Toast.LENGTH_SHORT).show()
                Log.d("YourArticlesActivity", "Blog post deleted: ${blogPost.id}")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete article: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.e("YourArticlesActivity", "Error deleting blog post", e)
            }
    }

    override fun onResume() {
        super.onResume()
        // Refresh articles when returning to this activity
        // Firestore listener handles this automatically
    }
}
