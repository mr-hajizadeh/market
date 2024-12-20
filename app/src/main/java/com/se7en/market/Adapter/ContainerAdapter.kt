package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Model.CartModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.MainListModel
import com.se7en.market.Model.ViewTypeModel
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.databinding.ViewHolderBrandListBinding
import com.se7en.market.databinding.ViewHolderCategoryListBinding
import com.se7en.market.databinding.ViewHolderProductListBinding
import com.se7en.market.databinding.ViewHolderShopListBinding

class ContainerAdapter(
    private val items: MutableList<MainListModel>,
    private val isVerticalGridLayout: Boolean,
    private val recyclerView: RecyclerView,
    private val containerView: View,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val viewTypeEnum = items.find { it.position == viewType }?.viewType
            ?: throw IllegalStateException("ViewType is null")
        return viewTypeHolder(viewTypeEnum, parent)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is CategoryViewHolder -> holder.bind(item.items)
            is ProductViewHolder -> holder.bind(item)
            is ShopViewHolder -> holder.bind(item)
            is BrandViewHolder -> holder.bind(item.items)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].position
    }

    fun syncCartWithProducts(newCartList: MutableList<CartModel>) {
        items.forEachIndexed { index, item ->
            if (item.viewType in listOf(
                    ViewTypeModel.HEADER_TYPE,
                    ViewTypeModel.TWO_ROW_TYPE,
                    ViewTypeModel.ONE_ROW_TYPE
                )
            ) {
                Handler(Looper.getMainLooper()).post {
                    val holder = recyclerView.findViewHolderForAdapterPosition(index)
                    if (holder is ProductViewHolder) {
                        holder.syncCartWithProducts(newCartList)
                    }
                }
            }
        }
    }

    class CategoryViewHolder(
        categoryBinding: ViewHolderCategoryListBinding,
        private val context: Context,
        private val isVerticalGridLayout: Boolean,
    ) :
        RecyclerView.ViewHolder(categoryBinding.root) {
        private val categoryView = categoryBinding.categoryView
        fun bind(items: MutableList<ListItemModel>) {
            val categoryLayoutManager = if (isVerticalGridLayout)
                GridLayoutManager(context, 4)
            else
                GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            categoryView.layoutManager = categoryLayoutManager
            categoryView.adapter = CategoryAdapter(items, !isVerticalGridLayout)
        }
    }

    class ProductViewHolder(
        binding: ViewHolderProductListBinding,
        private val context: Context,
        private val color: Int,
        private val containerView: View,
        private val isGrid: Boolean = false,
        private val isHeaderEnable: Boolean = false,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var adapter: ProductWithHeaderAdapter
        private val productView: RecyclerView = binding.productView

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: MainListModel) {
            val layoutManager = if (isGrid) {
                GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false).apply {
                    if (isHeaderEnable)
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return if (position == 0) 2 else 1
                            }
                        }
                }
            } else {
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            adapter = ProductWithHeaderAdapter(
                item.items,
                context.getString(R.string.title_recommended),
                context.getDrawable(R.drawable.ic_favorite),
                color,
                isHeaderEnable,
                CartManager,
                containerView,
            )
            productView.layoutManager = layoutManager
            productView.adapter = adapter
        }

        fun syncCartWithProducts(newCartList: MutableList<CartModel>) {
            adapter.syncCartWithProducts(newCartList)
        }
    }

    class ShopViewHolder(
        binding: ViewHolderShopListBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private var adapter: ShopAdapter? = null
        private val shopView: RecyclerView = binding.shopView
        private val title = binding.titleShop

        fun bind(item: MainListModel) {
            adapter = ShopAdapter(item.items)
            shopView.adapter = adapter
            title.text = item.title
        }
    }

    class BrandViewHolder(binding: ViewHolderBrandListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: MutableList<ListItemModel>) {
            Log.i("TAG", "bind: $items")
        }
    }

    private fun viewTypeHolder(
        viewType: ViewTypeModel,
        parent: ViewGroup,
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewTypeModel.CATEGORY_TYPE -> {
                CategoryViewHolder(
                    ViewHolderCategoryListBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    ), context, isVerticalGridLayout
                )
            }

            ViewTypeModel.HEADER_TYPE -> {
                ProductViewHolder(
                    ViewHolderProductListBinding.inflate(
                        LayoutInflater.from(context),
                        parent, false
                    ),
                    context,
                    context.getColor(R.color.orange),
                    containerView,
                    isGrid = true,
                    isHeaderEnable = true
                )
            }

            ViewTypeModel.TWO_ROW_TYPE -> {
                ProductViewHolder(
                    ViewHolderProductListBinding.inflate(
                        LayoutInflater.from(context),
                        parent, false
                    ),
                    context,
                    context.getColor(R.color.green),
                    containerView,
                    isGrid = true,
                    isHeaderEnable = false
                )
            }

            ViewTypeModel.ONE_ROW_TYPE -> {
                ProductViewHolder(
                    ViewHolderProductListBinding.inflate(
                        LayoutInflater.from(context),
                        parent, false
                    ),
                    context,
                    context.getColor(R.color.green),
                    containerView,
                    isGrid = false,
                    isHeaderEnable = false
                )
            }

            ViewTypeModel.SHOP_TYPE -> {
                ShopViewHolder(
                    ViewHolderShopListBinding.inflate(
                        LayoutInflater.from(context),
                        parent, false
                    )
                )
            }

            ViewTypeModel.BRAND_TYPE -> {
                BrandViewHolder(
                    ViewHolderBrandListBinding.inflate(
                        LayoutInflater.from(context),
                        parent, false
                    )
                )
            }
        }
    }
}