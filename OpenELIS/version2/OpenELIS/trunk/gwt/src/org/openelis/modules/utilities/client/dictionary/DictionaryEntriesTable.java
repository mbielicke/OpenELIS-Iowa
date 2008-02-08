package org.openelis.modules.utilities.client.dictionary;

import java.util.ArrayList;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;



public class DictionaryEntriesTable implements TableManager {
    
    private Dictionary dictionaryForm = null;
    private TableRow relEntryRow = null; 
    private ArrayList systemNamesList = new ArrayList();  
    private ArrayList entryList = new ArrayList(); 
    private boolean noSysNameError = true;
    private boolean noEntryError = true;


    public void setDictionaryForm(Dictionary dictionaryForm) {
        this.dictionaryForm = dictionaryForm;
    }

    public boolean action(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        return true;
    }

    public boolean canInsert(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSelect(int row, TableController controller) {       
        return true;
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public void finishedEditing(int row, int col, TableController controller) {        
        
        if(col == 1){         
            TableRow tableRow =  controller.model.getRow(row);
            StringField field = (StringField)tableRow.getColumn(col);   
            String systemName = (String)field.getValue();
            NumberField idField = (NumberField)tableRow.getHidden("id");  
            Integer id  = null;
            if(idField!=null){
             id  = (Integer)idField.getValue();
            } 
            boolean valuePresent = true;
            if(systemName != null){
              if(!systemName.trim().equals("")){
               dictionaryForm.checkSystemName(id,systemName.trim());
               existsInList("systemName",systemName.trim());
              }else{
                  valuePresent = false;
              }            
             }else{
                 valuePresent = false;
             }
            
            if(!valuePresent){
                dictionaryForm.showMessage("systemNameBlank") ;
            }            
          }      
         if(col == 3){            
            TableRow tableRow =  controller.model.getRow(row);
            StringField field = (StringField)tableRow.getColumn(col);   
            String entry = (String)field.getValue();
            NumberField idField = (NumberField)tableRow.getHidden("id");         
            Integer id  = null;
            if(idField!=null){
             id  = (Integer)idField.getValue();
            }          
            boolean valuePresent = true;
            if(entry != null){
              if(!entry.trim().equals("")){
               dictionaryForm.checkEntry(id,entry.trim());
               existsInList("entry",entry.trim());
              }else{
                  valuePresent = false;
              }            
             }else{
                 valuePresent = false;
             }
            
            if(!valuePresent){
                dictionaryForm.showMessage("entryBlank") ;
            } 
           
          } 
         
        if(col == 1 && (row == controller.model.numRows()-1)){          
          controller.addRow();  
         }
     }

    public void getNextPage(TableController controller) {
        // TODO Auto-generated method stub

    }

    public void getPage(int page) {
        // TODO Auto-generated method stub

    }

    public void getPreviousPage(TableController controller) {
        // TODO Auto-generated method stub

    }

    public void rowAdded(int row, TableController controller) {
        // TODO Auto-generated method stub

    }

    public void setModel(TableController controller, DataModel model) {
        // TODO Auto-generated method stub

    }
    
    public void setRelatedEntryId(Integer entryId){
        NumberField relEntryId = new NumberField(); 
        relEntryId.setType("integer");                     
        relEntryId.setValue(entryId);
        relEntryRow.addHidden("relEntryId", relEntryId);        
    }
    
    public void resetLists(){
        systemNamesList.clear();   
        entryList.clear();
    }
    
    public void existsInList(String listName, String text){
       if(listName.equals("systemName")){
          if(systemNamesList.contains(text)){
              dictionaryForm.showMessage("sysNameUnique");
              noSysNameError = false;
          }else{
              systemNamesList.add(text);
              noSysNameError = true;
             if(noEntryError){ 
              dictionaryForm.showMessage("noErrors");
             }else{
               dictionaryForm.showMessage("entryUnique");
             } 
          } 
       }
       if(listName.equals("entry")){
           if(entryList.contains(text)){
               dictionaryForm.showMessage("entryUnique");
               noEntryError = false;
           }else{
               entryList.add(text);  
               noEntryError = true;
               if(noSysNameError){
                dictionaryForm.showMessage("noErrors");
               }else{
                   dictionaryForm.showMessage("sysNameUnique"); 
               } 
           } 
        }    
    }
    
}
