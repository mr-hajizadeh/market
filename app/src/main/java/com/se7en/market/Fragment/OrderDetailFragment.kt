package com.se7en.market.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.se7en.market.R
import com.se7en.market.Utils.Constance
import com.se7en.market.databinding.FragmentOrderDetailBinding

class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickButtons()

    }

    private fun onClickButtons() {

        binding.backBtn.setOnClickListener { requireActivity().finish() }

        binding.completionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_payFragment)
        }

    }
}