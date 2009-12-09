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

package org.openelis.modules.auxiliary.server;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AuxFieldGroupManagerRemote;
import org.openelis.remote.AuxiliaryRemote;


public class AuxiliaryService {//implements
                             //AppScreenFormServiceInt<AuxiliaryForm, Query<TableDataRow<Integer>>> , AutoCompleteServiceInt{
    /*
    private static final int leftTableRowsPerPage = 26;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws Exception {
        List auxfgNames;
        
            AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");

            try {
                auxfgNames = remote.query(query.fields, query.page*leftTableRowsPerPage, leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(e.getMessage());
            }
            

        // fill the model with the query results
        int i = 0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else 
            query.results.clear();
        while (i < auxfgNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)auxfgNames.get(i);
            query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        }

        return query;
    }
    
    public AuxiliaryForm commitAdd(AuxiliaryForm rpc) throws Exception {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = getAuxFieldGroupDOFromRPC(rpc);
        Integer axfgId;
        List<AuxFieldViewDO> axfDOList = getAuxFieldDOListFromRPC(null, rpc);        
    
        try {
            axfgId = remote.updateAuxiliary(axfgDO,axfDOList);
            axfgDO = remote.getAuxFieldGroup(axfgId);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }
    
        axfgDO.setId(axfgId);
        setFieldsInRPC(rpc, axfgDO);
        return rpc;
    }
    
    public AuxiliaryForm commitUpdate(AuxiliaryForm rpc) throws Exception {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = getAuxFieldGroupDOFromRPC(rpc);
        IntegerField axfgId = rpc.id;
        List<AuxFieldViewDO> axfDOList = getAuxFieldDOListFromRPC(axfgId.getValue(), rpc);               
    
        try {
            remote.updateAuxiliary(axfgDO,axfDOList);
            axfgDO = remote.getAuxFieldGroup(axfgId.getValue());
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            throw new Exception(e.getMessage());            
        }
           
        setFieldsInRPC(rpc, axfgDO);
        return rpc;
    }
    
    public AuxiliaryForm commitDelete(AuxiliaryForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
    public AuxiliaryForm abort(AuxiliaryForm rpc) throws Exception {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = remote.getAuxFieldGroupAndUnlock(rpc.entityKey,SessionManager.getSession().getId());
        setFieldsInRPC(rpc, axfgDO);
        fillAuxFieldTable(rpc.entityKey, rpc);
        return rpc;
    }
       

    public AuxiliaryForm fetch(AuxiliaryForm rpc) throws Exception {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = remote.getAuxFieldGroup(rpc.entityKey);
        setFieldsInRPC(rpc, axfgDO);        
        fillAuxFieldTable(rpc.entityKey, rpc);
        return rpc;
    }

    public AuxiliaryForm fetchForUpdate(AuxiliaryForm rpc) throws Exception {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = new AuxFieldGroupDO(); 
        try{
            axfgDO = remote.getAuxFieldGroupAndLock(rpc.entityKey, SessionManager.getSession().getId());
        }   catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        setFieldsInRPC(rpc, axfgDO);
        fillAuxFieldTable(rpc.entityKey, rpc);
        return rpc;
    }

    public String getXML() throws Exception {
        return ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/auxiliary.xsl");
    }       
    
    public AuxiliaryForm getScreen(AuxiliaryForm rpc) throws Exception {        
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/auxiliary.xsl");        
         
        return rpc;
    }
    
    public HashMap<String, FieldType> getXMLData() throws Exception {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/auxiliary.xsl"));       

        HashMap<String, FieldType> map = new HashMap<String, FieldType>();
        map.put("xml", xml);        
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws Exception {
        return null;
    }

    public TableDataModel getMatches(String cat,TableDataModel model,String match,
                                HashMap<String, FieldType> params) throws Exception {
        TableDataModel<TableDataRow<Integer>> dataModel = null;
        List<IdNameVO> entries;
        ScriptletRemote sremote;
        AnalyteRemote aremote;
        MethodRemote mremote;
        
        if(("analyte").equals(cat)) {
            aremote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
            entries = aremote.fetchByName(match.trim() + "%", 10);
            //dataModel = getAutocompleteModel(entries);
        } else if(("method").equals(cat)) {
            mremote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
            entries = mremote.findByName(match.trim() + "%", 10);
            //dataModel = getAutocompleteModel(entries);
        } else if(("scriptlet").equals(cat)) {
            sremote = (ScriptletRemote)EJBFactory.lookup("openelis/ScriptletBean/remote");
            ArrayList<IdNameVO> scripts = sremote.findByName(match.trim() + "%", 10);
            //dataModel = getAutocompleteModel(scripts);
        }
        
        return dataModel;
    }  
    
    public AuxiliaryGeneralPurposeRPC getAuxFieldValueModel(AuxiliaryGeneralPurposeRPC rpc) {
      AuxiliaryRemote remote;
      List<AuxFieldValueDO> valueDOList;
      TableDataRow<Integer> set;
      
      remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
      valueDOList = remote.getAuxFieldValues(rpc.key);
      
      for(AuxFieldValueDO valueDO: valueDOList) {
          set = rpc.auxFieldValueModel.createNewSet();       
          set.key = valueDO.getId();      
          set.cells[0].setValue(new TableDataRow<Integer>(valueDO.getTypeId()));
          set.cells[1].setValue(valueDO.getValue());              
          rpc.auxFieldValueModel.add(set);
      }
      
      return rpc;
    }
    
    public AuxiliaryGeneralPurposeRPC getEntryIdForSystemName(AuxiliaryGeneralPurposeRPC rpc) {
        DictionaryRemote remote =  (DictionaryRemote)EJBFactory.lookup("openelis/DictionaryBean/remote");                            
        try{
            rpc.key = (remote.fetchBySystemName(rpc.stringValue)).getId();            
        }catch(Exception ex) {
            ex.printStackTrace();              
        }
           
          return rpc;
    }
    
    public AuxiliaryGeneralPurposeRPC getCategorySystemName(AuxiliaryGeneralPurposeRPC rpc) { 
      DictionaryRemote remote =  (DictionaryRemote)EJBFactory.lookup("openelis/DictionaryBean/remote");       
      try{
          rpc.stringValue = (remote.fetchById(rpc.key)).getSystemName();
       }catch(Exception ex) {
        ex.printStackTrace();
     }
    return rpc;
   }
    
    public AuxiliaryGeneralPurposeRPC getEntryIdForEntryText(AuxiliaryGeneralPurposeRPC rpc){
        DictionaryRemote remote =  (DictionaryRemote)EJBFactory.lookup("openelis/DictionaryBean/remote"); 
        List<DictionaryDO> list;
        
        try{
            list = remote.fetchByEntry(rpc.stringValue,100000);            
            rpc.key = list.get(0).getId();
          }catch(Exception ex) {
              ex.printStackTrace();              
          }
           
          return rpc;
    }
    
    private AuxFieldGroupDO getAuxFieldGroupDOFromRPC(AuxiliaryForm form){
        AuxFieldGroupDO auxFieldGroupDO = new AuxFieldGroupDO();
        if(form.id!=null)
         auxFieldGroupDO.setId(form.id.getValue());
        auxFieldGroupDO.setName(((String)form.name.getValue()));   
        Datetime activeBegin = form.activeBegin.getValue();
        if (activeBegin != null)
            auxFieldGroupDO.setActiveBegin(activeBegin);

        Datetime activeEnd = form.activeEnd.getValue();
        if (activeEnd != null)
            auxFieldGroupDO.setActiveEnd(activeEnd);

        auxFieldGroupDO.setDescription(form.description.getValue());
        auxFieldGroupDO.setIsActive(form.isActive.getValue());
        
        return auxFieldGroupDO;
    }
       
    
    private void setFieldsInRPC(AuxiliaryForm form,AuxFieldGroupDO axfgDO) {
        form.id.setValue(axfgDO.getId());
        form.activeBegin.setValue(Datetime.getInstance(Datetime.YEAR,Datetime.DAY,
                                                          axfgDO.getActiveBegin()
                                                                       .getDate()));
        form.activeEnd.setValue(Datetime.getInstance(Datetime.YEAR,Datetime.DAY,
                                                          axfgDO.getActiveEnd()
                                                                       .getDate()));
        form.isActive.setValue(axfgDO.getIsActive());         
        form.name.setValue(axfgDO.getName());        
        form.description.setValue(axfgDO.getDescription());
        
    }
    
     private List<AuxFieldViewDO> getAuxFieldDOListFromRPC(Integer key,AuxiliaryForm form) {
         TableDataModel<TableDataRow<Integer>> model = form.auxFieldTable.getValue();
         TableDataRow<Integer> row = null;
         List<AuxFieldViewDO> afDOList = new ArrayList<AuxFieldViewDO>();
         List<AuxFieldValueDO> valueDOList = null;
         AuxFieldViewDO afDO = null;
         
         for(int i= 0; i < model.size(); i++) {
              row = model.get(i);
              afDO = new AuxFieldViewDO();                          
              afDO.setId(row.key);                             
              //afDO.setDelete(false);              
              afDO.setUnitOfMeasureId((Integer)((DropDownField)row.cells[2]).getSelectedKey());
              afDO.setAnalyteId((Integer)((DropDownField)row.cells[0]).getSelectedKey());
              afDO.setAuxFieldGroupId(key);
              afDO.setDescription((String)row.cells[6].getValue());
              afDO.setIsActive((String)row.cells[3].getValue());
              afDO.setIsReportable((String)row.cells[5].getValue());
              afDO.setIsRequired((String)row.cells[4].getValue());
              afDO.setMethodId((Integer)((DropDownField)row.cells[1]).getSelectedKey());
              afDO.setScriptletId((Integer)((DropDownField)row.cells[7]).getSelectedKey());
              afDO.setSortOrder(i);
              valueDOList = getAuxFieldValueDOList(row.key,(TableDataModel)row.getData());
             //afDO.setAuxFieldValues(valueDOList);
              afDOList.add(afDO);
         }

         if(model.getDeletions()!=null) {
             for (int i = 0; i < model.getDeletions().size(); i++) {
                row = (TableDataRow<Integer>)model.getDeletions().get(i);
                afDO = new AuxFieldViewDO();
                afDO.setId(row.key);
               // afDO.setDelete(true);
                valueDOList = getAuxFieldValueDOList(row.key,
                                                     (TableDataModel)row.getData());
               // afDO.setAuxFieldValues(valueDOList);
                afDOList.add(afDO);
            }     
          model.getDeletions().clear();
       }            
         return afDOList;         
     }    
    
    private List<AuxFieldValueDO> getAuxFieldValueDOList(Integer auxFieldId,TableDataModel model) {
        List<AuxFieldValueDO> valueDOList;
        AuxFieldValueDO valueDO;
        TableDataRow<Integer> row; 
        DropDownField<Integer> type;         
        int j;
        ArrayList<TableDataRow<Integer>> deletions;
        
        valueDOList = null;                                      
        
        if(model != null) { 
            valueDOList = new ArrayList<AuxFieldValueDO>();
            for(j = 0 ; j <  model.size(); j++) {              
                valueDO = new AuxFieldValueDO();  
                row = model.get(j);           
                valueDO.setId(row.key);                 
                valueDO.setAuxFieldId(auxFieldId);
              //  valueDO.setDelete(false);
             
                type = (DropDownField<Integer>)row.cells[0];            
                valueDO.setTypeId((Integer)type.getSelectedKey());
                
                valueDO.setValue(((StringField)row.cells[1]).getValue());                                                
            
                valueDOList.add(valueDO);
            }
         
            deletions = model.getDeletions();
            
            if(deletions != null) {
                for(j = 0 ; j <  deletions.size(); j++) {
                    valueDO = new AuxFieldValueDO();  
                    row = deletions.get(j);
                    valueDO.setId(row.key);    
           //         valueDO.setDelete(true);       
                    valueDOList.add(valueDO);
                }
                deletions.clear();
            }
        } 
        return valueDOList;     
     }                 
    
    private void fillAuxFieldTable(Integer key,AuxiliaryForm form) {
        TableDataRow<Integer> row = null;               
        AuxFieldViewDO afDO = null;
        TableDataRow<Integer> set;
        TableDataModel<TableDataRow<Integer>> acModel;
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        TableDataModel<TableDataRow<Integer>> model = form.auxFieldTable.getValue();
        List<AuxFieldViewDO> afDOList = remote.getAuxFields(key); 
        model.clear();
        
        for(int i = 0; i < afDOList.size(); i++) {
            row = model.createNewSet();
            afDO = afDOList.get(i);                        
            row.key = (afDO.getId());
                        
            set = new TableDataRow<Integer>(afDO.getAnalyteId(),new StringObject(afDO.getAnalyteName()));       
            acModel = new TableDataModel<TableDataRow<Integer>>();
            acModel.add(set);            
            ((DropDownField)row.cells[0]).setModel(acModel);
            row.cells[0].setValue(set);
            
            acModel = new TableDataModel<TableDataRow<Integer>>();
            ((DropDownField)row.cells[1]).setModel(acModel);
            if(afDO.getMethodId()!=null) {
                 set = new TableDataRow<Integer>(afDO.getMethodId(),new StringObject(afDO.getMethodName()));                        
                 acModel.add(set);                             
                 row.cells[1].setValue(set);
            }
                        
            row.cells[2].setValue(new TableDataRow<Integer>(afDO.getUnitOfMeasureId()));            
            row.cells[3].setValue(afDO.getIsActive());
            row.cells[4].setValue(afDO.getIsRequired());
            row.cells[5].setValue(afDO.getIsReportable());
            row.cells[6].setValue(afDO.getDescription());      
            
            acModel = new TableDataModel<TableDataRow<Integer>>();
            ((DropDownField)row.cells[7]).setModel(acModel);
            if(afDO.getScriptletId()!=null) {
                set = new TableDataRow<Integer>(afDO.getScriptletId(),new StringObject(afDO.getScriptletName()));                        
                acModel.add(set);                             
                row.cells[7].setValue(set);
            }
            
            model.add(row);
        }
        
    }
    
    private TableDataModel<TableDataRow<Integer>> loadDropDown(List<IdNameDO> list) {
        TableDataRow<Integer> blankset = new TableDataRow<Integer>(null,new StringObject(""));
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
        model.add(blankset);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IdNameDO methodDO = (IdNameDO)iter.next();
            TableDataRow<Integer> set = new TableDataRow<Integer>(methodDO.getId(),new StringObject(methodDO.getName()));
            model.add(set);
        }
        return model;
    }

    private TableDataModel<TableDataRow<Integer>> getAutocompleteModel(List<IdNameDO> entries){
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {

            IdNameDO element = (IdNameDO)iter.next();
            Integer entryId = element.getId();
            String entryText = element.getName();

            TableDataRow<Integer> data = new TableDataRow<Integer>(entryId,
                                                                   new StringObject(entryText));
            dataModel.add(data);
        }
        
        return dataModel;
    }
    
    private void setRpcErrors(List exceptionList, AuxiliaryForm form) {
        TableField auxFieldTable = form.auxFieldTable;        
        HashMap<String,AbstractField> map = null;
        TableDataRow row = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
        // we need to get the keys and look them up in the resource bundle for
        // internationalization
        for (int i = 0; i < exceptionList.size(); i++) {
            if (exceptionList.get(i) instanceof TableFieldErrorException) {
                 TableFieldErrorException ferrex = (TableFieldErrorException)exceptionList.get(i);
                 String fieldName = ferrex.getFieldName();
                 int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                  auxFieldTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                 row = auxFieldTable.getValue().get(index);
                 if(ferrex.getChildExceptionList() != null) {                  
                  setAuxFieldValueErrors(ferrex.getChildExceptionList(),(TableDataModel)row.getData(),form);  
                 }
            } else if (exceptionList.get(i) instanceof FieldErrorException) {
                map.get(((FieldErrorException)exceptionList.get(i)).getFieldName())
                .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));   
            }
            // if the error is on the entire form
            else if (exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }
        form.status = Form.Status.invalid;
        
    }   
    
    private void setAuxFieldValueErrors(List exceptionList,TableDataModel model,AuxiliaryForm form) {
        AbstractField field = null;
        List<String> fieldIndex = form.auxFieldValueTable.getFieldIndex();
        int fi = -1;
        TableDataRow row = null;
        TableFieldErrorException ferrex = null;
        String fieldName = null, error = null;
        int index = -1;
        
        for (int i = 0; i < exceptionList.size(); i++) {             
          ferrex = (TableFieldErrorException)exceptionList.get(i);
          fieldName = ferrex.getFieldName();
          index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
          fi = fieldIndex.indexOf(fieldName); 
          row = (TableDataRow)model.get(index);
          field = (AbstractField)row.cells[fi];
          error = openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage());
          if(!field.getErrors().contains(error))
              field.addError(error);                                  
        }    
        
    }
    */
    public ArrayList<AuxFieldGroupDO> fetchActive(){
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        return remote.fetchActive();
    }
    
    //manager methods
    public AuxFieldGroupManager fetchGroupById(Integer id) throws Exception {
        return remoteManager().fetchById(id);
    }
    
    public AuxFieldGroupManager fetchGroupByIdWithFields(Integer id) throws Exception {
        return remoteManager().fetchWithFields(id);
    }
    
    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception {
        return remoteManager().fetchForUpdate(id);
    }
    
    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception {
        return remoteManager().abortUpdate(id);
    }
    
    public AuxFieldManager fetchAuxFieldById(Integer id) throws Exception {
        return remoteManager().fetchAuxFieldById(id);
    }
    
    public AuxFieldManager fetchByGroupIdWithValues(Integer groupId) throws Exception {
        try{
        return remoteManager().fetchByGroupIdWithValues(groupId);
        }catch(Exception e){
            throw e;
        }
    }
    
    public AuxFieldManager fetchByGroupId(Integer auxFieldGroupId) throws Exception {
        return remoteManager().fetchByGroupId(auxFieldGroupId);
    }
    
    public AuxFieldValueManager fetchByAuxFieldId(Integer auxFieldId) throws Exception {
        return remoteManager().fetchFieldValueByFieldId(auxFieldId);
    }
    
    private AuxFieldGroupManagerRemote remoteManager() {
        return (AuxFieldGroupManagerRemote)EJBFactory.lookup("openelis/AuxFieldGroupManagerBean/remote");
    }
}
