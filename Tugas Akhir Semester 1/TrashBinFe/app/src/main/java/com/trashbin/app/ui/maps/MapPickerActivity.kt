package com.trashbin.app.ui.maps

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.trashbin.app.R

class MapPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createLayout()
        setupUI()
    }
    
    private fun createLayout() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        
        val title = TextView(this).apply {
            text = "Map Picker"
            textSize = 24f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
        }
        
        val description = TextView(this).apply {
            text = "Redirecting to driver status..."
            textSize = 16f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 32
            }
        }
        
        layout.addView(title)
        layout.addView(description)
        
        setContentView(layout)
    }

    private fun setupUI() {
        // For the simplified version without maps, we'll redirect to driver status
        // after a location is selected or immediately if we're showing driver status
        
        // Redirect to driver status activity immediately
        val driverStatusIntent = Intent(this, DriverStatusActivity::class.java).apply {
            putExtra(DriverStatusActivity.EXTRA_DRIVER_NAME, "Bapak Suharto")
        }
        startActivity(driverStatusIntent)
        finish()
    }
}