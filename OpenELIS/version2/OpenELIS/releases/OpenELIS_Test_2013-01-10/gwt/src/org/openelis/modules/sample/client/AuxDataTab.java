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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.HasAuxDataInt;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.auxData.client.AuxDataUtil;
import org.openelis.utilcommon.ResultValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class AuxDataTab extends Screen {
    private boolean                 loaded;

    protected AuxGroupLookupScreen  auxGroupScreen;
    protected TableWidget           auxValsTable;
    protected AppButton             addAuxButton, removeAuxButton;
    protected TextBox               auxMethod, auxUnits, auxDesc;
    protected AutoComplete<Integer> ac, auxField;
    protected boolean              queryFieldEntered;
    protected HasAuxDataInt         parentMan;
    protected AuxDataManager        manager;

    public AuxDataTab(ScreenDefInt def, ScreenWindowInt window) {
        service = new ScreenService("controller?service=org.openelis.modules.auxiliary.server.AuxiliaryService");
        setDefinition(def);
        setWindow(window);

        initialize();
    }

    private void initialize() {
        final AuxDataTab tab = this;

        auxValsTable = (TableWidget)def.getWidget("auxValsTable");
        addScreenHandler(auxValsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                auxValsTable.load(getTableModel());
                queryFieldEntered = false;
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxValsTable.enable(EnumSet.of(State.QUERY, State.DISPLAY)
                                           .contains(event.getState()) ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
            }
        });

        auxValsTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                AuxFieldViewDO fieldDO;

                fieldDO = manager.getAuxFieldAt(auxValsTable.getSelectedRow());
                if (fieldDO != null) {
                    auxMethod.setValue(fieldDO.getMethodName());
                    auxDesc.setValue(fieldDO.getDescription());
                    auxUnits.setValue(fieldDO.getUnitOfMeasureName());
                } else {
                    auxMethod.setValue(null);
                    auxDesc.setValue(null);
                    auxUnits.setValue(null);
                }

                if (state == State.QUERY ||
                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(state)))
                    removeAuxButton.enable(true);
            };
        });

        auxValsTable.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent<TableDataRow> event) {
                auxMethod.setValue(null);
                auxDesc.setValue(null);
                auxUnits.setValue(null);
            }
        });

        auxValsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Object val;

                c = event.getCol();
                r = event.getRow();

                window.clearStatus();

                if (state == State.QUERY) {
                    val = auxValsTable.getObject(r, c);

                    if (c == 0) {
                        event.cancel();
                    } else if (c == 2 && queryFieldEntered && DataBaseUtil.isEmpty(val)) {
                        event.cancel();
                        window.setError(consts.get("auxDataOneQueryException"));
                    }
                } else if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    if ( !canEdit())
                        event.cancel();
                } else {
                    event.cancel();
                }
            }
        });

        auxValsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, i;
                Integer avId;
                TableDataRow row;
                String value;
                AuxDataViewDO data;
                ResultValidator rv;
                ArrayList<AuxFieldValueViewDO> values;
                AuxFieldValueViewDO valueDO;

                r = event.getRow();
                c = event.getCol();
                value = (String)auxValsTable.getObject(r, c);
                data = null;

                if (state == State.QUERY) {
                    if (DataBaseUtil.isEmpty(value))
                        queryFieldEntered = false;
                    else
                        queryFieldEntered = true;
                    return;
                }

                try {
                    data = manager.getAuxDataAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setIsReportable(value);
                        break;
                    case 2:
                        auxValsTable.clearCellExceptions(r, c);
                        if ( !DataBaseUtil.isEmpty(value)) {
                            values = manager.getAuxValuesAt(r);
                            row = auxValsTable.getRow(r);
                            rv = (ResultValidator)row.data;
                            try {
                                avId = rv.validate((Integer)null, value);
                                data.setValue(rv.getValue((Integer)null, avId, value));
                                for (i = 0; i < values.size(); i++ ) {
                                    valueDO = values.get(i);
                                    if (valueDO.getId().equals(avId)) {
                                        data.setTypeId(valueDO.getTypeId());
                                        if (Constants.dictionary().AUX_DICTIONARY.equals(valueDO.getTypeId()))
                                            data.setDictionary(valueDO.getDictionary());
                                        break;
                                    }
                                }
                            } catch (ParseException e) {
                                auxValsTable.setCellException(r, c, e);
                                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                                data.setValue(null);
                                data.setDictionary(null);
                            }
                        } else {
                            data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                            data.setValue(null);
                            data.setDictionary(null);
                        }
                        break;
                }
            }
        });

        addAuxButton = (AppButton)def.getWidget("addAuxButton");
        addScreenHandler(addAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (auxGroupScreen == null) {
                    try {
                        auxGroupScreen = new AuxGroupLookupScreen();
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                    auxGroupScreen.addActionHandler(new ActionHandler<AuxGroupLookupScreen.Action>() {
                        public void onAction(ActionEvent<AuxGroupLookupScreen.Action> event) {
                            groupsSelectedFromLookup((ArrayList<AuxFieldManager>)event.getData());
                        }
                    });
                }

                ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
                modal.setName(consts.get("auxGroupSelection"));
                modal.setContent(auxGroupScreen);
                auxGroupScreen.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAuxButton.enable(event.getState() == State.QUERY ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
            }
        });

        removeAuxButton = (AppButton)def.getWidget("removeAuxButton");
        addScreenHandler(removeAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = auxValsTable.getSelectedRow();
                if (r == -1)
                    return;
                if (Window.confirm(consts.get("removeAuxMessage"))) {
                    auxValsTable.unselect(r);
                    manager.removeAuxDataGroupAt(r);
                }
                removeAuxButton.enable(false);
                DataChangeEvent.fire(tab);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAuxButton.enable(false);
            }
        });

        auxMethod = (TextBox)def.getWidget("auxMethod");
        addScreenHandler(auxMethod, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                auxMethod.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxMethod.enable(false);
            }
        });

        auxUnits = (TextBox)def.getWidget("auxUnits");
        addScreenHandler(auxUnits, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                auxUnits.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxUnits.enable(false);
            }
        });

        auxDesc = (TextBox)def.getWidget("auxDesc");
        addScreenHandler(auxDesc, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                auxDesc.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDesc.enable(false);
            }
        });
    }

    private boolean canEdit() {
        if (parentMan == null)
            return false;
        else if (parentMan instanceof SampleManager)
            return !Constants.dictionary().SAMPLE_RELEASED.equals( ((SampleManager)parentMan).getSample()
                                                                                             .getStatusId());
        return true;
    }

    private ArrayList<TableDataRow> getTableModel() {
        int i;
        TableDataRow row;
        AuxDataViewDO data;
        AuxFieldViewDO field;
        ArrayList<AuxFieldValueViewDO> values;
        ArrayList<TableDataRow> model;
        ResultValidator validatorItem;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.count(); i++ ) {
                data = manager.getAuxDataAt(i);
                field = manager.getAuxFieldAt(i);
                values = manager.getAuxValuesAt(i);

                row = new TableDataRow(3);
                row.cells.get(0).setValue(data.getIsReportable());
                row.cells.get(1).setValue(field.getAnalyteName());
                if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId()))
                    row.cells.get(2).setValue(data.getDictionary());
                else
                    row.cells.get(2).setValue(data.getValue());

                validatorItem = AuxDataUtil.getValidatorForValues(values);
                row.data = validatorItem;

                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void groupsSelectedFromLookup(ArrayList<AuxFieldManager> fields) {
        ValidationErrorsList errors;
        try {
            // auxValsTable.fireEvents(false);
            errors = AuxDataUtil.addAuxGroupsFromAuxFields(fields, manager);
            DataChangeEvent.fire(this);
            // auxValsTable.fireEvents(true);
            if (errors != null && errors.size() > 0)
                showErrors(errors);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fieldList;
        AuxFieldViewDO fieldDO;
        TableDataRow row;
        QueryData field;

        fieldList = new ArrayList<QueryData>();

        for (int i = 0; i < auxValsTable.numRows(); i++ ) {
            row = auxValsTable.getRow(i);
            fieldDO = manager.getAuxFieldAt(i);

            if (row.cells.get(2).value != null) {

                field = new QueryData();
                field.key = SampleMeta.getAuxDataAuxFieldId();
                field.type = QueryData.Type.INTEGER;
                field.query = String.valueOf(fieldDO.getId());
                fieldList.add(field);

                // aux data value
                field = new QueryData();
                field.key = SampleMeta.getAuxDataValue();
                field.type = QueryData.Type.STRING;
                field.query = String.valueOf(row.cells.get(2).value);
                fieldList.add(field);
            }
        }

        return fieldList;
    }

    public void setManager(HasAuxDataInt parentMan) {
        this.parentMan = parentMan;
        loaded = false;
    }

    public void draw() {
        if ( !loaded) {
            try {
                manager = parentMan.getAuxData();
                loaded = true;

                if (state != State.QUERY)
                    StateChangeEvent.fire(this, state);

                DataChangeEvent.fire(this);

            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}