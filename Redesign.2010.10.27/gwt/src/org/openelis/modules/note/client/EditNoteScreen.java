/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.note.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextArea;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.tree.Node;
import org.openelis.gwt.widget.tree.Tree;
import org.openelis.gwt.widget.tree.event.BeforeNodeOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeNodeOpenHandler;
import org.openelis.gwt.widget.tree.event.NodeOpenedEvent;
import org.openelis.gwt.widget.tree.event.NodeOpenedHandler;
import org.openelis.meta.StandardNoteMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EditNoteScreen extends Screen implements HasActionHandlers<EditNoteScreen.Action> {

    private NoteViewDO              note;
    private TextArea                text, preview;
    private TextBox<String>         subject, search;
    private Button                  pasteButton, findButton, okButton, cancelButton;
    private Tree                    tree;
    private ArrayList<DictionaryDO> categoryList;

    public enum Action {
        OK, CANCEL
    };

    public EditNoteScreen() throws Exception {
        super((ScreenDefInt)GWT.create(EditNoteDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.standardnote.server.StandardNoteService");

        initialize();
        setState(State.DEFAULT);
    }

    private void initialize() {
        tree = (Tree)def.getWidget("noteTree");
        addScreenHandler(tree, new ScreenEventHandler<Node>() {
            public void onStateChange(StateChangeEvent<State> event) {
                tree.setEnabled(true);
            }
        });

        tree.addNodeOpenedHandler(new NodeOpenedHandler() {
			public void onNodeOpened(NodeOpenedEvent event) {
                final Node node = event.getNode();
                if ( !node.isLoaded()) {
                    find(node, (Integer)node.getKey());
                }
            }
        });

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                // TreeRow selectedRow = event.getSelectedItem();
                Node node;
                
                node = tree.getNodeAt(event.getSelectedItem());

                if ("note".equals(node.getType()))
                    preview.setValue((String)node.getData(), true);
                else
                    preview.setValue("", true);
            }
        });

        subject = (TextBox)def.getWidget("subject");
        addScreenHandler(subject, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                subject.setValue(note.getSubject());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // OK button will set the note's subject
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (note != null) {
                    if ("N".equals(note.getIsExternal())) {
                        subject.setEnabled(true);
                        subject.setVisible(true);
                    } else {
                        subject.setEnabled(false);
                        subject.setVisible(false);
                    }
                }

            }
        });

        text = (TextArea)def.getWidget("text");
        addScreenHandler(text, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                text.setValue(note.getText());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // OK button will set the note's text
            }

            public void onStateChange(StateChangeEvent<State> event) {
                text.setEnabled(true);
            }
        });

        search = (TextBox)def.getWidget("findTextBox");
        addScreenHandler(search, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                search.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                search.setEnabled(true);
            }
        });

        preview = (TextArea)def.getWidget("preview");
        addScreenHandler(preview, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                preview.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // disable the paste button if the preview text is empty
                pasteButton.setEnabled(! "".equals(preview.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preview.setEnabled(false);
            }
        });

        pasteButton = (Button)def.getWidget("pasteButton");
        addScreenHandler(pasteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                paste();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pasteButton.setEnabled(! "".equals(preview.getValue()));
            }
        });

        findButton = (Button)def.getWidget("findButton");
        addScreenHandler(findButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if ("".equals(search.getValue()))
                    buildTree();
                else
                    find();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                findButton.setEnabled(true);
            }
        });

        okButton = (Button)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.setEnabled(true);
            }
        });

        cancelButton = (Button)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.setEnabled(true);
            }
        });
    }

    private void find() {
        Query query;
        QueryData field;
        QueryFieldUtil parser;
        
        query = new Query();
        parser = new QueryFieldUtil();
        try {
        	parser.parse("*" + search.getValue() + "*");
        }catch(Exception e) {
        	
        }

        field = new QueryData();
        field.type = QueryData.Type.STRING;
        field.query = parser.getParameter().get(0);
        field.key = StandardNoteMeta.getName();
        query.setFields(field);

        field = new QueryData();
        field.type = QueryData.Type.STRING;
        field.query = parser.getParameter().get(0);
        field.key = StandardNoteMeta.getDescription();
        query.setFields(field);

        window.setBusy("querying");

        service.callList("fetchByNameOrDescription", query, new AsyncCallback<ArrayList<StandardNoteDO>>() {
            public void onSuccess(ArrayList<StandardNoteDO> result) {
                buildTree(result);
                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                buildTree(null);

                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    setState(State.DEFAULT);
                } else {
                    Window.alert("Error: EditNote call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }

    private void find(final Node node, Integer typeId) {
        window.setBusy("querying");

        service.callList("fetchByType", typeId, new AsyncCallback<ArrayList<StandardNoteDO>>() {
            public void onSuccess(ArrayList<StandardNoteDO> result) {
                buildTree(node, result);
                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                buildTree(node, null);

                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    setState(State.DEFAULT);
                } else {
                    Window.alert("Error: EditNote call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }

    private void paste() {
        String tmpValue;
        int cursorIndex = text.getCursorPos();
        text.setFocus(true);
        tmpValue = text.getValue().substring(0, cursorIndex) + preview.getValue() +
                   text.getValue().substring(cursorIndex);
        text.setValue(tmpValue, true);
        text.setSelectionRange(cursorIndex, preview.getValue().length());
    }

    public void ok() {
        String trimText;
        
        clearErrors();
        if (validate()) {
            note.setSubject(subject.getText());
            //
            // Convert all spaces and carriage returns to single spaces
            //
            trimText = text.getValue();
            trimText = trimText.replaceAll("\n+"," ");
            trimText = trimText.replaceAll(" +"," ");
            note.setText(trimText);
            setState(State.DEFAULT);
            ActionEvent.fire(this, Action.OK, null);
            clearErrors();
            window.close();
        }
    }

    public void cancel() {
        setState(State.DEFAULT);
        ActionEvent.fire(this, Action.CANCEL, null);
        clearErrors();
        window.close();
    }

    public boolean validate() {
        boolean valid;
        
        valid = true;
        if (note != null) {
            if ("N".equals(note.getIsExternal())) {
                if (subject.getValue().trim().length() == 0 && text.getValue().trim().length() > 0) {
                    subject.addException(new FieldErrorException("fieldRequiredException", ""));
                    window.setError(consts.get("correctErrors"));
                    valid = false;
                }
            }
        }

        return valid;
    }

    private void buildTree(ArrayList<StandardNoteDO> noteList) {
        Integer oldTypeId, currentTypeId;
        Node catNode = null, noteNode = null;
        StandardNoteDO note;
        DictionaryDO dictDO;

        if (tree.getRowCount() > 0)
            tree.clear();

        if (noteList == null)
            return;

        oldTypeId = null;
        for (int i = 0; i < noteList.size(); i++ ) {
            note = noteList.get(i);
            currentTypeId = note.getTypeId();

            if ( !currentTypeId.equals(oldTypeId)) {
                oldTypeId = currentTypeId;

                try {
                    dictDO = DictionaryCache.getEntryFromId(currentTypeId);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    dictDO = null;
                }

                catNode = new Node(1);
                catNode.setType("category");
                catNode.setKey(dictDO.getId());
                catNode.setCell(0,dictDO.getEntry());
                catNode.setOpen(true);
                tree.addNode(catNode);
            }

            noteNode = new Node(1);
            noteNode.setType("note");
            noteNode.setKey(note.getId());
            noteNode.setCell(0,note.getName() + " : " + note.getDescription());
            noteNode.setData(note.getText());
            tree.addNodeAt(catNode, noteNode);
        }
    }

    private void buildTree(Node node, ArrayList<StandardNoteDO> noteList) {
    	 StandardNoteDO noteDO;
    	 Node child;
    	
        if (noteList == null)
            return;

        for (int i = 0; i < noteList.size(); i++ ) {
            noteDO = noteList.get(i);

            child = new Node(1);
            child.setType("note");
            child.setKey(noteDO.getId());
            child.setCell(0,noteDO.getName() + " : " + noteDO.getDescription());
            child.setData(noteDO.getText());

            tree.addNodeAt(node, child);
        }
    }

    private void buildTree() {
    	Node root,node;
    	DictionaryDO dictDO; 
    	
        if (tree.getRowCount() > 0)
            tree.clear();

        if (categoryList == null)
            categoryList = DictionaryCache.getListByCategorySystemName("standard_note_type");

        root = new Node();
        for (int i = 0; i < categoryList.size(); i++ ) {
            dictDO = categoryList.get(i);

            node = new Node(1);
            node.setType("category");
            node.setDeferLoadingUntilExpand(true);
            node.setKey(dictDO.getId());
            node.setCell(0,dictDO.getEntry());

            root.add(node);
        }

        tree.setRoot(root);
    }

    public void setNote(NoteViewDO aNote) {
        note = aNote;

        buildTree();
        DataChangeEvent.fire(this);
        StateChangeEvent.fire(this, state);
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                if ("N".equals(note.getIsExternal()))
                    setFocus(subject);
                else
                    setFocus(text);
            }
        });
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
