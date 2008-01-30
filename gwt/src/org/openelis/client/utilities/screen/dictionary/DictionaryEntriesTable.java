package org.openelis.client.utilities.screen.dictionary;

import java.util.ArrayList;

import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.table.TableController;
import org.openelis.gwt.client.widget.table.TableManager;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;



public class DictionaryEntriesTable implements TableManager {
    
    private Dictionary dictionaryForm = null;
    private TableRow relEntryRow = null; 
    private ArrayList systemNamesList = new ArrayList();  
    private ArrayList entryList = new ArrayList(); 


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
         if(systemName != null){
           if(!systemName.trim().equals("")){
            dictionaryForm.checkSystemName(id,systemName.trim());
           } 
          }
         existsInList("systemName",systemName.trim());
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
         if(entry != null){
           if(!entry.trim().equals("")){
            dictionaryForm.checkEntry(id,entry.trim());
           } 
          }
         existsInList("entry",entry.trim());
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
              dictionaryForm.showError("sysNameUnique");
          }else{
              systemNamesList.add(text);
              
          } 
       }
       if(listName.equals("entry")){
           if(entryList.contains(text)){
               dictionaryForm.showError("entryUnique");
           }else{
               entryList.add(text);              
           } 
        }    
    }
    
}
