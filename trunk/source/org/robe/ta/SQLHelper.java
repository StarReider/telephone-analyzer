package org.robe.ta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SQLHelper 
{
	private static final String JDBC_URL;
	private static final String DB_DRIVER_NAME;
	private static final Log Log;
	private static final String EXIST_TEL_QUERY = "select 1 from telephones where telephone = ?";
	private static final String TABLE_NAME = "telephones";
	private static final String TEL_COL_NAME = "telephone";

	private Connection conn;
	private PreparedStatement existsPS;
	private int numberOfColumns;
	
	public int getNumberOfColumns() 
	{
		return numberOfColumns;
	}

	private String[] AttributeNames;
	
	static
	{
		JDBC_URL = "jdbc:derby:exampledb;create=true";
		DB_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
		Log = LogFactory.getLog("org.robe.ta.SQLHelper");
	}
	
	public SQLHelper() throws ClassNotFoundException, SQLException 
	{
		try 
		{
			Class.forName(DB_DRIVER_NAME);
		} 
		catch (Exception e) 
		{
			Log.error(e);
			throw e;
		}
		
		
		try 
		{
			conn = DriverManager.getConnection(JDBC_URL);
		} 
		catch (Exception e) 
		{
			Log.error(e);
			throw e;
		}
		
		getMetaInfo();
		
		try 
		{
			createStatements();
		} 
		catch (Exception e) 
		{
			Log.error(e);
			throw e;
		}  
	}
	
	public int updateTelephone(int id, BigInteger telephone, String name, String description) throws SQLException
	{
        PreparedStatement ps = conn.prepareStatement("UPDATE telephones SET telephone = ?, name = ?, description = ? WHERE id = " + id);  
	    int count;
        
        ps.setBigDecimal(1, telephone != null ? new BigDecimal(telephone) : null);  
        ps.setString(2, name);   
        ps.setString(3, description);  

        count = ps.executeUpdate();   
        ps.close();
        
        return count;
	}
	
	public void insertEmptyRow() throws SQLException
	{
		Statement st = conn.createStatement();
		st.execute("INSERT INTO telephones(telephone, name, description) VALUES(null, null, null)");   
        st.close();
	}
	
	public String getTableName(int index)
	{
        return (AttributeNames[index]);
	}
			
	private void getMetaInfo() throws SQLException 
	{
		Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);
        ResultSetMetaData RSMD = res.getMetaData();
        numberOfColumns = RSMD.getColumnCount();
        AttributeNames = new String[numberOfColumns];
        
        for(int i=0;i < numberOfColumns; i++)
            AttributeNames[i] = RSMD.getColumnName(i+1);
        
        stmt.close();
	}
	
	public void fillArray(List<String[]> list) throws SQLException
	{
		Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);  
        
        while(res.next()) 
        {
        	String[] rowData = new String[numberOfColumns];
        	for(int C=1; C<=numberOfColumns;C++)
            {
        		rowData[C-1] = res.getString(C);
        	}
            list.add(rowData);
        }
	}

	private void createStatements() throws SQLException
	{		
		existsPS = conn.prepareStatement(EXIST_TEL_QUERY);
	}

	public Connection getConn() 
	{
		return conn;
	}

	public boolean isTelephoneExist(String tel) throws SQLException
	{		
		existsPS.setString(1, tel);
		ResultSet rs = existsPS.executeQuery();
		
		return rs.next();
	}

	public void close() throws SQLException
	{		
		if(conn != null)
			conn.close();
	}
}