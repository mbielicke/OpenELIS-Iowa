package org.openelis.modules.utilities.client.standardNotePicker;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.widget.PopupWindow;
import org.openelis.gwt.widget.pagedtree.TreeModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class StandardNotePickerScreen extends AppScreenForm{

	private static StandardNotePickerServletIntAsync screenService = (StandardNotePickerServletIntAsync) GWT
	.create(StandardNotePickerServletInt.class);
	
	private static ServiceDefTarget target = (ServiceDefTarget) screenService;
	PopupWindow window;
	
	public StandardNotePickerScreen() {
		super();
		String base = GWT.getModuleBaseURL();
		base += "StandardNotePickerServlet";
		target.setServiceEntryPoint(base);
		service = screenService;
		formService = screenService;
		getXML();
	}
	
	public void afterDraw(boolean sucess) {
        window = new PopupWindow("Choose Standard Note");
        window.content.add(this);
        window.content.setStyleName("Content");
        window.setContentPanel(window.content);
       // ((ChooseMFKTable)((TableWidget)getWidget("chooseMFKTable")).controller.manager).setMFKForm(this);
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                window.setVisible(true);
                window.setPopupPosition((Window.getClientWidth() - window.getOffsetWidth())/2,100);
                window.size();
            }
        });
        
       
        screenService.getTreeModel(new Integer(1), true, new AsyncCallback(){
            public void onSuccess(Object result){
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
}
