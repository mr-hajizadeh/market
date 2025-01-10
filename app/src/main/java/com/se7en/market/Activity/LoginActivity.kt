package com.se7en.market.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import com.se7en.market.Model.LoginInfoModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Utils.SmsBroadcastReceiver
import com.se7en.market.ViewModel.LoginViewModel
import com.se7en.market.databinding.ActivityLoginBinding
import java.util.Locale

class LoginActivity : BaseActivity(), SmsBroadcastReceiver.OTPReceiveListener {

    private lateinit var binding: ActivityLoginBinding
    private var smsReceiver: SmsBroadcastReceiver = SmsBroadcastReceiver()
    private val viewModel: LoginViewModel by viewModels<LoginViewModel>()
    private var authenticationLayout: LinearLayout? = null
    private var receiveCodeLayout: LinearLayout? = null
    private var phoneEdt: EditText? = null
    private var submitBtn: Button? = null
    private var loginBtn: Button? = null
    private var editTexts: List<EditText>? = null

    private var _phone: String? = null
    private val phone get() = _phone!!
    private var _token: String? = null
    private val token get() = _token!!

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsReceiver, intentFilter, RECEIVER_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        smsReceiver.initOTPListener(this)
        initViews()
        setContentView(binding.root)
        startSMSListener()
        sendPhone()
        otpListener()
//        otpCodeListener()
        confirmCodeListener()
//        val helper = Helper(this)
//        Toast.makeText(this, helper.appSignatures.toString(), Toast.LENGTH_LONG).show()
//        Log.i("TAG", "appSignatures: ${helper.appSignatures}")
    }

    private fun initViews() {
        authenticationLayout = binding.AuthenticationLayout
        receiveCodeLayout = binding.receiverCodeLayout
        phoneEdt = binding.phoneEdt
        submitBtn = binding.submitBtn
        loginBtn = binding.loginBtn
        editTexts = listOf(
            binding.firstEdt,
            binding.secondEdt,
            binding.thirdEdt,
            binding.fourthEdt,
            binding.fifthEdt
        )
        convertToPersianEditText(phoneEdt!!, submitBtn = submitBtn)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    private fun setupAuthenticationViews(phone: String) {
        showAuthenticationLayout(false)
        showReceiveCodeLayout()
        binding.messageTxt.text = "کد 5 رقمی به شماره موبایل $phone ارسال گردید."
        initReceiverCodeView()
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
            viewModel.sendPhone(phoneWithoutZero(phone))
            timer.start()
        }

        changePhoneNumberBtn.setOnClickListener {
            showAuthenticationLayout()
            showReceiveCodeLayout(false)
            timer.cancel()
        }

        editTexts?.get(0)?.requestFocus()
        editTextsChangeText(editTexts!!)

        loginBtn?.setOnClickListener {
            if (!validateEditText(editTexts!!)) Toast.makeText(
                this,
                "Incorrect code!",
                Toast.LENGTH_SHORT
            ).show()
            else {
                loginLoading(true)
                val code: String =
                    editTexts!!.joinToString("") { convertToEnglishNumbers(it.text.toString()) }
                viewModel.confirmCode(token, code)
            }
        }

    }

    private fun editTextsChangeText(editTexts: List<EditText>) {
        editTexts.forEach {
            it.addTextChangedListener(object : TextWatcher {
                var isChange = false

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length!! > 0) {
                        if (editTexts.lastIndex != editTexts.indexOf(it))
                            editTexts[editTexts.indexOf(
                                it
                            ) + 1].requestFocus()
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

    private fun customIntent(activity: Class<out Activity> = MainActivity::class.java) {
        loginLoading()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@LoginActivity, activity))
            finish()
        }, 500)
    }

    private fun startSMSListener() {
        try {
            val client = SmsRetriever.getClient(this)
            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }

            task.addOnFailureListener {
                // Fail to start API
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun extractVerificationCode(message: String): String? {
        val regex = Regex("\\d{5}")
        val matchResult = regex.find(message)
        return matchResult?.value
    }

    private fun populateVerificationCode(code: String) {
        for (i in code.indices) {
            if (i < editTexts?.size!!) {
                editTexts?.get(i)?.setText(code[i].toString())
            }
        }
    }

    private fun showAuthenticationLayout(isVisibility: Boolean = true) {
        if (isVisibility) authenticationLayout?.visibility = View.VISIBLE
        else authenticationLayout?.visibility = View.GONE
    }

    private fun showReceiveCodeLayout(isVisibility: Boolean = true) {
        if (isVisibility) receiveCodeLayout?.visibility = View.VISIBLE
        else receiveCodeLayout?.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun otpListener() {
        viewModel.tokenStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    authLoading(true)
                }

                is NetworkResult.Success -> {
                    authLoading()
                    setupAuthenticationViews(phone)
                    _token = result.response.data.token
//                    viewModel.otpCode(token)
                    Log.i("TAG", "otpListener: ${result.response}")
                }

                is NetworkResult.Error -> {
                    authLoading()
                    Log.i("TAG", "otpErrorListener: ${result.message}")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun sendPhone() {
        submitBtn?.setOnClickListener {
            _phone = phoneEdt?.text.toString()
            viewModel.sendPhone(phoneWithoutZero(phone))
        }
    }

    private fun phoneWithoutZero(phone: String) = convertToEnglishNumbers(phone).removePrefix("0")

    private fun authLoading(isLoading: Boolean = false) {
        val progressLoading = binding.authProgressLoading
        if (isLoading) {
            submitBtn?.isEnabled = false
            progressLoading.visibility = View.VISIBLE
        } else {
            submitBtn?.isEnabled = true
            progressLoading.visibility = View.GONE
        }
    }

    private fun loginLoading(isLoading: Boolean = false) {
        val progressLoading = binding.loginProgressLoading
        if (isLoading) {
            loginBtn?.isEnabled = false
            progressLoading.visibility = View.VISIBLE
        } else {
            loginBtn?.isEnabled = true
            progressLoading.visibility = View.GONE
        }
    }

    private fun confirmCodeListener() {
        viewModel.confirmStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> loginLoading(true)


                is NetworkResult.Success -> {
                    loginLoading(false)
                    val token = result.response.data.token
                    viewModel.saveLoginInfo(LoginInfoModel(phoneWithoutZero(phone), token))
                    Log.i("TAG", "getLoginInfo: ${viewModel.getLoginInfo().value}")
                    val user = result.response.data.user
                    if (user.has_address) customIntent(SplashActivity::class.java)
                    else customIntent(MapActivity::class.java)

                    Log.i("TAG", "confirmCodeListener:TOKEN $token")
                    Log.i("TAG", "confirmCodeListener: ${result.response.data}")
                }

                is NetworkResult.Error -> {
                    loginLoading(false)
                    Snackbar.make(binding.main, result.message, Snackbar.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "confirmCodeListener: ${result.message}")
                }
            }
        }
    }

    override fun onOTPReceived(otp: String) {
        val code = extractVerificationCode(otp).toString()
        Log.i("onOTPReceived", "activityCode: $code")
        populateVerificationCode(code)
        Log.i("onOTPReceived", "activityToken: $token")
        viewModel.confirmCode(token, code)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver)
    }

    override fun onOTPTimeOut() {
        Log.i("TAG", "onOTPTimeOut: timeOut")
    }

}
