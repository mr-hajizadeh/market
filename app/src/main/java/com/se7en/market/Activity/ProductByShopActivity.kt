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
import com.se7en.market.databinding.ActivityProductByShopBinding

class ProductByShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductByShopBinding
    private lateinit var categoryData: CategoryModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductByShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryData = intent.getParcelableExtra(KEY_CATEGORY)!!
        initViews()
        buttonsClick()

    }

    private fun initViews() {

        binding.titleTxt.text = categoryData.title
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
        val shopList = listOf(
            ShopWithProductModel(
                ShopModel(id = 0, shop_name = "کوروش 1", courier_fee = 15000, delivery_time = 30),
                list
            ),
            ShopWithProductModel(
                ShopModel(id = 0, shop_name = "کوروش 2", courier_fee = 20000, delivery_time = 21),
                list
            ),ShopWithProductModel(
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