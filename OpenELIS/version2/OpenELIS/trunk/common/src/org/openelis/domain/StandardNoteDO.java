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

public class StandardNoteDO  implements Serializable{

	private static final long serialVersionUID = -1135423604704945867L;
	
	Integer id;
	String name;
	String description;
	Integer type;
	String text;
	
	public StandardNoteDO(){
		
	}

	public StandardNoteDO(Integer id, String name, String description, Integer type, String text){
		setId(id);
		setName(name);
		setDescription(description);
		setType(type);
		setText(text);		
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = DataBaseUtil.trim(description);
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = DataBaseUtil.trim(name);
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = DataBaseUtil.trim(text);
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}

}
