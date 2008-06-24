package org.openelis.modules.standardnotepicker.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.newmeta.OrganizationMetaMap;
import org.openelis.newmeta.StandardNoteMetaMap;

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
	        DataObject[] args = new DataObject[] {name,desc}; 
	        screenService.getObject("getTreeModel" , args, new AsyncCallback(){
	            public void onSuccess(Object result){
	            	ScreenVertical vp = (ScreenVertical) widgets.get("treeContainer");
	            	ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
	            	vp.clear();
	            	vp.getPanel().add(tree);
	                PagedTreeField treeField = (PagedTreeField)result;
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
        setBpanel((ButtonPanel) getWidget("buttons"));
        
        final ScreenVertical vp = (ScreenVertical) widgets.get("treeContainer");
        
        vp.clear();
        vp.remove(tree);
        
        window.setStatus("","spinnerIcon");
        
        StringObject name = new StringObject();
        StringObject desc = new StringObject();
        name.setValue("*");
        desc.setValue("*");
        
       // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {name,desc}; 
        screenService.getObject("getTreeModel" , args, new AsyncCallback(){
            public void onSuccess(Object result){
            	vp.clear();
            	vp.getPanel().add(tree);
                PagedTreeField treeField = (PagedTreeField)result;
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
			int id = Integer.parseInt((String)item.getUserObject());
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
	        DataObject[] args = new DataObject[] {idObj,name,desc}; 
	        
            screenService.getObject("getTreeModelSecondLevel", args, new AsyncCallback(){
	            public void onSuccess(Object result){
	               finalTreeItem.removeItems();
	               tree.controller.model.addTextChildItems(finalTreeItem, (String)((StringObject)result).getValue());	        
                   
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
