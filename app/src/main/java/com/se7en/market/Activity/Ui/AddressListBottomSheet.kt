package com.se7en.market.Activity.Ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.se7en.market.Fragment.AddressListFragment
import com.se7en.market.R
import com.se7en.market.databinding.AddressListBottomSheetBinding

class AddressListBottomSheet : BottomSheetDialogFragment() {

    private var _binding: AddressListBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var closeable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            closeable = it.getBoolean(KEY_CLOSABLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddressListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        if (!closeable)
            dialog.apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        loadFragment()
    }

    private fun initViews() {
        binding.closeBtn.setOnClickListener {
            if (closeable)
                dismiss()
            else
                Toast.makeText(requireActivity(), "a", Toast.LENGTH_LONG).show()
        }

    }

    private fun loadFragment() {
        val fragment = AddressListFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerInBottomSheet, fragment)
            .commit()
    }

    companion object {
        const val TAG = "AddressListBottomSheet"
        private const val KEY_CLOSABLE: String = "closeable"

        fun newInstance(closeable: Boolean) =
            AddressListBottomSheet().apply {
                arguments = Bundle().apply {
                    putBoolean(KEY_CLOSABLE, closeable)
                }
            }
    }

}