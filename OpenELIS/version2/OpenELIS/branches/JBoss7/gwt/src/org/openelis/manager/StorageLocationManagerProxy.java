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

import org.openelis.modules.storageLocation.client.StorageLocationService;

public class StorageLocationManagerProxy {

    public StorageLocationManagerProxy() {
    }

    public StorageLocationManager fetchById(Integer id) throws Exception {
        return StorageLocationService.get().fetchById(id);
    }

    public StorageLocationManager fetchWithChildren(Integer id) throws Exception {
        return StorageLocationService.get().fetchWithChildren(id);
    }

    public StorageLocationManager add(StorageLocationManager man) throws Exception {
        return StorageLocationService.get().add(man);
    }

    public StorageLocationManager update(StorageLocationManager man) throws Exception {
        return StorageLocationService.get().update(man);
    }
    
    /*
    public StorageLocationManager delete(StorageLocationManager man) throws Exception {
        return StorageLocationService.get().delete(man);
    }
    */

    public StorageLocationManager fetchForUpdate(Integer id) throws Exception {
        return StorageLocationService.get().fetchForUpdate(id);
    }

    public StorageLocationManager abortUpdate(Integer id) throws Exception {
        return StorageLocationService.get().abortUpdate(id);
    }

    public void validate(StorageLocationManager man) throws Exception {
    }
    
    public void validateForDelete(StorageLocationManager man) throws Exception {
    }
}
