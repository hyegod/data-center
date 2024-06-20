package com.next.up.code.datacenter.ui.home.video

import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.next.up.code.core.data.Resource
import com.next.up.code.core.data.local.constants.SortType
import com.next.up.code.core.domain.model.LatestFile
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.core.utils.Utils
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.adapter.AllFileAdapter
import com.next.up.code.datacenter.databinding.FragmentVideosBinding
import com.next.up.code.datacenter.ui.dialog.DialogEditFolderFragment
import com.next.up.code.datacenter.ui.file.FileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class VideosFragment : Fragment(), AllFileAdapter.OnDeleteButtonClickListener,
    AllFileAdapter.OnRenameButtonClickListener, AllFileAdapter.OnDownloadButtonClickListener {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: FileViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private var token = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVideosBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMainButton()
        preferencesSetup()
        token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()

        setupFile()
    }

    private fun setupMainButton() {
        binding?.apply {
            toolbar.btnBack.setOnClickListener { requireActivity().onBackPressed() }
            toolbar.title.text = resources.getString(R.string.text_category_video)
        }
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteAllFiles()
            }
            viewModel.getListFile("Bearer $token").observe(viewLifecycleOwner) {

            }
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun preferencesSetup() {
        sharedPreferences = requireContext().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun setupFile() {
        val allFileAdapter = AllFileAdapter(viewModel, viewLifecycleOwner, this, this, this)
        viewModel.getAllFolder(SortType.VIDEO, null).observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding?.ivNotFound?.visibility = View.VISIBLE
                binding?.tvAlertFolder?.visibility = View.VISIBLE
                binding?.rvDocuments?.visibility = View.GONE
            } else {
                binding?.ivNotFound?.visibility = View.GONE
                binding?.tvAlertFolder?.visibility = View.GONE
                binding?.rvDocuments?.visibility = View.VISIBLE
                allFileAdapter.submitList(it)
            }
        }
        Utils.setupRecycleView(allFileAdapter, binding?.rvDocuments, requireActivity())
        setupListener(allFileAdapter)
    }


    private fun setupListener(adapter: AllFileAdapter) {
        adapter.setOnItemClickCallback((object : AllFileAdapter.OnItemClickCallback {
            override fun onItemClicked(latestFile: LatestFile) {
                val bundle = Bundle()
                bundle.putString("fileName", latestFile.fileName)
                bundle.putString("from", "home")
                bundle.putString("path", "https://data-canter.taekwondosulsel.info/storage/" +latestFile.fileName)
                when (latestFile.fileType) {
                    "mp4" -> {
                        findNavController().navigate(
                            R.id.action_videosFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "avi" -> {
                        findNavController().navigate(
                            R.id.action_videosFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "mkv" -> {
                        findNavController().navigate(
                            R.id.action_videosFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "wmv" -> {
                        findNavController().navigate(
                            R.id.action_videosFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "ogg" -> {
                        findNavController().navigate(
                            R.id.action_videosFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "3gp" -> {
                        findNavController().navigate(
                            R.id.action_videosFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    else -> {
                        findNavController().navigate(
                            R.id.action_menu_folder_to_driveFragment, bundle
                        )
                    }
                }

            }

        }))

    }

    override fun onDeleteButtonClicked(item: LatestFile) {
        viewModel.deleteFolder("Bearer $token", item.id, 0)
            .observe(viewLifecycleOwner) { responseData ->
                when (responseData) {
                    is Resource.Success -> {
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteAllFiles()
                        }
                        viewModel.getListFile("Bearer $token").observe(this@VideosFragment) {
                        }
                    }

                    is Resource.Loading -> {
                        binding?.swipeRefreshLayout?.isRefreshing = true
                    }

                    is Resource.Error -> {
                        binding?.swipeRefreshLayout?.isRefreshing = false
                    }
                }
            }
    }

    override fun onRenameButtonClicked(item: LatestFile) {
        val bundle = Bundle()
        bundle.putString("fromFragment", "File")
        bundle.putString("folderName", item.fileName)
        bundle.putInt("folderId", item.id)
        val dialogEditFolderFragment = DialogEditFolderFragment()
        val mFragmentManager = childFragmentManager
        dialogEditFolderFragment.arguments = bundle
        dialogEditFolderFragment.show(mFragmentManager, "DialogEditFolderFragment")
    }

    override fun onDownloadButtonClicked(item: LatestFile) {
        viewModel.insertLogDownload("Bearer $token", item.id)
            .observe(viewLifecycleOwner) { responseData ->
                when (responseData) {
                    is Resource.Success -> {
                        Toast.makeText(
                            context,
                            "Lihat progress di notifikasi!",
                            Toast.LENGTH_LONG
                        ).show()
                        val url = item.fileUrl
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        root = null
    }

}