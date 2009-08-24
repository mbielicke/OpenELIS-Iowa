/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.environmentalSampleLogin.client;

import java.util.EnumSet;

import org.openelis.domain.AnalysisTestDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenDef;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.screen.rewrite.Screen.State;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.rewrite.AutoComplete;
import org.openelis.gwt.widget.rewrite.CalendarLookUp;
import org.openelis.gwt.widget.rewrite.CheckBox;
import org.openelis.gwt.widget.rewrite.Dropdown;
import org.openelis.metamap.SampleMetaMap;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class AnalysisTab extends Screen {
 private boolean dropdownsInited, loaded;
    
    private SampleMetaMap meta;
    protected AnalysisTestDO analysis;
//    protected Dropdown<Integer> typeOfSampleId, containerId, unitOfMeasureId;

    public AnalysisTab(ScreenDef def) {
        setDef(def);
        
        meta = new SampleMetaMap("sample.");
        
        initialize();
    }
    
    private void initialize() {
        final AutoComplete<Integer> test = (AutoComplete)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.TEST.getName());
        addScreenHandler(test, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                test.setSelection(analysis.test.getId(), analysis.test.getName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.test.setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                test.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                test.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final AutoComplete<Integer> method = (AutoComplete)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.TEST.METHOD.getName());
        addScreenHandler(method, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                method.setSelection(analysis.test.getMethodId(), analysis.test.getMethodName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.test.setMethodId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                method.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                method.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final Dropdown<Integer> statusId = (Dropdown)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(analysis.getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final TextBox revision = (TextBox)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getRevision());
        addScreenHandler(revision, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                revision.setValue(Integer.toString(analysis.getRevision()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setRevision(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                revision.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                revision.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final CheckBox isReportable = (CheckBox)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getIsReportable());
        addScreenHandler(isReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isReportable.setValue(analysis.getIsReportable());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                analysis.setIsReportable(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isReportable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                isReportable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final AutoComplete<Integer> sectionId = (AutoComplete)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getSectionId());
        addScreenHandler(sectionId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sectionId.setSelection(analysis.getSectionId(), analysis.getSection());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setSectionId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sectionId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                sectionId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final CalendarLookUp startedDate = (CalendarLookUp)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getStartedDate());
        addScreenHandler(startedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                startedDate.setValue(analysis.getStartedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setStartedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                startedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                startedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final CalendarLookUp completedDate = (CalendarLookUp)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getCompletedDate());
        addScreenHandler(completedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                completedDate.setValue(analysis.getCompletedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setCompletedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                completedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                completedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final CalendarLookUp releasedDate = (CalendarLookUp)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getReleasedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setValue(analysis.getReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setReleasedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                releasedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final CalendarLookUp printedDate = (CalendarLookUp)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getPrintedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                printedDate.setValue(analysis.getPrintedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setPrintedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                printedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                printedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });
    }
}
