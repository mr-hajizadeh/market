package com.se7en.market.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.se7en.market.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        binding.shimmer.shimmerLayout.startShimmer()


        return binding.root
    }
}