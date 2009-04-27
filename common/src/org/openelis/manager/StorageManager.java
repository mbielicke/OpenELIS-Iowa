package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.gwt.common.RPC;

public class StorageManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer  sampleItemId;
    protected ArrayList<Item>  storageList;
    protected boolean cached;
    protected transient StorageManagerIOInt manager;
    
    class Item implements Serializable {
        private static final long serialVersionUID = 1L;
        //storageDO
    }

    /**
     * Creates a new instance of this object. A default Specimen object is also created.
     */
    public static StorageManager getInstance() {
        StorageManager sm;

        sm = new StorageManager();

        return sm;
    }
    
    public Integer getSampleItemId() {
        return sampleItemId;
    }

    public void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
    }

    public StorageManagerIOInt getManager() {
        return manager;
    }

    public void setManager(StorageManagerIOInt manager) {
        this.manager = manager;
    }
}
