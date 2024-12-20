package com.se7en.market.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Adapter.ShopWithProductAdapter
import com.se7en.market.Model.CategoryModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Utils.Constance.Companion.KEY_CATEGORY
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.ViewModel.CategoryViewModel
import com.se7en.market.databinding.ActivityCategoryBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var categoryData: CategoryModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShopWithProductAdapter

    private val categoryViewModel: CategoryViewModel by viewModels<CategoryViewModel>()
    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryData = intent.getParcelableExtra(KEY_CATEGORY)!!
        initViews()
        buttonsClick()
        getActiveAddress()
        getGlobalCategoryItem()

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

    private fun getActiveAddress() {
        categoryViewModel.loadActiveAddress().observe(this) { address ->
            Log.i(TAG, "getActiveAddress: ${categoryData.id}")
            if (address != null)
                categoryViewModel.getGlobalCategoryItem(
                    categoryData.id,
                    mapOf("latitude" to address.latitude, "longitude" to address.longitude)
                )

        }
    }

    private fun getGlobalCategoryItem() {
        categoryViewModel.globalCategoryStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> recyclerView.loadSkeleton()

                is NetworkResult.Success -> {
                    recyclerView.hideSkeleton()
                    Log.i(TAG, "getGlobalCategoryItem: ${result.response}")
                    adapter = ShopWithProductAdapter(result.response.data, recyclerView)
                    recyclerView.adapter = adapter
                    getAllCart()
                }

                is NetworkResult.Error -> {
                    Log.i(TAG, "getGlobalCategoryItem: ${result.message}")
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
        private const val TAG = "CategoryActivity"
    }
}