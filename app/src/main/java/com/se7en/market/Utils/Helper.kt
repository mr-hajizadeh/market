package com.se7en.market.Utils

import android.content.Context
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import java.security.MessageDigest

class Helper {
    companion object {
        @RequiresApi(Build.VERSION_CODES.P)
        fun getAppHash(context: Context): String? {
            try {
                // دریافت نام پکیج اپلیکیشن
                val packageName = context.packageName

                // دریافت امضای کلید اپلیکیشن
                val signature = context.packageManager.getPackageInfo(
                    packageName,
                    android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
                ).signingInfo.apkContentsSigners[0].toByteArray()

                // تولید هش SHA-256 از امضای اپلیکیشن
                val messageDigest = MessageDigest.getInstance("SHA-256")
                messageDigest.update(signature)

                // استخراج اولین 9 بایت از هش برای تولید هش کوتاه
                val hashSignature = messageDigest.digest().copyOfRange(0, 9)

                // تبدیل هش به رشته Base64
                return Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    }
}