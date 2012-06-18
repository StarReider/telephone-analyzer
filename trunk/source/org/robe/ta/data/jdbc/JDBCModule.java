package org.robe.ta.data.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.data.DataProvider;
import org.robe.ta.data.jpa.Telephone;

public class JDBCModule implements DataProvider
{
	private static final String DB_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final Log Log;
	private static final String EXIST_TEL_QUERY = "select 1 from telephones where telephone = ?";
	private static final String TABLE_NAME = "telephones";
	private static final String TEL_COL_NAME = "telephone";
	private static final String NAME_COL_NAME = "name";
	private static final String DESCRIPTION_COL_NAME = "description";

	private Connection conn;
	private PreparedStatement existsPS;
	
	static
	{
		Log = LogFactory.getLog(JDBCModule.class);
	}
	
	public JDBCModule(String jdbcURL) throws Exception 
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
			conn = DriverManager.getConnection(jdbcURL);
			conn.setAutoCommit(true);
		} 
		catch (Exception e) 
		{
			Log.error(e);
			throw e;
		}
		
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
	
	public void updateBean(Telephone telephone) throws Exception
	{
        PreparedStatement ps = conn.prepareStatement("UPDATE " 
        		+ TABLE_NAME 
        		+ " SET " 
        		+ TEL_COL_NAME 
        		+ " = ?, " 
        		+ NAME_COL_NAME 
        		+ " = ?, " 
        		+ DESCRIPTION_COL_NAME 
        		+ " = ? WHERE id = " 
        		+ telephone.getId());  

        ps.setBigDecimal(1, telephone.getTelephone() != null ? new BigDecimal(telephone.getTelephone()) : null); 
        ps.setString(2, telephone.getName());   
        ps.setString(3, telephone.getDescription());  
 
        ps.execute();
        ps.close();
	}
	
	@Override
	public void createEmptyBean(Telephone telephone) throws Exception
	{
		Statement st = conn.createStatement();
		st.execute("INSERT INTO telephones(telephone, name, description) VALUES(null, null, null)");   
        st.close();
	}

	private void createStatements() throws SQLException
	{		
		existsPS = conn.prepareStatement(EXIST_TEL_QUERY);
	}

	@Override
	public boolean isTelephoneExist(String tel) throws Exception
	{		
		existsPS.setString(1, tel);
		ResultSet rs = existsPS.executeQuery();
		
		return rs.next();
	}

	@Override
	public void close() throws Exception
	{		
		if(conn != null)
			conn.close();
	}

	@Override
	public List<Telephone> getAllBeans() throws Exception 
	{
		Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);  
        List<Telephone> beans = new ArrayList<Telephone>();
        
        while(res.next()) 
        {
        	BigDecimal telephoneNumber = res.getBigDecimal(TEL_COL_NAME);
        	String name = res.getString(NAME_COL_NAME);
        	String descriptiopn = res.getString(DESCRIPTION_COL_NAME);
        	int id = res.getInt("id");
        	
        	Telephone telephone = new Telephone();
        	telephone.setName(name);
        	telephone.setDescription(descriptiopn);
        	telephone.setTelephone(telephoneNumber != null ? telephoneNumber.toBigInteger() : null);
        	telephone.setId(id);
            
        	beans.add(telephone);
        }
        
        return beans;
	}
}