package com.se7en.market.Activity.Ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.se7en.market.Activity.LoginActivity
import com.se7en.market.ViewModel.LogoutViewModel
import com.se7en.market.databinding.LogoutBottomSheetBinding

class LogoutBottomSheet : BottomSheetDialogFragment() {

    private var _binding: LogoutBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LogoutViewModel

    private var returnBtn: Button? = null
    private var logoutBtn: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LogoutBottomSheetBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LogoutViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        logoutAccount()
        dismissDialog()

    }

    private fun initViews() {

        returnBtn = binding.returnBtn
        logoutBtn = binding.logoutBtn

    }

    private fun logoutAccount() {
        logoutBtn?.setOnClickListener {
            viewModel.logout()
            val context = requireActivity()
            context.startActivity(Intent(context, LoginActivity::class.java))
            context.finish()
        }
    }

    private fun dismissDialog() {
        returnBtn?.setOnClickListener { dismiss() }
        binding.closeBtn.setOnClickListener { dismiss() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}