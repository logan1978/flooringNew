package ru.flooring_nn.beans;

import java.util.Vector;

public class VectorSQL {
	private String sql;
	private Vector<Object> setPars = new Vector<Object>();
	private int maxRows = Integer.MAX_VALUE;
	private int page = 1;
	
	public VectorSQL(String sql, Vector<Object> setPars) {
		// TODO Auto-generated constructor stub
		this.sql = sql;
		this.setPars = setPars;
	}

	public VectorSQL(String sql, Vector<Object> setPars, int maxRows, int page) {
		// TODO Auto-generated constructor stub
		this.sql = sql;
		this.setPars = setPars;
		this.maxRows = maxRows;
		this.page = page;
	}

	public String getSql() {
		return sql;
	}
	
	public Vector<Object> getSetPars() {
		return setPars;
	}
	
	public int getPage() {
		return page;
	}
	
	public int getMaxRows() {
		return maxRows;
	}
}
