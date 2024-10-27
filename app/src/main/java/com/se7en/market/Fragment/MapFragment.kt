package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.se7en.market.Activity.DetailsAddressActivity
import com.se7en.market.Activity.Ui.ModalBottomSheetMap.Companion.TAG
import com.se7en.market.R
import com.se7en.market.databinding.FragmentMapBinding
import kotlinx.coroutines.launch
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView

class MapFragment(private val latLon: LatLng? = null) : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var _map: MapView? = null
    private val map get() = _map!!

    private var _context: Context? = null
    private val nonNullContext get() = _context!!

    private var _mFusedLocationClient: FusedLocationProviderClient? = null
    private val mFusedLocationClient get() = _mFusedLocationClient!!

    private var fab: FloatingActionButton? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        _mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        submitLocationOnMap()
        findMyLocation()
        if (latLon == null)
            findUserLocation()
        createMap(latLon ?: LatLng(36.2972, 59.6067))

    }

    private fun initViews() {
        fab = binding.floatingActionButton
    }

    private fun submitLocationOnMap() {
        binding.submitLocationBtn.setOnClickListener {
            val latLon: LatLng = map.cameraTargetPosition
            Intent(context, DetailsAddressActivity::class.java).apply {
                putExtra("lat", latLon.latitude)
                putExtra("lon", latLon.longitude)
                nonNullContext.startActivity(this)
                requireActivity().finish()
            }
        }
    }

    private fun locationStatus(): Boolean {
        val locationManager =
            nonNullContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun displayLocationSettingsRequest() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(nonNullContext)
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
        if (locationStatus())
            getLocation()
    }

    private fun findMyLocation() {
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
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000)
                .setMaxUpdates(1)
                .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationRes: LocationResult) {
                for (location in locationRes.locations) {
                    if (location != null) {
                        findLocationStatus()
                        lifecycleScope.launch {
                            createMap(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        }
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
                    getString(R.string.permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun findLocationStatus(isSearching: Boolean = false) {
        Log.i(TAG, "findLocationStatus: $isSearching")
        if (isSearching) {
            binding.loading.visibility = View.VISIBLE
            binding.submitLocationBtn.isEnabled = false
            fab?.setImageIcon(null)
            fab?.isClickable = false
        } else {
            binding.loading.visibility = View.INVISIBLE
            binding.submitLocationBtn.isEnabled = true
            fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    nonNullContext,
                    R.drawable.ic_locate_fixed
                )
            )
            fab?.isClickable = true
        }
    }

    private fun createMap(latLon: LatLng) {
        _map = binding.map
        map.moveCamera(latLon, 0f)
        map.setZoom(18f, 0f)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}