package com.se7en.market.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StrikethroughSpan
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Arrays
import java.util.Locale

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
                    packageInfo.signingInfo?.apkContentsSigners
                else
                    packageInfo.signatures

                signatures
                    ?.mapNotNull { hash(packageName, it.toCharsString()) }
                    ?.mapTo(appCodes) { String.format("%s", it) }
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

        fun formatPriceToPersian(price: Long): String {
            val decimalFormatSymbols = DecimalFormatSymbols(Locale("fa", "IR"))
            val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
            val formattedPrice = decimalFormat.format(price)

            val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
            var persianPrice = formattedPrice
            for (i in 0..9) {
                persianPrice = persianPrice.replace(i.toString(), persianDigits[i])
            }

            return persianPrice
        }

        fun getStrikethroughPriceWithCurrency(price: Long, currency: String): CharSequence {
            val formattedPrice = formatPriceToPersian(price)
            val spannablePrice = SpannableString(formattedPrice).apply {
                setSpan(StrikethroughSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            return if (currency.isNotEmpty())
                TextUtils.concat(spannablePrice, " ", currency)
            else
                spannablePrice
        }

    }

    private fun dpToPx(context: Context, dp: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )

    fun createCustomShape(
        color: Int,
        cornerRadius: Float = 16f,
        style: Paint.Style = Paint.Style.FILL,
    ): Drawable {
        val radius = dpToPx(context, cornerRadius)

        val rectShape = RoundRectShape(
            floatArrayOf(
                radius, radius, radius, radius,
                radius, radius, radius, radius
            ), null, null
        )

        val shapeDrawable = ShapeDrawable(rectShape)
        shapeDrawable.paint.color = color
        shapeDrawable.paint.style = style
        shapeDrawable.paint.strokeWidth = dpToPx(context, 0.8f)
        shapeDrawable.paint.isAntiAlias = true
        shapeDrawable.paint.flags = Paint.ANTI_ALIAS_FLAG

        return shapeDrawable
    }

}