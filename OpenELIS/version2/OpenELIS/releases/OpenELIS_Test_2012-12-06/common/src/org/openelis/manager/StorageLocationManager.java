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
package org.openelis.manager;

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class StorageLocationManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer                     storageLocationId;
    protected StorageLocationViewDO       storageLocation;
    protected StorageLocationChildManager children;
    
    protected transient static StorageLocationManagerProxy proxy; 
    
    /**
     * This is a protected constructor.
     */
    protected StorageLocationManager() {
        children = null;
        storageLocation = null;
    }
    
    /**
     * Creates a new instance of this object. A default storage location object is
     * also created.
     */
    public static StorageLocationManager getInstance() {
        StorageLocationManager manager;
        
        manager = new StorageLocationManager();
        manager.storageLocation = new StorageLocationViewDO();
        
        return manager;
    }
    
    public Integer getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(Integer storageLocationId) {
        this.storageLocationId = storageLocationId;
    }

    public StorageLocationViewDO getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(StorageLocationViewDO storageLocation) {
        this.storageLocation = storageLocation;
    }
    
    // service methods
    public static StorageLocationManager fetchById(Integer id) throws Exception {        
        return proxy().fetchById(id);
    }
    
    public static StorageLocationManager fetchWithChildren(Integer id) throws Exception {
        return proxy().fetchWithChildren(id);
    }
    
    public StorageLocationManager add() throws Exception {
        return proxy().add(this);
    }
    
    public StorageLocationManager update() throws Exception {
        return proxy().update(this);
    }
    
    public StorageLocationManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(storageLocationId);
    }
    
    public StorageLocationManager abortUpdate() throws Exception {
        return proxy().abortUpdate(storageLocationId);
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
  
    // Convenience methods

    /**
     * Returns a concatenated storage name for display purposes.  
     */
    public static String getLocationForDisplay(String name, String unit,
                                               String location) {
        return name + ", " + unit + " " + location;
    }

    //
    // other managers
    //
    public StorageLocationChildManager getChildren() throws Exception {
        if (children == null) {
            if (storageLocation.getId() != null) {
                try {
                    children = StorageLocationChildManager.fetchByParentStorageLocationId(storageLocation.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (children == null)
                children = StorageLocationChildManager.getInstance();
        }
        return children;
    }
    
    private static StorageLocationManagerProxy proxy() {
        if (proxy == null)
            proxy = new StorageLocationManagerProxy();
        
        return proxy;
    }

}
