package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.OrderStatusActivity
import com.se7en.market.Model.OrderModel
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.Utils.Constance.Companion.KEY_ORDER_ID
import com.se7en.market.Utils.Helper
import com.se7en.market.databinding.ViewHolderOrderListBinding
import zabih.lib.zipersiancalendar.ZiPersianCalendar

class OrderAdapter(private val items: MutableList<OrderModel>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private lateinit var context: Context
    private lateinit var persianCalendar: ZiPersianCalendar
    private lateinit var helper: Helper
    private val statusColors = mapOf(
        "در انتظار پرداخت" to Pair("#4DFF9800", "#FF9800"),  // نارنجی
        "پرداخت شده" to Pair("#4D4CAF50", "#4CAF50"),       // سبز
        "در حال پردازش" to Pair("#4DFFC107", "#FFC107"),    // زرد کهربایی
        "ارسال شده" to Pair("#4D2196F3", "#2196F3"),        // آبی
        "تحویل داده شده" to Pair("#4D8BC34A", "#8BC34A"),   // سبز روشن
        "لغو شده" to Pair("#4DF44336", "#F44336"),          // قرمز
        "ناموفق" to Pair("#4DE91E63", "#E91E63"),          // قرمز تیره
        "بازگشت داده شده" to Pair("#4D9C27B0", "#9C27B0")   // بنفش
    )

    inner class OrderViewHolder(binding: ViewHolderOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val orderDetailBtn = binding.orderDetailBtn
        private val orderProductView = binding.orderProductView
        private val shopName = binding.shopNameTxt
        private val orderStatus = binding.orderStatusTxt
        private val submitOrderDate = binding.submitOrderDateTxt
        private val distanceAddress = binding.distanceAddressTxt
        private val totalFinance = binding.totalFinanceTxt

        @SuppressLint("SetTextI18n")
        fun bind(order: OrderModel) {
            persianCalendar.timeInMillis = order.getCreatedAtTimestamp()!!
            orderStatus.background =
                helper.createCustomShape(
                    Color.parseColor(statusColors[order.status_title]?.first),
                    8f
                )
            orderStatus.setTextColor(Color.parseColor(statusColors[order.status_title]?.second))
            orderStatus.text = order.status_title
            shopName.text = " - ${order.seller.shop_name}"
            submitOrderDate.text = persianCalendar.longDate
            distanceAddress.text = order.destination_address
            totalFinance.text =
                "جمع فاکتور: ${order.formatPriceToPersian()} ${context.getString(R.string.title_currency)}"
            val adapter = CartAdapter(0, CartManager)
            orderProductView.adapter = adapter
            adapter.setCartList(order.order_details)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        context = parent.context
        persianCalendar = ZiPersianCalendar()
        helper = Helper(context)
        return OrderViewHolder(
            ViewHolderOrderListBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = items[position]
        holder.bind(order)
        holder.orderDetailBtn.setOnClickListener {
            val intent = Intent(context, OrderStatusActivity::class.java)
            intent.putExtra(KEY_ORDER_ID, order.id)
            context.startActivity(intent)
        }
    }

}