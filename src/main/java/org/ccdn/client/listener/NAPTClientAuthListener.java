package org.ccdn.client.listener;

import org.ccdn.client.NAPTClientContainer;
import org.ccdn.client.constant.NAPTConst;
import org.ccdn.client.message.NAPTMessage;
import org.ccdn.client.util.NAPTClientManager;
import org.ccdn.client.util.NAPTConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/** 
* @Class Name : NAPTClientAuthListener 
* @Description: CCDN NAPT client listener
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTClientAuthListener implements ChannelFutureListener
{
    private static Logger m_log = LoggerFactory.getLogger(NAPTClientAuthListener.class);

    @Override
    public void operationComplete(ChannelFuture future) throws Exception
    {
        final NAPTConfig config = NAPTConfig.getConfig();
        final String host = config.strValue(NAPTConst.CCDN_NAPT_SERVER_HOST);
        final int port = config.intValue(NAPTConst.CCDN_NAPT_SERVER_PORT, NAPTConst.CCDN_NAPT_SERVER_PORT_DEFAULT);
        NAPTClientContainer container = NAPTClientContainer.getContainer();

        if (!future.isSuccess())
        {
            System.out.println("Unable to connect CCDN NAPT server <" + host + ":" + port + ">");
            m_log.warn("Failed to connect CCDN NAPT server {}", future.cause().getMessage());
            container.restart();
            return;
        }

        Channel clientChannel = future.channel();
        NAPTClientManager.setClientChannel(clientChannel);

        NAPTMessage hmsg = new NAPTMessage();
        hmsg.setType(NAPTMessage.TYPE_AUTH);
        hmsg.setUri(config.strValue(NAPTConst.CCDN_NAPT_ACCESS_KEY));
        clientChannel.writeAndFlush(hmsg);

        container.setSleepTime(NAPTConst.ONE_SECOND);
        m_log.info("Connect CCDN NAPT server success {}", clientChannel);
    }
}
