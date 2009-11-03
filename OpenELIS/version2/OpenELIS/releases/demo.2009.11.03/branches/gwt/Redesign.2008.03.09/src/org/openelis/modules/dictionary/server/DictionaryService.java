package org.openelis.modules.dictionary.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.OptionField;
import org.openelis.gwt.common.data.OptionItem;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.SessionManager;

import edu.uiowa.uhl.security.domain.SectionIdNameDO;
import edu.uiowa.uhl.security.remote.SystemUserUtilRemote;

public class DictionaryService implements AppScreenFormServiceInt,
                                           AutoCompleteServiceInt{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 19;   
   

    private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
                                                                new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
                                                                        ? "en" : (String)SessionManager.getSession().getAttribute("locale"))));

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/dictionary.xsl"); 
    }
    
    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/dictionary.xsl"));
        
        DataModel sectionDropDownField = (DataModel)CachingManager.getElement("InitialData", "sectionDropDown");
        DataModel activeDropDownField = (DataModel)CachingManager.getElement("InitialData", "activeDropDown");       
        
        if(sectionDropDownField ==null)
            sectionDropDownField = getInitialModel("section");
           
           if(activeDropDownField ==null)
               activeDropDownField = getInitialModel("isActive");
           
           /*ConstantMap cmap = new ConstantMap();
           HashMap<String,String> hmap = new HashMap<String, String>();
           hmap.put("dictSystemNameError", openElisConstants.getString("dictSystemNameError"));
           hmap.put("dictEntryError", openElisConstants.getString("dictEntryError"));
           cmap.setValue(hmap);*/
           return new DataObject[] {xml,sectionDropDownField,activeDropDownField};
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer categoryId = (Integer)key.getObject(0).getValue();
//      System.out.println("in contacts");
        CategoryDO catDO = new CategoryDO();
         try{
            catDO = remote.getCategoryAndUnlock(categoryId);
         }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
        rpcReturn.setFieldValue("categoryId", catDO.getId());
        rpcReturn.setFieldValue("systemName",catDO.getSystemName());
        rpcReturn.setFieldValue("name",catDO.getName());
        rpcReturn.setFieldValue("desc",catDO.getDescription());    
        rpcReturn.setFieldValue("sectionId",catDO.getSection());    
                
                                   
        List addressList = remote.getDictionaryEntries(categoryId);
        rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
        
        return rpcReturn;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {       
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        CategoryDO categoryDO = new CategoryDO();        
                
        categoryDO.setDescription((String)rpcSend.getFieldValue("desc"));
        categoryDO.setName((String)rpcSend.getFieldValue("name"));
        categoryDO.setSystemName((String)rpcSend.getFieldValue("systemName"));
                
        
        categoryDO.setSection((Integer)rpcSend.getFieldValue("sectionId"));
        
        
        List<DictionaryDO> dictDOList = new ArrayList<DictionaryDO>();
        
        TableModel dictEntryTable = (TableModel)rpcSend.getField("dictEntTable").getValue();
        
        for(int iter = 0; iter < dictEntryTable.numRows(); iter++){
         TableRow row = dictEntryTable.getRow(iter);
         DictionaryDO dictDO = new DictionaryDO();       
         
                              
           String sysName = (String)((StringField)row.getColumn(1)).getValue();
           String entry = (String)((StringField)row.getColumn(3)).getValue();       
              
           dictDO.setSystemName(sysName);
           dictDO.setEntry(entry);         
           //NumberField id = (NumberField)row.getHidden("id");
           NumberField relEntryId = (NumberField)row.getHidden("relEntryId");
         
          // dictDO.setId((Integer)id.getValue());
           if(relEntryId!=null){
            dictDO.setRelatedEntry((Integer)relEntryId.getValue());
           }            
           CheckField isActive =  (CheckField)row.getColumn(0);
           if(isActive.isChecked()){           
              dictDO.setIsActive("Y");
           }   
           else{
               dictDO.setIsActive("N");
           }   
           
           StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
           if(deleteFlag == null){
             dictDO.setDelete(false);
           }else{
           dictDO.setDelete("Y".equals(deleteFlag.getValue()));
          }
           
          dictDO.setSystemName((String)((StringField)row.getColumn(1)).getValue());
          dictDO.setLocalAbbrev((String)((StringField)row.getColumn(2)).getValue());
          dictDO.setEntry((String)((StringField)row.getColumn(3)).getValue());          
          if(relEntryId!=null){
              if(relEntryId.getValue()!=null){
               dictDO.setRelatedEntry((Integer)relEntryId.getValue());
             }
            }
                    
          
          dictDOList.add(dictDO);
         
        } 
        
        Integer categoryId = null;
       try{
          categoryId = remote.updateCategory(categoryDO, dictDOList);          
       }catch(Exception ex){           
           throw new RPCException(ex.getMessage());
       }
       
         categoryDO = remote.getCategory((Integer)categoryId); 
         
         rpcReturn.setFieldValue("categoryId", categoryId);        
         rpcReturn.setFieldValue("systemName", categoryDO.getSystemName());
         rpcReturn.setFieldValue("name", categoryDO.getName());
         rpcReturn.setFieldValue("desc", categoryDO.getDescription());
         rpcReturn.setFieldValue("sectionId",categoryDO.getSection()); 
                  
         
        // fillSectionOption(sectionOpt ,categoryDO);
         
         
         List addressList = remote.getDictionaryEntries(categoryId);
         rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
                  
        return rpcReturn;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {        
        //ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");        
        if(rpcSend == null){          
                
        
            FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":Category");

           if(rpc == null)
               throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));

            List categories = null;
                
            try{
                
                CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote"); 
                categories = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                
            }catch(Exception e){
	        	if(e instanceof LastPageException){
	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
	        	}else{
	        		throw new RPCException(e.getMessage());	
	        	}
            }
        
        int i=0;
        model.clear();
       // List providers = new ArrayList();
        
        //while(i < organizations.size() && i < leftTableRowsPerPage) {
        while(i < categories.size() && i < leftTableRowsPerPage) {
            //Object[] result = (Object[])organizations.get(i);
            Object[] result = (Object[])categories.get(i);
            //org id
            Integer idResult = (Integer)result[0];
            //org name
            String sysNameResult = (String)result[1];
                        

            DataSet row = new DataSet();
            
            NumberObject id = new NumberObject();
            id.setType("integer");
            id.setValue(idResult);
            
            StringObject sysName = new StringObject();
                        
            sysName.setValue(sysNameResult);    
            
            row.addObject(id);                      
            row.addObject(sysName);
            model.add(row);
            i++;
         }
        
                
        return model;   
        } else{
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
            
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();

            //contacts table
             TableModel dictEntryTable = null;
            if(rpcSend.getField("dictEntTable") != null)
                dictEntryTable = (TableModel)rpcSend.getField("dictEntTable").getValue();                            
            
            if(dictEntryTable != null){                   
                fields.put("isActive",(CollectionField)dictEntryTable.getRow(0).getColumn(0));
                fields.put("dictSystemName",(QueryStringField)dictEntryTable.getRow(0).getColumn(1));
                fields.put("abbreviation",(QueryStringField)dictEntryTable.getRow(0).getColumn(2));
                fields.put("entry",(QueryStringField)dictEntryTable.getRow(0).getColumn(3));                
                fields.put("relatedEntry",(QueryStringField)dictEntryTable.getRow(0).getColumn(4));                
                       
            }
            

            List systemNames = new ArrayList();
                try{                                        
                    systemNames = remote.query(fields,0,leftTableRowsPerPage);                     
            }catch(Exception e){
                e.printStackTrace();
                throw new RPCException(e.getMessage());                
            }

            Iterator itraaa = systemNames.iterator();
            model=  new DataModel();
            while(itraaa.hasNext()){
                Object[] result = (Object[])(Object[])itraaa.next();
                //org id
                Integer idResult = (Integer)result[0];
                //org name
                String sysNameResult = (String)result[1];
                            
                
                DataSet row = new DataSet();
                
                NumberObject id = new NumberObject();                
                StringObject sysName = new StringObject();
                id.setType("integer");
                
                sysName.setValue(sysNameResult);
                id.setValue(idResult);
                
                row.addObject(id);          
                
                row.addObject(sysName);
                model.add(row);

            } 
            if(SessionManager.getSession().getAttribute("systemUserId") == null)
                SessionManager.getSession().setAttribute("systemUserId", remote.getSystemUserId().toString());
            CachingManager.putElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":Category", rpcSend);          
       }
        return model;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        CategoryDO categoryDO = new CategoryDO();
        NumberField categoryId = (NumberField) rpcSend.getField("categoryId");
        
        categoryDO.setId((Integer)categoryId.getValue());
        categoryDO.setDescription((String)rpcSend.getFieldValue("desc"));
        categoryDO.setName((String)rpcSend.getFieldValue("name"));
        categoryDO.setSystemName((String)rpcSend.getFieldValue("systemName"));
        categoryDO.setSection((Integer)rpcSend.getFieldValue("sectionId"));        
        
        
        List<DictionaryDO> dictDOList = new ArrayList<DictionaryDO>();
        
        TableModel dictEntryTable = (TableModel)rpcSend.getField("dictEntTable").getValue();
        
        for(int iter = 0; iter < dictEntryTable.numRows(); iter++){
            
         TableRow row = dictEntryTable.getRow(iter);
         DictionaryDO dictDO = new DictionaryDO();
         
        
         
         String sysName = (String)((StringField)row.getColumn(1)).getValue();
         String entry = (String)((StringField)row.getColumn(3)).getValue();
         
           dictDO.setSystemName(sysName);
           dictDO.setEntry(entry);
                  
           NumberField id = (NumberField)row.getHidden("id");
           //NumberField relEntryId = (NumberField)row.getHidden("relEntryId");                         
         
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
         
            
             NumberField relEntryId = (NumberField)row.getColumn(4); 
              if(relEntryId!=null){
                 if(relEntryId.getValue()!=null){
                   dictDO.setRelatedEntry((Integer)relEntryId.getValue());
                 }
                }
         
            CheckField isActive =  (CheckField)row.getColumn(0);
             if(isActive.isChecked()){           
               dictDO.setIsActive("Y");
            }   
            else{
                dictDO.setIsActive("N");
             }   
             dictDO.setCategory((Integer)categoryId.getValue());         
             dictDO.setLocalAbbrev((String)((StringField)row.getColumn(2)).getValue());         
             dictDOList.add(dictDO);             
          }
        
         
         try{
             remote.updateCategory(categoryDO, dictDOList);       
         }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }
         
         categoryDO = remote.getCategory((Integer)categoryId.getValue());
         rpcReturn.setFieldValue("systemName", categoryDO.getSystemName());
         rpcReturn.setFieldValue("name", categoryDO.getName());
         rpcReturn.setFieldValue("desc", categoryDO.getDescription());
         rpcReturn.setFieldValue("sectionId",categoryDO.getSection()); 
         
         //fillSectionOption(sectionOpt ,categoryDO);
         
         List addressList = remote.getDictionaryEntries((Integer)categoryId.getValue());
         rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
         
         
        return rpcReturn;
    }

   

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {               
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer categoryId = (Integer)key.getObject(0).getValue();
//      System.out.println("in contacts");
        CategoryDO catDO = remote.getCategory(categoryId);
//      set the fields in the RPC
        rpcReturn.setFieldValue("categoryId", catDO.getId());
        rpcReturn.setFieldValue("systemName",catDO.getSystemName());
        rpcReturn.setFieldValue("name",catDO.getName());
        rpcReturn.setFieldValue("desc",catDO.getDescription());    
        rpcReturn.setFieldValue("sectionId",catDO.getSection());    
                
                                   
        List addressList = remote.getDictionaryEntries(categoryId);
        rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
        
        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Integer categoryId = (Integer)key.getObject(0).getValue();
//      System.out.println("in contacts");
        CategoryDO catDO = new CategoryDO();
         try{
            catDO = remote.getCategoryAndLock(categoryId);
         }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
        rpcReturn.setFieldValue("categoryId", catDO.getId());
        rpcReturn.setFieldValue("systemName",catDO.getSystemName());
        rpcReturn.setFieldValue("name",catDO.getName());
        rpcReturn.setFieldValue("desc",catDO.getDescription());    
        rpcReturn.setFieldValue("sectionId",catDO.getSection());    
                
                                   
        List addressList = remote.getDictionaryEntries(categoryId);
        rpcReturn.setFieldValue("dictEntTable",fillDictEntryTable((TableModel)rpcReturn.getField("dictEntTable").getValue(),addressList));
        
        return rpcReturn;
    }

    public TableModel fillDictEntryTable(TableModel dictEntryModel, List contactsList){        
         try{
             dictEntryModel.reset();
             
             for(int iter = 0;iter < contactsList.size();iter++) {
                 DictionaryDO dictDO  = (DictionaryDO)contactsList.get(iter);
                 //DictionaryDO dictDO = addressRow.getDictionaryDO();

                    TableRow row = dictEntryModel.createRow();
                    NumberField id = new NumberField();
                    id.setType("integer");                    
                     id.setValue(dictDO.getId());
                   
                     row.addHidden("id", id);
                     
                     NumberField relEntryId = new NumberField();
                     relEntryId.setType("integer");                     
                     relEntryId.setValue(dictDO.getRelatedEntry());

                     row.addHidden("relEntryId", relEntryId);
                     
                     row.getColumn(0).setValue(dictDO.getIsActive());
                     row.getColumn(1).setValue(dictDO.getSystemName());
                     row.getColumn(2).setValue(dictDO.getLocalAbbrev());
                     row.getColumn(3).setValue(dictDO.getEntry());
                     row.getColumn(4).setValue(dictDO.getRelatedEntry());                      
                     dictEntryModel.addRow(row);
            } 
             
         } catch (Exception e) {

             e.printStackTrace();
             return null;
         }       
         
         return dictEntryModel;  
     }
    
    public void fillSectionOption(OptionField sectionOpt ,CategoryDO catDO){
        sectionOpt.getOptions().clear();
        
         SystemUserUtilRemote utilRemote  = (SystemUserUtilRemote)EJBFactory.lookup("SystemUserUtilBean/remote");
         List<SectionIdNameDO> sections = utilRemote.getSections("openelis");
         
         if(catDO!=null){
           if(catDO.getSection()!=null){ 
             SectionIdNameDO section = utilRemote.getSection(catDO.getSection());
             //if((section.getId()).equals(catDO.getSection())){
             sectionOpt.addOption(catDO.getSection().toString(),section.getName());    
             // } 
             }else{   
                 sectionOpt.addOption("0"," "); 
             }
           }else{   
               sectionOpt.addOption("0"," "); 
           }
         
         OptionItem firstItem =  (OptionItem)sectionOpt.getOptions().get(0);
         if(sections!=null){               
            List<OptionItem> optionlist = new ArrayList<OptionItem>();
         
          for (Iterator iter = sections.iterator(); iter.hasNext();) {
              SectionIdNameDO sectionDO = (SectionIdNameDO)iter.next();                                   
              
              //if the section has already been added to the drop down don't add it again
                if(!firstItem.display.equals(sectionDO.getName())){          
                 OptionItem item = new OptionItem();
                 //System.out.println("section "+ sectionDO);
                 item.akey = sectionDO.getId().toString();
                 item.display = sectionDO.getName(); 
                 
                 optionlist.add(item);
                }
              //}             
           } 
         
           for (Iterator iter = optionlist.iterator(); iter.hasNext();) {
               sectionOpt.getOptions().add(iter.next());            
           }
          }
    }
    
           
    
    public NumberField getEntryIdForSystemName(StringObject systemName)throws Exception{
        try{
          CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
          Integer entryId = remote.getEntryIdForSystemName((String)systemName.getValue());
          NumberField idField = new NumberField();
          idField.setType("integer");
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
          NumberField idField = new NumberField();
          idField.setType("integer");
          idField.setValue(entryId);          
          return idField;
         }catch(Exception ex){
             throw ex;
         }
      }
       
    // the method called to initialize the autocomplete box(es) on the screen
    public DataModel getDisplay(String cat, DataModel model, AbstractField value) {     
      
      DataModel dataModel = new DataModel(); 
      try{
        if(cat.equals("relatedEntry")){
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        Object[] result  = (Object[])remote.autoCompleteLookupById((Integer)value.getValue()); 
        
        if(result !=null){              
         Integer dictId = (Integer)result[0];
         String entry = (String)result[1];
         DataSet data = new DataSet();
        
         NumberObject id = new NumberObject();
         id.setType("integer");
         id.setValue(dictId);
         StringObject nameObject = new StringObject();
         nameObject.setValue(entry.trim());
                
         data.addObject(id);        
         data.addObject(nameObject);
        
        
         dataModel.add(data);
        }
       }  
      }catch(Exception ex){
          ex.printStackTrace();
      } 
        return dataModel;
    }

    // the method called to load the dropdowns on the screen
    public DataModel getInitialModel(String cat) { 
         
        DataModel model = new DataModel();
        DataSet blankset = new DataSet();
        
        StringObject blankStringId = new StringObject();
                      
        BooleanObject blankSelected = new BooleanObject();               
        blankStringId.setValue("");
        blankset.addObject(blankStringId);
        
        if(cat.equals("section")){
         NumberObject blankNumberId = new NumberObject();
         blankNumberId.setType("integer");
         blankNumberId.setValue(new Integer(0));
         blankset.addObject(blankNumberId);
        }
        if(cat.equals("isActive")){
            StringObject blankStringObj = new StringObject();
           
            blankStringObj.setValue("");
            blankset.addObject(blankStringObj);
           }
        
        blankSelected.setValue(new Boolean(false));
        blankset.addObject(blankSelected);
        
        model.add(blankset);
        
        if(cat.equals("section")){
        
         SystemUserUtilRemote utilRemote  = (SystemUserUtilRemote)EJBFactory.lookup("SystemUserUtilBean/remote");
         List<SectionIdNameDO> sections = utilRemote.getSections("openelis");
                      
             
                
        // OptionItem firstItem =  (OptionItem)sectionOpt.getOptions().get(0);
         if(sections!=null){
            // System.out.println("sections.size() "+ sections.size());   
          //  List<OptionItem> optionlist = new ArrayList<OptionItem>();
         
          for (Iterator iter = sections.iterator(); iter.hasNext();) {
              SectionIdNameDO sectionDO = (SectionIdNameDO)iter.next();
                           
                DataSet set = new DataSet();
                //Object[] result = (Object[]) entries.get(i);
                //id
                Integer dropdownId = sectionDO.getId();
                //entry
                String dropdownText = sectionDO.getName();
                
                StringObject textObject = new StringObject();
                //StringObject stringId = new StringObject();
                NumberObject numberId = new NumberObject();
                BooleanObject selected = new BooleanObject();
                
                textObject.setValue(dropdownText);
                set.addObject(textObject);
                
               // if(cat.equals("contactType")){
                    numberId.setType("integer");
                    numberId.setValue(dropdownId);
                    set.addObject(numberId);
               // }else{
                  //  stringId.setValue(dropdownText);
                  //  set.addObject(stringId);            
               // }
                
                selected.setValue(new Boolean(false));
                set.addObject(selected);
                
                model.add(set);                
              //}             
           }                           
          }
        }
        if(cat.equals("isActive")){
            DataSet set = new DataSet();
            //Object[] result = (Object[]) entries.get(i);
            //id
                        
            StringObject textObject = new StringObject();
            //StringObject stringId = new StringObject();
            StringObject stringId = new StringObject();
            BooleanObject selected = new BooleanObject();
            
            textObject.setValue("Y");
            set.addObject(textObject);
            
            stringId.setValue("Y");
            set.addObject(stringId);
                
            selected.setValue(new Boolean(false));
            set.addObject(selected); 
            model.add(set);
            
            set = new DataSet();
            textObject = new StringObject();
            textObject.setValue("N");
            set.addObject(textObject);
                          
            stringId = new StringObject();
            stringId.setValue("N");
            set.addObject(stringId);
            
            selected.setValue(new Boolean(false));
            set.addObject(selected);
            
            model.add(set); 
          }       
        return model;
        
    }

   //  the method called to load the matching entries in the autocomplete box(es) on the screen
    public DataModel getMatches(String cat, DataModel model, String match) {        
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");        
        List entries = remote.getMatchingEntries(match+"%", 10);
        DataModel dataModel = new DataModel();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            
            Object[] element = (Object[])iter.next();
            Integer entryId = (Integer)element[0];                   
            String entryText = element[1].toString();
            
            DataSet data = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject();
            idObject.setType("integer");
            idObject.setValue(entryId);
            data.addObject(idObject);
            
            StringObject nameObject = new StringObject();
            nameObject.setValue(entryText.trim());
            data.addObject(nameObject);
            
            StringObject displayObject = new StringObject();
            displayObject.setValue(entryText.trim());
            data.addObject(displayObject);
            
            StringObject selectedFlag = new StringObject();
            selectedFlag.setValue("N");
            data.addObject(selectedFlag);
            
            //add the dataset to the datamodel
            dataModel.add(data);
        }
       
        
        return dataModel;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
