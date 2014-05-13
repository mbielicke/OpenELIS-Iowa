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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ExchangeCriteriaViewDO;
import org.openelis.domain.ExchangeExternalTermViewDO;
import org.openelis.domain.ExchangeProfileDO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.QaEventViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class DataExchangeXMLMapperBean {
    @EJB
    private UserCacheBean             systemUserCache;

    @EJB
    private DictionaryCacheBean       dictionaryCache;

    @EJB
    private MethodBean                method;

    @EJB
    private ProjectBean               project;

    @EJB
    private OrganizationBean          organization;

    @EJB
    private PWSBean                   pws;

    @EJB
    private QaEventBean               qaevent;

    @EJB
    private TestBean                  test;

    @EJB
    private AnalyteBean               analyte;

    @EJB
    private ExchangeExternalTermBean  exchangeExternalTerm;

    @EJB
    private TestTrailerBean           testTrailer;

    @EJB
    private SectionCacheBean          sectionCache;

    @EJB
    private PanelBean                 panel;

    @EJB
    private TestResultBean            testResult;

    @EJB
    private OrganizationParameterBean organizationParameter;

    private HashSet<Integer>          users, dicts, tests, testAnalytes, testResults, methods,
                    analytes, projects, organizations, qas, trailers, sections, panels;
    private static SimpleDateFormat   dateFormat, timeFormat;

    @PostConstruct
    public void init() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            timeFormat = new SimpleDateFormat("HH:mm:ss");
        }
    }

    /**
     * The method creates an XML doc from the sample manager information. The
     * method uses several fields from the exchange criteria such as
     * include-all-analyses, test-ids, etc. in creating the XML.
     * 
     * The produced XML segments such as analyses, tests, etc. are not nested
     * inside their parent segments (no hierarchy), however the ids present in
     * each segment can be used to nest the segments.
     */

    public Document getXML(SampleManager1 sm, ExchangeCriteriaManager cm) throws Exception {
        Boolean sampleOverridden, showValue;
        Document doc;
        Element root, header, elm, elm1;
        SampleOrganizationViewDO reportTo;
        ArrayList<OrganizationParameterDO> orgps;
        String testIds[];
        ArrayList<Integer> profiles;
        HashMap<Integer, Boolean> analyses;
        HashSet<Integer> onlyTests;

        /*
         * bunch of lookup lists that we need through the code
         */
        profiles = new ArrayList<Integer>();
        onlyTests = new HashSet<Integer>();
        analyses = new HashMap<Integer, Boolean>();

        /*
         * this xml doc is for sending results
         */
        doc = XMLUtil.createNew("message");
        root = doc.getDocumentElement();
        root.setAttribute("Type", "result-out");

        header = createHeader(doc, cm.getExchangeCriteria());
        root.appendChild(header);

        /*
         * either export all the analyses or just the specified tests
         */
        if ("N".equals(cm.getExchangeCriteria().getIsAllAnalysesIncluded())) {
            for (QueryData field : cm.getExchangeCriteria().getFields()) {
                if (SampleMeta.getAnalysisTestId().equals(field.getKey())) {
                    testIds = field.getQuery().split(",");
                    for (String id : testIds)
                        onlyTests.add(Integer.valueOf(id));
                    break;
                }
            }
        }

        /*
         * order of preference for translations
         */
        if (cm.getProfiles().count() > 0) {
            elm1 = doc.createElement("profiles");
            for (int i = 0; i < cm.getProfiles().count(); i++ ) {
                elm1.appendChild(createProfile(doc, cm.getProfiles().getProfileAt(i)));
                profiles.add(cm.getProfiles().getProfileAt(i).getProfileId());
            }
            header.appendChild(elm1);
        }

        /*
         * first level - sample, organizations, project ...
         */
        root.appendChild(createSample(doc, getSample(sm)));

        if (getSampleEnvironmental(sm) != null) {
            root.appendChild(createEnviromental(doc, getSampleEnvironmental(sm)));

            if (getSampleEnvironmental(sm).getLocationAddress().getId() != null)
                root.appendChild(createAddress(doc, getSampleEnvironmental(sm).getLocationAddress()));
        } else if (getSamplePrivateWell(sm) != null) {
            root.appendChild(createPrivateWell(doc, getSamplePrivateWell(sm)));

            if (getSamplePrivateWell(sm).getReportToAddress().getId() != null)
                root.appendChild(createAddress(doc, getSamplePrivateWell(sm).getReportToAddress()));

            if (getSamplePrivateWell(sm).getLocationAddress().getId() != null)
                root.appendChild(createAddress(doc, getSamplePrivateWell(sm).getLocationAddress()));
        } else if (getSampleSDWIS(sm) != null) {
            root.appendChild(createSDWIS(doc, getSampleSDWIS(sm)));
            root.appendChild(createPWS(doc, pws.fetchById(getSampleSDWIS(sm).getPwsId())));
        } else if (getSampleNeonatal(sm) != null) {
            root.appendChild(createNeonatal(doc, getSampleNeonatal(sm)));

            if (getSampleNeonatal(sm).getPatient() != null) {
                root.appendChild(createPatient(doc, getSampleNeonatal(sm).getPatient()));

                if (getSampleNeonatal(sm).getPatient().getAddress().getId() != null)
                    root.appendChild(createAddress(doc, getSampleNeonatal(sm).getPatient()
                                                                             .getAddress()));
            }

            if (getSampleNeonatal(sm).getNextOfKin() != null) {
                root.appendChild(createPatient(doc, getSampleNeonatal(sm).getNextOfKin()));

                if (getSampleNeonatal(sm).getNextOfKin().getAddress().getId() != null)
                    root.appendChild(createAddress(doc, getSampleNeonatal(sm).getNextOfKin()
                                                                             .getAddress()));
            }

            if (getSampleNeonatal(sm).getProviderId() != null)
                root.appendChild(createProvider(doc, getSampleNeonatal(sm).getProvider()));
        }

        sampleOverridden = false;
        if (getSampleQAs(sm) != null) {
            for (SampleQaEventViewDO sq : getSampleQAs(sm)) {
                root.appendChild(createSampleQaEvent(doc, sq));
                if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sq.getTypeId()))
                    sampleOverridden = true;
            }
        }

        if (getProjects(sm) != null) {
            for (SampleProjectViewDO p : getProjects(sm))
                root.appendChild(createSampleProject(doc, p));
        }

        reportTo = null;
        if (getOrganizations(sm) != null) {
            for (SampleOrganizationViewDO o : getOrganizations(sm)) {
                if (Constants.dictionary().ORG_REPORT_TO.equals(o.getTypeId()))
                    reportTo = o;
                root.appendChild(createSampleOrganization(doc, o));
            }
        }

        /*
         * add the report to organization's destination URL and aggregator URL
         * to the header
         */
        if (reportTo != null) {
            try {
                orgps = organizationParameter.fetchByOrganizationId(reportTo.getOrganizationId());
                for (OrganizationParameterDO op : orgps) {
                    if (Constants.dictionary().ORG_PROD_EPARTNER_URL.equals(op.getTypeId()) ||
                        Constants.dictionary().ORG_TEST_EPARTNER_URL.equals(op.getTypeId()) ||
                        Constants.dictionary().ORG_EPARTNER_AGGR.equals(op.getTypeId()))
                        header.appendChild(createOrganizationParameter(doc, op));
                }
            } catch (NotFoundException e) {
                // ignore
            }
        }

        if (getSampleExternalNote(sm) != null) {
            /*
             * put the sample note in its own group so it can be distinguished
             * from analysis external notes
             */
            elm = doc.createElement("sample_external_notes");
            elm.appendChild(createNote(doc, getSampleExternalNote(sm)));
            root.appendChild(elm);
        }

        if (getAuxilliary(sm) != null) {
            for (AuxDataViewDO a : getAuxilliary(sm)) {
                root.appendChild(createAuxData(doc, a));
                if (Constants.dictionary().AUX_DICTIONARY.equals(a.getTypeId()) &&
                    a.getValue() != null)
                    root.appendChild(createLinkDictionary(doc,
                                                          "aux_data_dictionary",
                                                          a.getId(),
                                                          Integer.valueOf(a.getValue())));
            }
        }

        /*
         * second level - sample items
         */
        for (SampleItemViewDO item : getItems(sm))
            root.appendChild(createItem(doc, item));

        /*
         * third level - analyses
         */
        if (getAnalyses(sm) != null) {
            for (AnalysisViewDO a : getAnalyses(sm)) {
                /*
                 * skip cancelled and test id's that were in restricted list
                 */
                if (Constants.dictionary().ANALYSIS_CANCELLED.equals(a.getStatusId()) ||
                    ( !onlyTests.isEmpty() && !onlyTests.contains(a.getTestId())))
                    continue;
                root.appendChild(createAnalysis(doc, a));
                analyses.put(a.getId(),
                             Constants.dictionary().ANALYSIS_RELEASED.equals(a.getStatusId()));
            }
        }

        /*
         * mark the analysis if it has overridden QA event
         */
        if (getAnalysisQAs(sm) != null) {
            for (AnalysisQaEventViewDO aq : getAnalysisQAs(sm)) {
                if (analyses.containsKey(aq.getAnalysisId())) {
                    root.appendChild(createAnalysisQaEvent(doc, aq));
                    if (Constants.dictionary().QAEVENT_OVERRIDE.equals(aq.getTypeId()))
                        analyses.put(aq.getAnalysisId(), false);
                }
            }
        }

        /*
         * fourth level - results
         */
        if (getResults(sm) != null) {
            for (ResultViewDO r : getResults(sm)) {
                /*
                 * skip the results if analysis cancelled or we are not
                 * reporting all the tests. don't show result value if analysis
                 * is not released or overridden.
                 */
                showValue = analyses.get(r.getAnalysisId());
                if (showValue == null)
                    continue;

                root.appendChild(createResult(doc, r, showValue && !sampleOverridden));
                if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(r.getTypeId()) &&
                    r.getValue() != null && showValue && !sampleOverridden) {
                    root.appendChild(createLinkDictionary(doc,
                                                          "result_dictionary",
                                                          r.getId(),
                                                          Integer.valueOf(r.getValue())));
                }
            }
        }

        if (getUsers(sm) != null) {
            for (AnalysisUserViewDO u : getUsers(sm)) {
                if (analyses.containsKey(u.getAnalysisId()))
                    root.appendChild(createAnalysisUser(doc, u));
            }
        }

        if (getAnalysisExternalNotes(sm) != null) {
            /*
             * put the analyses notes in its own group so it can be
             * distinguished from sample external notes
             */
            elm = null;
            for (NoteViewDO n : getAnalysisExternalNotes(sm)) {
                if (analyses.containsKey(n.getReferenceId())) {
                    if (elm == null) {
                        elm = doc.createElement("analysis_external_notes");
                        root.appendChild(elm);
                    }
                    elm.appendChild(createNote(doc, n));
                }
            }
        }

        /*
         * lookup and output various referenced objects; order is important
         */
        if (projects != null) {
            for (ProjectViewDO p : project.fetchByIds(projects))
                root.appendChild(createProject(doc, p));
        }

        if (qas != null) {
            for (QaEventViewDO qa : qaevent.fetchByIds(qas))
                root.appendChild(createQaEvent(doc, qa));
        }

        if (tests != null) {
            for (TestViewDO t : test.fetchByIds(tests))
                root.appendChild(createTest(doc, t));
        }

        if (methods != null) {
            for (MethodDO m : method.fetchByIds(methods))
                root.appendChild(createMethod(doc, m));
        }

        if (trailers != null) {
            for (TestTrailerDO t : testTrailer.fetchByIds(trailers))
                root.appendChild(createTrailer(doc, t));
        }

        if (testResults != null) {
            for (TestResultViewDO tr : testResult.fetchByIds(testResults))
                root.appendChild(createTestResult(doc, tr));
        }

        if (users != null) {
            for (Integer id : users)
                root.appendChild(createUser(doc, systemUserCache.getSystemUser(id)));
        }

        if (panels != null) {
            for (PanelDO p : panel.fetchByIds(panels))
                root.appendChild(createPanel(doc, p));
        }

        if (sections != null) {
            for (Integer id : sections)
                root.appendChild(createSection(doc, sectionCache.getById(id)));
        }

        if (organizations != null) {
            for (OrganizationViewDO org : organization.fetchByIds(organizations)) {
                root.appendChild(createOrganization(doc, org));
                root.appendChild(createAddress(doc, org.getAddress()));
            }
        }

        if (analytes != null) {
            for (AnalyteViewDO ana : analyte.fetchByIds(analytes))
                root.appendChild(createAnalyte(doc, ana));
        }

        if (dicts != null) {
            for (Integer id : dicts)
                root.appendChild(createDictionary(doc, dictionaryCache.getById(id)));
        }

        /*
         * add the translation mappings (external terms) for various reference
         * tables
         */
        if (profiles != null && profiles.size() > 0) {
            if (organizations.size() > 0) {
                elm = createTranslations(Constants.table().ORGANIZATION,
                                         organizations,
                                         profiles,
                                         "organization_translations",
                                         doc);
                if (elm != null)
                    root.appendChild(elm);
            }

            if (tests.size() > 0) {
                elm = createTranslations(Constants.table().TEST,
                                         tests,
                                         profiles,
                                         "test_translations",
                                         doc);
                if (elm != null)
                    root.appendChild(elm);
            }

            if (methods.size() > 0) {
                elm = createTranslations(Constants.table().METHOD,
                                         methods,
                                         profiles,
                                         "method_translations",
                                         doc);
                if (elm != null)
                    root.appendChild(elm);
            }

            if (testAnalytes.size() > 0) {
                elm = createTranslations(Constants.table().TEST_ANALYTE,
                                         testAnalytes,
                                         profiles,
                                         "test_analyte_translations",
                                         doc);
                if (elm != null)
                    root.appendChild(elm);
            }

            if (analytes.size() > 0) {
                elm = createTranslations(Constants.table().ANALYTE,
                                         analytes,
                                         profiles,
                                         "analyte_translations",
                                         doc);
                if (elm != null)
                    root.appendChild(elm);
            }

            if (dicts.size() > 0) {
                elm = createTranslations(Constants.table().DICTIONARY,
                                         dicts,
                                         profiles,
                                         "dictionary_translations",
                                         doc);
                if (elm != null)
                    root.appendChild(elm);
            }
        }
        return doc;
    }

    /**
     * creates and returns the following element: <header> <name></name>
     * 
     */
    public Element createHeader(Document doc, ExchangeCriteriaViewDO criteria) throws Exception {
        Element elm;

        if (criteria == null)
            return null;

        elm = doc.createElement("header");
        setAttribute(elm, "include_all_analyses", criteria.getIsAllAnalysesIncluded());
        setText(doc, elm, "name", criteria.getName());
        if (criteria.getEnvironmentId() != null)
            setText(doc, elm, "environment", dictionaryCache.getById(criteria.getEnvironmentId())
                                                            .getEntry());

        return elm;
    }

    public Element createProfile(Document doc, ExchangeProfileDO exchangeProfile) throws Exception {
        Element elm;

        elm = doc.createElement("profile");
        if (exchangeProfile.getProfileId() != null)
            elm.setTextContent(dictionaryCache.getById(exchangeProfile.getProfileId()).getEntry());

        return elm;
    }

    public Element createSample(Document doc, SampleDO sample) {
        Element elm;

        elm = doc.createElement("sample");
        setAttribute(elm, "id", sample.getId());
        setAttribute(elm, "domain", sample.getDomain());
        setAttribute(elm, "accession_number", sample.getAccessionNumber());
        setAttribute(elm, "revision", sample.getRevision());
        setAttribute(elm, "order_id", sample.getOrderId());
        setAttribute(elm, "entered_date", sample.getEnteredDate());
        setAttribute(elm, "received_date", sample.getReceivedDate());
        setAttribute(elm, "received_by_id", sample.getReceivedById());
        setAttribute(elm, "collection_date", sample.getCollectionDate());
        setAttribute(elm, "status_id", sample.getStatusId());
        setAttribute(elm, "collection_time", sample.getCollectionTime());
        setAttribute(elm, "package_id", sample.getPackageId());
        setText(doc, elm, "client_reference", sample.getClientReference());
        setAttribute(elm, "released_date", sample.getReleasedDate());

        addUser(sample.getReceivedById());
        addDictionary(sample.getStatusId());

        return elm;
    }

    public Element createEnviromental(Document doc, SampleEnvironmentalDO environmental) {
        Element elm;

        elm = doc.createElement("sample_environmental");
        setAttribute(elm, "id", environmental.getId());
        setAttribute(elm, "sample_id", environmental.getSampleId());
        setAttribute(elm, "is_hazardous", environmental.getIsHazardous());
        setAttribute(elm, "priority", environmental.getPriority());
        setText(doc, elm, "description", environmental.getDescription());
        setText(doc, elm, "collector", environmental.getCollector());
        setText(doc, elm, "collector_phone", environmental.getCollectorPhone());
        setText(doc, elm, "location", environmental.getLocation());
        setAttribute(elm, "location_address_id", environmental.getLocationAddress().getId());

        return elm;
    }

    public Element createPrivateWell(Document doc, SamplePrivateWellViewDO privateWell) {
        Element elm;

        elm = doc.createElement("sample_private_well");
        setAttribute(elm, "id", privateWell.getId());
        setAttribute(elm, "sample_id", privateWell.getSampleId());
        setText(doc, elm, "location", privateWell.getLocation());
        setAttribute(elm, "location_address_id", privateWell.getLocationAddress().getId());
        setText(doc, elm, "owner", privateWell.getOwner());
        setText(doc, elm, "collector", privateWell.getCollector());
        setText(doc, elm, "well_number", privateWell.getWellNumber());

        return elm;
    }

    public Element createSDWIS(Document doc, SampleSDWISDO sdwis) {
        Element elm;

        elm = doc.createElement("sample_sdwis");
        setAttribute(elm, "id", sdwis.getId());
        setAttribute(elm, "sample_id", sdwis.getSampleId());
        setAttribute(elm, "pws_id", sdwis.getPwsId());
        setAttribute(elm, "state_lab_id", sdwis.getStateLabId());
        setText(doc, elm, "facility_id", sdwis.getFacilityId());
        setAttribute(elm, "sample_type_id", sdwis.getSampleTypeId());
        setAttribute(elm, "sample_category_id", sdwis.getSampleCategoryId());
        setText(doc, elm, "sample_point_id", sdwis.getSamplePointId());
        setText(doc, elm, "location", sdwis.getLocation());
        setText(doc, elm, "collector", sdwis.getCollector());

        addDictionary(sdwis.getSampleTypeId());
        addDictionary(sdwis.getSampleCategoryId());

        return elm;
    }

    public Element createPWS(Document doc, PWSDO pws) {
        Element elm;

        elm = doc.createElement("pws");
        setAttribute(elm, "id", pws.getId());
        setAttribute(elm, "tinwsys_is_number", pws.getTinwsysIsNumber());
        setText(doc, elm, "number0", pws.getNumber0());
        setText(doc, elm, "alternate_st_num", pws.getAlternateStNum());
        setText(doc, elm, "name", pws.getName());
        setText(doc, elm, "activity_status_cd", pws.getActivityStatusCd());
        setText(doc, elm, "d_prin_city_svd_nm", pws.getDPrinCitySvdNm());
        setText(doc, elm, "d_prin_cnty_svd_nm", pws.getDPrinCntySvdNm());
        setText(doc, elm, "d_population_count", pws.getDPopulationCount());
        setText(doc, elm, "d_pws_st_type_cd", pws.getDPwsStTypeCd());
        setText(doc, elm, "activity_rsn_txt", pws.getActivityRsnTxt());

        return elm;
    }

    public Element createNeonatal(Document doc, SampleNeonatalDO neonatal) {
        Element elm;

        elm = doc.createElement("sample_neonatal");
        setAttribute(elm, "id", neonatal.getId());
        setAttribute(elm, "sample_id", neonatal.getSampleId());
        setAttribute(elm, "patient_id", neonatal.getPatientId());
        setAttribute(elm, "birth_order", neonatal.getBirthOrder());
        setAttribute(elm, "gestational_age", neonatal.getGestationalAge());
        setAttribute(elm, "next_of_kin_id", neonatal.getNextOfKinId());
        setAttribute(elm, "next_of_kin_relation_id", neonatal.getNextOfKinRelationId());
        setAttribute(elm, "is_repeat", neonatal.getIsRepeat());
        setAttribute(elm, "is_nicu", neonatal.getIsNicu());
        setAttribute(elm, "feeding_id", neonatal.getFeedingId());
        setAttribute(elm, "weight_sign", neonatal.getWeightSign());
        setAttribute(elm, "weight", neonatal.getWeight());
        setAttribute(elm, "is_transfused", neonatal.getIsTransfused());
        setAttribute(elm, "transfusion_date", neonatal.getTransfusionDate());
        setAttribute(elm, "is_collection_valid", neonatal.getIsCollectionValid());
        setAttribute(elm, "collection_age", neonatal.getCollectionAge());
        setAttribute(elm, "provider_id", neonatal.getProviderId());
        setText(doc, elm, "form_number", neonatal.getFormNumber());

        addDictionary(neonatal.getNextOfKinRelationId());
        addDictionary(neonatal.getFeedingId());

        return elm;
    }

    public Element createPatient(Document doc, PatientDO patient) {
        Element elm;

        elm = doc.createElement("patient");
        setAttribute(elm, "id", patient.getId());
        setText(doc, elm, "last_name", patient.getLastName());
        setText(doc, elm, "first_name", patient.getFirstName());
        setText(doc, elm, "middle_name", patient.getMiddleName());
        setAttribute(elm, "address_id", patient.getAddress().getId());
        setAttribute(elm, "birth_date", patient.getBirthDate());
        setAttribute(elm, "birth_time", patient.getBirthTime());
        setAttribute(elm, "gender_id", patient.getGenderId());
        setAttribute(elm, "race_id", patient.getRaceId());
        setAttribute(elm, "ethnicity_id", patient.getRaceId());

        addDictionary(patient.getGenderId());
        addDictionary(patient.getRaceId());
        addDictionary(patient.getEthnicityId());

        return elm;
    }

    public Element createProvider(Document doc, ProviderDO provider) {
        Element elm;

        elm = doc.createElement("provider");
        setAttribute(elm, "id", provider.getId());
        setText(doc, elm, "last_name", provider.getLastName());
        setText(doc, elm, "first_name", provider.getFirstName());
        setText(doc, elm, "middle_name", provider.getMiddleName());
        setAttribute(elm, "type_id", provider.getTypeId());
        setText(doc, elm, "npi", provider.getNpi());

        addDictionary(provider.getTypeId());

        return elm;
    }

    public Element createSampleProject(Document doc, SampleProjectViewDO sampleProject) {
        Element elm;

        elm = doc.createElement("sample_project");
        setAttribute(elm, "id", sampleProject.getId());
        setAttribute(elm, "sample_id", sampleProject.getSampleId());
        setAttribute(elm, "project_id", sampleProject.getProjectId());
        setAttribute(elm, "is_permanent", sampleProject.getIsPermanent());

        addProject(sampleProject.getProjectId());

        return elm;
    }

    public Element createSampleOrganization(Document doc,
                                            SampleOrganizationViewDO sampleOrganization) {
        Element elm;

        elm = doc.createElement("sample_organization");

        setAttribute(elm, "id", sampleOrganization.getId());
        setAttribute(elm, "sample_id", sampleOrganization.getSampleId());
        setAttribute(elm, "organization_id", sampleOrganization.getOrganizationId());
        setText(doc, elm, "organization_attention", sampleOrganization.getOrganizationAttention());
        setAttribute(elm, "type_id", sampleOrganization.getTypeId());

        addOrganization(sampleOrganization.getOrganizationId());
        addDictionary(sampleOrganization.getTypeId());

        return elm;
    }

    public Element createSampleQaEvent(Document doc, SampleQaEventViewDO sampleQa) {
        Element elm;

        elm = doc.createElement("sample_qaevent");
        setAttribute(elm, "id", sampleQa.getId());
        setAttribute(elm, "sample_id", sampleQa.getSampleId());
        setAttribute(elm, "qaevent_id", sampleQa.getQaEventId());
        setAttribute(elm, "type_id", sampleQa.getTypeId());
        setAttribute(elm, "is_billable", sampleQa.getIsBillable());

        addQa(sampleQa.getQaEventId());
        addDictionary(sampleQa.getTypeId());

        return elm;
    }

    public Element createAuxData(Document doc, AuxDataViewDO auxData) {
        Element elm;

        elm = doc.createElement("aux_data");
        setAttribute(elm, "id", auxData.getId());
        setAttribute(elm, "sort_order", auxData.getSortOrder());
        setAttribute(elm, "aux_field_id", auxData.getAuxFieldId());
        setAttribute(elm, "reference_id", auxData.getReferenceId());
        setAttribute(elm, "reference_table_id", auxData.getReferenceTableId());
        setAttribute(elm, "is_reportable", auxData.getIsReportable());
        setAttribute(elm, "analyte_id", auxData.getAnalyteId());
        setAttribute(elm, "type_id", auxData.getTypeId());
        setText(doc, elm, "value", auxData.getValue());

        addDictionary(auxData.getTypeId());
        addAnalyte(auxData.getAnalyteId());

        return elm;
    }

    public Element createItem(Document doc, SampleItemViewDO sampleItem) {
        Element elm;

        elm = doc.createElement("sample_item");
        setAttribute(elm, "id", sampleItem.getId());
        setAttribute(elm, "sample_id", sampleItem.getSampleId());
        setAttribute(elm, "sample_item_id", sampleItem.getSampleItemId());
        setAttribute(elm, "item_sequence", sampleItem.getItemSequence());
        setAttribute(elm, "type_of_sample_id", sampleItem.getTypeOfSampleId());
        setAttribute(elm, "source_of_sample_id", sampleItem.getSourceOfSampleId());
        setText(doc, elm, "source_other", sampleItem.getSourceOther());
        setAttribute(elm, "container_id", sampleItem.getContainerId());
        setAttribute(elm, "quantity", sampleItem.getQuantity());
        setAttribute(elm, "unit_of_measure_id", sampleItem.getUnitOfMeasureId());

        addDictionary(sampleItem.getTypeOfSampleId());
        addDictionary(sampleItem.getSourceOfSampleId());
        addDictionary(sampleItem.getContainerId());
        addDictionary(sampleItem.getUnitOfMeasureId());

        return elm;
    }

    public Element createAnalysis(Document doc, AnalysisViewDO analysis) {
        Element elm;

        elm = doc.createElement("analysis");
        setAttribute(elm, "id", analysis.getId());
        setAttribute(elm, "sample_item_id", analysis.getSampleItemId());
        setAttribute(elm, "revision", analysis.getRevision());
        setAttribute(elm, "test_id", analysis.getTestId());
        setAttribute(elm, "section_id", analysis.getSectionId());
        setAttribute(elm, "panel_id", analysis.getPanelId());
        setAttribute(elm, "pre_analysis_id", analysis.getPreAnalysisId());
        setAttribute(elm, "parent_analysis_id", analysis.getParentAnalysisId());
        setAttribute(elm, "parent_result_id", analysis.getParentResultId());
        setAttribute(elm, "type_id", analysis.getTypeId());
        setAttribute(elm, "is_reportable", analysis.getIsReportable());
        setAttribute(elm, "unit_of_measure_id", analysis.getUnitOfMeasureId());
        setAttribute(elm, "status_id", analysis.getStatusId());
        setAttribute(elm, "available_date", analysis.getAvailableDate());
        setAttribute(elm, "started_date", analysis.getStartedDate());
        setAttribute(elm, "completed_date", analysis.getCompletedDate());
        setAttribute(elm, "released_date", analysis.getReleasedDate());
        setAttribute(elm, "printed_date", analysis.getPrintedDate());

        addTest(analysis.getTestId());
        addMethod(analysis.getMethodId());
        addSection(analysis.getSectionId());
        addPanel(analysis.getPanelId());
        addDictionary(analysis.getStatusId());
        addDictionary(analysis.getUnitOfMeasureId());

        return elm;
    }

    public Element createTest(Document doc, TestViewDO test) {
        Element elm;

        elm = doc.createElement("test");
        setAttribute(elm, "id", test.getId());
        setText(doc, elm, "name", test.getName());
        setText(doc, elm, "description", test.getDescription());
        setText(doc, elm, "reporting_description", test.getReportingDescription());
        setAttribute(elm, "method_id", test.getMethodId());
        setAttribute(elm, "is_active", test.getIsActive());
        setAttribute(elm, "active_begin", test.getActiveBegin());
        setAttribute(elm, "active_end", test.getActiveEnd());
        setAttribute(elm, "is_reportable", test.getIsReportable());
        setAttribute(elm, "time_transit", test.getTimeTransit());
        setAttribute(elm, "time_holding", test.getTimeHolding());
        setAttribute(elm, "time_ta_average", test.getTimeTaAverage());
        setAttribute(elm, "time_ta_warning", test.getTimeTaWarning());
        setAttribute(elm, "time_ta_max", test.getTimeTaMax());
        setAttribute(elm, "test_trailer_id", test.getTestTrailerId());
        setAttribute(elm, "test_format_id", test.getTestFormatId());
        setAttribute(elm, "revision_method_id", test.getRevisionMethodId());
        setAttribute(elm, "reporting_method_id", test.getReportingMethodId());
        setAttribute(elm, "sorting_method_id", test.getSortingMethodId());
        setAttribute(elm, "reporting_sequence", test.getReportingSequence());

        addTrailer(test.getTestTrailerId());
        addDictionary(test.getTestFormatId());
        addDictionary(test.getTestFormatId());
        addDictionary(test.getRevisionMethodId());
        addDictionary(test.getReportingMethodId());
        addDictionary(test.getSortingMethodId());

        return elm;
    }

    public Element createMethod(Document doc, MethodDO method) {
        Element elm;

        elm = doc.createElement("method");
        setAttribute(elm, "id", method.getId());
        setText(doc, elm, "name", method.getName());
        setText(doc, elm, "description", method.getDescription());
        setText(doc, elm, "reporting_description", method.getReportingDescription());
        setAttribute(elm, "is_active", method.getIsActive());
        setAttribute(elm, "active_begin", method.getActiveBegin());
        setAttribute(elm, "active_end", method.getActiveEnd());

        return elm;
    }

    public Element createSection(Document doc, SectionDO section) {
        Element elm;

        elm = doc.createElement("section");
        setAttribute(elm, "id", section.getId());
        setText(doc, elm, "name", section.getName());
        setText(doc, elm, "description", section.getDescription());
        setAttribute(elm, "parent_section_id", section.getParentSectionId());
        setAttribute(elm, "is_external", section.getIsExternal());
        setAttribute(elm, "organization_id", section.getOrganizationId());

        addOrganization(section.getOrganizationId());

        return elm;
    }

    public Node createPanel(Document doc, PanelDO panel) {
        Element elm;

        elm = doc.createElement("panel");
        setAttribute(elm, "id", panel.getId());
        setText(doc, elm, "name", panel.getName());
        setText(doc, elm, "description", panel.getDescription());

        return elm;
    }

    public Node createTrailer(Document doc, TestTrailerDO trailer) {
        Element elm;

        elm = doc.createElement("test_trailer");
        setAttribute(elm, "id", trailer.getId());
        setText(doc, elm, "name", trailer.getName());
        setText(doc, elm, "description", trailer.getDescription());
        setText(doc, elm, "text", trailer.getText());

        return elm;
    }

    public Element createTestResult(Document doc, TestResultViewDO testResult) {
        Element elm;

        elm = doc.createElement("test_result");
        setAttribute(elm, "id", testResult.getId());
        setAttribute(elm, "test_id", testResult.getTestId());
        setAttribute(elm, "result_group", testResult.getResultGroup());
        setAttribute(elm, "sort_order", testResult.getSortOrder());
        setAttribute(elm, "flags_id", testResult.getFlagsId());
        setAttribute(elm, "type_id", testResult.getTypeId());
        setText(doc, elm, "value", testResult.getValue());
        setAttribute(elm, "roundingMethodId", testResult.getRoundingMethodId());
        setAttribute(elm, "unitOfMeasureId", testResult.getUnitOfMeasureId());

        addDictionary(testResult.getFlagsId());

        return elm;
    }

    public Element createAnalysisUser(Document doc, AnalysisUserViewDO analysisUser) {
        Element elm;

        elm = doc.createElement("analysis_user");
        setAttribute(elm, "id", analysisUser.getId());
        setAttribute(elm, "analysis_id", analysisUser.getAnalysisId());
        setAttribute(elm, "system_user_id", analysisUser.getSystemUserId());
        setAttribute(elm, "action_id", analysisUser.getActionId());

        addUser(analysisUser.getSystemUserId());
        addDictionary(analysisUser.getActionId());

        return elm;
    }

    public Element createProject(Document doc, ProjectViewDO project) {
        Element elm;

        elm = doc.createElement("project");
        setAttribute(elm, "id", project.getId());
        setText(doc, elm, "name", project.getName());
        setText(doc, elm, "description", project.getDescription());
        setAttribute(elm, "started_date", project.getStartedDate());
        setAttribute(elm, "completed_date", project.getCompletedDate());
        setAttribute(elm, "is_active", project.getIsActive());
        setText(doc, elm, "reference_to", project.getReferenceTo());
        setAttribute(elm, "owner_id", project.getOwnerId());

        addUser(project.getOwnerId());

        return elm;
    }

    public Element createOrganization(Document doc, OrganizationDO organization) {
        Element elm;

        elm = doc.createElement("organization");
        setAttribute(elm, "id", organization.getId());
        setAttribute(elm, "parent_organization_id", organization.getParentOrganizationId());
        setText(doc, elm, "name", organization.getName());
        setAttribute(elm, "is_active", organization.getIsActive());
        setAttribute(elm, "address_id", organization.getAddress().getId());

        return elm;
    }
    
    public Element createOrganizationParameter(Document doc,
                                               OrganizationParameterDO organizationParameter) {
        Element elm;

        elm = doc.createElement("organization_parameter");

        setAttribute(elm, "id", organizationParameter.getId());
        setAttribute(elm, "organization_id", organizationParameter.getOrganizationId());
        setAttribute(elm, "type_id", organizationParameter.getTypeId());
        setText(doc, elm, "value", organizationParameter.getValue());

        addDictionary(organizationParameter.getTypeId());

        return elm;
    }

    public Element createDictionary(Document doc, DictionaryDO dictionary) {
        Element elm;

        elm = doc.createElement("dictionary");
        setAttribute(elm, "id", dictionary.getId());
        setText(doc, elm, "system_name", dictionary.getSystemName());
        setText(doc, elm, "entry", dictionary.getEntry());

        return elm;
    }

    public Element createAddress(Document doc, AddressDO address) {
        Element elm;

        elm = doc.createElement("address");
        setAttribute(elm, "id", address.getId());
        setText(doc, elm, "multiple_unit", address.getMultipleUnit());
        setText(doc, elm, "street_address", address.getStreetAddress());
        setText(doc, elm, "city", address.getCity());
        setText(doc, elm, "state", address.getState());
        setText(doc, elm, "zip_code", address.getZipCode());
        setText(doc, elm, "work_phone", address.getWorkPhone());
        setText(doc, elm, "home_phone", address.getHomePhone());
        setText(doc, elm, "cell_phone", address.getCellPhone());
        setText(doc, elm, "fax_phone", address.getFaxPhone());
        setText(doc, elm, "email", address.getEmail());
        setText(doc, elm, "country", address.getCountry());

        return elm;
    }

    public Element createAnalysisQaEvent(Document doc, AnalysisQaEventViewDO sampleQaEvent) {
        Element elm;

        elm = doc.createElement("analysis_qaevent");
        setAttribute(elm, "id", sampleQaEvent.getId());
        setAttribute(elm, "analysis_id", sampleQaEvent.getAnalysisId());
        setAttribute(elm, "qaevent_id", sampleQaEvent.getQaEventId());
        setAttribute(elm, "type_id", sampleQaEvent.getTypeId());
        setAttribute(elm, "is_billable", sampleQaEvent.getIsBillable());

        addQa(sampleQaEvent.getQaEventId());
        addDictionary(sampleQaEvent.getTypeId());

        return elm;
    }

    public Element createQaEvent(Document doc, QaEventViewDO qaEvent) {
        Element elm;

        elm = doc.createElement("qaevent");
        setAttribute(elm, "id", qaEvent.getId());
        setText(doc, elm, "name", qaEvent.getName());
        setText(doc, elm, "description", qaEvent.getDescription());
        setAttribute(elm, "test_id", qaEvent.getTestId());
        setAttribute(elm, "type_id", qaEvent.getTypeId());
        setAttribute(elm, "is_billable", qaEvent.getIsBillable());
        setAttribute(elm, "reporting_sequence", qaEvent.getReportingSequence());
        setText(doc, elm, "reporting_text", qaEvent.getReportingText());

        addTest(qaEvent.getTestId());
        addDictionary(qaEvent.getTypeId());

        return elm;
    }

    public Element createAnalyte(Document doc, AnalyteViewDO analyte) {
        Element elm;

        elm = doc.createElement("analyte");
        setAttribute(elm, "id", analyte.getId());
        setText(doc, elm, "name", analyte.getName());
        setAttribute(elm, "is_active", analyte.getIsActive());
        setAttribute(elm, "parent_analyte_id", analyte.getParentAnalyteId());
        setText(doc, elm, "external_id", analyte.getExternalId());

        return elm;
    }

    public Element createNote(Document doc, NoteViewDO note) {
        Element elm;

        elm = doc.createElement("note");
        setAttribute(elm, "id", note.getId());
        setAttribute(elm, "reference_id", note.getReferenceId());
        setAttribute(elm, "reference_table_id", note.getReferenceTableId());
        setAttribute(elm, "timestamp", note.getTimestamp());
        setAttribute(elm, "is_external", note.getIsExternal());
        setAttribute(elm, "system_user_id", note.getSystemUserId());
        setText(doc, elm, "subject", note.getSubject());
        setText(doc, elm, "text", note.getText());

        return elm;
    }

    public Element createResult(Document doc, ResultViewDO result, boolean showValue) {
        Element elm;

        elm = doc.createElement("result");
        setAttribute(elm, "id", result.getId());
        setAttribute(elm, "analysis_id", result.getAnalysisId());
        setAttribute(elm, "test_analyte_id", result.getTestAnalyteId());
        setAttribute(elm, "test_result_id", result.getTestResultId());
        setAttribute(elm, "is_column", result.getIsColumn());
        setAttribute(elm, "sort_order", result.getSortOrder());
        setAttribute(elm, "is_reportable", result.getIsReportable());
        setAttribute(elm, "analyte_id", result.getAnalyteId());
        setAttribute(elm, "type_id", result.getTypeId());

        /*
         * the values are not shown if result override qaevents or analysis not
         * released
         */
        if (showValue && result.getValue() != null)
            setText(doc, elm, "value", result.getValue());

        addTestAnalyte(result.getTestAnalyteId());
        addTestResult(result.getTestResultId());
        addAnalyte(result.getAnalyteId());
        addDictionary(result.getTypeId());

        return elm;
    }

    public Element createExternalTerm(Document doc, ExchangeExternalTermViewDO externalTerm) throws Exception {
        Element elm;

        elm = doc.createElement("translation");
        setAttribute(elm, "id", externalTerm.getId());
        setAttribute(elm, "reference_id", externalTerm.getExchangeLocalTermReferenceId());
        setAttribute(elm, "is_active", externalTerm.getIsActive());
        setText(doc, elm, "profile", dictionaryCache.getById(externalTerm.getProfileId())
                                                    .getEntry());
        setText(doc, elm, "code", externalTerm.getExternalTerm());
        setText(doc, elm, "description", externalTerm.getExternalDescription());
        setText(doc, elm, "coding_system", externalTerm.getExternalCodingSystem());
        setText(doc, elm, "version", externalTerm.getVersion());

        return elm;
    }

    public Element createUser(Document doc, SystemUserVO user) {
        Element elm;

        elm = doc.createElement("system_user");
        setAttribute(elm, "id", user.getId());
        setText(doc, elm, "external_id", user.getExternalId());
        setText(doc, elm, "login_name", user.getLoginName());
        setText(doc, elm, "last_name", user.getLastName());
        setText(doc, elm, "first_name", user.getFirstName());
        setText(doc, elm, "initials", user.getInitials());

        return elm;
    }

    private Element createLinkDictionary(Document doc, String name, Integer resultId,
                                         Integer dictionaryId) {
        Element elm;

        elm = doc.createElement(name);
        setAttribute(elm, "id", resultId);
        setAttribute(elm, "dictionary_id", dictionaryId);

        addDictionary(dictionaryId);

        return elm;
    }

    private Element createTranslations(int referenceTable, HashSet<Integer> referenceIds,
                                       ArrayList<Integer> profiles, String nodeName, Document doc) throws Exception {
        Element elm;
        ArrayList<ExchangeExternalTermViewDO> terms;

        elm = doc.createElement(nodeName);
        try {
            terms = exchangeExternalTerm.fetchByReferenceTableIdReferenceIdsProfileIds(referenceTable,
                                                                                       referenceIds,
                                                                                       profiles);
            for (ExchangeExternalTermViewDO term : terms)
                elm.appendChild(createExternalTerm(doc, term));
        } catch (NotFoundException e) {
            return null;
        }

        return elm;
    }

    private void setAttribute(Element e, String name, Object value) {
        if (value != null) {
            if (value instanceof Datetime)
                e.setAttribute(name, format((Datetime)value));
            else
                e.setAttribute(name, value.toString());
        }
    }

    private Element setText(Document doc, Element e, String name, Object value) {
        Element e1;

        if (value != null) {
            e1 = doc.createElement(name);
            if (value instanceof Datetime)
                e1.setTextContent(format((Datetime)value));
            else
                e1.setTextContent(value.toString());
            e.appendChild(e1);
        } else {
            e1 = null;
        }
        return e1;
    }

    private void addProject(Integer id) {
        if (id != null) {
            if (projects == null)
                projects = new HashSet<Integer>();
            projects.add(id);
        }
    }

    private void addOrganization(Integer id) {
        if (id != null) {
            if (organizations == null)
                organizations = new HashSet<Integer>();
            organizations.add(id);
        }
    }

    private void addQa(Integer id) {
        if (id != null) {
            if (qas == null)
                qas = new HashSet<Integer>();
            qas.add(id);
        }
    }

    private void addPanel(Integer id) {
        if (id != null) {
            if (panels == null)
                panels = new HashSet<Integer>();
            panels.add(id);
        }
    }

    private void addTest(Integer id) {
        if (id != null) {
            if (tests == null)
                tests = new HashSet<Integer>();
            tests.add(id);
        }
    }

    private void addMethod(Integer id) {
        if (id != null) {
            if (methods == null)
                methods = new HashSet<Integer>();
            methods.add(id);
        }
    }

    private void addTrailer(Integer id) {
        if (id != null) {
            if (trailers == null)
                trailers = new HashSet<Integer>();
            trailers.add(id);
        }
    }

    private void addTestAnalyte(Integer id) {
        if (id != null) {
            if (testAnalytes == null)
                testAnalytes = new HashSet<Integer>();
            testAnalytes.add(id);
        }
    }

    private void addTestResult(Integer id) {
        if (id != null) {
            if (testResults == null)
                testResults = new HashSet<Integer>();
            testResults.add(id);
        }
    }

    private void addSection(Integer id) {
        if (id != null) {
            if (sections == null)
                sections = new HashSet<Integer>();
            sections.add(id);
        }
    }

    private void addUser(Integer id) {
        // skip user 0 (system)
        if (id != null && id != 0) {
            if (users == null)
                users = new HashSet<Integer>();
            users.add(id);
        }
    }

    private void addAnalyte(Integer id) {
        if (id != null) {
            if (analytes == null)
                analytes = new HashSet<Integer>();
            analytes.add(id);
        }
    }

    private void addDictionary(Integer id) {
        if (id != null) {
            if (dicts == null)
                dicts = new HashSet<Integer>();
            dicts.add(id);
        }
    }

    private String format(Datetime dt) {
        String ytod, htos;

        if (dt == null)
            return null;

        ytod = null;
        if (dt.getStartCode() < Datetime.DAY)
            ytod = dateFormat.format(dt.getDate());

        htos = null;
        if (dt.getEndCode() > Datetime.DAY)
            htos = timeFormat.format(dt.getDate());

        return DataBaseUtil.concatWithSeparator(ytod, "T", htos);
    }
}