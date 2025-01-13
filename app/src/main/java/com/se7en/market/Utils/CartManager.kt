package com.se7en.market.Utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.se7en.market.Adapter.CartAdapter
import com.se7en.market.Adapter.ChangeQuantityListener
import com.se7en.market.Adapter.ProductWithHeaderAdapter
import com.se7en.market.Model.AddressModel
import com.se7en.market.Model.CartDetailModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.ViewModel.CartViewModel

object CartManager : ChangeQuantityListener {

    private const val TAG = "CartManager"

    private lateinit var viewModel: CartViewModel
    private lateinit var lifecycleOwner: LifecycleOwner

    fun setValues(viewModel: CartViewModel, lifecycleOwner: LifecycleOwner) {
        this.viewModel = viewModel
        this.lifecycleOwner = lifecycleOwner
    }

    var activeAddress: AddressModel = AddressModel()

    override fun onAdd(
        product: ProductModel,
        position: Int,
        adapter: ProductWithHeaderAdapter?,
        activity: Activity,
    ) {

        if (!::viewModel.isInitialized) return

        viewModel.createOrAddItemToCart(product.id, activeAddress.id)
            .observe(lifecycleOwner) { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        if (adapter != null && position != -1)
                            adapter.updateItem(position, product)
                        Log.i(TAG, "Loading: position $position")
                        Log.i(TAG, "Loading: id=${product.id} loading=${product.loading} \n")
                    }

                    is NetworkResult.Success -> {
                        viewModel.getAllCarts()
                        result.response.data.card_details.find { it.item_id == product.id }
                            ?.let {
                                product.quantity = it.quantity
                                product.cartDetail = it
                            }
                        if (adapter != null && position != -1)
                            updateItemWithDelay(position, adapter, product)

                        Log.i(TAG, "onAdd: $activity")
                        showSnackBar(activity, result.response.messages[0])
                        Log.i(TAG, "Success: position $position")
                        Log.i(TAG, "Success: id=${product.id} loading=${product.loading} \n")
                    }

                    is NetworkResult.Error -> {
                        if (adapter != null && position != -1)
                            updateItemWithDelay(position, adapter, product)
                        showSnackBar(activity, result.message)
                        Log.e(TAG, "Failed to add product: ${result.message}")
                    }
                }
            }
    }

    override fun <T : RecyclerView.Adapter<RecyclerView.ViewHolder>> incrementItem(
        cartDetail: CartDetailModel,
        product: ProductModel,
        position: Int,
        adapter: T,
    ) {
        Log.i(TAG, "incrementItem called with adapter=${adapter::class.java}")
        viewModel.incrementItemQuantity(cartDetail.card_id, cartDetail.item_id)
            .observe(lifecycleOwner) { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        Log.i(TAG, "incrementItemQuantity: Loading")
                        when (adapter) {
                            is CartAdapter -> {
                                Log.i(TAG, "incrementItem: CartAdapter")
                                adapter.loadingQuantity(position, cartDetail)
                            }

                            is ProductWithHeaderAdapter ->
                                adapter.loadingQuantity(position, product)
                        }
                    }

                    is NetworkResult.Success -> {
                        Log.i(TAG, "incrementItemQuantity: Success ${result.response}")
                        val cartDetailsList = result.response.data.card_details
                        cartDetailsList.find { it.item_id == cartDetail.item_id }?.let {
                            cartDetail.quantity = it.quantity
                            product.quantity = it.quantity
                        }
                        when (adapter) {
                            is ProductWithHeaderAdapter -> {
                                viewModel.getAllCarts()
                                adapter.loadingQuantity(position, product)
                                adapter.updateQuantity(position, cartDetail)
                            }

                            is CartAdapter -> {
                                adapter.loadingQuantity(position, cartDetail)
                                adapter.updateQuantity(position, result.response.data)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        Log.i(TAG, "incrementItemQuantity: Error ${result.message}")
                        when (adapter) {
                            is ProductWithHeaderAdapter ->
                                adapter.loadingQuantity(position, product)

                            is CartAdapter ->
                                adapter.loadingQuantity(position, cartDetail)
                        }
                    }
                }
            }
    }

    override fun <T : RecyclerView.Adapter<RecyclerView.ViewHolder>> decrementItem(
        cartDetail: CartDetailModel,
        product: ProductModel,
        position: Int,
        adapter: T,
    ) {
        viewModel.decrementItemQuantity(cartDetail.card_id, product.id)
            .observe(lifecycleOwner) { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        Log.i(TAG, "decrementItemQuantity: Loading")
                        when (adapter) {
                            is ProductWithHeaderAdapter ->
                                adapter.loadingQuantity(position, product)

                            is CartAdapter ->
                                adapter.loadingQuantity(position, cartDetail)
                        }
                    }

                    is NetworkResult.Success -> {
                        Log.i(TAG, "decrementItemQuantity: Success ${result.response}")
                        val cartDetailsList = result.response.data.card_details
                        cartDetailsList.find { it.item_id == product.id }?.let {
                            cartDetail.quantity = it.quantity
                            product.quantity = it.quantity
                        }
                        when (adapter) {
                            is ProductWithHeaderAdapter -> {
                                viewModel.getAllCarts()
                                adapter.loadingQuantity(position, product)
                                adapter.updateQuantity(position, cartDetail)
                                if (cartDetailsList.none { it.item_id == product.id })
                                    adapter.notFoundItem(position, product)
                            }

                            is CartAdapter -> {
                                adapter.loadingQuantity(position, cartDetail)
                                adapter.updateQuantity(position, result.response.data)
                                if (cartDetailsList.none { it.item_id == product.id })
                                    adapter.retainList(result.response.data)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        Log.i(TAG, "decrementItemQuantity: Error ${result.message}")
                        when (adapter) {
                            is ProductWithHeaderAdapter ->
                                adapter.loadingQuantity(position, product)

                            is CartAdapter ->
                                adapter.loadingQuantity(position, cartDetail)
                        }
                    }
                }
            }
    }

    private fun updateItemWithDelay(
        position: Int,
        adapter: ProductWithHeaderAdapter,
        product: ProductModel,
    ) {
        Handler(Looper.getMainLooper()).postDelayed({
            adapter.updateItem(position, product)
            Log.i(TAG, "Error: $product")
        }, 500)
    }

    private fun showSnackBar(activity: Activity, message: String) {
        Snackbar.make(
            activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT
        ).show()
    }

}