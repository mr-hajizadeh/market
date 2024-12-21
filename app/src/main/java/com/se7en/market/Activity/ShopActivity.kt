package com.se7en.market.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomappbar.BottomAppBar
import com.se7en.market.Adapter.ContainerAdapter
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.MainListModel
import com.se7en.market.Model.ShopModel
import com.se7en.market.Model.ViewTypeModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Constance.Companion.KEY_SHOP
import com.se7en.market.Utils.Helper
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.ViewModel.ShopViewModel
import com.se7en.market.databinding.ActivityShopBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

@Suppress("UNCHECKED_CAST")
class ShopActivity : BaseActivity() {

    private lateinit var binding: ActivityShopBinding
    private lateinit var containerAdapter: ContainerAdapter
    private lateinit var shopData: ShopModel
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var containerView: CoordinatorLayout
    private lateinit var appbarLayout: ConstraintLayout

    private val viewModel: ShopViewModel by viewModels<ShopViewModel>()
    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()

    private var minPriceTxt: TextView? = null
    private var firstTimeSendTxt: TextView? = null
    private var totalPriceTxt: TextView? = null
    private var courierFeeTxt: TextView? = null
    private var shopNameTxt: TextView? = null
    private var totalItemsTxt: TextView? = null
    private var oldCartSize: Int = 0
    private var newCartSize: Int = 0
    private var isOldSizeSet: Boolean = false

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shopData = intent.getParcelableExtra(KEY_SHOP)!!
        initViews()
        onclickButtons()
        setupShopView()
        setData()

    }

    private fun initViews() {

        minPriceTxt = binding.minPriceTxt
        firstTimeSendTxt = binding.firstTimeSendTxt
        totalPriceTxt = binding.totalPriceTxt
        courierFeeTxt = binding.courierFeeTxt
        shopNameTxt = binding.shopNameTxt
        containerView = binding.shopActivityContainer
        totalItemsTxt = binding.totalItemsTxt
        bottomAppBar = binding.bottomAppBar
        appbarLayout = binding.appBarLayout

        bottomAppBar.background = ContextCompat.getDrawable(this, R.drawable.gradient_white_bg)

    }

    @SuppressLint("SetTextI18n")
    private fun setData() {

        with(shopData) {
            viewModel.getShopItems(id)
            viewModel.getShopCategories(id)
            minPriceTxt?.text = convertToPersianNumbers(minPriceTxt?.text.toString())
            firstTimeSendTxt?.text =
                "${convertToPersianNumbers(delivery_time.toString())} ${getString(R.string.title_minute)}"
            shopNameTxt?.text = shop_name
            courierFeeTxt?.text = if (courier_fee == 0) getString(R.string.title_free)
            else "${formatPriceToPersianCourierFee()} ${getString(R.string.title_currency)}"

        }
    }

    private fun onclickButtons() {

        binding.backBtn.setOnClickListener { finish() }

        binding.searchViewBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SearchActivity::class.java
                )
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupShopView() {

        val shopMainView = binding.shopMainView
        val dataStatus = MutableLiveData<MutableList<Boolean>>()
        val statusList = mutableListOf<Boolean>()
        val mainList = mutableListOf<MainListModel>()
        shopMainView.loadSkeleton(R.layout.view_holder_product_list)
        appbarLayout.loadSkeleton()

        viewModel.categoriesStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                }

                is NetworkResult.Success -> {
                    Log.i("TAG", "setupShopView: ${result.response.data}")
                    mainList.add(
                        MainListModel(
                            position = 0,
                            viewType = ViewTypeModel.CATEGORY_TYPE,
                            items = result.response.data as MutableList<ListItemModel>
                        )
                    )
                    statusList.add(true)
                    dataStatus.value = statusList
                }

                is NetworkResult.Error -> {
                    Log.i("TAG", "setupShopView: ${result.message}")
                }
            }
        }

        viewModel.itemsStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    mainList.add(
                        MainListModel(
                            title = getString(R.string.title_recommended),
                            position = 1,
                            color = getColor(R.color.orange),
                            viewType = ViewTypeModel.HEADER_TYPE,
                            items = result.response.data as MutableList<ListItemModel>
                        )
                    )
                    mainList.add(
                        MainListModel(
                            title = getString(R.string.title_recommended),
                            position = 2,
                            color = getColor(R.color.green),
                            viewType = ViewTypeModel.ONE_ROW_TYPE,
                            items = result.response.data as MutableList<ListItemModel>
                        )
                    )
                    mainList.add(
                        MainListModel(
                            title = getString(R.string.title_recommended),
                            position = 3,
                            color = getColor(R.color.green),
                            viewType = ViewTypeModel.ONE_ROW_TYPE,
                            items = result.response.data as MutableList<ListItemModel>
                        )
                    )
                    mainList.add(
                        MainListModel(
                            title = getString(R.string.title_recommended),
                            position = 4,
                            color = getColor(R.color.orange),
                            viewType = ViewTypeModel.HEADER_TYPE,
                            items = result.response.data as MutableList<ListItemModel>
                        )
                    )
                    statusList.add(true)
                    dataStatus.value = statusList
                }

                is NetworkResult.Error -> {
                    Log.i("TAG", "setupShopView: ${result.message}")
                }
            }
        }

        dataStatus.observe(this) { data ->
            data?.takeIf { it.all { status -> status } }?.let {
                Handler(Looper.getMainLooper()).postDelayed({
                    shopMainView.hideSkeleton()
                    appbarLayout.hideSkeleton()
                }, 1000)
                mainList.sortBy { it.position }
                containerAdapter =
                    ContainerAdapter(mainList, true, shopMainView, binding.root)

                shopMainView.adapter = containerAdapter
                getAllCart()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getAllCart() {
        cartViewModel.cartsStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                }

                is NetworkResult.Success -> {
                    containerAdapter.syncCartWithProducts(result.response.data)
                    val cartCount =
                        result.response.data.sumOf { it.card_details.sumOf { cartDetail -> cartDetail.quantity } }
                    if (!isOldSizeSet) {
                        oldCartSize = cartCount
                        isOldSizeSet = true
                    }
                    newCartSize = cartCount

                    result.response.data.find { it.seller_id == shopData.id }?.let { cart ->
                        bottomAppBar.visibility =
                            if (cart.card_details.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                        val totalAmount = cart.card_details.sumOf {
                            val itemTotal = it.quantity * it.item.amount
                            val discount = itemTotal * it.item.off_percent / 100
                            itemTotal - discount
                        }
                        val productCount = cart.card_details.sumOf { it.quantity }
                        totalItemsTxt?.text = convertToPersianNumbers(
                            "${getString(R.string.title_total_items)} (${productCount} کالا)"
                        )
                        totalPriceTxt?.text =
                            "${Helper.formatPriceToPersian(totalAmount)} ${getString(R.string.title_currency)}"
                    } ?: run { bottomAppBar.visibility = View.INVISIBLE }
                }

                is NetworkResult.Error -> {}
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (newCartSize != oldCartSize)
            cartViewModel.getAllCarts()
    }

}