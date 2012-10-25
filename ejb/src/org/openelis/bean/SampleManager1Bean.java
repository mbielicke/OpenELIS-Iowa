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
package org.openelis.bean;

import static org.openelis.manager.SampleManager1Accessor.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.AnalysisUserLocal;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.NoteLocal;
import org.openelis.local.ResultLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.local.SampleItemLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.local.SamplePrivateWellLocal;
import org.openelis.local.SampleProjectLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.local.StorageLocal;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleManager1;
import org.openelis.remote.SampleManager1Remote;

@Stateless
@SecurityDomain("openelis")
public class SampleManager1Bean implements SampleManager1Remote {

    @EJB
    private LockLocal                lock;

    @EJB
    private SampleLocal              sample;

    @EJB
    private SampleEnvironmentalLocal sampleEnvironmental;

    @EJB
    private SampleSDWISLocal         sampleSDWIS;

    @EJB
    private SamplePrivateWellLocal   samplePrivate;

    @EJB
    private SampleOrganizationLocal  sampleOrganization;

    @EJB
    private SampleProjectLocal       sampleProject;

    @EJB
    private SampleQAEventLocal       sampleQA;

    @EJB
    private AuxDataLocal             auxdata;

    @EJB
    private NoteLocal                note;

    @EJB
    private SampleItemLocal          item;

    @EJB
    private StorageLocal             storage;

    @EJB
    private AnalysisLocal            analysis;

    @EJB
    private AnalysisQAEventLocal     analysisQA;

    @EJB
    private AnalysisUserLocal        user;

    @EJB
    private ResultLocal              result;

    /**
     * Returns a sample manager for specified primary id and requested load
     * elements
     */
    public SampleManager1 fetchById(Integer sampleId, SampleManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<SampleManager1> sms;

        ids = new ArrayList<Integer>(1);
        ids.add(sampleId);
        sms = fetchByIds(ids, elements);
        return sms.size() == 0 ? null : sms.get(0);
    }

    /**
     * Returns sample managers for specified primary ids and requested load
     * elements
     */
    public ArrayList<SampleManager1> fetchByIds(ArrayList<Integer> sampleIds,
                                                SampleManager1.Load... elements) throws Exception {
        SampleManager1 sm;
        ArrayList<Integer> ids1, ids2;
        ArrayList<SampleManager1> sms;
        HashMap<Integer, SampleManager1> map1, map2;
        EnumSet<SampleManager1.Load> el;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        sms = new ArrayList<SampleManager1>();
        if (elements != null)
            el = EnumSet.copyOf(Arrays.asList(elements));
        else
            el = EnumSet.noneOf(SampleManager1.Load.class);

        /*
         * build level 1, everything is based on sample ids
         */
        ids1 = new ArrayList<Integer>();
        map1 = new HashMap<Integer, SampleManager1>();

        for (SampleDO data : sample.fetchByIds(sampleIds)) {
            sm = new SampleManager1();
            setSample(sm, data);
            sms.add(sm);

            ids1.add(data.getId()); // for fetch
            map1.put(data.getId(), sm); // for linking
        }

        /*
         * additional domains for each sample
         */
        for (SampleEnvironmentalDO data : sampleEnvironmental.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            setSampleEnvironmental(sm, data);
        }

        for (SampleSDWISViewDO data : sampleSDWIS.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            setSampleSDWIS(sm, data);
        }

        for (SamplePrivateWellViewDO data : samplePrivate.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            setSamplePrivateWell(sm, data);
        }

        /*
         * various lists for each sample
         */
        if (el.contains(SampleManager1.Load.ORGANIZATION)) {
            for (SampleOrganizationViewDO data : sampleOrganization.fetchBySampleIds(ids1)) {
                sm = map1.get(data.getSampleId());
                addOrganization(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.PROJECT)) {
            for (SampleProjectViewDO data : sampleProject.fetchBySampleIds(ids1)) {
                sm = map1.get(data.getSampleId());
                addProject(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.QA)) {
            for (SampleQaEventViewDO data : sampleQA.fetchBySampleIds(ids1)) {
                sm = map1.get(data.getSampleId());
                addSampleQA(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.AUXDATA)) {
            for (AuxDataViewDO data : auxdata.fetchByIds(ids1, ReferenceTable.SAMPLE)) {
                sm = map1.get(data.getReferenceId());
                addAuxilliary(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.NOTE)) {
            for (NoteViewDO data : note.fetchByIds(ids1, ReferenceTable.SAMPLE)) {
                sm = map1.get(data.getReferenceId());
                addSampleNote(sm, data);
            }
        }

        /*
         * build level 2, everything is based on item ids
         */
        ids2 = new ArrayList<Integer>();
        map2 = new HashMap<Integer, SampleManager1>();
        for (SampleItemViewDO data : item.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            addItem(sm, data);
            if ( !map2.containsKey(data.getId())) {
                ids2.add(data.getId());
                map2.put(data.getId(), sm);
            }
        }

        if (el.contains(SampleManager1.Load.STORAGE)) {
            for (StorageViewDO data : storage.fetchByIds(ids2, ReferenceTable.SAMPLE_ITEM)) {
                sm = map2.get(data.getReferenceId());
                addStorage(sm, data);
            }
        }

        /*
         * build level 3, everything is based on analysis ids
         */
        ids1 = new ArrayList<Integer>();
        map1 = new HashMap<Integer, SampleManager1>();
        for (AnalysisViewDO data : analysis.fetchBySampleItemIds(ids2)) {
            sm = map2.get(data.getSampleItemId());
            addAnalysis(sm, data);
            if ( !map1.containsKey(data.getId())) {
                ids1.add(data.getId());
                map1.put(data.getId(), sm);
            }
        }
        ids2 = null;
        map2 = null;

        if (el.contains(SampleManager1.Load.NOTE)) {
            for (NoteViewDO data : note.fetchByIds(ids1, ReferenceTable.ANALYSIS)) {
                sm = map1.get(data.getReferenceId());
                addAnalysisNote(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.QA)) {
            for (AnalysisQaEventViewDO data : analysisQA.fetchByAnalysisIds(ids1)) {
                sm = map1.get(data.getAnalysisId());
                addAnalysisQA(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.STORAGE)) {
            for (StorageViewDO data : storage.fetchByIds(ids1, ReferenceTable.ANALYSIS)) {
                sm = map1.get(data.getReferenceId());
                addStorage(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.ANALYSISUSER)) {
            for (AnalysisUserViewDO data : user.fetchByAnalysisIds(ids1)) {
                sm = map1.get(data.getAnalysisId());
                addUser(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.RESULT)) {
            for (ResultViewDO data : result.fetchByAnalysisIds(ids1)) {
                sm = map1.get(data.getAnalysisId());
                addResult(sm, data);
            }
        }

        return sms;
    }

    /**
     * Returns sample managers for specified analysis ids and requested load
     * elements
     */
    public ArrayList<SampleManager1> fetchByAnalyses(ArrayList<Integer> analysisIds,
                                                     SampleManager1.Load... elements) throws Exception {
        SampleManager1 sm;
        ArrayList<Integer> ids1, ids2;
        ArrayList<SampleManager1> sms;
        HashMap<Integer, SampleManager1> map1, map2;
        EnumSet<SampleManager1.Load> el;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        sms = new ArrayList<SampleManager1>();
        if (elements != null)
            el = EnumSet.copyOf(Arrays.asList(elements));
        else
            el = EnumSet.noneOf(SampleManager1.Load.class);

        /*
         * we will do it bottom up since we only have analysis ids.
         * 
         * build level 3, everything is based on analysis ids
         */
        ids1 = new ArrayList<Integer>();
        map1 = new HashMap<Integer, SampleManager1>();

        return sms;
    }

    /**
     * Returns a sample manager for specified accession number and requested
     * load elements
     */
    public SampleManager1 fetchByAccession(Integer accessionNumber, SampleManager1.Load... elements) throws Exception {
        return null;
    }

    /**
     * Returns a sample manager based on the specified query and requested load
     * elements
     */
    public ArrayList<SampleManager1> fetchByQuery(Query query, SampleManager1.Load... elements) throws Exception {
        return null;
    }

    /**
     * Returns a locked sample manager with specified sample id
     */
    public SampleManager1 fetchForUpdate(Integer sampleId) throws Exception {
        return fetchForUpdate(sampleId,
                              SampleManager1.Load.ORGANIZATION,
                              SampleManager1.Load.PROJECT,
                              SampleManager1.Load.QA,
                              SampleManager1.Load.AUXDATA,
                              SampleManager1.Load.STORAGE,
                              SampleManager1.Load.NOTE,
                              SampleManager1.Load.ANALYSISUSER,
                              SampleManager1.Load.RESULT);
    }

    /**
     * Returns a locked sample manager with specified sample id and requested
     * load elements
     */
    public SampleManager1 fetchForUpdate(Integer sampleId, SampleManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<SampleManager1> sms;

        ids = new ArrayList<Integer>(1);
        ids.add(sampleId);
        sms = fetchForUpdate(ids, elements);
        return sms.size() == 0 ? null : sms.get(0);
    }

    /**
     * Returns a list of locked sample managers with specified sample ids and
     * requested load elements
     */
    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds,
                                                    SampleManager1.Load... elements) throws Exception {
        lock.lock(ReferenceTable.SAMPLE, sampleIds);
        return fetchByIds(sampleIds, elements);
    }

    /**
     * Returns sample managers loaded with additional elements
     */
    public SampleManager1 fetchWith(SampleManager1 sm, SampleManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        EnumSet<SampleManager1.Load> el;

        if (elements != null)
            el = EnumSet.copyOf(Arrays.asList(elements));
        else
            return sm;

        /*
         * various lists for each sample
         */
        ids = new ArrayList<Integer>(1);
        ids.add(getSample(sm).getId());
        if (el.contains(SampleManager1.Load.ORGANIZATION)) {
            setOrganizations(sm, null);
            for (SampleOrganizationViewDO data : sampleOrganization.fetchBySampleIds(ids))
                addOrganization(sm, data);
        }

        if (el.contains(SampleManager1.Load.PROJECT)) {
            setProjects(sm, null);
            for (SampleProjectViewDO data : sampleProject.fetchBySampleIds(ids))
                addProject(sm, data);
        }

        if (el.contains(SampleManager1.Load.QA)) {
            setSampleQAs(sm, null);
            for (SampleQaEventViewDO data : sampleQA.fetchBySampleIds(ids))
                addSampleQA(sm, data);
        }

        if (el.contains(SampleManager1.Load.AUXDATA)) {
            setAuxilliary(sm, null);
            for (AuxDataViewDO data : auxdata.fetchByIds(ids, ReferenceTable.SAMPLE))
                addAuxilliary(sm, data);
        }

        if (el.contains(SampleManager1.Load.NOTE)) {
            setSampleNotes(sm, null);
            for (NoteViewDO data : note.fetchByIds(ids, ReferenceTable.SAMPLE))
                addSampleNote(sm, data);
        }

        /*
         * build level 2, everything is based on item ids
         */
        if (el.contains(SampleManager1.Load.STORAGE)) {
            ids = new ArrayList<Integer>();
            for (SampleItemViewDO data : getItems(sm))
                ids.add(data.getId());
            setStorages(sm, null);
            for (StorageViewDO data : storage.fetchByIds(ids, ReferenceTable.SAMPLE_ITEM))
                addStorage(sm, data);
        }

        /*
         * build level 3, everything is based on analysis ids
         */
        ids = new ArrayList<Integer>();
        for (AnalysisViewDO data : getAnalyses(sm))
            ids.add(data.getId());

        if (el.contains(SampleManager1.Load.NOTE)) {
            setAnalysisNotes(sm, null);
            for (NoteViewDO data : note.fetchByIds(ids, ReferenceTable.ANALYSIS))
                addAnalysisNote(sm, data);
        }

        if (el.contains(SampleManager1.Load.QA)) {
            setAnalysisQAs(sm, null);
            for (AnalysisQaEventViewDO data : analysisQA.fetchByAnalysisIds(ids))
                addAnalysisQA(sm, data);
        }

        if (el.contains(SampleManager1.Load.STORAGE)) {
            for (StorageViewDO data : storage.fetchByIds(ids, ReferenceTable.ANALYSIS))
                addStorage(sm, data);
        }

        if (el.contains(SampleManager1.Load.ANALYSISUSER)) {
            setUsers(sm, null);
            for (AnalysisUserViewDO data : user.fetchByAnalysisIds(ids))
                addUser(sm, data);
        }

        if (el.contains(SampleManager1.Load.RESULT)) {
            setResults(sm, null);
            for (ResultViewDO data : result.fetchByAnalysisIds(ids))
                addResult(sm, data);
        }

        return sm;
    }

    /*
     * test code
     */
    public ArrayList<SampleManager> fetchByIds(ArrayList<Integer> sampleIds) throws Exception {
        ArrayList<SampleManager> sms;
        long before, after;

        sms = new ArrayList<SampleManager>();

        before = System.currentTimeMillis();
        System.out.println("started at : " + before);
        for (int i = 0; i < sampleIds.size(); i++ ) {
            try {
                sms.add(SampleManager.fetchWithAllDataById(sampleIds.get(i)));
            } catch (NotFoundException e) {
                // ignore
            }
        }

        after = System.currentTimeMillis();
        System.out.println("ended at : " + after);

        System.out.println("total time : " + (after - before) / 1000 + " seconds");

        return sms;
    }
}