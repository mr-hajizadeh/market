package com.se7en.market.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.se7en.market.databinding.ViewHolderProductSliderBinding

class ProductSliderAdapter(private val urls: List<String>) :
    RecyclerView.Adapter<ProductSliderAdapter.SliderViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        context = parent.context
        return SliderViewHolder(
            ViewHolderProductSliderBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = urls.size

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setData(urls[position])
    }

    class SliderViewHolder(private val binding: ViewHolderProductSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val context: Context = binding.root.context

        fun setData(url: String) {
            Glide.with(binding.root.context).load(url).into(binding.productImg)
        }

    }

}