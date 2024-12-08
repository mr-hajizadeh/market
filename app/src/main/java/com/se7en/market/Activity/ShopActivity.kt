package com.se7en.market.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.se7en.market.Adapter.ContainerAdapter
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.MainListModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Model.ShopModel
import com.se7en.market.Model.ViewTypeModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Constance.Companion.KEY_SHOP
import com.se7en.market.ViewModel.ShopViewModel
import com.se7en.market.databinding.ActivityShopBinding

@Suppress("UNCHECKED_CAST")
class ShopActivity : BaseActivity() {

    private lateinit var binding: ActivityShopBinding
    private lateinit var containerAdapter: ContainerAdapter
    private lateinit var shopData: ShopModel

    private val viewModel: ShopViewModel by viewModels<ShopViewModel>()

    private var minPriceTxt: TextView? = null
    private var firstTimeSendTxt: TextView? = null
    private var totalPriceTxt: TextView? = null
    private var courierFeeTxt: TextView? = null
    private var shopNameTxt: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shopData = intent.getParcelableExtra(KEY_SHOP)!!
        initViews()
        setData()
        onclickButtons()
        setupShopView()
        manageBottomBar()

    }

    private fun initViews() {

        minPriceTxt = binding.minPriceTxt
        firstTimeSendTxt = binding.firstTimeSendTxt
        totalPriceTxt = binding.totalPriceTxt
        courierFeeTxt = binding.courierFeeTxt
        shopNameTxt = binding.shopNameTxt

        totalPriceTxt?.text = convertToPersianNumbers(totalPriceTxt?.text.toString())

    }

    @SuppressLint("SetTextI18n")
    private fun setData() {

        with(shopData) {
            viewModel.getShopItems(id)
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

    private fun setupShopView() {

        val shopMainView = binding.shopMainView

        viewModel.itemsStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    Log.i("TAG", "setupShopView: ${result.response}")
                    val mainList = mutableListOf(
//            MainListModel(
//                "pos 0",
//                0,
//                ViewTypeModel.CATEGORY_TYPE,
//                category
//            ),
                        MainListModel(
                            "تست",
                            2,
                            ViewTypeModel.HEADER_TYPE,
                            result.response.data as MutableList<ListItemModel>
                        )
                    ).apply { sortBy { it.position } }.toMutableList()
                    val categoryLayoutManager =
                        GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)
                    containerAdapter =
                        ContainerAdapter(
                            mainList,
                            categoryLayoutManager
                        ) { product, position, adapter ->
                            Handler(Looper.getMainLooper()).postDelayed({
                                adapter.updateItem(position, product)
                            }, 3000)
                        }
                    shopMainView.adapter = containerAdapter

                }

                is NetworkResult.Error -> {
                    Log.i("TAG", "setupShopView: ${result.message}")
                }
            }
        }
    }

    private fun manageBottomBar() {
        val bottomBar = binding.bottomBar

        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                bottomBar.animate().translationY(bottomBar.height.toFloat()).duration = 300
            } else if (scrollY < oldScrollY) {
                bottomBar.animate().translationY(0f).duration = 300
            }
        }
    }

}