package org.jjwsoft.common.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.jjwsoft.common.error.ErrorUtil;

/**
 * 一个二维表，用于保存查询结果
 */
public class Table {
	private LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
	private Object[][] data;

	/**
	 * 从jdbc resultset中读取数据构建table
	 * @param rs
	 */
	public Table(ResultSet rs) {
		try {
			int columnCount = rs.getMetaData().getColumnCount();
			for (int i = 0; i < columnCount; i++) 
				columns.put(rs.getMetaData().getColumnName(i + 1).toLowerCase(), i);
			
			List<Object[]> result = new ArrayList<Object[]>(); 
			while (rs.next()) {
				Object[] row = new Object[columnCount];
				for (int i = 0; i < columnCount; i++) {
					row[i] = rs.getObject(i + 1);
				}
				result.add(row);
			}	
			data = (Object[][]) result.toArray(new Object[result.size()][]);
		} catch (SQLException e) {
			throw ErrorUtil.createError(IllegalArgumentException.class, "读取DB查询数据结果失败", e);
		}
	}
	
	/**
	 * 返回指定行指定列的数据
	 * @param row
	 * @param col
	 * @return
	 */
	public Object get(int row, int col) {
		return data[row][col];
	}
	
	/**
	 * 返回指定行指定列的数据
	 * @param row
	 * @param col
	 * @return
	 */
	public Object get(int row, String col) {
		return data[row][checkColumn(col)];
	}

	/**
	 * 返回数据行数
	 * @return
	 */
	public int getRowCount() {
		return data == null ? 0 : data.length;
	}
	
	/**
	 * 返回数据列数
	 * @return
	 */
	public int getColCount() {
		return columns.size();
	}
	
	private int checkColumn(String col) {
		Integer result = columns.get(col.toLowerCase());
		if (result == null)
			throw new IllegalArgumentException("不存在的数据列：" + col);
		return result;
	}
}
