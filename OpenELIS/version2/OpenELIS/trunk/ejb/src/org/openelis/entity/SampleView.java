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
package org.openelis.entity;

/**
  * Sample View Entity POJO for database 
  */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

@NamedQueries( {                  
    @NamedQuery( name = "SampleView.FetchBySampleId",
                query = "select new org.openelis.domain.SampleViewVO(sample_id," +
                		"domain, accession_number, sample_revision, received_date," +
                		"collection_date, collection_time, sample_status_id, client_reference," +
                		"report_to_id, report_to_name, collector, location, location_city," +
                		"project_name, pws_number0, pws_name, sdwis_facility_id," +
                		"analysis_id, analysis_revision, analysis_status_id, test_reporting_description," +
                		"method_reporting_description)"
                      + " from SampleView sv where sv.sampleId = :id"
                      + " order by sv.accessionNumber, sv.test_reporting_description, sv.method_reporting_description"),
    @NamedQuery( name = "SampleView.FetchBySampleIds",
                query = "select new org.openelis.domain.SampleViewVO(sample_id," +
                        "domain, accession_number, sample_revision, received_date," +
                        "collection_date, collection_time, sample_status_id, client_reference," +
                        "report_to_id, report_to_name, collector, location, location_city," +
                        "project_name, pws_number0, pws_name, sdwis_facility_id," +
                        "analysis_id, analysis_revision, analysis_status_id, test_reporting_description," +
                        "method_reporting_description)"
                      + " from SampleView sv where sv.sampleId in (:ids)"
                      + " order by sv.accessionNumber, sv.test_reporting_description, sv.method_reporting_description")})
@Entity
@Table(name = "sample_view")
public class SampleView  {
    
    @Id
    @Column(name = "sample_id")
    private Integer                     sampleId;

    @Column(name = "domain")
    private String                      domain;

    @Column(name = "accession_number")
    private Integer                     accessionNumber;
    
    @Column(name = "sample_revision")
    private Integer                     sampleRevision;
    
    @Column(name = "received_date")
    private Date                        receivedDate;

    @Column(name = "collection_date")
    private Date                        collectionDate;

    @Column(name = "collection_time")
    private Date                        collectionTime;

    @Column(name = "sample_status_id")
    private Integer                     sampleStatusId;

    @Column(name = "client_reference")
    private String                      clientReference;    
    
    @Column(name = "report_to_id")
    private Integer                     reportToId;

    @Column(name = "report_to_name")
    private String                      reportToName;

    @Column(name = "collector")
    private String                      collector;
    
    @Column(name = "location")
    private String                      location;
    
    @Column(name = "location_city")
    private String                      locationCity;
    
    @Column(name = "project_name")
    private String                      projectName;
    
    @Column(name = "pws_number0")
    private String                      pwsNumber0;
    
    @Column(name = "pws_name")
    private String                      pwsName;
    
    @Column(name = "sdwis_facility_id")
    private String                      sdwisFacilityId;
    
    @Column(name = "analysis_id")
    private Integer                     analysisId;

    @Column(name = "analysis_revision")
    private Integer                     analysisRevision;

    @Column(name = "analysis_status_id")
    private Integer                     analysisStatusId;

    @Column(name = "test_reporting_description")
    private String                      testReportingDescription;
    
    @Column(name = "method_reporting_description")
    private String                      methodReportingDescription;
    
    public Integer getSampleId() {
        return sampleId;
    }

    public String getDomain() {
        return domain;
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }
    
    public Integer getSampleRevision() {
        return sampleRevision;
    }

    public Datetime getReceivedDate() {
        return DataBaseUtil.toYD(receivedDate);
    }
    
    public Datetime getCollectionDate() {
        return DataBaseUtil.toYD(collectionDate);
    }
    
    public Datetime getCollectionTime() {
        return DataBaseUtil.toHM(collectionTime);
    }

    public Integer getSampleStatusId() {
        return sampleStatusId;
    }

    public String getClientReference() {
        return clientReference;
    }

    public Integer getReportToId() {
        return reportToId;
    }

    public String getReportToName() {
        return reportToName;
    }

    public String getCollector() {
        return collector;
    }

    public String getLocation() {
        return location;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPwsNumber0() {
        return pwsNumber0;
    }

    public String getPwsName() {
        return pwsName;
    }

    public String getSdwisFacilityId() {
        return sdwisFacilityId;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public Integer getAnalysisRevision() {
        return analysisRevision;
    }

    public Integer getAnalysisStatusId() {
        return analysisStatusId;
    }

    public String getTestReportingDescription() {
        return testReportingDescription;
    }

    public String getMethodReportingDescription() {
        return methodReportingDescription;
    }
}   