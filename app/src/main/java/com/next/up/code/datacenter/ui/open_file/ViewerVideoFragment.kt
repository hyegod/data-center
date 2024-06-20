package com.next.up.code.datacenter.ui.open_file

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.next.up.code.datacenter.databinding.FragmentViewerVideoBinding
import com.next.up.code.datacenter.ui.detail.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViewerVideoFragment : Fragment() {

    private var _binding: FragmentViewerVideoBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private lateinit var videoView: VideoView
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentViewerVideoBinding.inflate(layoutInflater)
        root = _binding?.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fileName = arguments?.getString("fileName").toString()
        binding?.toolbar?.title?.text = fileName.replace("folder-file/", "")
        val from = arguments?.getString("from")
        binding?.toolbar?.btnBack?.setOnClickListener {

            if (from == "home") {
                requireActivity().onBackPressed()
            } else {
                requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
            }
        }
        videoView = binding!!.videoView
        setupVideo()
    }

    private fun setupVideo() {
        val path = arguments?.getString("path").toString()
        val videoUri = Uri.parse(path)
        videoView.setOnPreparedListener { mp ->
            binding?.progressBar?.visibility = View.GONE
            mp.start()
        }
        videoView.setVideoURI(videoUri)

        videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}