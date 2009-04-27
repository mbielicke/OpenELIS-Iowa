package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openelis.domain.SampleItemDO;
import org.openelis.gwt.common.RPC;

public class SampleItemsManager implements RPC {

    private static final long                   serialVersionUID = 1L;

    protected Integer                           sampleId;
    protected ArrayList<Item>                   items;
    protected boolean                           cached;
    protected transient SampleItemsManagerIOInt manager;

    class Item implements Serializable{
        private static final long serialVersionUID = 1L;
        SampleItemDO    sampleItem;
        StorageManager  storage;
        AnalysesManager analysis;
    }

    /**
     * Creates a new instance of this object.
     */
    public static SampleItemsManager getInstance() {
        SampleItemsManager sim;

        sim = new SampleItemsManager();
        sim.items = new ArrayList<Item>();

        return sim;
    }

    /**
     * Creates a new instance of this object with the specified Specimen. Use this function to load an instance of this object from database.
     */
    public static SampleItemsManager findById(Integer id) {
        SampleItemsManager sim = new SampleItemsManager();

        sim.setSampleId(id);
        sim.fetch();

        return sim;
    }
    
    public int count(){
        return items.size();
    }

    // getters/setters of child objects
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    
    public SampleItemDO add(){
        Item newItem = new Item();
        newItem.sampleItem = new SampleItemDO();
        items.add(newItem);
        
        return newItem.sampleItem;
    }

    // sample item
    public SampleItemDO getSampleItemAt(int i) {
        fetch();
        return getItem(i).sampleItem;

    }

    public void setSampleItemAt(SampleItemDO sampleItem, int i) {
        getItem(i).sampleItem = sampleItem;
    }

    // storage
    public StorageManager getStorageAt(int i) {
        Item item;

        fetch();
        item = getItem(i);

        if (item.storage == null) {
            item.storage = StorageManager.getInstance();
            item.storage.setSampleItemId(item.sampleItem.getId());
        }

        return item.storage;

    }

    public void setStorageAt(StorageManager storage, int i) {
        getItem(i).storage = storage;
    }

    // analysis
    public AnalysesManager getAnalysisAt(int i) {
        Item item;

        fetch();
        item = getItem(i);

        if (item.analysis == null) {
            item.analysis = AnalysesManager.getInstance();
            item.analysis.setSampleItemId(item.sampleItem.getId());
        }

        return item.analysis;
    }

    public void setAnalysisAt(AnalysesManager analysis, int i) {
        getItem(i).analysis = analysis;
    }

    private Item getItem(int i) {
        fetch();
        return (Item)items.get(i);
    }

    public SampleItemsManagerIOInt getManager() {
        return manager;
    }

    public void setManager(SampleItemsManagerIOInt manager) {
        this.manager = manager;
    }

    public void validate() {

    }

    // manager methods
    public Integer update() {
        return manager().update(this);
    }

    public void fetch() {
        if (cached)
            return;

        cached = true;
        List sampleItems = manager().fetch(sampleId);

        for (int i = 0; i < sampleItems.size(); i++) {
            Item item = new Item();
            item.sampleItem = (SampleItemDO)sampleItems.get(i);
            items.add(item);
        }
    }

    private SampleItemsManagerIOInt manager() {
        if (manager == null)
            manager = ManagerFactory.getSampleItemManagerIO();

        return manager;
    }
}
