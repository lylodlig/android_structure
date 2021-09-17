package com.huania.eew.data

import com.huania.eew.data.bean.Response
import com.huania.eew.data.bean.ResultData

suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): ResultData<T> {
    return try {
        ResultData.Success(call().data)
    } catch (e: Exception) {
        ResultData.Error(e)
    }
}
