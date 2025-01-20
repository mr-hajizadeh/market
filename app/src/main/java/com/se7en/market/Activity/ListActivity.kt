package com.se7en.market.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Adapter.ProductWithHeaderAdapter
import com.se7en.market.Adapter.ShopAdapter
import com.se7en.market.Model.CategoryModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.MainListModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Model.SearchTags
import com.se7en.market.Model.ShopModel
import com.se7en.market.Model.SliderModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.Utils.Constance.Companion.KEY_CATEGORY
import com.se7en.market.Utils.Constance.Companion.KEY_PRODUCT_LIST
import com.se7en.market.Utils.Constance.Companion.KEY_SHOP
import com.se7en.market.Utils.Constance.Companion.KEY_SLIDER
import com.se7en.market.Utils.Constance.Companion.KEY_TITLE
import com.se7en.market.ViewModel.AddressViewModel
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.ViewModel.SearchViewModel
import com.se7en.market.ViewModel.ShopViewModel
import com.se7en.market.databinding.ActivityListBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class ListActivity : BaseActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var titleTxt: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyLayout: LinearLayout
    private lateinit var adapter: ProductWithHeaderAdapter

    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()
    private val shopViewModel: ShopViewModel by viewModels<ShopViewModel>()
    private val searchViewModel: SearchViewModel by viewModels<SearchViewModel>()
    private val addressViewModel: AddressViewModel by viewModels<AddressViewModel>()

    @SuppressLint("SetTextI18n")
    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        buttonsClick()
        getCategoryItemsListener()
        globalSearch()

        val categoryData: CategoryModel? = intent.getParcelableExtra(KEY_CATEGORY)
        val mainListModel: MainListModel? = intent.getParcelableExtra(KEY_PRODUCT_LIST)
        val productList = intent.getParcelableArrayListExtra<ProductModel>(KEY_PRODUCT_LIST)
        val shopModel: ArrayList<ShopModel>? = intent.getParcelableArrayListExtra(KEY_SHOP)
        val slider: SliderModel? = intent.getParcelableExtra(KEY_SLIDER)

        when {
            categoryData != null -> {
                titleTxt.text = categoryData.title
                shopViewModel.getCategoryItems(categoryData.id)
            }

            mainListModel != null -> {
                titleTxt.text = mainListModel.title
                initProductRecyclerView(
                    (mainListModel.items as MutableList<ProductModel>),
                    mainListModel.color
                )
                window.statusBarColor = mainListModel.color
                recyclerView.hideSkeleton()
            }

            productList != null -> {
                titleTxt.text = "محصولات ${intent.getStringExtra(KEY_TITLE)}"
                initProductRecyclerView(
                    (productList as MutableList<ProductModel>),
                    getColor(R.color.green)
                )
                window.statusBarColor = getColor(R.color.green)
            }

            shopModel != null -> {
                titleTxt.text = getString(R.string.title_near_shop)
                initShopRecyclerView(shopModel)
            }

            slider != null -> {
                searchRequest(slider.tags)
                titleTxt.text = slider.title
            }
        }

    }

    private fun initViews() {

        recyclerView = binding.containerView
        titleTxt = binding.titleTxt
        emptyLayout = binding.emptyLayout

        recyclerView.layoutManager = GridLayoutManager(this, 2)
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

    private fun getCategoryItemsListener() {
        shopViewModel.categoryItemStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> loading()
                is NetworkResult.Success -> {
                    recyclerView.hideSkeleton()
                    initProductRecyclerView(result.response.data)
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

                is NetworkResult.Error -> {
                    recyclerView.hideSkeleton()
                    emptyLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun initProductRecyclerView(
        data: MutableList<ProductModel>,
        color: Int = getColor(R.color.green)
    ) {
        adapter = ProductWithHeaderAdapter(
            (data as MutableList<ListItemModel>),
            "",
            0,
            color,
            false,
            isMathParent = true,
            changeQuantityListener = CartManager,
        )
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
        getAllCart()
    }

    private fun initShopRecyclerView(shopList: ArrayList<ShopModel>) {
        recyclerView.setPadding(0, 0, 0, 0)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ShopAdapter(shopList.toMutableList())
    }

    private fun globalSearch() {
        searchViewModel.searchStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> loading()

                is NetworkResult.Success -> {
                    val data = result.response.data
                    if (data.isNotEmpty()) {
                        initProductRecyclerView(result.response.data)
                    } else {
                        emptyLayout.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                    recyclerView.hideSkeleton()
                }

                is NetworkResult.Error -> {
                    Log.i(TAG, "globalSearch: ${result.message}")
                    recyclerView.hideSkeleton()
                    emptyLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }

    private fun searchRequest(tags: List<String>) {
        addressViewModel.getActiveAddress().observe(this) { address ->
            if (address != null)
                searchViewModel.globalSearch(SearchTags(tags, address.latitude, address.longitude))
            else {
                startActivity(
                    Intent(this, MapActivity::class.java)
                )
                finish()
            }
        }
    }
    @Suppress("UNCHECKED_CAST")
    private fun loading() {
        val item = mutableListOf(
            ProductModel(0),
            ProductModel(1),
            ProductModel(2),
            ProductModel(3),
        ) as MutableList<ListItemModel>
        adapter = ProductWithHeaderAdapter(
            item, "", 0, 0,
            isHeaderEnable = false,
            isMathParent = true,
            changeQuantityListener = CartManager
        )
        recyclerView.adapter = adapter
        recyclerView.loadSkeleton()
    }

    companion object {
        private const val TAG = "ProductListActivity"
    }

}