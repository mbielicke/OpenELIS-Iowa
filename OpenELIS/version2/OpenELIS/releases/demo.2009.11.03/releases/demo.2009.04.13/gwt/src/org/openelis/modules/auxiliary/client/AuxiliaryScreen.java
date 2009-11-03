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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.KeyListManager;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AuxiliaryScreen extends OpenELISScreenForm<AuxiliaryForm, Query<TableDataRow<Integer>>> implements TableManager,
                                                                                                         ClickListener,
                                                                                                         ChangeListener,
                                                                                                         TableWidgetListener,
                                                                                                         TableModelListener,
                                                                                                         PopupListener{
    private ButtonPanel atozButtons;
    
    private KeyListManager keyList = new KeyListManager();
    
    private AuxFieldGroupMetaMap AuxFGMeta = new AuxFieldGroupMetaMap();
    
    private TableWidget auxFieldTableWidget, auxFieldValueTableWidget;
    
    private AppButton removeAuxFieldRowButton,dictionaryLookUpButton,
                      removeAuxFieldValueRowButton ;    
    
    private ArrayList<TableDataRow<Integer>> selectedRows = null; 
    
    private TableDataModel<TableDataRow<Integer>> defaultModel;   
        
    private ScreenWindow pickerWindow = null;              
    
    private TextBox grpName = null;
    
    AsyncCallback<AuxiliaryForm> checkModels = new AsyncCallback<AuxiliaryForm>() {     
      public void onSuccess(AuxiliaryForm rpc) {
            if(rpc.units != null) {
                setUnitsOfMeasureModel(rpc.units);
                rpc.units = null;
            }
            if(rpc.auxFieldValueTypes != null) {
                setAuxFieldValueTypesModel(rpc.auxFieldValueTypes);
                rpc.auxFieldValueTypes = null;
            }
      }

        public void onFailure(Throwable caught) {
            
        }
    };
    
    public AuxiliaryScreen() {
        super("org.openelis.modules.auxiliary.server.AuxiliaryService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new AuxiliaryForm());
    }

    public void afterDraw(boolean success) {
        ResultsTable atozTable;

        //
        // we are interested in getting button actions in two places,
        // modelwidget and the screen.
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
         
        setUnitsOfMeasureModel(form.units);
        setAuxFieldValueTypesModel(form.auxFieldValueTypes);
        setScriptletsModel(form.scriptlets);                
        
        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);                
        
        super.afterDraw(success);               
        
        form.auxFieldTable.setValue(auxFieldTableWidget.model.getData());
        form.auxFieldValueTable.setValue(auxFieldValueTableWidget.model.getData());
        
        defaultModel = (TableDataModel<TableDataRow<Integer>>)auxFieldValueTableWidget.model.getData().clone();
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
    }
    
    public void abort() {
        auxFieldValueTableWidget.model.enableAutoAdd(false); 
        auxFieldTableWidget.model.enableAutoAdd(false);         
        super.abort();
    }
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(Object result) {
            auxFieldValueTableWidget.model.enableAutoAdd(false);   
            auxFieldTableWidget.model.enableAutoAdd(true);  
            grpName.setFocus(true);
        }
    };  
    
    protected AsyncCallback<AuxiliaryForm> commitUpdateCallback = new AsyncCallback<AuxiliaryForm>() {
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

    protected AsyncCallback<AuxiliaryForm> commitAddCallback = new AsyncCallback<AuxiliaryForm>() {
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
        // TODO Auto-generated method stub
        return true;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow set) {
       return ((DataObject)set.cells[0]).getValue() != null && !((DataObject)set.cells[0]).getValue()
        .equals(-1);
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY)
            return true;
        
        return false;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(widget == auxFieldTableWidget)
            return true;
        else if (state == State.UPDATE || state == State.ADD || state == State.QUERY)
            return true;
        
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
    
    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {      
        final DictionaryEntryPickerScreen pickerScreen = (DictionaryEntryPickerScreen)pickerWindow.content;
        AuxiliaryGeneralPurposeRPC agrpc = new AuxiliaryGeneralPurposeRPC();
        agrpc.stringValue = "aux_dictionary";
        screenService.call("getEntryIdForSystemName",agrpc, 
                           new AsyncCallback<AuxiliaryGeneralPurposeRPC>(){                            
                            public void onSuccess(AuxiliaryGeneralPurposeRPC result) {                              
                                TableDataRow<Integer> dictSet = new TableDataRow<Integer>(result.key);
                                addAuxFieldValueRows(pickerScreen.selectedRows, dictSet);          
                            }                   
                            public void onFailure(Throwable caught) {
                                Window.alert(caught.getMessage());
                                window.setStatus("", "");                                
                            }
            
        });
    }  
    
    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub
        
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub
        
    }
    
    public void cellUpdated(SourcesTableModelEvents sender, int row, int cell) {
        // TODO Auto-generated method stub
        
    }

    public void dataChanged(SourcesTableModelEvents sender) {
        // TODO Auto-generated method stub
        
    }

    public void rowAdded(SourcesTableModelEvents sender, int rows) {
        // TODO Auto-generated method stub
        
    }

    public void rowDeleted(SourcesTableModelEvents sender, int row) {
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        
    }

    public void rowUpdated(SourcesTableModelEvents sender, int row) {
        // TODO Auto-generated method stub
        
    }

    public void unload(SourcesTableModelEvents sender) {
        // TODO Auto-generated method stub
        
    }
    
    private void addAuxFieldValueRows(ArrayList<TableDataRow<Integer>> selectedRows,
                               TableDataRow<Integer> dictSet) {
         List<String> entries = new ArrayList<String>();
         TableDataRow<Integer> row = null;
         TableDataRow<Integer> set;
         String entry = null;         

         if (selectedRows != null) {
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
    
    private void setAuxFieldValueTypesModel(TableDataModel<TableDataRow<Integer>> auxFieldValueTypes) {
        ((TableDropdown)auxFieldValueTableWidget.columns.get(0).getColumnWidget()).setModel(auxFieldValueTypes);
    }

    private void setUnitsOfMeasureModel(TableDataModel<TableDataRow<Integer>> units) {
        ((TableDropdown)auxFieldTableWidget.columns.get(2).getColumnWidget()).setModel(units);
    }
    
    private void setScriptletsModel(TableDataModel<TableDataRow<Integer>> scriptlets) {
        ((TableDropdown)auxFieldTableWidget.columns.get(7).getColumnWidget()).setModel(scriptlets);
    }
    
    private void onRemoveAuxFieldRowButtonClick() {
        if (auxFieldTableWidget.model.getData().getSelectedIndex() > -1)
            auxFieldTableWidget.model.deleteRow(auxFieldTableWidget.model.getData()
                                                               .getSelectedIndex());
        
    }
    
    private void onRemoveAuxFieldValueRowButtonClick() {
        int selIndex = auxFieldValueTableWidget.model.getData().getSelectedIndex();
        if (selIndex > -1) {                    
          auxFieldValueTableWidget.model.deleteRow(selIndex);          
        }
    }
    
    /**
     * This function opens a modal window which allows the users to select one
     * or more dictionary entries to be added to the Test Results table
     */
    private void onDictionaryLookUpButtonClicked() {
        int left = getAbsoluteLeft();
        int top = getAbsoluteTop();
        DictionaryEntryPickerScreen pickerScreen = new DictionaryEntryPickerScreen();        
        PopupPanel dictEntryPickerPopupPanel = new PopupPanel(false, true);
        pickerWindow = new ScreenWindow(dictEntryPickerPopupPanel,
                                                     consts.get("chooseDictEntry"),
                                                     "dictionaryEntryPicker",
                                                     "Loading...");
        pickerScreen.selectedRows = selectedRows;        
        pickerWindow.setContent(pickerScreen);   
        dictEntryPickerPopupPanel.addPopupListener(this);
        dictEntryPickerPopupPanel.add(pickerWindow);
        dictEntryPickerPopupPanel.setPopupPosition(left, top);
        dictEntryPickerPopupPanel.show();
    }
    

    public void finishedEditing(SourcesTableWidgetEvents sender,int row,int col) {    
        AuxiliaryGeneralPurposeRPC agrpc = null;   
        if(sender == auxFieldValueTableWidget && col == 1) {            
            final int currRow = row;
            final Integer selValue = (Integer)((DropDownField)auxFieldValueTableWidget.model.getRow(row).cells[0]).getSelectedKey();
            final String value = ((StringField)auxFieldValueTableWidget.model.getRow(row).cells[1]).getValue();            
            if (!"".equals(value.trim())) {  
                agrpc = new AuxiliaryGeneralPurposeRPC();
                agrpc.key = selValue;

                
                screenService.call("getCategorySystemName",agrpc,
                                   new SyncCallback() {
                                       public void onSuccess(Object result) {
                                           Double[] darray = new Double[2];
                                           String finalValue = "";
                                           if ("aux_dictionary".equals(((AuxiliaryGeneralPurposeRPC)result).stringValue)) {
                                               //
                                               // Find out if this value is stored in the database if
                                               // the type chosen was "Dictionary"
                                               //
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

                                           } else if ("aux_numeric".equals(((AuxiliaryGeneralPurposeRPC)result).stringValue)) {
                                               //
                                               // Get the string that was entered if the type
                                               // chosen was "Numeric" and try to break it up at
                                               // the "," if it follows the pattern number,number
                                               //
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
                                                   // If its a valid string store the converted
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

                                           } else if ("aux_alpha_lower".equals(((AuxiliaryGeneralPurposeRPC)result).stringValue)) {
                                               auxFieldValueTableWidget.model.setCell(currRow,1,value.toLowerCase()); 
                                           } else if ("aux_alpha_upper".equals(((AuxiliaryGeneralPurposeRPC)result).stringValue)) {
                                               auxFieldValueTableWidget.model.setCell(currRow,1,value.toUpperCase()); 
                                           } else if ("aux_yes_no".equals(((AuxiliaryGeneralPurposeRPC)result).stringValue)) {
                                              if((!"Y".equals(value.trim()))&&(!"N".equals(value.trim()))) {
                                                  auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                                              consts.get("illegalYesNoValueException"));
                                              }
                                           } else if ("aux_date".equals(((AuxiliaryGeneralPurposeRPC)result).stringValue)) {                                                                                            
                                             try{                                                                                            
                                               auxFieldValueTableWidget.model.setCell(currRow,1,validateDate(value)); 
                                             }catch(IllegalArgumentException ex) {
                                                  auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                   consts.get("illegalDateValueException"));                                                 
                                              } 
                                            } else if ("aux_date_time".equals(((AuxiliaryGeneralPurposeRPC)result).stringValue)) {                                                
                                              try{                                                                                                               
                                                auxFieldValueTableWidget.model.setCell(currRow,1,validateDateTime(value)); 
                                              }catch(IllegalArgumentException ex) {
                                                   auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                    consts.get("illegalDateTimeValueException"));   
                                                   
                                               }                                               
                                           } else if ("aux_time".equals(((AuxiliaryGeneralPurposeRPC)result).stringValue)) {                                                
                                               try{                                                                                                               
                                                   auxFieldValueTableWidget.model.setCell(currRow,1,validateTime(value)); 
                                                 }catch(IllegalArgumentException ex) {
                                                      auxFieldValueTableWidget.model.setCellError(currRow,1,
                                                                       consts.get("illegalTimeValueException"));   
                                                      
                                                  }                                               
                                              }

                                       }

                                    public void onFailure(Throwable caught) {
                                           Window.alert(caught.getMessage());
                                           window.clearStatus();
                                       }
                                   });

            }
        }
        
    }
    
    private void setModelInFieldValueTable(int row) {       
       AuxiliaryGeneralPurposeRPC agrpc = null;             
       final TableDataRow<Integer> set = auxFieldTableWidget.model.getRow(row);
       TableDataModel<TableDataRow<Integer>> model = (TableDataModel<TableDataRow<Integer>>)set.getData();
       if(set.key != null && model == null) {            
            agrpc = new AuxiliaryGeneralPurposeRPC();
            agrpc.auxFieldValueModel = (TableDataModel<TableDataRow<Integer>>)defaultModel.clone();
            agrpc.key = set.key;
            screenService.call("getAuxFieldValueModel", agrpc, new SyncCallback(){
                public void onSuccess(Object result) {                      
                    set.setData(((AuxiliaryGeneralPurposeRPC)result).auxFieldValueModel);                      
                }            
                public void onFailure(Throwable caught) {                
                    Window.alert(caught.getMessage());
                    window.setStatus("", "");
                }
                   
               });                       
        }
       
       if(set.getData()==null) 
        set.setData((TableDataModel)defaultModel.clone());  
               
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
    
    private String validateTime(String value) throws IllegalArgumentException {
     Date date = null;                                               
     DateField df = null;
     String[] split = null;
     String defDate = "2000-01-01 ";
     String dateStr = defDate + value;
     
     try{                  
        split = value.split(":");  
        if(split.length != 2)
          throw new IllegalArgumentException();
        
        date = new Date(dateStr.replaceAll("-", "/"));                                              
        df = new DateField((byte)0, (byte)4,date);                                                      
      }catch(IllegalArgumentException ex) {          
          throw ex;                   
       } 
      
      if(df!=null) 
       return df.format().replace(defDate,"");
      
      return null;
    }
  }      