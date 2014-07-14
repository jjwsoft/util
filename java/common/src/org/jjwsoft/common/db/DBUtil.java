package org.jjwsoft.common.db;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jjwsoft.common.error.ErrorUtil;

/**
 * 数据库的工具函数
 */
public class DBUtil {
	/**
	 * Oracle数据库驱动
	 */
	public static final String DRIVER_ORACLE = "oracle.jdbc.OracleDriver";
	/**
	 * 建立一个数据库连接的命令行参数说明
	 */
	public static final String USAGE_CREATECONNECTION = "参数列表：\n"
			+ "\tdbtype\t数据库类型，默认为oracle\n"
			+ "\tdbdriver\tJDBC驱动，如果不指定dbtype，也可指定jdbc驱动\n"
			+ "\tdburl\tJDBC URL，如果指定了此参数，则忽略dbhost, dbport, dbname\n"
			+ "\tdbhost\t数据库地址\n"
			+ "\tdbport\t数据库端口\n"
			+ "\tdbname\t数据库实例名称\n"
			+ "\tdbuser\t用户名\n"
			+ "\tdbpass\t密码\n"
			+ "使用举例：\n"
			+ "\t简单访问oracle\t--dbhost 192.168.14.200 --dbuser test --dbpass test\n"
			+ "\t指定url访问\t--dburl jdbc:oracle:thin:@db:1521:orcl --dbuser test --dbpass test\n";	
	private static final DBInfo DBINFO_ORACLE = new DBInfo("oracle", DRIVER_ORACLE, "jdbc:oracle:thin:@%s:%d:%s", 1521, "orcl");
	private static final DBInfo[] DBINFOS = new DBInfo[] { 
		DBINFO_ORACLE,
		new DBInfo("sqlserver", "net.sourceforge.jtds.jdbc.Driver", "jdbc:jtds:sqlserver://%s:%d/%s", 1433, "")
	};
	
	/**
	 * 建立一个数据库连接
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static Connection createConnection(String driver, String url, String username, String password) {
		try {
			DriverManager.registerDriver((Driver)(Class.forName(driver).newInstance()));			
		} catch (Throwable e) {
			throw ErrorUtil.createError(RuntimeException.class, "无法初始化数据库驱动类[" + driver + "]", e);
		}
		
		try {
			return DriverManager.getConnection(url, username, password);			
		} catch (Throwable e) {
			throw ErrorUtil.createError(RuntimeException.class, "无法通过参数[url: " + url + " username: " + username + "]建立连接", e);			
		}
	}

	/**
	 * 支持从命令行创建一个数据库连接
	 * @param args
	 * @return
	 */
	public static Connection createConnection(String[] args) {		
		Getopt getopt = new Getopt(null, args, "", new LongOpt[] {
				new LongOpt("dbtype", LongOpt.REQUIRED_ARGUMENT, null, 't'),
				new LongOpt("dbdriver", LongOpt.REQUIRED_ARGUMENT, null, 'd'),
				new LongOpt("dburl", LongOpt.REQUIRED_ARGUMENT, null, 'r'),
				new LongOpt("dbport", LongOpt.REQUIRED_ARGUMENT, null, 'p'),
				new LongOpt("dbname", LongOpt.REQUIRED_ARGUMENT, null, 'n'),				
				new LongOpt("dbhost", LongOpt.REQUIRED_ARGUMENT, null, 'h'),
				new LongOpt("dbuser", LongOpt.REQUIRED_ARGUMENT, null, 'u'),
				new LongOpt("dbpass", LongOpt.REQUIRED_ARGUMENT, null, 'a'),
		});
		
		String type = null;
		String driver = null;
		String url = null;
		String port = null;
		String name = null;
		String host = null;
		String user = null;
		String pass = null;
		
		int c;
		while ((c = getopt.getopt()) != -1) {
			switch (c) {
			case 't':		type = getopt.getOptarg();		break;
			case 'd':		driver = getopt.getOptarg();	break;
			case 'r':		url = getopt.getOptarg();			break;
			case 'p':		port = getopt.getOptarg();		break;
			case 'n':		name = getopt.getOptarg();		break;
			case 'h':		host = getopt.getOptarg();		break;
			case 'u':		user = getopt.getOptarg();		break;
			case 'a':		pass = getopt.getOptarg();		break;
			default:
				throw new IllegalArgumentException("未法识别的数据库连接参数：" + args[getopt.getOptind() - 1] + "。\n"	+ USAGE_CREATECONNECTION);
			}
		}
		
		DBInfo info = null;
		if (type != null)
			info = checkDBInfoByType(type);
		else if (driver != null)
			info = checkDBInfoByDriver(driver);
		else 
			info = DBINFO_ORACLE;			
		
		if (url == null) {
			if (host == null)
				throw new IllegalArgumentException("必须提供数据库地址dbhost参数。\n" + USAGE_CREATECONNECTION);
			int nPort = info.port;
			if (port != null)
				nPort = Integer.parseInt(port);		
			if (name == null)
				name = info.name;
			url = info.createUrl(host, nPort, name);
		}
		
		if (user == null || pass == null)
			throw new IllegalArgumentException("必须提供数据库用户dbuser与密码dbpass参数。\n" + USAGE_CREATECONNECTION);
	
		return createConnection(info.driver, url, user, pass);
	}	
	
	private static DBInfo checkDBInfoByType(String type) {
		for (DBInfo info : DBINFOS) {
			if (info.type.equalsIgnoreCase(type))
				return info;
		}
		throw new IllegalArgumentException("未知的数据库类型：" + type);
	}
	
	private static DBInfo checkDBInfoByDriver(String driver) {
		for (DBInfo info : DBINFOS) {
			if (info.driver.equals(driver))
				return info;
		}
		throw new IllegalArgumentException("未知的数据库驱动：" + driver);
	}
	
	private static class DBInfo {
		private String type;
		private String driver;
		private String url;
		private int port;
		private String name;
		
		public DBInfo(String type, String driver, String url, int port, String name) {
			super();
			this.type = type;
			this.driver = driver;
			this.url = url;
			this.port = port;
			this.name = name;
		}

		public String createUrl(String host, int port, String name) {
			return String.format(url, host, port, name);
		}
	}

	/**
	 * 执行一个数据库查询，返回一个二维表格
	 * @param conn
	 * @param sql
	 * @return
	 */
	public static Table queryForTable(Connection conn, String sql, Object...params) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			return new Table(rs);			
		} catch (SQLException e) {
			throw ErrorUtil.createError(IllegalArgumentException.class, "SQL语句执行失败：" + sql, e);
		} finally {
			close(ps, rs);			
		}
	}
	
	/**
	 * 执行一个更新语句
	 * @param conn
	 * @param sql
	 * @param params
	 * @return 返回更新语句影响的记录数
	 */
	public static int execute(Connection conn, String sql, Object...params) {		
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			return ps.executeUpdate();					
		} catch (SQLException e) {
			throw ErrorUtil.createError(IllegalArgumentException.class, "SQL语句执行失败：" + sql, e);
		} finally {
			close(ps);			
		}
	}

	/**
	 * 关闭各种SQL执行资源
	 * @param objects
	 */
	public static void close(Object...objects) {
		try {
			for (Object obj : objects) {
				if (obj != null) {
					if (obj instanceof ResultSet)
						((ResultSet)obj).close();
					else if (obj instanceof PreparedStatement)
						((PreparedStatement)obj).close();
					else if (obj instanceof Connection)
						((Connection)obj).close();	
					else if (obj instanceof Statement)
						((Statement)obj).close();										
					else
						throw new IllegalArgumentException("无法识别的DB资源：" + obj);
				}
			}
		} catch (SQLException e) {
			throw ErrorUtil.createError(IllegalArgumentException.class, "释放DB资源失败", e);
		}
	}
}
