package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.StandardNoteDO;

@Remote
public interface StandardNoteRemote {
	//commit a change to a standard note, or insert a new standard note
	public Integer updateStandardNote(StandardNoteDO standardNoteDO);
	
	//method to return a whole standard note
	public StandardNoteDO getStandardNote(Integer standardNoteId);
	
	//method to unlock and return a whole standard note
	public StandardNoteDO getStandardNoteAndUnlock(Integer standardNoteId);
	
	//method to lock and return a whole standard note
	public StandardNoteDO getStandardNoteAndLock(Integer standardNoteId) throws Exception;
	
	 //method to query for standard notes
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
	 public void deleteStandardNote(Integer standardNoteId) throws Exception;
}
