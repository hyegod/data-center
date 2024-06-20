package com.next.up.code.datacenter.ui.home.news.detail

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.next.up.code.core.domain.model.News
import com.next.up.code.datacenter.databinding.FragmentDetailNewsBinding
import com.squareup.picasso.Picasso


class DetailNewsFragment : Fragment() {

    private var _binding: FragmentDetailNewsBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailNewsBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val json = arguments?.getString("json")
        val newsItem = Gson().fromJson(json, News::class.java)
        setupDetail(newsItem)
        binding?.btnBack?.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun setupDetail(news: News) {
        binding?.apply {
            tvTitle.text = news.title
            tvCategories.text = "Info BMBK"
            tvDescription.text = Html.fromHtml(news.description)
            Picasso.get()
                .load("https://putr.sulselprov.go.id/uploads/berita/" + news.thumbnail)
                .into(ivCover)
        }
    }

}