package org.ccdn.client.util;

import org.ccdn.client.message.NAPTMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @Class Name : NAPTIdleChecker
 * @Description: check CCDN NAPT idle channel
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
 */
public class NAPTIdleChecker extends IdleStateHandler {
	private static Logger m_log = LoggerFactory.getLogger(NAPTIdleChecker.class);

	public NAPTIdleChecker(int readerIdleTime, int writerIdleTime, int allIdleTime) {
		super(readerIdleTime, writerIdleTime, allIdleTime);
	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		if (IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT == evt) {
			m_log.debug("Channel write timeout {}.", ctx.channel());
			NAPTMessage hmsg = new NAPTMessage();
			hmsg.setType(NAPTMessage.TYPE_HEARTBEAT);
			ctx.channel().writeAndFlush(hmsg);
		} else if (IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT == evt) {
			m_log.warn("Channel read timeout {}.", ctx.channel());
			ctx.channel().close();
		}
		super.channelIdle(ctx, evt);
	}
}
