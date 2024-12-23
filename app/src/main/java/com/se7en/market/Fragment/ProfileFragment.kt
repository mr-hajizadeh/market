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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import com.se7en.market.Activity.AddressActivity
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Activity.OrderStatusActivity
import com.se7en.market.Activity.ProfileActivity
import com.se7en.market.Activity.Ui.LogoutBottomSheet
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.ViewModel.ProfileViewModel
import com.se7en.market.databinding.FragmentProfileBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class ProfileFragment : Fragment(), LifecycleObserver {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var _context: Context? = null
    private val noneNullContext get() = _context!!

    private val viewmodel: ProfileViewModel by viewModels<ProfileViewModel>()

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
        logout()
        setupProfile()

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

        binding.favoriteBtn.setOnClickListener {
            startActivity(
                Intent(
                    noneNullContext,
                    OrderStatusActivity::class.java
                )
            )
        }

    }

    private fun initShimmer() {

        val creditView = binding.creditView
        creditView.loadSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            creditView.hideSkeleton()
        }, 1000)

    }

    private fun logout() {

        binding.logoutBtn.setOnClickListener {
            val modal = LogoutBottomSheet()
            modal.show(childFragmentManager, "LogoutBottomSheet")
        }

    }

    private fun setupProfile() {

        viewmodel.profileStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    val user = result.response.data
                    val username =
                        if (user.first_name.isNullOrEmpty() or user.last_name.isNullOrEmpty())
                            getString(R.string.title_your_profile)
                        else
                            "${user.first_name} ${user.last_name}"
                    binding.usernameTxt.text = username
                    binding.phoneTxt.text = MainActivity().convertToPersianNumbers(user.phone)
                    binding.creditAmountTxt.text =
                        Helper.formatPriceToPersian(user.wallet_balance.toLong())
                }

                is NetworkResult.Error -> {
                    viewmodel.getProfile()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}