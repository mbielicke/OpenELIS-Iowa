package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

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
		setId(id);
		setName(name);
		setIsActive(isActive);
		setAnalyteGroup(analyteGroup);
		setParentAnalyteId(parentAnalyteId);
		setParentAnalyte(parentAnalyte);
		setExternalId(externalId);		
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
		this.externalId = DataBaseUtil.trim(externalId);
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
		this.isActive = DataBaseUtil.trim(isActive);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = DataBaseUtil.trim(name);
	}
	public String getParentAnalyte() {
		return parentAnalyte;
	}
	public void setParentAnalyte(String parentAnalyte) {
		this.parentAnalyte = DataBaseUtil.trim(parentAnalyte);
	}
	public Integer getParentAnalyteId() {
		return parentAnalyteId;
	}
	public void setParentAnalyteId(Integer parentAnalyteId) {
		this.parentAnalyteId = parentAnalyteId;
	}
	
	
}
