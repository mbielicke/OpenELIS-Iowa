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

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;

public class StandardNotePickerScreen extends OpenELISScreenForm implements TreeListener, ClickListener{

	public TextArea noteTextArea;
	
    private StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
	public StandardNotePickerScreen(TextArea noteTextArea) {
		super("org.openelis.modules.standardnotepicker.server.StandardNotePickerService",false);
		this.noteTextArea = noteTextArea;		
        name="Standard Note Selection";
	}
	
	public void onClick(Widget sender) {
		String action = ((AppButton)sender).action;
		if(action.equals("find")){
			TextBox findTextBox = (TextBox)getWidget("findTextBox");
			 String queryString = findTextBox.getText()+(findTextBox.getText().endsWith("*") ? "" : "*");
			FormRPC queryRPC = (FormRPC) this.forms.get("queryByNameDescription");
			queryRPC.setFieldValue(StandardNoteMeta.getName(), queryString);
			queryRPC.setFieldValue(StandardNoteMeta.getDescription(), queryString);

			StringObject name = new StringObject();
	        StringObject desc = new StringObject();
	        name.setValue(queryString);
	        desc.setValue(queryString);
	        
	       // prepare the argument list for the getObject function
            Data[] args = new Data[] {name,desc}; 
	        screenService.getObject("getTreeModel" , args, new AsyncCallback<PagedTreeField>(){
	            public void onSuccess(PagedTreeField treeField){
	            	ScreenVertical vp = (ScreenVertical) widgets.get("treeContainer");
	            	ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
	            	vp.clear();
	            	vp.getPanel().add(tree);
	                tree.load(treeField);
	            }
	            
	            public void onFailure(Throwable caught){
	                Window.alert(caught.getMessage());
	            }
	         });        
		}
	}
	
	public void afterDraw(boolean sucess) {
        final ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
        tree.controller.setTreeListener(this);
        addCommandListener((ButtonPanel) getWidget("buttons"));
        ((ButtonPanel)getWidget("buttons")).addCommandListener(this);
        
        final ScreenVertical vp = (ScreenVertical) widgets.get("treeContainer");
        
        vp.clear();
        vp.remove(tree);
        
        window.setStatus("","spinnerIcon");
        
        StringObject name = new StringObject();
        StringObject desc = new StringObject();
        name.setValue("*");
        desc.setValue("*");
        
       // prepare the argument list for the getObject function
        Data[] args = new Data[] {name,desc}; 
        screenService.getObject("getTreeModel" , args, new AsyncCallback<PagedTreeField>(){
            public void onSuccess(PagedTreeField treeField){
            	vp.clear();
            	vp.getPanel().add(tree);
                tree.load(treeField);
                
                window.setStatus("","");
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

    public void onTreeItemSelected(TreeItem item) {
		try{
			item.setState(!item.getState(), true);
		
		}catch(NumberFormatException e){
			TextArea textArea = (TextArea)getWidget("noteText");
			textArea.setText((String)item.getUserObject());
		}
	}
	
	public void onTreeItemStateChanged(TreeItem item) {
		try{
			Integer id = Integer.valueOf((String)item.getUserObject());
			final TreeItem finalTreeItem = item;
			finalTreeItem.removeItems();
			
			final ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");

            window.setStatus("","spinnerIcon");
            
			FormRPC queryRPC = (FormRPC) this.forms.get("queryByNameDescription");

			StringObject name = new StringObject();
	        StringObject desc = new StringObject();
	        name.setValue(queryRPC.getFieldValue(StandardNoteMeta.getName())+(((String)queryRPC.getFieldValue(StandardNoteMeta.getName())).endsWith("*") ? "" : "*"));
	        desc.setValue(queryRPC.getFieldValue(StandardNoteMeta.getDescription())+(((String)queryRPC.getFieldValue(StandardNoteMeta.getName())).endsWith("*") ? "" : "*"));
	        
	        NumberObject idObj = new NumberObject(NumberObject.Type.INTEGER);
			idObj.setValue(id);
	        
	       // prepare the argument list for the getObject function
            Data[] args = new Data[] {idObj,name,desc}; 
	        
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
}
