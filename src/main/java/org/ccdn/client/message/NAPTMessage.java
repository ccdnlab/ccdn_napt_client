package org.ccdn.client.message;

import java.util.Arrays;

/**
 * @Class Name : NAPTMessage
 * @Description: Exchange message between CCDN NAPT client and server
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
 */
public class NAPTMessage {
	/**
	 * Authenticate message to check whether accessKey is correct
	 */
	public static final byte TYPE_AUTH = 0x01;

	/**
	 * There are no available ports for the access key
	 */
	public static final byte TYPE_NO_AVAILABLE_PORT = 0x02;

	/**
	 * CCDN NAPT connection message
	 */
	public static final byte TYPE_CONNECT = 0x03;

	/**
	 * CCDN NAPT disconnection message
	 */
	public static final byte TYPE_DISCONNECT = 0x04;

	/**
	 * CCDN NAPT data transfer
	 */
	public static final byte TYPE_TRANSFER = 0x05;

	/**
	 * Access key is in use by other CCDN NAPT client
	 */
	public static final byte TYPE_IS_INUSE_KEY = 0x06;

	/**
	 * CCDN NAPT beat
	 */
	public static final byte TYPE_HEARTBEAT = 0x07;

	/**
	 * Disabled access key
	 */
	public static final byte TYPE_DISABLED_ACCESS_KEY = 0x08;

	/**
	 * Disabled trial client
	 */
	public static final byte TYPE_DISABLED_TRIAL_CLIENT = 0x09;

	/**
	 * Invalid access key
	 */
	public static final byte TYPE_INVALID_KEY = 0x10;

	/**
	 * Message type
	 */
	private byte m_type;

	/**
	 * Message type serial number
	 */
	private long m_serialNumber;

	/**
	 * Message request command
	 */
	private String m_uri;

	/**
	 * Message transfer data
	 */
	private byte[] m_data;

	public byte getType() {
		return m_type;
	}

	public void setType(byte type) {
		this.m_type = type;
	}

	public long getSerialNumber() {
		return m_serialNumber;
	}

	public void setSerialNumber(long serialNumber) {
		this.m_serialNumber = serialNumber;
	}

	public String getUri() {
		return m_uri;
	}

	public void setUri(String uri) {
		this.m_uri = uri;
	}

	public byte[] getData() {
		return m_data;
	}

	public void setData(byte[] data) {
		this.m_data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CCDN NAPT Message [type=");
		builder.append(m_type);
		builder.append(", serialNumber=");
		builder.append(m_serialNumber);
		builder.append(", uri=");
		builder.append(m_uri);
		builder.append(", data=");
		builder.append(Arrays.toString(m_data));
		builder.append("]");
		return builder.toString();
	}
}
