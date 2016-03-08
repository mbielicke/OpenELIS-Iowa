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

import org.openelis.domain.DataView1VO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.CheckBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
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
                    enteredDate, releasedDate, statusId, sampleQAEventQAEventName, projectName,
                    clientReferenceHeader, reportToOrganizationId, reportToOrganizationName,
                    reportToAttention, reportToAddressMultipleUnit, reportToAddressStreetAddress,
                    reportToAddressCity, reportToAddressState, reportToAddressZipCode,
                    billToOrganizationId, billToOrganizationName, billToAttention,
                    billToAddressMultipleUnit, billToAddressStreetAddress, billToAddressCity,
                    billToAddressState, billToAddressZipCode, itemTypeofSampleId,
                    itemSourceOfSampleId, itemSourceOther, itemContainerId, itemContainerReference,
                    itemItemSequence, analysisId, analysisTestNameHeader, analysisMethodNameHeader,
                    analysisStatusIdHeader, analysisRevision, analysisIsReportableHeader,
                    analysisUnitOfMeasureId, analysisQAEventQAEventName, analysisCompletedDate,
                    analysisCompletedBy, analysisReleasedDate, analysisReleasedBy,
                    analysisStartedDate, analysisPrintedDate, analysisSectionName, analysisTypeId;

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
        addScreenHandler(accessionNumber,
                         SampleWebMeta.ACCESSION_NUMBER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 accessionNumber.setValue(getValue(SampleWebMeta.ACCESSION_NUMBER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ACCESSION_NUMBER, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 accessionNumber.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? revision : analysisTypeId;
                             }
                         });

        addScreenHandler(revision, SampleWebMeta.REVISION, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                revision.setValue(getValue(SampleWebMeta.REVISION));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.REVISION, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                revision.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? collectionDate : accessionNumber;
            }
        });

        addScreenHandler(collectionDate,
                         SampleWebMeta.COLLECTION_DATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 collectionDate.setValue(getValue(SampleWebMeta.COLLECTION_DATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.COLLECTION_DATE, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectionDate.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? receivedDate : revision;
                             }
                         });

        addScreenHandler(receivedDate, SampleWebMeta.RECEIVED_DATE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                receivedDate.setValue(getValue(SampleWebMeta.RECEIVED_DATE));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.RECEIVED_DATE, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                receivedDate.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? enteredDate : collectionDate;
            }
        });

        addScreenHandler(enteredDate, SampleWebMeta.ENTERED_DATE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                enteredDate.setValue(getValue(SampleWebMeta.ENTERED_DATE));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.ENTERED_DATE, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                enteredDate.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? releasedDate : receivedDate;
            }
        });

        addScreenHandler(releasedDate, SampleWebMeta.RELEASED_DATE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                releasedDate.setValue(getValue(SampleWebMeta.RELEASED_DATE));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.RELEASED_DATE, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                releasedDate.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? statusId : enteredDate;
            }
        });

        addScreenHandler(statusId, SampleWebMeta.STATUS_ID, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                statusId.setValue(getValue(SampleWebMeta.STATUS_ID));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.STATUS_ID, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                statusId.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? sampleQAEventQAEventName : releasedDate;
            }
        });

        addScreenHandler(sampleQAEventQAEventName,
                         SampleWebMeta.SAMPLE_QA_EVENT_QA_EVENT_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 sampleQAEventQAEventName.setValue(getValue(SampleWebMeta.SAMPLE_QA_EVENT_QA_EVENT_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.SAMPLE_QA_EVENT_QA_EVENT_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sampleQAEventQAEventName.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? projectName : statusId;
                             }
                         });

        addScreenHandler(projectName, SampleWebMeta.PROJECT_NAME, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                projectName.setValue(getValue(SampleWebMeta.PROJECT_NAME));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.PROJECT_NAME, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                projectName.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? clientReferenceHeader : sampleQAEventQAEventName;
            }
        });

        addScreenHandler(clientReferenceHeader,
                         SampleWebMeta.CLIENT_REFERENCE_HEADER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clientReferenceHeader.setValue(getValue(SampleWebMeta.CLIENT_REFERENCE_HEADER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIENT_REFERENCE_HEADER,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clientReferenceHeader.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToOrganizationId : projectName;
                             }
                         });

        addScreenHandler(reportToOrganizationId,
                         SampleWebMeta.REPORT_TO_ORG_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 reportToOrganizationId.setValue(getValue(SampleWebMeta.REPORT_TO_ORG_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.REPORT_TO_ORG_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportToOrganizationId.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToOrganizationName : clientReferenceHeader;
                             }
                         });

        addScreenHandler(reportToOrganizationName,
                         SampleWebMeta.REPORT_TO_ORG_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 reportToOrganizationName.setValue(getValue(SampleWebMeta.REPORT_TO_ORG_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.REPORT_TO_ORG_NAME, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportToOrganizationName.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToAttention : reportToOrganizationId;
                             }
                         });

        addScreenHandler(reportToAttention,
                         SampleWebMeta.REPORT_TO_ATTENTION,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 reportToAttention.setValue(getValue(SampleWebMeta.REPORT_TO_ATTENTION));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.REPORT_TO_ATTENTION,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportToAttention.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToAddressMultipleUnit
                                               : reportToOrganizationName;
                             }
                         });

        addScreenHandler(reportToAddressMultipleUnit,
                         SampleWebMeta.REPORT_TO_ADDR_MULTIPLE_UNIT,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 reportToAddressMultipleUnit.setValue(getValue(SampleWebMeta.REPORT_TO_ADDR_MULTIPLE_UNIT));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.REPORT_TO_ADDR_MULTIPLE_UNIT, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportToAddressMultipleUnit.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToAddressStreetAddress : reportToAttention;
                             }
                         });

        addScreenHandler(reportToAddressStreetAddress,
                         SampleWebMeta.REPORT_TO_ADDR_STREET_ADDRESS,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 reportToAddressStreetAddress.setValue(getValue(SampleWebMeta.REPORT_TO_ADDR_STREET_ADDRESS));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.REPORT_TO_ADDR_STREET_ADDRESS,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportToAddressStreetAddress.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToAddressCity : reportToAddressMultipleUnit;
                             }
                         });

        addScreenHandler(reportToAddressCity, SampleWebMeta.REPORT_TO_ADDR_CITY, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                reportToAddressCity.setValue(getValue(SampleWebMeta.REPORT_TO_ADDR_CITY));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.REPORT_TO_ADDR_CITY, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                reportToAddressCity.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? reportToAddressState : reportToAddressStreetAddress;
            }
        });

        addScreenHandler(reportToAddressState,
                         SampleWebMeta.REPORT_TO_ADDR_STATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 reportToAddressState.setValue(getValue(SampleWebMeta.REPORT_TO_ADDR_STATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.REPORT_TO_ADDR_STATE, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportToAddressState.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToAddressZipCode : reportToAddressCity;
                             }
                         });

        addScreenHandler(reportToAddressZipCode,
                         SampleWebMeta.REPORT_TO_ADDR_ZIP_CODE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 reportToAddressZipCode.setValue(getValue(SampleWebMeta.REPORT_TO_ADDR_ZIP_CODE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.REPORT_TO_ADDR_ZIP_CODE, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportToAddressZipCode.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? itemTypeofSampleId : reportToAddressState;
                             }
                         });
        

        addScreenHandler(billToOrganizationId,
                         SampleWebMeta.BILL_TO_ORG_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 billToOrganizationId.setValue(getValue(SampleWebMeta.BILL_TO_ORG_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.BILL_TO_ORG_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 billToOrganizationId.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToOrganizationName : clientReferenceHeader;
                             }
                         });

        addScreenHandler(billToOrganizationName,
                         SampleWebMeta.BILL_TO_ORG_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 billToOrganizationName.setValue(getValue(SampleWebMeta.BILL_TO_ORG_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.BILL_TO_ORG_NAME, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 billToOrganizationName.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToAttention : reportToOrganizationId;
                             }
                         });

        addScreenHandler(billToAttention,
                         SampleWebMeta.BILL_TO_ATTENTION,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 billToAttention.setValue(getValue(SampleWebMeta.BILL_TO_ATTENTION));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.BILL_TO_ATTENTION,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 billToAttention.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? billToAddressMultipleUnit
                                               : billToOrganizationName;
                             }
                         });

        addScreenHandler(billToAddressMultipleUnit,
                         SampleWebMeta.BILL_TO_ADDR_MULTIPLE_UNIT,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 billToAddressMultipleUnit.setValue(getValue(SampleWebMeta.BILL_TO_ADDR_MULTIPLE_UNIT));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.BILL_TO_ADDR_MULTIPLE_UNIT, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 billToAddressMultipleUnit.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? billToAddressStreetAddress : reportToAttention;
                             }
                         });

        addScreenHandler(billToAddressStreetAddress,
                         SampleWebMeta.BILL_TO_ADDR_STREET_ADDRESS,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 billToAddressStreetAddress.setValue(getValue(SampleWebMeta.BILL_TO_ADDR_STREET_ADDRESS));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.BILL_TO_ADDR_STREET_ADDRESS,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 billToAddressStreetAddress.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? billToAddressCity : billToAddressMultipleUnit;
                             }
                         });

        addScreenHandler(billToAddressCity, SampleWebMeta.BILL_TO_ADDR_CITY, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                billToAddressCity.setValue(getValue(SampleWebMeta.BILL_TO_ADDR_CITY));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.BILL_TO_ADDR_CITY, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                billToAddressCity.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? billToAddressState : billToAddressStreetAddress;
            }
        });

        addScreenHandler(billToAddressState,
                         SampleWebMeta.BILL_TO_ADDR_STATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 billToAddressState.setValue(getValue(SampleWebMeta.BILL_TO_ADDR_STATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.BILL_TO_ADDR_STATE, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 billToAddressState.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? billToAddressZipCode : billToAddressCity;
                             }
                         });

        addScreenHandler(billToAddressZipCode,
                         SampleWebMeta.BILL_TO_ADDR_ZIP_CODE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 billToAddressZipCode.setValue(getValue(SampleWebMeta.BILL_TO_ADDR_ZIP_CODE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.BILL_TO_ADDR_ZIP_CODE, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 billToAddressZipCode.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? itemTypeofSampleId : billToAddressState;
                             }
                         });

        addScreenHandler(itemTypeofSampleId,
                         SampleWebMeta.ITEM_TYPE_OF_SAMPLE_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 itemTypeofSampleId.setValue(getValue(SampleWebMeta.ITEM_TYPE_OF_SAMPLE_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ITEM_TYPE_OF_SAMPLE_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 itemTypeofSampleId.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? itemSourceOfSampleId : reportToAddressZipCode;
                             }
                         });

        addScreenHandler(itemSourceOfSampleId,
                         SampleWebMeta.ITEM_SOURCE_OF_SAMPLE_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 itemSourceOfSampleId.setValue(getValue(SampleWebMeta.ITEM_SOURCE_OF_SAMPLE_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ITEM_SOURCE_OF_SAMPLE_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 itemSourceOfSampleId.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? itemSourceOther : itemTypeofSampleId;
                             }
                         });

        addScreenHandler(itemSourceOther,
                         SampleWebMeta.ITEM_SOURCE_OTHER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 itemSourceOther.setValue(getValue(SampleWebMeta.ITEM_SOURCE_OTHER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ITEM_SOURCE_OTHER, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 itemSourceOther.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? itemContainerId : itemSourceOfSampleId;
                             }
                         });

        addScreenHandler(itemContainerId,
                         SampleWebMeta.ITEM_CONTAINER_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 itemContainerId.setValue(getValue(SampleWebMeta.ITEM_CONTAINER_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ITEM_CONTAINER_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 itemContainerId.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? itemContainerReference : itemSourceOther;
                             }
                         });

        addScreenHandler(itemContainerReference,
                         SampleWebMeta.ITEM_CONTAINER_REFERENCE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 itemContainerReference.setValue(getValue(SampleWebMeta.ITEM_CONTAINER_REFERENCE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ITEM_CONTAINER_REFERENCE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 itemContainerReference.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? itemItemSequence : itemContainerId;
                             }
                         });

        addScreenHandler(itemItemSequence,
                         SampleWebMeta.ITEM_ITEM_SEQUENCE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 itemItemSequence.setValue(getValue(SampleWebMeta.ITEM_ITEM_SEQUENCE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ITEM_ITEM_SEQUENCE, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 itemItemSequence.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisId : itemContainerReference;
                             }
                         });

        addScreenHandler(analysisId, SampleWebMeta.ANALYSIS_ID, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                analysisId.setValue(getValue(SampleWebMeta.ANALYSIS_ID));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.ANALYSIS_ID, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                analysisId.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisTestNameHeader : itemItemSequence;
            }
        });

        addScreenHandler(analysisTestNameHeader,
                         SampleWebMeta.ANALYSIS_TEST_NAME_HEADER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisTestNameHeader.setValue(getValue(SampleWebMeta.ANALYSIS_TEST_NAME_HEADER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_TEST_NAME_HEADER,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisTestNameHeader.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisMethodNameHeader : analysisId;
                             }
                         });

        addScreenHandler(analysisMethodNameHeader,
                         SampleWebMeta.ANALYSIS_METHOD_NAME_HEADER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisMethodNameHeader.setValue(getValue(SampleWebMeta.ANALYSIS_METHOD_NAME_HEADER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_METHOD_NAME_HEADER,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisMethodNameHeader.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisStatusIdHeader : analysisTestNameHeader;
                             }
                         });

        addScreenHandler(analysisStatusIdHeader,
                         SampleWebMeta.ANALYSIS_STATUS_ID_HEADER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisStatusIdHeader.setValue(getValue(SampleWebMeta.ANALYSIS_STATUS_ID_HEADER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_STATUS_ID_HEADER,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisStatusIdHeader.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisRevision : analysisMethodNameHeader;
                             }
                         });

        addScreenHandler(analysisRevision,
                         SampleWebMeta.ANALYSIS_REVISION,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisRevision.setValue(getValue(SampleWebMeta.ANALYSIS_REVISION));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_REVISION, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisRevision.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisIsReportableHeader
                                               : analysisStatusIdHeader;
                             }
                         });

        addScreenHandler(analysisIsReportableHeader,
                         SampleWebMeta.ANALYSIS_IS_REPORTABLE_HEADER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisIsReportableHeader.setValue(getValue(SampleWebMeta.ANALYSIS_IS_REPORTABLE_HEADER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_IS_REPORTABLE_HEADER,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisIsReportableHeader.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisUnitOfMeasureId : analysisRevision;
                             }
                         });

        addScreenHandler(analysisUnitOfMeasureId,
                         SampleWebMeta.ANALYSIS_UNIT_OF_MEASURE_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisUnitOfMeasureId.setValue(getValue(SampleWebMeta.ANALYSIS_UNIT_OF_MEASURE_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_UNIT_OF_MEASURE_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisUnitOfMeasureId.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisQAEventQAEventName
                                               : analysisIsReportableHeader;
                             }
                         });

        addScreenHandler(analysisQAEventQAEventName,
                         SampleWebMeta.ANALYSIS_QA_EVENT_QA_EVENT_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisQAEventQAEventName.setValue(getValue(SampleWebMeta.ANALYSIS_QA_EVENT_QA_EVENT_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_QA_EVENT_QA_EVENT_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisQAEventQAEventName.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisCompletedDate : analysisUnitOfMeasureId;
                             }
                         });

        addScreenHandler(analysisCompletedDate,
                         SampleWebMeta.ANALYSIS_COMPLETED_DATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisCompletedDate.setValue(getValue(SampleWebMeta.ANALYSIS_COMPLETED_DATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_COMPLETED_DATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisCompletedDate.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisCompletedBy : analysisQAEventQAEventName;
                             }
                         });

        addScreenHandler(analysisCompletedBy,
                         SampleWebMeta.ANALYSIS_COMPLETED_BY,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisCompletedBy.setValue(getValue(SampleWebMeta.ANALYSIS_COMPLETED_BY));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_COMPLETED_BY,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisCompletedBy.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisReleasedDate : analysisCompletedDate;
                             }
                         });

        addScreenHandler(analysisReleasedDate,
                         SampleWebMeta.ANALYSIS_RELEASED_DATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisReleasedDate.setValue(getValue(SampleWebMeta.ANALYSIS_RELEASED_DATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_RELEASED_DATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisReleasedDate.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisReleasedBy : analysisCompletedBy;
                             }
                         });

        addScreenHandler(analysisReleasedBy,
                         SampleWebMeta.ANALYSIS_RELEASED_BY,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisReleasedBy.setValue(getValue(SampleWebMeta.ANALYSIS_RELEASED_BY));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_RELEASED_BY,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisReleasedBy.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisStartedDate : analysisReleasedDate;
                             }
                         });

        addScreenHandler(analysisStartedDate,
                         SampleWebMeta.ANALYSIS_STARTED_DATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisStartedDate.setValue(getValue(SampleWebMeta.ANALYSIS_STARTED_DATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_STARTED_DATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisStartedDate.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisPrintedDate : analysisReleasedBy;
                             }
                         });

        addScreenHandler(analysisPrintedDate,
                         SampleWebMeta.ANALYSIS_PRINTED_DATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisPrintedDate.setValue(getValue(SampleWebMeta.ANALYSIS_PRINTED_DATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_PRINTED_DATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisPrintedDate.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisSectionName : analysisStartedDate;
                             }
                         });

        addScreenHandler(analysisSectionName,
                         SampleWebMeta.ANALYSIS_SECTION_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisSectionName.setValue(getValue(SampleWebMeta.ANALYSIS_SECTION_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_SECTION_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisSectionName.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisTypeId : analysisPrintedDate;
                             }
                         });

        addScreenHandler(analysisTypeId,
                         SampleWebMeta.ANALYSIS_TYPE_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisTypeId.setValue(getValue(SampleWebMeta.ANALYSIS_TYPE_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANALYSIS_TYPE_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisTypeId.setEnabled(isState(DEFAULT));
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
     * Returns the value indicating whether the passed column is selected or not
     * to be shown in the report; if the column is selected, the value is "Y";
     * otherwise it's "N"
     */
    private String getValue(String column) {
        if (data == null || data.getColumns() == null)
            return "N";
        return data.getColumns().contains(column) ? "Y" : "N";
    }

    /**
     * Fires an event to notify column order tab that the passed column needs to
     * be added to or removed from the list of columns shown in the report; the
     * column is added if the passed value is "Y"; it's removed otherwise
     */
    private void addRemoveColumn(String column, String value) {
        ColumnEvent.Action action;

        action = "Y".equals(value) ? ColumnEvent.Action.ADD : ColumnEvent.Action.REMOVE;
        parentBus.fireEvent(new ColumnEvent(column, action));
    }
}