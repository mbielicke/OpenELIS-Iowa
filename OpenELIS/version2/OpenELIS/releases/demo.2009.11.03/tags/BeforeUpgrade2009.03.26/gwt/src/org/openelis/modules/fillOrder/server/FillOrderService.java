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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.FillOrderDO;
import org.openelis.domain.OrderItemDO;
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.FillOrderMetaMap;
import org.openelis.modules.fillOrder.client.FillOrderForm;
import org.openelis.modules.fillOrder.client.FillOrderItemInfoForm;
import org.openelis.modules.fillOrder.client.FillOrderItemInfoRPC;
import org.openelis.modules.fillOrder.client.FillOrderLocationAutoRPC;
import org.openelis.modules.fillOrder.client.FillOrderOrderItemsKey;
import org.openelis.modules.fillOrder.client.FillOrderRPC;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.FillOrderRemote;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.CostCentersCacheHandler;
import org.openelis.server.handlers.OrderStatusCacheHandler;
import org.openelis.server.handlers.ShipFromCacheHandler;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FillOrderService implements AppScreenFormServiceInt<FillOrderRPC, Integer>, AutoCompleteServiceInt{

    private static Node subRpcNode;
    
    private static final FillOrderMetaMap FillOrderMeta = new FillOrderMetaMap();
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final int leftTableRowsPerPage = 250;
    
    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
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
            model = new DataModel<Integer>();
        
        //fill the model with the query results
        fillModelFromQuery(model, orders);        
        
        return model;
    }

    public FillOrderRPC commitAdd(FillOrderRPC rpc) throws RPCException {
        return null;
    }

    public FillOrderRPC commitUpdate(FillOrderRPC rpc) throws RPCException {
        return null;
    }

    public FillOrderRPC commitDelete(FillOrderRPC rpc) throws RPCException {
        return null;
    }

    public FillOrderRPC abort(FillOrderRPC rpc) throws RPCException {
        return null;
    }

    public FillOrderRPC fetch(FillOrderRPC rpc) throws RPCException {
        return null;
    }

    public FillOrderRPC fetchForUpdate(FillOrderRPC rpcReturn) throws RPCException {
        return null;
    }

    public String getXML() throws RPCException {
        return null;
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        return null;
    }
    
    public FillOrderRPC getScreen(FillOrderRPC rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/fillOrder.xsl");

        /*
         * Load initial  models to RPC and store cache verison of models into Session for 
         * comparisons for later fetches
         */
        rpc.statuses = OrderStatusCacheHandler.getStatuses();
        SessionManager.getSession().setAttribute("orderStatusVersion",OrderStatusCacheHandler.version);
        rpc.costCenters = CostCentersCacheHandler.getCostCenters();
        SessionManager.getSession().setAttribute("costCenterVersion",CostCentersCacheHandler.version);
        rpc.shipFroms = ShipFromCacheHandler.getShipFroms();
        SessionManager.getSession().setAttribute("shipFromVersion",ShipFromCacheHandler.version);

        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer pendingValue = null;
        try{
            pendingValue = remote.getEntryIdForSystemName("order_status_pending");    
        }catch(Exception e){}
        
        rpc.orderPendingValue = pendingValue;
        
        if(subRpcNode == null){
            try{
                Document xml = XMLUtil.parse(rpc.xml);
                NodeList rpcNodes = (NodeList)xml.getElementsByTagName("rpc");
                int i=0;
                while(i<rpcNodes.getLength() && !"itemInformation".equals(rpcNodes.item(i).getAttributes().getNamedItem("key").getNodeValue()))
                    i++;
                
                if(i<rpcNodes.getLength())
                    subRpcNode = rpcNodes.item(i);
        
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        
        return rpc;
    }
    
    public void checkModels(FillOrderRPC rpc) {
        /*
         * Retrieve current version of models from session.
         */
        int statuses = (Integer)SessionManager.getSession().getAttribute("orderStatusVersion");
        int costCenters = (Integer)SessionManager.getSession().getAttribute("costCenterVersion");
        int shipFroms = (Integer)SessionManager.getSession().getAttribute("shipFromVersion");
        /*
         * Compare stored version to current cache versions and update if necessary. 
         */
        if(statuses != OrderStatusCacheHandler.version){
            rpc.statuses = OrderStatusCacheHandler.getStatuses();
            SessionManager.getSession().setAttribute("orderStatusVersion",OrderStatusCacheHandler.version);
        }
        if(costCenters != CostCentersCacheHandler.version){
            rpc.costCenters = CostCentersCacheHandler.getCostCenters();
            SessionManager.getSession().setAttribute("costCenterVersion",CostCentersCacheHandler.version);
        }
        if(shipFroms != ShipFromCacheHandler.version){
            rpc.shipFroms = ShipFromCacheHandler.getShipFroms();
            SessionManager.getSession().setAttribute("shipFromVersion",ShipFromCacheHandler.version);
        }
    }
    
    public List getListOfOrdersFromTree(TreeDataModel model){
        ArrayList orderList = new ArrayList();
        
        for(int i=0; i<model.size(); i++){
            
            for(int j=0; j<model.get(i).getItems().size(); j++){
                TreeDataItem childItem = (TreeDataItem)model.get(i).getItem(j);
                FillOrderOrderItemsKey rowHiddenMap = (FillOrderOrderItemsKey)childItem.getData();

                FillOrderDO orderDO = new FillOrderDO();
                orderDO.setOrderItemId(rowHiddenMap.referenceId);
                orderDO.setOrderId((Integer)childItem.get(1).getValue());
                orderDO.setInventoryLocationId((Integer)((DropDownField)childItem.get(3)).getSelectedKey());
                orderDO.setQuantity((Integer)childItem.get(0).getValue());
                
                orderList.add(orderDO);
            }
        }
        
        return orderList;
    }
    
    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        return null;
    }
    
    public FillOrderItemInfoRPC setOrderToProcessed(FillOrderItemInfoRPC rpc) throws RPCException {
        List orders = getListOfOrdersFromTree(rpc.form.originalOrderItemsTree.getValue());
       
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        try {
            remote.setOrderToProcessed(orders);
        } catch (Exception e) {
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc.form);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        List refreshedRows = remote.getOrdersById(getOrderIds(rpc.tableData));
        fillModelFromQuery(rpc.tableData, refreshedRows);
        
        return rpc;
    }
    
    private List getOrderIds(DataModel<Integer> model){
        List returnList = new ArrayList();
        for(int i=0; i<model.size(); i++)
            returnList.add(model.get(i).get(1).getValue());
        
        return returnList;
    }
    
    public FillOrderItemInfoRPC validateOrders(FillOrderItemInfoRPC rpc) throws RPCException { 
        List orders = getListOfOrdersFromTree(rpc.form.originalOrderItemsTree.getValue());
        
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        try {
            remote.validateOrders(orders);
        } catch (Exception e) {
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc.form);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        return rpc;
    }

    public FillOrderLocationAutoRPC getMatchesObj(FillOrderLocationAutoRPC rpc) throws RPCException {
        if("invLocation".equals(rpc.cat))
            rpc.autoMatches =  getLocationMatches(rpc.match, rpc.id);
        
        return rpc;
    }
    
    public DataModel getMatches(String cat, DataModel model, String match, HashMap<String, FieldType> params) throws RPCException {
        return null;
    }
    
    private DataModel getLocationMatches(String match, Integer invItemId) throws RPCException{
        DataModel<Integer> dataModel = new DataModel<Integer>();
        List autoCompleteList = new ArrayList();

        if(invItemId != null){
            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
            //lookup by name, inv id
            autoCompleteList = remote.autoCompleteLocationLookupByNameInvId(match+"%", invItemId, 10);
        }
        
        for(int i=0; i < autoCompleteList.size(); i++){
            StorageLocationAutoDO resultDO = (StorageLocationAutoDO) autoCompleteList.get(i);
 
            //add the dataset to the datamodel
            dataModel.add(new DataSet<Integer>(resultDO.getLocationId(),new FieldType[] {new StringObject(resultDO.getLocation()), new StringObject(resultDO.getLotNum()), new IntegerObject(resultDO.getQtyOnHand())}));                            

        }       
        
        return dataModel;       
    }
    
    public static int daysBetween(Date startDate, Date endDate) {
        return (int)((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));  
    }  
    
    private boolean isQueryEmpty(Form form){
   
        return ("".equals(form.getFieldValue(FillOrderMeta.getId())) &&
                    form.getFieldValue(FillOrderMeta.getStatusId()) == null &&
                    form.getFieldValue(FillOrderMeta.getOrderedDate()) == null && 
                    form.getFieldValue(FillOrderMeta.getShipFromId()) == null &&
                    form.getFieldValue(FillOrderMeta.getOrganizationId()) == null && 
                    form.getFieldValue(FillOrderMeta.getDescription()) == null && 
                    form.getFieldValue(FillOrderMeta.getNeededInDays()) == null && 
                    //DAYS LEFT"".equals(rpc.getFieldValue(FillOrderMeta.InventoryReceiptMeta.getQcReference())) && 
                    "".equals(form.getFieldValue(FillOrderMeta.getRequestedBy())) && 
                    form.getFieldValue(FillOrderMeta.getCostCenterId()) == null);
    }
    
    private void fillModelFromQuery(DataModel<Integer> model, List orders){
        model.paged = false;

        int i=0;
        if(model == null)
            model = new DataModel<Integer>();
        else
            model.clear();
        
        Calendar laterDate = Calendar.getInstance();
        laterDate.set(Calendar.HOUR_OF_DAY, 3);
        laterDate.set(Calendar.MINUTE, 0);
        laterDate.set(Calendar.SECOND, 0);
        laterDate.set(Calendar.MILLISECOND, 0);
        
        while(i < orders.size() && i < leftTableRowsPerPage) {
            FillOrderDO resultDO = (FillOrderDO)orders.get(i);
 
            DataSet<Integer> set = new DataSet<Integer>();
            
            //empty check
            set.add(new CheckField());
            //order id
            set.add(new IntegerField(resultDO.getOrderId()));
            //status
            set.add(new DropDownField<Integer>(new DataSet<Integer>(resultDO.getStatusId())));
            //orderedDate
            set.add(new DateField(Datetime.YEAR, Datetime.DAY, resultDO.getOrderedDate().getDate()));
            //shipFrom
            set.add(new DropDownField<Integer>(new DataSet<Integer>(resultDO.getShipFromId())));
            //shipTo
            DropDownField<Integer> shipToDrop = new DropDownField<Integer>();
            if(resultDO.getShipToId() != null){
                DataModel<Integer> shipToModel = new DataModel<Integer>();
                shipToModel.add(new DataSet<Integer>(resultDO.getShipToId(),new StringObject(resultDO.getShipTo())));
                shipToDrop.setModel(shipToModel);
                shipToDrop.setValue(shipToModel.get(0));
            }
            set.add(shipToDrop);
            //description
            set.add(new StringField(resultDO.getDescription()));
            //#days
            set.add(new IntegerField(resultDO.getNumberOfDays()));
            //days left
            Calendar earlyDate = Calendar.getInstance();
            earlyDate.setTime(resultDO.getOrderedDate().getDate());
            earlyDate.set(Calendar.HOUR_OF_DAY, 3);
            earlyDate.set(Calendar.MINUTE, 0);
            earlyDate.set(Calendar.SECOND, 0);
            earlyDate.set(Calendar.MILLISECOND, 0);
            int days = daysBetween(earlyDate.getTime(), laterDate.getTime());
            set.add(new IntegerField(resultDO.getNumberOfDays() - days));
            //internal
            CheckField isInternal = new CheckField();
            if(resultDO.getShipToId() == null)
                isInternal.setValue("Y");                
            set.add(isInternal);
         
            model.add(set);
            
            i++;
        } 
    }
    
    public FillOrderItemInfoRPC getOrderItemsOrderNotes(FillOrderItemInfoRPC rpc) {
        DataModel<Integer> model = new DataModel<Integer>();
        Integer orderItemReferenceTableId = getOrderItemRefTable();
        
        //get and set the order note and item information
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        FillOrderDO fillOrderDO = remote.getOrderItemInfoAndOrderNote(rpc.key);
        if(fillOrderDO != null)
            rpc.form.orderShippingNotes.setValue(fillOrderDO.getOrderNote());
        else
            rpc.form.orderShippingNotes.setValue(null);
        
        rpc.form.requestedBy.setValue(fillOrderDO.getRequestedBy());
        rpc.form.costCenterId.setValue(new DataSet<Integer>(fillOrderDO.getCostCenterId()));
        rpc.form.multUnit.setValue(fillOrderDO.addressDO.getMultipleUnit());
        rpc.form.streetAddress.setValue(fillOrderDO.addressDO.getStreetAddress());
        rpc.form.city.setValue(fillOrderDO.addressDO.getCity());
        rpc.form.state.setValue(fillOrderDO.addressDO.getState());
        rpc.form.zipCode.setValue(fillOrderDO.addressDO.getZipCode());
        
        //get and set the order items tree
        List orderItems = remote.getOrderItems(rpc.key);
        TreeDataModel treeModel = rpc.form.originalOrderItemsTree.getValue();
        treeModel.clear();
        for(int i=0; i<orderItems.size(); i++){
            ///
            OrderItemDO itemDO = (OrderItemDO)orderItems.get(i);
            TreeDataItem row = treeModel.createTreeItem("top");
            
            row.get(0).setValue(itemDO.getQuantity());
            row.get(1).setValue(rpc.key);
            
            if(itemDO.getInventoryItemId() != null){
                DataModel<Integer> invItemModel = new DataModel<Integer>();
                invItemModel.add(new DataSet<Integer>(itemDO.getInventoryItemId(),new StringObject(itemDO.getInventoryItem())));
                ((DropDownField<Integer>)row.get(2)).setModel(invItemModel);
                ((DropDownField<Integer>)row.get(2)).setValue(invItemModel.get(0));
            }
            
            /*
            if(itemDO.getLocationId() != null){
                DataModel<Integer> locModel = new DataModel<Integer>();
                locModel.add(new DataSet<Integer>(itemDO.getLocationId(),new StringObject(itemDO.getLocation())));
                ((DropDownField<Integer>)row.get(3)).setModel(locModel);
                ((DropDownField<Integer>)row.get(3)).setValue(locModel.get(0));
            }
            
            row.get(4).setValue(itemDO.getLotNumber());
            row.get(5).setValue(itemDO.getQuantityOnHand());
            */
            FillOrderOrderItemsKey rowHiddenMap = new FillOrderOrderItemsKey();
            rowHiddenMap.referenceTableId = orderItemReferenceTableId;
            rowHiddenMap.referenceId = itemDO.getId();
            row.setData(rowHiddenMap);
            
            treeModel.add(row);

        }
        return rpc;
    }
    
    public FillOrderItemInfoRPC fetchOrderItemAndLock(FillOrderItemInfoRPC rpc)throws RPCException{
        rpc.tableData = new DataModel<Integer>();
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        List order=null;
        
        try{
            order = remote.getOrderAndLock(rpc.key);

        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        FillOrderDO fillOrderDO;
        if(order != null)
            fillModelFromQuery(rpc.tableData, order);
            
        return rpc;
    }
    
    public FillOrderItemInfoRPC unlockOrders(FillOrderItemInfoRPC rpc) throws RPCException{
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        try{
            remote.unlockOrders(getOrderIds(rpc.tableData));
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
                    
        return rpc;
    }
    
    public Integer getOrderItemRefTable(){ 
        Integer orderItemRefTableId = (Integer)CachingManager.getElement("InitialData", "orderItemReferenceTableId");
        
        if(orderItemRefTableId == null){
            FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
            orderItemRefTableId = remote.getOrderItemReferenceTableId();
            CachingManager.putElement("InitialData", "orderItemReferenceTableId", orderItemRefTableId);
        }
        
        return orderItemRefTableId;
    }
    
    private void setRpcErrors(List exceptionList, FillOrderItemInfoForm form) {
        for (int i = 0; i < exceptionList.size(); i++) {
            // if the error is inside the table
            if (exceptionList.get(i) instanceof FieldErrorException)
                form.getField(
                        ((FieldErrorException) exceptionList.get(i))
                                .getFieldName()).addError(
                        openElisConstants
                                .getString(((FieldErrorException) exceptionList
                                        .get(i)).getMessage()));

            // if the error is on the entire form
            else if (exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants
                        .getString(((FormErrorException) exceptionList.get(i))
                                .getMessage()));
        }

        form.status = Form.Status.invalid;
    }
}
