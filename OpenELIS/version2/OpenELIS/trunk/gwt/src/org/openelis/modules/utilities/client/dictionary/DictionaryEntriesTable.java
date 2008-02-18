package org.openelis.modules.utilities.client.dictionary;

import java.util.ArrayList;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.table.TableCellInputWidget;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;

import com.google.gwt.user.client.Window;


public class DictionaryEntriesTable implements TableManager {
    
    private Dictionary dictionaryForm = null;
    private TableRow relEntryRow = null; 
    private ArrayList systemNamesList = new ArrayList();  
    private ArrayList entryList = new ArrayList(); 
    //private boolean noSysNameError = true;
    //private boolean noEntryError = true;
      
    public TableController thiscontroller = null;

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
        
        TableRow tableRow =  controller.model.getRow(row);
        Integer id  = null;
        NumberField idField = (NumberField)tableRow.getHidden("id");  
        if(idField!=null){
         id  = (Integer)idField.getValue();
        } 
        
       if(col ==1){
        StringField snfield = (StringField)controller.model.getFieldAt(row, 1);               
        String systemName = (String)snfield.getValue();
        //Window.alert("id "+ id + " systemName "+systemName);
        if(systemName != null){
          if(!systemName.trim().equals("")){
              dictionaryForm.checkSystemName(id,systemName.trim(), row);  
                
          if(row < systemNamesList.size()){ 
           if(!(systemNamesList.get(row).equals(systemName))){
             if(checkInList("systemName",systemName.trim(), row)){
                //Window.alert("System names for Dictionary must be unique");
                snfield.addError("System names for Dictionary must be unique");
                ((TableCellInputWidget)controller.view.table.getWidget(row,1)).drawErrors();
             }
            } 
           }else{
              if(checkInList("systemName",systemName.trim(), row)){
                  snfield.addError("System names for Dictionary must be unique");
                  ((TableCellInputWidget)controller.view.table.getWidget(row,1)).drawErrors();
              }
           } 
         }
        }       
    }
            
        if(col==3){
         StringField efield = (StringField)controller.model.getFieldAt(row, 3); 
         String entry = (String)efield.getValue();                
       
         if(entry != null){
          if(!entry.trim().equals("")){
              dictionaryForm.checkEntry(id,entry.trim(), row);
               
          if(row < entryList.size()){               
           if(!(entryList.get(row).equals(entry))){
              if(checkInList("entry",entry.trim(), row)){
                   //Window.alert("Entry text for Dictionary must be unique");  
                  efield.addError("Entry text for Dictionary must be unique");
                  ((TableCellInputWidget)controller.view.table.getWidget(row,3)).drawErrors();
                  dictionaryForm.setSysNameUnique(false); 
               }else{
                   dictionaryForm.setSysNameUnique(true);
               }
            } 
           }
           else{
              if(checkInList("entry",entry.trim(), row)){
                  efield.addError("Entry text for Dictionary must be unique");
                  ((TableCellInputWidget)controller.view.table.getWidget(row,3)).drawErrors();
                  dictionaryForm.setEntryUnique(false); 
              }else{
                  dictionaryForm.setEntryUnique(true);
              }
           }
          } 
        
        
         }
        }        
        if((col == 1 || col == 3) && (row == controller.model.numRows()-1)){          
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
      
    
    public boolean checkInList(String listName,String text,int row){
      boolean duplicate =  false;      
       if(listName.equals("systemName")){
           if(systemNamesList.contains(text)){  
             if(row < systemNamesList.size()){  
                 Window.alert("text "+ "\""+text+"\""+ " data "+ "\""+systemNamesList.get(row)+"\"");
                  Window.alert(new Integer(systemNamesList.indexOf(text)).toString());                 
              if(!text.equals(systemNamesList.get(row))){  
                duplicate = true;
              }  
            }else{
                duplicate = true;
            }  
           } 
           
          if(!duplicate){
             if(row < systemNamesList.size()){
                 systemNamesList.remove(row);
                 systemNamesList.add(row,text) ; 
             }else{
                 systemNamesList.add(text);
             } 
          }        
        }
       
        if(listName.equals("entry")){          
           if(entryList.contains(text)){               
               if(row < entryList.size()){                     
                   if(!text.equals(entryList.get(row))){  
                     duplicate = true;
                   }  
                 }else{
                     duplicate = true;
                 }  
                } 
                   
           if(!duplicate){
               if(row < entryList.size()){
                   entryList.add(row,text) ; 
               }else{
                   entryList.add(text);  
               } 
            }               
         }
        return duplicate;
       }
    
    public void createLists(){        
       for(int iter =0; iter < thiscontroller.model.numRows(); iter++){
           systemNamesList.add(thiscontroller.model.getRow(iter).getColumn(1).getValue()) ;
           entryList.add(thiscontroller.model.getRow(iter).getColumn(3).getValue()) ;                   
       }
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }
    
}

