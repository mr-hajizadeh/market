package com.se7en.market.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.R
import com.se7en.market.databinding.ViewHolderOrderStatusBinding

class OrderStatusAdapter(private val statuses: List<String>, private val currentStep: Int) :
    RecyclerView.Adapter<OrderStatusAdapter.StatusViewHolder>() {

    private lateinit var context: Context

    inner class StatusViewHolder(binding: ViewHolderOrderStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val indicator: View = binding.indicator
        val statusText: TextView = binding.statusTxt
        val indicatorLayout: LinearLayout = binding.indicatorLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        context = parent.context
        return StatusViewHolder(
            ViewHolderOrderStatusBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.statusText.text = statuses[position]
        if (position == statuses.lastIndex)
            holder.indicatorLayout.visibility = View.GONE
        else
            holder.indicatorLayout.visibility = View.VISIBLE

        when {
            position < currentStep -> holder.indicator.setBackgroundResource(R.drawable.indicator_active)
            position == currentStep -> {
                holder.indicator.setBackgroundResource(R.drawable.indicator_current)
                holder.statusText.setTextColor(context.getColor(R.color.orange))
            }

            else -> {
                holder.indicator.setBackgroundResource(R.drawable.indicator_inactive)
                holder.statusText.setTextColor(context.getColor(R.color.light_gray))
            }
        }
    }

    override fun getItemCount(): Int = statuses.size
}