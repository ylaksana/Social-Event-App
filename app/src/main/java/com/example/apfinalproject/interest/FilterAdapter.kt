package com.example.apfinalproject.interest

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.R
import com.example.apfinalproject.databinding.InterestItemBinding

class FilterAdapter(private val viewModel: MainViewModel)
    : ListAdapter<InterestCategories.Interest, FilterAdapter.VH>(InterestDiff()){

        private var currentPosition = 0

    inner class VH(val rowPostBinding : InterestItemBinding)
        : RecyclerView.ViewHolder(rowPostBinding.root) {
        init {
            itemView.setOnClickListener {
                val previousPosition: Int = currentPosition
                currentPosition = bindingAdapterPosition
                val previousItem = getItem(previousPosition)
                val item = getItem(currentPosition)

                notifyItemChanged(previousPosition)
                notifyItemChanged(currentPosition)
                Log.d("FilterBinding", "${item.category}, ${item.selected}")
                if(item == previousItem) {
                    if(item.selected){
                        item.selected = false
                        viewModel.setFilter("")
                    }
                    else{
                        item.selected = true
                        viewModel.setFilter(item.category)
                    }
                }
                else{
                    previousItem.selected = false
                    item.selected = true
                    viewModel.setFilter(item.category)
                }

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