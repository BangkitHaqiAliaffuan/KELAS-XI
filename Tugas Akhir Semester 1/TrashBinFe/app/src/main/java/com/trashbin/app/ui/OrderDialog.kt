package com.trashbin.app.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trashbin.app.R

class OrderDialog(context: Context, private val onOrderConfirmed: (Int) -> Unit) : Dialog(context) {

    private lateinit var mainLayout: LinearLayout
    private lateinit var titleText: TextView
    private lateinit var quantityInputLayout: TextInputLayout
    private lateinit var quantityEditText: TextInputEditText
    private lateinit var btnCancel: Button
    private lateinit var btnOrder: Button

    init {
        setupUI()
    }

    private fun setupUI() {
        mainLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24 * context.resources.displayMetrics.density.toInt(), 
                24 * context.resources.displayMetrics.density.toInt(),
                24 * context.resources.displayMetrics.density.toInt(), 
                24 * context.resources.displayMetrics.density.toInt())
        }

        titleText = TextView(context).apply {
            text = "Buat Pesanan"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 16 * context.resources.displayMetrics.density.toInt())
        }
        mainLayout.addView(titleText)

        quantityInputLayout = TextInputLayout(context).apply {
            hint = "Jumlah"
        }
        quantityEditText = TextInputEditText(context).apply {
            id = View.generateViewId()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            hint = "1"
        }
        quantityInputLayout.addView(quantityEditText)
        mainLayout.addView(quantityInputLayout)

        val buttonLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, (16 * context.resources.displayMetrics.density).toInt(), 0, 0)
        }

        btnCancel = Button(context).apply {
            text = "Batal"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = (8 * context.resources.displayMetrics.density).toInt()
            }
        }
        buttonLayout.addView(btnCancel)

        btnOrder = Button(context).apply {
            text = "Pesan"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (8 * context.resources.displayMetrics.density).toInt()
            }
        }
        buttonLayout.addView(btnOrder)

        mainLayout.addView(buttonLayout)

        setContentView(mainLayout)

        // Set click listeners
        btnCancel.setOnClickListener {
            dismiss()
        }
        
        btnOrder.setOnClickListener {
            val quantity = try {
                quantityEditText.text.toString().toInt()
            } catch (e: NumberFormatException) {
                1 // default to 1 if input is invalid
            }
            onOrderConfirmed(quantity)
            dismiss()
        }
    }
}