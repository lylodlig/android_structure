package com.huania.eew.utils.netty

import android.annotation.SuppressLint
import android.os.SystemClock
import com.blankj.utilcode.util.NetworkUtils
import com.huania.eew.utils.log.logD
import com.huania.eew.utils.log.logE
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.IdleStateHandler
import io.netty.util.concurrent.GenericFutureListener
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class NettyTcpClient(
    private var host: String,
    private var port: Int,
    private val tag: String,
    private val heart: ByteBuf,
    private val RECONNECT_INTERVAL: Int = 10_000,
    private val HartBeatInterval: Long = 30L,
    private val decoder: ()->ChannelHandler
) {

    private var bootstrap: Bootstrap? = null
    private var reconnectNum: Int = 0
    private var group: EventLoopGroup = NioEventLoopGroup()
    private lateinit var listener: NettyClientListener<ProtoData>
    private var channel: Channel? = null

    fun setUrl(url: String?) {
        if (url.isNullOrBlank()) {
            return
        }
        host = url.split(":")[0]
        port = url.split(":")[1].toInt()
    }

    fun connect() {
        if (bootstrap == null) {
            bootstrap = Bootstrap().group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, RECONNECT_INTERVAL.toInt())
                .channel(NioSocketChannel::class.java as Class<out Channel>?)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    @Throws(Exception::class)
                    public override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(decoder.invoke())
                        ch.pipeline()
                            .addLast(IdleStateHandler(0, HartBeatInterval, 0, TimeUnit.SECONDS))
                        ch.pipeline().addLast(NettyClientHandler(listener, tag, heart))
                    }
                })
        }
        connectServer()
    }

    @SuppressLint("MissingPermission")
    private fun connectServer() {
        synchronized(this@NettyTcpClient) {
            var channelFuture = bootstrap?.connect(host, port)
            channelFuture?.addListener {
                if (it.isSuccess) {
                    logD("${tag}:连接成功")
                    reconnectNum = 0
                    channel = channelFuture?.channel()
                    listener?.onConnectSuccess()
                } else {
                    logE("$tag  失败:  网络：${NetworkUtils.isAvailable()}  ${it.cause().cause}   ${it.cause().message}")
                }
            }
        }
    }

    fun sendMsg(msg: ByteBuf) {
        logD("${tag}:发送消息:${ByteBufUtil.hexDump(msg)}")
        channel?.writeAndFlush(msg)
    }

    fun sendMsg(msg: ByteBuf, listener: GenericFutureListener<ChannelFuture>) {
        logD("${tag}:发送消息:${ByteBufUtil.hexDump(msg)}")
        channel?.writeAndFlush(msg)?.addListener(listener)
    }

    fun isConnected(): Boolean {
        return channel != null && channel!!.isActive
    }

    fun disconnect() {
        logD("${tag}:disconnect")
        if (channel != null) {
            channel!!.close()
        }
    }

    fun reconnect() {
        logD("${tag}:reconnect:$reconnectNum")
        var time = 2.0.pow(reconnectNum.toDouble())
        if (time > 32) {
            time = 32.0

        }
        SystemClock.sleep((time * 1000).toLong())
        reconnectNum++
        logE("${tag}:重新连接")
        connectServer()
    }

    fun setListener(listener: NettyClientListener<ProtoData>) {
        this.listener = listener
    }

}