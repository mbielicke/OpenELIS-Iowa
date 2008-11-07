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
package org.openelis.modules.panel.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.PanelItemDO;
import org.openelis.domain.QaEventTestDropdownDO;
import org.openelis.domain.TestMethodSectionNamesDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.PanelMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.PanelRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class PanelService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel> {

    private static final int leftTableRowsPerPage = 10;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));
    private static final PanelMetaMap PanelMeta = new PanelMetaMap();
    
    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = remote.getPanel((Integer)((DataObject)key.getKey()).getValue());
        setFieldsInRPC(rpcReturn, panelDO);
        List<PanelItemDO> itemDOList = remote.getPanelItems((Integer)((DataObject)key.getKey()).getValue());
        fillPanelItems(itemDOList, rpcReturn);
        return rpcReturn;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = getPanelDOFromRPC(rpcSend);
        List<PanelItemDO>itemDOList = getPanelItemsFromRPC(rpcSend);
        
        List exceptionList = remote.validateForAdd(panelDO,itemDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpcSend);
    
           return rpcSend;
        }
        
        Integer panelId;
        try {
            panelId = remote.updatePanel(panelDO,itemDOList);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpcSend);
    
            return rpcSend;
        }
    
        panelDO.setId(panelId);
        setFieldsInRPC(rpcReturn, panelDO);
        return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        try{
            remote.deletePanel((Integer)((DataObject)key.getKey()).getValue());
        }catch(Exception e){
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
        setFieldsInRPC(rpcReturn, new PanelDO());
        return rpcReturn;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List panelNames;
        // if the rpc is null then we need to get the page
        if (rpcSend == null) {

            FormRPC rpc = (FormRPC)SessionManager.getSession()
                                                 .getAttribute("PanelQuery");

            if (rpc == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");

            try {
                panelNames = remote.query(rpc.getFieldMap(),
                                         (model.getPage() * leftTableRowsPerPage),
                                         leftTableRowsPerPage + 1);
            } catch (Exception e) {
                if (e instanceof LastPageException) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                } else {
                    e.printStackTrace();
                    throw new RPCException(e.getMessage());
                }
            }
        } else {
            PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");

            HashMap<String, AbstractField> fields = rpcSend.getFieldMap();
            // fields.remove("contactsTable");

            try {
                panelNames = remote.query(fields, 0, leftTableRowsPerPage);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }

            // need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("PanelQuery", rpcSend);
        }

        // fill the model with the query results
        int i = 0;
        if(model == null)
            model = new DataModel();
        else 
            model.clear();
        while (i < panelNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)panelNames.get(i);

            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());
            StringObject name = new StringObject(resultDO.getName());

            row.setKey(id);
            row.add(name);
            model.add(row);
            i++;
        }

        return model;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = getPanelDOFromRPC(rpcSend);
        List<PanelItemDO>itemDOList = getPanelItemsFromRPC(rpcSend);
        
        List exceptionList = remote.validateForUpdate(panelDO,itemDOList);
        if (exceptionList.size() > 0) {
           setRpcErrors(exceptionList, rpcSend);
    
            return rpcSend;
        }
                
        try {
            remote.updatePanel(panelDO,itemDOList);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpcSend);
    
            return rpcSend;
        }

        setFieldsInRPC(rpcReturn, panelDO);
        return rpcReturn;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = remote.getPanel((Integer)((DataObject)key.getKey()).getValue());
        setFieldsInRPC(rpcReturn, panelDO);
        List<PanelItemDO> itemDOList = remote.getPanelItems((Integer)((DataObject)key.getKey()).getValue());
        fillPanelItems(itemDOList, rpcReturn);
        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = new PanelDO();
        try{ 
         panelDO = remote.getPanelAndLock((Integer)((DataObject)key.getKey()).getValue(),
                                          SessionManager.getSession().getId());
         
        }catch(Exception ex){
            ex.printStackTrace();
            throw new RPCException(ex.getMessage());
        } 
        setFieldsInRPC(rpcReturn, panelDO);
        List<PanelItemDO> itemDOList = remote.getPanelItems((Integer)((DataObject)key.getKey()).getValue());
        fillPanelItems(itemDOList, rpcReturn);
        return rpcReturn;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/panel.xsl");
    }

    public HashMap<String, Data> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/panel.xsl"));
                        
        HashMap<String, Data> map = new HashMap<String, Data>();
        DataModel testMethodNamesModelField = (DataModel)CachingManager.getElement("InitialData","testMethodNamesModel");
        if (testMethodNamesModelField == null) {
            testMethodNamesModelField = getTestMethodNames();
            CachingManager.putElement("InitialData",
                                      "testMethodNamesModel",
                                      testMethodNamesModelField);
        }
        
        map.put("xml", xml);
        map.put("allTests", getTestMethodNames());
        return map;
    }

    public HashMap<String, Data> getXMLData(HashMap<String, Data> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public DataModel getTestMethodNames(){
       PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
       DataModel model = new DataModel();
       List<TestMethodSectionNamesDO> list = remote.getTestMethodNames();
       for(int iter = 0; iter < list.size(); iter++){
           TestMethodSectionNamesDO tmsDO = list.get(iter);
           DataSet set = new DataSet();
           set.add(new StringObject(tmsDO.getTestName()));                              
           set.add(new StringObject(tmsDO.getMethodName()));                     
           set.add(new StringObject(tmsDO.getSectionName()));
           set.setKey(new StringObject(tmsDO.getTestName().trim()+","+tmsDO.getMethodName().trim()
                                       +","+tmsDO.getSectionName()));
           model.add(set);
       }
       return model;
    }
    
    public DataModel getInitialModel(String cat){
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        DataSet blankset = new DataSet();

        StringObject blankStringId = new StringObject();

        blankStringId.setValue("");
        blankset.add(blankStringId);
        
        blankset.setKey(blankStringId);
        DataModel model = new DataModel();
        model.add(blankset);
        
        List<QaEventTestDropdownDO> list = remote.getTestMethodNames();
        if(cat.equals("test")){
         for(int iter = 0; iter < list.size(); iter++){
            QaEventTestDropdownDO qaeDO = list.get(iter);
            DataSet set = new DataSet();
            set.add(new StringObject(qaeDO.getTest()));
            set.setKey(new StringObject(qaeDO.getTest()));
            model.add(set);
       }
      }else if(cat.equals("method")) {
          for(int iter = 0; iter < list.size(); iter++){
              QaEventTestDropdownDO qaeDO = list.get(iter);
              DataSet set = new DataSet();
              set.add(new StringObject(qaeDO.getMethod()));
              set.setKey(new StringObject(qaeDO.getMethod()));
              model.add(set);
         }
      }
       return model; 
    }
    
    
    
    
    private void fillPanelItems(List<PanelItemDO> list, FormRPC rpcReturn){
        DataModel model = (DataModel)rpcReturn.getField("addedTestTable").getValue();
        model.clear();
        
        if(list.size() >0){
           for(int iter = 0 ; iter < list.size(); iter++){
               PanelItemDO itemDO = list.get(iter);
               DataSet row = model.createNewSet();
               
               NumberField id = new NumberField(itemDO.getId());
               DataMap data = new DataMap();                    
               data.put("id", id);
               row.setData(data);
               row.get(0).setValue(itemDO.getTestName().trim());
               row.get(1).setValue(itemDO.getMethodName().trim());
               model.add(row);
           }  
          }
        }

    private void setFieldsInRPC(FormRPC rpcReturn, PanelDO panelDO) {  
        rpcReturn.setFieldValue(PanelMeta.getId(), panelDO.getId());
        rpcReturn.setFieldValue(PanelMeta.getName(), panelDO.getName());
        rpcReturn.setFieldValue(PanelMeta.getDescription(), panelDO.getDescription());
    }
    
    private PanelDO getPanelDOFromRPC(FormRPC rpcSend){
      Integer id = (Integer)rpcSend.getFieldValue(PanelMeta.getId()); 
      PanelDO panelDO = new PanelDO();
      panelDO.setId(id);
      panelDO.setDescription((String)rpcSend.getFieldValue(PanelMeta.getDescription()));      
      panelDO.setName((String)rpcSend.getFieldValue(PanelMeta.getName()));      
      return panelDO;
    }
    
    private List<PanelItemDO> getPanelItemsFromRPC(FormRPC rpcSend){
      DataModel model = (DataModel)rpcSend.getField("addedTestTable").getValue();
      List<PanelItemDO> itemDOList = new ArrayList<PanelItemDO>();
      Integer panelId = (Integer)rpcSend.getFieldValue(PanelMeta.getId());
      
      for (int i = 0; i < model.size(); i++) {
          DataSet row = model.get(i);
          PanelItemDO itemDO = new PanelItemDO();         
          itemDO.setDelete(new Boolean(false));   
          if(row.getData()!=null){
           NumberField id = (NumberField)((DataMap)row.getData()).get("id");
           if (id != null) {
              if (id.getValue() != null) {
                  itemDO.setId((Integer)id.getValue());
              }
           }
          } 
          itemDO.setPanelId(panelId);
          itemDO.setTestName((String)row.get(0).getValue());
          itemDO.setMethodName((String)row.get(1).getValue());
          itemDO.setSortOrder(new Integer(i));          
          itemDOList.add(itemDO);
      }
      for (int i = 0; i < model.getDeletions().size(); i++) {
          DataSet row = (DataSet)model.getDeletions().get(i);
          PanelItemDO itemDO = new PanelItemDO();
          if(row.getData()!=null){
              NumberField id = (NumberField)((DataMap)row.getData()).get("id");
              if (id != null) {
                 if (id.getValue() != null) {
                     itemDO.setId((Integer)id.getValue());
                 }
              }
             }
          itemDO.setDelete(new Boolean(true));
          itemDO.setPanelId(panelId);
          itemDO.setTestName((String)row.get(0).getValue());
          itemDO.setMethodName((String)row.get(1).getValue());
          itemDO.setSortOrder(new Integer(i));          
          itemDOList.add(itemDO);
     }
     
      model.getDeletions().clear();
      return itemDOList;
    }
    
    private void setRpcErrors(List exceptionList,  FormRPC rpcSend){
       TableField addedTestModel = (TableField)rpcSend.getField("addedTestTable");
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the entries table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName();
                addedTestModel.getField(index, fieldName)
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
    
}
