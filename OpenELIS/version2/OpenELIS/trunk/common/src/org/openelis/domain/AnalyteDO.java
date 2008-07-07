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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
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
