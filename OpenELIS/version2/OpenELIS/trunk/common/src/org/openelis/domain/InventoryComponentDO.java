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
package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class InventoryComponentDO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer inventoryItemId;
	protected Integer componentNameId;
	protected String componentName;
	protected String componentDesc;	
	protected Double quantity;
    
    protected boolean delete = false;
	
    public InventoryComponentDO(){
        
    }
    
	public InventoryComponentDO(Integer id, Integer inventoryItemId, Integer componentNameId, String componentName, String componentDesc, Double quantity){
		setId(id);
		setInventoryItemId(inventoryItemId);
		setComponentNameId(componentNameId);
		setComponentName(componentName);
		setComponentDesc(componentDesc);
		setQuantity(quantity);
	}

	public String getComponentDesc() {
		return componentDesc;
	}

	public void setComponentDesc(String componentDesc) {
		this.componentDesc = DataBaseUtil.trim(componentDesc);
	}

	public Integer getComponentNameId() {
		return componentNameId;
	}

	public void setComponentNameId(Integer componentNameId) {
		this.componentNameId = componentNameId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(Integer inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = DataBaseUtil.trim(componentName);
	}

    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
