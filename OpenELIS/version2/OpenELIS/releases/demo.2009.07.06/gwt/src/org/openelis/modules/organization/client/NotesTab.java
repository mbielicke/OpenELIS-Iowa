package org.openelis.modules.organization.client;

import java.util.EnumSet;
import java.util.Iterator;

import org.openelis.domain.NoteDO;
import org.openelis.gwt.event.CommandListenerCollection;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenDef;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.screen.rewrite.UIUtil;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;


public class NotesTab extends Screen {
	
	private ScreenDef def;
	private NotesRPC rpc;
    private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    private CommandListenerCollection commandListeners;
    private TextBox subject;
    private TextArea text;
	
	public NotesTab(ScreenDef def) {
		this.def = def;
		setHandlers();
	}
	
	EnumSet<State> enabledStates = EnumSet.of(State.ADD,State.QUERY,State.UPDATE);
	
	public void setHandlers() {
		subject = (TextBox) def.getWidget(OrgMeta.NOTE.getSubject());
		addScreenHandler(subject, new ScreenEventHandler<String>() {
			public void onDataChange(DataChangeEvent evet) {
				subject.setValue(rpc.subject);
			}
			public void onValueChange(ValueChangeEvent<String> event) {
				rpc.subject = event.getValue();
			}
			public void onStateChange(StateChangeEvent<State> event) {
				subject.setReadOnly(!enabledStates.contains(event.getState()));
			}
		});
		text = (TextArea) def.getWidget(OrgMeta.NOTE.getText());
		addScreenHandler(text,new ScreenEventHandler<String>() {
			public void onDataChange(DataChangeEvent event) {
				text.setValue(rpc.text);
			}
			public void onValueChange(ValueChangeEvent<String> event) {
				rpc.text = event.getValue();
			}
			public void onStateChange(StateChangeEvent<State> event) {
				text.setReadOnly(!enabledStates.contains(event.getState()));
			}
		});
		final ScrollPanel notesPanel = (ScrollPanel)def.getWidget("notesPanel");
		addScreenHandler(notesPanel,new ScreenEventHandler<String>() {
			public void onDataChange(DataChangeEvent event) {
				notesPanel.setWidget(getNotes());
			}
			public void onStateChange(StateChangeEvent<State> event) {
				if(event.getState() == State.ADD)
					notesPanel.clear();
			}
		});
		final AppButton standardNote = (AppButton)def.getWidget("standardNoteButton");
		standardNote.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
		        ScreenWindow modal = new ScreenWindow(null,"Standard Note Screen","standardNoteScreen","",true,false);
		        modal.setName(AppScreen.consts.get("standardNote"));
		        modal.setContent(new StandardNotePickerScreen(subject, text));
			}
		});
	}
	
	private Widget getNotes() {
        
        try{
        Document doc = XMLParser.parse("<VerticalPanel/>");
    	Element root = (Element) doc.getDocumentElement();
    	root.setAttribute("key", "notePanel");
    	if(rpc.notes == null) {
			return UIUtil.createWidget(root);
		}
    	int i=0;
    	Iterator itr = rpc.notes.iterator();
    	while(itr.hasNext()){
    	    NoteDO noteRow = (NoteDO)itr.next();
            
            //user id
            String userName = noteRow.getSystemUser();
            //body
            String body = noteRow.getText();
            
            if(body == null)
                body = "";
            
            //date
            String date = noteRow.getTimestamp().toString();
            //subject
            String subject = noteRow.getSubject();
            
            if(subject == null)
                subject = "";
                        
        	 Element mainRowPanel = (Element) doc.createElement("VerticalPanel");
        	 Element topRowPanel = (Element) doc.createElement("HorizontalPanel");
        	 Element titleWidgetTag = (Element) doc.createElement("widget");
        	 Element titleText = (Element) doc.createElement("text");
        	 Element authorWidgetTag = (Element) doc.createElement("widget");
        	 Element authorPanel = (Element) doc.createElement("VerticalPanel");
        	 Element dateText = (Element) doc.createElement("text");
        	 Element authorText = (Element) doc.createElement("text");
        	 Element bodyWidgetTag = (Element) doc.createElement("widget");
        	 Element bodytextTag =  (Element) doc.createElement("text");
        	 
        	 mainRowPanel.setAttribute("key", "note"+i);
        	 if(i % 2 == 1){
                 mainRowPanel.setAttribute("style", "AltTableRow");
             }else{
            	 mainRowPanel.setAttribute("style", "TableRow");
             }
        	 mainRowPanel.setAttribute("width", "530px");
        	 
        	 topRowPanel.setAttribute("width", "530px");
        	 titleText.setAttribute("key", "note"+i+"Title");
        	 titleText.setAttribute("style", "notesSubjectText");
        	 titleText.appendChild(doc.createTextNode(subject));
        	 authorWidgetTag.setAttribute("halign", "right");
        	 dateText.setAttribute("key", "note"+i+"Date");
        	 dateText.appendChild(doc.createTextNode(date));
        	 authorText.setAttribute("key", "note"+i+"Author");
        	 authorText.appendChild(doc.createTextNode("by "+userName));
        	 bodytextTag.setAttribute("key", "note"+i+"Body");
        	 bodytextTag.setAttribute("wordwrap", "true");
        	 bodytextTag.appendChild(doc.createTextNode(body));
        	 
        	 root.appendChild(mainRowPanel);
        	 mainRowPanel.appendChild(topRowPanel);
        	 mainRowPanel.appendChild(bodyWidgetTag);
        	 topRowPanel.appendChild(titleWidgetTag);
        	 topRowPanel.appendChild(authorWidgetTag);
        	 titleWidgetTag.appendChild(titleText);
        	 authorWidgetTag.appendChild(authorPanel);
        	 authorPanel.appendChild(dateText);
        	 authorPanel.appendChild(authorText);
        	 bodyWidgetTag.appendChild(bodytextTag);
             
             i++;
      }
        
        return UIUtil.createWidget(root);
    	
        }catch(Exception e){
        	e.printStackTrace();
        }
        return null;
	}
	
	public void setRPC(NotesRPC rpc) {
		if(rpc == null)
			rpc = new NotesRPC();
		this.rpc = rpc;
		DataChangeEvent.fire(this);
	}
	

}
