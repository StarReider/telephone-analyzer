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
	
	void updateBean(Telephone telephone) throws Exception;
	
	void createEmptyBean(Telephone telephone) throws Exception;
	
	List<Telephone> getAllBeans() throws Exception;
	
	String[] getAllOrganizations() throws Exception;
}