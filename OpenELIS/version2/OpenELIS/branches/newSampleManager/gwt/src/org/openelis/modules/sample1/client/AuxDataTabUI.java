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

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;

import org.openelis.constants.Messages;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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

public class AuxDataTabUI extends Screen {

    @UiTemplate("AuxDataTab.ui.xml")
    interface AuxDataTabUIBinder extends UiBinder<Widget, AuxDataTabUI> {
    };

    private static AuxDataTabUIBinder uiBinder = GWT.create(AuxDataTabUIBinder.class);

    // private boolean loaded;

    protected AuxGroupLookupScreen1   auxGroupScreen;

    protected AuxDataTabUI            screen;

    @UiField
    protected Table                   auxValsTable;

    @UiField
    protected Button                  removeAuxButton, addAuxButton;

    @UiField
    protected TextBox<String>         auxMethod, auxUnits, auxDesc;

    // protected boolean queryFieldEntered;
    // protected HasAuxDataInt parentMan;
    protected Screen                  parentScreen;

    protected boolean                 canEdit, canQuery, isVisible;

    protected SampleManager1          manager, displayedManager;

    public AuxDataTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedManager = null;
    }

    private void initialize() {
        screen = this;

        addScreenHandler(auxValsTable,
                         "auxValsTable",
                         new ScreenHandler<ArrayList<Item<Integer>>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 auxValsTable.setModel(getTableModel());
                                 // queryFieldEntered = false;
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 auxValsTable.setEnabled(isState(DISPLAY) || (canEdit));
                             }
                         });

        auxValsTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                // TODO change this code
                /*
                 * AuxFieldViewDO fieldDO;
                 * 
                 * fieldDO =
                 * manager.getAuxFieldAt(auxValsTable.getSelectedRow()); if
                 * (fieldDO != null) {
                 * auxMethod.setValue(fieldDO.getMethodName());
                 * auxDesc.setValue(fieldDO.getDescription());
                 * auxUnits.setValue(fieldDO.getUnitOfMeasureName()); } else {
                 * auxMethod.setValue(null); auxDesc.setValue(null);
                 * auxUnits.setValue(null); }
                 */

                removeAuxButton.setEnabled(canEdit);
            };
        });

        auxValsTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                setAuxMethod(null);
                setAuxDescription(null);
                setAuxUnits(null);
            }
        });

        auxValsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Object val;

                c = event.getCol();
                r = event.getRow();

                window.clearStatus();

                if (canQuery) {
                    val = auxValsTable.getValueAt(r, c);

                    if (c == 0) {
                        event.cancel();
                    }/*
                      * else if (c == 2 && queryFieldEntered &&
                      * DataBaseUtil.isEmpty(val)) { event.cancel();
                      * window.setError
                      * (Messages.get().auxDataOneQueryException()); }
                      */
                } else if ( !canEdit)
                    event.cancel();
            }
        });

        auxValsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, i;
                Integer avId;
                Item<Integer> row;
                String value;
                AuxDataViewDO data;
                // ResultValidator rv;
                // ArrayList<AuxFieldValueViewDO> values;
                // AuxFieldValueViewDO valueDO;

                r = event.getRow();
                c = event.getCol();
                value = (String)auxValsTable.getValueAt(r, c);
                data = null;

                if (isState(QUERY)) {
                    /*
                     * if (DataBaseUtil.isEmpty(value)) queryFieldEntered =
                     * false; else queryFieldEntered = true;
                     */
                    return;
                }

                try {
                    data = manager.auxData.get(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setIsReportable(value);
                        break;
                    case 2:
                        auxValsTable.clearExceptions(r, c);
                        if ( !DataBaseUtil.isEmpty(value)) {
                            // TODO change this code
                            /*
                             * values = manager.getAuxValuesAt(r); row =
                             * auxValsTable.getRowAt(r); rv =
                             * (ResultValidator)row.data; try { avId =
                             * rv.validate((Integer)null, value);
                             * data.setValue(rv.getValue((Integer)null, avId,
                             * value)); for (i = 0; i < values.size(); i++ ) {
                             * valueDO = values.get(i); if
                             * (valueDO.getId().equals(avId)) {
                             * data.setTypeId(valueDO.getTypeId()); if
                             * (Constants
                             * .dictionary().AUX_DICTIONARY.equals(valueDO
                             * .getTypeId()))
                             * data.setDictionary(valueDO.getDictionary());
                             * break; } } } catch (ParseException e) {
                             * auxValsTable.setCellException(r, c, e);
                             * data.setTypeId
                             * (Constants.dictionary().AUX_ALPHA_MIXED);
                             * data.setValue(null); data.setDictionary(null); }
                             */
                        } else {
                            data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                            data.setValue(null);
                            data.setDictionary(null);
                        }
                        break;
                }
            }
        });

        addScreenHandler(addAuxButton, "addAuxButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addAuxButton.setEnabled(canEdit);
            }
        });

        addScreenHandler(removeAuxButton, "removeAuxButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeAuxButton.setEnabled(false);
            }
        });

        addScreenHandler(auxMethod, "auxMethod", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                setAuxMethod(null);
            }

            public void onStateChange(StateChangeEvent event) {
                auxMethod.setEnabled(false);
            }
        });

        addScreenHandler(auxUnits, "auxUnits", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                setAuxUnits(null);
            }

            public void onStateChange(StateChangeEvent event) {
                auxUnits.setEnabled(false);
            }
        });

        addScreenHandler(auxDesc, "auxDesc", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                setAuxDescription(null);
            }

            public void onStateChange(StateChangeEvent event) {
                auxDesc.setEnabled(false);
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayAuxData();
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       setState(event.getState());
                                       evaluateEdit();
                                   }
                               });

        bus.addHandlerToSource(DataChangeEvent.getType(),
                               parentScreen,
                               new DataChangeEvent.Handler() {
                                   public void onDataChange(DataChangeEvent event) {
                                       displayAuxData();
                                   }
                               });
    }

    public void setData(SampleManager1 manager) {
        if ( !DataBaseUtil.isSame(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
            evaluateEdit();
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    private void evaluateEdit() {
        canEdit = canQuery = false;
        if (isState(QUERY)) {
            canEdit = canQuery = true;
        } else if (manager != null) {
            if (isState(ADD, UPDATE))
                canEdit = !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                .getStatusId());
        }
    }

    private void displayAuxData() {
        int count1, count2;
        boolean dataChanged;
        AuxDataViewDO aux1, aux2;

        if ( !isVisible)
            return;

        count1 = displayedManager == null ? 0 : displayedManager.auxData.count();
        count2 = manager == null ? 0 : manager.auxData.count();

        /*
         * find out if there's any difference between the aux data of the two
         * managers
         */
        if (count1 == count2) {
            dataChanged = false;
            for (int i = 0; i < count1; i++ ) {
                aux1 = displayedManager.auxData.get(i);
                aux2 = manager.auxData.get(i);

                if (DataBaseUtil.isDifferent(aux1.getTypeId(), aux2.getTypeId()) ||
                    DataBaseUtil.isDifferent(aux1.getValue(), aux2.getValue()) ||
                    DataBaseUtil.isDifferent(aux1.getAnalyteId(), aux2.getAnalyteId()) ||
                    DataBaseUtil.isDifferent(aux1.getGroupId(), aux2.getGroupId())) {
                    dataChanged = true;
                    break;
                }
            }
        } else {
            dataChanged = true;
        }

        if (dataChanged) {
            displayedManager = manager;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Item<Integer>> getTableModel() {
        int i;
        Item<Integer> row;
        AuxDataViewDO data;
        // AuxFieldViewDO field;
        // ArrayList<AuxFieldValueViewDO> values;
        ArrayList<Item<Integer>> model;
        // ResultValidator validatorItem;

        if (manager == null) {
            return null;
        }

        model = new ArrayList<Item<Integer>>();
        for (i = 0; i < manager.auxData.count(); i++ ) {
            // field = manager.auxData.getAuxFieldAt(i);
            // values = manager.auxData.getAuxValuesAt(i);

            row = new Item<Integer>(3);
            data = manager.auxData.get(i);
            row.setCell(0, data.getIsReportable());
            row.setCell(1, data.getAnalyteName());
            if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId()))
                row.setCell(2, data.getDictionary());
            else
                row.setCell(2, data.getValue());

            // TODO change this code
            // validatorItem = AuxDataUtil.getValidatorForValues(values);
            // row.data = validatorItem;
            model.add(row);
        }

        return model;
    }

    // TODO change this code
    /*
     * private void groupsSelectedFromLookup(ArrayList<AuxFieldManager> fields)
     * { ValidationErrorsList errors; try { // auxValsTable.fireEvents(false);
     * errors = AuxDataUtil.addAuxGroupsFromAuxFields(fields, manager);
     * DataChangeEvent.fire(this); // auxValsTable.fireEvents(true); if (errors
     * != null && errors.size() > 0) showErrors(errors); } catch (Exception e) {
     * Window.alert(e.getMessage()); } }
     */

    @UiHandler("removeAuxButton")
    protected void removeAuxButton(ClickEvent event) {
        int r;

        r = auxValsTable.getSelectedRow();
        if (r == -1)
            return;
        if (Window.confirm(Messages.get().removeAuxMessage())) {
            // auxValsTable.unselect(r);
            // manager.removeAuxDataGroupAt(r);
        }
        removeAuxButton.setEnabled(false);
        fireDataChange();
    }

    @UiHandler("addAuxButton")
    public void addAuxButton(ClickEvent event) {
        if (auxGroupScreen == null) {
            try {
                auxGroupScreen = new AuxGroupLookupScreen1();
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
            auxGroupScreen.addActionHandler(new ActionHandler<AuxGroupLookupScreen1.Action>() {
                public void onAction(ActionEvent<AuxGroupLookupScreen1.Action> event) {
                    // TODO change this code
                    // groupsSelectedFromLookup((ArrayList<AuxFieldManager>)event.getData());
                }
            });
        }

        ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(Messages.get().auxGroupSelection());
        modal.setContent(auxGroupScreen);
        auxGroupScreen.draw();

    }

    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fieldList;
        Item<Integer> row;
        QueryData field;

        fieldList = new ArrayList<QueryData>();

        for (int i = 0; i < auxValsTable.getRowCount(); i++ ) {
            row = auxValsTable.getRowAt(i);
            // fieldDO = manager.getAuxFieldAt(i);

            if (row.getCell(2) != null) {

                field = new QueryData();
                field.setKey(SampleMeta.getAuxDataAuxFieldId());
                field.setType(QueryData.Type.INTEGER);
                // TODO change this code
                // field.setQuery(String.valueOf(fieldDO.getId()));
                fieldList.add(field);

                // aux data value
                field = new QueryData();
                field.setKey(SampleMeta.getAuxDataValue());
                field.setType(QueryData.Type.STRING);
                field.setQuery(String.valueOf(row.getCell(2)));
                fieldList.add(field);
            }
        }

        return fieldList;
    }

    private void setAuxMethod(String method) {
        auxMethod.setValue(method);
    }

    private void setAuxDescription(String desc) {
        auxDesc.setValue(desc);
    }

    private void setAuxUnits(String units) {
        auxMethod.setValue(units);
    }

    /*
     * public void setManager(HasAuxDataInt parentMan) { this.parentMan =
     * parentMan; // loaded = false; }
     * 
     * public void draw() { /* if ( !loaded) { try { manager =
     * parentMan.getAuxData(); loaded = true;
     * 
     * if (state != State.QUERY) StateChangeEvent.fire(this, state);
     * 
     * DataChangeEvent.fire(this);
     * 
     * } catch (Exception e) { Window.alert(e.getMessage()); } }
     * 
     * }
     */
}