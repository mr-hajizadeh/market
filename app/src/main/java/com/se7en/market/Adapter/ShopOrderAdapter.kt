package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.CartActivity
import com.se7en.market.Model.ShopOrderModel
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.databinding.ViewHolderCartListBinding

class ShopOrderAdapter(
    private val items: MutableList<ShopOrderModel>,
    private val cartCount: (count: Int) -> Unit
) : RecyclerView.Adapter<ShopOrderAdapter.ShopHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopHolder {
        context = parent.context
        return ShopHolder(
            ViewHolderCartListBinding.inflate(
                LayoutInflater.from(context),
                parent, false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ShopHolder, position: Int) {
        cartCount(itemCount)
        holder.bind(items[position])

        holder.continueBtn.setOnClickListener {
            context.startActivity(Intent(context, CartActivity::class.java))
        }
    }

    class ShopHolder(private val binding: ViewHolderCartListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val continueBtn = binding.continueBtn
        val context: Context = binding.root.context
        val progressBar = binding.minPriceProgress

        @SuppressLint("SetTextI18n")
        fun bind(item: ShopOrderModel) {
            binding.shopNameTxt.text = item.shopName
            progressBar.max = item.minPrice.toInt()
            binding.cartView.adapter = CartAdapter(item.items, 0, { _: Int ->
            }, { price, profitPrice ->
                val remainingPrice = item.minPrice - (price - profitPrice)
                binding.remainingPriceTxt.text =
                    "${Helper.formatPriceToPersian(remainingPrice)} باقی مانده"
                progressBar.progress = remainingPrice.toInt()
            })
            binding.minPriceTxt.text =
                "${Helper.formatPriceToPersian(item.minPrice)} ${context.getString(R.string.title_currency)}"
        }
    }

}