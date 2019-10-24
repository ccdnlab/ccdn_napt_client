package org.ccdn.client.init;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.ccdn.client.constant.NAPTConst;
import org.ccdn.client.handler.NAPTClientHandler;
import org.ccdn.client.util.NAPTConfig;
import org.ccdn.client.util.NAPTIdleChecker;
import org.ccdn.client.util.NAPTMsgDecoder;
import org.ccdn.client.util.NAPTMsgEncoder;
import org.ccdn.client.util.NAPTSSLCreator;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

/** 
* @Class Name : NAPTClientInitializer 
* @Description: CCDN NAPT client initializer
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTClientInitializer extends ChannelInitializer<SocketChannel>
{
    private SSLContext m_sslContext = null;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception
    {
        if (NAPTConfig.getConfig().boolValue(NAPTConst.CCDN_NAPT_SSL_ENABLE, false))
        {
            if (null == this.m_sslContext)
            {
                this.m_sslContext = NAPTSSLCreator.getCreator().getSSLContext();
            }

            SSLEngine engine = this.m_sslContext.createSSLEngine();
            engine.setUseClientMode(true);
            ch.pipeline().addLast(new SslHandler(engine));
        }
        ch.pipeline().addLast(new NAPTMsgDecoder(NAPTConst.MAX_FRAME_LEN, NAPTConst.FIELD_OFFSET, NAPTConst.FIELD_LEN, NAPTConst.ADJUSTMENT, NAPTConst.INIT_BYTES_TO_STRIP));
        ch.pipeline().addLast(new NAPTMsgEncoder());
        ch.pipeline().addLast(new NAPTIdleChecker(NAPTConst.READ_IDLE_TIME, NAPTConst.WRITE_IDLE_TIME - 10, 0));
        ch.pipeline().addLast(new NAPTClientHandler());
    }

}
