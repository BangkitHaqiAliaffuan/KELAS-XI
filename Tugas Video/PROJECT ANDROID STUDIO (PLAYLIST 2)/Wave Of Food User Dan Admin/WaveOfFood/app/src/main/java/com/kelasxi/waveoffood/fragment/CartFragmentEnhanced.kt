package com.kelasxi.waveoffood.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.kelasxi.waveoffood.R
import com.kelasxi.waveoffood.adapter.CartAdapter
import com.kelasxi.waveoffood.model.CartItemModel
import com.kelasxi.waveoffood.model.FoodModel
import com.kelasxi.waveoffood.utils.CartManager

class CartFragmentEnhanced : Fragment(), CartManager.CartUpdateListener {
    
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var emptyCartLayout: View
    private lateinit var checkoutSection: CardView
    private lateinit var subtotalText: TextView
    private lateinit var deliveryFeeText: TextView
    private lateinit var totalPriceText: TextView
    private lateinit var checkoutButton: MaterialButton
    private lateinit var browseFoodButton: MaterialButton
    
    private lateinit var cartAdapter: CartAdapter
    
    private val deliveryFee = 2.99
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        
        // Register as listener for cart updates
        CartManager.addListener(this)
        updateCartDisplay()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        CartManager.removeListener(this)
    }
    
    override fun onCartUpdated() {
        updateCartDisplay()
    }
    
    private fun initializeViews(view: View) {
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        emptyCartLayout = view.findViewById(R.id.ll_empty_cart)
        checkoutSection = view.findViewById(R.id.cv_checkout_section)
        subtotalText = view.findViewById(R.id.tv_subtotal)
        deliveryFeeText = view.findViewById(R.id.tv_delivery_fee)
        totalPriceText = view.findViewById(R.id.tvTotalPrice)
        checkoutButton = view.findViewById(R.id.btnCheckout)
        browseFoodButton = view.findViewById(R.id.btn_browse_food)
    }
    
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            CartManager.getCartItems().toMutableList(),
            onQuantityChange = { cartItem, newQuantity ->
                CartManager.updateQuantity(cartItem, newQuantity)
            },
            onRemoveItem = { cartItem ->
                CartManager.removeFromCart(cartItem)
            }
        )
        
        cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }
    
    private fun setupClickListeners() {
        checkoutButton.setOnClickListener {
            proceedToCheckout()
        }
        
        browseFoodButton.setOnClickListener {
            // Navigate back to home fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragmentEnhanced())
                .addToBackStack(null)
                .commit()
        }
    }
    
    private fun updateCartDisplay() {
        val cartItems = CartManager.getCartItems()
        
        if (cartItems.isEmpty()) {
            showEmptyCart()
        } else {
            showCartWithItems()
            updatePriceCalculation()
        }
        
        // Update adapter data
        cartAdapter = CartAdapter(
            cartItems.toMutableList(),
            onQuantityChange = { cartItem, newQuantity ->
                CartManager.updateQuantity(cartItem, newQuantity)
            },
            onRemoveItem = { cartItem ->
                CartManager.removeFromCart(cartItem)
            }
        )
        cartRecyclerView.adapter = cartAdapter
    }
    
    private fun showEmptyCart() {
        cartRecyclerView.visibility = View.GONE
        checkoutSection.visibility = View.GONE
        emptyCartLayout.visibility = View.VISIBLE
    }
    
    private fun showCartWithItems() {
        cartRecyclerView.visibility = View.VISIBLE
        checkoutSection.visibility = View.VISIBLE
        emptyCartLayout.visibility = View.GONE
    }
    
    private fun updatePriceCalculation() {
        val cartItems = CartManager.getCartItems()
        val subtotal = cartItems.sumOf { it.calculateSubtotal() }
        val deliveryFeeInCents = (deliveryFee * 100).toLong()
        val total = subtotal + deliveryFeeInCents
        
        subtotalText.text = String.format("$%.2f", subtotal / 100.0)
        deliveryFeeText.text = String.format("$%.2f", deliveryFee)
        totalPriceText.text = String.format("$%.2f", total / 100.0)
    }
    
    private fun proceedToCheckout() {
        val cartItems = CartManager.getCartItems()
        if (cartItems.isNotEmpty()) {
            val total = CartManager.getCartTotal() + (deliveryFee * 100).toLong()
            Log.d("CartFragment", "Proceeding to checkout with total: Rp ${String.format("%,d", total)}")
            
            try {
                // Use simple checkout activity that won't crash
                val intent = android.content.Intent(context, com.kelasxi.waveoffood.CheckoutActivitySimple::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("CartFragment", "Error starting checkout", e)
                android.widget.Toast.makeText(
                    context, 
                    "Error opening checkout: ${e.message}", 
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            android.widget.Toast.makeText(
                context, 
                "Keranjang masih kosong", 
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    // Get cart item count for badge display
    fun getCartItemCount(): Int {
        return CartManager.getCartItemCount()
    }
    
    // Get cart total for display
    fun getCartTotal(): Double {
        return CartManager.getCartTotal() + deliveryFee
    }
}
