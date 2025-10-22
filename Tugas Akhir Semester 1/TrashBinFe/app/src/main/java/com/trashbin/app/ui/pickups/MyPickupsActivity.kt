package com.trashbin.app.ui.pickups

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trashbin.app.R
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.model.PickupResponse
import com.trashbin.app.data.repository.PickupRepository
import com.trashbin.app.data.repository.RepositoryResult
import com.trashbin.app.ui.viewmodel.PickupViewModel
import com.trashbin.app.ui.viewmodel.PickupViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MyPickupsActivity : AppCompatActivity() {

    private lateinit var viewModel: PickupViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var spinnerFilter: Spinner
    private lateinit var adapter: PickupsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupViewModel()
        setupUI()
        setupObservers()
        loadPickups()
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.apiService
        val repository = PickupRepository(apiService)
        val factory = PickupViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PickupViewModel::class.java]
    }

    private fun setupUI() {
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }

        // Header
        val header = createHeader()
        mainLayout.addView(header)

        // Filter spinner
        val filterLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(16), dp(8), dp(16), dp(8))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val filterLabel = TextView(this).apply {
            text = "Filter: "
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
        }

        spinnerFilter = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val statuses = arrayOf("All", "Pending", "Accepted", "On The Way", "Arrived", "Completed", "Cancelled")
        spinnerFilter.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = if (position == 0) null else statuses[position].lowercase().replace(" ", "_")
                loadPickups(status)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        filterLayout.addView(filterLabel)
        filterLayout.addView(spinnerFilter)
        mainLayout.addView(filterLayout)

        // Progress bar
        progressBar = ProgressBar(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dp(24)
            }
            visibility = View.GONE
        }
        mainLayout.addView(progressBar)

        // RecyclerView
        recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            layoutManager = LinearLayoutManager(this@MyPickupsActivity)
            visibility = View.GONE
        }
        mainLayout.addView(recyclerView)

        // Empty view
        emptyView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            visibility = View.GONE

            addView(TextView(this@MyPickupsActivity).apply {
                text = "🗑️"
                textSize = 48f
                gravity = Gravity.CENTER
            })

            addView(TextView(this@MyPickupsActivity).apply {
                text = "Tidak ada pickup request"
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(0, dp(16), 0, 0)
            })
        }
        mainLayout.addView(emptyView)

        setContentView(mainLayout)
    }

    private fun createHeader(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            setBackgroundColor(resources.getColor(R.color.primary))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Back button
            val btnBack = ImageButton(this@MyPickupsActivity).apply {
                setImageResource(R.drawable.ic_arrow_back)
                setBackgroundColor(Color.TRANSPARENT)
                layoutParams = LinearLayout.LayoutParams(dp(40), dp(40)).apply {
                    gravity = Gravity.CENTER_VERTICAL
                }
                setOnClickListener { finish() }
            }
            addView(btnBack)

            // Title
            val title = TextView(this@MyPickupsActivity).apply {
                text = "My Pickups"
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    gravity = Gravity.CENTER_VERTICAL
                    leftMargin = dp(16)
                }
            }
            addView(title)
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.pickups.observe(this) { result ->
            when (result) {
                is RepositoryResult.Loading -> {
                    // Tidak perlu tindakan khusus di sini karena isLoading sudah menangani
                }
                is RepositoryResult.Success -> {
                    val pickups = result.data
                    Log.d("MyPickupsActivity", "Pickups loaded: ${pickups.size}")
                    displayPickups(pickups)
                }
                is RepositoryResult.Error -> {
                    Log.e("MyPickupsActivity", "Error loading pickups", Exception(result.message))
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                    showEmptyView()
                }
            }
        }

        viewModel.pickupAction.observe(this) { result ->
            when (result) {
                is RepositoryResult.Success -> {
                    Toast.makeText(this, "Pickup cancelled successfully", Toast.LENGTH_SHORT).show()
                    loadPickups() // Reload pickups
                }
                is RepositoryResult.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun loadPickups(status: String? = null) {
        Log.d("MyPickupsActivity", "Loading pickups with status: $status")
        viewModel.loadPickups(status)
    }

    private fun displayPickups(pickups: List<PickupResponse>) {
        if (pickups.isEmpty()) {
            showEmptyView()
        } else {
            adapter = PickupsAdapter(pickups) { pickup, action ->
                handlePickupAction(pickup, action)
            }
            recyclerView.adapter = adapter
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    private fun showEmptyView() {
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
    }

    private fun handlePickupAction(pickup: PickupResponse, action: String) {
        when (action) {
            "cancel" -> showCancelDialog(pickup)
            "view_detail" -> showPickupDetail(pickup)
        }
    }

    private fun showCancelDialog(pickup: PickupResponse) {
        val input = EditText(this).apply {
            hint = "Alasan pembatalan"
        }

        android.app.AlertDialog.Builder(this)
            .setTitle("Cancel Pickup")
            .setMessage("Apakah Anda yakin ingin membatalkan pickup ini?")
            .setView(input)
            .setPositiveButton("Cancel Pickup") { _, _ ->
                val reason = input.text.toString()
                if (reason.isNotBlank()) {
                    viewModel.cancelPickup(pickup.id, reason)
                } else {
                    Toast.makeText(this, "Alasan pembatalan harus diisi", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Back", null)
            .show()
    }

    private fun showPickupDetail(pickup: PickupResponse) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Pickup Detail")
            .setMessage(buildPickupDetailMessage(pickup))
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }

    private fun buildPickupDetailMessage(pickup: PickupResponse): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        val itemsInfo = pickup.items.joinToString("\n") { item ->
            "- ${item.category.name}: ${item.estimatedWeight} kg" +
            if (item.actualWeight != null) " (Actual: ${item.actualWeight} kg)" else ""
        }

        return """
            Pickup ID: #${pickup.id}
            
            Address:
            ${pickup.address}
            
            Scheduled Date:
            ${dateFormat.format(Date(pickup.scheduledDate))}
            
            Items:
            $itemsInfo
            
            Status: ${pickup.status.uppercase().replace("_", " ")}
            
            ${if (pickup.actualWeight != null) "Total Weight: ${pickup.actualWeight} kg\n" else ""}
            ${if (pickup.totalPrice != null) "Total Price: ${currencyFormat.format(pickup.totalPrice)}\n" else ""}
            ${if (pickup.notes != null) "\nNotes:\n${pickup.notes}\n" else ""}
            ${if (pickup.collector != null) "\nCollector: ${pickup.collector.name}\nPhone: ${pickup.collector.phone}\n" else ""}
            ${if (pickup.cancellationReason != null) "\nCancellation Reason:\n${pickup.cancellationReason}" else ""}
        """.trimIndent()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

// Pickups Adapter
class PickupsAdapter(
    private val pickups: List<PickupResponse>,
    private val onAction: (PickupResponse, String) -> Unit
) : RecyclerView.Adapter<PickupsAdapter.PickupViewHolder>() {

    class PickupViewHolder(itemView: LinearLayout) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PickupViewHolder {
        val context = parent.context
        val dp = { value: Int -> (value * context.resources.displayMetrics.density).toInt() }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            layoutParams = androidx.recyclerview.widget.RecyclerView.LayoutParams(
                androidx.recyclerview.widget.RecyclerView.LayoutParams.MATCH_PARENT,
                androidx.recyclerview.widget.RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        return PickupViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PickupViewHolder, position: Int) {
        val pickup = pickups[position]
        val context = holder.itemView.context
        val holderLayout = holder.itemView as LinearLayout
        val dp = { value: Int -> (value * context.resources.displayMetrics.density).toInt() }
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        holderLayout.removeAllViews()

        // Pickup ID and Status
        val headerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
        }

        headerLayout.addView(TextView(context).apply {
            text = "Pickup #${pickup.id}"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })

        headerLayout.addView(TextView(context).apply {
            text = pickup.status.uppercase().replace("_", " ")
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), dp(4), dp(8), dp(4))
            setBackgroundColor(getStatusColor(pickup.status))
            setTextColor(Color.WHITE)
        })

        holderLayout.addView(headerLayout)

        // Scheduled date
        holderLayout.addView(TextView(context).apply {
            text = "📅 ${dateFormat.format(Date(pickup.scheduledDate))}"
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(4)
            }
        })

        // Address
        holderLayout.addView(TextView(context).apply {
            text = "📍 ${pickup.address}"
            textSize = 12f
            setTextColor(Color.GRAY)
            maxLines = 2
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(4)
            }
        })

        // Items count
        holderLayout.addView(TextView(context).apply {
            text = "Items: ${pickup.items.size}"
            textSize = 12f
            setTextColor(Color.GRAY)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
        })

        // Collector info (if assigned)
        if (pickup.collector != null) {
            holderLayout.addView(TextView(context).apply {
                text = "👤 Collector: ${pickup.collector.name}"
                textSize = 12f
                setTextColor(context.resources.getColor(R.color.primary))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(8)
                }
            })
        }

        // Action buttons
        val buttonLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
        }

        buttonLayout.addView(Button(context).apply {
            text = "Detail"
            textSize = 12f
            setPadding(dp(16), dp(8), dp(16), dp(8))
            setOnClickListener {
                onAction(pickup, "view_detail")
            }
        })

        if (pickup.status == "pending") {
            buttonLayout.addView(Button(context).apply {
                text = "Cancel"
                textSize = 12f
                setPadding(dp(16), dp(8), dp(16), dp(8))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    leftMargin = dp(8)
                }
                setBackgroundColor(Color.parseColor("#F44336"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    onAction(pickup, "cancel")
                }
            })
        }

        holderLayout.addView(buttonLayout)

        holderLayout.setOnClickListener {
            onAction(pickup, "view_detail")
        }
    }

    override fun getItemCount() = pickups.size

    private fun getStatusColor(status: String): Int {
        return when (status.lowercase()) {
            "pending" -> Color.parseColor("#FFA500")
            "accepted" -> Color.parseColor("#2196F3")
            "on_the_way" -> Color.parseColor("#9C27B0")
            "arrived" -> Color.parseColor("#FF9800")
            "completed" -> Color.parseColor("#4CAF50")
            "cancelled" -> Color.parseColor("#F44336")
            else -> Color.GRAY
        }
    }
}
