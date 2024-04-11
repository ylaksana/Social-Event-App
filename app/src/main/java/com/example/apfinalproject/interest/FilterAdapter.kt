package com.example.apfinalproject.interest

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apfinalproject.databinding.FilterRowBinding
import com.example.apfinalproject.ui.InterestAdapter
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.R
import com.example.apfinalproject.databinding.InterestItemBinding

class FilterAdapter(private val viewModel: MainViewModel)
    : ListAdapter<String, FilterAdapter.VH>(InterestAdapter.InterestDiff()){

    private var currentFilterPosition = 0
    private var previousFilterPosition = 0
    inner class VH(val rowPostBinding : InterestItemBinding)
        : RecyclerView.ViewHolder(rowPostBinding.root) {
        init {

            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                val item = getItem(position)
                if(position == RecyclerView.NO_POSITION) return@setOnClickListener
                // Save the old value of currentFilterPosition
                previousFilterPosition = currentFilterPosition
                notifyItemChanged(previousFilterPosition)
                currentFilterPosition = position
                notifyItemChanged(currentFilterPosition)

                Log.d("position", "previous = $previousFilterPosition")
                Log.d("position", "current = $currentFilterPosition")



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
        rowBinding.interestName.text = item
        if(previousFilterPosition != currentFilterPosition) {
            if (position == currentFilterPosition) {
                rowBinding.interestName.setBackgroundResource(R.drawable.interest_background_gray)
            } else {
                rowBinding.interestName.setBackgroundResource(R.drawable.interest_background)
            }
        }
        else{
            rowBinding.interestName.setBackgroundResource(R.drawable.interest_background)
        }
    }
}