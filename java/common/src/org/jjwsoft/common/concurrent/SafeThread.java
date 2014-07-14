package org.jjwsoft.common.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 对Thread进行一些默认行为附加，以便在使用线程时可以更规范，包含以下设置：
 * 1. 一定会提供线程名
 * 2. 避免Exception外的异常导致线程退出时没有任何信息
 * 3. 增加默认日志
 */
public final class SafeThread extends Thread {
	private static final Log logger = LogFactory.getLog(SafeThread.class);	
	private Runnable runner;
	
	public SafeThread(Runnable runner) {
		this(runner, runner.getClass().getSimpleName());
	}

	public SafeThread(Runnable runner, String name) {
		super(name);
		this.runner = runner;
	}

	@Override
	public void run() {
		if (logger.isDebugEnabled())
			logger.debug(String.format("线程已启动[%s]", getName()));
		try {
			runner.run();
		} catch (Throwable e) {
			logger.error(String.format("线程异常失败，将导致系统运行不正确[%s]。错误：", getName()), e);
		}
	}	
}
