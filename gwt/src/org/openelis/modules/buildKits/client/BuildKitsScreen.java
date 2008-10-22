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
package org.openelis.modules.buildKits.client;

import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BuildKitsScreen extends OpenELISScreenForm implements ClickListener, AutoCompleteCallInt, ChangeListener, TableManager{

    private KeyListManager keyList = new KeyListManager();
    
    private AutoComplete kitDropdown, kitLocationDropdown;
    private TableWidget subItemsTable;
    private TextBox numRequestedText;
    private int currentTableRow = -1;
    private boolean startedLoadingTable = false;
    
    private InventoryItemMetaMap InventoryItemMeta = new InventoryItemMetaMap();
    
    public BuildKitsScreen() {
        super("org.openelis.modules.buildKits.server.BuildKitsService", false);
    }
    
    public void onClick(Widget sender) {
        // TODO Auto-generated method stub
        
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
        if(sender == kitDropdown && kitDropdown.getSelections().size() > 0){
            NumberObject idObj = (NumberObject)((DataSet)kitDropdown.getSelections().get(0)).getKey();
            // prepare the argument list for the getObject function
            Data[] args = new Data[] {idObj}; 
            
            
            screenService.getObject("getComponentsFromId", args, new AsyncCallback() {
                public void onSuccess(Object result) {
                   DataModel model = (DataModel)result;
                   
                   subItemsTable.model.clear();

                   for(int i=0; i<model.size(); i++){
                       DataSet set = model.get(i);
                       DataSet tableRow = subItemsTable.model.createRow();
                       //id
                       //name
                       //qty
                       tableRow.setKey(set.getKey());
                       tableRow.get(0).setValue(set.get(0).getValue());
                       tableRow.get(2).setValue(set.get(1).getValue());

                       subItemsTable.model.addRow(tableRow);
                   }
                }
                
                public void onFailure(Throwable caught) {
                    Window.alert(caught.getMessage());
                }
            });
        }else if(sender == numRequestedText){
            //if the kit isnt selected do nothing
            if(!"".equals(numRequestedText.getText()) && kitDropdown.getSelections().size() > 0){
                Integer requested = Integer.valueOf(numRequestedText.getText());
                Integer realRequested = null;
                
                TableModel model = (TableModel)subItemsTable.model;
                for(int i=0; i<model.numRows(); i++){

                    if(model.getCell(i, 1) == null){
                        //we just take unit times num requested 
                        Integer unit = Integer.valueOf(Integer.valueOf(((Double)((NumberField)model.getObject(i, 2)).getValue()).toString()));
                        NumberField total = (NumberField)model.getObject(i, 3);
                        
                        total.setValue(unit * requested);
                        
                        subItemsTable.model.setCell(i, 3, total.getValue());
                        
                    }else{
                        //we need to look at the quantity on hand to make sure we can build the requested number of kits
                        Integer unit = Integer.valueOf(Integer.valueOf(((Double)model.getCell(i, 2)).toString()));
                        Integer qtyOnHand = (Integer)model.getCell(i, 4);

                        Integer totalProposed = unit * requested;
                        NumberField total = (NumberField)model.getObject(i, 3);

                        if(totalProposed.compareTo(qtyOnHand) > 0){
                            total.setValue(new Integer(-1000));
                        }else{
                            total.setValue(totalProposed);
                        }
                        
                        subItemsTable.model.setCell(i, 3, total.getValue());
                    }
                }
            }
            //if the kit is selected and the location is selected take the numRequested*unit
            //if the kit is selected and the location is not do nothing
        }
    }

    public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        kitDropdown = (AutoComplete)getWidget(InventoryItemMeta.getName());
        
        kitLocationDropdown = (AutoComplete)getWidget(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation());
        
        numRequestedText = (TextBox)getWidget("numRequested");
        
        startWidget = (ScreenInputWidget)widgets.get(InventoryItemMeta.getName());
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        subItemsTable = (TableWidget)getWidget("subItemsTable");
        
        super.afterDraw(success);
        
        rpc.setFieldValue("subItemsTable", subItemsTable.model.getData());
    }

    //
    // start table manager methods
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        return false;
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        currentTableRow = row;
        return false;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if(state == FormInt.State.ADD)           
            return true;
        return false;
    }
    /*
     public void finishedEditing(int row, int col, TableController controller) {
        DropDownField locationField;
        if(col == 1 && row > -1 && row < controller.model.numRows() && !startedLoadingTable){
            startedLoadingTable = true;
            locationField = (DropDownField)controller.model.getFieldAt(row, col);
            if(locationField.getValue() != null){
                DataSet dropdownDataSet = (DataSet)((TableAutoDropdown)((EditTable)controller).editors[1]).editor.getSelected().get(0);
                NumberField qtyOnHandField = new NumberField((Integer)((NumberObject)dropdownDataSet.getObject(1)).getValue());
                
                ((NumberField)controller.model.getFieldAt(row, 4)).setValue(qtyOnHandField.getValue());
                ((TableLabel)((EditTable)controller).view.table.getWidget(row, 4)).setField(qtyOnHandField);
                ((TableLabel)((EditTable)controller).view.table.getWidget(row, 4)).setDisplay();

                startedLoadingTable = false;
            }
        }     
    }
    */
    //
    //end table manager methods
    //

    //
    //auto complete method
    //
    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        StringObject catObj = new StringObject(widget.cat);
        StringObject matchObj = new StringObject(text);
        DataMap paramsObj = new DataMap();
        
        if(widget == kitLocationDropdown){
            paramsObj.put("addToExisting", rpc.getField("addToExisting"));    
        }else{
            paramsObj.put("id", (NumberObject)subItemsTable.model.getRow(currentTableRow).getKey());
        }
        
        // prepare the argument list for the getObject function
        Data[] args = new Data[] {catObj, model, matchObj, paramsObj}; 
        
        
        screenService.getObject("getMatchesObj", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                widget.showAutoMatches((DataModel)result);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setStatus(caught.getMessage(), "ErrorPanel");
                }else
                    Window.alert(caught.getMessage());
            }
        });
    }    
}
