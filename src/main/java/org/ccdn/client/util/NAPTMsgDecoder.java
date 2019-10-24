package org.ccdn.client.util;

import org.ccdn.client.constant.NAPTConst;
import org.ccdn.client.message.NAPTMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @Class Name : NAPTMsgDecoder
 * @Description: CCDN NAPT message decoder
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
 */
public class NAPTMsgDecoder extends LengthFieldBasedFrameDecoder {

	public NAPTMsgDecoder(int maxFrameLen, int lenFieldOffset, int lenFieldLen, int lenAdjustment,
			int initBytesToStrip) {
		super(maxFrameLen, lenFieldOffset, lenFieldLen, lenAdjustment, initBytesToStrip);
	}

	public NAPTMsgDecoder(int maxFrameLen, int lenFieldOffset, int lenFieldLen, int lenAdjustment, int initBytesToStrip,
			boolean failFast) {
		super(maxFrameLen, lenFieldOffset, lenFieldLen, lenAdjustment, initBytesToStrip, failFast);
	}

	@Override
	protected NAPTMessage decode(ChannelHandlerContext ctx, ByteBuf bin) throws Exception {
		ByteBuf in = (ByteBuf) super.decode(ctx, bin);
		if (null == in) {
			return null;
		}

		if (in.readableBytes() < NAPTConst.HEADER_SIZE) {
			return null;
		}

		int frameLen = in.readInt();
		if (in.readableBytes() < frameLen) {
			return null;
		}

		NAPTMessage hmsg = new NAPTMessage();
		hmsg.setType(in.readByte());
		hmsg.setSerialNumber(in.readLong());

		byte uriLen = in.readByte();
		byte[] uriBytes = new byte[uriLen];
		in.readBytes(uriBytes);
		hmsg.setUri(new String(uriBytes));

		int dataLen = frameLen - NAPTConst.TYPE_SIZE - NAPTConst.SERIAL_NUM_SIZE - NAPTConst.URI_LEN_SIZE - uriLen;
		byte[] data = new byte[dataLen];
		in.readBytes(data);
		hmsg.setData(data);
		in.release();

		return hmsg;
	}
}
