package com.trashbin.app.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.trashbin.app.R
import com.trashbin.app.data.model.WasteCategory
import com.trashbin.app.ui.adapters.CategoryAdapter

class CategoryBottomSheet(
    private val categories: List<WasteCategory>,
    private val onCategorySelected: (WasteCategory) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var mainLayout: LinearLayout
    private lateinit var titleText: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt()
            )
        }

        titleText = TextView(requireContext()).apply {
            text = "Pilih Kategori Sampah"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, (16 * resources.displayMetrics.density).toInt())
        }
        mainLayout.addView(titleText)

        recyclerView = RecyclerView(requireContext()).apply {
            id = View.generateViewId()
            layoutManager = LinearLayoutManager(requireContext())
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        mainLayout.addView(recyclerView)

        val adapter = CategoryAdapter(categories) { category ->
            onCategorySelected(category)
            dismiss()
        }
        
        recyclerView.adapter = adapter

        return mainLayout
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}