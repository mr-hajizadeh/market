package com.se7en.market.Activity.Ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Adapter.ChangeQuantityListener
import com.se7en.market.Adapter.ProductSliderAdapter
import com.se7en.market.Model.CartModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.databinding.ModalDetailProductBinding

class ModalProductDetail(
    private val color: Int = 0,
    private val product: ProductModel = ProductModel(),
    private val changeQuantityListener: ChangeQuantityListener? = null,
) : BottomSheetDialogFragment() {

    private var _binding: ModalDetailProductBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()

    var aaaaa: ConstraintLayout? = null
    private lateinit var addBtn: Button
    private lateinit var discountPercentage: TextView
    private lateinit var existingMessage: TextView
    private lateinit var loadingProgress: CircularProgressIndicator

    private val customShape: Drawable by lazy {
        Helper(requireActivity()).createCustomShape(color, 4f)
    }

    companion object {
        const val TAG = "ModalProductDetail"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ModalDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setData()
        onClickButtons()
        getAllCart()
    }

    private fun initViews() {

        addBtn = binding.addBtn
        discountPercentage = binding.percentTxt
        existingMessage = binding.existingMessageTxt
        loadingProgress = binding.loadingProgress
        aaaaa = binding.bottomBarLayout

        discountPercentage.background = customShape
        addBtn.backgroundTintList = ColorStateList.valueOf(color)
        loadingProgress.setIndicatorColor(color)

        val existsMessage =
            "${getString(R.string.title_item_exists_in_cart1)} ${getString(R.string.title_cart)} ${
                getString(R.string.title_item_exists_in_cart2)
            }"
        val spannable: Spannable = SpannableString(existsMessage)

        val cartTitle = getString(R.string.title_cart)
        val startIndex = existsMessage.indexOf(cartTitle)
        val endIndex = startIndex + cartTitle.length

        spannable.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        existingMessage.setText(spannable, TextView.BufferType.SPANNABLE)

    }

    private fun onClickButtons() {

        binding.closeBtn.setOnClickListener { dismiss() }

        addBtn.setOnClickListener {
            loading(true)
            changeQuantityListener?.onAdd(product, -1, null, requireActivity())
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        val dotIndicators = binding.dotsIndicator
        val viewPager = binding.sliderView
        val productTitle = binding.productTitleTxt
        val offPrice = binding.offPriceTxt
        val finalPrice = binding.finalPriceTxt
        val productDescription = binding.productDescriptionTxt

        with(product) {
            viewPager.adapter = ProductSliderAdapter(image)
            dotIndicators.visibility = if (image.size > 1) View.VISIBLE else View.GONE
            dotIndicators.attachTo(viewPager)

            productTitle.text = title
            productDescription.text = description
            if (off_percent > 0) {
                offPrice.visibility = View.VISIBLE
                discountPercentage.visibility = View.VISIBLE
            } else {
                offPrice.visibility = View.GONE
                discountPercentage.visibility = View.GONE
            }
            offPrice.text = Helper.getStrikethroughPriceWithCurrency(amount, "")
            discountPercentage.text =
                "%${MainActivity().convertToPersianNumbers(off_percent.toString())}"
            finalPrice.text =
                "${formatPriceToPersianAmount()} ${getString(R.string.title_currency)}"
        }

    }

    private fun getAllCart() {
        cartViewModel.cartsStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> updateUI(result.response.data, product)
                is NetworkResult.Error -> loading()
            }
        }
    }

    private fun updateUI(cartList: List<CartModel>, product: ProductModel) {
        val isExists = checkExistsItemInCart(cartList.toMutableList(), product)
        binding.addLayout.visibility = if (isExists) View.INVISIBLE else View.VISIBLE
        existingMessage.visibility = if (isExists) View.VISIBLE else View.INVISIBLE
        loading()
    }

    private fun checkExistsItemInCart(
        cartList: MutableList<CartModel>,
        item: ProductModel,
    ): Boolean {
        cartList.forEach { cart ->
            if (cart.card_details.any { detail -> detail.item_id == item.id }) {
                return true
            }
        }
        return false
    }

    private fun loading(isLoading: Boolean = false) {
        val progressLoading = binding.loadingProgress
        if (isLoading) {
            addBtn.isEnabled = false
            progressLoading.visibility = View.VISIBLE
        } else {
            addBtn.isEnabled = true
            progressLoading.visibility = View.GONE
        }
    }

}