/**
 * @author Roman Bezugomonnov
 */
package org.robe.ta.data;

import java.util.List;

import org.robe.ta.data.jpa.Telephone;


public interface DataProvider 
{
	boolean isTelephoneExist(String tel)  throws Exception;
	
	void close() throws Exception;
	
	void updateTelephone(Telephone telephone) throws Exception;
	
	void saveTelephone(Telephone telephone) throws Exception;
	
	List<Telephone> getAllTelephones() throws Exception;
	
	Telephone getTelephone(String tel) throws Exception;
	
	String[] getAllOrganizations() throws Exception;
	
	String getOrganization(String orgName) throws Exception;
}