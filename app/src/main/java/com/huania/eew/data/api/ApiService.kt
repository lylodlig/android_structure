package com.huania.eew.data.api

import com.huania.eew.data.bean.LoginResult
import com.huania.eew.data.bean.Response
import retrofit2.http.GET

interface ApiService {
    @GET("search/login")
    suspend fun login(): Response<LoginResult>

}