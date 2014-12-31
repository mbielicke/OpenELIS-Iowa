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
package org.openelis.modules.eorder.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.constants.Messages;
import org.openelis.domain.EOrderDO;
import org.openelis.meta.EOrderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

public abstract class EOrderLookupUI extends Screen {

    @UiTemplate("EOrderLookup.ui.xml")
    interface EOrderLookupUiBinder extends UiBinder<Widget, EOrderLookupUI> {
    };

    private static EOrderLookupUiBinder uiBinder = GWT.create(EOrderLookupUiBinder.class);

    @UiField
    protected Button                    search, select, cancel;
    @UiField
    protected Table                     eorderTable;
    @UiField
    protected TextBox<String>           paperOrderValidator;

    protected EOrderDO                  selectedEOrder;

    public EOrderLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));

        initialize();

        setState(DEFAULT);
        paperOrderValidator.setFocus(true);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        //
        // screen fields and buttons
        //
        addScreenHandler(paperOrderValidator,
                         EOrderMeta.getPaperOrderValidator(),
                         new ScreenHandler<String>() {
                             public void onStateChange(StateChangeEvent event) {
                                 paperOrderValidator.setEnabled(isState(ADD, UPDATE));
                                 paperOrderValidator.setQueryMode(isState(ADD, UPDATE));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? search : cancel;
                             }
                         });

        addScreenHandler(search, "search", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                search.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? eorderTable : paperOrderValidator;
            }
        });

        //
        // eorder search results table
        //
        addScreenHandler(eorderTable, "eorderTable", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                eorderTable.setEnabled(true);
                eorderTable.setAllowMultipleSelection(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? select : search;
            }
        });

        eorderTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        addScreenHandler(select, "select", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                select.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? cancel : eorderTable;
            }
        });

        addScreenHandler(cancel, "cancel", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancel.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? paperOrderValidator : select;
            }
        });
    }

    @UiHandler("search")
    protected void search(ClickEvent event) {
        executeQuery(paperOrderValidator.getText());
    }

    private void executeQuery(String pov) {
        QueryFieldUtil util;

        if (DataBaseUtil.isEmpty(pov)) {
            setQueryResult(null);
            return;
        }
        /*
         * convert the string entered by the user to the database's format 
         */
        util = new QueryFieldUtil();
        try {
            util.parse(pov);
            pov = util.getParameter().get(0);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        
        setBusy(Messages.get().gen_querying());
        EOrderService.get().fetchByPaperOrderValidator(pov, new AsyncCallback<ArrayList<EOrderDO>>() {
            public void onSuccess(ArrayList<EOrderDO> list) {
                setQueryResult(list);
            }

            public void onFailure(Throwable error) {
                setQueryResult(null);
                if (error instanceof NotFoundException) {
                    setDone(Messages.get().gen_noRecordsFound());
                } else {
                    Window.alert("Error: EOrderLookup call query failed; "+error.getMessage());
                    setError(Messages.get().gen_queryFailed());
                }
            }
        });
    }

    private void setQueryResult(ArrayList<EOrderDO> list) {
        ArrayList<Item<Integer>> model;
        Item<Integer> row;

        model = new ArrayList<Item<Integer>>();
        if (list == null || list.size() == 0) {
            setDone(Messages.get().gen_noRecordsFound());
        } else {
            for (EOrderDO eorderRow : list) {
                row = new Item<Integer>(4);
                row.setKey(eorderRow.getId());
                row.setCell(0, eorderRow.getId());
                row.setCell(1, eorderRow.getEnteredDate());
                row.setCell(2, eorderRow.getPaperOrderValidator());
                row.setCell(3, eorderRow.getDescription());
                row.setData(eorderRow);
                model.add(row);
            }

            setDone(Messages.get().gen_queryingComplete());
        }

        eorderTable.setModel(model);
        if (model.size() > 0)
            eorderTable.selectRowAt(0);
        eorderTable.setFocus(true);
    }

    public EOrderDO getSelectedEOrder() {
        return selectedEOrder;
    }

    /**
     * overridden to respond to the user clicking "select"
     */
    public abstract void select();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    @UiHandler("select")
    protected void select(ClickEvent event) {
        Item<Integer> selectedRow;

        selectedEOrder = null;
        if (eorderTable.getSelectedRow() != -1) {
            selectedRow = eorderTable.getRowAt(eorderTable.getSelectedRow());
            selectedEOrder = selectedRow.getData();
        }

        window.close();
        select();
    }

    @UiHandler("cancel")
    protected void cancel(ClickEvent event) {
        selectedEOrder = null;
        window.close();
        cancel();
    }

    public void setPaperOrderValidator(String pov) {
        paperOrderValidator.setText(pov);
        executeQuery(pov);
    }

    public void setWindow(WindowInt window) {
        super.setWindow(window);
        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                eorderTable.setModel(null);
            }
        });
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }
}