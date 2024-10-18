package com.se7en.market.Activity

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import com.se7en.market.Activity.Ui.ModalBottomSheetMap
import com.se7en.market.Activity.Ui.ModalBottomSheetMap.Companion.TAG
import com.se7en.market.Model.ReverseGeocoding
import com.se7en.market.R
import com.se7en.market.ViewModel.MapViewModel
import com.se7en.market.databinding.ActivityDetailsAddressBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import org.neshan.common.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsAddressBinding
    private lateinit var viewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        initViews()

        binding.submitBtn.setOnClickListener {
            validation()
        }

    }

    private fun initViews() {
        radiobuttons()

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        reverseGeocoding(lat, lon)

        binding.backBtn.setOnClickListener { finish() }
        binding.addressEdt.keyListener = null
        val imageView = binding.staticImg

        binding.test.loadSkeleton()
        binding.toolbar.loadSkeleton()
        Glide.with(this)
            .load("https://api.neshan.org/v4/static?key=service.b8ad9848b3614526bcd2dfb35fae9b14&type=standard-day&zoom=19&width=1024&height=512&center=$lat,$lon&markerToken=351047.QDbFpjwn")
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.test.hideSkeleton()
                    binding.toolbar.hideSkeleton()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.test.hideSkeleton()
                    binding.toolbar.hideSkeleton()
                    return false
                }

            })
            .into(imageView)

        binding.otherReceiverCb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.deliveryDetailsLayout.visibility = View.VISIBLE
            else binding.deliveryDetailsLayout.visibility = View.GONE
        }
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

    private fun validation() {

        val addressEdt = binding.addressEdt
        val houseNumberEdt = binding.houseNumberEdt
        val unitEdt = binding.unitEdt
        val phoneNumberEdt = binding.phoneEdt
        val deliveryNameEdt = binding.deliveryNameEdt
        val isOtherReceiver = binding.otherReceiverCb

        val address = addressEdt.text
        val houseNumber = houseNumberEdt.text
        val unit = unitEdt.text
        val phoneNumber = phoneNumberEdt.text
        val deliveryName = deliveryNameEdt.text

        val inputs = mutableListOf<Pair<CharSequence, EditText>>(
            houseNumber to houseNumberEdt,
            unit to unitEdt
        )

        if (isOtherReceiver.isChecked) inputs.addAll(
            listOf(
                phoneNumber to phoneNumberEdt,
                deliveryName to deliveryNameEdt
            )
        )
        else
            if (inputs.size > 2)
                inputs.subList(inputs.size - 2, inputs.size).clear()
        Log.i(TAG, "validation: $inputs")

        var isValidate = true
        inputs.forEach {
            if (it.first.isEmpty()) {
                it.second.setBackgroundResource(R.drawable.error_input_bg)
                isValidate = false
            }
        }

        Log.i(TAG, "validation: $isValidate")

        houseNumberEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! >= 1)
                    houseNumberEdt.setBackgroundResource(R.drawable.input_bg)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        unitEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! >= 1)
                    unitEdt.setBackgroundResource(R.drawable.input_bg)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        phoneNumberEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! >= 1)
                    phoneNumberEdt.setBackgroundResource(R.drawable.input_bg)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        deliveryNameEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! >= 1)
                    deliveryNameEdt.setBackgroundResource(R.drawable.input_bg)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun reverseGeocoding(lat: Double, lon: Double) {

        viewModel.reverseGeocoding(lat, lon)
            .enqueue(object : Callback<ReverseGeocoding> {
                override fun onResponse(
                    call: Call<ReverseGeocoding>,
                    response: Response<ReverseGeocoding>
                ) {
                    if (response.isSuccessful && response.body() != null)
                        binding.addressEdt.setText(response.body()?.formatted_address)
                }

                override fun onFailure(call: Call<ReverseGeocoding>, t: Throwable) {
                    Log.i(TAG, "onFailure: ${t.message}")
                }
            })

    }

}