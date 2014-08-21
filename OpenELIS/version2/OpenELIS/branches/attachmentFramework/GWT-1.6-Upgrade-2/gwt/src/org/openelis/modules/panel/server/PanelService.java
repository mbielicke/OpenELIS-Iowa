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

import org.openelis.domain.IdNameDO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.PanelItemDO;
import org.openelis.domain.QaEventTestDropdownDO;
import org.openelis.domain.TestMethodSectionNamesDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.PanelMetaMap;
import org.openelis.modules.panel.client.PanelForm;
import org.openelis.modules.panel.client.PanelRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.PanelRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PanelService implements AppScreenFormServiceInt<PanelRPC, Integer> {

    private static final int leftTableRowsPerPage = 10;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));
    private static final PanelMetaMap PanelMeta = new PanelMetaMap();
    
    public PanelRPC abort(PanelRPC rpc) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = remote.getPanel(rpc.key);
        setFieldsInRPC(rpc.form, panelDO);
        List<PanelItemDO> itemDOList = remote.getPanelItems(rpc.key);
        fillPanelItems(itemDOList, rpc.form);
        return rpc;
    }

    public PanelRPC commitAdd(PanelRPC rpc) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = getPanelDOFromRPC(rpc.form);
        List<PanelItemDO>itemDOList = getPanelItemsFromRPC(rpc.form);
        
        List exceptionList = remote.validateForAdd(panelDO,itemDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);
    
           return rpc;
        }
        
        Integer panelId;
        try {
            panelId = remote.updatePanel(panelDO,itemDOList);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }
    
        panelDO.setId(panelId);
        setFieldsInRPC(rpc.form, panelDO);
        return rpc;
    }

    public PanelRPC commitDelete(PanelRPC rpc) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        try{
            remote.deletePanel(rpc.key);
        }catch(Exception e){
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
        setFieldsInRPC(rpc.form, new PanelDO());
        return rpc;
    }

    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        List panelNames;
        // if the rpc is null then we need to get the page
        if (form == null) {

            form = (Form)SessionManager.getSession()
                                                 .getAttribute("PanelQuery");

            if (form == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");

            try {
                panelNames = remote.query(form.getFieldMap(),
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

            HashMap<String, AbstractField> fields = form.getFieldMap();
            // fields.remove("contactsTable");

            try {
                panelNames = remote.query(fields, 0, leftTableRowsPerPage);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }

            // need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("PanelQuery", form);
        }

        // fill the model with the query results
        int i = 0;
        if(model == null)
            model = new DataModel<Integer>();
        else 
            model.clear();
        while (i < panelNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)panelNames.get(i);
            model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        }

        return model;
    }

    public PanelRPC commitUpdate(PanelRPC rpc) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = getPanelDOFromRPC(rpc.form);
        List<PanelItemDO>itemDOList = getPanelItemsFromRPC(rpc.form);
        
        List exceptionList = remote.validateForUpdate(panelDO,itemDOList);
        if (exceptionList.size() > 0) {
           setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }
                
        try {
            remote.updatePanel(panelDO,itemDOList);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }

        setFieldsInRPC(rpc.form, panelDO);
        return rpc;
    }

    public PanelRPC fetch(PanelRPC rpc) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = remote.getPanel(rpc.key);
        setFieldsInRPC(rpc.form, panelDO);
        List<PanelItemDO> itemDOList = remote.getPanelItems(rpc.key);
        fillPanelItems(itemDOList, rpc.form);
        return rpc;
    }

    public PanelRPC fetchForUpdate(PanelRPC rpc) throws RPCException {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = new PanelDO();
        try{ 
         panelDO = remote.getPanelAndLock(rpc.key,
                                          SessionManager.getSession().getId());
         
        }catch(Exception ex){
            ex.printStackTrace();
            throw new RPCException(ex.getMessage());
        } 
        setFieldsInRPC(rpc.form, panelDO);
        List<PanelItemDO> itemDOList = remote.getPanelItems(rpc.key);
        fillPanelItems(itemDOList, rpc.form);
        return rpc;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/panel.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/panel.xsl"));                        
        HashMap<String, FieldType> map = new HashMap<String, FieldType>();               
        map.put("xml", xml);
        map.put("allTests", getTestMethodNames());
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public PanelRPC getScreen(PanelRPC rpc) throws RPCException{        
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/panel.xsl");                               
        rpc.allTests = getTestMethodNames();
        return rpc;
    }
    
    public DataModel<String> getTestMethodNames(){
       PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
       DataModel<String> model = new DataModel<String>();
       List<TestMethodSectionNamesDO> list = remote.getTestMethodNames();
       for(int iter = 0; iter < list.size(); iter++){
           TestMethodSectionNamesDO tmsDO = list.get(iter);
           DataSet<String> set = new DataSet<String>();
           set.add(new StringObject(tmsDO.getTestName()));                              
           set.add(new StringObject(tmsDO.getMethodName()));                     
           set.add(new StringObject(tmsDO.getSectionName()));
           set.setKey((tmsDO.getTestName().trim()+","+tmsDO.getMethodName().trim()
                                       +","+tmsDO.getSectionName()));
           model.add(set);
       }
       return model;
    }
    
    public DataModel<String> getInitialModel(String cat){
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        DataModel<String> model = new DataModel<String>();
        model.add(new DataSet<String>("",new StringObject("")));
        
        List<QaEventTestDropdownDO> list = remote.getTestMethodNames();
        if(cat.equals("test")){
         for(int iter = 0; iter < list.size(); iter++){
            QaEventTestDropdownDO qaeDO = list.get(iter);
            model.add(new DataSet<String>(qaeDO.getTest(),new StringObject(qaeDO.getTest())));
       }
      }else if(cat.equals("method")) {
          for(int iter = 0; iter < list.size(); iter++){
              QaEventTestDropdownDO qaeDO = list.get(iter);
              model.add(new DataSet<String>(qaeDO.getMethod(),new StringObject(qaeDO.getMethod())));
         }
      }
       return model; 
    }
    
    
    
    
    private void fillPanelItems(List<PanelItemDO> list, Form form){
        DataModel model = (DataModel)form.getField("addedTestTable").getValue();
        model.clear();
        
        if(list.size() >0){
           for(int iter = 0 ; iter < list.size(); iter++){
               PanelItemDO itemDO = list.get(iter);
               DataSet row = model.createNewSet();
               
               IntegerField id = new IntegerField(itemDO.getId());
               DataMap data = new DataMap();                    
               data.put("id", id);
               row.setData(data);
               ((Field)row.get(0)).setValue(itemDO.getTestName().trim());
               ((Field)row.get(1)).setValue(itemDO.getMethodName().trim());
               model.add(row);
           }  
          }
        }

    private void setFieldsInRPC(PanelForm form, PanelDO panelDO) {  
        form.id.setValue(panelDO.getId());
        form.name.setValue(panelDO.getName());
        form.description.setValue(panelDO.getDescription());
    }
    
    private PanelDO getPanelDOFromRPC(PanelForm form){
      PanelDO panelDO = new PanelDO();
      panelDO.setId(form.id.getValue());
      panelDO.setDescription(form.description.getValue());      
      panelDO.setName(form.name.getValue());      
      return panelDO;
    }
    
    private List<PanelItemDO> getPanelItemsFromRPC(PanelForm form){
      DataModel<Integer> model = (DataModel<Integer>)form.getField("addedTestTable").getValue();
      List<PanelItemDO> itemDOList = new ArrayList<PanelItemDO>();
      Integer panelId = (Integer)form.getFieldValue(PanelMeta.getId());
      
      for (int i = 0; i < model.size(); i++) {
          DataSet<Integer> row = model.get(i);
          PanelItemDO itemDO = new PanelItemDO();         
          itemDO.setDelete(new Boolean(false));   
          if(row.getData()!=null){
              IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
           if (id != null) {
              if (id.getValue() != null) {
                  itemDO.setId(id.getValue());
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
          DataSet<Data> row = (DataSet)model.getDeletions().get(i);
          PanelItemDO itemDO = new PanelItemDO();
          if(row.getData()!=null){
              IntegerField id = (IntegerField)((DataMap)row.getData()).get("id");
              if (id != null) {
                 if (id.getValue() != null) {
                     itemDO.setId(id.getValue());
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
    
    private void setRpcErrors(List exceptionList,  Form form){
       TableField addedTestModel = (TableField)form.getField("addedTestTable");
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
                form.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        form.status = Form.Status.invalid;
    }
    
}
