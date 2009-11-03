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

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

public class SampleDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer nextItemSequence;
    protected String domain;
    protected Integer accessionNumber;
    protected Integer revision;
    protected Datetime enteredDate;
    protected Datetime receivedDate;
    protected Integer receivedById;
    protected Datetime collectionDate;
    protected Datetime collectionTime;
    protected Integer statusId;
    protected Integer packageId;
    protected String clientReference;
    protected Datetime releasedDate;
    
    public SampleDO(){
        
    }
    
    public SampleDO(Integer id, Integer nextItemSequence, String domain, Integer accessionNumber,
                    Integer revision, Date enteredDate, Date receivedDate, Integer receivedById,
                    Date collectionDate, Date collectionTime, Integer statusId, 
                    Integer packageId, String clientReference, Date releasedDate){
        
        setId(id);
        setNextItemSequence(nextItemSequence);
        setDomain(domain);
        setAccessionNumber(accessionNumber);
        setRevision(revision);
        setEnteredDate(enteredDate);
        setReceivedDate(receivedDate);
        setReceivedById(receivedById);
        setCollectionDate(collectionDate);
        setCollectionTime(collectionTime);
        setStatusId(statusId);
        setPackageId(packageId);
        setClientReference(clientReference);
        setReleasedDate(releasedDate);
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getNextItemSequence() {
        return nextItemSequence;
    }
    public void setNextItemSequence(Integer nextItemSequence) {
        this.nextItemSequence = nextItemSequence;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public Integer getAccessionNumber() {
        return accessionNumber;
    }
    public void setAccessionNumber(Integer accessionNumber) {
        this.accessionNumber = accessionNumber;
    }
    public Integer getRevision() {
        return revision;
    }
    public void setRevision(Integer revision) {
        this.revision = revision;
    }
    public Datetime getEnteredDate() {
        return enteredDate;
    }
    public void setEnteredDate(Date enteredDate) {
        this.enteredDate = new Datetime(Datetime.YEAR, Datetime.DAY, enteredDate);
    }
    public Datetime getReceivedDate() {
        return receivedDate;
    }
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = new Datetime(Datetime.YEAR, Datetime.DAY, receivedDate);
    }
    public Integer getReceivedById() {
        return receivedById;
    }
    public void setReceivedById(Integer receivedById) {
        this.receivedById = receivedById;
    }
    public Datetime getCollectionDate() {
        return collectionDate;
    }
    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = new Datetime(Datetime.YEAR, Datetime.DAY, collectionDate);
    }
    public Datetime getCollectionTime() {
        return collectionTime;
    }
    public void setCollectionTime(Date collectionTime) {
        this.collectionTime = new Datetime(Datetime.HOUR, Datetime.MINUTE, collectionTime);
    }
    public Integer getStatusId() {
        return statusId;
    }
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
    public Integer getPackageId() {
        return packageId;
    }
    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }
    public String getClientReference() {
        return clientReference;
    }
    public void setClientReference(String clientReference) {
        this.clientReference = DataBaseUtil.trim(clientReference);
    }
    public Datetime getReleasedDate() {
        return releasedDate;
    }
    public void setReleasedDate(Date releasedDate) {
        this.releasedDate = new Datetime(Datetime.YEAR, Datetime.DAY, releasedDate);
    }    
}
