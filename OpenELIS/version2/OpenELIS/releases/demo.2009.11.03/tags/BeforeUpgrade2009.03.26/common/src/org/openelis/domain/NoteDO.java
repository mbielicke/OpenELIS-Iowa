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
package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class NoteDO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8290691816263621343L;
	
	protected Integer id;
	protected Integer referenceId;  
	protected Integer referenceTable;   
	protected Datetime timestamp;    
	protected String isExternal;  
	protected Integer systemUser; 
	protected String subject; 
	protected String text;
	
	public NoteDO() {

    }

    public NoteDO(Integer id, Integer referenceId, Integer referenceTable, Date timestamp, String isExternal, Integer systemUser, String subject, String text) {
    	setId(id);
    	setReferenceId(referenceId);
    	setReferenceTable(referenceTable);
    	setTimestamp(new Datetime(Datetime.YEAR,Datetime.MINUTE,timestamp));
    	setIsExternal(isExternal);
    	setSystemUser(systemUser);
    	setSubject(subject);
    	setText(text);
    }
    
    public NoteDO(Integer id, Integer systemUser, String text, Date timestamp, String subject){
    	setId(id);
    	setTimestamp(new Datetime(Datetime.YEAR,Datetime.MINUTE,timestamp));
    	setSystemUser(systemUser);
    	setSubject(subject);
    	setText(text);
    }
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIsExternal() {
		return isExternal;
	}
	public void setIsExternal(String isExternal) {
		this.isExternal = DataBaseUtil.trim(isExternal);
	}
	public Integer getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}
	public Integer getReferenceTable() {
		return referenceTable;
	}
	public void setReferenceTable(Integer referenceTable) {
		this.referenceTable = referenceTable;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = DataBaseUtil.trim(subject);
	}
	public Integer getSystemUser() {
		return systemUser;
	}
	public void setSystemUser(Integer systemUser) {
		this.systemUser = systemUser;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = DataBaseUtil.trim(text);
	}
	public Datetime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Datetime timestamp) {
		this.timestamp = timestamp;
	} 

}
