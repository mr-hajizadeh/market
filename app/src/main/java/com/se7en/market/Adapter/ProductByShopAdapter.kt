package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Model.ShopWithProductModel
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.databinding.ViewHolderShopWithProductBinding

class ProductByShopAdapter(
    private val items: List<ShopWithProductModel>,
) : RecyclerView.Adapter<ProductByShopAdapter.ShopViewHolder>() {

    private lateinit var context: Context

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

        @SuppressLint("SetTextI18n")
        fun bind(shopWithProductModel: ShopWithProductModel, context: Context) {
            with(shopWithProductModel) {
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
                    productItems,
                    "",
                    null,
                    context.getColor(R.color.green),
                    false,
                    CartManager,
                    binding.root,
                )
                binding.productView.adapter = adapter
            }
        }

    }
}