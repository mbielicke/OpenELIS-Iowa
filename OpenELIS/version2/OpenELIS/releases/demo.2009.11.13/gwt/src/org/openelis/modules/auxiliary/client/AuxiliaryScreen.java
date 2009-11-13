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
import org.openelis.domain.TestResultDO;
import org.openelis.gwt.common.data.deprecated.DateField;
import org.openelis.gwt.common.data.deprecated.DropDownField;
import org.openelis.gwt.common.data.deprecated.KeyListManager;
import org.openelis.gwt.common.data.deprecated.QueryStringField;
import org.openelis.gwt.common.data.deprecated.StringField;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.deprecated.CommandChain;
import org.openelis.gwt.screen.deprecated.ScreenTableWidget;
import org.openelis.gwt.screen.deprecated.ScreenWindow;
import org.openelis.gwt.widget.deprecated.AppButton;
import org.openelis.gwt.widget.deprecated.ButtonPanel;
import org.openelis.gwt.widget.deprecated.ResultsTable;
import org.openelis.gwt.widget.deprecated.AppButton.ButtonState;
import org.openelis.gwt.widget.table.deprecated.TableDropdown;
import org.openelis.gwt.widget.table.deprecated.TableManager;
import org.openelis.gwt.widget.table.deprecated.TableWidget;
import org.openelis.gwt.widget.table.deprecated.event.SourcesTableModelEvents;
import org.openelis.gwt.widget.table.deprecated.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.deprecated.event.TableModelListener;
import org.openelis.gwt.widget.table.deprecated.event.TableWidgetListener;
import org.openelis.metamap.AuxFieldGroupMetaMap;
import org.openelis.modules.dictionary.client.DictionaryLookupScreen;
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
           
    private TextBox grpName;     
    
    private DictionaryLookupScreen dictEntryPicker;

    public AuxiliaryScreen() {
        super("org.openelis.modules.auxiliary.server.AuxiliaryService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new AuxiliaryForm());
    }

    public void afterDraw(boolean success) {
        ResultsTable atozTable;
        ArrayList cache;
        TableDataModel<TableDataRow> model;

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
        
        //((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
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
                
        cache = DictionaryCache.getListByCategorySystemName("unit_of_measure");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)auxFieldTableWidget.columns.get(2).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("aux_field_value_type");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)auxFieldValueTableWidget.columns.get(0).getColumnWidget()).setModel(model);        
    }
    
    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String query = ((AppButton)obj).action;            
            if (query.indexOf(":") != -1) 
                getAuxFieldGroups(query.substring(6));
            else
                super.performCommand(action, obj);
        } else{
            super.performCommand(action, obj);
        }
    }
    
    public boolean canPerformCommand(Enum action, Object obj) {
        if(action == DictionaryLookupScreen.Action.OK || 
                        action == DictionaryLookupScreen.Action.CANCEL)
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
        super.abort(); 
        auxFieldValueTableWidget.model.enableAutoAdd(false); 
        auxFieldTableWidget.model.enableAutoAdd(false);                    
        auxFieldValueTableWidget.model.clear();                      
    }
    
    protected SyncCallback<AuxiliaryForm> afterUpdate = new SyncCallback<AuxiliaryForm>() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(AuxiliaryForm result) {
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
    
    private void addAuxFieldValueRows(ArrayList<org.openelis.gwt.widget.table.TableDataRow> selectedRows,
                                      Integer key) {
         List<String> entries;
         TableDataRow<Integer> row, dictSet;
         org.openelis.gwt.widget.table.TableDataRow set;
         String entry = null;         

         if (selectedRows != null) {
             dictSet = new TableDataRow<Integer>(key); 
             entries = new ArrayList<String>();
             for (int iter = 0; iter < selectedRows.size(); iter++) {
                 set = selectedRows.get(iter);
                 entry = (String)(set.cells.get(0)).getValue();
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
        if(dictEntryPicker == null) {
            try {
                dictEntryPicker = new DictionaryLookupScreen();
                dictEntryPicker.addActionHandler(new ActionHandler<DictionaryLookupScreen.Action>(){

                    public void onAction(ActionEvent<DictionaryLookupScreen.Action> event) {
                       int selTab;
                       ArrayList<org.openelis.gwt.widget.table.TableDataRow> model;
                       TestResultDO resDO;
                       org.openelis.gwt.widget.table.TableDataRow row;
                       Integer dictId;                               
                       if(event.getAction() == DictionaryLookupScreen.Action.CANCEL) {
                           model = (ArrayList<org.openelis.gwt.widget.table.TableDataRow>)event.getData();                                                                                      
                           dictId = null; //DictionaryCache.getIdFromSystemName("aux_dictionary");                             
                           if(auxFieldValueTableWidget.model.getAutoAdd()) {        
                               dictId = null; //DictionaryCache.getIdFromSystemName("aux_dictionary");  
                               addAuxFieldValueRows(model,dictId);                                                      
                           } else {
                               Window.alert(consts.get("auxFieldSelFirst"));             
                           }                                                         
                       }                                
                    }
                    
                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }                                       
        }
        modal = new ScreenWindow(null,"Dictionary LookUp","dictionaryEntryPickerScreen","",true,false);
        modal.setName(consts.get("chooseDictEntry"));
        modal.setContent(dictEntryPicker);
        dictEntryPicker.setScreenState(Screen.State.DEFAULT);
    }
    

    public void finishedEditing(SourcesTableWidgetEvents sender,int row,int col) {            
        Double[] darray;
        String finalValue,systemName;
        if(sender == auxFieldValueTableWidget && col == 1) {            
            final int currRow = row;            
            final String value = ((StringField)auxFieldValueTableWidget.model.getRow(row).cells[1]).getValue();            
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
                        AuxiliaryGeneralPurposeRPC agrpc = new AuxiliaryGeneralPurposeRPC();
                        agrpc.stringValue = value;
                        screenService.call("getEntryIdForEntryText",agrpc,
                                           new SyncCallback<AuxiliaryGeneralPurposeRPC>() {
                                               public void onSuccess(AuxiliaryGeneralPurposeRPC result) {
                                                   //
                                                   // If this value is not stored in the
                                                   // database then add error to this
                                                   // cell in the "Value" column
                                                   //
                                                   if (result.key == null) {
                                                       auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                                                   consts.get("illegalDictEntryException"));
                                                   } else {  
                                                       auxFieldValueTableWidget.model.setCell(currRow,1,result.stringValue);
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
            screenService.call("getAuxFieldValueModel", agrpc, new SyncCallback<AuxiliaryGeneralPurposeRPC>(){
                public void onSuccess(AuxiliaryGeneralPurposeRPC result) {                      
                    set.setData(result.auxFieldValueModel);                      
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
        String sysname;
        Integer key;
        
        if(row > -1) {
            trow = auxFieldValueTableWidget.model.getRow(row);
            key = (Integer)(((DropDownField<Integer>)trow.cells[0]).getSelectedKey());            
            sysname = null; //DictionaryCache.getSystemNameFromId(key);
            return sysname;
        }    
        return null;
    }
    
    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        TableDataRow<Integer> row;
        
        if(list == null)
            return m;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
  }      