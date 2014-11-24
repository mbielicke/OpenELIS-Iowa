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

import static org.openelis.manager.OrderManager1Accessor.*;
import static org.openelis.manager.SampleManager1Accessor.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.EOrderBodyDO;
import org.openelis.domain.ExchangeExternalTermViewDO;
import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrderTestAnalyteViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleClinicalViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.manager.OrderManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.PatientMeta;
import org.openelis.meta.ProviderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.util.XMLUtil;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;
import org.openelis.utils.ReportUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used for loading the data from a Send-out or electronic order
 * into a SampleManager
 */

@Stateless
@SecurityDomain("openelis")
public class SampleManagerOrderHelperBean {

    @EJB
    private DictionaryCacheBean       dictionaryCache;

    @EJB
    private EOrderBodyBean            eorderBody;
    
    @EJB
    private ExchangeExternalTermBean  exchangeExternalTerm;

    @EJB
    private OrganizationBean          organization;
    
    @EJB
    private OrganizationParameterBean organizationParameter;
    
    @EJB
    private PanelBean                 panel;

    @EJB
    private PatientBean               patient;

    @EJB
    private ProviderBean              provider;
    
    @EJB
    private TestManagerBean           testManager;

    @EJB
    private UserCacheBean             userCache;

    @EJB
    private SampleManager1Bean        sampleManager1;

    @EJB
    private AuxDataHelperBean         auxDataHelper;

    @EJB
    private OrderManager1Bean         orderManager1;

    private static final Logger       log             = Logger.getLogger("openelis");

    public static final String        ORG_HOLD_SAMPLE = "org_hold_sample";

    /**
     * Loads and merges the data in the SampleManager with the corresponding
     * data in the send-out order specified by its id and adds any errors
     * related to invalid data encountered during the process to the
     * ValidationErrorsList
     */
    public SampleTestReturnVO importSendoutOrder(SampleManager1 sm, Integer orderId,
                                                 ValidationErrorsList e) throws Exception {
        Integer accession;
        SampleDO data;
        OrderManager1 om;
        SampleTestReturnVO ret;

        data = getSample(sm);
        /*
         * for display
         */
        accession = data.getAccessionNumber();
        if (accession == null)
            accession = 0;

        om = orderManager1.fetchById(orderId,
                                     OrderManager1.Load.SAMPLE_DATA,
                                     OrderManager1.Load.ORGANIZATION);
        if (om == null)
            throw new FormErrorException(Messages.get().sample_orderIdInvalidException(accession,
                                                                                       orderId));        
        else if ( !Constants.order().SEND_OUT.equals(om.getOrder().getType()))
            throw new FormErrorException(Messages.get().sample_orderIdInvalidException(accession,
                                                                                       orderId));

        data.setOrderId(orderId);

        copyOrganizations(sm, om, e);
        copySampleItems(sm, om, e);
        ret = copyTests(sm, om, e);
        copyNotes(sm, om);

        if (getAuxilliary(om) != null)
            auxDataHelper.copyToSample(sm, getAuxilliary(om), e);

        return ret;
    }

    /**
     * Loads and merges the data in the SampleManager with the corresponding
     * data in the electronic order specified by its id and adds any errors
     * related to invalid data encountered during the process to the
     * ValidationErrorsList.  If there are any discrepancies between existing data
     * in the SampleManager and data in the electronic order, warnings are added
     * to the ValidationErrorsList.
     */
    public SampleTestReturnVO importEOrder(SampleManager1 sm, Integer orderId,
                                           ValidationErrorsList e) throws Exception {
        HashMap<String, ArrayList<OrderResult>> resultMap;
        ArrayList<ExchangeExternalTermViewDO> externalTerms, mapExternalTerms;
        HashMap<String, String> testMap;
        Document document;
        Element eorderBodyElem;
        EOrderBodyDO eobDO;
        HashMap<String, ArrayList<ExchangeExternalTermViewDO>> externalTermMap;
        HashMap<String, String> dataMap;
        Integer accession;
        SampleDO data;
        SampleTestReturnVO ret;

        ret = new SampleTestReturnVO();
        ret.setManager(sm);
        ret.setErrors(e);

        data = getSample(sm);
        /*
         * for display
         */
        accession = data.getAccessionNumber();
        if (accession == null)
            accession = 0;

        eobDO = eorderBody.fetchByEOrderId(orderId);        
        data.setOrderId(orderId);

        dataMap = new HashMap<String, String>();
        testMap = new HashMap<String, String>();
        resultMap = new HashMap<String, ArrayList<OrderResult>>();
        externalTermMap = new HashMap<String, ArrayList<ExchangeExternalTermViewDO>>();
        document = XMLUtil.parse(eobDO.getXml());
        eorderBodyElem = (Element) document.getDocumentElement();
        if (!eorderBodyElem.hasChildNodes())
            throw new Exception("EOrder has no sample data");
        
        loadDataMapFromXml(eorderBodyElem, dataMap, testMap, resultMap, externalTermMap);
        
        externalTerms = exchangeExternalTerm.fetchByExternalTerms(externalTermMap.keySet());
        for (ExchangeExternalTermViewDO eetVDO : externalTerms) {
            mapExternalTerms = externalTermMap.get(eetVDO.getExternalTerm());
            mapExternalTerms.add(eetVDO);
        }
        
        copySample(sm, dataMap, e);
        copyPatient(sm, dataMap, externalTermMap, e);
        copyOrganization(sm, dataMap, externalTermMap, e);
        copyProvider(sm, dataMap, e);
        ret = copyTests(ret, dataMap, testMap, resultMap, externalTermMap);

        return ret;
    }

    /**
     * Loads sample items in the SampleManager from the containers in the order.
     * Resets sequences in the items to match the containers. Also resets the
     * next item sequence in the sample to be greater than the sequence of the
     * last item.
     */
    private void copySampleItems(SampleManager1 sm, OrderManager1 om, ValidationErrorsList e) throws Exception {
        int i;
        Integer accession;
        OrderContainerDO oc;
        DictionaryDO dict;
        SampleItemViewDO item;
        ArrayList<SampleItemViewDO> items;
        ArrayList<OrderContainerDO> ocs;

        ocs = getContainers(om);
        if (ocs == null)
            return;

        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;

        /*
         * if a sample item is found at the sequence specified in a container
         * then it's filled from the container otherwise a new sample item is
         * created and filled
         */
        items = getItems(sm);
        for (i = 0; i < ocs.size(); i++ ) {
            if (items != null && i < items.size()) {
                item = items.get(i);
            } else {
                item = new SampleItemViewDO();
                item.setId(sm.getNextUID());

                item.setSampleId(getSample(sm).getId());
                addItem(sm, item);
            }

            if ( !DataBaseUtil.isSame(item.getItemSequence(), i))
                item.setItemSequence(i);

            oc = ocs.get(i);
            if (oc.getContainerId() != null &&
                !DataBaseUtil.isSame(item.getContainerId(), oc.getContainerId())) {
                try {
                    dict = dictionaryCache.getById(oc.getContainerId());
                    /*
                     * set the container only if it's active
                     */
                    if ("Y".equals(dict.getIsActive())) {
                        item.setContainer(dict.getEntry());
                        item.setContainerId(oc.getContainerId());
                    } else {
                        e.add(new FormErrorWarning(Messages.get()
                                                           .sample_orderImportException(accession,
                                                                                        "container",
                                                                                        dict.getEntry())));
                    }
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get()
                                                       .sample_orderImportException(accession,
                                                                                    "container id = ",
                                                                                    oc.getContainerId()
                                                                                      .toString())));
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Missing/invalid dictionary id '" + oc.getContainerId() +
                                          "'", ex);
                    throw ex;
                }
            }

            if (oc.getTypeOfSampleId() != null &&
                !DataBaseUtil.isSame(item.getTypeOfSampleId(), oc.getTypeOfSampleId())) {
                try {
                    dict = dictionaryCache.getById(oc.getTypeOfSampleId());
                    /*
                     * set the sample type only if it's active
                     */
                    if ("Y".equals(dict.getIsActive())) {
                        item.setTypeOfSampleId(oc.getTypeOfSampleId());
                        item.setTypeOfSample(dict.getEntry());
                    } else {
                        e.add(new FormErrorWarning(Messages.get()
                                                           .sample_orderImportException(accession,
                                                                                        "sample type",
                                                                                        dict.getEntry())));
                    }
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get()
                                                       .sample_orderImportException(accession,
                                                                                    "sample type id = ",
                                                                                    oc.getTypeOfSampleId()
                                                                                      .toString())));
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Missing/invalid dictionary id '" +
                                          oc.getTypeOfSampleId() + "'", ex);
                    throw ex;
                }
            }
        }

        /*
         * resequence the rest of the sample items
         */
        if (items != null) {
            for (i = ocs.size(); i < items.size(); i++ )
                items.get(i).setItemSequence(i);
        }

        if (getItems(sm) != null) {
            /*
             * reset next item sequence if the sample now has more items than it
             * ever did in the past
             */
            if (getSample(sm).getNextItemSequence() < getItems(sm).size())
                getSample(sm).setNextItemSequence(getItems(sm).size());
        }
    }

    /**
     * Adds analyses to the sample from the tests specifed in the order. Adds
     * any unresolved prep tests to the returned VO.
     */
    private SampleTestReturnVO copyTests(SampleManager1 sm, OrderManager1 om, ValidationErrorsList e) throws Exception {
        int min;
        SampleItemViewDO item;
        SampleTestReturnVO ret;
        ArrayList<Integer> anaIds;
        ArrayList<SampleTestRequestVO> tests;
        HashMap<Integer, ArrayList<Integer>> anamap;
        HashMap<Integer, SampleItemViewDO> items;

        ret = new SampleTestReturnVO();
        ret.setManager(sm);
        ret.setErrors(e);

        if (getTests(om) == null)
            return ret;

        /*
         * If there are no sample items then add one. Otherwise, add all tests
         * with unmatched sequence to the sample item with the lowest sequence.
         */
        if (getItems(sm) == null || getItems(sm).size() == 0) {
            item = new SampleItemViewDO();
            item.setId(sm.getNextUID());
            item.setItemSequence(0);
            addItem(sm, item);
            getSample(sm).setNextItemSequence(1);
        }

        items = new HashMap<Integer, SampleItemViewDO>();
        min = 0;
        for (SampleItemViewDO si : getItems(sm)) {
            items.put(si.getItemSequence(), si);
            min = Math.min(min, si.getItemSequence());
        }

        /*
         * find out the analytes that need to be marked reportable for each test
         */
        anamap = null;
        if (getAnalytes(om) != null) {
            anamap = new HashMap<Integer, ArrayList<Integer>>();
            for (OrderTestAnalyteViewDO ota : getAnalytes(om)) {
                anaIds = anamap.get(ota.getOrderTestId());
                if (anaIds == null) {
                    anaIds = new ArrayList<Integer>();
                    anamap.put(ota.getOrderTestId(), anaIds);
                }
                anaIds.add(ota.getAnalyteId());
            }
        }

        tests = new ArrayList<SampleTestRequestVO>();
        for (OrderTestViewDO ot : getTests(om)) {
            item = items.get(ot.getItemSequence());
            if (item == null)
                item = items.get(min);

            anaIds = null;
            if (anamap != null)
                anaIds = anamap.get(ot.getId());
            /*
             * add the test and mark the analytes specified by the ids,
             * reportable
             */
            tests.add(new SampleTestRequestVO(sm.getSample().getId(),
                                              item.getId(),
                                              ot.getTestId(),
                                              null,
                                              null,
                                              null,
                                              null,
                                              false,
                                              anaIds));
        }

        ret = sampleManager1.addAnalyses(sm, tests);
        /*
         * add the errors found during importing the order to the ones found
         * while adding tests, because the object returned by the above method
         * is different from the one created at the start of this method
         */
        if (ret.getErrors() != null)
            for (Exception ex : ret.getErrors().getErrorList())
                e.add(ex);
        ret.setErrors(e);
        return ret;
    }

    /**
     * Adds organizations like report-to and bill-to, to sample based on the
     * organizations specified in the order
     */
    private void copyOrganizations(SampleManager1 sm, OrderManager1 om, ValidationErrorsList e) throws Exception {
        Integer accession;
        String attention;
        OrganizationDO repOrg, billOrg, secOrg, shipOrg;
        OrderOrganizationViewDO orepOrg, obillOrg;
        ArrayList<OrderOrganizationViewDO> osecOrgs;
        SamplePrivateWellViewDO well;

        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;

        orepOrg = null;
        obillOrg = null;
        osecOrgs = new ArrayList<OrderOrganizationViewDO>();

        /*
         * find out if organizations of various types are specified in the order
         */
        if (getOrganizations(om) != null) {
            for (OrderOrganizationViewDO otmpOrg : getOrganizations(om)) {
                if (Constants.dictionary().ORG_REPORT_TO.equals(otmpOrg.getTypeId()))
                    orepOrg = otmpOrg;
                else if (Constants.dictionary().ORG_BILL_TO.equals(otmpOrg.getTypeId()))
                    obillOrg = otmpOrg;
                else if (Constants.dictionary().ORG_SECOND_REPORT_TO.equals(otmpOrg.getTypeId()))
                    osecOrgs.add(otmpOrg);
            }
        }

        /*
         * if report-to is null or inactive, use ship-to as the report-to but
         * only if ship-to is active
         */
        repOrg = null;
        attention = null;
        shipOrg = om.getOrder().getOrganization();
        if (orepOrg != null) {
            if ("Y".equals(orepOrg.getOrganizationIsActive())) {
                repOrg = createOrganization(orepOrg);
                attention = orepOrg.getOrganizationAttention();
            } else {
                e.add(new FormErrorWarning(Messages.get()
                                                   .sample_inactiveOrgWarning(accession,
                                                                              orepOrg.getOrganizationName())));
            }
        } else if ("Y".equals(shipOrg.getIsActive())) {
            repOrg = om.getOrder().getOrganization();
            attention = om.getOrder().getOrganizationAttention();
        } else {
            e.add(new FormErrorWarning(Messages.get().sample_inactiveOrgWarning(accession,
                                                                                shipOrg.getName())));
        }

        if (repOrg != null) {
            /*
             * for private well domain, the report-to is set in the domain
             * record itself and not linked through sample organization; for
             * other domains, add a report-to organization to the sample
             */
            if (Constants.domain().PRIVATEWELL.equals(getSample(sm).getDomain())) {
                well = getSamplePrivateWell(sm);
                well.setOrganizationId(repOrg.getId());
                well.setOrganization(repOrg);
                well.setReportToAttention(attention);
            } else {
                addOrganization(sm, createSampleOrganization(repOrg,
                                                             sm.getNextUID(),
                                                             attention,
                                                             Constants.dictionary().ORG_REPORT_TO));
            }
            checkIsHoldRefuseSample(repOrg, e);
        }

        /*
         * set the bill-to if it's different from report-to but only if bill-to
         * is active
         */
        if (obillOrg != null) {
            if (repOrg == null || !obillOrg.getOrganizationId().equals(repOrg.getId())) {
                if ("Y".equals(obillOrg.getOrganizationIsActive())) {
                    billOrg = createOrganization(obillOrg);
                    addOrganization(sm,
                                    createSampleOrganization(billOrg,
                                                             sm.getNextUID(),
                                                             obillOrg.getOrganizationAttention(),
                                                             Constants.dictionary().ORG_BILL_TO));
                    checkIsHoldRefuseSample(billOrg, e);
                } else {
                    e.add(new FormErrorWarning(Messages.get()
                                                       .sample_inactiveOrgWarning(accession,
                                                                                  obillOrg.getOrganizationName())));
                }
            }
        }

        /*
         * add secondary report-to organizations if any were specified, but only
         * if they are active
         */
        for (OrderOrganizationViewDO osecOrg : osecOrgs) {
            if ( (repOrg == null || !osecOrg.getOrganizationId().equals(repOrg.getId())) &&
                "Y".equals(osecOrg.getOrganizationIsActive())) {
                secOrg = createOrganization(osecOrg);
                addOrganization(sm,
                                createSampleOrganization(secOrg,
                                                         sm.getNextUID(),
                                                         osecOrg.getOrganizationAttention(),
                                                         Constants.dictionary().ORG_SECOND_REPORT_TO));
                checkIsHoldRefuseSample(secOrg, e);
            } else {
                e.add(new FormErrorWarning(Messages.get()
                                                   .sample_inactiveOrgWarning(accession,
                                                                              osecOrg.getOrganizationName())));
            }
        }
    }

    /**
     * Adds an internal note to the sample from the "sample" note defined in the
     * order.
     */
    private void copyNotes(SampleManager1 sm, OrderManager1 om) throws Exception {
        NoteViewDO note;
        SystemUserVO user;

        if (getSampleNote(om) == null)
            return;

        note = new NoteViewDO();
        note.setId(sm.getNextUID());
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        note.setIsExternal("N");
        user = userCache.getSystemUser();
        note.setSystemUserId(user.getId());
        note.setSystemUser(user.getLoginName());
        note.setSubject(Messages.get().orderNoteSubject());
        note.setText(getSampleNote(om).getText());
        if (getSampleInternalNotes(sm) != null)
            getSampleInternalNotes(sm).add(0, note);
        else
            addSampleInternalNote(sm, note);
    }

    /**
     * Loads sample data in the SampleManager from elements in the electronic
     * order xml.
     */
    private void copySample(SampleManager1 sm, HashMap<String, String> dataMap,
                            ValidationErrorsList e) throws Exception {
        Datetime collectionDate, collectionTime;
        SampleDO sampleDO;
        String collectionDateString, collectionTimeString;
        
        sampleDO = getSample(sm);

        collectionDateString = dataMap.get("sample.collection_date");
        collectionDate = ReportUtil.getDate(collectionDateString);
        if (collectionDate == null && collectionDateString != null && collectionDateString.length() > 0)
            e.add(new FormErrorWarning(Messages.get().eorderImport_unparsableCollectionDate(collectionDateString)));
        else
            sampleDO.setCollectionDate(collectionDate);

        collectionTimeString = dataMap.get("sample.collection_time");
        collectionTime = ReportUtil.getTime(collectionTimeString);
        if (collectionTime == null && collectionTimeString != null && collectionTimeString.length() > 0)
            e.add(new FormErrorWarning(Messages.get().eorderImport_unparsableCollectionTime(collectionTimeString)));
        else
            sampleDO.setCollectionTime(collectionTime);
        
        sampleDO.setClientReference(dataMap.get("sample.client_reference"));
    }

    /**
     * Loads patient data in the SampleManager from elements in the electronic
     * order xml.
     */
    private void copyPatient(SampleManager1 sm, HashMap<String, String> dataMap,
                             HashMap<String, ArrayList<ExchangeExternalTermViewDO>> externalTermMap,
                             ValidationErrorsList e) throws Exception {
        PatientDO patientDO, queryPatientDO, relatedPatientDO;
        SampleClinicalViewDO scVDO;
        SampleNeonatalViewDO snVDO;
        
        patientDO = null;
        relatedPatientDO = null;
        if (Constants.domain().CLINICAL.equals(getSample(sm).getDomain())) {
            scVDO = getSampleClinical(sm);
            patientDO = scVDO.getPatient();
            loadPatientData(patientDO, "patient", dataMap, externalTermMap, e);
            
            queryPatientDO = lookupExistingPatient(patientDO, e);
            if (queryPatientDO != null) {
                scVDO.setPatient(queryPatientDO);
                scVDO.setPatientId(queryPatientDO.getId());
            }
        } else if (Constants.domain().NEONATAL.equals(getSample(sm).getDomain())) {
            snVDO = getSampleNeonatal(sm);
            patientDO = snVDO.getPatient();
            relatedPatientDO = snVDO.getNextOfKin();
            loadPatientData(patientDO, "patient", dataMap, externalTermMap, e);
            if (dataMap.get("related_patient.last_name") != null) {
                loadPatientData(relatedPatientDO, "related_patient", dataMap, externalTermMap, e);
                snVDO.setNextOfKinRelationId(getLocalTermFromCodedElement("related_patient.type",
                                                                          dataMap.get("related_patient.type"),
                                                                          externalTermMap, Constants.table().DICTIONARY, e));
            }
        }
    }
    
    /**
     * Loads organization data in the SampleManager from elements in the electronic
     * order xml.
     */
    private void copyOrganization(SampleManager1 sm, HashMap<String, String> dataMap,
                                  HashMap<String, ArrayList<ExchangeExternalTermViewDO>> externalTermMap,
                                  ValidationErrorsList e) throws Exception {
        Integer orgId;
        OrganizationDO orgDO;
        String orgCode;

        orgCode = dataMap.get("organization.name");
        if (orgCode != null) {
            orgId = getLocalTermFromCodedElement("organization.name", orgCode, externalTermMap,
                                                 Constants.table().ORGANIZATION, e);
            if (orgId != null) {
                try {
                    orgDO = organization.fetchById(orgId);
                    if ("Y".equals(orgDO.getIsActive())) {
                        addOrganization(sm, createSampleOrganization(orgDO, sm.getNextUID(),
                                                                     dataMap.get("organization.attention"),
                                                                     Constants.dictionary().ORG_REPORT_TO));
                    } else {
                        e.add(new FormErrorWarning(Messages.get().eorderImport_inactiveOrgWarning(orgCode,
                                                                                                  orgDO.getName(),
                                                                                                  orgDO.getId().toString())));
                    }
                } catch (NotFoundException nfE) {
                    e.add(new FormErrorWarning(Messages.get().eorderImport_localTermNotFound(orgCode, "organization.name")));
                } catch (Exception anyE) {
                    log.log(Level.SEVERE, anyE.getMessage(), anyE);
                    e.add(new FormErrorWarning(Messages.get().eorderImport_localTermLookupFailure(orgCode, "organization.name")));
                }
            }
        } else {
            e.add(new FormErrorWarning(Messages.get().eorderImport_organizationNotSupplied()));
        }
    }
    
    /**
     * Loads provider data in the SampleManager from elements in the electronic
     * order xml.
     */
    private void copyProvider(SampleManager1 sm, HashMap<String, String> dataMap,
                              ValidationErrorsList e) throws Exception {
        boolean found;
        int i;
        ArrayList<IdFirstLastNameVO> iflnVOs;
        ArrayList<ProviderDO> providers, matchedProviders;
        ArrayList<QueryData> fields;
        ProviderDO providerDO;
        QueryData field;
        SampleClinicalViewDO scVDO;
        String firstName, lastName, npi;

        found = false;
        npi = dataMap.get("provider.npi");
        lastName = dataMap.get("provider.last_name");
        firstName = dataMap.get("provider.first_name");
        try {
            if (npi != null && npi.length() > 0) {
                providers = provider.fetchByLastNameNpiExternalId(npi, 5);
                if (providers.size() == 1) {
                    providerDO = providers.get(0);
                    scVDO = getSampleClinical(sm);
                    scVDO.setProvider(providerDO);
                    scVDO.setProviderId(providerDO.getId());
                    found = true;
                } else if (providers.size() > 1) {
                    matchedProviders = new ArrayList<ProviderDO>();
                    for (i = 0; i < providers.size(); i++) {
                        providerDO = providers.get(i);
                        if (lastName != null && lastName.length() > 0 && lastName.equals(providerDO.getLastName())) {
                            if ((firstName != null && firstName.length() > 0 && firstName.equals(providerDO.getFirstName())) ||
                                firstName == null || firstName.length() == 0) {
                                matchedProviders.add(providerDO);
                            }
                        } else if (lastName == null || lastName.length() == 0) {
                            matchedProviders.add(providerDO);
                        }
                    }
                    if (matchedProviders.size() > 1) {
                        e.add(new FormErrorWarning(Messages.get().eorderImport_multipleLocalTerms(npi, "provider.npi")));
                        found = true;
                    } else if (matchedProviders.size() == 1) {
                        providerDO = matchedProviders.get(0);
                        scVDO = getSampleClinical(sm);
                        scVDO.setProvider(providerDO);
                        scVDO.setProviderId(providerDO.getId());
                        found = true;
                    }
                }
            }
                
            if (!found) {
                fields = new ArrayList<QueryData>();

                if (lastName != null && lastName.length() > 0) {
                    field = new QueryData();
                    field.setKey(ProviderMeta.getLastName());
                    field.setType(QueryData.Type.STRING);
                    field.setQuery(lastName);
                    fields.add(field);
                }
                
                if (firstName != null && firstName.length() > 0) {
                    field = new QueryData();
                    field.setKey(ProviderMeta.getFirstName());
                    field.setType(QueryData.Type.STRING);
                    field.setQuery(firstName);
                    fields.add(field);
                }

                try {
                    iflnVOs = provider.query(fields, 0, 5);
                    if (iflnVOs.size() > 1) {
                        if (lastName != null)
                            e.add(new FormErrorWarning(Messages.get().eorderImport_multipleLocalTerms(lastName, "provider.last_name")));
                        if (firstName != null)
                            e.add(new FormErrorWarning(Messages.get().eorderImport_multipleLocalTerms(firstName, "provider.first_name")));
                    } else {
                        scVDO = getSampleClinical(sm);
                        providerDO = new ProviderDO(iflnVOs.get(0).getId(), iflnVOs.get(0).getLastName(),
                                                    iflnVOs.get(0).getFirstName(), null, null, null);
                        scVDO.setProvider(providerDO);
                        scVDO.setProviderId(providerDO.getId());
                    }
                } catch (NotFoundException nfE) {
                    e.add(new FormErrorWarning(Messages.get().eorderImport_localTermNotFound(npi, "provider.npi")));
                    if (lastName != null)
                        e.add(new FormErrorWarning(Messages.get().eorderImport_localTermNotFound(lastName, "provider.last_name")));
                    if (firstName != null)
                        e.add(new FormErrorWarning(Messages.get().eorderImport_localTermNotFound(firstName, "provider.first_name")));
                } catch (Exception anyE) {
                    log.log(Level.SEVERE, anyE.getMessage(), anyE);
                    if (lastName != null)
                        e.add(new FormErrorWarning(Messages.get().eorderImport_localTermLookupFailure(lastName, "provider.last_name")));
                    if (firstName != null)
                        e.add(new FormErrorWarning(Messages.get().eorderImport_localTermLookupFailure(firstName, "provider.first_name")));
                }                    
            }
        } catch (Exception anyE) {
            log.log(Level.SEVERE, anyE.getMessage(), anyE);
            e.add(new FormErrorWarning(Messages.get().eorderImport_localTermLookupFailure(npi, "provider.npi")));
        }
    }
    
    /**
     * Loads test data in the SampleManager from elements in the electronic
     * order xml.
     */
    private SampleTestReturnVO copyTests(SampleTestReturnVO ret, HashMap<String, String> dataMap,
                                         HashMap<String, String> testMap, HashMap<String, ArrayList<OrderResult>> resultMap,
                                         HashMap<String, ArrayList<ExchangeExternalTermViewDO>> externalTermMap) throws Exception {
        int i, seq;
        AnalysisViewDO aVDO;
        ArrayList<IdVO> panelTestVOs;
        ArrayList<Integer> testIds;
        ArrayList<SampleTestRequestVO> tests;
        DictionaryDO dictDO;
        HashMap<Integer, Integer> testAnalysisIndexMap;
        HashMap<String, ArrayList<Integer>> testCodeIdMap;
        Integer panelId, sampleSourceId, sampleTypeId, testId;
        Iterator<String> orderTestCodes;
        SampleItemViewDO siVDO, tempSiVDO;
        SampleManager1 sm;
        String placerOrderNum, sampleSourceKey, sampleSourceOther, sampleTypeKey, testKey;
        ValidationErrorsList e;
        
        sm = ret.getManager();
        e = ret.getErrors();
        
        sampleTypeKey = dataMap.get("sample_type");
        sampleTypeId = getLocalTermFromCodedElement("sample_type", sampleTypeKey,
                                                    externalTermMap, Constants.table().DICTIONARY, e);
        siVDO = null;
        if (sampleTypeId != null && getItems(sm) != null) {
            for (i = 0; i < getItems(sm).size(); i++) {
                tempSiVDO = getItems(sm).get(i);
                if (tempSiVDO.getTypeOfSampleId() != null && tempSiVDO.getTypeOfSampleId().equals(sampleTypeId)) {
                    siVDO = tempSiVDO;
                    break;
                }
            }
        }

        if (siVDO == null) {
            if (getItems(sm) != null && getItems(sm).size() > 0) {
                siVDO = getItems(sm).get(0);
            } else {
                seq = getSample(sm).getNextItemSequence();
                getSample(sm).setNextItemSequence(seq + 1);
                siVDO = new SampleItemViewDO();
                siVDO.setId(sm.getNextUID());
                siVDO.setItemSequence(seq);
    
                if (sampleTypeId != null) {
                    try {
                        dictDO = dictionaryCache.getById(sampleTypeId);
                        siVDO.setTypeOfSampleId(dictDO.getId());
                        siVDO.setTypeOfSample(dictDO.getEntry());
                    } catch (NotFoundException nfE) {
                        e.add(new FormErrorWarning(Messages.get().eorderImport_dictionaryNotFound(sampleTypeKey)));
                    } catch (Exception anyE) {
                        log.log(Level.SEVERE, anyE.getMessage(), anyE);
                        e.add(new FormErrorWarning(Messages.get().eorderImport_dictionaryLookupFailure(sampleTypeKey)));
                    }
                }
    
                addItem(sm, siVDO);
            }
        }
        
        sampleSourceKey = dataMap.get("sample_source");
        sampleSourceId = getLocalTermFromCodedElement("sample_source", sampleSourceKey,
                                                      externalTermMap, Constants.table().DICTIONARY, e);
        if (sampleSourceId != null) {
            try {
                dictDO = dictionaryCache.getById(sampleSourceId);
                if (siVDO.getSourceOfSampleId() == null) {
                    siVDO.setSourceOfSampleId(dictDO.getId());
                    siVDO.setSourceOfSample(dictDO.getEntry());
                } else if (!siVDO.getSourceOfSampleId().equals(sampleSourceId)) {
                    e.add(new FormErrorWarning(Messages.get().eorderImport_sourceOfSampleMismatch(siVDO.getSourceOfSample(), dictDO.getEntry())));
                }
            } catch (NotFoundException nfE) {
                e.add(new FormErrorWarning(Messages.get().eorderImport_dictionaryNotFound(sampleSourceKey)));
            } catch (Exception anyE) {
                log.log(Level.SEVERE, anyE.getMessage(), anyE);
                e.add(new FormErrorWarning(Messages.get().eorderImport_dictionaryLookupFailure(sampleSourceKey)));
            }
        }
        
        sampleSourceOther = dataMap.get("sample_source_other");
        if (sampleSourceOther != null && sampleSourceOther.length() > 0) {
            if (siVDO.getSourceOther() == null)
                siVDO.setSourceOther(sampleSourceOther);
            else if (!siVDO.getSourceOther().equals(sampleSourceOther))
                e.add(new FormErrorWarning(Messages.get().eorderImport_sourceOtherMismatch(siVDO.getSourceOther(), sampleSourceOther)));
        }
        
        testAnalysisIndexMap = new HashMap<Integer, Integer>();
        if (getAnalyses(sm) != null) {
            for (i = 0; i < getAnalyses(sm).size(); i++) {
                aVDO = getAnalyses(sm).get(i);
                if (aVDO.getSampleItemId().equals(siVDO.getId()) && aVDO.getTestId() != null &&
                    !Constants.dictionary().ANALYSIS_CANCELLED.equals(aVDO.getStatusId()) &&
                    !Constants.dictionary().ANALYSIS_RELEASED.equals(aVDO.getStatusId()))
                    testAnalysisIndexMap.put(aVDO.getTestId(), i);
            }
        }
        
        tests = new ArrayList<SampleTestRequestVO>();
        testCodeIdMap = new HashMap<String, ArrayList<Integer>>();
        orderTestCodes = testMap.keySet().iterator();
        while (orderTestCodes.hasNext()) {
            placerOrderNum = orderTestCodes.next();
            testIds = testCodeIdMap.get(placerOrderNum);
            if (testIds == null) {
                testIds = new ArrayList<Integer>();
                testCodeIdMap.put(placerOrderNum, testIds);
            }
            testKey = testMap.get(placerOrderNum);
            panelId = getLocalTermFromCodedElement("panel", testKey, externalTermMap,
                                                   Constants.table().PANEL, e);
            if (panelId != null) {
                panelTestVOs = panel.fetchTestIdsFromPanel(panelId);
                for (IdVO ptVO : panelTestVOs) {
                    testIds.add(ptVO.getId());
                    if (!testAnalysisIndexMap.containsKey(ptVO.getId()))
                        tests.add(new SampleTestRequestVO(sm.getSample().getId(),
                                                          siVDO.getId(),
                                                          ptVO.getId(),
                                                          null,
                                                          null,
                                                          null,
                                                          panelId,
                                                          false,
                                                          null));
                }
            } else {
                testId = getLocalTermFromCodedElement("test", testKey, externalTermMap,
                                                      Constants.table().TEST, e);
                if (testId != null && !testAnalysisIndexMap.containsKey(testId)) {
                    testIds.add(testId);
                    tests.add(new SampleTestRequestVO(sm.getSample().getId(),
                                                      siVDO.getId(),
                                                      testId,
                                                      null,
                                                      null,
                                                      null,
                                                      null,
                                                      false,
                                                      null));
                }
            }
        }
        
        ret = sampleManager1.addAnalyses(sm, tests);
        /*
         * add the errors found during importing the order to the ones found
         * while adding tests, because the object returned by the above method
         * is different from the one created at the start of this method
         */
        if (ret.getErrors() != null)
            for (Exception ex : ret.getErrors().getErrorList())
                e.add(ex);
        ret.setErrors(e);

        copyResults(ret, siVDO, testCodeIdMap, resultMap, externalTermMap);
        
        return ret;
    }
    
    /**
     * Loads result data in the SampleManager from elements in the electronic
     * order xml.
     */
    private void copyResults(SampleTestReturnVO ret, SampleItemViewDO siVDO,
                             HashMap<String, ArrayList<Integer>> testIdCodeMap,
                             HashMap<String, ArrayList<OrderResult>> resultMap,
                             HashMap<String, ArrayList<ExchangeExternalTermViewDO>> externalTermMap) throws Exception {
        int i;
        AnalysisViewDO aVDO;
        ArrayList<Integer> testIds;
        ArrayList<OrderResult> orderResults;
        DictionaryDO dictDO;
        HashMap<Integer, AnalysisViewDO> testAnalysisMap;
        HashMap<Integer, HashMap<Integer, Integer>> analysisResultIndexMap;
        HashMap<Integer, Integer> resultIndexMap;
        HashMap<Integer, TestManager> tManMap;
        HashSet<Integer> analysisIds;
        Integer analyteId, dictId, resultIndex;
        Iterator<String> orderTestCodes;
        NoteViewDO noteVDO;
        ResultFormatter rf;
        ResultViewDO rVDO;
        SampleManager1 sm;
        String placerOrderNum;
        SystemUserVO userVO;
        TestManager tMan;
        ValidationErrorsList e;
        
        sm = ret.getManager();
        e = ret.getErrors();
        
        analysisIds = new HashSet<Integer>();
        testAnalysisMap = new HashMap<Integer, AnalysisViewDO>();
        if (getAnalyses(sm) != null) {
            for (i = 0; i < getAnalyses(sm).size(); i++) {
                aVDO = getAnalyses(sm).get(i);
                if (aVDO.getSampleItemId().equals(siVDO.getId()) && aVDO.getTestId() != null &&
                    !Constants.dictionary().ANALYSIS_CANCELLED.equals(aVDO.getStatusId()) &&
                    !Constants.dictionary().ANALYSIS_RELEASED.equals(aVDO.getStatusId())) {
                    analysisIds.add(aVDO.getId());
                    testAnalysisMap.put(aVDO.getTestId(), aVDO);
                }
            }
        }

        analysisResultIndexMap = new HashMap<Integer, HashMap<Integer, Integer>>();
        if (getResults(sm) != null) {
            for (i = 0; i < getResults(sm).size(); i++) {
                rVDO = getResults(sm).get(i);
                if (analysisIds.contains(rVDO.getAnalysisId()) && "N".equals(rVDO.getIsColumn())) {
                    resultIndexMap = analysisResultIndexMap.get(rVDO.getAnalysisId());
                    if (resultIndexMap == null) {
                        resultIndexMap = new HashMap<Integer, Integer>();
                        analysisResultIndexMap.put(rVDO.getAnalysisId(), resultIndexMap);
                    }
                    resultIndexMap.put(rVDO.getAnalyteId(), i);
                }
            }
        }

        orderTestCodes = resultMap.keySet().iterator();
        tManMap = new HashMap<Integer, TestManager>();
        while (orderTestCodes.hasNext()) {
            placerOrderNum = orderTestCodes.next();
            testIds = testIdCodeMap.get(placerOrderNum);
            orderResults = resultMap.get(placerOrderNum);
            if (testIds != null && orderResults != null) {
                for (Integer testId : testIds) {
                    tMan = tManMap.get(testId);
                    if (tMan == null) {
                        try {
                            tMan = testManager.fetchById(testId);
                            tManMap.put(testId, tMan);
                        } catch (Exception anyE) {
                            e.add(new FormErrorWarning(Messages.get().eorderImport_errorLoadingResultFormatter(testId.toString())));
                            continue;
                        }
                    }
                    aVDO = testAnalysisMap.get(testId);
                    resultIndexMap = analysisResultIndexMap.get(aVDO.getId());
                    for (OrderResult orderResult : orderResults) {
                        if ("note".equals(orderResult.analyte)) {
                            noteVDO = new NoteViewDO();
                            noteVDO.setId(sm.getNextUID());
                            noteVDO.setReferenceId(aVDO.getId());
                            noteVDO.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                            noteVDO.setIsExternal("N");
                            userVO = userCache.getSystemUser();
                            noteVDO.setSystemUserId(userVO.getId());
                            noteVDO.setSystemUser(userVO.getLoginName());
                            noteVDO.setSubject(Messages.get().eorderImport_orderNoteSubject());
                            noteVDO.setText(orderResult.result);
                            addAnalysisInternalNote(sm, noteVDO);
                        } else {
                            analyteId = getLocalTermFromCodedElement("analyte", orderResult.analyte,
                                                                     externalTermMap,
                                                                     Constants.table().TEST_ANALYTE, e);
                            if (analyteId == null)
                                analyteId = getLocalTermFromCodedElement("analyte", orderResult.analyte,
                                                                         externalTermMap,
                                                                         Constants.table().ANALYTE, e);
                            if (analyteId != null) {
                                resultIndex = resultIndexMap.get(analyteId);
                                if (resultIndex != null) {
                                    rf = tMan.getFormatter();
                                    rVDO = getResults(sm).get(resultIndex);
                                    if (orderResult.result.startsWith("extTerm:")) {
                                        dictId = getLocalTermFromCodedElement("result",
                                                                              orderResult.result,
                                                                              externalTermMap,
                                                                              Constants.table().DICTIONARY, e);
                                        if (dictId != null) {
                                            dictDO = null;
                                            try {
                                                dictDO = dictionaryCache.getById(dictId);
                                                if (dictDO != null)
                                                    ResultHelper.formatValue(rVDO, dictDO.getEntry(), aVDO.getUnitOfMeasureId(), rf);
                                            } catch (NotFoundException nfE) {
                                                e.add(new FormErrorWarning(Messages.get().eorderImport_dictionaryNotFound(orderResult.result)));
                                            } catch (ParseException parE) {
                                                e.add(new FormErrorWarning(Messages.get().eorderImport_invalidValue(dictDO.getEntry(),
                                                                                                                    tMan.getTest().getName(),
                                                                                                                    tMan.getTest().getMethodName(),
                                                                                                                    rVDO.getAnalyte())));
                                            } catch (Exception anyE) {
                                                log.log(Level.SEVERE, anyE.getMessage(), anyE);
                                                e.add(new FormErrorWarning(Messages.get().eorderImport_dictionaryLookupFailure(orderResult.result)));
                                            }
                                        }
                                    } else {
                                        try {
                                            ResultHelper.formatValue(rVDO, orderResult.result, aVDO.getUnitOfMeasureId(), rf);
                                        } catch (ParseException parE) {
                                            e.add(new FormErrorWarning(Messages.get().eorderImport_invalidValue(orderResult.result,
                                                                                                                tMan.getTest().getName(),
                                                                                                                tMan.getTest().getMethodName(),
                                                                                                                rVDO.getAnalyte())));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Returns a newly created OrganizationDO, filled from the
     * OrderOrganizationViewDO
     */
    private OrganizationDO createOrganization(OrderOrganizationViewDO org) {
        OrganizationDO data;
        AddressDO addr;

        data = new OrganizationDO();
        data.setId(org.getOrganizationId());
        data.setName(org.getOrganizationName());

        addr = data.getAddress();
        addr.setMultipleUnit(org.getOrganizationAddressMultipleUnit());
        addr.setStreetAddress(org.getOrganizationAddressStreetAddress());
        addr.setCity(org.getOrganizationAddressCity());
        addr.setState(org.getOrganizationAddressState());
        addr.setZipCode(org.getOrganizationAddressZipCode());
        addr.setFaxPhone(org.getOrganizationAddressFaxPhone());
        addr.setWorkPhone(org.getOrganizationAddressWorkPhone());

        return data;
    }

    /**
     * Returns a newly created SampleOrganizationViewDO, filled from the
     * OrganizationDO and its id, attention and type set to the passed values
     */
    private SampleOrganizationViewDO createSampleOrganization(OrganizationDO org, Integer id,
                                                              String attention, Integer type) {
        SampleOrganizationViewDO data;
        AddressDO addr;

        data = new SampleOrganizationViewDO();
        data.setId(id);
        data.setOrganizationId(org.getId());
        data.setOrganizationName(org.getName());
        data.setOrganizationAttention(attention);
        data.setTypeId(type);
        addr = org.getAddress();
        data.setOrganizationMultipleUnit(addr.getMultipleUnit());
        data.setOrganizationStreetAddress(addr.getStreetAddress());
        data.setOrganizationCity(addr.getCity());
        data.setOrganizationState(addr.getState());
        data.setOrganizationZipCode(addr.getZipCode());
        data.setOrganizationCountry(addr.getCountry());

        return data;
    }

    /**
     * Loads the specified PatientDO object with data from the specified xml element
     */
    private void loadPatientData(PatientDO patDO, String prefix, HashMap<String, String> dataMap,
                                 HashMap<String, ArrayList<ExchangeExternalTermViewDO>> externalTermMap,
                                 ValidationErrorsList e) {
        Datetime birthDate, birthTime;
        String birthDateString, birthTimeString;

        patDO.setLastName(dataMap.get(prefix+".last_name"));
        patDO.setFirstName(dataMap.get(prefix+".first_name"));
        patDO.setMiddleName(dataMap.get(prefix+".maiden_name"));
        patDO.setNationalId(dataMap.get(prefix+".nid"));
        
        loadAddressData(patDO.getAddress(), prefix+".address", dataMap);

        birthDateString = dataMap.get(prefix+".birth_date");
        birthDate = ReportUtil.getDate(birthDateString);
        if (birthDate == null && birthDateString != null && birthDateString.length() > 0)
            e.add(new FormErrorWarning(Messages.get().eorderImport_unparsableBirthDate(birthDateString)));
        else
            patDO.setBirthDate(birthDate);

        birthTimeString = dataMap.get(prefix+".birth_time");
        birthTime = ReportUtil.getTime(birthTimeString);
        if (birthTime == null && birthTimeString != null && birthTimeString.length() > 0)
            e.add(new FormErrorWarning(Messages.get().eorderImport_unparsableBirthTime(birthTimeString)));
        else
            patDO.setBirthTime(birthTime);
        
        patDO.setGenderId(getLocalTermFromCodedElement(prefix+".gender", dataMap.get(prefix+".gender"),
                                                       externalTermMap, Constants.table().DICTIONARY, e));
        
        patDO.setRaceId(getLocalTermFromCodedElement(prefix+".race", dataMap.get(prefix+".race"),
                                                     externalTermMap, Constants.table().DICTIONARY, e));

        patDO.setEthnicityId(getLocalTermFromCodedElement(prefix+".ethnicity", dataMap.get(prefix+".ethnicity"),
                                                          externalTermMap, Constants.table().DICTIONARY, e));
    }
    
    /**
     * Loads the specified PatientDO object with data from the specified xml element
     */
    private void loadAddressData(AddressDO addDO, String prefix, HashMap<String, String> dataMap) {
        addDO.setMultipleUnit(dataMap.get(prefix+".multiple_unit"));
        addDO.setStreetAddress(dataMap.get(prefix+".street_address"));
        addDO.setCity(dataMap.get(prefix+".city"));
        addDO.setState(dataMap.get(prefix+".state"));
        addDO.setZipCode(dataMap.get(prefix+".zip_code"));
        addDO.setWorkPhone(dataMap.get(prefix+".work_phone"));
        addDO.setHomePhone(dataMap.get(prefix+".home_phone"));
        addDO.setCellPhone(dataMap.get(prefix+".cell_phone"));
        addDO.setFaxPhone(dataMap.get(prefix+".fax_phone"));
        addDO.setEmail(dataMap.get(prefix+".email"));
        addDO.setCountry(dataMap.get(prefix+".country"));
    }
    
    private void loadDataMapFromXml(Node node, HashMap<String, String> dataMap,
                                    HashMap<String, String> testMap, HashMap<String, ArrayList<OrderResult>> resultMap,
                                    HashMap<String, ArrayList<ExchangeExternalTermViewDO>> externalTermMap) {
        int i, j, k;
        ArrayList<OrderResult> results;
        Node childNode, childNode2, identNode, obvNode, parentNode, parentNode2, valueNode;
        NodeList childNodes, resultNodes, noteNodes, noteTextNodes;
        String termKey, termKey2, placerOrderNum, noteText;
        
        childNodes = node.getChildNodes();
        for (i = 0; i < childNodes.getLength(); i++) {
            childNode = childNodes.item(i);
            if (childNode.hasChildNodes() && Node.ELEMENT_NODE == childNode.getFirstChild().getNodeType()) {
                if ("test".equals(childNode.getNodeName())) {
                    placerOrderNum = XMLUtil.getNodeText((Element)childNode, "placer_order_num");
                    if (placerOrderNum != null) {
                        childNode2 = ((Element)childNode).getElementsByTagName("name").item(0);
                        termKey = "extTerm:"+XMLUtil.getNodeText((Element)childNode2, "term");
                        if (!externalTermMap.containsKey(XMLUtil.getNodeText((Element)childNode2, "term")))
                            externalTermMap.put(XMLUtil.getNodeText((Element)childNode2, "term"), new ArrayList<ExchangeExternalTermViewDO>());
                        if (((Element)childNode2).getElementsByTagName("coding_system") != null &&
                            ((Element)childNode2).getElementsByTagName("coding_system").getLength() > 0 &&
                            XMLUtil.getNodeText((Element)childNode2, "coding_system") != null)
                            termKey += "extCS:"+XMLUtil.getNodeText((Element)childNode2, "coding_system");
                        testMap.put(placerOrderNum, termKey);
                        
                        if (((Element)childNode).getElementsByTagName("sample_type") != null && ((Element)childNode).getElementsByTagName("sample_type").getLength() > 0) {
                            childNode2 = ((Element)childNode).getElementsByTagName("sample_type").item(0);
                            termKey = "extTerm:"+XMLUtil.getNodeText((Element)childNode2, "term");
                            if (!externalTermMap.containsKey(XMLUtil.getNodeText((Element)childNode2, "term")))
                                externalTermMap.put(XMLUtil.getNodeText((Element)childNode2, "term"), new ArrayList<ExchangeExternalTermViewDO>());
                            if (((Element)childNode2).getElementsByTagName("coding_system") != null &&
                                ((Element)childNode2).getElementsByTagName("coding_system").getLength() > 0 &&
                                XMLUtil.getNodeText((Element)childNode2, "coding_system") != null)
                                termKey += "extCS:"+XMLUtil.getNodeText((Element)childNode2, "coding_system");
                            dataMap.put("sample_type", termKey);
                        }
                        
                        if (((Element)childNode).getElementsByTagName("sample_source") != null && ((Element)childNode).getElementsByTagName("sample_source").getLength() > 0) {
                            childNode2 = ((Element)childNode).getElementsByTagName("sample_source").item(0);
                            termKey = "extTerm:"+XMLUtil.getNodeText((Element)childNode2, "term");
                            if (!externalTermMap.containsKey(XMLUtil.getNodeText((Element)childNode2, "term")))
                                externalTermMap.put(XMLUtil.getNodeText((Element)childNode2, "term"), new ArrayList<ExchangeExternalTermViewDO>());
                            if (((Element)childNode2).getElementsByTagName("coding_system") != null &&
                                ((Element)childNode2).getElementsByTagName("coding_system").getLength() > 0 &&
                                XMLUtil.getNodeText((Element)childNode2, "coding_system") != null)
                                termKey += "extCS:"+XMLUtil.getNodeText((Element)childNode2, "coding_system");
                            dataMap.put("sample_source", termKey);
                        }
                        
                        dataMap.put("source_other", XMLUtil.getNodeText((Element)childNode, "source_other"));
                        
                        results = resultMap.get(placerOrderNum);
                        if (results == null) {
                            results = new ArrayList<OrderResult>();
                            resultMap.put(placerOrderNum, results);
                        }
                        resultNodes = ((Element)childNode).getElementsByTagName("result");
                        for (j = 0; j < resultNodes.getLength(); j++) {
                            childNode2 = resultNodes.item(j);
                            obvNode = ((Element)childNode2).getElementsByTagName("observation").item(0);

                            identNode = ((Element)obvNode).getElementsByTagName("identifier").item(0);
                            termKey = "extTerm:"+XMLUtil.getNodeText((Element)identNode, "term");
                            if (!externalTermMap.containsKey(XMLUtil.getNodeText((Element)identNode, "term")))
                                externalTermMap.put(XMLUtil.getNodeText((Element)identNode, "term"), new ArrayList<ExchangeExternalTermViewDO>());
                            if (((Element)identNode).getElementsByTagName("coding_system") != null &&
                                ((Element)identNode).getElementsByTagName("coding_system").getLength() > 0 &&
                                XMLUtil.getNodeText((Element)identNode, "coding_system") != null)
                                termKey += "extCS:"+XMLUtil.getNodeText((Element)identNode, "coding_system");
                            
                            valueNode = ((Element)obvNode).getElementsByTagName("value").item(0);
                            if ("CE".equals(XMLUtil.getNodeText((Element)obvNode, "value_type"))) {
                                termKey2 = "extTerm:"+XMLUtil.getNodeText((Element)valueNode, "term");
                                if (!externalTermMap.containsKey(XMLUtil.getNodeText((Element)valueNode, "term")))
                                    externalTermMap.put(XMLUtil.getNodeText((Element)valueNode, "term"), new ArrayList<ExchangeExternalTermViewDO>());
                                if (((Element)valueNode).getElementsByTagName("coding_system") != null &&
                                    ((Element)valueNode).getElementsByTagName("coding_system").getLength() > 0 &&
                                    XMLUtil.getNodeText((Element)valueNode, "coding_system") != null)
                                    termKey2 += "extCS:"+XMLUtil.getNodeText((Element)valueNode, "coding_system");
                                results.add(new OrderResult(termKey, termKey2));
                            } else {
                                results.add(new OrderResult(termKey, XMLUtil.getNodeText((Element)valueNode, "term")));
                            }
                        }
                        
                        noteText = "";
                        noteNodes = ((Element)childNode).getElementsByTagName("note");
                        if (noteNodes != null && noteNodes.getLength() > 0) {
                            for (j = 0; j < noteNodes.getLength(); j++) {
                                childNode2 = noteNodes.item(j);
                                noteTextNodes = ((Element)childNode2).getElementsByTagName("text");
                                if (noteTextNodes != null && noteTextNodes.getLength() > 0) {
                                    if (noteText.length() > 0)
                                        noteText += "\n";
                                    for (k = 0; k < noteTextNodes.getLength(); k++) {
                                        if (noteText.length() > 0)
                                            noteText += "\n";
                                        noteText += noteTextNodes.item(k).getFirstChild().getNodeValue();
                                    }
                                }
                            }
                            results.add(new OrderResult("note", noteText));
                        }
                    }                        
                } else {
                    loadDataMapFromXml(childNode, dataMap, testMap, resultMap, externalTermMap);
                }
            } else {
                if (childNode.getFirstChild() == null || childNode.getFirstChild().getNodeValue() == null)
                    continue;
                if ("organization".equals(node.getNodeName()) && "name".equals(childNode.getNodeName()) && childNode.getFirstChild() != null) {
                    dataMap.put(node.getNodeName()+"."+childNode.getNodeName(), "extTerm:"+childNode.getFirstChild().getNodeValue().trim());
                    if (!externalTermMap.containsKey(childNode.getFirstChild().getNodeValue().trim()))
                        externalTermMap.put(childNode.getFirstChild().getNodeValue().trim(), new ArrayList<ExchangeExternalTermViewDO>());
                } else if ("patient".equals(node.getNodeName())) {
                    parentNode = node.getParentNode();
                    if (parentNode != null && "related_patient".equals(parentNode.getNodeName()))
                        dataMap.put(parentNode.getNodeName()+"."+node.getNodeName()+"."+childNode.getNodeName(), childNode.getFirstChild().getNodeValue().trim());
                    else
                        dataMap.put(node.getNodeName()+"."+childNode.getNodeName(), childNode.getFirstChild().getNodeValue().trim());
                } else if ("address".equals(node.getNodeName())) {
                    parentNode = node.getParentNode();
                    if (parentNode != null) {
                        if ("patient".equals(parentNode.getNodeName())) {
                            parentNode2 = parentNode.getParentNode();
                            if (parentNode2 != null && "related_patient".equals(parentNode2.getNodeName())) {
                                dataMap.put("related_patient.address."+childNode.getNodeName(), childNode.getFirstChild().getNodeValue().trim());
                            } else {
                                dataMap.put(parentNode.getNodeName()+"."+node.getNodeName()+"."+childNode.getNodeName(), childNode.getFirstChild().getNodeValue().trim());
                            }
                        } else {
                            dataMap.put(parentNode.getNodeName()+"."+node.getNodeName()+"."+childNode.getNodeName(), childNode.getFirstChild().getNodeValue().trim());
                        }
                    } else {
                        dataMap.put(node.getNodeName()+"."+childNode.getNodeName(), childNode.getFirstChild().getNodeValue().trim());
                    }
                } else if ("term".equals(childNode.getNodeName())) {
                    termKey = "extTerm:"+childNode.getFirstChild().getNodeValue().trim();
                    childNode2 = null;
                    while (++i < childNodes.getLength()) {
                        if ("coding_system".equals(childNodes.item(i).getNodeName()))
                            childNode2 = childNodes.item(i);
                    }
                    if (childNode2 != null && childNode2.getFirstChild() != null)
                        termKey += "extCS:"+childNode2.getFirstChild().getNodeValue().trim();
                    if (!externalTermMap.containsKey(childNode.getFirstChild().getNodeValue().trim()))
                        externalTermMap.put(childNode.getFirstChild().getNodeValue().trim(), new ArrayList<ExchangeExternalTermViewDO>());
                    
                    parentNode = node.getParentNode();
                    if (parentNode != null)
                        dataMap.put(parentNode.getNodeName()+"."+node.getNodeName(), termKey);
                    else
                        dataMap.put(node.getNodeName(), termKey);
                    break;
                } else {
                    dataMap.put(node.getNodeName()+"."+childNode.getNodeName(), childNode.getFirstChild().getNodeValue().trim());
                }
            }
        }
    }
    
    /*
     * The external key is of the format "extTerm:<term>extCS:<coding_system>"
     */
    private Integer getLocalTermFromCodedElement(String fieldName, String externalKey,
                                                 HashMap<String, ArrayList<ExchangeExternalTermViewDO>> externalTermMap,
                                                 Integer referenceTableId, ValidationErrorsList e) {
        int splitIndex;
        ArrayList<ExchangeExternalTermViewDO> externalTerms, matchedTerms;
        String externalTerm, externalCodingSystem;

        externalTerm = "";
        matchedTerms = new ArrayList<ExchangeExternalTermViewDO>();
        if (externalKey != null && externalKey.length() > 0) {
            splitIndex = externalKey.indexOf("extCS:");
            if (splitIndex != -1) {
                externalTerm = externalKey.substring(8, splitIndex);
                externalCodingSystem = externalKey.substring(splitIndex + 6);
            } else {
                externalTerm = externalKey.substring(8);
                externalCodingSystem = null;
            }
            
            externalTerms = externalTermMap.get(externalTerm);
            if (externalTerms != null) {
                for (ExchangeExternalTermViewDO eetVDO : externalTerms) {
                    if (referenceTableId.equals(eetVDO.getExchangeLocalTermReferenceTableId())) {
                        if (externalCodingSystem != null) {
                            if (!externalCodingSystem.equals(eetVDO.getExternalCodingSystem()))
                                continue;
                        }
                        matchedTerms.add(eetVDO);
                    }
                }
            }

            if (matchedTerms.size() == 1) {
                return matchedTerms.get(0).getExchangeLocalTermReferenceId();
            } else {
                if (matchedTerms.size() == 0) {
                    if (!Constants.table().PANEL.equals(referenceTableId))
                        e.add(new FormErrorWarning(Messages.get().eorderImport_localTermNotFound(externalTerm, fieldName)));
                } else {
                    e.add(new FormErrorWarning(Messages.get().eorderImport_multipleLocalTerms(externalTerm, fieldName)));
                }
            }
        }
        return null;
    }

    /**
     * Adds a warning if samples from this organization are to be held/refused.
     */
    private void checkIsHoldRefuseSample(OrganizationDO org, ValidationErrorsList e) throws Exception {
        try {
            organizationParameter.fetchByOrgIdAndDictSystemName(org.getId(), ORG_HOLD_SAMPLE);
            e.add(new FormErrorWarning(Messages.get()
                                               .gen_orgMarkedAsHoldRefuseSample(org.getName())));
        } catch (NotFoundException ex) {
            // ignore
        }
    }
    
    private PatientDO lookupExistingPatient(PatientDO patDO, ValidationErrorsList e) {
        ArrayList<PatientDO> patientDOs;
        ArrayList<QueryData> fields;
        PatientDO existingDO;
        QueryData field;

        existingDO = null;
        
        fields = new ArrayList<QueryData>();
        if (patDO.getLastName() != null && patDO.getLastName().length() > 0) {
            field = new QueryData();
            field.setKey(PatientMeta.getLastName());
            field.setType(QueryData.Type.STRING);
            field.setQuery(patDO.getLastName());
            fields.add(field);
        }
        if (patDO.getFirstName() != null && patDO.getFirstName().length() > 0) {
            field = new QueryData();
            field.setKey(PatientMeta.getFirstName());
            field.setType(QueryData.Type.STRING);
            field.setQuery(patDO.getFirstName());
            fields.add(field);
        }
        if (patDO.getBirthDate() != null) {
            field = new QueryData();
            field.setKey(PatientMeta.getBirthDate());
            field.setType(QueryData.Type.DATE);
            field.setQuery(patDO.getBirthDate().toString());
            fields.add(field);
        }
        if (patDO.getNationalId() != null && patDO.getNationalId().length() > 0) {
            field = new QueryData();
            field.setKey(PatientMeta.getNationalId());
            field.setType(QueryData.Type.STRING);
            field.setQuery(patDO.getNationalId());
            fields.add(field);
        }
        
        if (fields.size() > 0) {
            try {
                patientDOs = patient.query(fields, 0, 10);
                if (patientDOs.size() > 1) {
                    e.add(new FormErrorWarning(Messages.get().eorderImport_mulitpleMatchPatients()));
                } else {
                    existingDO = patientDOs.get(0);
                }
            } catch (NotFoundException nfE) {
                // ignore
            } catch (Exception anyE) {
                log.log(Level.SEVERE, anyE.getMessage(), anyE);
                e.add(new FormErrorWarning(Messages.get().eorderImport_patientLookupFailure()));
            }
        }
        
        return existingDO;
    }
    
    private class OrderResult {
        String analyte, result;
        
        public OrderResult(String analyte, String result) {
            this.analyte = analyte;
            this.result = result;
        }
    }
}