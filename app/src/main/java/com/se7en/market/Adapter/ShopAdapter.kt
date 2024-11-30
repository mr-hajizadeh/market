package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.ShopActivity
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.ShopModel
import com.se7en.market.R
import com.se7en.market.Utils.Constance.Companion.KEY_SHOP_ID
import com.se7en.market.databinding.ViewHolderShopBinding

class ShopAdapter(private val shopList: MutableList<ListItemModel>) :
    RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ViewHolderShopBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = if (shopList.size >= 3) 3 else shopList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = shopList[position] as ShopModel
        holder.shopName.text = item.shop_name
        holder.shopAddress.text = item.shop_address
        holder.deliveryTime.text = "تحویل در ${item.delivery_time} دقیقه"
        holder.courierFee.text =
            "${item.formatPriceToPersianCourierFee()} ${context.getString(R.string.title_currency)}"
        if (position % 2 == 0)
            holder.shopName.setTextColor(context.getColor(R.color.blue))
        else
            holder.shopName.setTextColor(context.getColor(R.color.green))

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShopActivity::class.java)
            intent.putExtra(KEY_SHOP_ID, item.id)
            context.startActivity(intent)
        }

    }

    class ViewHolder(shopBinding: ViewHolderShopBinding) :
        RecyclerView.ViewHolder(shopBinding.root) {
        val shopName = shopBinding.shopNameTxt
        val shopAddress = shopBinding.shopAddressTxt
        val deliveryTime = shopBinding.deliveryTimeTxt
        val courierFee = shopBinding.courierFeeTxt
    }

}