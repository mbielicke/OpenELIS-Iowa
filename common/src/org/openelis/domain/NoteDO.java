package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

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
    	this.id = id;
    	this.referenceId = referenceId;
    	this.referenceTable = referenceTable;
    	this.timestamp = new Datetime(Datetime.YEAR,Datetime.MINUTE,timestamp);
    	this.isExternal = isExternal;
    	this.systemUser = systemUser;
    	this.subject = subject;
    	this.text = text;
    }
    
    public NoteDO(Integer id, Integer systemUser, String text, Date timestamp, String subject){
    	this.id = id;
    	this.timestamp = new Datetime(Datetime.YEAR,Datetime.MINUTE,timestamp);
    	this.systemUser = systemUser;
    	this.subject = subject;
    	this.text = text;
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
		this.isExternal = isExternal;
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
		this.subject = subject;
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
		this.text = text;
	}
	public Datetime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Datetime timestamp) {
		this.timestamp = new Datetime(Datetime.YEAR,Datetime.MINUTE,timestamp);
	} 

}
