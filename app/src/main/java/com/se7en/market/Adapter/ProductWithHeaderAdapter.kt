package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Activity.Ui.ModalProductDetail
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.R
import com.se7en.market.Utils.Constance.Companion.BASE_URL
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
    private val onItemUpdate: (position: Int, product: ProductModel) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_LIST = 1
    }

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
        val productListSize = (productList as MutableList<*>).size
        val maxItemCount = if (productListSize >= 10) 11 else productListSize + 1
        return if (isHeaderEnable) maxItemCount else productListSize
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
                updateItem(position, item)
                onItemUpdate(position, item)
            }
            holder.itemView.setOnClickListener {
                val modal = ModalProductDetail(itemColor)
                modal.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "ModalProductDetail"
                )
            }
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
        private val offLayout = productBinding.offLayout
        private val mainPrice = productBinding.mainPriceTxt
        private val offPrice = productBinding.offPriceTxt
        private val productImage = productBinding.productImg
        private val mainActivity = MainActivity()

        @SuppressLint("SetTextI18n")
        fun setData(product: ProductModel, context: Context) {

            with(product) {

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
                        .into(productImage)
            }
        }
    }

    fun updateItem(position: Int, item: ProductModel) {
        item.loading = !item.loading
        notifyItemChanged(position)
    }

}