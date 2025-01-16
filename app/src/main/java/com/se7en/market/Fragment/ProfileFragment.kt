package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import com.se7en.market.Activity.AddressActivity
import com.se7en.market.Activity.ChatActivity
import com.se7en.market.Activity.DiscountActivity
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Activity.ProfileActivity
import com.se7en.market.Activity.Ui.LogoutBottomSheet
import com.se7en.market.Activity.Ui.ModalWallet
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

    private lateinit var webView: WebView
    private lateinit var creditView: LinearLayout

    private val viewmodel: ProfileViewModel by viewModels<ProfileViewModel>()

    private var shouldDisableBackPress: Boolean = false

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

        initViews()
        menuButtons()
        logout()
        setupProfile()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (shouldDisableBackPress) {
                        Toast.makeText(
                            requireContext(),
                            "دکمه بازگشت غیرفعال است",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })

    }

    private fun initViews() {
        webView = binding.webView
        webView.visibility = View.GONE
        creditView = binding.creditView
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

        binding.discountBtn.setOnClickListener {
            startActivity(
                Intent(
                    noneNullContext,
                    DiscountActivity::class.java
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

        binding.supportBtn.setOnClickListener {
            startActivity(
                Intent(
                    noneNullContext,
                    ChatActivity::class.java
                )
            )
        }

        binding.walletBtn.setOnClickListener {
            val modal = ModalWallet { paymentUrl: String ->
                Log.i("TAG", "menuButtons: $paymentUrl")
                showWebView(paymentUrl)
                val fragmentManager = requireActivity().supportFragmentManager
                val existingFragment = fragmentManager.findFragmentByTag(ModalWallet.TAG)

                if (existingFragment != null && existingFragment is ModalWallet)
                    existingFragment.dismiss()

                (requireActivity() as MainActivity).navView.visibility = View.GONE
                shouldDisableBackPress = true
            }
            modal.show(requireActivity().supportFragmentManager, ModalWallet.TAG)
        }

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
                is NetworkResult.Loading -> {
                    creditView.loadSkeleton()
                }

                is NetworkResult.Success -> {
                    creditView.hideSkeleton()
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun showWebView(redirectUrl: String) {
        webView.visibility = View.VISIBLE
        binding.mainScrollVIew.visibility = View.GONE
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun exitWebView() {
                requireActivity().runOnUiThread {
                    webView.visibility = View.GONE
                    binding.mainScrollVIew.visibility = View.VISIBLE
                    viewmodel.getProfile()
                    (requireActivity() as MainActivity).navView.visibility = View.VISIBLE
                    shouldDisableBackPress = false
                }
            }
        }, "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                when {
                    url.contains("Status=OK") -> {
                        Toast.makeText(
                            requireActivity(),
                            "پرداخت با موفقیت انجام شد",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    url.contains("payment/failure") -> {
                        Toast.makeText(
                            requireActivity(),
                            "پرداخت انجام نشد",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        webView.loadUrl(redirectUrl)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}