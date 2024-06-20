package com.next.up.code.datacenter.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.next.up.code.core.R
import com.next.up.code.core.databinding.ItemLatestFileBinding
import com.next.up.code.core.domain.model.LatestFile
import com.next.up.code.datacenter.ui.file.FileViewModel
import com.next.up.code.datacenter.ui.properties.PropertiesActivity
import com.squareup.picasso.Picasso

class AllFileAdapter(
    private val viewModel: FileViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onDeleteButtonClickListener: OnDeleteButtonClickListener,
    private val onRenameButtonClickListener: OnRenameButtonClickListener,
    private val onDownloadButtonClickListener: OnDownloadButtonClickListener

) : ListAdapter<LatestFile, AllFileAdapter.ViewHolder>(diffCallback) {

    private var popupWindow: PopupWindow? = null
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(private val binding: ItemLatestFileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(latestFile: LatestFile) {
            binding.ivMore.setOnClickListener {
                showPopupMenu(it, latestFile)
            }
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(latestFile)
            }
            with(binding) {
                when (latestFile.fileType) {
                    "pdf" -> {
                        ivTypeFile.setImageResource(R.drawable.ic_pdf)
                    }

                    "doc" -> {
                        ivTypeFile.setImageResource(R.drawable.ic_word)
                    }

                    "docx" -> {
                        ivTypeFile.setImageResource(R.drawable.ic_word)
                    }

                    "xls" -> {
                        ivTypeFile.setImageResource(R.drawable.ic_excel)
                    }

                    "xlsx" -> {
                        ivTypeFile.setImageResource(R.drawable.ic_excel)
                    }

                    "pptx" -> {
                        ivTypeFile.setImageResource(R.drawable.ic_power_point)
                    }

                    "folder" -> {
                        ivTypeFile.setImageResource(R.drawable.ic_folder_file)
                        tvFileSize.visibility = View.GONE
                        view.visibility = View.GONE
                    }

                    "png" -> {
                        Picasso.get()
                            .load("https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName)
                            .resize(300, 300)
                            .into(ivTypeFile)
                    }

                    "jpeg" -> {
                        Picasso.get()
                            .load("https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName)
                            .resize(300, 300)
                            .into(ivTypeFile)
                    }

                    "jpg" -> {
                        Picasso.get()
                            .load("https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName)
                            .resize(300, 300)
                            .into(ivTypeFile)
                    }

                    "mp4" -> {
                        ivTypeFile.setImageResource(com.next.up.code.datacenter.R.drawable.ic_play)
                    }

                    "avi" -> {
                        ivTypeFile.setImageResource(com.next.up.code.datacenter.R.drawable.ic_play)
                    }

                    "mkv" -> {
                        ivTypeFile.setImageResource(com.next.up.code.datacenter.R.drawable.ic_play)
                    }

                    "wmv" -> {
                        ivTypeFile.setImageResource(com.next.up.code.datacenter.R.drawable.ic_play)
                    }

                    "ogg" -> {
                        ivTypeFile.setImageResource(com.next.up.code.datacenter.R.drawable.ic_play)
                    }

                    "3gp" -> {
                        ivTypeFile.setImageResource(com.next.up.code.datacenter.R.drawable.ic_play)
                    }

                    "mp3" -> {
                        ivTypeFile.setImageResource(com.next.up.code.datacenter.R.drawable.ic_audio)
                    }

                    "opus" -> {
                        ivTypeFile.setImageResource(com.next.up.code.datacenter.R.drawable.ic_audio)
                    }

                }

                tvFileSize.text = latestFile.fileSize
                tvFilename.text = latestFile.fileName.replace("folder-file/", "")
                tvTimeAgo.text = latestFile.timeAgo

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLatestFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    interface OnItemClickCallback {
        fun onItemClicked(latestFile: LatestFile)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<LatestFile>() {
            override fun areItemsTheSame(oldItem: LatestFile, newItem: LatestFile): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LatestFile, newItem: LatestFile): Boolean {
                return oldItem == newItem
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showPopupMenu(view: View, item: LatestFile) {
        val context = view.context
        popupWindow?.dismiss()
        val inflater = LayoutInflater.from(view.context)
        val popupView = inflater.inflate(R.layout.popup_custom_more_file, null)

        popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )

        val backgroundDrawable = ColorDrawable(Color.TRANSPARENT)
        popupWindow?.setBackgroundDrawable(backgroundDrawable)

        popupWindow?.isFocusable = true
        popupWindow?.isOutsideTouchable = true

        val btnShare = popupView.findViewById<LinearLayout>(R.id.btn_share)
        val btnRename = popupView.findViewById<LinearLayout>(R.id.btn_rename)
        val btnDownload = popupView.findViewById<LinearLayout>(R.id.btn_download)
        val btnFavorite = popupView.findViewById<LinearLayout>(R.id.btn_favorite)
        val btnDelete = popupView.findViewById<LinearLayout>(R.id.btn_delete)
        val btnDetails = popupView.findViewById<LinearLayout>(R.id.btn_details)

        viewModel.getUser.observe(lifecycleOwner) {
            if (it.permissionEdit != "1") {
                btnRename.visibility = View.GONE
            }
            if (it.permissionDelete != "1") {
                btnDelete.visibility = View.GONE
            }
            if (it.permissionDownload != "1") {
                btnDownload.visibility = View.GONE
            }

        }
        if (item.fileType == "folder") {
            btnShare.visibility = View.GONE
            btnFavorite.visibility = View.GONE
            btnDownload.visibility = View.GONE
        } else {
            btnShare.visibility = View.VISIBLE
            btnFavorite.visibility = View.GONE
            btnDownload.visibility = View.VISIBLE
            btnRename.visibility = View.GONE
        }



        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            val fileUrl = "https://data-canter.taekwondosulsel.info/storage/" + item.fileName
            val title = "Share From Data Center App"
            val subject = "Filename: ${item.fileName.replace("folder-file", "")}"
            val message = "Download link: $fileUrl"
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TITLE, title)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooserTitle = "Share via"
            val shareChooser = Intent.createChooser(shareIntent, chooserTitle)

            context.startActivity(shareChooser)
        }

        btnDelete.setOnClickListener {
            popupWindow?.dismiss()
            onDeleteButtonClickListener.onDeleteButtonClicked(item)
        }


        btnDownload.setOnClickListener {
            popupWindow?.dismiss()
            onDownloadButtonClickListener.onDownloadButtonClicked(item)
        }
        btnRename.setOnClickListener {
            popupWindow?.dismiss()
            onRenameButtonClickListener.onRenameButtonClicked(item)
        }
        btnDetails.setOnClickListener {
            val gson = Gson()
            val itemJson = gson.toJson(item)
            val intent = Intent(context, PropertiesActivity::class.java)
            intent.putExtra("item", itemJson)
            context.startActivity(intent)
        }



        popupView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popupWindow?.dismiss()
                true
            } else {
                false
            }
        }

        popupWindow?.showAsDropDown(view)
    }

    interface OnDeleteButtonClickListener {
        fun onDeleteButtonClicked(item: LatestFile)
    }

    interface OnRenameButtonClickListener {
        fun onRenameButtonClicked(item: LatestFile)
    }

    interface OnDownloadButtonClickListener {
        fun onDownloadButtonClicked(item: LatestFile)
    }


}
