/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.TestTrailerDO;

@Remote
public interface TestTrailerRemote {
	//commit a change to test trailer, or insert a new test trailer
	public Integer updateTestTrailer(TestTrailerDO testTrailerDO) throws Exception;
	
	//method to return a whole test trailer
	public TestTrailerDO getTestTrailer(Integer testTrailerId);
	
	//method to return a whole test trailer and lock it
	public TestTrailerDO getTestTrailerAndLock(Integer testTrailerId, String session) throws Exception;
	
	//method to return a whole test trailer and unlock it
	public TestTrailerDO getTestTrailerAndUnlock(Integer testTrailerId, String session);
	
	 //method to query for test trailers
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 public void deleteTestTrailer(Integer testTrailerId) throws Exception;
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForUpdate(TestTrailerDO testTrailerDO);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForAdd(TestTrailerDO testTrailerDO);
	 
	 //method to validate the fields before the backend deletes it
	 public List validateForDelete(Integer testTrailerId);
}