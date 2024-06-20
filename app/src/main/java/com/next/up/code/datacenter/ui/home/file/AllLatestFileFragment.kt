package com.next.up.code.datacenter.ui.home.file

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
import com.next.up.code.datacenter.adapter.LatestFileAdapter
import com.next.up.code.datacenter.databinding.FragmentAllLatestFileBinding
import com.next.up.code.datacenter.ui.dialog.DialogEditFolderFragment
import com.next.up.code.datacenter.ui.dialog.MediaPlayerDialogFragment
import com.next.up.code.datacenter.ui.file.FileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class AllLatestFileFragment : Fragment(), LatestFileAdapter.OnDeleteButtonClickListener,
    LatestFileAdapter.OnRenameButtonClickListener, LatestFileAdapter.OnDownloadButtonClickListener {

    private var _binding: FragmentAllLatestFileBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: FileViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private var token = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllLatestFileBinding.inflate(layoutInflater)
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

    private fun preferencesSetup() {
        sharedPreferences = requireContext().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
    }


    private fun setupMainButton() {
        binding?.apply {
            toolbar.btnBack.setOnClickListener { requireActivity().onBackPressed() }
            toolbar.title.text = resources.getString(R.string.text_category_latest_file)
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

    private fun setupFile() {
        val allFileAdapter = LatestFileAdapter(viewModel, viewLifecycleOwner, this, this, this)
        viewModel.getAllFolder(SortType.LATEST, null).observe(viewLifecycleOwner) {
            allFileAdapter.submitList(it)
        }
        Utils.setupRecycleView(allFileAdapter, binding?.rvFileLatest, requireActivity())
        setupListener(allFileAdapter)
    }


    private fun setupListener(adapter: LatestFileAdapter) {
        adapter.setOnItemClickCallback((object : LatestFileAdapter.OnItemClickCallback {
            override fun onItemClicked(latestFile: LatestFile) {
                val bundle = Bundle()
                bundle.putString("fileName", latestFile.fileName)
                bundle.putString("from", "home")
                bundle.putString(
                    "path",
                    "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                )
                when (latestFile.fileType) {
                    "jpg" -> {
                        findNavController().navigate(
                            R.id.action_allLatestFileFragment_to_viewerImageFragment, bundle
                        )
                    }

                    "png" -> {
                        findNavController().navigate(
                            R.id.action_allLatestFileFragment_to_viewerImageFragment, bundle
                        )
                    }

                    "jpeg" -> {
                        findNavController().navigate(
                            R.id.action_allLatestFileFragment_to_viewerImageFragment, bundle
                        )
                    }

                    "mp4" -> {
                        findNavController().navigate(
                            R.id.action_allLatestFileFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "avi" -> {
                        findNavController().navigate(
                            R.id.action_allLatestFileFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "mkv" -> {
                        findNavController().navigate(
                            R.id.action_allLatestFileFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "wmv" -> {
                        findNavController().navigate(
                            R.id.action_allLatestFileFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "ogg" -> {
                        findNavController().navigate(
                            R.id.action_allLatestFileFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "3gp" -> {
                        findNavController().navigate(
                            R.id.action_allLatestFileFragment_to_viewerVideoFragment, bundle
                        )
                    }

                    "mp3" -> {
                        val mediaPlayerDialogFragment = MediaPlayerDialogFragment()
                        val mFragmentManager = childFragmentManager
                        mediaPlayerDialogFragment.arguments = bundle
                        mediaPlayerDialogFragment.show(
                            mFragmentManager,
                            "MediaPlayerDialogFragment"
                        )
                    }

                    "wav" -> {
                        val mediaPlayerDialogFragment = MediaPlayerDialogFragment()
                        val mFragmentManager = childFragmentManager
                        mediaPlayerDialogFragment.arguments = bundle
                        mediaPlayerDialogFragment.show(
                            mFragmentManager,
                            "MediaPlayerDialogFragment"
                        )
                    }

                    "opus" -> {
                        val mediaPlayerDialogFragment = MediaPlayerDialogFragment()
                        val mFragmentManager = childFragmentManager
                        mediaPlayerDialogFragment.arguments = bundle
                        mediaPlayerDialogFragment.show(
                            mFragmentManager,
                            "MediaPlayerDialogFragment"
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
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteAllFiles()
                        }
                        viewModel.getListFile("Bearer $token").observe(this@AllLatestFileFragment) {
                        }
                        binding?.swipeRefreshLayout?.isRefreshing = false
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}