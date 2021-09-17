package com.huania.eew.data.bean

import kotlin.properties.Delegates

class Response<out T> {
    private val code by Delegates.notNull<Int>()
    val msg by Delegates.notNull<String>()
    val data: T? = null

    val success
        get() = code==0
}
