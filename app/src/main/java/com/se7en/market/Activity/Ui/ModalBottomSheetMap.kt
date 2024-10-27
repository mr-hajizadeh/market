package com.se7en.market.Activity.Ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.se7en.market.Fragment.MapFragment
import com.se7en.market.R
import com.se7en.market.databinding.MapBottomSheetBinding
import org.neshan.common.model.LatLng

class ModalBottomSheetMap(private val latLng: LatLng? = null) : BottomSheetDialogFragment() {

    private var _binding: MapBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MapBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeSheet()
        loadMapFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            isCancelable = false

            setOnShowListener { it ->
                val sheet = it as BottomSheetDialog
                val bottomSheet =
                    sheet.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.isDraggable = false
                }
            }

            return dialog
        }
    }

    private fun loadMapFragment() {
        val fragment = MapFragment(latLng)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerFragmentMap, fragment)
            .commit()
    }

    private fun closeSheet() {
        binding.closeBtn.setOnClickListener { dialog?.dismiss() }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}