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

import org.openelis.domain.StandardNoteDO;

@Remote
public interface StandardNoteRemote {
	//commit a change to a standard note, or insert a new standard note
	public Integer updateStandardNote(StandardNoteDO standardNoteDO) throws Exception;
	
	//method to return a whole standard note
	public StandardNoteDO getStandardNote(Integer standardNoteId);
	
	//method to unlock and return a whole standard note
	public StandardNoteDO getStandardNoteAndUnlock(Integer standardNoteId);
	
	//method to lock and return a whole standard note
	public StandardNoteDO getStandardNoteAndLock(Integer standardNoteId) throws Exception;
	
    //method to return a whole standard note by type
    public List getStandardNoteByType(HashMap fields) throws Exception;
	
	 //method to query for standard notes
	 public List query(HashMap fields, int first, int max) throws Exception;
     
     //method to query for standard notes
     public List queryForType(HashMap fields) throws Exception;
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
	 public void deleteStandardNote(Integer standardNoteId) throws Exception;
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForUpdate(StandardNoteDO standardNoteDO);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForAdd(StandardNoteDO standardNoteDO);
	 
	 //method to validate the fields before the backend deletes it
	 public List validateForDelete(Integer standardNoteId);
}
