package org.ccdn.client.listener;

import org.ccdn.client.NAPTClientContainer;
import org.ccdn.client.constant.NAPTConst;
import org.ccdn.client.message.NAPTMessage;
import org.ccdn.client.util.NAPTClientManager;
import org.ccdn.client.util.NAPTConfig;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;

/** 
* @Class Name : NAPTIntraServerListener 
* @Description: CCDN NAPT Intra-server listener 
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTIntraServerListener implements ChannelFutureListener
{
    private Channel m_clientChannel = null;
    private NAPTMessage m_msg = null;

    /** 
    * @Title      : NAPTIntraServerListener 
    * @Description: constructor 
    * @Param      : @param channel
    * @Param      : @param msg
    */
    public NAPTIntraServerListener(Channel channel, NAPTMessage msg)
    {
        this.m_clientChannel = channel;
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
            return;
        }

        final NAPTConfig config = NAPTConfig.getConfig();
        final Channel intraChannel = future.channel();
        intraChannel.config().setOption(ChannelOption.AUTO_READ, false);

        Channel naptChannel = NAPTClientManager.pollClient();
        if (null == naptChannel)
        {
            String host = config.strValue(NAPTConst.CCDN_NAPT_SERVER_HOST);
            int port = config.intValue(NAPTConst.CCDN_NAPT_SERVER_PORT);

            NAPTClientConnectListener listener = new NAPTClientConnectListener(this.m_clientChannel, intraChannel, this.m_msg);
            Bootstrap naptClient = NAPTClientContainer.getContainer().getNaptClient();
            naptClient.connect(host, port).addListener(listener);
            return;
        }

        naptChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).set(intraChannel);
        intraChannel.attr(NAPTConst.CCDN_NAPT_CHANNEL).set(naptChannel);

        NAPTMessage hmsg = new NAPTMessage();
        hmsg.setType(NAPTMessage.TYPE_CONNECT);
        hmsg.setUri(this.m_msg.getUri() + "@" + config.strValue(NAPTConst.CCDN_NAPT_ACCESS_KEY));
        naptChannel.writeAndFlush(hmsg);

        intraChannel.config().setOption(ChannelOption.AUTO_READ, true);
        NAPTClientManager.addIntraServer(this.m_msg.getUri(), intraChannel);
        NAPTClientManager.setIntraServerUri(intraChannel, this.m_msg.getUri());
    }
}
