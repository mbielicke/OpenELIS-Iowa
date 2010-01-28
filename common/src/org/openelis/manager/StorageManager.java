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
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class StorageManager implements RPC {

    private static final long                      serialVersionUID = 1L;
    protected Integer                              referenceId, referenceTableId;
    protected ArrayList<StorageViewDO>             storageList;

    protected transient static StorageManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static StorageManager getInstance() {
        StorageManager sm;

        sm = new StorageManager();
        sm.storageList = new ArrayList<StorageViewDO>();

        return sm;
    }

    public static StorageManager fetchByRefTableRefId(Integer tableId, Integer id) throws Exception {
        return proxy().fetch(tableId, id);
    }

    // service methods
    public StorageManager add() throws Exception {
        return proxy().add(this);
    }

    public StorageManager update() throws Exception {
        return proxy().update(this);
    }

    public StorageViewDO getStorageAt(int i) {
        return storageList.get(i);
    }

    public void addStorage(StorageViewDO storage) throws Exception {
        if (storageList == null)
            storageList = new ArrayList<StorageViewDO>();

        storageList.add(0, storage);
    }

    public void removeStorageAt(int i) {
        if (storageList == null || i >= storageList.size())
            return;

        storageList.remove(i);
    }

    public int count() {
        if (storageList == null)
            return 0;
    
        return storageList.size();
    }

    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getReferenceId() {
        return referenceId;
    }

    void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    Integer getReferenceTableId() {
        return referenceTableId;
    }

    void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    ArrayList<StorageViewDO> getStorages() {
        return storageList;
    }

    void setStorages(ArrayList<StorageViewDO> storages) {
        storageList = storages;
    }

    private static StorageManagerProxy proxy() {
        if (proxy == null)
            proxy = new StorageManagerProxy();
    
        return proxy;
    }
}
