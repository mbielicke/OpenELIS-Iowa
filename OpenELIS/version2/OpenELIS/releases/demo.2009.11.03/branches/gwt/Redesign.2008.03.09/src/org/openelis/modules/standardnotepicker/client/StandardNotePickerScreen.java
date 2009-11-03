package org.openelis.modules.standardnotepicker.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.PopupWindow;
import org.openelis.gwt.widget.pagedtree.TreeModel;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class StandardNotePickerScreen extends OpenELISScreenForm implements TreeListener{

	PopupWindow window;
	public TextArea noteTextArea;
	
	public StandardNotePickerScreen(TextArea noteTextArea) {
		super("org.openelis.modules.standardnote.server.StandardNotePickerService",false);
		this.noteTextArea = noteTextArea;		
        name="Standard Note Selection";
	}
	
	public void afterDraw(boolean sucess) {
        window = new PopupWindow("Choose Standard Note");
        window.content.add(this);
        window.content.setStyleName("Content");
        window.setContentPanel(window.content);
        final ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
        tree.controller.setTreeListener(this);
        bpanel = (ButtonPanel) getWidget("buttons");
        super.afterDraw(sucess);
        
        bpanel.setButtonState("commit", AppButton.UNPRESSED);
        bpanel.setButtonState("abort", AppButton.UNPRESSED);
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                window.setVisible(true);
                window.setPopupPosition((Window.getClientWidth() - window.getOffsetWidth())/2,100);
                window.size();
            }
        });
        
        final ScreenVertical vp = (ScreenVertical) widgets.get("treeContainer");
        final HTML loadingHtml = new HTML();
        loadingHtml.setHTML("<img src=\"Images/OSXspinnerGIF.gif\"> Loading...");
        vp.clear();
        //vp.remove(tree);
        vp.add(loadingHtml);
        screenService.getObject("method" , null, new AsyncCallback(){
            public void onSuccess(Object result){
            	vp.clear();
            	vp.add(tree);
                ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
                PagedTreeField treeField = new PagedTreeField();
                treeField.setValue((TreeModel)result);
                tree.load(treeField);
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
         });        
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
			int id = Integer.parseInt((String)item.getUserObject());
			final TreeItem finalTreeItem = item;
			finalTreeItem.removeItems();
			
			final ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
			final HTML loadingHtml = new HTML();
	        loadingHtml.setHTML("<img src=\"Images/OSXspinnerGIF.gif\"> Loading...");
			finalTreeItem.addItem(loadingHtml);
//			need async request to xml for 2nd layer
			screenService.getObject("id", null, new AsyncCallback(){
	            public void onSuccess(Object result){
	               finalTreeItem.removeItems();
	               tree.controller.model.addTextChildItems(finalTreeItem, (String)result);	           
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
	
	public void abort(int state) {
		window.close();
	}
	
	public void commit(int state) {
		TextArea textArea = (TextArea)getWidget("noteText");
		noteTextArea.setText(noteTextArea.getText()+textArea.getText());
		
		window.close();
	}
}
