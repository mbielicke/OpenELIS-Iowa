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

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.TextArea;
import org.openelis.gwt.widget.table.TableDataRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class SampleStatusQALookupScreen extends Screen {

    protected TextArea qaLookupTextArea;
    protected Integer     id;
    protected Type        type;
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
        DataChangeEvent.fire(this);
    }

    private void initialize() {

        qaLookupTextArea = (TextArea)def.getWidget("sampleStatusQALookupTextArea");
        addScreenHandler(qaLookupTextArea, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                loadQaText();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qaLookupTextArea.enable(false);
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

    public void refresh(Integer id, Type type) {
        this.id = id;
        this.type = type;
        DataChangeEvent.fire(this);
    }

    private void loadQaText() {
        StringBuffer text;
        ArrayList<SampleQaEventViewDO> sampleData;
        ArrayList<AnalysisQaEventViewDO> analysisData;

        text = new StringBuffer();
        if (Type.SAMPLE.equals(type)) {
            try {
                sampleData = SampleStatusReportService.get().getSampleQaEventsBySampleId(id);
                for (SampleQaEventViewDO sqeVDO : sampleData) {
                    if (text.length() > 0)
                        text.append("\n");
                    text.append(sqeVDO.getQaEventReportingText());
                }
            } catch (NotFoundException e) {
                window.setError(Messages.get().noRecordsFound());
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        } else if (Type.ANALYSIS.equals(type)) {
            try {
                analysisData = SampleStatusReportService.get().getAnalysisQaEventsByAnalysisId(id);
                for (AnalysisQaEventViewDO aqeVDO : analysisData) {
                    if (text.length() > 0)
                        text.append("\n");
                    text.append(aqeVDO.getQaEventReportingText());
                }
            } catch (NotFoundException e) {
                window.setError(Messages.get().noRecordsFound());
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }

        qaLookupTextArea.setText(text.toString());
    }
}