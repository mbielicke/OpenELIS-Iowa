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
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SectionPermission;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;

@Stateless
@SecurityDomain("openelis")
public class SampleManager1Bean {

    @EJB
    private LockBean                lock;

    @EJB
    private SampleBean              sample;

    @EJB
    private SampleEnvironmentalBean sampleEnvironmental;

    @EJB
    private SampleSDWISBean         sampleSDWIS;

    @EJB
    private SamplePrivateWellBean   samplePrivate;
    
    @EJB
    private SampleNeonatalBean      sampleNeonatal;

    @EJB
    private SampleOrganizationBean  sampleOrganization;

    @EJB
    private SampleProjectBean       sampleProject;

    @EJB
    private SampleQAEventBean       sampleQA;

    @EJB
    private AuxDataBean             auxdata;

    @EJB
    private NoteBean                note;

    @EJB
    private SampleItemBean          item;

    @EJB
    private StorageBean             storage;

    @EJB
    private AnalysisBean            analysis;

    @EJB
    private AnalysisQAEventBean     analysisQA;

    @EJB
    private AnalysisUserBean        user;

    @EJB
    private ResultBean              result;

    @EJB
    private TestManagerBean         test;

    @EJB
    private SystemVariableBean      systemVariable;

    @EJB
    private UserCacheBean           userCache;

    private static final Logger    log = Logger.getLogger("openelis");

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
        
        for (SampleNeonatalDO data : sampleNeonatal.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            setSampleNeonatal(sm, data);
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
            for (AuxDataViewDO data : auxdata.fetchByIds(ids1, Constants.table().SAMPLE)) {
                sm = map1.get(data.getReferenceId());
                addAuxilliary(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.NOTE)) {
            for (NoteViewDO data : note.fetchByIds(ids1, Constants.table().SAMPLE)) {
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
            for (StorageViewDO data : storage.fetchByIds(ids2, Constants.table().SAMPLE_ITEM)) {
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
            for (NoteViewDO data : note.fetchByIds(ids1, Constants.table().ANALYSIS)) {
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
            for (StorageViewDO data : storage.fetchByIds(ids1, Constants.table().ANALYSIS)) {
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
     * elements. Optionally, the Load.SINGLERESULT can be specified to only
     * fetch the result for the specified analysis rather than all the analysis.
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
         * build level 2, everything is based on item ids
         */
        ids1 = new ArrayList<Integer>(); // sample ids
        map1 = new HashMap<Integer, SampleManager1>();
        ids2 = new ArrayList<Integer>(); // sample item ids
        map2 = new HashMap<Integer, SampleManager1>();
        for (SampleItemViewDO data : item.fetchByAnalysisIds(analysisIds)) {
            sm = map1.get(data.getSampleId());
            if (sm == null) {
                sm = new SampleManager1();
                sms.add(sm);
                ids1.add(data.getSampleId());
                map1.put(data.getSampleId(), sm);
            }
            addItem(sm, data);
            if ( !map2.containsKey(data.getId())) {
                ids2.add(data.getId());
                map2.put(data.getId(), sm);
            }
        }

        if (el.contains(SampleManager1.Load.STORAGE)) {
            for (StorageViewDO data : storage.fetchByIds(ids2, Constants.table().SAMPLE_ITEM)) {
                sm = map2.get(data.getReferenceId());
                addStorage(sm, data);
            }
        }

        /*
         * build level 1, everything is based on sample ids
         */
        for (SampleDO data : sample.fetchByIds(ids1)) {
            sm = map1.get(data.getId());
            setSample(sm, data);
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
        
        for (SampleNeonatalDO data : sampleNeonatal.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            setSampleNeonatal(sm, data);
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
            for (AuxDataViewDO data : auxdata.fetchByIds(ids1, Constants.table().SAMPLE)) {
                sm = map1.get(data.getReferenceId());
                addAuxilliary(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.NOTE)) {
            for (NoteViewDO data : note.fetchByIds(ids1, Constants.table().SAMPLE)) {
                sm = map1.get(data.getReferenceId());
                addSampleNote(sm, data);
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
            for (NoteViewDO data : note.fetchByIds(ids1, Constants.table().ANALYSIS)) {
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
            for (StorageViewDO data : storage.fetchByIds(ids1, Constants.table().ANALYSIS)) {
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
        } else if (el.contains(SampleManager1.Load.SINGLERESULT)) {
            for (ResultViewDO data : result.fetchByAnalysisIds(analysisIds)) {
                sm = map1.get(data.getAnalysisId());
                addResult(sm, data);
            }
        }

        return sms;
    }

    /**
     * Returns a sample manager for specified accession number and requested
     * load elements
     */
    public SampleManager1 fetchByAccession(Integer accessionNumber, SampleManager1.Load... elements) throws Exception {
        Query query;
        QueryData field;
        ArrayList<SampleManager1> sms; 
        
        query = new Query();
        field = new QueryData();
        field.key = SampleMeta.getAccessionNumber();
        field.query = accessionNumber.toString();
        field.type  = QueryData.Type.INTEGER;
        query.setFields(field);

        sms = fetchByQuery(query, elements);
        return sms.size() == 0 ? null : sms.get(0);
    }

    /**
     * Returns a sample manager based on the specified query and requested load
     * elements
     */
    public ArrayList<SampleManager1> fetchByQuery(Query query, SampleManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();
        for (IdAccessionVO vo : sample.query(query.getFields(), 0, -1))
            ids.add(vo.getId());
        return fetchByIds(ids, elements);
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
            for (AuxDataViewDO data : auxdata.fetchByIds(ids, Constants.table().SAMPLE))
                addAuxilliary(sm, data);
        }

        if (el.contains(SampleManager1.Load.NOTE)) {
            setSampleNotes(sm, null);
            for (NoteViewDO data : note.fetchByIds(ids, Constants.table().SAMPLE))
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
            for (StorageViewDO data : storage.fetchByIds(ids, Constants.table().SAMPLE_ITEM))
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
            for (NoteViewDO data : note.fetchByIds(ids, Constants.table().ANALYSIS))
                addAnalysisNote(sm, data);
        }

        if (el.contains(SampleManager1.Load.QA)) {
            setAnalysisQAs(sm, null);
            for (AnalysisQaEventViewDO data : analysisQA.fetchByAnalysisIds(ids))
                addAnalysisQA(sm, data);
        }

        if (el.contains(SampleManager1.Load.STORAGE)) {
            for (StorageViewDO data : storage.fetchByIds(ids, Constants.table().ANALYSIS))
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

    /**
     * Returns a locked sample manager with specified sample id
     */
    @RolesAllowed("sample-update")
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
    @RolesAllowed("sample-update")
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
    @RolesAllowed("sample-update")
    public ArrayList<SampleManager1> fetchForUpdate(ArrayList<Integer> sampleIds,
                                                    SampleManager1.Load... elements) throws Exception {
        lock.lock(Constants.table().SAMPLE, sampleIds);
        return fetchByIds(sampleIds, elements);
    }

    /**
     * Returns a list of locked sample managers with specified analysis ids and
     * requested load elements.
     */
    @RolesAllowed("sample-update")
    public ArrayList<SampleManager1> fetchForUpdateByAnalyses(ArrayList<Integer> analysisIds,
                                                              SampleManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();
        for (SampleItemViewDO data : item.fetchByAnalysisIds(analysisIds))
            ids.add(data.getSampleId());

        lock.lock(Constants.table().SAMPLE, ids);
        return fetchByAnalyses(analysisIds, elements);
    }

    /**
     * Adds the sample and all related records into the database. All the
     * records within the manager are validated before the insertion.
     */
    @RolesAllowed("sample-add")
    public SampleManager1 add(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        ArrayList<SampleManager1> sms;

        sms = new ArrayList<SampleManager1>(1);
        sms.add(sm);
        add(sms, ignoreWarnings);
        return sms.get(0);
    }

    /**
     * Adds all the samples and all related records into the database. All the
     * records within the manager are validated before the insertion.
     */
    @RolesAllowed("sample-add")
    public ArrayList<SampleManager1> add(ArrayList<SampleManager1> sms, boolean ignoreWarnings) throws Exception {
        int dep, ldep;
        boolean nodep;
        Integer tmpid, id, so;
        HashSet<Integer> ids;
        ArrayList<TestManager> tms;
        HashMap<Integer, Integer> idmap, seq;

        /*
         * validation needs test manager. Build a list of analysis test ids from
         * to fetch test managers
         */
        ids = new HashSet<Integer>();
        for (SampleManager1 sm : sms) {
            for (AnalysisViewDO an : getAnalyses(sm))
                ids.add(an.getTestId());
        }
        tms = test.fetchByIds(new ArrayList<Integer>(ids));
        validate(sms, tms, ignoreWarnings);
        tms = null;
        ids = null;

        /*
         * the front code uses negative ids (temporary ids) to link sample items
         * and analysis, analysis and results. The negative ids are mapped to
         * actual database ids through idmap. the seq map is used for managing
         * result sort order.
         */
        idmap = new HashMap<Integer, Integer>();
        seq = new HashMap<Integer, Integer>();
        for (SampleManager1 sm : sms) {
            idmap.clear();
            seq.clear();

            // add sample
            sample.add(getSample(sm));

            // add sample domain
            if (getSampleEnvironmental(sm) != null) {
                getSampleEnvironmental(sm).setSampleId(getSample(sm).getId());
                sampleEnvironmental.add(getSampleEnvironmental(sm));
            } else if (getSamplePrivateWell(sm) != null) {
                getSamplePrivateWell(sm).setSampleId(getSample(sm).getId());
                samplePrivate.add(getSamplePrivateWell(sm));
            } else if (getSampleSDWIS(sm) != null) {
                getSampleSDWIS(sm).setSampleId(getSample(sm).getId());
                sampleSDWIS.add(getSampleSDWIS(sm));
            } else if (getSampleNeonatal(sm) != null) {
                getSampleNeonatal(sm).setSampleId(getSample(sm).getId());
                sampleNeonatal.add(getSampleNeonatal(sm));
            }

            // add top level items/lists
            if (getOrganizations(sm) != null)
                for (SampleOrganizationViewDO data : getOrganizations(sm)) {
                    data.setSampleId(getSample(sm).getId());
                    sampleOrganization.add(data);
                }

            if (getProjects(sm) != null)
                for (SampleProjectViewDO data : getProjects(sm)) {
                    data.setSampleId(getSample(sm).getId());
                    sampleProject.add(data);
                }

            if (getSampleQAs(sm) != null)
                for (SampleQaEventViewDO data : getSampleQAs(sm)) {
                    data.setSampleId(getSample(sm).getId());
                    sampleQA.add(data);
                }

            if (getAuxilliary(sm) != null)
                for (AuxDataViewDO data : getAuxilliary(sm)) {
                    data.setReferenceTableId(Constants.table().SAMPLE);
                    data.setReferenceId(getSample(sm).getId());
                    auxdata.add(data);
                }

            if (getSampleNotes(sm) != null)
                for (NoteViewDO data : getSampleNotes(sm)) {
                    data.setReferenceTableId(Constants.table().SAMPLE);
                    data.setReferenceId(getSample(sm).getId());
                    note.add(data);
                }

            // add sample items.
            for (SampleItemViewDO data : getItems(sm)) {
                tmpid = data.getId();
                item.add(data);
                idmap.put(tmpid, data.getId());
            }

            /*
             * some analysis can be dependent on other analysis for prep or for
             * reflex. Additionally an analysis maybe dependent on a result that
             * triggered the reflex. This code tries to resolve those
             * dependencies by alternating between adding analysis and result
             * until all the records have been added. The code also detects
             * infinite loops by ensuring every iteration resolves some
             * dependency
             */
            dep = ldep = 0;
            do {
                ldep = dep;
                dep = 0;
                // add analysis
                for (AnalysisViewDO data : getAnalyses(sm)) {
                    if (data.getId() < 0) {
                        nodep = true;

                        if (data.getPreAnalysisId() != null && data.getPreAnalysisId() < 0) {
                            id = idmap.get(data.getPreAnalysisId());
                            if (id != null)
                                data.setPreAnalysisId(id);
                            else
                                nodep = false;
                        }

                        if (data.getParentAnalysisId() != null && data.getParentAnalysisId() < 0) {
                            id = idmap.get(data.getParentAnalysisId());
                            if (id != null)
                                data.setParentAnalysisId(id);
                            else
                                nodep = false;
                        }

                        if (data.getParentResultId() != null && data.getParentResultId() < 0) {
                            id = idmap.get(data.getParentResultId());
                            if (id != null)
                                data.setParentResultId(id);
                            else
                                nodep = false;
                        }

                        if (nodep) {
                            tmpid = data.getId();
                            data.setSampleItemId(idmap.get(data.getSampleItemId()));
                            analysis.add(data);
                            idmap.put(tmpid, data.getId());
                        } else {
                            dep++ ;
                        }
                    }
                }

                // add results
                if (getResults(sm) != null) {
                    for (ResultViewDO data : getResults(sm)) {
                        if (data.getId() < 0) {
                            id = idmap.get(data.getAnalysisId());
                            if (id != null) {
                                data.setAnalysisId(id);

                                // sort order is per analysis
                                so = seq.get(id);
                                if (so == null)
                                    so = 1;
                                data.setSortOrder(so);
                                seq.put(id, so + 1);

                                tmpid = data.getId();
                                result.add(data);
                                idmap.put(tmpid, data.getId());
                            }
                        }
                    }
                }

            } while (dep > 0 && ldep != dep);
            // TODO change the exception to the actual exception
            if (dep > 0 && ldep == dep)
                throw new InconsistencyException("Infinite loop");

            // add second tier analysis related records
            if (getAnalysisNotes(sm) != null)
                for (NoteViewDO data : getAnalysisNotes(sm)) {
                    data.setReferenceTableId(Constants.table().ANALYSIS);
                    data.setReferenceId(idmap.get(data.getReferenceId()));
                    note.add(data);
                }

            if (getAnalysisQAs(sm) != null)
                for (AnalysisQaEventViewDO data : getAnalysisQAs(sm)) {
                    data.setAnalysisId(idmap.get(data.getAnalysisId()));
                    analysisQA.add(data);
                }

            if (getUsers(sm) != null)
                for (AnalysisUserViewDO data : getUsers(sm)) {
                    data.setAnalysisId(idmap.get(data.getAnalysisId()));
                    user.add(data);
                }

            if (getStorages(sm) != null)
                for (StorageViewDO data : getStorages(sm)) {
                    data.setReferenceId(idmap.get(data.getReferenceId()));
                    storage.add(data);
                }
        }

        return sms;
    }

    /**
     * Updates the sample and related records in the database. The records are
     * validated before update and the sample record must have a lock record.
     */
    @RolesAllowed("sample-update")
    public SampleManager1 update(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        ArrayList<SampleManager1> sms;

        sms = new ArrayList<SampleManager1>(1);
        sms.add(sm);
        update(sms, ignoreWarnings);
        return sms.get(0);
    }

    /**
     * Updates all the samples and related records in the database. All the
     * records are validated before update and the sample records must each have
     * a lock record.
     */
    @RolesAllowed("sample-update")
    public ArrayList<SampleManager1> update(ArrayList<SampleManager1> sms, boolean ignoreWarnings) throws Exception {
        int dep, ldep;
        boolean nodep;
        Integer tmpid, id, so;
        HashSet<Integer> ids;
        ArrayList<TestManager> tms;
        HashMap<Integer, Integer> imap, amap, rmap, seq;

        /*
         * validation needs test manager. Build a list of analysis test ids from
         * to fetch test managers. Also build a test map for permission check.
         */
        ids = new HashSet<Integer>();
        for (SampleManager1 sm : sms) {
            for (AnalysisViewDO an : getAnalyses(sm))
                ids.add(an.getTestId());
        }
        tms = test.fetchByIds(new ArrayList<Integer>(ids));
        validate(sms, tms, ignoreWarnings);
        tms = null;

        /*
         * check all the locks
         */
        ids.clear();
        for (SampleManager1 sm : sms)
            ids.add(getSample(sm).getId());
        lock.validateLock(Constants.table().SAMPLE, new ArrayList<Integer>(ids));

        /*
         * the front code uses negative ids (temporary ids) to link sample items
         * and analysis, analysis and results. The negative ids are mapped to
         * actual database ids through several maps: imap for items, amap for
         * analysis and . the seq map is used for managing result sort order.
         */
        imap = new HashMap<Integer, Integer>();
        amap = new HashMap<Integer, Integer>();
        rmap = new HashMap<Integer, Integer>();
        seq = new HashMap<Integer, Integer>();
        for (SampleManager1 sm : sms) {
            imap.clear();
            amap.clear();
            rmap.clear();
            seq.clear();

            /*
             * go through remove list and delete all the unwanted records
             */
            if (getRemoved(sm) != null) {
                for (DataObject data : getRemoved(sm)) {
                    if (data instanceof SampleEnvironmentalDO)
                        sampleEnvironmental.delete( ((SampleEnvironmentalDO)data));
                    else if (data instanceof SampleSDWISViewDO)
                        sampleSDWIS.delete( ((SampleSDWISViewDO)data));
                    else if (data instanceof SamplePrivateWellViewDO)
                        samplePrivate.delete( ((SamplePrivateWellViewDO)data));
                    else if (data instanceof SampleNeonatalDO)
                        sampleNeonatal.delete( ((SampleNeonatalDO)data));
                    else if (data instanceof SampleOrganizationViewDO)
                        sampleOrganization.delete( ((SampleOrganizationViewDO)data));
                    else if (data instanceof SampleProjectViewDO)
                        sampleProject.delete( ((SampleProjectViewDO)data));
                    else if (data instanceof SampleQaEventViewDO)
                        sampleQA.delete( ((SampleQaEventViewDO)data));
                    else if (data instanceof AuxDataViewDO)
                        auxdata.delete( ((AuxDataViewDO)data));
                    else if (data instanceof NoteViewDO)
                        note.delete( ((NoteViewDO)data));
                    else if (data instanceof SampleItemViewDO)
                        item.delete( ((SampleItemViewDO)data));
                    else if (data instanceof AnalysisViewDO)
                        analysis.delete( ((AnalysisViewDO)data));
                    else if (data instanceof AnalysisQaEventViewDO)
                        analysisQA.delete( ((AnalysisQaEventViewDO)data));
                    else if (data instanceof StorageViewDO)
                        storage.delete( ((StorageViewDO)data));
                    else if (data instanceof AnalysisUserViewDO)
                        user.delete( ((AnalysisUserViewDO)data));
                    else if (data instanceof ResultViewDO)
                        result.delete( ((ResultViewDO)data));
                    else
                        throw new Exception("ERROR: DataObject passed for removal is of unknown type");
                }
            }

            // update sample
            sample.update(getSample(sm));

            // update sample domain
            if (getSampleEnvironmental(sm) != null) {
                if (getSampleEnvironmental(sm).getId() == null) {
                    getSampleEnvironmental(sm).setSampleId(getSample(sm).getId());
                    sampleEnvironmental.add(getSampleEnvironmental(sm));
                } else {
                    sampleEnvironmental.update(getSampleEnvironmental(sm));
                }
            } else if (getSampleSDWIS(sm) != null) {
                if (getSampleSDWIS(sm).getId() == null) {
                    getSampleSDWIS(sm).setSampleId(getSample(sm).getId());
                    sampleSDWIS.add(getSampleSDWIS(sm));
                } else {
                    sampleSDWIS.update(getSampleSDWIS(sm));
                }
            } else if (getSamplePrivateWell(sm) != null) {
                if (getSamplePrivateWell(sm).getId() == null) {
                    getSamplePrivateWell(sm).setSampleId(getSample(sm).getId());
                    samplePrivate.add(getSamplePrivateWell(sm));
                } else {
                    samplePrivate.update(getSamplePrivateWell(sm));
                }
            } else if (getSampleNeonatal(sm) != null) {
                if (getSampleNeonatal(sm).getId() == null) {
                    getSampleNeonatal(sm).setSampleId(getSample(sm).getId());
                    sampleNeonatal.add(getSampleNeonatal(sm));
                } else {
                    sampleNeonatal.update(getSampleNeonatal(sm));
                }
            }

            // add top level items/lists
            if (getOrganizations(sm) == null)
                for (SampleOrganizationViewDO data : getOrganizations(sm)) {
                    if (data.getId() == null) {
                        data.setSampleId(getSample(sm).getId());
                        sampleOrganization.add(data);
                    } else {
                        sampleOrganization.update(data);
                    }
                }

            if (getProjects(sm) != null)
                for (SampleProjectViewDO data : getProjects(sm)) {
                    if (data.getId() == null) {
                        data.setSampleId(getSample(sm).getId());
                        sampleProject.add(data);
                    } else {
                        sampleProject.update(data);
                    }
                }

            if (getSampleQAs(sm) != null)
                for (SampleQaEventViewDO data : getSampleQAs(sm)) {
                    if (data.getId() == null) {
                        data.setSampleId(getSample(sm).getId());
                        sampleQA.add(data);
                    } else {
                        sampleQA.update(data);
                    }
                }

            if (getAuxilliary(sm) != null)
                for (AuxDataViewDO data : getAuxilliary(sm)) {
                    if (data.getId() == null) {
                        data.setReferenceTableId(Constants.table().SAMPLE);
                        data.setReferenceId(getSample(sm).getId());
                        auxdata.add(data);
                    } else {
                        auxdata.update(data);
                    }
                }

            if (getSampleNotes(sm) != null)
                for (NoteViewDO data : getSampleNotes(sm)) {
                    if (data.getId() == null) {
                        data.setReferenceTableId(Constants.table().SAMPLE);
                        data.setReferenceId(getSample(sm).getId());
                        note.add(data);
                    } else {
                        note.update(data);
                    }
                }

            // add/update sample items. keep track of all the keys (pos or neg)
            for (SampleItemViewDO data : getItems(sm)) {
                if (data.getId() < 0) {
                    tmpid = data.getId();
                    item.add(data);
                } else {
                    tmpid = data.getId();
                    item.update(data);
                }
                imap.put(tmpid, data.getId());
            }

            /*
             * the code and dependency is similar to the add routine.
             */
            dep = ldep = 0;
            do {
                ldep = dep;
                dep = 0;
                // add analysis
                for (AnalysisViewDO data : getAnalyses(sm)) {
                    nodep = true;

                    if (data.getPreAnalysisId() != null && data.getPreAnalysisId() < 0) {
                        id = amap.get(data.getPreAnalysisId());
                        if (id != null)
                            data.setPreAnalysisId(id);
                        else
                            nodep = false;
                    }

                    if (data.getParentAnalysisId() != null && data.getParentAnalysisId() < 0) {
                        id = amap.get(data.getParentAnalysisId());
                        if (id != null)
                            data.setParentAnalysisId(id);
                        else
                            nodep = false;
                    }

                    if (data.getParentResultId() != null && data.getParentResultId() < 0) {
                        id = rmap.get(data.getParentResultId());
                        if (id != null)
                            data.setParentResultId(id);
                        else
                            nodep = false;
                    }

                    if (nodep) {
                        if (data.getId() < 0) {
                            tmpid = data.getId();
                            data.setSampleItemId(imap.get(data.getSampleItemId()));
                            analysis.add(data);
                            amap.put(tmpid, data.getId());
                            amap.put(data.getId(), data.getId());
                        } else if ( !amap.containsKey(data.getId())) {
                            tmpid = data.getId();
                            analysis.update(data);
                            amap.put(tmpid, data.getId());
                        }
                    } else {
                        dep++ ;
                    }
                }

                // add results
                if (getResults(sm) != null) {
                    for (ResultViewDO data : getResults(sm)) {
                        id = data.getAnalysisId();
                        if (id < 0)
                            id = amap.get(id);
                        if (id != null && !rmap.containsKey(data.getId())) {
                            // sort order is per analysis. avoid updating sort
                            // order
                            // if numbers are ascending
                            so = seq.get(id);
                            if (so == null)
                                so = 1;
                            if (data.getSortOrder() != null && data.getSortOrder() >= so)
                                so = data.getSortOrder();
                            else
                                data.setSortOrder(so);
                            seq.put(id, so + 1);

                            tmpid = data.getId();
                            if (tmpid < 0) {
                                data.setAnalysisId(id);
                                result.add(data);
                            } else {
                                result.update(data);
                            }
                            rmap.put(tmpid, data.getId());
                        }
                    }
                }

            } while (dep > 0 && ldep != dep);

            if (dep > 0 && ldep == dep)
                throw new InconsistencyException("sample.unresolvedAnalyses");

            // add/update analysis notes
            for (NoteViewDO data : getAnalysisNotes(sm)) {
                if (data.getId() == null) {
                    data.setReferenceTableId(Constants.table().ANALYSIS);
                    data.setReferenceId(amap.get(data.getReferenceId()));
                    note.add(data);
                } else if (data.isChanged()) {
                    note.update(data);
                }
            }

            // add/update analysis qa events
            for (AnalysisQaEventViewDO data : getAnalysisQAs(sm)) {
                if (data.getId() == null) {
                    data.setAnalysisId(amap.get(data.getAnalysisId()));
                    analysisQA.add(data);
                } else if (data.isChanged()) {
                    analysisQA.update(data);
                }
            }

            // add/update analysis users
            for (AnalysisUserViewDO data : getUsers(sm)) {
                if (data.getId() == null) {
                    data.setAnalysisId(amap.get(data.getAnalysisId()));
                    user.add(data);
                } else if (data.isChanged()) {
                    user.update(data);
                }
            }

            // add/update storage
            for (StorageViewDO data : getStorages(sm)) {
                if (data.getId() == null) {
                    data.setReferenceId(amap.get(data.getReferenceId()));
                    storage.add(data);
                } else if (data.isChanged()) {
                    storage.update(data);
                }
            }
        }

        lock.unlock(Constants.table().SAMPLE, new ArrayList<Integer>(ids));

        return sms;
    }

    /**
     * Validates the sample manager for add or update. The routine throws a list
     * of exceptions/warnings listing all the problems for each sample.
     */
    protected void validate(ArrayList<SampleManager1> sms, ArrayList<TestManager> tms,
                            boolean ignoreWarning) throws Exception {
        int cnt;
        AnalysisViewDO ana;
        SystemVariableDO sys;
        ValidationErrorsList e;
        Integer accession, maxAccession;
        HashMap<Integer, SampleItemViewDO> imap;
        HashMap<Integer, AnalysisViewDO> amap;
        SystemUserPermission permission;

        e = new ValidationErrorsList();
        imap = new HashMap<Integer, SampleItemViewDO>();
        amap = new HashMap<Integer, AnalysisViewDO>();

        // user permission for adding/updating analysis
        permission = userCache.getPermission();

        /*
         * see what was the last accession number we have given out
         */
        try {
            sys = systemVariable.fetchByName("last_accession_number");
            maxAccession = Integer.valueOf(sys.getValue());
        } catch (Exception any) {
            log.log(Level.SEVERE, "Missing/invalid system variable 'last_accession_number'", e);
            throw new FormErrorException("systemVariable.missingInvalidSystemVariable",
                                         "last_accession_number");
        }

        for (SampleManager1 sm : sms) {
            /*
             * sample level
             */
            accession = null;
            if (getSample(sm) != null) {
                accession = getSample(sm).getAccessionNumber();
                if (getSample(sm).isChanged())
                    try {
                        sample.validate(getSample(sm), maxAccession, ignoreWarning);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
            }

            /*
             * additional domain sample validation for sdwis, private well, ...
             * samples should go here after checking to see if the VO/DO has
             * changed.
             */

            /*
             * samples have to have one report to.
             */
            cnt = 0;
            if (getOrganizations(sm) != null) {
                for (SampleOrganizationViewDO data : getOrganizations(sm))
                    if (Constants.dictionary().ORG_REPORT_TO.equals(data.getTypeId()))
                        cnt++ ;
            }
            if (cnt != 1 && !ignoreWarning)
                e.add(new FormErrorException("sample.moreThanOneReportToException", accession));

            /*
             * at least one sample item and items must have sample type
             */
            imap.clear();
            if (getItems(sm) == null || getItems(sm).size() < 1) {
                e.add(new FormErrorException("sample.minOneSampleItemException", accession));
            } else {
                for (SampleItemViewDO data : getItems(sm)) {
                    imap.put(data.getId(), data);
                    if (data.isChanged())
                        try {
                            item.validate(data, accession);
                        } catch (Exception err) {
                            DataBaseUtil.mergeException(e, err);
                        }
                }
            }

            /*
             * each analysis must be valid for sample item type
             */
            amap.clear();
            if (getAnalyses(sm) != null) {
                for (AnalysisViewDO data : getAnalyses(sm)) {
                    amap.put(data.getId(), data);
                    if (data.isChanged() || imap.get(data.getSampleItemId()).isChanged())
                        try {
                            analysis.validate(data,
                                              tms.get(data.getTestId()),
                                              accession,
                                              imap.get(data.getSampleItemId()),
                                              ignoreWarning);
                            if (data.isChanged())
                                validatePermission(getSample(sm).getAccessionNumber(),
                                                   data,
                                                   permission);
                        } catch (Exception err) {
                            DataBaseUtil.mergeException(e, err);
                        }
                }
            }

            /*
             * results must be valid for the group
             */
            if (getResults(sm) != null) {
                for (ResultViewDO data : getResults(sm)) {
                    ana = amap.get(data.getAnalysisId());
                    if (data.isChanged() || ana.isChanged())
                        try {
                            result.validate(data,
                                            tms.get(ana.getTestId()),
                                            accession,
                                            amap.get(data.getAnalysisId()),
                                            ignoreWarning);
                        } catch (Exception err) {
                            DataBaseUtil.mergeException(e, err);
                        }
                }
            }
        }

        if (e.size() > 0)
            throw e;
    }

    /**
     * Validate that the user has permission to add/update this analysis DO
     */
    protected void validatePermission(Integer accession, AnalysisViewDO data,
                                      SystemUserPermission perm) throws Exception {
        SectionPermission sp;

        sp = perm.getSection(data.getSectionName());
        if (Constants.dictionary().ANALYSIS_CANCELLED.equals(data.getStatusId())) {
            if (sp == null || !sp.hasCancelPermission())
                throw new FormErrorException("analysis.noCancelPermission",
                                             accession,
                                             data.getTestName(),
                                             data.getMethodName());
        } else if (Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId())) {
            if (sp == null || !sp.hasReleasePermission())
                throw new FormErrorException("analysis.noReleasePermission",
                                             accession,
                                             data.getTestName(),
                                             data.getMethodName());
        } else if (Constants.dictionary().ANALYSIS_COMPLETED.equals(data.getStatusId())) {
            if (sp == null || !sp.hasCompletePermission())
                throw new FormErrorException("analysis.noCompletePermission",
                                             accession,
                                             data.getTestName(),
                                             data.getMethodName());
        } else {
            if (sp == null || ( !sp.hasCompletePermission() && !sp.hasAssignPermission()))
                throw new FormErrorException("analysis.noAssignPermission",
                                             accession,
                                             data.getTestName(),
                                             data.getMethodName());
        }
    }

    /**
     * Validates the accession number. Throws exception if accession is not
     * valid
     */
    @RolesAllowed({"sample-add", "sample-update"})
    public void validateAccessionNumber(SampleDO data) throws Exception {
        Integer acc;
        SampleDO dup;
        SystemVariableDO sys;

        /*
         * accession number must be > 0, previously issued, and not duplicate
         */
        acc = data.getAccessionNumber();
        if (acc == null || acc <= 0)
            throw new FormErrorException("sample.accessionNumberNotValidException",
                                         data.getAccessionNumber());

        try {
            sys = systemVariable.fetchByName("last_accession_number");
            if (acc.compareTo(Integer.valueOf(sys.getValue())) > 0)
                throw new FormErrorException("sample.accessionNumberNotInUse",
                                             data.getAccessionNumber());
        } catch (Exception any) {
            log.log(Level.SEVERE, "Missing/invalid system variable 'last_accession_number'", any);
            throw any;
        }

        try {
            dup = sample.fetchByAccessionNumber(acc);
            if ( !dup.getId().equals(data.getId()))
                throw new FormErrorException("sample.accessionNumberDuplicate",
                                             data.getAccessionNumber());
        } catch (NotFoundException nf) {
            // ok if no other sample with the same accession number
        }
    }
}