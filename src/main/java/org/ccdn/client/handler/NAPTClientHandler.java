package org.ccdn.client.handler;

import org.apache.commons.lang.StringUtils;
import org.ccdn.client.NAPTClientContainer;
import org.ccdn.client.constant.NAPTConst;
import org.ccdn.client.listener.NAPTIntraServerListener;
import org.ccdn.client.message.NAPTMessage;
import org.ccdn.client.util.NAPTClientManager;
import org.ccdn.client.util.NAPTConfig;
import org.ccdn.client.util.NAPTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;

/** 
* @Class Name : NAPTClientHandler 
* @Description: CCDN NAPT client channel handler 
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTClientHandler extends SimpleChannelInboundHandler<NAPTMessage>
{
    private static Logger m_log = LoggerFactory.getLogger(NAPTClientHandler.class);

    private NAPTConfig m_config = NAPTConfig.getConfig();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
    	m_log.error("Caught CCDN NAPT client exception {} {}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    /**
    * 
    * @Title      : handleConnectMsg 
    * @Description: Handle connected message 
    * @Param      : @param clientChannel
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleConnectMsg(final Channel clientChannel, final NAPTMessage msg)
    {
        String srvData = new String(msg.getData());
        String host = StringUtils.substringBefore(srvData, ":");
        String port = StringUtils.substringAfter(srvData, ":");

        if (StringUtils.isBlank(host))
        {
        	m_log.error("Invalid server host {}", host);
            return;
        }

        if (StringUtils.isBlank(port) || !StringUtils.isNumeric(port))
        {
        	m_log.error("Invalid server port {}", port);
            return;
        }

        int portNum = Integer.parseInt(port);
        Bootstrap intraServer = NAPTClientContainer.getContainer().getIntraServer();
        NAPTIntraServerListener listener = new NAPTIntraServerListener(clientChannel, msg);
        intraServer.connect(host, portNum).addListener(listener);
    }

    /**
    * 
    * @Title      : handleDisconnectMsg 
    * @Description: Handle disconnected message 
    * @Param      : @param clientChannel
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleDisconnectMsg(final Channel clientChannel, final NAPTMessage msg)
    {
        Channel intraChannel = clientChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).get();
        if (null == intraChannel)
        {
            return;
        }

        clientChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).set(null);
        NAPTClientManager.pushClient(clientChannel);
        intraChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
    * 
    * @Title      : handleTransferMsg 
    * @Description: Handle transferred message  
    * @Param      : @param clientChannel
    * @Param      : @param allocator
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleTransferMsg(final Channel clientChannel, final ByteBufAllocator allocator, final NAPTMessage msg)
    {
        Channel intraChannel = clientChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).get();
        if (null == intraChannel)
        {
            return;
        }

        ByteBuf buf = allocator.buffer(msg.getData().length);
        buf.writeBytes(msg.getData());
        intraChannel.writeAndFlush(buf);
    }

    /**
    * 
    * @Title      : handleServerMsg 
    * @Description: Handle server message 
    * @Param      : @param clientChannel
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleServerMsg(final Channel clientChannel, final NAPTMessage msg)
    {
        String msgInfo = NAPTConst.EMPTY;
        switch (msg.getType())
        {
            case NAPTMessage.TYPE_NO_AVAILABLE_PORT:
                msgInfo = NAPTConst.MSG_NO_AVAILABLE_PORT;
                break;
            case NAPTMessage.TYPE_IS_INUSE_KEY:
                msgInfo = NAPTConst.MSG_IS_INUSE_KEY;
                break;
            case NAPTMessage.TYPE_DISABLED_ACCESS_KEY:
                msgInfo = NAPTConst.MSG_DISABLED_ACCESS_KEY;
                break;
            case NAPTMessage.TYPE_DISABLED_TRIAL_CLIENT:
                //msgInfo = NAPTConst.MSG_DISABLED_TRIAL_CLIENT;
                break;
            case NAPTMessage.TYPE_INVALID_KEY:
                msgInfo = NAPTConst.MSG_INVALID_ACCESS_KEY;
                break;
            default:
                msgInfo = NAPTConst.EMPTY;
                break;
        }

        if (StringUtils.isEmpty(msgInfo))
        {
            return;
        }

        System.out.println(NAPTConst.CCDN_NAPT_ACCESS_KEY + "=" + m_config.strValue(NAPTConst.CCDN_NAPT_ACCESS_KEY));
        System.out.println(msgInfo);

        NAPTUtil.close(clientChannel);
        NAPTClientContainer.getContainer().stop(msg.getType());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NAPTMessage msg) throws Exception
    {
        Channel clientChannel = ctx.channel();
        switch (msg.getType())
        {
            case NAPTMessage.TYPE_CONNECT:
                handleConnectMsg(clientChannel, msg);
                break;
            case NAPTMessage.TYPE_DISCONNECT:
                handleDisconnectMsg(clientChannel, msg);
                break;
            case NAPTMessage.TYPE_TRANSFER:
                handleTransferMsg(clientChannel, ctx.alloc(), msg);
                break;
            default:
                handleServerMsg(clientChannel, msg);
                break;
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception
    {
        Channel clientChannel = ctx.channel();
        Channel intraChannel = clientChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).get();
        if (null != intraChannel)
        {
            intraChannel.config().setOption(ChannelOption.AUTO_READ, clientChannel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        Channel clientChannel = ctx.channel();
        if (NAPTClientManager.getClientChannel() == clientChannel)
        {
            NAPTClientManager.setClientChannel(null);
            NAPTClientManager.clearIntraServer();
            NAPTClientContainer.getContainer().restart();
        }
        else
        {
            Channel intraChannel = clientChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).get();
            NAPTUtil.close(intraChannel);
        }

        NAPTClientManager.removeClient(clientChannel);
        super.channelInactive(ctx);
    }
}
