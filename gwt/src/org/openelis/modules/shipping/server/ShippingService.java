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
package org.openelis.modules.shipping.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.OrganizationContactDO;
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
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.ShippingRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class ShippingService implements
                            AppScreenFormServiceInt,
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
            fields.remove("itemsTable");
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
        TableModel trackingNumsTable = (TableModel)rpcSend.getField("trackingNumbersTable")
                                                          .getValue();
        trackingNumbers = getTrackingNumberListFromRPC(trackingNumsTable,
                                                       shippingDO.getId());

        // shipping items info
        TableModel shippingItemsTable = (TableModel)rpcSend.getField("itemsTable")
                                                           .getValue();
        shippingItems = getShippingItemsListFromRPC(shippingItemsTable,
                                                    shippingDO.getId());

        // validate the fields on the backend
        List exceptionList = remote.validateForAdd(shippingDO,
                                                   shippingItems,
                                                   trackingNumbers);

        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, shippingItemsTable, rpcSend);
            return rpcSend;
        }

        // send the changes to the database
        Integer shippingId;
        try {
            shippingId = (Integer)remote.updateShipment(shippingDO,
                                                        shippingItems,
                                                        trackingNumbers);
        } catch (Exception e) {
            exceptionList = new ArrayList();
            exceptionList.add(e);

            setRpcErrors(exceptionList, shippingItemsTable, rpcSend);

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
        TableModel trackingNumsTable = (TableModel)rpcSend.getField("trackingNumbersTable")
                                                          .getValue();
        trackingNumbers = getTrackingNumberListFromRPC(trackingNumsTable,
                                                       shippingDO.getId());

        // shipping items info
        TableModel shippingItemsTable = (TableModel)rpcSend.getField("itemsTable")
                                                           .getValue();
        shippingItems = getShippingItemsListFromRPC(shippingItemsTable,
                                                    shippingDO.getId());

        // validate the fields on the backend
        List exceptionList = remote.validateForUpdate(shippingDO,
                                                      shippingItems,
                                                      trackingNumbers);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, trackingNumsTable, rpcSend);

            return rpcSend;
        }

        // send the changes to the database
        try {
            remote.updateShipment(shippingDO, shippingItems, trackingNumbers);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());

            exceptionList = new ArrayList();
            exceptionList.add(e);

            setRpcErrors(exceptionList, trackingNumsTable, rpcSend);

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

        ShippingDO shippingDO = remote.getShipmentAndUnlock((Integer)key.getKey()
                                                                        .getValue());

        // set the fields in the RPC
        setFieldsInRPC(rpcReturn, shippingDO);

        // set tracking numbers in rpc
        loadTrackingNumbers(key, rpcReturn);
        
        // set shipping items in rpc
        loadShippingItems(key, rpcReturn);

        return rpcReturn;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        // remote interface to call the shipping bean
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");

        // get the shipping record
        ShippingDO shippingDO = remote.getShipment((Integer)key.getKey()
                                                               .getValue());

        // set the fields in the RPC
        setFieldsInRPC(rpcReturn, shippingDO);

        // get the shipment items
        loadShippingItems(key, rpcReturn);
        
        // get the tracking numbers
        loadTrackingNumbers(key, rpcReturn);

        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        // remote interface to call the shipping bean
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");

        ShippingDO shippingDO = new ShippingDO();
        try {
            shippingDO = remote.getShipmentAndLock((Integer)key.getKey()
                                                               .getValue());
        } catch (Exception e) {
            throw new RPCException(e.getMessage());
        }

        // set the fields in the RPC
        setFieldsInRPC(rpcReturn, shippingDO);

        // get the shipment items
        loadShippingItems(key, rpcReturn);
        
        // get the tracking numbers
        loadTrackingNumbers(key, rpcReturn);

        return rpcReturn;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/shipping.xsl");
    }

    public HashMap<String, DataObject> getXMLData() throws RPCException {
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

    public HashMap<String, DataObject> getXMLData(HashMap<String, DataObject> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public FormRPC loadTrackingNumbers(DataSet key, FormRPC rpcReturn) throws RPCException {
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        List trackingNumbersList = remote.getTrackingNumbers((Integer)key.getKey()
                                                             .getValue());
        TableModel trackingNumbersModel = (TableModel)rpcReturn.getFieldValue("trackingNumbersTable");

        try 
        {
            trackingNumbersModel.reset();
            
            for(int iter = 0;iter < trackingNumbersList.size();iter++) {
                ShippingTrackingDO trackingDO = (ShippingTrackingDO)trackingNumbersList.get(iter);
    
                   TableRow row = trackingNumbersModel.createRow();
                   NumberField id = new NumberField(trackingDO.getId());
                   
                    row.addHidden("id", id);
                    row.getColumn(0).setValue(trackingDO.getTrackingNumber());
                                        
                    trackingNumbersModel.addRow(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return rpcReturn;
    }
    
    public FormRPC loadShippingItems(DataSet key, FormRPC rpcReturn) throws RPCException {
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        List shippingItemsList = remote.getShippingItems((Integer)key.getKey().getValue());
        TableModel shippingItemsModel = (TableModel)rpcReturn.getFieldValue("itemsTable");

        try 
        {
            shippingItemsModel.reset();
            
            for(int iter = 0;iter < shippingItemsList.size();iter++) {
                ShippingItemDO itemDO = (ShippingItemDO)shippingItemsList.get(iter);
    
                   TableRow row = shippingItemsModel.createRow();
                   NumberField id = new NumberField(itemDO.getId());
                   NumberField referenceTableId = new NumberField(itemDO.getReferenceTableId());
                   NumberField referenceId = new NumberField(itemDO.getReferenceId());
                   
                    row.addHidden("id", id);
                    row.addHidden("referenceTableId", referenceTableId);
                    row.addHidden("referenceId", referenceId);
                    
                    row.getColumn(0).setValue(itemDO.getItemDescription());
                                        
                    shippingItemsModel.addRow(row);
           } 
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }       
        
        return rpcReturn;
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
            blankset.addObject(blankStringId);

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
            set.addObject(textObject);

            numberId.setValue(dropdownId);
            set.setKey(numberId);

            returnModel.add(set);

            i++;
        }

        return returnModel;
    }

    public ModelObject getAddAutoFillValues() throws Exception {
        ModelObject modelObj = new ModelObject();
        DataModel model = new DataModel();
        DataSet set = new DataSet();

        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        ShippingAddAutoFillDO autoDO;

        autoDO = remote.getAddAutoFillValues();

        DropDownField status = new DropDownField(autoDO.getStatus());
        DateField processedDate = new DateField(Datetime.YEAR,
                                                Datetime.DAY,
                                                DatetimeRPC.getInstance(Datetime.YEAR,
                                                                        Datetime.DAY,
                                                                        autoDO.getProcessedDate()
                                                                              .getDate()));
        StringField processedBy = new StringField(autoDO.getProcessedBy());
        NumberField systemUserId = new NumberField(autoDO.getSystemUserId());

        set.addObject(status);
        set.addObject(processedDate);
        set.addObject(processedBy);
        set.addObject(systemUserId);

        model.add(set);

        modelObj.setValue(model);

        return modelObj;
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

        shippingDO.setShippedFromId((Integer)rpcSend.getFieldValue(ShippingMeta.getShippedFromId()));
        shippingDO.setShippedMethodId((Integer)rpcSend.getFieldValue(ShippingMeta.getShippedMethodId()));
        shippingDO.setStatusId((Integer)rpcSend.getFieldValue(ShippingMeta.getStatusId()));

        // set shipped to values
        if (rpcSend.getField(ShippingMeta.ORGANIZATION_META.getName()) != null) {
            shippingDO.setShippedToId((Integer)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.getName()));
            shippingDO.setShippedTo((String)((DropDownField)rpcSend.getField(ShippingMeta.ORGANIZATION_META.getName())).getTextValue());
            shippingDO.addressDO.setMultipleUnit((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit()));
            shippingDO.addressDO.setStreetAddress((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress()));
            shippingDO.addressDO.setCity((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity()));
            shippingDO.addressDO.setState((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getState()));
            shippingDO.addressDO.setZipCode((String)rpcSend.getFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode()));
        }

        return shippingDO;
    }

    private List getShippingItemsListFromRPC(TableModel itemsTable,
                                             Integer shippingId) {
        List shippingItems = new ArrayList();

        for (int i = 0; i < itemsTable.numRows(); i++) {
            ShippingItemDO itemDO = new ShippingItemDO();
            TableRow row = itemsTable.getRow(i);

            NumberField itemId = (NumberField)row.getHidden("id");
            NumberField referenceId = (NumberField)row.getHidden("referenceId");
            NumberField referenceTableId = (NumberField)row.getHidden("referenceTableId");
            
            if(itemId != null)
                itemDO.setId((Integer)itemId.getValue());
            
            if(referenceId != null)
                itemDO.setReferenceId((Integer)referenceId.getValue());
            
            if(referenceTableId != null)
                itemDO.setReferenceTableId((Integer)referenceTableId.getValue());
            
            itemDO.setShippingId(shippingId);
            
            shippingItems.add(itemDO);
        }

        return shippingItems;
    }

    private List getTrackingNumberListFromRPC(TableModel trackingNumsTable,
                                              Integer shippingId) {
        List tackingNums = new ArrayList();

        for (int i = 0; i < trackingNumsTable.numRows(); i++) {
            ShippingTrackingDO trackingDO = new ShippingTrackingDO();
            TableRow row = trackingNumsTable.getRow(i);

            NumberField itemId = (NumberField)row.getHidden("id");
            StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
            if (deleteFlag == null) {
                trackingDO.setDelete(false);
            } else {
                trackingDO.setDelete("Y".equals(deleteFlag.getValue()));
            }

            // if the user created the row and clicked the remove button before commit...
            // we dont need to do anything with that row
            if (deleteFlag != null && "Y".equals(deleteFlag.getValue())
                && itemId == null) {
                // do nothing
            } else {
                if (itemId != null)
                    trackingDO.setId((Integer)itemId.getValue());
                trackingDO.setShippingId(shippingId);
                trackingDO.setTrackingNumber((String)row.getColumn(0)
                                                        .getValue());

                tackingNums.add(trackingDO);
            }
        }

        return tackingNums;
    }

    private void setRpcErrors(List exceptionList,
                              TableModel table,
                              FormRPC rpcSend) {
        // we need to get the keys and look them up in the resource bundle for internationalization
        for (int i = 0; i < exceptionList.size(); i++) {
            // if the error is inside the order items table
            if (exceptionList.get(i) instanceof TableFieldErrorException) {
                TableRow row = table.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                row.getColumn(table.getColumnIndexByFieldName(((TableFieldErrorException)exceptionList.get(i)).getFieldName()))
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                // if the error is on the field
            } else if (exceptionList.get(i) instanceof FieldErrorException)
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName())
                       .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            // if the error is on the entire form
            else if (exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }
        rpcSend.status = Status.invalid;
    }

    private void setFieldsInRPC(FormRPC rpcReturn, ShippingDO shippingDO) {
        // create dataset for ship to auto complete
        if (shippingDO.getShippedToId() == null)
            rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.getName(),
                                    null);
        else {
            DataSet shipToSet = new DataSet();
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER);
            StringObject text = new StringObject();
            id.setValue(shippingDO.getShippedToId());
            text.setValue(shippingDO.getShippedTo());
            shipToSet.setKey(id);
            shipToSet.addObject(text);
            rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.getName(),
                                    shipToSet);
        }

        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit(),
                                shippingDO.addressDO.getMultipleUnit());
        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress(),
                                shippingDO.addressDO.getStreetAddress());
        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity(),
                                shippingDO.addressDO.getCity());
        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getState(),
                                shippingDO.addressDO.getState());
        rpcReturn.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode(),
                                shippingDO.addressDO.getZipCode());

        rpcReturn.setFieldValue(ShippingMeta.getId(), shippingDO.getId());
        rpcReturn.setFieldValue(ShippingMeta.getCost(), shippingDO.getCost());
        rpcReturn.setFieldValue(ShippingMeta.getNumberOfPackages(),
                                Integer.valueOf(shippingDO.getNumberOfPackages()));
        rpcReturn.setFieldValue(ShippingMeta.getProcessedById(),
                                shippingDO.getProcessedBy());
        rpcReturn.setFieldValue("systemUserId", shippingDO.getProcessedById());
        if (shippingDO.getProcessedDate() != null && shippingDO.getProcessedDate()
                                                               .getDate() != null)
            rpcReturn.setFieldValue(ShippingMeta.getProcessedDate(),
                                    DatetimeRPC.getInstance(Datetime.YEAR,
                                                            Datetime.DAY,
                                                            shippingDO.getProcessedDate()
                                                                      .getDate()));

        if (shippingDO.getShippedDate() != null && shippingDO.getShippedDate()
                                                             .getDate() != null)
            rpcReturn.setFieldValue(ShippingMeta.getShippedDate(),
                                    DatetimeRPC.getInstance(Datetime.YEAR,
                                                            Datetime.DAY,
                                                            shippingDO.getShippedDate()
                                                                      .getDate()));
        rpcReturn.setFieldValue(ShippingMeta.getShippedFromId(),
                                shippingDO.getShippedFromId());
        rpcReturn.setFieldValue(ShippingMeta.getShippedMethodId(),
                                shippingDO.getShippedMethodId());
        rpcReturn.setFieldValue(ShippingMeta.getStatusId(),
                                shippingDO.getStatusId());

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
            data.addObject(nameObject);
            StringObject addressObject = new StringObject();
            addressObject.setValue(address);
            data.addObject(addressObject);
            StringObject cityObject = new StringObject();
            cityObject.setValue(city);
            data.addObject(cityObject);
            StringObject stateObject = new StringObject();
            stateObject.setValue(state);
            data.addObject(stateObject);

            // hidden fields
            StringObject aptSuiteObj = new StringObject();
            aptSuiteObj.setValue(aptSuite);
            data.addObject(aptSuiteObj);
            StringObject zipCodeObj = new StringObject();
            zipCodeObj.setValue(zipCode);
            data.addObject(zipCodeObj);

            // add the dataset to the datamodel
            dataModel.add(data);
        }

        return dataModel;
    }

}