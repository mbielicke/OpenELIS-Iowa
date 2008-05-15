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
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.meta.CategoryMeta;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import edu.uiowa.uhl.security.domain.SectionIdNameDO;
import edu.uiowa.uhl.security.remote.SystemUserUtilRemote;

public class DictionaryService implements AppScreenFormServiceInt,
                                           AutoCompleteServiceInt{

    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 19;           

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List systemNames = new ArrayList();
        if(rpcSend == null){          
//          need to get the query rpc out of the cache
            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("DictionaryQuery");
    
           if(rpc == null)
               throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
                
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
            fields.remove("dictEntTable");
           
                try{                                        
                    systemNames = remote.query(fields,0,leftTableRowsPerPage);                     
            }catch(Exception e){
                e.printStackTrace();
                throw new RPCException(e.getMessage());                
            }
    
//          need to save the rpc used to the encache
            if(SessionManager.getSession().getAttribute("DictionaryQuery") == null)
                SessionManager.getSession().setAttribute("DictionaryQuery", rpcSend);
       }
        
        int i=0;
        model.clear();    
        while(i < systemNames.size() && i < leftTableRowsPerPage) {
    
            IdNameDO resultDO = (IdNameDO)systemNames.get(i);
            //category id
            Integer idResult = resultDO.getId();
            //category name
            String sysNameResult = resultDO.getName();
                        
    
            DataSet row = new DataSet();
            
            NumberObject id = new NumberObject(NumberObject.INTEGER);
            id.setValue(idResult);
            
            StringObject sysName = new StringObject();
                        
            sysName.setValue((sysNameResult != null ? sysNameResult.trim() : null));    
            
            row.setKey(id);                      
            row.addObject(sysName);
            model.add(row);
            i++;
         }        
        
        return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {       
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");         
        NumberField catId = (NumberField) rpcSend.getField(CategoryMeta.ID);
        
        CategoryDO categoryDO = getCategoryDOFromRPC(rpcSend);
        
                
        TableModel dictEntryTable = (TableModel)rpcSend.getField("dictEntTable").getValue();
        
        ArrayList<DictionaryDO> dictDOList = getDictionaryEntriesFromRPC(dictEntryTable, (Integer)catId.getValue());
        
        List<Exception> exceptionList = remote.validateForAdd(categoryDO,dictDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, dictEntryTable, rpcSend);   
            //rpcSend.status = IForm.INVALID_FORM;
            return rpcSend;
        } 
        
        Integer categoryId = null;
       try{
          categoryId = remote.updateCategory(categoryDO, dictDOList);          
       }catch(Exception ex){
           exceptionList = new ArrayList<Exception>();
           exceptionList.add(ex);
           
           setRpcErrors(exceptionList, dictEntryTable, rpcSend);
           
           return rpcSend;
       }
       
         categoryDO.setId(categoryId);
           
         setFieldsInRPC(rpcReturn, categoryDO);                                   

        // rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),dictDOList));
                  
        return rpcReturn;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");         
        NumberField categoryId = (NumberField) rpcSend.getField(CategoryMeta.ID);
        
        CategoryDO categoryDO = getCategoryDOFromRPC(rpcSend);
        
                
        TableModel dictEntryTable = (TableModel)rpcSend.getField("dictEntTable").getValue();
        
        ArrayList<DictionaryDO> dictDOList = getDictionaryEntriesFromRPC(dictEntryTable, (Integer)categoryId.getValue());
        List<Exception> exceptionList = remote.validateForAdd(categoryDO,dictDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, dictEntryTable, rpcSend);   
            return rpcSend;
        } 
        
        
       try{
           remote.updateCategory(categoryDO, dictDOList);          
       }catch(Exception ex){
           if(ex instanceof EntityLockedException)
               throw new RPCException(ex.getMessage());
           
           exceptionList = new ArrayList<Exception>();
           exceptionList.add(ex);
           
           setRpcErrors(exceptionList, dictEntryTable, rpcSend);
           
           return rpcSend;
       }
         
         setFieldsInRPC(rpcReturn,categoryDO);
         
        // rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),dictDOList));
         
         
        return rpcReturn;
    }

   

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
            Integer categoryId = (Integer)key.getKey().getValue();
    
            CategoryDO catDO = new CategoryDO();
             try{
                catDO = remote.getCategoryAndUnlock(categoryId);
             }catch(Exception ex){
                 throw new RPCException(ex.getMessage());
             }  
    //      set the fields in the RPC
             setFieldsInRPC(rpcReturn,catDO);           
                                                       
            List addressList = remote.getDictionaryEntries(categoryId);
            rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
            
            return rpcReturn;
        }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {               
        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer categoryId = (Integer)key.getKey().getValue();        
        CategoryDO catDO = remote.getCategory(categoryId);
//      set the fields in the RPC
                    
        setFieldsInRPC(rpcReturn,catDO);               
        List addressList = remote.getDictionaryEntries(categoryId);
        rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
        
        return rpcReturn;
     
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer categoryId = (Integer)key.getKey().getValue();

        CategoryDO catDO = new CategoryDO();
         try{
            catDO = remote.getCategoryAndLock(categoryId);
         }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
         setFieldsInRPC(rpcReturn,catDO);                                                  
        List addressList = remote.getDictionaryEntries(categoryId);
        rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
        
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

    public TableModel fillDictEntryTable(TableModel dictEntryModel, List contactsList){        
         try{
             dictEntryModel.reset();
                          
             
             for(int iter = 0;iter < contactsList.size();iter++) {
                 DictionaryDO dictDO  = (DictionaryDO)contactsList.get(iter);

                    TableRow row = dictEntryModel.createRow();
                    NumberField id = new NumberField(NumberObject.INTEGER);
                     id.setValue(dictDO.getId());
                   
                     row.addHidden("id", id);
                     
                     NumberField relEntryId = new NumberField(NumberObject.INTEGER);
                     relEntryId.setValue(dictDO.getRelatedEntryId());

                     row.addHidden("relEntryId", relEntryId);
                     
                     row.getColumn(0).setValue(dictDO.getIsActive());                                         
                     row.getColumn(1).setValue(dictDO.getSystemName());
                                          
                     
                     row.getColumn(2).setValue(dictDO.getLocalAbbrev());
                     row.getColumn(3).setValue(dictDO.getEntry());
                                                                                                        
                     
//                   we need to create a dataset for the parent organization auto complete
                    if(dictDO.getRelatedEntryId() == null)
                       row.getColumn(4).setValue(null);
                    else{
                        DataSet relEntrySet = new DataSet();
                        NumberObject idObj = new NumberObject(NumberObject.INTEGER);
                        StringObject text = new StringObject();
                        idObj.setValue(dictDO.getRelatedEntryId());
                        text.setValue(dictDO.getRelatedEntryText().trim());
                        relEntrySet.setKey(id);
                        relEntrySet.addObject(text);
                        row.getColumn(4).setValue(relEntrySet);
                    }
                     dictEntryModel.addRow(row);
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
          NumberField idField = new NumberField(NumberObject.INTEGER);
          idField.setValue(entryId);          
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
          NumberField idField = new NumberField(NumberObject.INTEGER);
          idField.setValue(entryId);          
          return idField;
         }catch(Exception ex){
             throw ex;
         }
      }

    // the method called to load the dropdowns on the screen
    public DataModel getInitialModel(String cat) { 
         
        DataModel model = new DataModel();
        DataSet blankset = new DataSet();
        
        StringObject blankStringId = new StringObject();
                                    
        blankStringId.setValue("");
        blankset.addObject(blankStringId);
        
        if(cat.equals("section")){
         NumberObject blankNumberId = new NumberObject(NumberObject.INTEGER);
         blankNumberId.setValue(-1);
         blankset.setKey(blankNumberId);
        }                        
        
        model.add(blankset);
        
        if(cat.equals("section")){
        
         SystemUserUtilRemote utilRemote  = (SystemUserUtilRemote)EJBFactory.lookup("SystemUserUtilBean/remote");
         List<SectionIdNameDO> sections = utilRemote.getSections("openelis");
                      
                                 
         if(sections!=null){

          for (Iterator iter = sections.iterator(); iter.hasNext();) {
              SectionIdNameDO sectionDO = (SectionIdNameDO)iter.next();
                           
                DataSet set = new DataSet();
                //id
                Integer dropdownId = sectionDO.getId();
                //entry
                String dropdownText = sectionDO.getName();
                
                StringObject textObject = new StringObject();
                NumberObject numberId = new NumberObject(NumberObject.INTEGER);
                
                textObject.setValue(dropdownText);
                set.addObject(textObject);
             
                numberId.setValue(dropdownId);
                set.setKey(numberId);              
                
                model.add(set);                          
           }                           
          }
        }        
        return model;
        
    }

   //  the method called to load the matching entries in the autocomplete box(es) on the screen
    public DataModel getMatches(String cat, DataModel model, String match) {         
        
       if(("relatedEntry").equals(cat)){ 
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
        List entries = remote.getMatchingEntries(match+"%", 10);
        DataModel dataModel = new DataModel();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            
            IdNameDO element = (IdNameDO)iter.next();
            Integer entryId = element.getId();                   
            String entryText = element.getName();
            
            DataSet data = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(NumberObject.INTEGER);
            idObject.setValue(entryId);
            data.setKey(idObject);
            
            StringObject nameObject = new StringObject();
            nameObject.setValue(entryText.trim());
            data.addObject(nameObject);
            
            //add the dataset to the datamodel
            dataModel.add(data);
        }       
        
        return dataModel;
       }
      return null; 
    }

    private void setFieldsInRPC(FormRPC rpcReturn, CategoryDO catDO){
        rpcReturn.setFieldValue(CategoryMeta.ID, catDO.getId());
        rpcReturn.setFieldValue(CategoryMeta.SYSTEM_NAME,catDO.getSystemName());
        rpcReturn.setFieldValue(CategoryMeta.NAME,catDO.getName());
        rpcReturn.setFieldValue(CategoryMeta.DESCRIPTION,catDO.getDescription());    
        if(catDO.getSection()!=null){
            rpcReturn.setFieldValue(CategoryMeta.SECTION,catDO.getSection());
         }else{
            rpcReturn.setFieldValue(CategoryMeta.SECTION,new Integer(-1));  
         }
    }
    
    private CategoryDO getCategoryDOFromRPC(FormRPC rpcSend){        
        NumberField categoryId = (NumberField) rpcSend.getField(CategoryMeta.ID);
        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setId((Integer)categoryId.getValue());
        categoryDO.setDescription(((String)rpcSend.getFieldValue(CategoryMeta.DESCRIPTION)).trim());
        categoryDO.setName(((String)rpcSend.getFieldValue(CategoryMeta.NAME)).trim());
        categoryDO.setSystemName(((String)rpcSend.getFieldValue(CategoryMeta.SYSTEM_NAME)).trim());
                
        if(!new Integer(-1).equals(rpcSend.getFieldValue(CategoryMeta.SECTION)))
           categoryDO.setSection((Integer)rpcSend.getFieldValue(CategoryMeta.SECTION));
        
        return categoryDO; 
    }
        
    
    private ArrayList<DictionaryDO> getDictionaryEntriesFromRPC(TableModel dictEntryTable, Integer categoryId){
        
        ArrayList<DictionaryDO> dictDOList = new ArrayList<DictionaryDO>();
        for(int iter = 0; iter < dictEntryTable.numRows(); iter++){
            
         TableRow row = dictEntryTable.getRow(iter);
         DictionaryDO dictDO = new DictionaryDO();
                          
         String sysName = (String)((StringField)row.getColumn(1)).getValue();
         String entry = (String)((StringField)row.getColumn(3)).getValue();
         
           dictDO.setSystemName(sysName.trim());           
           dictDO.setEntry(entry.trim());  
           NumberField id = (NumberField)row.getHidden("id");                 
         
            StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
              if(deleteFlag == null){
                dictDO.setDelete(false);
              }else{
              dictDO.setDelete("Y".equals(deleteFlag.getValue()));
             }
         
             if(id!=null){
              if(id.getValue()!=null){
                dictDO.setId((Integer)id.getValue());
              } 
             } 
         
            
             DropDownField relEntryId = (DropDownField)row.getColumn(4); 
              if(relEntryId!=null){
                 if(relEntryId.getValue()!=null){
                   dictDO.setRelatedEntryId((Integer)relEntryId.getValue());
                 }
                }
                     
              CheckField isActive =  (CheckField)row.getColumn(0);              
              dictDO.setIsActive((String)isActive.getValue());
              
             dictDO.setCategory(categoryId);         
             dictDO.setLocalAbbrev(((String)((StringField)row.getColumn(2)).getValue()).trim());         
             dictDOList.add(dictDO);             
          }
        
        return dictDOList;
    }

	private void setRpcErrors(List exceptionList, TableModel entriesTable, FormRPC rpcSend){
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the entries table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                TableRow row = entriesTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                row.getColumn(entriesTable.getColumnIndexByFieldName(((TableFieldErrorException)exceptionList.get(i)).getFieldName()))
                                                                        .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        rpcSend.status = IForm.INVALID_FORM;
    }
}
