package com.se7en.market.Activity

import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.se7en.market.Activity.Ui.BirthdayChangeListener
import com.se7en.market.Activity.Ui.ModalBottomSheetCalendar
import com.se7en.market.Model.UserModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.ViewModel.ProfileViewModel
import com.se7en.market.databinding.ActivityProfileBinding
import zabih.lib.zipersiancalendar.ZiPersianCalendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : BaseActivity(), BirthdayChangeListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var user: UserModel
    private val viewModel: ProfileViewModel by viewModels<ProfileViewModel>()
    private var _editTexts: List<EditText>? = null
    private val editTexts get() = _editTexts!!
    private var birthdayEdt: TextView? = null
    private var firstnameEdt: EditText? = null
    private var lastnameEdt: EditText? = null
    private var emailEdt: EditText? = null
    private var persianCalendar = ZiPersianCalendar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setGender()
        getProfile()
        updateProfileListener()
        onClickButtons()
        showCalendarModal()
        setDefaultBirthday()

    }

    private fun initViews() {

        user = UserModel()
        emailEdt = binding.emailEdt
        firstnameEdt = binding.firstnameEdt
        lastnameEdt = binding.lastnameEdt
        birthdayEdt = binding.birthdayTxt

        _editTexts = listOf(firstnameEdt!!, lastnameEdt!!)
        convertToPersianEditText(firstnameEdt!!, isError = true)
        convertToPersianEditText(lastnameEdt!!, isError = true)


    }

    private fun showCalendarModal() {
        birthdayEdt?.setOnClickListener {
            val modal = ModalBottomSheetCalendar(
                this, persianCalendar
            )
            modal.show(supportFragmentManager, ModalBottomSheetCalendar.TAG)
        }
    }

    private fun onClickButtons() {

        binding.backBtn.setOnClickListener { finish() }

        binding.submitBtn.setOnClickListener {
            if (validateEditText(editTexts)) {
                user.first_name = firstnameEdt?.text.toString()
                user.last_name = lastnameEdt?.text.toString()
                user.email = emailEdt?.text.toString()
                viewModel.updateProfile(user)
            } else
                setErrorEditText(editTexts)
        }

    }

    private fun setDefaultBirthday() {
        val persianCalendar = ZiPersianCalendar()
        val birthdayText = if (user.date_of_birth != null) {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = user.date_of_birth?.let { format.parse(it) }
            persianCalendar.timeInMillis = date?.time!!
            persianCalendar.longDate
        } else
            persianCalendar.longDate

        birthdayEdt?.text = convertToPersianNumbers(birthdayText)
    }

    private fun setGender() {

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.maleRd.id -> user.gender = "male"
                binding.femaleRd.id -> user.gender = "female"
            }
        }

    }

    private fun getProfile() {

        viewModel.profileStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    user = result.response.data
                    emailEdt?.setText(user.email)
                    firstnameEdt?.setText(user.first_name)
                    lastnameEdt?.setText(user.last_name)
                    if (user.gender == "male")
                        binding.maleRd.isChecked = true
                    else
                        binding.femaleRd.isChecked = true
                }

                is NetworkResult.Error -> {
                    viewModel.getProfile()
                }
            }
        }

    }

    private fun updateProfileListener() {
        viewModel.updateStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> progressLoading(true)
                is NetworkResult.Success -> {
                    progressLoading(false)
                    showMessage(result.response.messages[0])
                    viewModel.getProfile()
                }

                is NetworkResult.Error -> {
                    showMessage(result.message)
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.main, message, Snackbar.LENGTH_SHORT).show()
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

    override fun onBirthdayChange(vararg date: Int) {
        persianCalendar = ZiPersianCalendar(date[0], date[1], date[2])
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        user.date_of_birth = format.format(Date(persianCalendar.timeInMillis))
        birthdayEdt?.text = convertToPersianNumbers(persianCalendar.longDate)
    }

}