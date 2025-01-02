package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.CartActivity
import com.se7en.market.Adapter.ShopOrderAdapter
import com.se7en.market.Model.CartModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Constance.Companion.KEY_ID
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.databinding.FragmentCartBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()
    private var orderAdapter: ShopOrderAdapter? = null

    private lateinit var cartView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        getAllCarts()

    }

    private fun initViews() {
        cartView = binding.cartView
    }

    @SuppressLint("SetTextI18n")
    private fun setupCartView(cartList: MutableList<CartModel>) {
        orderAdapter = ShopOrderAdapter(cartList, { cartId: Int, position: Int ->
            deleteCart(cartId, position)
        }, { cart: CartModel ->
            cartViewModel.getCartDetails(cart.id)
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra(KEY_ID, cart.id)
            startActivity(intent)
        })
        cartView.adapter = orderAdapter
    }

    private fun getAllCarts() {
        cartViewModel.cartsStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    Log.i(TAG, "setupCartView: Loading")
                    manageSkeletonLoading(true)
                }

                is NetworkResult.Success -> {
                    manageSkeletonLoading()
                    Log.i(TAG, "setupCartView: Response ${result.response}")
                    val carts = result.response.data
                    if (carts.size > 0) {
                        manageView(false)
                        setupCartView(carts)
                        val cartCount = result.response.data.count()
                        binding.cartCountTxt.text =
                            if (cartCount > 0) "$cartCount سبد" else getString(R.string.sub_cart_empty)
                    } else
                        manageView()
                }

                is NetworkResult.Error -> {
                    Log.i(TAG, "getAllCarts: ${result.message}")
                    manageView()
                    manageSkeletonLoading()
                }
            }
        }
    }

    private fun deleteCart(cartId: Int, position: Int) {
        cartViewModel.deleteCart(cartId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    orderAdapter?.loadingItem(position)
                }

                is NetworkResult.Success -> {
                    orderAdapter?.loadingItem(position)
                    orderAdapter?.deleteItem(position)
                    cartViewModel.getAllCarts()
                }

                is NetworkResult.Error -> {
                    orderAdapter?.loadingItem(position)
                }
            }
        }
    }

    companion object {
        private const val TAG = "CartFragment"
    }

    private fun manageSkeletonLoading(loading: Boolean = false) {
        if (loading)
            cartView.loadSkeleton(R.layout.view_holder_cart_list)
        else
            cartView.hideSkeleton()
    }

    private fun manageView(emptyCart: Boolean = true) {
        val emptyLayout = binding.emptyLayout
        if (emptyCart) {
            emptyLayout.visibility = View.VISIBLE
            cartView.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.GONE
            cartView.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}