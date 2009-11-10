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
import org.openelis.domain.PanelVO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.data.deprecated.TableField;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.modules.panel.client.PanelForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.PanelRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class PanelService implements AppScreenFormServiceInt<PanelForm, Query<TableDataRow<Integer>>> {

    private static final int leftTableRowsPerPage = 13;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));
    
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws Exception {
        List panelNames;
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");            

        try {
            panelNames = remote.query(query.fields, query.page*leftTableRowsPerPage, leftTableRowsPerPage);
        }catch(LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
         } catch (Exception e) {            
            throw new Exception(e.getMessage());
         }
            

        // fill the model with the query results
        int i = 0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else 
            query.results.clear();
        while (i < panelNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)panelNames.get(i);
            query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        }

        return query;
    }

    public PanelForm commitAdd(PanelForm rpc) throws Exception {
        PanelRemote remote;
        PanelDO panelDO;
        List<PanelItemDO>itemDOList;               
        Integer panelId;
        
        remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        panelDO = getPanelDOFromRPC(rpc);
        itemDOList = getPanelItemsFromRPC(rpc);
        try {
            panelId = remote.updatePanel(panelDO,itemDOList);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }
    
        panelDO.setId(panelId);
        setFieldsInRPC(rpc, panelDO);
        return rpc;
    }
    
    public PanelForm commitUpdate(PanelForm rpc) throws Exception {
        PanelRemote remote;
        PanelDO panelDO;
        List<PanelItemDO>itemDOList;  
        
        remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        panelDO = getPanelDOFromRPC(rpc);
        itemDOList = getPanelItemsFromRPC(rpc);
                
        try {
            remote.updatePanel(panelDO,itemDOList);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }

        setFieldsInRPC(rpc, panelDO);
        return rpc;
    }

    public PanelForm commitDelete(PanelForm rpc) throws Exception {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        try{
            remote.deletePanel(rpc.entityKey);
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        setFieldsInRPC(rpc, new PanelDO());
        return rpc;
    }

    public PanelForm abort(PanelForm rpc) throws Exception {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = remote.getPanel(rpc.entityKey);
        setFieldsInRPC(rpc, panelDO);
        List<PanelItemDO> itemDOList = remote.getPanelItems(rpc.entityKey);
        fillPanelItems(itemDOList, rpc);
        return rpc;
    }

    public PanelForm fetch(PanelForm rpc) throws Exception {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = remote.getPanel(rpc.entityKey);
        setFieldsInRPC(rpc, panelDO);
        List<PanelItemDO> itemDOList = remote.getPanelItems(rpc.entityKey);
        fillPanelItems(itemDOList, rpc);
        return rpc;
    }

    public PanelForm fetchForUpdate(PanelForm rpc) throws Exception {
        PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
        PanelDO panelDO = new PanelDO();
        try{ 
         panelDO = remote.getPanelAndLock(rpc.entityKey,
                                          SessionManager.getSession().getId());
         
        }catch(Exception ex){
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        } 
        setFieldsInRPC(rpc, panelDO);
        List<PanelItemDO> itemDOList = remote.getPanelItems(rpc.entityKey);
        fillPanelItems(itemDOList, rpc);
        return rpc;
    }
    
    public PanelForm getScreen(PanelForm rpc) throws Exception{        
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/panel.xsl");                               
        rpc.allTests = getTestMethodNames();
        return rpc;
    }
    
    public PanelForm getTestMethodNames(PanelForm rpc) {
      rpc.addedTestTable.setValue(getTestMethodNames());
      return rpc;
    }
    
    private TableDataModel<TableDataRow<String>> getTestMethodNames(){
       PanelRemote remote = (PanelRemote)EJBFactory.lookup("openelis/PanelBean/remote");
       TableDataModel<TableDataRow<String>> model = new TableDataModel<TableDataRow<String>>();
       List<PanelVO> list = remote.getTestMethodNames();
       for(int iter = 0; iter < list.size(); iter++){
           PanelVO tmsDO = list.get(iter);
           model.add(new TableDataRow<String>(tmsDO.getTestName().trim()+","+tmsDO.getMethodName().trim()+","+tmsDO.getSectionName(),
                                              new FieldType[] {
                                                               new StringObject(tmsDO.getTestName()),                              
                                                               new StringObject(tmsDO.getMethodName()),                     
                                                               new StringObject(tmsDO.getSectionName())
                                              }
                     )
           );
       }
       return model;
    }                  
    
    private void fillPanelItems(List<PanelItemDO> list, PanelForm form){
        TableDataModel<TableDataRow<Integer>> model = form.addedTestTable.getValue();
        model.clear();
        
        if(list.size() >0){
           for(int iter = 0 ; iter < list.size(); iter++){
               PanelItemDO itemDO = list.get(iter);
               TableDataRow<Integer> row = model.createNewSet();
                                 
               row.key = itemDO.getId();
               (row.getCells().get(0)).setValue(itemDO.getTestName().trim());
               (row.getCells().get(1)).setValue(itemDO.getMethodName().trim());
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
      TableDataModel<TableDataRow<Integer>> model;
      List<PanelItemDO> itemDOList;
      Integer panelId;
      int i;
      List <TableDataRow<Integer>> deletions;
      TableDataRow<Integer> row;
      PanelItemDO itemDO;
      
      model = form.addedTestTable.getValue();
      itemDOList = new ArrayList<PanelItemDO>();
      panelId = form.id.getValue();
      
      for (i = 0; i < model.size(); i++) {
          row = model.get(i);
          itemDO = new PanelItemDO();         
         // itemDO.setDelete(false);             
          itemDO.setId(row.key);           
          itemDO.setPanelId(panelId);
          itemDO.setTestName((String)row.getCells().get(0).getValue());
          itemDO.setMethodName((String)row.getCells().get(1).getValue());
          itemDO.setSortOrder(i);          
          itemDOList.add(itemDO);
      }
      
      deletions = model.getDeletions();
      if(deletions != null) {
       for (i = 0; i < deletions.size(); i++) {
          row = deletions.get(i);
          itemDO = new PanelItemDO();
          itemDO.setId(row.key);
         // itemDO.setDelete(true);          
          itemDOList.add(itemDO);
        }
     
        deletions.clear();
      }  
      return itemDOList;
    }
    
    private void setRpcErrors(List exceptionList,  PanelForm form){
       TableField addedTestModel = form.addedTestTable;
       HashMap<String,AbstractField> map = null;
       if(exceptionList.size() > 0)
           map = FormUtil.createFieldMap(form);
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
                map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        form.status = Form.Status.invalid;
    }
    
}
