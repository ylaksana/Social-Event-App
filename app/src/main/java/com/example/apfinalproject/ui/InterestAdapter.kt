package com.example.apfinalproject.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apfinalproject.interest.InterestCategories.Interest
import com.example.apfinalproject.databinding.InterestItemBinding

class InterestAdapter(private val viewModel: MainViewModel)
//    : ListAdapter<Interest, InterestAdapter.InterestViewHolder>(InterestDiff()) {
    : ListAdapter<String, InterestAdapter.InterestViewHolder>(InterestDiff()) {
    inner class InterestViewHolder(val interestItemBinding: InterestItemBinding)
        : RecyclerView.ViewHolder(interestItemBinding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestViewHolder {
        val interestItemBinding = InterestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InterestViewHolder(interestItemBinding)
    }

    override fun onBindViewHolder(holder: InterestViewHolder, position: Int) {
        val interestItemBinding = holder.interestItemBinding
        val interest = getItem(position)
//        interestItemBinding.interestName.text = interest.subcategory
        interestItemBinding.interestName.text = interest

    }

//    class InterestDiff : DiffUtil.ItemCallback<Interest>() {
//        override fun areItemsTheSame(oldItem: Interest, newItem: Interest): Boolean {
//            return oldItem.subcategory == newItem.subcategory
//        }
//
//        override fun areContentsTheSame(oldItem: Interest, newItem: Interest): Boolean {
//            return oldItem.subcategory == newItem.subcategory &&
//                    oldItem.category == newItem.category
//        }
//    }
    class InterestDiff : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}