package com.se7en.market.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.se7en.market.Activity.Ui.ModalBottomSheetMap
import com.se7en.market.Adapter.AddressAdapter
import com.se7en.market.Model.AddressModel
import com.se7en.market.R
import com.se7en.market.databinding.DialogLayoutBinding
import com.se7en.market.databinding.FragmentAddressListBinding

class AddressListFragment : Fragment() {

    private var _binding: FragmentAddressListBinding? = null
    private val binding get() = _binding!!

    private var _context: Context? = null
    private val noneNullContext get() = _context!!

    private lateinit var adapter: AddressAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {

        binding.newAddressBtn.setOnClickListener {
            if (!isPermissionGranted()) requestPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            else showMap()
        }

        setupAddressView()
    }

    private fun setupAddressView() {
        val addressView = binding.addressView
        adapter = AddressAdapter(listOf(AddressModel("خانه", "test")))
        addressView.adapter = adapter
        addressView.setHasFixedSize(true)
    }

    @SuppressLint("InflateParams")
    private fun showDialog() {

        val builderBinding = DialogLayoutBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(noneNullContext)
            .create().apply {
                setView(builderBinding.root)
                setCancelable(false)
                show()
            }
        builderBinding.apply {
            positiveBtn.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", noneNullContext.packageName, null)
                }
                startActivity(intent)
                builder.dismiss()
            }
            closeBtn.setOnClickListener {
                builder.dismiss()
            }
        }
    }

    private fun isPermissionGranted(): Boolean =
        noneNullContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


    private fun showMap() {
        val modal = ModalBottomSheetMap()
        modal.show(requireActivity().supportFragmentManager, ModalBottomSheetMap.TAG)
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isFineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val isCoarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (isFineLocationGranted || isCoarseLocationGranted)
                showMap()
            else
                showDialog()
        }


}