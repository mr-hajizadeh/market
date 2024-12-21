package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Model.CartModel
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.Utils.Helper
import com.se7en.market.databinding.ViewHolderCartListBinding

class ShopOrderAdapter(
    private val items: MutableList<CartModel>,
    private val deleteCart: (cartId: Int, position: Int) -> Unit,
    private val cart: (cart: CartModel) -> Unit,
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
        val item = items[position]
        holder.bind(item)

        if (item.loading) {
            holder.loadingProgress.visibility = View.VISIBLE
            holder.deleteBtn.visibility = View.INVISIBLE
        } else {
            holder.loadingProgress.visibility = View.INVISIBLE
            holder.deleteBtn.visibility = View.VISIBLE
        }

        holder.continueBtn.setOnClickListener {
            cart(item)
        }

        holder.deleteBtn.setOnClickListener {
            deleteCart(item.id, position)
        }
    }

    class ShopHolder(private val binding: ViewHolderCartListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val continueBtn = binding.continueBtn
        val context: Context = binding.root.context
        val deleteBtn = binding.deleteOrderBtn
        val loadingProgress = binding.loadingProgress
        private val progressBar = binding.minPriceProgress
        private val deliveryTimeTxt = binding.deliveryTimeTxt
        private val courierFeeTxt = binding.courierFeeTxt
        private lateinit var adapter: CartAdapter

        @SuppressLint("SetTextI18n")
        fun bind(item: CartModel) {
            with(item.seller) {
                binding.shopNameTxt.text = shop_name
                deliveryTimeTxt.text = "تحویل در $delivery_time دقیقه"
                courierFeeTxt.text =
                    "${formatPriceToPersianCourierFee()} ${context.getString(R.string.title_currency)}"
                progressBar.max = /*item.minPrice.toInt()*/0
                adapter =
                    CartAdapter(0, CartManager, { _: Int ->
                    }, { price, profitPrice ->
                        val remainingPrice = /*item.minPrice*/ 0 - (price - profitPrice)
                        binding.remainingPriceTxt.text =
                            "${Helper.formatPriceToPersian(remainingPrice)} ${context.getString(R.string.title_remaining)}"
                        progressBar.progress = remainingPrice.toInt()
                    })
                binding.minPriceTxt.text =
                    "${Helper.formatPriceToPersian(/*item.minPrice*/ 0)} ${context.getString(R.string.title_currency)}"
                adapter.setCartList(item.card_details)
                binding.cartView.adapter = adapter
            }

        }
    }

    fun deleteItem(position: Int) {
        if (position in 0 until items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }

    fun loadingItem(position: Int) {
        if (position in 0 until items.size) {
            items[position].loading = !items[position].loading
            notifyItemChanged(position)
        }
    }

}