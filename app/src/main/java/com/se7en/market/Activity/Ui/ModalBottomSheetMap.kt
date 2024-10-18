package com.se7en.market.Activity.Ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.se7en.market.Activity.DetailsAddressActivity
import com.se7en.market.Model.ReverseGeocoding
import com.se7en.market.R
import com.se7en.market.ViewModel.MapViewModel
import com.se7en.market.databinding.MapBottomSheetBinding
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModalBottomSheetMap : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    private lateinit var binding: MapBottomSheetBinding
    private lateinit var context: Context
    private lateinit var map: MapView
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: MapViewModel
    private var fab: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MapBottomSheetBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]

        initViews()
        findUserLocation()
        binding.closeBtn.setOnClickListener { dialog?.dismiss() }
        binding.submitLocationBtn.setOnClickListener {
            val latLon: LatLng = map.cameraTargetPosition
            Intent(context, DetailsAddressActivity::class.java).apply {
                putExtra("lat", latLon.latitude)
                putExtra("lon", latLon.longitude)
                context.startActivity(this)
                dismiss()
            }
        }
        if (locationStatus())
            getLocation()
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            isCancelable = false
        }
        dialog.setOnShowListener { it ->
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    private fun locationStatus(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun initViews() {
        map = binding.map
        fab = binding.floatingActionButton
    }

    private fun displayLocationSettingsRequest() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(context)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Log.i(TAG, "All location settings are satisfied.")
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettingsLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.i(TAG, "PendingIntent unable to execute request.")
                }
            } else {
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here.")
            }
        }
    }

    private fun findUserLocation() {

        fab?.setOnClickListener {
            if (!locationStatus())
                displayLocationSettingsRequest()
            else
                getLocation()
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        findLocationStatus(true)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000)
                .setMaxUpdates(1)
                .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationRes: LocationResult) {
                for (location in locationRes.locations) {
                    if (location != null) {
                        findLocationStatus()
                        val latLon = LatLng(location.latitude, location.longitude)
                        map.moveCamera(latLon, 0f)
                        map.setZoom(18f, 0f)
                    }
                }
            }
        }
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                Log.i(TAG, "User agreed to make required location settings changes.")
                getLocation()
            }

            Activity.RESULT_CANCELED -> {
                Toast.makeText(
                    context,
                    "برای موقعیت یابی باید موقعیت مکانی را روشن کنید.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun findLocationStatus(isSearching: Boolean = false) {
        if (isSearching) {
            binding.loading.visibility = View.VISIBLE
            fab?.setImageIcon(null)
            fab?.isClickable = false
        } else {
            binding.loading.visibility = View.INVISIBLE
            fab?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_locate_fixed))
            fab?.isClickable = true
        }
    }

}