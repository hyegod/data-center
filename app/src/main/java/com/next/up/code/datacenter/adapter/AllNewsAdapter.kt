package com.next.up.code.datacenter.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.next.up.code.core.domain.model.News
import com.next.up.code.datacenter.databinding.ItemNewsBinding
import com.squareup.picasso.Picasso

class AllNewsAdapter : ListAdapter<News, AllNewsAdapter.ViewHolder>(diffCallback) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(news)
            }
            with(binding) {

                tvCategories.text = "Info BMBK"
                tvTitle.text = news.title
                tvDescription.text = Html.fromHtml(news.description)
                Picasso.get()
                    .load("https://putr.sulselprov.go.id/uploads/berita/" + news.thumbnail)
                    .into(ivNews)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    interface OnItemClickCallback {
        fun onItemClicked(news: News)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem == newItem
            }
        }
    }

}
