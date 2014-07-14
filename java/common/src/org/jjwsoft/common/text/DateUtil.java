package org.jjwsoft.common.text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 日期相关工具类
 */
public class DateUtil {
	/**
	 * 日期格式
	 */
	public static final String PATTERN_YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
	private static ConcurrentMap<String, ThreadLocal<DateFormat>> formaters = new ConcurrentHashMap<String, ThreadLocal<DateFormat>>();
	
	/**
	 * 获取一个格式化类，使用线程局部变量，以提高性能并且避免DateUtil并发
	 * @param pattern
	 * @return
	 */
	public static DateFormat getFormat(String pattern) {
		ThreadLocal<DateFormat> formater = formaters.get(pattern);
		if (formater == null) {
			formater = new DateFormatThreadLocal(pattern);
			formaters.put(pattern, formater);
		}
		return formater.get();
	}

	/**
	 * 获取一个格式化类，使用默认的模式 {@link #PATTERN_YYYYMMDD_HHMMSS}
	 * @return
	 */
	public static DateFormat getFormat() {
		return getFormat(PATTERN_YYYYMMDD_HHMMSS);
	}
	
	/**
	 * 将指定日期用指定的格式转化为文本
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		return getFormat(pattern).format(date);
	}
	
	/**
	 * 将指定日期用默认的格式转化为文本 {@link #PATTERN_YYYYMMDD_HHMMSS}
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return format(date, PATTERN_YYYYMMDD_HHMMSS);
	}

	private static class DateFormatThreadLocal extends ThreadLocal<DateFormat> {
		private String pattern;

		public DateFormatThreadLocal(String pattern) {
			super();
			this.pattern = pattern;
		}

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(pattern);
		}
	}
}
