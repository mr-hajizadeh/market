package com.se7en.market.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Adapter.ProductWithHeaderAdapter
import com.se7en.market.Model.AddressModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Model.SearchTags
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.CartManager
import com.se7en.market.ViewModel.AddressViewModel
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.ViewModel.SearchViewModel
import com.se7en.market.databinding.ActivitySearchBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: ProductWithHeaderAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyLayout: LinearLayout
    private lateinit var address: AddressModel
    private lateinit var searchBar: EditText

    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()
    private val searchViewModel: SearchViewModel by viewModels<SearchViewModel>()
    private val addressViewModel: AddressViewModel by viewModels<AddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getActiveAddress()
        initViews()
        globalSearch()
        initSearchBar()

    }

    private fun initViews() {
        binding.backBtn.setOnClickListener { finish() }
        emptyLayout = binding.emptyLayout
        recyclerView = binding.searchView
        searchBar = binding.searchBarEdt

    }

    private fun initSearchBar() {
        var searchJob: Job? = null

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                s?.let { text ->
                    searchJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        val tags = text.split(" ").filter { it.isNotBlank() }
                        searchViewModel.globalSearch(
                            SearchTags(tags, address.latitude, address.longitude)
                        )
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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

    private fun getActiveAddress() {
        addressViewModel.getActiveAddress().observe(this) { address ->
            if (address != null)
                this.address = address
            else {
                startActivity(
                    Intent(this, MapActivity::class.java)
                )
                finish()
            }
        }
    }

    private fun globalSearch() {
        searchViewModel.searchStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> recyclerView.loadSkeleton()
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
                    recyclerView.hideSkeleton()
                    emptyLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }

}