package com.trashbin.app.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.trashbin.app.R

class CategoryBottomSheet(context: Context, private val categories: List<String>, private val onCategorySelected: (String) -> Unit) : BottomSheetDialog(context) {

    private lateinit var mainLayout: LinearLayout
    private lateinit var titleText: TextView
    private lateinit var rvCategories: RecyclerView

    init {
        setupUI()
    }

    private fun setupUI() {
        mainLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16 * context.resources.displayMetrics.density.toInt(), 
                16 * context.resources.displayMetrics.density.toInt(),
                16 * context.resources.displayMetrics.density.toInt(), 
                16 * context.resources.displayMetrics.density.toInt())
        }

        titleText = TextView(context).apply {
            text = "Pilih Kategori Sampah"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 16 * context.resources.displayMetrics.density.toInt())
        }
        mainLayout.addView(titleText)

        rvCategories = RecyclerView(context).apply {
            id = View.generateViewId()
            layoutManager = LinearLayoutManager(context)
            setPadding(0, (8 * context.resources.displayMetrics.density).toInt(), 0, 0)
            
            // Set adapter with categories
            // adapter = CategoryAdapter(categories) { category ->
            //     onCategorySelected(category)
            //     dismiss()
            // }
        }
        mainLayout.addView(rvCategories)

        setContentView(mainLayout)
    }
}