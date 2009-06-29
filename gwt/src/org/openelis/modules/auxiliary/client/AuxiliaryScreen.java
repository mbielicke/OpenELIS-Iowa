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
package org.openelis.modules.auxiliary.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableModelEvents;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableModelListener;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.AuxFieldGroupMetaMap;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AuxiliaryScreen extends OpenELISScreenForm<AuxiliaryForm, Query<TableDataRow<Integer>>> implements TableManager,
                                                                                                         ClickListener,
                                                                                                         ChangeListener,
                                                                                                         TableWidgetListener,
                                                                                                         TableModelListener{
    private ButtonPanel atozButtons;
    
    private KeyListManager keyList = new KeyListManager();
    
    private AuxFieldGroupMetaMap AuxFGMeta = new AuxFieldGroupMetaMap();
    
    private TableWidget auxFieldTableWidget,                             // the widget for the table for auxiliary fields           
                        auxFieldValueTableWidget;                        // the widget for the table for auxiliary field values 
    
    private AppButton removeAuxFieldRowButton,dictionaryLookUpButton,
                      removeAuxFieldValueRowButton ;    
    
    private ArrayList<TableDataRow<Integer>> selectedRows; 
    
    private TableDataModel<TableDataRow>         // the model that's used to represent the default structure of    
                                                                         // the data in auxFieldValueTableWidget
                                                  auxFieldValueTypeModel; // the model that acts as a reference to auxFieldValueTableWidget's
                                                                         // type dropdown's TableDataModel           
    private TextBox grpName;       

    public AuxiliaryScreen() {
        super("org.openelis.modules.auxiliary.server.AuxiliaryService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new AuxiliaryForm());
    }

    public void afterDraw(boolean success) {
        ResultsTable atozTable;

        //
        // we are interested in getting button actions in two places,
        // modelwidget and the screen
        //
        atozTable = (ResultsTable)getWidget("azTable");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        atozButtons = (ButtonPanel)getWidget("atozButtons");  
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);
        formChain.addCommand(atozTable);
        formChain.addCommand(atozButtons);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        ScreenTableWidget s = (ScreenTableWidget)widgets.get("auxFieldTable");
        auxFieldTableWidget = (TableWidget)s.getWidget();
        s = (ScreenTableWidget)widgets.get("auxFieldValueTable");  
        auxFieldValueTableWidget = (TableWidget)s.getWidget();
        auxFieldValueTableWidget.model.enableAutoAdd(false);
        
        auxFieldTableWidget.model.addTableModelListener(this);
        auxFieldValueTableWidget.addTableWidgetListener(this);
        
        removeAuxFieldRowButton = (AppButton)getWidget("removeAuxFieldRowButton");
        dictionaryLookUpButton = (AppButton)getWidget("dictionaryLookUpButton");
        removeAuxFieldValueRowButton = (AppButton)getWidget("removeAuxFieldValueRowButton");
         
        grpName = (TextBox)getWidget(AuxFGMeta.getName());
         
        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);
        
        super.afterDraw(success); 
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("unit_of_measure");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)auxFieldTableWidget.columns.get(2).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("aux_field_value_type");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)auxFieldValueTableWidget.columns.get(0).getColumnWidget()).setModel(model);
        auxFieldValueTypeModel = model;
        
        ((TableDropdown)auxFieldTableWidget.columns.get(7).getColumnWidget()).setModel(form.scriptlets);
        form.scriptlets = null;
    }
    
    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String query = ((AppButton)obj).action;            
            if (query.indexOf(":") != -1) 
                getAuxFieldGroups(query.substring(6));
            else
                super.performCommand(action, obj);
        } else if(action == DictionaryEntryPickerScreen.Action.COMMIT) {              
            selectedRows = (ArrayList<TableDataRow<Integer>>)obj;
            dictionaryLookupClosed();
        } else{
            super.performCommand(action, obj);
        }
    }
    
    public boolean canPerformCommand(Enum action, Object obj) {
        if(action == DictionaryEntryPickerScreen.Action.COMMIT || 
                        action == DictionaryEntryPickerScreen.Action.ABORT)
            return true;
        else
            return super.canPerformCommand(action, obj);
    }
    
    public void query() {
        super.query();     
        auxFieldTableWidget.model.enableAutoAdd(false);
        auxFieldValueTableWidget.model.enableAutoAdd(false);  
        removeAuxFieldRowButton.changeState(ButtonState.DISABLED);
        grpName.setFocus(true);
    }
    
    public void add() {
        super.add();
        auxFieldValueTableWidget.model.enableAutoAdd(false);
        auxFieldTableWidget.model.enableAutoAdd(true);  
        grpName.setFocus(true);
        auxFieldTableWidget.activeRow = -1;
    }
    
    public void abort() {
        auxFieldValueTableWidget.model.enableAutoAdd(false); 
        auxFieldTableWidget.model.enableAutoAdd(false);                    
        auxFieldValueTableWidget.model.clear();
        super.abort();                
    }
    
    protected SyncCallback afterUpdate = new SyncCallback() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(Object result) {
            auxFieldValueTableWidget.model.enableAutoAdd(false);   
            auxFieldTableWidget.model.enableAutoAdd(true);            
            grpName.setFocus(true);
            auxFieldTableWidget.activeRow = -1;
        }
    };  
    
    protected SyncCallback<AuxiliaryForm> commitUpdateCallback = new SyncCallback<AuxiliaryForm>() {
        public void onSuccess(AuxiliaryForm result) {
            if (form.status == Form.Status.valid) {               
                auxFieldValueTableWidget.model.enableAutoAdd(false); 
                auxFieldTableWidget.model.enableAutoAdd(false);
            }
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    protected SyncCallback<AuxiliaryForm> commitAddCallback = new SyncCallback<AuxiliaryForm>() {
        public void onSuccess(AuxiliaryForm result) {
            if (form.status == Form.Status.valid) {               
                auxFieldValueTableWidget.model.enableAutoAdd(false); 
                auxFieldTableWidget.model.enableAutoAdd(false);
            }
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };
    
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {        
        return true;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow set) {
       return ((DropDownField)set.cells[0]).getSelectedKey() != null;
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {        
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        if(widget == auxFieldTableWidget) {
            if (state == State.UPDATE || state == State.ADD || state == State.QUERY)
                return true;
        } else {
            if (state == State.UPDATE || state == State.ADD)
                return true;
        }
        
        return false;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(widget == auxFieldTableWidget) {
            return true;
        }   else  {
                if (state == State.UPDATE || state == State.ADD)
                    return true;
        }
        
        return false;          
    }

    public void onClick(Widget sender) {
        if(sender == removeAuxFieldRowButton)
            onRemoveAuxFieldRowButtonClick();
        if(sender == dictionaryLookUpButton)
            onDictionaryLookUpButtonClicked();
        if(sender == removeAuxFieldValueRowButton)
            onRemoveAuxFieldValueRowButtonClick();                
    }   

    public void getAuxFieldGroups(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryStringField qField = new QueryStringField(AuxFGMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
    }
    
    /**
     * This function is executed whenever the modal window for dictionary lookup
     * is closed. If autoadd is enabled for auxFieldValueTableWidget which 
     * means that the data currently showing in it is being referred to as the field
     * values for some row in auxFieldTableWidget, then the rows selected, if any,
     * from the lookup screen are added to auxFieldValueTableWidget. If autoadd is
     * not enabled for auxFieldValueTableWidget, then the user is notified about first 
     * selecting some row in auxFieldTableWidget.
     */
    public void dictionaryLookupClosed() {              
        Integer key;
        if(auxFieldValueTableWidget.model.getAutoAdd()) {        
            key = getIdForSystemName("aux_dictionary");  
            addAuxFieldValueRows(selectedRows,key);                                                      
        } else {
            Window.alert(consts.get("auxFieldSelFirst"));             
        }       
    }
    
    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
        
    }
    
    public void cellUpdated(SourcesTableModelEvents sender, int row, int cell) {
        
    }

    public void dataChanged(SourcesTableModelEvents sender) {
        
    }

    public void rowAdded(SourcesTableModelEvents sender, int rows) {
        
    }

    public void rowDeleted(SourcesTableModelEvents sender, int row) {
        
    }

    public void rowSelected(SourcesTableModelEvents sender, int row) {      
        if(sender == auxFieldTableWidget.model) {          
         if((state == State.UPDATE || state == State.ADD))  
           if(!auxFieldValueTableWidget.model.getAutoAdd())  
            auxFieldValueTableWidget.model.enableAutoAdd(true);           
         setModelInFieldValueTable(row);            
        }    
    }

    public void rowUnselected(SourcesTableModelEvents sender, int row) {
        
    }

    public void rowUpdated(SourcesTableModelEvents sender, int row) {
        
    }

    public void unload(SourcesTableModelEvents sender) {
        
    }
    
    private void addAuxFieldValueRows(ArrayList<TableDataRow<Integer>> selectedRows,
                                      Integer key) {
         List<String> entries;
         TableDataRow<Integer> row , set, dictSet;
         String entry = null;         

         if (selectedRows != null) {
             dictSet = new TableDataRow<Integer>(key); 
             entries = new ArrayList<String>();
             for (int iter = 0; iter < selectedRows.size(); iter++) {
                 set = selectedRows.get(iter);
                 entry = (String)(set.cells[0]).getValue();
                 if (entry != null && !entries.contains(entry.trim())) {
                     entries.add(entry);
                     row = (TableDataRow<Integer>)auxFieldValueTableWidget.model.createRow();
                     row.cells[0].setValue(dictSet);
                     row.cells[1].setValue(entry);                                                     
                     auxFieldValueTableWidget.model.addRow(row);                     
                 }
             }
             auxFieldTableWidget.model.refresh();
         }
     }
    
    private void onRemoveAuxFieldRowButtonClick() {
        if(auxFieldTableWidget.modelIndexList.length > 0) {
            int index = auxFieldTableWidget.modelIndexList[auxFieldTableWidget.activeRow];
            int numRows = 0 ;
            if (index > -1)
                auxFieldTableWidget.model.deleteRow(index);  
        
            numRows = auxFieldTableWidget.model.numRows(); 
        //
        // The following is done because after a row is deleted from auxFieldTableWidget,
        // auxFieldTableValueWidget should be loaded with the model that belongs to the 
        // row in auxFieldTableWidget that gets selected next after the deletion.
        //
            if(numRows > 0 && index <= numRows)
                setModelInFieldValueTable(index);
       } 
    }
    
    private void onRemoveAuxFieldValueRowButtonClick() {
        if(auxFieldValueTableWidget.modelIndexList.length > 0) {
            int selIndex = auxFieldValueTableWidget.modelIndexList[auxFieldValueTableWidget.activeRow];
            if (selIndex > -1)                     
                auxFieldValueTableWidget.model.deleteRow(selIndex);
        }
    }
    
    /**
     * This function opens a dialog window which allows the users to select one
     * or more dictionary entries to be added to the auxiliary field value  table
     */
    private void onDictionaryLookUpButtonClicked() {
        ScreenWindow modal;
        DictionaryEntryPickerScreen pickerScreen;
        
        pickerScreen = new DictionaryEntryPickerScreen();       
        modal = new ScreenWindow(null,"Dictionary LookUp","dictionaryEntryPickerScreen","Loading...",true,false);
        pickerScreen.addCommandListener(this);
        modal.setName(consts.get("chooseDictEntry"));
        modal.setContent(pickerScreen);
    }
    

    public void finishedEditing(SourcesTableWidgetEvents sender,int row,int col) {    
        AuxiliaryGeneralPurposeRPC agrpc;   
        Double[] darray;
        String finalValue,systemName;
        if(sender == auxFieldValueTableWidget && col == 1) {            
            final int currRow = row;            
            final String value = ((StringField)auxFieldValueTableWidget.model.getRow(row).cells[1]).getValue();
            agrpc = new AuxiliaryGeneralPurposeRPC();
            agrpc.key = (Integer)((DropDownField)auxFieldValueTableWidget.model.getRow(row).cells[0]).getSelectedKey();
            darray = new Double[2];
            finalValue = "";
            systemName = getSelectedSystemName(row);                                  
            if(systemName!=null){
                if ("aux_dictionary".equals(systemName)) {
                    //
                    // Find out if this value is stored in the database if
                    // the type chosen was "Dictionary"
                    //
                    if (!"".equals(value.trim())) {   
                        AuxiliaryGeneralPurposeRPC agrpc1 = new AuxiliaryGeneralPurposeRPC();
                        agrpc1.stringValue = value;
                        screenService.call("getEntryIdForEntryText",agrpc1,
                                           new SyncCallback() {
                                               public void onSuccess(Object result1) {
                                                   //
                                                   // If this value is not stored in the
                                                   // database then add error to this
                                                   // cell in the "Value" column
                                                   //
                                                   if (((AuxiliaryGeneralPurposeRPC)result1).key == null) {
                                                       auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                                                   consts.get("illegalDictEntryException"));
                                                                          } else {  
                                                                              auxFieldValueTableWidget.model.setCell(currRow,1,((AuxiliaryGeneralPurposeRPC)result1).stringValue);
                                                                          }
                                                                      }

                                                                      public void onFailure(Throwable caught) {
                                                                          Window.alert(caught.getMessage());
                                                                          window.clearStatus();
                                                                      }
                                                                  });
                                            } else {
                                                auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                  consts.get("fieldRequiredException"));  
                                            }
                                           } else if ("aux_numeric".equals(systemName)) {                                              
                                               //
                                               // Get the string that was entered if the type
                                               // chosen was "Numeric" and try to break it up at
                                               // the "," if it follows the pattern number,number
                                               //
                                              if (!"".equals(value.trim())) {    
                                               String[] strList = value.split(",");
                                               boolean convert = false;
                                               if (strList.length == 2) {
                                                   for (int iter = 0; iter < strList.length; iter++) {
                                                       String token = strList[iter];
                                                       try {
                                                           // 
                                                           // Convert each number obtained
                                                           // from the string and store its value
                                                           // converted to double if its a valid
                                                           // number, into an array
                                                           //
                                                           Double doubleVal = Double.valueOf(token);
                                                           darray[iter] = doubleVal.doubleValue();
                                                           convert = true;
                                                       } catch (NumberFormatException ex) {
                                                           convert = false;
                                                       }
                                                   }
                                               }

                                               if (convert) {
                                                   //
                                                   // If it's a valid string store the converted
                                                   // string back into the column otherwise add
                                                   // an error to the cell and store empty
                                                   // string into the cell
                                                   //  
                                                   if (darray[0].toString()
                                                                .indexOf(".") == -1) {
                                                       finalValue = darray[0].toString() + ".0"
                                                                    + ",";
                                                   } else {
                                                       finalValue = darray[0].toString() + ",";
                                                   }

                                                   if (darray[1].toString()
                                                                .indexOf(".") == -1) {
                                                       finalValue += darray[1].toString() + ".0";
                                                   } else {
                                                       finalValue += darray[1].toString();
                                                   }
                                                   auxFieldValueTableWidget.model.setCell(currRow,1,finalValue);

                                               } else {
                                                   auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                                   consts.get("illegalNumericFormatException"));
                                               }
                                            }  else {
                                                auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                   consts.get("fieldRequiredException"));  
                                             }
                                           } else if ("aux_alpha_lower".equals(systemName)) {
                                               auxFieldValueTableWidget.model.setCell(currRow,1,value.toLowerCase()); 
                                           } else if ("aux_alpha_upper".equals(getSelectedSystemName(row))) {
                                               auxFieldValueTableWidget.model.setCell(currRow,1,value.toUpperCase()); 
                                           } else if ("aux_yes_no".equals(systemName)) {
                                             if (!"".equals(value.trim())) {      
                                              if((!"Y".equals(value.trim()))&&(!"N".equals(value.trim()))) {
                                                  auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                                              consts.get("illegalYesNoValueException"));
                                              }
                                             }  
                                           } else if ("aux_date".equals(systemName)) {
                                             if (!"".equals(value.trim())) {      
                                              try{                                                                                            
                                                auxFieldValueTableWidget.model.setCell(currRow,1,validateDate(value)); 
                                               }catch(IllegalArgumentException ex) {
                                                  auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                   consts.get("illegalDateValueException"));                                                 
                                              }
                                             }  
                                            } else if ("aux_date_time".equals(systemName)) {                                                
                                              if (!"".equals(value.trim())) { 
                                               try{                                                                                                               
                                                auxFieldValueTableWidget.model.setCell(currRow,1,validateDateTime(value)); 
                                              }catch(IllegalArgumentException ex) {
                                                   auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                    consts.get("illegalDateTimeValueException"));   
                                                   
                                               }   
                                              }  
                                           } else if ("aux_time".equals(systemName)) { 
                                              if (!"".equals(value.trim())) {  
                                               try{                                                                                                               
                                                   auxFieldValueTableWidget.model.setCell(currRow,1,validateTime(value)); 
                                                 }catch(IllegalArgumentException ex) {
                                                      auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                       consts.get("illegalTimeValueException"));   
                                                      
                                                  }                                               
                                                }                                           
                                     }
                                   }                                              

        }
        
    }
    
    private void setModelInFieldValueTable(int row) {       
       AuxiliaryGeneralPurposeRPC agrpc;
       TableDataModel<TableDataRow<Integer>> defaultModel;
       
       final TableDataRow<Integer> set = auxFieldTableWidget.model.getRow(row);
       TableDataModel<TableDataRow<Integer>> model = (TableDataModel<TableDataRow<Integer>>)set.getData();
       if(set.key != null && model == null) {            
            agrpc = new AuxiliaryGeneralPurposeRPC();
            defaultModel = new TableDataModel<TableDataRow<Integer>>();
            defaultModel.setDefaultSet(form.auxFieldValueTable.defaultRow);
            agrpc.auxFieldValueModel = defaultModel;
            agrpc.key = set.key;
            screenService.call("getAuxFieldValueModel", agrpc, new SyncCallback(){
                public void onSuccess(Object result) {                      
                    set.setData(((AuxiliaryGeneralPurposeRPC)result).auxFieldValueModel);                      
                }            
                public void onFailure(Throwable caught) {                
                    Window.alert(caught.getMessage());
                    window.clearStatus();
                }
                   
               });                       
        }
       
       if(set.getData()==null) {
           defaultModel = new TableDataModel<TableDataRow<Integer>>();
           defaultModel.setDefaultSet(form.auxFieldValueTable.defaultRow);
           set.setData(defaultModel);
       }
               
       auxFieldValueTableWidget.model.load((TableDataModel)set.getData());
    }     
    
    private String validateDate(String value) throws IllegalArgumentException {
        Date date = null;                                               
        DateField df = null;                                                 
      try{  
        date = new Date(value.replaceAll("-", "/"));                                              
        df = new DateField((byte)0, (byte)2,date);                                                       
      }catch(IllegalArgumentException ex) {
           throw ex;                                                 
       } 
      
      if(df!=null)
          return df.format();
     
      return null;
    }
    
    private String validateDateTime(String value) throws IllegalArgumentException {
        Date date = null;                                               
        DateField df = null;
        String[] split = null;
        String hhmm = null;
      try{                  
        split = value.split(" ");  
        if(split.length != 2)
          throw new IllegalArgumentException();
        
        hhmm = split[1];
        if(hhmm.split(":").length != 2) 
            throw new IllegalArgumentException();  
        
        date = new Date(value.replaceAll("-", "/"));                                              
        df = new DateField((byte)0, (byte)4,date);                                               
       
      }catch(IllegalArgumentException ex) {
           throw ex;           
       } 
      
      if(df!=null) 
       return df.format();
      
      return null;
    }
    
    /**
     * This method validates whether the argument "value" is of the format
     * [HH:mm] and then returns the formatted version of it , if it conforms with
     * format. For example a string like "7:5" will be converted to "07:05".
     * It throws an IllegalArgumentException if the value doesn't conform with the format 
     */
    private String validateTime(String value) throws IllegalArgumentException {
     Date date = null;                                               
     DateField df = null;
     String[] split = null;
     String defDate = "2000-01-01 ";
     String nextDayDate = "2000-01-02 ";
     String dateStr = defDate + value;
     boolean nextDay = false;
     
     try{                  
        split = value.split(":");  
        if(split.length != 2)
          throw new IllegalArgumentException();               
        
        date = new Date(dateStr.replaceAll("-", "/"));
        
        if(Integer.parseInt(split[0]) > 23) 
            nextDay = true;
            
        df = new DateField((byte)0, (byte)4,date);                                                      
      }catch(IllegalArgumentException ex) {          
          throw ex;                   
       } 
      
      if(df!=null) { 
       if(nextDay)    
        return df.format().replace(nextDayDate,"");   
       else   
        return df.format().replace(defDate,"");
      } 
      
      return null;
    }
    
    /**
     * This function returns the system name for the option that was selected
     * in a given row, represented by the argument "row", from the type dropdown
     * in auxFieldTableWidget, it returns null if no option was selected   
     */
    private String getSelectedSystemName(int row){
        TableDataRow<Integer> trow;
        ArrayList<TableDataRow<Integer>> list;
        StringObject data;
        if(row > -1) {
            trow = auxFieldValueTableWidget.model.getRow(row);
            list = (ArrayList<TableDataRow<Integer>>)trow.cells[0].getValue();
        
            if(list == null) {
                return null;
            } else  {
                if(list.get(0).key == null) { 
                   return null;
                } else {
                   data = (StringObject)list.get(0).getData();
                   return data.getValue();
                }   
            }                      
        }    
        return null;
    }
    
    /**
     * This function iterates through the TableDataModel of the type dropdown in 
     * auxFieldValueTableWidget and returns the key of the TableDataRow that  
     * has the value of its data, set to the argument "systemName" 
     */
    private Integer getIdForSystemName(String systemName) {
        TableDataRow<Integer> row;
        StringObject data = null;
        if(auxFieldValueTypeModel != null) {
            for(int i = 0; i < auxFieldValueTypeModel.size(); i++) {
                row = auxFieldValueTypeModel.get(i);
                data = (StringObject)row.getData();
                if(row.key!= null && systemName.equals(data.getValue())) {
                    return row.key;
                }
            }
        }
        return null;
    }
    
    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        if(list == null)
            return null;
        
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        
        for(int i=0; i<list.size(); i++){
            TableDataRow<Integer> row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
  }      