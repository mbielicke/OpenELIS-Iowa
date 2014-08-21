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
package org.openelis.modules.standardnotepicker.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeModel;
import org.openelis.gwt.widget.tree.TreeServiceCallInt;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.SourcesTreeModelEvents;
import org.openelis.gwt.widget.tree.event.TreeModelListener;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class StandardNotePickerScreen extends OpenELISScreenForm<StandardNotePickerForm,Query<TableDataRow<Integer>>> implements TreeManager, TreeServiceCallInt, TreeModelListener, ClickListener{

	public TextArea noteTextArea;
	private TextBox findTextBox;
	private TreeWidget tree;
    private StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
	public StandardNotePickerScreen(TextArea noteTextArea) {
		super("org.openelis.modules.standardnotepicker.server.StandardNotePickerService");

		this.noteTextArea = noteTextArea;		
        name="Standard Note Selection";
        query = new Query<TableDataRow<Integer>>();
        getScreen(new StandardNotePickerForm());
	}
		
	public void onClick(Widget sender) {
		String action = ((AppButton)sender).action;
		if(action.equals("find")){
		    //StandardNotePickerRPC snprpc = new StandardNotePickerRPC();
	        //snprpc.key = form.key;
	        //snprpc.form = form.form;
	        form.queryString = findTextBox.getText()+(findTextBox.getText().endsWith("*") ? "" : "*");
	        
	        screenService.call("getTreeModel" , form, new AsyncCallback<StandardNotePickerForm>(){
	            public void onSuccess(StandardNotePickerForm result){
	                tree.model.load(result.treeModel);
	            }
	            
	            public void onFailure(Throwable caught){
	                Window.alert(caught.getMessage());
	            }
	         });        
		}
	}
	
	public void afterDraw(boolean sucess) {
        tree = (TreeWidget)getWidget("noteTree");
        tree.model.addTreeModelListener(this);
        findTextBox = (TextBox)getWidget("findTextBox");
        
        addCommandListener((ButtonPanel) getWidget("buttons"));
        ((ButtonPanel)getWidget("buttons")).addCommandListener(this);
        
        final ScreenVertical vp = (ScreenVertical) widgets.get("treeContainer");
        
        //vp.clear();
       // vp.remove(tree);
        
        window.setBusy();
       // prepare the argument list for the getObject function
        //StandardNotePickerRPC snprpc = new StandardNotePickerRPC();
        //snprpc.key = form.key;
        //snprpc.form = form.form;
        form.queryString = "*";
        
        screenService.call("getTreeModel" , form, new AsyncCallback<StandardNotePickerForm>(){
            public void onSuccess(StandardNotePickerForm result){
                tree.model.load(result.treeModel);
            	
                window.clearStatus();
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
         });        
        super.afterDraw(sucess);
	}
	
	public void commit() {
    	TextArea textArea = (TextArea)getWidget("noteText");
    	noteTextArea.setText(noteTextArea.getText()+textArea.getText());
    	
    	window.close();
    }

    public void abort() {
    	window.close();
    }
/*
    public void onTreeItemSelected(TreeItem item) {
		
	}
	
	public void onTreeItemStateChanged(TreeItem item) {
		try{
			Integer id = Integer.valueOf((String)item.getUserObject());
			final TreeItem finalTreeItem = item;
			finalTreeItem.removeItems();
			
			final ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");

            window.setStatus("","spinnerIcon");
            
			Form queryRPC = (Form)forms.get("queryByNameDescription");

			StringObject name = new StringObject();
	        StringObject desc = new StringObject();
	        name.setValue(queryRPC.getFieldValue(StandardNoteMeta.getName())+(((String)queryRPC.getFieldValue(StandardNoteMeta.getName())).endsWith("*") ? "" : "*"));
	        desc.setValue(queryRPC.getFieldValue(StandardNoteMeta.getDescription())+(((String)queryRPC.getFieldValue(StandardNoteMeta.getName())).endsWith("*") ? "" : "*"));
	        
	        NumberObject idObj = new NumberObject(NumberObject.Type.INTEGER);
			idObj.setValue(id);
	        
	       // prepare the argument list for the getObject function
            FieldType[] args = new FieldType[] {idObj,name,desc}; 
	        
            screenService.getObject("getTreeModelSecondLevel", args, new AsyncCallback<StringObject>(){
	            public void onSuccess(StringObject result){
	               finalTreeItem.removeItems();
	               tree.controller.model.addTextChildItems(finalTreeItem, (String)result.getValue());	        
                   
                   window.setStatus("","");
	            }
	            
	            public void onFailure(Throwable caught){
	            	finalTreeItem.removeItems();
	                Window.alert(caught.getMessage());
	            }
	         });        
		
		}catch(NumberFormatException e){
			//this means that it is the bottom level...
		}
	}
	*/

	//
	//start tree manager methods
	//
    public boolean canAdd(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canClose(TreeWidget widget, TreeDataItem set, int row) {
        return true;
    }

    public boolean canDelete(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canDrag(TreeWidget widget, TreeDataItem item, int row) {
        return false;
    }

    public boolean canDrop(TreeWidget widget,
                           Widget dragWidget,
                           TreeDataItem dropTarget,
                           int targetRow) {
        return false;
    }

    public boolean canDrop(TreeWidget widget,
                           Widget dragWidget,
                           Widget dropWidget) {
        return false;
    }

    public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {
        return false;
    }

    public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem set, int row) {
        if("leaf".equals(set.leafType))
            return true;
        
        return false;
    }

    public void drop(TreeWidget widget,
                     Widget dragWidget,
                     TreeDataItem dropTarget,
                     int targetRow) {
        
    }

    public void drop(TreeWidget widget, Widget dragWidget) {

        
    }
    //
    //end tree manager methods
    //

    //
    //start tree model listener methods
    //
    public void cellUpdated(SourcesTreeModelEvents sender, int row, int cell) {}

    public void dataChanged(SourcesTreeModelEvents sender) {}

    public void rowAdded(SourcesTreeModelEvents sender, int rows) {}

    public void rowClosed(SourcesTreeModelEvents sender, int row, TreeDataItem item) {}

    public void rowDeleted(SourcesTreeModelEvents sender, int row) {}

    public void rowOpened(SourcesTreeModelEvents sender, int row, final TreeDataItem item) {
        
    }

    public void rowSelectd(SourcesTreeModelEvents sender, int row) {
        final TreeDataItem item = tree.model.getRow(row);
            TextArea textArea = (TextArea)getWidget("noteText");
            textArea.setText((String)((StringObject)item.getData()).getValue());
                
    }

    public void rowUnselected(SourcesTreeModelEvents sender, int row) {}

    public void rowUpdated(SourcesTreeModelEvents sender, int row) {}

    public void unload(SourcesTreeModelEvents sender) {}
    //
    //end tree model listener methods
    //

    //method to help lazy load the tree
    public void getChildNodes(final TreeModel model, final int row) {
        final TreeDataItem item = model.getRow(row);
        Integer id = item.key;
        item.getItems().clear();
        
        //final ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");

        window.setBusy();
        
        //Form queryRPC = (Form)forms.get("queryByNameDescription");
        
        //StandardNotePickerRPC snprpc = new StandardNotePickerRPC();
        //snprpc.key = form.key;
        //snprpc.form = form.form;
        form.id = id;
        form.queryString = findTextBox.getText()+(findTextBox.getText().endsWith("*") ? "" : "*");
        
        screenService.call("getTreeModelSecondLevel", form, new AsyncCallback<StandardNotePickerForm>(){
            public void onSuccess(StandardNotePickerForm result){
                for(int i=0; i<result.treeModel.size(); i++)
                    item.addItem(result.treeModel.get(i));
                item.loaded = true;
                
                model.toggle(row);
                
                window.clearStatus();
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
         });        
        
    }
}
