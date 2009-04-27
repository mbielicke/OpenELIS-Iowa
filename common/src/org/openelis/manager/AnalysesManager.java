package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.gwt.common.RPC;
import org.openelis.manager.StorageManager.Item;

public class AnalysesManager implements RPC {
    private static final long serialVersionUID = 1L;
    
    protected Integer  sampleItemId;
    protected ArrayList<Item>  storageList;
    protected boolean cached;

    public static AnalysesManager getInstance() {
        AnalysesManager sm;

        sm = new AnalysesManager();

        return sm;
    }

    public Integer getSampleItemId() {
        return sampleItemId;
    }

    public void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
    }

}
