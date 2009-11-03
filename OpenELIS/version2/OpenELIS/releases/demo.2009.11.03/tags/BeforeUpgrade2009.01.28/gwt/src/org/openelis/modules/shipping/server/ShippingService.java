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
package org.openelis.modules.shipping.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.ShippingAddAutoFillDO;
import org.openelis.domain.ShippingDO;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingTrackingDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.OrderRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.ShippingRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class ShippingService implements
                            AppScreenFormServiceInt<FormRPC, DataSet, DataModel>,
                            AutoCompleteServiceInt {

    private UTFResource                  openElisConstants    = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                                            .getAttribute("locale"));

    private static final ShippingMetaMap ShippingMeta         = new ShippingMetaMap();

    private static final int             leftTableRowsPerPage = 20;

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List shippingIds;

        if (rpcSend == null) {

            FormRPC rpc = (FormRPC)SessionManager.getSession()
                                                 .getAttribute("ShippingQuery");

            if (rpc == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            HashMap<String, AbstractField> fields = rpc.getFieldMap();
            fields.remove("trackingNumbersTable");

            ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
            try {
                shippingIds = remote.query(fields,
                                           (model.getPage() * leftTableRowsPerPage),
                                           leftTableRowsPerPage + 1);
            } catch (Exception e) {
                if (e instanceof LastPageException) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                } else {
                    throw new RPCException(e.getMessage());
                }
            }
        } else {
            ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
            HashMap<String, AbstractField> fields = rpcSend.getFieldMap();
            fields.remove("itemsTable");
            fields.remove("trackingNumbersTable");

            try {
                shippingIds = remote.query(fields, 0, leftTableRowsPerPage);

            } catch (Exception e) {
                throw new RPCException(e.getMessage());
            }

            // need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("ShippingQuery", rpcSend);
        }

        // fill the model with the query results
        int i = 0;
        if (model == null)
            model = new DataModel();
        else
            model.clear();
        while (i < shippingIds.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)shippingIds.get(i);

            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());

            row.setKey(id);
            model.add(row);
            i++;
        }

        return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // remote interface to call the shipping bean
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        ShippingDO shippingDO = new ShippingDO();
        List trackingNumbers = new ArrayList();
        List shippingItems = new ArrayList();

        // build the shippingDO from the form
        shippingDO = getShippingDOFromRPC(rpcSend);

        // tracking numbers info
        DataModel trackingNumsTable = (DataModel)((FormRPC)rpcSend.getField("shippingItems")).getField("trackingNumbersTable").getValue();
        trackingNumbers = getTrackingNumberListFromRPC(trackingNumsTable, shippingDO.getId());

        // shipping items info
        DataModel shippingItemsTable = (DataModel)((FormRPC)rpcSend.getField("shippingItems")).getField("itemsTable").getValue();
        shippingItems = getShippingItemsListFromRPC(shippingItemsTable, shippingDO.getId());

        // validate the fields on the backend
        List exceptionList = remote.validateForAdd(shippingDO,
                                                   shippingItems,
                                                   trackingNumbers);

        if (exceptionList.size() > 0) {
            //TODO setRpcErrors(exceptionList, shippingItemsTable, rpcSend);
            return rpcSend;
        }
        
        DataModel model = (DataModel)rpcSend.getFieldValue("unlockModel");

        // send the changes to the database
        Integer shippingId;
        try {
            shippingId = (Integer)remote.updateShipment(shippingDO,
                                                        shippingItems,
                                                        trackingNumbers, model);
        } catch (Exception e) {
            exceptionList = new ArrayList();
            exceptionList.add(e);

            //TODO setRpcErrors(exceptionList, shippingItemsTable, rpcSend);

            return rpcSend;
        }

        // lookup the changes from the database and build the rpc
        shippingDO.setId(shippingId);

        // set the fields in the RPC
        setFieldsInRPC(rpcReturn, shippingDO);

        return rpcReturn;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // remote interface to call the shipping bean
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        ShippingDO shippingDO = new ShippingDO();
        List trackingNumbers = new ArrayList();
        List shippingItems = new ArrayList();

        // build the shippingDO from the form
        shippingDO = getShippingDOFromRPC(rpcSend);

        // tracking numbers info
        DataModel trackingNumsTable = (DataModel)((FormRPC)rpcSend.getField("shippingItems")).getField("trackingNumbersTable").getValue();
        trackingNumbers = getTrackingNumberListFromRPC(trackingNumsTable, shippingDO.getId());

        // shipping items info
        DataModel shippingItemsTable = (DataModel)((FormRPC)rpcSend.getField("shippingItems")).getField("itemsTable").getValue();
        shippingItems = getShippingItemsListFromRPC(shippingItemsTable, shippingDO.getId());

        // validate the fields on the backend
        List exceptionList = remote.validateForUpdate(shippingDO, shippingItems, trackingNumbers);
        
        if (exceptionList.size() > 0) {
            //TODO setRpcErrors(exceptionList, trackingNumsTable, rpcSend);

            return rpcSend;
        }

        // send the changes to the database
        try {
            remote.updateShipment(shippingDO, shippingItems, trackingNumbers, null);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());

            exceptionList = new ArrayList();
            exceptionList.add(e);

            //TODO setRpcErrors(exceptionList, trackingNumsTable, rpcSend);

            return rpcSend;
        }

        // set the fields in the RPC
        setFieldsInRPC(rpcReturn, shippingDO);

        return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        // remote interface to call the shipping bean
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        DataModel model = (DataModel)rpcReturn.getFieldValue("unlockModel");
        
        ShippingDO shippingDO = remote.getShipmentAndUnlock((Integer)((DataObject)key.getKey()).getValue(), model);

        // set the fields in the RPC
        setFieldsInRPC(rpcReturn, shippingDO);

        if(((FormRPC)rpcReturn.getField("shippingItems")).load){
            FormRPC itemsRpc = (FormRPC)rpcReturn.getField("shippingItems");
            loadShippingItems(key, itemsRpc);
        }
        
        if(((FormRPC)rpcReturn.getField("orderShippingNotes")).load){
            FormRPC notesRpc = (FormRPC)rpcReturn.getField("orderShippingNotes");
            loadOrderShippingNotes(key, notesRpc);
        }

        return rpcReturn;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        // remote interface to call the shipping bean
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");

        // get the shipping record
        ShippingDO shippingDO = remote.getShipment((Integer)((DataObject)key.getKey()).getValue());

        // set the fields in the RPC
        setFieldsInRPC(rpcReturn, shippingDO);

        if(((FormRPC)rpcReturn.getField("shippingItems")).load){
            FormRPC itemsRpc = (FormRPC)rpcReturn.getField("shippingItems");
            loadShippingItems(key, itemsRpc);
        }
        
        if(((FormRPC)rpcReturn.getField("orderShippingNotes")).load){
            FormRPC notesRpc = (FormRPC)rpcReturn.getField("orderShippingNotes");
            loadOrderShippingNotes(key, notesRpc);
        }

        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        // remote interface to call the shipping bean
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");

        ShippingDO shippingDO = new ShippingDO();
        try {
            shippingDO = remote.getShipmentAndLock((Integer)((DataObject)key.getKey()).getValue());
        } catch (Exception e) {
            throw new RPCException(e.getMessage());
        }

        // set the fields in the RPC
        setFieldsInRPC(rpcReturn, shippingDO);

        if(((FormRPC)rpcReturn.getField("shippingItems")).load){
            FormRPC itemsRpc = (FormRPC)rpcReturn.getField("shippingItems");
            loadShippingItems(key, itemsRpc);
        }
        
        if(((FormRPC)rpcReturn.getField("orderShippingNotes")).load){
            FormRPC notesRpc = (FormRPC)rpcReturn.getField("orderShippingNotes");
            loadOrderShippingNotes(key, notesRpc);
        }

        return rpcReturn;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/shipping.xsl");
    }

    public HashMap<String, Data> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/shipping.xsl"));

        DataModel shipFromDropdownField = (DataModel)CachingManager.getElement("InitialData",
                                                                               "shipFromDropdown");
        DataModel statusDropdownField = (DataModel)CachingManager.getElement("InitialData",
                                                                             "shippingStatusDropdown");
        DataModel shippingMethodDropdownField = (DataModel)CachingManager.getElement("InitialData",
                                                                                     "shippingMethodDropdown");

        // status dropdown
        if (statusDropdownField == null) {
            statusDropdownField = getInitialModel("status");
            CachingManager.putElement("InitialData",
                                      "shippingStatusDropdown",
                                      statusDropdownField);
        }
        // ship from dropdown
        if (shipFromDropdownField == null) {
            shipFromDropdownField = getInitialModel("shipFrom");
            CachingManager.putElement("InitialData",
                                      "shipFromDropdown",
                                      shipFromDropdownField);
        }
        // ship method dropdown
        if (shippingMethodDropdownField == null) {
            shippingMethodDropdownField = getInitialModel("shippingMethod");
            CachingManager.putElement("InitialData",
                                      "shippingMethodDropdown",
                                      shippingMethodDropdownField);
        }

        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("status", statusDropdownField);
        map.put("shipFrom", shipFromDropdownField);
        map.put("shippingMethod", shippingMethodDropdownField);

        return map;
    }

    public HashMap<String, Data> getXMLData(HashMap<String, Data> args) throws RPCException {
        return null;
    }
    
    public FormRPC loadShippingItems(DataSet key, FormRPC rpcReturn) throws RPCException {
        getShippingItemsModel((NumberObject)key.getKey(), (TableField)rpcReturn.getField("itemsTable"));        
        getTrackingNumbersModel((NumberObject)key.getKey(), (TableField)rpcReturn.getField("trackingNumbersTable"));
        
        rpcReturn.load = true;
        return rpcReturn;
    }
    
    public FormRPC loadOrderShippingNotes(DataSet key, FormRPC rpcReturn) throws RPCException {
        rpcReturn.setFieldValue(ShippingMeta.ORDER_SHIPPING_NOTE_META.getText(), getOrderShippingNotesValue((NumberObject)key.getKey()));
        
        rpcReturn.load = true;
        return rpcReturn;
    }
    public void getShippingItemsModel(NumberObject key, TableField shippingItemsTable) throws RPCException {
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        List shippingItemsList = remote.getShippingItems((Integer)key.getValue());
        DataModel shippingItemsModel = (DataModel)shippingItemsTable.getValue();

        shippingItemsModel.clear();
        
        for(int iter = 0;iter < shippingItemsList.size();iter++) {
            ShippingItemDO itemDO = (ShippingItemDO)shippingItemsList.get(iter);

           DataSet row = shippingItemsModel.createNewSet();
           NumberObject id = new NumberObject(itemDO.getId());
           NumberObject referenceTableId = new NumberObject(itemDO.getReferenceTableId());
           NumberObject referenceId = new NumberObject(itemDO.getReferenceId());
           
            row.setKey(id);
            DataMap map = new DataMap();
            map.put("referenceTableId", referenceTableId);
            map.put("referenceId", referenceId);
            row.setData(map);
            
            row.get(0).setValue(itemDO.getQuantity());
            row.get(1).setValue(itemDO.getItemDescription());
                                
            shippingItemsModel.add(row);
       } 
    }
    
    public void getTrackingNumbersModel(NumberObject key, TableField trackingTable) throws RPCException {
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        List trackingNumbersList = remote.getTrackingNumbers((Integer)key.getValue());
        DataModel trackingNumbersModel = (DataModel)trackingTable.getValue();

        trackingNumbersModel.clear();
            
        for(int iter = 0;iter < trackingNumbersList.size();iter++) {
            ShippingTrackingDO trackingDO = (ShippingTrackingDO)trackingNumbersList.get(iter);

           DataSet row = trackingNumbersModel.createNewSet();
           NumberObject id = new NumberObject(trackingDO.getId());
           
           row.setKey(id);
           row.get(0).setValue(trackingDO.getTrackingNumber());
                                
           trackingNumbersModel.add(row);
       } 
    }
    
    public String getOrderShippingNotesValue(NumberObject key) throws RPCException {
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteDO noteDO = remote.getOrderShippingNote((Integer)key.getValue());
        
        if(noteDO != null)
            return noteDO.getText();
        
        return null;
    }
    
    public StringObject unlockOrderRecords(DataModel unlockList){
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        
        remote.unlockOrders(unlockList);
        
        return new StringObject("");
    }
    
    public DataModel getInitialModel(String cat) {
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");

        if (cat.equals("status"))
            id = remote.getCategoryId("shippingStatus");
        else if (cat.equals("shipFrom"))
            id = remote.getCategoryId("shipFrom");
        else if (cat.equals("shippingMethod"))
            id = remote.getCategoryId("shippingMethod");

        List entries = new ArrayList();
        if (id != null)
            entries = remote.getDropdownValues(id);

        // we need to build the model to return
        DataModel returnModel = new DataModel();

        if (entries.size() > 0) {
            // create a blank entry to begin the list
            DataSet blankset = new DataSet();

            StringObject blankStringId = new StringObject();
            NumberObject blankNumberId = new NumberObject(NumberObject.Type.INTEGER);

            blankStringId.setValue("");
            blankset.add(blankStringId);

            blankNumberId.setValue(new Integer(0));
            blankset.setKey(blankNumberId);

            returnModel.add(blankset);
        }

        int i = 0;
        while (i < entries.size()) {
            DataSet set = new DataSet();
            IdNameDO resultDO = (IdNameDO)entries.get(i);
            // id
            Integer dropdownId = resultDO.getId();
            // entry
            String dropdownText = resultDO.getName();

            StringObject textObject = new StringObject();
            NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);

            textObject.setValue(dropdownText);
            set.add(textObject);

            numberId.setValue(dropdownId);
            set.setKey(numberId);

            returnModel.add(set);

            i++;
        }

        return returnModel;
    }

    public DataModel getAddAutoFillValues() throws Exception {
        DataModel model = new DataModel();
        DataSet set = new DataSet();

        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        ShippingAddAutoFillDO autoDO;

        autoDO = remote.getAddAutoFillValues();

        DropDownField status = new DropDownField(new DataSet(new NumberObject(autoDO.getStatus())));
        DateField processedDate = new DateField(Datetime.YEAR,
                                                Datetime.DAY,
                                                DatetimeRPC.getInstance(Datetime.YEAR,
                                                                        Datetime.DAY,
                                                                        autoDO.getProcessedDate()
                                                                              .getDate()));
        StringField processedBy = new StringField(autoDO.getProcessedBy());
        NumberField systemUserId = new NumberField(autoDO.getSystemUserId());

        set.add(status);
        set.add(processedDate);
        set.add(processedBy);
        set.add(systemUserId);

        model.add(set);

        return model;
    }

    private ShippingDO getShippingDOFromRPC(FormRPC rpcSend) {
        ShippingDO shippingDO = new ShippingDO();

        shippingDO.setId((Integer)rpcSend.getFieldValue(ShippingMeta.getId()));

        if (rpcSend.getFieldValue(ShippingMeta.getCost()) != null)
            shippingDO.setCost(((Double)rpcSend.getFieldValue(ShippingMeta.getCost())).doubleValue());

        if (rpcSend.getFieldValue(ShippingMeta.getNumberOfPackages()) != null)
            shippingDO.setNumberOfPackages((Integer)rpcSend.getFieldValue(ShippingMeta.getNumberOfPackages()));

        shippingDO.setProcessedById((Integer)rpcSend.getFieldValue("systemUserId"));

        if (rpcSend.getFieldValue(ShippingMeta.getProcessedDate()) != null)
            shippingDO.setProcessedDate(((DatetimeRPC)rpcSend.getFieldValue(ShippingMeta.getProcessedDate())).getDate());

        if (rpcSend.getFieldValue(ShippingMeta.getShippedDate()) != null)
            shippingDO.setShippedDate(((DatetimeRPC)rpcSend.getFieldValue(ShippingMeta.getShippedDate())).getDate());

        shippingDO.setShippedFromId((Integer)((DropDownField)rpcSend.getField(ShippingMeta.getShippedFromId())).getSelectedKey());
        shippingDO.setShippedMethodId((Integer)((DropDownField)rpcSend.getField(ShippingMeta.getShippedMethodId())).getSelectedKey());
        shippingDO.setStatusId((Integer)((DropDownField)rpcSend.getField(ShippingMeta.getStatusId())).getSelectedKey());

        // set shipped to values
        if (rpcSend.getField(ShippingMeta.ORGANIZATION_META.getName()) != null) {
            shippingDO.setShippedToId((Integer)((DropDownField)rpcSend.getField(ShippingMeta.ORGANIZATION_META.getName())).getSelectedKey());
            shippingDO.setShippedTo((String)((DropDownField)rpcSend.getField(ShippingMeta.ORGANIZATION_META.getName())).getTextValue());
            shippingDO.addressDO.setMultipleUnit((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit()));
            shippingDO.addressDO.setStreetAddress((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress()));
            shippingDO.addressDO.setCity((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity()));
            shippingDO.addressDO.setState((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getState()));
            shippingDO.addressDO.setZipCode((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode()));
        }

        return shippingDO;
    }

    private List getShippingItemsListFromRPC(DataModel itemsTable, Integer shippingId) {
        List shippingItems = new ArrayList();

        for (int i = 0; i < itemsTable.size(); i++) {
            ShippingItemDO itemDO = new ShippingItemDO();
            DataSet row = itemsTable.get(i);

            NumberObject itemId = (NumberObject)row.getKey();
            
            DataMap map = (DataMap)row.getData();
            NumberObject referenceId = (NumberObject)map.get("referenceId");
            NumberObject referenceTableId = (NumberObject)map.get("referenceTableId");
            
            if(itemId != null)
                itemDO.setId((Integer)itemId.getValue());
            
            if(referenceId != null)
                itemDO.setReferenceId((Integer)referenceId.getValue());
            
            if(referenceTableId != null)
                itemDO.setReferenceTableId((Integer)referenceTableId.getValue());
            
            itemDO.setQuantity((Integer)row.get(0).getValue());
            itemDO.setShippingId(shippingId);
            
            shippingItems.add(itemDO);
        }

        return shippingItems;
    }

    private List getTrackingNumberListFromRPC(DataModel trackingNumsTable, Integer shippingId) {
        List tackingNums = new ArrayList();
        List deletedRows = trackingNumsTable.getDeletions();

        for (int i = 0; i < trackingNumsTable.size(); i++) {
            ShippingTrackingDO trackingDO = new ShippingTrackingDO();
            DataSet row = trackingNumsTable.get(i);

            NumberObject itemId = (NumberObject)row.getKey();

            if (itemId != null)
                trackingDO.setId((Integer)itemId.getValue());
            
            trackingDO.setShippingId(shippingId);
            trackingDO.setTrackingNumber((String)row.get(0).getValue());

            tackingNums.add(trackingDO);
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet deletedRow = (DataSet)deletedRows.get(j);
            if(deletedRow.getKey() != null){
                ShippingTrackingDO trackingDO = new ShippingTrackingDO();
                trackingDO.setDelete(true);
                trackingDO.setId((Integer)((NumberField)deletedRow.getKey()).getValue());
            }
        }

        return tackingNums;
    }

    private void setRpcErrors(List exceptionList, TableField table, FormRPC rpcSend) {
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                table.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));

            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }        
        
        rpcSend.status = Status.invalid;
    }

    private void setFieldsInRPC(FormRPC rpcReturn, ShippingDO shippingDO) {
        // create dataset for ship to auto complete
        if (shippingDO.getShippedToId() == null)
            rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.getName(), null);
        else {
            DataModel model = new DataModel();
            model.add(new NumberObject(shippingDO.getShippedToId()),new StringObject(shippingDO.getShippedTo()));
            ((DropDownField)rpcReturn.getField(ShippingMeta.ORGANIZATION_META.getName())).setModel(model);
            rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.getName(), model.get(0));
        }

        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit(), shippingDO.addressDO.getMultipleUnit());
        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress(), shippingDO.addressDO.getStreetAddress());
        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity(), shippingDO.addressDO.getCity());
        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getState(), shippingDO.addressDO.getState());
        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode(), shippingDO.addressDO.getZipCode());

        rpcReturn.setFieldValue(ShippingMeta.getId(), shippingDO.getId());
        rpcReturn.setFieldValue(ShippingMeta.getCost(), shippingDO.getCost());
        rpcReturn.setFieldValue(ShippingMeta.getNumberOfPackages(), Integer.valueOf(shippingDO.getNumberOfPackages()));
        rpcReturn.setFieldValue(ShippingMeta.getProcessedById(), shippingDO.getProcessedBy());
        rpcReturn.setFieldValue("systemUserId", shippingDO.getProcessedById());
        if (shippingDO.getProcessedDate() != null && shippingDO.getProcessedDate().getDate() != null)
            rpcReturn.setFieldValue(ShippingMeta.getProcessedDate(), 
                                    DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, shippingDO.getProcessedDate().getDate()));

        if (shippingDO.getShippedDate() != null && shippingDO.getShippedDate().getDate() != null)
            rpcReturn.setFieldValue(ShippingMeta.getShippedDate(),
                                    DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, shippingDO.getShippedDate().getDate()));
        if(shippingDO.getShippedFromId() != null)
            rpcReturn.setFieldValue(ShippingMeta.getShippedFromId(), new DataSet(new NumberObject(shippingDO.getShippedFromId())));
        if(shippingDO.getShippedMethodId() != null)
            rpcReturn.setFieldValue(ShippingMeta.getShippedMethodId(), new DataSet(new NumberObject(shippingDO.getShippedMethodId())));
        if(shippingDO.getStatusId() != null)
            rpcReturn.setFieldValue(ShippingMeta.getStatusId(), new DataSet(new NumberObject(shippingDO.getStatusId())));

    }

    public DataModel getMatches(String cat,
                                DataModel model,
                                String match,
                                HashMap params) throws RPCException {
        if ("shippedTo".equals(cat))
            return getShippedToMatches(match);

        return null;
    }

    private DataModel getShippedToMatches(String match) {
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;

        try {
            int id = Integer.parseInt(match); // this will throw an exception if it isnt an id
            // lookup by id...should only bring back 1 result
            autoCompleteList = remote.autoCompleteLookupById(id);

        } catch (NumberFormatException e) {
            // it isnt an id
            // lookup by name
            autoCompleteList = remote.autoCompleteLookupByName(match + "%", 10);
        }

        for (int i = 0; i < autoCompleteList.size(); i++) {
            OrganizationAutoDO resultDO = (OrganizationAutoDO)autoCompleteList.get(i);
            // org id
            Integer orgId = resultDO.getId();
            // org name
            String name = resultDO.getName();
            // org apt suite #
            String aptSuite = resultDO.getAptSuite();
            // org street address
            String address = resultDO.getAddress();
            // org city
            String city = resultDO.getCity();
            // org state
            String state = resultDO.getState();
            // org zipcode
            String zipCode = resultDO.getZipCode();

            DataSet data = new DataSet();
            // hidden id
            NumberObject idObject = new NumberObject(NumberObject.Type.INTEGER);
            idObject.setValue(orgId);
            data.setKey(idObject);
            // columns
            StringObject nameObject = new StringObject();
            nameObject.setValue(name);
            data.add(nameObject);
            StringObject addressObject = new StringObject();
            addressObject.setValue(address);
            data.add(addressObject);
            StringObject cityObject = new StringObject();
            cityObject.setValue(city);
            data.add(cityObject);
            StringObject stateObject = new StringObject();
            stateObject.setValue(state);
            data.add(stateObject);

            DataMap map = new DataMap();
            // hidden fields
            StringObject aptSuiteObj = new StringObject();
            aptSuiteObj.setValue(aptSuite);
            map.put("aptSuite", aptSuiteObj);
            StringObject zipCodeObj = new StringObject();
            zipCodeObj.setValue(zipCode);
            map.put("zipCode", zipCodeObj);
            data.setData(map);

            // add the dataset to the datamodel
            dataModel.add(data);
        }

        return dataModel;
    }

    class ShippingItemsHidden{
        public Integer referenceTableId;
        public Integer referenceId;
    }
}