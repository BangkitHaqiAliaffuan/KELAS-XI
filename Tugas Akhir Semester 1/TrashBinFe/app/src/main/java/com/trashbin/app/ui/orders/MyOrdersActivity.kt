package com.trashbin.app.ui.orders

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trashbin.app.R
import com.trashbin.app.data.api.RetrofitClient
import com.trashbin.app.data.model.Order
import com.trashbin.app.data.repository.OrderRepository
import com.trashbin.app.ui.viewmodel.OrderViewModel
import com.trashbin.app.ui.viewmodel.OrderViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MyOrdersActivity : AppCompatActivity() {

    private lateinit var viewModel: OrderViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var spinnerFilter: Spinner
    private lateinit var adapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupViewModel()
        setupUI()
        setupObservers()
        loadOrders()
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.apiService
        val repository = OrderRepository(apiService)
        val factory = OrderViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
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

        val statuses = arrayOf("All", "Pending", "Confirmed", "Shipped", "Completed", "Cancelled")
        spinnerFilter.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = if (position == 0) null else statuses[position].lowercase()
                loadOrders(status)
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
            layoutManager = LinearLayoutManager(this@MyOrdersActivity)
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

            addView(TextView(this@MyOrdersActivity).apply {
                text = "📦"
                textSize = 48f
                gravity = Gravity.CENTER
            })

            addView(TextView(this@MyOrdersActivity).apply {
                text = "Tidak ada pesanan"
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
            val btnBack = ImageButton(this@MyOrdersActivity).apply {
                setImageResource(R.drawable.ic_arrow_back)
                setBackgroundColor(Color.TRANSPARENT)
                layoutParams = LinearLayout.LayoutParams(dp(40), dp(40)).apply {
                    gravity = Gravity.CENTER_VERTICAL
                }
                setOnClickListener { finish() }
            }
            addView(btnBack)

            // Title
            val title = TextView(this@MyOrdersActivity).apply {
                text = "My Orders"
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

        viewModel.orders.observe(this) { result ->
            when (result) {
                is com.trashbin.app.data.repository.RepositoryResult.Success -> {
                    val orders = result.data
                    Log.d("MyOrdersActivity", "Orders loaded: ${orders.size}")
                    displayOrders(orders)
                }
                is com.trashbin.app.data.repository.RepositoryResult.Error -> {
                    Log.e("MyOrdersActivity", "Error loading orders", Exception(result.message))
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                    showEmptyView()
                }
                is com.trashbin.app.data.repository.RepositoryResult.Loading -> {
                    // Handle loading state if needed
                }
            }
        }

        viewModel.orderAction.observe(this) { result ->
            when (result) {
                is com.trashbin.app.data.repository.RepositoryResult.Success -> {
                    val order = result.data
                    Toast.makeText(this, "Order updated successfully", Toast.LENGTH_SHORT).show()
                    loadOrders() // Reload orders
                }
                is com.trashbin.app.data.repository.RepositoryResult.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is com.trashbin.app.data.repository.RepositoryResult.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    private fun loadOrders(status: String? = null) {
        Log.d("MyOrdersActivity", "Loading orders with status: $status")
        viewModel.loadOrders("buyer", status)
    }

    private fun displayOrders(orders: List<Order>) {
        if (orders.isEmpty()) {
            showEmptyView()
        } else {
            adapter = OrdersAdapter(orders) { order, action ->
                handleOrderAction(order, action)
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

    private fun handleOrderAction(order: Order, action: String) {
        when (action) {
            "complete" -> viewModel.completeOrder(order.id)
            "view_detail" -> showOrderDetail(order)
        }
    }

    private fun showOrderDetail(order: Order) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Order Detail")
            .setMessage(buildOrderDetailMessage(order))
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }

    private fun buildOrderDetailMessage(order: Order): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        
        var orderDateStr = "N/A"
        try {
            val orderDate = inputFormat.parse(order.createdAt)
            orderDateStr = if (orderDate != null) outputFormat.format(orderDate) else "N/A"
        } catch (e: Exception) {
            Log.e("MyOrdersActivity", "Error parsing order date: ${order.createdAt}", e)
        }

        return """
            Order ID: #${order.id}
            
            Product: ${order.listing.title}
            Quantity: ${order.quantity} ${order.listing.unit}
            Total: ${currencyFormat.format(order.totalPrice)}
            
            Shipping Address:
            ${order.shippingAddress}
            
            Status: ${order.status.uppercase()}
            
            ${if (order.notes != null) "Notes:\n${order.notes}\n\n" else ""}
            Seller: ${order.seller.name}
            Phone: ${order.seller.phone}
            
            Order Date: $orderDateStr
        """.trimIndent()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

// Orders Adapter
class OrdersAdapter(
    private val orders: List<Order>,
    private val onAction: (Order, String) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(val view: LinearLayout) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val context = parent.context
        val dp = { value: Int -> (value * context.resources.displayMetrics.density).toInt() }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        return OrderViewHolder(layout)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val context = holder.view.context
        val dp = { value: Int -> (value * context.resources.displayMetrics.density).toInt() }
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        holder.view.removeAllViews()

        // Order ID and Status
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
            text = "Order #${order.id}"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })

        headerLayout.addView(TextView(context).apply {
            text = order.status.uppercase()
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), dp(4), dp(8), dp(4))
            setBackgroundColor(getStatusColor(order.status))
            setTextColor(Color.WHITE)
        })

        holder.view.addView(headerLayout)

        // Product info
        holder.view.addView(TextView(context).apply {
            text = order.listing.title
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(4)
            }
        })

        holder.view.addView(TextView(context).apply {
            text = "Quantity: ${order.quantity} ${order.listing.unit}"
            textSize = 12f
            setTextColor(Color.GRAY)
        })

        holder.view.addView(TextView(context).apply {
            text = "Total: ${currencyFormat.format(order.totalPrice)}"
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setTextColor(context.resources.getColor(R.color.primary))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
                bottomMargin = dp(8)
            }
        })

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
                onAction(order, "view_detail")
            }
        })

        if (order.status == "shipped") {
            buttonLayout.addView(Button(context).apply {
                text = "Mark Complete"
                textSize = 12f
                setPadding(dp(16), dp(8), dp(16), dp(8))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    leftMargin = dp(8)
                }
                setBackgroundColor(context.resources.getColor(R.color.primary))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    onAction(order, "complete")
                }
            })
        }

        holder.view.addView(buttonLayout)

        holder.view.setOnClickListener {
            onAction(order, "view_detail")
        }
    }

    override fun getItemCount() = orders.size

    private fun getStatusColor(status: String): Int {
        return when (status.lowercase()) {
            "pending" -> Color.parseColor("#FFA500")
            "confirmed" -> Color.parseColor("#2196F3")
            "shipped" -> Color.parseColor("#9C27B0")
            "completed" -> Color.parseColor("#4CAF50")
            "cancelled" -> Color.parseColor("#F44336")
            else -> Color.GRAY
        }
    }
}
