package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.se7en.market.Activity.CartActivity
import com.se7en.market.Adapter.CartAdapter
import com.se7en.market.Model.ProductModel
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.databinding.FragmentOrderDetailBinding

class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var totalPriceTxt: TextView
    private lateinit var totalProductPriceTxt: TextView
    private lateinit var profitTxt: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        onClickButtons()
        setupOrdersView()

    }

    private fun initViews() {

        totalPriceTxt = binding.totalPriceTxt
        totalProductPriceTxt = binding.totalProductPriceTxt
        profitTxt = binding.profitTxt

    }

    private fun onClickButtons() {

        binding.backBtn.setOnClickListener { requireActivity().finish() }

        binding.completionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_payFragment)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setupOrdersView() {

        val ordersView = binding.ordersView
        val totalItems = binding.totalItemsTxt
        val list: MutableList<ProductModel> = mutableListOf(
            ProductModel(1, off_percent = 40, count = 2),
            ProductModel(2, off_percent = 50, count = 3),
            ProductModel(3, off_percent = 10, count = 4),
        )


        ordersView.adapter = CartAdapter(list, 1, { count: Int ->
            totalItems.text = "${getString(R.string.title_total_items)} ($count کالا)"
        }, { totalPrice: Long, profitPrice: Long ->
            totalProductPriceTxt.text = Helper.formatPriceToPersian(totalPrice)
            profitTxt.text = Helper.formatPriceToPersian(profitPrice)
            totalPriceTxt.text = Helper.formatPriceToPersian((totalPrice - profitPrice))
        })

    }


}