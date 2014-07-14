
package org.jjwsoft.common.db;

import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.jjwsoft.common.util.SimpleProperties;

/**
 * DBCP数据库连接配置
 */
public class DBCPConfig {	
	private String driver;
	private String url;
	private String user;
	private String password;
	private int initializeSize = 0;
	private int maxActive = 2;
	private int maxIdle = maxActive;
	private int minIdle = 0;
	private int maxWait = 30000;
	private boolean removeAbandoned = true;
	private int removeAbandonedTimeout = 300;
	private boolean logAbandoned = false;

	/**
	 * 从文件中构建配置
	 * @param fileName
	 */
	public DBCPConfig(String fileName) {		
		load(new SimpleProperties(fileName));		
	}
	
	/**
	 * 从Properties中构建配置
	 * @param props
	 */
	public DBCPConfig(Properties props) {
		load(new SimpleProperties(props));
	}

	/**
	 * 使用指定的jdbc常见连接构建配置
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 */
	public DBCPConfig(String driver, String url, String user, String password) {
		super();
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	private void load(SimpleProperties props) {
		driver = props.checkString("jdbc.driverClassName");
		url = props.checkString("jdbc.url");
		try {
			user = props.checkString("jdbc.username");
		} catch (Throwable e) {
			user = props.checkString("jdbc.user");
		}
		password = props.checkString("jdbc.password");
		initializeSize = props.getInteger("dbcp.initializeSize", initializeSize);
		maxActive = props.getInteger("dbcp.maxActive", maxActive);
		maxIdle = props.getInteger("dbcp.maxIdle", maxIdle);
		minIdle = props.getInteger("dbcp.minIdle", minIdle);
		maxWait = props.getInteger("dbcp.maxWait", maxWait);
		removeAbandoned = props.getBoolean("dbcp.removeAbandoned", removeAbandoned);
		removeAbandonedTimeout = props.getInteger("dbcp.removeAbandonedTimeout", removeAbandonedTimeout);
		logAbandoned = props.getBoolean("dbcp.logAbandoned", logAbandoned);
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public int getInitializeSize() {
		return initializeSize;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public int getMaxWait() {
		return maxWait;
	}
	
	public boolean isRemoveAbandoned() {
		return removeAbandoned;
	}

	public int getRemoveAbandonedTimeout() {
		return removeAbandonedTimeout;
	}

	public boolean isLogAbandoned() {
		return logAbandoned;
	}
	
	public BasicDataSource createDataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(password);
		ds.setInitialSize(initializeSize);
		ds.setMaxActive(maxActive);
		ds.setMaxIdle(maxIdle);
		ds.setMinIdle(minIdle);
		ds.setMaxWait(maxWait);
		ds.setRemoveAbandoned(removeAbandoned);
		ds.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		ds.setLogAbandoned(logAbandoned);
		return ds;
	}
}
