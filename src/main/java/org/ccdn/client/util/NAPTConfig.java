package org.ccdn.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.ccdn.client.constant.NAPTConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Class Name : NAPTConfig
 * @Description: CCDN NAPT configuration
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
 */
public class NAPTConfig {

	private static Logger m_log = LoggerFactory.getLogger(NAPTConfig.class);

	private static NAPTConfig m_config = null;

	private Properties m_naptConf = new Properties();

	public static NAPTConfig getConfig() {
		if (null == m_config) {
			m_config = new NAPTConfig();
		}
		return m_config;
	}

	public NAPTConfig() {
		this.initConfig(NAPTConst.CCDN_NAPT_CONF);
	}

	public void initConfig(String confFile) {
		InputStream is = null;
		try {
			is = NAPTConfig.class.getClassLoader().getResourceAsStream(confFile);
			if (null == is) {
				m_log.warn("Can not find configuration file: " + confFile);
				return;
			}
			this.m_naptConf.load(is);
		} catch (IOException e) {
			m_log.error("Failed to load configuration file.");
			throw new RuntimeException(e);
		} finally {
			NAPTUtil.close(is);
		}
	}

	public String strValue(String key) {
		String value = this.m_naptConf.getProperty(key);
		return StringUtils.trim(value);
	}

	public String strValue(String key, String defaultValue) {
		String value = this.strValue(key);
		if (StringUtils.isBlank(value)) {
			return defaultValue;
		}
		return value;
	}

	public Integer intValue(String key) {
		String value = this.strValue(key);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		if (StringUtils.isNumeric(value)) {
			return Integer.valueOf(value);
		}
		return null;
	}

	public int intValue(String key, int defaultValue) {
		Integer value = this.intValue(key);
		if (null == value) {
			return defaultValue;
		}
		return value;
	}

	public Boolean boolValue(String key) {
		String value = this.strValue(key);
		if (StringUtils.isBlank(value)) {
			return null;
		}

		if (!value.equals(Boolean.toString(false)) && !value.equals(Boolean.toString(true))) {
			return null;
		}

		return Boolean.valueOf(value);
	}

	public boolean boolValue(String key, Boolean defaultValue) {
		Boolean value = this.boolValue(key);
		if (null == value) {
			return defaultValue;
		}
		return value;
	}
}
