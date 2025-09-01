package com.kelasxi.blogapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Adapter untuk RecyclerView yang menampilkan daftar blog posts
 * Menangani binding data ke view dan click events
 */
class BlogPostAdapter(
    private var blogPosts: List<BlogPost>
) : RecyclerView.Adapter<BlogPostAdapter.BlogPostViewHolder>() {

    // Interface untuk handle click events
    interface OnBlogPostClickListener {
        fun onBlogPostClick(blogPost: BlogPost)
        fun onLikeClick(blogPost: BlogPost, position: Int)
        fun onSaveClick(blogPost: BlogPost, position: Int)
        fun onMoreClick(blogPost: BlogPost, position: Int)
        fun onAuthorClick(blogPost: BlogPost)
    }

    private var clickListener: OnBlogPostClickListener? = null

    /**
     * Set click listener
     */
    fun setOnBlogPostClickListener(listener: OnBlogPostClickListener) {
        this.clickListener = listener
    }

    /**
     * Update data dan refresh RecyclerView
     */
    fun updateData(newBlogPosts: List<BlogPost>) {
        this.blogPosts = newBlogPosts
        notifyDataSetChanged()
    }

    /**
     * Update single item di posisi tertentu
     */
    fun updateSingleItem(position: Int, updatedBlogPost: BlogPost) {
        if (position >= 0 && position < blogPosts.size) {
            val mutableList = blogPosts.toMutableList()
            mutableList[position] = updatedBlogPost
            this.blogPosts = mutableList
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blog_post, parent, false)
        return BlogPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogPostViewHolder, position: Int) {
        val blogPost = blogPosts[position]
        holder.bind(blogPost, position)
    }

    override fun getItemCount(): Int = blogPosts.size

    /**
     * ViewHolder class untuk blog post item
     */
    inner class BlogPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        // UI Components
        private val ivAuthorProfile: CircleImageView = itemView.findViewById(R.id.ivAuthorProfile)
        private val tvAuthorName: TextView = itemView.findViewById(R.id.tvAuthorName)
        private val tvPostTime: TextView = itemView.findViewById(R.id.tvPostTime)
        private val btnMore: MaterialButton = itemView.findViewById(R.id.btnMore)
        private val tvBlogTitle: TextView = itemView.findViewById(R.id.tvBlogTitle)
        private val tvBlogContent: TextView = itemView.findViewById(R.id.tvBlogContent)
        private val btnLike: MaterialButton = itemView.findViewById(R.id.btnLike)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        private val btnSave: MaterialButton = itemView.findViewById(R.id.btnSave)
        private val tvReadMore: TextView = itemView.findViewById(R.id.tvReadMore)

        /**
         * Bind data ke views
         */
        fun bind(blogPost: BlogPost, position: Int) {
            // Set basic information
            tvAuthorName.text = blogPost.authorName
            tvPostTime.text = blogPost.getFormattedTime()
            tvBlogTitle.text = blogPost.title
            tvBlogContent.text = blogPost.getPreviewContent()
            tvLikeCount.text = blogPost.likeCount.toString()

            // Set like button state
            if (blogPost.isLiked) {
                btnLike.setIconResource(android.R.drawable.btn_star_big_on)
            } else {
                btnLike.setIconResource(android.R.drawable.btn_star_big_off)
            }

            // Set save button state
            if (blogPost.isSaved) {
                btnSave.setIconResource(android.R.drawable.ic_menu_save)
                // TODO: Change to filled save icon
            } else {
                btnSave.setIconResource(android.R.drawable.ic_menu_save)
            }

            // Load author profile image with ProfileImageHelper
            ProfileImageHelper.loadProfileImage(
                ivAuthorProfile,
                blogPost.authorProfileUrl,
                R.drawable.ic_launcher_foreground
            )

            // Set click listeners
            setupClickListeners(blogPost, position)
        }

        /**
         * Setup click listeners untuk berbagai UI elements
         */
        private fun setupClickListeners(blogPost: BlogPost, position: Int) {
            // Main blog post click
            itemView.setOnClickListener {
                clickListener?.onBlogPostClick(blogPost)
            }

            // Read more click
            tvReadMore.setOnClickListener {
                clickListener?.onBlogPostClick(blogPost)
            }

            // Like button click
            btnLike.setOnClickListener {
                clickListener?.onLikeClick(blogPost, position)
            }

            // Save button click
            btnSave.setOnClickListener {
                clickListener?.onSaveClick(blogPost, position)
            }

            // More options click
            btnMore.setOnClickListener {
                clickListener?.onMoreClick(blogPost, position)
            }

            // Author profile click
            ivAuthorProfile.setOnClickListener {
                clickListener?.onAuthorClick(blogPost)
            }

            tvAuthorName.setOnClickListener {
                clickListener?.onAuthorClick(blogPost)
            }
        }
    }
}
