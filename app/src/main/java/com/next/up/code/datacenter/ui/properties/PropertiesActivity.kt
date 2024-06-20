package com.next.up.code.datacenter.ui.properties

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.inyongtisto.myhelper.extension.toastSuccess
import com.next.up.code.core.data.Resource
import com.next.up.code.core.domain.model.LatestFile
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.databinding.ActivityPropertiesBinding
import com.next.up.code.datacenter.ui.dialog.DialogEditFolderFragment
import com.next.up.code.datacenter.ui.file.FileViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PropertiesActivity : AppCompatActivity() {
    private var _binding: ActivityPropertiesBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private var popupWindow: PopupWindow? = null
    private val viewModel: FileViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPropertiesBinding.inflate(layoutInflater)
        root = binding?.root
        setContentView(root)
        supportActionBar?.hide()
        setupUI()
        val itemJson = intent.getStringExtra("item")
        if (itemJson != null) {
            val gson = Gson()
            val file = gson.fromJson(itemJson, LatestFile::class.java)
            preferencesSetup()
            token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()

            setupDetail(file)

            binding?.toolbar?.btnMoreSetting?.setOnClickListener { showPopupMenu(it, file) }
            binding?.btnShare?.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                val fileUrl = "https://data-canter.taekwondosulsel.info/storage/" + file.fileName
                val title = "Share From Data Center App"
                val subject = "Filename: ${file.fileName.replace("folder-file", "")}"
                val message = "Download link: $fileUrl"
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TITLE, title)
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                val chooserTitle = "Share via"
                val shareChooser = Intent.createChooser(shareIntent, chooserTitle)

                startActivity(shareChooser)
            }
        }

    }

    private fun preferencesSetup() {
        sharedPreferences = getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME, MODE_PRIVATE
        )
    }

    private fun setupUI() {
        binding?.toolbar?.title?.text = resources.getString(R.string.text_details)
        binding?.toolbar?.btnBack?.setOnClickListener { onBackPressed() }
    }

    private fun setupDetail(file: LatestFile) {
        binding?.apply {
            if (file.fileType == "folder") {
                vShare.visibility = View.GONE
            }
            tvFilename.text = file.fileName.replace("folder-file/", "")
            tvTypeFileItem.text = file.fileType
            tvFileSizeItem.text = file.fileSize ?: "-"
            tvTimeAgoItem.text = file.timeAgo
            tvName.text = file.user
            Picasso.get()
                .load(file.picture)
                .placeholder(R.drawable.ic_profile_pic)
                .error(R.drawable.ic_profile_pic)
                .into(ivPicture)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showPopupMenu(view: View, item: LatestFile) {
        val context = view.context
        popupWindow?.dismiss()
        val inflater = LayoutInflater.from(view.context)
        val popupView =
            inflater.inflate(com.next.up.code.core.R.layout.popup_custom_more_file, null)

        popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )

        val backgroundDrawable = ColorDrawable(Color.TRANSPARENT)
        popupWindow?.setBackgroundDrawable(backgroundDrawable)

        popupWindow?.isFocusable = true
        popupWindow?.isOutsideTouchable = true

        val btnShare = popupView.findViewById<LinearLayout>(com.next.up.code.core.R.id.btn_share)
        val btnRename = popupView.findViewById<LinearLayout>(com.next.up.code.core.R.id.btn_rename)
        val btnDownload =
            popupView.findViewById<LinearLayout>(com.next.up.code.core.R.id.btn_download)
        val btnFavorite =
            popupView.findViewById<LinearLayout>(com.next.up.code.core.R.id.btn_favorite)
        val btnDelete = popupView.findViewById<LinearLayout>(com.next.up.code.core.R.id.btn_delete)
        val btnDetails =
            popupView.findViewById<LinearLayout>(com.next.up.code.core.R.id.btn_details)

        btnDetails.visibility = View.GONE

        if (item.fileType == "folder") {
            btnShare.visibility = View.GONE
            btnFavorite.visibility = View.GONE
            btnDownload.visibility = View.GONE
        } else {
            btnShare.visibility = View.GONE
            btnFavorite.visibility = View.GONE
            btnDownload.visibility = View.VISIBLE
            btnRename.visibility = View.GONE
        }


        btnDownload.setOnClickListener {
            Toast.makeText(
                context,
                "Lihat progress di notifikasi!",
                Toast.LENGTH_LONG
            ).show()
            val url = "https://data-canter.taekwondosulsel.info/storage/" + item.fileName
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(item.fileName)
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, item.fileName)
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        }

        btnRename.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("fromFragment", "Property")
            bundle.putString("folderName", item.fileName)
            bundle.putInt("folderId", item.id)
            val dialogEditFolderFragment = DialogEditFolderFragment()
            val mFragmentManager = supportFragmentManager
            dialogEditFolderFragment.arguments = bundle
            dialogEditFolderFragment.show(mFragmentManager, "DialogEditFolderFragment")
        }

        btnDelete.setOnClickListener {
            viewModel.deleteFolder("Bearer $token", item.id, 0)
                .observe(this@PropertiesActivity) { responseData ->
                    when (responseData) {
                        is Resource.Success -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteAllFiles()
                            }
                            viewModel.getListFile("Bearer $token")
                                .observe(this@PropertiesActivity) { file ->
                                    when (file) {
                                        is Resource.Success -> {
                                            toastSuccess("Berhasil di hapus!")
                                            onBackPressed()
                                        }

                                        is Resource.Loading -> {
                                        }

                                        is Resource.Error -> {
                                        }
                                    }

                                }


                        }

                        else -> {

                        }
                    }
                }
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

}