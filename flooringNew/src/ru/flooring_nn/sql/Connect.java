package ru.flooring_nn.sql;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class Connect {
	
	public static Connection getConnection() throws Exception {
		InitialContext cxt = new InitialContext();
		if ( cxt == null ) {
		   throw new Exception("no context!");
		}
//		DataSource ds = (DataSource) cxt.lookup("java:comp/env/jdbc/pstgr");
		DataSource ds = (DataSource) cxt.lookup("java:comp/env/jdbc/mysql");
		if ( ds == null ) {
			   throw new Exception("Data source not found!");
		}
		Connection conn=null;
		try {
			conn = ds.getConnection();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return conn;
	}

}
