package org.openelis.modules.dataView.client;

import java.util.EnumSet;

import org.openelis.domain.DataViewVO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.meta.SampleWebMeta;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class CommonTab extends Screen {
    
    private DataViewVO data;
    private CheckBox   accessionNumber, revision, collectionDate, receivedDate, enteredDate,
                       releasedDate, statusId, projectName, clientReferenceHeader, sampleOrgId,
                       sampleOrgOrganizationName, sampleOrgAttention, addressMultipleUnit,
                       addressStreetAddress, addressCity, addressState, addressZipCode,
                       itemTypeofSampleId, itemSourceOfSampleId, itemSourceOther,
                       itemContainerId, analysisTestNameHeader, analysisMethodNameHeader,
                       analysisStatusIdHeader, analysisRevision, analysisIsReportable,
                       analysisSubQaName, analysisCompletedDate, analysisCompletedBy,
                       analysisReleasedDate, analysisReleasedBy, analysisStartedDate,
                       analysisPrintedDate;
    private boolean    loaded;
    
    public CommonTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();
    }
    
    private void initialize() {
        accessionNumber = (CheckBox)def.getWidget(SampleWebMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(data.getAccessionNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAccessionNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        revision = (CheckBox)def.getWidget(SampleWebMeta.getRevision());
        addScreenHandler(revision, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                revision.setValue(data.getRevision());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setRevision(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                revision.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        collectionDate = (CheckBox)def.getWidget(SampleWebMeta.getCollectionDate());
        addScreenHandler(collectionDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectionDate.setValue(data.getCollectionDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectionDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        receivedDate = (CheckBox)def.getWidget(SampleWebMeta.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(data.getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        enteredDate = (CheckBox)def.getWidget(SampleWebMeta.getEnteredDate());
        addScreenHandler(enteredDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                enteredDate.setValue(data.getEnteredDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setEnteredDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                enteredDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        releasedDate = (CheckBox)def.getWidget(SampleWebMeta.getReleasedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setValue(data.getReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReleasedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        statusId = (CheckBox)def.getWidget(SampleWebMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(data.getStatusId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        projectName = (CheckBox)def.getWidget(SampleWebMeta.getProjectName());
        addScreenHandler(projectName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                projectName.setValue(data.getProjectName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setProjectName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        clientReferenceHeader = (CheckBox)def.getWidget(SampleWebMeta.getClientReferenceHeader());
        addScreenHandler(clientReferenceHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReferenceHeader.setValue(data.getClientReferenceHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setClientReferenceHeader(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                clientReferenceHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sampleOrgId = (CheckBox)def.getWidget(SampleWebMeta.getSampleOrgId());
        addScreenHandler(sampleOrgId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgId.setValue(data.getOrganizationId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleOrgId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sampleOrgOrganizationName = (CheckBox)def.getWidget(SampleWebMeta.getSampleOrgOrganizationName());
        addScreenHandler(sampleOrgOrganizationName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgOrganizationName.setValue(data.getOrganizationName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleOrgOrganizationName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sampleOrgAttention = (CheckBox)def.getWidget(SampleWebMeta.getSampleOrgAttention());
        addScreenHandler(sampleOrgAttention, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgAttention.setValue(data.getOrganizationAttention());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAttention(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleOrgAttention.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressMultipleUnit = (CheckBox)def.getWidget(SampleWebMeta.getAddressMultipleUnit());
        addScreenHandler(addressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressMultipleUnit.setValue(data.getOrganizationAddressMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressMultipleUnit.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressStreetAddress = (CheckBox)def.getWidget(SampleWebMeta.getAddressStreetAddress());
        addScreenHandler(addressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressStreetAddress.setValue(data.getOrganizationAddressAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressStreetAddress.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressCity = (CheckBox)def.getWidget(SampleWebMeta.getAddressCity());
        addScreenHandler(addressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressCity.setValue(data.getOrganizationAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressCity.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressState = (CheckBox)def.getWidget(SampleWebMeta.getAddressState());
        addScreenHandler(addressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressState.setValue(data.getOrganizationAddressState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressState.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressZipCode = (CheckBox)def.getWidget(SampleWebMeta.getAddressZipCode());
        addScreenHandler(addressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressZipCode.setValue(data.getOrganizationAddressZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressZipCode.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        itemTypeofSampleId = (CheckBox)def.getWidget(SampleWebMeta.getItemTypeofSampleId());
        addScreenHandler(itemTypeofSampleId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemTypeofSampleId.setValue(data.getSampleItemTypeofSampleId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemTypeofSampleId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemTypeofSampleId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        itemSourceOfSampleId = (CheckBox)def.getWidget(SampleWebMeta.getItemSourceOfSampleId());
        addScreenHandler(itemSourceOfSampleId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemSourceOfSampleId.setValue(data.getSampleItemSourceOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemSourceOfSampleId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemSourceOfSampleId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        itemSourceOther = (CheckBox)def.getWidget(SampleWebMeta.getItemSourceOther());
        addScreenHandler(itemSourceOther, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemSourceOther.setValue(data.getSampleItemSourceOther());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemSourceOther(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemSourceOther.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        itemContainerId = (CheckBox)def.getWidget(SampleWebMeta.getItemContainerId());
        addScreenHandler(itemContainerId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemContainerId.setValue(data.getSampleItemContainerId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemContainerId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemContainerId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisTestNameHeader = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisTestNameHeader());
        addScreenHandler(analysisTestNameHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTestNameHeader.setValue(data.getAnalysisTestNameHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestNameHeader(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTestNameHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisMethodNameHeader = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisMethodNameHeader());
        addScreenHandler(analysisMethodNameHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisMethodNameHeader.setValue(data.getAnalysisTestMethodNameHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestMethodNameHeader(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisMethodNameHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisStatusIdHeader = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisStatusIdHeader());
        addScreenHandler(analysisStatusIdHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisStatusIdHeader.setValue(data.getAnalysisStatusIdHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisStatusIdHeader(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisStatusIdHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisRevision = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisRevision());
        addScreenHandler(analysisRevision, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisRevision.setValue(data.getAnalysisRevision());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisRevision(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisRevision.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisIsReportable = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisIsReportable());
        addScreenHandler(analysisIsReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisIsReportable.setValue(data.getAnalysisIsReportable());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisIsReportable(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisIsReportable.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisSubQaName = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisSubQaName());
        addScreenHandler(analysisSubQaName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisSubQaName.setValue(data.getAnalysisQaName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisQaName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisSubQaName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisCompletedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisCompletedDate());
        addScreenHandler(analysisCompletedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisCompletedDate.setValue(data.getAnalysisCompletedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisCompletedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisCompletedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisCompletedBy = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisCompletedBy());
        addScreenHandler(analysisCompletedBy, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisCompletedBy.setValue(data.getAnalysisCompletedBy());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisCompletedBy(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisCompletedBy.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisReleasedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisReleasedDate());
        addScreenHandler(analysisReleasedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisReleasedDate.setValue(data.getAnalysisReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisReleasedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisReleasedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisReleasedBy = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisReleasedBy());
        addScreenHandler(analysisReleasedBy, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisReleasedBy.setValue(data.getAnalysisReleasedBy());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisReleasedBy(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisReleasedBy.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisStartedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisStartedDate());
        addScreenHandler(analysisStartedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisStartedDate.setValue(data.getAnalysisStartedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisStartedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisStartedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisPrintedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisPrintedDate());
        addScreenHandler(analysisPrintedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisPrintedDate.setValue(data.getAnalysisPrintedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisPrintedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisPrintedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
    }   
    
    public void setData(DataViewVO data) {
        this.data = data;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
    
}
