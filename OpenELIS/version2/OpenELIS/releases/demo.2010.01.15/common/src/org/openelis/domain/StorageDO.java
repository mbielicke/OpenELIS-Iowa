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

import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table storage.  
 */

public class StorageDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, referenceId, referenceTableId, storageLocationId, systemUserId;
    protected String          storageLocation;
    protected Datetime        checkin, checkout;

    public StorageDO() {
    }

    public StorageDO(Integer id, Integer referenceId, Integer referenceTableId,
                     Integer storageLocationId, Date checkin, Date checkout, Integer systemUserId) {
        setId(id);
        setReferenceId(referenceId);
        setReferenceTableId(referenceTableId);
        setStorageLocationId(storageLocationId);
        setCheckin(DataBaseUtil.toYM(checkin));
        setCheckout(DataBaseUtil.toYM(checkout));
        setSystemUserId(systemUserId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
        _changed = true;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
        _changed = true;
    }

    public Integer getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(Integer storageLocationId) {
        this.storageLocationId = storageLocationId;
        _changed = true;
    }

    public Datetime getCheckin() {
        return checkin;
    }

    public void setCheckin(Datetime checkin) {
        this.checkin = DataBaseUtil.toYM(checkin);
        _changed = true;
    }

    public Datetime getCheckout() {
        return checkout;
    }

    public void setCheckout(Datetime checkout) {
        this.checkout = DataBaseUtil.toYM(checkout);
        _changed = true;
}

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        this.systemUserId = systemUserId;
        _changed = true;
    }
}
