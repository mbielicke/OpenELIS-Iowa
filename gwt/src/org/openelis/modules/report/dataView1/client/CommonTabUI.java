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
package org.openelis.modules.report.dataView1.client;

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Map;

import org.openelis.domain.DataView1VO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.CheckBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class CommonTabUI extends Screen {
    @UiTemplate("CommonTab.ui.xml")
    interface CommonTabUIBinder extends UiBinder<Widget, CommonTabUI> {
    };

    private static CommonTabUIBinder uiBinder = GWT.create(CommonTabUIBinder.class);

    @UiField
    protected CheckBox               accessionNumber, revision, collectionDate, receivedDate,
                    enteredDate, releasedDate, statusId, projectName, clientReferenceHeader,
                    sampleOrgId, sampleOrgOrganizationName, sampleOrgAttention,
                    addressMultipleUnit, addressStreetAddress, addressCity, addressState,
                    addressZipCode, itemTypeofSampleId, itemSourceOfSampleId, itemSourceOther,
                    itemContainerId, itemContainerReference, itemItemSequence, analysisId,
                    analysisTestNameHeader, analysisMethodNameHeader, analysisStatusIdHeader,
                    analysisRevision, analysisIsReportableHeader, analysisUnitOfMeasureId,
                    analysisSubQaName, analysisCompletedDate, analysisCompletedBy,
                    analysisReleasedDate, analysisReleasedBy, analysisStartedDate,
                    analysisPrintedDate, analysisSectionName, analysisTypeId;

    protected Screen                 parentScreen;

    protected EventBus               parentBus;

    protected DataView1VO            data;

    public CommonTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        data = null;
    }

    public void initialize() {
        addScreenHandler(accessionNumber, SampleWebMeta.getAccessionNumber(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(getValue(SampleWebMeta.getAccessionNumber()));
            }

            public void onStateChange(StateChangeEvent event) {
                accessionNumber.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? revision : analysisTypeId;
            }
        });

        addScreenHandler(revision, SampleWebMeta.getRevision(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                revision.setValue(getValue(SampleWebMeta.getRevision()));
            }

            public void onStateChange(StateChangeEvent event) {
                revision.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? collectionDate : accessionNumber;
            }
        });
        
        addScreenHandler(collectionDate, SampleWebMeta.getCollectionDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectionDate.setValue(getValue(SampleWebMeta.getCollectionDate()));
            }

            public void onStateChange(StateChangeEvent event) {
                collectionDate.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? receivedDate : revision;
            }
        });
        
        addScreenHandler(receivedDate, SampleWebMeta.getReceivedDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(getValue(SampleWebMeta.getReceivedDate()));
            }

            public void onStateChange(StateChangeEvent event) {
                receivedDate.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? enteredDate : collectionDate;
            }
        });

        addScreenHandler(enteredDate, SampleWebMeta.getEnteredDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                enteredDate.setValue(getValue(SampleWebMeta.getEnteredDate()));
            }

            public void onStateChange(StateChangeEvent event) {
                enteredDate.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? releasedDate : receivedDate;
            }
        });

        addScreenHandler(releasedDate, SampleWebMeta.getReleasedDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setValue(getValue(SampleWebMeta.getReleasedDate()));
            }

            public void onStateChange(StateChangeEvent event) {
                releasedDate.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? statusId : enteredDate;
            }
        });

        addScreenHandler(statusId, SampleWebMeta.getStatusId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(getValue(SampleWebMeta.getStatusId()));
            }

            public void onStateChange(StateChangeEvent event) {
                statusId.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? projectName : releasedDate;
            }
        });

        addScreenHandler(projectName, SampleWebMeta.getProjectName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                projectName.setValue(getValue(SampleWebMeta.getProjectName()));
            }

            public void onStateChange(StateChangeEvent event) {
                projectName.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? clientReferenceHeader : statusId;
            }
        });

        addScreenHandler(clientReferenceHeader, SampleWebMeta.getClientReferenceHeader(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReferenceHeader.setValue(getValue(SampleWebMeta.getClientReferenceHeader()));
            }

            public void onStateChange(StateChangeEvent event) {
                clientReferenceHeader.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? sampleOrgId : projectName;
            }
        });

        addScreenHandler(sampleOrgId, SampleWebMeta.getSampleOrgId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgId.setValue(getValue(SampleWebMeta.getSampleOrgId()));
            }

            public void onStateChange(StateChangeEvent event) {
                sampleOrgId.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? sampleOrgOrganizationName : clientReferenceHeader;
            }
        });

        addScreenHandler(sampleOrgOrganizationName, SampleWebMeta.getSampleOrgOrganizationName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgOrganizationName.setValue(getValue(SampleWebMeta.getSampleOrgOrganizationName()));
            }

            public void onStateChange(StateChangeEvent event) {
                sampleOrgOrganizationName.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? sampleOrgAttention : sampleOrgId;
            }
        });

        addScreenHandler(sampleOrgAttention, SampleWebMeta.getSampleOrgAttention(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgAttention.setValue(getValue(SampleWebMeta.getSampleOrgAttention()));
            }

            public void onStateChange(StateChangeEvent event) {
                sampleOrgAttention.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? addressMultipleUnit : sampleOrgOrganizationName;
            }
        });

        addScreenHandler(addressMultipleUnit, SampleWebMeta.getAddressMultipleUnit(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressMultipleUnit.setValue(getValue(SampleWebMeta.getAddressMultipleUnit()));
            }

            public void onStateChange(StateChangeEvent event) {
                addressMultipleUnit.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? addressStreetAddress : sampleOrgAttention;
            }
        });

        addScreenHandler(addressStreetAddress, SampleWebMeta.getAddressStreetAddress(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressStreetAddress.setValue(getValue(SampleWebMeta.getAddressStreetAddress()));
            }

            public void onStateChange(StateChangeEvent event) {
                addressStreetAddress.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? addressCity : addressMultipleUnit;
            }
        });

        addScreenHandler(addressCity, SampleWebMeta.getAddressCity(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressCity.setValue(getValue(SampleWebMeta.getAddressCity()));
            }

            public void onStateChange(StateChangeEvent event) {
                addressCity.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? addressState : addressStreetAddress;
            }
        });

        addScreenHandler(addressState, SampleWebMeta.getAddressState(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressState.setValue(getValue(SampleWebMeta.getAddressState()));
            }

            public void onStateChange(StateChangeEvent event) {
                addressState.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? addressZipCode : addressCity;
            }
        });

        addScreenHandler(addressZipCode, SampleWebMeta.getAddressZipCode(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressZipCode.setValue(getValue(SampleWebMeta.getAddressZipCode()));
            }

            public void onStateChange(StateChangeEvent event) {
                addressZipCode.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? itemTypeofSampleId : addressState;
            }
        });

        addScreenHandler(itemTypeofSampleId, SampleWebMeta.getItemTypeofSampleId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemTypeofSampleId.setValue(getValue(SampleWebMeta.getItemTypeofSampleId()));
            }

            public void onStateChange(StateChangeEvent event) {
                itemTypeofSampleId.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? itemSourceOfSampleId : addressZipCode;
            }
        });
        
        addScreenHandler(itemSourceOfSampleId, SampleWebMeta.getItemSourceOfSampleId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemSourceOfSampleId.setValue(getValue(SampleWebMeta.getItemSourceOfSampleId()));
            }

            public void onStateChange(StateChangeEvent event) {
                itemSourceOfSampleId.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? itemSourceOther : itemTypeofSampleId;
            }
        });

        addScreenHandler(itemSourceOther, SampleWebMeta.getItemSourceOther(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemSourceOther.setValue(getValue(SampleWebMeta.getItemSourceOther()));
            }

            public void onStateChange(StateChangeEvent event) {
                itemSourceOther.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? itemContainerId : itemSourceOfSampleId;
            }
        });

        addScreenHandler(itemContainerId, SampleWebMeta.getItemContainerId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemContainerId.setValue(getValue(SampleWebMeta.getItemContainerId()));
            }

            public void onStateChange(StateChangeEvent event) {
                itemContainerId.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? itemContainerReference : itemSourceOther;
            }
        });

        addScreenHandler(itemContainerReference, SampleWebMeta.getItemContainerReference(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemContainerReference.setValue(getValue(SampleWebMeta.getItemContainerReference()));
            }

            public void onStateChange(StateChangeEvent event) {
                itemContainerReference.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? itemItemSequence : itemContainerId;
            }
        });

        addScreenHandler(itemItemSequence, SampleWebMeta.getItemItemSequence(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemItemSequence.setValue(getValue(SampleWebMeta.getItemItemSequence()));
            }

            public void onStateChange(StateChangeEvent event) {
                itemItemSequence.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisId : itemContainerReference;
            }
        });

        addScreenHandler(analysisId, SampleWebMeta.getAnalysisId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisId.setValue(getValue(SampleWebMeta.getAnalysisId()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisId.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisTestNameHeader : itemItemSequence;
            }
        });

        addScreenHandler(analysisTestNameHeader, SampleWebMeta.getAnalysisTestNameHeader(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTestNameHeader.setValue(getValue(SampleWebMeta.getAnalysisTestNameHeader()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisTestNameHeader.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisMethodNameHeader : analysisId;
            }
        });

        addScreenHandler(analysisMethodNameHeader, SampleWebMeta.getAnalysisMethodNameHeader(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisMethodNameHeader.setValue(getValue(SampleWebMeta.getAnalysisMethodNameHeader()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisMethodNameHeader.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisStatusIdHeader : analysisTestNameHeader;
            }
        });

        addScreenHandler(analysisStatusIdHeader, SampleWebMeta.getAnalysisStatusIdHeader(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisStatusIdHeader.setValue(getValue(SampleWebMeta.getAnalysisStatusIdHeader()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisStatusIdHeader.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisRevision : analysisMethodNameHeader;
            }
        });

        addScreenHandler(analysisRevision, SampleWebMeta.getAnalysisRevision(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisRevision.setValue(getValue(SampleWebMeta.getAnalysisRevision()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisRevision.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisIsReportableHeader : analysisStatusIdHeader;
            }
        });

        addScreenHandler(analysisIsReportableHeader, SampleWebMeta.getAnalysisIsReportableHeader(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisIsReportableHeader.setValue(getValue(SampleWebMeta.getAnalysisIsReportableHeader()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisIsReportableHeader.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisUnitOfMeasureId : analysisRevision;
            }
        });

        addScreenHandler(analysisUnitOfMeasureId, SampleWebMeta.getAnalysisUnitOfMeasureId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisUnitOfMeasureId.setValue(getValue(SampleWebMeta.getAnalysisUnitOfMeasureId()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisUnitOfMeasureId.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisSubQaName : analysisIsReportableHeader;
            }
        });

        addScreenHandler(analysisSubQaName, SampleWebMeta.getAnalysisSubQaName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisSubQaName.setValue(getValue(SampleWebMeta.getAnalysisSubQaName()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisSubQaName.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisCompletedDate : analysisUnitOfMeasureId;
            }
        });

        addScreenHandler(analysisCompletedDate, SampleWebMeta.getAnalysisCompletedDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisCompletedDate.setValue(getValue(SampleWebMeta.getAnalysisCompletedDate()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisCompletedDate.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisCompletedBy : analysisSubQaName;
            }
        });

        addScreenHandler(analysisCompletedBy, SampleWebMeta.getAnalysisCompletedBy(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisCompletedBy.setValue(getValue(SampleWebMeta.getAnalysisCompletedBy()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisCompletedBy.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisReleasedDate : analysisCompletedDate;
            }
        });

        addScreenHandler(analysisReleasedDate, SampleWebMeta.getAnalysisReleasedDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisReleasedDate.setValue(getValue(SampleWebMeta.getAnalysisReleasedDate()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisReleasedDate.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisReleasedBy : analysisCompletedBy;
            }
        });

        addScreenHandler(analysisReleasedBy, SampleWebMeta.getAnalysisReleasedBy(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisReleasedBy.setValue(getValue(SampleWebMeta.getAnalysisReleasedBy()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisReleasedBy.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisStartedDate : analysisReleasedDate;
            }
        });

        addScreenHandler(analysisStartedDate, SampleWebMeta.getAnalysisStartedDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisStartedDate.setValue(getValue(SampleWebMeta.getAnalysisStartedDate()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisStartedDate.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisPrintedDate : analysisReleasedBy;
            }
        });

        addScreenHandler(analysisPrintedDate, SampleWebMeta.getAnalysisPrintedDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisPrintedDate.setValue(getValue(SampleWebMeta.getAnalysisPrintedDate()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisPrintedDate.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisSectionName : analysisStartedDate;
            }
        });

        addScreenHandler(analysisSectionName, SampleWebMeta.getAnalysisSectionName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisSectionName.setValue(getValue(SampleWebMeta.getAnalysisSectionName()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisSectionName.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisTypeId : analysisPrintedDate;
            }
        });
        
        addScreenHandler(analysisTypeId, SampleWebMeta.getAnalysisTypeId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTypeId.setValue(getValue(SampleWebMeta.getAnalysisTypeId()));
            }

            public void onStateChange(StateChangeEvent event) {
                analysisTypeId.setEnabled( isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? accessionNumber : analysisSectionName;
            }
        });
    }
    
    public void setData(DataView1VO data) {
        this.data = data;
    }

    public void onDataChange() {
        fireDataChange();
    }
    
    /**
     * Adds the keys for all checked checkboxes to the list of columns shown
     * in the generated excel file
     */
    public void addColumns(ArrayList<String> columns) {
        Widget w;
        CheckBox cb;

        for (Map.Entry<String, ScreenHandler<?>> entry : handlers.entrySet()) {
            w = entry.getValue().widget;
            if (w instanceof CheckBox) {
                cb = (CheckBox)w;
                if ("Y".equals(cb.getValue()))
                    columns.add(entry.getKey());
            }
        }
    }

    private String getValue(String column) {
        if (data == null || data.getColumns() == null)
            return "N";
        return data.getColumns().contains(column) ? "Y" : "N";
    }
}