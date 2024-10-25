package com.se7en.market.Activity

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.se7en.market.Activity.Ui.BirthdayChangeListener
import com.se7en.market.Activity.Ui.ModalBottomSheetCalendar
import com.se7en.market.databinding.ActivityProfileBinding
import zabih.lib.zipersiancalendar.ZiPersianCalendar

@Suppress("IMPLICIT_CAST_TO_ANY")
class ProfileActivity : BaseActivity(), BirthdayChangeListener {

    private lateinit var binding: ActivityProfileBinding

    private var _editTexts: List<EditText>? = null
    private val editTexts get() = _editTexts!!
    private var birthdayEdt: TextView? = null
    private var persianCalendar = ZiPersianCalendar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        onClickButtons()
        showCalendarModal()
        setDefaultBirthday()

    }

    private fun initViews() {

        val firstnameEdt = binding.firstnameEdt
        val lastnameEdt = binding.lastnameEdt
        birthdayEdt = binding.birthdayTxt

        _editTexts = listOf(firstnameEdt, lastnameEdt)

        convertToPersianEditText(firstnameEdt, isError = true)
        convertToPersianEditText(lastnameEdt, isError = true)
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
            setErrorEditText(editTexts)
        }

    }

    private fun setDefaultBirthday() {
        birthdayEdt?.text = convertToPersianNumbers(persianCalendar.longDate)
    }

    override fun onBirthdayChange(vararg date: Int) {
        persianCalendar = ZiPersianCalendar(date[0], date[1], date[2])
        birthdayEdt?.text = convertToPersianNumbers(persianCalendar.longDate)
    }

}