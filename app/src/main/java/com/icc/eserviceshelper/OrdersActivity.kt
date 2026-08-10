package com.icc.eserviceshelper

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.icc.eserviceshelper.adapters.OrderAdapter
import com.icc.eserviceshelper.databinding.ActivityOrdersBinding
import com.icc.eserviceshelper.repository.FirebaseRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding
    private val repository = FirebaseRepository()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        setupToolbar()
        setupRecyclerView()
        observeOrders()

        binding.swipeRefresh.setOnRefreshListener {
            observeOrders()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.inflateMenu(R.menu.menu_orders)
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_logout) {
                auth.signOut()
                finish()
                true
            } else {
                false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this)
    }

    private fun observeOrders() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            repository.getOrders().collectLatest { result ->
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                
                result.onSuccess { orders ->
                    if (orders.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.recyclerViewOrders.visibility = View.GONE
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.recyclerViewOrders.visibility = View.VISIBLE
                        binding.recyclerViewOrders.adapter = OrderAdapter(orders) { order, newStatus ->
                            updateStatus(order.id, newStatus)
                        }
                    }
                }.onFailure {
                    Toast.makeText(this@OrdersActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateStatus(orderId: String, newStatus: String) {
        lifecycleScope.launch {
            val result = repository.updateOrderStatus(orderId, newStatus)
            if (result.isFailure) {
                Toast.makeText(this@OrdersActivity, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
