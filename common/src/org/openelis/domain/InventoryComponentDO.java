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

import org.openelis.utilcommon.DataBaseUtil;

public class InventoryComponentDO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer parentInventoryItemId;
    protected Integer componentInventoryItemId;
	protected Integer componentNameId;
	protected String componentName;
	protected String componentDesc;	
	protected Double quantity;
    
    protected boolean delete = false;
	
    public InventoryComponentDO(){
        
    }

	public InventoryComponentDO(Integer id, Integer parentInventoryItemId, Integer componentNameId, String componentName, String componentDesc, 
                                Double quantity, Integer componentInventoryItemId){
		setId(id);
		setParentInventoryItemId(parentInventoryItemId);
		setComponentNameId(componentNameId);
		setComponentName(componentName);
		setComponentDesc(componentDesc);
		setQuantity(quantity);
        setComponentInventoryItemId(componentInventoryItemId);
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

	public Integer getParentInventoryItemId() {
		return parentInventoryItemId;
	}

	public void setParentInventoryItemId(Integer parentInventoryItemId) {
		this.parentInventoryItemId = parentInventoryItemId;
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

    public Integer getComponentInventoryItemId() {
        return componentInventoryItemId;
    }

    public void setComponentInventoryItemId(Integer componentInventoryItemId) {
        this.componentInventoryItemId = componentInventoryItemId;
    }
}
