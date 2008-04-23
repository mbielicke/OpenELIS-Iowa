package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.TestTrailerDO;

@Remote
public interface TestTrailerRemote {
	//commit a change to test trailer, or insert a new test trailer
	public Integer updateTestTrailer(TestTrailerDO testTrailerDO);
	
	//method to return a whole test trailer
	public TestTrailerDO getTestTrailer(Integer testTrailerId);
	
	//method to return a whole test trailer and lock it
	public TestTrailerDO getTestTrailerAndLock(Integer testTrailerId) throws Exception;
	
	//method to return a whole test trailer and unlock it
	public TestTrailerDO getTestTrailerAndUnlock(Integer testTrailerId);
	
	 //method to query for test trailers
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
	 public void deleteTestTrailer(Integer testTrailerId) throws Exception;
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForUpdate(TestTrailerDO testTrailerDO);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForAdd(TestTrailerDO testTrailerDO);
	 
	 //method to validate the fields before the backend deletes it
	 public List validateForDelete(Integer testTrailerId);
}
