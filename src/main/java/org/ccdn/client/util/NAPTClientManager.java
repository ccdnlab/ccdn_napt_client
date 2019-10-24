/*
 * Copyright 2018-present, Yudong (Dom) Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ccdn.client.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.StringUtils;
import org.ccdn.client.constant.NAPTConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;

/** 
* @Class Name : NAPTClientManager 
* @Description: CCDN NAPT client channel manager 
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTClientManager
{
    private static Logger m_log = LoggerFactory.getLogger(NAPTClientManager.class);

    private static Map<String, Channel> m_intraServers = new ConcurrentHashMap<String, Channel>();

    private static ConcurrentLinkedQueue<Channel> m_naptClients = new ConcurrentLinkedQueue<Channel>();

    private static volatile Channel m_clientChannel = null;

    public static Channel getClientChannel()
    {
        return m_clientChannel;
    }

    public static void setClientChannel(Channel channel)
    {
        NAPTClientManager.m_clientChannel = channel;
    }

    public static void setIntraServerUri(Channel channel, String uri)
    {
        channel.attr(NAPTConst.CCDN_NAPT_URI).set(uri);
    }

    public static String getIntraServerUri(Channel channel)
    {
        return channel.attr(NAPTConst.CCDN_NAPT_URI).get();
    }

    public static void removeClient(Channel channel)
    {
    	m_naptClients.remove(channel);
    }

    /**
    *     
    * @Title      : pollClient 
    * @Description: pull one napt client 
    * @Param      : @return 
    * @Return     : Channel
    * @Throws     :
     */
    public static Channel pollClient()
    {
        return m_naptClients.poll();
    }

    /**
    * 
    * @Title      : pushClient 
    * @Description: Return a napt client 
    * @Param      : @param channel 
    * @Return     : void
    * @Throws     :
     */
    public static void pushClient(Channel channel)
    {
        if (m_naptClients.size() > NAPTConst.MAX_POOL_SIZE)
        {
            channel.close();
            return;
        }

        channel.config().setOption(ChannelOption.AUTO_READ, true);
        channel.attr(NAPTConst.CCDN_NAPT_CHANNEL).set(null);
        m_naptClients.offer(channel);
    }

    /**
    * 
    * @Title      : clearIntraServer 
    * @Description: Clear intra-server channel 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public static void clearIntraServer()
    {
    	m_log.info("Channel closed, clear intranet server channels.");
        for (Entry<String, Channel> entry : m_intraServers.entrySet())
        {
            Channel channel = entry.getValue();
            if (channel.isActive())
            {
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
        m_intraServers.clear();
    }

    /**
    * 
    * @Title      : clearNAPTClient 
    * @Description: Close and clear all the channels 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public static void clearNAPTClient()
    {
        // Clear and close intra-servers
        for (Entry<String, Channel> entry : m_intraServers.entrySet())
        {
            Channel channel = entry.getValue();
            NAPTUtil.close(channel);
        }
        m_intraServers.clear();

        // Close and clear client channel
        NAPTUtil.close(m_clientChannel);
        for (Channel clientChannel : m_naptClients)
        {
        	NAPTUtil.close(clientChannel);
        }
    }

    /**
    * 
    * @Title      : addIntraServer 
    * @Description: Add intra-server  
    * @Param      : @param uri
    * @Param      : @param channel 
    * @Return     : void
    * @Throws     :
     */
    public static void addIntraServer(String uri, Channel channel)
    {
    	m_intraServers.put(uri, channel);
    }

    /**
    * 
    * @Title      : removeIntraServer 
    * @Description: Remove intra-server 
    * @Param      : @param uri
    * @Param      : @return 
    * @Return     : Channel
    * @Throws     :
     */
    public static Channel removeIntraServer(String uri)
    {
        if (StringUtils.isEmpty(uri))
        {
            return null;
        }
        return m_intraServers.remove(uri);
    }

}
