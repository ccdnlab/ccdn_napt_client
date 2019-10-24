package org.ccdn.client.util;

import org.ccdn.client.constant.NAPTConst;
import org.ccdn.client.message.NAPTMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/** 
* @Class Name : NAPTMsgEncoder 
* @Description: CCDN NAPT message encoder 
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTMsgEncoder extends MessageToByteEncoder<NAPTMessage> {
	@Override
	protected void encode(ChannelHandlerContext ctx, NAPTMessage msg, ByteBuf out) throws Exception {
		int bodyLen = NAPTConst.TYPE_SIZE + NAPTConst.SERIAL_NUM_SIZE + NAPTConst.URI_LEN_SIZE;
		byte[] uriBytes = null;
		if (null != msg.getUri()) {
			uriBytes = msg.getUri().getBytes();
			bodyLen += uriBytes.length;
		}

		if (null != msg.getData()) {
			bodyLen += msg.getData().length;
		}

		// Write the total packet length but without length field's length.
		out.writeInt(bodyLen);
		out.writeByte(msg.getType());
		out.writeLong(msg.getSerialNumber());

		if (null != uriBytes) {
			out.writeByte((byte) uriBytes.length);
			out.writeBytes(uriBytes);
		} else {
			out.writeByte((byte) 0x00);
		}

		if (null != msg.getData()) {
			out.writeBytes(msg.getData());
		}
	}
}
