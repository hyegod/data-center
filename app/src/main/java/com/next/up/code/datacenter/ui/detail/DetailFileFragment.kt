package com.next.up.code.datacenter.ui.detail

import android.annotation.SuppressLint
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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.toRequestBody
import com.inyongtisto.myhelper.extension.toastError
import com.next.up.code.core.data.Resource
import com.next.up.code.core.data.local.constants.SortType
import com.next.up.code.core.domain.model.BreadCrumbs
import com.next.up.code.core.domain.model.LatestFile
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.core.utils.ProgressEmittingRequestBody
import com.next.up.code.core.utils.Utils
import com.next.up.code.core.utils.Utils.setupRecycleView
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.adapter.AllFileAdapter
import com.next.up.code.datacenter.adapter.BreadCrumbsAdapter
import com.next.up.code.datacenter.databinding.FragmentDetailFileBinding
import com.next.up.code.datacenter.ui.dialog.DialogAddFragment
import com.next.up.code.datacenter.ui.dialog.DialogEditFolderFragment
import com.next.up.code.datacenter.ui.file.FileViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailFileFragment : BaseFragment(), AllFileAdapter.OnDeleteButtonClickListener,
    AllFileAdapter.OnRenameButtonClickListener, AllFileAdapter.OnDownloadButtonClickListener,
    DialogAddFragment.UploadFileListener {

    private var _binding: FragmentDetailFileBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: FileViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private var token = ""
    private val detailViewModel: DetailViewModel by viewModel()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            detailViewModel.getBreadCrumbs.observe(viewLifecycleOwner) {

                if (it.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        detailViewModel.removeLastBreadCrumbs()
                    }
                    findNavController().popBackStack()
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailFileBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesSetup()


        token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()

        val fileName = arguments?.getString("fileName").toString()

        setupUI(fileName)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        setupBreadCrumbs()
        val parentNameId = arguments?.getInt("parentNameId", 0)
        parentNameId?.let { setupMainButton(it) }
        parentNameId?.let { setupFile(it) }


    }

    private fun preferencesSetup() {
        sharedPreferences = requireContext().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE
        )
    }


    private fun setupUI(fileName: String) {

        binding?.toolbar?.title?.text = fileName.replace("folder-file/", "")
        binding?.toolbar?.btnBack?.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                detailViewModel.removeLastBreadCrumbs()
            }
            findNavController().popBackStack()
        }
    }

    private fun setupMainButton(parentNameId: Int) {
        binding?.btnAdd?.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("parentNameId", parentNameId)
            bundle.putString("fromFragment", "Detail")
            val dialogAddFragment = DialogAddFragment()
            dialogAddFragment.arguments = bundle
            val mFragmentManager = childFragmentManager
            dialogAddFragment.show(mFragmentManager, "DialogAddFragment")
        }
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteAllFiles()
            }
            viewModel.getListFile("Bearer $token").observe(viewLifecycleOwner) {}
            binding?.swipeRefreshLayout?.isRefreshing = false

        }

    }


    private fun setupBreadCrumbs() {
        val breadCrumbsAdapter = BreadCrumbsAdapter()
        detailViewModel.getBreadCrumbs.observe(viewLifecycleOwner) {
            breadCrumbsAdapter.submitList(it)
            with(binding?.rvBreadCrumbs) {
                this?.adapter = breadCrumbsAdapter
                this?.setHasFixedSize(true)
                this?.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }

        breadCrumbsAdapter.setOnItemClickCallback((object : BreadCrumbsAdapter.OnItemClickCallback {
            override fun onItemClicked(breadCrumb: BreadCrumbs) {
                detailViewModel.getBreadCrumbs.observe(viewLifecycleOwner) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        detailViewModel.removeBreadCrumbs(breadCrumb)
                    }
                    findNavController().popBackStack()
                }
            }

        }))
    }

    private fun setupFile(parentName: Int) {
        val allFileAdapter = AllFileAdapter(viewModel, viewLifecycleOwner, this, this, this)
        viewModel.getAllFolder(SortType.DETAIL_FOLDER, parentName).observe(viewLifecycleOwner) {
            allFileAdapter.submitList(it)
            if (it.isEmpty()) {
                binding?.rvFileLatest?.visibility = View.GONE
                binding?.tvAlertFolder?.visibility = View.VISIBLE
                binding?.ivNotFound?.visibility = View.VISIBLE
            } else {
                binding?.swipeRefreshLayout?.isRefreshing = false
                binding?.rvFileLatest?.visibility = View.VISIBLE
                binding?.tvAlertFolder?.visibility = View.GONE
                binding?.ivNotFound?.visibility = View.GONE
            }
        }


        setupRecycleView(allFileAdapter, binding?.rvFileLatest, requireContext())

        allFileAdapter.setOnItemClickCallback((object : AllFileAdapter.OnItemClickCallback {
            override fun onItemClicked(latestFile: LatestFile) {
                val bundle = Bundle()
                bundle.putString("fileName", latestFile.fileName)
                fragment.add(findNavController().currentDestination!!.id)
                when (latestFile.fileType) {
                    "folder" -> {
                        detailViewModel.getBreadCrumbs.observe(viewLifecycleOwner) {
                            detailViewModel.insertBreadCrumbs(
                                BreadCrumbs(
                                    it.last().id?.plus(1),
                                    latestFile.fileName,
                                    latestFile.parentNameId?.toInt()
                                )
                            )
                        }
                        bundle.putInt("parentNameId", latestFile.id)
                        findNavController().navigate(R.id.action_detailFileFragment_self, bundle)
                    }

                    "jpg" -> {
                        bundle.putString("from", "home")
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_detailFileFragment_to_viewerImageFragment, bundle
                        )
                    }

                    "png" -> {

                        bundle.putString("from", "home")
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_detailFileFragment_to_viewerImageFragment, bundle
                        )
                    }

                    "jpeg" -> {

                        bundle.putString("from", "home")
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_detailFileFragment_to_viewerImageFragment, bundle
                        )
                    }

                    else -> {
                        bundle.putString("from", "home")
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_detailFileFragment_to_driveFragment, bundle
                        )
                    }
                }

            }

        }))
    }


    override fun onResume() {
        super.onResume()
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteAllFiles()
            }
            viewModel.getListFile("Bearer $token").observe(viewLifecycleOwner) {}
            binding?.swipeRefreshLayout?.isRefreshing = false

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        root = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
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
                        viewModel.getListFile("Bearer $token").observe(this@DetailFileFragment) {}
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
                            context, "Lihat progress di notifikasi!", Toast.LENGTH_LONG
                        ).show()
                        val url =
                            "https://data-canter.taekwondosulsel.info/storage/" + item.fileName
                        val request =
                            DownloadManager.Request(Uri.parse(url)).setTitle(item.fileName)
                                .setDescription("Downloading")
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setDestinationInExternalPublicDir(
                                    Environment.DIRECTORY_DOWNLOADS, item.fileName
                                )
                        val downloadManager =
                            context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        downloadManager.enqueue(request)
                    }

                    else -> {
                        toastError(responseData.message.toString())
                    }
                }
            }
    }

    companion object {
        var breadCrumbs = ArrayList<BreadCrumbs>()
        var fragment = ArrayList<Int>()
    }

    @SuppressLint("CheckResult")
    override fun uploadFile(fileName: String, file: File?, parentNameId: Int?) {
        if (file != null) {
            val filePart = ProgressEmittingRequestBody(file)
            val vFile = MultipartBody.Part.createFormData("folder_name", fileName, filePart)
            when (Utils.getFileExtension(fileName)) {
                "pdf" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(com.next.up.code.core.R.drawable.ic_pdf)
                }

                "doc" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(com.next.up.code.core.R.drawable.ic_word)
                }

                "docx" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(com.next.up.code.core.R.drawable.ic_word)
                }

                "xls" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(com.next.up.code.core.R.drawable.ic_excel)
                }

                "xlsx" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(com.next.up.code.core.R.drawable.ic_excel)
                }

                "pptx" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(com.next.up.code.core.R.drawable.ic_power_point)
                }

                "mp4" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(R.drawable.ic_play)
                }

                "avi" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(R.drawable.ic_play)
                }

                "mkv" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(R.drawable.ic_play)
                }

                "wmv" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(R.drawable.ic_play)
                }

                "ogg" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(R.drawable.ic_play)
                }

                "3gp" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(R.drawable.ic_play)
                }

                "mp3" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(com.next.up.code.datacenter.R.drawable.ic_audio)
                }

                "opus" -> {
                    binding?.componentProgressUpload?.ivTypeFile?.setImageResource(com.next.up.code.datacenter.R.drawable.ic_audio)
                }

            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.uploadFile("Bearer $token", vFile, parentNameId?.toRequestBody())
                    .observe(viewLifecycleOwner) { responseData ->
                        when (responseData) {
                            is Resource.Success -> {
                                binding?.componentProgressUpload?.progress?.visibility = View.GONE
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteAllFiles()
                                }
                                viewModel.getListFile("Bearer $token")
                                    .observe(viewLifecycleOwner) {}
                                binding?.swipeRefreshLayout?.isRefreshing = false
                            }

                            is Resource.Loading -> {
                                binding?.swipeRefreshLayout?.isRefreshing = true
                                binding?.componentProgressUpload?.tvFilename?.text = fileName
                                binding?.componentProgressUpload?.progress?.visibility =
                                    View.VISIBLE
                                filePart.getProgressSubject()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe { progress ->
                                        binding?.componentProgressUpload?.progressBar?.progress =
                                            progress
                                    }
                            }

                            is Resource.Error -> {
                                toastError("File yang di upload corrupted!")
                            }
                        }
                    }
            }


        }

    }


}