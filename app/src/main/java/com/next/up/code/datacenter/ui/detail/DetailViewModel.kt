package com.next.up.code.datacenter.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.next.up.code.core.domain.model.BreadCrumbs
import com.next.up.code.core.domain.usecase.CoreUseCase

class DetailViewModel(private val coreUseCase: CoreUseCase) : ViewModel() {
    val getBreadCrumbs = coreUseCase.getAllBreadCrumbs().asLiveData()
    fun insertBreadCrumbs(breadCrumbs: BreadCrumbs) = coreUseCase.insertBreadCrumbs(breadCrumbs)
    fun removeLastBreadCrumbs() = coreUseCase.deleteLastRecord()

    fun removeBreadCrumbs(breadCrumbs: BreadCrumbs) = coreUseCase.deleteBreadCrumbs(breadCrumbs)
}