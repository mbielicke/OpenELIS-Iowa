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

import static org.openelis.manager.OrderManager1Accessor.getAnalytes;
import static org.openelis.manager.OrderManager1Accessor.getAuxilliary;
import static org.openelis.manager.OrderManager1Accessor.getContainers;
import static org.openelis.manager.OrderManager1Accessor.getOrganizations;
import static org.openelis.manager.OrderManager1Accessor.getSampleNote;
import static org.openelis.manager.OrderManager1Accessor.getTests;
import static org.openelis.manager.SampleManager1Accessor.addItem;
import static org.openelis.manager.SampleManager1Accessor.addOrganization;
import static org.openelis.manager.SampleManager1Accessor.addSampleInternalNote;
import static org.openelis.manager.SampleManager1Accessor.getItems;
import static org.openelis.manager.SampleManager1Accessor.getSample;
import static org.openelis.manager.SampleManager1Accessor.getSampleInternalNotes;
import static org.openelis.manager.SampleManager1Accessor.getSamplePrivateWell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrderTestAnalyteViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.manager.OrderManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;

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
    private OrganizationParameterBean organizationParameter;

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
}