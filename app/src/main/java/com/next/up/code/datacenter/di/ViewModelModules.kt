package com.next.up.code.datacenter.di


import com.next.up.code.datacenter.MainViewModel
import com.next.up.code.datacenter.ui.detail.DetailViewModel
import com.next.up.code.datacenter.ui.file.FileViewModel
import com.next.up.code.datacenter.ui.home.HomeViewModel
import com.next.up.code.datacenter.ui.login.LoginViewModel
import com.next.up.code.datacenter.ui.setting.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { LoginViewModel(get()) }
    viewModel { FileViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { SettingViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { DetailViewModel(get()) }
}