package com.icc.eserviceshelper.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.icc.eserviceshelper.R
import com.icc.eserviceshelper.databinding.ItemOrderBinding
import com.icc.eserviceshelper.models.Order
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val orders: List<Order>,
    private val onStatusChanged: (Order, String) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var expandedPosition = -1

    class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val context = holder.itemView.context
        val isExpanded = position == expandedPosition

        with(holder.binding) {
            tvCustomerName.text = order.userName
            tvServiceName.text = order.service
            tvSubserviceName.text = order.subservice
            tvStatus.text = order.status
            
            // Expansion Logic
            layoutDetails.visibility = if (isExpanded) View.VISIBLE else View.GONE
            ivExpand.rotation = if (isExpanded) 180f else 0f
            
            cardOrder.setOnClickListener {
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                val prevExpanded = expandedPosition
                val isCurrentlyExpanded = currentPosition == expandedPosition
                
                expandedPosition = if (isCurrentlyExpanded) -1 else currentPosition
                
                (holder.itemView.parent as? ViewGroup)?.let {
                    TransitionManager.beginDelayedTransition(it)
                }
                
                if (prevExpanded != -1) {
                    notifyItemChanged(prevExpanded)
                }
                if (expandedPosition != -1) {
                    notifyItemChanged(expandedPosition)
                }
            }

            // Set status background
            tvStatus.setBackgroundResource(when (order.status) {
                Order.STATUS_PENDING -> R.drawable.bg_status_pending
                Order.STATUS_PROCESSING -> R.drawable.bg_status_processing
                Order.STATUS_COMPLETED -> R.drawable.bg_status_completed
                Order.STATUS_CANCELLED -> R.drawable.bg_status_cancelled
                else -> R.drawable.bg_status_pending
            })

            if (isExpanded) {
                tvMobile.text = order.mobileNumber
                
                // Format date and time
                val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                tvDateTime.text = sdf.format(Date(order.timestamp))

                btnCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.mobileNumber}"))
                    context.startActivity(intent)
                }

                // Setup status dropdown
                val statuses = listOf(
                    Order.STATUS_PENDING,
                    Order.STATUS_PROCESSING,
                    Order.STATUS_COMPLETED,
                    Order.STATUS_CANCELLED
                )
                val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, statuses)
                actvStatus.setAdapter(adapter)
                actvStatus.setText(order.status, false)

                actvStatus.setOnItemClickListener { _, _, pos, _ ->
                    val newStatus = statuses[pos]
                    if (newStatus != order.status) {
                        onStatusChanged(order, newStatus)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = orders.size
}
