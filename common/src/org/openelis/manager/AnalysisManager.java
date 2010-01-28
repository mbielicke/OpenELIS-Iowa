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
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class AnalysisManager implements RPC, HasNotesInt {
    private static final long                       serialVersionUID = 1L;

    protected Integer                               sampleItemId;
    protected transient Integer                     anLoggedInId, anInitiatedId, anCompletedId,
                    anReleasedId, anInPrepId, anOnHoldId, anRequeueId, anCancelledId,
                    anErrorLoggedInId, anErrorInitiatedId, anErrorInPrepId, anErrorCompletedId;

    protected ArrayList<AnalysisListItem>           items, deletedList;

    protected transient static AnalysisManagerProxy proxy;

    public static AnalysisManager getInstance() {
        AnalysisManager sm;

        sm = new AnalysisManager();
        sm.items = new ArrayList<AnalysisListItem>();

        return sm;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use
     * this function to load an instance of this object from database.
     */
    public static AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        return proxy().fetchBySampleItemId(sampleItemId);
    }

    // service methods
    public AnalysisManager add() throws Exception {
        return proxy().add(this);
    }

    public AnalysisManager update() throws Exception {
        return proxy().update(this);
    }

    public static AnalysisManager fetchForUpdate(Integer sampleItemId) throws Exception {
        return proxy().fetchBySampleItemIdForUpdate(sampleItemId);
    }

    // analysis
    public AnalysisViewDO getAnalysisAt(int i) {
        return getItemAt(i).analysis;

    }

    public void setAnalysisAt(AnalysisViewDO analysis, int i) {
        getItemAt(i).analysis = analysis;
    }

    public void addAnalysis(AnalysisViewDO analysis) {
        AnalysisListItem item = new AnalysisListItem();
        item.analysis = analysis;
        items.add(item);
    }

    public void removeAnalysisAt(int i) {
        if (items == null || i >= items.size())
            return;

        AnalysisListItem tmpList = items.remove(i);

        if (deletedList == null)
            deletedList = new ArrayList<AnalysisListItem>();

        if (tmpList.analysis.getId() != null)
            deletedList.add(tmpList);
    }

    public void cancelAnalysisAt(int index) {
        AnalysisViewDO anDO = items.get(index).analysis;
        Integer analysisCancelledId = null;

        try {
            proxy().getIdFromSystemName("analysis_cancelled");

            anDO.setStatusId(analysisCancelledId);
            anDO.setPreAnalysisId(null);
        } catch (Exception e) {
            return;
        }
    }

    public int count() {
        if (items == null)
            return 0;
    
        return items.size();
    }

    // item
    public AnalysisListItem getItemAt(int i) {
        return items.get(i);
    }

    //
    //other managers
    //
    // qaevents
    public AnalysisQaEventManager getQAEventAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);
        if (item.qaEvents == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.qaEvents = AnalysisQaEventManager.fetchByAnalysisId(item.analysis.getId());

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (item.qaEvents == null)
            item.qaEvents = AnalysisQaEventManager.getInstance();

        return item.qaEvents;
    }

    public void setQAEventAt(AnalysisQaEventManager qaEvent, int i) {
        getItemAt(i).qaEvents = qaEvent;
    }

    // internal notes
    public NoteManager getInternalNotesAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);
        if (item.analysisInternalNotes == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.analysisInternalNotes = NoteManager.fetchByRefTableRefIdIsExt(
                                                                                       ReferenceTable.ANALYSIS,
                                                                                       item.analysis.getId(),
                                                                                       "N");

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (item.analysisInternalNotes == null)
            item.analysisInternalNotes = NoteManager.getInstance();

        return item.analysisInternalNotes;
    }

    public void setInternalNotes(NoteManager notes, int i) {
        getItemAt(i).analysisInternalNotes = notes;
    }

    // external note
    public NoteManager getExternalNoteAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);
        if (item.analysisExternalNote == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.analysisExternalNote = NoteManager.fetchByRefTableRefIdIsExt(
                                                                                      ReferenceTable.ANALYSIS,
                                                                                      item.analysis.getId(),
                                                                                      "Y");

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (item.analysisExternalNote == null)
            item.analysisExternalNote = NoteManager.getInstance();

        return item.analysisExternalNote;
    }

    public void setExternalNoteAt(NoteManager note, int i) {
        getItemAt(i).analysisExternalNote = note;
    }

    // storage
    public StorageManager getStorageAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);

        if (item.storages == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.storages = StorageManager.fetchByRefTableRefId(ReferenceTable.ANALYSIS,
                                                                        item.analysis.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (item.storages == null)
            item.storages = StorageManager.getInstance();

        return item.storages;
    }

    public void setStorageAt(StorageManager storage, int i) {
        getItemAt(i).storages = storage;
    }

    // analysis test result
    public AnalysisResultManager getAnalysisResultAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);

        if (item.analysisResult == null) {
            if (item.analysis != null) {
                if (item.analysis.getId() != null && item.analysis.getId() > -1) {
                    try {
                        item.analysisResult = AnalysisResultManager.fetchForUpdate(
                                                                                      item.analysis.getId(),
                                                                                      item.analysis.getTestId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                } else if (item.analysis.getTestId() != null) {
                    try {
                        item.analysisResult = AnalysisResultManager.fetchForUpdate(item.analysis.getTestId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }
        }

        if (item.analysisResult == null)
            item.analysisResult = AnalysisResultManager.getInstance();

        return item.analysisResult;
    }

    public AnalysisResultManager getDisplayAnalysisResultAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);

        if (item.analysisResult == null) {
            if (item.analysis != null) {
                if (item.analysis.getId() != null && item.analysis.getId() > -1) {
                    try {
                        item.analysisResult = AnalysisResultManager.fetchByAnalysisId(item.analysis.getId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }
        }

        if (item.analysisResult == null)
            item.analysisResult = AnalysisResultManager.getInstance();

        return item.analysisResult;
    }

    public void setAnalysisResultAt(AnalysisResultManager analysisResult, int i) {
        getItemAt(i).analysisResult = analysisResult;
    }

    // test
    public void setTestAt(TestManager test, int i) {
        getItemAt(i).tests = test;
    }

    public TestManager getTestAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);

        if (item.tests == null) {
            if (item.analysis != null && item.analysis.getTestId() != null) {
                try {
                    item.tests = TestManager.fetchWithPrepTestsSampleTypes(item.analysis.getTestId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (item.tests == null)
            item.tests = TestManager.getInstance();

        return item.tests;
    }

    //
    //helper methods
    //
    public Integer getAnalysisIdByTestId(Integer testId) {
        Integer id = null;

        for (int i = 0; i < count(); i++ ) {

        }

        return id;
    }

    public NoteManager getNotes() throws Exception {
        throw new UnsupportedOperationException();
    }

    public int getIndex(AnalysisViewDO aDO) {
        for (int i = 0; i < count(); i++ )
            if (items.get(i).analysis == aDO)
                return i;
    
        return -1;
    }

    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(String sampleItemSequence,
                         Integer sampleTypeId,
                         ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, sampleItemSequence, sampleTypeId, errorsList);
    }

    protected void updateAnalysisStatusAt(int index, Integer sampleTypeId) throws Exception {
        TestManager testMan;
        boolean error = false;
        AnalysisViewDO anDO;
        Integer currentStatus;
        if (anLoggedInId == null)
            loadDictionaryEntries();
        anDO = getItemAt(index).analysis;
    
        // if the analysis is cancelled stop now
        if (anCancelledId.equals(anDO.getStatusId()))
            return;
    
        testMan = getTestAt(index);
        currentStatus = anDO.getStatusId();
    
        // sample item needs to match
        if ( !testMan.getSampleTypes().hasType(sampleTypeId))
            error = true;
    
        // unit needs filled out
        if (anDO.getUnitOfMeasureId() == null)
            error = true;
    
        if (error) {
            if (currentStatus.equals(anLoggedInId))
                anDO.setStatusId(anErrorLoggedInId);
            else if (currentStatus.equals(anInitiatedId))
                anDO.setStatusId(anErrorInitiatedId);
            else if (currentStatus.equals(anCompletedId))
                anDO.setStatusId(anErrorCompletedId);
            else if (currentStatus.equals(anInPrepId))
                anDO.setStatusId(anErrorInPrepId);
        } else {
            if (currentStatus.equals(anErrorLoggedInId))
                anDO.setStatusId(anLoggedInId);
            else if (currentStatus.equals(anErrorInitiatedId))
                anDO.setStatusId(anInitiatedId);
            else if (currentStatus.equals(anErrorCompletedId))
                anDO.setStatusId(anCompletedId);
            else if (currentStatus.equals(anErrorInPrepId))
                anDO.setStatusId(anInPrepId);
        }
    }

    protected void removeAnalysisAtNoDelete(int index) {
        if (items == null || index >= items.size())
            return;
    
        items.remove(index);
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getSampleItemId() {
        return sampleItemId;
    }

    void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;
    
        return deletedList.size();
    }

    AnalysisListItem getDeletedAt(int i) {
        return deletedList.get(i);
    }

    private void loadDictionaryEntries() throws Exception {
        anLoggedInId = proxy().getIdFromSystemName("analysis_logged_in");
        anInitiatedId = proxy().getIdFromSystemName("analysis_initiated");
        anCompletedId = proxy().getIdFromSystemName("analysis_completed");
        anReleasedId = proxy().getIdFromSystemName("analysis_released");
        anInPrepId = proxy().getIdFromSystemName("analysis_inprep");
        anOnHoldId = proxy().getIdFromSystemName("analysis_on_hold");
        anRequeueId = proxy().getIdFromSystemName("analysis_requeue");
        anCancelledId = proxy().getIdFromSystemName("analysis_cancelled");
        anErrorLoggedInId = proxy().getIdFromSystemName("analysis_error_logged_in");
        anErrorInitiatedId = proxy().getIdFromSystemName("analysis_error_initiated");
        anErrorInPrepId = proxy().getIdFromSystemName("analysis_error_inprep");
        anErrorCompletedId = proxy().getIdFromSystemName("analysis_error_completed");
    }

    private static AnalysisManagerProxy proxy() {
        if (proxy == null)
            proxy = new AnalysisManagerProxy();

        return proxy;
    }

    static class AnalysisListItem implements RPC {
        private static final long serialVersionUID = 1L;

        AnalysisResultManager     analysisResult;
        AnalysisViewDO            analysis;
        AnalysisQaEventManager    qaEvents;
        NoteManager               analysisInternalNotes, analysisExternalNote;
        StorageManager            storages;
        TestManager               tests;
    }
}