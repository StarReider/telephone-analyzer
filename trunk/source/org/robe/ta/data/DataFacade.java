/**
 * @author Roman Bezugomonnov
 */
package org.robe.ta.data;

import java.math.BigInteger;
import java.util.List;

import org.robe.ta.Telephone;


public interface DataFacade 
{
	boolean isTelephoneExist(String tel)  throws Exception;
	
	void close() throws Exception;
	
	int updateTelephone(int id, BigInteger telephone, String name, String description) throws Exception;
	
	void insertEmptyRow() throws Exception;
	
	List<Telephone> getAllTelephones() throws Exception;
	
	Telephone getTelephone(int id);
}