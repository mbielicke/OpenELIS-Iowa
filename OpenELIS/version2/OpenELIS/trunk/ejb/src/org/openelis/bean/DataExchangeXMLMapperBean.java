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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ExchangeCriteriaViewDO;
import org.openelis.domain.ExchangeExternalTermViewDO;
import org.openelis.domain.ExchangeProfileDO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.domain.QaEventViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalyteLocal;
import org.openelis.local.DataExchangeXMLMapperLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.ExchangeExternalTermLocal;
import org.openelis.local.MethodLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.local.PWSLocal;
import org.openelis.local.ProjectLocal;
import org.openelis.local.QaeventLocal;
import org.openelis.local.SectionCacheLocal;
import org.openelis.local.TestTrailerLocal;
import org.openelis.local.UserCacheLocal;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.AnalysisUserManager;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.meta.SampleMeta;
import org.openelis.util.XMLUtil;
import org.openelis.utils.EJBFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)

public class DataExchangeXMLMapperBean implements DataExchangeXMLMapperLocal {

    @EJB
    private UserCacheLocal            systemUserCache;

    @EJB
    private DictionaryCacheLocal      dictionaryCache;
    
    @EJB
    private MethodLocal               method;

    @EJB
    private ProjectLocal              project;

    @EJB
    private OrganizationLocal         organization;

    @EJB
    private PWSLocal                  pws;

    @EJB
    private QaeventLocal              qaevent;

    @EJB
    private AnalyteLocal              analyte;

    @EJB
    private ExchangeExternalTermLocal exchangeExternalTerm;
    
    @EJB
    private TestTrailerLocal          testTrailer;
    
    @EJB
    private SectionCacheLocal         sectionCache;
     
    private static Integer            resultDictTypeId, auxDictTypeId, cancelledStatusId, releasedStatusId;

    private static SimpleDateFormat   dateFormat, timeFormat;
    
    private static final String       VERSION = "2.1.8", MESSAGE_TYPE = "result-out";
    
    private static final Logger       log = Logger.getLogger(DataExchangeXMLMapperBean.class);
    
    @PostConstruct
    public void init() {
        if (resultDictTypeId == null) { 
            try {
                resultDictTypeId = EJBFactory.getDictionary().fetchBySystemName("test_res_type_dictionary").getId();
                auxDictTypeId = EJBFactory.getDictionary().fetchBySystemName("aux_dictionary").getId();
                cancelledStatusId = EJBFactory.getDictionary().fetchBySystemName("analysis_cancelled").getId();
                releasedStatusId = EJBFactory.getDictionary().fetchBySystemName("analysis_released").getId();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                timeFormat = new SimpleDateFormat("HH:mm:ss");
            } catch (Exception e) {
                log.error("Could not fetch dictionary with system name "+ "test_res_type_dictionary", e);
            }
        }
    }
    
    public Document getXML(SampleManager manager, ExchangeCriteriaManager exchangeCriteriaMan) throws Exception {
        int i, j, k, l;
        boolean sampleOverridden, analysisOverridden, showValue;
        Integer typeId, dictId;
        String testQuery[];
        ArrayList<Integer> profileIds;
        ArrayList<QueryData> fields;
        HashSet<Integer> userIds, critTestIds, dictIds, testIds, methodIds, analyteIds, projIds,
                         orgIds, qaIds, trailerIds, sectionIds;
        Document doc;
        Element root, header, profiles, sampleNotes, anaNotes;
        SystemUserVO user;
        DictionaryDO dict;
        ArrayList<AnalyteViewDO> analyteList;
        ArrayList<ProjectViewDO> projList;
        ArrayList<OrganizationViewDO> orgList;
        ExchangeCriteriaViewDO criteria;
        ExchangeProfileManager profileMan;
        ExchangeProfileDO profile;       
        SampleDO sample;
        SampleEnvironmentalDO env;
        SamplePrivateWellViewDO well;
        SampleSDWISViewDO sdwis;
        PWSDO pwsDO;        
        SampleItemViewDO item;        
        SampleItemManager itemMan;
        SampleProjectViewDO sampleProj;
        SampleProjectManager projMan;
        SampleOrganizationViewDO sampleOrg;
        SampleOrganizationManager orgMan;
        SampleQaEventManager sampleQaMan;
        SampleQaEventViewDO sampleQa;
        AuxDataManager auxDataMan;       
        AuxDataViewDO auxData;
        NoteManager sampleNoteMan, anaNoteMan;
        NoteViewDO note;
        ArrayList<QaEventViewDO> qaList;
        AnalysisViewDO analysis;
        AnalysisManager anaMan;
        TestViewDO testDO;
        SectionViewDO sect;
        ArrayList<TestTrailerDO> trailerList; 
        ArrayList<MethodDO> methodList;        
        ResultViewDO result;
        ArrayList<ResultViewDO> results;
        AnalysisResultManager resultMan;
        AnalysisUserManager anaUserMan; 
        AnalysisUserViewDO anaUser;
        AnalysisQaEventManager anaQaMan;
        AnalysisQaEventViewDO anaQa;        

        doc = XMLUtil.createNew("message");
        root = doc.getDocumentElement();
        root.setAttribute("type", MESSAGE_TYPE);
        
        profileIds = null;
        critTestIds = null;
        if (exchangeCriteriaMan != null) {
            criteria = exchangeCriteriaMan.getExchangeCriteria();
            header = getHeader(doc, criteria);

            /*
             * if all analyses are not to be included in the xml document then only 
             * the ones that have the test ids from the query defined in the exchange
             * criteria are included 
             */
            if ("N".equals(criteria.getIsAllAnalysesIncluded())) {
                fields = criteria.getFields();
                critTestIds = new HashSet<Integer>();
                for (QueryData field : fields) {
                    if (SampleMeta.getAnalysisTestId().equals(field.key)) {
                        testQuery = field.query.split(",");
                        for (String tq : testQuery) {
                            try {
                                critTestIds.add(Integer.valueOf(tq));
                            } catch (NumberFormatException e) {
                                log.error("Invalid test id: " + tq, e);
                            }
                        }
                        break;
                    }
                }
            }
            
            profileMan = exchangeCriteriaMan.getProfiles();

            if (profileMan.count() > 0) {
                profileIds = new ArrayList<Integer>();
                profiles = doc.createElement("profiles");
                for (i = 0; i < profileMan.count(); i++ ) {
                    profile = profileMan.getProfileAt(i);
                    profiles.appendChild(getProfile(doc, profile));
                    profileIds.add(profile.getProfileId());
                }
                header.appendChild(profiles);
            }

            root.appendChild(header);
        }
        
        sample = manager.getSample();
        root.appendChild(getSample(doc, sample));
        
        userIds = new HashSet<Integer>();
        userIds.add(sample.getReceivedById());
        
        dictIds = new HashSet<Integer>();
        dictIds.add(sample.getStatusId());
        
        orgIds = new HashSet<Integer>();
        /*
         * create element for the domain manager
         */
        if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(sample.getDomain())) {
            env = ((SampleEnvironmentalManager) manager.getDomainManager()).getEnvironmental();
            root.appendChild(getSampleEnviromental(doc, env));
            if (env.getLocationAddress().getId() != null)
                root.appendChild(getAddress(doc, env.getLocationAddress()));
        } else if (SampleManager.WELL_DOMAIN_FLAG.equals(sample.getDomain())) {
            well = ((SamplePrivateWellManager) manager.getDomainManager()).getPrivateWell();
            root.appendChild(getSamplePrivateWell(doc, well));
            
            orgIds.add(well.getOrganizationId());
            
            if (well.getReportToAddress().getId() != null)
                root.appendChild(getAddress(doc, well.getReportToAddress()));            
            
            if (well.getLocationAddress().getId() != null)
                root.appendChild(getAddress(doc, well.getLocationAddress()));
        } else if (SampleManager.SDWIS_DOMAIN_FLAG.equals(sample.getDomain())) {
            sdwis = ((SampleSDWISManager) manager.getDomainManager()).getSDWIS();
            root.appendChild(getSampleSDWIS(doc, sdwis));
            dictIds.add(sdwis.getSampleTypeId());
            dictIds.add(sdwis.getSampleCategoryId());
            
            pwsDO = pws.fetchById(sdwis.getPwsId());
            root.appendChild(getPWS(doc, pwsDO));
        }
        
        sampleQaMan = manager.getQaEvents();
        sampleOverridden = sampleQaMan.hasResultOverrideQA();
        qaIds = new HashSet<Integer>(); 
        for (i = 0; i < sampleQaMan.count(); i++) {
            /*
             * create elements for sample qa events
             */
            sampleQa = sampleQaMan.getSampleQAAt(i);
            root.appendChild(getSampleQaEvent(doc, sampleQa));
            
            dictIds.add(sampleQa.getTypeId());
            qaIds.add(sampleQa.getQaEventId());
        }
        
        testIds = new HashSet<Integer>();
        methodIds = new HashSet<Integer>();
        analyteIds = new HashSet<Integer>();
        trailerIds = new HashSet<Integer>();
        sectionIds = new HashSet<Integer>();
        anaNotes = null;
        
        itemMan = manager.getSampleItems();
        for (i = 0; i < itemMan.count(); i++) {
            item = itemMan.getSampleItemAt(i);
            root.appendChild(getSampleItem(doc, item));

            dictIds.add(item.getTypeOfSampleId());
            if (item.getSourceOfSampleId() != null)
                dictIds.add(item.getSourceOfSampleId());
            if (item.getContainerId() != null)
                dictIds.add(item.getContainerId());
            if (item.getUnitOfMeasureId() != null)
                dictIds.add(item.getUnitOfMeasureId());
            
            anaMan = itemMan.getAnalysisAt(i);                                               
            for (j = 0; j < anaMan.count(); j++) {
                analysis = anaMan.getAnalysisAt(j); 
                /*
                 * the data for an analysis is not shown in the xml if it is cancelled
                 * or its test id was not included in the query defined in the exchange
                 * criteria the flag for not including all analyses was true  
                 */
                if (cancelledStatusId.equals(analysis.getStatusId()) || 
                                (critTestIds != null && !critTestIds.contains(analysis.getTestId())))
                    continue;
                
                anaQaMan = anaMan.getQAEventAt(j);
                analysisOverridden = anaQaMan.hasResultOverrideQA();                
                for (k = 0; k < anaQaMan.count(); k++) {
                    anaQa = anaQaMan.getAnalysisQAAt(k);
                    root.appendChild(getAnalysisQaEvent(doc, anaQa));
                    
                    dictIds.add(anaQa.getTypeId());
                    qaIds.add(anaQa.getQaEventId());
                }
                
                /*
                 * create elements for analyses, tests and methods
                 */
                               
                root.appendChild(getAnalysis(doc, analysis));
                
                testDO = anaMan.getTestAt(j).getTest();
                root.appendChild(getTest(doc, testDO));
                
                dictIds.add(testDO.getTestFormatId());
                if (testDO.getRevisionMethodId() != null)
                    dictIds.add(testDO.getRevisionMethodId());
                dictIds.add(testDO.getReportingMethodId());
                dictIds.add(testDO.getSortingMethodId());
                
                testIds.add(analysis.getTestId());
                methodIds.add(analysis.getMethodId());
                
                if (testDO.getTestTrailerId() != null)
                    trailerIds.add(testDO.getTestTrailerId());                
                
                if (analysis.getUnitOfMeasureId() != null)
                    dictIds.add(analysis.getUnitOfMeasureId());
                dictIds.add(analysis.getStatusId());
                
                if (analysis.getSectionId() != null)
                    sectionIds.add(analysis.getSectionId());
                
                /*
                 * create elements for results
                 */
                resultMan = anaMan.getAnalysisResultAt(j);
                for (k = 0; k < resultMan.rowCount(); k++) {
                    results = resultMan.getRowAt(k); 
                    for (l = 0; l < results.size(); l++) {
                        result = results.get(l);
                        
                        /*
                         * the values for those results are not shown, the analysis
                         * for which has qa event(s) of type result override or 
                         * which belongs to a sample that has such qa event(s) or
                         * the status is something other than released 
                         */
                        showValue = !sampleOverridden && !analysisOverridden
                                    && releasedStatusId.equals(analysis.getStatusId());
                        
                        root.appendChild(getResult(doc, result, showValue));
                        
                        analyteIds.add(result.getAnalyteId());
                        
                        typeId = result.getTypeId();
                        if (typeId != null)
                            dictIds.add(typeId);
                        
                        if (!showValue) 
                            continue;
                        
                        if (resultDictTypeId.equals(typeId) && result.getValue() != null) {
                            dictId = Integer.valueOf(result.getValue());
                            dictIds.add(dictId);
                            /* 
                             * This element acts as the link between this result
                             * and the dictionary entry that its value may be the
                             * id of. Having this element makes it possible to associate
                             * the value with its translation without having to 
                             * do look-ups involving the type id and the 
                             * dictionary id at the same time in the xsl.   
                             */
                            root.appendChild(getResultAuxDictionary(doc, "result_dictionary", result.getId(), dictId));
                        }
                    }
                }
                
                /*
                 * create elements for analysis users
                 */
                anaUserMan = anaMan.getAnalysisUserAt(j);
                for (k = 0; k < anaUserMan.count(); k++) {
                    anaUser = anaUserMan.getAnalysisUserAt(k); 
                    root.appendChild(getAnalysisUser(doc, anaUser));
                    userIds.add(anaUser.getSystemUserId());
                    if (anaUser.getActionId() != null)
                        dictIds.add(anaUser.getActionId());                    
                }
                
                anaNoteMan = anaMan.getExternalNoteAt(j);
                if (anaNoteMan.count() > 0) {
                    if (anaNotes == null) {
                        anaNotes = doc.createElement("analysis_external_notes");
                        root.appendChild(anaNotes);
                    }
                    for (k = 0; k < anaNoteMan.count(); k++ ) {
                        note = anaNoteMan.getNoteAt(k);
                        anaNotes.appendChild(getNote(doc, note));
                    }
                }
            }                        
        }
        
        projMan = manager.getProjects();
        projIds = new HashSet<Integer>();
        for (i = 0; i < projMan.count(); i++) {
            sampleProj = projMan.getProjectAt(i);
            root.appendChild(getSampleProject(doc, sampleProj));
            projIds.add(sampleProj.getProjectId());
        }
                
        orgMan = manager.getOrganizations();        
        for (i = 0; i < orgMan.count(); i++) {
            sampleOrg = orgMan.getOrganizationAt(i);
            root.appendChild(getSampleOrganization(doc, sampleOrg));
            orgIds.add(sampleOrg.getOrganizationId());
            dictIds.add(sampleOrg.getTypeId());
        } 
        
        sampleNoteMan = manager.getExternalNote();
        if (sampleNoteMan.count() > 0) {
            sampleNotes = doc.createElement("sample_external_notes");
            for (i = 0; i < sampleNoteMan.count(); i++) {
                note = sampleNoteMan.getNoteAt(i);
                sampleNotes.appendChild(getNote(doc, note));
            }
            root.appendChild(sampleNotes);
        }
        
        if (qaIds.size() > 0) {
            try {
                qaList = qaevent.fetchByIds(qaIds);
                for (QaEventViewDO qa : qaList) {
                    root.appendChild(getQaEvent(doc, qa));
                    if (qa.getTestId() != null)
                        testIds.add(qa.getTestId());
                    dictIds.add(qa.getTypeId());
                }
            } catch (Exception e) {
                log.error("Could not fetch qa events", e);
            }
        }
        
        auxDataMan = manager.getAuxData();
        for (i = 0; i < auxDataMan.count(); i++) {
            auxData = auxDataMan.getAuxDataAt(i);
            root.appendChild(getAuxData(doc, auxData));
            
            dictIds.add(auxData.getTypeId());
            analyteIds.add(auxData.getAnalyteId());
            
            if (auxDictTypeId.equals(auxData.getTypeId()) && auxData.getValue() != null) {
                dictId = Integer.valueOf(auxData.getValue());
                dictIds.add(dictId);
                /* 
                 * This element acts as the link between this aux data and the 
                 * dictionary entry that its value may be the id of. Having this 
                 * element makes it possible to associate the value with its translation
                 * without having to do look-ups involving the type id and the 
                 * dictionary id at the same time in the xsl.   
                 */
                root.appendChild(getResultAuxDictionary(doc, "aux_data_dictionary", auxData.getId(), dictId));
            }
        }
        
        for (Integer id: dictIds) {
            try {
                dict = dictionaryCache.getById(id);
                root.appendChild(getDictionary(doc, dict));
            } catch (Exception e) {
                log.error("Could not fetch dictionary with id: "+ id, e);
            }
        }          
        
        if (methodIds.size() > 0) {
            try {
                methodList = method.fetchByIds(methodIds);
                for (MethodDO mtd : methodList)
                    root.appendChild(getMethod(doc, mtd));
            } catch (Exception e) {
                log.error("Could not fetch methods", e);
            }
        }
        
        if (trailerIds.size() > 0) {
            try {
                trailerList = testTrailer.fetchByIds(trailerIds);
                for (TestTrailerDO trailer : trailerList)
                    root.appendChild(getTestTrailer(doc, trailer));
            } catch (Exception e) {
                log.error("Could not fetch test trailers", e);
            }
        }
        
        for (Integer id: sectionIds) {
            try {
                sect = sectionCache.getById(id);
                root.appendChild(getSection(doc, sect));
                if (sect.getOrganizationId() != null)
                    orgIds.add(sect.getOrganizationId());
            } catch (Exception e) {
                log.error("Could not fetch section with id: "+ id, e);
            }
        }
        
        if (analyteIds.size() > 0) {
            try {
                analyteList = analyte.fetchByIds(analyteIds);
                for (AnalyteViewDO ana : analyteList)
                    root.appendChild(getAnalyte(doc, ana));
            } catch (Exception e) {
                log.error("Could not fetch analytes", e);
            }
        }
        
        if (projIds.size() > 0) {
            try {
                projList = project.fetchByIds(projIds);
                for (ProjectViewDO proj : projList) {
                    root.appendChild(getProject(doc, proj));
                    userIds.add(proj.getOwnerId());
                }
            } catch (Exception e) {
                log.error("Could not fetch projects", e);
            }
        }
        
        for (Integer id: userIds) {
            try {
                user = systemUserCache.getSystemUser(id);
                root.appendChild(getSystemUser(doc, user));
            } catch (Exception e) {
                log.error("Could not fetch system user with id: "+ id, e);
            }
        }
        
        if (orgIds.size() > 0) {
            try {
                orgList = organization.fetchByIds(orgIds);
                for (OrganizationViewDO org : orgList) {
                    root.appendChild(getOrganization(doc, org));
                    root.appendChild(getAddress(doc, org.getAddress()));
                }
            } catch (Exception e) {
                log.error("Could not fetch organizations", e);
            }
        }        
                  
        if (profileIds != null && profileIds.size() > 0) {
            /*
             * add the translation mappings (external terms) for various reference
             * tables
             */            
            if (dictIds.size() > 0)
                createTranslations(ReferenceTable.DICTIONARY, profileIds, dictIds, "dictionary_translations", doc, root, VERSION);
            
            if (testIds.size() > 0)
                createTranslations(ReferenceTable.TEST, profileIds, testIds, "test_translations", doc, root, VERSION);      
            
            if (methodIds.size() > 0)
                createTranslations(ReferenceTable.METHOD, profileIds, methodIds, "method_translations", doc, root, VERSION);
            
            if (analyteIds.size() > 0)
                createTranslations(ReferenceTable.ANALYTE, profileIds, analyteIds, "analyte_translations", doc, root, VERSION);
            
            if (orgIds.size() > 0)
                createTranslations(ReferenceTable.ORGANIZATION, profileIds, orgIds, "organization_translations", doc, root, VERSION);
        }
        
        return doc;
    }

    private Element getProfile(Document document, ExchangeProfileDO exchangeProfile) {
        Element parent;
        DictionaryDO dict;
        
        try {
            dict = dictionaryCache.getById(exchangeProfile.getProfileId());
            parent = document.createElement("profile");
            parent.setTextContent(dict.getEntry());
            
            return parent;
        } catch (Exception e) {
            log.error("Could not fetch dictionary with id: "+ exchangeProfile.getProfileId(), e);           
        }
        
        return null;
    }

    public Element getSystemUser(Document document, SystemUserVO user) {
        Element parent, child;
        
        if (document == null || user == null)
            return null;
        
        parent = document.createElement("system_user");
        parent.setAttribute("id", user.getId().toString());
        
        if (user.getExternalId() != null) {
            child = document.createElement("external_id");
            child.setTextContent(user.getExternalId());
            parent.appendChild(child);
        }
        
        child = document.createElement("login_name");
        child.setTextContent(user.getLoginName());
        parent.appendChild(child);
        
        if (user.getLastName() != null) {
            child = document.createElement("last_name");
            child.setTextContent(user.getLastName());
            parent.appendChild(child);
        }
        
        if (user.getFirstName() != null) {
            child = document.createElement("first_name");
            child.setTextContent(user.getFirstName());
            parent.appendChild(child);
        }
        
        if (user.getInitials() != null) {
            child = document.createElement("initials");
            child.setTextContent(user.getInitials());
            parent.appendChild(child);
        }
        
        return parent;
    }

    public Element getSample(Document document, SampleDO sample) {
        Element parent, child;

        if (document == null || sample == null)
            return null;
        
        parent = document.createElement("sample");

        parent.setAttribute("id", sample.getId().toString());       
        parent.setAttribute("domain", sample.getDomain());
        parent.setAttribute("accession_number", sample.getAccessionNumber().toString());
        parent.setAttribute("revision", sample.getRevision().toString());
        
        if (sample.getOrderId() != null)
            parent.setAttribute("order_id", sample.getOrderId().toString());
        
        parent.setAttribute("entered_date", getDatetimeForSchema(sample.getEnteredDate()));
        parent.setAttribute("received_date", getDatetimeForSchema(sample.getReceivedDate()));
        parent.setAttribute("received_by_id", sample.getReceivedById().toString());
        
        if (sample.getCollectionDate() != null)
            parent.setAttribute("collection_date", getDatetimeForSchema(sample.getCollectionDate()));
        
        parent.setAttribute("status_id", sample.getStatusId().toString());

        if (sample.getCollectionTime() != null)
            parent.setAttribute("collection_time", getDatetimeForSchema(sample.getCollectionTime()));        
        
        if (sample.getPackageId() != null)
            parent.setAttribute("package_id", sample.getPackageId().toString());
        
        if (sample.getClientReference() != null) {
            child = document.createElement("client_reference");
            child.setTextContent(sample.getClientReference());
            parent.appendChild(child);
        }
        
        if (sample.getReleasedDate() != null)
            parent.setAttribute("released_date", getDatetimeForSchema(sample.getReleasedDate()));
        
        return parent;
    }
    
    public Element getSampleEnviromental(Document document, SampleEnvironmentalDO environmental) {
        Element parent, child;
        
        if (document == null || environmental == null)
            return null;
        
        parent = document.createElement("sample_environmental");
        parent.setAttribute("id", environmental.getId().toString());
        parent.setAttribute("sample_id", environmental.getSampleId().toString());
        if (environmental.getIsHazardous() != null)
            parent.setAttribute("is_hazardous", environmental.getIsHazardous());
        if (environmental.getPriority() != null)
            parent.setAttribute("priority", environmental.getPriority().toString());        
        if (environmental.getDescription() != null) {
            child = document.createElement("description");
            child.setTextContent(environmental.getDescription());
            parent.appendChild(child);
        }
        if (environmental.getCollector() != null) {
            child = document.createElement("collector");
            child.setTextContent(environmental.getCollector());
            parent.appendChild(child);
        }
        if (environmental.getCollectorPhone() != null) {
            child = document.createElement("collector_phone");
            child.setTextContent(environmental.getCollectorPhone());
            parent.appendChild(child);
        }
        if (environmental.getLocation() != null) {
            child = document.createElement("location");
            child.setTextContent(environmental.getLocation());
            parent.appendChild(child);
        }
        if (environmental.getLocationAddress().getId() != null)
            parent.setAttribute("location_address_id", environmental.getLocationAddress().getId().toString());        
        
        return parent;
    }
    
    public Element getSamplePrivateWell(Document document, SamplePrivateWellViewDO privateWell) {
        Element parent, child;
        
        if (document == null || privateWell == null)
            return null;
        
        parent = document.createElement("sample_private_well");
        parent.setAttribute("id", privateWell.getId().toString());
        parent.setAttribute("sample_id", privateWell.getSampleId().toString());
        if (privateWell.getOrganizationId() != null)
            parent.setAttribute("organization_id", privateWell.getOrganizationId().toString());
        if (privateWell.getReportToName() != null) {
            child = document.createElement("report_to_name");
            child.setTextContent(privateWell.getReportToName());
            parent.appendChild(child);
        }        
        if (privateWell.getReportToAttention() != null) {
            child = document.createElement("report_to_attention");
            child.setTextContent(privateWell.getReportToAttention());
            parent.appendChild(child);
        }
        if (privateWell.getReportToAddress().getId() != null)
            parent.setAttribute("report_to_address_id", privateWell.getReportToAddress().getId().toString());        
        if (privateWell.getLocation() != null) {
            child = document.createElement("location");
            child.setTextContent(privateWell.getLocation());
            parent.appendChild(child);
        }
        if (privateWell.getLocationAddress().getId() != null)
            parent.setAttribute("location_address_id", privateWell.getLocationAddress().getId().toString());        
        if (privateWell.getOwner() != null) {
            child = document.createElement("owner");
            child.setTextContent(privateWell.getOwner());
            parent.appendChild(child);
        }
        if (privateWell.getCollector() != null) {
            child = document.createElement("collector");
            child.setTextContent(privateWell.getCollector());
            parent.appendChild(child);
        }
        if (privateWell.getWellNumber() != null) {
            child = document.createElement("well_number");
            child.setTextContent(privateWell.getWellNumber().toString());
            parent.appendChild(child);
        }
        
        return parent;
    }
    
    public Element getSampleSDWIS(Document document, SampleSDWISDO sdwis) {
        Element parent, child;
        
        if (document == null || sdwis == null)
            return null;
        
        parent = document.createElement("sample_sdwis");
        parent.setAttribute("id", sdwis.getId().toString());
        parent.setAttribute("sample_id", sdwis.getSampleId().toString());
        parent.setAttribute("pws_id", sdwis.getPwsId().toString());
        
        if (sdwis.getStateLabId() != null)
            parent.setAttribute("state_lab_id", sdwis.getStateLabId().toString());
        
        if (sdwis.getFacilityId() != null) {
            child = document.createElement("facility_id");
            child.setTextContent(sdwis.getFacilityId());
            parent.appendChild(child);
        }        
        
        parent.setAttribute("sample_type_id", sdwis.getSampleTypeId().toString());        
        parent.setAttribute("sample_category_id", sdwis.getSampleCategoryId().toString());        
        
        child = document.createElement("sample_point_id");
        child.setTextContent(sdwis.getSamplePointId());
        parent.appendChild(child);
        
        if (sdwis.getLocation() != null) {
            child = document.createElement("location");
            child.setTextContent(sdwis.getLocation());
            parent.appendChild(child);
        }
        if (sdwis.getCollector() != null) {
            child = document.createElement("collector");
            child.setTextContent(sdwis.getCollector());
            parent.appendChild(child);
        }
        
        return parent;
    }
    
    public Element getPWS(Document document, PWSDO pws) {
        Element parent, child;
        
        if (document == null || pws == null)
            return null;
        
        parent = document.createElement("pws");
        parent.setAttribute("id", pws.getId().toString());
        parent.setAttribute("tinwsys_is_number", pws.getTinwsysIsNumber().toString());
        
        child = document.createElement("number0");
        child.setTextContent(pws.getNumber0());
        parent.appendChild(child);
        
        if (pws.getAlternateStNum() != null) {
            child = document.createElement("alternate_st_num");
            child.setTextContent(pws.getAlternateStNum());
            parent.appendChild(child);
        }        
        if (pws.getName() != null) {
            child = document.createElement("name");
            child.setTextContent(pws.getName());
            parent.appendChild(child);
        }
        if (pws.getActivityStatusCd() != null) {
            child = document.createElement("activity_status_cd");
            child.setTextContent(pws.getActivityStatusCd());
            parent.appendChild(child);
        }
        if (pws.getDPrinCitySvdNm() != null) {
            child = document.createElement("d_prin_city_svd_nm");
            child.setTextContent(pws.getDPrinCitySvdNm());
            parent.appendChild(child);
        }
        if (pws.getDPrinCntySvdNm() != null) {
            child = document.createElement("d_prin_cnty_svd_nm");
            child.setTextContent(pws.getDPrinCntySvdNm());
            parent.appendChild(child);
        }
        if (pws.getDPopulationCount() != null) {
            child = document.createElement("d_population_count");
            child.setTextContent(pws.getDPopulationCount().toString());
            parent.appendChild(child);
        }        
        if (pws.getDPwsStTypeCd() != null) {
            child = document.createElement("d_pws_st_type_cd");
            child.setTextContent(pws.getDPwsStTypeCd());
            parent.appendChild(child);
        }        
        if (pws.getActivityRsnTxt() != null) {
            child = document.createElement("activity_rsn_txt");
            child.setTextContent(pws.getActivityRsnTxt());
            parent.appendChild(child);
        }
        if (pws.getActivityStatusCd() != null) {
            child = document.createElement("activity_status_cd");
            child.setTextContent(pws.getActivityStatusCd());
            parent.appendChild(child);
        }
        
        return parent;
    }
    
    public Element getSampleItem(Document document, SampleItemViewDO sampleItem) {
        Element parent, child;        
        
        if (document == null || sampleItem == null)
            return null;
        
        parent = document.createElement("sample_item");
        parent.setAttribute("id", sampleItem.getId().toString());
        parent.setAttribute("sample_id", sampleItem.getSampleId().toString());
        if (sampleItem.getSampleItemId() != null)
            parent.setAttribute("sample_item_id", sampleItem.getSampleItemId().toString());
        parent.setAttribute("item_sequence", sampleItem.getItemSequence().toString());
        parent.setAttribute("type_of_sample_id", sampleItem.getTypeOfSampleId().toString());
                
        if (sampleItem.getSourceOfSampleId() != null) 
            parent.setAttribute("source_of_sample_id", sampleItem.getSourceOfSampleId().toString());        
        
        if (sampleItem.getSourceOther() != null) {
            child = document.createElement("source_other");
            child.setTextContent(sampleItem.getSourceOther());
        }
        
        if (sampleItem.getContainerId() != null) 
            parent.setAttribute("container_id", sampleItem.getContainerId().toString());
        
        if (sampleItem.getQuantity() != null) 
            parent.setAttribute("quantity", sampleItem.getQuantity().toString());
        
        if (sampleItem.getUnitOfMeasureId() != null) 
            parent.setAttribute("unit_of_measure_id", sampleItem.getUnitOfMeasureId().toString());
        
        return parent;
    }
    
    public Element getAnalysis(Document document, AnalysisViewDO analysis) {
        Element parent;
        
        if (document == null || analysis == null) 
            return null;
        
        parent = document.createElement("analysis");
        parent.setAttribute("id", analysis.getId().toString());
        parent.setAttribute("sample_item_id", analysis.getSampleItemId().toString());
        parent.setAttribute("revision", analysis.getRevision().toString());
        parent.setAttribute("test_id", analysis.getTestId().toString());        
        parent.setAttribute("section_id", analysis.getSectionId().toString());
        
        if (analysis.getPreAnalysisId() != null)
            parent.setAttribute("pre_analysis_id", analysis.getPreAnalysisId().toString());  
        
        if (analysis.getParentAnalysisId() != null)
            parent.setAttribute("parent_analysis_id", analysis.getParentAnalysisId().toString());
        
        parent.setAttribute("is_reportable", analysis.getIsReportable());
        
        if (analysis.getUnitOfMeasureId() != null) 
            parent.setAttribute("unit_of_measure_id", analysis.getUnitOfMeasureId().toString());
        
        parent.setAttribute("status_id", analysis.getStatusId().toString());
        
        if (analysis.getAvailableDate() != null)
            parent.setAttribute("available_date", getDatetimeForSchema(analysis.getAvailableDate()));
        
        if (analysis.getStartedDate() != null)
            parent.setAttribute("started_date", getDatetimeForSchema(analysis.getStartedDate()));
        
        if (analysis.getCompletedDate() != null)
            parent.setAttribute("completed_date", getDatetimeForSchema(analysis.getCompletedDate()));
        
        if (analysis.getReleasedDate() != null)
            parent.setAttribute("released_date", getDatetimeForSchema(analysis.getReleasedDate()));
        
        if (analysis.getPrintedDate() != null)
            parent.setAttribute("printed_date", getDatetimeForSchema(analysis.getPrintedDate()));
        
        return parent;
    }
    
    public Element getTest(Document document, TestViewDO test) {
        Element parent, child;
        
        if (document == null || test == null) 
            return null;
        
        parent = document.createElement("test");
        parent.setAttribute("id", test.getId().toString());
        
        child = document.createElement("name");
        child.setTextContent(test.getName());
        parent.appendChild(child);
        
        child = document.createElement("description");
        child.setTextContent(test.getDescription());
        parent.appendChild(child);
        
        child = document.createElement("reporting_description");
        child.setTextContent(test.getReportingDescription());
        parent.appendChild(child);
        
        parent.setAttribute("method_id", test.getMethodId().toString());
        parent.setAttribute("is_active", test.getIsActive());  
        parent.setAttribute("active_begin", getDatetimeForSchema(test.getActiveBegin()));
        parent.setAttribute("active_end", getDatetimeForSchema(test.getActiveEnd()));
        parent.setAttribute("is_reportable", test.getIsReportable());
        parent.setAttribute("time_transit", test.getTimeTransit().toString());
        parent.setAttribute("time_holding", test.getTimeHolding().toString());
        parent.setAttribute("time_ta_average", test.getTimeTaAverage().toString());
        parent.setAttribute("time_ta_warning", test.getTimeTaWarning().toString());
        parent.setAttribute("time_ta_max", test.getTimeTaMax().toString());
        
        if (test.getTestTrailerId() != null)
            parent.setAttribute("test_trailer_id", test.getTestTrailerId().toString());
        
        if (test.getTestFormatId() != null)
            parent.setAttribute("test_format_id", test.getTestFormatId().toString());  
        
        if (test.getRevisionMethodId() != null)
            parent.setAttribute("revision_method_id", test.getRevisionMethodId().toString());
        
        if (test.getReportingMethodId() != null)
            parent.setAttribute("reporting_method_id", test.getReportingMethodId().toString());
        
        if (test.getSortingMethodId() != null)
            parent.setAttribute("sorting_method_id", test.getSortingMethodId().toString());
        
        if (test.getReportingSequence() != null)
            parent.setAttribute("reporting_sequence", test.getReportingSequence().toString());
        
        return parent;
    }
    
    public Element getMethod(Document document, MethodDO method) {
        Element parent, child;
        
        if (document == null || method == null) 
            return null;
        
        parent = document.createElement("method");
        parent.setAttribute("id", method.getId().toString());
        
        child = document.createElement("name");
        child.setTextContent(method.getName());
        parent.appendChild(child);
        
        child = document.createElement("description");
        child.setTextContent(method.getDescription());
        parent.appendChild(child);
        
        child = document.createElement("reporting_description");
        child.setTextContent(method.getReportingDescription());
        parent.appendChild(child);
        
        parent.setAttribute("is_active", method.getIsActive());
        parent.setAttribute("active_begin", getDatetimeForSchema(method.getActiveBegin()));
        parent.setAttribute("active_end", getDatetimeForSchema(method.getActiveEnd()));
        
        return parent;
    }
    
    public Element getSection(Document document, SectionDO method) {
        Element parent, child;
        
        if (document == null || method == null) 
            return null;
        
        parent = document.createElement("section");
        parent.setAttribute("id", method.getId().toString());
        
        child = document.createElement("name");
        child.setTextContent(method.getName());
        parent.appendChild(child);
        
        child = document.createElement("description");
        child.setTextContent(method.getDescription());
        parent.appendChild(child);
        
        if (method.getParentSectionId() != null)
            parent.setAttribute("parent_section_id", method.getParentSectionId().toString());
        
        parent.setAttribute("is_external", method.getIsExternal());
        
        if (method.getOrganizationId() != null)
            parent.setAttribute("organization_id", method.getOrganizationId().toString());
        
        return parent;
    }
    
    private Node getTestTrailer(Document document, TestTrailerDO trailer) {
        Element parent, child;
        
        if (document == null || trailer == null) 
            return null;
        
        parent = document.createElement("test_trailer");
        parent.setAttribute("id", trailer.getId().toString());
        
        child = document.createElement("name");
        child.setTextContent(trailer.getName());
        parent.appendChild(child);
        
        child = document.createElement("description");
        child.setTextContent(trailer.getDescription());
        parent.appendChild(child);
        
        child = document.createElement("text");
        child.setTextContent(trailer.getText());
        parent.appendChild(child);
        
        return parent;
    }
    
    public Element getAnalysisUser(Document document, AnalysisUserViewDO analysisUser) {
        Element parent;
        
        if (document == null || analysisUser == null) 
            return null;
        
        parent = document.createElement("analysis_user");
        parent.setAttribute("id", analysisUser.getId().toString());
        parent.setAttribute("analysis_id", analysisUser.getAnalysisId().toString());
        parent.setAttribute("system_user_id", analysisUser.getSystemUserId().toString());
        if (analysisUser.getActionId() != null)
            parent.setAttribute("action_id", analysisUser.getActionId().toString());        
        
        return parent;
    }
    
    public Element getSampleProject(Document document, SampleProjectViewDO sampleProject) { 
        Element parent;
        
        if (document == null || sampleProject == null) 
            return null;
        
        parent = document.createElement("sample_project");
        parent.setAttribute("id", sampleProject.getId().toString());
        parent.setAttribute("sample_id", sampleProject.getSampleId().toString());
        parent.setAttribute("project_id", sampleProject.getProjectId().toString());
        parent.setAttribute("is_permanent", sampleProject.getIsPermanent());
        
        return parent;
    }
    
    public Element getProject(Document document, ProjectViewDO project) {
        Element parent, child;
        
        if (document == null || project == null) 
            return null;
        
        parent = document.createElement("project");
        parent.setAttribute("id", project.getId().toString());
        
        child = document.createElement("name");
        child.setTextContent(project.getName());
        parent.appendChild(child);
        
        child = document.createElement("description");
        child.setTextContent(project.getDescription());
        parent.appendChild(child);
        
        parent.setAttribute("started_date", getDatetimeForSchema(project.getStartedDate()));
        parent.setAttribute("completed_date", getDatetimeForSchema(project.getCompletedDate()));
        parent.setAttribute("is_active", project.getIsActive());
        
        if (project.getReferenceTo() != null) {
            child = document.createElement("reference_to");
            child.setTextContent(project.getReferenceTo());
            parent.appendChild(child);
        }
        
        if (project.getOwnerId() != null) 
            parent.setAttribute("owner_id", project.getOwnerId().toString());                
        
        return parent;
    }
    
    public Element getSampleOrganization(Document document, SampleOrganizationViewDO sampleOrganization) {
        Element parent, child;

        if (document == null || sampleOrganization == null)
            return null;
        
        parent = document.createElement("sample_organization");

        parent.setAttribute("id", sampleOrganization.getId().toString());
        parent.setAttribute("sample_id", sampleOrganization.getSampleId().toString());
        parent.setAttribute("organization_id", sampleOrganization.getOrganizationId().toString());
        if (sampleOrganization.getOrganizationAttention() != null) {
            child = document.createElement("organization_attention");
            child.setTextContent(sampleOrganization.getOrganizationAttention());
            parent.appendChild(child);
        }
        parent.setAttribute("type_id", sampleOrganization.getTypeId().toString());
        
        return parent;
    }
    
    public Element getOrganization(Document document, OrganizationDO organization) {
        Element parent, child;

        if (document == null || organization == null)
            return null;
        
        parent = document.createElement("organization");

        parent.setAttribute("id", organization.getId().toString());
        if (organization.getParentOrganizationId() != null)
            parent.setAttribute("parent_organization_id", organization.getParentOrganizationId().toString());
        
        child = document.createElement("name");
        child.setTextContent(organization.getName());
        parent.appendChild(child);
        
        parent.setAttribute("is_active", organization.getIsActive());
        parent.setAttribute("address_id", organization.getAddress().getId().toString());
        
        return parent;
    }
    
    public Element getDictionary(Document document, DictionaryDO dictionary) {
        Element parent, child;
        
        if (document == null || dictionary == null) 
            return null;
        
        parent = document.createElement("dictionary");
        parent.setAttribute("id", dictionary.getId().toString());
        
        if (dictionary.getSystemName() != null) {
            child = document.createElement("system_name");
            child.setTextContent(dictionary.getSystemName());
            parent.appendChild(child);
        }
        
        child = document.createElement("entry");
        child.setTextContent(dictionary.getEntry());
        parent.appendChild(child);
        
        return parent;
    }
    
    public Element getAddress(Document document, AddressDO address) {
        Element parent, child;
        
        if (document == null || address == null) 
            return null;
        
        parent = document.createElement("address");
        
        parent.setAttribute("id", address.getId().toString());        
        if (address.getMultipleUnit() != null) {
            child = document.createElement("multiple_unit");
            child.setTextContent(address.getMultipleUnit());
            parent.appendChild(child);
        }
        if (address.getStreetAddress() != null) {
            child = document.createElement("street_address");
            child.setTextContent(address.getStreetAddress());
            parent.appendChild(child);
        }
        if (address.getCity() != null) {
            child = document.createElement("city");
            child.setTextContent(address.getCity());
            parent.appendChild(child);
        }
        if (address.getState() != null) {
            child = document.createElement("state");
            child.setTextContent(address.getState());
            parent.appendChild(child);
        }
        if (address.getZipCode() != null) {
            child = document.createElement("zip_code");
            child.setTextContent(address.getZipCode());
            parent.appendChild(child);
        }
        if (address.getWorkPhone() != null) {
            child = document.createElement("work_phone");
            child.setTextContent(address.getWorkPhone());
            parent.appendChild(child);
        }
        if (address.getHomePhone() != null) { 
            child = document.createElement("home_phone");
            child.setTextContent(address.getHomePhone());
            parent.appendChild(child);
        }
        if (address.getCellPhone() != null) { 
            child = document.createElement("cell_phone");
            child.setTextContent(address.getCellPhone());
            parent.appendChild(child);
        }
        if (address.getFaxPhone() != null) {
            child = document.createElement("fax_phone");
            child.setTextContent(address.getFaxPhone());
            parent.appendChild(child);
        }
        if (address.getEmail() != null) {
            child = document.createElement("email");
            child.setTextContent(address.getEmail());
            parent.appendChild(child);
        }
        if (address.getCountry() != null) {
            child = document.createElement("country");
            child.setTextContent(address.getCountry());
            parent.appendChild(child);
        }
        return parent;
    }
    
    public Element getSampleQaEvent(Document document, SampleQaEventViewDO sampleQaEvent) {
        Element parent;

        if (document == null || sampleQaEvent == null)
            return null;
        
        parent = document.createElement("sample_qaevent");
        
        parent.setAttribute("id", sampleQaEvent.getId().toString());
        parent.setAttribute("sample_id", sampleQaEvent.getSampleId().toString()); 
        parent.setAttribute("qaevent_id", sampleQaEvent.getQaEventId().toString()); 
        parent.setAttribute("type_id", sampleQaEvent.getTypeId().toString());
        parent.setAttribute("is_billable", sampleQaEvent.getIsBillable());
        
        return parent;
    }
    
    public Element getAuxData(Document document, AuxDataViewDO auxData) {
        Element parent, child;

        if (document == null || auxData == null)
            return null;
        
        parent = document.createElement("aux_data");
        parent.setAttribute("id", auxData.getId().toString());
        parent.setAttribute("sort_order", auxData.getSortOrder().toString());
        parent.setAttribute("aux_field_id", auxData.getAuxFieldId().toString());
        parent.setAttribute("reference_id", auxData.getReferenceId().toString());
        parent.setAttribute("reference_table_id", auxData.getReferenceTableId().toString());
        parent.setAttribute("is_reportable", auxData.getIsReportable().toString());
        parent.setAttribute("analyte_id", auxData.getAnalyteId().toString());
        parent.setAttribute("type_id", auxData.getTypeId().toString());
        
        if (auxData.getValue() != null) {
            child = document.createElement("value");
            child.setTextContent(auxData.getValue());
            parent.appendChild(child);
        }
        
        return parent;
    }
    
    public Element getAnalysisQaEvent(Document document, AnalysisQaEventViewDO sampleQaEvent) {
        Element parent;

        if (document == null || sampleQaEvent == null)
            return null;
        
        parent = document.createElement("analysis_qaevent");
        
        parent.setAttribute("id", sampleQaEvent.getId().toString());
        parent.setAttribute("analysis_id", sampleQaEvent.getAnalysisId().toString()); 
        parent.setAttribute("qaevent_id", sampleQaEvent.getQaEventId().toString()); 
        parent.setAttribute("type_id", sampleQaEvent.getTypeId().toString());
        parent.setAttribute("is_billable", sampleQaEvent.getIsBillable());
        
        return parent;
    }
    
    public Element getQaEvent(Document document, QaEventViewDO qaEvent) {
        Element parent, child;

        if (document == null || qaEvent == null)
            return null;
        
        parent = document.createElement("qaevent");
        
        parent.setAttribute("id", qaEvent.getId().toString());
        
        child = document.createElement("name");
        child.setTextContent(qaEvent.getName());
        parent.appendChild(child);
        
        if (qaEvent.getDescription() != null) {
            child = document.createElement("description");
            child.setTextContent(qaEvent.getDescription());
            parent.appendChild(child);
        }
        
        if (qaEvent.getTestId() != null)
            parent.setAttribute("test_id", qaEvent.getTestId().toString());
        parent.setAttribute("type_id", qaEvent.getTypeId().toString());
        parent.setAttribute("is_billable", qaEvent.getIsBillable());
        if (qaEvent.getReportingSequence() != null)
            parent.setAttribute("reporting_sequence", qaEvent.getReportingSequence().toString());
        
        child = document.createElement("reporting_text");
        child.setTextContent(qaEvent.getReportingText());
        parent.appendChild(child);
        
        return parent;
    }
    
    public Element getAnalyte(Document document, AnalyteViewDO analyte) {
        Element parent, child;

        if (document == null || analyte == null)
            return null;
        
        parent = document.createElement("analyte");
        
        parent.setAttribute("id", analyte.getId().toString());
        
        child = document.createElement("name");
        child.setTextContent(analyte.getName());
        parent.appendChild(child);
        
        parent.setAttribute("is_active", analyte.getIsActive());
        if (analyte.getParentAnalyteId() != null)        
            parent.setAttribute("parent_analyte_id", analyte.getParentAnalyteId().toString());
        
        if (analyte.getExternalId() != null) {
            child = document.createElement("external_id");
            child.setTextContent(analyte.getExternalId());
            parent.appendChild(child);
        }        
        
        return parent;
    }
    
    public Element getResult(Document document, ResultViewDO result, 
                             boolean showValue) {
        Element parent, child;

        if (document == null || result == null)
            return null;
        
        parent = document.createElement("result");
        
        parent.setAttribute("id", result.getId().toString());
        parent.setAttribute("analysis_id", result.getAnalysisId().toString());
        parent.setAttribute("test_analyte_id", result.getTestAnalyteId().toString());
        if (result.getTestResultId() != null)
            parent.setAttribute("test_result_id", result.getTestResultId().toString());
        parent.setAttribute("is_column", result.getIsColumn().toString());
        parent.setAttribute("sort_order", result.getSortOrder().toString());
        parent.setAttribute("is_reportable", result.getIsReportable().toString());
        parent.setAttribute("analyte_id", result.getAnalyteId().toString());
        
        if (result.getTypeId() != null)
            parent.setAttribute("type_id", result.getTypeId().toString());
        
        /*
         * the values for those results are not shown, the analysis for which, has
         * qa event(s) of type result override or which belongs to a sample
         * that has such qa event(s) 
         */
        if (showValue && result.getValue() != null) {
            child = document.createElement("value");
            child.setTextContent(result.getValue());
            parent.appendChild(child);
        }
        
        return parent;
    }
    
    public Element getNote(Document document, NoteViewDO note) {
        Element parent, child;

        if (document == null || note == null)
            return null;
        
        parent = document.createElement("note");
        parent.setAttribute("id", note.getId().toString());
        parent.setAttribute("reference_id", note.getReferenceId().toString());
        parent.setAttribute("reference_table_id", note.getReferenceTableId().toString());
        parent.setAttribute("timestamp", getDatetimeForSchema(note.getTimestamp()));
        parent.setAttribute("is_external", note.getIsExternal());
        parent.setAttribute("system_user_id", note.getSystemUserId().toString());
        
        if (note.getSubject() != null) {
            child = document.createElement("subject");
            child.setTextContent(note.getSubject());
            parent.appendChild(child);
        }
        
        if (note.getText() != null) {
            child = document.createElement("text");
            child.setTextContent(note.getText());
            parent.appendChild(child);
        }
        
        return parent;
    }        
    
    public ArrayList<Element> getExternalTerms(Integer refTableId, HashSet<Integer> refIds, 
                                           ArrayList<Integer> profileIds, Document document,
                                           String version) throws Exception {
        ArrayList<Element> elements;
        ArrayList<ExchangeExternalTermViewDO> externalTerms;
        
        if (document == null || refTableId == null || refIds == null || profileIds == null)
            return null;

        try {
            externalTerms = exchangeExternalTerm.fetchByReferenceTableIdReferenceIdsProfileIds(refTableId, refIds, profileIds);
            elements = new ArrayList<Element>();
            
            for (ExchangeExternalTermViewDO extTerm : externalTerms)
                elements.add(getExternalTerm(document, extTerm, version));                       
            
            return elements;
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    public Element getExternalTerm(Document document, ExchangeExternalTermViewDO externalTerm, String version) {
        Element parent, child;
        DictionaryDO dict;
        
        if (document == null || externalTerm == null)
            return null;
        
        parent = document.createElement("translation");
        parent.setAttribute("id", externalTerm.getId().toString());
        parent.setAttribute("reference_id", externalTerm.getExchangeLocalTermReferenceId().toString());
        
        try {
            dict = dictionaryCache.getById(externalTerm.getProfileId());
            child = document.createElement("profile");
            child.setTextContent(dict.getEntry());
            parent.appendChild(child);
        } catch (Exception e) {
            log.error("Could not fetch dictionary with id: "+ externalTerm.getProfileId(), e);
        }
        
        child = document.createElement("code");
        child.setTextContent(externalTerm.getExternalTerm());
        parent.appendChild(child);
        
        child = document.createElement("description");
        child.setTextContent(externalTerm.getExternalDescription());
        parent.appendChild(child);
        
        child = document.createElement("coding_system");
        child.setTextContent(externalTerm.getExternalCodingSystem());
        parent.appendChild(child);
        
        child = document.createElement("version");
        child.setTextContent(version);
        parent.appendChild(child);
        
        return parent;
    }
    
    private Element getResultAuxDictionary(Document document, String name,
                                           Integer resultId, Integer dictionaryId) {
        Element parent;
        
        if (document == null || resultId == null || dictionaryId == null)
            return null;
        
        parent = document.createElement(name);
        parent.setAttribute("id", resultId.toString());
        parent.setAttribute("dictionary_id", dictionaryId.toString());
        
        return parent;
    }
    
    private void createTranslations(int referenceTable, ArrayList<Integer> profileIds,
                                    HashSet<Integer> referenceIds, String nodeName,
                                    Document doc, Element root, String version) throws Exception {
        Element translations;
        ArrayList<Element> externalTerms;
        
        externalTerms = getExternalTerms(referenceTable, referenceIds, profileIds, doc, version);
        
        if (externalTerms != null) {
            translations = doc.createElement(nodeName);
            
            for (Element e : externalTerms) 
                translations.appendChild(e);
            
            root.appendChild(translations);
        }
    }
    
    private Element getHeader(Document document, ExchangeCriteriaViewDO criteria) {
        Element parent, child;
        DictionaryDO dict;

        if (document == null || criteria == null)
            return null;
        
        parent = document.createElement("header");
        
        child = document.createElement("name");
        child.setTextContent(criteria.getName());
        parent.appendChild(child);
        
        try {
            dict = dictionaryCache.getById(criteria.getEnvironmentId());
            child = document.createElement("environment");
            child.setTextContent(dict.getEntry());
            parent.appendChild(child);
        } catch (Exception e) {
            log.error("Could not fetch dictionary with id: "+ criteria.getEnvironmentId(), e);
        }
        
        parent.setAttribute("include_all_analyses", criteria.getIsAllAnalysesIncluded());
        
        return parent;
    }
    
    private String getDatetimeForSchema(Datetime dt) {
        String ytod, htos;
        Date d;
        
        if (dt == null)
            return null;
        
        d = dt.getDate();
        
        ytod = null;        
        if (dt.getStartCode() < Datetime.DAY)         
            ytod = dateFormat.format(d);        
        
        htos = null;
        if (dt.getEndCode() > Datetime.DAY)
            htos = timeFormat.format(d);
                
        return DataBaseUtil.concatWithSeparator(ytod, "T", htos);
    }
}