package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.CartActivity.Companion.cartId
import com.se7en.market.Activity.CartActivity.Companion.isOldSizeSet
import com.se7en.market.Activity.CartActivity.Companion.newCartSize
import com.se7en.market.Activity.CartActivity.Companion.oldCartSize
import com.se7en.market.Adapter.CartAdapter
import com.se7en.market.Model.CartModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.Utils.Helper
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.databinding.FragmentOrderDetailBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()

    private lateinit var finalPriceTxt: TextView
    private lateinit var totalProductPriceTxt: TextView
    private lateinit var profitTxt: TextView
    private lateinit var adapter: CartAdapter
    private lateinit var ordersView: RecyclerView

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
        setupOrdersView()
        onClickButtons()
        getCartDetail()
    }

    private fun initViews() {

        finalPriceTxt = binding.finalPriceTxt
        totalProductPriceTxt = binding.totalProductPriceTxt
        profitTxt = binding.profitTxt
        ordersView = binding.ordersView

    }

    private fun onClickButtons() {

        binding.backBtn.setOnClickListener { requireActivity().finish() }

        binding.completionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_payFragment)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setupOrdersView() {
        val totalItems = binding.totalItemsTxt

        CartManager.setValues(cartViewModel, viewLifecycleOwner)
        adapter = CartAdapter(1, CartManager,
            { count: Int ->
                totalItems.text = "${getString(R.string.title_total_items)} ($count کالا)"
                newCartSize = count
            }, { cart: CartModel ->
                adapter.setCart(cart)
                if (cart.card_details.isEmpty())
                    requireActivity().finish()
                val finalPrice = cart.card_details.sumOf { it.final_price }
                totalProductPriceTxt.text = Helper.formatPriceToPersian(finalPrice)
                profitTxt.text = Helper.formatPriceToPersian(cart.total_off)
                finalPriceTxt.text = Helper.formatPriceToPersian(cart.final_price)
            })

        ordersView.adapter = adapter
    }

    private fun getCartDetail() {
        cartViewModel.cartDetailStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    manageSkeletonLoading(true)
                }

                is NetworkResult.Success -> {
                    Log.i(TAG, "getCartDetail: Success ${result.response}")
                    val cart: CartModel = result.response.data
                    val cartCount = result.response.data.card_details.sumOf { it.quantity }
                    if (!isOldSizeSet) {
                        oldCartSize = cartCount
                        isOldSizeSet = true
                    }
                    newCartSize = cartCount
                    adapter.setCart(cart)
                    adapter.setCartList(cart.card_details)
                    manageSkeletonLoading(false)
                }

                is NetworkResult.Error -> {
                    Log.i(TAG, "getCartDetail: Error ${result.message}")
                    if (result.message.contains("404")) {
                        requireActivity().finish()
                        cartViewModel.getAllCarts()
                    }
                    manageSkeletonLoading(false)
                }
            }
        }
        cartViewModel.getCartDetails(cartId)
    }

    private fun manageSkeletonLoading(loading: Boolean = false) {
        val containerView = binding.containerView
        val bottomBarLayout = binding.bottomBarLayout
        if (loading) {
            ordersView.loadSkeleton(R.layout.view_holder_product_cart_with_buttons) {
                itemCount(3)
            }
            containerView.loadSkeleton()
            bottomBarLayout.loadSkeleton()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                containerView.hideSkeleton()
                bottomBarLayout.hideSkeleton()
                ordersView.hideSkeleton()
            }, 1000)
        }
    }

    companion object {
        private const val TAG = "OrderDetailFragment"
    }
}