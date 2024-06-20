package com.next.up.code.datacenter.ui.open_file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.next.up.code.datacenter.databinding.FragmentViewerImageBinding
import com.next.up.code.datacenter.ui.detail.DetailViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViewerImageFragment : Fragment() {

    private var _binding: FragmentViewerImageBinding? = null
    private val binding get() = _binding
    private var root: View? = null
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
        _binding = FragmentViewerImageBinding.inflate(layoutInflater)
        root = binding?.root
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fileName = arguments?.getString("fileName").toString()
        val path = arguments?.getString("path").toString()
        binding?.toolbar?.title?.text = fileName.replace("folder-file/", "")
        val from = arguments?.getString("from")
        binding?.toolbar?.btnBack?.setOnClickListener {

            if (from == "home") {

                requireActivity().onBackPressed()
            } else {
                requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
            }
        }
        Picasso.get().load(path)
            .into(binding?.ivImage)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        root = null
    }

}