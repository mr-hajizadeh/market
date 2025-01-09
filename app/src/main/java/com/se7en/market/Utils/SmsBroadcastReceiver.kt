package com.se7en.market.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {

    private lateinit var otpReceiver: OTPReceiveListener

    fun initOTPListener(receiver: OTPReceiveListener) {
        this.otpReceiver = receiver
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val smsMessage: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Log.d("OTP_Message", smsMessage)

                    try {
                        if (::otpReceiver.isInitialized) {
                            val otpRegex = Regex("کد تایید شما: (\\d{5})")
                            val matchResult = otpRegex.find(smsMessage)
                            val processedOtp = matchResult?.groups?.get(1)?.value

                            if (!processedOtp.isNullOrBlank()) {
                                Log.i("TAG", "onReceive: OTP extracted: $processedOtp")
                                otpReceiver.onOTPReceived(processedOtp)
                            } else {
                                Log.e("SmsBroadcastReceiver", "OTP not found in message")
                            }
                        } else {
                            Log.e("SmsBroadcastReceiver", "otpReceiver is not initialized")
                        }
                    } catch (e: Exception) {
                        Log.e("SmsBroadcastReceiver", "Error processing OTP", e)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    if (::otpReceiver.isInitialized) {
                        otpReceiver.onOTPTimeOut()
                    } else {
                        Log.e("SmsBroadcastReceiver", "otpReceiver is not initialized")
                    }
                }
            }
        }
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut()
    }
}