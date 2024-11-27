package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.CartActivity
import com.se7en.market.Model.ProductModel
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.databinding.ViewHolderProductCartBinding
import com.se7en.market.databinding.ViewHolderProductCartWithButtonsBinding

class CartAdapter(
    private val items: MutableList<ProductModel>,
    private val cartViewType: Int,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var changeItemCount: ((count: Int) -> Unit)? = null
    private var totalPrice: ((price: Long, profitPrice: Long) -> Unit)? = null

    constructor(
        items: MutableList<ProductModel>,
        cartViewType: Int,
        changeItemCount: (count: Int) -> Unit,
        totalPrice: (price: Long, profitPrice: Long) -> Unit
    ) : this(items, cartViewType) {
        this.changeItemCount = changeItemCount
        this.totalPrice = totalPrice
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
        val item = items[position]

        updateItemCount()
        calculateTotal()

        when (holder) {
            is CartHolder -> holder.bind(item)
            is HorizontalCartHolder -> {
                holder.bind(item)

                holder.plusBtn.setOnClickListener {
                    if (item.count <= 999) {
                        item.count += 1
                        holder.changeCounterTextView(item.count)
                        notifyItemChanged(position)
                    }
                }
                holder.minusBtn.setOnClickListener {
                    if (item.count > 1) {
                        item.count -= 1
                        holder.changeCounterTextView(item.count)
                        notifyItemChanged(position)
                    }
                }

                if (item.count == 1)
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
        var total = 0L
        var profitTotal = 0L

        for (item in items) {
            total += 7000 * item.count
            profitTotal += 3500 * item.count
        }

        totalPrice?.invoke(total, profitTotal)
    }

    class CartHolder(val binding: ViewHolderProductCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val counterView = binding.productCountTxt


        fun bind(item: ProductModel) {
            binding.productImg.setImageDrawable(binding.root.context.getDrawable(R.drawable.t3))
            counterView.text = CartActivity().convertToPersianNumbers(item.count.toString())
            binding.percentTxt.text = "%${CartActivity().convertToPersianNumbers("50")}"
        }
    }

    class HorizontalCartHolder(val binding: ViewHolderProductCartWithButtonsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val plusBtn = binding.plusBtn
        val minusBtn = binding.minusBtn
        private val counterView = binding.countTxt
        private val context: Context = binding.root.context

        @SuppressLint("SetTextI18n")
        fun bind(item: ProductModel) {
            counterView.text = CartActivity().convertToPersianNumbers(item.count.toString())
            binding.productImg.setImageDrawable(binding.root.context.getDrawable(R.drawable.t3))
            binding.productPriceTxt.text = Helper.getStrikethroughPriceWithCurrency(
                7000,
                context.getString(R.string.title_currency)
            )
            val productPrice = 3500 * item.count
            binding.finalPriceTxt.text =
                "${Helper.formatPriceToPersian(productPrice.toLong())} ${context.getString(R.string.title_currency)}"
            binding.percentTxt.text = "%${CartActivity().convertToPersianNumbers("50")}"
        }

        fun changeCounterTextView(count: Int) {
            counterView.text = CartActivity().convertToPersianNumbers(count.toString())
        }

    }

    private fun updateItemCount() =
        changeItemCount?.invoke(items.sumOf { it.count })


}