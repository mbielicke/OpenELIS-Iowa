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
import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrderItemDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.FillOrderMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.FillOrderRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class FillOrderService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel>, AutoCompleteServiceInt{

    private static final FillOrderMetaMap FillOrderMeta = new FillOrderMetaMap();
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final int leftTableRowsPerPage = 250;
    
    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List orders;
        //if the rpc is null then we need to get the page
        if(rpcSend == null){

            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("FillOrderQuery");
    
            if(rpc == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
            try{
                orders = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
            fields.remove("fillItemsTable");
            fields.remove("process");
            fields.remove("daysLeft");
            
           //TODO get this back when query table is fixed if(isQueryEmpty(rpcSend))
                //throw new QueryException(openElisConstants.getString("emptyQueryException"));
           
            try{    
                orders = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("FillOrderQuery", rpcSend);
        }
        
        if(model == null)
            model = new DataModel();
        
        //fill the model with the query results
        fillModelFromQuery(model, orders);        
 
        return model;
    }
    
    public DataModel commitQueryAndLock(DataModel model) throws RPCException {
        List orders;
        FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("FillOrderQuery");

        if(rpc == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        try{
            orders = remote.queryAndLock(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }    
        
        if(model == null)
            model = new DataModel();
        
        //fill the model with the query results
        fillModelFromQuery(model, orders);
        
        return model;
    }
    
    public DataModel commitQueryAndUnlock(DataModel model) throws RPCException {
        List orders;
        FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("FillOrderQuery");

        if(rpc == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        try{
            orders = remote.queryAndUnlock(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }    
        
        if(model == null)
            model = new DataModel();
        
        //fill the model with the query results
        fillModelFromQuery(model, orders);
 
        return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
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
            FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
            orderItemReferenceTableId = new NumberObject(remote.getOrderItemReferenceTableId());
            CachingManager.putElement("InitialData", "orderItemReferenceTableId", orderItemReferenceTableId);
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
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel getMatches(String cat, DataModel model, String match, HashMap<String, Data> params) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static int daysBetween(Date startDate, Date endDate) {
        return (int)((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));  
    }  
    
    private boolean isQueryEmpty(FormRPC rpc){
   
        return ("".equals(rpc.getFieldValue(FillOrderMeta.getId())) &&
                    rpc.getFieldValue(FillOrderMeta.getStatusId()) == null && 
                    "".equals(rpc.getFieldValue(FillOrderMeta.getOrderedDate())) && 
                    rpc.getFieldValue(FillOrderMeta.getShipFromId()) == null &&
                    rpc.getFieldValue(FillOrderMeta.getOrganizationId()) == null && 
                    "".equals(rpc.getFieldValue(FillOrderMeta.getDescription())) && 
                    "".equals(rpc.getFieldValue(FillOrderMeta.getNeededInDays())) && 
                    //DAYS LEFT"".equals(rpc.getFieldValue(FillOrderMeta.InventoryReceiptMeta.getQcReference())) && 
                    "".equals(rpc.getFieldValue(FillOrderMeta.getRequestedBy())) && 
                    rpc.getFieldValue(FillOrderMeta.getCostCenterId()) == null);
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
            DropDownField shipFrom = new DropDownField(new DataSet(new NumberObject(resultDO.getShipFromId())));
            DropDownField shipTo = new DropDownField();
            StringField description = new StringField(resultDO.getDescription());
            NumberField numberOfDays = new NumberField(resultDO.getNumberOfDays());
            NumberField daysLeft = new NumberField(NumberObject.Type.INTEGER);
            StringField requestedBy = new StringField(resultDO.getRequestedBy());
            DropDownField costCenter = new DropDownField(new DataSet(new NumberObject(resultDO.getCostCenterId())));
            StringField multUnit = new StringField(resultDO.addressDO.getMultipleUnit());
            StringField streetAddress = new StringField(resultDO.addressDO.getStreetAddress());
            StringField city = new StringField(resultDO.addressDO.getCity());
            StringField state = new StringField(resultDO.addressDO.getState());
            StringField zipCode = new StringField(resultDO.addressDO.getZipCode());
            
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
    
    public DataModel getOrderItems(NumberObject orderId) {
        FillOrderRemote remote = (FillOrderRemote)EJBFactory.lookup("openelis/FillOrderBean/remote");
        List orderItems = remote.getOrderItems((Integer)orderId.getValue());
        DataModel model = new DataModel();
        Integer orderItemReferenceTableId = (Integer)((NumberObject)CachingManager.getElement("InitialData", "orderItemReferenceTableId")).getValue();
        
        for(int i=0; i<orderItems.size(); i++){
            OrderItemDO itemDO = (OrderItemDO)orderItems.get(i);
            DataSet set = new DataSet();
            NumberField orderItemId = new NumberField(itemDO.getId());
            set.setKey(orderItemId);
            StringField itemName = new StringField(itemDO.getInventoryItem());
            set.add(itemName);
            NumberField quan = new NumberField(itemDO.getQuantityRequested());
            set.add(quan);
            StringField loc = new StringField(itemDO.getLocation());
            set.add(loc);
            NumberField referenceTableId = new NumberField(orderItemReferenceTableId);
            set.add(referenceTableId);            
            
           model.add(set);
        }
        
        return model;
    }
}
