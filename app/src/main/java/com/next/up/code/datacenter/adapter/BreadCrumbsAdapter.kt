package com.next.up.code.datacenter.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.next.up.code.core.databinding.ItemsBreadcrumbsBinding
import com.next.up.code.core.domain.model.BreadCrumbs
import com.next.up.code.core.domain.model.News
import com.next.up.code.datacenter.databinding.ItemNewsBinding
import com.squareup.picasso.Picasso

class BreadCrumbsAdapter : ListAdapter<BreadCrumbs, BreadCrumbsAdapter.ViewHolder>(diffCallback) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(private val binding: ItemsBreadcrumbsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(breadCrumbs: BreadCrumbs) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(breadCrumbs)
            }
            with(binding) {

                tvFileName.text = breadCrumbs.fileName

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemsBreadcrumbsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    interface OnItemClickCallback {
        fun onItemClicked(breadCrumb: BreadCrumbs)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<BreadCrumbs>() {
            override fun areItemsTheSame(oldItem: BreadCrumbs, newItem: BreadCrumbs): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BreadCrumbs, newItem: BreadCrumbs): Boolean {
                return oldItem == newItem
            }
        }
    }

}
