package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.BaseActivity
import com.se7en.market.Model.DiscountModel
import com.se7en.market.R
import com.se7en.market.Utils.Constance.Companion.LABEL_DISCOUNT
import com.se7en.market.databinding.ViewHolderDiscountBinding
import zabih.lib.zipersiancalendar.ZiPersianCalendar
import java.text.SimpleDateFormat
import java.util.Locale


class DiscountAdapter(private val discountList: MutableList<DiscountModel>) :
    RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder>() {

    private lateinit var context: Context
    private val persianCalendar = ZiPersianCalendar()
    private val baseActivity = BaseActivity()

    inner class DiscountViewHolder(binding: ViewHolderDiscountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val discountTitle = binding.discountTitleTxt
        val discountAmount = binding.discountAmountTxt
        val discountDescription = binding.discountDescriptionTxt
        val discountCode = binding.discountCodeTxt
        val expireDate = binding.expireDateTxt
        val allowedTimes = binding.allowedTimesTxt
        val copyBtn = binding.copyBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountViewHolder {
        context = parent.context
        return DiscountViewHolder(
            ViewHolderDiscountBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = discountList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DiscountViewHolder, position: Int) {
        val discount = discountList[position]

        holder.discountTitle.text = discount.title
        holder.discountAmount.text =
            "${context.getString(R.string.title_amount)} : ${discount.formatAmountToPersianAmount()} ${
                context.getString(
                    R.string.title_currency
                )
            }"
        holder.discountDescription.visibility =
            if (discount.description == null) View.GONE else View.VISIBLE
        holder.discountDescription.text = discount.description
        persianCalendar.timeInMillis = convertToTimestamp(discount.expiration)
        holder.expireDate.text =
            " قابل استفاده تا ${baseActivity.convertToPersianNumbers(persianCalendar.longDate)}"
        holder.allowedTimes.text =
            if (discount.used) context.getString(R.string.used) else baseActivity.convertToPersianNumbers(
                context.getString(R.string.allowed_times)
            )
        holder.discountCode.text = discount.code

        holder.copyBtn.setOnClickListener {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(LABEL_DISCOUNT, discount.code)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(context, "کد تخفیف کپی شد!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun convertToTimestamp(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        return format.parse(dateString)?.time ?: 0L
    }

}