package com.se7en.market.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.ShopModel
import com.se7en.market.R
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

    override fun getItemCount() = 3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = shopList[position] as ShopModel
        holder.shopName.text = item.name
        if (position % 2 == 0)
            holder.shopName.setTextColor(context.getColor(R.color.blue))
        else
            holder.shopName.setTextColor(context.getColor(R.color.green))
    }

    class ViewHolder(shopBinding: ViewHolderShopBinding) :
        RecyclerView.ViewHolder(shopBinding.root) {
        val shopName = shopBinding.shopNameTxt
    }

}