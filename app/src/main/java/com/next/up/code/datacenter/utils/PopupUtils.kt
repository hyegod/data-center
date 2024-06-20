package com.next.up.code.datacenter.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.next.up.code.core.data.local.constants.SortType
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.adapter.SortingAdapter

object PopupUtils {

    private var popupWindow: PopupWindow? = null

    @SuppressLint("ClickableViewAccessibility")
    fun showPopupCategory(
        view: View,
        callback: CategorySelectionCallback
    ) {
        popupWindow?.dismiss()
        val inflater = LayoutInflater.from(view.context)
        val popupView = inflater.inflate(R.layout.popup_custom_list_sorting, null)

        popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )

        val backgroundDrawable = ColorDrawable(Color.TRANSPARENT)
        popupWindow?.setBackgroundDrawable(backgroundDrawable)

        popupWindow?.isFocusable = true
        popupWindow?.isOutsideTouchable = true

        val listCategory = popupView.findViewById<RecyclerView>(R.id.rv_category_in_popup)
        val sortingAdapter = SortingAdapter()

        sortingAdapter.setList(sorting)
        listCategory.adapter = sortingAdapter
        listCategory.setHasFixedSize(true)
        listCategory.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        sortingAdapter.setOnItemClickCallback((object :
            SortingAdapter.OnItemClickCallback {
            override fun onItemClicked(sorting: DummySorting) {
                when (sorting.id) {
                    1 -> {
                        callback.onCategorySelected(SortType.ASCENDING_FOLDER, sorting.sortingName)
                    }

                    2 -> {
                        callback.onCategorySelected(SortType.DESCENDING_FOLDER, sorting.sortingName)
                    }


                }
                popupWindow?.dismiss()

            }

        }))


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

    interface CategorySelectionCallback {
        fun onCategorySelected(sortType: SortType, sortName: String)
    }
}