package com.se7en.market.Activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.se7en.market.R
import com.se7en.market.databinding.ActivityLoginBinding
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var authLayout: LinearLayout? = null
    private var receiveCode: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        initViews()
        initAuthViews()
        setContentView(binding.root)
    }

    private fun initViews() {
        authLayout = binding.AuthenticationLayout
        receiveCode = binding.receiverCodeLayout
    }

    private fun initAuthViews() {

        binding.submitBtn.setOnClickListener {
            val phoneEdt = binding.phoneEdt
            val phone = phoneEdt.text.toString()
            if (phone.isEmpty())
                phoneEdt.setBackgroundResource(R.drawable.error_input_bg)
            else {
                authLayout?.visibility = View.GONE
                receiveCode?.visibility = View.VISIBLE
                binding.messageTxt.text = " کد 5 رقمی به شماره موبایل $phone ارسال گردید."
                initReceiverCodeView()
            }
        }


    }

    private fun initReceiverCodeView() {

        val resendBtn = binding.resendBtn
        val changePhoneNumberBtn = binding.changePhoneBtn
        val timerTxt = binding.timerTxt

        val timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val second = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / 1000) / 60
                val format = String.format(Locale.getDefault(), "%02d:%02d", minutes, second)
                timerTxt.text = format
            }

            override fun onFinish() {
                resendBtn.visibility = View.VISIBLE
                timerTxt.visibility = View.GONE
            }
        }
        timer.start()

        resendBtn.setOnClickListener {

        }

        changePhoneNumberBtn.setOnClickListener {
            authLayout?.visibility = View.VISIBLE
            receiveCode?.visibility = View.GONE
            timer.cancel()
        }

        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

}
