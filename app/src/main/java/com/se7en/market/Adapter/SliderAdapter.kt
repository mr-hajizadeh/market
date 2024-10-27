package com.se7en.market.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.se7en.market.Model.SliderModel
import com.se7en.market.databinding.SliderViewHolderBinding

class SliderAdapter(private val items: List<SliderModel>) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        context = parent.context
        return SliderViewHolder(
            SliderViewHolderBinding.inflate(
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

    class SliderViewHolder(private val binding: SliderViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(item: SliderModel) {
            binding.sliderImg.setImageResource(item.image)
        }
    }

}