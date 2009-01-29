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
package org.openelis.modules.fillOrder.server;

import org.openelis.domain.FillOrderDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrderItemDO;
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.FillOrderMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.FillOrderRemote;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.remote.OrderRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FillOrderService implements AppScreenFormServiceInt<RPC, DataModel<DataSet>>, AutoCompleteServiceInt{

    private static final FillOrderMetaMap FillOrderMeta = new FillOrderMetaMap();
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final int leftTableRowsPerPage = 250;
    
    public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> model) throws RPCException {
        List orders;
        //if the rpc is null then we need to get the page
        if(form == null){

            form = (Form)SessionManager.getSession().getAttribute("FillOrderQuery");
    
            if(form == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
            try{
                orders = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        
            HashMap<String,AbstractField> fields = form.getFieldMap();
            fields.remove("fillItemsTable");
            fields.remove("process");
            fields.remove("daysLeft");
            
            if(isQueryEmpty(form))
                throw new QueryException(openElisConstants.getString("emptyQueryException"));
           
            try{    
                orders = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("FillOrderQuery", form);
        }
        
        if(model == null)
            model = new DataModel<DataSet>();
        
        //fill the model with the query results
        fillModelFromQuery(model, orders);        
 
        return model;
    }

    public DataModel<DataSet> commitQueryAndUnlock(DataModel<DataSet> model) throws RPCException {
        List orders;
        Form form = (Form)SessionManager.getSession().getAttribute("FillOrderQuery");

        if(form == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        try{
            orders = remote.queryAndUnlock(form.getFieldMap(), model, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }    
        
        if(model == null)
            model = new DataModel<DataSet>();
        
        //fill the model with the query results
        fillModelFromQuery(model, orders);
 
        return model;
    }

    //if we make it to this method we know we are handling internal orders
    public RPC commitAdd(RPC rpc) throws RPCException {
       
        
        
        TreeDataModel model = (TreeDataModel)rpc.form.getFieldValue("orderItemsTree");
        List orderList = getListOfOrdersFromTree(model);
        
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        
        try{
        remote.updateInternalOrders(orderList);
        
        }catch(Exception e){
            
            
            return rpc;
        }
        
        
        return rpc;
    }

    public RPC commitUpdate(RPC rpc) throws RPCException {
        return null;
    }

    public RPC commitDelete(RPC rpc) throws RPCException {
        return null;
    }

    public RPC abort(RPC rpc) throws RPCException {
        return null;
    }

    public RPC fetch(RPC rpc) throws RPCException {
        return null;
    }

    public RPC fetchForUpdate(RPC rpcReturn) throws RPCException {
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/fillOrder.xsl");
    }

    public HashMap<String, Data> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/fillOrder.xsl"));
        
        DataModel shipFromDropdownField = (DataModel)CachingManager.getElement("InitialData", "shipFromDropdown");
        DataModel costCenterDropdownField = (DataModel)CachingManager.getElement("InitialData", "orderCostCenterDropdown");
        DataModel statusDropdownField = (DataModel)CachingManager.getElement("InitialData", "orderStatusDropdown");
        NumberObject orderItemReferenceTableId = (NumberObject)CachingManager.getElement("InitialData", "orderItemReferenceTableId");
        
        //status dropdown
        if(statusDropdownField == null){
            statusDropdownField = getInitialModel("status");
            CachingManager.putElement("InitialData", "orderStatusDropdown", statusDropdownField);
        }
        //ship from dropdown
        if(shipFromDropdownField == null){
            shipFromDropdownField = getInitialModel("shipFrom");
            CachingManager.putElement("InitialData", "shipFromDropdown", shipFromDropdownField);
        }

        //cost center type dropdown
        if(costCenterDropdownField == null){
            costCenterDropdownField = getInitialModel("costCenter");
            CachingManager.putElement("InitialData", "costCenterDropdown", costCenterDropdownField);
        }
        //reference table id
        if(orderItemReferenceTableId == null){
            orderItemReferenceTableId = getOrderItemRefTable();
            CachingManager.putElement("InitialData", "orderItemReferenceTableId", getOrderItemRefTable());
        }
        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer pendingValue = null;
        try{
            pendingValue = remote.getEntryIdForSystemName("order_status_pending");    
        }catch(Exception e){}
        
        NumberObject pendingValueObj = new NumberObject(pendingValue);
        
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("status", statusDropdownField);
        map.put("costCenter", costCenterDropdownField);
        map.put("shipFrom", shipFromDropdownField);
        map.put("pendingValue", pendingValueObj);
        map.put("orderItemReferenceTableId", orderItemReferenceTableId);
        
        return map;
    }
    
    public RPC getScreen(RPC rpc) {
        return rpc;
    }
    
    public List getListOfOrdersFromTree(TreeDataModel model){
        ArrayList orderList = new ArrayList();
        
        for(int i=0; i<model.size(); i++){
            
            for(int j=0; j<model.get(i).getItems().size(); j++){
                TreeDataItem childItem = (TreeDataItem)model.get(i).get(j);
                
                FillOrderDO orderDO = new FillOrderDO();
                orderDO.setOrderItemId((Integer)((NumberObject)childItem.getKey()).getValue());
                orderDO.setOrderId((Integer)childItem.get(1).getValue());
                orderDO.setInventoryLocationId((Integer)((DropDownField)childItem.get(3)).getSelectedKey());
                orderDO.setQuantity((Integer)childItem.get(0).getValue());
            }
        }
        
        return orderList;
    }
    
    public DataModel getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        
        if(cat.equals("status"))
            id = remote.getCategoryId("order_status");
        else if(cat.equals("shipFrom"))
            id = remote.getCategoryId("shipFrom");
        else if(cat.equals("costCenter"))
            id = remote.getCategoryId("cost_centers");
        
        List entries = new ArrayList();
        if(id != null)
            entries = remote.getDropdownValues(id);
        
        //we need to build the model to return
        DataModel returnModel = new DataModel();

        if(entries.size() > 0){ 
            //create a blank entry to begin the list
            DataSet blankset = new DataSet();
            
            StringObject blankStringId = new StringObject();
            NumberObject blankNumberId = new NumberObject(NumberObject.Type.INTEGER);
            
            blankStringId.setValue("");
            blankset.add(blankStringId);
            
            blankNumberId.setValue(new Integer(0));
            blankset.setKey(blankNumberId);
            
            returnModel.add(blankset);
        }
        
        int i=0;
        while(i < entries.size()){
            DataSet set = new DataSet();
            IdNameDO resultDO = (IdNameDO) entries.get(i);
            //id
            Integer dropdownId = resultDO.getId();
            //entry
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

    public HashMap<String, Data> getXMLData(HashMap<String, Data> args) throws RPCException {
        return null;
    }

    public DataModel getMatchesObj(StringObject cat, DataModel model, StringObject match, DataMap params) throws RPCException {
        return getMatches((String)cat.getValue(), model, (String)match.getValue(), params);
        
    }
    
    public DataModel getMatches(String cat, DataModel model, String match, HashMap<String, Data> params) throws RPCException {
        if(cat.equals("invLocation"))
            return getLocationMatches(match, params);
        
        return null;
    }
    
    private DataModel getLocationMatches(String match, HashMap params) throws RPCException{
        DataModel dataModel = new DataModel();
        Integer invItemId = null;
        List autoCompleteList = new ArrayList();
        
        invItemId = (Integer)((NumberObject)params.get("id")).getValue();
        
        if(invItemId != null){
            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
            //lookup by name, inv id
            autoCompleteList = remote.autoCompleteLocationLookupByNameInvId(match+"%", invItemId, 10);
        }
        
        for(int i=0; i < autoCompleteList.size(); i++){
            StorageLocationAutoDO resultDO = (StorageLocationAutoDO) autoCompleteList.get(i);
            //id
            Integer id = resultDO.getId();
            //desc
            String desc = resultDO.getLocation();
            //qty on hand
            Integer qty = resultDO.getQtyOnHand();
          
            DataSet set = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(id);
            set.setKey(idObject);
            //columns
            StringObject descObject = new StringObject(desc);
            set.add(descObject);
            NumberObject qtyObject = new NumberObject(qty);
            set.add(qtyObject);
            
            //add the dataset to the datamodel
            dataModel.add(set);                            

        }       
        
        return dataModel;       
    }
    
    public static int daysBetween(Date startDate, Date endDate) {
        return (int)((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));  
    }  
    
    private boolean isQueryEmpty(Form form){
   
        return ("".equals(form.getFieldValue(FillOrderMeta.getId())) &&
                    form.getFieldValue(FillOrderMeta.getStatusId()) == null && 
                    "".equals(form.getFieldValue(FillOrderMeta.getOrderedDate())) && 
                    form.getFieldValue(FillOrderMeta.getShipFromId()) == null &&
                    form.getFieldValue(FillOrderMeta.getOrganizationId()) == null && 
                    "".equals(form.getFieldValue(FillOrderMeta.getDescription())) && 
                    "".equals(form.getFieldValue(FillOrderMeta.getNeededInDays())) && 
                    //DAYS LEFT"".equals(rpc.getFieldValue(FillOrderMeta.InventoryReceiptMeta.getQcReference())) && 
                    "".equals(form.getFieldValue(FillOrderMeta.getRequestedBy())) && 
                    form.getFieldValue(FillOrderMeta.getCostCenterId()) == null);
    }
    
    private void fillModelFromQuery(DataModel model, List orders){
        int i=0;
        if(model == null)
            model = new DataModel();
        else
            model.clear();
        
        Calendar laterDate = Calendar.getInstance();
        laterDate.set(Calendar.HOUR_OF_DAY, 3);
        laterDate.set(Calendar.MINUTE, 0);
        laterDate.set(Calendar.SECOND, 0);
        laterDate.set(Calendar.MILLISECOND, 0);
        
        while(i < orders.size() && i < leftTableRowsPerPage) {
            FillOrderDO resultDO = (FillOrderDO)orders.get(i);
 
            DataSet row = new DataSet();
            NumberField id = new NumberField(resultDO.getOrderId());
            DateField orderDate = new DateField(Datetime.YEAR, Datetime.DAY, resultDO.getOrderedDate().getDate());
            DropDownField status = new DropDownField(new DataSet(new NumberObject(resultDO.getStatusId())));
            
            DropDownField shipFrom = null;
            if(resultDO.getShipFromId() != null)
                shipFrom = new DropDownField(new DataSet(new NumberObject(resultDO.getShipFromId())));
            else
                shipFrom = new DropDownField();
            
            DropDownField shipTo = new DropDownField();
            StringField description = new StringField(resultDO.getDescription());
            NumberField numberOfDays = new NumberField(resultDO.getNumberOfDays());
            NumberField daysLeft = new NumberField(NumberObject.Type.INTEGER);
            StringField requestedBy = new StringField(resultDO.getRequestedBy());
            
            DropDownField costCenter = new DropDownField();
            if(resultDO.getCostCenterId() != null)
                costCenter.setValue(new DataSet(new NumberObject(resultDO.getCostCenterId())));
            
            StringField multUnit = new StringField(resultDO.addressDO.getMultipleUnit());
            StringField streetAddress = new StringField(resultDO.addressDO.getStreetAddress());
            StringField city = new StringField(resultDO.addressDO.getCity());
            StringField state = new StringField(resultDO.addressDO.getState());
            StringField zipCode = new StringField(resultDO.addressDO.getZipCode());
            CheckField isInternal = new CheckField();
            if(resultDO.getShipToId() == null)
                isInternal.setValue("Y");                
            
            if(resultDO.getShipToId() == null)
                shipTo.setValue(null);
            else{
                DataModel shipToModel = new DataModel();
                shipToModel.add(new NumberObject(resultDO.getShipToId()),new StringObject(resultDO.getShipTo()));
                shipTo.setModel(shipToModel);
                shipTo.setValue(shipToModel.get(0));
            }
            
            Calendar earlyDate = Calendar.getInstance();
            earlyDate.setTime(resultDO.getOrderedDate().getDate());
            earlyDate.set(Calendar.HOUR_OF_DAY, 3);
            earlyDate.set(Calendar.MINUTE, 0);
            earlyDate.set(Calendar.SECOND, 0);
            earlyDate.set(Calendar.MILLISECOND, 0);
            
            int days = daysBetween(earlyDate.getTime(), laterDate.getTime());
            
            daysLeft.setValue(resultDO.getNumberOfDays() - days);
            
            row.setKey(id);         
            row.add(status);
            row.add(orderDate);
            row.add(shipFrom);
            row.add(shipTo);
            row.add(description);
            row.add(numberOfDays);
            row.add(daysLeft);
            row.add(isInternal);
            row.add(requestedBy);
            row.add(costCenter);
            row.add(multUnit);
            row.add(streetAddress);
            row.add(city);
            row.add(state);
            row.add(zipCode);
            
            model.add(row);
            i++;
        } 
    }
    
    public DataModel getOrderItemsOrderNotes(NumberObject orderId) {
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        List orderItems = remote.getOrderItems((Integer)orderId.getValue());
        DataModel model = new DataModel();
        
        Integer orderItemReferenceTableId;
        if(CachingManager.getElement("InitialData", "orderItemReferenceTableId") != null)
            orderItemReferenceTableId = (Integer)((NumberObject)CachingManager.getElement("InitialData", "orderItemReferenceTableId")).getValue();
        else
            orderItemReferenceTableId = (Integer)getOrderItemRefTable().getValue();
        
        OrderRemote orderRemote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteDO noteDO = orderRemote.getOrderShippingNote((Integer)orderId.getValue());
        
        for(int i=0; i<orderItems.size(); i++){
            OrderItemDO itemDO = (OrderItemDO)orderItems.get(i);
            DataSet set = new DataSet();
            NumberObject orderItemId = new NumberObject(itemDO.getId());
            set.setKey(orderItemId);
            NumberField quan = new NumberField(itemDO.getQuantity());
            set.add(quan);
            set.add(orderId);
            
            DropDownField item = new DropDownField();
            if(itemDO.getInventoryItemId() != null){
                DataModel itemModel = new DataModel();
                itemModel.add(new NumberObject(itemDO.getInventoryItemId()),new StringObject(itemDO.getInventoryItem()));
                item.setModel(itemModel);
                item.setValue(itemModel.get(0));
            }
            
            set.add(item);
            
            DropDownField loc = new DropDownField();
            if(itemDO.getLocationId() != null){
                DataModel locModel = new DataModel();
                locModel.add(new NumberObject(itemDO.getLocationId()),new StringObject(itemDO.getLocation()));
                loc.setModel(locModel);
                loc.setValue(locModel.get(0));
            }
            set.add(loc);
            
            StringField lotNumber = new StringField(itemDO.getLotNumber());
            set.add(lotNumber);
            NumberField qtyOnHand = new NumberField(itemDO.getQuantityOnHand());
            set.add(qtyOnHand);
            NumberObject referenceTableId = new NumberObject(orderItemReferenceTableId);
            set.add(referenceTableId);
            
            //we put the note in the data of the first row sent back
            if(i == 0 && noteDO != null)
                set.setData(new StringObject(noteDO.getText()));
            
           model.add(set);
        }
        
        return model;
    }
    
    public DataModel<DataSet> fetchOrderItemAndLock(NumberObject orderId)throws RPCException{
        DataModel<DataSet> returnModel = new DataModel<DataSet>();
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        List order=null;
        try{
            order = remote.getOrderAndLock((Integer)orderId.getValue());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        DataModel orderItemsModel;
        FillOrderDO fillOrderDO;
        if(order != null){
            //fill the return model with the fillOrderDO
            fillModelFromQuery(returnModel, order);
            
            //we need to lookup the order items
            fillOrderDO = (FillOrderDO)order.get(0);
            orderItemsModel = getOrderItemsOrderNotes(new NumberObject(fillOrderDO.getOrderId()));
            
            returnModel.get(0).setData(orderItemsModel);
        }
            
        return returnModel;
    }
    
    public DataModel fetchOrderItemAndUnlock(NumberObject orderId)throws RPCException{
        DataModel returnModel = new DataModel();
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        List order=null;
        try{
            order = remote.getOrderAndLock((Integer)orderId.getValue());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        DataModel orderItemsModel;
        FillOrderDO fillOrderDO;
        if(order != null){
            //fill the return model with the fillOrderDO
            fillModelFromQuery(returnModel, order);
            
            //we need to lookup the order items
            //fillOrderDO = (FillOrderDO)order.get(0);
            //orderItemsModel = getOrderItemsOrderNotes(new NumberObject(fillOrderDO.getOrderId()));
            
            //returnModel.get(0).setData(orderItemsModel);
        }
            
        return returnModel;
    }
    
    public NumberObject getOrderItemRefTable(){ 
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        return new NumberObject(remote.getOrderItemReferenceTableId());
    }
}
