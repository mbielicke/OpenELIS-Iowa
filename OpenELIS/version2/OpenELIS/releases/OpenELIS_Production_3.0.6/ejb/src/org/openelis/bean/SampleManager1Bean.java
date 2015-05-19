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
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalysisWorksheetVO;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleClinicalViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePTDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.StorageViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.PostProcessing;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
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
    private AnalysisReportFlagsBean      analysisReportFlags;

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
    private AuxDataHelperBean            auxDataHelper;

    @EJB
    private AuxFieldGroupManagerBean     auxFieldGroupManager;

    @EJB
    private AuxDataBean                  auxData;

    @EJB
    private QaEventBean                  qaEvent;

    @EJB
    private WorksheetBean                worksheet;

    @EJB
    private FinalReportBean              finalReport;

    @EJB
    private SampleClinicalBean           sampleClinical;

    @EJB
    private AttachmentItemBean           attachmentItem;

    @EJB
    private DictionaryCacheBean          dictionaryCache;

    @EJB
    private TestBean                     test;

    @EJB
    private ScriptletHelperBean          scriptletHelper;

    @EJB
    private SamplePTBean                 samplePT;

    private static final Logger          log = Logger.getLogger("openelis");

    /**
     * Returns a new instance of sample manager with pre-initailized sample and
     * other structures.
     */
    public SampleManager1 getInstance(String domain) throws Exception {
        SampleManager1 sm;
        SampleDO s;

        // sample
        sm = new SampleManager1();

        s = new SampleDO();
        /*
         * set default values in fields like revision, entered date etc.
         */
        setDefaults(s);

        setSample(sm, s);

        setDomain(sm, domain);

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

        for (SampleClinicalViewDO data : sampleClinical.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            setSampleClinical(sm, data);
        }

        for (SamplePTDO data : samplePT.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            setSamplePT(sm, data);
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
                addAuxiliary(sm, data);
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

        if (el.contains(SampleManager1.Load.ATTACHMENT)) {
            for (AttachmentItemViewDO data : attachmentItem.fetchByIds(ids1,
                                                                       Constants.table().SAMPLE)) {
                sm = map1.get(data.getReferenceId());
                addAttachment(sm, data);
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
            SampleManager1Accessor.addAnalysis(sm, data);
            ids1.add(data.getId());
            map1.put(data.getId(), sm);
        }

        ids2 = null;
        map2 = null;

        /*
         * it is possible for a sample to have no analyses before it's verified
         */
        if (ids1.size() > 0) {
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

            if (el.contains(SampleManager1.Load.WORKSHEET)) {
                for (AnalysisWorksheetVO data : worksheet.fetchByAnalysisIds(ids1)) {
                    sm = map1.get(data.getAnalysisId());
                    addWorksheet(sm, data);
                }
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

        for (SampleClinicalViewDO data : sampleClinical.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            setSampleClinical(sm, data);
        }

        for (SamplePTDO data : samplePT.fetchBySampleIds(ids1)) {
            sm = map1.get(data.getSampleId());
            setSamplePT(sm, data);
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
                addAuxiliary(sm, data);
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

        if (el.contains(SampleManager1.Load.ATTACHMENT)) {
            for (AttachmentItemViewDO data : attachmentItem.fetchByIds(ids1,
                                                                       Constants.table().SAMPLE)) {
                sm = map1.get(data.getReferenceId());
                addAttachment(sm, data);
            }
        }

        /*
         * build level 3, everything is based on analysis ids
         */
        ids1 = new ArrayList<Integer>();
        map1 = new HashMap<Integer, SampleManager1>();
        for (AnalysisViewDO data : analysis.fetchBySampleItemIds(ids2)) {
            sm = map2.get(data.getSampleItemId());
            SampleManager1Accessor.addAnalysis(sm, data);
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
        } else if (el.contains(SampleManager1.Load.SINGLERESULT)) {
            for (ResultViewDO data : result.fetchByAnalysisIds(analysisIds)) {
                sm = map1.get(data.getAnalysisId());
                addResult(sm, data);
            }
        }

        if (el.contains(SampleManager1.Load.WORKSHEET)) {
            for (AnalysisWorksheetVO data : worksheet.fetchByAnalysisIds(ids1)) {
                sm = map1.get(data.getAnalysisId());
                addWorksheet(sm, data);
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
     * Returns sample managers based on the analyses found by the specified
     * query and requested load elements
     */
    public ArrayList<SampleManager1> fetchByAnalysisQuery(ArrayList<QueryData> fields, int first,
                                                          int max, SampleManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdAccessionVO vo : analysis.query(fields, first, max))
            ids.add(vo.getId());
        return fetchByAnalyses(ids, elements);
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
            setAuxiliary(sm, null);
            for (AuxDataViewDO data : auxdata.fetchByIds(ids, Constants.table().SAMPLE))
                addAuxiliary(sm, data);
        }

        if (el.contains(SampleManager1.Load.NOTE)) {
            setSampleInternalNotes(sm, null);
            for (NoteViewDO data : note.fetchByIds(ids, Constants.table().SAMPLE))
                if ("Y".equals(data.getIsExternal()))
                    setSampleExternalNote(sm, data);
                else
                    addSampleInternalNote(sm, data);
        }
        
        if (el.contains(SampleManager1.Load.ATTACHMENT)) {
            setAttachments(sm, null);
            for (AttachmentItemViewDO data : attachmentItem.fetchByIds(ids,
                                                                       Constants.table().SAMPLE))
                addAttachment(sm, data);
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

        if (el.contains(SampleManager1.Load.WORKSHEET)) {
            setWorksheets(sm, null);
            for (AnalysisWorksheetVO data : worksheet.fetchByAnalysisIds(ids))
                addWorksheet(sm, data);
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
                              SampleManager1.Load.RESULT,
                              SampleManager1.Load.WORKSHEET,
                              SampleManager1.Load.ATTACHMENT);
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
        Integer tmpid, id, so, maxAccession;
        NoteViewDO ext;
        SystemVariableDO sys;
        SystemUserPermission permission;
        ValidationErrorsList e;
        HashSet<Integer> ids, ids1, ids2;
        ArrayList<Integer> locks;
        HashMap<Integer, TestManager> tms;
        HashMap<Integer, AuxFieldGroupManager> ams;
        HashMap<Integer, QaEventDO> qas;
        HashMap<Integer, Integer> imap, amap, rmap, seq;
        AnalysisReportFlagsDO defaultARF;
        Datetime now;
        HashMap<String, Object> cache;
        AttachmentItemViewDO atti;
        ArrayList<AttachmentItemViewDO> attachments;

        /*
         * validation needs test, aux group manager and pws DO. Build lists of
         * analysis test ids, aux group ids to fetch test and aux group
         * managers.
         */
        ids = new HashSet<Integer>();
        ids1 = new HashSet<Integer>();
        ids2 = new HashSet<Integer>();
        for (SampleManager1 sm : sms) {
            if (getSampleSDWIS(sm) != null)
                ids2.add(getSampleSDWIS(sm).getPwsId());
            if (getAnalyses(sm) != null) {
                for (AnalysisViewDO an : getAnalyses(sm))
                    ids.add(an.getTestId());
            }
            if (getAuxiliary(sm) != null) {
                for (AuxDataViewDO aux : getAuxiliary(sm))
                    ids1.add(aux.getAuxFieldGroupId());
            }
        }

        /*
         * build test map and aux map for easy access to a manager during
         * validation; also populate the cache used by scriptlets
         */
        cache = new HashMap<String, Object>();
        tms = new HashMap<Integer, TestManager>();
        if (ids.size() > 0) {
            for (TestManager tm : testManager.fetchByIds(new ArrayList<Integer>(ids))) {
                tms.put(tm.getTest().getId(), tm);
                cache.put(Constants.uid().getTest(tm.getTest().getId()), tm);
            }
        }

        ams = null;
        if (ids1.size() > 0) {
            ams = new HashMap<Integer, AuxFieldGroupManager>();
            for (AuxFieldGroupManager am : auxFieldGroupManager.fetchByIds(new ArrayList<Integer>(ids1))) {
                ams.put(am.getGroup().getId(), am);
                cache.put(Constants.uid().getAuxFieldGroup(am.getGroup().getId()), am);
            }
        }

        // user permission for adding/updating analysis
        permission = userCache.getPermission();

        /*
         * see what was the last accession number we have given out
         */
        try {
            sys = systemVariable.fetchByName("last_accession_number");
            maxAccession = Integer.valueOf(sys.getValue());
        } catch (Exception any) {
            log.log(Level.SEVERE, "Missing/invalid system variable 'last_accession_number'", any);
            throw new FormErrorException(Messages.get()
                                                 .systemVariable_missingInvalidSystemVariable("last_accession_number"));
        }

        /*
         * this map is used to validate analysis qa events
         */
        qas = new HashMap<Integer, QaEventDO>();
        for (QaEventDO data : qaEvent.fetchAll())
            qas.put(data.getId(), data);

        e = new ValidationErrorsList();
        /*
         * validate each sample and re-evaluate its status
         */
        for (SampleManager1 sm : sms) {
            try {
                /*
                 * run the scriptlets for this manager
                 */
                scriptletHelper.runScriptlets(sm, cache, null, null, Action_Before.UPDATE);

                validate(sm, permission, maxAccession, tms, ams, qas);
                /*
                 * the status will be the lowest of the statuses of the analyses
                 */
                changeSampleStatus(sm, null);
            } catch (ValidationErrorsList err) {
                if (err.hasErrors() || !ignoreWarnings)
                    DataBaseUtil.mergeException(e, err);
                else if (err.hasWarnings())
                    /*
                     * force Error status because the data is to be committed
                     * with warnings
                     */
                    changeSampleStatus(sm, Constants.dictionary().SAMPLE_ERROR);
                else
                    /*
                     * this makes sure that if the ValidationErrorsList only has
                     * cautions and the sample was previously in error status,
                     * it comes out of that status if no analyses are in error
                     * status
                     */
                    changeSampleStatus(sm, null);
            } catch (Exception err) {
                DataBaseUtil.mergeException(e, err);
            }
        }

        if (e.size() > 0)
            throw e;

        tms = null;
        ams = null;

        /*
         * check all the locks
         */
        ids.clear();
        ids1.clear();
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
         * Creating the default AnalysisReportFlags record to be added for each
         * analysis that is added to the database
         */
        defaultARF = new AnalysisReportFlagsDO(null, "N", "N", null, null, null);

        /*
         * this will be used as the released date for any newly released
         * analyses
         */
        now = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);

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
                    else if (data instanceof SampleNeonatalViewDO)
                        sampleNeonatal.delete( ((SampleNeonatalViewDO)data));
                    else if (data instanceof SampleClinicalViewDO)
                        sampleClinical.delete( ((SampleClinicalViewDO)data));
                    else if (data instanceof SamplePTDO)
                        samplePT.delete( ((SamplePTDO)data));
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
                    else if (data instanceof AttachmentItemViewDO)
                        attachmentItem.delete( ((AttachmentItemViewDO)data));
                    else if (data instanceof SampleItemViewDO)
                        item.delete( ((SampleItemViewDO)data));
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
            if (getSample(sm).getId() == null) {
                sample.add(getSample(sm));
            } else {
                /*
                 * check to see if the sample or any analysis has been
                 * unreleased. call final report e-save if they have; add the
                 * attachment created for the final report to the manager
                 */
                if (getPostProcessing(sm) == PostProcessing.UNRELEASE) {
                    atti = finalReport.runReportForESave(getSample(sm).getAccessionNumber());
                    if (atti != null) {
                        attachments = getAttachments(sm);
                        if (attachments == null) {
                            attachments = new ArrayList<AttachmentItemViewDO>();
                            setAttachments(sm, attachments);
                        }
                        attachments.add(atti);
                    }
                    setPostProcessing(sm, null);
                }
                sample.update(getSample(sm));
            }

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
                if (getSampleNeonatal(sm).getId() == null) {
                    getSampleNeonatal(sm).setSampleId(getSample(sm).getId());
                    sampleNeonatal.add(getSampleNeonatal(sm));
                } else {
                    sampleNeonatal.update(getSampleNeonatal(sm));
                }
            } else if (getSampleClinical(sm) != null) {
                if (getSampleClinical(sm).getId() == null) {
                    getSampleClinical(sm).setSampleId(getSample(sm).getId());
                    sampleClinical.add(getSampleClinical(sm));
                } else {
                    sampleClinical.update(getSampleClinical(sm));
                }
            } else if (getSamplePT(sm) != null) {
                if (getSamplePT(sm).getId() == null) {
                    getSamplePT(sm).setSampleId(getSample(sm).getId());
                    samplePT.add(getSamplePT(sm));
                } else {
                    samplePT.update(getSamplePT(sm));
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
            if (getAuxiliary(sm) != null) {
                for (AuxDataViewDO data : getAuxiliary(sm)) {
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

            if (getAttachments(sm) != null) {
                for (AttachmentItemViewDO data : getAttachments(sm)) {
                    if (data.getId() < 0) {
                        data.setReferenceTableId(Constants.table().SAMPLE);
                        data.setReferenceId(getSample(sm).getId());
                        attachmentItem.add(data);
                    } else {
                        attachmentItem.update(data);
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
            if (getAnalyses(sm) != null) {
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

                                defaultARF.setAnalysisId(data.getId());
                                analysisReportFlags.add(defaultARF);

                                amap.put(tmpid, data.getId());
                                amap.put(data.getId(), data.getId());
                            } else if ( !amap.containsKey(data.getId())) {
                                tmpid = data.getId();
                                /*
                                 * in the front-end if an existing analysis is
                                 * moved to an uncommitted item, the analysis'
                                 * item id would be negative; this makes sure
                                 * that the item gets linked correctly
                                 */
                                if (data.getSampleItemId() < 0)
                                    data.setSampleItemId(imap.get(data.getSampleItemId()));
                                /*
                                 * set the released date if this analysis is
                                 * currently being released
                                 */
                                if (Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()) &&
                                    data.getReleasedDate() == null)
                                    data.setReleasedDate(now);
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
                                // sort order is per analysis. avoid updating
                                // sort order if numbers are ascending
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
            }

            // add analysis notes; no updates allowed
            if (getAnalysisInternalNotes(sm) != null) {
                for (NoteViewDO data : getAnalysisInternalNotes(sm)) {
                    if (data.getId() < 0) {
                        data.setReferenceTableId(Constants.table().ANALYSIS);
                        data.setReferenceId(amap.get(data.getReferenceId()));
                        note.add(data);
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
     * Unlocks and returns list of sample managers with specified analysis ids
     * and requested load elements
     */
    @RolesAllowed({"sample-add", "sample-update"})
    public ArrayList<SampleManager1> unlockByAnalyses(ArrayList<Integer> analysisIds,
                                                      SampleManager1.Load... elements) throws Exception {
        HashSet<Integer> ids;
        ArrayList<SampleManager1> sms;

        sms = fetchByAnalyses(analysisIds, elements);

        ids = new HashSet<Integer>();
        for (SampleManager1 sm : sms)
            ids.add(getSample(sm).getId());

        lock.unlock(Constants.table().SAMPLE, new ArrayList<Integer>(ids));
        return sms;
    }

    /**
     * Check to see if 1) the accession number has been issued by the login
     * barcode process and 2) accession number has not been assigned to another
     * sample.
     */
    @RolesAllowed({"sample-add", "sample-update"})
    public void validateAccessionNumber(SampleManager1 sm) throws Exception {
        boolean isPositive;
        Integer accession;
        SampleDO data;
        SystemVariableDO sys;

        /*
         * accession number must be > 0, previously issued, and not duplicate
         */
        accession = getSample(sm).getAccessionNumber();
        isPositive = true;
        if (accession == null) {
            /*
             * for display
             */
            accession = 0;
            isPositive = false;
        } else if (accession <= 0) {
            isPositive = false;
        }

        if ( !isPositive)
            throw new InconsistencyException(Messages.get()
                                                     .sample_accessionNumberNotValidException(accession));

        try {
            sys = systemVariable.fetchByName("last_accession_number");
            if (accession.compareTo(Integer.valueOf(sys.getValue())) > 0)
                throw new InconsistencyException(Messages.get()
                                                         .sample_accessionNumberNotInUse(accession));
        } catch (Exception any) {
            log.log(Level.SEVERE, "Missing/invalid system variable 'last_accession_number'", any);
            throw any;
        }

        try {
            data = sample.fetchByAccessionNumber(accession);
            if ( !data.getId().equals(getSample(sm).getId()))
                throw new InconsistencyException(Messages.get()
                                                         .sample_accessionNumberDuplicate(accession));
        } catch (NotFoundException nf) {
        }
    }

    /**
     * Returns a manager loaded with the data of the quick-entered sample that
     * has the same accession number as the passed manager. Also, locks the
     * returned sample. Throws a NotFoundException if such a sample couldn't be
     * found; InconsistancyException if domain is not quick-entry or if the
     * sample was loaded from order.
     */
    public SampleManager1 mergeQuickEntry(SampleManager1 sm) throws Exception {
        SampleDO data, qdata;
        SampleManager1 qsm;

        data = getSample(sm);
        qdata = sample.fetchByAccessionNumber(data.getAccessionNumber());

        if ( !Constants.domain().QUICKENTRY.equals(qdata.getDomain()))
            throw new InconsistencyException(Messages.get()
                                                     .sample_notQuickEntryException(data.getAccessionNumber()));

        /*
         * there's a special condition that is not allowed; 1. the user loads a
         * sample from an order 2. the accession number is changed to
         * quick-entered sample
         */
        if (data.getOrderId() != null)
            throw new InconsistencyException(Messages.get()
                                                     .sample_cantLoadQEOrderPresentException(data.getAccessionNumber()));

        qsm = fetchForUpdate(qdata.getId());
        getSample(qsm).setDomain(data.getDomain());
        setSampleEnvironmental(qsm, getSampleEnvironmental(sm));
        setSamplePrivateWell(qsm, getSamplePrivateWell(sm));
        setSampleSDWIS(qsm, getSampleSDWIS(sm));
        setSampleNeonatal(qsm, getSampleNeonatal(sm));
        setSampleClinical(qsm, getSampleClinical(sm));
        setSamplePT(qsm, getSamplePT(sm));

        return qsm;
    }

    /**
     * Loads the data from send-out order or electronic order, depending on
     * domain, into the SampleManager. This method returns both the loaded
     * SampleManager and the list of warnings due to invalid data in the order.
     */
    public SampleTestReturnVO importOrder(SampleManager1 sm, Integer orderId) throws Exception {
        SampleDO data;
        ValidationErrorsList e;

        data = getSample(sm);
        if (data.getAccessionNumber() == null)
            throw new FormErrorException(Messages.get().sample_enterAccNumBeforeOrderLoad());

        e = new ValidationErrorsList();

        if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain()) ||
            Constants.domain().PRIVATEWELL.equals(data.getDomain()) ||
            Constants.domain().SDWIS.equals(data.getDomain()) ||
            Constants.domain().PT.equals(data.getDomain()))
            return sampleManagerOrderHelper.importSendoutOrder(sm, orderId, e);
        else if (Constants.domain().CLINICAL.equals(data.getDomain()) ||
                 Constants.domain().NEONATAL.equals(data.getDomain()))
            return sampleManagerOrderHelper.importEOrder(sm, orderId, e);
        else
            return null;
    }

    /**
     * Returns a VO with the manager filled with the data of the sample with the
     * passed id. The ids in the returned manager are set to either null or
     * negative numbers. Duplication is not allowed (exception is thrown) if any
     * analysis is past logged-in status or if there are reflexed analyses.
     * Cancelled analyses, storages and internal notes are not duplicated. If
     * some data e.g. organization could not be duplicated because of being
     * inactive, then the returned VO contains warnings for that and the
     * partially duplicated manager.
     */
    public SampleTestReturnVO duplicate(Integer sampleId) throws Exception {
        int i;
        Integer tmpId, accession, newAccession, seq, prevGroupId;
        Datetime now;
        SampleManager1 sm;
        SampleOrganizationViewDO sorg;
        SampleProjectViewDO sproj;
        AuxDataViewDO aux;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        DictionaryDO dict;
        ResultViewDO res;
        NoteViewDO n;
        AnalysisQaEventViewDO aqa;
        SampleTestReturnVO ret;
        ValidationErrorsList e;
        HashMap<Integer, Integer> amap;
        HashMap<Integer, SampleItemViewDO> imap;
        HashMap<Integer, TestViewDO> tmap;
        ArrayList<Integer> tids;
        ArrayList<TestViewDO> tests;
        ArrayList<SampleOrganizationViewDO> sorgs;
        ArrayList<SampleProjectViewDO> sprojs;
        ArrayList<AuxDataViewDO> auxiliary;
        ArrayList<AnalysisViewDO> analyses;
        ArrayList<ResultViewDO> results;
        ArrayList<NoteViewDO> notes;
        ArrayList<AnalysisQaEventViewDO> aqas;

        sm = fetchById(sampleId,
                       SampleManager1.Load.ORGANIZATION,
                       SampleManager1.Load.PROJECT,
                       SampleManager1.Load.QA,
                       SampleManager1.Load.AUXDATA,
                       SampleManager1.Load.NOTE,
                       SampleManager1.Load.ATTACHMENT,
                       SampleManager1.Load.RESULT);

        accession = getSample(sm).getAccessionNumber();
        /*
         * can't duplicate a completed or released sample
         */
        if (Constants.dictionary().SAMPLE_COMPLETED.equals(getSample(sm).getStatusId()) ||
            Constants.dictionary().SAMPLE_RELEASED.equals(getSample(sm).getStatusId()))
            throw new InconsistencyException(Messages.get()
                                                     .sample_cantDuplicateCompRelException(accession));

        ret = new SampleTestReturnVO();
        ret.setManager(sm);
        e = new ValidationErrorsList();
        ret.setErrors(e);

        getSample(sm).setId(null);
        getSample(sm).setAccessionNumber(null);

        /*
         * set default values in fields like revision, entered date etc.
         */
        setDefaults(getSample(sm));

        /*
         * sample level data
         */
        if (getSampleEnvironmental(sm) != null) {
            getSampleEnvironmental(sm).setId(null);
            getSampleEnvironmental(sm).setSampleId(null);
            if (getSampleEnvironmental(sm).getLocationAddress() != null)
                getSampleEnvironmental(sm).getLocationAddress().setId(null);
        } else if (getSamplePrivateWell(sm) != null) {
            getSamplePrivateWell(sm).setId(null);
            getSamplePrivateWell(sm).setSampleId(null);
            if (getSamplePrivateWell(sm).getLocationAddress() != null)
                getSamplePrivateWell(sm).getLocationAddress().setId(null);
            if (getSamplePrivateWell(sm).getReportToAddress() != null)
                getSamplePrivateWell(sm).getReportToAddress().setId(null);
        } else if (getSampleSDWIS(sm) != null) {
            getSampleSDWIS(sm).setId(null);
            getSampleSDWIS(sm).setSampleId(null);
        } else if (getSampleNeonatal(sm) != null) {
            getSampleNeonatal(sm).setId(null);
            getSampleNeonatal(sm).setSampleId(null);
        } else if (getSampleClinical(sm) != null) {
            getSampleClinical(sm).setId(null);
            getSampleClinical(sm).setSampleId(null);
        } else if (getSamplePT(sm) != null) {
            getSamplePT(sm).setId(null);
            getSamplePT(sm).setSampleId(null);
        }

        /*
         * this is the accession number used in the warnings for inactive
         * organizations etc. because the warnings apply to this sample and not
         * the sample that is being duplicated and the accession number for this
         * sample currently is null
         */
        newAccession = 0;
        sorgs = getOrganizations(sm);
        if (sorgs != null) {
            i = 0;
            while (i < sorgs.size()) {
                sorg = sorgs.get(i);
                /*
                 * duplicate the organization only if it's active; otherwise add
                 * the warning for it to inform the user
                 */
                if ("Y".equals(sorg.getOrganizationIsActive())) {
                    sorg.setId(sm.getNextUID());
                    sorg.setSampleId(null);
                    i++ ;
                } else {
                    e.add(new FormErrorWarning(Messages.get()
                                                       .sample_inactiveOrgWarning(newAccession,
                                                                                  sorg.getOrganizationName())));
                    sorgs.remove(i);
                }
            }
        }

        sprojs = getProjects(sm);
        if (sprojs != null) {
            i = 0;
            while (i < sprojs.size()) {
                sproj = sprojs.get(i);
                /*
                 * duplicate the project only if it's active; otherwise add the
                 * warning for it to inform the user
                 */
                if ("Y".equals(sproj.getProjectIsActive())) {
                    sproj.setId(sm.getNextUID());
                    sproj.setSampleId(null);
                    i++ ;
                } else {
                    e.add(new FormErrorWarning(Messages.get()
                                                       .sample_inactiveProjectWarning(newAccession,
                                                                                      sproj.getProjectName())));
                    sprojs.remove(i);
                }
            }
        }

        if (getSampleQAs(sm) != null) {
            for (SampleQaEventViewDO data : getSampleQAs(sm)) {
                data.setId(sm.getNextUID());
                data.setSampleId(null);
            }
        }

        auxiliary = getAuxiliary(sm);
        prevGroupId = null;
        if (auxiliary != null) {
            i = 0;
            while (i < auxiliary.size()) {
                aux = auxiliary.get(i);
                /*
                 * duplicate the aux group only if it's active; otherwise add
                 * the warning for it to inform the user; show the warning for
                 * each inactive group only once
                 */
                if ("Y".equals(aux.getAuxFieldGroupIsActive())) {
                    aux.setId(sm.getNextUID());
                    aux.setReferenceId(null);
                    i++ ;
                } else {
                    if ( !aux.getAuxFieldGroupId().equals(prevGroupId))
                        e.add(new FormErrorWarning(Messages.get()
                                                           .sample_inactiveAuxGroupWarning(newAccession,
                                                                                           aux.getAuxFieldGroupName())));
                    auxiliary.remove(i);
                }

                prevGroupId = aux.getAuxFieldGroupId();
            }
        }

        /*
         * duplicate only external note
         */
        if (getSampleExternalNote(sm) != null) {
            getSampleExternalNote(sm).setId(sm.getNextUID());
            getSampleExternalNote(sm).setReferenceId(null);
            getSampleExternalNote(sm).setTimestamp(null);
        }

        setSampleInternalNotes(sm, null);

        if (getAttachments(sm) != null) {
            for (AttachmentItemViewDO data : getAttachments(sm)) {
                data.setId(sm.getNextUID());
                data.setReferenceId(null);
            }
        }

        /*
         * level 2: everything is based on item ids
         */
        imap = new HashMap<Integer, SampleItemViewDO>();
        seq = 0;
        for (SampleItemViewDO data : getItems(sm)) {
            tmpId = data.getId();
            data.setId(sm.getNextUID());
            data.setItemSequence(seq++ );

            if (data.getTypeOfSampleId() != null) {
                /*
                 * don't duplicate the sample type if it's inactive
                 */
                dict = dictionaryCache.getById(data.getTypeOfSampleId());
                if ("N".equals(dict.getIsActive())) {
                    data.setTypeOfSampleId(null);
                    data.setTypeOfSample(null);
                    e.add(new FormErrorWarning(Messages.get()
                                                       .sampleItem_inactiveSampleTypeWarning(newAccession,
                                                                                             data.getItemSequence(),
                                                                                             dict.getEntry())));

                }
            }

            if (data.getSourceOfSampleId() != null) {
                /*
                 * don't duplicate the source if it's inactive
                 */
                dict = dictionaryCache.getById(data.getSourceOfSampleId());
                if ("N".equals(dict.getIsActive())) {
                    data.setSourceOfSampleId(null);
                    data.setSourceOfSample(null);
                    e.add(new FormErrorWarning(Messages.get()
                                                       .sampleItem_inactiveSourceWarning(newAccession,
                                                                                         data.getItemSequence(),
                                                                                         dict.getEntry())));
                }
            }

            if (data.getContainerId() != null) {
                /*
                 * don't duplicate the container if it's inactive
                 */
                dict = dictionaryCache.getById(data.getContainerId());
                if ("N".equals(dict.getIsActive())) {
                    data.setContainerId(null);
                    data.setContainer(null);
                    e.add(new FormErrorWarning(Messages.get()
                                                       .sampleItem_inactiveContainerWarning(newAccession,
                                                                                            data.getItemSequence(),
                                                                                            dict.getEntry())));
                }
            }

            if (data.getUnitOfMeasureId() != null) {
                /*
                 * don't duplicate the unit if it's inactive
                 */
                dict = dictionaryCache.getById(data.getUnitOfMeasureId());
                if ("N".equals(dict.getIsActive())) {
                    data.setUnitOfMeasureId(null);
                    e.add(new FormErrorWarning(Messages.get()
                                                       .sampleItem_inactiveUnitWarning(newAccession,
                                                                                       data.getItemSequence(),
                                                                                       dict.getEntry())));
                }
            }
            imap.put(tmpId, data);
        }
        getSample(sm).setNextItemSequence(seq);

        /*
         * level 3: everything is based on analysis ids
         */
        amap = new HashMap<Integer, Integer>();
        analyses = getAnalyses(sm);
        if (analyses != null) {
            /*
             * analyses linked to inactive tests are not duplicated; so fetch
             * the tests to find out which of them are inactive
             */
            tids = new ArrayList<Integer>();
            for (AnalysisViewDO a : analyses) {
                /*
                 * only add a test id to the list for fetching the tests if it's
                 * linked to an analysis that could be duplicated
                 */
                if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(a.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN.equals(a.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_INPREP.equals(a.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(a.getStatusId()))
                    tids.add(a.getTestId());
            }
            tmap = new HashMap<Integer, TestViewDO>();
            if (tids.size() > 0) {
                tests = test.fetchByIds(tids);
                for (TestViewDO t : tests)
                    tmap.put(t.getId(), t);
            }

            i = 0;
            now = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
            while (i < analyses.size()) {
                ana = analyses.get(i);
                if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(ana.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN.equals(ana.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_INPREP.equals(ana.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(ana.getStatusId())) {
                    if (ana.getParentAnalysisId() != null) {
                        /*
                         * don't allow duplication if any analysis has reflexed
                         * analyses
                         */
                        throw new InconsistencyException(Messages.get()
                                                                 .sample_cantDuplicateReflexAnaException(accession,
                                                                                                         ana.getTestName(),
                                                                                                         ana.getMethodName()));
                    } else if ("N".equals(tmap.get(ana.getTestId()).getIsActive())) {
                        /*
                         * don't duplicate the analysis if its test is inactive
                         */
                        e.add(new FormErrorWarning(Messages.get()
                                                           .sample_inactiveTestWarning(accession,
                                                                                       ana.getTestName(),
                                                                                       ana.getMethodName())));
                        analyses.remove(i);
                    } else {
                        tmpId = ana.getId();
                        ana.setId(sm.getNextUID());
                        item = imap.get(ana.getSampleItemId());
                        ana.setSampleItemId(item.getId());
                        ana.setRevision(0);
                        if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(ana.getStatusId()) ||
                            Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN.equals(ana.getStatusId()))
                            ana.setAvailableDate(now);
                        if (ana.getUnitOfMeasureId() != null) {
                            /*
                             * don't duplicate the unit if it's inactive
                             */
                            dict = dictionaryCache.getById(ana.getUnitOfMeasureId());
                            if ("N".equals(dict.getIsActive())) {
                                ana.setUnitOfMeasureId(null);
                                e.add(new FormErrorWarning(Messages.get()
                                                                   .analysis_inactiveUnitWarning(accession,
                                                                                                 item.getItemSequence(),
                                                                                                 ana.getTestName(),
                                                                                                 ana.getMethodName(),
                                                                                                 dict.getEntry())));
                            }
                        }
                        amap.put(tmpId, ana.getId());
                        i++ ;
                    }
                } else if (Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId())) {
                    /*
                     * don't duplicate cancelled analyses
                     */
                    analyses.remove(i);
                } else {
                    /*
                     * don't allow duplication if any analysis is past logged-in
                     * status
                     */
                    throw new InconsistencyException(Messages.get()
                                                             .sample_cantDuplicateAnaPastLoggedInException(accession));
                }
            }

            /*
             * resolve prep-analyses
             */
            for (AnalysisViewDO data : analyses)
                data.setPreAnalysisId(amap.get(data.getPreAnalysisId()));
        }

        if (getResults(sm) != null) {
            /*
             * remove the results whose analysis is no longer in the manager
             * (cancelled analyses are removed); link the remaining results to
             * their analysis using the negative ids
             */
            i = 0;
            results = getResults(sm);
            while (i < results.size()) {
                res = results.get(i);
                tmpId = amap.get(res.getAnalysisId());
                if (tmpId != null) {
                    res.setId(sm.getNextUID());
                    res.setAnalysisId(tmpId);
                    i++ ;
                } else {
                    results.remove(i);
                }
            }
        }

        if (getAnalysisExternalNotes(sm) != null) {
            /*
             * remove the external notes whose analyses are no longer in the
             * manager (cancelled analyses are removed); link the remaining
             * external notes to their analysis using the negative ids
             */
            i = 0;
            notes = getAnalysisExternalNotes(sm);
            while (i < notes.size()) {
                n = notes.get(i);
                tmpId = amap.get(n.getReferenceId());
                if (tmpId != null) {
                    n.setId(sm.getNextUID());
                    n.setReferenceId(tmpId);
                    n.setTimestamp(null);
                    i++ ;
                } else {
                    notes.remove(i);
                }
            }
        }

        setAnalysisInternalNotes(sm, null);

        if (getAnalysisQAs(sm) != null) {
            /*
             * remove the qa events whose analyses are no longer in the manager
             * (cancelled analyses are removed); link the remaining qa events to
             * their analysis using the negative ids
             */
            i = 0;
            aqas = getAnalysisQAs(sm);
            while (i < aqas.size()) {
                aqa = aqas.get(i);
                tmpId = amap.get(aqa.getAnalysisId());
                if (tmpId != null) {
                    aqa.setId(sm.getNextUID());
                    aqa.setAnalysisId(tmpId);
                    i++ ;
                } else {
                    aqas.remove(i);
                }
            }
        }

        return ret;
    }

    /**
     * Changes the sample's domain to the passed value if it's different from
     * the sample's domain. Removes the old domain's data and initializes the
     * new domain with the defaults for a new sample of this domain. Throws
     * exception if the sample is released, is not an existing one or if the
     * current or passed domain is Quick Entry.
     */
    public SampleManager1 changeDomain(SampleManager1 sm, String domain) throws Exception {
        boolean hasEOrderId, hasSendoutOrderId;
        String pov;
        SampleDO data;
        SampleNeonatalViewDO sn;
        SampleClinicalViewDO sc;

        data = getSample(sm);
        if (data.getDomain().equals(domain))
            return sm;

        /*
         * can only change the domain of an existing sample and only if it's not
         * released; can't change the domain to or from quick-entry
         */
        if (getSample(sm).getId() == null)
            throw new InconsistencyException(Messages.get()
                                                     .sample_cantChangeDomainNewSampleException());
        else if (Constants.dictionary().SAMPLE_RELEASED.equals(getSample(sm).getStatusId()))
            throw new InconsistencyException(Messages.get()
                                                     .sample_cantChangeDomainReleasedSampleException());
        else if (Constants.domain().QUICKENTRY.equals(data.getDomain()))
            throw new InconsistencyException(Messages.get()
                                                     .sample_cantChangeDomainQuickEntryException());
        else if (Constants.domain().QUICKENTRY.equals(domain))
            throw new InconsistencyException(Messages.get()
                                                     .sample_cantChangeDomainToQuickEntryException());

        if (getRemoved(sm) == null)
            setRemoved(sm, new ArrayList<DataObject>());

        /*
         * delete the existing domain; also find out if the sample is linked to
         * an eorder or send-out order
         */
        hasEOrderId = false;
        hasSendoutOrderId = false;
        pov = null;
        if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain())) {
            if (getSampleEnvironmental(sm).getId() != null)
                getRemoved(sm).add(getSampleEnvironmental(sm));
            setSampleEnvironmental(sm, null);
            hasSendoutOrderId = getSample(sm).getOrderId() != null;
        } else if (Constants.domain().PRIVATEWELL.equals(data.getDomain())) {
            if (getSamplePrivateWell(sm).getId() != null)
                getRemoved(sm).add(getSamplePrivateWell(sm));
            setSamplePrivateWell(sm, null);
            hasSendoutOrderId = getSample(sm).getOrderId() != null;
        } else if (Constants.domain().SDWIS.equals(data.getDomain())) {
            if (getSampleSDWIS(sm).getId() != null)
                getRemoved(sm).add(getSampleSDWIS(sm));
            setSampleSDWIS(sm, null);
            hasSendoutOrderId = getSample(sm).getOrderId() != null;
        } else if (Constants.domain().NEONATAL.equals(data.getDomain())) {
            sn = getSampleNeonatal(sm);
            if (sn.getId() != null)
                getRemoved(sm).add(sn);
            setSampleNeonatal(sm, null);
            hasEOrderId = getSample(sm).getOrderId() != null;
            pov = sn.getPaperOrderValidator();
        } else if (Constants.domain().CLINICAL.equals(data.getDomain())) {
            sc = getSampleClinical(sm);
            if (sc.getId() != null)
                getRemoved(sm).add(sc);
            setSampleClinical(sm, null);
            hasEOrderId = getSample(sm).getOrderId() != null;
            pov = sc.getPaperOrderValidator();
        } else if (Constants.domain().PT.equals(data.getDomain())) {
            if (getSamplePT(sm).getId() != null)
                getRemoved(sm).add(getSamplePT(sm));
            setSamplePT(sm, null);
            hasSendoutOrderId = getSample(sm).getOrderId() != null;
        }

        /*
         * change the domain to the passed value and set the defaults
         */
        setDomain(sm, domain);

        /*
         * if the previous domain used e-orders and the current one uses
         * send-out orders or vice-versa, blank the order id so that it doesn't
         * link to the wrong table; copy the paper order validator from the
         * previous domain to the current one if they both use it
         */
        if (hasEOrderId) {
            if ( (Constants.domain().ENVIRONMENTAL.equals(domain) ||
                  Constants.domain().PRIVATEWELL.equals(domain) ||
                  Constants.domain().SDWIS.equals(domain) || Constants.domain().PT.equals(domain)))
                getSample(sm).setOrderId(null);
            else if (Constants.domain().NEONATAL.equals(domain))
                getSampleNeonatal(sm).setPaperOrderValidator(pov);
            else if (Constants.domain().CLINICAL.equals(domain))
                getSampleClinical(sm).setPaperOrderValidator(pov);
        } else if (hasSendoutOrderId &&
                   (Constants.domain().NEONATAL.equals(domain) || Constants.domain().CLINICAL.equals(domain))) {
            getSample(sm).setOrderId(null);
        }

        return sm;
    }

    /**
     * Changes the sample's status to the passed value or the lowest status of
     * the analyses
     */
    public void changeSampleStatus(SampleManager1 sm, Integer statusId) {
        boolean isError, isLoggedIn, isCompleted, isReleased;
        Integer currStatusId, nextStatusId;
        SampleDO data;

        data = getSample(sm);

        currStatusId = data.getStatusId();
        nextStatusId = currStatusId;

        if (Constants.dictionary().SAMPLE_NOT_VERIFIED.equals(currStatusId)) {
            if (Constants.dictionary().SAMPLE_LOGGED_IN.equals(statusId))
                /*
                 * the sample was verified
                 */
                nextStatusId = statusId;
        } else if (Constants.dictionary().SAMPLE_ERROR.equals(statusId)) {
            nextStatusId = statusId;
        } else if (Constants.dictionary().SAMPLE_COMPLETED.equals(statusId) &&
                   Constants.dictionary().SAMPLE_RELEASED.equals(currStatusId)) {
            /*
             * the sample was unreleased
             */
            nextStatusId = statusId;
        } else {
            if (getAnalyses(sm) != null) {
                isError = false;
                isLoggedIn = false;
                isCompleted = false;
                isReleased = false;

                for (AnalysisViewDO ana : getAnalyses(sm)) {
                    if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(ana.getStatusId()) ||
                        Constants.dictionary().ANALYSIS_INPREP.equals(ana.getStatusId()) ||
                        Constants.dictionary().ANALYSIS_INITIATED.equals(ana.getStatusId()) ||
                        Constants.dictionary().ANALYSIS_REQUEUE.equals(ana.getStatusId()))
                        isLoggedIn = true;
                    else if (Constants.dictionary().ANALYSIS_COMPLETED.equals(ana.getStatusId()) ||
                             Constants.dictionary().ANALYSIS_ON_HOLD.equals(ana.getStatusId()))
                        isCompleted = true;
                    else if (Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
                             Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
                        isReleased = true;
                    else if (Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN.equals(ana.getStatusId()) ||
                             Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(ana.getStatusId()) ||
                             Constants.dictionary().ANALYSIS_ERROR_INITIATED.equals(ana.getStatusId()) ||
                             Constants.dictionary().ANALYSIS_ERROR_COMPLETED.equals(ana.getStatusId()))
                        isError = true;
                }

                /*
                 * change the sample status to lowest
                 */
                if (isError)
                    nextStatusId = Constants.dictionary().SAMPLE_ERROR;
                else if (isLoggedIn)
                    nextStatusId = Constants.dictionary().SAMPLE_LOGGED_IN;
                else if (isCompleted)
                    nextStatusId = Constants.dictionary().SAMPLE_COMPLETED;
                else if (isReleased)
                    nextStatusId = Constants.dictionary().SAMPLE_RELEASED;
            }
        }

        if ( !currStatusId.equals(nextStatusId)) {
            if (Constants.dictionary().SAMPLE_RELEASED.equals(nextStatusId)) {
                /*
                 * the sample was released
                 */
                if (data.getReleasedDate() == null)
                    data.setReleasedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
            } else if (data.getReleasedDate() != null) {
                data.setReleasedDate(null);
            }
            data.setStatusId(nextStatusId);
        }
    }

    /**
     * Resets the sample's status, released date and revision to take it out of
     * released status. Sets the flag for post-processing because of the sample
     * getting unreleased. Throws an exception if the sample is not released.
     */
    public SampleManager1 unrelease(SampleManager1 sm) throws Exception {
        String status;
        SampleDO data;

        data = getSample(sm);
        /*
         * the sample must be in Released status to unrelease it
         */
        if ( !Constants.dictionary().SAMPLE_RELEASED.equals(data.getStatusId())) {
            status = dictionaryCache.getById(Constants.dictionary().SAMPLE_RELEASED).getEntry();
            throw new InconsistencyException(Messages.get().sample_wrongStatusUnrelease(status));
        }

        data.setStatusId(Constants.dictionary().SAMPLE_COMPLETED);
        data.setReleasedDate(null);
        data.setRevision(data.getRevision() + 1);

        /*
         * mark the sample for post processing e.g. e-save as a result of it
         * getting unreleased
         */
        setPostProcessing(sm, SampleManager1.PostProcessing.UNRELEASE);

        return sm;
    }

    /**
     * Adds aux groups with the passed ids to the sample
     */
    public SampleTestReturnVO addAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        SampleTestReturnVO ret;
        ValidationErrorsList e;
        ArrayList<AuxDataViewDO> auxiliary;

        auxiliary = getAuxiliary(sm);
        if (auxiliary == null) {
            auxiliary = new ArrayList<AuxDataViewDO>();
            setAuxiliary(sm, auxiliary);
        }

        ret = new SampleTestReturnVO();
        ret.setManager(sm);
        e = new ValidationErrorsList();
        ret.setErrors(e);

        auxDataHelper.addAuxGroups(auxiliary, groupIds, e);

        /*
         * set negative ids in the newly added aux data
         */
        for (AuxDataViewDO aux : auxiliary) {
            if (aux.getId() == null)
                aux.setId(sm.getNextUID());
        }

        return ret;
    }

    /**
     * Removes aux data from the sample based on the list of group ids
     */
    public SampleManager1 removeAuxGroups(SampleManager1 sm, ArrayList<Integer> groupIds) throws Exception {
        ArrayList<AuxDataViewDO> removed;

        removed = auxDataHelper.removeAuxGroups(getAuxiliary(sm), new HashSet<Integer>(groupIds));

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
     * Adds an analysis to the sample based on the data provided in the VO. This
     * method returns both the loaded SampleManager and the list of
     * errors/warnings encountered while adding the analyses.
     */
    public SampleTestReturnVO addAnalysis(SampleManager1 sm, SampleTestRequestVO test) throws Exception {
        ArrayList<SampleTestRequestVO> tests;

        tests = new ArrayList<SampleTestRequestVO>();
        tests.add(test);

        return addAnalyses(sm, tests);
    }

    /**
     * Adds analyses to the sample based on the data provided in the list. This
     * method returns both the loaded SampleManager and the list of
     * errors/warnings encountered while adding the analyses.
     */
    public SampleTestReturnVO addAnalyses(SampleManager1 sm, ArrayList<SampleTestRequestVO> tests) throws Exception {
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
                }
                anaById.put(a.getId(), a);
            }
        }

        /*
         * the list of all the tests that are to be added
         */
        testIds = new ArrayList<Integer>();
        groupIds = null;
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
                    panelTests.add(new SampleTestRequestVO(test.getSampleId(),
                                                           test.getSampleItemId(),
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

        e = new ValidationErrorsList();
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

                analysisHelper.setPrepAnalysis(sm, anaById.get(test.getAnalysisId()), prep);
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
     * This method changes the specified analysis's status to the specified
     * status. It also updates any links between other analyses and this one, if
     * need be and runs the scriptlets linked to any part of the sample, because
     * of the change in status.
     */
    public SampleManager1 changeAnalysisStatus(SampleManager1 sm, Integer analysisId,
                                               Integer statusId) throws Exception {
        Action_Before action;
        AnalysisViewDO ana;

        /*
         * find the analysis
         */
        ana = null;
        for (AnalysisViewDO data : getAnalyses(sm)) {
            if (data.getId().equals(analysisId)) {
                ana = data;
                break;
            }
        }

        /*
         * determine if scriptlets need to be run because of the potential
         * change in status; runScriptlets is called before changeAnalysisStatus
         * because scriptlets can't be run if the status gets changed to
         * released and also some scriptlet may need to prevent an analysis from
         * changing status
         */
        action = null;
        if (Constants.dictionary().ANALYSIS_COMPLETED.equals(statusId)) {
            /*
             * find out if the analysis was completed or unreleased
             */
            if (Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()))
                action = Action_Before.UNRELEASE;
            else
                action = Action_Before.COMPLETE;
        } else if (Constants.dictionary().ANALYSIS_RELEASED.equals(statusId)) {
            action = Action_Before.RELEASE;
        }

        if (action != null) {
            /*
             * run the scriptlets
             */
            scriptletHelper.runScriptlets(sm,
                                          scriptletHelper.createCache(sm),
                                          Constants.uid().get(ana),
                                          SampleMeta.getAnalysisStatusId(),
                                          action);
        }

        sm = analysisHelper.changeAnalysisStatus(sm, analysisId, statusId);

        return sm;
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

    /**
     * This method sets preAnalysisId as the prep analysis id of the specified
     * analysis. If preAnalysisId is null then the analysis is taken out of
     * in-prep status.
     */
    public SampleManager1 changeAnalysisPrep(SampleManager1 sm, Integer analysisId,
                                             Integer preAnalysisId) throws Exception {
        return analysisHelper.changeAnalysisPrep(sm, analysisId, preAnalysisId);
    }

    /**
     * This method removes the analysis with the specified id and all of its
     * child data, e.g. results, qa events etc. It also removes any links
     * between other analyses and this one.
     */
    public SampleManager1 removeAnalysis(SampleManager1 sm, Integer analysisId) throws Exception {
        return analysisHelper.removeAnalysis(sm, analysisId);
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
        analysisHelper.addRowAnalytes(sm, analysis, analytes, indexes);
        return sm;
    }

    /**
     * Sets default values in the fields essential for a new sample
     */
    private void setDefaults(SampleDO data) {
        data.setNextItemSequence(0);
        data.setRevision(0);
        data.setEnteredDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
        data.setReceivedById(userCache.getId());
        data.setStatusId(Constants.dictionary().SAMPLE_NOT_VERIFIED);
    }

    /**
     * Sets the passed value as the sample's domain and initializes the domain
     * with defaults. Throws an exception if the passed domain is not valid.
     */
    private void setDomain(SampleManager1 sm, String domain) throws Exception {
        SampleDO s;

        s = getSample(sm);
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
            snn.setPatient(new PatientDO());
            snn.setNextOfKin(new PatientDO());
            setSampleNeonatal(sm, snn);
            s.setDomain(domain);
        } else if (Constants.domain().CLINICAL.equals(domain)) {
            SampleClinicalViewDO sc;

            sc = new SampleClinicalViewDO();
            sc.setPatient(new PatientDO());
            setSampleClinical(sm, sc);
            s.setDomain(domain);
        } else if (Constants.domain().PT.equals(domain)) {
            SamplePTDO sp;

            sp = new SamplePTDO();
            setSamplePT(sm, sp);
            s.setDomain(domain);
        } else {
            throw new InconsistencyException(Messages.get()
                                                     .sample_domainNotValid(s.getAccessionNumber()));
        }
    }

    /**
     * Validates the sample manager for add or update. The routine throws a list
     * of exceptions/warnings listing all the problems for each sample.
     */
    private void validate(SampleManager1 sm, SystemUserPermission permission, Integer maxAccession,
                          HashMap<Integer, TestManager> tms,
                          HashMap<Integer, AuxFieldGroupManager> ams,
                          HashMap<Integer, QaEventDO> qas) throws Exception {
        int cnt;
        AnalysisViewDO ana;
        ValidationErrorsList e;
        Integer accession;
        QaEventDO qa;
        HashMap<Integer, SampleItemViewDO> imap;
        HashMap<Integer, AnalysisViewDO> amap;

        e = new ValidationErrorsList();

        /*
         * sample level
         */
        accession = null;
        if (getSample(sm) != null) {
            accession = getSample(sm).getAccessionNumber();
            if (getSample(sm).isChanged())
                try {
                    sample.validate(getSample(sm), maxAccession);
                } catch (Exception err) {
                    DataBaseUtil.mergeException(e, err);
                }
        }

        /*
         * additional domain sample validation for sdwis, private well, ...
         * samples should go here after checking to see if the VO/DO has
         * changed.
         */
        if (getSampleSDWIS(sm) != null &&
            (getSampleSDWIS(sm).getId() == null || getSampleSDWIS(sm).isChanged())) {
            try {
                sampleSDWIS.validate(getSampleSDWIS(sm), accession);
            } catch (Exception err) {
                DataBaseUtil.mergeException(e, err);
            }
        }

        if (getSamplePT(sm) != null &&
            (getSamplePT(sm).getId() == null || getSamplePT(sm).isChanged())) {
            try {
                samplePT.validate(getSamplePT(sm), accession);
            } catch (Exception err) {
                DataBaseUtil.mergeException(e, err);
            }
        }

        /*
         * private well report-to is part of the domain information
         */
        cnt = 0;
        if (getSamplePrivateWell(sm) != null &&
            (getSamplePrivateWell(sm).getOrganizationId() != null || getSamplePrivateWell(sm).getReportToAddress() != null))
            cnt = 1;

        /*
         * samples have to have one report to.
         */
        if (getOrganizations(sm) != null) {
            for (SampleOrganizationViewDO data : getOrganizations(sm))
                if (Constants.dictionary().ORG_REPORT_TO.equals(data.getTypeId()))
                    cnt++ ;
        }

        if (cnt == 0)
            e.add(new FormErrorWarning(Messages.get().sample_reportToMissingWarning(accession)));
        else if (cnt != 1)
            e.add(new FormErrorException(Messages.get()
                                                 .sample_moreThanOneReportToException(accession)));

        /*
         * internal notes can't be updated or be empty
         */
        if (getSampleInternalNotes(sm) != null) {
            for (NoteViewDO data : getSampleInternalNotes(sm)) {
                try {
                    note.validate(data);
                } catch (Exception err) {
                    DataBaseUtil.mergeException(e,
                                                new FormErrorException(Messages.get()
                                                                               .sample_noteException(accession,
                                                                                                     err.getMessage())));
                }
            }
        }

        /*
         * aux data must be valid for the aux field
         */
        if (getAuxiliary(sm) != null) {
            for (AuxDataViewDO data : getAuxiliary(sm)) {
                if (data.isChanged())
                    try {
                        auxData.validate(data,
                                         ams.get(data.getAuxFieldGroupId()).getFormatter(),
                                         accession);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
            }
        }

        /*
         * type is required for each qa event
         */
        if (getSampleQAs(sm) != null) {
            for (SampleQaEventViewDO data : getSampleQAs(sm)) {
                if (data.isChanged())
                    try {
                        sampleQA.validate(data, accession);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
            }
        }

        /*
         * at least one sample item and items must have sample type
         */
        imap = new HashMap<Integer, SampleItemViewDO>();
        if (getItems(sm) == null || getItems(sm).size() < 1) {
            e.add(new FormErrorException(Messages.get().sample_minOneSampleItemException(accession)));
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
        amap = new HashMap<Integer, AnalysisViewDO>();
        if (getAnalyses(sm) != null) {
            for (AnalysisViewDO data : getAnalyses(sm)) {
                amap.put(data.getId(), data);
                if (data.isChanged() || imap.get(data.getSampleItemId()).isChanged()) {
                    try {
                        try {
                            analysis.validate(data,
                                              tms.get(data.getTestId()),
                                              sm,
                                              imap.get(data.getSampleItemId()));
                        } catch (ValidationErrorsList err) {
                            /*
                             * analysis validate can throw errors, warnings and
                             * cautions; the analysis can be committed as not
                             * being in error if only cautions were thrown, but
                             * they still need to be added to the list if they
                             * need to be shown to the user
                             */
                            if (err.hasWarnings() || err.hasErrors())
                                throw err;
                            else
                                DataBaseUtil.mergeException(e, err);
                        }
                        if (data.isChanged())
                            validatePermission(getSample(sm).getAccessionNumber(), data, permission);
                        /*
                         * if validation succeeded for an analysis previously in
                         * error, change the status to mark it as not in error
                         */
                        if (Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN.equals(data.getStatusId()))
                            analysisHelper.changeAnalysisStatus(sm,
                                                                data.getId(),
                                                                Constants.dictionary().ANALYSIS_LOGGED_IN);
                        else if (Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(data.getStatusId()))
                            analysisHelper.changeAnalysisStatus(sm,
                                                                data.getId(),
                                                                Constants.dictionary().ANALYSIS_INPREP);
                        else if (Constants.dictionary().ANALYSIS_ERROR_INITIATED.equals(data.getStatusId()))
                            analysisHelper.changeAnalysisStatus(sm,
                                                                data.getId(),
                                                                Constants.dictionary().ANALYSIS_INITIATED);
                        else if (Constants.dictionary().ANALYSIS_ERROR_COMPLETED.equals(data.getStatusId()))
                            analysisHelper.changeAnalysisStatus(sm,
                                                                data.getId(),
                                                                Constants.dictionary().ANALYSIS_COMPLETED);
                    } catch (ValidationErrorsList err) {
                        if (err.hasWarnings()) {
                            /*
                             * set the analysis in error because its data is
                             * inconsistent e.g. its unit of measure is not
                             * valid for the sample item's sample type
                             */
                            if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(data.getStatusId()))
                                analysisHelper.changeAnalysisStatus(sm,
                                                                    data.getId(),
                                                                    Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN);
                            else if (Constants.dictionary().ANALYSIS_INPREP.equals(data.getStatusId()))
                                analysisHelper.changeAnalysisStatus(sm,
                                                                    data.getId(),
                                                                    Constants.dictionary().ANALYSIS_ERROR_INPREP);
                            else if (Constants.dictionary().ANALYSIS_INITIATED.equals(data.getStatusId()))
                                analysisHelper.changeAnalysisStatus(sm,
                                                                    data.getId(),
                                                                    Constants.dictionary().ANALYSIS_ERROR_INITIATED);
                            else if (Constants.dictionary().ANALYSIS_COMPLETED.equals(data.getStatusId()))
                                analysisHelper.changeAnalysisStatus(sm,
                                                                    data.getId(),
                                                                    Constants.dictionary().ANALYSIS_ERROR_COMPLETED);
                        }
                        DataBaseUtil.mergeException(e, err);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        /*
         * internal notes can't be updated or be empty
         */
        if (getAnalysisInternalNotes(sm) != null) {
            for (NoteViewDO data : getAnalysisInternalNotes(sm)) {
                ana = amap.get(data.getReferenceId());
                try {
                    note.validate(data);
                } catch (Exception err) {
                    DataBaseUtil.mergeException(e,
                                                new FormErrorException(Messages.get()
                                                                               .analysis_noteException(accession,
                                                                                                       imap.get(ana.getSampleItemId())
                                                                                                           .getItemSequence(),
                                                                                                       ana.getTestName(),
                                                                                                       ana.getMethodName(),
                                                                                                       err.getMessage())));
                }
            }
        }

        /*
         * test specific analysis qa events must be valid for the analysis'
         * test; also, type is required for each qa event
         */

        if (getAnalysisQAs(sm) != null) {
            for (AnalysisQaEventViewDO data : getAnalysisQAs(sm)) {
                qa = qas.get(data.getQaEventId());
                ana = amap.get(data.getAnalysisId());
                if (data.isChanged())
                    try {
                        analysisQA.validate(data, accession, imap.get(ana.getSampleItemId())
                                                                 .getItemSequence(), ana);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }

                if (qa.getTestId() != null && !qa.getTestId().equals(ana.getTestId())) {
                    e.add(new FormErrorException(Messages.get()
                                                         .analysisQAEvent_invalidQAException(accession,
                                                                                             imap.get(ana.getSampleItemId())
                                                                                                 .getItemSequence(),
                                                                                             ana.getTestName(),
                                                                                             ana.getMethodName(),
                                                                                             qa.getName())));
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
                                        amap.get(data.getAnalysisId()));
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
            }
        }

        try {
            validateForDelete(sm);
        } catch (Exception err) {
            DataBaseUtil.mergeException(e, err);
        }

        if (e.size() > 0)
            throw e;
    }

    /**
     * Validate that the user has permission to add/update this analysis DO
     */
    private void validatePermission(Integer accession, AnalysisViewDO data,
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
     * Validate that removing any record won't cause any discrepancies in the
     * data e.g. links to nonexistent records
     */
    private void validateForDelete(SampleManager1 sm) throws Exception {
        Integer accession;
        SampleItemViewDO item;
        AnalysisViewDO ana;

        if (getRemoved(sm) == null)
            return;

        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;

        for (DataObject data : getRemoved(sm)) {
            if (data instanceof SampleItemViewDO) {
                item = (SampleItemViewDO)data;
                /*
                 * find out if this sample item is linked to any analyses
                 */
                if (getAnalyses(sm) != null) {
                    for (AnalysisViewDO a : getAnalyses(sm)) {
                        if (item.getId().equals(a.getSampleItemId()))
                            throw new FormErrorException(Messages.get()
                                                                 .sampleItem_cantRemoveException(accession,
                                                                                                 item.getItemSequence()));
                    }
                }
            } else if (data instanceof AnalysisViewDO) {
                /*
                 * previously committed analyses can't be removed; uncommitted
                 * analyses have negative ids, so they shouldn't be in this list
                 * either
                 */
                ana = (AnalysisViewDO)data;
                throw new FormErrorException(Messages.get()
                                                     .analysis_cantRemoveInUpdateException(accession,
                                                                                           ana.getTestName(),
                                                                                           ana.getMethodName()));
            }
        }
    }

    /**
     * creates an analysis and results using the requested test. It also finds
     * and sets the prep or returns list of prep tests.
     */
    private AnalysisViewDO addAnalysisAndPrep(SampleTestReturnVO ret,
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
        prepIds = analysisHelper.setPrepForAnalysis(ret.getManager(), ana, analyses, tm);
        if (prepIds != null)
            for (Integer id : prepIds)
                ret.addTest(ret.getManager().getSample().getId(),
                            test.getSampleItemId(),
                            id,
                            ana.getId(),
                            null,
                            null,
                            null,
                            false,
                            null);
        analysisHelper.addResults(ret.getManager(), tm, ana, test.getReportableAnalytes(), null);

        analyses.put(ana.getTestId(), ana);

        return ana;
    }
}