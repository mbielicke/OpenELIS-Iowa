package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.gwt.common.RPC;
import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

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
        this.enteredDate = new Datetime(Datetime.YEAR, Datetime.SECOND, enteredDate);
    }
    public Datetime getReceivedDate() {
        return receivedDate;
    }
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = new Datetime(Datetime.YEAR, Datetime.SECOND, receivedDate);
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
        this.collectionDate = new Datetime(Datetime.YEAR, Datetime.SECOND, collectionDate);
    }
    public Datetime getCollectionTime() {
        return collectionTime;
    }
    public void setCollectionTime(Date collectionTime) {
        this.collectionTime = new Datetime(Datetime.HOUR, Datetime.SECOND, collectionTime);
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
        this.releasedDate = new Datetime(Datetime.YEAR, Datetime.SECOND, releasedDate);
    }    
}
