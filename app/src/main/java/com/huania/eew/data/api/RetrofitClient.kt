package com.huania.eew.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit

class RetrofitClient private constructor(hostType: Int) : BaseRetrofitClient() {
    val service by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        getService(ApiService::class.java, hostType)
    }

    companion object {
        @Volatile
        private var instance: RetrofitClient? = null

        @JvmOverloads
        fun getInStance(hostType: Int = 0) = instance ?: synchronized(this) {
            instance ?: RetrofitClient(hostType).also { instance = it }
        }

    }

    //okHttp 扩展
    override fun handleBuilder(builder: OkHttpClient.Builder) {
//        builder.cookieJar(cookieJar)
    }

    //retrofit扩展
    override fun retrofitBuilder(builder: Retrofit.Builder) {
    }
}
