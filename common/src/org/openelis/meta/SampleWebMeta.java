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
package org.openelis.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class SampleWebMeta implements Meta, MetaMap {

    public static final String     ID = "_sample.id",
                    DOMAIN = "_sample.domain",
                    ACCESSION_NUMBER = "_sample.accessionNumber",
                    ACCESSION_NUMBER_FROM = "_display.accessionNumberFrom",
                    ACCESSION_NUMBER_TO = "_display.accessionNumberTo",
                    REVISION = "_sample.revision",
                    ENTERED_DATE = "_sample.enteredDate",
                    ENTERED_DATE_FROM = "_sample.enteredDateFrom",
                    ENTERED_DATE_TO = "_sample.enteredDateTo",
                    RECEIVED_DATE = "_sample.receivedDate",
                    RECEIVED_DATE_FROM = "_sample.receivedDateFrom",
                    RECEIVED_DATE_TO = "_sample.receivedDateTo",
                    RECEIVED_BY_ID = "_sample.receivedById",
                    COLLECTION_DATE = "_sample.collectionDate",
                    COLLECTION_DATE_FROM = "_display.collectionDateFrom",
                    COLLECTION_DATE_TO = "_display.collectionDateTo",
                    COLLECTION_TIME = "_sample.collectionTime",
                    STATUS_ID = "_sample.statusId",
                    CLIENT_REFERENCE = "_sample.clientReference",
                    CLIENT_REFERENCE_HEADER = "_sample.clientReferenceHeader",
                    RELEASED_DATE = "_sample.releasedDate",
                    RELEASED_DATE_FROM = "_display.releasedDateFrom",
                    RELEASED_DATE_TO = "_display.releasedDateTo",

                    // sample environmental
                    ENV_ID = "_sampleEnvironmental.id",
                    ENV_SAMPLE_ID = "_sampleEnvironmental.sampleId",
                    ENV_IS_HAZARDOUS = "_sampleEnvironmental.isHazardous",
                    ENV_PRIORITY = "_sampleEnvironmental.priority",
                    ENV_DESCRIPTION = "_sampleEnvironmental.description",
                    ENV_COLLECTOR = "_sampleEnvironmental.collector",
                    ENV_COLLECTOR_HEADER = "_sampleEnvironmental.collectorHeader",
                    ENV_COLLECTOR_PHONE = "_sampleEnvironmental.collectorPhone",
                    ENV_LOCATION = "_sampleEnvironmental.location",
                    ENV_LOCATION_HEADER = "_sampleEnvironmental.locationHeader",
                    ENV_LOCATION_ADDRESS_ID = "_sampleEnvironmental.locationAddressId",

                    LOCATION_ADDR_MULTIPLE_UNIT = "_locationAddress.multipleUnit",
                    LOCATION_ADDR_STREET_ADDRESS = "_locationAddress.streetAddress",
                    LOCATION_ADDR_CITY = "_locationAddress.city",
                    LOCATION_ADDR_STATE = "_locationAddress.state",
                    LOCATION_ADDR_ZIP_CODE = "_locationAddress.zipCode",
                    LOCATION_ADDR_WORK_PHONE = "_locationAddress.workPhone",
                    LOCATION_ADDR_HOME_PHONE = "_locationAddress.homePhone",
                    LOCATION_ADDR_CELL_PHONE = "_locationAddress.cellPhone",
                    LOCATION_ADDR_FAX_PHONE = "_locationAddress.faxPhone",
                    LOCATION_ADDR_EMAIL = "_locationAddress.email",
                    LOCATION_ADDR_COUNTRY = "_locationAddress.country",

                    // sample private well
                    WELL_ID = "_samplePrivateWell.id",
                    WELL_SAMPLE_ID = "_samplePrivateWell.sampleId",
                    WELL_ORGANIZATION_ID = "_samplePrivateWell.organizationId",
                    WELL_ORGANIZATION_ADDRESS_ID = "_wellOrganization.addressId",
                    WELL_ORGANIZATION_NAME = "_wellOrganization.name",
                    WELL_ORGANIZATION_ADDR_ID = "_wellOrganizationAddress.id",
                    WELL_ORGANIZATION_ADDR_CITY = "_wellOrganizationAddress.city",
                    WELL_LOCATION = "_samplePrivateWell.location",
                    WELL_LOCATION_ADDRESS_ID = "_samplePrivateWell.locationAddressId",
                    WELL_OWNER = "_samplePrivateWell.owner",
                    WELL_COLLECTOR = "_samplePrivateWell.collector",
                    WELL_WELL_NUMBER = "_samplePrivateWell.wellNumber",
                    WELL_REPORT_TO_ADDR_CITY = "_privateWellReportToAddress.city",
                    WELL_REPORT_TO_ADDR_FAX_PHONE = "_privateWellReportToAddress.faxPhone",
                    WELL_LOCATION_ADDR_WORK_PHONE = "_wellLocationAddress.workPhone",

                    WELL_LOCATION_ADDR_ID = "_wellLocationAddress.id",
                    WELL_LOCATION_ADDR_MULTIPLE_UNIT = "_wellLocationAddress.multipleUnit",
                    WELL_LOCATION_ADDR_STREET_ADDRESS = "_wellLocationAddress.streetAddress",
                    WELL_LOCATION_ADDR_CITY = "_wellLocationAddress.city",
                    WELL_LOCATION_ADDR_STATE = "_wellLocationAddress.state",
                    WELL_LOCATION_ADDR_ZIP_CODE = "_wellLocationAddress.zipCode",

                    // sample sdwis
                    SDWIS_ID = "_sampleSDWIS.id",
                    SDWIS_SAMPLE_ID = "_sampleSDWIS.sampleId",
                    SDWIS_PWS_ID = "_sampleSDWIS.pwsId",
                    SDWIS_STATE_LAB_ID = "_sampleSDWIS.stateLabId",
                    SDWIS_FACILITY_ID = "_sampleSDWIS.facilityId",
                    SDWIS_SAMPLE_TYPE_ID = "_sampleSDWIS.sampleTypeId",
                    SDWIS_SAMPLE_CATEGORY_ID = "_sampleSDWIS.sampleCategoryId",
                    SDWIS_SAMPLE_POINT_ID = "_sampleSDWIS.samplePointId",
                    SDWIS_PRIORITY = "_sampleSDWIS.priority",
                    SDWIS_LOCATION = "_sampleSDWIS.location",
                    SDWIS_COLLECTOR = "_sampleSDWIS.collector",

                    PWS_NUMBER0 = "_pws.number0",
                    PWS_ID = "_pws.id",
                    PWS_NAME = "_pws.name",

                    // sample neonatal
                    NEO_PATIENT_ID = "_sampleNeonatal.patientId",
                    NEO_BIRTH_ORDER = "_sampleNeonatal.birthOrder",
                    NEO_GESTATIONAL_AGE = "_sampleNeonatal.gestationalAge",
                    NEO_NEXT_OF_KIN_ID = "_sampleNeonatal.nextOfKinId",
                    NEO_NEXT_OF_KIN_RELATION_ID = "_sampleNeonatal.nextOfKinRelationId",
                    NEO_IS_REPEAT = "_sampleNeonatal.isRepeat",
                    NEO_IS_NICU = "_sampleNeonatal.isNicu",
                    NEO_FEEDING_ID = "_sampleNeonatal.feedingId",
                    NEO_WEIGHT = "_sampleNeonatal.weight",
                    NEO_IS_TRANSFUSED = "_sampleNeonatal.isTransfused",
                    NEO_TRANSFUSION_DATE = "_sampleNeonatal.transfusionDate",
                    NEO_IS_COLLECTION_VALID = "_sampleNeonatal.isCollectionValid",
                    NEO_COLLECTION_AGE = "_sampleNeonatal.collectionAge",
                    NEO_FORM_NUMBER = "_sampleNeonatal.formNumber",

                    NEO_PATIENT_LAST_NAME = "_neonatalPatient.lastName",
                    NEO_PATIENT_FIRST_NAME = "_neonatalPatient.firstName",
                    NEO_PATIENT_MIDDLE_NAME = "_neonatalPatient.middleName",
                    NEO_PATIENT_ADDRESS_ID = "_neonatalPatient.addressId",
                    NEO_PATIENT_BIRTH_DATE = "_neonatalPatient.birthDate",
                    NEO_PATIENT_BIRTH_TIME = "_neonatalPatient.birthTime",
                    NEO_PATIENT_GENDER_ID = "_neonatalPatient.genderId",
                    NEO_PATIENT_RACE_ID = "_neonatalPatient.raceId",
                    NEO_PATIENT_ETHNICITY_ID = "_neonatalPatient.ethnicityId",
                    NEO_PATIENT_NATIONAL_ID = "_neonatalPatient.nationalId",

                    NEO_PATIENT_ADDR_MULTIPLE_UNIT = "_neonatalPatientAddress.multipleUnit",
                    NEO_PATIENT_ADDR_STREET_ADDRESS = "_neonatalPatientAddress.streetAddress",
                    NEO_PATIENT_ADDR_CITY = "_neonatalPatientAddress.city",
                    NEO_PATIENT_ADDR_STATE = "_neonatalPatientAddress.state",
                    NEO_PATIENT_ADDR_ZIP_CODE = "_neonatalPatientAddress.zipCode",

                    NEO_NEXT_OF_KIN_LAST_NAME = "_neonatalNextOfKin.lastName",
                    NEO_NEXT_OF_KIN_FIRST_NAME = "_neonatalNextOfKin.firstName",
                    NEO_NEXT_OF_KIN_MIDDLE_NAME = "_neonatalNextOfKin.middleName",
                    NEO_NEXT_OF_KIN_BIRTH_DATE = "_neonatalNextOfKin.birthDate",
                    NEO_NEXT_OF_KIN_GENDER_ID = "_neonatalNextOfKin.genderId",
                    NEO_NEXT_OF_KIN_RACE_ID = "_neonatalNextOfKin.raceId",
                    NEO_NEXT_OF_KIN_ETHNICITY_ID = "_neonatalNextOfKin.ethnicityId",
                    NEO_NEXT_OF_KIN_NATIONAL_ID = "_neonatalNextOfKin.nationalId",

                    NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT = "_neonatalNextOfKinAddress.multipleUnit",
                    NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS = "_neonatalNextOfKinAddress.streetAddress",
                    NEO_NEXT_OF_KIN_ADDR_CITY = "_neonatalNextOfKinAddress.city",
                    NEO_NEXT_OF_KIN_ADDR_STATE = "_neonatalNextOfKinAddress.state",
                    NEO_NEXT_OF_KIN_ADDR_ZIP_CODE = "_neonatalNextOfKinAddress.zipCode",
                    NEO_NEXT_OF_KIN_ADDR_HOME_PHONE = "_neonatalNextOfKinAddress.homePhone",

                    // sample clinical
                    CLIN_PATIENT_ID = "_clinicalPatient.id",
                    CLIN_PROVIDER_PHONE = "_sampleClinical.providerPhone",
                    CLIN_PATIENT_LAST_NAME = "_clinicalPatient.lastName",
                    CLIN_PATIENT_FIRST_NAME = "_clinicalPatient.firstName",
                    CLIN_PATIENT_BIRTH_DATE = "_clinicalPatient.birthDate",
                    CLIN_PATIENT_BIRTH_DATE_FROM = "display.patientBirthDateFrom",
                    CLIN_PATIENT_BIRTH_DATE_TO = "display.patientBirthDateTo",
                    CLIN_PATIENT_BIRTH_TIME = "_clinicalPatient.birthTime",
                    CLIN_PATIENT_GENDER_ID = "_clinicalPatient.genderId",
                    CLIN_PATIENT_RACE_ID = "_clinicalPatient.raceId",
                    CLIN_PATIENT_ETHNICITY_ID = "_clinicalPatient.ethnicityId",
                    CLIN_PATIENT_NATIONAL_ID = "_clinicalPatient.nationalId",

                    CLIN_PATIENT_ADDR_MULTIPLE_UNIT = "_clinicalPatientAddress.multipleUnit",
                    CLIN_PATIENT_ADDR_STREET_ADDRESS = "_clinicalPatientAddress.streetAddress",
                    CLIN_PATIENT_ADDR_CITY = "_clinicalPatientAddress.city",
                    CLIN_PATIENT_ADDR_STATE = "_clinicalPatientAddress.state",
                    CLIN_PATIENT_ADDR_ZIP_CODE = "_clinicalPatientAddress.zipCode",
                    CLIN_PATIENT_ADDR_HOME_PHONE = "_clinicalPatientAddress.homePhone",

                    // sample pt
                    PT_ID = "_samplePT.id", PT_SAMPLE_ID = "_samplePT.sampleId",
                    PT_PT_PROVIDER_ID = "_samplePT.ptProviderId", PT_SERIES = "_samplePT.series",
                    PT_DUE_DATE = "_samplePT.dueDate",
                    PT_ADDITIONAL_DOMAIN = "_samplePT.additionalDomain",

                    LOCATION_ADDR_CITY_HEADER = "_locationAddress.cityHeader",

                    ITEM_ID = "_sampleItem.id", ITEM_SAMPLE_ID = "_sampleItem.sampleId",
                    ITEM_ITEM_SEQUENCE = "_sampleItem.itemSequence",
                    ITEM_SAMPLE_ITEM_ID = "_sampleItem.sampleItemId",
                    ITEM_TYPE_OF_SAMPLE_ID = "_sampleItem.typeOfSampleId",
                    ITEM_SOURCE_OF_SAMPLE_ID = "_sampleItem.sourceOfSampleId",
                    ITEM_SOURCE_OTHER = "_sampleItem.sourceOther",
                    ITEM_CONTAINER_ID = "_sampleItem.containerId",
                    ITEM_CONTAINER_REFERENCE = "_sampleItem.containerReference",

                    ANALYSIS_ID = "_analysis.id",
                    ANALYSIS_SAMPLE_ITEM_ID = "_analysis.sampleItemId",
                    ANALYSIS_REVISION = "_analysis.revision",
                    ANALYSIS_TYPE_ID = "_analysis.typeId",
                    ANALYSIS_IS_REPORTABLE = "_analysis.isReportable",
                    ANALYSIS_IS_REPORTABLE_HEADER = "_analysis.isReportableHeader",
                    ANALYSIS_UNIT_OF_MEASURE_ID = "_analysis.unitOfMeasureId",
                    ANALYSIS_STATUS_ID = "_analysis.statusId",
                    ANALYSIS_STATUS_ID_HEADER = "_analysis.statusIdHeader",
                    ANALYSIS_STARTED_DATE = "_analysis.startedDate",
                    ANALYSIS_COMPLETED_DATE = "_analysis.completedDate",
                    ANALYSIS_COMPLETED_DATE_FROM = "_analysis.completedDateFrom",
                    ANALYSIS_COMPLETED_DATE_TO = "_analysis.completedDateTo",
                    ANALYSIS_COMPLETED_BY = "_analysis.completedBy",
                    ANALYSIS_RELEASED_DATE = "_analysis.releasedDate",
                    ANALYSIS_RELEASED_DATE_FROM = "_analysis.releasedDateFrom",
                    ANALYSIS_RELEASED_DATE_TO = "_analysis.releasedDateTo",
                    ANALYSIS_RELEASED_BY = "_analysis.releasedBy",
                    ANALYSIS_PRINTED_DATE = "_analysis.printedDate",

                    RESULT_ID = "_result.id", RESULT_ANALYSIS_ID = "_result.analysisId",
                    RESULT_TEST_ANALYTE_ID = "_result.testAnalyteId",
                    RESULT_TEST_RESULT_ID = "_result.testResultId",
                    RESULT_IS_COLUMN = "_result.isColumn", RESULT_SORT_ORDER = "_result.sortOrder",
                    RESULT_IS_REPORTABLE = "_result.isReportable",
                    RESULT_ANALYTE_ID = "_result.analyteId", RESULT_TYPE_ID = "_result.typeId",
                    RESULT_VALUE = "_result.value",

                    ANALYSISQA_QAEVENT_ID = "_analysisQaevent.qaeventId",

                    ANALYSISSUBQA_ID = "_aQaevent.id", ANALYSISSUBQA_NAME = "_aQaevent.name",

                    SAMPLE_ORG_ID = "_sampleOrganization.id",
                    SAMPLE_ORG_SAMPLE_ID = "_sampleOrganization.sampleId",
                    SAMPLE_ORG_ORGANIZATION_ID = "_sampleOrganization.organizationId",
                    SAMPLE_ORG_ATTENTION = "_sampleOrganization.attention",
                    SAMPLE_ORG_TYPE_ID = "_sampleOrganization.typeId",

                    ORG_ID = "_organization.id", ORG_NAME = "_organization.name",

                    ADDR_MULTIPLE_UNIT = "_address.multipleUnit",
                    ADDR_STREET_ADDRESS = "_address.streetAddress", ADDR_CITY = "_address.city",
                    ADDR_STATE = "_address.state", ADDR_ZIP_CODE = "_address.zipCode",
                    ADDR_COUNTRY = "_address.country",

                    SAMPLE_PROJECT_ID = "_sampleProject.id",
                    SAMPLE_PROJECT_SAMPLE_ID = "_sampleProject.sampleId",
                    SAMPLE_PROJECT_PROJECT_ID = "_sampleProject.projectId",
                    SAMPLE_PROJECT_IS_PERMANENT = "_sampleProject.isPermanent",

                    PROJECT_ID = "_project.id", PROJECT_ID_HEADER = "_project.idHeader",
                    PROJECT_NAME = "_project.name", PROJECT_DESCRIPTION = "_project.description",

                    NEO_PROVIDER_LAST_NAME = "_neonatalProvider.lastName",
                    NEO_PROVIDER_FIRST_NAME = "_neonatalProvider.firstName",

                    CLIN_PROVIDER_LAST_NAME = "_clinicalProvider.lastName",
                    CLIN_PROVIDER_FIRST_NAME = "_clinicalProvider.firstName",

                    AUX_DATA_ID = "_auxData.id", AUX_DATA_AUX_FIELD_ID = "_auxData.auxFieldId",
                    AUX_DATA_REFERENCE_ID = "_auxData.referenceId",
                    AUX_DATA_REFERENCE_TABLE_ID = "_auxData.referenceTableId",
                    AUX_DATA_IS_REPORTABLE = "_auxData.isReportable",
                    AUX_DATA_TYPE_ID = "_auxData.typeId", AUX_DATA_VALUE = "_auxData.value",

                    ANALYSIS_TEST_NAME = "_test.name", ANALYSIS_METHOD_NAME = "_method.name",
                    ANALYSIS_TEST_NAME_HEADER = "_test.nameHeader",
                    ANALYSIS_METHOD_NAME_HEADER = "_method.nameHeader",
                    ANALYSIS_SECTION_NAME = "_section.name",

                    RESULT_ANALYTE_NAME = "_result.analyte.name",
                    RESULT_TEST_ANALYTE_ROW_GROUP = "_result.testAnalyte.rowGroup",
                    RESULT_TEST_ANALYTE_TYPE_ID = "_result.testAnalyte.typeId",
                    RESULT_TEST_ANALYTE_RESULT_GROUP = "_result.testAnalyte.resultGroup",

                    AUX_DATA_FIELD_ID = "_auxField.id",
                    AUX_DATA_FIELD_ANALYTE_ID = "_auxField.analyteId",
                    AUX_DATA_FIELD_ANALYTE_NAME = "_auxField.analyte.name";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID,
                                                  DOMAIN,
                                                  ACCESSION_NUMBER,
                                                  ACCESSION_NUMBER_FROM,
                                                  ACCESSION_NUMBER_TO,
                                                  REVISION,
                                                  ENTERED_DATE,
                                                  ENTERED_DATE_FROM,
                                                  ENTERED_DATE_TO,
                                                  RECEIVED_DATE,
                                                  RECEIVED_DATE_FROM,
                                                  RECEIVED_DATE_TO,
                                                  RECEIVED_BY_ID,
                                                  COLLECTION_DATE,
                                                  COLLECTION_DATE_FROM,
                                                  COLLECTION_DATE_TO,
                                                  COLLECTION_TIME,
                                                  STATUS_ID,
                                                  CLIENT_REFERENCE,
                                                  CLIENT_REFERENCE_HEADER,
                                                  RELEASED_DATE,
                                                  RELEASED_DATE_FROM,
                                                  RELEASED_DATE_TO,
                                                  CLIN_PATIENT_LAST_NAME,
                                                  CLIN_PATIENT_FIRST_NAME,
                                                  CLIN_PATIENT_BIRTH_DATE,
                                                  CLIN_PATIENT_BIRTH_DATE_FROM,
                                                  CLIN_PATIENT_BIRTH_DATE_TO,
                                                  CLIN_PATIENT_BIRTH_TIME,
                                                  ENV_ID,
                                                  ENV_SAMPLE_ID,
                                                  ENV_IS_HAZARDOUS,
                                                  ENV_PRIORITY,
                                                  ENV_DESCRIPTION,
                                                  ENV_COLLECTOR,
                                                  ENV_COLLECTOR_HEADER,
                                                  ENV_COLLECTOR_PHONE,
                                                  ENV_LOCATION,
                                                  ENV_LOCATION_HEADER,
                                                  ENV_LOCATION_ADDRESS_ID,
                                                  ENV_LOCATION_ADDRESS_ID,
                                                  LOCATION_ADDR_MULTIPLE_UNIT,
                                                  LOCATION_ADDR_STREET_ADDRESS,
                                                  LOCATION_ADDR_CITY,
                                                  LOCATION_ADDR_STATE,
                                                  LOCATION_ADDR_ZIP_CODE,
                                                  LOCATION_ADDR_WORK_PHONE,
                                                  LOCATION_ADDR_HOME_PHONE,
                                                  LOCATION_ADDR_CELL_PHONE,
                                                  LOCATION_ADDR_FAX_PHONE,
                                                  LOCATION_ADDR_EMAIL,
                                                  LOCATION_ADDR_COUNTRY,
                                                  WELL_ID,
                                                  WELL_SAMPLE_ID,
                                                  WELL_ORGANIZATION_ID,
                                                  WELL_ORGANIZATION_ADDRESS_ID,
                                                  WELL_ORGANIZATION_NAME,
                                                  WELL_ORGANIZATION_ADDR_ID,
                                                  WELL_ORGANIZATION_ADDR_CITY,
                                                  WELL_LOCATION,
                                                  WELL_LOCATION_ADDRESS_ID,
                                                  WELL_OWNER,
                                                  WELL_COLLECTOR,
                                                  WELL_WELL_NUMBER,
                                                  WELL_REPORT_TO_ADDR_CITY,
                                                  WELL_REPORT_TO_ADDR_FAX_PHONE,
                                                  WELL_LOCATION_ADDR_WORK_PHONE,
                                                  WELL_LOCATION_ADDR_MULTIPLE_UNIT,
                                                  WELL_LOCATION_ADDR_STREET_ADDRESS,
                                                  WELL_LOCATION_ADDR_CITY,
                                                  WELL_LOCATION_ADDR_STATE,
                                                  WELL_LOCATION_ADDR_ZIP_CODE,
                                                  SDWIS_ID,
                                                  SDWIS_SAMPLE_ID,
                                                  SDWIS_PWS_ID,
                                                  SDWIS_STATE_LAB_ID,
                                                  SDWIS_FACILITY_ID,
                                                  SDWIS_SAMPLE_TYPE_ID,
                                                  SDWIS_SAMPLE_CATEGORY_ID,
                                                  SDWIS_SAMPLE_POINT_ID,
                                                  SDWIS_PRIORITY,
                                                  SDWIS_LOCATION,
                                                  SDWIS_COLLECTOR,
                                                  PWS_NUMBER0,
                                                  PWS_ID,
                                                  PWS_NAME,
                                                  NEO_PATIENT_ID,
                                                  NEO_BIRTH_ORDER,
                                                  NEO_GESTATIONAL_AGE,
                                                  NEO_NEXT_OF_KIN_ID,
                                                  NEO_NEXT_OF_KIN_RELATION_ID,
                                                  NEO_IS_REPEAT,
                                                  NEO_IS_NICU,
                                                  NEO_FEEDING_ID,
                                                  NEO_WEIGHT,
                                                  NEO_IS_TRANSFUSED,
                                                  NEO_TRANSFUSION_DATE,
                                                  NEO_IS_COLLECTION_VALID,
                                                  NEO_COLLECTION_AGE,
                                                  NEO_FORM_NUMBER,
                                                  NEO_PATIENT_LAST_NAME,
                                                  NEO_PATIENT_FIRST_NAME,
                                                  NEO_PATIENT_MIDDLE_NAME,
                                                  NEO_PATIENT_ADDRESS_ID,
                                                  NEO_PATIENT_BIRTH_DATE,
                                                  NEO_PATIENT_BIRTH_TIME,
                                                  NEO_PATIENT_GENDER_ID,
                                                  NEO_PATIENT_RACE_ID,
                                                  NEO_PATIENT_ETHNICITY_ID,
                                                  NEO_PATIENT_NATIONAL_ID,
                                                  NEO_PATIENT_ADDR_MULTIPLE_UNIT,
                                                  NEO_PATIENT_ADDR_STREET_ADDRESS,
                                                  NEO_PATIENT_ADDR_CITY,
                                                  NEO_PATIENT_ADDR_STATE,
                                                  NEO_PATIENT_ADDR_ZIP_CODE,
                                                  NEO_NEXT_OF_KIN_LAST_NAME,
                                                  NEO_NEXT_OF_KIN_FIRST_NAME,
                                                  NEO_NEXT_OF_KIN_MIDDLE_NAME,
                                                  NEO_NEXT_OF_KIN_BIRTH_DATE,
                                                  NEO_NEXT_OF_KIN_GENDER_ID,
                                                  NEO_NEXT_OF_KIN_RACE_ID,
                                                  NEO_NEXT_OF_KIN_ETHNICITY_ID,
                                                  NEO_NEXT_OF_KIN_NATIONAL_ID,
                                                  NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT,
                                                  NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS,
                                                  NEO_NEXT_OF_KIN_ADDR_CITY,
                                                  NEO_NEXT_OF_KIN_ADDR_STATE,
                                                  NEO_NEXT_OF_KIN_ADDR_ZIP_CODE,
                                                  NEO_NEXT_OF_KIN_ADDR_HOME_PHONE,
                                                  CLIN_PATIENT_ID,
                                                  CLIN_PROVIDER_PHONE,
                                                  CLIN_PATIENT_LAST_NAME,
                                                  CLIN_PATIENT_FIRST_NAME,
                                                  CLIN_PATIENT_BIRTH_DATE,
                                                  CLIN_PATIENT_BIRTH_DATE_FROM,
                                                  CLIN_PATIENT_BIRTH_DATE_TO,
                                                  CLIN_PATIENT_BIRTH_TIME,
                                                  CLIN_PATIENT_GENDER_ID,
                                                  CLIN_PATIENT_RACE_ID,
                                                  CLIN_PATIENT_ETHNICITY_ID,
                                                  CLIN_PATIENT_NATIONAL_ID,
                                                  CLIN_PATIENT_ADDR_MULTIPLE_UNIT,
                                                  CLIN_PATIENT_ADDR_STREET_ADDRESS,
                                                  CLIN_PATIENT_ADDR_CITY,
                                                  CLIN_PATIENT_ADDR_STATE,
                                                  CLIN_PATIENT_ADDR_ZIP_CODE,
                                                  CLIN_PATIENT_ADDR_HOME_PHONE,
                                                  PT_ID,
                                                  PT_SAMPLE_ID,
                                                  PT_PT_PROVIDER_ID,
                                                  PT_SERIES,
                                                  PT_DUE_DATE,
                                                  PT_ADDITIONAL_DOMAIN,
                                                  ITEM_ID,
                                                  ITEM_SAMPLE_ID,
                                                  ITEM_SAMPLE_ITEM_ID,
                                                  ITEM_ITEM_SEQUENCE,
                                                  ITEM_TYPE_OF_SAMPLE_ID,
                                                  ITEM_SOURCE_OF_SAMPLE_ID,
                                                  ITEM_SOURCE_OTHER,
                                                  ITEM_CONTAINER_ID,
                                                  ITEM_CONTAINER_REFERENCE,
                                                  ANALYSIS_ID,
                                                  ANALYSIS_SAMPLE_ITEM_ID,
                                                  ANALYSIS_REVISION,
                                                  ANALYSIS_TYPE_ID,
                                                  ANALYSIS_IS_REPORTABLE,
                                                  ANALYSIS_IS_REPORTABLE_HEADER,
                                                  ANALYSIS_UNIT_OF_MEASURE_ID,
                                                  ANALYSIS_STATUS_ID,
                                                  ANALYSIS_STATUS_ID_HEADER,
                                                  ANALYSIS_STARTED_DATE,
                                                  ANALYSIS_COMPLETED_DATE,
                                                  ANALYSIS_COMPLETED_DATE_FROM,
                                                  ANALYSIS_COMPLETED_DATE_TO,
                                                  ANALYSIS_COMPLETED_BY,
                                                  ANALYSIS_RELEASED_DATE,
                                                  ANALYSIS_RELEASED_DATE_FROM,
                                                  ANALYSIS_RELEASED_DATE_TO,
                                                  ANALYSIS_RELEASED_BY,
                                                  ANALYSIS_PRINTED_DATE,
                                                  RESULT_ID,
                                                  RESULT_ANALYSIS_ID,
                                                  RESULT_TEST_ANALYTE_ID,
                                                  RESULT_TEST_RESULT_ID,
                                                  RESULT_IS_COLUMN,
                                                  RESULT_SORT_ORDER,
                                                  RESULT_IS_REPORTABLE,
                                                  RESULT_ANALYTE_ID,
                                                  RESULT_TYPE_ID,
                                                  RESULT_VALUE,
                                                  ANALYSISQA_QAEVENT_ID,
                                                  ANALYSISSUBQA_ID,
                                                  ANALYSISSUBQA_NAME,
                                                  SAMPLE_ORG_ID,
                                                  SAMPLE_ORG_SAMPLE_ID,
                                                  SAMPLE_ORG_ORGANIZATION_ID,
                                                  SAMPLE_ORG_ATTENTION,
                                                  SAMPLE_ORG_TYPE_ID,
                                                  ORG_ID,
                                                  ORG_NAME,
                                                  ADDR_MULTIPLE_UNIT,
                                                  ADDR_STREET_ADDRESS,
                                                  ADDR_CITY,
                                                  ADDR_STATE,
                                                  ADDR_ZIP_CODE,
                                                  ADDR_COUNTRY,
                                                  SAMPLE_PROJECT_ID,
                                                  SAMPLE_PROJECT_SAMPLE_ID,
                                                  SAMPLE_PROJECT_PROJECT_ID,
                                                  PROJECT_ID_HEADER,
                                                  SAMPLE_PROJECT_IS_PERMANENT,
                                                  PROJECT_ID,
                                                  PROJECT_NAME,
                                                  PROJECT_DESCRIPTION,
                                                  NEO_PROVIDER_LAST_NAME,
                                                  NEO_PROVIDER_FIRST_NAME,
                                                  CLIN_PROVIDER_LAST_NAME,
                                                  CLIN_PROVIDER_FIRST_NAME,
                                                  AUX_DATA_ID,
                                                  AUX_DATA_AUX_FIELD_ID,
                                                  AUX_DATA_REFERENCE_ID,
                                                  AUX_DATA_REFERENCE_TABLE_ID,
                                                  AUX_DATA_IS_REPORTABLE,
                                                  AUX_DATA_TYPE_ID,
                                                  AUX_DATA_VALUE,
                                                  ANALYSIS_TEST_NAME,
                                                  ANALYSIS_METHOD_NAME,
                                                  ANALYSIS_TEST_NAME_HEADER,
                                                  ANALYSIS_METHOD_NAME_HEADER,
                                                  ANALYSIS_SECTION_NAME,
                                                  RESULT_ANALYTE_NAME,
                                                  RESULT_TEST_ANALYTE_ROW_GROUP,
                                                  RESULT_TEST_ANALYTE_TYPE_ID,
                                                  RESULT_TEST_ANALYTE_RESULT_GROUP,
                                                  AUX_DATA_FIELD_ID,
                                                  AUX_DATA_FIELD_ANALYTE_ID,
                                                  AUX_DATA_FIELD_ANALYTE_NAME));
    }

    public static String getId() {
        return ID;
    }

    public static String getDomain() {
        return DOMAIN;
    }

    public static String getAccessionNumber() {
        return ACCESSION_NUMBER;
    }

    public static String getAccessionNumberFrom() {
        return ACCESSION_NUMBER_FROM;
    }

    public static String getAccessionNumberTo() {
        return ACCESSION_NUMBER_TO;
    }

    public static String getRevision() {
        return REVISION;
    }

    public static String getEnteredDate() {
        return ENTERED_DATE;
    }

    public static String getEnteredDateFrom() {
        return ENTERED_DATE_FROM;
    }

    public static String getEnteredDateTo() {
        return ENTERED_DATE_TO;
    }

    public static String getReceivedDate() {
        return RECEIVED_DATE;
    }

    public static String getReceivedDateFrom() {
        return RECEIVED_DATE_FROM;
    }

    public static String getReceivedDateTo() {
        return RECEIVED_DATE_TO;
    }

    public static String getReceivedById() {
        return RECEIVED_BY_ID;
    }

    public static String getCollectionDate() {
        return COLLECTION_DATE;
    }

    public static String getCollectionDateFrom() {
        return COLLECTION_DATE_FROM;
    }

    public static String getCollectionDateTo() {
        return COLLECTION_DATE_TO;
    }

    public static String getCollectionTime() {
        return COLLECTION_TIME;
    }

    public static String getStatusId() {
        return STATUS_ID;
    }

    public static String getClientReference() {
        return CLIENT_REFERENCE;
    }

    public static String getClientReferenceHeader() {
        return CLIENT_REFERENCE_HEADER;
    }

    public static String getReleasedDate() {
        return RELEASED_DATE;
    }

    public static String getReleasedDateFrom() {
        return RELEASED_DATE_FROM;
    }

    public static String getReleasedDateTo() {
        return RELEASED_DATE_TO;
    }

    public static String getClinPatientLastName() {
        return CLIN_PATIENT_LAST_NAME;
    }

    public static String getClinPatientFirstName() {
        return CLIN_PATIENT_FIRST_NAME;
    }

    public static String getClinPatientBirthDate() {
        return CLIN_PATIENT_BIRTH_DATE;
    }

    public static String getClinPatientBirthDateFrom() {
        return CLIN_PATIENT_BIRTH_DATE_FROM;
    }

    public static String getClinPatientBirthDateTo() {
        return CLIN_PATIENT_BIRTH_DATE_TO;
    }

    public static String getClinPatientBirthTime() {
        return CLIN_PATIENT_BIRTH_TIME;
    }

    public static String getEnvLocationAddrId() {
        return ENV_LOCATION_ADDRESS_ID;
    }

    public static String getLocationAddrMultipleUnit() {
        return LOCATION_ADDR_MULTIPLE_UNIT;
    }
    
    public static String getLocationAddrStreetAddress() {
        return LOCATION_ADDR_STREET_ADDRESS;
    }
    
    public static String getLocationAddrCity() {
        return LOCATION_ADDR_CITY;
    }

    public static String getLocationAddrState() {
        return LOCATION_ADDR_STATE;
    }
    
    public static String getLocationAddrZipCode() {
        return LOCATION_ADDR_ZIP_CODE;
    }
    
    public static String getLocationAddrWorkPhone() {
        return LOCATION_ADDR_WORK_PHONE;
    }
    
    public static String getLocationAddrHomePhone() {
        return LOCATION_ADDR_HOME_PHONE;
    }
    
    public static String getLocationAddrCellPhone() {
        return LOCATION_ADDR_CELL_PHONE;
    }
    
    public static String getLocationAddrFaxPhone() {
        return LOCATION_ADDR_FAX_PHONE;
    }
    
    public static String getLocationAddrEmail() {
        return LOCATION_ADDR_EMAIL;
    }
    
    public static String getLocationAddrCountry() {
        return LOCATION_ADDR_COUNTRY;
    }

    public static String getLocationAddrCityHeader() {
        return LOCATION_ADDR_CITY_HEADER;
    }

    public static String getEnvId() {
        return ENV_ID;
    }

    public static String getEnvSampleId() {
        return ENV_SAMPLE_ID;
    }

    public static String getEnvIsHazardous() {
        return ENV_IS_HAZARDOUS;
    }

    public static String getEnvPriority() {
        return ENV_PRIORITY;
    }

    public static String getEnvDescription() {
        return ENV_DESCRIPTION;
    }

    public static String getEnvCollector() {
        return ENV_COLLECTOR;
    }

    public static String getEnvCollectorHeader() {
        return ENV_COLLECTOR_HEADER;
    }

    public static String getEnvCollectorPhone() {
        return ENV_COLLECTOR_PHONE;
    }

    public static String getEnvLocation() {
        return ENV_LOCATION;
    }

    public static String getEnvLocationHeader() {
        return ENV_LOCATION_HEADER;
    }

    public static String getEnvLocationAddressId() {
        return ENV_LOCATION_ADDRESS_ID;
    }

    public static String getWellId() {
        return WELL_ID;
    }

    public static String getWellSampleId() {
        return WELL_SAMPLE_ID;
    }

    public static String getWellOrganizationId() {
        return WELL_ORGANIZATION_ID;
    }

    public static String getWellOrganizationAddressId() {
        return WELL_ORGANIZATION_ADDRESS_ID;
    }

    public static String getWellOrganizationName() {
        return WELL_ORGANIZATION_NAME;
    }

    public static String getWellOrganizationAddrId() {
        return WELL_ORGANIZATION_ADDR_ID;
    }

    public static String getWellOrganizationAddrCity() {
        return WELL_ORGANIZATION_ADDR_CITY;
    }

    public static String getWellLocation() {
        return WELL_LOCATION;
    }

    public static String getWellLocationAddressId() {
        return WELL_LOCATION_ADDRESS_ID;
    }

    public static String getWellOwner() {
        return WELL_OWNER;
    }

    public static String getWellCollector() {
        return WELL_COLLECTOR;
    }

    public static String getWellWellNumber() {
        return WELL_WELL_NUMBER;
    }

    public static String getWellReportToAddressCity() {
        return WELL_REPORT_TO_ADDR_CITY;
    }

    public static String getWellReportToAddressFaxPhone() {
        return WELL_REPORT_TO_ADDR_FAX_PHONE;
    }

    public static String getWellReportToAddressWorkPhone() {
        return WELL_LOCATION_ADDR_WORK_PHONE;
    }

    public static String getWellLocationAddrId() {
        return WELL_LOCATION_ADDR_ID;
    }

    public static String getWellLocationAddrMultipleUnit() {
        return WELL_LOCATION_ADDR_MULTIPLE_UNIT;
    }

    public static String getWellLocationAddrStreetAddress() {
        return WELL_LOCATION_ADDR_STREET_ADDRESS;
    }

    public static String getWellLocationAddrCity() {
        return WELL_LOCATION_ADDR_CITY;
    }

    public static String getWellLocationAddrState() {
        return WELL_LOCATION_ADDR_STATE;
    }

    public static String getWellLocationAddrZipCode() {
        return WELL_LOCATION_ADDR_ZIP_CODE;
    }

    public static String getSDWISId() {
        return SDWIS_ID;
    }

    public static String getSDWISSampleId() {
        return SDWIS_SAMPLE_ID;
    }

    public static String getSDWISPwsId() {
        return SDWIS_PWS_ID;
    }

    public static String getSDWISStateLabId() {
        return SDWIS_STATE_LAB_ID;
    }

    public static String getSDWISFacilityId() {
        return SDWIS_FACILITY_ID;
    }

    public static String getSDWISSampleTypeId() {
        return SDWIS_SAMPLE_TYPE_ID;
    }

    public static String getSDWISSampleCategoryId() {
        return SDWIS_SAMPLE_CATEGORY_ID;
    }

    public static String getSDWISSamplePointId() {
        return SDWIS_SAMPLE_POINT_ID;
    }

    public static String getSDWISPriority() {
        return SDWIS_PRIORITY;
    }

    public static String getSDWISLocation() {
        return SDWIS_LOCATION;
    }

    public static String getSDWISCollector() {
        return SDWIS_COLLECTOR;
    }

    public static String getPwsNumber0() {
        return PWS_NUMBER0;
    }

    public static String getPwsId() {
        return PWS_ID;
    }

    public static String getPwsName() {
        return PWS_NAME;
    }

    public static String getNeonatalPatientId() {
        return NEO_PATIENT_ID;
    }

    public static String getNeonatalBirthOrder() {
        return NEO_BIRTH_ORDER;
    }

    public static String getNeonatalGestationalAge() {
        return NEO_GESTATIONAL_AGE;
    }

    public static String getNeonatalNextOfKinId() {
        return NEO_NEXT_OF_KIN_ID;
    }

    public static String getNeonatalNextOfKinRelationId() {
        return NEO_NEXT_OF_KIN_RELATION_ID;
    }

    public static String getNeonatalIsRepeat() {
        return NEO_IS_REPEAT;
    }

    public static String getNeonatalIsNicu() {
        return NEO_IS_NICU;
    }

    public static String getNeonatalFeedingId() {
        return NEO_FEEDING_ID;
    }

    public static String getNeonatalWeight() {
        return NEO_WEIGHT;
    }

    public static String getNeonatalIsTransfused() {
        return NEO_IS_TRANSFUSED;
    }

    public static String getNeonatalTransfusionDate() {
        return NEO_TRANSFUSION_DATE;
    }

    public static String getNeonatalIsCollectionValid() {
        return NEO_IS_COLLECTION_VALID;
    }

    public static String getNeonatalCollectionAge() {
        return NEO_COLLECTION_AGE;
    }

    public static String getNeonatalFormNumber() {
        return NEO_FORM_NUMBER;
    }

    public static String getNeonatalPatientLastName() {
        return NEO_PATIENT_LAST_NAME;
    }

    public static String getNeonatalPatientFirstName() {
        return NEO_PATIENT_FIRST_NAME;
    }

    public static String getNeonatalPatientMiddleName() {
        return NEO_PATIENT_MIDDLE_NAME;
    }

    public static String getNeonatalPatientAddressId() {
        return NEO_PATIENT_ADDRESS_ID;
    }

    public static String getNeonatalPatientBirthDate() {
        return NEO_PATIENT_BIRTH_DATE;
    }

    public static String getNeonatalPatientBirthTime() {
        return NEO_PATIENT_BIRTH_TIME;
    }

    public static String getNeonatalPatientGenderId() {
        return NEO_PATIENT_GENDER_ID;
    }

    public static String getNeonatalPatientRaceId() {
        return NEO_PATIENT_RACE_ID;
    }

    public static String getNeonatalPatientEthnicityId() {
        return NEO_PATIENT_ETHNICITY_ID;
    }

    public static String getNeonatalPatientNationalId() {
        return NEO_PATIENT_NATIONAL_ID;
    }

    public static String getNeonatalPatientAddrMultipleUnit() {
        return NEO_PATIENT_ADDR_MULTIPLE_UNIT;
    }

    public static String getNeonatalPatientAddrStreetAddress() {
        return NEO_PATIENT_ADDR_STREET_ADDRESS;
    }

    public static String getNeonatalPatientAddrCity() {
        return NEO_PATIENT_ADDR_CITY;
    }

    public static String getNeonatalPatientAddrState() {
        return NEO_PATIENT_ADDR_STATE;
    }

    public static String getNeonatalPatientAddrZipCode() {
        return NEO_PATIENT_ADDR_ZIP_CODE;
    }

    public static String getNeonatalNextOfKinLastName() {
        return NEO_NEXT_OF_KIN_LAST_NAME;
    }

    public static String getNeonatalNextOfKinFirstName() {
        return NEO_NEXT_OF_KIN_FIRST_NAME;
    }

    public static String getNeonatalNextOfKinMiddleName() {
        return NEO_NEXT_OF_KIN_MIDDLE_NAME;
    }

    public static String getNeonatalNextOfKinBirthDate() {
        return NEO_NEXT_OF_KIN_BIRTH_DATE;
    }

    public static String getNeonatalNextOfKinGenderId() {
        return NEO_NEXT_OF_KIN_GENDER_ID;
    }

    public static String getNeonatalNextOfKinRaceId() {
        return NEO_NEXT_OF_KIN_RACE_ID;
    }

    public static String getNeonatalNextOfKinEthnicityId() {
        return NEO_NEXT_OF_KIN_ETHNICITY_ID;
    }

    public static String getNeonatalNextOfKinNationalId() {
        return NEO_NEXT_OF_KIN_NATIONAL_ID;
    }

    public static String getNeonatalNextOfKinAddrMultipleUnit() {
        return NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT;
    }

    public static String getNeonatalNextOfKinAddrStreetAddress() {
        return NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS;
    }

    public static String getNeonatalNextOfKinAddrCity() {
        return NEO_NEXT_OF_KIN_ADDR_CITY;
    }

    public static String getNeonatalNextOfKinAddrState() {
        return NEO_NEXT_OF_KIN_ADDR_STATE;
    }

    public static String getNeonatalNextOfKinAddrZipCode() {
        return NEO_NEXT_OF_KIN_ADDR_ZIP_CODE;
    }

    public static String getNeonatalNextOfKinAddrHomePhone() {
        return NEO_NEXT_OF_KIN_ADDR_HOME_PHONE;
    }

    public static String getClinicalPatientId() {
        return CLIN_PATIENT_ID;
    }

    public static String getClinicalProviderPhone() {
        return CLIN_PROVIDER_PHONE;
    }

    public static String getClinicalPatientLastName() {
        return CLIN_PATIENT_LAST_NAME;
    }

    public static String getClinicalPatientFirstName() {
        return CLIN_PATIENT_FIRST_NAME;
    }

    public static String getClinicalPatientBirthDate() {
        return CLIN_PATIENT_BIRTH_DATE;
    }

    public static String getClinicalPatientBirthDateFrom() {
        return CLIN_PATIENT_BIRTH_DATE_FROM;
    }

    public static String getClinicalPatientBirthDateTo() {
        return CLIN_PATIENT_BIRTH_DATE_TO;
    }

    public static String getClinicalPatientBirthTime() {
        return CLIN_PATIENT_BIRTH_TIME;
    }

    public static String getClinicalPatientGenderId() {
        return CLIN_PATIENT_GENDER_ID;
    }

    public static String getClinicalPatientRaceId() {
        return CLIN_PATIENT_RACE_ID;
    }

    public static String getClinicalPatientEthnicityId() {
        return CLIN_PATIENT_ETHNICITY_ID;
    }

    public static String getClinicalPatientNationalId() {
        return CLIN_PATIENT_NATIONAL_ID;
    }

    public static String getClinicalPatientAddrMultipleUnit() {
        return CLIN_PATIENT_ADDR_MULTIPLE_UNIT;
    }

    public static String getClinicalPatientAddrStreetAddress() {
        return CLIN_PATIENT_ADDR_STREET_ADDRESS;
    }

    public static String getClinicalPatientAddrCity() {
        return CLIN_PATIENT_ADDR_CITY;
    }

    public static String getClinicalPatientAddrState() {
        return CLIN_PATIENT_ADDR_STATE;
    }

    public static String getClinicalPatientAddrZipCode() {
        return CLIN_PATIENT_ADDR_ZIP_CODE;
    }

    public static String getClinicalPatientAddrHomePhone() {
        return CLIN_PATIENT_ADDR_HOME_PHONE;
    }

    public static String getPTSampleId() {
        return PT_SAMPLE_ID;
    }

    public static String getPTPTProviderId() {
        return PT_PT_PROVIDER_ID;
    }

    public static String getPTSeries() {
        return PT_SERIES;
    }

    public static String getPTDueDate() {
        return PT_DUE_DATE;
    }

    public static String getPTAdditionalDomain() {
        return PT_ADDITIONAL_DOMAIN;
    }

    public static String getItemId() {
        return ITEM_ID;
    }

    public static String getItemSampleId() {
        return ITEM_SAMPLE_ID;
    }

    public static String getItemSampleItemId() {
        return ITEM_SAMPLE_ITEM_ID;
    }

    public static String getItemItemSequence() {
        return ITEM_ITEM_SEQUENCE;
    }

    public static String getItemTypeofSampleId() {
        return ITEM_TYPE_OF_SAMPLE_ID;
    }

    public static String getItemSourceOfSampleId() {
        return ITEM_SOURCE_OF_SAMPLE_ID;
    }

    public static String getItemSourceOther() {
        return ITEM_SOURCE_OTHER;
    }

    public static String getItemContainerId() {
        return ITEM_CONTAINER_ID;
    }

    public static String getItemContainerReference() {
        return ITEM_CONTAINER_REFERENCE;
    }

    public static String getAnalysisId() {
        return ANALYSIS_ID;
    }

    public static String getAnalysisSampleItemId() {
        return ANALYSIS_SAMPLE_ITEM_ID;
    }

    public static String getAnalysisRevision() {
        return ANALYSIS_REVISION;
    }
    
    public static String getAnalysisTypeId() {
        return ANALYSIS_TYPE_ID;
    }

    public static String getAnalysisIsReportable() {
        return ANALYSIS_IS_REPORTABLE;
    }

    public static String getAnalysisIsReportableHeader() {
        return ANALYSIS_IS_REPORTABLE_HEADER;
    }

    public static String getAnalysisUnitOfMeasureId() {
        return ANALYSIS_UNIT_OF_MEASURE_ID;
    }

    public static String getAnalysisStatusId() {
        return ANALYSIS_STATUS_ID;
    }

    public static String getAnalysisStatusIdHeader() {
        return ANALYSIS_STATUS_ID_HEADER;
    }

    public static String getAnalysisStartedDate() {
        return ANALYSIS_STARTED_DATE;
    }

    public static String getAnalysisCompletedDate() {
        return ANALYSIS_COMPLETED_DATE;
    }

    public static String getAnalysisCompletedDateFrom() {
        return ANALYSIS_COMPLETED_DATE_FROM;
    }

    public static String getAnalysisCompletedDateTo() {
        return ANALYSIS_COMPLETED_DATE_TO;
    }

    public static String getAnalysisCompletedBy() {
        return ANALYSIS_COMPLETED_BY;
    }

    public static String getAnalysisReleasedDate() {
        return ANALYSIS_RELEASED_DATE;
    }

    public static String getAnalysisReleasedDateFrom() {
        return ANALYSIS_RELEASED_DATE_FROM;
    }

    public static String getAnalysisReleasedDateTo() {
        return ANALYSIS_RELEASED_DATE_TO;
    }

    public static String getAnalysisReleasedBy() {
        return ANALYSIS_RELEASED_BY;
    }

    public static String getAnalysisPrintedDate() {
        return ANALYSIS_PRINTED_DATE;
    }

    public static String getResultId() {
        return RESULT_ID;
    }

    public static String getResultAnalysisid() {
        return RESULT_ANALYSIS_ID;
    }

    public static String getResultTestAnalyteId() {
        return RESULT_TEST_ANALYTE_ID;
    }

    public static String getResultTestResultId() {
        return RESULT_TEST_RESULT_ID;
    }

    public static String getResultIsColumn() {
        return RESULT_IS_COLUMN;
    }

    public static String getResultSortOrder() {
        return RESULT_SORT_ORDER;
    }

    public static String getResultIsReportable() {
        return RESULT_IS_REPORTABLE;
    }

    public static String getResultAnalyteId() {
        return RESULT_ANALYTE_ID;
    }

    public static String getResultTypeId() {
        return RESULT_TYPE_ID;
    }

    public static String getResultValue() {
        return RESULT_VALUE;
    }

    public static String getAnalysisQaQaeventId() {
        return ANALYSISQA_QAEVENT_ID;
    }

    public static String getAnalysisSubQaId() {
        return ANALYSISSUBQA_ID;
    }

    public static String getAnalysisSubQaName() {
        return ANALYSISSUBQA_NAME;
    }

    public static String getSampleOrgId() {
        return SAMPLE_ORG_ID;
    }

    public static String getSampleOrgSampleId() {
        return SAMPLE_ORG_SAMPLE_ID;
    }

    public static String getSampleOrgOrganizationId() {
        return SAMPLE_ORG_ORGANIZATION_ID;
    }

    public static String getSampleOrgAttention() {
        return SAMPLE_ORG_ATTENTION;
    }

    public static String getSampleOrgTypeId() {
        return SAMPLE_ORG_TYPE_ID;
    }

    public static String getSampleOrgOrganizationName() {
        return ORG_NAME;
    }

    public static String getAddressMultipleUnit() {
        return ADDR_MULTIPLE_UNIT;
    }

    public static String getAddressStreetAddress() {
        return ADDR_STREET_ADDRESS;
    }

    public static String getAddressCity() {
        return ADDR_CITY;
    }

    public static String getAddressState() {
        return ADDR_STATE;
    }

    public static String getAddressZipCode() {
        return ADDR_ZIP_CODE;
    }

    public static String getAddressCountry() {
        return ADDR_COUNTRY;
    }

    public static String getSampleProjectId() {
        return SAMPLE_PROJECT_ID;
    }

    public static String getSampleProjectSampleId() {
        return SAMPLE_PROJECT_SAMPLE_ID;
    }

    public static String getSampleProjectProjectId() {
        return SAMPLE_PROJECT_PROJECT_ID;
    }

    public static String getSampleProjectIsPermanent() {
        return SAMPLE_PROJECT_IS_PERMANENT;
    }

    public static String getProjectId() {
        return PROJECT_ID;
    }

    public static String getProjectIdHeader() {
        return PROJECT_ID_HEADER;
    }

    public static String getProjectName() {
        return PROJECT_NAME;
    }

    public static String getProjectDescription() {
        return PROJECT_DESCRIPTION;
    }

    public static String getAuxDataId() {
        return AUX_DATA_ID;
    }

    public static String getAuxDataAuxFieldId() {
        return AUX_DATA_AUX_FIELD_ID;
    }

    public static String getAuxDataReferenceId() {
        return AUX_DATA_REFERENCE_ID;
    }

    public static String getAuxDataReferenceTableId() {
        return AUX_DATA_REFERENCE_TABLE_ID;
    }

    public static String getAuxDataIsReportable() {
        return AUX_DATA_IS_REPORTABLE;
    }

    public static String getAuxDataTypeId() {
        return AUX_DATA_TYPE_ID;
    }

    public static String getAuxDataValue() {
        return AUX_DATA_VALUE;
    }

    public static String getNeonatalProviderLastName() {
        return NEO_PROVIDER_LAST_NAME;
    }

    public static String getNeonatalProviderFirstName() {
        return NEO_PROVIDER_FIRST_NAME;
    }

    public static String getClinicalProviderLastName() {
        return CLIN_PROVIDER_LAST_NAME;
    }

    public static String getClinicalProviderFirstName() {
        return CLIN_PROVIDER_FIRST_NAME;
    }

    public static String getAnalysisTestName() {
        return ANALYSIS_TEST_NAME;
    }

    public static String getAnalysisTestNameHeader() {
        return ANALYSIS_TEST_NAME_HEADER;
    }

    public static String getAnalysisMethodName() {
        return ANALYSIS_METHOD_NAME;
    }

    public static String getAnalysisMethodNameHeader() {
        return ANALYSIS_METHOD_NAME_HEADER;
    }

    public static String getAnalysisSectionName() {
        return ANALYSIS_SECTION_NAME;
    }

    public static String getResultAnalyteName() {
        return RESULT_ANALYTE_NAME;
    }

    public static String getResultTestAnalyteRowGroup() {
        return RESULT_TEST_ANALYTE_ROW_GROUP;
    }

    public static String getResultTestAnalyteTypeId() {
        return RESULT_TEST_ANALYTE_TYPE_ID;
    }

    public static String getResultTestAnalyteResultGroup() {
        return RESULT_TEST_ANALYTE_RESULT_GROUP;
    }

    public static String getAuxDataFieldId() {
        return AUX_DATA_FIELD_ID;
    }

    public static String getAuxDataFieldAnalyteId() {
        return AUX_DATA_FIELD_ANALYTE_ID;
    }

    public static String getAuxDataFieldAnalyteName() {
        return AUX_DATA_FIELD_ANALYTE_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from = "Sample _sample ";

        // sample env
        if (where.indexOf("sampleEnvironmental.") > -1) {
            from += ", IN (_sample.sampleEnvironmental) _sampleEnvironmental ";
            from += " LEFT JOIN _sampleEnvironmental.locationAddress _locationAddress ";
        }

        if (where.indexOf("locationAddress.") > -1 && where.indexOf("sampleEnvironmental.") == -1) {
            from += ", IN (_sample.sampleEnvironmental) _sampleEnvironmental ";
            from += " LEFT JOIN _sampleEnvironmental.locationAddress _locationAddress ";
        }

        // sample private well
        if (where.indexOf("samplePrivateWell.") > -1 ||
            where.indexOf("privateWellReportToAddress") > -1 ||
            where.indexOf("wellOrganization.") > -1 || where.indexOf("wellLocationAddress.") > -1)
            from += " LEFT JOIN _sample.samplePrivateWell _samplePrivateWell ";

        if (where.indexOf("wellOrganization.") > -1 ||
            where.indexOf("privateWellReportToAddress") > -1)
            from += " LEFT JOIN _samplePrivateWell.organization _wellOrganization ";

        if (where.indexOf("privateWellReportToAddress") > -1) {
            from += " LEFT JOIN _wellOrganization.address _address ";
            from += " LEFT JOIN _samplePrivateWell.reportToAddress _privateWellReportToAddress ";
        }

        if (where.indexOf("wellOrganizationAddress") > -1) {
            if (where.indexOf("_samplePrivateWell.organization") == -1)
                from += " LEFT JOIN _samplePrivateWell.organization _wellOrganization ";
            from += " LEFT JOIN _wellOrganization.address _wellOrganizationAddress ";
        }

        if (where.indexOf("wellLocationAddress.") > -1)
            from += ", IN (_samplePrivateWell.locationAddress) _wellLocationAddress ";

        // sample sdwis
        if (where.indexOf("sampleSDWIS.") > -1)
            from += ", IN (_sample.sampleSDWIS) _sampleSDWIS ";
        // pws
        if (where.indexOf("pws.") > -1) {
            if (where.indexOf("sampleSDWIS.") == -1)
                from += ", IN (_sample.sampleSDWIS) _sampleSDWIS ";
            from += ", IN ( _sampleSDWIS.pws) _pws ";
        }

        // sample clinical
        if (where.indexOf("sampleClinical.") > -1 || where.indexOf("clinicalPatient.") > -1 ||
            where.indexOf("clinicalPatientAddress.") > -1 ||
            where.indexOf("clinicalProvider.") > -1)
            from += ", IN (_sample.sampleClinical) _sampleClinical ";

        // sample pt
        if (where.indexOf("samplePT.") > -1)
            from += ", IN (_sample.samplePT) _samplePT ";

        if (where.indexOf("clinicalPatient.") > -1 || where.indexOf("clinicalPatientAddress.") > -1)
            from += " LEFT JOIN _sampleClinical.patient _clinicalPatient ";

        if (where.indexOf("clinicalPatientAddress.") > -1)
            from += " LEFT JOIN _clinicalPatient.address _clinicalPatientAddress ";

        if (where.indexOf("clinicalProvider.") > -1)
            from += " LEFT JOIN _sampleClinical.provider _clinicalProvider ";

        if (where.indexOf("project.") > -1) {
            from += " LEFT JOIN _sample.sampleProject _sampleProject ";
            from += " LEFT JOIN _sampleProject.project _project ";
        }

        if (where.indexOf("_sampleProject.") > -1 && where.indexOf("_project.") == -1)
            from += " LEFT JOIN _sample.sampleProject _sampleProject ";

        if (where.indexOf("_organization.") > -1) {
            from += ", IN (_sample.sampleOrganization) _sampleOrganization ";
            from += ", IN (_sampleOrganization.organization) _organization ";
        }

        if (where.indexOf("_sampleOrganization.") > -1 && where.indexOf("_organization.") == -1)
            from += ", IN (_sample.sampleOrganization) _sampleOrganization ";

        if (where.indexOf("sampleItem.") > -1 || where.indexOf("analysis.") > -1 ||
            where.indexOf("test.") > -1 || where.indexOf("method.") > -1 ||
            where.indexOf("analysisQaevent.") > -1 || where.indexOf("aQaevent.") > -1)
            from += ", IN (_sample.sampleItem) _sampleItem";

        if (where.indexOf("analysis.") > -1 || where.indexOf("test.") > -1 ||
            where.indexOf("method.") > -1 || where.indexOf("analysisQaevent.") > -1 ||
            where.indexOf("aQaevent.") > -1 || where.indexOf("result.") > -1)
            from += ", IN (_sampleItem.analysis) _analysis ";

        if (where.indexOf("test.") > -1 || where.indexOf("method.") > -1)
            from += ", IN (_analysis.test) _test ";

        if (where.indexOf("method.") > -1)
            from += ", IN (_test.method) _method ";

        if (where.indexOf("result.") > -1)
            from += ", IN (_analysis.result) _result ";

        if (where.indexOf("sampleQaevent.") > -1 || where.indexOf("sQaevent.") > -1)
            from += ", IN(_sample.sampleQAEvent) _sampleQaevent";

        if (where.indexOf("sQaevent.") > -1)
            from += ", IN (_sampleQaevent.qaEvent) _sQaevent ";

        if (where.indexOf("analysisQaevent.") > -1 || where.indexOf("aQaevent.") > -1)
            from += ", IN(_analysis.analysisQAEvent) _analysisQaevent";

        if (where.indexOf("aQaevent.") > -1)
            from += ", IN (_analysisQaevent.qaEvent) _aQaevent ";

        if (where.indexOf("auxData.") > -1)
            from += ", IN (_sample.auxData) _auxData ";

        if (where.indexOf("auxField.") > -1) {
            if (where.indexOf("auxData.") == -1)
                from += ", IN (_sample.auxData) _auxData ";
            from += ", IN (_auxData.auxField) _auxField ";
        }

        return from;
    }
}
