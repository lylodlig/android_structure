package com.huania.eew.utils.netty

interface NettyClientListener<T> {

    fun onMessageResponseClient(msg: T)

    fun onConnectSuccess()

    fun onClose()
}