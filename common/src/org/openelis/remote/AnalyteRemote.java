/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.AnalyteDO;

@Remote
public interface AnalyteRemote {
	//commit a change to analyte, or insert a new analyte
	public Integer updateAnalyte(AnalyteDO analyteDO) throws Exception;
	
	//method to return a whole analyte
	public AnalyteDO getAnalyte(Integer analyteId);
	
	//method to return a whole analyte and lock it
	public AnalyteDO getAnalyteAndLock(Integer analyteId, String session) throws Exception;
	
	//method to return a whole analyte and unlock it
	public AnalyteDO getAnalyteAndUnlock(Integer analyteId, String session);
	
	 //method to query for analytes
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
	 //auto complete lookup
	 public List autoCompleteLookupByName(String name, int maxResults);
	 
	 public void deleteAnalyte(Integer analyteId) throws Exception;
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForUpdate(AnalyteDO analyteDO);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForAdd(AnalyteDO analyteDO);
	 
	 //method to validate the fields before the backend deletes it
	 public List validateForDelete(Integer analyteId);
}
