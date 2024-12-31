package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.se7en.market.Activity.CartActivity
import com.se7en.market.Model.CartDetailModel
import com.se7en.market.R
import com.se7en.market.Utils.Constance.Companion.IMAGE_URL_PATH
import com.se7en.market.Utils.Helper
import com.se7en.market.databinding.ViewHolderProductCartBinding
import com.se7en.market.databinding.ViewHolderProductCartWithButtonsBinding

class CartAdapter(
    private val cartViewType: Int,
    private val changeQuantityListener: ChangeQuantityListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var items: MutableList<CartDetailModel> = mutableListOf()
    private var changeItemCount: ((count: Int) -> Unit)? = null
    private var totalPrice: ((price: Long, profitPrice: Long) -> Unit)? = null

    constructor(
        cartViewType: Int,
        changeQuantityListener: ChangeQuantityListener,
        changeItemCount: (count: Int) -> Unit,
        totalPrice: (price: Long, profitPrice: Long) -> Unit,
    ) : this(cartViewType, changeQuantityListener) {
        this.changeItemCount = changeItemCount
        this.totalPrice = totalPrice
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCartList(items: MutableList<CartDetailModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == cartViewType) {
            CartHolder(
                ViewHolderProductCartBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )
        } else
            HorizontalCartHolder(
                ViewHolderProductCartWithButtonsBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cartDetail = items[position]

        calculateTotal()
        changeItemCount?.invoke(items.sumOf { it.quantity })

        when (holder) {
            is CartHolder -> holder.bind(cartDetail)
            is HorizontalCartHolder -> {
                holder.bind(cartDetail)
                holder.changeCounterTextView(cartDetail.quantity)

                val product = cartDetail.item
                holder.plusBtn.setOnClickListener {
                    changeQuantityListener.incrementItem(
                        cartDetail,
                        product,
                        position,
                        this@CartAdapter,
                    )
                }
                holder.minusBtn.setOnClickListener {
                    changeQuantityListener.decrementItem(
                        cartDetail,
                        product,
                        position,
                        this@CartAdapter,
                    )
                }

                if (cartDetail.item.quantityLoading) {
                    holder.counterView.visibility = View.INVISIBLE
                    holder.loadingProgress.visibility = View.VISIBLE
                } else {
                    holder.counterView.visibility = View.VISIBLE
                    holder.loadingProgress.visibility = View.INVISIBLE
                }

                if (cartDetail.quantity == 1)
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

            }
        }
    }

    private fun calculateTotal() {
        var totalWithoutDiscount = 0L
        var totalWithDiscount = 0L

        for (cartDetail in items) {
            val originalPrice = cartDetail.item.amount * cartDetail.quantity
            val discountAmount = (cartDetail.item.offPrice() * cartDetail.quantity).toLong()
            val finalPrice = originalPrice - discountAmount

            totalWithoutDiscount += originalPrice
            totalWithDiscount += finalPrice
        }

        totalPrice?.invoke((totalWithoutDiscount - totalWithDiscount), totalWithDiscount)
    }

    class CartHolder(val binding: ViewHolderProductCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val percentView = binding.percentTxt
        private val counterView = binding.productCountTxt
        private val context: Context = binding.root.context

        @SuppressLint("SetTextI18n")
        fun bind(cartDetail: CartDetailModel) {
            val productItem = cartDetail.item
            counterView.text =
                CartActivity().convertToPersianNumbers(cartDetail.quantity.toString())
            percentView.visibility = if (productItem.off_percent > 0) View.VISIBLE else View.GONE
            percentView.text =
                "%${CartActivity().convertToPersianNumbers(productItem.off_percent.toString())}"
            if (productItem.image.isNotEmpty())
                Glide.with(context)
                    .load("$IMAGE_URL_PATH${productItem.image[0]}")
                    .placeholder(R.mipmap.ic_launcher)
                    .into(binding.productImg)
        }
    }

    class HorizontalCartHolder(val binding: ViewHolderProductCartWithButtonsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val plusBtn = binding.plusBtn
        val minusBtn = binding.minusBtn
        val counterView = binding.countTxt
        val loadingProgress = binding.loadingProgress
        private val percentView = binding.percentTxt
        private val productPrice = binding.productPriceTxt
        private val context: Context = binding.root.context

        @SuppressLint("SetTextI18n")
        fun bind(cartDetail: CartDetailModel) {
            val productItem = cartDetail.item
            counterView.text =
                CartActivity().convertToPersianNumbers(cartDetail.quantity.toString())
            productPrice.visibility = if (productItem.off_percent > 0) View.VISIBLE else View.GONE
            productPrice.text = Helper.getStrikethroughPriceWithCurrency(
                productItem.amount,
                context.getString(R.string.title_currency)
            )
            val productPrice = cartDetail.item.offPrice() * cartDetail.quantity
            binding.finalPriceTxt.text =
                "${Helper.formatPriceToPersian(productPrice.toLong())} ${context.getString(R.string.title_currency)}"
            percentView.visibility = if (productItem.off_percent > 0) View.VISIBLE else View.GONE
            percentView.text =
                "%${CartActivity().convertToPersianNumbers(productItem.off_percent.toString())}"
            if (productItem.image.isNotEmpty())
                Glide.with(context)
                    .load("$IMAGE_URL_PATH${productItem.image[0]}")
                    .placeholder(R.mipmap.ic_launcher)
                    .into(binding.productImg)
        }

        fun changeCounterTextView(count: Int) {
            counterView.text = CartActivity().convertToPersianNumbers(count.toString())
        }

    }

    fun loadingQuantity(position: Int, cartDetail: CartDetailModel) {
        if (position in 0 until items.size) {
            cartDetail.item.quantityLoading = !cartDetail.item.quantityLoading
            notifyItemChanged(position)
        }
    }

    fun updateQuantity(position: Int, cartDetail: CartDetailModel) {
        if (position in 0 until items.size) {
            items[position] = cartDetail
            notifyItemChanged(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun retainList(newCartDetailList: MutableList<CartDetailModel>) {
        if (newCartDetailList.size == 0)
            totalPrice?.invoke(0, 0)
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (newCartDetailList.none { it.item_id == item.item_id })
                iterator.remove()
        }
        notifyDataSetChanged()
    }

}