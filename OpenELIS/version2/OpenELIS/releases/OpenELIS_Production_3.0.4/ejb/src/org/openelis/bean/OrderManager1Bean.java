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

import static org.openelis.manager.OrderManager1Accessor.addAnalyte;
import static org.openelis.manager.OrderManager1Accessor.addAuxilliary;
import static org.openelis.manager.OrderManager1Accessor.addContainer;
import static org.openelis.manager.OrderManager1Accessor.addFill;
import static org.openelis.manager.OrderManager1Accessor.addInternalNote;
import static org.openelis.manager.OrderManager1Accessor.addItem;
import static org.openelis.manager.OrderManager1Accessor.addOrganization;
import static org.openelis.manager.OrderManager1Accessor.addReceipt;
import static org.openelis.manager.OrderManager1Accessor.getAnalytes;
import static org.openelis.manager.OrderManager1Accessor.getAuxilliary;
import static org.openelis.manager.OrderManager1Accessor.getContainers;
import static org.openelis.manager.OrderManager1Accessor.getCustomerNote;
import static org.openelis.manager.OrderManager1Accessor.getFills;
import static org.openelis.manager.OrderManager1Accessor.getInternalNotes;
import static org.openelis.manager.OrderManager1Accessor.getItems;
import static org.openelis.manager.OrderManager1Accessor.getOrder;
import static org.openelis.manager.OrderManager1Accessor.getOrganizations;
import static org.openelis.manager.OrderManager1Accessor.getRecurrence;
import static org.openelis.manager.OrderManager1Accessor.getRemoved;
import static org.openelis.manager.OrderManager1Accessor.getSampleNote;
import static org.openelis.manager.OrderManager1Accessor.getShippingNote;
import static org.openelis.manager.OrderManager1Accessor.getTests;
import static org.openelis.manager.OrderManager1Accessor.setAnalytes;
import static org.openelis.manager.OrderManager1Accessor.setAuxilliary;
import static org.openelis.manager.OrderManager1Accessor.setContainers;
import static org.openelis.manager.OrderManager1Accessor.setCustomerNote;
import static org.openelis.manager.OrderManager1Accessor.setFills;
import static org.openelis.manager.OrderManager1Accessor.setInternalNotes;
import static org.openelis.manager.OrderManager1Accessor.setItems;
import static org.openelis.manager.OrderManager1Accessor.setOrder;
import static org.openelis.manager.OrderManager1Accessor.setOrganizations;
import static org.openelis.manager.OrderManager1Accessor.setReceipts;
import static org.openelis.manager.OrderManager1Accessor.setRecurrence;
import static org.openelis.manager.OrderManager1Accessor.setRemoved;
import static org.openelis.manager.OrderManager1Accessor.setSampleNote;
import static org.openelis.manager.OrderManager1Accessor.setShippingNote;
import static org.openelis.manager.OrderManager1Accessor.setTests;
import static org.openelis.manager.SampleManager1Accessor.getOrganizations;
import static org.openelis.manager.SampleManager1Accessor.getSample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderReturnVO;
import org.openelis.domain.OrderTestAnalyteViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
public class OrderManager1Bean {

    @Resource
    private SessionContext         ctx;

    @EJB
    private LockBean               lock;

    @EJB
    private OrderBean              order;

    @EJB
    private OrderOrganizationBean  orderOrganization;

    @EJB
    private OrderItemBean          orderItem;

    @EJB
    private InventoryXUseBean      orderFill;

    @EJB
    private InventoryXPutBean      orderReceipt;

    @EJB
    private OrderContainerBean     orderContainer;

    @EJB
    private OrderTestBean          orderTest;

    @EJB
    private TestManagerBean        testManager;

    @EJB
    private OrderTestAnalyteBean   orderTestAnalyte;

    @EJB
    private NoteBean               note;

    @EJB
    private AuxDataBean            auxdata;

    @EJB
    private OrderRecurrenceBean    orderRecurrence;

    @EJB
    private PanelBean              panel;

    @EJB
    private AuxDataHelperBean      auxDataHelper;

    @EJB
    private OrderTestHelperBean    orderTestHelper;

    @EJB
    private OrganizationBean       organization;

    @EJB
    private DictionaryCacheBean    dictionary;

    @EJB
    private InventoryItemCacheBean inventoryItem;

    private static final Logger    log = Logger.getLogger("openelis");

    public OrderManager1 getInstance(String type) throws Exception {
        OrderManager1 om;
        OrderViewDO o;
        Datetime now;

        // order
        now = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
        om = new OrderManager1();

        o = new OrderViewDO();
        o.setStatusId(Constants.dictionary().ORDER_STATUS_PENDING);
        o.setOrderedDate(now);
        o.setRequestedBy(User.getName(ctx));
        setOrder(om, o);
        if (Constants.order().SEND_OUT.equals(type))
            o.setOrganization(new OrganizationDO());
        else if ( !Constants.order().INTERNAL.equals(type) &&
                 !Constants.order().VENDOR.equals(type))
            throw new InconsistencyException("Specified type is invalid");
        o.setType(type);

        return om;
    }

    /**
     * Returns an order manager for specified primary id and requested load
     * elements
     */
    public OrderManager1 fetchById(Integer orderId, OrderManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<OrderManager1> oms;

        ids = new ArrayList<Integer>(1);
        ids.add(orderId);
        oms = fetchByIds(ids, false, elements);
        return oms.size() == 0 ? null : oms.get(0);
    }

    /**
     * Returns order managers for specified primary ids and requested load
     * elements
     */
    public ArrayList<OrderManager1> fetchByIds(ArrayList<Integer> orderIds,
                                               OrderManager1.Load... elements) throws Exception {
        return fetchByIds(orderIds, false, elements);
    }

    /**
     * Returns an order manager based on the specified query and requested load
     * elements
     */
    public ArrayList<OrderManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                 OrderManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdNameVO vo : order.query(fields, first, max))
            ids.add(vo.getId());
        return fetchByIds(ids, false, elements);
    }

    public OrderManager1 fetchWith(OrderManager1 om, OrderManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids, ids2;
        EnumSet<OrderManager1.Load> el;

        if (elements != null)
            el = EnumSet.copyOf(Arrays.asList(elements));
        else
            return om;

        /*
         * various lists for each order
         */
        ids = new ArrayList<Integer>(1);
        ids.add(getOrder(om).getId());

        if (el.contains(OrderManager1.Load.ITEMS)) {
            setItems(om, null);
            for (OrderItemViewDO data : orderItem.fetchByOrderIds(ids))
                addItem(om, data);

            setFills(om, null);
            for (InventoryXUseViewDO data : orderFill.fetchByOrderIds(ids))
                addFill(om, data);

            setReceipts(om, null);
            for (InventoryXPutViewDO data : orderReceipt.fetchByOrderIds(ids))
                addReceipt(om, data);
        }

        if (el.contains(OrderManager1.Load.ORGANIZATION)) {
            setOrganizations(om, null);
            for (OrderOrganizationViewDO data : orderOrganization.fetchByOrderIds(ids))
                addOrganization(om, data);
        }

        if (el.contains(OrderManager1.Load.SAMPLE_DATA)) {
            setContainers(om, null);
            for (OrderContainerDO data : orderContainer.fetchByOrderIds(ids))
                addContainer(om, data);

            setAuxilliary(om, null);
            for (AuxDataViewDO data : auxdata.fetchByIds(ids, Constants.table().ORDER))
                addAuxilliary(om, data);

            /*
             * build level 2, everything is based on order test ids
             */
            ids2 = new ArrayList<Integer>();

            setTests(om, null);
            for (OrderTestViewDO data : orderTest.fetchByOrderIds(ids)) {
                addTest(om, data.getId(), true, data.getItemSequence());
                ids2.add(data.getId());
            }

            setAnalytes(om, null);
            for (OrderTestAnalyteViewDO data : orderTestAnalyte.fetchByOrderTestIds(ids2))
                addAnalyte(om, data);
        }

        if (el.contains(OrderManager1.Load.RECURRENCE)) {
            setRecurrence(om, null);
            for (OrderRecurrenceDO data : orderRecurrence.fetchByOrderIds(ids))
                setRecurrence(om, data);
        }

        return om;
    }

    /**
     * Returns a locked order manager with specified order id
     */
    @RolesAllowed("order-update")
    public OrderManager1 fetchForUpdate(Integer orderId) throws Exception {
        return fetchForUpdate(orderId,
                              OrderManager1.Load.ITEMS,
                              OrderManager1.Load.ORGANIZATION,
                              OrderManager1.Load.SAMPLE_DATA,
                              OrderManager1.Load.RECURRENCE);
    }

    /**
     * Returns a locked order manager with specified order id and requested load
     * elements
     */
    @RolesAllowed("order-update")
    public OrderManager1 fetchForUpdate(Integer orderId, OrderManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<OrderManager1> oms;

        ids = new ArrayList<Integer>(1);
        ids.add(orderId);
        oms = fetchForUpdate(ids, elements);
        return oms.size() == 0 ? null : oms.get(0);
    }

    /**
     * Returns a list of locked order managers with specified order ids and
     * requested load elements
     */
    @RolesAllowed("order-update")
    public ArrayList<OrderManager1> fetchForUpdate(ArrayList<Integer> orderIds,
                                                   OrderManager1.Load... elements) throws Exception {
        lock.lock(Constants.table().ORDER, orderIds);
        return fetchByIds(orderIds, true, elements);
    }

    /**
     * Unlocks and returns a order manager with specified order id and requested
     * load elements
     */
    @RolesAllowed({"order-add", "order-update"})
    public OrderManager1 unlock(Integer orderId, OrderManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<OrderManager1> sms;

        ids = new ArrayList<Integer>(1);
        ids.add(orderId);
        sms = unlock(ids, elements);
        return sms.size() == 0 ? null : sms.get(0);
    }

    /**
     * Unlocks and returns list of order managers with specified order ids and
     * requested load elements
     */
    @RolesAllowed({"order-add", "order-update"})
    public ArrayList<OrderManager1> unlock(ArrayList<Integer> orderIds,
                                           OrderManager1.Load... elements) throws Exception {
        lock.unlock(Constants.table().ORDER, orderIds);
        return fetchByIds(orderIds, false, elements);
    }

    /**
     * duplicates the order with the given order ID and commits the new order
     */
    public void recur(Integer id) throws Exception {
        StringBuffer noteText;
        OrderReturnVO order;
        OrderManager1 om;
        NoteViewDO note;
        ArrayList<NoteViewDO> notes;

        order = duplicate(id, false, true);
        om = order.getManager();

        /*
         * set duplication errors/warnings as an internal note
         */
        if (order.getErrors() != null && order.getErrors().getErrorList() != null &&
            order.getErrors().getErrorList().size() > 0) {
            noteText = new StringBuffer();
            for (Exception e : order.getErrors().getErrorList()) {
                noteText.append(e.getMessage()).append("\n");
            }
            note = new NoteViewDO();
            note.setText(noteText.toString());
            note.setIsExternal("N");
            note.setSubject(Messages.get().order_recurError());
            note.setSystemUserId(0);
            notes = new ArrayList<NoteViewDO>();
            notes.add(note);
            setInternalNotes(om, notes);
        }
        update(om, true);
    }

    /**
     * duplicates the order with the given order ID and returns the new order
     * manager
     */
    public OrderReturnVO duplicate(Integer id) throws Exception {
        return duplicate(id, false, false);
    }

    /**
     * duplicates the order with the given order ID, with or without sample
     * notes, and returns the new order manager
     */
    public OrderReturnVO duplicate(Integer id, boolean sampleNotes, boolean forRecurrence) throws Exception {
        Integer oldId, prevGroupId;
        Datetime now;
        OrderManager1 om;
        OrderReturnVO ret;
        DictionaryDO dict;
        InventoryItemDO item;
        ValidationErrorsList errors;
        ArrayList<Integer> ids;
        ArrayList<OrderManager1> oms;
        ArrayList<OrderOrganizationViewDO> orgs;
        ArrayList<AuxDataViewDO> aux;
        ArrayList<OrderTestViewDO> tests;
        ArrayList<OrderTestAnalyteViewDO> analytes;
        ArrayList<OrderItemViewDO> items;
        HashMap<Integer, Integer> tids;

        /*
         * fetchByIds is called here instead of fetchById because in this case,
         * the order test analytes need to be merged with the original test
         * analytes, and we indicate this by passing true for the argument
         * isUpdate, just like in fetchForUpdate
         */
        ids = new ArrayList<Integer>(1);
        ids.add(id);
        oms = fetchByIds(ids,
                         !forRecurrence,
                         OrderManager1.Load.SAMPLE_DATA,
                         OrderManager1.Load.ORGANIZATION,
                         OrderManager1.Load.ITEMS);

        om = oms.get(0);
        errors = new ValidationErrorsList();
        if (forRecurrence)
            getOrder(om).setParentOrderId(getOrder(om).getId());
        getOrder(om).setId(null);
        getOrder(om).setStatusId(Constants.dictionary().ORDER_STATUS_PENDING);
        now = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);
        getOrder(om).setOrderedDate(now);
        if ( !forRecurrence)
            getOrder(om).setRequestedBy(User.getName(ctx));

        if ( !"Y".equals(getOrder(om).getOrganization().getIsActive())) {
            if (forRecurrence) {
                getOrder(om).setStatusId(Constants.dictionary().ORDER_STATUS_ERROR);
            }
            errors.add(new FormErrorWarning(Messages.get()
                                                    .order_inactiveOrganizationWarning(getOrder(om).getOrganization()
                                                                                                   .getName())));
            getOrder(om).setOrganization(null);
            getOrder(om).setOrganizationId(null);
        }

        if (getOrganizations(om) != null) {
            orgs = new ArrayList<OrderOrganizationViewDO>();
            for (OrderOrganizationViewDO data : getOrganizations(om)) {
                if ("Y".equals(data.getOrganizationIsActive())) {
                    data.setId(null);
                    data.setOrderId(null);
                    orgs.add(data);
                } else {
                    if (forRecurrence) {
                        getOrder(om).setStatusId(Constants.dictionary().ORDER_STATUS_ERROR);
                    }
                    errors.add(new FormErrorWarning(Messages.get()
                                                            .order_inactiveOrganizationWarning(data.getOrganizationName())));
                }
            }
            if (orgs.size() > 0)
                setOrganizations(om, orgs);
            else
                setOrganizations(om, null);
        }

        if (getItems(om) != null) {
            items = new ArrayList<OrderItemViewDO>();
            for (OrderItemViewDO data : getItems(om)) {
                item = inventoryItem.getById(data.getInventoryItemId());
                if ("N".equals(item.getIsActive())) {
                    if (forRecurrence)
                        getOrder(om).setStatusId(Constants.dictionary().ORDER_STATUS_ERROR);
                    errors.add(new FormErrorWarning(Messages.get()
                                                            .order_inactiveItemWarning(item.getName())));
                    continue;
                }
                data.setId(null);
                data.setOrderId(null);
                items.add(data);
            }
            if (items.size() > 0)
                setItems(om, items);
            else
                setItems(om, null);
        }

        setFills(om, null);
        setReceipts(om, null);

        if (getShippingNote(om) != null) {
            getShippingNote(om).setId(null);
            getShippingNote(om).setReferenceId(null);
            getShippingNote(om).setTimestamp(null);
        }

        if (getCustomerNote(om) != null) {
            getCustomerNote(om).setId(null);
            getCustomerNote(om).setReferenceId(null);
            getCustomerNote(om).setTimestamp(null);
        }

        setInternalNotes(om, null);

        if ( !sampleNotes)
            setSampleNote(om, null);
        if (getSampleNote(om) != null) {
            getSampleNote(om).setId(null);
            getSampleNote(om).setReferenceId(null);
            getSampleNote(om).setTimestamp(null);
        }

        if (getContainers(om) != null) {
            for (OrderContainerDO data : getContainers(om)) {
                dict = dictionary.getById(data.getContainerId());
                if ("N".equals(dict.getIsActive())) {
                    if (forRecurrence)
                        getOrder(om).setStatusId(Constants.dictionary().ORDER_STATUS_ERROR);
                    errors.add(new FormErrorWarning(Messages.get()
                                                            .order_inactiveContainerWarning(dict.getEntry())));
                }
                dict = dictionary.getById(data.getTypeOfSampleId());
                if ("N".equals(dict.getIsActive())) {
                    if (forRecurrence)
                        getOrder(om).setStatusId(Constants.dictionary().ORDER_STATUS_ERROR);
                    errors.add(new FormErrorWarning(Messages.get()
                                                            .order_inactiveSampleTypeWarning(dict.getEntry())));
                    data.setTypeOfSampleId(null);
                }
                data.setId(null);
                data.setOrderId(null);
            }
        }

        if (getAuxilliary(om) != null) {
            aux = new ArrayList<AuxDataViewDO>();
            prevGroupId = null;
            for (AuxDataViewDO data : getAuxilliary(om)) {
                /*
                 * don't duplicate the aux group if it is inactive and warn the
                 * user
                 */
                if ("Y".equals(data.getAuxFieldGroupIsActive())) {
                    data.setId(null);
                    data.setReferenceId(null);
                    aux.add(data);
                } else {
                    if (forRecurrence) {
                        getOrder(om).setStatusId(Constants.dictionary().ORDER_STATUS_ERROR);
                    }
                    if ( !data.getAuxFieldGroupId().equals(prevGroupId)) {
                        errors.add(new FormErrorWarning(Messages.get()
                                                                .order_inactiveAuxGroupWarning(data.getAuxFieldGroupName())));
                    }
                }
                prevGroupId = data.getAuxFieldGroupId();
            }
            if (aux.size() > 0)
                setAuxilliary(om, aux);
            else
                setAuxilliary(om, null);
        }

        tids = new HashMap<Integer, Integer>();
        if (getTests(om) != null) {
            tests = new ArrayList<OrderTestViewDO>();
            for (OrderTestViewDO data : getTests(om)) {
                if ("Y".equals(data.getIsActive())) {
                    oldId = data.getId();
                    data.setId(om.getNextUID());
                    tids.put(oldId, data.getId());
                    tests.add(data);
                } else {
                    if (forRecurrence) {
                        getOrder(om).setStatusId(Constants.dictionary().ORDER_STATUS_ERROR);
                        continue;
                    }
                    errors.add(new FormErrorWarning(Messages.get()
                                                            .order_inactiveTestWarning(data.getTestName(),
                                                                                       data.getMethodName())));
                }
            }
            if (tests.size() > 0)
                setTests(om, tests);
            else
                setTests(om, null);
        }

        if (getAnalytes(om) != null) {
            analytes = new ArrayList<OrderTestAnalyteViewDO>();
            for (OrderTestAnalyteViewDO data : getAnalytes(om)) {
                if (tids.get(data.getOrderTestId()) != null) {
                    data.setId(om.getNextUID());
                    data.setOrderTestId(tids.get(data.getOrderTestId()));
                    analytes.add(data);
                }
            }
            if (analytes.size() > 0)
                setAnalytes(om, analytes);
            else
                setAnalytes(om, null);
        }
        setRecurrence(om, null);

        ret = new OrderReturnVO();
        ret.setManager(om);
        ret.setErrors(errors);
        return ret;
    }

    /**
     * Adds/updates the order and related records in the database. The records
     * are validated before add/update and the order record must have a lock
     * record if it has an id.
     */
    @RolesAllowed({"order-add", "order-update"})
    public OrderManager1 update(OrderManager1 om, boolean ignoreWarnings) throws Exception {
        ArrayList<OrderManager1> oms;

        oms = new ArrayList<OrderManager1>(1);
        oms.add(om);
        update(oms, ignoreWarnings);
        return oms.get(0);
    }

    /**
     * Adds/updates all the orders and related records in the database. All the
     * records are validated before add/update and the order records must each
     * have a lock record if they have an id.
     */
    @RolesAllowed({"order-add", "order-update"})
    public ArrayList<OrderManager1> update(ArrayList<OrderManager1> oms, boolean ignoreWarnings) throws Exception {
        int i;
        Integer so, tmpid;
        NoteViewDO nd;
        OrderRecurrenceDO or;
        OrderTestAnalyteViewDO ota;
        HashSet<Integer> ids;
        HashMap<Integer, TestManager> tms;
        ArrayList<Integer> locks;
        HashMap<Integer, Integer> tmap;

        /*
         * validation needs test manager. Build a list of order-test test ids to
         * fetch test managers. Also build a test map for permission check.
         */
        ids = new HashSet<Integer>();
        for (OrderManager1 om : oms) {
            if (getTests(om) != null) {
                for (OrderTestViewDO an : getTests(om))
                    ids.add(an.getTestId());
            }
        }

        tms = null;
        if (ids.size() > 0) {
            tms = new HashMap<Integer, TestManager>();
            for (TestManager tm : testManager.fetchByIds(new ArrayList<Integer>(ids)))
                tms.put(tm.getTest().getId(), tm);
        }
        validate(oms, tms, ignoreWarnings);

        /*
         * check all the locks
         */
        ids.clear();
        for (OrderManager1 om : oms) {
            if (getOrder(om).getId() != null)
                ids.add(getOrder(om).getId());
        }
        if (ids.size() > 0) {
            locks = new ArrayList<Integer>(ids);
            lock.validateLock(Constants.table().ORDER, locks);
        } else {
            locks = null;
        }
        ids = null;

        /*
         * the front code uses negative ids (temporary ids) to link order tests
         * and order test analytes. The negative ids are mapped to actual
         * database ids through tmap.
         */
        tmap = new HashMap<Integer, Integer>();
        for (OrderManager1 om : oms) {
            tmap.clear();

            if (getAnalytes(om) != null) {
                i = 0;
                /*
                 * For adding and updating, we only keep checked order test
                 * analytes. Existing unchecked analytes are removed from the
                 * database.
                 */
                while (i < getAnalytes(om).size()) {
                    ota = getAnalytes(om).get(i);
                    if ("Y".equals(ota.getTestAnalyteIsReportable())) {
                        i++ ;
                        continue;
                    }
                    getAnalytes(om).remove(i);
                    if (ota.getId() > 0) {
                        if (getRemoved(om) == null)
                            setRemoved(om, new ArrayList<DataObject>());
                        getRemoved(om).add(ota);
                    }
                }
            }

            /*
             * do not add an empty recurrence record; remove an existing record
             * if it is now empty
             */
            or = getRecurrence(om);
            if (or != null && orderRecurrence.isEmpty(or)) {
                if (or.getId() != null) {
                    if (getRemoved(om) == null)
                        setRemoved(om, new ArrayList<DataObject>());
                    getRemoved(om).add(or);
                }
                setRecurrence(om, null);
            }

            /*
             * go through remove list and delete all the unwanted records
             */
            if (getRemoved(om) != null) {
                for (DataObject data : getRemoved(om)) {
                    if (data instanceof OrderOrganizationViewDO)
                        orderOrganization.delete( ((OrderOrganizationViewDO)data));
                    else if (data instanceof OrderItemViewDO)
                        orderItem.delete( ((OrderItemViewDO)data));
                    else if (data instanceof InventoryXUseViewDO)
                        orderFill.delete( ((InventoryXUseViewDO)data));
                    else if (data instanceof InventoryXPutViewDO)
                        orderReceipt.delete( ((InventoryXPutViewDO)data));
                    else if (data instanceof OrderContainerDO)
                        orderContainer.delete( ((OrderContainerDO)data));
                    else if (data instanceof OrderTestViewDO)
                        orderTest.delete( ((OrderTestViewDO)data));
                    else if (data instanceof OrderTestAnalyteViewDO)
                        orderTestAnalyte.delete( ((OrderTestAnalyteViewDO)data));
                    else if (data instanceof NoteViewDO)
                        note.delete( ((NoteViewDO)data));
                    else if (data instanceof AuxDataViewDO)
                        auxdata.delete( ((AuxDataViewDO)data));
                    else if (data instanceof OrderRecurrenceDO)
                        orderRecurrence.delete((OrderRecurrenceDO)data);
                    else
                        throw new Exception("ERROR: DataObject passed for removal is of unknown type");
                }
            }
            // add/update order
            if (getOrder(om).getId() == null)
                order.add(getOrder(om));
            else
                order.update(getOrder(om));

            if (getOrganizations(om) != null) {
                for (OrderOrganizationViewDO data : getOrganizations(om)) {
                    if (data.getId() == null) {
                        data.setOrderId(getOrder(om).getId());
                        orderOrganization.add(data);
                    } else {
                        orderOrganization.update(data);
                    }
                }
            }

            so = 0;
            if (getAuxilliary(om) != null) {
                for (AuxDataViewDO data : getAuxilliary(om)) {
                    so++ ;
                    if ( !DataBaseUtil.isSame(so, data.getSortOrder()))
                        data.setSortOrder(so);
                    if (data.getId() == null) {
                        data.setReferenceTableId(Constants.table().ORDER);
                        data.setReferenceId(getOrder(om).getId());
                        auxdata.add(data);
                    } else {
                        auxdata.update(data);
                    }
                }
            }

            so = 0;
            if (getTests(om) != null) {
                for (OrderTestViewDO data : getTests(om)) {
                    tmpid = data.getId();
                    data.setSortOrder( ++so);
                    if (data.getId() < 0) {
                        data.setOrderId(getOrder(om).getId());
                        orderTest.add(data);
                    } else {
                        orderTest.update(data);
                    }
                    tmap.put(tmpid, data.getId());
                }
            }

            if (getAnalytes(om) != null) {
                for (OrderTestAnalyteViewDO data : getAnalytes(om)) {
                    if (data.getId() < 0) {
                        data.setOrderTestId(tmap.get(data.getOrderTestId()));
                        orderTestAnalyte.add(data);
                    } else {
                        orderTestAnalyte.update(data);
                    }
                }
            }

            if (getContainers(om) != null) {
                for (OrderContainerDO data : getContainers(om)) {
                    if (data.getId() == null) {
                        data.setOrderId(getOrder(om).getId());
                        orderContainer.add(data);
                    } else {
                        orderContainer.update(data);
                    }
                }
            }

            if (getItems(om) != null) {
                for (OrderItemViewDO data : getItems(om)) {
                    if (data.getId() == null) {
                        data.setOrderId(getOrder(om).getId());
                        orderItem.add(data);
                    } else {
                        orderItem.update(data);
                    }
                }
            }

            if (getShippingNote(om) != null) {
                nd = getShippingNote(om);
                if (nd.getId() == null) {
                    nd.setReferenceTableId(Constants.table().ORDER_SHIPPING_NOTE);
                    nd.setReferenceId(getOrder(om).getId());
                    note.add(nd);
                } else {
                    note.update(nd);
                }
            }

            if (getCustomerNote(om) != null) {
                nd = getCustomerNote(om);
                if (nd.getId() == null) {
                    nd.setReferenceTableId(Constants.table().ORDER_CUSTOMER_NOTE);
                    nd.setReferenceId(getOrder(om).getId());
                    note.add(nd);
                } else {
                    note.update(nd);
                }
            }

            if (getSampleNote(om) != null) {
                nd = getSampleNote(om);
                if (nd.getId() == null) {
                    nd.setReferenceTableId(Constants.table().ORDER_SAMPLE_NOTE);
                    nd.setReferenceId(getOrder(om).getId());
                    note.add(nd);
                } else {
                    note.update(nd);
                }
            }

            if (getInternalNotes(om) != null) {
                for (NoteViewDO data : getInternalNotes(om)) {
                    if (data.getId() == null) {
                        data.setReferenceTableId(Constants.table().ORDER);
                        data.setReferenceId(getOrder(om).getId());
                        note.add(data);
                    } else {
                        note.update(data);
                    }
                }
            }

            if (getRecurrence(om) != null) {
                or = getRecurrence(om);
                if (or.getId() == null) {
                    or.setOrderId(getOrder(om).getId());
                    orderRecurrence.add(or);
                } else {

                    orderRecurrence.update(or);
                }
            }

            if (getFills(om) != null) {
                for (InventoryXUseViewDO data : getFills(om)) {
                    if (data.getId() == null) {
                        data.setOrderItemOrderId(getOrder(om).getId());
                        orderFill.add(data);
                    } else {
                        orderFill.update(data);
                    }
                }
            }
        }

        if (locks != null)
            lock.unlock(Constants.table().ORDER, locks);

        return oms;
    }

    /**
     * Adds aux groups with ids to the order based on the list of group
     */
    public OrderReturnVO addAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds) throws Exception {
        OrderReturnVO ret;
        ValidationErrorsList errors;
        ArrayList<AuxDataViewDO> auxiliary;

        auxiliary = getAuxilliary(om);
        if (auxiliary == null) {
            auxiliary = new ArrayList<AuxDataViewDO>();
            setAuxilliary(om, auxiliary);
        }

        ret = new OrderReturnVO();
        ret.setManager(om);
        errors = new ValidationErrorsList();
        ret.setErrors(errors);

        auxDataHelper.addAuxGroups(auxiliary, groupIds, errors);

        return ret;
    }

    /**
     * Removes aux data from the order based on the list of group ids
     */
    public OrderManager1 removeAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds) throws Exception {
        ArrayList<AuxDataViewDO> removed;

        removed = auxDataHelper.removeAuxGroups(getAuxilliary(om), new HashSet<Integer>(groupIds));

        if (removed != null && removed.size() > 0) {
            if (getRemoved(om) == null)
                setRemoved(om, new ArrayList<DataObject>());
            for (AuxDataViewDO data : removed) {
                if (data.getId() != null)
                    getRemoved(om).add(data);
            }
        }
        return om;
    }

    /**
     * Adds a test or all the tests and aux groups from a panel to the manager
     */
    public OrderReturnVO addTest(OrderManager1 om, Integer id, boolean isTest, Integer index) throws Exception {
        ValidationErrorsList e;
        OrderReturnVO ret;
        OrderTestViewDO ot;
        TestManager tm;
        HashMap<Integer, TestManager> tms;
        ArrayList<Integer> testIds, groupIds;
        ArrayList<IdVO> pts, pgs;
        ArrayList<OrderTestViewDO> tests;

        e = new ValidationErrorsList();
        ret = new OrderReturnVO();
        ret.setManager(om);
        ret.setErrors(e);
        testIds = new ArrayList<Integer>();
        groupIds = null;

        if (isTest) {
            testIds.add(id);
        } else {
            /*
             * fetch the IDs of the tests specified in the panel
             */
            pts = panel.fetchTestIdsFromPanel(id);
            if (pts != null && pts.size() > 0) {
                for (IdVO pt : pts)
                    testIds.add(pt.getId());
            }
            /*
             * fetch the IDs of the aux groups specified in the panel
             */
            pgs = panel.fetchAuxIdsFromPanel(id);
            groupIds = new ArrayList<Integer>();
            for (IdVO pg : pgs)
                groupIds.add(pg.getId());
        }
        tms = orderTestHelper.getTestManagers(testIds, e);

        tests = getTests(om);
        if (tests == null) {
            tests = new ArrayList<OrderTestViewDO>();
            setTests(om, tests);
        }
        if (index == null)
            index = tests.size();
        /*
         * Add the tests and the analytes to the order
         */
        for (Integer testId : testIds) {
            tm = tms.get(testId);
            if (tm == null)
                continue;
            ot = orderTestHelper.addTest(om, tm, index++ );
            orderTestHelper.addAnalytes(om, tm, ot.getId());
        }

        /*
         * if a panel was added above and it had any aux groups linked to it
         * then add them to the order
         */
        if (groupIds != null && groupIds.size() > 0)
            addAuxGroups(om, groupIds);

        return ret;
    }

    /**
     * Removes a test from the manager
     */
    public OrderManager1 removeTest(OrderManager1 om, Integer id) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();
        ids.add(id);
        return removeTests(om, ids);
    }

    /**
     * Removes tests from the manager
     */
    public OrderManager1 removeTests(OrderManager1 om, ArrayList<Integer> ids) throws Exception {
        int i;
        OrderTestViewDO ot;
        OrderTestAnalyteViewDO ota;
        HashSet<Integer> set;

        set = new HashSet<Integer>(ids);
        if (getRemoved(om) == null)
            setRemoved(om, new ArrayList<DataObject>());
        i = 0;
        while (i < getTests(om).size()) {
            ot = getTests(om).get(i);
            if ( !set.contains(ot.getId())) {
                i++ ;
                continue;
            }
            if (ot.getId() > 0)
                getRemoved(om).add(ot);
            getTests(om).remove(i);

        }

        if (getAnalytes(om) != null) {
            i = 0;
            while (i < getAnalytes(om).size()) {
                ota = getAnalytes(om).get(i);
                if ( !set.contains(ota.getOrderTestId())) {
                    i++ ;
                    continue;
                }
                if (ota.getId() > 0)
                    getRemoved(om).add(ota);
                getAnalytes(om).remove(i);
            }
        }
        return om;
    }

    /**
     * Creates an order in the database from the data in the order manager
     * merged with the data in the sample manager; the merged data includes
     * organizations and aux data specified by the list of analytes. Any empty
     * required fields are also set with default values.
     */
    public void createOrderFromSample(OrderManager1 om, SampleManager1 sm,
                                      ArrayList<String> analytes) throws Exception {
        Integer accession;
        SampleOrganizationViewDO sRepOrg, sBillOrg;
        OrderViewDO data;
        ArrayList<SampleOrganizationViewDO> sSecOrgs;

        data = getOrder(om);

        /*
         * set default values
         */
        if (data.getShipFromId() == null)
            data.setShipFromId(Constants.dictionary().LABORATORY_LOCATION_IC);
        data.setStatusId(Constants.dictionary().ORDER_STATUS_ON_HOLD);
        data.setOrderedDate(Datetime.getInstance());
        if (data.getRequestedBy() == null)
            data.setRequestedBy("system");
        if (data.getCostCenterId() == null)
            data.setCostCenterId(Constants.dictionary().COST_CENTER_UNKNOWN);
        if (data.getNeededInDays() == null)
            data.setNeededInDays(0);

        sRepOrg = null;
        sBillOrg = null;
        sSecOrgs = null;
        setOrganizations(om, null);

        /*
         * find out if organizations of various types are specified in the
         * sample
         */
        for (SampleOrganizationViewDO sorg : getOrganizations(sm)) {
            if (Constants.dictionary().ORG_REPORT_TO.equals(sorg.getTypeId())) {
                sRepOrg = sorg;
            } else if (Constants.dictionary().ORG_BILL_TO.equals(sorg.getTypeId())) {
                sBillOrg = sorg;
            } else if (Constants.dictionary().ORG_SECOND_REPORT_TO.equals(sorg.getTypeId())) {
                if (sSecOrgs == null)
                    sSecOrgs = new ArrayList<SampleOrganizationViewDO>();
                sSecOrgs.add(sorg);
            }
        }

        /*
         * check if the sample has any organizations to set as the ship to
         */
        if (sRepOrg == null) {
            accession = getSample(sm).getAccessionNumber();
            log.log(Level.SEVERE, Messages.get().sample_reportToMissingWarning(accession));
            if (sBillOrg == null) {
                if (sSecOrgs == null) {
                    if (data.getOrganization() == null) {
                        log.log(Level.SEVERE, Messages.get()
                                                      .sdwisScan_noSampleOrgsException(accession));
                        throw new InconsistencyException(Messages.get()
                                                                 .sdwisScan_noSampleOrgsException(accession));
                    }
                } else {
                    sRepOrg = sSecOrgs.get(0);
                }
            } else {
                sRepOrg = sBillOrg;
            }
        } else {
            addOrganization(om, createOrderOrganization(sRepOrg));
        }

        /*
         * set the ship to
         */
        data.setOrganizationId(sRepOrg.getOrganizationId());
        data.setOrganizationAttention(sRepOrg.getOrganizationAttention());

        if (sBillOrg != null && !sRepOrg.getOrganizationId().equals(sBillOrg.getOrganizationId()))
            addOrganization(om, createOrderOrganization(sBillOrg));

        if (sSecOrgs != null) {
            for (SampleOrganizationViewDO sorg : sSecOrgs)
                addOrganization(om, createOrderOrganization(sorg));
        }

        /*
         * we assume the order template has an aux group added to it
         */
        auxDataHelper.copyFromSample(sm, getAuxilliary(om), analytes);
        update(om, true);
    }

    /**
     * create a new order organization object from the data in a sample
     * organization object
     */
    private OrderOrganizationViewDO createOrderOrganization(SampleOrganizationViewDO sorg) {
        OrderOrganizationViewDO oorg;

        oorg = new OrderOrganizationViewDO();
        oorg.setOrganizationId(sorg.getOrganizationId());
        oorg.setOrganizationAttention(sorg.getOrganizationAttention());
        oorg.setTypeId(sorg.getTypeId());

        return oorg;
    }

    private ArrayList<OrderManager1> fetchByIds(ArrayList<Integer> orderIds, boolean isUpdate,
                                                OrderManager1.Load... elements) throws Exception {
        OrderManager1 om;
        EnumSet<OrderManager1.Load> el;
        ArrayList<Integer> ids1, ids2, testIds, orgIds;
        ArrayList<OrderManager1> oms;
        ArrayList<OrderTestAnalyteViewDO> otas;
        ArrayList<OrganizationViewDO> orgs;
        ArrayList<OrderViewDO> orders;
        HashMap<Integer, OrganizationViewDO> orgMap;
        HashMap<Integer, OrderManager1> map1, map2;
        HashMap<Integer, ArrayList<OrderTestAnalyteViewDO>> otaMap;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        oms = new ArrayList<OrderManager1>();
        if (elements != null && elements.length > 0)
            el = EnumSet.copyOf(Arrays.asList(elements));
        else
            el = EnumSet.noneOf(OrderManager1.Load.class);

        /*
         * if there are no orders, then return an empty list
         */
        orders = order.fetchByIds(orderIds);
        if (orders.size() < 1)
            return oms;

        /*
         * build level 1, everything is based on order ids
         */
        ids1 = new ArrayList<Integer>();
        ids2 = new ArrayList<Integer>();
        map1 = new HashMap<Integer, OrderManager1>();
        orgIds = new ArrayList<Integer>();

        for (OrderViewDO data : orders) {
            om = new OrderManager1();
            setOrder(om, data);
            oms.add(om);

            ids1.add(data.getId()); // for fetch
            map1.put(data.getId(), om); // for linking
            if (data.getOrganizationId() != null)
                orgIds.add(data.getOrganizationId());
        }

        if (orgIds.size() > 0) {
            /*
             * fetch all the organizations required for the orders and set them
             * in the respective managers
             */
            orgs = organization.fetchByIds(orgIds);
            orgMap = new HashMap<Integer, OrganizationViewDO>();
            for (OrganizationViewDO org : orgs)
                orgMap.put(org.getId(), org);

            for (OrderManager1 om1 : oms) {
                if (getOrder(om1).getOrganizationId() != null)
                    getOrder(om1).setOrganization(orgMap.get(getOrder(om1).getOrganizationId()));
            }
        }

        ids2 = new ArrayList<Integer>();
        ids2.add(Constants.table().ORDER_SHIPPING_NOTE);
        ids2.add(Constants.table().ORDER_CUSTOMER_NOTE);
        ids2.add(Constants.table().ORDER_SAMPLE_NOTE);
        ids2.add(Constants.table().ORDER);
        for (NoteViewDO data : note.fetchByIdsAndTables(ids1, ids2)) {
            om = map1.get(data.getReferenceId());
            if (Constants.table().ORDER_SHIPPING_NOTE.equals(data.getReferenceTableId()))
                setShippingNote(om, data);
            else if (Constants.table().ORDER_CUSTOMER_NOTE.equals(data.getReferenceTableId()))
                setCustomerNote(om, data);
            else if (Constants.table().ORDER_SAMPLE_NOTE.equals(data.getReferenceTableId()))
                setSampleNote(om, data);
            else
                addInternalNote(om, data);
        }

        /*
         * various lists for each order
         */
        if (el.contains(OrderManager1.Load.ITEMS)) {
            for (OrderItemViewDO data : orderItem.fetchByOrderIds(ids1)) {
                om = map1.get(data.getOrderId());
                addItem(om, data);
            }
            for (InventoryXUseViewDO data : orderFill.fetchByOrderIds(ids1)) {
                om = map1.get(data.getOrderItemOrderId());
                addFill(om, data);
            }
            for (InventoryXPutViewDO data : orderReceipt.fetchByOrderIds(ids1)) {
                om = map1.get(data.getInventoryReceiptOrderItemOrderId());
                addReceipt(om, data);
            }
        }

        if (el.contains(OrderManager1.Load.ORGANIZATION)) {
            for (OrderOrganizationViewDO data : orderOrganization.fetchByOrderIds(ids1)) {
                om = map1.get(data.getOrderId());
                addOrganization(om, data);
            }
        }

        if (el.contains(OrderManager1.Load.SAMPLE_DATA)) {
            for (OrderContainerDO data : orderContainer.fetchByOrderIds(ids1)) {
                om = map1.get(data.getOrderId());
                addContainer(om, data);
            }
            for (AuxDataViewDO data : auxdata.fetchByIds(ids1, Constants.table().ORDER)) {
                om = map1.get(data.getReferenceId());
                addAuxilliary(om, data);
            }

            /*
             * build level 2, everything is based on order test ids
             */
            ids2 = new ArrayList<Integer>();
            map2 = new HashMap<Integer, OrderManager1>();
            if (isUpdate)
                testIds = new ArrayList<Integer>();
            else
                testIds = null;

            for (OrderTestViewDO data : orderTest.fetchByOrderIds(ids1)) {
                om = map1.get(data.getOrderId());
                org.openelis.manager.OrderManager1Accessor.addTest(om, data);
                ids2.add(data.getId());
                map2.put(data.getId(), om);
                if (testIds != null)
                    testIds.add(data.getTestId());
            }
            if (isUpdate)
                otaMap = new HashMap<Integer, ArrayList<OrderTestAnalyteViewDO>>();
            else
                otaMap = null;
            otas = null;
            if (ids2.size() > 0) {
                for (OrderTestAnalyteViewDO data : orderTestAnalyte.fetchByOrderTestIds(ids2)) {
                    om = map2.get(data.getOrderTestId());

                    if (otaMap != null) {
                        /*
                         * create mapping between order tests and their order
                         * test analytes
                         */
                        otas = otaMap.get(data.getOrderTestId());
                        if (otas == null) {
                            otas = new ArrayList<OrderTestAnalyteViewDO>();
                            otaMap.put(data.getOrderTestId(), otas);
                        }
                        otas.add(data);
                    } else {
                        /*
                         * the order test analytes don't need to be merged with
                         * the original test's analytes, so they can just be
                         * added to the order directly
                         */
                        addAnalyte(om, data);
                    }
                }
            }
            if (testIds != null && testIds.size() > 0)
                orderTestHelper.mergeAnalytes(testIds, otaMap, oms);
        }

        if (el.contains(OrderManager1.Load.RECURRENCE)) {
            for (OrderRecurrenceDO data : orderRecurrence.fetchByOrderIds(ids1)) {
                om = map1.get(data.getOrderId());
                setRecurrence(om, data);
            }
        }
        return oms;
    }

    /**
     * Validates the order manager for add or update. The routine throws a list
     * of exceptions/warnings listing all the problems for each order.
     */
    private void validate(ArrayList<OrderManager1> oms, HashMap<Integer, TestManager> tms,
                          boolean ignoreWarning) throws Exception {
        int rcnt, bcnt;
        Integer orderId, itemId;
        ValidationErrorsList e;
        HashSet<Integer> itemIds;

        e = new ValidationErrorsList();

        if ( !ignoreWarning) {
            validateTestsAndContainers(oms, tms, e);
            if (e.size() > 0)
                throw e;
        }

        for (OrderManager1 om : oms) {
            /*
             * order level
             */
            orderId = null;
            if (getOrder(om) != null) {
                orderId = getOrder(om).getId();
                if (getOrder(om).isChanged()) {
                    try {
                        order.validate(getOrder(om));
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }

            /*
             * orders must have no more than one report to or bill to.
             */
            rcnt = 0;
            bcnt = 0;
            if (getOrganizations(om) != null) {
                for (OrderOrganizationViewDO data : getOrganizations(om)) {
                    try {
                        orderOrganization.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                    if (Constants.dictionary().ORG_REPORT_TO.equals(data.getTypeId()))
                        rcnt++ ;
                    else if (Constants.dictionary().ORG_BILL_TO.equals(data.getTypeId()))
                        bcnt++ ;
                }
                if (rcnt > 1)
                    e.add(new FormErrorException(Messages.get()
                                                         .order_multipleReportToException(orderId)));
                if (bcnt > 1)
                    e.add(new FormErrorException(Messages.get()
                                                         .order_multipleBillToException(orderId)));
            }

            if (getContainers(om) != null) {
                for (OrderContainerDO data : getContainers(om)) {
                    try {
                        orderContainer.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }

            if (getItems(om) != null) {
                itemIds = null;
                for (OrderItemViewDO data : getItems(om)) {
                    try {
                        orderItem.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                    if ( !OrderManager.TYPE_VENDOR.equals(getOrder(om).getType()))
                        continue;
                    if (itemIds == null)
                        itemIds = new HashSet<Integer>();

                    itemId = data.getInventoryItemId();
                    if (itemId != null) {
                        if (itemIds.contains(itemId))
                            e.add(new FormErrorException(Messages.get()
                                                                 .order_duplicateInvItemVendorOrderException(orderId,
                                                                                                             data.getInventoryItemName())));
                        else
                            itemIds.add(itemId);
                    }
                }
            }

            if (getRecurrence(om) != null) {
                try {
                    orderRecurrence.validate(getRecurrence(om));
                } catch (Exception err) {
                    DataBaseUtil.mergeException(e, err);
                }
            }
        }

        if (e.size() > 0)
            throw e;
    }

    private void validateTestsAndContainers(ArrayList<OrderManager1> oms,
                                            HashMap<Integer, TestManager> tms,
                                            ValidationErrorsList e) throws Exception {
        int i, testCount, contCount;
        Integer sequence, testId;
        Integer orderId;
        ArrayList<OrderTestViewDO> ots;
        ArrayList<OrderContainerDO> ocs;
        OrderTestViewDO test;
        OrderContainerDO cont;
        TestTypeOfSampleManager samTypeMan;

        for (OrderManager1 om : oms) {
            orderId = getOrder(om).getId();
            /*
             * for display
             */
            if (orderId == null)
                orderId = 0;
            ots = getTests(om);
            ocs = getContainers(om);

            testCount = ots == null ? 0 : ots.size();
            contCount = ocs == null ? 0 : ocs.size();

            for (i = 0; i < testCount; i++ ) {
                test = ots.get(i);
                sequence = test.getItemSequence();
                testId = test.getTestId();

                if (sequence == null)
                    continue;

                if (sequence >= contCount) {
                    /*
                     * no container is present for this item sequence
                     */
                    e.add(new FormErrorWarning(Messages.get()
                                                       .order_noContainerWithItemNumWarning(orderId,
                                                                                            sequence.toString(),
                                                                                            getTestLabel(test))));
                } else if (sequence >= 0 && testId != null) {
                    cont = ocs.get(sequence);
                    if (cont.getTypeOfSampleId() == null)
                        continue;
                    /*
                     * fetch the sample types for this test and find out if the
                     * sample type specified for the container is valid for the
                     * test
                     */

                    samTypeMan = tms.get(testId).getSampleTypes();
                    if ( !testHasSampleType(cont.getTypeOfSampleId(), samTypeMan))
                        e.add(new FormErrorWarning(Messages.get()
                                                           .order_invalidSampleTypeForTestWarning(orderId,
                                                                                                  sequence.toString(),
                                                                                                  getTestLabel(test))));

                }
            }
            for (i = 0; i < contCount; i++ ) {
                cont = ocs.get(i);
                if (cont.getTypeOfSampleId() == null)
                    e.add(new FormErrorWarning(Messages.get()
                                                       .order_noSampleTypeForContainerWarning(orderId,
                                                                                              cont.getItemSequence()
                                                                                                  .toString())));
            }
        }
    }

    private boolean testHasSampleType(Integer ttsid, TestTypeOfSampleManager ttsm) {
        TestTypeOfSampleDO tts;

        for (int i = 0; i < ttsm.count(); i++ ) {
            tts = ttsm.getTypeAt(i);
            if (DataBaseUtil.isSame(tts.getTypeOfSampleId(), ttsid))
                return true;
        }
        return false;
    }

    private String getTestLabel(OrderTestViewDO test) {
        return (test.getTestId() == null) ? ""
                                         : DataBaseUtil.concatWithSeparator(test.getTestName(),
                                                                            ", ",
                                                                            test.getMethodName());
    }
}
