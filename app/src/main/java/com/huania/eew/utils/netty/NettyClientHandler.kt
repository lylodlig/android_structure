package com.huania.eew.utils.netty

import com.huania.eew.utils.log.logD
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

class NettyClientHandler(
    private val listener: NettyClientListener<ProtoData>,
    private val tag: String,
    private val heart: ByteBuf
) : SimpleChannelInboundHandler<ProtoData>() {

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent && evt.state() == IdleState.WRITER_IDLE) {
            logD("${tag}:发送心跳:${ByteBufUtil.hexDump(heart)}")
            ctx.channel().writeAndFlush(heart).addListener {
                if (!it.isSuccess) {
                    logD("发送心跳失败")
                    ctx.channel().close()
                }
            }
        }
    }

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, msg: ProtoData) {
        val data = if (msg.data == null) "" else (msg.data as ByteArray).contentToString()
        logD("channelRead type:${msg.type}    data:$data")
        listener.onMessageResponseClient(msg)
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext?) {
        super.channelUnregistered(ctx)
        listener.onClose()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

}
