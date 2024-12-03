package com.se7en.market.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.se7en.market.Model.SliderModel
import com.se7en.market.databinding.ViewHolderBannerBinding

class SliderAdapter :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {
    private var items: List<SliderModel> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<SliderModel>) {
        items = newItems
        notifyDataSetChanged()
    }
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        context = parent.context
        return SliderViewHolder(
            ViewHolderBannerBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setData(items[position])
    }

    class SliderViewHolder(private val binding: ViewHolderBannerBinding) :
        RecyclerView.ViewHolder(binding.root), OnClickListener {

        val context: Context = binding.root.context

        init {
            binding.root.setOnClickListener(this)
        }

        fun setData(item: SliderModel) {
            Glide.with(binding.root.context).load(item.banner).into(binding.sliderImg)
        }

        override fun onClick(v: View?) {
        }
    }

}