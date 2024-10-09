package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.se7en.market.R
import com.se7en.market.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), LifecycleObserver {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initShimmer()

        return root
    }


    private fun initShimmer() {

        val shimmer = binding.creditShimmer
        val creditView = binding.creditView
        Handler(Looper.getMainLooper()).postDelayed({
            shimmer.stopShimmer()
            shimmer.visibility = View.GONE
            creditView.visibility = View.VISIBLE

        }, 3000)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}