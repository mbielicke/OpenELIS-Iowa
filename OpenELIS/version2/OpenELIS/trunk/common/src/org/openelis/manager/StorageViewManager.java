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

import java.io.Serializable;

import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.meta.StorageMeta;

public class StorageViewManager implements Serializable {

    private static final long        serialVersionUID = 1L;
    protected StorageManager         current, history;
    protected StorageLocationManager storageLocation;

    protected transient static StorageViewManagerProxy proxy;
    
    /**
     * This is a protected constructor. See the two static methods for
     * allocation.
     */
    protected StorageViewManager() {
        current = null;
        history = null;
        storageLocation = null;
    }

    /**
     * Creates a new instance of this object. A default storageLocation object is
     * also created.
     */
    public static StorageViewManager getInstance() {
        StorageViewManager manager;

        manager = new StorageViewManager();

        return manager;
    }
    
    public static StorageViewManager fetchById(Integer id) throws Exception {
        StorageLocationManager slm;
        StorageViewManager svm;
        
        slm = StorageLocationManager.fetchById(id);
        svm = StorageViewManager.getInstance();
         
        svm.storageLocation = slm;
        svm.storageLocation.setStorageLocationId(id);
        
        return svm;
    }
    
    public static StorageViewManager fetchWithCurrent(Integer id) throws Exception {
        StorageLocationManager slm;
        StorageManager sm;
        StorageViewManager svm;
        
        slm = StorageLocationManager.fetchById(id);
        sm = StorageManager.fetchCurrentByLocationId(id);
        svm = StorageViewManager.getInstance();
         
        svm.storageLocation = slm;
        svm.current = sm;
        
        return svm;
    }
    
    public static StorageViewManager fetchWithHistory(Integer id, int first, int max) throws Exception {
        StorageLocationManager slm;
        StorageManager sm;
        StorageViewManager svm;
        Query query;
        QueryData field;
        
        slm = StorageLocationManager.fetchById(id);
        
        query = new Query();
        
        field = new QueryData();
        field.key = StorageMeta.getStorageLocationId();
        field.query = id.toString();
        field.type = QueryData.Type.INTEGER;            
        query.setFields(field);
        
        query.setPage(first);
        
        sm = StorageManager.fetchHistoryByLocationId(id, first, max);
        svm = StorageViewManager.getInstance();
         
        svm.storageLocation = slm;
        svm.history = sm;
        
        return svm;
    }
    
    public StorageViewManager update() throws Exception {         
        current = current.update();
        return this;
    }
    
    public StorageViewManager fetchForUpdate() throws Exception {
        StorageViewManager svm;
  
        svm = StorageViewManager.getInstance();         
        svm.storageLocation = storageLocation.fetchForUpdate();         
        
        return svm;
    }
    
    public StorageViewManager abortUpdate() throws Exception {
        StorageViewManager svm;
  
        svm = StorageViewManager.getInstance();        
        svm.storageLocation = storageLocation.abortUpdate();         
        
        return svm;
    }
    
    //
    // other managers
    //    
    public StorageLocationManager getStorageLocation() {
        return storageLocation;
    }
    
    public StorageManager getCurrent() throws Exception {
        if (current == null) {
            if (storageLocation.getStorageLocation().getId() != null) {
                try {
                    current = StorageManager.fetchCurrentByLocationId(storageLocation.getStorageLocation().getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (current == null)
                current = StorageManager.getInstance();
        }
        return current;
    }
    
    public StorageManager getHistory(Integer id, int first, int max) throws Exception {
        if (storageLocation != null && storageLocation.getStorageLocation().getId() != null) {
            try {
                history = StorageManager.fetchHistoryByLocationId(id, first, max);
            } catch (NotFoundException e) {
                // ignore
            } catch (Exception e) {
                throw e;
            }
        }
        if (history == null)
            history = StorageManager.getInstance();
        return history;
    }
    
}
