package com.next.up.code.datacenter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.next.up.code.core.databinding.ItemsRolesBinding
import com.next.up.code.core.domain.model.Roles
import com.next.up.code.datacenter.R

class FolderAdapter : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    private val listRoles = ArrayList<Roles>()
    private var selectedPosition = 0
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newList: List<Roles>) {
        listRoles.clear()
        listRoles.addAll(newList)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = ItemsRolesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder((view))
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(listRoles[position], position)
    }

    override fun getItemCount(): Int = listRoles.size


    inner class FolderViewHolder(private val binding: ItemsRolesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
        fun bind(roles: Roles, position: Int) {
            binding.root.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onItemClickCallback?.onItemClicked(roles)
            }
            binding.apply {
                folderName.text = when (roles.id) {
                    1 -> {
                        itemView.context.getString(R.string.text_all)
                    }

                    else -> {
                        roles.roleName
                    }
                }

                val cardColorRes = if (selectedPosition == position) {
                    R.color.primaryColor
                } else {
                    R.color.colorCard
                }
                val textColorRes = if (selectedPosition == position) {
                    R.color.white
                } else {
                    R.color.dark
                }
                cvFolder.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context, cardColorRes
                    )
                )
                folderName.setTextColor(ContextCompat.getColor(itemView.context, textColorRes))
                tvTotal.setTextColor(ContextCompat.getColor(itemView.context, textColorRes))
//                tvTotal.text = "( "+ roles.count.toString() +" )"
            }


        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(roles: Roles)
    }

}