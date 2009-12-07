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
package org.openelis.meta;

/**
  * SampleEnvironmental META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class SampleMeta implements Meta, MetaMap {
    private static final String    
                    ENV_ID      = "_sampleEnvironmental.id",
                    ENV_SAMPLE_ID = "_sampleEnvironmental.sampleId",
                    ENV_IS_HAZARDOUS = "_sampleEnvironmental.isHazardous",
                    ENV_PRIORITY = "_sampleEnvironmental.priority",
                    ENV_DESCRIPTION = "_sampleEnvironmental.description",
                    ENV_COLLECTOR = "_sampleEnvironmental.collector",
                    ENV_COLLECTOR_PHONE = "_sampleEnvironmental.collectorPhone",
                    ENV_SAMPLING_LOCATION = "_sampleEnvironmental.samplingLocation",
                    ENV_ADDRESS_ID = "_sampleEnvironmental.addressId",

                    ID = "_sample.id", 
                    NEXT_ITEM_SEQUENCE = "_sample.nextItemSequence",
                    DOMAIN = "_sample.domain", 
                    ACCESSION_NUMBER = "_sample.accessionNumber",
                    REVISION = "_sample.revision", 
                    ENTERED_DATE = "_sample.enteredDate",
                    RECEIVED_DATE = "_sample.receivedDate",
                    RECEIVED_BY_ID = "_sample.receivedById",
                    COLLECTION_DATE = "_sample.collectionDate",
                    COLLECTION_TIME = "_sample.collectionTime", 
                    STATUS_ID = "_sample.statusId",
                    PACKAGE_ID = "_sample.packageId", 
                    CLIENT_REFERENCE = "_sample.clientReference",
                    RELEASED_DATE = "_sample.releasedDate",

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

                    ITEM_ID = "_sampleItem.id", 
                    ITEM_SAMPLE_ID = "_sampleItem.sampleId",
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

                    ANALYSISQA_ID = "_analysisQaevent.id",
                    ANALYSISQA_ANALYSIS_ID = "_analysisQaevent.analysisId",
                    ANALYSISQA_QAEVENT_ID = "_analysisQaevent.qaeventId",
                    ANALYSISQA_TYPE_ID = "_analysisQaevent.typeId",
                    ANALYSISQA_IS_BILLABLE = "_analysisQaevent.isBillable",

                    ANALYSISSUBQA_ID = "_aQaevent.id", 
                    ANALYSISSUBQA_NAME = "_aQaevent.name",
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
                    ORG_NAME = "_organization.name", ORG_IS_ACTIVE = "_organization.isActive",
                    ORG_ADDRESS_ID = "_organization.addressId",

                    SAMPLE_PROJECT_ID = "_sampleProject.id",
                    SAMPLE_PROJECT_SAMPLE_ID = "_sampleProject.sampleId",
                    SAMPLE_PROJECT_PROJECT_ID = "_sampleProject.projectId",
                    SAMPLE_PROJECT_IS_PERMANENT = "_sampleProject.isPermanent",

                    PROJECT_ID = "_project.id", 
                    PROJECT_NAME = "_project.name",
                    PROJECT_DESCRIPTION = "_project.description",
                    PROJECT_STARTED_DATE = "_project.startedDate",
                    PROJECT_COMPLETED_DATE = "_project.completedDate",
                    PROJECT_IS_ACTIVE = "_project.isActive",
                    PROJECT_REFERENCE_TO = "_project.referenceTo",
                    PROJECT_OWNER_ID = "_project.ownerId",
                    PROJECT_SCRIPTLET_ID = "_project.scriptletId",

                    ANALYSIS_TEST_NAME = "_test.name", 
                    ANALYSIS_SECTION_NAME = "_section.name";
    
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ENV_ID, ENV_SAMPLE_ID, ENV_IS_HAZARDOUS, ENV_PRIORITY,
                                                  ENV_DESCRIPTION, ENV_COLLECTOR, ENV_COLLECTOR_PHONE,
                                                  ENV_SAMPLING_LOCATION, ENV_ADDRESS_ID, ID,
                                                  NEXT_ITEM_SEQUENCE, DOMAIN, ACCESSION_NUMBER, REVISION,
                                                  ENTERED_DATE, RECEIVED_DATE, RECEIVED_BY_ID,
                                                  COLLECTION_DATE, COLLECTION_TIME, STATUS_ID, PACKAGE_ID, 
                                                  CLIENT_REFERENCE, RELEASED_DATE, ADDR_ID, ADDR_MULTIPLE_UNIT,
                                                  ADDR_STREET_ADDRESS, ADDR_CITY, ADDR_STATE, ADDR_ZIP_CODE, 
                                                  ADDR_WORK_PHONE, ADDR_HOME_PHONE, ADDR_CELL_PHONE, ADDR_FAX_PHONE,
                                                  ADDR_EMAIL, ADDR_COUNTRY, ITEM_ID, ITEM_SAMPLE_ID, 
                                                  ITEM_SAMPLE_ITEM_ID, ITEM_ITEM_SEQUENCE, ITEM_TYPE_OF_SAMPLE_ID,
                                                  ITEM_SOURCE_OF_SAMPLE_ID, ITEM_SOURCE_OTHER, ITEM_CONTAINER_ID,
                                                  ITEM_CONTAINER_REFERENCE, ITEM_QUANTITY, ITEM_UNIT_OF_MEASURE_ID,
                                                  ANALYSIS_ID, ANALYSIS_SAMPLE_ITEM_ID, ANALYSIS_REVISION,
                                                  ANALYSIS_TEST_ID, ANALYSIS_SECTION_ID, ANALYSIS_PRE_ANALYSIS_ID,
                                                  ANALYSIS_PARENT_ANALYSIS_ID, ANALYSIS_PARENT_RESULT_ID,
                                                  ANALYSIS_IS_REPORTABLE, ANALYSIS_UNIT_OF_MEASURE_ID, ANALYSIS_STATUS_ID,
                                                  ANALYSIS_AVAILABLE_DATE, ANALYSIS_STARTED_DATE, ANALYSIS_COMPLETED_DATE,
                                                  ANALYSIS_RELEASED_DATE, ANALYSIS_PRINTED_DATE, ANALYSISQA_ID,
                                                  ANALYSISQA_ANALYSIS_ID, ANALYSISQA_QAEVENT_ID, ANALYSISQA_TYPE_ID,
                                                  ANALYSISQA_IS_BILLABLE, ANALYSISSUBQA_ID, ANALYSISSUBQA_NAME, 
                                                  ANALYSISSUBQA_DESCRIPTION, ANALYSISSUBQA_TEST_ID, ANALYSISSUBQA_TYPE_ID,
                                                  ANALYSISSUBQA_IS_BILLABLE, ANALYSISSUBQA_REPORTING_SEQUENCE,
                                                  ANALYSISSUBQA_REPORTING_TEXT, SAMPLEQA_ID, SAMPLEQA_SAMPLE_ID,
                                                  SAMPLEQA_QAEVENT_ID, SAMPLEQA_TYPE_ID,SAMPLEQA_IS_BILLABLE,
                                                  SAMPLESUBQA_ID, SAMPLESUBQA_NAME, SAMPLESUBQA_DESCRIPTION,
                                                  SAMPLESUBQA_TEST_ID, SAMPLESUBQA_TYPE_ID, SAMPLESUBQA_IS_BILLABLE,
                                                  SAMPLESUBQA_REPORTING_SEQUENCE, SAMPLESUBQA_REPORTING_TEXT,
                                                  SAMPLE_ORG_ID, SAMPLE_ORG_SAMPLE_ID, SAMPLE_ORG_ORGANIZATION_ID,
                                                  SAMPLE_ORG_TYPE_ID, ORG_ID, ORG_PARENT_ORGANIZATION_ID,
                                                  ORG_NAME, ORG_IS_ACTIVE, ORG_ADDRESS_ID, SAMPLE_PROJECT_ID,
                                                  SAMPLE_PROJECT_SAMPLE_ID, SAMPLE_PROJECT_PROJECT_ID,
                                                  SAMPLE_PROJECT_IS_PERMANENT, PROJECT_ID, PROJECT_NAME, PROJECT_DESCRIPTION,
                                                  PROJECT_STARTED_DATE, PROJECT_COMPLETED_DATE, PROJECT_IS_ACTIVE,
                                                  PROJECT_REFERENCE_TO, PROJECT_OWNER_ID, PROJECT_SCRIPTLET_ID,
                                                  ANALYSIS_TEST_NAME, ANALYSIS_SECTION_NAME));
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

    public static String getEnvSamplingLocation() {
        return ENV_SAMPLING_LOCATION;
    }

    public static String getEnvAddressId() {
        return ENV_ADDRESS_ID;
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

    public static String getAddrId() {
        return ADDR_ID;
    }

    public static String getAddrMultipleUnit() {
        return ADDR_MULTIPLE_UNIT;
    }

    public static String getAddrStreetAddress() {
        return ADDR_STREET_ADDRESS;
    }

    public static String getAddrCity() {
        return ADDR_CITY;
    }

    public static String getAddrState() {
        return ADDR_STATE;
    }

    public static String getAddrZipCode() {
        return ADDR_ZIP_CODE;
    }

    public static String getAddrWorkPhone() {
        return ADDR_WORK_PHONE;
    }

    public static String getAddrHomePhone() {
        return ADDR_HOME_PHONE;
    }

    public static String getAddrCellPhone() {
        return ADDR_CELL_PHONE;
    }

    public static String getAddrFaxPhone() {
        return ADDR_FAX_PHONE;
    }

    public static String getAddrEmail() {
        return ADDR_EMAIL;
    }

    public static String getAddrCountry() {
        return ADDR_COUNTRY;
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

    public static String getAnalysisAubQaName() {
        return ANALYSISSUBQA_NAME;
    }

    public static String getAnalysisSubqaDescription() {
        return ANALYSISSUBQA_DESCRIPTION;
    }

    public static String getAnalysisAubQaTestId() {
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

    public static String getAnalysisTestName() {
        return ANALYSIS_TEST_NAME;
    }
    
    public static String getAnalysisMethodName() {
        return "method";
    }

    public static String getAnalysisSectionName() {
        return ANALYSIS_SECTION_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from = "SampleEnvironmental _sampleEnvironmental ";

        from += ", IN (_sampleEnvironmental.sample) _sample ";
        
        if(where.indexOf("address.") > -1)
            from += ", IN (_sampleEnvironmentalse.address) _address ";
        
        if(where.indexOf("sampleProject.") > -1)
            from += ", IN (_sample.sampleProject) _sampleProject ";
            
        if(where.indexOf("sampleOrganization.") > -1)
            from += ", IN (_sample.sampleOrganization) _sampleOrganization ";
        
        if(where.indexOf("sampleItem.") > -1 || where.indexOf("analysis.") > -1)
            from += ", IN (_sample.sampleItem) _sampleItem";
        
        if(where.indexOf("analysis.") > -1)
            from += ", IN (_sampleItem.analysis) _analysis ";
        
        if(where.indexOf("sampleQAEvent.") > -1)
            from += ", IN (_sampleItem.sampleQAEvent) sampleQAEvent ";
        
        return from;
    }  
}   
