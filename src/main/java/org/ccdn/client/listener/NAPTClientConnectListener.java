package org.ccdn.client.listener;

import org.ccdn.client.constant.NAPTConst;
import org.ccdn.client.message.NAPTMessage;
import org.ccdn.client.util.NAPTClientManager;
import org.ccdn.client.util.NAPTConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;

/** 
* @Class Name : NAPTClientConnectListener 
* @Description: CCDN NAPT client connection listener
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTClientConnectListener implements ChannelFutureListener
{
    private static Logger m_log = LoggerFactory.getLogger(NAPTClientConnectListener.class);
    
    private Channel m_clientChannel = null;
    private Channel m_intraChannel = null;
    private NAPTMessage m_msg = null;

    /** 
    * @Title      : NAPTClientConnectListener 
    * @Description: constructor 
    * @Param      : @param clientChannel
    * @Param      : @param intraServer
    * @Param      : @param msg
    */
    public NAPTClientConnectListener(Channel clientChannel, Channel intraChannel, NAPTMessage msg)
    {
        this.m_clientChannel = clientChannel;
        this.m_intraChannel = intraChannel;
        this.m_msg = msg;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception
    {
        if (!future.isSuccess())
        {
        	NAPTMessage hmsg = new NAPTMessage();
            hmsg.setType(NAPTMessage.TYPE_DISCONNECT);
            hmsg.setUri(this.m_msg.getUri());
            this.m_clientChannel.writeAndFlush(hmsg);
            m_log.error("Failed to connect CCDN NAPT server {}", future.cause().getMessage());
            return;
        }

        NAPTConfig config = NAPTConfig.getConfig();
        Channel naptChannel = future.channel();

        naptChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).set(this.m_intraChannel);
        this.m_intraChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).set(naptChannel);

        NAPTMessage hmsg = new NAPTMessage();
        hmsg.setType(NAPTMessage.TYPE_CONNECT);
        hmsg.setUri(this.m_msg.getUri() + "@" + config.strValue(NAPTConst.CCDN_NAPT_ACCESS_KEY));
        naptChannel.writeAndFlush(hmsg);

        this.m_intraChannel.config().setOption(ChannelOption.AUTO_READ, true);
        NAPTClientManager.addIntraServer(this.m_msg.getUri(), this.m_intraChannel);
        NAPTClientManager.setIntraServerUri(this.m_intraChannel, this.m_msg.getUri());
    }
}
