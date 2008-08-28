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
package org.openelis.modules.fillOrder.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrderDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateObject;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.FillOrderRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class FillOrderService implements AppScreenFormServiceInt, AutoCompleteServiceInt{

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

            try{    
                orders = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("FillOrderQuery", rpcSend);
        }
        
        //fill the model with the query results
        int i=0;
        if(model == null)
            model = new DataModel();
        else
            model.clear();
        while(i < orders.size() && i < leftTableRowsPerPage) {
            OrderDO resultDO = (OrderDO)orders.get(i);
 
            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());
            DateObject name = new DateObject(Datetime.YEAR,Datetime.DAY, resultDO.getOrderedDate().getDate());
            
            row.setKey(id);         
            row.addObject(name);
            model.add(row);
            i++;
        } 
 
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

    public HashMap<String, DataObject> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/fillOrder.xsl"));
        
        DataModel shipFromDropdownField = (DataModel)CachingManager.getElement("InitialData", "shipFromDropdown");
        DataModel costCenterDropdownField = (DataModel)CachingManager.getElement("InitialData", "orderCostCenterDropdown");
        DataModel statusDropdownField = (DataModel)CachingManager.getElement("InitialData", "orderStatusDropdown");
       
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
        
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("status", shipFromDropdownField);
        map.put("costCenter", costCenterDropdownField);
        map.put("shipFrom", shipFromDropdownField);
        
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
            blankset.addObject(blankStringId);
            
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
            set.addObject(textObject);
            
            numberId.setValue(dropdownId);
            set.setKey(numberId);
            
            returnModel.add(set);
            
            i++;
        }       
        
        return returnModel;
    }

    public HashMap<String, DataObject> getXMLData(HashMap<String, DataObject> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel getMatches(String cat, DataModel model, String match, HashMap<String, DataObject> params) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
}
