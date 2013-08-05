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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderOrganizationManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.ReportUtil;

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
    private CategoryCacheBean         categoryCache;

    @EJB
    private ProjectBean               project;

    @EJB
    private PWSBean                   pws;

    @EJB
    private SystemVariableBean        systemVariable;

    @EJB
    private AuxFieldGroupBean         auxFieldGroup;

    @EJB
    private OrganizationParameterBean organizationParameter;

    @EJB
    private UserCacheBean             userCache;

    @EJB
    private SampleManager1Bean        sampleManager1;

    @EJB
    private AuxDataHelperBean         auxDataHelper;

    private static final Logger       log = Logger.getLogger("openelis");

    private static final String       SAMPLE_ENV_AUX_DATA = "sample_env_aux_data",
                    SAMPLE_WELL_AUX_DATA = "sample_well_aux_data",
                    SAMPLE_SDWIS_AUX_DATA = "sample_sdwis_aux_data",
                    SMPL_COLLECTED_DATE = "smpl_collected_date",
                    SMPL_COLLECTED_TIME = "smpl_collected_time",
                    SMPL_CLIENT_REF = "smpl_client_ref", IS_HAZARDOUS = "is_hazardous",
                    COLLECTOR = "collector", LOCATION = "location",
                    LOC_MULT_UNIT = "loc_mult_unit", LOC_STREET_ADDRESS = "loc_street_address",
                    LOC_CITY = "loc_city", LOC_STATE = "loc_state", LOC_ZIP_CODE = "loc_zip_code",
                    LOC_COUNTRY = "loc_country", PRIORITY = "priority",
                    COLLECTOR_PHONE = "collector_phone", DESCRIPTION = "description",
                    PROJECT_NAME = "project_name", OWNER = "owner", WELL_NUMBER = "well_number",
                    PWS_ID = "pws_id", STATE_LAB_NUM = "state_lab_num",
                    FACILITY_ID = "facility_id", SAMPLE_TYPE = "sample_type",
                    SAMPLE_CAT = "sample_cat", SAMPLE_PT_ID = "sample_pt_id", YES = "yes",
                    STATE = "state", COUNTRY = "country", SDWIS_SAMPLE_TYPE = "sdwis_sample_type",
                    SDWIS_SAMPLE_CATEGORY = "sdwis_sample_category",
                    ORG_HOLD_SAMPLE = "org_hold_sample";

    /**
     * Loads and merges the data in the SampleManager with the corresponding
     * data in the send-out order specified by its id and adds any errors
     * related to invalid data encountered during the process to the
     * ValidationErrorsList
     */
    public SampleTestReturnVO importSendoutOrder(SampleManager1 sm, Integer orderId,
                                                 ValidationErrorsList e) throws Exception {
        Integer domainGrpId;
        SampleDO data;
        OrderManager om;
        AuxDataViewDO aux;
        AuxDataManager am;
        SampleTestReturnVO ret;
        HashMap<Integer, AuxDataViewDO> auxGrp;
        HashMap<Integer, HashMap<Integer, AuxDataViewDO>> auxGrps;

        data = getSample(sm);
        try {
            om = OrderManager.fetchById(orderId);
            if ( !Constants.order().SEND_OUT.equals(om.getOrder().getType()))
                throw new FormErrorException(Messages.get()
                                                     .sample_orderIdInvalidException(data.getAccessionNumber(),
                                                                                     orderId));
        } catch (NotFoundException ex) {
            throw new FormErrorException(Messages.get()
                                                 .sample_orderIdInvalidException(data.getAccessionNumber(),
                                                                                 orderId));
        }

        data.setOrderId(orderId);
        /*
         * make a hash of aux groups
         */
        domainGrpId = getDomainAuxGroupId(data);
        am = om.getAuxData();
        auxGrps = new HashMap<Integer, HashMap<Integer, AuxDataViewDO>>();
        for (int i = 0; i < am.count(); i++ ) {
            aux = am.getAuxDataAt(i);
            auxGrp = auxGrps.get(aux.getGroupId());
            if (auxGrp == null) {
                auxGrp = new HashMap<Integer, AuxDataViewDO>();
                auxGrps.put(aux.getGroupId(), auxGrp);
            }
            auxGrp.put(aux.getAnalyteId(), aux);
        }

        /*
         * fieldAux are consumed by sample, sample domain, project
         */
        auxGrp = auxGrps.get(domainGrpId);
        if (auxGrp != null) {
            copyGeneralFields(sm, auxGrp, e);
            if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain()))
                copyEnvironmentalFields(getSampleEnvironmental(sm), auxGrp, e);
            else if (Constants.domain().PRIVATEWELL.equals(data.getDomain()))
                copyPrivateWellFields(getSamplePrivateWell(sm), auxGrp, e);
            else if (Constants.domain().SDWIS.equals(data.getDomain()))
                copySDWISFields(getSampleSDWIS(sm), auxGrp, e);
            auxGrps.remove(domainGrpId);
        }

        copyOrganizations(sm, om, e);
        copySampleItems(sm, om, e);
        ret = copyTests(sm, om, e);
        copyNotes(sm, om);
        copyAuxData(sm, auxGrps, e);

        return ret;
    }

    /**
     * Sets values of fields independent of domain from the corresponding aux
     * data in the list. Adds warnings or throws exception for invalid data.
     */
    private void copyGeneralFields(SampleManager1 sm, HashMap<Integer, AuxDataViewDO> grp,
                                   ValidationErrorsList e) throws Exception {
        String extId;
        SampleDO sample;
        ArrayList<ProjectDO> projects;

        sample = getSample(sm);
        for (AuxDataViewDO data : grp.values()) {
            extId = data.getAnalyteExternalId();
            if (SMPL_COLLECTED_DATE.equals(extId)) {
                sample.setCollectionDate(ReportUtil.getDate(data.getValue()));
            } else if (SMPL_COLLECTED_TIME.equals(extId)) {
                sample.setCollectionTime(ReportUtil.getTime(data.getValue()));
            } else if (SMPL_CLIENT_REF.equals(extId)) {
                sample.setClientReference(data.getValue());
            } else if (PROJECT_NAME.equals(extId) && data.getValue() != null) {
                try {
                    projects = project.fetchActiveByName(data.getValue(), 1);
                    if (projects.size() > 0)
                        sm.project.add(projects.get(0));
                    else
                        e.add(new FormErrorWarning(Messages.get().orderImportError("project",
                                                                                   data.getValue())));
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Missing/invalid project '" + data.getValue() + "'", ex);
                    throw ex;
                }
            }
        }
    }

    /**
     * Sets values of environmental fields from the corresponding aux data in
     * the list. Adds warnings or throws exception for invalid data.
     */
    private void copyEnvironmentalFields(SampleEnvironmentalDO env,
                                         HashMap<Integer, AuxDataViewDO> grp, ValidationErrorsList e) throws Exception {
        Integer p;
        String extId;

        for (AuxDataViewDO data : grp.values()) {
            extId = data.getAnalyteExternalId();
            if (IS_HAZARDOUS.equals(extId)) {
                try {
                    if (data.getValue() != null &&
                        YES.equals(dictionaryCache.getById(new Integer(data.getValue()))
                                                  .getSystemName()))
                        env.setIsHazardous("Y");
                    else
                        env.setIsHazardous("N");
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get().orderImportError("flag hazardous",
                                                                               data.getValue())));
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Missing/invalid flag hazardous '" + data.getValue() +
                                          "'", ex);
                    throw ex;
                }
            } else if (COLLECTOR.equals(extId)) {
                env.setCollector(data.getValue());
            } else if (LOCATION.equals(extId)) {
                env.setLocation(data.getValue());
            } else if (PRIORITY.equals(extId)) {
                try {
                    p = null;
                    if (data.getValue() != null)
                        p = new Integer(data.getValue());
                    env.setPriority(p);
                } catch (Exception ex) {
                    e.add(new FormErrorWarning(Messages.get().orderImportError("priority",
                                                                               data.getValue())));
                }
            } else if (COLLECTOR_PHONE.equals(extId)) {
                env.setCollectorPhone(data.getValue());
            } else if (DESCRIPTION.equals(extId)) {
                env.setDescription(data.getValue());
            } else {
                copyAddressFields(data, e, extId, env.getLocationAddress());
            }
        }
    }

    /**
     * Sets values of private well fields from the corresponding aux data in the
     * list. Adds warnings or throws exception for invalid data.
     */
    private void copyPrivateWellFields(SamplePrivateWellViewDO well,
                                       HashMap<Integer, AuxDataViewDO> grp, ValidationErrorsList e) throws Exception {
        Integer w;
        String extId;

        for (AuxDataViewDO data : grp.values()) {
            extId = data.getAnalyteExternalId();
            if (LOCATION.equals(extId)) {
                well.setLocation(data.getValue());
            } else if (OWNER.equals(extId)) {
                well.setOwner(data.getValue());
            } else if (COLLECTOR.equals(extId)) {
                well.setCollector(data.getValue());
            } else if (WELL_NUMBER.equals(extId)) {
                try {
                    w = null;
                    if (data.getValue() != null)
                        w = new Integer(data.getValue());
                    well.setWellNumber(w);
                } catch (Exception ex) {
                    e.add(new FormErrorWarning(Messages.get().orderImportError("well number",
                                                                               data.getValue())));
                }
            } else {
                copyAddressFields(data, e, extId, well.getLocationAddress());
            }
        }
    }

    /**
     * Sets values of SDWIS fields from the corresponding aux data in the list.
     * Adds warnings or throws exception for invalid data.
     */
    private void copySDWISFields(SampleSDWISViewDO sdwis, HashMap<Integer, AuxDataViewDO> grp,
                                 ValidationErrorsList e) throws Exception {
        Integer dictId;
        String extId;
        PWSDO pwsDO;

        for (AuxDataViewDO data : grp.values()) {
            extId = data.getAnalyteExternalId();
            if (PWS_ID.equals(extId) && data.getValue() != null) {
                try {
                    pwsDO = pws.fetchByNumber0(data.getValue());
                    sdwis.setPwsId(pwsDO.getId());
                    sdwis.setPwsName(pwsDO.getName());
                    sdwis.setPwsNumber0(pwsDO.getNumber0());
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get().orderImportError("pws id",
                                                                               data.getValue())));
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Missing/invalid pws id '" + data.getValue() + "'", ex);
                    throw ex;
                }
            } else if (STATE_LAB_NUM.equals(extId) && data.getValue() != null) {
                sdwis.setStateLabId(new Integer(data.getValue()));
            } else if (FACILITY_ID.equals(extId)) {
                sdwis.setFacilityId(data.getValue());
            } else if (SAMPLE_TYPE.equals(extId)) {
                dictId = null;
                if (data.getValue() != null) {
                    try {
                        dictId = new Integer(data.getValue());
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Missing/invalid dictionary id '" + data.getValue() +
                                              "'", ex);
                        throw ex;
                    }
                    if ( !isInCategory(SDWIS_SAMPLE_TYPE, dictId))
                        e.add(new FormErrorWarning(Messages.get().orderImportError("sample type",
                                                                                   data.getValue())));
                }
                sdwis.setSampleTypeId(dictId);
            } else if (SAMPLE_CAT.equals(extId)) {
                dictId = null;
                if (data.getValue() != null) {
                    try {
                        dictId = new Integer(data.getValue());
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Missing/invalid dictionary id '" + data.getValue() +
                                              "'", ex);
                        throw ex;
                    }
                    if ( !isInCategory(SDWIS_SAMPLE_CATEGORY, dictId))
                        e.add(new FormErrorWarning(Messages.get()
                                                           .orderImportError("sample category",
                                                                             data.getValue())));
                }
                sdwis.setSampleCategoryId(dictId);
            } else if (SAMPLE_PT_ID.equals(extId)) {
                sdwis.setSamplePointId(data.getValue());
            } else if (LOCATION.equals(extId)) {
                sdwis.setLocation(data.getValue());
            } else if (COLLECTOR.equals(extId)) {
                sdwis.setCollector(data.getValue());
            }
        }
    }

    /**
     * Sets values of address fields from the aux data. Adds warnings for
     * invalid data.
     */
    private void copyAddressFields(AuxDataViewDO data, ValidationErrorsList e, String extId,
                                   AddressDO addr) throws Exception {
        if (LOC_MULT_UNIT.equals(extId)) {
            addr.setMultipleUnit(data.getValue());
        } else if (LOC_STREET_ADDRESS.equals(extId)) {
            addr.setStreetAddress(data.getValue());
        } else if (LOC_CITY.equals(extId)) {
            addr.setCity(data.getValue());
        } else if (LOC_STATE.equals(extId) && data.getValue() != null) {
            if (isInCategory(STATE, data.getValue()))
                addr.setState(data.getValue());
            else
                e.add(new FormErrorWarning(Messages.get().orderImportError(STATE, data.getValue())));
        } else if (LOC_ZIP_CODE.equals(extId)) {
            addr.setZipCode(data.getValue());
        } else if (LOC_COUNTRY.equals(extId) && data.getValue() != null) {
            if (isInCategory(COUNTRY, data.getValue()))
                addr.setCountry(data.getValue());
            else
                e.add(new FormErrorWarning(Messages.get()
                                                   .orderImportError(COUNTRY, data.getValue())));
        }
    }

    /**
     * Add to the sample, the aux groups specified in the order
     */
    private void copyAuxData(SampleManager1 sm,
                             HashMap<Integer, HashMap<Integer, AuxDataViewDO>> grps,
                             ValidationErrorsList e) throws Exception {
        ArrayList<AuxDataViewDO> auxiliary;

        /*
         * fields for the aux group present in the order but not in the sample
         * are fetched and aux data for them is added to the sample
         */
        if (grps.size() > 0) {
            auxiliary = getAuxilliary(sm);
            if (auxiliary == null) {
                auxiliary = new ArrayList<AuxDataViewDO>();
                setAuxilliary(sm, auxiliary);
            }
            auxDataHelper.addAuxGroups(auxiliary, grps);

            /*
             * set negative ids in the newly added aux data
             */
            for (AuxDataViewDO aux : auxiliary) {
                if (aux.getId() == null)
                    aux.setId(sm.getNextUID());
            }
        }
    }

    /**
     * Loads sample items in the SampleManager from the containers in the order.
     * Resets sequences in the items to match the containers. Also resets the
     * next item sequence in the sample to be greater than the sequence of the
     * last item.
     */
    private void copySampleItems(SampleManager1 sm, OrderManager om, ValidationErrorsList e) throws Exception {
        int i;
        OrderContainerDO oc;
        OrderContainerManager ocm;
        DictionaryDO dict;
        SampleItemViewDO item;
        ArrayList<SampleItemViewDO> items;

        ocm = om.getContainers();
        if (ocm.count() == 0)
            return;

        /*
         * if a sample item is found at the sequence specified in a container
         * then it's filled from the container otherwise a new sample item is
         * created and filled
         */
        items = getItems(sm);
        for (i = 0; i < ocm.count(); i++ ) {
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

            oc = ocm.getContainerAt(i);
            if (oc.getContainerId() != null &&
                !DataBaseUtil.isSame(item.getContainerId(), oc.getContainerId())) {
                try {
                    dict = dictionaryCache.getById(oc.getContainerId());
                    if ("Y".equals(dict.getIsActive())) {
                        item.setContainer(dict.getEntry());
                        item.setContainerId(oc.getContainerId());
                    } else {
                        e.add(new FormErrorWarning(Messages.get().orderImportError("container",
                                                                                   dict.getEntry())));
                    }
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get().orderImportError("container id = ",
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
                    if ("Y".equals(dict.getIsActive())) {
                        item.setTypeOfSampleId(oc.getTypeOfSampleId());
                        item.setTypeOfSample(dict.getEntry());
                    } else {
                        e.add(new FormErrorWarning(Messages.get().orderImportError("sample type",
                                                                                   dict.getEntry())));
                    }
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get()
                                                       .orderImportError("sample type id = ",
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
            for (i = ocm.count(); i < items.size(); i++ )
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
    private SampleTestReturnVO copyTests(SampleManager1 sm, OrderManager om, ValidationErrorsList e) throws Exception {
        int i, j, min;
        OrderTestManager otm;
        SampleItemViewDO item;
        SampleTestReturnVO ret;
        ArrayList<Integer> analyteIds;
        ArrayList<SampleTestRequestVO> tests;
        HashMap<Integer, SampleItemViewDO> items;

        ret = new SampleTestReturnVO();
        ret.setManager(sm);
        ret.setErrors(e);

        otm = om.getTests();
        if (otm.count() == 0)
            return ret;

        /*
         * If there are no sample items then add one. Otherwise, add all tests
         * with unmatched sequence to the sample item with the lowest sequence.
         */
        if (getItems(sm) == null || getItems(sm).size() == 0) {
            item = new SampleItemViewDO();
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

        tests = new ArrayList<SampleTestRequestVO>();
        for (i = 0; i < otm.count(); i++ ) {
            item = items.get(otm.getTestAt(i).getItemSequence());
            if (item == null)
                item = items.get(min);

            analyteIds = null;
            if (otm.getAnalytesAt(i).count() > 0) {
                analyteIds = new ArrayList<Integer>();
                for (j = 0; j < otm.getAnalytesAt(i).count(); j++ )
                    analyteIds.add(otm.getAnalytesAt(i).getAnalyteAt(j).getAnalyteId());
            }
            tests.add(new SampleTestRequestVO(item.getId(),
                                              otm.getTestAt(i).getTestId(),
                                              null,
                                              null,
                                              null,
                                              false,
                                              analyteIds));
        }

        ret = sampleManager1.addTests(sm, tests);
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
    private void copyOrganizations(SampleManager1 sm, OrderManager om, ValidationErrorsList e) throws Exception {
        String attention;
        OrganizationDO repOrg, billOrg, secOrg;
        OrderOrganizationManager orgm;
        OrderOrganizationViewDO otmpOrg, orepOrg, obillOrg;
        ArrayList<OrderOrganizationViewDO> osecOrgs;
        SampleOrganizationViewDO samOrg;
        SamplePrivateWellViewDO well;

        orgm = om.getOrganizations();
        orepOrg = null;
        obillOrg = null;
        osecOrgs = new ArrayList<OrderOrganizationViewDO>();

        /*
         * find out if organizations of various types are specified in the order
         */
        for (int i = 0; i < orgm.count(); i++ ) {
            otmpOrg = orgm.getOrganizationAt(i);
            if (Constants.dictionary().ORG_REPORT_TO.equals(otmpOrg.getTypeId()))
                orepOrg = otmpOrg;
            else if (Constants.dictionary().ORG_BILL_TO.equals(otmpOrg.getTypeId()))
                obillOrg = otmpOrg;
            else if (Constants.dictionary().ORG_SECOND_REPORT_TO.equals(otmpOrg.getTypeId()))
                osecOrgs.add(otmpOrg);
        }

        /*
         * if report-to is null, use ship-to as the report-to
         */
        if (orepOrg != null) {
            repOrg = createOrganization(orepOrg);
            attention = orepOrg.getOrganizationAttention();
        } else {
            repOrg = om.getOrder().getOrganization();
            attention = om.getOrder().getOrganizationAttention();
        }

        /*
         * for private well domain, the report-to is set in the domain record
         * itself and not linked through sample organization
         */
        if (Constants.domain().PRIVATEWELL.equals(getSample(sm).getDomain())) {
            well = getSamplePrivateWell(sm);
            well.setOrganizationId(repOrg.getId());
            well.setOrganization(repOrg);
            well.setReportToAttention(attention);
        } else {
            samOrg = sm.organization.add(repOrg);
            samOrg.setOrganizationAttention(attention);
            samOrg.setTypeId(Constants.dictionary().ORG_REPORT_TO);
        }
        checkIsHoldRefuseSample(repOrg, e);

        /*
         * set the bill-to if it's different from report-to
         */
        if (obillOrg != null) {
            if ( !obillOrg.getOrganizationId().equals(repOrg.getId())) {
                billOrg = createOrganization(obillOrg);
                samOrg = sm.organization.add(billOrg);
                samOrg.setOrganizationAttention(obillOrg.getOrganizationAttention());
                samOrg.setTypeId(Constants.dictionary().ORG_BILL_TO);
                checkIsHoldRefuseSample(billOrg, e);
            }
        }

        /*
         * add secondary report-to organizations if any were specified
         */
        for (OrderOrganizationViewDO osecOrg : osecOrgs) {
            if ( !osecOrg.getOrganizationId().equals(repOrg.getId())) {
                secOrg = createOrganization(osecOrg);
                samOrg = sm.organization.add(secOrg);
                samOrg.setOrganizationAttention(osecOrg.getOrganizationAttention());
                samOrg.setTypeId(Constants.dictionary().ORG_SECOND_REPORT_TO);
                checkIsHoldRefuseSample(secOrg, e);
            }
        }
    }

    /**
     * Adds an internal note to the sample from the "sample" note defined in the
     * order.
     */
    private void copyNotes(SampleManager1 sm, OrderManager om) throws Exception {
        NoteViewDO note;
        NoteManager nm;
        SystemUserVO user;

        nm = om.getSampleNotes();
        if (nm.count() == 0)
            return;

        note = new NoteViewDO();
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        note.setIsExternal("N");
        user = userCache.getSystemUser();
        note.setSystemUserId(user.getId());
        note.setSystemUser(user.getLoginName());
        note.setSubject(Messages.get().orderNoteSubject());
        note.setText(nm.getNoteAt(0).getText());
        if (getSampleInternalNotes(sm) != null)
            getSampleInternalNotes(sm).add(0, note);
        else
            addSampleInternalNote(sm, note);
    }

    /**
     * Returns the id of an aux field group. This id is specific to a group of
     * aux prompts that mimic sample specific fields.
     */
    private Integer getDomainAuxGroupId(SampleDO data) throws Exception {
        String name;
        SystemVariableDO sys;
        AuxFieldGroupDO aux;

        name = null;
        if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain()))
            name = SAMPLE_ENV_AUX_DATA;
        else if (Constants.domain().PRIVATEWELL.equals(data.getDomain()))
            name = SAMPLE_WELL_AUX_DATA;
        else if (Constants.domain().SDWIS.equals(data.getDomain()))
            name = SAMPLE_SDWIS_AUX_DATA;

        /*
         * we don't want to use a hard-coded reference to aux group. So we use a
         * system variable that that points to the aux group.
         */
        try {
            sys = systemVariable.fetchByName(name);
        } catch (NotFoundException ex) {
            return null;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Missing/invalid system variable '" + name + "'", ex);
            throw ex;
        }

        try {
            aux = auxFieldGroup.fetchActiveByName(sys.getValue());
            return aux.getId();
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Missing/invalid aux field group '" + sys.getValue() + "'", ex);
            throw ex;
        }
    }

    /**
     * Returns true if a dictionary with the specified entry can be found in the
     * category specified by the system name, and false otherwise
     */
    private boolean isInCategory(String systemName, String entry) throws Exception {
        ArrayList<DictionaryDO> entries;

        entries = categoryCache.getBySystemName(systemName).getDictionaryList();
        for (DictionaryDO data : entries) {
            if (DataBaseUtil.isSame(entry, data.getEntry()))
                return true;
        }
        return false;
    }

    /**
     * Returns true if a dictionary with the specified id can be found in the
     * category specified by the system name, and false otherwise
     */
    private boolean isInCategory(String systemName, Integer id) throws Exception {
        ArrayList<DictionaryDO> entries;

        entries = categoryCache.getBySystemName(systemName).getDictionaryList();
        for (DictionaryDO data : entries) {
            if (DataBaseUtil.isSame(id, data.getId()))
                return true;
        }

        return false;
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
     * Adds a warning if samples from this organization are to be held/refused.
     */
    private void checkIsHoldRefuseSample(OrganizationDO org, ValidationErrorsList e) throws Exception {
        try {
            organizationParameter.fetchByOrgIdAndDictSystemName(org.getId(), ORG_HOLD_SAMPLE);
            e.add(new FormErrorWarning(Messages.get().orgMarkedAsHoldRefuseSample(org.getName())));
        } catch (NotFoundException ex) {
            // ignore
        }
    }
}