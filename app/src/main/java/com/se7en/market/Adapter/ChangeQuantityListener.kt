package com.se7en.market.Adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Model.CartDetailModel
import com.se7en.market.Model.ProductModel

interface ChangeQuantityListener {
    fun onAdd(
        product: ProductModel,
        position: Int,
        adapter: ProductWithHeaderAdapter?,
        containerView: View,
    )

    fun <T : RecyclerView.Adapter<RecyclerView.ViewHolder>> incrementItem(
        cartDetail: CartDetailModel,
        product: ProductModel,
        position: Int,
        adapter: T,
    )

    fun <T : RecyclerView.Adapter<RecyclerView.ViewHolder>> decrementItem(
        cartDetail: CartDetailModel,
        product: ProductModel,
        position: Int,
        adapter: T,
    )

}