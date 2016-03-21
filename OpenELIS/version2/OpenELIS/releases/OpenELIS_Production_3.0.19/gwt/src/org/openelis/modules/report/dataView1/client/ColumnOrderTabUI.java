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

import org.openelis.constants.Messages;
import org.openelis.domain.DataView1VO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class ColumnOrderTabUI extends Screen {
    @UiTemplate("ColumnOrderTab.ui.xml")
    interface ColumnOrderTabUIBinder extends UiBinder<Widget, ColumnOrderTabUI> {
    };

    private static ColumnOrderTabUIBinder uiBinder = GWT.create(ColumnOrderTabUIBinder.class);

    @UiField
    protected Table                       table;

    @UiField
    protected Button                      moveUpButton, moveDownButton;

    protected Screen                      parentScreen;

    protected EventBus                    parentBus;

    protected DataView1VO                 data;

    public ColumnOrderTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    public void initialize() {
        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent<ArrayList<Row>> event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(isState(DEFAULT));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                moveUpButton.setEnabled(isState(DEFAULT));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                moveDownButton.setEnabled(isState(DEFAULT));
            }
        });

        /*
         * handler for the event that gets fired when the checkbox for a column
         * gets checked or unchecked to add or remove the column respectively
         */
        parentBus.addHandler(ColumnEvent.getType(), new ColumnEvent.Handler() {
            @Override
            public void onColumn(ColumnEvent event) {
                int i;
                String column;

                column = event.getColumn();
                switch (event.getAction()) {
                    case ADD:
                        /*
                         * add the column and the row for it
                         */
                        if (data.getColumns() == null)
                            data.setColumns(new ArrayList<String>());
                        data.getColumns().add(column);
                        table.addRow(new Row(getHeader(column)));
                        break;
                    case REMOVE:
                        /*
                         * remove the column and the row for it
                         */
                        i = 0;
                        for (String c : data.getColumns()) {
                            if (c.equals(column)) {
                                data.getColumns().remove(i);
                                table.removeRowAt(i);
                                break;
                            }
                            i++ ;
                        }
                        break;
                }
            }
        });
    }

    public void setData(DataView1VO data) {
        this.data = data;
    }

    public void onDataChange() {
        fireDataChange();
    }

    @UiHandler("moveUpButton")
    protected void moveUp(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r <= 0)
            return;

        moveColumn(r, r - 1);
        fireDataChange();
        table.selectRowAt(r - 1);
    }

    @UiHandler("moveDownButton")
    protected void moveDown(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r < 0 || r >= table.getRowCount() - 1)
            return;

        moveColumn(r, r + 1);
        fireDataChange();
        table.selectRowAt(r + 1);
    }

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;

        if (data == null || data.getColumns() == null)
            return null;

        model = new ArrayList<Row>();
        for (String c : data.getColumns())
            model.add(new Row(getHeader(c)));

        return model;
    }

    private String getHeader(String column) {
        switch (column) {
        /*
         * sample fields
         */
            case SampleWebMeta.ACCESSION_NUMBER:
                return Messages.get().sample_accessionNum();
            case SampleWebMeta.REVISION:
                return Messages.get().dataView_sampleRevision();
            case SampleWebMeta.COLLECTION_DATE:
                return Messages.get().sample_collectedDate();
            case SampleWebMeta.RECEIVED_DATE:
                return Messages.get().gen_receivedDate();
            case SampleWebMeta.ENTERED_DATE:
                return Messages.get().gen_enteredDate();
            case SampleWebMeta.RELEASED_DATE:
                return Messages.get().sample_releasedDate();
            case SampleWebMeta.STATUS_ID:
                return Messages.get().sample_status();
            case SampleWebMeta.SAMPLE_QA_EVENT_QA_EVENT_NAME:
                return Messages.get().dataView_sampleQAEvent();
            case SampleWebMeta.PROJECT_NAME:
                return Messages.get().project_project();
            case SampleWebMeta.CLIENT_REFERENCE_HEADER:
                return Messages.get().sample_clntRef();
                /*
                 * organization fields
                 */
            case SampleWebMeta.SAMPLE_ORG_ID:
                return Messages.get().organization_num();
            case SampleWebMeta.ORG_NAME:
                return Messages.get().organization_name();
            case SampleWebMeta.SAMPLE_ORG_ATTENTION:
                return Messages.get().order_attention();
            case SampleWebMeta.ADDR_MULTIPLE_UNIT:
                return Messages.get().address_aptSuite();
            case SampleWebMeta.ADDR_STREET_ADDRESS:
                return Messages.get().address_address();
            case SampleWebMeta.ADDR_CITY:
                return Messages.get().address_city();
            case SampleWebMeta.ADDR_STATE:
                return Messages.get().address_state();
            case SampleWebMeta.ADDR_ZIP_CODE:
                return Messages.get().address_zipcode();
                /*
                 * sample item fields
                 */
            case SampleWebMeta.ITEM_TYPE_OF_SAMPLE_ID:
                return Messages.get().gen_sampleType();
            case SampleWebMeta.ITEM_SOURCE_OF_SAMPLE_ID:
                return Messages.get().gen_source();
            case SampleWebMeta.ITEM_SOURCE_OTHER:
                return Messages.get().sampleItem_sourceOther();
            case SampleWebMeta.ITEM_CONTAINER_ID:
                return Messages.get().gen_container();
            case SampleWebMeta.ITEM_CONTAINER_REFERENCE:
                return Messages.get().sampleItem_containerReference();
            case SampleWebMeta.ITEM_ITEM_SEQUENCE:
                return Messages.get().gen_sequence();
                /*
                 * analysis fields
                 */
            case SampleWebMeta.ANALYSIS_ID:
                return Messages.get().analysis_id();
            case SampleWebMeta.ANALYSIS_TEST_NAME_HEADER:
                return Messages.get().gen_test();
            case SampleWebMeta.ANALYSIS_METHOD_NAME_HEADER:
                return Messages.get().gen_method();
            case SampleWebMeta.ANALYSIS_STATUS_ID_HEADER:
                return Messages.get().analysis_status();
            case SampleWebMeta.ANALYSIS_REVISION:
                return Messages.get().analysis_revision();
            case SampleWebMeta.ANALYSIS_IS_REPORTABLE_HEADER:
                return Messages.get().gen_reportable();
            case SampleWebMeta.ANALYSIS_UNIT_OF_MEASURE_ID:
                return Messages.get().gen_unit();
            case SampleWebMeta.ANALYSIS_QA_EVENT_QA_EVENT_NAME:
                return Messages.get().dataView_analysisQAEvent();
            case SampleWebMeta.ANALYSIS_COMPLETED_DATE:
                return Messages.get().gen_completedDate();
            case SampleWebMeta.ANALYSIS_COMPLETED_BY:
                return Messages.get().dataView_completedBy();
            case SampleWebMeta.ANALYSIS_RELEASED_DATE:
                return Messages.get().analysis_releasedDate();
            case SampleWebMeta.ANALYSIS_RELEASED_BY:
                return Messages.get().dataView_releasedBy();
            case SampleWebMeta.ANALYSIS_STARTED_DATE:
                return Messages.get().gen_startedDate();
            case SampleWebMeta.ANALYSIS_PRINTED_DATE:
                return Messages.get().gen_printedDate();
            case SampleWebMeta.ANALYSIS_SECTION_NAME:
                return Messages.get().gen_section();
            case SampleWebMeta.ANALYSIS_TYPE_ID:
                return Messages.get().analysis_type();
                /*
                 * environmental fields
                 */
            case SampleWebMeta.ENV_IS_HAZARDOUS:
                return Messages.get().sampleEnvironmental_hazardous();
            case SampleWebMeta.ENV_PRIORITY:
                return Messages.get().gen_priority();
            case SampleWebMeta.ENV_COLLECTOR_HEADER:
                return Messages.get().env_collector();
            case SampleWebMeta.ENV_COLLECTOR_PHONE:
                return Messages.get().address_phone();
            case SampleWebMeta.ENV_DESCRIPTION:
                return Messages.get().sample_description();
            case SampleWebMeta.ENV_LOCATION:
                return Messages.get().env_location();
            case SampleWebMeta.LOCATION_ADDR_MULTIPLE_UNIT:
                return Messages.get().dataView_locationAptSuite();
            case SampleWebMeta.LOCATION_ADDR_STREET_ADDRESS:
                return Messages.get().dataView_locationAddress();
            case SampleWebMeta.LOCATION_ADDR_CITY:
                return Messages.get().dataView_locationCity();
            case SampleWebMeta.LOCATION_ADDR_STATE:
                return Messages.get().dataView_locationState();
            case SampleWebMeta.LOCATION_ADDR_ZIP_CODE:
                return Messages.get().dataView_locationZipCode();
            case SampleWebMeta.LOCATION_ADDR_COUNTRY:
                return Messages.get().dataView_locationCountry();
                /*
                 * sdwis fields
                 */
            case SampleWebMeta.SDWIS_PWS_ID:
                return Messages.get().pws_id();
            case SampleWebMeta.PWS_NAME:
                return Messages.get().sampleSDWIS_pwsName();
            case SampleWebMeta.SDWIS_STATE_LAB_ID:
                return Messages.get().sampleSDWIS_stateLabNo();
            case SampleWebMeta.SDWIS_FACILITY_ID:
                return Messages.get().sampleSDWIS_facilityId();
            case SampleWebMeta.SDWIS_SAMPLE_TYPE_ID:
                return Messages.get().sampleSDWIS_sampleType();
            case SampleWebMeta.SDWIS_SAMPLE_CATEGORY_ID:
                return Messages.get().sampleSDWIS_category();
            case SampleWebMeta.SDWIS_SAMPLE_POINT_ID:
                return Messages.get().sampleSDWIS_samplePtId();
            case SampleWebMeta.SDWIS_LOCATION:
                return Messages.get().sampleSDWIS_location();
            case SampleWebMeta.SDWIS_PRIORITY:
                return Messages.get().gen_priority();
            case SampleWebMeta.SDWIS_COLLECTOR_HEADER:
                return Messages.get().sampleSDWIS_collector();
                /*
                 * clinical fields
                 */
            case SampleWebMeta.CLIN_PATIENT_ID:
                return Messages.get().dataView_patientId();
            case SampleWebMeta.CLIN_PATIENT_LAST_NAME_HEADER:
                return Messages.get().dataView_patientLastName();
            case SampleWebMeta.CLIN_PATIENT_FIRST_NAME_HEADER:
                return Messages.get().dataView_patientFirstName();
            case SampleWebMeta.CLIN_PATIENT_BIRTH_DATE:
                return Messages.get().dataView_patientBirthDate();
            case SampleWebMeta.CLIN_PATIENT_NATIONAL_ID:
                return Messages.get().dataView_patientNationalId();
            case SampleWebMeta.CLIN_PATIENT_ADDR_MULTIPLE_UNIT:
                return Messages.get().dataView_patientAptSuite();
            case SampleWebMeta.CLIN_PATIENT_ADDR_STREET_ADDRESS:
                return Messages.get().dataView_patientAddress();
            case SampleWebMeta.CLIN_PATIENT_ADDR_CITY:
                return Messages.get().dataView_patientCity();
            case SampleWebMeta.CLIN_PATIENT_ADDR_STATE:
                return Messages.get().dataView_patientState();
            case SampleWebMeta.CLIN_PATIENT_ADDR_ZIP_CODE:
                return Messages.get().dataView_patientZipcode();
            case SampleWebMeta.CLIN_PATIENT_ADDR_HOME_PHONE:
                return Messages.get().dataView_patientPhone();
            case SampleWebMeta.CLIN_PATIENT_GENDER_ID:
                return Messages.get().dataView_patientGender();
            case SampleWebMeta.CLIN_PATIENT_RACE_ID:
                return Messages.get().patient_race();
            case SampleWebMeta.CLIN_PATIENT_ETHNICITY_ID:
                return Messages.get().patient_ethnicity();
            case SampleWebMeta.CLIN_PROVIDER_LAST_NAME:
                return Messages.get().provider_lastName();
            case SampleWebMeta.CLIN_PROVIDER_FIRST_NAME:
                return Messages.get().provider_firstName();
            case SampleWebMeta.CLIN_PROVIDER_PHONE:
                return Messages.get().dataView_providerPhone();
                /*
                 * neonatal fields
                 */
            case SampleWebMeta.NEO_PATIENT_ID:
                return Messages.get().dataView_patientId();
            case SampleWebMeta.NEO_PATIENT_LAST_NAME:
                return Messages.get().dataView_patientLastName();
            case SampleWebMeta.NEO_PATIENT_FIRST_NAME:
                return Messages.get().dataView_patientFirstName();
            case SampleWebMeta.NEO_PATIENT_BIRTH_DATE:
                return Messages.get().dataView_patientBirthDate();
            case SampleWebMeta.NEO_PATIENT_ADDR_MULTIPLE_UNIT:
                return Messages.get().dataView_patientAptSuite();
            case SampleWebMeta.NEO_PATIENT_ADDR_STREET_ADDRESS:
                return Messages.get().dataView_patientAddress();
            case SampleWebMeta.NEO_PATIENT_ADDR_CITY:
                return Messages.get().dataView_patientCity();
            case SampleWebMeta.NEO_PATIENT_ADDR_STATE:
                return Messages.get().dataView_patientState();
            case SampleWebMeta.NEO_PATIENT_ADDR_ZIP_CODE:
                return Messages.get().dataView_patientZipcode();
            case SampleWebMeta.NEO_PATIENT_GENDER_ID:
                return Messages.get().dataView_patientGender();
            case SampleWebMeta.NEO_PATIENT_RACE_ID:
                return Messages.get().patient_race();
            case SampleWebMeta.NEO_PATIENT_ETHNICITY_ID:
                return Messages.get().patient_ethnicity();
            case SampleWebMeta.NEO_IS_NICU:
                return Messages.get().sampleNeonatal_nicu();
            case SampleWebMeta.NEO_BIRTH_ORDER:
                return Messages.get().sampleNeonatal_birthOrder();
            case SampleWebMeta.NEO_GESTATIONAL_AGE:
                return Messages.get().sampleNeonatal_gestAge();
            case SampleWebMeta.NEO_FEEDING_ID:
                return Messages.get().sampleNeonatal_feeding();
            case SampleWebMeta.NEO_WEIGHT:
                return Messages.get().sampleNeonatal_weight();
            case SampleWebMeta.NEO_IS_TRANSFUSED:
                return Messages.get().sampleNeonatal_transfused();
            case SampleWebMeta.NEO_TRANSFUSION_DATE:
                return Messages.get().sampleNeonatal_transDate();
            case SampleWebMeta.NEO_IS_REPEAT:
                return Messages.get().gen_repeat();
            case SampleWebMeta.NEO_COLLECTION_AGE:
                return Messages.get().sampleNeonatal_collectAge();
            case SampleWebMeta.NEO_IS_COLLECTION_VALID:
                return Messages.get().sampleNeonatal_collectValid();
            case SampleWebMeta.NEO_FORM_NUMBER:
                return Messages.get().gen_barcode();
            case SampleWebMeta.NEO_NEXT_OF_KIN_ID:
                return Messages.get().dataView_nextOfKinId();
            case SampleWebMeta.NEO_NEXT_OF_KIN_LAST_NAME:
                return Messages.get().dataView_nextOfKinLastName();
            case SampleWebMeta.NEO_NEXT_OF_KIN_MIDDLE_NAME:
                return Messages.get().dataView_nextOfKinMaidenName();
            case SampleWebMeta.NEO_NEXT_OF_KIN_FIRST_NAME:
                return Messages.get().dataView_nextOfKinFirstName();
            case SampleWebMeta.NEO_NEXT_OF_KIN_RELATION_ID:
                return Messages.get().gen_relation();
            case SampleWebMeta.NEO_NEXT_OF_KIN_BIRTH_DATE:
                return Messages.get().dataView_nextOfKinBirthDate();
            case SampleWebMeta.NEO_NEXT_OF_KIN_NATIONAL_ID:
                return Messages.get().dataView_nextOfKinNationalId();
            case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT:
                return Messages.get().dataView_nextOfKinAptSuite();
            case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS:
                return Messages.get().dataView_nextOfKinAddress();
            case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_CITY:
                return Messages.get().dataView_nextOfKinCity();
            case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STATE:
                return Messages.get().dataView_nextOfKinState();
            case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_ZIP_CODE:
                return Messages.get().dataView_nextOfKinZipcode();
            case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_HOME_PHONE:
                return Messages.get().dataView_nextOfKinPhone();
            case SampleWebMeta.NEO_NEXT_OF_KIN_GENDER_ID:
                return Messages.get().dataView_nextOfKinGender();
            case SampleWebMeta.NEO_NEXT_OF_KIN_RACE_ID:
                return Messages.get().patient_race();
            case SampleWebMeta.NEO_NEXT_OF_KIN_ETHNICITY_ID:
                return Messages.get().patient_ethnicity();
            case SampleWebMeta.NEO_PROVIDER_LAST_NAME:
                return Messages.get().provider_lastName();
            case SampleWebMeta.NEO_PROVIDER_FIRST_NAME:
                return Messages.get().provider_firstName();
                /*
                 * pt fields
                 */
            case SampleWebMeta.PT_PT_PROVIDER_ID:
                return Messages.get().provider_provider();
            case SampleWebMeta.PT_SERIES:
                return Messages.get().gen_series();
            case SampleWebMeta.PT_DUE_DATE:
                return Messages.get().gen_due();
            case SampleWebMeta.RECEIVED_BY_ID:
                return Messages.get().gen_receivedBy();
            default:
                return null;
        }
    }

    /**
     * Moves the column at "oldIndex" to "newIndex" in the list of columns shown
     * in the report
     */
    private void moveColumn(int oldIndex, int newIndex) {
        String column;

        column = data.getColumns().remove(oldIndex);

        if (newIndex >= data.getColumns().size())
            data.getColumns().add(column);
        else
            data.getColumns().add(newIndex, column);
    }
}