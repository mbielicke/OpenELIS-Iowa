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
package org.openelis.web.modules.sampleStatusReport.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.SampleQaEventManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class SampleStatusQALookupScreen extends Screen {

    protected TableWidget qaLookupTable;
    protected Integer     id;
    protected Type        type;
    protected Integer     qaInternalId;
    protected AppButton   okButton;

    public enum Type {
        SAMPLE, ANALYSIS
    };

    public SampleStatusQALookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleStatusQALookupDef.class));
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    private void postConstructor() {

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    private void initialize() {

        qaLookupTable = (TableWidget)def.getWidget("sampleStatusQALookUpTable");
        addScreenHandler(qaLookupTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                qaLookupTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qaLookupTable.enable(true);
            }
        });

        qaLookupTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                window.close();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });
    }

    private void initializeDropdowns() {
        try {
            qaInternalId = DictionaryCache.getIdBySystemName("qaevent_internal");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    public void refresh(Integer id, Type type) {
        this.id = id;
        this.type = type;
        DataChangeEvent.fire(this);
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        SampleQaEventViewDO sampleData;
        AnalysisQaEventViewDO analysisData;
        TableDataRow row;
        SampleQaEventManager sqm;
        AnalysisQaEventManager aqm;

        model = new ArrayList<TableDataRow>();

        if (Type.SAMPLE.equals(type)) {
            try {
                sqm = SampleQaEventManager.fetchBySampleId(id);
                for (int i = 0; i < sqm.count(); i++ ) {
                    sampleData = sqm.getSampleQAAt(i);
                    if (qaInternalId != sampleData.getTypeId()) {
                        row = new TableDataRow(1);
                        row.cells.get(0).setValue(sampleData.getQaEventReportingText());
                        model.add(row);
                    }
                }
            } catch (NotFoundException e) {
                window.setError(consts.get("noResultsFound"));
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        } else if (Type.ANALYSIS.equals(type)) {
            try {
                aqm = AnalysisQaEventManager.fetchByAnalysisId(id);
                for (int i = 0; i < aqm.count(); i++ ) {
                    analysisData = aqm.getAnalysisQAAt(i);
                    if (qaInternalId != analysisData.getTypeId()) {
                        row = new TableDataRow(1);
                        row.cells.get(0).setValue(analysisData.getQaEventReportingText());
                        model.add(row);
                    }
                }
            } catch (NotFoundException e) {
                window.setError(consts.get("noResultsFound"));
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
        return model;
    }
}
