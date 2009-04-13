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
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
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
import org.openelis.modules.fillOrder.client.FillOrderLocationAutoRPC;
import org.openelis.modules.fillOrder.client.FillOrderOrderItemsKey;
import org.openelis.modules.fillOrder.client.FillOrderForm;
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
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FillOrderService implements AppScreenFormServiceInt<FillOrderForm, Query<TableDataRow<Integer>>>, AutoCompleteServiceInt{

    private static Node subRpcNode;
    
    private static final FillOrderMetaMap FillOrderMeta = new FillOrderMetaMap();
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final int leftTableRowsPerPage = 250;
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List orders;
        //if the rpc is null then we need to get the page
       /* if(form == null){

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
        }else{*/
            FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
            
            if(query.fields.isEmpty())
                throw new QueryException(openElisConstants.getString("emptyQueryException"));
           
            try{    
                orders = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }    
        
            //need to save the rpc used to the encache
            //SessionManager.getSession().setAttribute("FillOrderQuery", form);
        //}
        
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        
        //fill the model with the query results
        fillModelFromQuery(query.results, orders);        
        
        return query;
    }

    public FillOrderForm commitAdd(FillOrderForm rpc) throws RPCException {
        return null;
    }

    public FillOrderForm commitUpdate(FillOrderForm rpc) throws RPCException {
        return null;
    }

    public FillOrderForm commitDelete(FillOrderForm rpc) throws RPCException {
        return null;
    }

    public FillOrderForm abort(FillOrderForm rpc) throws RPCException {
        return null;
    }

    public FillOrderForm fetch(FillOrderForm rpc) throws RPCException {
        return null;
    }

    public FillOrderForm fetchForUpdate(FillOrderForm rpcReturn) throws RPCException {
        return null;
    }

    public String getXML() throws RPCException {
        return null;
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        return null;
    }
    
    public FillOrderForm getScreen(FillOrderForm rpc) throws RPCException {
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
    
    public void checkModels(FillOrderForm rpc) {
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
                orderDO.setOrderId((Integer)childItem.cells[1].getValue());
                orderDO.setInventoryLocationId((Integer)((DropDownField)childItem.cells[3]).getSelectedKey());
                orderDO.setQuantity((Integer)childItem.cells[0].getValue());
                
                orderList.add(orderDO);
            }
        }
        
        return orderList;
    }
    
    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        return null;
    }
    
    public FillOrderItemInfoForm setOrderToProcessed(FillOrderItemInfoForm rpc) throws RPCException {
        List orders = getListOfOrdersFromTree(rpc.originalOrderItemsTree.getValue());
       
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        try {
            remote.setOrderToProcessed(orders);
        } catch (Exception e) {
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        List refreshedRows = remote.getOrdersById(getOrderIds(rpc.tableData));
        fillModelFromQuery(rpc.tableData, refreshedRows);
        
        return rpc;
    }
    
    private List getOrderIds(TableDataModel<TableDataRow<Integer>> model){
        List returnList = new ArrayList();
        for(int i=0; i<model.size(); i++)
            returnList.add(model.get(i).cells[1].getValue());
        
        return returnList;
    }
    
    public FillOrderItemInfoForm validateOrders(FillOrderItemInfoForm rpc) throws RPCException { 
        List orders = getListOfOrdersFromTree(rpc.originalOrderItemsTree.getValue());
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        try {
            remote.validateOrders(orders);
        } catch (Exception e) {
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else{
                throw new RPCException(e.getMessage());
            }
        }
        
        return rpc;
    }

    public FillOrderLocationAutoRPC getMatchesObj(FillOrderLocationAutoRPC rpc) throws RPCException {
        if("invLocation".equals(rpc.cat))
            rpc.autoMatches =  getLocationMatches(rpc.match, rpc.id);
        
        return rpc;
    }
    
    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap<String, FieldType> params) throws RPCException {
        return null;
    }
    
    private TableDataModel getLocationMatches(String match, Integer invItemId) throws RPCException{
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList = new ArrayList();

        if(invItemId != null){
            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
            //lookup by name, inv id
            autoCompleteList = remote.autoCompleteLocationLookupByNameInvId(match+"%", invItemId, 10);
        }
        
        for(int i=0; i < autoCompleteList.size(); i++){
            StorageLocationAutoDO resultDO = (StorageLocationAutoDO) autoCompleteList.get(i);
 
            //add the dataset to the datamodel
            dataModel.add(new TableDataRow<Integer>(resultDO.getLocationId(),new FieldType[] {new StringObject(resultDO.getLocation()), new StringObject(resultDO.getLotNum()), new IntegerObject(resultDO.getQtyOnHand())}));                            

        }       
        
        return dataModel;       
    }
    
    public static int daysBetween(Date startDate, Date endDate) {
        return (int)((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));  
    }  

    
    private void fillModelFromQuery(TableDataModel<TableDataRow<Integer>> model, List orders){
        model.paged = false;

        int i=0;
        if(model == null)
            model = new TableDataModel<TableDataRow<Integer>>();
        else
            model.clear();
        
        Calendar laterDate = Calendar.getInstance();
        laterDate.set(Calendar.HOUR_OF_DAY, 3);
        laterDate.set(Calendar.MINUTE, 0);
        laterDate.set(Calendar.SECOND, 0);
        laterDate.set(Calendar.MILLISECOND, 0);
        
        while(i < orders.size() && i < leftTableRowsPerPage) {
            FillOrderDO resultDO = (FillOrderDO)orders.get(i);
 
            TableDataRow<Integer> set = new TableDataRow<Integer>(10);
            
            //empty check
            set.cells[0] = (new CheckField());
            //order id
            set.cells[1] =(new IntegerField(resultDO.getOrderId()));
            //status
            set.cells[2] = (new DropDownField<Integer>(new TableDataRow<Integer>(resultDO.getStatusId())));
            //orderedDate
            set.cells[3] = (new DateField(Datetime.YEAR, Datetime.DAY, resultDO.getOrderedDate().getDate()));
            //shipFrom
            set.cells[4] = (new DropDownField<Integer>(new TableDataRow<Integer>(resultDO.getShipFromId())));
            //shipTo
            DropDownField<Integer> shipToDrop = new DropDownField<Integer>();
            if(resultDO.getShipToId() != null){
                TableDataModel<TableDataRow<Integer>> shipToModel = new TableDataModel<TableDataRow<Integer>>();
                shipToModel.add(new TableDataRow<Integer>(resultDO.getShipToId(),new StringObject(resultDO.getShipTo())));
                shipToDrop.setModel(shipToModel);
                shipToDrop.setValue(shipToModel.get(0));
            }
            set.cells[5] = (shipToDrop);
            //description
            set.cells[6] = (new StringField(resultDO.getDescription()));
            //#days
            set.cells[7] = (new IntegerField(resultDO.getNumberOfDays()));
            //days left
            Calendar earlyDate = Calendar.getInstance();
            earlyDate.setTime(resultDO.getOrderedDate().getDate());
            earlyDate.set(Calendar.HOUR_OF_DAY, 3);
            earlyDate.set(Calendar.MINUTE, 0);
            earlyDate.set(Calendar.SECOND, 0);
            earlyDate.set(Calendar.MILLISECOND, 0);
            int days = daysBetween(earlyDate.getTime(), laterDate.getTime());
            set.cells[8] = (new IntegerField(resultDO.getNumberOfDays() - days));
            //internal
            CheckField isInternal = new CheckField();
            if(resultDO.getShipToId() == null)
                isInternal.setValue("Y");                
            set.cells[9] = (isInternal);
         
            model.add(set);
            
            i++;
        } 
    }
    
    public FillOrderItemInfoForm getOrderItemsOrderNotes(FillOrderItemInfoForm rpc) {
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
        Integer orderItemReferenceTableId = getOrderItemRefTable();
        
        //get and set the order note and item information
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        FillOrderDO fillOrderDO = remote.getOrderItemInfoAndOrderNote(rpc.entityKey);
        if(fillOrderDO != null)
            rpc.orderShippingNotes.setValue(fillOrderDO.getOrderNote());
        else
            rpc.orderShippingNotes.setValue(null);
        
        rpc.requestedBy.setValue(fillOrderDO.getRequestedBy());
        rpc.costCenterId.setValue(new TableDataRow<Integer>(fillOrderDO.getCostCenterId()));
        rpc.multUnit.setValue(fillOrderDO.addressDO.getMultipleUnit());
        rpc.streetAddress.setValue(fillOrderDO.addressDO.getStreetAddress());
        rpc.city.setValue(fillOrderDO.addressDO.getCity());
        rpc.state.setValue(fillOrderDO.addressDO.getState());
        rpc.zipCode.setValue(fillOrderDO.addressDO.getZipCode());
        
        //get and set the order items tree
        List orderItems = remote.getOrderItems(rpc.entityKey);
        TreeDataModel treeModel = rpc.originalOrderItemsTree.getValue();
        treeModel.clear();
        for(int i=0; i<orderItems.size(); i++){
            ///
            OrderItemDO itemDO = (OrderItemDO)orderItems.get(i);
            //TreeDataItem row = treeModel.createTreeItem("top");
            TreeDataItem row = treeModel.createTreeItem("orderItem");
            
            row.cells[0].setValue(itemDO.getQuantity());
            row.cells[1].setValue(rpc.entityKey);
            
            if(itemDO.getInventoryItemId() != null){
                TableDataModel<TableDataRow<Integer>> invItemModel = new TableDataModel<TableDataRow<Integer>>();
                invItemModel.add(new TableDataRow<Integer>(itemDO.getInventoryItemId(),new StringObject(itemDO.getInventoryItem())));
                ((DropDownField<Integer>)row.cells[2]).setModel(invItemModel);
                ((DropDownField<Integer>)row.cells[2]).setValue(invItemModel.get(0));
            }
            
            /*
            if(itemDO.getLocationId() != null){
                TableDataModel<TableDataRow<Integer>> locModel = new TableDataModel<TableDataRow<Integer>>();
                locModel.add(new TableDataRow<Integer>(itemDO.getLocationId(),new StringObject(itemDO.getLocation())));
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
    
    public FillOrderItemInfoForm fetchOrderItemAndLock(FillOrderItemInfoForm rpc)throws RPCException{
        rpc.tableData = new TableDataModel<TableDataRow<Integer>>();
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        List order=null;
        
        try{
            order = remote.getOrderAndLock(rpc.entityKey);

        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        FillOrderDO fillOrderDO;
        if(order != null)
            fillModelFromQuery(rpc.tableData, order);
            
        return rpc;
    }
    
    public FillOrderItemInfoForm unlockOrders(FillOrderItemInfoForm rpc) throws RPCException{
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
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0) 
            map = FormUtil.createFieldMap(form);
        for (int i = 0; i < exceptionList.size(); i++) {
            // if the error is inside the table
            if (exceptionList.get(i) instanceof FieldErrorException)
                map.get(
                        ((FieldErrorException) exceptionList.get(i))
                                .getFieldName()).addError(
                        openElisConstants
                                .getString(((FieldErrorException) exceptionList
                                        .get(i)).getMessage()));

            // if the error is on the entire form
            else if (exceptionList.get(i) instanceof FormErrorException){
                form.addError(openElisConstants
                        .getString(((FormErrorException) exceptionList.get(i))
                                .getMessage()));
            }
        }

        form.status = Form.Status.invalid;
    }
}
