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

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class SampleWebMeta implements Meta, MetaMap {

    private static final String    ID = "_sample.id",
                    ACCESSION_NUMBER = "_sample.accessionNumber",
                    ACCESSION_NUMBER_FROM = "_display.accessionNumberFrom",
                    ACCESSION_NUMBER_TO = "_display.accessionNumberTo",
                    COLLECTION_DATE = "_sample.collectionDate",
                    COLLECTION_DATE_FROM = "_display.collectionDateFrom",
                    COLLECTION_DATE_TO = "_display.collectionDateTo",
                    COLLECTION_TIME = "_sample.collectionTime",
                    STATUS_ID = "_sample.statusId",
                    CLIENT_REFERENCE = "_sample.clientReference",
                    RELEASED_DATE = "_sample.releasedDate",
                    RELEASED_DATE_FROM = "_display.releasedDateFrom",
                    RELEASED_DATE_TO = "_display.releasedDateTo",

                    // sample environmental
                    ENV_ID = "_sampleEnvironmental.id",
                    ENV_SAMPLE_ID = "_sampleEnvironmental.sampleId",
                    ENV_COLLECTOR = "_sampleEnvironmental.collector",
                    ENV_LOCATION = "_sampleEnvironmental.location",
                    ENV_LOCATION_ADDRESS_ID = "_sampleEnvironmental.locationAddressId",

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
                    WELL_REPORT_TO_ADDR_CITY = "_privateWellReportToAddress.city",

                    WELL_LOCATION_ADDR_ID = "_wellLocationAddress.id",
                    WELL_LOCATION_ADDR_CITY = "_wellLocationAddress.city",

                    // sample sdwis
                    SDWIS_ID = "_sampleSDWIS.id", SDWIS_SAMPLE_ID = "_sampleSDWIS.sampleId",
                    SDWIS_PWS_ID = "_sampleSDWIS.pwsId",
                    SDWIS_FACILITY_ID = "_sampleSDWIS.facilityId",
                    SDWIS_LOCATION = "_sampleSDWIS.location",
                    SDWIS_COLLECTOR = "_sampleSDWIS.collector",

                    PWS_NUMBER0 = "_pws.number0", PWS_ID = "_pws.id", PWS_NAME = "_pws.name",

                    LOCATION_ADDR_CITY = "_locationAddress.city",

                    ITEM_ID = "_sampleItem.id", ITEM_SAMPLE_ID = "_sampleItem.sampleId",
                    ITEM_SAMPLE_ITEM_ID = "_sampleItem.sampleItemId",

                    ANALYSIS_SAMPLE_ITEM_ID = "_analysis.sampleItemId",
                    ANALYSIS_REVISION = "_analysis.revision",
                    ANALYSIS_IS_REPORTABLE = "_analysis.isReportable",
                    ANALYSIS_STATUS_ID = "_analysis.statusId",

                    SAMPLE_ORG_ID = "_sampleOrganization.id",
                    SAMPLE_ORG_SAMPLE_ID = "_sampleOrganization.sampleId",
                    SAMPLE_ORG_ORGANIZATION_ID = "_sampleOrganization.organizationId",
                    SAMPLE_ORG_TYPE_ID = "_sampleOrganization.typeId",
                    
                    SAMPLE_PROJECT_ID = "_sampleProject.id",
                    SAMPLE_PROJECT_SAMPLE_ID = "_sampleProject.sampleId",
                    SAMPLE_PROJECT_PROJECT_ID = "_sampleProject.projectId",
                    SAMPLE_PROJECT_IS_PERMANENT = "_sampleProject.isPermanent",
                    
                    PROJECT_ID = "_project.id", 
                    PROJECT_NAME = "_project.name",                    
                    PROJECT_DESCRIPTION = "_project.description",
                        
                    ANALYSIS_TEST_NAME = "_test.name", 
                    ANALYSIS_METHOD_NAME = "_method.name";

    private static HashSet<String> names;

    static {
        //TODO format
        names = new HashSet<String>(Arrays.asList(ID,
                                                  ACCESSION_NUMBER, ACCESSION_NUMBER_FROM, ACCESSION_NUMBER_TO,
                                                  COLLECTION_DATE, COLLECTION_DATE_FROM, COLLECTION_DATE_TO, 
                                                  COLLECTION_TIME,
                                                  STATUS_ID,
                                                  CLIENT_REFERENCE,
                                                  RELEASED_DATE, RELEASED_DATE_FROM, RELEASED_DATE_TO, 
                                                  ENV_ID,
                                                  ENV_SAMPLE_ID,
                                                  ENV_COLLECTOR,
                                                  ENV_LOCATION,
                                                  ENV_LOCATION_ADDRESS_ID,
                                                  ENV_LOCATION_ADDRESS_ID,
                                                  LOCATION_ADDR_CITY,
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
                                                  WELL_REPORT_TO_ADDR_CITY,
                                                  WELL_LOCATION_ADDR_CITY,
                                                  SDWIS_ID,
                                                  SDWIS_SAMPLE_ID,
                                                  SDWIS_PWS_ID,
                                                  SDWIS_FACILITY_ID,
                                                  SDWIS_LOCATION,
                                                  SDWIS_COLLECTOR,
                                                  PWS_NUMBER0,
                                                  PWS_ID,
                                                  PWS_NAME,
                                                  ITEM_ID,
                                                  ITEM_SAMPLE_ID,
                                                  ITEM_SAMPLE_ITEM_ID,
                                                  ANALYSIS_SAMPLE_ITEM_ID,
                                                  ANALYSIS_REVISION,
                                                  ANALYSIS_IS_REPORTABLE,
                                                  ANALYSIS_STATUS_ID,
                                                  SAMPLE_ORG_ID,
                                                  SAMPLE_ORG_SAMPLE_ID,
                                                  SAMPLE_ORG_ORGANIZATION_ID,
                                                  SAMPLE_ORG_TYPE_ID, 
                                                  SAMPLE_PROJECT_ID, SAMPLE_PROJECT_SAMPLE_ID,
                                                  SAMPLE_PROJECT_PROJECT_ID,
                                                  SAMPLE_PROJECT_IS_PERMANENT,PROJECT_ID, 
                                                  PROJECT_NAME, PROJECT_DESCRIPTION,
                                                  ANALYSIS_TEST_NAME,
                                                  ANALYSIS_METHOD_NAME));
    }

    public static String getId() {
        return ID;
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

    public static String getReleasedDate() {
        return RELEASED_DATE;
    }
    
    public static String getReleasedDateFrom() {
        return RELEASED_DATE_FROM;
    }
    
    public static String getReleasedDateTo() {
        return RELEASED_DATE_TO;
    }

    public static String getEnvLocationAddrId() {
        return ENV_LOCATION_ADDRESS_ID;
    }

    public static String getLocationAddrCity() {
        return LOCATION_ADDR_CITY;
    }

    public static String getEnvId() {
        return ENV_ID;
    }

    public static String getEnvSampleId() {
        return ENV_SAMPLE_ID;
    }

    public static String getEnvCollector() {
        return ENV_COLLECTOR;
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

    public static String getWellReportToAddressCity() {
        return WELL_REPORT_TO_ADDR_CITY;
    }

    public static String getWellLocationAddrId() {
        return WELL_LOCATION_ADDR_ID;
    }

    public static String getWellLocationAddrCity() {
        return WELL_LOCATION_ADDR_CITY;
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

    public static String getSDWISFacilityId() {
        return SDWIS_FACILITY_ID;
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

    public static String getItemId() {
        return ITEM_ID;
    }

    public static String getItemSampleId() {
        return ITEM_SAMPLE_ID;
    }

    public static String getItemSampleItemId() {
        return ITEM_SAMPLE_ITEM_ID;
    }

    public static String getAnalysisSampleItemId() {
        return ANALYSIS_SAMPLE_ITEM_ID;
    }

    public static String getAnalysisRevision() {
        return ANALYSIS_REVISION;
    }

    public static String getAnalysisIsReportable() {
        return ANALYSIS_IS_REPORTABLE;
    }

    public static String getAnalysisStatusId() {
        return ANALYSIS_STATUS_ID;
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

        // sample env
        if (where.indexOf("sampleEnvironmental.") > -1) {
            from += ", IN (_sample.sampleEnvironmental) _sampleEnvironmental ";
            from += " left join _sampleEnvironmental.locationAddress _locationAddress ";
        }

        // sample private well
        if (where.indexOf("samplePrivateWell.") > -1 ||
            where.indexOf("privateWellReportToAddress") > -1 ||
            where.indexOf("wellOrganization.") > -1 || where.indexOf("wellLocationAddress.") > -1)
            from += ", IN (_sample.samplePrivateWell) _samplePrivateWell ";

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
        
        /*if (where.indexOf("sampleProject.") > -1) {
            from += ", IN (_sample.sampleProject) _sampleProject ";    
            from += ", IN (_sampleProject.project) _project ";
        }*/

        // common sample fields
        /*if (where.indexOf("project.") > -1) {
            if (where.indexOf("sampleProject.") == -1)
                from += ", IN (_sample.sampleProject) _sampleProject ";
            from += ", IN (_sampleProject.project) _project ";
        }*/
        
        if (where.indexOf("project.") > -1){
            from += ", IN (_sample.sampleProject) _sampleProject ";
            from += ", IN (_sampleProject.project) _project ";
        }

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
            where.indexOf("aQaevent.") > -1)
            from += ", IN (_sampleItem.analysis) _analysis ";

        if (where.indexOf("test.") > -1 || where.indexOf("method.") > -1)
            from += ", IN (_analysis.test) _test ";

        if (where.indexOf("method.") > -1)
            from += ", IN (_test.method) _method ";

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

        return from;
    }
}
