package org.openelis.domain;

import java.io.Serializable;

public class InventoryItemDO implements Serializable{

	private static final long serialVersionUID = 1L;

	protected Integer id;
	protected String name;
	protected String description;
	protected Integer quantityMinLevel;
	protected Integer quantityMaxLevel;
	protected Integer quantityToReorder;
	protected Integer unitOfMeasure;
	protected String isReorderAuto;
	protected String isLotMaintained;
	protected String isActive;
	protected Integer aveLeadTime;
	protected double aveCost;
	protected Integer aveDailyUse;
	
	//FIXME fields not yet in database
	protected Integer store;
	protected String purchasedUnit;
	protected String dispensedUnit;
	protected String isBulk;
	protected String isNotForSale;
	protected String isSubAssembly;
	protected String isComponent;
	
	public InventoryItemDO(Integer id, String name, String description, Integer quantityMinLevel, Integer quantityMaxLevel,
							Integer quantityToReorder, Integer unitOfMeasure, String isReorderAuto, String isLotMaintained,
							String isActive, Integer aveLeadTime, double aveCost, Integer aveDailyUse){
		this.id = id;
		this.name = name;
		this.description = description;
		this.quantityMinLevel = quantityMinLevel;
		this.quantityMaxLevel = quantityMaxLevel;
		this.quantityToReorder = quantityToReorder;
		this.unitOfMeasure = unitOfMeasure;
		this.isReorderAuto = isReorderAuto;
		this.isLotMaintained = isLotMaintained;
		this.isActive = isActive;
		this.aveLeadTime = aveLeadTime;
		this.aveCost = aveCost;
		this.aveDailyUse = aveDailyUse;
	}
	
	//this will be the contructor when all the items get in the database
	public InventoryItemDO(Integer id, String name, String description, Integer quantityMinLevel, Integer quantityMaxLevel,
			Integer quantityToReorder, Integer unitOfMeasure, String isReorderAuto, String isLotMaintained, String isActive,
			Integer aveLeadTime, double aveCost, Integer aveDailyUse, Integer store, String purchasedUnit, String dispensedUnit,
			String isBulk, String isNotForSale, String isSubAssembly, String isComponent){
		this.id = id;
		this.name = name;
		this.description = description;
		this.quantityMinLevel = quantityMinLevel;
		this.quantityMaxLevel = quantityMaxLevel;
		this.quantityToReorder = quantityToReorder;
		this.unitOfMeasure = unitOfMeasure;
		this.isReorderAuto = isReorderAuto;
		this.isLotMaintained = isLotMaintained;
		this.isActive = isActive;
		this.aveLeadTime = aveLeadTime;
		this.aveCost = aveCost;
		this.aveDailyUse = aveDailyUse;
		this.store = store;
		this.purchasedUnit = purchasedUnit;
		this.dispensedUnit = dispensedUnit;
		this.isBulk = isBulk;
		this.isNotForSale = isNotForSale;
		this.isSubAssembly = isSubAssembly;
		this.isComponent = isComponent;
	}

	public double getAveCost() {
		return aveCost;
	}

	public void setAveCost(double aveCost) {
		this.aveCost = aveCost;
	}

	public Integer getAveDailyUse() {
		return aveDailyUse;
	}

	public void setAveDailyUse(Integer aveDailyUse) {
		this.aveDailyUse = aveDailyUse;
	}

	public Integer getAveLeadTime() {
		return aveLeadTime;
	}

	public void setAveLeadTime(Integer aveLeadTime) {
		this.aveLeadTime = aveLeadTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDispensedUnit() {
		return dispensedUnit;
	}

	public void setDispensedUnit(String dispensedUnit) {
		this.dispensedUnit = dispensedUnit;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIsBulk() {
		return isBulk;
	}

	public void setIsBulk(String isBulk) {
		this.isBulk = isBulk;
	}

	public String getIsComponent() {
		return isComponent;
	}

	public void setIsComponent(String isComponent) {
		this.isComponent = isComponent;
	}

	public String getIsLotMaintained() {
		return isLotMaintained;
	}

	public void setIsLotMaintained(String isLotMaintained) {
		this.isLotMaintained = isLotMaintained;
	}

	public String getIsNotForSale() {
		return isNotForSale;
	}

	public void setIsNotForSale(String isNotForSale) {
		this.isNotForSale = isNotForSale;
	}

	public String getIsReorderAuto() {
		return isReorderAuto;
	}

	public void setIsReorderAuto(String isReorderAuto) {
		this.isReorderAuto = isReorderAuto;
	}

	public String getIsSubAssembly() {
		return isSubAssembly;
	}

	public void setIsSubAssembly(String isSubAssembly) {
		this.isSubAssembly = isSubAssembly;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPurchasedUnit() {
		return purchasedUnit;
	}

	public void setPurchasedUnit(String purchasedUnit) {
		this.purchasedUnit = purchasedUnit;
	}

	public Integer getQuantityMaxLevel() {
		return quantityMaxLevel;
	}

	public void setQuantityMaxLevel(Integer quantityMaxLevel) {
		this.quantityMaxLevel = quantityMaxLevel;
	}

	public Integer getQuantityMinLevel() {
		return quantityMinLevel;
	}

	public void setQuantityMinLevel(Integer quantityMinLevel) {
		this.quantityMinLevel = quantityMinLevel;
	}

	public Integer getQuantityToReorder() {
		return quantityToReorder;
	}

	public void setQuantityToReorder(Integer quantityToReorder) {
		this.quantityToReorder = quantityToReorder;
	}

	public Integer getStore() {
		return store;
	}

	public void setStore(Integer store) {
		this.store = store;
	}

	public Integer getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(Integer unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}
}
