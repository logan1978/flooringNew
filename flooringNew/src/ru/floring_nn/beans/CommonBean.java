package ru.floring_nn.beans;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLPermission;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import ru.flooring_nn.sql.Connect;

public class CommonBean {
	
	public HashMap<String, Vector<HashMap<String, String>>> results = new HashMap<String, Vector<HashMap<String,String>>>();
	private HashMap<String, String> pars = new HashMap<String, String>();
	private int level = 0;
	private int page = 1;
	private final static int PAGE_SIZE = 16;
	
	public static int getPageSize() {
		return PAGE_SIZE;
	}
	
	public void setPars(HashMap<String, String> pars) {
		this.pars = pars;
	}
	
	public HashMap<String, String> getPars() {
		return pars;
	}

	public static Connection getConnection() throws Exception {
		return Connect.getConnection();
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel() {
		try {
			level = Integer.parseInt(pars.get("level"));
		} catch (Exception e) {
			level = 0;
			pars = new HashMap<String, String>();
		}
	}
	
	public void setPage() {
		try {
			page = Integer.parseInt(pars.get("page"));
		} catch (Exception e) {
			page = 1;
		}
	}
	
	public int getPage() {
		return page;
	}
	
	private void cleanResults() {
		results = new HashMap<String, Vector<HashMap<String,String>>>();
	}
	
	public String getNormPrefix(int num, String str) {
		int i = num % 10;
		if(i==1 && num!=11) {
			return str;
		} else {
			if(i >1 && i < 5 && (num < 5 || num > 20)) {
				return str+"а";
			}
				
		}
			return str+"ов";
	}
	
	public HashMap<String, Vector<HashMap<String, String>>> getResult() {
		cleanResults();
		HashMap<String, Vector<HashMap<String, String>>> result = new HashMap<String, Vector<HashMap<String, String>>>();
		Vector<VectorSQL> requestsVec = new Vector<VectorSQL>();
//		Vector<Object> result2 = new Vector<Object>();
		if("poisk".equals(pars.get("id"))) {
			Vector <Object> setPars = new Vector<Object>();
			StringBuilder sql = new StringBuilder();
				sql.append("select t2.* from flooring.catalog as t1\r\n"+
					"	inner JOIN flooring.catalog_decor_name as t2 on t1.ID_DEC=t2.ID\r\n");
				String poisk=null;
				try {
					poisk = "%"+new String(getPars().get("poisk").getBytes("ISO-8859-1"), "utf-8")+"%";
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(poisk!=null) { 
					sql.append("	where \r\n"+
					"	(LOWER(t2.DECOR) like LOWER(?) or LOWER(t2.DESCRIPTION) like LOWER(?)) \r\n");
					setPars.addElement(poisk);
					setPars.addElement(poisk);
				}
				if("1".equals(pars.get("level_parent"))) {
					sql.append("	and t1.id_sec = ? \r\n");
					String id_parent = pars.get("id_parent");
					setPars.addElement(id_parent);
				} else if("2".equals(pars.get("level_parent"))) {
					sql.append("	and t1.id_firm = ? \r\n");
					String id_parent = pars.get("id_parent");
					setPars.addElement(id_parent);
				} else if("3".equals(pars.get("level_parent"))) {
					sql.append("	and t1.id_coll = ? \r\n");
					String id_parent = pars.get("id_parent");
					setPars.addElement(id_parent);
				}
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars, PAGE_SIZE, getPage()));
		} else if(getLevel()==1) {
			Vector <Object> setPars = new Vector<Object>();
			StringBuilder sql = new StringBuilder("select distinct t2.*\r\n"+		
					"	from flooring.catalog as t1\r\n"+
					"	inner join flooring.catalog_firms_name as t2\r\n"+
					"	on t1.ID_SEC=t2.ID_SEC \r\n");
			String id = getPars().get("id");
			if(id!=null) { 
				sql.append("where t1.id_sec=?\r\n");
				setPars.addElement(id);
			}
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars, PAGE_SIZE, getPage()));
			sql =  new StringBuilder("select t1.SECTION, t1.ID ID_SECTION from flooring.catalog_sections_name as t1 \r\n");
			setPars = new Vector<Object>();
			if(id!=null) { 
				sql.append("where t1.id=?\r\n");
				setPars.addElement(id);
			}
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars));
		} else if(getLevel()==2) {
			Vector <Object> setPars = new Vector<Object>();
			StringBuilder sql = new StringBuilder("select distinct t2.*\r\n"+
					"	from flooring.catalog as t1\r\n"+
					"	inner join flooring.catalog_collections_name as t2\r\n"+
					"	on t1.ID_COLL=t2.ID and t1.ID_FIRM=t2.ID_FIRM\r\n");		
			String id = getPars().get("id");
			if(id!=null) { 
				sql.append("where t1.id_firm=?\r\n");
				setPars.addElement(id);
			}
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars, PAGE_SIZE, getPage()));
			sql =  new StringBuilder("select t1.FIRMA, t1.ID as ID_FIRMA, t2.SECTION, t2.ID as ID_SECTION from flooring.catalog_firms_name as t1\r\n"+ 
					"	inner join flooring.catalog_sections_name as t2 on t1.ID_SEC=t2.id\r\n");
			setPars = new Vector<Object>();
			if(id!=null) { 
				sql.append("where t1.id=?\r\n");
				setPars.addElement(id);
			}
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars));
		} else if(getLevel()==3) {
			Vector <Object> setPars = new Vector<Object>();
			StringBuilder sql = new StringBuilder("select distinct t2.*\r\n"+
					"	from flooring.catalog as t1\r\n"+
					"	inner join flooring.catalog_decor_name as t2\r\n"+
					"	on t1.ID_DEC=t2.ID and t1.ID_COLL=t2.ID_COLL\r\n");		
			String id = getPars().get("id");
			if(id!=null) { 
				sql.append("where t1.id_coll=?\r\n");
				setPars.addElement(id);
			}
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars, PAGE_SIZE, getPage()));
			sql =  new StringBuilder("select t1.COLLECTION, t1.ID as ID_COLLECTION, t2.FIRMA, t2.ID as ID_FIRMA, \r\n"
					+ "	t3.SECTION, t3.ID as ID_SECTION from flooring.catalog_collections_name t1\r\n"+
					"	inner join flooring.catalog_firms_name as t2 on t1.ID_FIRM=t2.ID\r\n"+
					"	inner join flooring.catalog_sections_name as t3 on t2.ID_SEC=t3.id\r\n");
			setPars = new Vector<Object>();
			if(id!=null) { 
				sql.append("where t1.id=?\r\n");
				setPars.addElement(id);
			}
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars));
		} else if(getLevel()==4) {
			Vector <Object> setPars = new Vector<Object>();
			StringBuilder sql = new StringBuilder("select distinct t2.*\r\n"+
					"	from flooring.catalog as t1\r\n"+
					"	inner join flooring.catalog_decor_name as t2\r\n"+
					"	on t1.ID_DEC=t2.ID and t1.ID_COLL=t2.ID_COLL\r\n");		
			String id = getPars().get("id");
			if(id!=null) { 
				sql.append("where t1.id_dec=?\r\n");
				setPars.addElement(id);
			}
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars, PAGE_SIZE, getPage()));
			sql =  new StringBuilder("select t1.COLLECTION, t1.ID as ID_COLLECTION, t2.FIRMA, t2.ID as ID_FIRMA, \r\n"
					+ "	t3.SECTION, t3.ID as ID_SECTION from flooring.catalog_decor_name as t0\r\n"
					+ "	inner join flooring.catalog_collections_name t1 on t0.id_coll= t1.id\r\n"+
					"	inner join flooring.catalog_firms_name as t2 on t1.ID_FIRM=t2.ID\r\n"+
					"	inner join flooring.catalog_sections_name as t3 on t2.ID_SEC=t3.id\r\n");
			setPars = new Vector<Object>();
			if(id!=null) { 
				sql.append("where t0.id=?\r\n");
				setPars.addElement(id);
			}
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars));
		} else {
			Vector <Object> setPars = new Vector<Object>();
			StringBuilder sql = new StringBuilder("select * from flooring.catalog_sections_name");
			requestsVec.addElement(new VectorSQL(sql.toString(), setPars));
		}
		A resClass = new A();
		long timerStart = new Date().getTime();
		result = resClass.getResultSet(requestsVec);
		long timerEnd = new Date().getTime();
		System.out.println("TimerCommon: "+(timerEnd-timerStart)+" msec");
/*		
		String div = "<div id='new'><table><tr><td style='border: 1px'>test</td><td style='border: 1px'>тест</td></tr></table></div>";
		String ins_sql = "insert into flooring.pages (NAME, BODY) VALUES ('test2', ?) ";
		
		sql = "select body from flooring.pages where name='test_div'";

		try {
			Connection conn = getConnection();
//			PreparedStatement stmt = conn.prepareStatement(ins_sql);
//			ByteArrayInputStream bais = new ByteArrayInputStream(div.getBytes());
//			stmt.setObject(1, div.getBytes());
//			stmt.execute();
			byte[] buff = new byte[4096];
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			div = "";
			ResultSet rs = stmt.executeQuery();
			int cols = rs.getMetaData().getColumnCount(); 
			while (rs.next()) {
				for(int i=1; i<=cols; i++) {
					Object body = rs.getObject(i);
					result2.addElement((String) body.toString());
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		return result;
		
	}
	
	public final static int MIN_LENGTH = 0;

	public static String getString(byte [] gzip, int size) throws IOException {
//		int size = gzip.length;
		if (size > MIN_LENGTH) {
			ByteArrayInputStream bais = new ByteArrayInputStream(gzip);
			DataInputStream inStream = new DataInputStream(new BufferedInputStream(bais));
			StringBuffer buffer = new StringBuffer(size);
			while(inStream.available()!=0){
				try {
					buffer.append(inStream.readChar());
				} catch (EOFException e) { 
					break;
				}
			}
/*			DataInputStream stream = new DataInputStream(new GZIPInputStream(bais));   
			StringBuffer buffer = new StringBuffer(size);
			for (int index = 0; buffer.toString().indexOf("</report>")==-1; index++) {
				buffer.append(stream.readChar());
			}
*/			
			return buffer.toString();
		} else {
			return gzip.toString();
		}
	}
	

	
	private class A implements Runnable {
		private String sql;
		private Vector<Object> setPars;
		private int page = 1;
		private int maxRows;
		
		public A () {
			
		}
		
		public A (String sql, Vector<Object> setPars, int page, int maxRows) {
			this.sql = sql;
			this.setPars = setPars;
			this.page = page;
			this.maxRows = maxRows;
		}
		
		private HashMap<String, Vector<HashMap<String, String>>> getResultSet(Vector<VectorSQL> sqlVector) {

			for(VectorSQL req : sqlVector) {
				int index = sqlVector.indexOf(req);
				String sql = req.getSql();
				Vector<Object> setPars = req.getSetPars();
				int page = this.page;
				try {
					page = req.getPage();
				} catch(Exception e) {}

				int maxRows = PAGE_SIZE;
				try {
					maxRows = req.getMaxRows();
				} catch(Exception e) {}
				
				A myThread = new A(sql, setPars, page, maxRows);
		        new Thread(myThread, "Request_"+index).start();
				
			}
			while(true) {
				if(results!=null && results.size()==sqlVector.size())
					return results;
			}
		}
		
		private Vector<HashMap<String, String>> getResultSetOld(Vector<String> sqlVector, Vector<Object> sqlParams) {
			
			Vector<HashMap<String, String>> result = new Vector<HashMap<String, String>>();
			PreparedStatement stmt = null;
			Connection con = null;
			try {
				con = getConnection();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			if(con!=null) {
				for(String sql : sqlVector) {
					Vector<Object> setPars = (Vector<Object>) sqlParams.elementAt(sqlVector.indexOf(sql));
					try {
						stmt = con.prepareStatement(sql.toString());
						for(int i=0; i<setPars.size(); i++) {
							stmt.setObject(i+1, setPars.elementAt(i));
						}
						ResultSet rs = stmt.executeQuery();
						int cols = rs.getMetaData().getColumnCount();
						int row_num = 0;
						while (rs.next()) {
							if((row_num>=(getPage() - 1) * PAGE_SIZE) && (row_num < getPage() * PAGE_SIZE)) {
								HashMap<String, String> column = new HashMap<String, String>();
								for(int i=1; i<=cols; i++) {
									String name = rs.getMetaData().getColumnName(i);
									String val = rs.getString(i);
									column.put(name, val);
								}
								result.addElement(column);
							} else if (row_num >= getPage() * PAGE_SIZE) {
								break;
							}
							row_num++;
						}
						rs.last();
						HashMap<String, String> rows = new HashMap<String, String>();
	//					int pages = (int) Math.ceil(rs.getRow() / (PAGE_SIZE*1.0));
						
						rows.put("ROWS", String.valueOf(rs.getRow()));
						result.addElement(rows);
						stmt.close();
						con.close();
						rs.close();
					} catch (Exception e) {
						try {
							if(con !=null && !con.isClosed())
								con.close();
						} catch (SQLException e1) {
								// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							if(stmt!=null && !stmt.isClosed())
								stmt.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return result;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long timerStart1 = new Date().getTime();

			Thread thr = Thread.currentThread();
			String thrName = thr.getName();
			Vector<HashMap<String, String>> result = new Vector<HashMap<String, String>>();

//			System.out.println(thrName+"\r\nSQL: "+this.sql+"\r\nPARAMS: "+this.setPars);

			PreparedStatement stmt = null;
			Connection con = null;
			try {
				con = getConnection();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			if(con!=null) {
				long timerStart2 = new Date().getTime();
				
				try {
					stmt = con.prepareStatement(sql.toString());
					for(int i=0; i<setPars.size(); i++) {
						stmt.setObject(i+1, setPars.elementAt(i));
					}
					ResultSet rs = stmt.executeQuery();

					long timerEnd2 = new Date().getTime();
//					System.out.println("TimerThreadRequest["+thrName+"]: "+(timerEnd2-timerStart2)+" msec");

					int cols = rs.getMetaData().getColumnCount();
					int row_num = 0;
					while (rs.next()) {
						if((row_num>=(page - 1) * maxRows) && (row_num < page * maxRows)) {
							HashMap<String, String> column = new HashMap<String, String>();
							for(int i=1; i<=cols; i++) {
								String name = rs.getMetaData().getColumnLabel(i);
								String val = rs.getString(i);

								column.put(name, val);
							}
							result.addElement(column);
//							addResult(column);
						} else if (row_num >= page * maxRows) {
							break;
						}
						row_num++;
					}
					rs.last();
					HashMap<String, String> rows = new HashMap<String, String>();
//					int pages = (int) Math.ceil(rs.getRow() / (PAGE_SIZE*1.0));
					
					rows.put("ROWS", String.valueOf(rs.getRow()));
					result.addElement(rows);

					synchronized (results) {
						results.put(thrName, result);
					}
					
//					addResult(thrName, result);
					stmt.close();
					con.close();
					rs.close();
					long timerEnd1 = new Date().getTime();
//					System.out.println("TimerThread["+thrName+"]: "+(timerEnd1-timerStart1)+" msec\r\n-----------------------------------------------");
					
				} catch (Exception e) {
					try {
						if(con !=null && !con.isClosed())
							con.close();
					} catch (SQLException e1) {
							// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						if(stmt!=null && !stmt.isClosed())
							stmt.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
	}
}
