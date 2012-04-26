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
 * the caches used for the various todo lists  
 */
public class SampleCacheVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id, statusId, accessionNumber;                              
    protected String          domain, qaeventResultOverride, 
                              domainSpecificField, reportToName;
    protected Datetime        receivedDate, collectionDate, collectionTime;
    
    public SampleCacheVO() {        
    }
    
    public SampleCacheVO(Integer id, Integer statusId, String domain, Integer accessionNumber,
                         Date receivedDate, Date collectionDate, Date collectionTime, 
                         String qaeventResultOverride, String reportToName) {
        setId(id);
        setStatusId(statusId);
        setDomain(domain);
        setAccessionNumber(accessionNumber);
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setQaeventResultOverride(qaeventResultOverride);
        setReportToName(reportToName);
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getStatusId() {
        return statusId;
    }
    
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }   
        
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String sampleDomain) {
        this.domain = DataBaseUtil.trim(sampleDomain);
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

    public String getQaeventResultOverride() {
        return qaeventResultOverride;
    }
    
    public void setQaeventResultOverride(String qaeventResultOverride) {
        this.qaeventResultOverride = DataBaseUtil.trim(qaeventResultOverride);
    }
    
    public String getDomainSpecificField() {
        return domainSpecificField;
    }

    public void setDomainSpecificField(String domainSpecificField) {
        this.domainSpecificField = DataBaseUtil.trim(domainSpecificField);
    }
    
    public String getReportToName() {
        return reportToName;
    }
    
    public void setReportToName(String reportToName) {
        this.reportToName = DataBaseUtil.trim(reportToName);
    }
}