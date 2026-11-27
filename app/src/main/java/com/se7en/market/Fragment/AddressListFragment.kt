package com.se7en.market.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.se7en.market.Activity.MapActivity
import com.se7en.market.Activity.Ui.ModalBottomSheetMap
import com.se7en.market.Adapter.AddressAdapter
import com.se7en.market.Model.AddressModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.ViewModel.AddressViewModel
import com.se7en.market.databinding.DialogLayoutBinding
import com.se7en.market.databinding.FragmentAddressListBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class AddressListFragment : Fragment(), AddressAdapter.onClickAddressListener {

    private var _binding: FragmentAddressListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AddressViewModel>()
    private var addressView: RecyclerView? = null

    private lateinit var adapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAddressList()
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
        setupAddressView()
    }

    private fun initViews() {
        addressView = binding.addressView
        binding.newAddressBtn.setOnClickListener {
            if (!isPermissionGranted()) requestPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            else showMap()
        }

    }

    private fun setupAddressView() {
        viewModel.listStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> loadSkeleton()

                is NetworkResult.Success -> {
                    Log.i("TAG", "setupAddressView: ${result.response.data}")
                    hideSkeleton()
                    val addressList = result.response.data
                    addressList.find { it.id == viewModel.getActiveAddress().value?.id }
                        ?.let { it.active = true }
                    adapter = AddressAdapter(this, addressList)
                    addressView?.adapter = adapter
                }

                is NetworkResult.Error -> hideSkeleton()
            }
        }

    }

    private fun loadSkeleton() {
        addressView?.loadSkeleton(R.layout.view_holder_address) { itemCount(3) }
    }

    private fun hideSkeleton() {
        addressView?.hideSkeleton()
    }

    @SuppressLint("InflateParams")
    private fun showDialog() {
        val context = requireContext()
        val builderBinding = DialogLayoutBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(context)
            .create().apply {
                setView(builderBinding.root)
                setCancelable(false)
                show()
            }
        builderBinding.apply {
            positiveBtn.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
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
        requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


    private fun showMap() {
        val modal = ModalBottomSheetMap()
        modal.show(parentFragmentManager, ModalBottomSheetMap.TAG)
    }

    private fun deletedAddressListener(item: AddressModel) {
        viewModel.deleteStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    loadSkeleton()
                }

                is NetworkResult.Success -> {
                    Log.i("TAG", "deleteAddress: success ${result.response.status}")
                    hideSkeleton()
                    adapter.removeItem(item)
                    adapter.getAddressList().let {
                        if (it.size == 0) {
                            startActivity(Intent(requireContext(), MapActivity::class.java))
                            activity?.finish()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Log.i("TAG", "deleteAddress: ${result.message}")
                }
            }
        }
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

    override fun activeAddress(item: AddressModel) {
        adapter.setActiveAddress(item)
        viewModel.saveActiveAddress(item)
    }

    override fun deleteAddress(item: AddressModel) {
        viewModel.deleteAddress(item.id)
        deletedAddressListener(item)
        if (item.active) {
            adapter.getAddressList().let {
                viewModel.saveActiveAddress(it.first().apply { active = true })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}