package com.next.up.code.datacenter

import android.app.Application
import com.next.up.code.core.di.databaseModule
import com.next.up.code.core.di.repositoryModule
import com.next.up.code.core.di.useCaseModule
import com.next.up.code.datacenter.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(

                    databaseModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModules
                )
            )
        }
    }
}