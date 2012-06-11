package org.robe.ta.data.jpa;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.robe.ta.data.DataFacade;

public class JPAModule implements DataFacade
{
	private EntityManager em;
	private EntityManagerFactory emf;
	
	public JPAModule(String jdbcURL) 
	{
		emf = Persistence.createEntityManagerFactory("telephones");
        em = emf.createEntityManager();
	}

	@Override
	public boolean isTelephoneExist(String tel) throws Exception 
	{
		return false;
	}

	@Override
	public void close() throws Exception 
	{
		
	}

	@Override
	public int updateTelephone(int id, BigInteger telephone, String name,
			String description) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void insertEmptyRow() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillArray(List<String[]> list) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
