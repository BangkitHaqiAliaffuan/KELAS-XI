package com.kelasxi.blogapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

/**
 * Adapter untuk RecyclerView yang menampilkan daftar blog posts milik user
 * Menyertakan fungsi edit dan delete
 */
class UserBlogPostAdapter(
    private var blogPosts: List<BlogPost>
) : RecyclerView.Adapter<UserBlogPostAdapter.UserBlogPostViewHolder>() {

    // Interface untuk handle click events
    interface OnUserBlogPostClickListener {
        fun onBlogPostClick(blogPost: BlogPost)
        fun onEditClick(blogPost: BlogPost)
        fun onDeleteClick(blogPost: BlogPost)
        fun onMoreClick(blogPost: BlogPost)
    }

    private var clickListener: OnUserBlogPostClickListener? = null

    /**
     * Set click listener
     */
    fun setOnUserBlogPostClickListener(listener: OnUserBlogPostClickListener) {
        this.clickListener = listener
    }

    /**
     * Update data dan refresh RecyclerView
     */
    fun updateData(newBlogPosts: List<BlogPost>) {
        this.blogPosts = newBlogPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBlogPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_blog_post, parent, false)
        return UserBlogPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserBlogPostViewHolder, position: Int) {
        val blogPost = blogPosts[position]
        holder.bind(blogPost)
    }

    override fun getItemCount(): Int = blogPosts.size

    /**
     * ViewHolder class untuk user blog post item
     */
    inner class UserBlogPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        // UI Components
        private val tvPostTime: TextView = itemView.findViewById(R.id.tvPostTime)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        private val tvBlogTitle: TextView = itemView.findViewById(R.id.tvBlogTitle)
        private val tvBlogContent: TextView = itemView.findViewById(R.id.tvBlogContent)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        private val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
        private val tvReadMore: TextView = itemView.findViewById(R.id.tvReadMore)

        /**
         * Bind data ke views
         */
        fun bind(blogPost: BlogPost) {
            // Set basic information
            tvPostTime.text = blogPost.getFormattedTime()
            tvBlogTitle.text = blogPost.title
            tvBlogContent.text = blogPost.getPreviewContent()
            tvLikeCount.text = blogPost.likeCount.toString()

            // Set click listeners
            setupClickListeners(blogPost)
        }

        /**
         * Setup click listeners untuk berbagai UI elements
         */
        private fun setupClickListeners(blogPost: BlogPost) {
            // Main blog post click
            itemView.setOnClickListener {
                clickListener?.onBlogPostClick(blogPost)
            }

            // Read more click
            tvReadMore.setOnClickListener {
                clickListener?.onBlogPostClick(blogPost)
            }

            // Edit button click
            btnEdit.setOnClickListener {
                clickListener?.onEditClick(blogPost)
            }

            // Delete button click
            btnDelete.setOnClickListener {
                clickListener?.onDeleteClick(blogPost)
            }

            // More options click
            btnMore.setOnClickListener {
                clickListener?.onMoreClick(blogPost)
            }
        }
    }
}
