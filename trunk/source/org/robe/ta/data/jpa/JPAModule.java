package org.robe.ta.data.jpa;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

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
		Query query = em.createQuery("SELECT e FROM Telephone e WHERE e.telephone = " + new BigInteger(tel));
	    List<Telephone> beans = query.getResultList();
	    if(beans.size() > 0)
	    	return true;
	    else
	    	return false;
	}

	@Override
	public void close() throws Exception 
	{
		em.close();
		emf.close();
	}

	@Override
	public void updateBean(Telephone telephone) throws Exception 
	{
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		em.persist(telephone);
		transaction.commit();
	}

	@Override
	public void createEmptyBean(Telephone telephone) throws Exception 
	{
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		em.persist(telephone);
		transaction.commit();
	}

	@Override
	public List<Telephone> getAllBeans() throws Exception 
	{
		Query query = em.createQuery("SELECT e FROM Telephone e");
	    return query.getResultList();
	}
}