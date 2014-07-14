package org.jjwsoft.common.error;

import java.lang.reflect.Constructor;

/**
 * 错误工具类
 */
public class ErrorUtil {
	/**
	 * 用常见的范式建立一个异常
	 * @param exceptionClass
	 * @param message
	 * @param cause
	 * @return
	 */
	public static <T> T createError(Class<T> exceptionClass, String message, Throwable cause) {
		try {
			Constructor<T> constructor = exceptionClass.getConstructor(String.class, Throwable.class);
			String causeMessage = cause.getMessage();
			return constructor.newInstance(message + "。错误："
					+ (causeMessage == null ? cause.getClass().getSimpleName() : causeMessage), cause);
		} catch (Throwable e) {
			String causeMessage = e.getMessage();
			throw new RuntimeException("建立异常类[" + exceptionClass.getName() + "]失败。错误："
					+ (causeMessage == null ? e.getClass().getSimpleName() : causeMessage), e);
		}
	}
}
