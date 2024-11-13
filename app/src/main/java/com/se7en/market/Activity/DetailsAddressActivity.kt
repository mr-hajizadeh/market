package com.se7en.market.Activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.se7en.market.Activity.Ui.ModalBottomSheetMap.Companion.TAG
import com.se7en.market.Model.AddressModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Constance.Companion.KEY_ADDRESS
import com.se7en.market.ViewModel.AddressViewModel
import com.se7en.market.ViewModel.MapViewModel
import com.se7en.market.databinding.ActivityDetailsAddressBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class DetailsAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailsAddressBinding
    private lateinit var mapViewModel: MapViewModel
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var editTexts: MutableList<EditText>
    private var userAddress = AddressModel()
    private var toolbar: ConstraintLayout? = null
    private var containerLayout: LinearLayout? = null
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        addressViewModel = ViewModelProvider(this)[AddressViewModel::class.java]

        lifecycleScope.launch { initViews() }

    }

    private fun initViews() {

        userAddress = intent.extras?.getParcelable(KEY_ADDRESS)!!
        editTexts = mutableListOf(binding.addressEdt, binding.apartmentEdt, binding.unitEdt)

        toolbar = binding.toolbar
        containerLayout = binding.containerLayout

        binding.backBtn.setOnClickListener { finish() }
        binding.addressEdt.keyListener = null

        setTitleAddress()
        startSkeleton()
        reverseGeocoding(userAddress.latitude, userAddress.longitude)
        loadStaticMap(userAddress.latitude, userAddress.longitude)
        recipientChangeText()
        setupOtherReceiverCheckBox()
        convertToPersian()
        storeAddressListener()
        editAddressListener()

        binding.submitBtn.setOnClickListener {
            if (validateEditText(editTexts)) {
                userAddress.unit =
                    convertToEnglishNumbers(binding.unitEdt.text.toString()).toInt()
                userAddress.apartment =
                    convertToEnglishNumbers(binding.apartmentEdt.text.toString()).toInt()
                if (userAddress.id == 0) addressViewModel.storeAddress(userAddress)
                else addressViewModel.editAddress(userAddress)
            } else setErrorEditText(editTexts)
        }

    }

    private fun setupOtherReceiverCheckBox() {
        val deliveryDetailsLayout = binding.deliveryDetailsLayout
        val otherReceiverCb = binding.otherReceiverCb

        otherReceiverCb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                deliveryDetailsLayout.visibility = View.VISIBLE
                editTexts.addAll(listOf(binding.recipientPhoneEdt, binding.recipientNameEdt))
            } else {
                deliveryDetailsLayout.visibility = View.GONE
                editTexts.subList(editTexts.size - 2, editTexts.size).clear()
            }
        }
    }

    private fun loadStaticMap(lat: Double, lon: Double) {
        Glide.with(this)
            .load("https://api.neshan.org/v4/static?key=service.b8ad9848b3614526bcd2dfb35fae9b14&type=dreamy&zoom=19&width=512&height=256&center=$lat,$lon&markerToken=351047.QDbFpjwn")
            .error(R.mipmap.ic_launcher)
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
                    return true
                }

            })
            .into(binding.staticImg)
    }

    private fun startSkeleton() {
        containerLayout?.loadSkeleton()
    }

    private fun hideSkeleton() {
        Handler(Looper.getMainLooper()).postDelayed({ containerLayout?.hideSkeleton() }, 1000)
    }

    private fun setTitleAddress() {

        val addressTitleEdt = binding.addressTitleEdt
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.homeRd.id -> {
                    addressTitleEdt.visibility = View.GONE
                    userAddress.title = getString(R.string.title_home)
                }

                binding.workplaceRd.id -> {
                    addressTitleEdt.visibility = View.GONE
                    userAddress.title = getString(R.string.title_workplace)
                }

                binding.otherRd.id -> {
                    addressTitleEdt.visibility = View.VISIBLE
                }
            }
        }

        addressTitleEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userAddress.title = convertToEnglishNumbers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun convertToPersian() {

        convertToPersianEditText(binding.addressEdt, isError = true)
        convertToPersianEditText(binding.apartmentEdt, isError = true)
        convertToPersianEditText(binding.unitEdt, isError = true)
        convertToPersianEditText(binding.recipientPhoneEdt, isError = true)
        convertToPersianEditText(binding.recipientNameEdt, isError = true)
        convertToPersianEditText(binding.addressTitleEdt, isError = false)

    }

    private fun reverseGeocoding(lat: Double, lon: Double) {

        mapViewModel.geocodingStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading ->
                    Log.i(TAG, "reverseGeocoding: Loading")

                is NetworkResult.Success -> {
                    userAddress.address = result.response.formatted_address
                    binding.addressEdt.setText(userAddress.address)
                }

                is NetworkResult.Error ->
                    Log.i(TAG, "reverseGeocoding: Error")

            }
        }

        mapViewModel.reverseGeocoding(lat, lon)
    }

    private fun storeAddressListener() {

        addressViewModel.storeStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> progressLoading(true)

                is NetworkResult.Success -> {
                    progressLoading()
                    val address = result.response.data.apply { active = true }
                    addressViewModel.saveActiveAddress(address)
                    showMessage(result.response.messages[0])
                    snackbar?.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (event == DISMISS_EVENT_TIMEOUT) {
                                val intent =
                                    Intent(this@DetailsAddressActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }

                        override fun onShown(sb: Snackbar?) {
                            super.onShown(sb)
                        }
                    })
                }

                is NetworkResult.Error -> {
                    progressLoading()
                    showMessage(result.message)
                }
            }
        }

    }

    private fun editAddressListener() {

        addressViewModel.editStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> progressLoading(true)

                is NetworkResult.Success -> {
                    progressLoading()
                    val address = result.response.data.apply { active = true }
                    addressViewModel.saveActiveAddress(address)
                    showMessage(result.response.messages[0])
                    snackbar?.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (event == DISMISS_EVENT_TIMEOUT) {
                                val intent =
                                    Intent(this@DetailsAddressActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }

                        override fun onShown(sb: Snackbar?) {
                            super.onShown(sb)
                        }
                    })
                }

                is NetworkResult.Error -> {
                    progressLoading()
                    showMessage(result.message)
                }
            }
        }

    }

    private fun recipientChangeText() {

        binding.recipientPhoneEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userAddress.recipient_phone = convertToEnglishNumbers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.recipientNameEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userAddress.recipient_name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun showMessage(message: String) {
        snackbar = Snackbar.make(binding.main, message, Snackbar.LENGTH_SHORT)
        snackbar?.show()
    }

    private fun progressLoading(isLoading: Boolean = false) {
        val progressLoading = binding.progressLoading
        val submitBtn = binding.submitBtn
        if (isLoading) {
            submitBtn.isEnabled = false
            progressLoading.visibility = View.VISIBLE
        } else {
            submitBtn.isEnabled = true
            progressLoading.visibility = View.GONE
        }
    }
}