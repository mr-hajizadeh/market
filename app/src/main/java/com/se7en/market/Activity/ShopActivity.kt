package com.se7en.market.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.GridLayoutManager
import com.se7en.market.Adapter.ContainerAdapter
import com.se7en.market.Model.CategoryModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.MainListModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Model.ViewTypeModel
import com.se7en.market.R
import com.se7en.market.databinding.ActivityShopBinding

class ShopActivity : BaseActivity() {

    private lateinit var binding: ActivityShopBinding
    private lateinit var containerAdapter: ContainerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        onclickButtons()
        setupShopView()
        manageBottomBar()

    }

    private fun initViews() {

        val minPriceTxt = binding.minPriceTxt
        val firstTimeSendTxt = binding.firstTimeSendTxt
        val totalPriceTxt = binding.totalPriceTxt

        minPriceTxt.text = convertToPersianNumbers(minPriceTxt.text.toString())
        firstTimeSendTxt.text = convertToPersianNumbers(firstTimeSendTxt.text.toString())
        totalPriceTxt.text = convertToPersianNumbers(totalPriceTxt.text.toString())

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
        val list: MutableList<ListItemModel> = mutableListOf(
            ProductModel(1, "40"),
            ProductModel(2, "50"),
            ProductModel(3, "10"),
            ProductModel(4, "40"),
            ProductModel(5, "20"),
            ProductModel(6, "15"),
            ProductModel(7, "35"),
            ProductModel(8, "20"),
            ProductModel(9, "5"),
            ProductModel(10, "95"),
            ProductModel(11, "70")
        )
        val list2: MutableList<ListItemModel> = mutableListOf(
            ProductModel(21, "40"),
            ProductModel(12, "50"),
            ProductModel(13, "10"),
            ProductModel(14, "40"),
            ProductModel(15, "20"),
            ProductModel(16, "15"),
            ProductModel(71, "35"),
            ProductModel(81, "20"),
            ProductModel(91, "5"),
            ProductModel(110, "95"),
            ProductModel(111, "70")
        )
        val category: MutableList<ListItemModel> = mutableListOf(
            CategoryModel(R.drawable.t1, "میوه و سبزیجات تازه"),
            CategoryModel(R.drawable.t2, "لبنیات و تخم مرغ"),
            CategoryModel(R.drawable.t3, "نوشیدنی‌ها"),
            CategoryModel(R.drawable.t1, "گوشت و مرغ"),
            CategoryModel(R.drawable.t2, "مواد غذایی کنسروی"),
            CategoryModel(R.drawable.t3, "نان و شیرینی"),
            CategoryModel(R.drawable.t1, "آجیل و خشکبار"),
            CategoryModel(R.drawable.t2, "غذاهای یخ‌زده"),
            CategoryModel(R.drawable.t3, "مواد غذایی کودک"),
            CategoryModel(R.drawable.t1, "مواد شوینده و بهداشتی"),
            CategoryModel(R.drawable.t2, "محصولات پروتئینی"),
            CategoryModel(R.drawable.t3, "آرایشی و بهداشتی"),
        )

        val lists = mutableListOf(
            MainListModel(
                "pos 0",
                0,
                ViewTypeModel.CATEGORY_TYPE,
                category
            ),
            MainListModel(
                "pos 2",
                2,
                ViewTypeModel.HEADER_TYPE,
                list
            ),
            MainListModel(
                "pos 1",
                3,
                ViewTypeModel.ONE_ROW_TYPE,
                list
            ),
            MainListModel(
                "pos 5",
                5,
                ViewTypeModel.ONE_ROW_TYPE,
                list2
            ),
            MainListModel(getString(R.string.title_recommended), 5, ViewTypeModel.BRAND_TYPE, list)
        ).apply { sortBy { it.position } }
        val categoryLayoutManager = GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)
        containerAdapter =
            ContainerAdapter(lists, categoryLayoutManager) { product, position, adapter ->
                Handler(Looper.getMainLooper()).postDelayed({
                    adapter.updateItem(position, product)
                }, 3000)
            }
        shopMainView.adapter = containerAdapter

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