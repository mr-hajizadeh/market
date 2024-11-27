package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.card.MaterialCardView
import com.se7en.market.Activity.CartActivity
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.databinding.FragmentPayBinding

class PayFragment : Fragment() {

    private var _binding: FragmentPayBinding? = null
    private val binding get() = _binding!!

    private lateinit var onlinePay: MaterialCardView
    private lateinit var walletPay: MaterialCardView
    private lateinit var sitePay: MaterialCardView

    private var cards: List<MaterialCardView> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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


        binding.totalProductPriceTxt.text = Helper.formatPriceToPersian(170000)
        binding.profitTxt.text = Helper.formatPriceToPersian(5000)
        binding.discountPriceTxt.text = Helper.formatPriceToPersian(0)
        binding.serviceCostPriceTxt.text = Helper.formatPriceToPersian(3000)
        binding.totalPriceTxt.text = Helper.formatPriceToPersian(165000)

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
        selectedCard: MaterialCardView
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

}