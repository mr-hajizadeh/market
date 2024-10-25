package com.se7en.market.Activity.Ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.se7en.market.Activity.ProfileActivity
import com.se7en.market.R
import com.se7en.market.databinding.CalendarBottomSheetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zabih.lib.zipersiancalendar.ZiPersianCalendar

class ModalBottomSheetCalendar(
    private val birthdayChangeListener: BirthdayChangeListener,
    private var persianCalendar: ZiPersianCalendar
) :
    BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ModalBottomSheetCalendar"
    }

    private var _binding: CalendarBottomSheetBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CalendarBottomSheetBinding.inflate(inflater, container, false)

        initViews()
        closeBtn()

        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                dialog.apply {
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)

                    setOnShowListener { it ->
                        val sheet = it as BottomSheetDialog
                        val bottomSheetDialog =
                            sheet.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                        bottomSheetDialog?.let {
                            val behavior = BottomSheetBehavior.from(it)
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            behavior.isDraggable = false
                        }
                    }
                }
            }
        }

        return dialog
    }

    private fun initViews() {

        val yearPicker = binding.year
        val monthPicker = binding.month
        val dayPicker = binding.day
        val pickers = listOf(yearPicker, monthPicker, dayPicker)

        setupNumberPickers(pickers)
        binding.submitBtn.setOnClickListener {
            birthdayChangeListener.onBirthdayChange(
                yearPicker.displayedValues[yearPicker.value].toInt(),
                (monthPicker.value - 1),
                dayPicker.value,
            )
            dismiss()
        }
    }

    private fun setupNumberPickers(pickers: List<NumberPicker>) {
        val currentYear = persianCalendar.year
        val currentMonth = persianCalendar.month + 1
        var currentDay = persianCalendar.dayOfMonth
        var dayPicker: NumberPicker? = null

        pickers.forEach { picker ->
            picker.wrapSelectorWheel = false
            picker.isFocusable = false
            when (picker.id) {
                R.id.year -> {
                    setupYearPicker(picker, currentYear) { newYear ->
                        currentDay = adjustDayForMonth(currentDay, persianCalendar.daysInMonth)
                        updatePersianCalendar(newYear, currentMonth, currentDay)
                        updateDayPicker(dayPicker!!, currentDay)
                    }
                }

                R.id.month -> {
                    setupMonthPicker(picker, currentMonth) { newMonth ->
                        currentDay = adjustDayForMonth(currentDay, persianCalendar.daysInMonth)
                        updatePersianCalendar(persianCalendar.year, newMonth, currentDay)
                        updateDayPicker(dayPicker!!, currentDay)
                    }
                }

                R.id.day -> {
                    dayPicker = picker
                    picker.setOnValueChangedListener { _, _, newDay ->
                        currentDay = newDay
                    }
                    updateDayPicker(dayPicker!!, currentDay)
                }
            }
        }
    }

    private fun setupYearPicker(
        picker: NumberPicker,
        currentYear: Int,
        onYearChange: (Int) -> Unit
    ) {

        val cal = ZiPersianCalendar()
        val maxYear = cal.year
        val minYear = cal.year - 100

        val yearsArray = (minYear..maxYear).toList().reversed().map {
            convertToPersianNumbers(it.toString())
        }.toTypedArray()

        picker.setOnValueChangedListener { _, _, newYearIndex ->
            onYearChange(minYear + (yearsArray.size - 1 - newYearIndex))
        }

        picker.minValue = 0
        picker.maxValue = yearsArray.size - 1
        picker.displayedValues = yearsArray
        picker.value =
            yearsArray.indexOf(convertToPersianNumbers(currentYear.toString()))
    }

    private fun setupMonthPicker(
        picker: NumberPicker,
        currentMonth: Int,
        onMonthChange: (Int) -> Unit
    ) {
        picker.minValue = 1
        picker.maxValue = 12
        picker.displayedValues = (1..12).map {
            convertToPersianNumbers(it.toString())
        }.toTypedArray()

        picker.setOnValueChangedListener { _, _, newMonth ->
            onMonthChange(newMonth)
        }

        picker.value = currentMonth
    }

    private fun updatePersianCalendar(year: Int, month: Int, day: Int) {
        persianCalendar = ZiPersianCalendar(year, month - 1, day)
    }

    private fun adjustDayForMonth(currentDay: Int, maxDay: Int) =
        if (currentDay > maxDay) maxDay else currentDay

    private fun updateDayPicker(picker: NumberPicker, currentDay: Int) {
        picker.minValue = 1
        picker.displayedValues = null
        picker.maxValue = persianCalendar.daysInMonth

        picker.displayedValues = (1..persianCalendar.daysInMonth).map {
            convertToPersianNumbers(it.toString())
        }.toTypedArray()


        picker.value = currentDay
    }


    private fun convertToPersianNumbers(value: String) =
        ProfileActivity().convertToPersianNumbers(value)

    private fun closeBtn() {
        binding.closeBtn.setOnClickListener { dismiss() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}