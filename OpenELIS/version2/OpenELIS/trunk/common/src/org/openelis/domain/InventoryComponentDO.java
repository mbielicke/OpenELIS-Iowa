package org.openelis.domain;

import java.io.Serializable;

public class InventoryComponentDO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer inventoryItemId;
	protected Integer componentId;
	protected String componentName;
	protected String componentDesc;
	
	protected double quantity;
	
	public InventoryComponentDO(Integer id, Integer inventoryItemId, Integer componentId, String componentName, String componentDesc, double quantity){
		this.id = id;
		this.inventoryItemId = inventoryItemId;
		this.componentId = componentId;
		this.componentName = componentName;
		this.componentDesc = componentDesc;
		this.quantity = quantity;
	}

	public String getComponentDesc() {
		return componentDesc;
	}

	public void setComponentDesc(String componentDesc) {
		this.componentDesc = componentDesc;
	}

	public Integer getComponentId() {
		return componentId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
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

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
}
