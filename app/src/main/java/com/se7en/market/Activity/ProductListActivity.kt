package com.se7en.market.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Adapter.ProductWithHeaderAdapter
import com.se7en.market.Model.CategoryModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.Utils.Constance.Companion.KEY_CATEGORY
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.ViewModel.ShopViewModel
import com.se7en.market.databinding.ActivityProductListBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class ProductListActivity : BaseActivity() {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var categoryData: CategoryModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductWithHeaderAdapter

    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()
    private val shopViewModel: ShopViewModel by viewModels<ShopViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryData = intent.getParcelableExtra(KEY_CATEGORY)!!
        shopViewModel.getCategoryItems(categoryData.id)
        initViews()
        buttonsClick()
        getCategoryItems()

    }

    private fun initViews() {

        recyclerView = binding.containerView
        binding.titleTxt.text = categoryData.title

    }

    private fun buttonsClick() {

        binding.backBtn.setOnClickListener { finish() }
        binding.searchBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SearchActivity::class.java
                )
            )
        }

    }

    @Suppress("UNCHECKED_CAST")
    private fun getCategoryItems() {
        shopViewModel.categoryItemStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> recyclerView.loadSkeleton(R.layout.view_holder_product)

                is NetworkResult.Success -> {
                    recyclerView.hideSkeleton()
                    adapter = ProductWithHeaderAdapter(
                        (result.response.data as MutableList<ListItemModel>),
                        "",
                        null,
                        getColor(R.color.green),
                        false,
                        CartManager,
                        binding.root,
                        true
                    )
                    recyclerView.layoutManager = GridLayoutManager(this, 2)
                    recyclerView.adapter = adapter
                    getAllCart()
                }

                is NetworkResult.Error -> {
                    Log.i(TAG, "getCategoryItems: ${result.message}")
                    recyclerView.hideSkeleton()
                }
            }
        }
    }

    private fun getAllCart() {
        cartViewModel.cartsStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {}

                is NetworkResult.Success -> {
                    adapter.syncCartWithProducts(result.response.data)
                }

                is NetworkResult.Error -> {}
            }
        }
    }

    companion object {
        private const val TAG = "ProductListActivity"
    }

}