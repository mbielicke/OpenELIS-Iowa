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
package org.openelis.modules.editNote.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.LastPageException;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.TextArea;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.LeafOpenedEvent;
import org.openelis.gwt.widget.tree.event.LeafOpenedHandler;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.organization.client.OrganizationDef;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EditNoteScreen extends Screen implements HasActionHandlers<EditNoteScreen.Action> {

    private NoteViewDO managerNoteDO, screenNoteDO;

    public enum Action {
        COMMIT, ABORT
    };

    protected TextArea                text, preview;
    protected TextBox                 subject, search;
    protected AppButton               pasteButton, findButton;
    protected TreeWidget              tree;

    protected ArrayList<DictionaryDO> categoryList;

    private StandardNoteMetaMap       meta = new StandardNoteMetaMap();

    public EditNoteScreen() throws Exception {
        super((ScreenDefInt)GWT.create(EditNoteDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.editNote.server.EditNoteService");

        // Setup link between Screen and widget Handlers
        initialize();
        setState(State.DEFAULT);
    }

    private void initialize() {
        tree = (TreeWidget)def.getWidget("noteTree");
        addScreenHandler(tree, new ScreenEventHandler<ArrayList<TreeDataItem>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                tree.enable(true);
            }
        });

        tree.addLeafOpenedHandler(new LeafOpenedHandler() {
            public void onLeafOpened(LeafOpenedEvent event) {
                final TreeDataItem row = event.getItem();
                if ( !row.isLoaded()) {
                    find(row, (Integer)row.key);
                    row.checkForChildren(false);

                }
            }
        });

        tree.addSelectionHandler(new SelectionHandler<TreeDataItem>() {
            public void onSelection(SelectionEvent<TreeDataItem> event) {
                // TreeRow selectedRow = event.getSelectedItem();
                TreeDataItem item = event.getSelectedItem();

                if ("note".equals(item.leafType))
                    preview.setValue((String)item.data, true);
                else
                    preview.setValue("", true);
            }
        });

        subject = (TextBox)def.getWidget("subject");
        addScreenHandler(subject, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                subject.setValue(screenNoteDO.getSubject());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                screenNoteDO.setSubject(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (screenNoteDO != null) {
                    if ("N".equals(screenNoteDO.getIsExternal())) {
                        subject.setVisible(true);
                        subject.setFocus(true);
                    } else {
                        subject.setVisible(false);
                        text.setFocus(true);
                    }
                }

            }
        });

        text = (TextArea)def.getWidget("text");
        addScreenHandler(text, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                text.setValue(screenNoteDO.getText());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                screenNoteDO.setText(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                text.enable(true);

            }
        });

        search = (TextBox)def.getWidget("findTextBox");
        addScreenHandler(search, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                search.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                search.enable(true);

            }
        });

        preview = (TextArea)def.getWidget("preview");
        addScreenHandler(preview, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                preview.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // disable the paste button if the preview text is empty
                if ("".equals(preview.getValue()))
                    pasteButton.enable(false);
                else
                    pasteButton.enable(true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preview.enable(false);

            }
        });

        pasteButton = (AppButton)def.getWidget("pasteButton");
        addScreenHandler(pasteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                paste();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if ("".equals(preview.getValue()))
                    pasteButton.enable(false);
                else
                    pasteButton.enable(true);
            }
        });

        findButton = (AppButton)def.getWidget("findButton");
        addScreenHandler(findButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if ("".equals(search.getValue()))
                    buildTree();
                else
                    find();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                findButton.enable(true);
            }
        });

        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });

        final AppButton abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(true);
            }
        });
    }

    private void find() {
        QueryData field;
        Query query;
        ArrayList<QueryData> fields;

        fields = new ArrayList<QueryData>();

        field = new QueryData();
        field.type = QueryData.Type.STRING;
        field.query = search.getValue();
        field.key = meta.getName();
        fields.add(field);

        field = new QueryData();
        field.type = QueryData.Type.STRING;
        field.query = search.getValue();
        field.key = meta.getDescription();
        fields.add(field);

        query = new Query();
        query.setFields(fields);

        executeQuery(query, null);
    }

    private void find(TreeDataItem row, Integer typeId) {
        Query query;
        QueryData field;

        field = new QueryData();
        field.type = QueryData.Type.INTEGER;
        field.query = String.valueOf(typeId);
        field.key = meta.getTypeId();

        query = new Query();
        query.setFields(field);

        executeQuery(query, row);
    }

    private void executeQuery(Query query, final TreeDataItem row) {
        window.setBusy("querying");

        service.callList("query", query, new AsyncCallback<ArrayList<StandardNoteDO>>() {
            public void onSuccess(ArrayList<StandardNoteDO> result) {
                if (row == null)
                    buildTree(result);
                else
                    buildTree(row, result);
                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                if (row == null)
                    buildTree(null);
                else
                    buildTree(row, null);

                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    setState(State.DEFAULT);
                } else if (error instanceof LastPageException) {
                    window.setError("No more records in this direction");
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
        tmpValue = text.getText().substring(0, cursorIndex) + preview.getText() +
                   text.getText().substring(cursorIndex);
        text.setValue(tmpValue, true);
        text.setSelectionRange(cursorIndex, preview.getText().length());
    }

    public void commit() {
        if (validate()) {
            managerNoteDO.copy(screenNoteDO);

            ActionEvent.fire(this, Action.COMMIT, null);
            clearErrors();
            window.close();
        }
    }

    public void abort() {
        ActionEvent.fire(this, Action.ABORT, null);
        clearErrors();
        window.close();
    }

    protected boolean validate() {
        return true;
        /*
         * boolean valid = true; if (screenNoteDO != null) { if
         * ("N".equals(screenNoteDO.getIsExternal())) { if
         * (subject.getValue().trim().length() == 0) {
         * subject.addError(consts.get("fieldRequiredException")); valid =
         * false; } } if (text.getValue().trim().length() == 0) {
         * text.addError(consts.get("fieldRequiredException")); valid = false; }
         * } window.setError(consts.get("correctErrors")); return valid;
         */
    }

    private void buildTree(ArrayList<StandardNoteDO> noteList) {
        Integer oldTypeId, currentTypeId;
        TreeDataItem catNode = null, noteNode = null;
        StandardNoteDO note;

        if (tree.numRows() > 0)
            tree.clear();

        if (noteList == null)
            return;

        oldTypeId = null;
        for (int i = 0; i < noteList.size(); i++ ) {
            note = noteList.get(i);
            currentTypeId = note.getTypeId();

            if ( !currentTypeId.equals(oldTypeId)) {
                oldTypeId = currentTypeId;
                DictionaryDO dictDO = DictionaryCache.getEntryFromId(currentTypeId);

                catNode = new TreeDataItem(1);
                catNode.leafType = "category";
                catNode.key = dictDO.getId();
                catNode.cells.get(0).value = dictDO.getEntry();
                catNode.open = true;
                tree.addRow(catNode);
            }

            noteNode = new TreeDataItem(1);
            noteNode.leafType = "note";
            noteNode.key = screenNoteDO.getId();
            noteNode.cells.get(0).value = note.getName() + " : " + note.getDescription();
            noteNode.data = note.getText();
            tree.addChildItem(catNode, noteNode);
        }
    }

    private void buildTree(TreeDataItem row, ArrayList<StandardNoteDO> noteList) {
        if (noteList == null)
            return;

        for (int i = 0; i < noteList.size(); i++ ) {
            StandardNoteDO noteDO = noteList.get(i);

            TreeDataItem treeModelItem = new TreeDataItem(1);
            treeModelItem.leafType = "note";
            treeModelItem.key = noteDO.getId();
            treeModelItem.cells.get(0).value = noteDO.getName() + " : " + noteDO.getDescription();
            treeModelItem.data = noteDO.getText();

            tree.addChildItem(row, treeModelItem);
        }
    }

    private void buildTree() {
        if (tree.numRows() > 0)
            tree.clear();

        if (categoryList == null)
            categoryList = DictionaryCache.getListByCategorySystemName("standard_note_type");

        ArrayList<TreeDataItem> treeList = new ArrayList<TreeDataItem>();
        for (int i = 0; i < categoryList.size(); i++ ) {
            DictionaryDO dictDO = categoryList.get(i);

            TreeDataItem treeModelItem = new TreeDataItem(1);
            treeModelItem.leafType = "category";
            treeModelItem.checkForChildren(true);
            treeModelItem.key = dictDO.getId();
            treeModelItem.cells.get(0).value = dictDO.getEntry();

            treeList.add(treeModelItem);
        }

        tree.load(treeList);
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public void setNote(NoteViewDO note) {
        screenNoteDO = new NoteViewDO();
        screenNoteDO.copy(note);
        managerNoteDO = note;

        buildTree();
        DataChangeEvent.fire(this);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
