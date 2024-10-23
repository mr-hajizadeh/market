package com.se7en.market.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.se7en.market.R

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun convertToEnglishNumbers(input: String): String {
        return input
            .replace("۰", "0")
            .replace("۱", "1")
            .replace("۲", "2")
            .replace("۳", "3")
            .replace("۴", "4")
            .replace("۵", "5")
            .replace("۶", "6")
            .replace("۷", "7")
            .replace("۸", "8")
            .replace("۹", "9")
    }

    fun convertToPersianNumbers(input: String): String {
        return input
            .replace("0", "۰")
            .replace("1", "۱")
            .replace("2", "۲")
            .replace("3", "۳")
            .replace("4", "۴")
            .replace("5", "۵")
            .replace("6", "۶")
            .replace("7", "۷")
            .replace("8", "۸")
            .replace("9", "۹")
    }

    fun convertToPersianEditText(
        view: EditText,
        submitBtn: Button? = null,
        isError: Boolean = false
    ) {
        view.addTextChangedListener(object : TextWatcher {
            private var isChange = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    !isChange -> {
                        isChange = true
                        val formated = convertToPersianNumbers(s.toString())
                        view.setText(convertToPersianNumbers(formated))
                        view.setSelection(formated.length)
                        isChange = false
                    }
                    isError && s?.length!! >= 1 ->
                        view.setBackgroundResource(R.drawable.input_bg)

                    submitBtn != null -> submitBtn.isEnabled = s?.length!! >= 11
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    fun validateEditText(editTexts: List<EditText>) = editTexts.all { it.text.isNotEmpty() }

}