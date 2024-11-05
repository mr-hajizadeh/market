package com.se7en.market.Activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.se7en.market.Activity.Ui.ModalBottomSheetMap.Companion.TAG
import com.se7en.market.Network.NetworkResult
import com.se7en.market.ViewModel.MapViewModel
import com.se7en.market.databinding.ActivityDetailsAddressBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailsAddressBinding
    private lateinit var viewModel: MapViewModel
    private lateinit var editTexts: MutableList<EditText>
    private var toolbar: ConstraintLayout? = null
    private var containerLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        lifecycleScope.launch { initViews() }

    }

    private suspend fun initViews() {

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        editTexts = mutableListOf(binding.addressEdt, binding.houseNumberEdt, binding.unitEdt)

        toolbar = binding.toolbar
        containerLayout = binding.containerLayout

        binding.backBtn.setOnClickListener { finish() }
        binding.addressEdt.keyListener = null

        radiobuttons()
        startSkeleton()
        reverseGeocoding(lat, lon)
        loadStaticMap(lat, lon)
        setupOtherReceiverCheckBox()
        convertToPersian()

        binding.submitBtn.setOnClickListener {
            if (validateEditText(editTexts)) {
                Toast.makeText(this@DetailsAddressActivity, "form submitted", Toast.LENGTH_SHORT)
                    .show()
            } else setErrorEditText(editTexts)
        }

    }

    private fun setupOtherReceiverCheckBox() {
        val deliveryDetailsLayout = binding.deliveryDetailsLayout
        val otherReceiverCb = binding.otherReceiverCb

        otherReceiverCb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                deliveryDetailsLayout.visibility = View.VISIBLE
                editTexts.addAll(listOf(binding.phoneEdt, binding.deliveryNameEdt))
            } else {
                deliveryDetailsLayout.visibility = View.GONE
                editTexts.subList(editTexts.size - 2, editTexts.size).clear()
            }
        }
    }

    private fun loadStaticMap(lat: Double, lon: Double) {
        Glide.with(this)
            .load("https://api.neshan.org/v4/static?key=service.b8ad9848b3614526bcd2dfb35fae9b14&type=standard-day&zoom=19&width=1024&height=512&center=$lat,$lon&markerToken=351047.QDbFpjwn")
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    hideSkeleton()
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    hideSkeleton()
                    return true                }

            })
            .into(binding.staticImg)
    }

    private fun startSkeleton() {
        containerLayout?.loadSkeleton()
    }

    private fun hideSkeleton() {
        containerLayout?.hideSkeleton()
    }

    private fun radiobuttons() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.homeRd.id -> {
                    binding.addressTitleEdt.visibility = View.GONE
                }

                binding.workplaceRd.id -> {
                    binding.addressTitleEdt.visibility = View.GONE
                }

                binding.otherRd.id -> {
                    binding.addressTitleEdt.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun convertToPersian() {

        convertToPersianEditText(binding.addressEdt, isError = true)
        convertToPersianEditText(binding.houseNumberEdt, isError = true)
        convertToPersianEditText(binding.unitEdt, isError = true)
        convertToPersianEditText(binding.phoneEdt, isError = true)
        convertToPersianEditText(binding.deliveryNameEdt, isError = true)

    }

    private suspend fun reverseGeocoding(lat: Double, lon: Double) {

        viewModel.geocodingStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading ->
                    Log.i(TAG, "reverseGeocoding: Loading")

                is NetworkResult.Success ->
                    binding.addressEdt.setText(result.data.formatted_address)

                is NetworkResult.Error ->
                    Log.i(TAG, "reverseGeocoding: Error")

            }
        }

        withContext(Dispatchers.IO) { viewModel.reverseGeocoding(lat, lon) }
    }

}