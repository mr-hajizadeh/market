package com.se7en.market.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import com.se7en.market.Activity.AddressActivity
import com.se7en.market.Activity.ProfileActivity
import com.se7en.market.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), LifecycleObserver {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var _context: Context? = null
    private val noneNullContext get() = _context!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initShimmer()
        menuButtons()

        return root
    }

    private fun initViews() {

    }

    private fun menuButtons() {

        binding.myAddressBtn.setOnClickListener {
            startActivity(
                Intent(
                    noneNullContext,
                    AddressActivity::class.java
                )
            )
        }

        binding.profileBtn.setOnClickListener {
            startActivity(
                Intent(
                    noneNullContext,
                    ProfileActivity::class.java
                )
            )
        }

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