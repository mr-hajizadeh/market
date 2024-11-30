package com.se7en.market.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.se7en.market.Model.CategoryModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Utils.Constance.Companion.IMAGE_URL_PATH
import com.se7en.market.databinding.ViewHolderCategoryBinding

class CategoryAdapter(private val items: MutableList<ListItemModel>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        context = parent.context
        return CategoryViewHolder(
            ViewHolderCategoryBinding.inflate(
                LayoutInflater.from(context),
                parent, false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = items[position] as CategoryModel
        holder.setData(item, context)
    }

    class CategoryViewHolder(val binding: ViewHolderCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(item: CategoryModel, context: Context) {
            Glide.with(context)
                .load("$IMAGE_URL_PATH${item.image}")
                .into(binding.categoryImg)
            binding.categoryTitleTxt.text = item.title
        }
    }

}