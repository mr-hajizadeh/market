package com.se7en.market.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.se7en.market.Adapter.OrderAdapter
import com.se7en.market.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)

        val adapter = OrderAdapter(mutableListOf("123", "465", "465"))
        binding.orderView.adapter = adapter

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}