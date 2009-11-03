package org.openelis.modules.dictionary.client;

import java.util.ArrayList;

import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableCellInputWidget;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;



public class DictionaryEntriesTable implements TableManager {
    
    private DictionaryScreen dictionaryForm = null;
    private TableRow relEntryRow = null; 
    private ArrayList systemNamesList = new ArrayList();  
    private ArrayList entryList = new ArrayList();          

    public void setDictionaryForm(DictionaryScreen dictionaryForm) {
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
       //if(col == 0 && (row == controller.model.numRows()-1)){          
         //  controller.addRow();  
      // }                 
        return true;
    }

    public boolean canInsert(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSelect(int row, TableController controller) {   
       if(dictionaryForm.bpanel.state == FormInt.ADD || dictionaryForm.bpanel.state == FormInt.UPDATE) 
        return true;
       
       return false;
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
        
        if(systemName != null){
          if(!systemName.trim().equals("")){
              dictionaryForm.checkSystemName(id,systemName.trim(), row);  
                
          if(row < systemNamesList.size()){ 
           if(!(systemNamesList.get(row).equals(systemName))){
             if(checkInList("systemName",systemName.trim(), row)){                                
                 //showError(row-controller.start,1,controller,"System names for Dictionary must be unique");
                 showError(row,1,controller,"System names for Dictionary must be unique");
             }
            } 
           }else{
              if(checkInList("systemName",systemName.trim(), row)){                  
                  //showError(row-controller.start,1,controller,"System names for Dictionary must be unique");
                  showError(row,1,controller,"System names for Dictionary must be unique");
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
                  //showError(row-controller.start,3,controller,"Entry text for Dictionary must be unique");
                  showError(row,3,controller,"Entry text for Dictionary must be unique");
               }
            } 
           }
           else{
              if(checkInList("entry",entry.trim(), row)){                 
                  showError(row,3,controller,"Entry text for Dictionary must be unique");  
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
    
    public void createLists(TableController controller){        
       for(int iter =0; iter < controller.model.numRows(); iter++){
           systemNamesList.add(controller.model.getRow(iter).getColumn(1).getValue()) ;
           entryList.add(controller.model.getRow(iter).getColumn(3).getValue()) ;                   
       }
    }

    public void showError(int row, int col, TableController controller,String error) {
         AbstractField field =  controller.model.getFieldAt(row, col);      
         field.addError(error);
         ((TableCellInputWidget)controller.view.table.getWidget(row,col)).drawErrors();
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }
    
    
    
}

