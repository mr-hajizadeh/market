package com.se7en.market.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.Ui.ModalBottomSheetMap
import com.se7en.market.Model.AddressModel
import com.se7en.market.databinding.AddressViewHolderBinding
import org.neshan.common.model.LatLng

class AddressAdapter(private val items: List<AddressModel>, ) :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        context = parent.context
        return AddressViewHolder(
            AddressViewHolderBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.setData(items[position], context)
    }

    class AddressViewHolder(
        private val binding: AddressViewHolderBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(item: AddressModel, context: Context) = binding.apply {
            titleTxt.visibility = if (item.title.isEmpty()) View.GONE else View.VISIBLE
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
                val modal = ModalBottomSheetMap(LatLng(36.2972, 59.6067))
                modal.show((context as AppCompatActivity).supportFragmentManager, modal.tag)
            }
            deleteBtn.setOnClickListener { }

        }

    }

}