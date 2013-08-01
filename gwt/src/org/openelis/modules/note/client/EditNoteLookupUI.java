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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.meta.StandardNoteMeta;
import org.openelis.modules.standardnote.client.StandardNoteService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextArea;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;
import org.openelis.ui.widget.tree.event.BeforeNodeOpenEvent;
import org.openelis.ui.widget.tree.event.BeforeNodeOpenHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public abstract class EditNoteLookupUI extends Screen {

    @UiTemplate("EditNoteLookup.ui.xml")
    interface EditNoteLookupUIBinder extends UiBinder<Widget, EditNoteLookupUI> {
    };

    private static EditNoteLookupUIBinder uiBinder = GWT.create(EditNoteLookupUIBinder.class);

    @UiField
    protected TextArea                    text, preview;

    @UiField
    protected TextBox<String>             subject, find;

    @UiField
    protected Button                      pasteButton, findButton, okButton, cancelButton;

    @UiField
    protected Tree                        noteTree;

    protected String                      noteSubject, noteText;

    protected boolean                     hasSubject, reloadTree;

    public EditNoteLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        reloadTree = true;
    }

    private void initialize() {
        addScreenHandler(noteTree, "noteTree", new ScreenHandler<ArrayList<Node>>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * the tree is only loaded when DataChangeEvent is fired for the
                 * first time after bringing this class up; it's fired every
                 * time a field like noteSubject is set by the code invoking
                 * this class
                 */
                if (reloadTree) {
                    noteTree.setRoot(getRoot());
                    reloadTree = false;
                }
            }

            public void onStateChange(StateChangeEvent event) {
                noteTree.setEnabled(true);
            }
        });

        noteTree.addBeforeNodeOpenHandler(new BeforeNodeOpenHandler() {
            @Override
            public void onBeforeNodeOpen(BeforeNodeOpenEvent event) {
                Node node;

                node = event.getNode();

                if ( !node.isLoaded()) {
                    getNotesForCategory(node, (Integer)node.getKey());
                    node.setDeferLoadingUntilExpand(false);
                }
            }
        });

        noteTree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                Node item, parent;

                item = noteTree.getNodeAt(event.getSelectedItem());
                if ("note".equals(item.getType())) {
                    preview.setValue((String)item.getData(), true);
                    /*
                     * set the category's name as the subject if it already
                     * hasn't been specified and if the subject is showing
                     */
                    if (subject.isEnabled() && subject.isVisible() &&
                        DataBaseUtil.isEmpty(subject.getValue())) {
                        parent = item.getParent();
                        subject.setValue((String)parent.getCell(0));
                    }
                } else {
                    preview.setValue("", true);
                }
            }
        });

        addScreenHandler(subject, "subject", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                subject.setValue(noteSubject);
            }

            public void onStateChange(StateChangeEvent event) {
                subject.setEnabled(hasSubject);
            }
        });

        addScreenHandler(text, "text", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                text.setValue(noteText);
            }

            public void onStateChange(StateChangeEvent event) {
                text.setEnabled(true);
            }
        });

        addScreenHandler(find, "find", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                find.setValue(null);
            }

            public void onStateChange(StateChangeEvent event) {
                find.setEnabled(true);
            }
        });

        addScreenHandler(preview, "preview", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                preview.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                pasteButton.setEnabled( !DataBaseUtil.isEmpty(event.getValue()));
            }

            public void onStateChange(StateChangeEvent event) {
                preview.setEnabled(false);
            }
        });

        addScreenHandler(pasteButton, "pasteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                pasteButton.setEnabled(false);
            }
        });

        addScreenHandler(findButton, "findButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                findButton.setEnabled(true);
            }
        });

        addScreenHandler(okButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                okButton.setEnabled(true);
            }
        });

        addScreenHandler(cancelButton, "cancelButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancelButton.setEnabled(true);
            }
        });
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setSubject(String noteSubject) {
        this.noteSubject = noteSubject;
        refresh();
    }

    public String getSubject() {
        return noteSubject;
    }

    public void setText(String noteText) {
        this.noteText = noteText;
        refresh();
    }

    public String getText() {
        return noteText;
    }

    public void setHasSubject(boolean hasSubject) {
        this.hasSubject = hasSubject;
        refresh();
    }

    public boolean getHasSubject() {
        return hasSubject;
    }

    public boolean validate() {
        if (hasSubject) {
            if (DataBaseUtil.isEmpty(subject.getValue()) && !DataBaseUtil.isEmpty(text.getValue())) {
                subject.addException(new FieldErrorException(Messages.get()
                                                                     .fieldRequiredException(), ""));
                window.setError(Messages.get().correctErrors());
                return false;
            }
        }

        return true;
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    @UiHandler("findButton")
    protected void find(ClickEvent event) {
        Query query;
        QueryData field;
        QueryFieldUtil parser;

        if (DataBaseUtil.isEmpty(find.getValue())) {
            noteTree.setRoot(getRoot());
            return;
        }

        query = new Query();
        parser = new QueryFieldUtil();
        try {
            /*
             * this is done to have "like" in the clause instead of "="
             */
            parser.parse("*" + find.getValue() + "*");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }

        field = new QueryData();
        field.setType(QueryData.Type.STRING);
        field.setQuery(parser.getParameter().get(0));
        field.setKey(StandardNoteMeta.getName());
        query.setFields(field);

        field = new QueryData();
        field.setType(QueryData.Type.STRING);
        field.setQuery(parser.getParameter().get(0));
        field.setKey(StandardNoteMeta.getDescription());
        query.setFields(field);

        window.setBusy(Messages.get().querying());

        StandardNoteService.get()
                           .fetchByNameOrDescription(query,
                                                     new AsyncCallback<ArrayList<StandardNoteDO>>() {
                                                         public void onSuccess(ArrayList<StandardNoteDO> result) {
                                                             noteTree.setRoot(getNotes(result));
                                                             window.clearStatus();
                                                         }

                                                         public void onFailure(Throwable error) {
                                                             noteTree.setRoot(getNotes(null));

                                                             if (error instanceof NotFoundException) {
                                                                 window.setDone(Messages.get()
                                                                                        .noRecordsFound());
                                                             } else {
                                                                 Window.alert("Error: EditNote call query failed; " +
                                                                              error.getMessage());
                                                                 window.setError(Messages.get()
                                                                                         .queryFailed());
                                                             }
                                                         }
                                                     });
    }

    @UiHandler("pasteButton")
    protected void paste(ClickEvent event) {
        int cursorPos;
        String selected, current;

        selected = preview.getValue();
        cursorPos = 0;
        /*
         * copy from preview to the cursor's position in the note's text
         */
        current = text.getValue();
        if ( !DataBaseUtil.isEmpty(current)) {
            cursorPos = text.getCursorPos();
            selected = DataBaseUtil.concatWithSeparator(current.substring(0, cursorPos),
                                                        selected,
                                                        current.substring(cursorPos));
        }

        text.setFocus(true);
        text.setValue(selected, true);
        text.setSelectionRange(cursorPos, preview.getValue().length());
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        String trimText;

        clearErrors();
        if ( !validate())
            return;

        noteSubject = subject.getValue();

        trimText = text.getValue();
        //
        // a PatternSyntaxException is thrown if trimText is empty
        //
        if ( !DataBaseUtil.isEmpty(trimText)) {
            //
            // Convert bunch of repeated chars to reasonable white spaces
            //
            trimText = trimText.replaceAll("\n{3, }", "\n\n"); // 3 or more
                                                               // to 2
            trimText = trimText.replaceAll(" {2, }", " "); // 2 or more to 1
        }
        noteText = trimText;
        window.close();
        reloadTree = true;
        ok();
    }

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        window.close();
        reloadTree = true;
        cancel();
    }

    private void refresh() {
        setState(state);
        fireDataChange();
        clearErrors();

        if (hasSubject)
            subject.setFocus(true);
        else
            text.setFocus(true);
    }

    private void getNotesForCategory(final Node node, Integer categoryId) {
        ArrayList<StandardNoteDO> notes;

        window.setBusy(Messages.get().fetching());

        try {
            notes = StandardNoteService.get().fetchByType(categoryId);
            for (StandardNoteDO n : notes)
                noteTree.addNodeAt(node, createNoteNode(n));
            window.clearStatus();
        } catch (NotFoundException e) {
            window.setDone(Messages.get().noRecordsFound());
        } catch (Exception e) {
            Window.alert("Error: EditNote call query failed; " + e.getMessage());
            window.setError(Messages.get().queryFailed());
        }
    }

    private Node getNotes(ArrayList<StandardNoteDO> notes) {
        Integer prevTypeId, currTypeId;
        Node node, root;
        DictionaryDO d;

        root = new Node();

        if (notes == null)
            return root;

        prevTypeId = null;
        node = null;
        for (StandardNoteDO note : notes) {
            currTypeId = note.getTypeId();

            if ( !currTypeId.equals(prevTypeId)) {
                prevTypeId = currTypeId;

                try {
                    d = DictionaryCache.getById(currTypeId);
                    node = createCategoryNode(d);
                    node.setOpen(true);
                    root.add(node);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    continue;
                }
            }

            node.add(createNoteNode(note));
        }

        return root;
    }

    private Node getRoot() {
        Node root, node;
        ArrayList<DictionaryDO> list;

        list = CategoryCache.getBySystemName("standard_note_type");
        root = new Node();
        for (DictionaryDO d : list) {
            node = createCategoryNode(d);
            node.setDeferLoadingUntilExpand(true);
            root.add(node);
        }

        return root;
    }

    private Node createCategoryNode(DictionaryDO d) {
        Node node;

        node = new Node(1);
        node.setType("category");
        node.setKey(d.getId());
        node.setCell(0, d.getEntry());

        return node;
    }

    private Node createNoteNode(StandardNoteDO n) {
        Node node;

        node = new Node(1);
        node.setType("note");
        node.setKey(n.getId());
        node.setCell(0, n.getDescription());
        node.setData(n.getText());

        return node;
    }
}