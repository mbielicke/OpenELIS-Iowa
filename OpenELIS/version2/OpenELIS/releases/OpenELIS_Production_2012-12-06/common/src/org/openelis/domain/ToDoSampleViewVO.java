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
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;
/** 
 * This class's objects store the data for the individual records that populate
 * the todo lists for samples
 */
public class ToDoSampleViewVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer           sampleId, accessionNumber, sampleStatusId;
    protected String            domain, primaryOrganizationName, sampleResultOverride,
                                 description;
    protected Datetime          receivedDate, collectionDate, collectionTime;
    
    public ToDoSampleViewVO() {        
    }
    
    public ToDoSampleViewVO(Integer sampleId, String domain, Integer accessionNumber, 
                            Date receivedDate, Date collectionDate, Date collectionTime,
                            String primaryOrganizationName, String description,
                            Integer sampleStatusId, String sampleResultOverride) {
        setSampleId(sampleId);
        setDomain(domain);
        setAccessionNumber(accessionNumber);
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setPrimaryOrganizationName(primaryOrganizationName);
        setDescription(description);
        setSampleStatusId(sampleStatusId);
        setSampleResultOverride(sampleResultOverride);
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = DataBaseUtil.trim(domain);
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(Integer accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public Datetime getReceivedDate() {
        return receivedDate;
    }
    
    public void setReceivedDate(Datetime receivedDate) {
        this.receivedDate = DataBaseUtil.toYM(receivedDate);
    }
    
    public Datetime getCollectionDate() {
        return collectionDate;
    }
    
    public void setCollectionDate(Datetime collectionDate) {
        this.collectionDate = DataBaseUtil.toYD(collectionDate);
    }
    
    public Datetime getCollectionTime() {
        return collectionTime;
    }
    
    public void setCollectionTime(Datetime collectionTime) {
        this.collectionTime = DataBaseUtil.toHM(collectionTime);
    }

    public String getPrimaryOrganizationName() {
        return primaryOrganizationName;
    }

    public void setPrimaryOrganizationName(String primaryOrganizationName) {
        this.primaryOrganizationName = DataBaseUtil.trim(primaryOrganizationName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }
    
    public Integer getSampleStatusId() {
        return sampleStatusId;
    }

    public void setSampleStatusId(Integer sampleStatusId) {
        this.sampleStatusId = sampleStatusId;
    }

    public String getSampleResultOverride() {
        return sampleResultOverride;
    }

    public void setSampleResultOverride(String sampleResultOverride) {
        this.sampleResultOverride = DataBaseUtil.trim(sampleResultOverride);
    }
}