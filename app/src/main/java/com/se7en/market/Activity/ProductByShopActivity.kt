package com.se7en.market.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.se7en.market.Model.CategoryModel
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

    }

    private fun buttonsClick() {

        binding.backBtn.setOnClickListener { finish() }
        binding.searchBtn.setOnClickListener { startActivity(Intent(this, SearchActivity::class.java)) }

    }
}