package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Activity untuk menampilkan artikel yang disimpan oleh user
 */
class SavedArticlesActivity : AppCompatActivity() {

    // UI Components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvSavedArticles: RecyclerView
    private lateinit var tvEmptyState: LinearLayout
    private lateinit var blogPostAdapter: BlogPostAdapter
    
    // Firebase
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    
    // Data
    private val savedArticles = mutableListOf<BlogPost>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_articles)

        // Initialize UI components
        initViews()
        
        // Setup toolbar
        setupToolbar()
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Load saved articles
        loadSavedArticles()
    }

    /**
     * Initialize UI components
     */
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        rvSavedArticles = findViewById(R.id.rvSavedArticles)
        tvEmptyState = findViewById(R.id.tvEmptyState)
    }

    /**
     * Setup toolbar
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Saved Articles"
    }

    /**
     * Setup RecyclerView dengan adapter
     */
    private fun setupRecyclerView() {
        rvSavedArticles.layoutManager = LinearLayoutManager(this)
        
        // Initialize adapter
        blogPostAdapter = BlogPostAdapter(emptyList())
        rvSavedArticles.adapter = blogPostAdapter
        
        // Setup click listeners
        setupClickListeners()
    }

    /**
     * Setup click listeners untuk blog post adapter
     */
    private fun setupClickListeners() {
        blogPostAdapter.setOnBlogPostClickListener(object : BlogPostAdapter.OnBlogPostClickListener {
            override fun onBlogPostClick(blogPost: BlogPost) {
                // Navigate to blog detail activity
                val intent = Intent(this@SavedArticlesActivity, BlogDetailActivity::class.java)
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
                // TODO: Show options menu for saved articles
                Toast.makeText(this@SavedArticlesActivity, "More options for: ${blogPost.title}", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthorClick(blogPost: BlogPost) {
                // TODO: Navigate to author profile
                Toast.makeText(this@SavedArticlesActivity, "Author: ${blogPost.authorName}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Load saved articles from Firestore
     */
    private fun loadSavedArticles() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to view saved articles", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = currentUser.uid
        
        // Get user's saved articles
        firestore.collection("users")
            .document(userId)
            .collection("savedPosts")
            .get()
            .addOnSuccessListener { savedArticlesSnapshot ->
                val savedArticleIds = savedArticlesSnapshot.documents.map { it.id }
                
                Log.d("SavedArticlesActivity", "Found ${savedArticleIds.size} saved post IDs: $savedArticleIds")
                
                if (savedArticleIds.isEmpty()) {
                    showEmptyState()
                    return@addOnSuccessListener
                }
                
                // Firestore whereIn has a limit of 10 items, so we need to chunk the requests
                val chunks = savedArticleIds.chunked(10)
                val allBlogPosts = mutableListOf<BlogPost>()
                var completedChunks = 0
                
                for (chunk in chunks) {
                    Log.d("SavedArticlesActivity", "Querying chunk: $chunk")
                    
                    // Try getting documents by ID directly from posts collection
                    var foundDocuments = false
                    var processedInChunk = 0
                    val targetCount = chunk.size
                    
                    for (postId in chunk) {
                        firestore.collection("posts").document(postId)
                            .get()
                            .addOnSuccessListener { document ->
                                processedInChunk++
                                if (document.exists()) {
                                    foundDocuments = true
                                    processDocuments(listOf(document), allBlogPosts, userId)
                                    Log.d("SavedArticlesActivity", "Found document in posts: ${document.id}")
                                }
                                
                                if (processedInChunk == targetCount) {
                                    if (!foundDocuments) {
                                        // Try blogPosts collection
                                        tryBlogPostsCollection(chunk, allBlogPosts, userId) {
                                            completedChunks++
                                            checkCompletion(allBlogPosts, completedChunks, chunks.size)
                                        }
                                    } else {
                                        completedChunks++
                                        checkCompletion(allBlogPosts, completedChunks, chunks.size)
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("SavedArticlesActivity", "Error getting document $postId from posts", e)
                                processedInChunk++
                                if (processedInChunk == targetCount && !foundDocuments) {
                                    // Try blogPosts collection
                                    tryBlogPostsCollection(chunk, allBlogPosts, userId) {
                                        completedChunks++
                                        checkCompletion(allBlogPosts, completedChunks, chunks.size)
                                    }
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("SavedArticlesActivity", "Error loading saved articles", e)
                Toast.makeText(this, "Failed to load saved articles", Toast.LENGTH_SHORT).show()
                showEmptyState()
            }
    }

    private fun processDocuments(documents: List<com.google.firebase.firestore.DocumentSnapshot>, allBlogPosts: MutableList<BlogPost>, userId: String) {
        for (document in documents) {
            try {
                Log.d("SavedArticlesActivity", "Processing document: ${document.id}, data: ${document.data}")
                
                // Handle both document.id and field "id"
                val postId = document.getString("id") ?: document.id
                
                val blogPost = BlogPost(
                    id = postId,
                    title = document.getString("title") ?: "",
                    content = document.getString("content") ?: "",
                    authorName = document.getString("authorName") ?: document.getString("author") ?: "",
                    authorId = document.getString("authorId") ?: "",
                    authorProfileUrl = document.getString("authorProfileUrl") ?: "",
                    timestamp = document.getLong("timestamp") ?: document.getTimestamp("timestamp")?.toDate()?.time ?: 0L,
                    likeCount = document.getLong("likeCount")?.toInt() ?: 0,
                    isLiked = false, // Will be set after this
                    isSaved = true, // All articles here are saved
                    tags = (document.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                )
                
                // Set interaction states
                val updatedBlogPost = blogPost.copy(
                    isLiked = (document.get("likes") as? Map<*, *>)?.containsKey(userId) == true
                )
                
                allBlogPosts.add(updatedBlogPost)
                Log.d("SavedArticlesActivity", "Added blog post: ${updatedBlogPost.title} (ID: ${updatedBlogPost.id})")
            } catch (e: Exception) {
                Log.e("SavedArticlesActivity", "Error parsing blog post: ${document.id}", e)
            }
        }
    }

    private fun tryBlogPostsCollection(chunk: List<String>, allBlogPosts: MutableList<BlogPost>, userId: String, onComplete: () -> Unit) {
        Log.d("SavedArticlesActivity", "Trying blogPosts collection for chunk: $chunk")
        var processedInChunk = 0
        val targetCount = chunk.size
        
        for (postId in chunk) {
            firestore.collection("blogPosts").document(postId)
                .get()
                .addOnSuccessListener { document ->
                    processedInChunk++
                    if (document.exists()) {
                        processDocuments(listOf(document), allBlogPosts, userId)
                        Log.d("SavedArticlesActivity", "Found document in blogPosts: ${document.id}")
                    }
                    
                    if (processedInChunk == targetCount) {
                        onComplete()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("SavedArticlesActivity", "Error getting document $postId from blogPosts", e)
                    processedInChunk++
                    if (processedInChunk == targetCount) {
                        onComplete()
                    }
                }
        }
    }

    private fun checkCompletion(allBlogPosts: MutableList<BlogPost>, completedChunks: Int, totalChunks: Int) {
        if (completedChunks == totalChunks) {
            // All chunks completed
            Log.d("SavedArticlesActivity", "All chunks completed. Found ${allBlogPosts.size} blog posts")
            if (allBlogPosts.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                // Sort by timestamp (newest first)
                allBlogPosts.sortByDescending { it.timestamp }
                savedArticles.clear()
                savedArticles.addAll(allBlogPosts)
                blogPostAdapter.updateData(allBlogPosts)
            }
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
                // Update the blogPost object with new values
                val updatedBlogPost = blogPost.copy(
                    isLiked = !blogPost.isLiked,
                    likeCount = newLikeCount
                )
                
                // Update the list
                if (position < savedArticles.size) {
                    savedArticles[position] = updatedBlogPost
                    blogPostAdapter.updateSingleItem(position, updatedBlogPost)
                }
                
                val message = if (updatedBlogPost.isLiked) "Post liked!" else "Post unliked!"
                Toast.makeText(this@SavedArticlesActivity, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@SavedArticlesActivity, "Failed to update like", Toast.LENGTH_SHORT).show()
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
                // Since this is saved articles, unsaving should remove from list
                if (blogPost.isSaved) {
                    // Article was saved, now unsaved - remove from list
                    if (position < savedArticles.size) {
                        savedArticles.removeAt(position)
                        blogPostAdapter.updateData(savedArticles.toList())
                        
                        if (savedArticles.isEmpty()) {
                            showEmptyState()
                        }
                        
                        Toast.makeText(this@SavedArticlesActivity, "Article removed from saved!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // This shouldn't happen in saved articles context
                    val updatedBlogPost = blogPost.copy(isSaved = true)
                    if (position < savedArticles.size) {
                        savedArticles[position] = updatedBlogPost
                        blogPostAdapter.updateSingleItem(position, updatedBlogPost)
                    }
                }
            } else {
                Toast.makeText(this@SavedArticlesActivity, "Failed to update save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Show empty state
     */
    private fun showEmptyState() {
        tvEmptyState.visibility = android.view.View.VISIBLE
        rvSavedArticles.visibility = android.view.View.GONE
    }

    /**
     * Hide empty state
     */
    private fun hideEmptyState() {
        tvEmptyState.visibility = android.view.View.GONE
        rvSavedArticles.visibility = android.view.View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh saved articles when returning to this activity
        loadSavedArticles()
    }
}
