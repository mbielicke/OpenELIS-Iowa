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
package org.openelis.modules.dictionary.server;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.modules.dictionary.client.DictionaryEntryTextRPC;
import org.openelis.modules.dictionary.client.DictionaryForm;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DictionaryService implements AppScreenFormServiceInt<DictionaryForm,Query<TableDataRow<Integer>>>,
                                           AutoCompleteServiceInt{

    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 19;           

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final CategoryMetaMap CatMeta = new CategoryMetaMap();  
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List systemNames = new ArrayList();

            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
           
            try{                                        
                   systemNames = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);        
            }catch(LastPageException e) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
            }catch(Exception e){
                e.printStackTrace();
                throw new RPCException(e.getMessage());                
            }
    
        
        int i=0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else 
            query.results.clear();
        while(i < systemNames.size() && i < leftTableRowsPerPage) {
    
            IdNameDO resultDO = (IdNameDO)systemNames.get(i);                        
            query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
         }        
        
        return query;
    }

    public DictionaryForm commitAdd(DictionaryForm rpc) throws RPCException {       
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");         
        IntegerField catId = (IntegerField) rpc.id;
        
        CategoryDO categoryDO = getCategoryDOFromRPC(rpc);
        
                
        TableDataModel<TableDataRow<Integer>> dictEntryTable = rpc.dictEntTable.getValue();
        
        ArrayList<DictionaryDO> dictDOList = getDictionaryEntriesFromRPC(dictEntryTable, catId.getValue());
        
        List<Exception> exceptionList = remote.validateForAdd(categoryDO,dictDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList,rpc);   
            return rpc;
        } 
        
        Integer categoryId = null;
       try{
          categoryId = remote.updateCategory(categoryDO, dictDOList);          
       }catch(Exception ex){
           exceptionList = new ArrayList<Exception>();
           exceptionList.add(ex);
           
           setRpcErrors(exceptionList,rpc);
           
           return rpc;
       }
       
         categoryDO.setId(categoryId);
           
         setFieldsInRPC(rpc, categoryDO);                                            
                  
        return rpc;
    }

    public DictionaryForm commitUpdate(DictionaryForm rpc) throws RPCException {
        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");         
        IntegerField categoryId = (IntegerField) rpc.id;
        
        CategoryDO categoryDO = getCategoryDOFromRPC(rpc);
        
                
        TableDataModel<TableDataRow<Integer>> dictEntryTable = rpc.dictEntTable.getValue();
        
        ArrayList<DictionaryDO> dictDOList = getDictionaryEntriesFromRPC(dictEntryTable, categoryId.getValue());
        List<Exception> exceptionList = remote.validateForAdd(categoryDO,dictDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList,rpc);   
            return rpc;
        } 
        
        
       try{
           remote.updateCategory(categoryDO, dictDOList);          
       }catch(Exception ex){
           if(ex instanceof EntityLockedException)
               throw new RPCException(ex.getMessage());           
           ex.printStackTrace();
           exceptionList = new ArrayList<Exception>();
           exceptionList.add(ex);
           
           setRpcErrors(exceptionList, rpc);
           
           return rpc;
       }
         
         setFieldsInRPC(rpc,categoryDO);                       
         
        return rpc;
    }

   

    public DictionaryForm commitDelete(DictionaryForm rpc) throws RPCException {
        // Delete functionality is not needed on the screen 
        return null;
    }

    public DictionaryForm abort(DictionaryForm rpc) throws RPCException {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
            Integer categoryId = (Integer)rpc.entityKey;
    
            CategoryDO catDO = new CategoryDO();
             try{
                catDO = remote.getCategoryAndUnlock(categoryId, SessionManager.getSession().getId());
             }catch(Exception ex){
                 throw new RPCException(ex.getMessage());
             }  
    //      set the fields in the RPC
             setFieldsInRPC(rpc,catDO);           
                                                       
            List addressList = remote.getDictionaryEntries(categoryId);
            rpc.dictEntTable.setValue(fillDictEntryTable(rpc.dictEntTable.getValue(),addressList));
            
            return rpc;
        }

    public DictionaryForm fetch(DictionaryForm rpc) throws RPCException {               
        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer categoryId = rpc.entityKey;        
        CategoryDO catDO = remote.getCategory(categoryId);
//      set the fields in the RPC
                    
        setFieldsInRPC(rpc,catDO);               
        List addressList = remote.getDictionaryEntries(categoryId);
        rpc.dictEntTable.setValue(fillDictEntryTable(rpc.dictEntTable.getValue(),addressList));
        
        return rpc;
     
    }

    public DictionaryForm fetchForUpdate(DictionaryForm rpc) throws RPCException {
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer categoryId = rpc.entityKey;

        CategoryDO catDO = new CategoryDO();
         try{
            catDO = remote.getCategoryAndLock(categoryId, SessionManager.getSession().getId());
         }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
         setFieldsInRPC(rpc,catDO);                                                  
        List addressList = remote.getDictionaryEntries(categoryId);
        rpc.dictEntTable.setValue(fillDictEntryTable(rpc.dictEntTable.getValue(),addressList));
        
        return rpc;
    }
    
    public DictionaryForm getScreen(DictionaryForm rpc) throws RPCException {        
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/dictionary.xsl");        
        TableDataModel<TableDataRow<Integer>> sectionDropDownField = (TableDataModel<TableDataRow<Integer>>)CachingManager.getElement("InitialData", "sectionDropDown");             
        
        if(sectionDropDownField == null) 
           rpc.sections = getInitialModel("section");                            
                      
        return rpc;
    }
    

    public TableDataModel<TableDataRow<Integer>> fillDictEntryTable(TableDataModel<TableDataRow<Integer>> dictEntryModel, List contactsList){        
         try{
             dictEntryModel.clear();
                          
             
             for(int iter = 0;iter < contactsList.size();iter++) {
                 DictionaryDO dictDO  = (DictionaryDO)contactsList.get(iter);

                    TableDataRow<Integer> row = dictEntryModel.createNewSet();
                    IntegerField id = new IntegerField(dictDO.getId());
                   
                    DataMap data = new DataMap();
                    data.put("id", id);
                    
                    IntegerField relEntryId = new IntegerField(dictDO.getRelatedEntryId());
                    data.put("relEntryId", relEntryId);
                     
                    row.setData(data);
                    row.key = dictDO.getId();
                    
                    ((Field)row.cells[0]).setValue(dictDO.getIsActive());                      
                    ((Field)row.cells[1]).setValue(dictDO.getSystemName());                                                               
                    ((Field)row.cells[2]).setValue(dictDO.getLocalAbbrev());
                    ((Field)row.cells[3]).setValue(dictDO.getEntry());
                                                                                                        
                     
                    //we need to create a dataset for the parent organization auto complete
                    if(dictDO.getRelatedEntryId() == null)
                       ((Field)row.cells[4]).setValue(null);
                    else{
                        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
                        model.add(new TableDataRow<Integer>(dictDO.getRelatedEntryId(),new StringObject(dictDO.getRelatedEntryText())));
                        model.add(new TableDataRow<Integer>(-1,new StringObject("")));                        
                        ((DropDownField<Integer>)row.cells[4]).setModel(model);
                        ((Field)row.cells[4]).setValue(model.get(0));
                    }
                     dictEntryModel.add(row);
            } 
             
         } catch (Exception e) {

             e.printStackTrace();
             return null;
         }       
         
         return dictEntryModel;  
     }        
    
           
    
    public IntegerField getEntryIdForSystemName(StringObject systemName)throws Exception{
        try{
          CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
          Integer entryId = remote.getEntryIdForSystemName((String)systemName.getValue());
          IntegerField idField = new IntegerField(entryId);          
          return idField;
        }catch(Exception ex){
            throw ex;
        } 
      }
      
    // the method that returns a id of that row, if any, from the Dictionary table which stores the Entry specified by "entry" 
    // it throws an exception if 
      public IntegerField getEntryIdForEntry(StringObject entry)throws Exception{
         try{ 
          CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
          Integer entryId = remote.getEntryIdForEntry((String)entry.getValue());        
          IntegerField idField = new IntegerField(entryId);          
          return idField;
         }catch(Exception ex){
             throw ex;
         }
      }

    // the method called to load the dropdowns on the screen
    protected TableDataModel<TableDataRow<Integer>> getInitialModel(String cat) { 
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();             
        
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");        
        List<IdNameDO> values = remote.getSectionDropDownValues();
                                                      
         if(values!=null){
          TableDataRow<Integer> blankset = new TableDataRow<Integer>(-1,new StringObject(""));                                                            
          model.add(blankset);
             
          for (Iterator iter = values.iterator(); iter.hasNext();) {
              IdNameDO sectionDO = (IdNameDO)iter.next();                           
              TableDataRow<Integer> set = new TableDataRow<Integer>(sectionDO.getId(),new StringObject(sectionDO.getName()));         
              model.add(set);                          
           }                           
        }        
        return model;
        
    }

   //  the method called to load the matching entries in the autocomplete box(es) on the screen
    public TableDataModel<TableDataRow<Integer>> getMatches(String cat, TableDataModel model, String match, HashMap params) {         
      if("relatedEntry".equals(cat)) {        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
        List entries = remote.getMatchingEntries(match+"%", 10);
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            
            IdNameDO element = (IdNameDO)iter.next();
            
            TableDataRow<Integer> data = new TableDataRow<Integer>(element.getId(),new StringObject(element.getName()));            
            //add the dataset to the datamodel
            dataModel.add(data);
        }                
        return dataModel;
      }
      return null;
    }    
    
    public DictionaryEntryTextRPC getNumResultsAffected(DictionaryEntryTextRPC rpc) {
        CategoryRemote remote = null;        
        try { 
         remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
         rpc.count = remote.getNumResultsAffected(rpc.entryText,rpc.id);
         rpc.entryText = remote.getEntryById(rpc.id);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
         return rpc;
     }

    private void setFieldsInRPC(DictionaryForm form, CategoryDO catDO){
        form.id.setValue(catDO.getId());
        form.systemName.setValue(catDO.getSystemName());
        form.name.setValue(catDO.getName());
        form.description.setValue(catDO.getDescription());  
        
        if(catDO.getSection() != null)
            form.sectionId.setValue(new TableDataRow<Integer>(catDO.getSection()));
    }
    
    private CategoryDO getCategoryDOFromRPC(DictionaryForm form){        
        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setId(form.id.getValue());
        categoryDO.setDescription(form.description.getValue());
        categoryDO.setName(form.name.getValue());
        categoryDO.setSystemName(form.systemName.getValue());
        categoryDO.setSection((Integer)form.sectionId.getSelectedKey());
        
        return categoryDO; 
    }
        
    
    private ArrayList<DictionaryDO> getDictionaryEntriesFromRPC(TableDataModel<TableDataRow<Integer>> dictEntryTable, Integer categoryId){
        
        ArrayList<DictionaryDO> dictDOList = new ArrayList<DictionaryDO>();
        for(int iter = 0; iter < dictEntryTable.size(); iter++){
            
         TableDataRow<Integer> row = dictEntryTable.get(iter);
         DictionaryDO dictDO = new DictionaryDO();

         IntegerField id = null;
           if(row.getData()!=null)
             id = (IntegerField)((DataMap)row.getData()).get("id");           
           
           if(id!=null){              
                 dictDO.setId(id.getValue());                
              } 
         
           
            dictDO.setDelete(false);
                           
            dictDO.setIsActive((String)((CheckField)row.cells[0]).getValue());
            dictDO.setSystemName((String)((StringField)row.cells[1]).getValue()); 
            dictDO.setLocalAbbrev(((String)((StringField)row.cells[2]).getValue()));
            dictDO.setEntry((String)((StringField)row.cells[3]).getValue());                                    
            dictDO.setRelatedEntryId((Integer)((DropDownField)row.cells[4]).getSelectedKey());                                       
              
            dictDO.setCategory(categoryId);         
                      
            dictDOList.add(dictDO);             
          }
        
        if(dictEntryTable.getDeletions() != null) {
         for(int iter = 0; iter < dictEntryTable.getDeletions().size(); iter++){
            
            TableDataRow row = (TableDataRow)dictEntryTable.getDeletions().get(iter);
            DictionaryDO dictDO = new DictionaryDO();
                             
            String sysName = (String)((StringField)row.cells[1]).getValue();
            String entry = (String)((StringField)row.cells[3]).getValue();
            
              dictDO.setSystemName(sysName);           
              dictDO.setEntry(entry);  
              IntegerField id = null;
              if(row.getData()!=null)
                id = (IntegerField)((DataMap)row.getData()).get("id");           
              
              if(id!=null){                  
                    dictDO.setId(id.getValue());                   
                 } 
              
              dictDO.setDelete(true);
                                               
              dictDO.setIsActive((String)row.cells[0].getValue());
              dictDO.setSystemName((String)row.cells[1].getValue()); 
              dictDO.setLocalAbbrev(((String)row.cells[2].getValue()));
              dictDO.setEntry((String)row.cells[3].getValue());                                    
              dictDO.setRelatedEntryId((Integer)((DropDownField<Integer>)row.cells[4]).getSelectedKey());                                       
                
              dictDO.setCategory(categoryId);         
                        
              dictDOList.add(dictDO);             
             }
         dictEntryTable.getDeletions().clear();
        } 
        return dictDOList;
    }

	private void setRpcErrors(List exceptionList, DictionaryForm form){
        TableField entriesTable = form.dictEntTable;
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
                  //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the entries table
            if(exceptionList.get(i) instanceof TableFieldErrorException){                               
                    String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName();
                    
                    int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();                    
                    entriesTable.getField(index, fieldName)
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
