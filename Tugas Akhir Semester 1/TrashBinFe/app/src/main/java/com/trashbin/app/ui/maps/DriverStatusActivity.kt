package com.trashbin.app.ui.maps

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.trashbin.app.R

class DriverStatusActivity : AppCompatActivity() {

    // Views
    private lateinit var tvStatus: TextView
    private lateinit var tvDriverName: TextView
    private lateinit var ivStatus: ImageView
    private lateinit var btnBack: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var currentStatus = 0
    private lateinit var timer: CountDownTimer

    companion object {
        const val EXTRA_PICKUP_ID = "pickup_id"
        const val EXTRA_DRIVER_NAME = "driver_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createLayout()
        setupUI()
        startDriverStatusUpdates()
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
            gravity = Gravity.CENTER_VERTICAL
        }

        tvDriverName = TextView(this).apply {
            text = "Driver: Loading..."
            textSize = 18f
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        ivStatus = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = 24
            }
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        tvStatus = TextView(this).apply {
            text = "Loading status..."
            textSize = 16f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        progressBar = ProgressBar(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = 24
            }
            visibility = View.GONE
        }

        btnBack = MaterialButton(this).apply {
            text = "Kembali"
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
            }
        }

        layout.addView(tvDriverName)
        layout.addView(ivStatus)
        layout.addView(tvStatus)
        layout.addView(progressBar)
        layout.addView(btnBack)

        setContentView(layout)
    }

    private fun setupUI() {
        // Set initial status - driver is heading to customer
        tvStatus.text = "Driver sedang menuju ke rumah Anda"
        tvDriverName.text = "Driver: ${intent.getStringExtra(EXTRA_DRIVER_NAME) ?: "Bapak Suharto"}"
        ivStatus.setImageResource(android.R.drawable.ic_menu_directions)
        
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun startDriverStatusUpdates() {
        // Simulate driver status updates using a timer
        timer = object : CountDownTimer(30000, 5000) { // 30 seconds total, updates every 5 seconds
            override fun onTick(millisUntilFinished: Long) {
                updateStatus()
            }

            override fun onFinish() {
                // After 30 seconds, driver has completed the route
                tvStatus.text = "Driver telah selesai mengangkut sampah"
                ivStatus.setImageResource(android.R.drawable.checkbox_on_background)
            }
        }.start()
    }

    private fun updateStatus() {
        currentStatus++
        when (currentStatus) {
            1 -> {
                tvStatus.text = "Driver telah sampai di rumah Anda"
                ivStatus.setImageResource(android.R.drawable.ic_menu_mylocation)
            }
            2 -> {
                tvStatus.text = "Driver sedang memuat sampah"
                ivStatus.setImageResource(android.R.drawable.ic_menu_delete)
                progressBar.visibility = View.VISIBLE
            }
            3 -> {
                tvStatus.text = "Driver sedang menuju tempat pembuangan"
                ivStatus.setImageResource(android.R.drawable.ic_menu_directions)
            }
            4 -> {
                tvStatus.text = "Driver sedang membuang sampah"
                ivStatus.setImageResource(android.R.drawable.ic_menu_rotate)
            }
            5 -> {
                tvStatus.text = "Proses pengambilan sampah selesai"
                ivStatus.setImageResource(android.R.drawable.checkbox_on_background)
            }
            6 -> {
                tvStatus.text = "Driver kembali ke base"
                ivStatus.setImageResource(android.R.drawable.ic_menu_myplaces)
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        if (::timer.isInitialized) {
            timer.cancel()
        }
        super.onDestroy()
    }
}