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
package org.openelis.modules.sample1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.meta.AttachmentMeta;
import org.openelis.modules.attachment.client.AttachmentAddedEvent;
import org.openelis.modules.attachment.client.AttachmentScreenUI;
import org.openelis.modules.attachment.client.AttachmentService;
import org.openelis.modules.attachment.client.DisplayAttachmentEvent;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.SectionPermission.SectionFlags;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public abstract class AttachmentTabUI extends Screen {

    @UiTemplate("AttachmentTab.ui.xml")
    interface AttachmentTabUIBinder extends UiBinder<Widget, AttachmentTabUI> {
    };

    private static AttachmentTabUIBinder               uiBinder = GWT.create(AttachmentTabUIBinder.class);

    @UiField
    protected Table                                    currentTable, searchTable;

    @UiField
    protected Dropdown<Integer>                        currentTableSection, searchTableSection;

    @UiField
    protected Button                                   detachButton, displayButton, moveLeftButton,
                    searchButton, attachButton;

    @UiField
    protected Calendar                                 attachmentCreatedDate;

    @UiField
    protected TextBox<String>                          attachmentDescription;

    protected Screen                                   parentScreen;

    protected AttachmentTabUI                          screen;

    protected EventBus                                 parentBus;

    protected AttachmentScreenUI                       attachmentScreen;

    protected AsyncCallbackUI<ArrayList<AttachmentDO>> queryCall;

    protected boolean                                  isVisible, redraw, canQuery;

    public AttachmentTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;

        screen = this;

        addScreenHandler(currentTable, "currentTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( ! (isState(QUERY) && canQuery))
                    currentTable.setModel(getCurrentTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                currentTable.setEnabled(true);
                currentTable.setQueryMode(isState(QUERY) && canQuery);
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                if ( !isState(QUERY) || !canQuery)
                    return null;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 3; i++ ) {
                    qd = (QueryData) ((Queryable)currentTable.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(getAttachmentCreatedDateMetaKey());
                                break;
                            case 1:
                                qd.setKey(getAttachmentSectionIdKey());
                                break;
                            case 2:
                                qd.setKey(getAttachmentDescriptionKey());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }

            public Widget onTab(boolean forward) {
                return forward ? detachButton : attachButton;
            }
        });

        currentTable.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                detachButton.setEnabled(isState(ADD, UPDATE));
                displayButton.setEnabled(true);
            }
        });

        currentTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        currentTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                remove(event.getIndex());
                detachButton.setEnabled(false);
                displayButton.setEnabled(false);
            }
        });

        currentTable.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                /*
                 * passing null as the name will make the file open in a
                 * different window than the previous one because the value for
                 * name in the url will be the name of the temp file
                 */
                displayAttachment(currentTable.getSelectedRow(), false);
            }
        });

        addScreenHandler(detachButton, "detachButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                detachButton.setEnabled(isState(ADD, UPDATE) && currentTable.getSelectedRow() > -1);
            }

            public Widget onTab(boolean forward) {
                return forward ? displayButton : currentTable;
            }
        });

        addScreenHandler(displayButton, "displayButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                displayButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? attachmentCreatedDate : detachButton;
            }
        });

        addScreenHandler(moveLeftButton, "moveLeftButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                moveLeftButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addScreenHandler(attachmentCreatedDate,
                         AttachmentMeta.getCreatedDate(),
                         new ScreenHandler<ArrayList<Row>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 attachmentCreatedDate.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 attachmentCreatedDate.setEnabled(isState(ADD, UPDATE));
                                 attachmentCreatedDate.setQueryMode(isState(ADD, UPDATE));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? attachmentDescription : displayButton;
                             }
                         });

        addScreenHandler(attachmentDescription,
                         AttachmentMeta.getDescription(),
                         new ScreenHandler<ArrayList<Row>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 attachmentDescription.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 attachmentDescription.setEnabled(isState(ADD, UPDATE));
                                 attachmentDescription.setQueryMode(isState(ADD, UPDATE));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? searchButton : attachmentCreatedDate;
                             }
                         });

        addScreenHandler(searchButton, "searchButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                searchButton.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? searchTable : attachmentDescription;
            }
        });

        addScreenHandler(searchTable, "searchTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                searchTable.setModel(getSearchTableModel(null));
            }

            public void onStateChange(StateChangeEvent event) {
                searchTable.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? attachButton : attachmentDescription;
            }
        });

        searchTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        addScreenHandler(attachButton, "attachButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                attachButton.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? currentTable : searchTable;
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayAttachments();
            }
        });

        parentBus.addHandler(AttachmentAddedEvent.getType(), new AttachmentAddedEvent.Handler() {
            @Override
            public void onAttachmentAdded(AttachmentAddedEvent event) {
                redraw = true;
                displayAttachments();
            }
        });

        model = new ArrayList<Item<Integer>>();
        for (SectionDO s : SectionCache.getList())
            model.add(new Item<Integer>(s.getId(), s.getName()));

        currentTableSection.setModel(model);
        searchTableSection.setModel(model);
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * overridden to return the total number of attachment items in the manager
     */
    public abstract int count();

    /**
     * overridden to return the attachment item at the specified index in the
     * manager
     */
    public abstract AttachmentItemViewDO get(int i);

    /**
     * overridden to return the key for attachment created date from the meta
     * used by the main screen
     */
    public abstract String getAttachmentCreatedDateMetaKey();

    /**
     * overridden to return the key for attachment section id from the meta used
     * by the main screen
     */
    public abstract String getAttachmentSectionIdKey();

    /**
     * overridden to return the key for attachment description from the meta
     * used by the main screen
     */
    public abstract String getAttachmentDescriptionKey();

    /**
     * overridden to return the an AttachmentItemViewDO filled with the data of
     * the passed AttachmentDO
     */
    public abstract AttachmentItemViewDO createAttachmentItem(AttachmentDO att);

    /**
     * overridden to remove the attachment item at the passed index from the
     * manager used by the main screen
     */
    public abstract void remove(int i);

    /**
     * Notifies the tab that it may need to refresh the display in its widgets;
     * if the data currently showing in the widgets is the same as the data in
     * the latest manager then the widgets are not refreshed
     */
    public void onDataChange() {
        int i, count1, count2;
        AttachmentItemViewDO data;
        Row row;

        count1 = currentTable.getRowCount();
        count2 = count();

        if (count1 == count2) {
            /*
             * find out if there's any difference between the attachment item
             * being displayed and the attachment item in the manager
             */
            for (i = 0; i < count1; i++ ) {
                data = get(i);

                row = currentTable.getRowAt(i);
                if (DataBaseUtil.isDifferent(data.getAttachmentCreatedDate(), row.getCell(0)) ||
                    DataBaseUtil.isDifferent(data.getAttachmentSectionId(), row.getCell(1)) ||
                    DataBaseUtil.isDifferent(data.getAttachmentDescription(), row.getCell(2))) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }
        displayAttachments();
        /*
         * this is done here and not in displayButton's StateChangeHandler
         * because at the time StateChangeEvent is fired, the table may have
         * rows but this method may empty the table in which case displayButton
         * needs to be disabled
         */
        displayButton.setEnabled(currentTable.getSelectedRow() >= 0);
        searchTable.setModel(getSearchTableModel(null));
    }

    public void setCanQuery(boolean canQuery) {
        this.canQuery = canQuery;
    }

    /**
     * Deletes the selected row from the table showing the sample's attachment
     * items if the user has permission to do so; shows an error otherwise
     */
    @UiHandler("detachButton")
    protected void detach(ClickEvent event) {
        int r;
        AttachmentItemViewDO data;

        r = currentTable.getSelectedRow();
        if (r > -1 && currentTable.getRowCount() > 0) {
            data = get(r);
            if (hasPermission(data.getAttachmentSectionId(), SectionFlags.CANCEL))
                currentTable.removeRowAt(r);
            else
                Window.alert(Messages.get()
                                     .attachment_detachPermException(data.getAttachmentDescription()));
        }
    }

    /**
     * Opens the file linked to the attachment item at the selected row in the
     * table showing the sample's attachment items
     */
    @UiHandler("displayButton")
    protected void display(ClickEvent event) {
        displayAttachment(currentTable.getSelectedRow(), true);
    }

    /**
     * Adds to the sample, an attachment item from the row selected in the table
     * showing the results of the query for matching attachments
     */
    @UiHandler("moveLeftButton")
    protected void moveLeft(ClickEvent event) {
        AttachmentDO att;
        AttachmentItemViewDO atti;

        if (searchTable.getSelectedRow() < 0)
            return;

        /*
         * add the attachment selected in the table showing search results, to
         * the record e.g. sample, if the user has permission to attach it; show
         * an error otherwise
         */
        att = (AttachmentDO)searchTable.getRowAt(searchTable.getSelectedRow()).getData();
        if ( !canAttach(att)) {
            Window.alert(Messages.get().attachment_attachPermException(att.getDescription()));
            return;
        }
        atti = createAttachmentItem(att);

        currentTable.addRow(createAttachmentRow(atti));
        currentTable.selectRowAt(currentTable.getRowCount() - 1);
        detachButton.setEnabled(true);
        displayButton.setEnabled(true);
    }

    /**
     * Executes a query to fetch attachments matching the values specified by
     * the user in the created date and/or the description for attachment
     */
    @UiHandler("searchButton")
    protected void search(ClickEvent event) {
        Query query;

        query = new Query();
        query.setFields(getQueryFields());
        query.setRowsPerPage(1000);

        parentScreen.setBusy(Messages.get().gen_querying());

        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<AttachmentDO>>() {
                public void success(ArrayList<AttachmentDO> result) {
                    parentScreen.clearStatus();
                    searchTable.setModel(getSearchTableModel(result));
                }

                public void notFound() {
                    searchTable.setModel(getSearchTableModel(null));
                    parentScreen.setDone(Messages.get().gen_noRecordsFound());
                }

                public void failure(Throwable e) {
                    searchTable.setModel(getSearchTableModel(null));
                    Window.alert("Error: Attachment call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    parentScreen.setError(Messages.get().gen_queryFailed());
                }
            };
        }

        AttachmentService.get().query(query, queryCall);
    }

    /**
     * Opens the attachment screen and adds the attachments selected on it, if
     * any, to the sample when that screen is closed
     */
    @UiHandler("attachButton")
    protected void attach(ClickEvent event) {
        ModalWindow modal;

        if (attachmentScreen == null) {
            try {
                attachmentScreen = new AttachmentScreenUI() {
                    @Override
                    public boolean isAttach() {
                        return true;
                    }

                    @Override
                    public void attach(ArrayList<AttachmentDO> attachments) {
                        String fullMsg, msg;
                        AttachmentItemViewDO atti;

                        if (attachments == null)
                            return;

                        /*
                         * add the selected attachments to the record; if the
                         * user doesn't have permission to attach any attachment
                         * show an error for it
                         */
                        fullMsg = "";
                        for (AttachmentDO att : attachments) {
                            if (canAttach(att)) {
                                atti = createAttachmentItem(att);
                                currentTable.addRow(createAttachmentRow(atti));
                            } else {
                                msg = Messages.get()
                                              .attachment_attachPermException(att.getDescription());
                                if (fullMsg.length() == 0)
                                    fullMsg = msg;
                                else
                                    fullMsg = DataBaseUtil.concatWithSeparator(fullMsg, "\n", msg);
                            }
                        }
                        if (fullMsg.length() > 0)
                            Window.alert(fullMsg);
                    }
                };
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return;
            }
        }

        modal = new ModalWindow();
        modal.setName(Messages.get().attachment_attachment());
        modal.setSize("782px", "521px");
        modal.setContent(attachmentScreen);

        attachmentScreen.setWindow(modal);
    }

    public void setFocus() {
        currentTable.setFocus(true);
        if (currentTable.getRowCount() > 0) {
            currentTable.selectRowAt(0);
            detachButton.setEnabled(isState(ADD, UPDATE));
            displayButton.setEnabled(true);
        }
    }

    private void displayAttachments() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            fireDataChange();
        }
    }

    private ArrayList<Row> getCurrentTableModel() {
        AttachmentItemViewDO data;
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();

        for (int i = 0; i < count(); i++ ) {
            data = get(i);
            row = createAttachmentRow(data);
            model.add(row);
        }

        return model;
    }

    private ArrayList<Row> getSearchTableModel(ArrayList<AttachmentDO> attachments) {
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (attachments == null)
            return model;

        for (AttachmentDO data : attachments) {
            row = new Row(3);
            row.setCell(0, data.getCreatedDate());
            row.setCell(1, data.getSectionId());
            row.setCell(2, data.getDescription());
            row.setData(data);
            model.add(row);
        }

        return model;
    }

    /**
     * Returns true if the user has permission to attach the passed attachment;
     * returns false otherwise
     */
    private boolean canAttach(AttachmentDO att) {
        return hasPermission(att.getSectionId(), SectionFlags.ASSIGN);
    }

    /**
     * Returns true if the user has the passed permission for the section with
     * the passed id; returns false otherwise
     */
    private boolean hasPermission(Integer sectionId, SectionFlags perm) {
        SectionViewDO sect;

        try {
            sect = SectionCache.getById(sectionId);
            return UserCache.getPermission().has(sect.getName(), perm);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    private Row createAttachmentRow(AttachmentItemViewDO data) {
        Row row;

        row = new Row(3);
        row.setCell(0, data.getAttachmentCreatedDate());
        row.setCell(1, data.getAttachmentSectionId());
        row.setCell(2, data.getAttachmentDescription());
        row.setData(data);
        return row;
    }

    /**
     * Opens the file linked to the attachment on the selected row in the table
     * showing the sample's attachment items. If isSameWindow is true then the
     * file is opened in the same browser window/tab as before, otherwise it's
     * opened in a different one.
     */
    private void displayAttachment(int index, boolean isSameWindow) {
        AttachmentItemViewDO data;

        /*
         * display the file linked to the attachment for the selected row
         */
        data = currentTable.getRowAt(index).getData();
        parentBus.fireEvent(new DisplayAttachmentEvent(data.getAttachmentId(), isSameWindow));
    }
}