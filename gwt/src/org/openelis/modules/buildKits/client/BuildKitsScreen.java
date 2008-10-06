package org.openelis.modules.buildKits.client;

import java.util.HashMap;

import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableLabel;
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

public class BuildKitsScreen extends OpenELISScreenForm implements ClickListener, AutoCompleteCallInt, ChangeListener{

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
        if(sender == kitDropdown){
            //getComponentsFromId(NumberObject inventoryItemId)
            NumberObject idObj = new NumberObject((Integer)rpc.getFieldValue(InventoryItemMeta.getName()));
            
            // prepare the argument list for the getObject function
            DataObject[] args = new DataObject[] {idObj}; 
            
            
            screenService.getObject("getComponentsFromId", args, new AsyncCallback() {
                public void onSuccess(Object result) {
                   DataModel model = (DataModel)((ModelObject)result).getValue();
                   
                   subItemsTable.model.clear();
                   
                   for(int i=0; i<model.size(); i++){
                       subItemsTable.model.addRow(model.get(i));
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

                    if(((DropDownField)model.getFieldAt(i, 1)).getValue() == null){
                        //Window.alert("IN HERE");
                        //we just take unit times num requested 
                        Integer unit = Integer.valueOf(Integer.valueOf(((Double)((NumberField)model.getFieldAt(i, 2)).getValue()).toString()));
                       // Window.alert("("+unit+")");
                       // Window.alert("("+(unit*requested)+")");
                        NumberField total = (NumberField)model.getFieldAt(i, 3);
                        
                        total.setValue(unit * requested);
                        
                        ((NumberField)subItemsController.model.getFieldAt(i, 3)).setValue(total.getValue());
                        ((TableLabel)subItemsController.view.table.getWidget(i, 3)).setField(total);
                        ((TableLabel)subItemsController.view.table.getWidget(i, 3)).setDisplay();
                    }else{
                        //we need to look at the quantity on hand to make sure we can build the requested number of kits
                        Integer unit = Integer.valueOf(Integer.valueOf(((Double)((NumberField)model.getFieldAt(i, 2)).getValue()).toString()));
                        Integer qtyOnHand = (Integer)((NumberField)model.getFieldAt(i, 4)).getValue();

                        Integer totalProposed = unit * requested;
                        NumberField total = (NumberField)model.getFieldAt(i, 3);

                        if(totalProposed.compareTo(qtyOnHand) > 0){
                            total.setValue(new Integer(-1000));
                        }else{
                            total.setValue(totalProposed);
                        }
                        
                        ((NumberField)subItemsController.model.getFieldAt(i, 3)).setValue(total.getValue());
                        ((TableLabel)subItemsController.view.table.getWidget(i, 3)).setField(total);
                        ((TableLabel)subItemsController.view.table.getWidget(i, 3)).setDisplay();
                    }
                }
            }
            //if the kit is selected and the location is selected take the numRequested*unit
            //if the kit is selected and the location is not do nothing
        }
    }

    public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        kitDropdown = (AutoCompleteDropdown)getWidget(InventoryItemMeta.getName());
        
        kitLocationDropdown = (AutoCompleteDropdown)getWidget(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation());
        
        numRequestedText = (TextBox)getWidget("numRequested");
        
        startWidget = (ScreenInputWidget)widgets.get(InventoryItemMeta.getName());
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        subItemsController = ((TableWidget)getWidget("subItemsTable")).controller;
        
        super.afterDraw(success);
    }

    //
    // start table manager methods
    //
    public boolean canSelect(int row, TableController controller) {        
        if(state == FormInt.State.ADD)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        currentTableRow = row;
        
       return true;
    }

    public boolean canDelete(int row, TableController controller) {
        return true;
    }

    public boolean action(int row, int col, TableController controller) {  
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;     
    }

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

    public boolean doAutoAdd(TableRow autoAddRow, TableController controller) {
        return false;
    }

    public void rowAdded(int row, TableController controller) {}

    public void getNextPage(TableController controller) {}

    public void getPage(int page) {}

    public void getPreviousPage(TableController controller) {}

    public void setModel(TableController controller, DataModel model) {}

    public void validateRow(int row, TableController controller) {}

    public void setMultiple(int row, int col, TableController controller) {}
    //
    //end table manager methods
    //

    //
    //auto complete method
    //
    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        HashMap params = new HashMap();
        
        if(widget == kitLocationDropdown){
            params.put("addToExisting", rpc.getField("addToExisting"));    
        }else{
            params.put("id", (NumberField)subItemsController.model.getRow(currentTableRow).getHidden("id"));
        }
        
        StringObject catObj = new StringObject(widget.cat);
        ModelObject modelObj = new ModelObject(model);
        StringObject matchObj = new StringObject(text);
        DataMap paramsObj = new DataMap(params);
        
        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {catObj, modelObj, matchObj, paramsObj}; 
        
        
        screenService.getObject("getMatchesObj", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                widget.showAutoMatches((DataModel)((ModelObject)result).getValue());
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
