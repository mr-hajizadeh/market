package com.se7en.market.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.ViewModel.ProfileViewModel
import com.se7en.market.databinding.ActivityWalletBinding

class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding

    private val viewModel: ProfileViewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        onClickButtons()
        setupProfile()

    }

    private fun initViews() {}

    private fun onClickButtons() {
        binding.backBtn.setOnClickListener { finish() }
    }

    @SuppressLint("SetTextI18n")
    private fun setupProfile() {

        viewModel.profileStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    val user = result.response.data
                    binding.creditTxt.text =
                        "${Helper.formatPriceToPersian(user.wallet_balance.toLong())} ${
                            getString(
                                R.string.title_currency
                            )
                        }"
                }

                is NetworkResult.Error -> {
                    viewModel.getProfile()
                }
            }
        }

    }

}