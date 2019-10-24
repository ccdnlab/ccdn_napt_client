package org.ccdn.client.constant;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @Class Name : NAPTConst
 * @Description: CCDN napt client constant definition
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
 */
public class NAPTConst {

	public static final int MAX_POOL_SIZE = 100;

	public static final String CCDN_NAPT_ACCESS_KEY = "CCDN_NAPT_ACCESS_KEY";

	public static final String MSG_NO_AVAILABLE_PORT = "There are no available ports for the ccdn napt access key.\n";

	public static final String MSG_IS_INUSE_KEY = "CCDN NAPT access key is in use by other CCDN NAPT client.\n";

	public static final String MSG_DISABLED_ACCESS_KEY = "CCDN NAPT access key has been disabled.\n";

	public static final String MSG_INVALID_ACCESS_KEY = "CCDN NAPT access key is not valid.\n";

	public static final byte HEADER_SIZE = 4;

	public static final int TYPE_SIZE = 1;

	public static final int SERIAL_NUM_SIZE = 8;

	public static final int URI_LEN_SIZE = 1;

	/* Max packet size is 2M */
	public static final int MAX_FRAME_LEN = 2 * 1024 * 1024;

	public static final int FIELD_OFFSET = 0;

	public static final int FIELD_LEN = 4;

	public static final int INIT_BYTES_TO_STRIP = 0;

	public static final int ADJUSTMENT = 0;

	public static final int READ_IDLE_TIME = 60;

	public static final int WRITE_IDLE_TIME = 40;

	public static final int CCDN_NAPT_SERVER_PORT_DEFAULT = 6060;

	public static final long HALF_SECOND = 500L;

	public static final long ONE_SECOND = 1000L;

	public static final long THREE_SECONDS = 3 * ONE_SECOND;

	public static final long THIRTY_SECONDS = 30 * ONE_SECOND;

	public static final long ONE_MINUTE = 60 * ONE_SECOND;

	public static final long ONE_HOUR = 60 * ONE_MINUTE;

	public static final long TWELVE_HOURS = 12 * ONE_HOUR;

	public static final long ONE_DAY = 24 * ONE_HOUR;

	public static final long THREE_DAYS = 3 * ONE_DAY;

	public static final long TWO_MINUTES = 2 * ONE_MINUTE;

	public static final long THREE_MINUTES = 3 * ONE_MINUTE;

	public static final long FIVE_MINUTES = 5 * ONE_MINUTE;

	public static final long SEVEN_MINUTES = 7 * ONE_MINUTE;

	public static final long TEN_MINUTES = 10 * ONE_MINUTE;

	public static final long TWENTY_MINUTES = 20 * ONE_MINUTE;

	public static final String TLS = "TLS";

	public static final String JKS = "JKS";

	public static final String EMPTY = "";

	public static final String CCDN_NAPT_CONF = "ccdn_napt.conf";

	public static final String CCDN_NAPT_SERVER_HOST = "CCDN_NAPT_SERVER_HOST";

	public static final String CCDN_NAPT_SERVER_PORT = "CCDN_NAPT_SERVER_PORT";

	public static final String CCDN_NAPT_SSL_ENABLE = "CCDN_NAPT_SSL_ENABLE";

	public static final String CCDN_NAPT_SSL_JKS = "CCDN_NAPT_SSL_JKS";

	public static final String CCDN_NAPT_SSL_PASSWD = "CCDN_NAPT_SSL_PASSWORD";

	public static final String CCDN_NAPT_SSL_PASSWD_DEFAULT = "Wisdom-CCDN NAPT";

	public static final String CCDN_NAPT_SSL_JKS_DEFAULT = "ccdn_napt.jks";

	public static final AttributeKey<Channel> CCDN_NAPT_CHANNEL = AttributeKey.newInstance("CCDN_NAPT_CHANNEL");

	public static final AttributeKey<String> CCDN_NAPT_URI = AttributeKey.newInstance("CCDN_NAPT_URI");

	public static final AttributeKey<String> CCDN_NAPT_KEY = AttributeKey.newInstance("CCDN_NAPT_KEY");

}
