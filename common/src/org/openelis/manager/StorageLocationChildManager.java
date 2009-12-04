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

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.RPC;

public class StorageLocationChildManager implements RPC {

    private static final long                                   serialVersionUID = 1L;

    protected Integer                                           parentStorageLocationId;
    protected String                                            parentStorageLocationName;
    protected ArrayList<StorageLocationViewDO>                  children, deleted;

    protected transient static StorageLocationChildManagerProxy proxy;

    protected StorageLocationChildManager() {
    }

    public static StorageLocationChildManager getInstance() {
        return new StorageLocationChildManager();
    }

    public int count() {
        if (children == null)
            return 0;

        return children.size();
    }

    public StorageLocationViewDO getChildAt(int i) {
        return children.get(i);
    }

    public void setChildAt(StorageLocationViewDO child, int i) {
        if (children == null)
            children = new ArrayList<StorageLocationViewDO>();
        children.set(i, child);
    }

    public void addChild(StorageLocationViewDO child) {
        if (children == null)
            children = new ArrayList<StorageLocationViewDO>();
        children.add(child);
    }

    public void addChildAt(StorageLocationViewDO child, int i) {
        if (children == null)
            children = new ArrayList<StorageLocationViewDO>();
        children.add(i, child);
    }

    public void removeChildAt(int i) {
        StorageLocationViewDO tmp;

        if (children == null || i >= children.size())
            return;

        tmp = children.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<StorageLocationViewDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static StorageLocationChildManager fetchByParentStorageLocationId(Integer id)
                                                                                        throws Exception {
        return proxy().fetchByParentStorageLocationId(id);
    }

    public StorageLocationChildManager add() throws Exception {
        return proxy().add(this);
    }

    public StorageLocationChildManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    Integer getParentStorageLocationId() {
        return parentStorageLocationId;
    }

    void setParentStorageLocationId(Integer parentStorageLocationId) {
        this.parentStorageLocationId = parentStorageLocationId;
    }
    
    String getParentStorageLocationName() {
        return parentStorageLocationName;
    }

    void setParentStorageLocationName(String parentStorageLocationName) {
        this.parentStorageLocationName = parentStorageLocationName;
    }

    ArrayList<StorageLocationViewDO> getChildren() {
        return children;
    }

    void setChildren(ArrayList<StorageLocationViewDO> children) {
        this.children = children;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    StorageLocationViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static StorageLocationChildManagerProxy proxy() {
        if (proxy == null)
            proxy = new StorageLocationChildManagerProxy();
        return proxy;
    }
}
