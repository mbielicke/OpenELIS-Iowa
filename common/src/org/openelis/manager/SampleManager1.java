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
import org.openelis.domain.AnalysisWorksheetVO;
import org.openelis.domain.AttachmentItemDO;
import org.openelis.domain.AttachmentItemViewDO;
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
import org.openelis.domain.SampleAnimalDO;
import org.openelis.domain.SampleClinicalViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePTDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.StorageDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.ui.common.Datetime;

/**
 * This class encapsulates a sample and all its related information including
 * domain, organization, items, analysis, etc. Although the class provides some
 * basic functions internally, it is designed to interact with EJB methods to
 * provide majority of the operations needed to manage a sample.
 */
public class SampleManager1 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Flags that specify what optional data to load with the manager
     */
    public enum Load {
        ORGANIZATION, PROJECT, QA, AUXDATA, STORAGE, NOTE, SINGLEANALYSIS, ANALYSISUSER, RESULT,
        SINGLERESULT, WORKSHEET, ATTACHMENT, EORDER, PROVIDER
    };

    /**
     * Flags that specify what operations need to be performed for the sample,
     * after update
     */
    public enum PostProcessing {
        UNRELEASE
    };

    protected SampleDO                            sample;
    protected SampleEnvironmentalDO               sampleEnvironmental;
    protected SampleSDWISViewDO                   sampleSDWIS;
    protected SampleNeonatalViewDO                sampleNeonatal;
    protected SampleClinicalViewDO                sampleClinical;
    protected SamplePTDO                          samplePT;
    protected SampleAnimalDO                      sampleAnimal;
    protected ArrayList<SampleOrganizationViewDO> organizations;
    protected ArrayList<SampleProjectViewDO>      projects;
    protected ArrayList<SampleQaEventViewDO>      sampleQAs;
    protected ArrayList<AuxDataViewDO>            auxiliary;
    protected NoteViewDO                          sampleExtNote;
    protected ArrayList<NoteViewDO>               sampleIntNotes, analysisExtNotes,
                    analysisIntNotes;
    protected ArrayList<AttachmentItemViewDO>     attachments;
    protected ArrayList<SampleItemViewDO>         items;
    protected ArrayList<AnalysisViewDO>           analyses;
    protected ArrayList<AnalysisQaEventViewDO>    analysisQAs;
    protected ArrayList<StorageViewDO>            storages;
    protected ArrayList<AnalysisUserViewDO>       users;
    protected ArrayList<ResultViewDO>             results;
    protected ArrayList<AnalysisWorksheetVO>      worksheets;
    protected ArrayList<DataObject>               removed;
    protected int                                 nextUID              = -1;
    protected PostProcessing                      postProcessing;

    transient public final SampleOrganization     organization         = new SampleOrganization();
    transient public final SampleProject          project              = new SampleProject();
    transient public final QAEvent                qaEvent              = new QAEvent();
    transient public final AuxData                auxData              = new AuxData();
    transient public final SampleExternalNote     sampleExternalNote   = new SampleExternalNote();
    transient public final SampleInternalNote     sampleInternalNote   = new SampleInternalNote();
    transient public final Attachment             attachment           = new Attachment();
    transient public final AnalysisExternalNote   analysisExternalNote = new AnalysisExternalNote();
    transient public final AnalysisInternalNote   analysisInternalNote = new AnalysisInternalNote();
    transient public final SampleItem             item                 = new SampleItem();
    transient public final Analysis               analysis             = new Analysis();
    transient public final Storage                storage              = new Storage();
    transient public final AnalysisUser           analysisUser         = new AnalysisUser();
    transient public final Result                 result               = new Result();
    transient public final Worksheet              worksheet            = new Worksheet();
    transient private HashMap<String, DataObject> uidMap;

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

    public SampleNeonatalViewDO getSampleNeonatal() {
        return sampleNeonatal;
    }

    public SampleClinicalViewDO getSampleClinical() {
        return sampleClinical;
    }

    public SamplePTDO getSamplePT() {
        return samplePT;
    }

    public SampleAnimalDO getSampleAnimal() {
        return sampleAnimal;
    }

    /**
     * Returns the next negative Id for this sample's newly created and as yet
     * uncommitted data objects e.g. sample items, analyses and results etc.
     */
    public int getNextUID() {
        return --nextUID;
    }

    /**
     * Returns the data object using its Uid.
     */
    public DataObject getObject(String uid) {
        if (uidMap == null) {
            uidMap = new HashMap<String, DataObject>();

            if (sampleQAs != null)
                for (SampleQaEventDO data : sampleQAs)
                    uidMap.put(Constants.uid().get(data), data);

            if (auxiliary != null)
                for (AuxDataViewDO data : auxiliary)
                    uidMap.put(Constants.uid().get(data), data);

            if (analysisQAs != null)
                for (AnalysisQaEventDO data : analysisQAs)
                    uidMap.put(Constants.uid().get(data), data);

            if (sampleIntNotes != null)
                for (NoteDO data : sampleIntNotes)
                    uidMap.put(Constants.uid().get(data), data);

            if (attachments != null)
                for (AttachmentItemDO data : attachments)
                    uidMap.put(Constants.uid().get(data), data);

            if (analysisIntNotes != null)
                for (NoteDO data : analysisIntNotes)
                    uidMap.put(Constants.uid().get(data), data);

            if (items != null)
                for (SampleItemDO data : items)
                    uidMap.put(Constants.uid().get(data), data);

            if (storages != null)
                for (StorageDO data : storages)
                    uidMap.put(Constants.uid().get(data), data);

            if (analyses != null)
                for (AnalysisDO data : analyses)
                    uidMap.put(Constants.uid().get(data), data);

            if (users != null)
                for (AnalysisUserDO data : users)
                    uidMap.put(Constants.uid().get(data), data);

            if (results != null)
                for (ResultDO data : results)
                    uidMap.put(Constants.uid().get(data), data);

        }
        return uidMap.get(uid);
    }

    public PostProcessing getPostProcessing() {
        return postProcessing;
    }

    public void setPostProcessing(PostProcessing postProcessing) {
        this.postProcessing = postProcessing;
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
            data.setId(getNextUID());
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

            data = organizations.get(i);
            organizations.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(SampleOrganizationViewDO data) {
            organizations.remove(data);
            dataObjectRemove(data.getId(), data);
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
            data.setId(getNextUID());
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

            data = projects.get(i);
            projects.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(SampleProjectViewDO data) {
            projects.remove(data);
            dataObjectRemove(data.getId(), data);
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
        transient protected HashMap<Integer, ArrayList<AnalysisQaEventViewDO>> localmap = null;

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
            localmapBuild();
            return localmap.get(analysis.getId()).get(i);
        }

        /**
         * Returns a sample qa event initialized with optional qa event
         */
        public SampleQaEventViewDO add(QaEventDO event) {
            SampleQaEventViewDO data;

            data = new SampleQaEventViewDO();
            data.setId(getNextUID());
            if (event != null) {
                data.setQaEventId(event.getId());
                data.setQaEventName(event.getName());
                data.setTypeId(event.getTypeId());
                data.setIsBillable(event.getIsBillable());
            }
            if (sampleQAs == null)
                sampleQAs = new ArrayList<SampleQaEventViewDO>();
            sampleQAs.add(data);
            uidMapAdd(Constants.uid().get(data), data);

            return data;
        }

        /**
         * Returns an analysis qa event linked to analysis and initialized with
         * optional qa event
         */
        public AnalysisQaEventViewDO add(AnalysisDO analysis, QaEventDO event) {
            AnalysisQaEventViewDO data;

            data = new AnalysisQaEventViewDO();
            data.setId(getNextUID());
            data.setAnalysisId(analysis.getId());
            if (event != null) {
                data.setQaEventId(event.getId());
                data.setQaEventName(event.getName());
                data.setTypeId(event.getTypeId());
                data.setIsBillable(event.getIsBillable());
            }
            if (analysisQAs == null)
                analysisQAs = new ArrayList<AnalysisQaEventViewDO>();
            analysisQAs.add(data);
            localmapAdd(data);
            uidMapAdd(Constants.uid().get(data), data);

            return data;
        }

        /**
         * Removes a sample's qa event
         */
        public void remove(int i) {
            SampleQaEventViewDO data;

            data = sampleQAs.get(i);
            sampleQAs.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        public void remove(SampleQaEventViewDO data) {
            sampleQAs.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        /**
         * Removes an analysis qa event
         */
        public void remove(AnalysisDO analysis, int i) {
            AnalysisQaEventViewDO data;

            localmapBuild();
            data = localmap.get(analysis.getId()).get(i);
            analysisQAs.remove(data);
            localmapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        public void remove(AnalysisDO analysis, AnalysisQaEventViewDO data) {
            localmapBuild();
            analysisQAs.remove(data);
            localmapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
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
                localmapBuild();
                l = localmap.get(analysis.getId());
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
                localmapBuild();
                l = localmap.get(analysis.getId());
                if (l != null) {
                    for (AnalysisQaEventViewDO data : l)
                        if (type.equals(data.getTypeId()))
                            return true;
                }
            }
            return false;
        }

        /**
         * Returns true if the analysis has a qa event with specified name
         */
        public boolean hasName(AnalysisViewDO analysis, String name) {
            ArrayList<AnalysisQaEventViewDO> l;

            if (analysisQAs != null) {
                localmapBuild();
                l = localmap.get(analysis.getId());
                if (l != null) {
                    for (AnalysisQaEventViewDO data : l)
                        if (name.equals(data.getQaEventName()))
                            return true;
                }
            }
            return false;
        }

        /**
         * Returns the qa event with specified name if it's been added to the
         * passed analysis; otherwise returns null
         */
        public AnalysisQaEventViewDO getByName(AnalysisViewDO analysis, String name) {
            ArrayList<AnalysisQaEventViewDO> l;

            if (analysisQAs != null) {
                localmapBuild();
                l = localmap.get(analysis.getId());
                if (l != null) {
                    for (AnalysisQaEventViewDO data : l)
                        if (name.equals(data.getQaEventName()))
                            return data;
                }
            }
            return null;
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
                localmapBuild();
                l = localmap.get(analysis.getId());
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
        private void localmapBuild() {
            if (localmap == null && analysisQAs != null) {
                localmap = new HashMap<Integer, ArrayList<AnalysisQaEventViewDO>>();
                for (AnalysisQaEventViewDO data : analysisQAs)
                    localmapAdd(data);
            }
        }

        /*
         * adds a new qa event to the hash map
         */
        private void localmapAdd(AnalysisQaEventViewDO data) {
            ArrayList<AnalysisQaEventViewDO> l;

            if (localmap != null) {
                l = localmap.get(data.getAnalysisId());
                if (l == null) {
                    l = new ArrayList<AnalysisQaEventViewDO>();
                    localmap.put(data.getAnalysisId(), l);
                }
                l.add(data);
            }
        }

        /*
         * removes the qa event from hash map
         */
        private void localmapRemove(AnalysisQaEventViewDO data) {
            ArrayList<AnalysisQaEventViewDO> l;

            if (localmap != null) {
                l = localmap.get(data.getAnalysisId());
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
            return auxiliary.get(i);
        }

        /**
         * Returns the number of aux data associated with the sample
         */
        public int count() {
            if (auxiliary == null)
                return 0;
            return auxiliary.size();
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
                sampleExtNote.setId(getNextUID());
                sampleExtNote.setIsExternal("Y");
            }

            return sampleExtNote;
        }

        /**
         * Removes the editing note. For external, the entire external note is
         * removed.
         */
        public void removeEditing() {
            if (sampleExtNote != null) {
                dataObjectRemove(sampleExtNote.getId(), sampleExtNote);
                sampleExtNote = null;
            }
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
                data.setId(getNextUID());
                data.setIsExternal("N");
                sampleIntNotes.add(0, data);

                uidMapAdd(Constants.uid().get(data), data);
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
                if (data.getId() < 0)
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
     * Class to manage the attachments associated with the sample
     */
    public class Attachment {
        /**
         * Returns the attachment item at specified index.
         */
        public AttachmentItemViewDO get(int i) {
            return attachments.get(i);
        }

        /**
         * Returns an attachment item
         */
        public AttachmentItemViewDO add() {
            AttachmentItemViewDO data;

            data = new AttachmentItemViewDO();
            data.setId(getNextUID());
            if (attachments == null)
                attachments = new ArrayList<AttachmentItemViewDO>();
            attachments.add(data);
            uidMapAdd(Constants.uid().get(data), data);

            return data;
        }

        /**
         * Removes an attachment item from the list
         */
        public void remove(int i) {
            AttachmentItemDO data;

            data = attachments.get(i);
            attachments.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        public void remove(AttachmentItemDO data) {
            attachments.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        /**
         * Returns the number of attachment items associated with this sample
         */
        public int count() {
            if (attachments != null)
                return attachments.size();
            return 0;
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
            return (SampleItemViewDO)getObject(Constants.uid().getSampleItem(id));
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
            uidMapAdd(Constants.uid().get(data), data);

            return data;
        }

        /**
         * Removes an item from the list
         */
        public void remove(int i) {
            SampleItemViewDO data;

            data = items.get(i);
            assert analysis.count(data) == 0 : "one or more analyses are linked to the sample item";

            items.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        public void remove(SampleItemViewDO data) {
            assert analysis.count(data) == 0 : "one or more analyses are linked to the sample item";

            items.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
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
                    storages.remove(data);
                    dataObjectRemove(data.getId(), data);
                    uidMapRemove(Constants.uid().get(data));
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
            data.setId(getNextUID());
            data.setReferenceTableId(tableId);
            data.setReferenceId(id);
            if (storages == null)
                storages = new ArrayList<StorageViewDO>();
            storages.add(data);

            uidMapAdd(Constants.uid().get(data), data);
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
                        storages.remove(data);
                        dataObjectRemove(data.getId(), data);
                        uidMapRemove(Constants.uid().get(data));
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
        transient protected HashMap<Integer, ArrayList<AnalysisViewDO>> localmap = null;
        // @formatter:off
                                                                     //L  P  I  C  R  H  Q  X  EL EP EI EC
        transient protected final int[][] statuses = new int[][] {
                                                                      {0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0}, // Logged-in 
                                                                      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // in-Prep
                                                                      {1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0}, // Initiated
                                                                      {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0}, // Completed
                                                                      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Released
                                                                      {1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0}, // Onhold
                                                                      {1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0}, // reQueue
                                                                      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // cancel X
                                                                      {0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0}, // Error-Logged-in
                                                                      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Error-in-Prep
                                                                      {1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0}, // Error-Initiated
                                                                      {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0}  // Error-Completed
                                                                      };
        // @formatter:on

        /**
         * Returns the sample item's analysis at specified index.
         */
        public AnalysisViewDO get(SampleItemDO item, int i) {
            localmapBuild();
            return localmap.get(item.getId()).get(i);
        }

        /**
         * Returns the number of analyses associated with specified sample item
         */
        public int count(SampleItemDO item) {
            ArrayList<AnalysisViewDO> l;

            if (analyses != null) {
                localmapBuild();
                l = localmap.get(item.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /**
         * Returns the analysis at specified index in the list of all analyses,
         * regardless of the sample item.
         */
        public AnalysisViewDO get(int i) {
            return analyses.get(i);
        }

        /**
         * Returns the total number of analyses, regardless of the sample item
         */
        public int count() {
            if (analyses != null)
                return analyses.size();
            return 0;
        }

        /**
         * Returns true if the sample has at least one released analysis
         */
        public boolean hasReleasedAnalysis() {
            if (analyses != null) {
                for (AnalysisViewDO a : analyses) {
                    if (Constants.dictionary().ANALYSIS_RELEASED.equals(a.getStatusId()))
                        return true;
                }
            }

            return false;
        }

        /**
         * Returns true if at least one released analysis is linked to the
         * sample item
         */
        public boolean hasReleasedAnalysis(SampleItemViewDO item) {
            ArrayList<AnalysisViewDO> l;

            if (analyses != null) {
                localmapBuild();
                l = localmap.get(item.getId());
                if (l != null) {
                    for (AnalysisViewDO a : l) {
                        if (Constants.dictionary().ANALYSIS_RELEASED.equals(a.getStatusId()))
                            return true;
                    }
                }
            }

            return false;
        }

        /**
         * Links the analysis with the specified id to the sample item with the
         * specified id, if the analysis isn't already linked to the item and is
         * not released or cancelled
         */
        public void moveAnalysis(Integer analysisId, Integer sampleItemId) {
            AnalysisViewDO data;

            data = (AnalysisViewDO)getObject(Constants.uid().getAnalysis(analysisId));
            if ( !sampleItemId.equals(data.getSampleItemId()) &&
                !Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()) &&
                !Constants.dictionary().ANALYSIS_CANCELLED.equals(data.getStatusId())) {
                data.setSampleItemId(sampleItemId);
                localmap = null;
            }
        }

        /**
         * Removes the analysis with the specified id from the manager, if the
         * analysis is not a committed one
         */
        public void removeAnalysis(Integer analysisId) {
            AnalysisViewDO data, ana;

            assert analysisId < 0 : "an existing analysis cannot be removed";

            data = (AnalysisViewDO)getObject(Constants.uid().getAnalysis(analysisId));
            for (int i = 0; i < analyses.size(); i++ ) {
                ana = analyses.get(i);
                if (analysisId.equals(ana.getPreAnalysisId())) {
                    ana.setPreAnalysisId(null);
                    ana.setPreAnalysisTest(null);
                    ana.setPreAnalysisMethod(null);
                    ana.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
                    ana.setAvailableDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                }
            }
            analyses.remove(data);
            localmap = null;
        }

        /**
         * Returns true if an analysis' status can be changed from the first
         * argument to the second, directly by the user, e.g. through the
         * dropdown on analysis tab. Not used in the back-end, where it's
         * assumed that the status is changed only under the right conditions.
         */
        public boolean canChangeStatus(Integer fromStatusId, Integer toStatusId) {
            assert fromStatusId != null && toStatusId != null : "statuses cannot be null";

            return statuses[getStatusIndex(fromStatusId)][getStatusIndex(toStatusId)] == 1;
        }

        /*
         * create a hash map from analyses list
         */
        private void localmapBuild() {
            if (localmap == null && analyses != null) {
                localmap = new HashMap<Integer, ArrayList<AnalysisViewDO>>();
                for (AnalysisViewDO data : analyses)
                    localmapAdd(data);
            }
        }

        /*
         * adds a new analysis to the hash map
         */
        private void localmapAdd(AnalysisViewDO data) {
            ArrayList<AnalysisViewDO> l;

            if (localmap != null) {
                l = localmap.get(data.getSampleItemId());
                if (l == null) {
                    l = new ArrayList<AnalysisViewDO>();
                    localmap.put(data.getSampleItemId(), l);
                }
                l.add(data);
            }
        }

        /*
         * Return the row or column for this status in the grid for statuses
         */
        private int getStatusIndex(Integer statusId) {
            if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(statusId))
                return 0;
            if (Constants.dictionary().ANALYSIS_INPREP.equals(statusId))
                return 1;
            if (Constants.dictionary().ANALYSIS_INITIATED.equals(statusId))
                return 2;
            if (Constants.dictionary().ANALYSIS_COMPLETED.equals(statusId))
                return 3;
            if (Constants.dictionary().ANALYSIS_RELEASED.equals(statusId))
                return 4;
            if (Constants.dictionary().ANALYSIS_ON_HOLD.equals(statusId))
                return 5;
            if (Constants.dictionary().ANALYSIS_REQUEUE.equals(statusId))
                return 6;
            if (Constants.dictionary().ANALYSIS_CANCELLED.equals(statusId))
                return 7;
            if (Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN.equals(statusId))
                return 8;
            if (Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(statusId))
                return 9;
            if (Constants.dictionary().ANALYSIS_ERROR_INITIATED.equals(statusId))
                return 10;
            if (Constants.dictionary().ANALYSIS_ERROR_COMPLETED.equals(statusId))
                return 11;

            return -1;
        }
    }

    /**
     * Class to manage analysis external notes
     */
    public class AnalysisExternalNote {
        transient protected HashMap<Integer, NoteViewDO> localmap = null;

        /**
         * Returns the analysis's one (1) external note
         */
        public NoteViewDO get(AnalysisDO analysis) {
            localmapBuild();
            if (localmap == null)
                return null;
            return localmap.get(analysis.getId());
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

            localmapBuild();
            if (localmap.get(analysis.getId()) == null) {
                data = new NoteViewDO();
                data.setId(getNextUID());
                data.setIsExternal("Y");
                data.setReferenceId(analysis.getId());
                analysisExtNotes.add(data);
                localmap.put(analysis.getId(), data);
                uidMapAdd(Constants.uid().get(data), data);
            }

            return localmap.get(analysis.getId());
        }

        /**
         * Removes the editing note. For external, the entire external note is
         * removed
         */
        public void removeEditing(AnalysisDO analysis) {
            NoteViewDO data;

            localmapBuild();
            if (localmap != null) {
                data = localmap.get(analysis.getId());
                if (data != null) {
                    analysisExtNotes.remove(data);
                    localmap.remove(analysis.getId());
                    dataObjectRemove(data.getId(), data);
                    uidMapRemove(Constants.uid().get(data));
                }
            }
        }

        /*
         * find the index to external editable notes
         */
        private void localmapBuild() {
            if (analysisExtNotes != null) {
                if (localmap == null) {
                    localmap = new HashMap<Integer, NoteViewDO>();
                    for (NoteViewDO data : analysisExtNotes)
                        localmap.put(data.getReferenceId(), data);
                }
            }
        }
    }

    /**
     * Class to manage analysis internal notes
     */
    public class AnalysisInternalNote {
        transient protected HashMap<Integer, ArrayList<NoteViewDO>> localmap = null;

        /**
         * Returns the analysis's internal note at specified index.
         */
        public NoteViewDO get(AnalysisDO analysis, int i) {
            localmapBuild();
            if (localmap == null)
                return null;
            return localmap.get(analysis.getId()).get(i);
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

            localmapBuild();
            l = localmap.get(analysis.getId());
            if (l == null) {
                l = new ArrayList<NoteViewDO>();
                localmap.put(analysis.getId(), l);
            }

            if (l.size() == 0 || (l.get(0).getId() != null && l.get(0).getId() > 0)) {
                data = new NoteViewDO();
                data.setId(getNextUID());
                data.setIsExternal("N");
                data.setReferenceId(analysis.getId());
                /*
                 * the new note is added at the beginning of the list for all
                 * internal notes to make sure that when the manager gets sent
                 * to the back-end and brought back, the new note is still the
                 * first one for the analysis and not the last one
                 */
                analysisIntNotes.add(0, data);

                l.add(0, data);

                uidMapAdd(Constants.uid().get(data), data);
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

            localmapBuild();
            if (localmap != null) {
                l = localmap.get(analysis.getId());
                if (l != null && l.size() > 0 && l.get(0).getId() < 0) {
                    data = l.remove(0);
                    analysisIntNotes.remove(data);
                    dataObjectRemove(data.getId(), data);
                    uidMapRemove(Constants.uid().get(data));
                }
            }
        }

        /**
         * Returns the number of internal note(s)
         */
        public int count(AnalysisDO analysis) {
            ArrayList<NoteViewDO> l;

            localmapBuild();
            if (analysisIntNotes == null)
                return 0;

            l = localmap.get(analysis.getId());
            return (l == null) ? 0 : l.size();
        }

        /*
         * find the index to internal editable notes
         */
        private void localmapBuild() {
            ArrayList<NoteViewDO> l;

            if (analysisIntNotes != null) {
                if (localmap == null) {
                    localmap = new HashMap<Integer, ArrayList<NoteViewDO>>();
                    for (NoteViewDO data : analysisIntNotes) {
                        l = localmap.get(data.getReferenceId());
                        if (l == null) {
                            l = new ArrayList<NoteViewDO>();
                            localmap.put(data.getReferenceId(), l);
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
        transient protected HashMap<Integer, ArrayList<AnalysisUserViewDO>> localmap = null;

        /**
         * Returns the user for given analysis at specified index.
         */
        public AnalysisUserViewDO get(AnalysisDO analysis, int i) {
            localmapBuild();
            return localmap.get(analysis.getId()).get(i);
        }

        /**
         * Returns a new storage location for a sample item or analysis
         */
        public AnalysisUserViewDO add(AnalysisDO analysis) {
            AnalysisUserViewDO data;

            data = new AnalysisUserViewDO();
            data.setId(getNextUID());
            data.setAnalysisId(analysis.getId());
            if (users == null)
                users = new ArrayList<AnalysisUserViewDO>();
            users.add(data);
            localmapAdd(data);
            uidMapAdd(Constants.uid().get(data), data);

            return data;
        }

        /**
         * Removes an analysis qa event
         */
        public void remove(AnalysisDO analysis, int i) {
            AnalysisUserViewDO data;

            localmapBuild();
            data = localmap.get(analysis.getId()).get(i);
            users.remove(data);
            localmapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        public void remove(AnalysisDO analysis, AnalysisUserViewDO data) {
            users.remove(data);
            localmapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        /**
         * Returns the number of users associated with specified analysis
         */
        public int count(AnalysisDO analysis) {
            ArrayList<AnalysisUserViewDO> l;

            if (users != null) {
                localmapBuild();
                l = localmap.get(analysis.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash map from analysis user list.
         */
        private void localmapBuild() {
            if (localmap == null && users != null) {
                localmap = new HashMap<Integer, ArrayList<AnalysisUserViewDO>>();
                for (AnalysisUserViewDO data : users)
                    localmapAdd(data);
            }
        }

        /*
         * adds a new analysis user to the hash map
         */
        private void localmapAdd(AnalysisUserViewDO data) {
            ArrayList<AnalysisUserViewDO> l;

            if (localmap != null) {
                l = localmap.get(data.getAnalysisId());
                if (l == null) {
                    l = new ArrayList<AnalysisUserViewDO>();
                    localmap.put(data.getAnalysisId(), l);
                }
                l.add(data);
            }
        }

        /*
         * removes the a analysis user from hash map
         */
        private void localmapRemove(AnalysisUserViewDO data) {
            ArrayList<AnalysisUserViewDO> l;

            if (localmap != null) {
                l = localmap.get(data.getAnalysisId());
                if (l != null)
                    l.remove(data);
            }
        }
    }

    public class Result {
        transient protected HashMap<Integer, ArrayList<ArrayList<ResultViewDO>>> localmap = null;

        /**
         * Returns the result for given analysis at row and col
         */
        public ResultViewDO get(AnalysisDO analysis, int r, int c) {
            localmapBuild();
            return localmap.get(analysis.getId()).get(r).get(c);
        }

        /**
         * Returns true if for given analysis a row group begins at specified
         * row
         */
        public boolean isHeader(AnalysisDO analysis, int r) {
            int rg1, rg2;

            if (r == 0)
                return true;

            localmapBuild();
            rg1 = localmap.get(analysis.getId()).get(r).get(0).getRowGroup();
            rg2 = localmap.get(analysis.getId()).get(r - 1).get(0).getRowGroup();
            return rg1 != rg2;
        }

        /**
         * Removes the specified result row from analysis
         */
        public void remove(AnalysisDO analysis, int r) {
            ArrayList<ResultViewDO> rl;
            ArrayList<ArrayList<ResultViewDO>> rls;

            localmapBuild();
            rls = localmap.get(analysis.getId());
            rl = rls.get(r);
            for (ResultViewDO data : rl) {
                results.remove(data);
                dataObjectRemove(data.getId(), data);
                uidMapRemove(Constants.uid().get(data));
            }

            rls.remove(r);
        }

        /**
         * Returns the number of result rows for specified analysis
         */
        public int count(AnalysisDO analysis) {
            localmapBuild();
            if (localmap != null && localmap.get(analysis.getId()) != null)
                return localmap.get(analysis.getId()).size();
            else
                return 0;
        }

        /**
         * Returns the number of result columns for specified analysis's row
         */
        public int count(AnalysisDO analysis, int r) {
            localmapBuild();
            return localmap.get(analysis.getId()).get(r).size();
        }

        /**
         * Returns the number of result columns for specified analysis's row
         */
        public int maxColumns(AnalysisDO analysis) {
            int n;
            ArrayList<ArrayList<ResultViewDO>> rl;

            localmapBuild();
            n = 0;
            rl = localmap.get(analysis.getId());
            if (rl != null) {
                for (ArrayList<ResultViewDO> l : rl)
                    n = Math.max(l.size(), n);
            }

            return n;
        }

        /*
         * create a hash map from all the results
         */
        private void localmapBuild() {
            Integer id;
            ArrayList<ResultViewDO> rl;
            ArrayList<ArrayList<ResultViewDO>> l;

            if (localmap == null && results != null) {
                id = null;
                l = null;
                rl = null;
                localmap = new HashMap<Integer, ArrayList<ArrayList<ResultViewDO>>>();
                for (ResultViewDO data : results) {
                    if (id == null || !id.equals(data.getAnalysisId())) {
                        id = data.getAnalysisId();
                        l = localmap.get(id);
                        if (l == null) {
                            l = new ArrayList<ArrayList<ResultViewDO>>();
                            localmap.put(id, l);
                        }
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
    }

    /**
     * Class to manage the worksheets associated with the sample's analyses
     */
    public class Worksheet {
        transient protected HashMap<Integer, ArrayList<AnalysisWorksheetVO>> localmap = null;

        /**
         * Returns the worksheets for given analysis at specified index.
         */
        public AnalysisWorksheetVO get(AnalysisDO analysis, int i) {
            localmapBuild();
            return localmap.get(analysis.getId()).get(i);
        }

        /**
         * Returns the number of worksheets associated with specified analysis
         */
        public int count(AnalysisDO analysis) {
            ArrayList<AnalysisWorksheetVO> l;

            if (worksheets != null) {
                localmapBuild();
                l = localmap.get(analysis.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash map from worksheet list.
         */
        private void localmapBuild() {
            if (localmap == null && worksheets != null) {
                localmap = new HashMap<Integer, ArrayList<AnalysisWorksheetVO>>();
                for (AnalysisWorksheetVO data : worksheets)
                    localmapAdd(data);
            }
        }

        /*
         * adds a new worksheet to the hash map
         */
        private void localmapAdd(AnalysisWorksheetVO data) {
            ArrayList<AnalysisWorksheetVO> l;

            if (localmap != null) {
                l = localmap.get(data.getAnalysisId());
                if (l == null) {
                    l = new ArrayList<AnalysisWorksheetVO>();
                    localmap.put(data.getAnalysisId(), l);
                }
                l.add(data);
            }
        }
    }

    /**
     * adds an object to uid map
     */
    private void uidMapAdd(String uid, DataObject data) {
        if (uidMap != null)
            uidMap.put(uid, data);
    }

    /**
     * removes the object from uid map
     */
    private void uidMapRemove(String uid) {
        if (uidMap != null)
            uidMap.remove(uid);
    }

    /**
     * adds the data object to the list of objects that should be removed from
     * the database
     */
    private void dataObjectRemove(Integer id, DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();
        if (id > 0)
            removed.add(data);
    }
}