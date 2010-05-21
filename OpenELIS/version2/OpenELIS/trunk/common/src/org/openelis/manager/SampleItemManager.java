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

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class SampleItemManager implements RPC {
    private static final long                         serialVersionUID = 1L;

    protected Integer                                 sampleId;
    protected transient Integer                       anInPrepId;
    protected SampleManager                           sampleManager;
    private int                                       tempId;
    protected ArrayList<SampleItemListItem>           items;
    protected ArrayList<SampleItemListItem>           deletedList;

    protected transient static SampleItemManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleItemManager getInstance() {
        SampleItemManager sim;

        sim = new SampleItemManager();
        sim.items = new ArrayList<SampleItemListItem>();

        return sim;
    }

    // sample item
    public SampleItemViewDO getSampleItemAt(int i) {
        return getItemAt(i).sampleItem;

    }

    public void setSampleItemAt(SampleItemViewDO sampleItem, int i) {
        getItemAt(i).sampleItem = sampleItem;
    }

    public int addSampleItem() {
        SampleItemListItem item;
        SampleItemViewDO si;

        assert sampleManager != null : "sampleManager is null";

        item = new SampleItemListItem();
        si = new SampleItemViewDO();
        item.sampleItem = si;

        items.add(item);
        setDefaultsAt(count() - 1);

        return count() - 1;
    }

    public void removeSampleItemAt(int index) {
        SampleItemListItem tmpList;
        SampleDataBundle bundle;

        if (items == null || index >= items.size())
            return;

        tmpList = items.remove(index);

        // renumber sample bundle sample item indexes
        // when a node is removed
        for (int i = index; i < items.size(); i++ ) {
            bundle = items.get(i).bundle;

            if (bundle != null)
                bundle.setIndex(i);
        }

        if (deletedList == null)
            deletedList = new ArrayList<SampleItemListItem>();

        if (tmpList.sampleItem.getId() != null)
            deletedList.add(tmpList);
    }

    public int count() {
        if (items == null)
            return 0;

        return items.size();
    }

    public SampleDataBundle getBundleAt(int index) {
        SampleDataBundle bundle;

        bundle = getItemAt(index).bundle;
        if (bundle == null) {
            bundle = new SampleDataBundle(SampleDataBundle.Type.SAMPLE_ITEM, sampleManager, null,
                                          index);
            getItemAt(index).bundle = bundle;
        }

        return bundle;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use
     * this function to load an instance of this object from database.
     */
    public static SampleItemManager fetchBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }

    public SampleItemManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleItemManager update() throws Exception {
        return proxy().update(this);
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

    public void setDefaultsAt(int index) {
        SampleItemViewDO si;

        assert sampleManager != null : "sampleManager is null";

        si = getItemAt(index).sampleItem;
        si.setItemSequence(sampleManager.getNextSequence());
    }

    //
    // other managers
    //
    // storage
    public StorageManager getStorageAt(int i) throws Exception {
        SampleItemListItem item = getItemAt(i);

        if (item.storage == null) {
            if (item.sampleItem != null && item.sampleItem.getId() != null) {
                try {
                    item.storage = StorageManager.fetchByRefTableRefId(ReferenceTable.SAMPLE_ITEM,
                                                                       item.sampleItem.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (item.storage == null)
                item.storage = StorageManager.getInstance();
        }

        return item.storage;
    }

    public void setStorageAt(StorageManager storage, int i) {
        getItemAt(i).storage = storage;
    }

    // analysis
    public AnalysisManager getAnalysisAt(int i) throws Exception {
        SampleItemListItem item = getItemAt(i);

        if (item.analysis == null) {
            if (item.sampleItem != null && item.sampleItem.getId() != null) {
                try {
                    item.analysis = AnalysisManager.fetchBySampleItemId(item.sampleItem.getId());

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (item.analysis == null)
                item.analysis = AnalysisManager.getInstance();

            item.analysis.setSampleItemManager(this);
            item.analysis.setSampleItemBundle(getBundleAt(i));
        }

        return item.analysis;
    }

    /**
     * This moves an analysis from one sample item to the end of another sample
     * item
     * 
     * @param fromAnalysisBundle
     *        bundle of the analysis row you want to move
     * @param toSampleItemBundle
     *        bundle of the sample item row you want to move to
     * @throws Exception
     */
    public void moveAnalysis(SampleDataBundle fromAnalysisBundle, SampleDataBundle toBundle)
                                                                                            throws Exception {
        int toItemIndex, fromItemIndex, fromAnalysisIndex;
        AnalysisManager fromMan, toMan;
        AnalysisViewDO analysisDO;

        assert fromAnalysisBundle.getType() == SampleDataBundle.Type.ANALYSIS : "from bundle needs to be analysis bundle";

        toItemIndex = toBundle.getSampleItemIndex();
        fromItemIndex = fromAnalysisBundle.getSampleItemIndex();
        fromAnalysisIndex = fromAnalysisBundle.getAnalysisIndex();

        fromMan = getAnalysisAt(fromItemIndex);
        toMan = getAnalysisAt(toItemIndex);

        analysisDO = fromMan.getAnalysisAt(fromAnalysisIndex);
        fromMan.removeAnalysisAtNoDelete(fromAnalysisIndex);
        toMan.addAnalysis(analysisDO);
    }

    public void setAnalysisAt(AnalysisManager analysis, int i) {
        getItemAt(i).analysis = analysis;
    }

    public SampleItemListItem getItemAt(int i) {
        return (SampleItemListItem)items.get(i);
    }

    /**
     * Links the actual analysis with the prep analysis with the right indexes.
     * The actual analysis is updated with status 'In prep' and available date
     * is cleared.
     * 
     * @param index
     * @param prepTestIndex
     */
    public void linkPrepTest(int sampleItemIndex,
                             int analysisIndex,
                             int prepSampleItemIndex,
                             int prepAnalysisIndex) throws Exception {
        AnalysisViewDO anDO, prepDO;
        
        loadDictionaryEntries();

        anDO = getAnalysisAt(sampleItemIndex).getAnalysisAt(analysisIndex);
        prepDO = getAnalysisAt(prepSampleItemIndex).getAnalysisAt(prepAnalysisIndex);
        anDO.setPreAnalysisId(prepDO.getId());
        anDO.setPreAnalysisTest(prepDO.getTestName());
        anDO.setPreAnalysisMethod(prepDO.getMethodName());
        anDO.setStatusId(anInPrepId);
        anDO.setAvailableDate(null);
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getSampleId() {
        return sampleId;
    }

    void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    SampleManager getSampleManager() {
        return sampleManager;
    }

    void setSampleManager(SampleManager sampleManager) {
        this.sampleManager = sampleManager;
    }

    int getNextTempId() {
        return --tempId;
    }
    
    void setNextTempId(int tempId) {
        this.tempId = tempId;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    SampleItemListItem getDeletedAt(int i) {
        return deletedList.get(i);
    }

    void addSampleItems(ArrayList<SampleItemViewDO> sampleItems) {
        SampleItemListItem item;
        SampleItemViewDO si;
        for (int i = 0; i < sampleItems.size(); i++ ) {
            item = new SampleItemListItem();
            si = sampleItems.get(i);
            item.sampleItem = si;

            items.add(item);
        }
    }

    boolean hasReleasedAnalysis() {
        boolean released = false;

        for (int i = 0; i < count(); i++ ) {
            try {
                if (getAnalysisAt(i).hasReleasedAnalysis()) {
                    released = true;
                    break;
                }
            } catch (Exception e) {
                // ignore
            }
        }

        return released;
    }
    
    private void loadDictionaryEntries() throws Exception {
        if (anInPrepId == null) 
            anInPrepId = proxy().getIdFromSystemName("analysis_inprep");
    }

    private static SampleItemManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleItemManagerProxy();

        return proxy;
    }

    static class SampleItemListItem implements RPC {
        private static final long serialVersionUID = 1L;

        public SampleItemListItem() {
        }

        SampleItemViewDO sampleItem;
        StorageManager   storage;
        AnalysisManager  analysis;
        SampleDataBundle bundle;
    }
}
