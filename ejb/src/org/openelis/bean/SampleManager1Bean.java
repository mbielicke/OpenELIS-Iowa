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
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.StorageViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

@Stateless
@SecurityDomain("openelis")
public class SampleManager1Bean {

    @EJB
    private LockBean                     lock;

    @EJB
    private SampleBean                   sample;

    @EJB
    private SampleEnvironmentalBean      sampleEnvironmental;

    @EJB
    private SampleSDWISBean              sampleSDWIS;

    @EJB
    private SamplePrivateWellBean        samplePrivate;

    @EJB
    private SampleNeonatalBean           sampleNeonatal;

    @EJB
    private SampleOrganizationBean       sampleOrganization;

    @EJB
    private SampleProjectBean            sampleProject;

    @EJB
    private SampleQAEventBean            sampleQA;

    @EJB
    private AuxDataBean                  auxdata;

    @EJB
    private NoteBean                     note;

    @EJB
    private SampleItemBean               item;

    @EJB
    private StorageBean                  storage;

    @EJB
    private AnalysisBean                 analysis;

    @EJB
    private AnalysisQAEventBean          analysisQA;

    @EJB
    private AnalysisUserBean             user;

    @EJB
    private ResultBean                   result;

    @EJB
    private TestManagerBean              testManager;

    @EJB
    private SystemVariableBean           systemVariable;

    @EJB
    private UserCacheBean                userCache;

    @EJB
    private SampleManagerOrderHelperBean sampleManagerOrderHelper;

    @EJB
    private PanelBean                    panel;

    @EJB
    private AnalysisHelperBean           analysisHelper;

    @EJB
    private PatientBean                  patient;

    @EJB
    private AuxDataHelperBean            auxDataHelper;

    @EJB
    private AuxFieldGroupManagerBean     auxFieldGroupManager;

    @EJB
    private AuxDataBean                  auxData;

    private static final Logger          log = Logger.getLogger("openelis");

    /**
     * Returns a new instance of sample manager with pre-initailized sample and
     * other structures.
     */
    public SampleManager1 getInstance(String domain) throws Exception {
        SampleManager1 sm;
        SampleDO s;
        Datetime now;

        // sample
        now = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
        sm = new SampleManager1();

        s = new SampleDO();
        s.setNextItemSequence(0);
        s.setRevision(0);
        s.setEnteredDate(now);
        s.setReceivedById(userCache.getId());
        s.setStatusId(Constants.dictionary().SAMPLE_NOT_VERIFIED);

        setSample(sm, s);

        // set the domain
        if (Constants.domain().QUICKENTRY.equals(domain)) {
            s.setDomain(domain);
        } else if (Constants.domain().ENVIRONMENTAL.equals(domain)) {
            SampleEnvironmentalDO se;

            se = new SampleEnvironmentalDO();
            se.setIsHazardous("N");
            setSampleEnvironmental(sm, se);

            s.setDomain(domain);
        } else if (Constants.domain().PRIVATEWELL.equals(domain)) {
            SamplePrivateWellViewDO spw;

            spw = new SamplePrivateWellViewDO();
            setSamplePrivateWell(sm, spw);

            s.setDomain(domain);
        } else if (Constants.domain().SDWIS.equals(domain)) {
            SampleSDWISViewDO ssd;

            ssd = new SampleSDWISViewDO();
            setSampleSDWIS(sm, ssd);

            s.setDomain(domain);
        } else if (Constants.domain().NEONATAL.equals(domain)) {
            SampleNeonatalViewDO snn;

            snn = new SampleNeonatalViewDO();
            snn.setIsRepeat("N");
            snn.setIsNicu("N");
            snn.setIsTransfused("N");
            snn.setIsCollectionValid("N");
            setSampleNeonatal(sm, snn);

            s.setDomain(domain);
        } else {
            throw new InconsistencyException(Messages.get()
                                                     .sample_domainNotValid(s.getAccessionNumber()));
        }

        return sm;
    }

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
        if (elements != null && elements.length > 0)
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

        for (SampleNeonatalViewDO data : sampleNeonatal.fetchBySampleIds(ids1)) {
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
                if ("Y".equals(data.getIsExternal()))
                    setSampleExternalNote(sm, data);
                else
                    addSampleInternalNote(sm, data);
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
            ids2.add(data.getId());
            map2.put(data.getId(), sm);
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
            ids1.add(data.getId());
            map1.put(data.getId(), sm);
        }
        ids2 = null;
        map2 = null;

        if (el.contains(SampleManager1.Load.NOTE)) {
            for (NoteViewDO data : note.fetchByIds(ids1, Constants.table().ANALYSIS)) {
                sm = map1.get(data.getReferenceId());
                if ("Y".equals(data.getIsExternal()))
                    addAnalysisExternalNote(sm, data);
                else
                    addAnalysisInternalNote(sm, data);
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

        for (SampleNeonatalViewDO data : sampleNeonatal.fetchBySampleIds(ids1)) {
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
                if ("Y".equals(data.getIsExternal()))
                    setSampleExternalNote(sm, data);
                else
                    addSampleInternalNote(sm, data);
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
            ids1.add(data.getId());
            map1.put(data.getId(), sm);
        }
        ids2 = null;
        map2 = null;

        if (el.contains(SampleManager1.Load.NOTE)) {
            for (NoteViewDO data : note.fetchByIds(ids1, Constants.table().ANALYSIS)) {
                sm = map1.get(data.getReferenceId());
                addAnalysisInternalNote(sm, data);
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
        field.setKey(SampleMeta.getAccessionNumber());
        field.setQuery(accessionNumber.toString());
        field.setType(QueryData.Type.INTEGER);
        query.setFields(field);

        sms = fetchByQuery(query.getFields(), 0, -1, elements);
        return sms.size() == 0 ? null : sms.get(0);
    }

    /**
     * Returns a sample manager based on the specified query and requested load
     * elements
     */
    public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                  SampleManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdAccessionVO vo : sample.query(fields, first, max))
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
            setSampleInternalNotes(sm, null);
            for (NoteViewDO data : note.fetchByIds(ids, Constants.table().SAMPLE))
                if ("Y".equals(data.getIsExternal()))
                    setSampleExternalNote(sm, data);
                else
                    addSampleInternalNote(sm, data);
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
            setAnalysisInternalNotes(sm, null);
            for (NoteViewDO data : note.fetchByIds(ids, Constants.table().ANALYSIS))
                addAnalysisInternalNote(sm, data);
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
        HashSet<Integer> ids;

        ids = new HashSet<Integer>();
        for (SampleItemViewDO data : item.fetchByAnalysisIds(analysisIds))
            ids.add(data.getSampleId());

        lock.lock(Constants.table().SAMPLE, new ArrayList<Integer>(ids));
        return fetchByAnalyses(analysisIds, elements);
    }

    /**
     * Unlocks and returns a sample manager with specified sample id and
     * requested load elements
     */
    @RolesAllowed({"sample-add", "sample-update"})
    public SampleManager1 unlock(Integer sampleId, SampleManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<SampleManager1> sms;

        ids = new ArrayList<Integer>(1);
        ids.add(sampleId);
        sms = unlock(ids, elements);
        return sms.size() == 0 ? null : sms.get(0);
    }

    /**
     * Unlocks and returns list of sample managers with specified sample ids and
     * requested load elements
     */
    @RolesAllowed({"sample-add", "sample-update"})
    public ArrayList<SampleManager1> unlock(ArrayList<Integer> sampleIds,
                                            SampleManager1.Load... elements) throws Exception {
        lock.unlock(Constants.table().SAMPLE, sampleIds);
        return fetchByIds(sampleIds, elements);
    }

    /**
     * Adds/updates the sample and related records in the database. The records
     * are validated before add/update and the sample record must have a lock
     * record if it has an id.
     */
    @RolesAllowed({"sample-add", "sample-update"})
    public SampleManager1 update(SampleManager1 sm, boolean ignoreWarnings) throws Exception {
        ArrayList<SampleManager1> sms;

        sms = new ArrayList<SampleManager1>(1);
        sms.add(sm);
        update(sms, ignoreWarnings);
        return sms.get(0);
    }

    /**
     * Adds/updates all the samples and related records in the database. All the
     * records are validated before add/update and the sample records must each
     * have a lock record if they have an id.
     */
    @RolesAllowed({"sample-add", "sample-update"})
    public ArrayList<SampleManager1> update(ArrayList<SampleManager1> sms, boolean ignoreWarnings) throws Exception {
        int dep, ldep;
        boolean nodep;
        Integer tmpid, id, so;
        HashSet<Integer> ids, ids1;
        ArrayList<Integer> locks;
        HashMap<Integer, TestManager> tms;
        HashMap<Integer, AuxFieldGroupManager> ams;
        NoteViewDO ext;
        PatientDO pat;
        HashMap<Integer, Integer> imap, amap, rmap, seq;

        /*
         * validation needs test and aux group manager. Build lists of analysis
         * test ids and aux group ids to fetch test and aux group managers.
         */
        ids = new HashSet<Integer>();
        ids1 = new HashSet<Integer>();
        for (SampleManager1 sm : sms) {
            for (AnalysisViewDO an : getAnalyses(sm))
                ids.add(an.getTestId());
            if (getAuxilliary(sm) != null) {
                for (AuxDataViewDO aux : getAuxilliary(sm))
                    ids1.add(aux.getGroupId());
            }
        }

        /*
         * build test map and aux map for easy access to a manager during
         * validation
         */
        tms = new HashMap<Integer, TestManager>();
        for (TestManager tm : testManager.fetchByIds(new ArrayList<Integer>(ids)))
            tms.put(tm.getTest().getId(), tm);

        ams = null;
        if (ids1.size() > 0) {
            ams = new HashMap<Integer, AuxFieldGroupManager>();
            for (AuxFieldGroupManager am : auxFieldGroupManager.fetchByIds(new ArrayList<Integer>(ids1)))
                ams.put(am.getGroup().getId(), am);
        }

        validate(sms, tms, ams, ignoreWarnings);

        ids1 = null;
        tms = null;
        ams = null;

        /*
         * check all the locks
         */
        ids.clear();
        for (SampleManager1 sm : sms) {
            if (getSample(sm).getId() != null)
                ids.add(getSample(sm).getId());
        }
        if (ids.size() > 0) {
            locks = new ArrayList<Integer>(ids);
            lock.validateLock(Constants.table().SAMPLE, locks);
        } else {
            locks = null;
        }
        ids = null;

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

            // add/update sample
            if (getSample(sm).getId() == null)
                sample.add(getSample(sm));
            else
                sample.update(getSample(sm));

            // add/update sample domain
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
                /*
                 * add/update patient and next of kin
                 */
                if (getSampleNeonatal(sm).getPatient().getId() == null)
                    pat = patient.add(getSampleNeonatal(sm).getPatient());
                else
                    pat = patient.update(getSampleNeonatal(sm).getPatient());
                getSampleNeonatal(sm).getPatient().setId(pat.getId());

                if (getSampleNeonatal(sm).getNextOfKin().getId() == null)
                    pat = patient.add(getSampleNeonatal(sm).getNextOfKin());
                else
                    pat = patient.update(getSampleNeonatal(sm).getNextOfKin());
                getSampleNeonatal(sm).getNextOfKin().setId(pat.getId());

                if (getSampleNeonatal(sm).getId() == null) {
                    getSampleNeonatal(sm).setSampleId(getSample(sm).getId());
                    sampleNeonatal.add(getSampleNeonatal(sm));
                } else {
                    sampleNeonatal.update(getSampleNeonatal(sm));
                }
            }

            if (getOrganizations(sm) != null) {
                for (SampleOrganizationViewDO data : getOrganizations(sm)) {
                    if (data.getId() < 0) {
                        data.setSampleId(getSample(sm).getId());
                        sampleOrganization.add(data);
                    } else {
                        sampleOrganization.update(data);
                    }
                }
            }

            if (getProjects(sm) != null) {
                for (SampleProjectViewDO data : getProjects(sm)) {
                    if (data.getId() < 0) {
                        data.setSampleId(getSample(sm).getId());
                        sampleProject.add(data);
                    } else {
                        sampleProject.update(data);
                    }
                }
            }

            if (getSampleQAs(sm) != null) {
                for (SampleQaEventViewDO data : getSampleQAs(sm)) {
                    if (data.getId() < 0) {
                        data.setSampleId(getSample(sm).getId());
                        sampleQA.add(data);
                    } else {
                        sampleQA.update(data);
                    }
                }
            }

            so = 0;
            if (getAuxilliary(sm) != null) {
                for (AuxDataViewDO data : getAuxilliary(sm)) {
                    so++ ;
                    if ( !DataBaseUtil.isSame(so, data.getSortOrder()))
                        data.setSortOrder(so);
                    if (data.getId() < 0) {
                        data.setReferenceTableId(Constants.table().SAMPLE);
                        data.setReferenceId(getSample(sm).getId());
                        auxdata.add(data);
                    } else {
                        auxdata.update(data);
                    }
                }
            }

            if (getSampleExternalNote(sm) != null) {
                ext = getSampleExternalNote(sm);
                if (ext.getId() < 0) {
                    ext.setReferenceTableId(Constants.table().SAMPLE);
                    ext.setReferenceId(getSample(sm).getId());
                    note.add(ext);
                } else {
                    note.update(ext);
                }
            }

            if (getSampleInternalNotes(sm) != null) {
                for (NoteViewDO data : getSampleInternalNotes(sm)) {
                    if (data.getId() < 0) {
                        data.setReferenceTableId(Constants.table().SAMPLE);
                        data.setReferenceId(getSample(sm).getId());
                        note.add(data);
                    } else {
                        note.update(data);
                    }
                }
            }

            // add/update sample items. keep track of all the keys (pos or neg)
            for (SampleItemViewDO data : getItems(sm)) {
                if (data.getId() < 0) {
                    tmpid = data.getId();
                    data.setSampleId(getSample(sm).getId());
                    item.add(data);
                } else {
                    tmpid = data.getId();
                    item.update(data);
                }
                imap.put(tmpid, data.getId());
            }

            /*
             * some analysis can be dependent on other analysis for prep or for
             * reflex. Additionally an analysis maybe dependent on a result that
             * triggered the reflex. This code tries to resolve those
             * dependencies by alternating between adding/updating analysis and
             * result until all the records have been added/updated. The code
             * also detects infinite loops by ensuring every iteration resolves
             * some dependency
             */
            dep = ldep = 0;
            do {
                ldep = dep;
                dep = 0;
                // add/update analysis
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

                // add/update results
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
                throw new InconsistencyException(Messages.get().analysis_circularReference());

            // add/update analysis notes
            if (getAnalysisInternalNotes(sm) != null) {
                for (NoteViewDO data : getAnalysisInternalNotes(sm)) {
                    if (data.getId() < 0) {
                        data.setReferenceTableId(Constants.table().ANALYSIS);
                        data.setReferenceId(amap.get(data.getReferenceId()));
                        note.add(data);
                    } else if (data.isChanged()) {
                        note.update(data);
                    }
                }
            }

            if (getAnalysisExternalNotes(sm) != null) {
                for (NoteViewDO data : getAnalysisExternalNotes(sm)) {
                    if (data.getId() < 0) {
                        data.setReferenceTableId(Constants.table().ANALYSIS);
                        data.setReferenceId(amap.get(data.getReferenceId()));
                        note.add(data);
                    } else if (data.isChanged()) {
                        note.update(data);
                    }
                }
            }

            // add/update analysis qa events
            if (getAnalysisQAs(sm) != null) {
                for (AnalysisQaEventViewDO data : getAnalysisQAs(sm)) {
                    if (data.getId() < 0) {
                        data.setAnalysisId(amap.get(data.getAnalysisId()));
                        analysisQA.add(data);
                    } else if (data.isChanged()) {
                        analysisQA.update(data);
                    }
                }
            }

            // add/update analysis users
            if (getUsers(sm) != null) {
                for (AnalysisUserViewDO data : getUsers(sm)) {
                    if (data.getId() < 0) {
                        data.setAnalysisId(amap.get(data.getAnalysisId()));
                        user.add(data);
                    } else if (data.isChanged()) {
                        user.update(data);
                    }
                }
            }

            // add/update storage
            if (getStorages(sm) != null) {
                for (StorageViewDO data : getStorages(sm)) {
                    if (data.getId() < 0) {
                        if (Constants.table().SAMPLE_ITEM.equals(data.getReferenceTableId()))
                            data.setReferenceId(imap.get(data.getReferenceId()));
                        else if (Constants.table().ANALYSIS.equals(data.getReferenceTableId()))
                            data.setReferenceId(amap.get(data.getReferenceId()));
                        storage.add(data);
                    } else if (data.isChanged()) {
                        storage.update(data);
                    }
                }
            }
        }

        if (locks != null)
            lock.unlock(Constants.table().SAMPLE, locks);

        return sms;
    }

    /**
     * Sets the specified accession number in the manager's sample. If there is
     * an existing Quick Entry sample associated with this accession number, the
     * returned manager contains its data (quick-entered sample is locked).
     */
    public SampleManager1 setAccessionNumber(SampleManager1 sm, Integer accession) throws Exception {
        String domain;
        SampleDO data, qdata;
        SampleManager1 qsm;

        data = getSample(sm);
        try {
            qdata = sample.fetchByAccessionNumber(accession);

            if (Constants.domain().QUICKENTRY.equals(qdata.getDomain()) && data.getId() == null) {
                /*
                 * there's a special condition that is not allowed; 1. the user
                 * loads a sample from an order 2. the accession number is
                 * changed to quick-entered sample
                 */
                if (data.getOrderId() != null)
                    throw new FormErrorException(Messages.get().cantLoadQEIfOrderNumPresent());

                qsm = fetchForUpdate(qdata.getId());
                domain = data.getDomain();
                duplicateManager(sm, qsm);
                getSample(sm).setDomain(domain);
            } else if (qdata.getId().equals(data.getId())) {
                data.setAccessionNumber(accession);
            } else {
                /*
                 * the accession number belongs to some other sample
                 */
                throw new FormErrorException(Messages.get()
                                                     .sample_accessionNumberDuplicate(accession));
            }
        } catch (NotFoundException ex) {
            validateAccessionNumber(accession);
            data.setAccessionNumber(accession);
        }

        return sm;
    }

    /**
     * Loads the data from send-out order or electronic order, depending on
     * domain, into the SampleManager. This method returns both the loaded
     * SampleManager and the list of warnings due to invalid data in the order.
     */
    public SampleTestReturnVO setOrderId(SampleManager1 sm, Integer orderId) throws Exception {
        SampleDO data;
        ValidationErrorsList e;

        data = getSample(sm);
        if (data.getAccessionNumber() == null)
            throw new FormErrorException(Messages.get().enterAccNumBeforeOrderLoad());

        e = new ValidationErrorsList();
        // TODO uncomment the code
        /*
         * if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain()) ||
         * Constants.domain().PRIVATEWELL.equals(data.getDomain()) ||
         * Constants.domain().SDWIS.equals(data.getDomain()))
         */
        return sampleManagerOrderHelper.importSendoutOrder(sm, orderId, e);
    }

    /**
     * Adds an analysis to the sample based on the data provided in the VO. This
     * method returns both the loaded SampleManager and the list of
     * errors/warnings encountered while adding the analyses.
     */
    public SampleTestReturnVO addTest(SampleManager1 sm, SampleTestRequestVO test) throws Exception {
        ArrayList<SampleTestRequestVO> tests;

        tests = new ArrayList<SampleTestRequestVO>();
        tests.add(test);

        return addTests(sm, tests);
    }

    /**
     * Adds analyses to the sample based on the data provided in the list. This
     * method returns both the loaded SampleManager and the list of
     * errors/warnings encountered while adding the analyses.
     */
    public SampleTestReturnVO addTests(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests) throws Exception {
        SampleTestReturnVO ret;
        HashMap<Integer, AnalysisViewDO> anaByTest, anaById;
        ValidationErrorsList e;
        AnalysisViewDO ana, ref, prep;
        TestManager tm;
        HashMap<Integer, TestManager> tms;
        ArrayList<IdVO> pts, pgs;
        ArrayList<Integer> testIds, groupIds;
        ArrayList<SampleTestRequestVO> panelTests;

        /*
         * the list of tests in the sample
         */
        anaByTest = new HashMap<Integer, AnalysisViewDO>();
        anaById = new HashMap<Integer, AnalysisViewDO>();
        if (getAnalyses(sm) != null) {
            for (AnalysisViewDO a : getAnalyses(sm)) {
                if (anaByTest.get(a.getTestId()) == null &&
                    !Constants.dictionary().ANALYSIS_CANCELLED.equals(a.getStatusId())) {
                    anaByTest.put(a.getTestId(), a);
                    anaById.put(a.getId(), a);
                }
            }
        }

        /*
         * the list of all the tests that are to be added
         */
        testIds = new ArrayList<Integer>();
        groupIds = null;
        e = new ValidationErrorsList();
        panelTests = null;
        for (SampleTestRequestVO test : tests) {
            if (test.getTestId() != null) {
                testIds.add(test.getTestId());
            } else if (test.getPanelId() != null) {
                pts = panel.fetchTestIdsFromPanel(test.getPanelId());
                /*
                 * this new list is created here because adding to the original
                 * list makes a ConcurrentModificationException thrown
                 */
                panelTests = new ArrayList<SampleTestRequestVO>();
                for (IdVO pt : pts) {
                    testIds.add(pt.getId());
                    /*
                     * the tests defined in the panel are added as analytical
                     * tests
                     */
                    panelTests.add(new SampleTestRequestVO(test.getSampleItemId(),
                                                           pt.getId(),
                                                           null,
                                                           null,
                                                           null,
                                                           test.getPanelId(),
                                                           false,
                                                           null));
                }

                /*
                 * fetch the ids of the aux groups specified in the panel
                 */
                pgs = panel.fetchAuxIdsFromPanel(test.getPanelId());
                groupIds = new ArrayList<Integer>();
                for (IdVO pg : pgs)
                    groupIds.add(pg.getId());
            }
        }

        if (panelTests != null)
            tests.addAll(panelTests);

        tms = analysisHelper.getTestManagers(testIds, e);

        ret = new SampleTestReturnVO();
        ret.setManager(sm);
        ret.setErrors(e);
        for (SampleTestRequestVO test : tests) {
            tm = tms.get(test.getTestId());
            if (tm == null)
                continue;
            if (test.getAnalysisId() == null) {
                /*
                 * add an analysis
                 */
                ana = addAnalysisAndPrep(ret, anaByTest, tm, test, null);
                ana.setPanelId(test.getPanelId());
                anaById.put(ana.getId(), ana);
            } else if (test.getResultId() != null) {
                /*
                 * add a reflex analysis for this analysis
                 */
                ref = anaByTest.get(test.getTestId());
                if (ref == null || test.getAllowDuplicate()) {
                    ref = addAnalysisAndPrep(ret, anaByTest, tm, test, null);
                    ref.setParentAnalysisId(test.getAnalysisId());
                    ref.setParentResultId(test.getResultId());
                    anaById.put(ref.getId(), ref);
                }
            } else {
                /*
                 * add a prep analysis for this analysis
                 */
                prep = anaByTest.get(test.getTestId());
                if (prep == null || test.getAllowDuplicate()) {
                    prep = addAnalysisAndPrep(ret, anaByTest, tm, test, null);
                    prep.setPanelId(test.getPanelId());
                    anaById.put(prep.getId(), prep);
                }
                ana = anaById.get(test.getAnalysisId());
                ana.setPreAnalysisId(prep.getId());
                ana.setPreAnalysisTest(prep.getTestName());
                ana.setPreAnalysisMethod(prep.getMethodName());
                if ( !Constants.dictionary().ANALYSIS_COMPLETED.equals(prep.getStatusId()) &&
                    !Constants.dictionary().ANALYSIS_RELEASED.equals(prep.getStatusId())) {
                    ana.setStatusId(Constants.dictionary().ANALYSIS_INPREP);
                    ana.setAvailableDate(null);
                }
            }
        }

        /*
         * if a panel was added above and it had any aux groups linked to it
         * then add them to the sample
         */
        if (groupIds != null && groupIds.size() > 0)
            addAuxGroups(sm, groupIds);

        return ret;
    }

    /**
     * Adds result rows to the analysis, with the specified row analytes, at the
     * positions specified by the corresponding indexes. Assumes that the two
     * lists are of the same length and the indexes are sorted in ascending
     * order.
     */
    public SampleManager1 addRowAnalytes(SampleManager1 sm, AnalysisViewDO analysis,
                                         ArrayList<TestAnalyteViewDO> analytes,
                                         ArrayList<Integer> indexes) throws Exception {
        return analysisHelper.addRowAnalytes(sm, analysis, analytes, indexes);
    }

    /**
     * Adds aux groups with the passed ids to the sample
     */
    public SampleManager1 addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        ArrayList<AuxDataViewDO> auxiliary;

        auxiliary = getAuxilliary(sm);
        if (auxiliary == null) {
            auxiliary = new ArrayList<AuxDataViewDO>();
            setAuxilliary(sm, auxiliary);
        }

        auxDataHelper.addAuxGroups(auxiliary, new HashSet<Integer>(groupIds));

        /*
         * set negative ids in the newly added aux data
         */
        for (AuxDataViewDO aux : auxiliary) {
            if (aux.getId() == null)
                aux.setId(sm.getNextUID());
        }

        return sm;
    }

    /**
     * Removes aux data from the sample based on the list of group ids
     */
    public SampleManager1 removeAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        ArrayList<AuxDataViewDO> removed;

        removed = auxDataHelper.removeAuxGroups(getAuxilliary(sm), new HashSet<Integer>(groupIds));

        if (removed != null && removed.size() > 0) {
            if (getRemoved(sm) == null)
                setRemoved(sm, new ArrayList<DataObject>());
            for (AuxDataViewDO data : removed) {
                if (data.getId() > 0)
                    getRemoved(sm).add(data);
            }
        }
        return sm;
    }

    /**
     * This method changes the specified analysis's method to the specified
     * method. The old results are removed and their values are merged with the
     * results added from the new method. Returns a list of prep tests that
     * could be added to satisfy the prep requirement for the new test.
     */
    public SampleTestReturnVO changeAnalysisMethod(SampleManager1 sm, Integer analysisId,
                                                   Integer methodId) throws Exception {
        return analysisHelper.changeAnalysisMethod(sm, analysisId, methodId);
    }

    /**
     * This method changes the specified analysis's unit to the specified unit.
     * The defaults defined for this unit in this analysis' test are loaded in
     * those results of the analysis that don't have a value. The type is set to
     * null in all results of the analysis to force validation.
     */
    public SampleManager1 changeAnalysisUnit(SampleManager1 sm, Integer analysisId, Integer unitId) throws Exception {
        return analysisHelper.changeAnalysisUnit(sm, analysisId, unitId);
    }
    

    public SampleManager1 changeAnalysisStatus(SampleManager1 sm, Integer analysisId, Integer unitId) throws Exception {
        return analysisHelper.changeAnalysisStatus(sm, analysisId, unitId);
    }

    /**
     * Validates the accession number. Throws exception if accession is not
     * valid
     */
    @RolesAllowed({"sample-add", "sample-update"})
    public void validateAccessionNumber(Integer accession) throws Exception {
        SystemVariableDO sys;

        /*
         * accession number must be > 0, previously issued, and not duplicate
         */
        if (accession == null || accession <= 0)
            throw new FormErrorException(Messages.get()
                                                 .sample_accessionNumberNotValidException(DataBaseUtil.asString(accession)));

        try {
            sys = systemVariable.fetchByName("last_accession_number");
            if (accession.compareTo(Integer.valueOf(sys.getValue())) > 0)
                throw new FormErrorException(Messages.get()
                                                     .sample_accessionNumberNotInUse(accession));
        } catch (Exception any) {
            log.log(Level.SEVERE, "Missing/invalid system variable 'last_accession_number'", any);
            throw any;
        }
    }

    /**
     * Validates the sample manager for add or update. The routine throws a list
     * of exceptions/warnings listing all the problems for each sample.
     */
    protected void validate(ArrayList<SampleManager1> sms, HashMap<Integer, TestManager> tms,
                            HashMap<Integer, AuxFieldGroupManager> ams, boolean ignoreWarning) throws Exception {
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
            throw new FormErrorException(Messages.get()
                                                 .systemVariable_missingInvalidSystemVariable("last_accession_number"));
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
                e.add(new FormErrorException(Messages.get()
                                                     .sample_moreThanOneReportToException(accession)));

            /*
             * at least one sample item and items must have sample type
             */
            imap.clear();
            if (getItems(sm) == null || getItems(sm).size() < 1) {
                e.add(new FormErrorException(Messages.get()
                                                     .sample_minOneSampleItemException(accession)));
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
                                            tms.get(ana.getTestId()).getFormatter(),
                                            accession,
                                            amap.get(data.getAnalysisId()),
                                            ignoreWarning);
                        } catch (Exception err) {
                            DataBaseUtil.mergeException(e, err);
                        }
                }
            }

            /*
             * aux data must be valid for the aux field
             */

            if (getAuxilliary(sm) != null) {
                for (AuxDataViewDO data : getAuxilliary(sm)) {
                    if (data.isChanged())
                        try {
                            auxData.validate(data,
                                             ams.get(data.getGroupId()).getFormatter(),
                                             accession,
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
                throw new FormErrorException(Messages.get()
                                                     .analysis_noCancelPermission(DataBaseUtil.toString(accession),
                                                                                  data.getTestName(),
                                                                                  data.getMethodName()));
        } else if (Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId())) {
            if (sp == null || !sp.hasReleasePermission())
                throw new FormErrorException(Messages.get()
                                                     .analysis_noReleasePermission(DataBaseUtil.toString(accession),
                                                                                   data.getTestName(),
                                                                                   data.getMethodName()));
        } else if (Constants.dictionary().ANALYSIS_COMPLETED.equals(data.getStatusId())) {
            if (sp == null || !sp.hasCompletePermission())
                throw new FormErrorException(Messages.get()
                                                     .analysis_noCompletePermission(DataBaseUtil.toString(accession),
                                                                                    data.getTestName(),
                                                                                    data.getMethodName()));
        } else {
            if (sp == null || ( !sp.hasCompletePermission() && !sp.hasAssignPermission()))
                throw new FormErrorException(Messages.get()
                                                     .analysis_noAssignPermission(DataBaseUtil.toString(accession),
                                                                                  data.getTestName(),
                                                                                  data.getMethodName()));
        }
    }

    /**
     * copies all parts of fromsm to tosm e.g sample except the data for the
     * domain
     */
    protected void duplicateManager(SampleManager1 tosm, SampleManager1 fromsm) {
        setSample(tosm, getSample(fromsm));
        setOrganizations(tosm, getOrganizations(fromsm));
        setProjects(tosm, getProjects(fromsm));
        setSampleQAs(tosm, getSampleQAs(fromsm));
        setAuxilliary(tosm, getAuxilliary(fromsm));
        setSampleExternalNote(tosm, getSampleExternalNote(fromsm));
        setSampleInternalNotes(tosm, getSampleInternalNotes(fromsm));
        setItems(tosm, getItems(fromsm));
        setStorages(tosm, getStorages(fromsm));
        setAnalyses(tosm, getAnalyses(fromsm));
        setAnalysisQAs(tosm, getAnalysisQAs(fromsm));
        setAnalysisExternalNotes(tosm, getAnalysisExternalNotes(fromsm));
        setAnalysisInternalNotes(tosm, getAnalysisInternalNotes(fromsm));
        setUsers(tosm, getUsers(fromsm));
        setResults(tosm, getResults(fromsm));
    }

    /**
     * creates an analysis and results using the requested test. It also finds
     * and sets the prep or returns list of prep tests.
     */
    protected AnalysisViewDO addAnalysisAndPrep(SampleTestReturnVO ret,
                                                HashMap<Integer, AnalysisViewDO> analyses,
                                                TestManager tm, SampleTestRequestVO test,
                                                ArrayList<Integer> analyteIds) throws Exception {
        AnalysisViewDO ana;
        ArrayList<Integer> prepIds;

        ana = analysisHelper.addAnalysis(ret.getManager(),
                                         test.getSampleItemId(),
                                         tm,
                                         test.getSectionId(),
                                         ret.getErrors());
        prepIds = analysisHelper.setPrepForAnalysis(ana, analyses, tm);
        if (prepIds != null)
            for (Integer id : prepIds)
                ret.addTest(test.getSampleItemId(), id, ana.getId(), null, null, null, false, null);
        analysisHelper.addResults(ret.getManager(), tm, ana, test.getReportableAnalytes(), null);

        analyses.put(ana.getTestId(), ana);

        return ana;
    }
}