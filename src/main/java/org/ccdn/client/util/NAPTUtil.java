package org.ccdn.client.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Class Name : NAPTUtil
 * @Description: CCDN NAPT utilities
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
 */
public class NAPTUtil {

	private static Logger m_log = LoggerFactory.getLogger(NAPTUtil.class);

	public static <T> String toJson(T instance) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(instance);
		} catch (Exception e) {
			m_log.debug("Write object [" + instance + "] as json string failed.", e);
		}
		return StringUtils.EMPTY;
	}

	public static <T> T toObject(String json, Class<T> clas) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (T) mapper.readValue(json, clas);
		} catch (Exception e) {
			m_log.error("Failed to change json text [" + json + "] to object.", e);
		}

		return null;
	}

	public static <T> T toObject(String json, TypeToken<T> typeToken) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(json, typeToken.getType());
		} catch (Exception e) {
			m_log.debug("Change JSON to object failed.", e);
		}
		return null;
	}

	public static <T> void toJsonFile(File jf, T instance) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(jf, instance);
		} catch (Exception e) {
			m_log.error("Change object [" + instance + "] to json file [" + jf.getName() + "] failed.", e);
		}
	}

	public static void close(InputStream is) {
		if (null == is) {
			return;
		}
		try {
			is.close();
		} catch (IOException e) {
			// Ignore this exception
		}
	}

	public static void close(Channel channel) {
		if (null == channel) {
			return;
		}
		try {
			channel.close();
		} catch (Exception e) {
			// Ignore this exception
		}
	}

	public static void close(ChannelHandlerContext ctx) {
		if (null == ctx) {
			return;
		}
		try {
			close(ctx.channel());
			ctx.close();
		} catch (Exception e) {
			// Ignore this exception
		}
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// Ignore this exception
		}
	}

	public static String division(long a, long b) {
		double num = (double) a / b;
		DecimalFormat df = new DecimalFormat("0.0");
		String result = df.format(num);
		return result;
	}

	public static void exec(String cmd) {
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			m_log.error("Failed to execute command: " + cmd, e);
		}
	}

	public static boolean isActive(Channel ch) {
		if (null == ch) {
			return false;
		}
		if (ch.isActive() || ch.isOpen()) {
			return true;
		}
		return false;
	}
}
