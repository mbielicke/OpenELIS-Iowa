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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class DictionaryService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel>,
                                           AutoCompleteServiceInt{

    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 19;           

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final CategoryMetaMap CatMeta = new CategoryMetaMap();  
    
    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List systemNames = new ArrayList();
        if(rpcSend == null){          
//          need to get the query rpc out of the cache
            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("DictionaryQuery");
    
           if(rpc == null)
               throw new QueryException(openElisConstants.getString("queryExpiredException"));
                
            try{
                
                CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote"); 
                systemNames = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                
            }catch(Exception e){
            	if(e instanceof LastPageException){
            		throw new LastPageException(openElisConstants.getString("lastPageException"));
            	}else{
            		throw new RPCException(e.getMessage());	
            	}
            }

        } else{
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
            
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
           
                try{                                        
                    systemNames = remote.query(fields,0,leftTableRowsPerPage);                     
            }catch(Exception e){
                e.printStackTrace();
                throw new RPCException(e.getMessage());                
            }
    
//          need to save the rpc used to the ehcache
            SessionManager.getSession().setAttribute("DictionaryQuery", rpcSend);
       }
        
        int i=0;
        if(model == null)
            model = new DataModel();
        else 
            model.clear();
        while(i < systemNames.size() && i < leftTableRowsPerPage) {
    
            IdNameDO resultDO = (IdNameDO)systemNames.get(i);                        
    
            DataSet row = new DataSet();
            
            NumberObject id = new NumberObject(resultDO.getId());
            
            StringObject sysName = new StringObject(resultDO.getName());                        
            
            row.setKey(id);                      
            row.add(sysName);
            model.add(row);
            i++;
         }        
        
        return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {       
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");         
        NumberField catId = (NumberField) rpcSend.getField(CatMeta.getId());
        
        CategoryDO categoryDO = getCategoryDOFromRPC(rpcSend);
        
                
        DataModel dictEntryTable = (DataModel)rpcSend.getField("dictEntTable").getValue();
        
        ArrayList<DictionaryDO> dictDOList = getDictionaryEntriesFromRPC(dictEntryTable, (Integer)catId.getValue());
        
        List<Exception> exceptionList = remote.validateForAdd(categoryDO,dictDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList,rpcSend);   
            return rpcSend;
        } 
        
        Integer categoryId = null;
       try{
          categoryId = remote.updateCategory(categoryDO, dictDOList);          
       }catch(Exception ex){
           exceptionList = new ArrayList<Exception>();
           exceptionList.add(ex);
           
           setRpcErrors(exceptionList,rpcSend);
           
           return rpcSend;
       }
       
         categoryDO.setId(categoryId);
           
         setFieldsInRPC(rpcReturn, categoryDO);                                            
                  
        return rpcReturn;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");         
        NumberField categoryId = (NumberField) rpcSend.getField(CatMeta.getId());
        
        CategoryDO categoryDO = getCategoryDOFromRPC(rpcSend);
        
                
        DataModel dictEntryTable = (DataModel)rpcSend.getField("dictEntTable").getValue();
        
        ArrayList<DictionaryDO> dictDOList = getDictionaryEntriesFromRPC(dictEntryTable, (Integer)categoryId.getValue());
        List<Exception> exceptionList = remote.validateForAdd(categoryDO,dictDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList,rpcSend);   
            return rpcSend;
        } 
        
        
       try{
           remote.updateCategory(categoryDO, dictDOList);          
       }catch(Exception ex){
           if(ex instanceof EntityLockedException)
               throw new RPCException(ex.getMessage());
           
           exceptionList = new ArrayList<Exception>();
           exceptionList.add(ex);
           
           setRpcErrors(exceptionList, rpcSend);
           
           return rpcSend;
       }
         
         setFieldsInRPC(rpcReturn,categoryDO);                       
         
        return rpcReturn;
    }

   

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // Delete functionality is not needed on the screen 
        return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
            Integer categoryId = (Integer)((DataObject)key.getKey()).getValue();
    
            CategoryDO catDO = new CategoryDO();
             try{
                catDO = remote.getCategoryAndUnlock(categoryId, SessionManager.getSession().getId());
             }catch(Exception ex){
                 throw new RPCException(ex.getMessage());
             }  
    //      set the fields in the RPC
             setFieldsInRPC(rpcReturn,catDO);           
                                                       
            List addressList = remote.getDictionaryEntries(categoryId);
            rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((DataModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
            
            return rpcReturn;
        }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {               
        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer categoryId = (Integer)((DataObject)key.getKey()).getValue();        
        CategoryDO catDO = remote.getCategory(categoryId);
//      set the fields in the RPC
                    
        setFieldsInRPC(rpcReturn,catDO);               
        List addressList = remote.getDictionaryEntries(categoryId);
        rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((DataModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
        
        return rpcReturn;
     
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer categoryId = (Integer)((DataObject)key.getKey()).getValue();

        CategoryDO catDO = new CategoryDO();
         try{
            catDO = remote.getCategoryAndLock(categoryId, SessionManager.getSession().getId());
         }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
         setFieldsInRPC(rpcReturn,catDO);                                                  
        List addressList = remote.getDictionaryEntries(categoryId);
        rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((DataModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
        
        return rpcReturn;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/dictionary.xsl"); 
    }
    
    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/dictionary.xsl"));
        
        DataModel sectionDropDownField = (DataModel)CachingManager.getElement("InitialData", "sectionDropDown");             
        
        if(sectionDropDownField ==null)
            sectionDropDownField = getInitialModel("section");                     
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("sections",sectionDropDownField);
        return map;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public DataModel fillDictEntryTable(DataModel dictEntryModel, List contactsList){        
         try{
             dictEntryModel.clear();
                          
             
             for(int iter = 0;iter < contactsList.size();iter++) {
                 DictionaryDO dictDO  = (DictionaryDO)contactsList.get(iter);

                    DataSet row = dictEntryModel.createNewSet();
                    NumberField id = new NumberField(dictDO.getId());
                   
                    DataMap data = new DataMap();
                    data.put("id", id);
                    
                    NumberField relEntryId = new NumberField(dictDO.getRelatedEntryId());
                    data.put("relEntryId", relEntryId);
                     
                    row.setData(data);
                    
                    row.get(0).setValue(dictDO.getIsActive());                      
                    row.get(1).setValue(dictDO.getSystemName());                                                               
                    row.get(2).setValue(dictDO.getLocalAbbrev());
                    row.get(3).setValue(dictDO.getEntry());
                                                                                                        
                     
                    //we need to create a dataset for the parent organization auto complete
                    if(dictDO.getRelatedEntryId() == null)
                       row.get(4).setValue(null);
                    else{
                        DataModel model = new DataModel();
                        model.add(new NumberObject(dictDO.getRelatedEntryId()),new StringObject(dictDO.getRelatedEntryText()));
                        model.add(new NumberObject(-1),new StringObject(""));                        
                        ((DropDownField)row.get(4)).setModel(model);
                        row.get(4).setValue(model.get(0));
                    }
                     dictEntryModel.add(row);
            } 
             
         } catch (Exception e) {

             e.printStackTrace();
             return null;
         }       
         
         return dictEntryModel;  
     }        
    
           
    
    public NumberField getEntryIdForSystemName(StringObject systemName)throws Exception{
        try{
          CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
          Integer entryId = remote.getEntryIdForSystemName((String)systemName.getValue());
          NumberField idField = new NumberField(entryId);          
          return idField;
        }catch(Exception ex){
            throw ex;
        } 
      }
      
    // the method that returns a id of that row, if any, from the Dictionary table which stores the Entry specified by "entry" 
    // it throws an exception if 
      public NumberField getEntryIdForEntry(StringObject entry)throws Exception{
         try{ 
          CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
          Integer entryId = remote.getEntryIdForEntry((String)entry.getValue());        
          NumberField idField = new NumberField(entryId);          
          return idField;
         }catch(Exception ex){
             throw ex;
         }
      }

    // the method called to load the dropdowns on the screen
    protected DataModel getInitialModel(String cat) { 
        DataModel model = new DataModel();             
        
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");        
        List<IdNameDO> values = remote.getSectionDropDownValues();
                                                      
         if(values!=null){

             DataSet blankset = new DataSet();
             
             StringObject blankStringId = new StringObject("");

             blankset.add(blankStringId);
             
            
              NumberObject blankNumberId = new NumberObject(-1);
              blankset.setKey(blankNumberId);                                    
             
             model.add(blankset);
             
          for (Iterator iter = values.iterator(); iter.hasNext();) {
              IdNameDO sectionDO = (IdNameDO)iter.next();
                           
                DataSet set = new DataSet();
                
                StringObject textObject = new StringObject(sectionDO.getName());
                NumberObject numberId = new NumberObject(sectionDO.getId());
                
                set.add(textObject);
             
                set.setKey(numberId);              
                
                model.add(set);                          
           }                           
        }        
        return model;
        
    }

   //  the method called to load the matching entries in the autocomplete box(es) on the screen
    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) {         
      if("relatedEntry".equals(cat)) {        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
        List entries = remote.getMatchingEntries(match+"%", 10);
        DataModel dataModel = new DataModel();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            
            IdNameDO element = (IdNameDO)iter.next();
            
            DataSet data = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(element.getId());
            data.setKey(idObject);
            
            StringObject nameObject = new StringObject(element.getName());
            data.add(nameObject);
            
            //add the dataset to the datamodel
            dataModel.add(data);
        }                
        return dataModel;
      }
      return null;
    }
    
    public DataSet getNumResultsAffected(StringField text, NumberField id) {
       NumberObject nobj = null;
       DataSet set = null;
       StringObject sobj = null;
       CategoryRemote remote = null;
       Integer count = null;
       String entry = null;
       
       try { 
        remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        count = remote.getNumResultsAffected((String)text.getValue(),(Integer)id.getValue());
        nobj = new NumberObject(count);
        entry = remote.getEntryById((Integer)id.getValue());
        set = new DataSet();
        sobj = new StringObject(entry);
        set.add(nobj);
        set.add(sobj);
       }catch(Exception ex) {
           ex.printStackTrace();
       }
        return set;
    }

    private void setFieldsInRPC(FormRPC rpcReturn, CategoryDO catDO){
        rpcReturn.setFieldValue(CatMeta.getId(), catDO.getId());
        rpcReturn.setFieldValue(CatMeta.getSystemName(),catDO.getSystemName());
        rpcReturn.setFieldValue(CatMeta.getName(),catDO.getName());
        rpcReturn.setFieldValue(CatMeta.getDescription(),catDO.getDescription());  
        
        if(catDO.getSection() != null)
            rpcReturn.setFieldValue(CatMeta.getSectionId(), new DataSet(new NumberObject(catDO.getSection())));
    }
    
    private CategoryDO getCategoryDOFromRPC(FormRPC rpcSend){        
        NumberField categoryId = (NumberField) rpcSend.getField(CatMeta.getId());
        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setId((Integer)categoryId.getValue());
        categoryDO.setDescription(((String)rpcSend.getFieldValue(CatMeta.getDescription())));
        categoryDO.setName(((String)rpcSend.getFieldValue(CatMeta.getName())));
        categoryDO.setSystemName(((String)rpcSend.getFieldValue(CatMeta.getSystemName())));
        categoryDO.setSection((Integer)((DropDownField)rpcSend.getField(CatMeta.getSectionId())).getSelectedKey());
        
        return categoryDO; 
    }
        
    
    private ArrayList<DictionaryDO> getDictionaryEntriesFromRPC(DataModel dictEntryTable, Integer categoryId){
        
        ArrayList<DictionaryDO> dictDOList = new ArrayList<DictionaryDO>();
        for(int iter = 0; iter < dictEntryTable.size(); iter++){
            
         DataSet row = dictEntryTable.get(iter);
         DictionaryDO dictDO = new DictionaryDO();

           NumberField id = null;
           if(row.getData()!=null)
             id = (NumberField)((DataMap)row.getData()).get("id");           
           
           if(id!=null){              
                 dictDO.setId((Integer)id.getValue());                
              } 
         
           
            dictDO.setDelete(false);
                           
            dictDO.setIsActive((String)((CheckField)row.get(0)).getValue());
            dictDO.setSystemName((String)((StringField)row.get(1)).getValue()); 
            dictDO.setLocalAbbrev(((String)((StringField)row.get(2)).getValue()));
            dictDO.setEntry((String)((StringField)row.get(3)).getValue());                                    
            dictDO.setRelatedEntryId((Integer)((DropDownField)row.get(4)).getSelectedKey());                                       
              
            dictDO.setCategory(categoryId);         
                      
            dictDOList.add(dictDO);             
          }
        for(int iter = 0; iter < dictEntryTable.getDeletions().size(); iter++){
            
            DataSet row = (DataSet)dictEntryTable.getDeletions().get(iter);
            DictionaryDO dictDO = new DictionaryDO();
                             
            String sysName = (String)((StringField)row.get(1)).getValue();
            String entry = (String)((StringField)row.get(3)).getValue();
            
              dictDO.setSystemName(sysName);           
              dictDO.setEntry(entry);  
              NumberField id = null;
              if(row.getData()!=null)
                id = (NumberField)((DataMap)row.getData()).get("id");           
              
              if(id!=null){                  
                    dictDO.setId((Integer)id.getValue());                   
                 } 
              
              dictDO.setDelete(true);
                                               
              dictDO.setIsActive((String)((CheckField)row.get(0)).getValue());
              dictDO.setSystemName((String)((StringField)row.get(1)).getValue()); 
              dictDO.setLocalAbbrev(((String)((StringField)row.get(2)).getValue()));
              dictDO.setEntry((String)((StringField)row.get(3)).getValue());                                    
              dictDO.setRelatedEntryId((Integer)((DropDownField)row.get(4)).getSelectedKey());                                       
                
              dictDO.setCategory(categoryId);         
                        
              dictDOList.add(dictDO);             
             }
        dictEntryTable.getDeletions().clear();
        return dictDOList;
    }

	private void setRpcErrors(List exceptionList, FormRPC rpcSend){
        TableField entriesTable = (TableField)((FormRPC)rpcSend).getField("dictEntTable");
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
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        rpcSend.status = Status.invalid;
    }
}
