package com.next.up.code.core.data.api.network

import com.next.up.code.core.data.api.response.ResponseData
import com.next.up.code.core.data.api.response.item.NewsPagination
import retrofit2.http.GET

interface ApiServiceIntegration {

    @GET("berita")
    suspend fun getNews(): ResponseData<NewsPagination>
}