package com.se7en.market.Activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Adapter.DiscountAdapter
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.ViewModel.DiscountViewModel
import com.se7en.market.databinding.ActivityDiscountBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class DiscountActivity : BaseActivity() {

    private lateinit var binding: ActivityDiscountBinding
    private lateinit var recyclerView: RecyclerView

    private val viewModel by viewModels<DiscountViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        buttonsClick()
        getAllDiscountCodes()

    }

    private fun initViews() {
        recyclerView = binding.discountView
    }

    private fun buttonsClick() {
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun getAllDiscountCodes() {
        viewModel.discountStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> recyclerView.loadSkeleton(R.layout.view_holder_discount) {
                    itemCount(3)
                }

                is NetworkResult.Success -> {
                    recyclerView.hideSkeleton()
                    recyclerView.adapter = DiscountAdapter(result.response.data)
                }

                is NetworkResult.Error -> {
                    recyclerView.hideSkeleton()
                }
            }
        }
        viewModel.getAllDiscountCodes()
    }

}