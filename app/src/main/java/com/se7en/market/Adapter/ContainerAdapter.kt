package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.MainListModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Model.ViewTypeModel
import com.se7en.market.R
import com.se7en.market.databinding.ViewHolderBrandListBinding
import com.se7en.market.databinding.ViewHolderCategoryListBinding
import com.se7en.market.databinding.ViewHolderProductListBinding
import com.se7en.market.databinding.ViewHolderShopListBinding

class ContainerAdapter(
    private val items: MutableList<MainListModel>,
    private val categoryLayout: GridLayoutManager,
    private val addToCartItem: (product: ProductModel, position: Int, adapter: ProductWithHeaderAdapter) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val viewTypeEnum = items.find { it.position == viewType }?.viewType
            ?: throw IllegalArgumentException("Invalid view type")
        return viewTypeHolder(viewTypeEnum, parent)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is CategoryViewHolder -> holder.bind(item.items)
            is HeaderViewHolder -> holder.bind(item)
            is TwoViewHolder -> holder.bind(item.items)
            is OneViewHolder -> holder.bind(item.items)
            is ShopViewHolder -> holder.bind(item)
            is BrandViewHolder -> holder.bind(item.items)
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.i("TAG", "getItemViewType: ${items[position].position}")
        return items[position].position
    }

    class CategoryViewHolder(
        categoryBinding: ViewHolderCategoryListBinding,
        private val categoryLayout: GridLayoutManager,
    ) :
        RecyclerView.ViewHolder(categoryBinding.root) {
        private val categoryView = categoryBinding.categoryView
        fun bind(items: MutableList<ListItemModel>) {
            categoryView.layoutManager = categoryLayout
            categoryView.adapter = CategoryAdapter(items)
        }
    }

    class HeaderViewHolder(
        binding: ViewHolderProductListBinding,
        private val context: Context,
        private val addToCartItem: (product: ProductModel, position: Int, adapter: ProductWithHeaderAdapter) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var adapter: ProductWithHeaderAdapter
        private val productView: RecyclerView = binding.productView
        private val listTitle = binding.listTitle
        private var gridLayout =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == 0) 2
                        else 1
                    }
                }
            }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: MainListModel) {
            productView.layoutManager = gridLayout
            listTitle.text = item.title
            adapter = ProductWithHeaderAdapter(
                item.items,
                item.title,
                context.getDrawable(R.drawable.ic_favorite)!!,
                context.getColor(R.color.orange), true
            ) { position, product ->
                addToCartItem(product, position, adapter)
            }
            productView.adapter = adapter
        }
    }

    class OneViewHolder(
        binding: ViewHolderProductListBinding,
        private val context: Context,
        private val addToCartItem: (product: ProductModel, position: Int, adapter: ProductWithHeaderAdapter) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var adapter: ProductWithHeaderAdapter
        private val productView: RecyclerView = binding.productView
        private var layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(items: MutableList<ListItemModel>) {
            adapter = ProductWithHeaderAdapter(
                items,
                context.getString(R.string.title_recommended),
                context.getDrawable(R.drawable.ic_favorite)!!,
                context.getColor(R.color.green)
            ) { position, product ->
                addToCartItem(product, position, adapter)
            }
            productView.layoutManager = layoutManager
            productView.adapter = adapter
        }
    }

    class TwoViewHolder(
        binding: ViewHolderProductListBinding,
        private val context: Context,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private var adapter: ProductWithHeaderAdapter? = null
        private val productView: RecyclerView = binding.productView
        private var gridLayout =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(items: MutableList<ListItemModel>) {
            adapter = ProductWithHeaderAdapter(
                items,
                context.getString(R.string.title_recommended),
                context.getDrawable(R.drawable.ic_favorite)!!,
                context.getColor(R.color.green)
            ) { position, product ->
//                addToCartItem(items[position] as ProductModel, position, adapter)
            }
            productView.layoutManager = gridLayout
            productView.adapter = adapter
        }
    }

    class ShopViewHolder(
        binding: ViewHolderShopListBinding,
        private val context: Context,
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
                    ), categoryLayout
                )
            }

            ViewTypeModel.HEADER_TYPE -> {
                HeaderViewHolder(
                    ViewHolderProductListBinding.inflate(
                        LayoutInflater.from(context),
                        parent, false
                    ), context, addToCartItem
                )
            }

            ViewTypeModel.TWO_ROW_TYPE -> {
                TwoViewHolder(
                    ViewHolderProductListBinding.inflate(
                        LayoutInflater.from(context),
                        parent, false
                    ), context
                )
            }

            ViewTypeModel.ONE_ROW_TYPE -> {
                OneViewHolder(
                    ViewHolderProductListBinding.inflate(
                        LayoutInflater.from(context),
                        parent, false
                    ), context, addToCartItem
                )
            }

            ViewTypeModel.SHOP_TYPE -> {
                ShopViewHolder(
                    ViewHolderShopListBinding.inflate(
                        LayoutInflater.from(context),
                        parent, false
                    ), context
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