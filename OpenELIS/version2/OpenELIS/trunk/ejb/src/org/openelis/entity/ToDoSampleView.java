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
  * ToDo Sample View Entity POJO for database 
  */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

@NamedQueries( {                  
    @NamedQuery( name = "ToDoSampleView.FetchBySampleStatusId",
                query = "select distinct new org.openelis.domain.ToDoSampleViewVO(sv.sampleId, sv.domain, sv.accessionNumber," +
                		"sv.sampleStatusId, sv.receivedDate, sv.collectionDate, sv.collectionTime, sv.primaryOrganizationName," +
                		"sv.description, sv.sampleResultOverride)"
                      + " from ToDoSampleView sv where sv.sampleStatusId = :statusId order by sv.accessionNumber")})
@Entity
@Table(name = "todo_sample_view")
public class ToDoSampleView  {
    
    @Id
    @Column(name = "sample_id")
    private Integer                     sampleId;

    @Column(name = "domain")
    private String                      domain;

    @Column(name = "accession_number")
    private Integer                     accessionNumber;
    
    @Column(name = "sample_status_id")
    private Integer                     sampleStatusId;
    
    @Column(name = "received_date")
    private Date                        receivedDate;

    @Column(name = "collection_date")
    private Date                        collectionDate;

    @Column(name = "collection_time")
    private Date                        collectionTime;

    @Column(name = "primary_organization_name")
    private String                      primaryOrganizationName;

    @Column(name = "description")
    private String                      description;    

    @Column(name = "sample_result_override")
    private String                      sampleResultOverride;
    
    public Integer getSampleId() {
        return sampleId;
    }

    public String getDomain() {
        return domain;
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }
    
    public Integer getSampleStatusId() {
        return sampleStatusId;
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

    public String getPrimaryOrganizationName() {
        return primaryOrganizationName;
    }

    public String getDescription() {
        return description;
    }

    public String getAnalysisResultOverride() {
        return sampleResultOverride;
    }
}   