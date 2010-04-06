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
import java.util.Date;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.DateField;
import org.openelis.gwt.widget.ScreenWindow;
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
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.manager.HasAuxDataInt;
import org.openelis.utilcommon.ResultValidator;
import org.openelis.utilcommon.ResultValidator.Type;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class AuxDataTab extends Screen implements GetMatchesHandler {
    private boolean                 loaded;

    protected AuxGroupLookupScreen  auxGroupScreen;
    protected TableWidget           auxValsTable;
    protected AppButton             addAuxButton, removeAuxButton;
    protected TextBox               auxMethod, auxUnits, auxDesc;
    protected AutoComplete<Integer> ac, auxField;
    protected Integer               alphaLowerId, alphaUpperId, alphaMixedId, timeId, numericId,
                    dateId, dateTimeId, dictionaryId;
    protected boolean               queryFieldEntered;
    protected HasAuxDataInt         parentMan;
    protected AuxDataManager        manager;

    public AuxDataTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService(
                                    "OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService");
        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        final AuxDataTab tab = this;

        auxValsTable = (TableWidget)def.getWidget("auxValsTable");
        ((AuxTableColumn)auxValsTable.getColumns().get(2)).setScreen(this);
        addScreenHandler(auxValsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                auxValsTable.load(getTableModel());
                queryFieldEntered = false;
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxValsTable.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
                // auxValsTable.setQueryMode(event.getState() == State.QUERY);
                // ((HasField)auxValsTable.getColumns().get(1).colWidget).setQueryMode(false);
                // auxValsTable.fireEvents(true);
            }
        });

        auxValsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Object val;
                c = event.getCol();
                r = event.getRow();

                window.clearStatus();
                
                if (state == State.QUERY){
                    val = auxValsTable.getObject(r, c);
                    
                    if(c == 0)
                        event.cancel();
                    else if(c == 2 && queryFieldEntered && (val == null || "".equals(val))){
                        event.cancel();
                        window.setError(consts.get("auxDataOneQueryException"));
                    }
                }
            }
        });
        auxValsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                TableDataRow row;
                Object val;
                AuxDataViewDO data;
                AuxFieldViewDO fieldDO;
                AuxDataBundle adb;

                r = event.getRow();
                c = event.getCol();
                val = auxValsTable.getObject(r, c);
                data = null;

                if (state == State.QUERY){
                    if(val == null || "".equals(val))
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
                        data.setIsReportable((String)val);
                        break;
                    case 2:
                        row = auxValsTable.getRow(r);
                        adb = (AuxDataBundle)row.data;
                        ResultValidator rv = adb.validator;
                        fieldDO = (AuxFieldViewDO)adb.fieldDO;
                        data.setValue(getCorrectManValueByType(val, fieldDO.getTypeId()));

                        if (rv != null && getCorrectManValueByType(val, fieldDO.getTypeId()) != null) {
                            try {
                                auxValsTable.clearCellExceptions(r, c);
                                rv.validate(null,
                                            getCorrectManValueByType(val, fieldDO.getTypeId()));
                            } catch (ParseException e) {
                                // auxValsTable.clearCellExceptions(r, c);
                                auxValsTable.setCellException(r, c, e);
                            } catch (Exception e) {
                                Window.alert(e.getMessage());
                            }
                        }

                        break;
                }
            }
        });

        auxValsTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                // always allow selection
            }
        });

        auxValsTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                TableRow row;
                AuxDataBundle bundle;

                row = event.getSelectedItem();
                bundle = (AuxDataBundle)row.row.data;
                if (bundle != null) {
                    AuxFieldViewDO fieldDO = bundle.fieldDO;

                    auxMethod.setValue(fieldDO.getMethodName());
                    auxDesc.setValue(fieldDO.getDescription());
                    auxUnits.setValue(fieldDO.getUnitOfMeasureName());
                }

                if (EnumSet.of(State.ADD, State.UPDATE, State.QUERY).contains(state))
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
                addAuxButton.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
            }
        });

        removeAuxButton = (AppButton)def.getWidget("removeAuxButton");
        addScreenHandler(removeAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (auxValsTable.getSelectedRow() == -1)
                    return;

                if (Window.confirm(consts.get("removeAuxMessage")))
                    manager.removeAuxDataGroupAt(auxValsTable.getSelectedRow());

                removeAuxButton.enable(false);

                // reload the tab
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

    private ArrayList<TableDataRow> getTableModel() {
        int i;
        TableDataRow row;
        AuxDataViewDO data;
        AuxFieldViewDO field;
        ArrayList<AuxFieldValueViewDO> values;
        AuxFieldValueViewDO val;
        ArrayList<TableDataRow> model;
        AuxDataBundle adb;
        ResultValidator validatorItem;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.count(); i++ ) {
                data = manager.getAuxDataAt(i);
                field = manager.getFieldsAt(i).getAuxFieldAt(0);
                values = manager.getFieldsAt(i).getValuesAt(0).getValues();
                val = values.get(0);

                row = new TableDataRow(3);
                row.cells.get(0).value = data.getIsReportable();
                row.cells.get(1).value = field.getAnalyteName();
                row.cells.get(2).value = getCorrectColValueByType(data.getValue(),
                                                                  data.getDictionary(),
                                                                  val.getTypeId());

                field.setTypeId(val.getTypeId());

                validatorItem = getDataItemForRow(field.getTypeId(), values);
                adb = new AuxDataBundle(validatorItem, field);
                row.data = adb;

                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void groupsSelectedFromLookup(ArrayList<AuxFieldManager> fields) {
        AuxFieldManager man;
        AuxFieldViewDO fieldDO;
        ArrayList<AuxFieldValueViewDO> values;
        AuxFieldValueViewDO valueDO, defaultValue;
        AuxDataViewDO dataDO;
        TableDataRow row;
        AuxDataBundle adb;
        ResultValidator validatorItem;

        try {
            auxValsTable.fireEvents(false);
            for (int i = 0; i < fields.size(); i++ ) {
                man = fields.get(i);

                for (int j = 0; j < man.count(); j++ ) {
                    fieldDO = man.getAuxFieldAt(j);

                    if ("Y".equals(fieldDO.getIsActive())) {
                        values = man.getValuesAt(j).getValues();
                        defaultValue = man.getValuesAt(j).getDefaultValue();
                        valueDO = values.get(0);
                        dataDO = new AuxDataViewDO();

                        dataDO.setTypeId(valueDO.getTypeId());
                        dataDO.setAuxFieldId(fieldDO.getId());
                        dataDO.setIsReportable(fieldDO.getIsReportable());
                        // dataDO.setValue(valueDO.getValue());
                        dataDO.setDictionary(valueDO.getDictionary());
                        fieldDO.setTypeId(valueDO.getTypeId());

                        manager.addAuxDataFieldsAndValues(dataDO, fieldDO, values);

                        row = new TableDataRow(3);
                        row.cells.get(0).value = fieldDO.getIsReportable();
                        row.cells.get(1).value = fieldDO.getAnalyteName();

                        validatorItem = getDataItemForRow(valueDO.getTypeId(), values);
                        adb = new AuxDataBundle(validatorItem, fieldDO);
                        row.data = adb;

                        if (defaultValue != null && state != State.QUERY) {
                            try {
                                if (dictionaryId.equals(valueDO.getTypeId())) {
                                    String dictionary, value;
                                    AuxFieldValueViewDO a;

                                    dictionary = defaultValue.getValue();

                                    value = null;
                                    for (int k = 0; k < values.size(); k++ ) {
                                        a = values.get(k);
                                        if (a.getDictionary().equals(dictionary)) {
                                            value = a.getValue();
                                            break;
                                        }
                                    }

                                    if (value == null)
                                        throw new Exception();
                                    else
                                        row.cells.get(2).value = getCorrectColValueByType(
                                                                                          value,
                                                                                          dictionary,
                                                                                          valueDO.getTypeId());

                                } else
                                    row.cells.get(2).value = getCorrectColValueByType(
                                                                                      defaultValue.getValue(),
                                                                                      null,
                                                                                      valueDO.getTypeId());

                            } catch (Exception e) {
                                row.cells.get(2)
                                         .addException(
                                                       new LocalizedException(
                                                                              "illegalDefaultValueException"));
                            }
                        }
                        auxValsTable.addRow(row);
                    }
                }
            }
            auxValsTable.fireEvents(true);

        } catch (Exception e) {

            Window.alert(e.getMessage());
        }
    }

    private Object getCorrectColValueByType(String value, String dictionary, Integer typeId)
                                                                                            throws Exception {
        if (value == null)
            return null;

        if (alphaLowerId.equals(typeId) || alphaUpperId.equals(typeId) ||
            alphaMixedId.equals(typeId) || timeId.equals(typeId))
            return value;

        else if (numericId.equals(typeId))
            return new Double(value);

        else if (dateId.equals(typeId))
            return new Datetime(Datetime.YEAR, Datetime.DAY, new Date(value));

        else if (dateTimeId.equals(typeId))
            return new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date(value));

        else if (dictionaryId.equals(typeId)) {
            if (dictionary == null)
                throw new Exception();

            return new TableDataRow(new Integer(value), dictionary);
        }

        return null;
    }

    private ResultValidator getDataItemForRow(Integer typeId, ArrayList<AuxFieldValueViewDO> values) {
        ResultValidator rv = new ResultValidator();
        AuxFieldValueViewDO af;

        // we only need to validate numerics and times because the widget will
        // validate everything else
        if (numericId.equals(typeId) || timeId.equals(typeId)) {
            try {
                for (int i = 0; i < values.size(); i++ ) {
                    af = values.get(i);
                    if (numericId.equals(typeId))
                        rv.addResult(af.getId(), null, Type.NUMERIC, af.getValue());
                    else
                        rv.addResult(af.getId(), null, Type.TIME, af.getValue());
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                return null;
            }
        } else
            rv = null;

        return rv;
    }

    private String getCorrectManValueByType(Object value, Integer typeId) {
        DateField df;
        if (alphaLowerId.equals(typeId) || alphaUpperId.equals(typeId) ||
            alphaMixedId.equals(typeId) || timeId.equals(typeId))
            return (String)value;
        
        else if(numericId.equals(typeId))
            return ((Integer)value).toString();

        else if (dateId.equals(typeId)) {
            df = new DateField();
            df.setBegin(Datetime.YEAR);
            df.setEnd(Datetime.DAY);
            df.setFormat(consts.get("datePattern"));
            df.setValue( ((Datetime)value));
            return df.toString().replaceAll("-", "/");
        } else if (dateTimeId.equals(typeId)) {
            df = new DateField();
            df.setBegin(Datetime.YEAR);
            df.setEnd(Datetime.MINUTE);
            df.setFormat(consts.get("dateTimePattern"));
            df.setValue( ((Datetime)value));
            return  df.toString().replaceAll("-", "/");
        } else if (dictionaryId.equals(typeId))
            return ((TableDataRow)value).key.toString();

        return null;
    }

    public void onGetMatches(GetMatchesEvent event) {
        ArrayList<TableDataRow> model;
        int index;
        AuxFieldValueViewDO valDO;
        String match;
        boolean showWholeList;

        index = auxValsTable.getSelectedRow();
        model = new ArrayList<TableDataRow>();
        match = event.getMatch();

        showWholeList = "".equals(match.trim());

        // give them the dictionary entries
        try {
            AuxFieldValueManager valMan = manager.getFieldsAt(index).getValuesAt(0);

            for (int i = 0; i < valMan.count(); i++ ) {
                valDO = valMan.getAuxFieldValueAt(i);

                if (valDO.getDictionary() != null &&
                    (showWholeList || valDO.getDictionary().startsWith(match)))
                    model.add(new TableDataRow(new Integer(valDO.getValue()), valDO.getDictionary()));
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }

        ((AutoComplete<Integer>)event.getSource()).showAutoMatches(model);
    }

    public ArrayList<IdNameVO> getAuxQueryFields() {
        ArrayList<IdNameVO> returnList;
        TableDataRow row;
        AuxDataBundle adb;
        IdNameVO idName;
        returnList = new ArrayList<IdNameVO>();

        for (int i = 0; i < auxValsTable.numRows(); i++ ) {
            row = auxValsTable.getRow(i);
            adb = (AuxDataBundle)row.data;

            if (row.cells.get(2).value != null) {
                idName = new IdNameVO();
                idName.setId(adb.fieldDO.getId());
                idName.setName(getCorrectManValueByType(row.cells.get(2).value,
                                                        adb.fieldDO.getTypeId()));
                returnList.add(idName);
            }
        }

        return returnList;
    }

    private void initializeDropdowns() {
        try {
            alphaLowerId = DictionaryCache.getIdFromSystemName("aux_alpha_lower");
            alphaUpperId = DictionaryCache.getIdFromSystemName("aux_alpha_upper");
            alphaMixedId = DictionaryCache.getIdFromSystemName("aux_alpha_mixed");
            timeId = DictionaryCache.getIdFromSystemName("aux_time");
            numericId = DictionaryCache.getIdFromSystemName("aux_numeric");
            dateId = DictionaryCache.getIdFromSystemName("aux_date");
            dateTimeId = DictionaryCache.getIdFromSystemName("aux_date_time");
            dictionaryId = DictionaryCache.getIdFromSystemName("aux_dictionary");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
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
