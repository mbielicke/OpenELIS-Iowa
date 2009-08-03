package org.openelis.modules.organization.client;

import org.openelis.domain.NoteDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
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
import org.openelis.manager.HasNotesInt;
import org.openelis.manager.NotesManager;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.standardnotepicker.client.EditNoteScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

public class NotesTab extends Screen {

    private HasNotesInt         parentManager;
    private NotesManager        manager;
    private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    private TextBox             subject;
    private TextArea            text;
    protected EditNoteScreen    editNote;
    private boolean             loaded  = false;

    public NotesTab(ScreenDef def) {
        setDef(def);
        initialize();
    }

    public void initialize() {
        /*
         * subject = (TextBox) def.getWidget(OrgMeta.NOTE.getSubject()); addScreenHandler(subject, new ScreenEventHandler<String>() { public void onDataChange(DataChangeEvent evet) { if(manager != null && manager.count() > 0) subject.setValue(manager.getNoteAt(0).getSubject()); else subject.setValue(null); } public void onValueChange(ValueChangeEvent<String> event) { if(manager != null && manager.count() > 0) manager.getNoteAt(0).setSubject(event.getValue());
         * 
         * } public void onStateChange(StateChangeEvent<State> event) { subject.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState())); } });
         */
        /*
         * text = (TextArea) def.getWidget(OrgMeta.NOTE.getText()); addScreenHandler(text,new ScreenEventHandler<String>() { public void onDataChange(DataChangeEvent event) { if(manager != null && manager.count() > 0) text.setValue(manager.getNoteAt(0).getText()); else text.setValue(null); } public void onValueChange(ValueChangeEvent<String> event) { if(manager != null && manager.count() > 0) manager.getNoteAt(0).setText(event.getValue()); } public void onStateChange(StateChangeEvent<State> event) { text.setReadOnly(!EnumSet.of(State.ADD,State.UPDATE).contains(event.getState())); } });
         */
        final ScrollPanel notesPanel = (ScrollPanel)def.getWidget("notesPanel");
        addScreenHandler(notesPanel, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                notesPanel.setWidget(getNotes());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD)
                    notesPanel.clear();
            }
        });
        final AppButton standardNote = (AppButton)def.getWidget("standardNoteButton");
        addScreenHandler(standardNote, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (editNote == null) {
                    try {
                        editNote = new EditNoteScreen();
                        editNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
                            public void onAction(ActionEvent<EditNoteScreen.Action> event) {
                                if (event.getAction() == EditNoteScreen.Action.COMMIT) {
                                    loaded = false;
                                    draw();
                                }
                            }
                        });
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert("error: " + e.getMessage());
                        return;
                    }
                }

                ScreenWindow modal = new ScreenWindow(null,
                                                      "Edit Note Screen",
                                                      "editNoteScreen",
                                                      "",
                                                      true,
                                                      false);
                modal.setName(AppScreen.consts.get("standardNote"));
                modal.setContent(editNote);
                
                editNote.setNote(manager.getInternalEditingNote());
            }

            
            public void onStateChange(StateChangeEvent<State> event) {
                
                if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                    standardNote.enable(true);
                else
                    standardNote.enable(false);
            }
        });
    }

    private Widget getNotes() {

        try {
            Document doc = XMLParser.parse("<VerticalPanel/>");
            Element root = (Element)doc.getDocumentElement();
            root.setAttribute("key", "notePanel");
            if (manager == null || manager.count() == 0) {
                return UIUtil.createWidget(root);
            }

            for (int i = 0; i < manager.count(); i++) {
                NoteDO noteRow = manager.getNoteAt(i);

                // user id
                String userName = noteRow.getSystemUser();
                // body
                String body = noteRow.getText();

                if (body == null)
                    body = "";

                // date
                // String date = noteRow.getTimestamp().toString();
                // subject
                String subject = noteRow.getSubject();

                if (subject == null)
                    subject = "";

                Element mainRowPanel = (Element)doc.createElement("VerticalPanel");
                Element topRowPanel = (Element)doc.createElement("HorizontalPanel");
                Element titleWidgetTag = (Element)doc.createElement("widget");
                Element titleText = (Element)doc.createElement("text");
                Element authorWidgetTag = (Element)doc.createElement("widget");
                Element authorPanel = (Element)doc.createElement("VerticalPanel");
                Element dateText = (Element)doc.createElement("text");
                Element authorText = (Element)doc.createElement("text");
                Element bodyWidgetTag = (Element)doc.createElement("widget");
                Element bodytextTag = (Element)doc.createElement("text");

                mainRowPanel.setAttribute("key", "note" + i);
                if (i % 2 == 1) {
                    mainRowPanel.setAttribute("style", "AltTableRow");
                } else {
                    mainRowPanel.setAttribute("style", "TableRow");
                }
                mainRowPanel.setAttribute("width", "530px");

                topRowPanel.setAttribute("width", "530px");
                titleText.setAttribute("key", "note" + i + "Title");
                titleText.setAttribute("style", "notesSubjectText");
                titleText.appendChild(doc.createTextNode(subject));
                authorWidgetTag.setAttribute("halign", "right");
                dateText.setAttribute("key", "note" + i + "Date");
                // dateText.appendChild(doc.createTextNode(date));
                authorText.setAttribute("key", "note" + i + "Author");
                authorText.appendChild(doc.createTextNode("by " + userName));
                bodytextTag.setAttribute("key", "note" + i + "Body");
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setManager(HasNotesInt parentManager) {
        this.parentManager = parentManager;
        loaded = false;
    }

    public void draw() {
        if (parentManager != null && !loaded) {
            try {
                manager = parentManager.getNotes();
                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}
