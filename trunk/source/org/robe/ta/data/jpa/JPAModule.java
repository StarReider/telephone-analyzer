package org.robe.ta.data.jpa;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.robe.ta.data.DataProvider;

public class JPAModule implements DataProvider
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
		Query query = em.createQuery("SELECT e FROM Telephone e WHERE e.telephone = ?1");
		query.setParameter(1, new BigDecimal(tel));
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
	public void updateTelephone(Telephone telephone) throws Exception 
	{
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		em.merge(telephone);
		transaction.commit();
	}

	@Override
	public void saveTelephone(Telephone telephone) throws Exception 
	{
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		em.persist(telephone);
		transaction.commit();
	}

	@Override
	public List<Telephone> getAllTelephones() throws Exception 
	{
		Query query = em.createQuery("SELECT e FROM Telephone e");
	    return query.getResultList();
	}

	@Override
	public String[] getAllOrganizations() throws Exception 
	{
		Query query = em.createQuery("SELECT distinct e.name FROM Telephone e ORDER BY e.name");
		List<String> orgs = query.getResultList();
		orgs.add("");
	    return (String[]) orgs.toArray(new String[orgs.size()]);
	}

	@Override
	public String getOrganization(String tel) throws Exception 
	{
		Query query = em.createQuery("SELECT e.name FROM Telephone e WHERE e.telephone = ?1");
		query.setParameter(1, new BigDecimal(tel));
	    String org = (String) query.getSingleResult();
		return org;
	}

	@Override
	public Telephone getTelephone(String tel) throws Exception 
	{
		Query query = em.createQuery("SELECT e FROM Telephone e WHERE e.telephone = ?1");
		query.setParameter(1, new BigDecimal(tel));
		List l = query.getResultList();
		return (Telephone) l.get(0);
	}
}