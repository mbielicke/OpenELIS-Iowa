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
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * Class represents the fields in database table sample.
 */

public class SampleDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, nextItemSequence, accessionNumber, revision, orderId, receivedById,
                              statusId, packageId;
    protected Datetime        enteredDate, receivedDate, collectionDate, collectionTime,
                              releasedDate;
    protected String          domain, clientReference;

    public SampleDO() {
    }

    public SampleDO(Integer id, Integer nextItemSequence, String domain, Integer accessionNumber,
                    Integer revision, Integer orderId, Date enteredDate, Date receivedDate, Integer receivedById,
                    Date collectionDate, Date collectionTime, Integer statusId, Integer packageId,
                    String clientReference, Date releasedDate) {
        setId(id);
        setNextItemSequence(nextItemSequence);
        setDomain(domain);
        setAccessionNumber(accessionNumber);
        setRevision(revision);
        setOrderId(orderId);
        setEnteredDate(DataBaseUtil.toYM(enteredDate));
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setReceivedById(receivedById);
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setStatusId(statusId);
        setPackageId(packageId);
        setClientReference(clientReference);
        setReleasedDate(DataBaseUtil.toYM(releasedDate));
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getNextItemSequence() {
        return nextItemSequence;
    }

    public void setNextItemSequence(Integer nextItemSequence) {
        this.nextItemSequence = nextItemSequence;
        _changed = true;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = DataBaseUtil.trim(domain);
        _changed = true;
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(Integer accessionNumber) {
        this.accessionNumber = accessionNumber;
        _changed = true;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
        _changed = true;
    }
    
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
        _changed = true;
    }

    public Datetime getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(Datetime enteredDate) {
        this.enteredDate = DataBaseUtil.toYM(enteredDate);
        _changed = true;
    }

    public Datetime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Datetime receivedDate) {
        this.receivedDate = DataBaseUtil.toYM(receivedDate);
        _changed = true;
}

    public Integer getReceivedById() {
        return receivedById;
    }

    public void setReceivedById(Integer receivedById) {
        this.receivedById = receivedById;
        _changed = true;
    }

    public Datetime getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Datetime collectionDate) {
        this.collectionDate = DataBaseUtil.toYD(collectionDate);
        _changed = true;
    }

    public Datetime getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Datetime collectionTime) {
        this.collectionTime = DataBaseUtil.toHM(collectionTime);
        _changed = true;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        _changed = true;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
        _changed = true;
    }

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        this.clientReference = DataBaseUtil.trim(clientReference);
        _changed = true;
    }

    public Datetime getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(Datetime releasedDate) {
        this.releasedDate = DataBaseUtil.toYM(releasedDate);
        _changed = true;
    }
}
