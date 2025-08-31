package com.kelasxi.blogapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Activity utama yang menampilkan feed blog posts
 * Menggunakan RecyclerView untuk menampilkan daftar blog
 */
class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvBlogPosts: RecyclerView
    private lateinit var fabWriteBlog: FloatingActionButton

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
     * Setup RecyclerView dengan dummy data
     */
    private fun setupRecyclerView() {
        rvBlogPosts.layoutManager = LinearLayoutManager(this)
        
        // TODO: Create and set adapter with real data from Firebase
        // For now, we'll add some dummy data later
        
        // Create dummy blog posts for demonstration
        val dummyBlogPosts = createDummyBlogPosts()
        val adapter = BlogPostAdapter(dummyBlogPosts)
        rvBlogPosts.adapter = adapter
    }

    /**
     * Setup FAB click listener untuk navigasi ke write blog activity
     */
    private fun setupFabClickListener() {
        fabWriteBlog.setOnClickListener {
            val intent = Intent(this, WriteBlogActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Create dummy blog posts untuk testing UI
     * TODO: Replace dengan data dari Firebase
     */
    private fun createDummyBlogPosts(): List<BlogPost> {
        return listOf(
            BlogPost(
                id = "1",
                title = "Welcome to My Blog!",
                content = "This is my first blog post. I'm excited to share my thoughts and experiences with you all. Stay tuned for more interesting content!",
                authorName = "John Doe",
                authorId = "user1",
                timestamp = System.currentTimeMillis(),
                likeCount = 5,
                isLiked = false,
                isSaved = false
            ),
            BlogPost(
                id = "2",
                title = "10 Tips for Better Programming",
                content = "Programming is an art and science. Here are some tips that have helped me become a better developer over the years...",
                authorName = "Jane Smith",
                authorId = "user2",
                timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                likeCount = 12,
                isLiked = true,
                isSaved = false
            ),
            BlogPost(
                id = "3",
                title = "The Future of Mobile Development",
                content = "Mobile development is constantly evolving. With new frameworks and technologies emerging, it's an exciting time to be a mobile developer...",
                authorName = "Mike Johnson",
                authorId = "user3",
                timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                likeCount = 8,
                isLiked = false,
                isSaved = true
            )
        )
    }
}