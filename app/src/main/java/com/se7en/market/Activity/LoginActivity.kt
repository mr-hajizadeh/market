package com.se7en.market.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.se7en.market.Utils.Helper
import com.se7en.market.Utils.SmsBroadcastReceiver
import com.se7en.market.databinding.ActivityLoginBinding
import java.util.Locale

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val smsBroadcastReceiver = SmsBroadcastReceiver()

    private var authenticationLayout: LinearLayout? = null
    private var receiveCodeLayout: LinearLayout? = null
    private var phoneEdt: EditText? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsBroadcastReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        initViews()
        setupAuthenticationViews()
        startSmsRetriever()
        setContentView(binding.root)
    }

    private fun initViews() {
        authenticationLayout = binding.AuthenticationLayout
        receiveCodeLayout = binding.receiverCodeLayout
        phoneEdt = binding.phoneEdt
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    private fun setupAuthenticationViews() {
        val submitBtn = binding.submitBtn
        submitBtn.setOnClickListener {
            val phone = phoneEdt?.text.toString()
            showAuthenticationLayout(false)
            showReceiveCodeLayout()
            binding.messageTxt.text = "کد 5 رقمی به شماره موبایل $phone ارسال گردید."
            initReceiverCodeView()
            Log.i("TAG", "onFinish: ${Helper.getAppHash(this)}")
        }
        convertToPersianEditText(phoneEdt!!, submitBtn = submitBtn)
    }

    private fun initReceiverCodeView() {

        val resendBtn = binding.resendBtn
        val changePhoneNumberBtn = binding.changePhoneBtn
        val timerTxt = binding.timerTxt
        val editTexts = listOf(
            binding.firstEdt,
            binding.secondEdt,
            binding.thirdEdt,
            binding.fourthEdt,
            binding.fifthEdt
        )

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
            showAuthenticationLayout()
            showReceiveCodeLayout(false)
            timer.cancel()
        }

        editTexts[0].requestFocus()
        handleCodeEditText(editTexts)

        binding.loginBtn.setOnClickListener {
            if (!validateEditText(editTexts)) Toast.makeText(
                this,
                "Incorrect code!",
                Toast.LENGTH_SHORT
            ).show()
            else loginIntent()
        }

    }

    private fun handleCodeEditText(editTexts: List<EditText>) {
        editTexts.forEach {
            it.addTextChangedListener(object : TextWatcher {
                var isChange = false

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length!! > 0) {
                        if (editTexts.lastIndex != editTexts.indexOf(it)) editTexts[editTexts.indexOf(
                            it
                        ) + 1].requestFocus()
                        if (validateEditText(editTexts)) loginIntent()
                        if (!isChange) {
                            isChange = true
                            val convertedText = convertToPersianNumbers(s.toString())
                            if (convertedText != it.text.toString()) {
                                it.setText(convertedText)
                                it.setSelection(convertedText.length)
                            }
                            isChange = false
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    private fun loginIntent() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun startSmsRetriever() {
        val client = SmsRetriever.getClient(this /* context */)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.i("TAG", "startSmsRetriever: $it")
        }
        task.addOnFailureListener {
            Log.i("TAG", "startSmsRetriever: ")
        }
        smsBroadcastReceiver.setListener(object : SmsBroadcastReceiver.SmsListener {
            override fun onSmsReceived(message: String) {
                Log.i("TAG", "onSmsReceived: $message")
            }

            override fun onSmsTimeOut() {
                Log.i("TAG", "onSmsTimeOut: ")
            }
        })
    }

    private fun showAuthenticationLayout(isVisibility: Boolean = true) {
        if (isVisibility) authenticationLayout?.visibility = View.VISIBLE
        else authenticationLayout?.visibility = View.GONE
    }

    private fun showReceiveCodeLayout(isVisibility: Boolean = true) {
        if (isVisibility) receiveCodeLayout?.visibility = View.VISIBLE
        else receiveCodeLayout?.visibility = View.GONE
    }

}
