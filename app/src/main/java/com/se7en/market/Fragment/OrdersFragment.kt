package com.se7en.market.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Adapter.OrderAdapter
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.ViewModel.OrderViewModel
import com.se7en.market.databinding.FragmentOrdersBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyLayout: LinearLayout

    private val viewModel: OrderViewModel by viewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        getAllOrders()
    }

    private fun initViews() {
        recyclerView = binding.orderView
        emptyLayout = binding.emptyLayout
    }

    private fun getAllOrders() {
        viewModel.ordersStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    recyclerView.loadSkeleton(R.layout.view_holder_order_list)
                }

                is NetworkResult.Success -> {
                    recyclerView.hideSkeleton()
                    val orders = result.response.data
                    if (orders.size > 0) {
                        manageView(false)
                        recyclerView.adapter = OrderAdapter(orders)
                    } else
                        manageView()
                }

                is NetworkResult.Error -> {
                    recyclerView.hideSkeleton()
                    Log.i("TAG", "initRecyclerView: ${result.message}")
                }
            }
        }
        viewModel.getAllOrders()
    }

    private fun manageView(emptyCart: Boolean = true) {
        if (emptyCart) {
            emptyLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}