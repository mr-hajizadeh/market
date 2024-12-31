package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.se7en.market.Activity.BaseActivity
import com.se7en.market.Activity.CartActivity
import com.se7en.market.Model.AddressModel
import com.se7en.market.Model.CartDetailModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.ViewModel.AddressViewModel
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.databinding.FragmentPayBinding

class PayFragment : Fragment() {

    private var _binding: FragmentPayBinding? = null
    private val binding get() = _binding!!

    private val addressViewModel: AddressViewModel by viewModels<AddressViewModel>()
    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()

    private lateinit var onlinePay: MaterialCardView
    private lateinit var walletPay: MaterialCardView
    private lateinit var sitePay: MaterialCardView
    private lateinit var activeAddress: AddressModel

    private var cards: List<MaterialCardView> = emptyList()

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
        setAddress()
        getCartDetail()

    }

    private fun initViews() {

        onlinePay = binding.onlinePaymentCard
        walletPay = binding.walletPaymentCard
        sitePay = binding.sitePaymentCard
        cards = listOf(onlinePay, walletPay, sitePay)

        val placeAddress = binding.placeAddressTxt
        val receiveTime = binding.receiveTimeTxt
        placeAddress.text = convertToPersianNumbers(placeAddress.text.toString())
        receiveTime.text = convertToPersianNumbers(receiveTime.text.toString())

        binding.discountCodeEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                convertToPersianNumbers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun onClickButtons() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
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
    private fun calculate(cartDetail: MutableList<CartDetailModel>, courierFee: Int) {
        val totalProductPrice = cartDetail.sumOf { it.item.offPrice() * it.quantity }.toLong()

        val productCount = cartDetail.sumOf { it.quantity }

        val buyerProfit = cartDetail.sumOf {
            (it.item.amount * it.item.off_percent / 100) * it.quantity
        }

        val serviceCost = 3000L
        val totalPrice = totalProductPrice + serviceCost + courierFee

        binding.totalProductTxt.text =
            "${requireActivity().getString(R.string.title_total_items)} (${
                BaseActivity().convertToPersianNumbers(productCount.toString())
            })"
        binding.totalProductPriceTxt.text = Helper.formatPriceToPersian(totalProductPrice)
        binding.profitTxt.text = Helper.formatPriceToPersian(buyerProfit)
        binding.discountPriceTxt.text = Helper.formatPriceToPersian(0)
        binding.serviceCostPriceTxt.text = Helper.formatPriceToPersian(serviceCost)
        binding.totalPriceTxt.text = Helper.formatPriceToPersian(totalPrice)
    }


    private fun setAddress() {
        addressViewModel.getActiveAddress().observe(viewLifecycleOwner) { address ->
            activeAddress = address!!
            binding.placeAddressTxt.text = activeAddress.address
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getCartDetail() {
        cartViewModel.cartDetailStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> Log.i(TAG, "getCartDetail: Loading")

                is NetworkResult.Success -> {
                    Log.i(TAG, "getCartDetail: Success ${result.response}")
                    val shop = cartViewModel.getShopById(result.response.data.seller_id)
                    var courierFee = 0
                    shop?.let {
                        binding.receiveTimeTxt.text =
                            "${shop.delivery_time} ${requireActivity().getString(R.string.title_minute)}"
                        courierFee = shop.courier_fee
                        if (courierFee > 0) {
                            binding.courierFeeTxt.text = shop.formatPriceToPersianCourierFee()
                            binding.courierFeeCurrencyTxt.visibility = View.VISIBLE
                        } else {
                            binding.courierFeeTxt.text =
                                requireActivity().getString(R.string.title_free)
                            binding.courierFeeCurrencyTxt.visibility = View.GONE
                        }
                    }
                    calculate(result.response.data.card_details, courierFee)
                }

                is NetworkResult.Error -> Log.i(TAG, "getCartDetail: Error ${result.message}")
            }
        }
    }

    companion object {
        private const val TAG = "PayFragment"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}