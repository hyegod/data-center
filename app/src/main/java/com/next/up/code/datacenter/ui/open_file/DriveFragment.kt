package com.next.up.code.datacenter.ui.open_file

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.next.up.code.datacenter.databinding.FragmentDriveBinding
import com.next.up.code.datacenter.ui.detail.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class DriveFragment : Fragment() {

    private var _binding: FragmentDriveBinding? = null
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDriveBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fileName = arguments?.getString("fileName").toString()
        val path = arguments?.getString("path").toString()
        setupUI(fileName)
        val driveLink =
            "https://docs.google.com/gview?embedded=true&url=$path"
        setWeb(driveLink)
    }

    private fun setupUI(fileName: String) {
        binding?.actionBar?.title?.text = fileName.replace("folder-file/", "")
        val from = arguments?.getString("from")
        binding?.actionBar?.btnBack?.setOnClickListener {
            if (from == "home") {
                requireActivity().onBackPressed()
            } else {
                requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
            }

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWeb(link: String) {
        binding?.apply {
            webView.settings.javaScriptEnabled = true
            webView.settings.pluginState = WebSettings.PluginState.ON
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false
            webView.webChromeClient = WebChromeClient()
            webView.webViewClient = MyWebViewClient()

            webView.loadUrl(link)
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding?.progressBar?.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding?.progressBar?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        root = null
    }

}