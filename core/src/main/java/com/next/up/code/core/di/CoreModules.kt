package com.next.up.code.core.di


import androidx.room.Room
import com.next.up.code.core.data.CoreRepository
import com.next.up.code.core.data.api.ApiDataSource
import com.next.up.code.core.data.api.network.NetworkClient.Companion.getApiServiceForBaseUrl1
import com.next.up.code.core.data.api.network.NetworkClient.Companion.getApiServiceForBaseUrl2
import com.next.up.code.core.data.local.LocalDataSource
import com.next.up.code.core.data.local.room.DataCenterDatabase
import com.next.up.code.core.domain.repository.ICoreRepository
import com.next.up.code.core.domain.usecase.CoreInteract
import com.next.up.code.core.domain.usecase.CoreUseCase
import com.next.up.code.core.utils.AppExecutors
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { LocalDataSource(get(), get(), get(), get()) }
    single { getApiServiceForBaseUrl1() }
    single { getApiServiceForBaseUrl2() }
    single { ApiDataSource(get(), get()) }
    factory { AppExecutors() }
    single<ICoreRepository> { CoreRepository(get(), get(), get()) }
}

val useCaseModule = module {
    factory<CoreUseCase> { CoreInteract(get()) }
}
val databaseModule = module {
    factory { get<DataCenterDatabase>().fileDao() }
    factory { get<DataCenterDatabase>().userDao() }
    factory { get<DataCenterDatabase>().newsDao() }
    factory { get<DataCenterDatabase>().breadCrumbs() }
    single {

        Room.databaseBuilder(
            androidContext(),
            DataCenterDatabase::class.java, "DataCenter.db"
        ).fallbackToDestructiveMigration().build()
    }
}