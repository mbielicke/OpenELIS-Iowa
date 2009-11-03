package org.openelis.modules.shipping.client;

import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;

public class ShippingDataService {
    Integer shipFromId;
    Integer shipToId;
    String shipToText;   
    String multUnitText;
    String streetAddressText;
    String cityText;
    String stateText;
    String zipCodeText;
    TableDataModel<TableDataRow<Integer>> itemsShippedModel = new TableDataModel<TableDataRow<Integer>>();
    
    public TableDataModel<TableDataRow<Integer>> getItemsShippedModel() {
        return itemsShippedModel;
    }
    public void setItemsShippedModel(TableDataModel<TableDataRow<Integer>> itemsShippedModel) {
        this.itemsShippedModel = itemsShippedModel;
    }
    public String getCityText() {
        return cityText;
    }
    public void setCityText(String cityText) {
        this.cityText = cityText;
    }
    public String getMultUnitText() {
        return multUnitText;
    }
    public void setMultUnitText(String multUnitText) {
        this.multUnitText = multUnitText;
    }
    public Integer getShipFromId() {
        return shipFromId;
    }
    public void setShipFromId(Integer shipFromId) {
        this.shipFromId = shipFromId;
    }
    public Integer getShipToId() {
        return shipToId;
    }
    public void setShipToId(Integer shipToId) {
        this.shipToId = shipToId;
    }
    public String getShipToText() {
        return shipToText;
    }
    public void setShipToText(String shipToText) {
        this.shipToText = shipToText;
    }
    public String getStateText() {
        return stateText;
    }
    public void setStateText(String stateText) {
        this.stateText = stateText;
    }
    public String getStreetAddressText() {
        return streetAddressText;
    }
    public void setStreetAddressText(String streetAddressText) {
        this.streetAddressText = streetAddressText;
    }
    public String getZipCodeText() {
        return zipCodeText;
    }
    public void setZipCodeText(String zipCodeText) {
        this.zipCodeText = zipCodeText;
    }
}
