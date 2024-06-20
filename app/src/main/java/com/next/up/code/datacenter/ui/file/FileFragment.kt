package com.next.up.code.datacenter.ui.file

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.toRequestBody
import com.inyongtisto.myhelper.extension.toastError
import com.next.up.code.core.data.Resource
import com.next.up.code.core.data.local.constants.SortType
import com.next.up.code.core.domain.model.BreadCrumbs
import com.next.up.code.core.domain.model.LatestFile
import com.next.up.code.core.domain.model.Roles
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.core.utils.ProgressEmittingRequestBody
import com.next.up.code.core.utils.Utils
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.adapter.AllFileAdapter
import com.next.up.code.datacenter.adapter.FolderAdapter
import com.next.up.code.datacenter.databinding.FragmentFileBinding
import com.next.up.code.datacenter.ui.detail.DetailViewModel
import com.next.up.code.datacenter.ui.dialog.DialogAddFragment
import com.next.up.code.datacenter.ui.dialog.DialogEditFolderFragment
import com.next.up.code.datacenter.ui.dialog.MediaPlayerDialogFragment
import com.next.up.code.datacenter.utils.PopupUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class FileFragment : BaseFragment(), AllFileAdapter.OnDeleteButtonClickListener,
    AllFileAdapter.OnRenameButtonClickListener, AllFileAdapter.OnDownloadButtonClickListener,
    DialogAddFragment.UploadFileListener {
    private var _binding: FragmentFileBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: FileViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private var token = ""
    private val detailViewModel: DetailViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFileBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMainButton()
        preferencesSetup()
        token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()


        setupPermissionFile()
        setupSorting()
        setupRoles()
        setupFile(SortType.ASCENDING_FOLDER, null)
        setupSearch()
    }

    private fun preferencesSetup() {
        sharedPreferences = requireContext().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun setupPermissionFile() {
        viewModel.getUser.observe(viewLifecycleOwner) {
            if (it.permissionUpload != "1") {
                binding?.vAdd?.visibility = View.GONE
            } else {
                binding?.vAdd?.visibility = View.VISIBLE
            }
        }
    }

    private fun setupMainButton() {
        binding?.btnAdd?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("fromFragment", "File")
            val dialogAddFragment = DialogAddFragment()
            val mFragmentManager = childFragmentManager
            dialogAddFragment.arguments = bundle
            dialogAddFragment.show(mFragmentManager, "DialogAddFragment")
        }
    }


    private fun setupRoles() {
        val folderAdapter = FolderAdapter()
        viewModel.getRoles("Bearer $token").observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val data = it.data
                    folderAdapter.setList(data!!)
                }

                is Resource.Loading -> {

                }

                is Resource.Error -> {

                }
            }
        }

        with(binding?.rvRoles) {
            this?.adapter = folderAdapter
            this?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this?.setHasFixedSize(true)
        }

        folderAdapter.setOnItemClickCallback((object : FolderAdapter.OnItemClickCallback {
            override fun onItemClicked(roles: Roles) {
                if (roles.id == 1) {
                    setupFile(SortType.ASCENDING_FOLDER, null)
                } else {
                    setupFile(SortType.ROLES, null, roles.id)
                }
            }

        }))

    }

    

    private fun setupSorting() {
        binding?.apply {
            sorting.text = resources.getString(R.string.text_ascending)
            sorting.setOnClickListener {
                PopupUtils.showPopupCategory(it, object : PopupUtils.CategorySelectionCallback {
                    override fun onCategorySelected(sortType: SortType, sortName: String) {
                        setupFile(sortType, null)
                        sorting.text = sortName
                    }

                })
            }
        }
    }


    private fun setupFile(sortType: SortType, parentNameId: Int?, roleId: Int? = null) {
        val allFileAdapter = AllFileAdapter(viewModel, viewLifecycleOwner, this, this, this)
        viewModel.getAllFolder(sortType, parentNameId, roleId).observe(viewLifecycleOwner) {
            allFileAdapter.submitList(it)
        }
        setupRecycleView(allFileAdapter, binding?.rvFileLatest, requireContext())
        setupListener(allFileAdapter)
    }


    private fun setupSearch() {
        binding?.searchBar?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                searchFile(query)
                return true
            }
        })
    }

    private fun searchFile(keyword: String) {
        if (keyword.isNullOrEmpty()) {
            return setupFile(SortType.ASCENDING_FOLDER, null)
        }
        val fileAdapter = AllFileAdapter(viewModel, viewLifecycleOwner, this, this, this)
        viewModel.searchFilesByName(keyword).observe(viewLifecycleOwner) {
            fileAdapter.submitList(it)
        }
        setupRecycleView(fileAdapter, binding?.rvFileLatest, requireContext())
        setupListener(fileAdapter)
    }


    private fun setupRecycleView(
        adapter: RecyclerView.Adapter<*>, recycleView: RecyclerView?, context: Context
    ) {
        with(recycleView) {
            this?.adapter = adapter
            this?.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            this?.setHasFixedSize(true)
        }
    }

    private fun setupListener(adapter: AllFileAdapter) {
        adapter.setOnItemClickCallback((object : AllFileAdapter.OnItemClickCallback {
            override fun onItemClicked(latestFile: LatestFile) {
                val bundle = Bundle()
                bundle.putString("fileName", latestFile.fileName)

                when (latestFile.fileType) {
                    "folder" -> {
                        detailViewModel.insertBreadCrumbs(
                            BreadCrumbs(
                                1, latestFile.fileName, latestFile.parentNameId?.toInt()
                            )
                        )
                        bundle.putInt("parentNameId", latestFile.id)
                        findNavController().navigate(
                            R.id.action_menu_folder_to_detailFileFragment, bundle
                        )
                    }

                    "jpg" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_viewerImageFragment, bundle
                        )
                    }

                    "png" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_viewerImageFragment, bundle
                        )
                    }

                    "jpeg" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_viewerImageFragment, bundle
                        )
                    }

                    "mp4" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_viewerVideoFragment, bundle
                        )
                    }

                    "avi" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_viewerVideoFragment, bundle
                        )
                    }

                    "mkv" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_viewerVideoFragment, bundle
                        )
                    }

                    "wmv" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_viewerVideoFragment, bundle
                        )
                    }

                    "ogg" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_viewerVideoFragment, bundle
                        )
                    }

                    "3gp" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_viewerVideoFragment, bundle
                        )
                    }

                    "mp3" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        val mediaPlayerDialogFragment = MediaPlayerDialogFragment()
                        val mFragmentManager = childFragmentManager
                        mediaPlayerDialogFragment.arguments = bundle
                        mediaPlayerDialogFragment.show(
                            mFragmentManager, "MediaPlayerDialogFragment"
                        )
                    }

                    "wav" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        val mediaPlayerDialogFragment = MediaPlayerDialogFragment()
                        val mFragmentManager = childFragmentManager
                        mediaPlayerDialogFragment.arguments = bundle
                        mediaPlayerDialogFragment.show(
                            mFragmentManager, "MediaPlayerDialogFragment"
                        )
                    }

                    "opus" -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        val mediaPlayerDialogFragment = MediaPlayerDialogFragment()
                        val mFragmentManager = childFragmentManager
                        mediaPlayerDialogFragment.arguments = bundle
                        mediaPlayerDialogFragment.show(
                            mFragmentManager, "MediaPlayerDialogFragment"
                        )
                    }

                    else -> {
                        bundle.putString(
                            "path",
                            "https://data-canter.taekwondosulsel.info/storage/" + latestFile.fileName
                        )
                        findNavController().navigate(
                            R.id.action_menu_folder_to_driveFragment, bundle
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
            viewModel.getListFile("Bearer $token").observe(viewLifecycleOwner) {

            }
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        root = null
    }

    override fun onDeleteButtonClicked(item: LatestFile) {
        viewModel.deleteFolder("Bearer $token", item.id, 0)
            .observe(viewLifecycleOwner) { responseData ->
                when (responseData) {
                    is Resource.Success -> {

                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteAllFiles()
                        }
                        viewModel.getListFile("Bearer $token").observe(this@FileFragment) { file ->
                            when (file) {
                                is Resource.Success -> {
                                    binding?.swipeRefreshLayout?.isRefreshing = false
                                }

                                is Resource.Loading -> {
                                    binding?.swipeRefreshLayout?.isRefreshing
                                }

                                is Resource.Error -> {
                                    binding?.swipeRefreshLayout?.isRefreshing = false
                                }
                            }

                        }


                    }

                    else -> {

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

                    }
                }
            }
    }

    @SuppressLint("CheckResult")
    override fun uploadFile(fileName: String, file: File?, parentNameId: Int?) {

        if (file != null) {
            val filePart = ProgressEmittingRequestBody(file)
            val vFile = MultipartBody.Part.createFormData("folder_name", fileName, filePart)
            val typeFile = Utils.getFileExtension(fileName)
            when (typeFile) {
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

                "png" -> {
                    binding?.componentProgressUpload?.tvTypeName?.visibility = View.VISIBLE
                    binding?.componentProgressUpload?.tvTypeName?.text = typeFile
                    val drawable =
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.next.up.code.core.R.drawable.background_oval_file
                        )
                    val colorInt =
                        resources.getColor(R.color.colorImage)
                    (drawable as? GradientDrawable)?.setColor(
                        colorInt
                    )
                    binding?.componentProgressUpload?.ivTypeFile?.setImageDrawable(drawable)
                }

                "jpeg" -> {
                    binding?.componentProgressUpload?.tvTypeName?.visibility = View.VISIBLE
                    binding?.componentProgressUpload?.tvTypeName?.text = typeFile
                    val drawable =
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.next.up.code.core.R.drawable.background_oval_file
                        )
                    val colorInt =
                        resources.getColor(R.color.colorImage)
                    (drawable as? GradientDrawable)?.setColor(
                        colorInt
                    )
                    binding?.componentProgressUpload?.ivTypeFile?.setImageDrawable(drawable)
                }

                "jpg" -> {
                    binding?.componentProgressUpload?.tvTypeName?.visibility = View.VISIBLE
                    binding?.componentProgressUpload?.tvTypeName?.text = typeFile
                    val drawable =
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.next.up.code.core.R.drawable.background_oval_file
                        )
                    val colorInt =
                        resources.getColor(R.color.colorImage)
                    (drawable as? GradientDrawable)?.setColor(
                        colorInt
                    )
                    binding?.componentProgressUpload?.ivTypeFile?.setImageDrawable(drawable)
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
                                binding?.componentProgressUpload?.tvFilename?.text = fileName
                                binding?.componentProgressUpload?.progress?.visibility =
                                    View.VISIBLE

                                binding?.swipeRefreshLayout?.isRefreshing = true
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