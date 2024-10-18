package com.se7en.market.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.se7en.market.Activity.Ui.ModalBottomSheetMap
import com.se7en.market.databinding.ActivityAddressBinding
import com.se7en.market.databinding.DialogLayoutBinding


class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

    }

    private fun initViews() {
        binding.backBtn.setOnClickListener {
            finish()
        }

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

    @SuppressLint("InflateParams")
    private fun showDialog() {

        val builderBinding = DialogLayoutBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this)
            .create().apply {
                setView(builderBinding.root)
                setCancelable(false)
                show()
            }
        builderBinding.apply {
            positiveBtn.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
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
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


    private fun showMap() {
        val modal = ModalBottomSheetMap()
        modal.show(supportFragmentManager, ModalBottomSheetMap.TAG)
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