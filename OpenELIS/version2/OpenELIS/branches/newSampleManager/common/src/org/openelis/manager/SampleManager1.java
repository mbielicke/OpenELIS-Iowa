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
import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalysisQaEventDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.NoteDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.ResultDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.StorageDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.domain.TestDO;

/**
 * This class encapsulates a sample and all its related information including
 * domain, organization, items, analysis, etc. Although the class provides some
 * basic functions internally, it is designed to interact with EJB methods to
 * provide majority of the operations needed to manage a sample.
 */
public class SampleManager1 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Flags that specifies what optional data to load with the manager
     */
    public enum Load {
        ORGANIZATION, PROJECT, QA, AUXDATA, STORAGE, NOTE, ANALYSISUSER, RESULT, SINGLERESULT
    };

    protected SampleDO                            sample;
    protected SampleEnvironmentalDO               sampleEnvironmental;
    protected SampleSDWISViewDO                   sampleSDWIS;
    protected SamplePrivateWellViewDO             samplePrivateWell;
    protected SampleNeonatalViewDO                sampleNeonatal;
    protected ArrayList<SampleOrganizationViewDO> organizations;
    protected ArrayList<SampleProjectViewDO>      projects;
    protected ArrayList<SampleQaEventViewDO>      sampleQAs;
    protected ArrayList<AuxDataViewDO>            auxilliary;
    protected NoteViewDO                          sampleExtNote;
    protected ArrayList<NoteViewDO>               sampleIntNotes, analysisExtNotes,
                    analysisIntNotes;
    protected ArrayList<SampleItemViewDO>         items;
    protected ArrayList<AnalysisViewDO>           analyses;
    protected ArrayList<AnalysisQaEventViewDO>    analysisQAs;
    protected ArrayList<StorageViewDO>            storages;
    protected ArrayList<AnalysisUserViewDO>       users;
    protected ArrayList<ResultViewDO>             results;
    protected ArrayList<DataObject>               removed;
    protected int                                 nextUID              = -1;

    transient public final SampleOrganization     organization         = new SampleOrganization();
    transient public final SampleProject          project              = new SampleProject();
    transient public final QAEvent                qaEvent              = new QAEvent();
    transient public final AuxData                auxData              = new AuxData();
    transient public final SampleExternalNote     sampleExternalNote   = new SampleExternalNote();
    transient public final SampleInternalNote     sampleInternalNote   = new SampleInternalNote();
    transient public final AnalysisExternalNote   analysisExternalNote = new AnalysisExternalNote();
    transient public final AnalysisInternalNote   analysisInternalNote = new AnalysisInternalNote();
    transient public final SampleItem             item                 = new SampleItem();
    transient public final Analysis               analysis             = new Analysis();
    transient public final Storage                storage              = new Storage();
    transient public final AnalysisUser           analysisUser         = new AnalysisUser();
    transient public final Result                 result               = new Result();
    transient private HashMap<String, DataObject> doMap;

    /**
     * Initialize an empty sample manager
     */
    public SampleManager1() {
    }

    /**
     * Returns the sample DO
     */
    public SampleDO getSample() {
        return sample;
    }

    /**
     * Returns the sample's domain specific DO.
     */
    public SampleEnvironmentalDO getSampleEnvironmental() {
        return sampleEnvironmental;
    }

    public SampleSDWISViewDO getSampleSDWIS() {
        return sampleSDWIS;
    }

    public SamplePrivateWellViewDO getSamplePrivateWell() {
        return samplePrivateWell;
    }

    public SampleNeonatalViewDO getSampleNeonatal() {
        return sampleNeonatal;
    }

    /**
     * Returns the next negative Id for this sample's newly created and as yet
     * uncommitted sample items, analyses and results
     */
    public int getNextUID() {
        return --nextUID;
    }

    /**
     * Returns a unique id representing the data object's type and key. This id
     * can be used to directly find the object this manager rather than serially
     * traversing the lists.
     */

    public String getUid(SampleQaEventDO data) {
        return getSampleQAEventUid(data.getId());
    }

    public String getUid(AnalysisQaEventDO data) {
        return getAnalysisQAEventUid(data.getId());
    }

    public String getUid(NoteDO data) {
        return getNoteUid(data.getId());
    }

    public String getUid(SampleItemDO data) {
        return getSampleItemUid(data.getId());
    }

    public String getUid(AnalysisDO data) {
        return getAnalysisUid(data.getId());
    }

    public String getUid(TestDO data) {
        return getTestUid(data.getId());
    }

    public String getUid(StorageDO data) {
        return getStorageUid(data.getId());
    }

    public String getUid(AnalysisUserDO data) {
        return getAnalysisUserUid(data.getId());
    }

    public String getUid(ResultDO data) {
        return getResultUid(data.getId());
    }

    /**
     * Returns the data object using its Uid.
     */
    public DataObject getObject(String uid) {
        if (doMap == null) {
            doMap = new HashMap<String, DataObject>();

            if (sampleQAs != null)
                for (SampleQaEventDO data : sampleQAs)
                    doMap.put(getSampleQAEventUid(data.getId()), data);

            if (analysisQAs != null)
                for (AnalysisQaEventDO data : analysisQAs)
                    doMap.put(getAnalysisQAEventUid(data.getId()), data);

            if (sampleIntNotes != null)
                for (NoteDO data : sampleIntNotes)
                    doMap.put(getNoteUid(data.getId()), data);

            if (analysisIntNotes != null)
                for (NoteDO data : analysisIntNotes)
                    doMap.put(getNoteUid(data.getId()), data);

            if (items != null)
                for (SampleItemDO data : items)
                    doMap.put(getSampleItemUid(data.getId()), data);

            if (storages != null)
                for (StorageDO data : storages)
                    doMap.put(getStorageUid(data.getId()), data);

            if (analyses != null)
                for (AnalysisDO data : analyses)
                    doMap.put(getAnalysisUid(data.getId()), data);

            if (users != null)
                for (AnalysisUserDO data : users)
                    doMap.put(getAnalysisUserUid(data.getId()), data);

            if (results != null)
                for (ResultDO data : results)
                    doMap.put(getResultUid(data.getId()), data);

        }
        return doMap.get(uid);
    }

    /**
     * Returns the unique identifiers for each data object.
     */

    public String getSampleQAEventUid(Integer id) {
        return "Q:" + id;
    }

    public String getAnalysisQAEventUid(Integer id) {
        return "E:" + id;
    }

    public String getNoteUid(Integer id) {
        return "N:" + id;
    }

    public String getSampleItemUid(Integer id) {
        return "I:" + id;
    }

    public String getStorageUid(Integer id) {
        return "S:" + id;
    }

    public String getAnalysisUid(Integer id) {
        return "A:" + id;
    }

    public String getTestUid(Integer id) {
        return "T:" + id;
    }

    public String getAnalysisUserUid(Integer id) {
        return "U:" + id;
    }

    public String getResultUid(Integer id) {
        return "R:" + id;
    }

    /**
     * Class to manage Sample Organization information
     */
    public class SampleOrganization {
        /**
         * Returns the organization at specified index.
         */
        public SampleOrganizationViewDO get(int i) {
            return organizations.get(i);
        }

        public SampleOrganizationViewDO add() {
            SampleOrganizationViewDO data;

            data = new SampleOrganizationViewDO();
            if (organizations == null)
                organizations = new ArrayList<SampleOrganizationViewDO>();
            organizations.add(data);

            return data;
        }

        public SampleOrganizationViewDO add(OrganizationDO organization) {
            SampleOrganizationViewDO data;
            AddressDO addr;

            data = add();
            data.setOrganizationId(organization.getId());
            data.setOrganizationName(organization.getName());
            addr = organization.getAddress();
            data.setOrganizationMultipleUnit(addr.getMultipleUnit());
            data.setOrganizationStreetAddress(addr.getStreetAddress());
            data.setOrganizationCity(addr.getCity());
            data.setOrganizationState(addr.getState());
            data.setOrganizationZipCode(addr.getZipCode());
            data.setOrganizationCountry(addr.getCountry());

            return data;
        }

        /**
         * Removes an organization from the list
         */
        public void remove(int i) {
            SampleOrganizationViewDO data;

            data = organizations.remove(i);
            if (data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        public void remove(SampleOrganizationViewDO data) {
            if (organizations.remove(data) && data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        /**
         * Returns the number of organizations associated with this sample
         */
        public int count() {
            if (organizations != null)
                return organizations.size();
            return 0;
        }

        /**
         * Returns a list of sample organizations that are of specified typeId.
         */
        public ArrayList<SampleOrganizationViewDO> getByType(Integer typeId) {
            ArrayList<SampleOrganizationViewDO> list;

            list = null;
            if (organizations != null) {
                for (SampleOrganizationViewDO data : organizations) {
                    if (typeId.equals(data.getTypeId())) {
                        if (list == null)
                            list = new ArrayList<SampleOrganizationViewDO>();
                        list.add(data);
                    }
                }
            }
            return list;
        }
    }

    /**
     * Class to manage Sample Project information
     */
    public class SampleProject {
        /**
         * Returns the project at specified index.
         */
        public SampleProjectViewDO get(int i) {
            return projects.get(i);
        }

        /**
         * Returns a new project
         */
        public SampleProjectViewDO add() {
            SampleProjectViewDO data;

            data = new SampleProjectViewDO();
            data.setIsPermanent("Y");
            if (projects == null)
                projects = new ArrayList<SampleProjectViewDO>();
            projects.add(data);

            return data;
        }

        public SampleProjectViewDO add(ProjectDO project) {
            SampleProjectViewDO data;

            data = add();
            data.setProjectId(project.getId());
            data.setProjectName(project.getName());
            data.setProjectDescription(project.getDescription());

            return data;
        }

        /**
         * Removes a project from the list
         */
        public void remove(int i) {
            SampleProjectViewDO data;

            data = projects.remove(i);
            if (data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        public void remove(SampleProjectViewDO data) {
            if (projects.remove(data) && data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        /**
         * Returns the number of projects associated with this sample
         */
        public int count() {
            if (projects != null)
                return projects.size();
            return 0;
        }

        /**
         * Returns a list of sample projects that match the isPermament (Y/N)
         * flag.
         */
        public ArrayList<SampleProjectViewDO> getByType(String isPermament) {
            ArrayList<SampleProjectViewDO> list;

            list = null;
            if (projects != null) {
                for (SampleProjectViewDO data : projects) {
                    if (isPermament.equals(data.getIsPermanent())) {
                        if (list == null)
                            list = new ArrayList<SampleProjectViewDO>();
                        list.add(data);
                    }
                }
            }
            return list;
        }
    }

    /**
     * Class to manage Sample & Analysis QAEvents
     */
    public class QAEvent {
        transient protected HashMap<Integer, ArrayList<AnalysisQaEventViewDO>> map = null;

        /**
         * Returns the sample's QA Event at specified index.
         */
        public SampleQaEventViewDO get(int i) {
            return sampleQAs.get(i);
        }

        /**
         * Returns the analysis QA Event at specified index.
         */
        public AnalysisQaEventViewDO get(AnalysisDO analysis, int i) {
            mapBuild();
            return map.get(analysis.getId()).get(i);
        }

        /**
         * Returns a sample qa event initialized with optional qa event
         */
        public SampleQaEventViewDO add(QaEventDO event) {
            SampleQaEventViewDO data;

            data = new SampleQaEventViewDO();
            if (event != null) {
                data.setQaEventId(event.getId());
                data.setTypeId(event.getTypeId());
                data.setIsBillable(event.getIsBillable());
            }
            if (sampleQAs == null)
                sampleQAs = new ArrayList<SampleQaEventViewDO>();
            sampleQAs.add(data);

            return data;
        }

        /**
         * Returns an analysis qa event linked to analysis and initialized with
         * optional qa event
         */
        public AnalysisQaEventViewDO add(AnalysisDO analysis, QaEventDO event) {
            AnalysisQaEventViewDO data;

            data = new AnalysisQaEventViewDO();
            data.setAnalysisId(analysis.getId());
            if (event != null) {
                data.setQaEventId(event.getId());
                data.setTypeId(event.getTypeId());
                data.setIsBillable(event.getIsBillable());
            }
            if (analysisQAs == null)
                analysisQAs = new ArrayList<AnalysisQaEventViewDO>();
            analysisQAs.add(data);
            mapAdd(data);

            return data;
        }

        /**
         * Removes a sample's qa event
         */
        public void remove(int i) {
            SampleQaEventViewDO data;

            data = sampleQAs.remove(i);
            if (data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        public void remove(SampleQaEventViewDO data) {
            if (sampleQAs.remove(data) && data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        /**
         * Removes an analysis qa event
         */
        public void remove(AnalysisDO analysis, int i) {
            AnalysisQaEventViewDO data;

            mapBuild();
            data = map.get(analysis.getId()).get(i);
            analysisQAs.remove(data);
            mapRemove(data);
            if (data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        public void remove(AnalysisDO analysis, AnalysisQaEventViewDO data) {
            if (analysisQAs.remove(data) && data.getId() != null && data.getId() > 0) {
                removeDataObject(data);
                mapRemove(data);
            }
        }

        /**
         * Returns the number of qa events associated with this sample
         */
        public int count() {
            if (sampleQAs != null)
                return sampleQAs.size();
            return 0;
        }

        /**
         * Returns the number of qa events associated with specified analysis
         */
        public int count(AnalysisDO analysis) {
            ArrayList<AnalysisQaEventViewDO> l;

            if (analysisQAs != null) {
                mapBuild();
                l = map.get(analysis.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /**
         * Returns true if sample has any qa event with specified type
         */
        public boolean hasType(Integer type) {
            if (sampleQAs != null) {
                for (SampleQaEventViewDO data : sampleQAs)
                    if (type.equals(data.getTypeId()))
                        return true;
            }
            return false;
        }

        /**
         * Returns true if analysis has any qa event with specified type
         */
        public boolean hasType(AnalysisDO analysis, Integer type) {
            ArrayList<AnalysisQaEventViewDO> l;

            if (analysisQAs != null) {
                mapBuild();
                l = map.get(analysis.getId());
                if (l != null) {
                    for (AnalysisQaEventViewDO data : l)
                        if (type.equals(data.getTypeId()))
                            return true;
                }
            }
            return false;
        }

        /**
         * Returns true if sample has any not billable qa events
         */
        public boolean hasNonBillable() {
            if (sampleQAs != null) {
                for (SampleQaEventViewDO data : sampleQAs)
                    if ("N".equals(data.getIsBillable()))
                        return true;
            }
            return false;
        }

        /**
         * Returns true if sample has any not billable qa events
         */
        public boolean hasNonBillable(AnalysisDO analysis) {
            ArrayList<AnalysisQaEventViewDO> l;

            if (analysisQAs != null) {
                mapBuild();
                l = map.get(analysis.getId());
                if (l != null) {
                    for (AnalysisQaEventViewDO data : l)
                        if ("N".equals(data.getIsBillable()))
                            return true;
                }
            }
            return false;
        }

        /*
         * create a hash map from analysis qa event list.
         */
        private void mapBuild() {
            if (map == null && analysisQAs != null) {
                map = new HashMap<Integer, ArrayList<AnalysisQaEventViewDO>>();
                for (AnalysisQaEventViewDO data : analysisQAs)
                    mapAdd(data);
            }
        }

        /*
         * adds a new qa event to the hash map
         */
        private void mapAdd(AnalysisQaEventViewDO data) {
            ArrayList<AnalysisQaEventViewDO> l;

            if (map != null) {
                l = map.get(data.getAnalysisId());
                if (l == null) {
                    l = new ArrayList<AnalysisQaEventViewDO>();
                    map.put(data.getAnalysisId(), l);
                }
                l.add(data);
            }
        }

        /*
         * removes the qa event from hash map
         */
        private void mapRemove(AnalysisQaEventViewDO data) {
            ArrayList<AnalysisQaEventViewDO> l;

            if (map != null) {
                l = map.get(data.getAnalysisId());
                if (l != null)
                    l.remove(data);
            }
        }
    }

    /**
     * Class to manage auxiliary data
     */
    public class AuxData {
        /**
         * Returns the aux data at specified index.
         */
        public AuxDataViewDO get(int i) {
            return auxilliary.get(i);
        }

        /**
         * Returns the number of aux data associated with the sample
         */
        public int count() {
            if (auxilliary == null)
                return 0;
            return auxilliary.size();
        }
    }

    /**
     * Class to manage sample external notes
     */
    public class SampleExternalNote {

        /**
         * Returns the sample's one (1) external note
         */
        public NoteViewDO get() {
            return sampleExtNote;
        }

        /**
         * Returns the editing note. For external, there is only 1 note and that
         * note can be edited regardless of whether its committed to the
         * database or not.
         */
        public NoteViewDO getEditing() {
            if (sampleExtNote == null) {
                sampleExtNote = new NoteViewDO();
                sampleExtNote.setIsExternal("Y");
            }

            return sampleExtNote;
        }

        /**
         * Removes the editing note. For external, the entire external note is
         * removed.
         */
        public void removeEditing() {
            if (sampleExtNote != null && sampleExtNote.getId() != null && sampleExtNote.getId() > 0)
                removeDataObject(sampleExtNote);
        }
    }

    /**
     * Class to manage sample internal notes
     */
    public class SampleInternalNote {

        /**
         * Returns the sample's internal note at specified index.
         */
        public NoteViewDO get(int i) {
            return sampleIntNotes.get(i);
        }

        /**
         * Returns the editing note. For internal, only the currently
         * uncommitted note can be edited. If no editing note currently exists,
         * one is created and returned.
         */
        public NoteViewDO getEditing() {
            NoteViewDO data;

            if (sampleIntNotes == null)
                sampleIntNotes = new ArrayList<NoteViewDO>(1);

            if (sampleIntNotes.size() == 0 ||
                (sampleIntNotes.get(0).getId() != null && sampleIntNotes.get(0).getId() > 0)) {
                data = new NoteViewDO();
                data.setIsExternal("N");
                sampleIntNotes.add(0, data);
            }

            return sampleIntNotes.get(0);
        }

        /**
         * Removes the editing note. For internal, only the uncommitted note is
         * removed.
         */
        public void removeEditing() {
            NoteViewDO data;

            if (sampleIntNotes != null && sampleIntNotes.size() > 0) {
                data = sampleIntNotes.get(0);
                if (data.getId() == null && data.getId() < 0)
                    sampleIntNotes.remove(0);
            }
        }

        /**
         * Returns the number of internal note(s)
         */
        public int count() {
            return (sampleIntNotes == null) ? 0 : sampleIntNotes.size();
        }
    }

    /**
     * Class to manage sample items
     */
    public class SampleItem {

        /**
         * Returns the item with the specified id
         */
        public SampleItemViewDO getById(Integer id) {
            return (SampleItemViewDO)getObject(getSampleItemUid(id));
        }

        /**
         * Returns the item at specified index.
         */
        public SampleItemViewDO get(int i) {
            return items.get(i);
        }

        /**
         * Returns a new item initialized with the next sequence
         */
        public SampleItemViewDO add() {
            int seq;
            SampleItemViewDO data;

            assert sample != null : "Manager.sample == null";

            seq = sample.getNextItemSequence();
            sample.setNextItemSequence(seq + 1);

            data = new SampleItemViewDO();
            data.setId(getNextUID());            
            data.setItemSequence(seq);
            if (items == null)
                items = new ArrayList<SampleItemViewDO>();
            items.add(data);

            return data;
        }

        /**
         * Removes an item from the list
         */
        public void remove(int i) {
            SampleItemViewDO data;

            data = items.remove(i);
            if (data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        public void remove(SampleItemViewDO data) {
            if (items.remove(data) && data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        /**
         * Returns the number of items associated with this sample
         */
        public int count() {
            if (items != null)
                return items.size();
            return 0;
        }

    }

    /**
     * Class to manage sample item & Analysis storage
     */
    public class Storage {
        //
        // Storage records for the entire sample, both sample items and
        // analyses, are often less than 10 records. That is why this class uses
        // simple loops rather than hashmap to manage this information
        //

        /**
         * Returns the storage record for a sample items or analysis at
         * specified index.
         */
        public StorageViewDO get(SampleItemViewDO item, int i) {
            return get(Constants.table().SAMPLE_ITEM, item.getId(), i);
        }

        public StorageViewDO get(AnalysisDO analysis, int i) {
            return get(Constants.table().ANALYSIS, analysis.getId(), i);
        }

        /**
         * Returns a new storage location for a sample item or analysis
         */
        public StorageViewDO add(SampleItemViewDO item) {
            return add(Constants.table().SAMPLE_ITEM, item.getId());
        }

        public StorageViewDO add(AnalysisDO analysis) {
            return add(Constants.table().ANALYSIS, analysis.getId());
        }

        /**
         * Removes a storage from sample item or analysis
         */
        public void remove(SampleItemViewDO item, int i) {
            remove(Constants.table().SAMPLE_ITEM, item.getId(), i);
        }

        public void remove(AnalysisDO analysis, int i) {
            remove(Constants.table().ANALYSIS, analysis.getId(), i);
        }

        public void remove(StorageViewDO data) {
            for (StorageViewDO storage : storages) {
                if (storage.getId().equals(data.getId())) {
                    if (data.getId() != null && data.getId() > 0)
                        removeDataObject(data);
                    break;
                }
            }
        }

        /**
         * Returns the number storage records associated with specified sample
         * item or analysis
         */
        public int count(SampleItemViewDO item) {
            return count(Constants.table().SAMPLE_ITEM, item.getId());
        }

        public int count(AnalysisDO analysis) {
            return count(Constants.table().ANALYSIS, analysis.getId());
        }

        private StorageViewDO get(int tableId, int id, int i) {
            int n;

            n = -1;
            for (StorageViewDO data : storages) {
                if (tableId == data.getReferenceTableId() && id == data.getReferenceId()) {
                    n++ ;
                    if (n == i)
                        return data;
                }
            }
            return null;
        }

        private StorageViewDO add(int tableId, int id) {
            StorageViewDO data;

            data = new StorageViewDO();
            data.setReferenceTableId(tableId);
            data.setReferenceId(id);
            if (storages == null)
                storages = new ArrayList<StorageViewDO>();
            storages.add(data);

            return data;
        }

        private void remove(int tableId, int id, int i) {
            int n;
            StorageViewDO data;

            n = -1;
            for (int j = 0; j < storages.size(); j++ ) {
                data = storages.get(j);
                if (tableId == data.getReferenceTableId() && id == data.getReferenceId()) {
                    n++ ;
                    if (n == i) {
                        storages.remove(j);
                        if (data.getId() != null && data.getId() > 0)
                            removeDataObject(data);
                        break;
                    }
                }
            }
        }

        private int count(int tableId, int id) {
            int n;

            n = 0;
            if (storages != null) {
                for (StorageViewDO storage : storages) {
                    if (tableId == storage.getReferenceTableId() && id == storage.getReferenceId())
                        n++ ;
                }
            }

            return n;
        }
    }

    /**
     * Class to manage analysis data
     */
    public class Analysis {
        transient protected HashMap<Integer, ArrayList<AnalysisViewDO>> map = null;

        /**
         * Returns the sample item's analysis at specified index.
         */
        public AnalysisViewDO get(SampleItemDO item, int i) {
            mapBuild();
            return map.get(item.getId()).get(i);
        }

        /**
         * Returns the number of analyses associated with specified sample item
         */
        public int count(SampleItemDO item) {
            ArrayList<AnalysisViewDO> l;

            if (analyses != null) {
                mapBuild();
                l = map.get(item.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash map from analyses list
         */
        private void mapBuild() {
            if (map == null && analyses != null) {
                map = new HashMap<Integer, ArrayList<AnalysisViewDO>>();
                for (AnalysisViewDO data : analyses)
                    mapAdd(data);
            }
        }

        /*
         * adds a new analysis to the hash map
         */
        private void mapAdd(AnalysisViewDO data) {
            ArrayList<AnalysisViewDO> l;

            if (map != null) {
                l = map.get(data.getSampleItemId());
                if (l == null) {
                    l = new ArrayList<AnalysisViewDO>();
                    map.put(data.getSampleItemId(), l);
                }
                l.add(data);
            }
        }
    }

    /**
     * Class to manage analysis external notes
     */
    public class AnalysisExternalNote {
        transient protected HashMap<Integer, NoteViewDO> map = null;

        /**
         * Returns the analysis's one (1) external note
         */
        public NoteViewDO get(AnalysisDO analysis) {
            map();
            if (map == null)
                return null;
            return map.get(analysis.getId());
        }

        /**
         * Returns the editing note. For external, there is only 1 note and that
         * note can be edited regardless of whether its committed to the
         * database or not.
         */
        public NoteViewDO getEditing(AnalysisDO analysis) {
            NoteViewDO data;

            if (analysisExtNotes == null)
                analysisExtNotes = new ArrayList<NoteViewDO>(1);

            map();
            if (map.get(analysis.getId()) == null) {
                data = new NoteViewDO();
                data.setIsExternal("Y");
                data.setReferenceId(analysis.getId());
                analysisExtNotes.add(data);
                map.put(analysis.getId(), data);
            }

            return map.get(analysis.getId());
        }

        /**
         * Removes the editing note. For external, the entire external note is
         * removed
         */
        public void removeEditing(AnalysisDO analysis) {
            NoteViewDO data;

            map();
            data = map.get(analysis.getId());
            if (data != null) {
                analysisExtNotes.remove(data);
                if (data.getId() != null && data.getId() > 0)
                    removeDataObject(data);
            }
        }

        /*
         * find the index to external and internal editable notes
         */
        private void map() {
            if (analysisExtNotes != null) {
                if (map == null) {
                    map = new HashMap<Integer, NoteViewDO>();
                    for (NoteViewDO data : analysisExtNotes)
                        map.put(data.getReferenceId(), data);
                }
            }
        }
    }

    /**
     * Class to manage analysis internal notes
     */
    public class AnalysisInternalNote {
        transient protected HashMap<Integer, ArrayList<NoteViewDO>> map = null;

        /**
         * Returns the analysis's internal note at specified index.
         */
        public NoteViewDO get(AnalysisDO analysis, int i) {
            map();
            if (map == null)
                return null;
            return map.get(analysis.getId()).get(i);
        }

        /**
         * Returns the editing note. For internal, only the currently
         * uncommitted note can be edited. If no editing note currently exists,
         * one is created and returned.
         */
        public NoteViewDO getEditing(AnalysisDO analysis) {
            NoteViewDO data;
            ArrayList<NoteViewDO> l;

            if (analysisIntNotes == null)
                analysisIntNotes = new ArrayList<NoteViewDO>(1);

            map();
            l = map.get(analysis.getId());
            if (l == null) {
                l = new ArrayList<NoteViewDO>();
                map.put(analysis.getId(), l);
            }

            if (l.size() == 0 || (l.get(0).getId() != null && l.get(0).getId() > 0)) {
                data = new NoteViewDO();
                data.setIsExternal("N");
                data.setReferenceId(analysis.getId());
                analysisIntNotes.add(data);
                l.add(0, data);
            }

            return l.get(0);
        }

        /**
         * Removes the editing note. For internal, only the uncommitted note is
         * removed.
         */
        public void removeEditing(AnalysisDO analysis) {
            NoteViewDO data;
            ArrayList<NoteViewDO> l;

            map();
            l = map.get(analysis.getId());
            if (l != null && l.size() > 0) {
                data = l.remove(0);
                analysisIntNotes.remove(data);
                if (data.getId() != null && data.getId() > 0)
                    removeDataObject(data);
            }
        }

        /**
         * Returns the number of internal note(s)
         */
        public int count(AnalysisDO analysis) {
            ArrayList<NoteViewDO> l;

            map();
            if (analysisIntNotes == null)
                return 0;

            l = map.get(analysis.getId());
            return (l == null) ? 0 : l.size();
        }

        /*
         * find the index to internal editable notes
         */
        private void map() {
            ArrayList<NoteViewDO> l;

            if (analysisIntNotes != null) {
                if (map == null) {
                    map = new HashMap<Integer, ArrayList<NoteViewDO>>();
                    for (NoteViewDO data : analysisIntNotes) {
                        l = map.get(data.getReferenceId());
                        if (l == null) {
                            l = new ArrayList<NoteViewDO>();
                            map.put(data.getReferenceId(), l);
                        }
                        l.add(data);
                    }
                }
            }
        }
    }

    /**
     * Class to manage analysis users
     */
    public class AnalysisUser {
        transient protected HashMap<Integer, ArrayList<AnalysisUserViewDO>> map = null;

        /**
         * Returns the user for given analysis at specified index.
         */
        public AnalysisUserViewDO get(AnalysisDO analysis, int i) {
            mapBuild();
            return map.get(analysis.getId()).get(i);
        }

        /**
         * Returns a new storage location for a sample item or analysis
         */
        public AnalysisUserViewDO add(AnalysisDO analysis) {
            AnalysisUserViewDO data;

            data = new AnalysisUserViewDO();
            data.setAnalysisId(analysis.getId());
            if (users == null)
                users = new ArrayList<AnalysisUserViewDO>();
            users.add(data);
            mapAdd(data);

            return data;
        }

        /**
         * Removes an analysis qa event
         */
        public void remove(AnalysisDO analysis, int i) {
            AnalysisUserViewDO data;

            mapBuild();
            data = map.get(analysis.getId()).get(i);
            users.remove(data);
            mapRemove(data);
            if (data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        public void remove(AnalysisDO analysis, AnalysisUserViewDO data) {
            if (users.remove(data) && data.getId() != null && data.getId() > 0) {
                removeDataObject(data);
                mapRemove(data);
            }
        }

        /**
         * Returns the number of users associated with specified analysis
         */
        public int count(AnalysisDO analysis) {
            ArrayList<AnalysisUserViewDO> l;

            if (users != null) {
                mapBuild();
                l = map.get(analysis.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash map from analysis user list.
         */
        private void mapBuild() {
            if (map == null && users != null) {
                map = new HashMap<Integer, ArrayList<AnalysisUserViewDO>>();
                for (AnalysisUserViewDO data : users)
                    mapAdd(data);
            }
        }

        /*
         * adds a new analysis user to the hash map
         */
        private void mapAdd(AnalysisUserViewDO data) {
            ArrayList<AnalysisUserViewDO> l;

            if (map != null) {
                l = map.get(data.getAnalysisId());
                if (l == null) {
                    l = new ArrayList<AnalysisUserViewDO>();
                    map.put(data.getAnalysisId(), l);
                }
                l.add(data);
            }
        }

        /*
         * removes the a analysis user from hash map
         */
        private void mapRemove(AnalysisUserViewDO data) {
            ArrayList<AnalysisUserViewDO> l;

            if (map != null) {
                l = map.get(data.getAnalysisId());
                if (l != null)
                    l.remove(data);
            }
        }
    }

    public class Result {
        transient protected HashMap<Integer, ArrayList<ArrayList<ResultViewDO>>> map = null;

        /**
         * Returns the result for given analysis at row and col
         */
        public ResultViewDO get(AnalysisDO analysis, int r, int c) {
            mapBuild();
            return map.get(analysis.getId()).get(r).get(c);
        }

        /**
         * Returns true if for given analysis a row group begins at specified
         * row
         */
        public boolean isHeader(AnalysisDO analysis, int r) {
            int rg1, rg2;

            if (r == 0)
                return true;

            mapBuild();
            rg1 = map.get(analysis.getId()).get(r).get(0).getRowGroup();
            rg2 = map.get(analysis.getId()).get(r - 1).get(0).getRowGroup();
            return rg1 != rg2;
        }

        /**
         * Removes the specified result row from analysis
         */
        public void remove(AnalysisDO analysis, int r) {
            ArrayList<ResultViewDO> rl;

            mapBuild();
            rl = map.get(analysis.getId()).get(r);
            for (ResultViewDO data : rl) {
                results.remove(data);
                if (data.getId() != null && data.getId() > 0)
                    removeDataObject(data);
            }
            rl.remove(r);
        }

        /**
         * Returns the number of result rows for specified analysis
         */
        public int count(AnalysisDO analysis) {
            mapBuild();
            if (map != null && map.get(analysis.getId()) != null)
                return map.get(analysis.getId()).size();
            else
                return 0;
        }

        /**
         * Returns the number of result columns for specified analysis's row
         */
        public int count(AnalysisDO analysis, int r) {
            mapBuild();
            return map.get(analysis.getId()).get(r).size();
        }

        /**
         * Returns the number of result columns for specified analysis's row
         */
        public int maxColumns(AnalysisDO analysis) {
            int n;
            ArrayList<ArrayList<ResultViewDO>> rl;

            mapBuild();
            n = 0;
            rl = map.get(analysis.getId());
            if (rl != null) {
                for (ArrayList<ResultViewDO> l : rl)
                    n = Math.max(l.size(), n);
            } else {
                n = 0;
            }

            return n;
        }

        /*
         * create a hash map from from all the results
         */
        private void mapBuild() {
            Integer id;
            ArrayList<ResultViewDO> rl;
            ArrayList<ArrayList<ResultViewDO>> l;

            if (map == null && results != null) {
                l = null;
                rl = null;
                id = null;
                map = new HashMap<Integer, ArrayList<ArrayList<ResultViewDO>>>();
                for (ResultViewDO data : results) {
                    /*
                     * new analysis
                     */
                    if (id == null || !id.equals(data.getAnalysisId())) {
                        id = data.getAnalysisId();
                        l = new ArrayList<ArrayList<ResultViewDO>>();
                        map.put(id, l);
                    }
                    /*
                     * new row
                     */
                    if ("N".equals(data.getIsColumn())) {
                        rl = new ArrayList<ResultViewDO>();
                        l.add(rl);
                    }
                    rl.add(data);
                }
            }
        }

        // /**
        // * Adds a new row. The first column is created from row-analyte data
        // while remaining columns
        // * are duplicated from the source-row.
        // */
        // public ResultViewDO add(AnalysisDO analysis, int row,
        // TestAnalyteViewDO rowAnalyte, int sourceRow) {
        // ResultViewDO data, from;
        // ArrayList<ResultViewDO> o, n;
        //
        // mapBuild();
        // o = map.get(analysis.getId()).get(sourceRow);
        // n = new ArrayList<ResultViewDO>(o.size());
        // from = o.get(0);
        // data = new ResultViewDO(0, analysis.getId(), rowAnalyte.getId(),
        // null,
        // "N", null, rowAnalyte.getIsReportable(), rowAnalyte.getAnalyteId(),
        // null, null, rowAnalyte.getAnalyteName(), from.getRowGroup(),
        // null, null);
        // n.add(data);
        // for (int i = 1; i < n.size(); i++)
        // from = o.get(i);
        // n.add(new ResultViewDO(0, analysis.getId(), rowAnalyte.getId(), null,
        // "N", null, rowAnalyte.getIsReportable(), rowAnalyte.getAnalyteId(),
        // null, null, rowAnalyte.getAnalyteName(), o.get(0).getRowGroup(),
        // null, null));
        //
        // }

    }

    /**
     * Adds the specified data object to the list of objects that should be
     * removed from the database.
     */
    private void removeDataObject(DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();
        removed.add(data);
    }
}