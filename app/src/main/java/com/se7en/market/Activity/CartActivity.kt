package com.se7en.market.Activity

import android.os.Bundle
import androidx.activity.viewModels
import com.se7en.market.Utils.Constance.Companion.KEY_ID
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.databinding.ActivityCartBinding

class CartActivity : BaseActivity() {

    private lateinit var binding: ActivityCartBinding
    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()

    companion object {
        var oldCartSize: Int = 0
        var newCartSize: Int = 0
        var isOldSizeSet: Boolean = false
        var cartId: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cartId = intent.extras?.getInt(KEY_ID)!!
    }

    override fun onDestroy() {
        super.onDestroy()
        if (newCartSize != oldCartSize) {
            oldCartSize = 0
            newCartSize = 0
            isOldSizeSet = false
            cartViewModel.getAllCarts()
        }
    }

}