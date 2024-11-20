package com.se7en.market.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

@Suppress("DEPRECATION")
class Helper(val context: Context) {

    val appSignatures: ArrayList<String>
        @SuppressLint("PackageManagerGetSignatures")
        get() {
            val appCodes = ArrayList<String>()

            try {
                val packageName = context.packageName
                val packageManager = context.packageManager
                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    packageManager.getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                    )
                else
                    packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)

                val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    packageInfo.signingInfo.apkContentsSigners
                else
                    packageInfo.signatures

                signatures
                    .mapNotNull { hash(packageName, it.toCharsString()) }
                    .mapTo(appCodes) { String.format("%s", it) }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.v(TAG, "Unable to find package to obtain hash.", e)
            }

            return appCodes
        }

    companion object {
        val TAG: String = Helper::class.java.simpleName
        private const val HASH_TYPE = "SHA-256"
        private const val NUM_HASHED_BYTES = 9
        private const val NUM_BASE64_CHAR = 11

        private fun hash(packageName: String, signature: String): String? {
            val appInfo = "$packageName $signature"
            try {
                val messageDigest = MessageDigest.getInstance(HASH_TYPE)
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
                var hashSignature = messageDigest.digest()

                hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)

                var base64Hash =
                    Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
                base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

                Log.v(
                    TAG + "sms_sample_test",
                    String.format("pkg: %s -- hash: %s", packageName, base64Hash)
                )
                return base64Hash
            } catch (e: NoSuchAlgorithmException) {
                Log.v(TAG + "sms_sample_test", "hash:NoSuchAlgorithm", e)
            }

            return null
        }
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

}