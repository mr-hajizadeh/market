package com.se7en.market.Activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.se7en.market.Activity.Ui.ModalBottomSheetMap.Companion.TAG
import com.se7en.market.Model.ReverseGeocoding
import com.se7en.market.R
import com.se7en.market.ViewModel.MapViewModel
import com.se7en.market.databinding.ActivityDetailsAddressBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        initViews()

    }

    private fun initViews() {

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
            } else setErrorEditText()
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
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    hideSkeleton()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    hideSkeleton()
                    return false
                }

            })
            .into(binding.staticImg)
    }

    private fun startSkeleton() {
        toolbar?.loadSkeleton()
        containerLayout?.loadSkeleton()
    }

    private fun hideSkeleton() {
        toolbar?.hideSkeleton()
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

    private fun setErrorEditText() {
        editTexts.forEach {
            if (it.text.isEmpty())
                it.setBackgroundResource(R.drawable.error_input_bg)
        }
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