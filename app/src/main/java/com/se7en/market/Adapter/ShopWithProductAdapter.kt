package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.ListActivity
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Model.CartModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.ShopModel
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.Utils.Constance.Companion.KEY_PRODUCT_LIST
import com.se7en.market.Utils.Constance.Companion.KEY_TITLE
import com.se7en.market.databinding.ViewHolderShopWithProductBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

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
        holder.shopBtn.setOnClickListener {
            val intent = Intent(context, ListActivity::class.java)
            val productList = ArrayList(items[position].items)
            intent.putParcelableArrayListExtra(KEY_PRODUCT_LIST, productList)
            intent.putExtra(KEY_TITLE, items[position].shop_name)
            context.startActivity(intent)
        }

    }

    class ShopViewHolder(private val binding: ViewHolderShopWithProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var adapter: ProductWithHeaderAdapter
        private var productView = binding.productView
        val shopBtn = binding.shopBtn

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
                0,
                context.getColor(R.color.green),
                false,
                changeQuantityListener = CartManager
            )
            productView.adapter = adapter
            productView.loadSkeleton(R.layout.view_holder_product)
            Handler(Looper.getMainLooper()).postDelayed({ productView.hideSkeleton() }, 1000)
        }

        fun syncCartWithProducts(newCartList: MutableList<CartModel>) {
            adapter.syncCartWithProducts(newCartList)
        }

    }
}