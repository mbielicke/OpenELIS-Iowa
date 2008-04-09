package org.openelis.domain;

import java.io.Serializable;

public class AnalyteDO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected String name;
	protected String isActive;
	protected Integer analyteGroup;
	protected Integer parentAnalyteId;
	protected String parentAnalyte;
	protected String externalId;
	
	public AnalyteDO(){
		
	}

	public AnalyteDO(Integer id, String name, String isActive, Integer analyteGroup, Integer parentAnalyteId, String parentAnalyte,
						String externalId){
		this.id = id;
		this.name = name;
		this.isActive = isActive;
		this.analyteGroup = analyteGroup;
		this.parentAnalyteId = parentAnalyteId;
		this.parentAnalyte = parentAnalyte;
		this.externalId = externalId;		
	}
	
	public Integer getAnalyteGroup() {
		return analyteGroup;
	}
	public void setAnalyteGroup(Integer analyteGroup) {
		this.analyteGroup = analyteGroup;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentAnalyte() {
		return parentAnalyte;
	}
	public void setParentAnalyte(String parentAnalyte) {
		this.parentAnalyte = parentAnalyte;
	}
	public Integer getParentAnalyteId() {
		return parentAnalyteId;
	}
	public void setParentAnalyteId(Integer parentAnalyteId) {
		this.parentAnalyteId = parentAnalyteId;
	}
	
	
}
