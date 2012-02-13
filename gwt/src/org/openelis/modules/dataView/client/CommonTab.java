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
                       itemContainerId, itemContainerReference, itemItemSequence, analysisTestNameHeader, 
                       analysisMethodNameHeader, analysisStatusIdHeader, analysisRevision,
                       analysisIsReportable, analysisUnitOfMeasureId,
                       analysisSubQaName, analysisCompletedDate, analysisCompletedBy,
                       analysisReleasedDate, analysisReleasedBy, analysisStartedDate,
                       analysisPrintedDate;
    private boolean    loaded;
    private int        checkCount;
    
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
                changeCount(data.getAccessionNumber(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAccessionNumber(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        revision = (CheckBox)def.getWidget(SampleWebMeta.getRevision());
        addScreenHandler(revision, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                revision.setValue(data.getRevision());
                changeCount(data.getRevision(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setRevision(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                revision.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        collectionDate = (CheckBox)def.getWidget(SampleWebMeta.getCollectionDate());
        addScreenHandler(collectionDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectionDate.setValue(data.getCollectionDate());
                changeCount(data.getCollectionDate(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectionDate(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectionDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        receivedDate = (CheckBox)def.getWidget(SampleWebMeta.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(data.getReceivedDate());
                changeCount(data.getReceivedDate(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReceivedDate(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        enteredDate = (CheckBox)def.getWidget(SampleWebMeta.getEnteredDate());
        addScreenHandler(enteredDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                enteredDate.setValue(data.getEnteredDate());
                changeCount(data.getEnteredDate(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setEnteredDate(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                enteredDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        releasedDate = (CheckBox)def.getWidget(SampleWebMeta.getReleasedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setValue(data.getReleasedDate());
                changeCount(data.getReleasedDate(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReleasedDate(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        statusId = (CheckBox)def.getWidget(SampleWebMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(data.getStatusId());
                changeCount(data.getStatusId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setStatusId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        projectName = (CheckBox)def.getWidget(SampleWebMeta.getProjectName());
        addScreenHandler(projectName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                projectName.setValue(data.getProjectName());
                changeCount(data.getProjectName(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setProjectName(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        clientReferenceHeader = (CheckBox)def.getWidget(SampleWebMeta.getClientReferenceHeader());
        addScreenHandler(clientReferenceHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReferenceHeader.setValue(data.getClientReferenceHeader());
                changeCount(data.getClientReferenceHeader(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setClientReferenceHeader(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                clientReferenceHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sampleOrgId = (CheckBox)def.getWidget(SampleWebMeta.getSampleOrgId());
        addScreenHandler(sampleOrgId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgId.setValue(data.getOrganizationId());
                changeCount(data.getOrganizationId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleOrgId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sampleOrgOrganizationName = (CheckBox)def.getWidget(SampleWebMeta.getSampleOrgOrganizationName());
        addScreenHandler(sampleOrgOrganizationName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgOrganizationName.setValue(data.getOrganizationName());
                changeCount(data.getOrganizationName(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationName(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleOrgOrganizationName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sampleOrgAttention = (CheckBox)def.getWidget(SampleWebMeta.getSampleOrgAttention());
        addScreenHandler(sampleOrgAttention, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgAttention.setValue(data.getOrganizationAttention());
                changeCount(data.getOrganizationAttention(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAttention(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleOrgAttention.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressMultipleUnit = (CheckBox)def.getWidget(SampleWebMeta.getAddressMultipleUnit());
        addScreenHandler(addressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressMultipleUnit.setValue(data.getOrganizationAddressMultipleUnit());
                changeCount(data.getOrganizationAddressMultipleUnit(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressMultipleUnit(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressMultipleUnit.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressStreetAddress = (CheckBox)def.getWidget(SampleWebMeta.getAddressStreetAddress());
        addScreenHandler(addressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressStreetAddress.setValue(data.getOrganizationAddressAddress());
                changeCount(data.getOrganizationAddressAddress(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressAddress(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressStreetAddress.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressCity = (CheckBox)def.getWidget(SampleWebMeta.getAddressCity());
        addScreenHandler(addressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressCity.setValue(data.getOrganizationAddressCity());
                changeCount(data.getOrganizationAddressCity(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressCity(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressCity.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressState = (CheckBox)def.getWidget(SampleWebMeta.getAddressState());
        addScreenHandler(addressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressState.setValue(data.getOrganizationAddressState());
                changeCount(data.getOrganizationAddressState(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressState(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressState.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressZipCode = (CheckBox)def.getWidget(SampleWebMeta.getAddressZipCode());
        addScreenHandler(addressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressZipCode.setValue(data.getOrganizationAddressZipCode());
                changeCount(data.getOrganizationAddressZipCode(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressZipCode(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressZipCode.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        itemTypeofSampleId = (CheckBox)def.getWidget(SampleWebMeta.getItemTypeofSampleId());
        addScreenHandler(itemTypeofSampleId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemTypeofSampleId.setValue(data.getSampleItemTypeofSampleId());
                changeCount(data.getSampleItemTypeofSampleId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemTypeofSampleId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemTypeofSampleId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        itemSourceOfSampleId = (CheckBox)def.getWidget(SampleWebMeta.getItemSourceOfSampleId());
        addScreenHandler(itemSourceOfSampleId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemSourceOfSampleId.setValue(data.getSampleItemSourceOfSampleId());
                changeCount(data.getSampleItemSourceOfSampleId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemSourceOfSampleId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemSourceOfSampleId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        itemSourceOther = (CheckBox)def.getWidget(SampleWebMeta.getItemSourceOther());
        addScreenHandler(itemSourceOther, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemSourceOther.setValue(data.getSampleItemSourceOther());
                changeCount(data.getSampleItemSourceOther(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemSourceOther(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemSourceOther.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        itemContainerId = (CheckBox)def.getWidget(SampleWebMeta.getItemContainerId());
        addScreenHandler(itemContainerId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemContainerId.setValue(data.getSampleItemContainerId());
                changeCount(data.getSampleItemContainerId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemContainerId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemContainerId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        itemContainerReference = (CheckBox)def.getWidget(SampleWebMeta.getItemContainerReference());
        addScreenHandler(itemContainerReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemContainerReference.setValue(data.getSampleItemContainerReference());
                changeCount(data.getSampleItemContainerReference(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemContainerReference(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemContainerReference.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        itemItemSequence = (CheckBox)def.getWidget(SampleWebMeta.getItemItemSequence());
        addScreenHandler(itemItemSequence, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemItemSequence.setValue(data.getSampleItemItemSequence());
                changeCount(data.getSampleItemItemSequence(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemItemSequence(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemItemSequence.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisTestNameHeader = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisTestNameHeader());
        addScreenHandler(analysisTestNameHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTestNameHeader.setValue(data.getAnalysisTestNameHeader());
                changeCount(data.getAnalysisTestNameHeader(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestNameHeader(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTestNameHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisMethodNameHeader = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisMethodNameHeader());
        addScreenHandler(analysisMethodNameHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisMethodNameHeader.setValue(data.getAnalysisTestMethodNameHeader());
                changeCount(data.getAnalysisTestMethodNameHeader(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestMethodNameHeader(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisMethodNameHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisStatusIdHeader = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisStatusIdHeader());
        addScreenHandler(analysisStatusIdHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisStatusIdHeader.setValue(data.getAnalysisStatusIdHeader());
                changeCount(data.getAnalysisStatusIdHeader(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisStatusIdHeader(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisStatusIdHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisRevision = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisRevision());
        addScreenHandler(analysisRevision, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisRevision.setValue(data.getAnalysisRevision());
                changeCount(data.getAnalysisRevision(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisRevision(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisRevision.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisIsReportable = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisIsReportable());
        addScreenHandler(analysisIsReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisIsReportable.setValue(data.getAnalysisIsReportable());
                changeCount(data.getAnalysisIsReportable(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisIsReportable(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisIsReportable.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        analysisUnitOfMeasureId = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisUnitOfMeasureId());
        addScreenHandler(analysisUnitOfMeasureId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisUnitOfMeasureId.setValue(data.getAnalysisUnitOfMeasureId());
                changeCount(data.getAnalysisUnitOfMeasureId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisUnitOfMeasureId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisUnitOfMeasureId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        analysisSubQaName = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisSubQaName());
        addScreenHandler(analysisSubQaName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisSubQaName.setValue(data.getAnalysisQaName());
                changeCount(data.getAnalysisQaName(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisQaName(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisSubQaName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisCompletedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisCompletedDate());
        addScreenHandler(analysisCompletedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisCompletedDate.setValue(data.getAnalysisCompletedDate());
                changeCount(data.getAnalysisCompletedDate(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisCompletedDate(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisCompletedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisCompletedBy = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisCompletedBy());
        addScreenHandler(analysisCompletedBy, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisCompletedBy.setValue(data.getAnalysisCompletedBy());
                changeCount(data.getAnalysisCompletedBy(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisCompletedBy(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisCompletedBy.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisReleasedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisReleasedDate());
        addScreenHandler(analysisReleasedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisReleasedDate.setValue(data.getAnalysisReleasedDate());
                changeCount(data.getAnalysisReleasedDate(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisReleasedDate(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisReleasedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisReleasedBy = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisReleasedBy());
        addScreenHandler(analysisReleasedBy, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisReleasedBy.setValue(data.getAnalysisReleasedBy());
                changeCount(data.getAnalysisReleasedBy(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisReleasedBy(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisReleasedBy.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisStartedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisStartedDate());
        addScreenHandler(analysisStartedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisStartedDate.setValue(data.getAnalysisStartedDate());
                changeCount(data.getAnalysisStartedDate(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisStartedDate(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisStartedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisPrintedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisPrintedDate());
        addScreenHandler(analysisPrintedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisPrintedDate.setValue(data.getAnalysisPrintedDate());
                changeCount(data.getAnalysisPrintedDate(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisPrintedDate(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisPrintedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
    }   
    
    public void setData(DataViewVO data) {
        this.data = data;
        loaded = false;
        checkCount = 0;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
    
    public int getCheckIndicator() {
        if (checkCount > 0)
            return 1;
         return 0;
    }
    
    private void changeCount(String val, boolean manual) {
        /*
         * CheckCount keeps track of the number of checkboxes checked in the tab.
         * It's decremented only when the value is changed manually i.e. 
         * when ValueChangeEvent gets fired and only if it doesn't become negative.
         * This is done to make sure that only the checkboxes unchecked by the user 
         * affect the value rather than the default values in the VO.     
         */
        if ("Y".equals(val))            
            checkCount++;        
        else if (checkCount > 0 && manual)
            checkCount--;
    }
    
}
