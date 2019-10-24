package org.ccdn.client.init;

import org.ccdn.client.handler.NAPTIntraServerHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/** 
* @Class Name : NAPTIntraServerInitializer 
* @Description: NAPT Intra-server initializer 
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTIntraServerInitializer extends ChannelInitializer<SocketChannel>
{
    @Override
    protected void initChannel(SocketChannel ch) throws Exception
    {
        ch.pipeline().addLast(new NAPTIntraServerHandler());
    }
}
