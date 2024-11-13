package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.Ui.ModalBottomSheetMap
import com.se7en.market.Model.AddressModel
import com.se7en.market.R
import com.se7en.market.databinding.ViewHolderAddressBinding
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import okhttp3.internal.notify
import org.neshan.common.model.LatLng

class AddressAdapter(
    private val onClickAddress: onClickAddressListener,
    private var items: MutableList<AddressModel> = mutableListOf()
) :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private lateinit var context: Context

    fun getAddressList() = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        context = parent.context
        return AddressViewHolder(
            ViewHolderAddressBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ), onClickAddress
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.setData(items[position], context)
    }

    class AddressViewHolder(
        private val binding: ViewHolderAddressBinding,
        private val onClickAddressListener: onClickAddressListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun setData(item: AddressModel, context: Context) = binding.apply {
            if (item.active) {
                containerItem.setBackgroundCompat(context.getDrawable(R.drawable.input_bg))
                statusImg.setImageDrawable(context.getDrawable(R.drawable.ic_square_check))
            } else {
                containerItem.setBackgroundCompat(null)
                statusImg.setImageDrawable(context.getDrawable(R.drawable.ic_location))
            }

            titleTxt.visibility = if (item.title == null) View.GONE else View.VISIBLE
            titleTxt.text = item.title
            addressTxt.text = item.address
            moreBtn.setOnClickListener {
                if (settingLayout.isVisible) {
                    settingLayout.visibility = View.GONE
                } else {
                    settingLayout.visibility = View.VISIBLE
                }
            }
            editBtn.setOnClickListener {
                val modal = ModalBottomSheetMap(item)
                modal.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    ModalBottomSheetMap.TAG
                )

            }

            binding.root.setOnClickListener {
                onClickAddressListener.activeAddress(item)
            }
            deleteBtn.setOnClickListener {
                onClickAddressListener.deleteAddress(item)
            }

        }

    }

    fun removeItem(item: AddressModel) {
        val position = items.indexOf(item)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setActiveAddress(item: AddressModel) {
        items.forEach { it.active = false }
        item.active = true
        notifyDataSetChanged()
    }


    interface onClickAddressListener {
        fun activeAddress(item: AddressModel)
        fun deleteAddress(item: AddressModel)
    }

}