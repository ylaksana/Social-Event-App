package com.example.apfinalproject.interest

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apfinalproject.databinding.FilterRowBinding
import com.example.apfinalproject.ui.InterestAdapter
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.R
import com.example.apfinalproject.databinding.InterestItemBinding

class FilterAdapter(private val viewModel: MainViewModel)
    : ListAdapter<InterestCategories.Interest, FilterAdapter.VH>(InterestDiff()){

    inner class VH(val rowPostBinding : InterestItemBinding)
        : RecyclerView.ViewHolder(rowPostBinding.root) {
        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                val item = getItem(position)
                item.selected = !item.selected
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterAdapter.VH {
        val interestItemBinding = InterestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(interestItemBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val rowBinding = holder.rowPostBinding
        val item = getItem(position)
        rowBinding.interestName.text = item.category
        if(item.selected){
            rowBinding.interestName.setBackgroundResource(R.drawable.interest_background_gray)
        } else {
            rowBinding.interestName.setBackgroundResource(R.drawable.interest_background)
        }
    }

    class InterestDiff : DiffUtil.ItemCallback<InterestCategories.Interest>() {
        override fun areItemsTheSame(oldItem: InterestCategories.Interest, newItem: InterestCategories.Interest): Boolean {
            // Return true if the items are the same.
            return oldItem.category == newItem.category
        }

        override fun areContentsTheSame(oldItem: InterestCategories.Interest, newItem: InterestCategories.Interest): Boolean {
            // Return true if the contents of the items are the same.
            return oldItem == newItem
        }
    }

}