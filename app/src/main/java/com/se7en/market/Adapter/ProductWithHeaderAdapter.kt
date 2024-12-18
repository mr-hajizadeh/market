package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.se7en.market.Activity.CartActivity
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Activity.Ui.ModalProductDetail
import com.se7en.market.Model.CartDetailModel
import com.se7en.market.Model.CartModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.R
import com.se7en.market.Utils.Constance.Companion.IMAGE_URL_PATH
import com.se7en.market.Utils.Helper
import com.se7en.market.databinding.ViewHolderHeaderBinding
import com.se7en.market.databinding.ViewHolderProductBinding

class ProductWithHeaderAdapter(
    private val productList: MutableList<ListItemModel>,
    private val headerTitle: String = "",
    private val headerIcon: Drawable? = null,
    private val itemColor: Int,
    private val isHeaderEnable: Boolean = false,
    private val changeQuantityListener: ChangeQuantityListener,
    private val containerView: View,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_LIST = 1
    }

    private var cartList: MutableList<CartModel> = mutableListOf()

    private val customHeaderShape: Drawable by lazy {
        helper.createCustomShape(itemColor)
    }

    private val customPercentageShape: Drawable by lazy {
        helper.createCustomShape(itemColor, 4f)
    }

    private val customAddButtonShape: Drawable by lazy {
        helper.createCustomShape(itemColor, 50f, Paint.Style.STROKE)
    }

    private lateinit var context: Context
    private lateinit var helper: Helper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        helper = Helper(context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                ViewHolderHeaderBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            else -> ProductViewHolder(
                ViewHolderProductBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        val productListSize = productList.size
        val maxItemCount =
            if (productListSize >= 10 && isHeaderEnable) 11
            else if (productListSize <= 10 && isHeaderEnable) productListSize + 1
            else productListSize
        return maxItemCount
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeaderEnable && position == 0 && holder is HeaderViewHolder) {
            holder.headerTxt.text = headerTitle
            holder.headerIcon.setImageDrawable(headerIcon)
            holder.headerLayout.background = customHeaderShape
        } else if (holder is ProductViewHolder) {
            val item = productList[if (isHeaderEnable) position - 1 else position] as ProductModel
            holder.setData(item, context)
            holder.discountPercentage.background = customPercentageShape
            holder.addBtn.imageTintList = ColorStateList.valueOf(itemColor)
            holder.loadingProgress.setIndicatorColor(itemColor)
            holder.addBtn.background = customAddButtonShape

            if (item.loading) {
                holder.loadingProgress.visibility = View.VISIBLE
                holder.addBtn.visibility = View.GONE
            } else {
                holder.loadingProgress.visibility = View.GONE
                holder.addBtn.visibility = View.VISIBLE
            }

            holder.addBtn.setOnClickListener {
                @Suppress("UNCHECKED_CAST")
                val list = productList as MutableList<ProductModel>
                if (list.none { it.loading })
                    changeQuantityListener.onAdd(
                        item,
                        position,
                        this,
                        containerView
                    )
            }

            holder.plusBtn.setOnClickListener {
                changeQuantityListener.incrementItem(
                    item.cartDetail!!,
                    item,
                    position,
                    this@ProductWithHeaderAdapter,
                )
            }

            holder.minusBtn.setOnClickListener {
                changeQuantityListener.decrementItem(
                    item.cartDetail!!,
                    item,
                    position,
                    this@ProductWithHeaderAdapter,
                )
            }

            if (item.quantityLoading) {
                holder.counterView.visibility = View.INVISIBLE
                holder.quantityProgress.visibility = View.VISIBLE
            } else {
                holder.counterView.visibility = View.VISIBLE
                holder.quantityProgress.visibility = View.INVISIBLE
            }

            if (item.quantity > 0) {
                Log.i("TAG", "onBindViewHolder: itemID=${item.id} quantity=${item.quantity}")
                holder.addLayout.visibility = View.GONE
                holder.quantityLayout.visibility = View.VISIBLE
            } else {
                holder.addLayout.visibility = View.VISIBLE
                holder.quantityLayout.visibility = View.GONE
            }

            if (item.quantity == 1)
                holder.minusBtn.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_trash
                    )
                )
            else
                holder.minusBtn.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        org.neshan.mapsdk.R.drawable.ic_minus
                    )
                )

            holder.itemView.setOnClickListener {
                val modal = ModalProductDetail(
                    itemColor,
                    item,
                    changeQuantityListener
                )
                modal.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    ModalProductDetail.TAG
                )
            }

            holder.changeCounterTextView(item.quantity)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeaderEnable && position == 0) TYPE_HEADER else TYPE_LIST
    }

    class HeaderViewHolder(headerBinding: ViewHolderHeaderBinding) :
        RecyclerView.ViewHolder(headerBinding.root) {
        val headerTxt = headerBinding.headerTxt
        val headerIcon = headerBinding.headIc
        val headerLayout = headerBinding.headerLayout
    }

    class ProductViewHolder(
        productBinding: ViewHolderProductBinding,
    ) :
        RecyclerView.ViewHolder(productBinding.root) {

        val loadingProgress = productBinding.loadingProgress
        val addBtn = productBinding.addBtn
        val discountPercentage = productBinding.discountPercentageTxt
        private val productTitle = productBinding.productTittleTxt
        private val offLayout = productBinding.offLayout
        private val mainPrice = productBinding.mainPriceTxt
        private val offPrice = productBinding.offPriceTxt
        private val productImage = productBinding.productImg
        val addLayout = productBinding.addLayout
        val quantityLayout = productBinding.quantityLayout
        val quantityProgress = productBinding.quantityProgress
        val plusBtn = productBinding.plusBtn
        val minusBtn = productBinding.minusBtn
        val counterView = productBinding.countTxt
        private val mainActivity = MainActivity()

        @SuppressLint("SetTextI18n")
        fun setData(product: ProductModel, context: Context) {

            with(product) {
                productTitle.text = title
                offLayout.visibility = if (off_percent > 0) View.VISIBLE else View.GONE
                offPrice.text = Helper.getStrikethroughPriceWithCurrency(
                    amount,
                    ""
                )
                mainPrice.text =
                    "${formatPriceToPersianAmount()} ${context.getString(R.string.title_currency)}"
                discountPercentage.text = mainActivity.convertToPersianNumbers("%${off_percent}")

                if (image.isNotEmpty())
                    Glide.with(context)
                        .load("$IMAGE_URL_PATH${image[0]}")
                        .placeholder(R.mipmap.ic_launcher)
                        .into(productImage)

            }
        }

        fun changeCounterTextView(count: Int) {
            counterView.text = CartActivity().convertToPersianNumbers(count.toString())
        }
    }

    fun updateItem(position: Int, item: ProductModel) {
        item.loading = !item.loading
        notifyItemChanged(position)
    }

    fun loadingQuantity(position: Int, item: ProductModel) {
        if (position in 0 until productList.size) {
            item.quantityLoading = !item.quantityLoading
            notifyItemChanged(position)
        }
    }

    fun updateQuantity(position: Int, cartDetail: CartDetailModel) {
        if (position in 0 until productList.size) {
            val product = (productList[position] as ProductModel)
            product.quantity = cartDetail.quantity
            notifyItemChanged(position)
        }
    }

    fun notFoundItem(position: Int, product: ProductModel) {
        cartList.find { cart ->
            cart.card_details.any { it.item_id == product.id }
        }?.let { cart ->
            val cartDetail = cart.card_details.find { it.item_id == product.id }
            cartDetail?.let { cart.card_details.remove(it) }
        }
        product.quantity = 0
        notifyItemChanged(position)
    }

    fun syncCartWithProducts(newCartList: MutableList<CartModel>) {
        val cartMap = newCartList.flatMap { it.card_details }
            .associateBy { it.item_id }

        productList.forEachIndexed { index, product ->
            if (product is ProductModel) {
                cartMap[product.id]?.let { cartDetail ->
                    product.quantity = cartDetail.quantity
                    product.cartDetail = cartDetail
                    notifyItemChanged(if (isHeaderEnable) index + 1 else index)
                }
            }
        }
    }

}