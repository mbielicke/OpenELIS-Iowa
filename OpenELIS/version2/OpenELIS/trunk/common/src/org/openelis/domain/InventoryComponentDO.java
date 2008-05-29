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
