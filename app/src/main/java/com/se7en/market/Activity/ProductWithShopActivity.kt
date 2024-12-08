package com.se7en.market.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.se7en.market.Adapter.ProductByShopAdapter
import com.se7en.market.Model.CategoryModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Model.ShopModel
import com.se7en.market.Model.ShopWithProductModel
import com.se7en.market.Utils.Constance.Companion.KEY_CATEGORY
import com.se7en.market.databinding.ActivityProductWithShopBinding

class ProductWithShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductWithShopBinding
    private lateinit var categoryData: CategoryModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductWithShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryData = intent.getParcelableExtra(KEY_CATEGORY)!!
        initViews()
        buttonsClick()

    }

    private fun initViews() {

        binding.titleTxt.text = categoryData.title
        val list: MutableList<ListItemModel> = mutableListOf(
            ProductModel(1, off_percent = 40),
            ProductModel(2, off_percent = 50),
            ProductModel(3, off_percent = 10),
            ProductModel(4, off_percent = 40),
            ProductModel(5, off_percent = 20),
            ProductModel(6, off_percent = 15),
            ProductModel(7, off_percent = 35),
            ProductModel(8, off_percent = 20),
            ProductModel(9, off_percent = 5),
            ProductModel(10, off_percent = 95),
            ProductModel(11, off_percent = 70)
        )
        val shopList = listOf(
            ShopWithProductModel(
                ShopModel(id = 0, shop_name = "کوروش 1", courier_fee = 15000, delivery_time = 30),
                list
            ),
            ShopWithProductModel(
                ShopModel(id = 0, shop_name = "کوروش 2", courier_fee = 20000, delivery_time = 21),
                list
            ), ShopWithProductModel(
                ShopModel(id = 0, shop_name = "کوروش 3", courier_fee = 15000, delivery_time = 10),
                list
            )
        )
        binding.containerView.adapter = ProductByShopAdapter(shopList)

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
}