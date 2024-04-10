package com.example.apfinalproject.interest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apfinalproject.databinding.FilterRowBinding
import com.example.apfinalproject.model.InterestCategories
import com.example.apfinalproject.ui.InterestAdapter
import com.example.apfinalproject.ui.MainViewModel

class FilterAdapter(viewModel: MainViewModel, interests: MutableMap<String, MutableList<InterestCategories.Interest>>)
                    : ListAdapter<String, FilterAdapter.VH>(InterestAdapter.InterestDiff()){

    inner class VH(val rowPostBinding : FilterRowBinding)
        : RecyclerView.ViewHolder(rowPostBinding.root) {
        init {}
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterAdapter.VH {
        val filterRowBinding = FilterRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(filterRowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val rowBinding = holder.rowPostBinding
        val item = getItem(position)
    }
}