package org.openelis.modules.inventoryItem.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;

public class InventoryComponentsTable implements TableManager {
    private InventoryScreen userForm;
    public boolean disableRows = false;
    
    public void setInventoryForm(InventoryScreen form) {
        userForm = form;
    }
    
    public boolean canSelect(int row, TableController controller) {        
        if(userForm.state == FormInt.ADD || userForm.state == FormInt.UPDATE)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        //FIXME maybe add this later
        //|| (((CheckBox)userForm.getWidget("inventoryItem.isSubAssembly")).isChecked())
        if(disableRows){
            return false;
        }
        
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

    public void finishedEditing(final int row, int col, final TableController controller) {
        DropDownField componentField;
        
        if(col == 0){
            componentField = (DropDownField)controller.model.getFieldAt(row, col);
            if(componentField.getValue() != null){
                userForm.window.setStatus("","spinnerIcon");
                NumberObject componentIdObj = new NumberObject(NumberObject.INTEGER);
                componentIdObj.setValue((Integer)componentField.getValue());
                  
                // prepare the argument list for the getObject function
                DataObject[] args = new DataObject[] {componentIdObj}; 
                  
                userForm.screenService.getObject("getComponentDescriptionText", args, new AsyncCallback(){
                    public void onSuccess(Object result){    
                      // get the datamodel, load it in the notes panel and set the value in the rpc
                        StringField descString = new StringField();
                        descString.setValue((String) ((StringObject)result).getValue());
                        
                        TableRow tableRow = controller.model.getRow(row);
                        tableRow.setColumn(1, descString);
                        
                        controller.scrollLoad(-1);
                        
                        controller.select(row, 2);
                        
                        userForm.window.setStatus("","");
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                    }
                });
            }
        }     
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {
        if(col == 0)
            return true;
        else
            return false;
    }

    public void rowAdded(int row, TableController controller) {}

    public void getNextPage(TableController controller) {}

    public void getPage(int page) {}

    public void getPreviousPage(TableController controller) {}

    public void setModel(TableController controller, DataModel model) {}

    public void validateRow(int row, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }
}
