package org.jjwsoft.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jjwsoft.common.error.ErrorUtil;

/**
 * <pre>
 * Properties文件读取工具类，有几个特点：
 * 1. 提供更方面的数据类型转换
 * 2. 支持默认值
 * 3. 封装一些错误
 * 4. 支持变量替换
 * </pre>
 */
public class SimpleProperties {
	private static final Log logger = LogFactory.getLog(SimpleProperties.class);
	private Properties props;
	
	public SimpleProperties(Properties props) {
		this.props = props;
	}
	
	public SimpleProperties(String configFile) {
		InputStream is = null;
		try {
			is = new FileInputStream(new File(configFile));
			props = new Properties();
			props.load(is);
		} catch (FileNotFoundException e) {
			throw ErrorUtil.createError(RuntimeException.class, "配置文件不存在：" + configFile, e);
		} catch (IOException e) {
			throw ErrorUtil.createError(RuntimeException.class, "配置文件读取失败：" + configFile, e);
		} finally {			
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.warn(String.format("配置文件关闭失败：%s。错误：%s", configFile, e));
					logger.debug("堆栈：", e);
				}
			}
		}
	}
	
	public int getInteger(String name, int defValue) {
		String value = getString(name);
		if (value == null)
			return defValue;

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			logger.warn(String.format("数据库配置文件配置项[%s:%s]格式错误，将使用默认值[%d]。错误：%s", name, value, defValue, e));
			logger.debug("堆栈：", e);
			return defValue;
		}
	}

	public String checkString(String name) {
		String value = getString(name);
		if (value == null)
			throw new RuntimeException("数据库配置文件未提供必须的配置项：" + name);
		return value;
	}

	public String getString(String name) {
		String value = props.getProperty(name);
		if (value == null)
			return null;
		
		if (value.indexOf(MARCO_START) < 0)
			return value;
		
		Set<String> accessed = new HashSet<String>();		
		return getStringMarco(name, accessed);
	}
	
	private static final String MARCO_START = "${";
	private static final String MARCO_END = "}";

	private String getStringMarco(String name, Set<String> accessed) {
		if (accessed.contains(name))
			throw new RuntimeException("宏替换存在循环定义：" + name);
		accessed.add(name);
		
		try {
			String value = props.getProperty(name);
			if (value == null)
				return "";
			while (true) {
				int marcoStart = value.indexOf(MARCO_START);
				if (marcoStart < 0)
					break;
				int marcoEnd = value.indexOf(MARCO_END, marcoStart);
				if (marcoEnd < 0)
					break;
				String marco = value.substring(marcoStart + 2, marcoEnd);
				String marcoValue = getStringMarco(marco, accessed);
				value = value.substring(0, marcoStart) + marcoValue + value.substring(marcoEnd + 1);
			}
			return value;
		} finally {
			accessed.remove(name);
		}
	}

	public boolean getBoolean(String name, boolean defValue) {
		String value = getString(name);
		if (value == null)
			return defValue;

		try {
			return Boolean.parseBoolean(value);
		} catch (Throwable e) {
			logger.warn(String.format("数据库配置文件配置项[%s:%s]格式错误，将使用默认值[%d]。错误：%s", name, value, defValue, e));
			logger.debug("堆栈：", e);
			return defValue;
		}
	}
}
