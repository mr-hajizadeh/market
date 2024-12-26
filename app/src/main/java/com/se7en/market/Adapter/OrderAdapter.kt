package com.se7en.market.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.OrderStatusActivity
import com.se7en.market.Model.CartDetailModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Utils.CartManager
import com.se7en.market.databinding.ViewHolderOrderListBinding

class OrderAdapter(private val items: MutableList<String>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private lateinit var context: Context

    inner class OrderViewHolder(binding: ViewHolderOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val orderDetailBtn = binding.orderDetailBtn
        private val orderProductView = binding.orderProductView
        fun bind(string: String) {
            val adapter = CartAdapter(0, CartManager)
            orderProductView.adapter = adapter
            adapter.setCartList(
                mutableListOf(
                    CartDetailModel(item = ProductModel(0)),
                    CartDetailModel(item = ProductModel(0)),
                    CartDetailModel(item = ProductModel(0)),
                    CartDetailModel(item = ProductModel(0)),
                    CartDetailModel(item = ProductModel(0)),
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        context = parent.context
        return OrderViewHolder(
            ViewHolderOrderListBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(items[position])
        holder.orderDetailBtn.setOnClickListener {
            context.startActivity(Intent(context, OrderStatusActivity::class.java))
        }
    }

}