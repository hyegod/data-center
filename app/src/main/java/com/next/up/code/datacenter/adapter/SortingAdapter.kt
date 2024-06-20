package com.next.up.code.datacenter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.next.up.code.datacenter.databinding.ItemSortingBinding
import com.next.up.code.datacenter.utils.DummySorting

class SortingAdapter : RecyclerView.Adapter<SortingAdapter.FolderViewHolder>() {
    private val listSorting = ArrayList<DummySorting>()
    private var selectedPosition = 0
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newSortingData: List<DummySorting>) {
        listSorting.clear()
        listSorting.addAll(newSortingData)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = ItemSortingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder((view))
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(listSorting[position], position)
    }

    override fun getItemCount(): Int = listSorting.size


    inner class FolderViewHolder(private val binding: ItemSortingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(sorting: DummySorting, position: Int) {
            binding.root.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onItemClickCallback?.onItemClicked(sorting)
            }
            binding.apply {
                tvSorting.text = sorting.sortingName
            }


        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(sorting: DummySorting)
    }

}