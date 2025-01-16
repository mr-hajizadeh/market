package com.se7en.market.Activity.Ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.se7en.market.Activity.BaseActivity
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.ViewModel.ProfileViewModel
import com.se7en.market.databinding.ModalWalletBinding
import java.text.DecimalFormat

class ModalWallet(private val paymentUrl: (url: String) -> Unit) :
    BottomSheetDialogFragment() {

    private var _binding: ModalWalletBinding? = null
    private val binding get() = _binding!!

    private lateinit var minCard: MaterialCardView
    private lateinit var mediumCard: MaterialCardView
    private lateinit var highCard: MaterialCardView
    private lateinit var amountEdt: EditText
    private lateinit var paymentBtn: Button

    private var amount: Long = 0
    private var cards: List<MaterialCardView> = emptyList()

    private val viewModel: ProfileViewModel by viewModels<ProfileViewModel>()
    private val defaultAmount: List<Long> = listOf(50000, 110000, 200000)

    companion object {
        const val TAG = "ModalWallet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ModalWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        onClickButtons()
        managementPayButtons(cards, selectedCard = minCard)

    }


    @SuppressLint("SetTextI18n")
    private fun initViews() {
        minCard = binding.cardMinPrice
        mediumCard = binding.cardMediumPrice
        highCard = binding.cardHighPrice
        amountEdt = binding.amountEdt
        paymentBtn = binding.paymentBtn
        cards = listOf(minCard, mediumCard, highCard)
        onChangePayment(cards)

        val minPriceTxt = binding.minPriceTxt
        val mediumPriceTxt = binding.mediumPriceTxt
        val highPriceTxt = binding.highPriceTxt

        minPriceTxt.text =
            "${formatPriceToPersianAmount(defaultAmount[0].toString())} ${getString(R.string.title_currency)}"
        mediumPriceTxt.text =
            "${formatPriceToPersianAmount(defaultAmount[1].toString())} ${getString(R.string.title_currency)}"
        highPriceTxt.text =
            "${formatPriceToPersianAmount(defaultAmount[2].toString())} ${getString(R.string.title_currency)}"

        amountEdt.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isFormatting && !s.isNullOrEmpty()) {
                    isFormatting = true
                    try {
                        val formattedText =
                            formatPriceToPersianAmount(s.toString().replace(",", ""))
                        amountEdt.setText(formattedText)
                        amountEdt.setSelection(formattedText.length)
                        amount =
                            BaseActivity().convertToEnglishNumbers(formattedText.replace(",", ""))
                                .toLong()
                        managementPayButtons(cards, null)
                    } catch (e: Exception) {
                        Log.i(TAG, "onTextChanged: ${e.message}")
                        e.printStackTrace()
                    } finally {
                        isFormatting = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun formatPriceToPersianAmount(value: String): String {
        val decimalFormat = DecimalFormat("#,###")
        val number = value.toLongOrNull() ?: return value
        val formattedPrice = decimalFormat.format(number)

        val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        return formattedPrice.map {
            if (it.isDigit()) persianDigits[it.toString().toInt()] else it
        }.joinToString("")
    }

    private fun onClickButtons() {

        binding.closeBtn.setOnClickListener { dismiss() }

        binding.paymentBtn.setOnClickListener {
            val amountValue = amountEdt.text.toString()
            if (amountValue.length <= 3) {
                val message =
                    "مبلغ نمی‌تواند کمتر از ${formatPriceToPersianAmount("10000")} تومان باشد."
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()

            } else
                viewModel.incrementWallet(mapOf("amount" to amount))
                    .observe(requireActivity()) { result ->
                        when (result) {
                            is NetworkResult.Loading -> paymentLoading(true)

                            is NetworkResult.Success -> {
                                paymentLoading()
                                paymentUrl(result.response.data.payment_url.action)
                            }

                            is NetworkResult.Error -> {
                                paymentLoading()
                                Toast.makeText(
                                    requireActivity(),
                                    result.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun managementPayButtons(
        cards: List<MaterialCardView>,
        selectedCard: MaterialCardView? = null,
    ) {
        cards.forEach { card ->
            card.isChecked = selectedCard?.let { it == card } ?: (card.tag == amount)
        }

        selectedCard?.let {
            when (it.id) {
                minCard.id -> amountEdt.setText(defaultAmount[0].toString())
                mediumCard.id -> amountEdt.setText(defaultAmount[1].toString())
                highCard.id -> amountEdt.setText(defaultAmount[2].toString())
            }
        }
    }



    private fun onChangePayment(cards: List<MaterialCardView>) {
        cards.forEachIndexed { index, card ->
            card.tag = defaultAmount[index]
            card.setOnClickListener { managementPayButtons(cards, selectedCard = card) }
        }
    }

    private fun paymentLoading(isLoading: Boolean = false) {
        binding.loadingProgress.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        paymentBtn.isEnabled = !isLoading
    }

}