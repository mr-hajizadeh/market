package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.se7en.market.Activity.BaseActivity
import com.se7en.market.Activity.CartActivity
import com.se7en.market.Activity.CartActivity.Companion.cartId
import com.se7en.market.Model.AddressModel
import com.se7en.market.Model.CartModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.ViewModel.AddressViewModel
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.databinding.FragmentPayBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class PayFragment : Fragment() {

    private var _binding: FragmentPayBinding? = null
    private val binding get() = _binding!!

    private val addressViewModel: AddressViewModel by viewModels<AddressViewModel>()
    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()

    private lateinit var onlinePay: MaterialCardView
    private lateinit var walletPay: MaterialCardView
    private lateinit var sitePay: MaterialCardView
    private lateinit var submitDiscountBtn: Button
    private lateinit var discountCodeEdt: EditText

    private var cards: List<MaterialCardView> = emptyList()
    private var discountCodeStatus = MutableLiveData<Boolean>().apply { value = false }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        onClickButtons()
        managementPayButtons(cards, selectedCard = onlinePay)
        onChangePayment(cards)
        getCartDetail()

    }

    private fun initViews() {

        onlinePay = binding.onlinePaymentCard
        walletPay = binding.walletPaymentCard
        sitePay = binding.sitePaymentCard
        submitDiscountBtn = binding.submitDiscountBtn
        discountCodeEdt = binding.discountCodeEdt
        cards = listOf(onlinePay, walletPay, sitePay)

        val placeAddress = binding.placeAddressTxt
        val receiveTime = binding.receiveTimeTxt
        placeAddress.text = convertToPersianNumbers(placeAddress.text.toString())
        receiveTime.text = convertToPersianNumbers(receiveTime.text.toString())

        discountCodeStatus.observe(viewLifecycleOwner) { isSet ->
            if (isSet) {
                submitDiscountBtn.setBackgroundColor(requireActivity().getColor(R.color.red))
                submitDiscountBtn.text =
                    requireActivity().getString(R.string.title_remove_discount_code)
            } else {
                submitDiscountBtn.setBackgroundColor(requireActivity().getColor(R.color.green))
                submitDiscountBtn.text =
                    requireActivity().getString(R.string.title_submit_discount_code)
                discountCodeEdt.text.clear()
            }
        }

    }

    private fun onClickButtons() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        submitDiscountBtn.setOnClickListener {
            val discountCode = discountCodeEdt.text.toString()
            if (discountCodeStatus.value == false && discountCode.isNotEmpty()) {
                val mapData = mapOf("code" to discountCode)
                setDiscountCode(cartId, mapData)
            } else
                unsetDiscountCode(cartId)
        }

    }

    private fun onChangePayment(cards: List<MaterialCardView>) {
        cards.forEach { card ->
            card.setOnClickListener { managementPayButtons(cards, selectedCard = card) }
        }
    }

    private fun convertToPersianNumbers(input: String) =
        CartActivity().convertToPersianNumbers(input)

    private fun managementPayButtons(
        cards: List<MaterialCardView>,
        selectedCard: MaterialCardView,
    ) {
        cards.forEach { card ->
            card.isChecked = false
            when (card.id) {
                walletPay.id -> binding.walletImg.setBackgroundColor(
                    context?.getColor(R.color.text_color) ?: Color.GRAY
                )

                onlinePay.id -> binding.onlineImg.setBackgroundColor(
                    context?.getColor(R.color.text_color) ?: Color.GRAY
                )

                sitePay.id -> binding.siteImg.setBackgroundColor(
                    context?.getColor(R.color.text_color) ?: Color.GRAY
                )
            }
        }

        selectedCard.isChecked = true
        when (selectedCard.id) {
            walletPay.id -> binding.walletImg.setBackgroundColor(
                context?.getColor(R.color.green) ?: Color.GREEN
            )

            onlinePay.id -> binding.onlineImg.setBackgroundColor(
                context?.getColor(R.color.green) ?: Color.GREEN
            )

            sitePay.id -> binding.siteImg.setBackgroundColor(
                context?.getColor(R.color.green) ?: Color.GREEN
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initCartDetails(cartModel: CartModel) {

        if (cartModel.discount_price > 0)
            discountCodeStatus.postValue(true)

        binding.totalProductPriceTxt.text =
            Helper.formatPriceToPersian((cartModel.total_price - cartModel.total_off))
        binding.profitTxt.text =
            Helper.formatPriceToPersian((cartModel.total_off + cartModel.discount_price))
        binding.discountPriceTxt.text =
            Helper.formatPriceToPersian(cartModel.discount_price)
        binding.serviceCostPriceTxt.text =
            Helper.formatPriceToPersian(cartModel.courier_fee)
        binding.finalPriceTxt.text =
            Helper.formatPriceToPersian(cartModel.final_price)

    }

    private fun getAddressById(addressId: Int): LiveData<AddressModel?> {
        val liveData = MutableLiveData<AddressModel?>()
        addressViewModel.listStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val address = result.response.data.find { it.id == addressId }
                    liveData.value = address
                }

                is NetworkResult.Error -> liveData.value = null
                else -> {}
            }
        }
        return liveData
    }

    @SuppressLint("SetTextI18n")
    private fun getCartDetail() {
        cartViewModel.getCartDetails(cartId)
        cartViewModel.cartDetailStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    binding.linearLayout.loadSkeleton()
                    binding.bottomBarLayout.loadSkeleton()
                }

                is NetworkResult.Success -> {
                    Log.i(TAG, "getCartDetail: Success ${result.response}")
                    result.response.data.also { cartModel ->
                        val shop = cartViewModel.getShopById(cartModel.seller_id)
                        val address = getAddressById(cartModel.address_id)
                        shop?.let {
                            binding.receiveTimeTxt.text =
                                "${shop.delivery_time} ${requireActivity().getString(R.string.title_minute)}"
                            if (shop.courier_fee > 0) {
                                binding.courierFeeTxt.text = shop.formatPriceToPersianCourierFee()
                                binding.courierFeeCurrencyTxt.visibility = View.VISIBLE
                            } else {
                                binding.courierFeeTxt.text =
                                    requireActivity().getString(R.string.title_free)
                                binding.courierFeeCurrencyTxt.visibility = View.GONE
                            }
                        }
                        address.observe(viewLifecycleOwner) {
                            binding.placeAddressTxt.text = it?.address
                        }
                        val productCount = cartModel.card_details.sumOf { it.quantity }
                        binding.totalProductTxt.text =
                            "${requireActivity().getString(R.string.title_total_items)} (${
                                BaseActivity().convertToPersianNumbers(productCount.toString())
                            })"
                        initCartDetails(cartModel)
                        binding.linearLayout.hideSkeleton()
                        binding.bottomBarLayout.hideSkeleton()
                    }
                }

                is NetworkResult.Error -> {
                    binding.linearLayout.hideSkeleton()
                    binding.bottomBarLayout.hideSkeleton()
                    Log.i(TAG, "getCartDetail: Error ${result.message}")
                }
            }
        }
    }

    private fun setDiscountCode(cartId: Int, mapData: Map<String, String>) {
        cartViewModel.setDiscountCode(cartId, mapData).observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> discountLoading(true)

                is NetworkResult.Success -> {
                    discountCodeStatus.postValue(true)
                    result.response.messages.takeIf { it.isNotEmpty() }?.let {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            it[0],
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    result.response.data.let { cartModel -> initCartDetails(cartModel) }
                    discountLoading(false)
                }

                is NetworkResult.Error -> {
                    discountLoading(false)
                }
            }
        }
    }

    private fun unsetDiscountCode(cartId: Int) {
        cartViewModel.unsetDiscountCode(cartId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> discountLoading(true)

                is NetworkResult.Success -> {
                    discountCodeStatus.postValue(false)
                    result.response.messages.takeIf { it.isNotEmpty() }?.let {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            it[0],
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    result.response.data.let { cartModel -> initCartDetails(cartModel) }
                    discountLoading(false)
                }

                is NetworkResult.Error -> {
                    discountLoading(false)
                }
            }
        }
    }

    private fun discountLoading(isLoading: Boolean = false) {
        binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        submitDiscountBtn.isEnabled = !isLoading
    }

    companion object {
        private const val TAG = "PayFragment"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}