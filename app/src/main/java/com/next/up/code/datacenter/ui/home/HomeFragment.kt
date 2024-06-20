package com.next.up.code.datacenter.ui.home

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.toSalam
import com.inyongtisto.myhelper.extension.toastError
import com.next.up.code.core.data.Resource
import com.next.up.code.core.data.local.constants.SortType
import com.next.up.code.core.domain.model.LatestFile
import com.next.up.code.core.domain.model.News
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.adapter.AllNewsAdapter
import com.next.up.code.datacenter.adapter.LatestFileAdapter
import com.next.up.code.datacenter.databinding.FragmentHomeBinding
import com.next.up.code.datacenter.ui.file.FileViewModel
import com.next.up.code.datacenter.ui.setting.SettingViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment(), LatestFileAdapter.OnDeleteButtonClickListener,
    LatestFileAdapter.OnRenameButtonClickListener, LatestFileAdapter.OnDownloadButtonClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: HomeViewModel by viewModel()
    private val fileViewModel: FileViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private val settingViewModel: SettingViewModel by viewModel()
    private var token = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMainButton()

        preferencesSetup()
        token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()
        setupUser()
        setupNews()
        setupAnnouncement(token)


    }

    private fun preferencesSetup() {
        sharedPreferences = requireContext().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun setupAnnouncement(token: String) {
        viewModel.getAnnouncement("Bearer $token").observe(viewLifecycleOwner) { responseData ->
            when (responseData) {
                is Resource.Success -> {
                    progress.dismiss()
                    val response = responseData.data
                    binding?.apply {
                        tvTitle.text = response?.title
                        tvPostName.text = response?.users
                        tvDate.text = response?.date
                        if (response?.file != null) {
                            binding?.btnAttachment?.visibility = View.VISIBLE
                            binding?.btnAttachment?.setOnClickListener {
                                val bundle = Bundle()
                                bundle.putString("fileName", response.title)
                                bundle.putString("from", "home")
                                bundle.putString(
                                    "path",
                                    "https://data-canter.taekwondosulsel.info/storage/" + response.file
                                )
                                findNavController().navigate(
                                    R.id.action_menu_home_to_driveFragment,
                                    bundle
                                )
                            }
                        } else {
                            binding?.btnAttachment?.visibility = View.GONE
                        }
                    }
                }

                is Resource.Loading -> {
                }

                is Resource.Error -> {
                    progress.dismiss()
                }
            }
        }
    }


    private fun setupMainButton() {
        binding?.apply {
            llMenuDocument.setOnClickListener {
                findNavController().navigate(R.id.action_menu_home_to_documentsFragment)
            }
            llMenuImages.setOnClickListener {
                findNavController().navigate(R.id.action_menu_home_to_imagesFragment)
            }
            llMenuAudio.setOnClickListener {
                findNavController().navigate(R.id.action_menu_home_to_audioFragment)
            }
            llMenuVideos.setOnClickListener {
                findNavController().navigate(R.id.action_menu_home_to_videosFragment)
            }
            btnMore.setOnClickListener {
                findNavController().navigate(R.id.action_menu_home_to_allLatestFileFragment)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUser() {
        settingViewModel.getUser().observe(viewLifecycleOwner) { data ->
            binding?.apply {
                val name = data?.personResponsible
                val picture = data?.picture
                val stringWelcome = "Selamat Datang".toSalam()
                binding?.tvName?.text = "$stringWelcome, $name"
                Picasso.get().load(picture).error(R.drawable.ic_profile_pic)
                    .placeholder(R.drawable.ic_profile_pic).into(binding?.ivPicture)
            }
        }
    }

    private fun setupLatestFile() {
        val latestFileAdapter =
            LatestFileAdapter(fileViewModel, viewLifecycleOwner, this, this, this)
        fileViewModel.getAllFolder(SortType.THREE_LATEST, null).observe(viewLifecycleOwner) {
            latestFileAdapter.submitList(it)
        }

        with(binding?.rvFileLatest) {
            this?.adapter = latestFileAdapter
            this?.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            this?.setHasFixedSize(true)
        }

        latestFileAdapter.setOnItemClickCallback((object : LatestFileAdapter.OnItemClickCallback {
            override fun onItemClicked(latestFile: LatestFile) {
                val bundle = Bundle()
                bundle.putString("fileName", latestFile.fileName)
                bundle.putString("from", "home")
                when (latestFile.fileType) {

                    "jpg" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_home_to_viewerImageFragment, bundle
                        )
                    }

                    "png" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_home_to_viewerImageFragment, bundle
                        )
                    }

                    "jpeg" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_home_to_viewerImageFragment, bundle
                        )
                    }

                    else -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_home_to_driveFragment, bundle
                        )
                    }
                }
            }

        }))
    }

    private fun setupNews() {
        val newsAdapter = AllNewsAdapter()
        viewModel.setNews().observe(viewLifecycleOwner) {
            Log.d("TAG", "setupNews: $it")
            newsAdapter.submitList(it)
        }
        with(binding?.rvNews) {
            this?.adapter = newsAdapter
            this?.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            this?.setHasFixedSize(true)
        }

        newsAdapter.setOnItemClickCallback((object : AllNewsAdapter.OnItemClickCallback {
            override fun onItemClicked(news: News) {
                val json = Gson().toJson(news, News::class.java)
                val bundle = Bundle()
                bundle.putString("json", json)
                findNavController().navigate(R.id.action_menu_home_to_detailNewsFragment, bundle)
            }

        }))
    }

    override fun onResume() {
        super.onResume()
        setupLatestFile()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDeleteButtonClicked(item: LatestFile) {
        viewModel.deleteFolder("Bearer $token", item.id, 0)
            .observe(viewLifecycleOwner) { responseData ->
                when (responseData) {
                    is Resource.Success -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteAllFiles()
                        }
                        viewModel.getListFile("Bearer $token").observe(this@HomeFragment) {

                        }
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Error -> {
                    }
                }
            }
    }

    override fun onRenameButtonClicked(item: LatestFile) {

    }

    override fun onDownloadButtonClicked(item: LatestFile) {
        fileViewModel.insertLogDownload("Bearer $token", item.id)
            .observe(viewLifecycleOwner) { responseData ->
                when (responseData) {
                    is Resource.Success -> {
                        Toast.makeText(
                            context,
                            "Lihat progress di notifikasi!",
                            Toast.LENGTH_LONG
                        ).show()
                        val url =
                            "https://data-canter.taekwondosulsel.info/storage/" + item.fileName
                        val request = DownloadManager.Request(Uri.parse(url))
                            .setTitle(item.fileName)
                            .setDescription("Downloading")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                item.fileName
                            )
                        val downloadManager =
                            context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        downloadManager.enqueue(request)
                    }

                    else -> {

                    }
                }
            }
    }

}