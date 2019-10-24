package org.ccdn.client;

import org.ccdn.client.init.NAPTClientInitializer;
import org.ccdn.client.init.NAPTIntraServerInitializer;
import org.ccdn.client.listener.NAPTClientAuthListener;
import org.ccdn.client.util.NAPTClientManager;
import org.ccdn.client.constant.NAPTConst;
import org.ccdn.client.util.NAPTConfig;
import org.ccdn.client.util.NAPTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/** 
* @Class Name : NAPTClientContainer 
* @Description: CCDN NAPT client container 
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTClientContainer
{
    private static Logger m_log = LoggerFactory.getLogger(NAPTClientContainer.class);
    
    private static NAPTClientContainer m_container = null;
    private NioEventLoopGroup m_workerGroup = null;
    private Bootstrap m_naptClient = null;
    private Bootstrap m_intraServer = null;
    private NAPTConfig m_config = NAPTConfig.getConfig();
    private long m_sleepTime = NAPTConst.ONE_SECOND;

    /** 
    * @Title      : NAPTClientContainer 
    * @Description: Default constructor 
    * @Param      : 
    */
    public NAPTClientContainer()
    {
        this.init();
    }

    /**
    * 
    * @Title      : getContainer 
    * @Description: Get container instance
    * @Param      : @return 
    * @Return     : ClientContainer
    * @Throws     :
     */
    public static NAPTClientContainer getContainer()
    {
        if (null == m_container)
        {
        	m_container = new NAPTClientContainer();
        }
        return m_container;
    }

    /**
    * 
    * @Title      : init 
    * @Description: Initialize variables 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public void init()
    {
        this.m_sleepTime = NAPTConst.ONE_SECOND;
        this.m_workerGroup = new NioEventLoopGroup();

        this.m_intraServer = new Bootstrap();
        this.m_intraServer.group(this.m_workerGroup);
        this.m_intraServer.channel(NioSocketChannel.class);
        this.m_intraServer.handler(new NAPTIntraServerInitializer());

        this.m_naptClient = new Bootstrap();
        this.m_naptClient.group(this.m_workerGroup);
        this.m_naptClient.channel(NioSocketChannel.class);
        this.m_naptClient.handler(new NAPTClientInitializer());
    }

    public void setSleepTime(long sleepTime)
    {
        this.m_sleepTime = sleepTime;
    }

    public Bootstrap getNaptClient()
    {
        return m_naptClient;
    }

    public Bootstrap getIntraServer()
    {
        return m_intraServer;
    }

    /**
    * 
    * @Title      : connectNaptServer 
    * @Description: Connect to napt server 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    private void connectNaptServer()
    {
        final String host = m_config.strValue(NAPTConst.CCDN_NAPT_SERVER_HOST);
        final int port = m_config.intValue(NAPTConst.CCDN_NAPT_SERVER_PORT, NAPTConst.CCDN_NAPT_SERVER_PORT_DEFAULT);
        this.m_naptClient.connect(host, port).addListener(new NAPTClientAuthListener());
    }

    /**
    * 
    * @Title      : waitMoment 
    * @Description: Wait for reconnection 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    private void waitMoment()
    {
        if (this.m_sleepTime > NAPTConst.ONE_MINUTE)
        {
            this.m_sleepTime = NAPTConst.ONE_SECOND;
        }

        this.m_sleepTime = this.m_sleepTime * 2;
        NAPTUtil.sleep(this.m_sleepTime);
    }

    /**
    * 
    * @Title      : start 
    * @Description: Start napt client 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public void start()
    {
        this.connectNaptServer();
    }

    /**
    * 
    * @Title      : stop 
    * @Description: Stop napt client 
    * @Param      : @param status 
    * @Return     : void
    * @Throws     :
     */
    public void stop(byte status)
    {
        this.m_workerGroup.shutdownGracefully();
        NAPTClientManager.clearNAPTClient();

        m_log.info("Stopped napt client with status {}", status);
        System.exit(status);
    }

    /**
    * 
    * @Title      : restart 
    * @Description: Restart napt client 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public void restart()
    {
        this.waitMoment();
        this.connectNaptServer();
    }

    /**
    * 
    * @Title      : main 
    * @Description: napt client main process 
    * @Param      : @param args 
    * @Return     : void
    * @Throws     :
     */
    public static void main(String[] args)
    {
        NAPTClientContainer.getContainer().start();
    }
}