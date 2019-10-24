package org.ccdn.client.handler;

import org.ccdn.client.constant.NAPTConst;
import org.ccdn.client.message.NAPTMessage;
import org.ccdn.client.util.NAPTClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Class Name : NAPTIntraServerHandler
 * @Description: CCDN NAPT Intranet server channel handler
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
 */
public class NAPTIntraServerHandler extends SimpleChannelInboundHandler<ByteBuf>
{
    private static Logger m_log = LoggerFactory.getLogger(NAPTIntraServerHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
    	m_log.error("Caught intra-server exception {} {}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception
    {
        Channel intraChannel = ctx.channel();
        Channel naptChannel = intraChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).get();

        if (null == naptChannel)
        {
            intraChannel.close();
            return;
        }
        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);

        NAPTMessage hmsg = new NAPTMessage();
        hmsg.setType(NAPTMessage.TYPE_TRANSFER);
        hmsg.setUri(NAPTClientManager.getIntraServerUri(intraChannel));
        hmsg.setData(data);
        naptChannel.writeAndFlush(hmsg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        Channel intraChannel = ctx.channel();
        String uri = NAPTClientManager.getIntraServerUri(intraChannel);
        NAPTClientManager.removeIntraServer(uri);

        Channel naptChannel = intraChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).get();
        if (null != naptChannel)
        {
        	NAPTMessage hmsg = new NAPTMessage();
            hmsg.setType(NAPTMessage.TYPE_DISCONNECT);
            hmsg.setUri(uri);
            naptChannel.writeAndFlush(hmsg);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception
    {
        Channel intraChannel = ctx.channel();
        Channel naptChannel = intraChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).get();
        if (null != naptChannel)
        {
        	naptChannel.config().setOption(ChannelOption.AUTO_READ, intraChannel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }
}
