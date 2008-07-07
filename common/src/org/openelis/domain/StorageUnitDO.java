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

public class StorageUnitDO implements Serializable{

	private static final long serialVersionUID = -896897119790254351L;

	protected Integer id; 
	protected String category; 
	protected String description;  
	protected String isSingular; 
	 
	public StorageUnitDO() {

    }

    public StorageUnitDO(Integer id, String category, String description, String isSingular) {
        setId(id);
        setCategory(category);
        setDescription(description);
        setIsSingular(isSingular);
    }

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = DataBaseUtil.trim(category);
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

	public String getIsSingular() {
		return isSingular;
	}

	public void setIsSingular(String isSingular) {
		this.isSingular = DataBaseUtil.trim(isSingular);
	}
}
