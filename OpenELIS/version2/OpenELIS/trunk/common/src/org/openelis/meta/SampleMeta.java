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

/**
 * SampleEnvironmental META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class SampleMeta implements Meta, MetaMap {
    private static final String    ID = "_sample.id",
                    NEXT_ITEM_SEQUENCE = "_sample.nextItemSequence",
                    DOMAIN = "_sample.domain",
                    ACCESSION_NUMBER = "_sample.accessionNumber",
                    REVISION = "_sample.revision",
                    ORDER_ID = "_sample.orderId",
                    ENTERED_DATE = "_sample.enteredDate",
                    RECEIVED_DATE = "_sample.receivedDate",
                    RECEIVED_BY_ID = "_sample.receivedById",
                    COLLECTION_DATE = "_sample.collectionDate",
                    COLLECTION_TIME = "_sample.collectionTime",
                    STATUS_ID = "_sample.statusId",
                    PACKAGE_ID = "_sample.packageId",
                    CLIENT_REFERENCE = "_sample.clientReference",
                    RELEASED_DATE = "_sample.releasedDate",

                    // sample environmental
                    ENV_ID = "_sampleEnvironmental.id",
                    ENV_SAMPLE_ID = "_sampleEnvironmental.sampleId",
                    ENV_IS_HAZARDOUS = "_sampleEnvironmental.isHazardous",
                    ENV_PRIORITY = "_sampleEnvironmental.priority",
                    ENV_DESCRIPTION = "_sampleEnvironmental.description",
                    ENV_COLLECTOR = "_sampleEnvironmental.collector",
                    ENV_COLLECTOR_PHONE = "_sampleEnvironmental.collectorPhone",
                    ENV_LOCATION = "_sampleEnvironmental.location",
                    ENV_LOCATION_ADDRESS_ID = "_sampleEnvironmental.locationAddressId",
                    
                    // sample private well
                    WELL_ID = "_samplePrivateWell.id",
                    WELL_SAMPLE_ID = "_samplePrivateWell.sampleId",
                    WELL_ORGANIZATION_ID = "_samplePrivateWell.organizationId",
                    WELL_REPORT_TO_NAME = "_samplePrivateWell.reportToName",
                    WELL_REPORT_TO_ATTENTION = "_samplePrivateWell.reportToAttention",
                    WELL_REPORT_TO_ADDRESS_ID = "_samplePrivateWell.reportToAddressId",
                    WELL_LOCATION = "_samplePrivateWell.location",
                    WELL_LOCATION_ADDRESS_ID = "_samplePrivateWell.locationAddressId",
                    WELL_OWNER = "_samplePrivateWell.owner",
                    WELL_COLLECTOR = "_samplePrivateWell.collector",
                    WELL_WELL_NUMBER = "_samplePrivateWell.wellNumber",
                    WELL_ORGANIZATION_NAME = "_wellOrganization.name",
                    WELL_REPORT_TO_ADDR_ID = "_privateWellReportToAddress.id", 
                    WELL_REPORT_TO_ADDR_MULTIPLE_UNIT = "_privateWellReportToAddress.multipleUnit",
                    WELL_REPORT_TO_ADDR_STREET_ADDRESS = "_privateWellReportToAddress.streetAddress", 
                    WELL_REPORT_TO_ADDR_CITY = "_privateWellReportToAddress.city",
                    WELL_REPORT_TO_ADDR_STATE = "_privateWellReportToAddress.state", 
                    WELL_REPORT_TO_ADDR_ZIP_CODE = "_privateWellReportToAddress.zipCode",
                    WELL_REPORT_TO_ADDR_WORK_PHONE = "_privateWellReportToAddress.workPhone", 
                    WELL_REPORT_TO_ADDR_HOME_PHONE = "_privateWellReportToAddress.homePhone",
                    WELL_REPORT_TO_ADDR_CELL_PHONE = "_privateWellReportToAddress.cellPhone", 
                    WELL_REPORT_TO_ADDR_FAX_PHONE = "_privateWellReportToAddress.faxPhone",
                    WELL_REPORT_TO_ADDR_EMAIL = "_privateWellReportToAddress.email", 
                    WELL_REPORT_TO_ADDR_COUNTRY = "_privateWellReportToAddress.country",
                    WELL_LOCATION_ADDR_MULTIPLE_UNIT = "_wellLocationAddress.multipleUnit",
                    WELL_LOCATION_ADDR_STREET_ADDRESS = "_wellLocationAddress.streetAddress", 
                    WELL_LOCATION_ADDR_CITY = "_wellLocationAddress.city",
                    WELL_LOCATION_ADDR_STATE = "_wellLocationAddress.state", 
                    WELL_LOCATION_ADDR_ZIP_CODE = "_wellLocationAddress.zipCode",
                    WELL_LOCATION_ADDR_WORK_PHONE = "_wellLocationAddress.workPhone", 
                    WELL_LOCATION_ADDR_HOME_PHONE = "_wellLocationAddress.homePhone",
                    WELL_LOCATION_ADDR_CELL_PHONE = "_wellLocationAddress.cellPhone", 
                    WELL_LOCATION_ADDR_FAX_PHONE = "_wellLocationAddress.faxPhone",
                    WELL_LOCATION_ADDR_EMAIL = "_wellLocationAddress.email", 
                    WELL_LOCATION_ADDR_COUNTRY = "wellLocationAddress.country",
                    
                    //sample sdwis
                    SDWIS_ID = "_sampleSDWIS.id",
                    SDWIS_SAMPLE_ID = "_sampleSDWIS.sampleId",
                    SDWIS_PWS_ID = "_sampleSDWIS.pwdId",
                    SDWIS_STATE_LAB_ID = "_sampleSDWIS.stateLabId",
                    SDWIS_FACILITY_ID = "_sampleSDWIS.facilityId",
                    SDWIS_SAMPLE_TYPE_ID = "_sampleSDWIS.sampleTypeId",
                    SDWIS_SAMPLE_CATEGORY_ID = "_sampleSDWIS.sampleCategoryId",
                    SDWIS_PB_SAMPLE_TYPE_ID = "_sampleSDWIS.pbSampleTypeId",
                    SDWIS_SAMPLE_POINT_ID = "_sampleSDWIS.samplePointId",
                    SDWIS_LOCATION = "_sampleSDWIS.location",
                    SDWIS_COLLECTOR = "_sampleSDWIS.collector",
                    SDWIS_ORIGINAL_SAMPLE_NUMBER = "_sampleSDWIS.originalSampleNumber",
                    SDWIS_REPEAT_CODE_ID = "_sampleSDWIS.repeatCodeId",
                    SDWIS_COMPOSITE_INDICATOR = "_sampleSDWIS.compositeIndicator",
                    SDWIS_COMPOSITE_SAMPLE_NUMBER = "_sampleSDWIS.compositeSampleNumber",
                    SDWIS_COMPOSITE_DATE = "_sampleSDWIS.compositeDate",
                    SDWIS_COMPOSITE_SEQUENCE = "_sampleSDWIS.compositeSequence",

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
                    
                    ITEM_ID = "_sampleItem.id", ITEM_SAMPLE_ID = "_sampleItem.sampleId",
                    ITEM_SAMPLE_ITEM_ID = "_sampleItem.sampleItemId",
                    ITEM_ITEM_SEQUENCE = "_sampleItem.itemSequence",
                    ITEM_TYPE_OF_SAMPLE_ID = "_sampleItem.typeOfSampleId",
                    ITEM_SOURCE_OF_SAMPLE_ID = "_sampleItem.sourceOfSampleId",
                    ITEM_SOURCE_OTHER = "_sampleItem.sourceOther",
                    ITEM_CONTAINER_ID = "_sampleItem.containerId",
                    ITEM_CONTAINER_REFERENCE = "_sampleItem.containerReference",
                    ITEM_QUANTITY = "_sampleItem.quantity",
                    ITEM_UNIT_OF_MEASURE_ID = "_sampleItem.unitOfMeasureId",

                    ANALYSIS_ID = "_analysis.id",
                    ANALYSIS_SAMPLE_ITEM_ID = "_analysis.sampleItemId",
                    ANALYSIS_REVISION = "_analysis.revision",
                    ANALYSIS_TEST_ID = "_analysis.testId",
                    ANALYSIS_SECTION_ID = "_analysis.sectionId",
                    ANALYSIS_PRE_ANALYSIS_ID = "_analysis.preAnalysisId",
                    ANALYSIS_PARENT_ANALYSIS_ID = "_analysis.parentAnalysisId",
                    ANALYSIS_PARENT_RESULT_ID = "_analysis.parentResultId",
                    ANALYSIS_IS_REPORTABLE = "_analysis.isReportable",
                    ANALYSIS_UNIT_OF_MEASURE_ID = "_analysis.unitOfMeasureId",
                    ANALYSIS_STATUS_ID = "_analysis.statusId",
                    ANALYSIS_AVAILABLE_DATE = "_analysis.availableDate",
                    ANALYSIS_STARTED_DATE = "_analysis.startedDate",
                    ANALYSIS_COMPLETED_DATE = "_analysis.completedDate",
                    ANALYSIS_RELEASED_DATE = "_analysis.releasedDate",
                    ANALYSIS_PRINTED_DATE = "_analysis.printedDate",
                    ANALYSIS_SAMPLE_PREP = "analysisSamplePrep",

                    ANALYSISQA_ID = "_analysisQaevent.id",
                    ANALYSISQA_ANALYSIS_ID = "_analysisQaevent.analysisId",
                    ANALYSISQA_QAEVENT_ID = "_analysisQaevent.qaeventId",
                    ANALYSISQA_TYPE_ID = "_analysisQaevent.typeId",
                    ANALYSISQA_IS_BILLABLE = "_analysisQaevent.isBillable",

                    ANALYSISSUBQA_ID = "_aQaevent.id", ANALYSISSUBQA_NAME = "_aQaevent.name",
                    ANALYSISSUBQA_DESCRIPTION = "_aQaevent.description",
                    ANALYSISSUBQA_TEST_ID = "_aQaevent.testId",
                    ANALYSISSUBQA_TYPE_ID = "_aQaevent.typeId",
                    ANALYSISSUBQA_IS_BILLABLE = "_aQaevent.isBillable",
                    ANALYSISSUBQA_REPORTING_SEQUENCE = "_aQaevent.reportingSequence",
                    ANALYSISSUBQA_REPORTING_TEXT = "_aQaevent.reportingText",

                    SAMPLEQA_ID = "_sampleQaevent.id",
                    SAMPLEQA_SAMPLE_ID = "_sampleQaevent.sampleId",
                    SAMPLEQA_QAEVENT_ID = "_sampleQaevent.qaeventId",
                    SAMPLEQA_TYPE_ID = "_sampleQaevent.typeId",
                    SAMPLEQA_IS_BILLABLE = "_sampleQaevent.isBillable",

                    SAMPLESUBQA_ID = "_sQaevent.id", 
                    SAMPLESUBQA_NAME = "_sQaevent.name",
                    SAMPLESUBQA_DESCRIPTION = "_sQaevent.description",
                    SAMPLESUBQA_TEST_ID = "_sQaevent.testId",
                    SAMPLESUBQA_TYPE_ID = "_sQaevent.typeId",
                    SAMPLESUBQA_IS_BILLABLE = "_sQaevent.isBillable",
                    SAMPLESUBQA_REPORTING_SEQUENCE = "_sQaevent.reportingSequence",
                    SAMPLESUBQA_REPORTING_TEXT = "_sQaevent.reportingText",

                    SAMPLE_ORG_ID = "_sampleOrganization.id",
                    SAMPLE_ORG_SAMPLE_ID = "_sampleOrganization.sampleId",
                    SAMPLE_ORG_ORGANIZATION_ID = "_sampleOrganization.organizationId",
                    SAMPLE_ORG_TYPE_ID = "_sampleOrganization.typeId",

                    ORG_ID = "_organization.id",
                    ORG_PARENT_ORGANIZATION_ID = "_organization.parentOrganizationId",
                    ORG_NAME = "_organization.name", 
                    ORG_IS_ACTIVE = "_organization.isActive",
                    ORG_ADDRESS_ID = "_organization.addressId",
                    
                    ADDR_ID = "_address.id", 
                    ADDR_MULTIPLE_UNIT = "_address.multipleUnit",
                    ADDR_STREET_ADDRESS = "_address.streetAddress", 
                    ADDR_CITY = "_address.city",
                    ADDR_STATE = "_address.state", 
                    ADDR_ZIP_CODE = "_address.zipCode",
                    ADDR_WORK_PHONE = "_address.workPhone", 
                    ADDR_HOME_PHONE = "_address.homePhone",
                    ADDR_CELL_PHONE = "_address.cellPhone", 
                    ADDR_FAX_PHONE = "_address.faxPhone",
                    ADDR_EMAIL = "_address.email", 
                    ADDR_COUNTRY = "_address.country",

                    SAMPLE_PROJECT_ID = "_sampleProject.id",
                    SAMPLE_PROJECT_SAMPLE_ID = "_sampleProject.sampleId",
                    SAMPLE_PROJECT_PROJECT_ID = "_sampleProject.projectId",
                    SAMPLE_PROJECT_IS_PERMANENT = "_sampleProject.isPermanent",

                    PROJECT_ID = "_project.id", PROJECT_NAME = "_project.name",
                    PROJECT_DESCRIPTION = "_project.description",
                    PROJECT_STARTED_DATE = "_project.startedDate",
                    PROJECT_COMPLETED_DATE = "_project.completedDate",
                    PROJECT_IS_ACTIVE = "_project.isActive",
                    PROJECT_REFERENCE_TO = "_project.referenceTo",
                    PROJECT_OWNER_ID = "_project.ownerId",
                    PROJECT_SCRIPTLET_ID = "_project.scriptletId",
                    
                    AUX_DATA_ID = "_auxData.id",
                    AUX_DATA_AUX_FIELD_ID = "_auxData.auxFieldId",
                    AUX_DATA_REFERENCE_ID = "_auxData.referenceId",
                    AUX_DATA_REFERENCE_TABLE_ID = "_auxData.referenceTableId",
                    AUX_DATA_IS_REPORTABLE = "_auxData.isReportable",
                    AUX_DATA_TYPE_ID = "_auxData.typeId",
                    AUX_DATA_VALUE = "_auxData.value",

                    ANALYSIS_TEST_NAME = "_test.name", 
                    ANALYSIS_METHOD_NAME = "_method.name";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, NEXT_ITEM_SEQUENCE, DOMAIN, ACCESSION_NUMBER,
                                                  REVISION, ORDER_ID, ENTERED_DATE, RECEIVED_DATE,
                                                  RECEIVED_BY_ID, COLLECTION_DATE, COLLECTION_TIME,
                                                  STATUS_ID, PACKAGE_ID, CLIENT_REFERENCE,
                                                  RELEASED_DATE,  ENV_ID, ENV_SAMPLE_ID,
                                                  ENV_IS_HAZARDOUS, ENV_PRIORITY, ENV_DESCRIPTION,
                                                  ENV_COLLECTOR, ENV_COLLECTOR_PHONE, ENV_LOCATION,
                                                  ENV_LOCATION_ADDRESS_ID, ENV_LOCATION_ADDRESS_ID, 
                                                  LOCATION_ADDR_MULTIPLE_UNIT, LOCATION_ADDR_STREET_ADDRESS, 
                                                  LOCATION_ADDR_CITY, LOCATION_ADDR_STATE,
                                                  LOCATION_ADDR_ZIP_CODE, LOCATION_ADDR_WORK_PHONE, 
                                                  LOCATION_ADDR_HOME_PHONE, LOCATION_ADDR_CELL_PHONE, 
                                                  LOCATION_ADDR_FAX_PHONE, LOCATION_ADDR_EMAIL,
                                                  LOCATION_ADDR_COUNTRY, WELL_ID, WELL_SAMPLE_ID,
                                                  WELL_ORGANIZATION_ID, WELL_REPORT_TO_NAME, WELL_REPORT_TO_ATTENTION,
                                                  WELL_REPORT_TO_ADDRESS_ID, WELL_LOCATION,
                                                  WELL_LOCATION_ADDRESS_ID, WELL_OWNER,
                                                  WELL_COLLECTOR, WELL_WELL_NUMBER, WELL_ORGANIZATION_NAME,
                                                  WELL_REPORT_TO_ADDR_ID, WELL_REPORT_TO_ADDR_MULTIPLE_UNIT, 
                                                  WELL_REPORT_TO_ADDR_STREET_ADDRESS, WELL_REPORT_TO_ADDR_CITY,
                                                  WELL_REPORT_TO_ADDR_STATE, WELL_REPORT_TO_ADDR_ZIP_CODE,
                                                  WELL_REPORT_TO_ADDR_WORK_PHONE, WELL_REPORT_TO_ADDR_HOME_PHONE,
                                                  WELL_REPORT_TO_ADDR_CELL_PHONE, WELL_REPORT_TO_ADDR_FAX_PHONE,
                                                  WELL_REPORT_TO_ADDR_EMAIL, WELL_REPORT_TO_ADDR_COUNTRY,
                                                  WELL_LOCATION_ADDR_MULTIPLE_UNIT, WELL_LOCATION_ADDR_STREET_ADDRESS,
                                                  WELL_LOCATION_ADDR_CITY, WELL_LOCATION_ADDR_STATE,
                                                  WELL_LOCATION_ADDR_ZIP_CODE, WELL_LOCATION_ADDR_WORK_PHONE, 
                                                  WELL_LOCATION_ADDR_HOME_PHONE, WELL_LOCATION_ADDR_CELL_PHONE, 
                                                  WELL_LOCATION_ADDR_FAX_PHONE, WELL_LOCATION_ADDR_EMAIL, 
                                                  WELL_LOCATION_ADDR_COUNTRY, SDWIS_ID, SDWIS_SAMPLE_ID,
                                                  SDWIS_PWS_ID, SDWIS_STATE_LAB_ID, SDWIS_FACILITY_ID,
                                                  SDWIS_SAMPLE_TYPE_ID, SDWIS_SAMPLE_CATEGORY_ID,
                                                  SDWIS_PB_SAMPLE_TYPE_ID, SDWIS_SAMPLE_POINT_ID,
                                                  SDWIS_LOCATION, SDWIS_COLLECTOR, SDWIS_ORIGINAL_SAMPLE_NUMBER,
                                                  SDWIS_REPEAT_CODE_ID, SDWIS_COMPOSITE_INDICATOR,
                                                  SDWIS_COMPOSITE_SAMPLE_NUMBER, SDWIS_COMPOSITE_DATE,
                                                  SDWIS_COMPOSITE_SEQUENCE, ITEM_ID, ITEM_SAMPLE_ID, ITEM_SAMPLE_ITEM_ID,
                                                  ITEM_ITEM_SEQUENCE, ITEM_TYPE_OF_SAMPLE_ID,
                                                  ITEM_SOURCE_OF_SAMPLE_ID, ITEM_SOURCE_OTHER,
                                                  ITEM_CONTAINER_ID, ITEM_CONTAINER_REFERENCE,
                                                  ITEM_QUANTITY, ITEM_UNIT_OF_MEASURE_ID,
                                                  ANALYSIS_ID, ANALYSIS_SAMPLE_ITEM_ID,
                                                  ANALYSIS_REVISION, ANALYSIS_TEST_ID,
                                                  ANALYSIS_SECTION_ID, ANALYSIS_PRE_ANALYSIS_ID,
                                                  ANALYSIS_PARENT_ANALYSIS_ID,
                                                  ANALYSIS_PARENT_RESULT_ID,
                                                  ANALYSIS_IS_REPORTABLE,
                                                  ANALYSIS_UNIT_OF_MEASURE_ID, ANALYSIS_STATUS_ID,
                                                  ANALYSIS_AVAILABLE_DATE, ANALYSIS_STARTED_DATE,
                                                  ANALYSIS_COMPLETED_DATE, ANALYSIS_RELEASED_DATE,
                                                  ANALYSIS_PRINTED_DATE, ANALYSISQA_ID,
                                                  ANALYSISQA_ANALYSIS_ID, ANALYSISQA_QAEVENT_ID,
                                                  ANALYSISQA_TYPE_ID, ANALYSISQA_IS_BILLABLE,
                                                  ANALYSISSUBQA_ID, ANALYSISSUBQA_NAME,
                                                  ANALYSISSUBQA_DESCRIPTION, ANALYSISSUBQA_TEST_ID,
                                                  ANALYSISSUBQA_TYPE_ID, ANALYSISSUBQA_IS_BILLABLE,
                                                  ANALYSISSUBQA_REPORTING_SEQUENCE,
                                                  ANALYSISSUBQA_REPORTING_TEXT, SAMPLEQA_ID,
                                                  SAMPLEQA_SAMPLE_ID, SAMPLEQA_QAEVENT_ID,
                                                  SAMPLEQA_TYPE_ID, SAMPLEQA_IS_BILLABLE,
                                                  SAMPLESUBQA_ID, SAMPLESUBQA_NAME,
                                                  SAMPLESUBQA_DESCRIPTION, SAMPLESUBQA_TEST_ID,
                                                  SAMPLESUBQA_TYPE_ID, SAMPLESUBQA_IS_BILLABLE,
                                                  SAMPLESUBQA_REPORTING_SEQUENCE,
                                                  SAMPLESUBQA_REPORTING_TEXT, SAMPLE_ORG_ID,
                                                  SAMPLE_ORG_SAMPLE_ID, SAMPLE_ORG_ORGANIZATION_ID,
                                                  SAMPLE_ORG_TYPE_ID, ORG_ID,
                                                  ORG_PARENT_ORGANIZATION_ID, ORG_NAME,
                                                  ORG_IS_ACTIVE, ORG_ADDRESS_ID, 
                                                  ADDR_ID, ADDR_MULTIPLE_UNIT, ADDR_STREET_ADDRESS,
                                                  ADDR_CITY, ADDR_STATE, ADDR_ZIP_CODE,
                                                  ADDR_WORK_PHONE, ADDR_HOME_PHONE, ADDR_CELL_PHONE,
                                                  ADDR_FAX_PHONE, ADDR_EMAIL, ADDR_COUNTRY,
                                                  SAMPLE_PROJECT_SAMPLE_ID,
                                                  SAMPLE_PROJECT_PROJECT_ID,
                                                  SAMPLE_PROJECT_IS_PERMANENT, PROJECT_ID,
                                                  PROJECT_NAME, PROJECT_DESCRIPTION,
                                                  PROJECT_STARTED_DATE, PROJECT_COMPLETED_DATE,
                                                  PROJECT_IS_ACTIVE, PROJECT_REFERENCE_TO,
                                                  PROJECT_OWNER_ID, PROJECT_SCRIPTLET_ID,
                                                  AUX_DATA_ID, AUX_DATA_AUX_FIELD_ID,
                                                  AUX_DATA_REFERENCE_ID, AUX_DATA_REFERENCE_TABLE_ID,
                                                  AUX_DATA_IS_REPORTABLE, AUX_DATA_TYPE_ID,
                                                  AUX_DATA_VALUE, ANALYSIS_TEST_NAME, ANALYSIS_METHOD_NAME));
    }

    public static String getId() {
        return ID;
    }

    public static String getNextItemSequence() {
        return NEXT_ITEM_SEQUENCE;
    }

    public static String getDomain() {
        return DOMAIN;
    }

    public static String getAccessionNumber() {
        return ACCESSION_NUMBER;
    }

    public static String getRevision() {
        return REVISION;
    }

    public static String getOrderId() {
        return ORDER_ID;
    }

    public static String getEnteredDate() {
        return ENTERED_DATE;
    }

    public static String getReceivedDate() {
        return RECEIVED_DATE;
    }

    public static String getReceivedById() {
        return RECEIVED_BY_ID;
    }

    public static String getCollectionDate() {
        return COLLECTION_DATE;
    }

    public static String getCollectionTime() {
        return COLLECTION_TIME;
    }

    public static String getStatusId() {
        return STATUS_ID;
    }

    public static String getPackageId() {
        return PACKAGE_ID;
    }

    public static String getClientReference() {
        return CLIENT_REFERENCE;
    }

    public static String getReleasedDate() {
        return RELEASED_DATE;
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

    public static String getEnvCollectorPhone() {
        return ENV_COLLECTOR_PHONE;
    }

    public static String getEnvLocation() {
        return ENV_LOCATION;
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

    public static String getWellReportToName() {
        return WELL_REPORT_TO_NAME;
    }
    
    public static String getWellReportToAttention(){
       return WELL_REPORT_TO_ATTENTION;
    }

    public static String getWellReportToAddressId() {
        return WELL_REPORT_TO_ADDRESS_ID;
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
    
    public static String getWellOrganizationName(){
        return WELL_ORGANIZATION_NAME;
    }
    
    public static String getWellReportToAddressMultipleUnit(){
     return WELL_REPORT_TO_ADDR_MULTIPLE_UNIT;   
    }

    public static String getWellReportToAddressStreetAddress(){
        return WELL_REPORT_TO_ADDR_STREET_ADDRESS;
    }

    public static String getWellReportToAddressCity(){
        return WELL_REPORT_TO_ADDR_CITY;
    }
    
    public static String getWellReportToAddressState(){
        return WELL_REPORT_TO_ADDR_STATE;
    }
    
    public static String getWellReportToAddressZipCode(){
        return WELL_REPORT_TO_ADDR_ZIP_CODE;
    }
    
    public static String getWellReportToAddressWorkPhone(){
        return WELL_REPORT_TO_ADDR_WORK_PHONE;
    }

    public static String getWellReportToAddressHomePhone(){
        return WELL_REPORT_TO_ADDR_HOME_PHONE;
    }

    public static String getWellReportToAddressCellPhone(){
        return WELL_REPORT_TO_ADDR_CELL_PHONE;
    }

    public static String getWellReportToAddressFaxPhone(){
        return WELL_REPORT_TO_ADDR_FAX_PHONE;
    }
    
    public static String getWellReportToAddressEmail(){
        return WELL_REPORT_TO_ADDR_EMAIL;
    }
    
    public static String getWellReportToAddressCountry(){
        return WELL_REPORT_TO_ADDR_COUNTRY;
    }
    
    public static String getWellLocationAddrMultipleUnit(){
        return WELL_LOCATION_ADDR_MULTIPLE_UNIT;
    }
    
    public static String getWellLocationAddrStreetAddress(){
        return WELL_LOCATION_ADDR_STREET_ADDRESS;
    }
    
    public static String getWellLocationAddrCity(){
        return WELL_LOCATION_ADDR_CITY;
    }
    
    public static String getWellLocationAddrState(){
        return WELL_LOCATION_ADDR_STATE;
    }
    
    public static String getWellLocationAddrZipCode(){
        return WELL_LOCATION_ADDR_ZIP_CODE;
    }
    
    public static String getWellLocationAddrWorkPhone(){
        return WELL_LOCATION_ADDR_WORK_PHONE;
    }
    
    public static String getWellLocationAddrHomePhone(){
        return WELL_LOCATION_ADDR_HOME_PHONE;
    }
    
    public static String getWellLocationAddrCellPhone(){
        return WELL_LOCATION_ADDR_CELL_PHONE;
    }
    
    public static String getWellLocationAddrFaxPhone(){
        return WELL_LOCATION_ADDR_FAX_PHONE;
    }
    
    public static String getWellLocationAddrEmail(){
        return WELL_LOCATION_ADDR_EMAIL;
    }
    
    public static String getWellLocationAddrCountry(){
        return WELL_LOCATION_ADDR_COUNTRY;
    }
    
    public static String getSDWISId(){
        return SDWIS_ID;
    }
    public static String getSDWISSampleId(){
        return SDWIS_SAMPLE_ID;
    }
    public static String getSDWISPwsId(){
        return SDWIS_PWS_ID;
    }
    public static String getSDWISStateLabId(){
        return SDWIS_STATE_LAB_ID;
    }
    public static String getSDWISFacilityId(){
        return SDWIS_FACILITY_ID;
    }
    public static String getSDWISSampleTypeId(){
        return SDWIS_SAMPLE_TYPE_ID;
    }
    public static String getSDWISSampleCategoryId(){
        return SDWIS_SAMPLE_CATEGORY_ID;
    }
    public static String getSDWISPbSampleTypeId(){
        return SDWIS_PB_SAMPLE_TYPE_ID;
    }
    public static String getSDWISSamplePointId(){
        return SDWIS_SAMPLE_POINT_ID;
    }
    public static String getSDWISLocation(){
        return SDWIS_LOCATION;
    }
    public static String getSDWISCollector(){
        return SDWIS_COLLECTOR;
    }
    public static String getSDWISOriginalSampleNumber(){
        return SDWIS_ORIGINAL_SAMPLE_NUMBER;
    }
    public static String getSDWISRepeatCodeId(){
        return SDWIS_REPEAT_CODE_ID;
    }
    public static String getSDWISCompositeIndicator(){
        return SDWIS_COMPOSITE_INDICATOR;
    }
    public static String getSDWISCompositeSampleNumber(){
        return SDWIS_COMPOSITE_SAMPLE_NUMBER;
    }
    public static String getSDWISCompositeDate(){
        return SDWIS_COMPOSITE_DATE;
    }
    public static String getSDWISCompositeSequence(){
        return SDWIS_COMPOSITE_SEQUENCE;
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

    public static String getItemTypeOfSampleId() {
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

    public static String getItemQuantity() {
        return ITEM_QUANTITY;
    }

    public static String getItemUnitOfMeasureId() {
        return ITEM_UNIT_OF_MEASURE_ID;
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

    public static String getAnalysisTestId() {
        return ANALYSIS_TEST_ID;
    }

    public static String getAnalysisSectionId() {
        return ANALYSIS_SECTION_ID;
    }

    public static String getAnalysisPreAnalysisId() {
        return ANALYSIS_PRE_ANALYSIS_ID;
    }

    public static String getAnalysisParentAnalysisId() {
        return ANALYSIS_PARENT_ANALYSIS_ID;
    }

    public static String getAnalysisParentResultId() {
        return ANALYSIS_PARENT_RESULT_ID;
    }

    public static String getAnalysisIsReportable() {
        return ANALYSIS_IS_REPORTABLE;
    }

    public static String getAnalysisUnitOfMeasureId() {
        return ANALYSIS_UNIT_OF_MEASURE_ID;
    }

    public static String getAnalysisStatusId() {
        return ANALYSIS_STATUS_ID;
    }

    public static String getAnalysisAvailableDate() {
        return ANALYSIS_AVAILABLE_DATE;
    }

    public static String getAnalysisStartedDate() {
        return ANALYSIS_STARTED_DATE;
    }

    public static String getAnalysisCompletedDate() {
        return ANALYSIS_COMPLETED_DATE;
    }

    public static String getAnalysisReleasedDate() {
        return ANALYSIS_RELEASED_DATE;
    }

    public static String getAnalysisPrintedDate() {
        return ANALYSIS_PRINTED_DATE;
    }

    public static String getAnalysisSamplePrep() {
        return ANALYSIS_SAMPLE_PREP;
    }

    public static String getAnalysisQaId() {
        return ANALYSISQA_ID;
    }

    public static String getAnalysisQaAnalysisId() {
        return ANALYSISQA_ANALYSIS_ID;
    }

    public static String getAnalysisQaQaeventId() {
        return ANALYSISQA_QAEVENT_ID;
    }

    public static String getAnalysisQaTypeId() {
        return ANALYSISQA_TYPE_ID;
    }

    public static String getAnalysisQaIsBillable() {
        return ANALYSISQA_IS_BILLABLE;
    }

    public static String getAnalysisSubQaId() {
        return ANALYSISSUBQA_ID;
    }

    public static String getAnalysisSubQaName() {
        return ANALYSISSUBQA_NAME;
    }

    public static String getAnalysisSubqaDescription() {
        return ANALYSISSUBQA_DESCRIPTION;
    }

    public static String getAnalysisSubQaTestId() {
        return ANALYSISSUBQA_TEST_ID;
    }

    public static String getAnalysisSubQaTypeId() {
        return ANALYSISSUBQA_TYPE_ID;
    }

    public static String getAnalysisSubQaIsBillable() {
        return ANALYSISSUBQA_IS_BILLABLE;
    }

    public static String getAnalysisSubQaReportingSequence() {
        return ANALYSISSUBQA_REPORTING_SEQUENCE;
    }

    public static String getAnalysisSubQaReportingText() {
        return ANALYSISSUBQA_REPORTING_TEXT;
    }

    public static String getSampleQaId() {
        return SAMPLEQA_ID;
    }

    public static String getSampleQaSampleId() {
        return SAMPLEQA_SAMPLE_ID;
    }

    public static String getSampleQaQaeventId() {
        return SAMPLEQA_QAEVENT_ID;
    }

    public static String getSampleQaTypeId() {
        return SAMPLEQA_TYPE_ID;
    }

    public static String getSampleQaIsBillable() {
        return SAMPLEQA_IS_BILLABLE;
    }

    public static String getSampleSubQaId() {
        return SAMPLESUBQA_ID;
    }

    public static String getSampleSubQaName() {
        return SAMPLESUBQA_NAME;
    }

    public static String getSampleSubQaDescription() {
        return SAMPLESUBQA_DESCRIPTION;
    }

    public static String getSampleSubQaTestId() {
        return SAMPLESUBQA_TEST_ID;
    }

    public static String getSampleSubQaTypeId() {
        return SAMPLESUBQA_TYPE_ID;
    }

    public static String getSampleSubQaIsBillable() {
        return SAMPLESUBQA_IS_BILLABLE;
    }

    public static String getSampleSubQaReportingSequence() {
        return SAMPLESUBQA_REPORTING_SEQUENCE;
    }

    public static String getSampleSubQaReportingText() {
        return SAMPLESUBQA_REPORTING_TEXT;
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

    public static String getSampleOrgTypeId() {
        return SAMPLE_ORG_TYPE_ID;
    }

    public static String getOrgId() {
        return ORG_ID;
    }

    public static String getOrgParentOrganizationId() {
        return ORG_PARENT_ORGANIZATION_ID;
    }

    public static String getOrgName() {
        return ORG_NAME;
    }

    public static String getBillTo() {
        return "billTo";
    }

    public static String getOrgIsActive() {
        return ORG_IS_ACTIVE;
    }

    public static String getOrgAddressId() {
        return ORG_ADDRESS_ID;
    }
    
    public static String getAddressId(){
        return ADDR_ID;
    }
    
    public static String getAddressMultipleUnit(){
     return ADDR_MULTIPLE_UNIT;   
    }

    public static String getAddressStreetAddress(){
        return ADDR_STREET_ADDRESS;
    }

    public static String getAddressCity(){
        return ADDR_CITY;
    }
    
    public static String getAddressState(){
        return ADDR_STATE;
    }
    
    public static String getAddressZipCode(){
        return ADDR_ZIP_CODE;
    }
    
    public static String getAddressWorkPhone(){
        return ADDR_WORK_PHONE;
    }

    public static String getAddressHomePhone(){
        return ADDR_HOME_PHONE;
    }

    public static String getAddressCellPhone(){
        return ADDR_CELL_PHONE;
    }

    public static String getAddressFaxPhone(){
        return ADDR_FAX_PHONE;
    }
    
    public static String getAddressEmail(){
        return ADDR_EMAIL;
    }
    
    public static String getAddressCountry(){
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

    public static String getProjectName() {
        return PROJECT_NAME;
    }

    public static String getProjectDescription() {
        return PROJECT_DESCRIPTION;
    }

    public static String getProjectStartedDate() {
        return PROJECT_STARTED_DATE;
    }

    public static String getProjectCompletedDate() {
        return PROJECT_COMPLETED_DATE;
    }

    public static String getProjectIsActive() {
        return PROJECT_IS_ACTIVE;
    }

    public static String getProjectReferenceTo() {
        return PROJECT_REFERENCE_TO;
    }

    public static String getProjectOwnerId() {
        return PROJECT_OWNER_ID;
    }

    public static String getProjectScriptletId() {
        return PROJECT_SCRIPTLET_ID;
    }
    
    public static String getAuxDataId(){
        return AUX_DATA_ID;
    }
    
    public static String getAuxDataAuxFieldId(){
        return AUX_DATA_AUX_FIELD_ID;
    }
    
    public static String getAuxDataReferenceId(){
        return AUX_DATA_REFERENCE_ID;
    }
    
    public static String getAuxDataReferenceTableId(){
        return AUX_DATA_REFERENCE_TABLE_ID;
    }
    
    public static String getAuxDataIsReportable(){
        return AUX_DATA_IS_REPORTABLE;
    }
    
    public static String getAuxDataTypeId(){
        return AUX_DATA_TYPE_ID;
    }
    
    public static String getAuxDataValue(){
        return AUX_DATA_VALUE;
    }

    public static String getAnalysisTestName() {
        return ANALYSIS_TEST_NAME;
    }

    public static String getAnalysisMethodName() {
        return ANALYSIS_METHOD_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from = "Sample _sample ";

        //sample env
        if (where.indexOf("sampleEnvironmental.") > -1) 
            from += ", IN (_sample.sampleEnvironmental) _sampleEnvironmental ";
        
        //sample private well
        if (where.indexOf("samplePrivateWell.") > -1 || where.indexOf("privateWellReportToAddress") > -1 || 
                        where.indexOf("wellOrganization.") > -1 || where.indexOf("wellLocationAddress.") > -1) 
            from += ", IN (_sample.samplePrivateWell) _samplePrivateWell ";
        
        if (where.indexOf("wellOrganization.") > -1 || where.indexOf("privateWellReportToAddress") > -1)
            from += " LEFT JOIN _samplePrivateWell.organization _wellOrganization ";
        
        if (where.indexOf("privateWellReportToAddress") > -1){
            from += " LEFT JOIN _wellOrganization.address _address ";
            from += " LEFT JOIN _samplePrivateWell.reportToAddress _privateWellReportToAddress ";
        } 

        if (where.indexOf("wellLocationAddress.") > -1)
            from += ", IN (_samplePrivateWell.locationAddress) _wellLocationAddress ";
        
        //sample sdwis
        if(where.indexOf("sampleSDWIS.") > -1)
            from += ", IN (_sample.sampleSDWIS) _sampleSDWIS ";
            
        //common sample fields
        if (where.indexOf("project.") > -1){
            from += ", IN (_sample.sampleProject) _sampleProject ";
            from += ", IN (_sampleProject.project) _project ";
        }

        if (where.indexOf("sampleItem.") > -1 || where.indexOf("analysis.") > -1 || where.indexOf("test.") > -1 || 
                        where.indexOf("method.") > -1|| where.indexOf("analysisQaevent.") > -1 || 
                        where.indexOf("aQaevent.") > -1)
            from += ", IN (_sample.sampleItem) _sampleItem";

        if (where.indexOf("analysis.") > -1 || where.indexOf("test.") > -1 || 
                        where.indexOf("method.") > -1 || where.indexOf("analysisQaevent.") > -1 || 
                        where.indexOf("aQaevent.") > -1)
            from += ", IN (_sampleItem.analysis) _analysis ";
        
        if (where.indexOf("test.") > -1 || where.indexOf("method.") > -1)
            from += ", IN (_analysis.test) _test ";
        
        if (where.indexOf("method.") > -1)
            from += ", IN (_test.method) _method ";
        
        if(where.indexOf("sampleQaevent.") > -1 || where.indexOf("sQaevent.") > -1)
            from += ", IN(_sample.sampleQAEvent) _sampleQaevent";

        if (where.indexOf("sQaevent.") > -1)
            from += ", IN (_sampleQaevent.qaEvent) _sQaevent ";

        if(where.indexOf("analysisQaevent.") > -1 || where.indexOf("aQaevent.") > -1)
            from += ", IN(_analysis.analysisQAEvent) _analysisQaevent";

        if (where.indexOf("aQaevent.") > -1)
            from += ", IN (_analysisQaevent.qaEvent) _aQaevent ";
        
        if(where.indexOf("auxData.") > -1)
            from += ", IN (_sample.auxData) _auxData ";

        return from;
    }
}
