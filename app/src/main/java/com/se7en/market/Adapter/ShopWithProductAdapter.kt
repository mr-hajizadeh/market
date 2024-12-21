package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Model.CartModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.ShopModel
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.databinding.ViewHolderShopWithProductBinding

class ShopWithProductAdapter(
    private val items: List<ShopModel>,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<ShopWithProductAdapter.ShopViewHolder>() {

    private lateinit var context: Context

    fun syncCartWithProducts(newCartList: MutableList<CartModel>) {
        items.forEachIndexed { index, _ ->
            Handler(Looper.getMainLooper()).post {
                val holder = recyclerView.findViewHolderForAdapterPosition(index)
                if (holder is ShopViewHolder) {
                    holder.syncCartWithProducts(newCartList)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        context = parent.context
        return ShopViewHolder(
            ViewHolderShopWithProductBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(items[position], context)
    }

    class ShopViewHolder(private val binding: ViewHolderShopWithProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var adapter: ProductWithHeaderAdapter

        @Suppress("UNCHECKED_CAST")
        @SuppressLint("SetTextI18n")
        fun bind(shop: ShopModel, context: Context) {
            binding.shopNameTxt.text = " - ${shop.shop_name}"
            binding.deliveryTimeTxt.text =
                "${MainActivity().convertToPersianNumbers(shop.delivery_time.toString())} ${
                    context.getString(
                        R.string.title_minute
                    )
                }"
            binding.courierFeeTxt.text =
                if (shop.courier_fee == 0) context.getString(R.string.title_free)
                else "${shop.formatPriceToPersianCourierFee()} ${context.getString(R.string.title_currency)}"
            adapter = ProductWithHeaderAdapter(
                (shop.items as MutableList<ListItemModel>),
                "",
                null,
                context.getColor(R.color.green),
                false,
                changeQuantityListener = CartManager
            )
            binding.productView.adapter = adapter
        }

        fun syncCartWithProducts(newCartList: MutableList<CartModel>) {
            adapter.syncCartWithProducts(newCartList)
        }

    }
}