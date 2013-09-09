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
package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.AddressDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderTestAnalyteDO;
import org.openelis.domain.OrderTestAnalyteViewDO;
import org.openelis.domain.OrderTestDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;

/**
 * This class encapsulates an order and all its related information including
 * organization, items, tests, etc. Although the class provides some basic
 * functions internally, it is designed to interact with EJB methods to provide
 * majority of the operations needed to manage an order.
 */
public class OrderManager1 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Flags that specifies what optional data to load with the manager
     */
    public enum Load {
        ITEMS, // item, fill, receipt
        ORGANIZATION, // organization
        SAMPLE_DATA, // auxdata, test, container
        RECURRENCE // recurrence
    };

    protected OrderViewDO                         order;
    protected ArrayList<OrderOrganizationViewDO>  organizations;
    protected ArrayList<OrderItemViewDO>          items;
    protected ArrayList<InventoryXUseViewDO>      fills;
    protected ArrayList<InventoryXPutViewDO>      receipts;
    protected ArrayList<OrderContainerDO>         containers;
    protected ArrayList<OrderTestViewDO>          tests;
    protected ArrayList<OrderTestAnalyteViewDO>   analytes;
    protected NoteViewDO                          shipNote, custNote, sampNote;
    protected ArrayList<NoteViewDO>               internalNotes;
    protected ArrayList<AuxDataViewDO>            auxilliary;
    protected OrderRecurrenceDO                   recurrence;
    protected ArrayList<DataObject>               removed;
    protected int                                 nextUID      = -1;

    transient public final OrderOrganization      organization = new OrderOrganization();
    transient public final OrderItem              item         = new OrderItem();
    transient public final OrderFill              fill         = new OrderFill();
    transient public final OrderReceipt           receipt      = new OrderReceipt();
    transient public final OrderContainer         container    = new OrderContainer();
    transient public final OrderTest              test         = new OrderTest();
    transient public final OrderTestAnalyte       analyte      = new OrderTestAnalyte();
    transient public final ShippingNote           shippingNote = new ShippingNote();
    transient public final CustomerNote           customerNote = new CustomerNote();
    transient public final SampleNote             sampleNote   = new SampleNote();
    transient public final InternalNote           internalNote = new InternalNote();
    transient public final AuxData                auxData      = new AuxData();
    transient private HashMap<String, DataObject> uidMap;

    /**
     * Initialize an empty order manager
     */
    public OrderManager1() {
    }

    /**
     * Returns the order view DO
     */
    public OrderViewDO getOrder() {
        return order;
    }

    public OrderRecurrenceDO getRecurrence() {
        return recurrence;
    }

    /**
     * Adds a recurrence DO to the manager and sets the status of the order to
     * recurring
     */
    public OrderRecurrenceDO addRecurrence() {
        if (recurrence == null) {
            recurrence = new OrderRecurrenceDO();
            recurrence.setIsActive("Y");
            order.setStatusId(Constants.dictionary().ORDER_STATUS_RECURRING);
        }

        return recurrence;
    }

    /**
     * Returns the next negative Id for this sample's newly created and as yet
     * uncommitted data objects e.g. order tests and order test analytes.
     */
    public int getNextUID() {
        return --nextUID;
    }

    /**
     * Returns a unique id representing the data object's type and key. This id
     * can be used to directly find the object this manager rather than serially
     * traversing the lists.
     */
    public String getUid(OrderTestDO data) {
        return getOrderTestUid(data.getId());
    }

    /**
     * Returns the data object using its Uid.
     */
    public DataObject getObject(String uid) {
        if (uidMap == null) {
            uidMap = new HashMap<String, DataObject>();

            if (tests != null)
                for (OrderTestDO data : tests)
                    uidMap.put(getOrderTestUid(data.getId()), data);
        }
        return uidMap.get(uid);
    }

    /**
     * Returns the unique identifiers for each data object.
     */
    public String getOrderTestUid(Integer id) {
        return "T:" + id;
    }

    /**
     * Class to manage Order Organization information
     */
    public class OrderOrganization {
        /**
         * Returns the organization at specified index.
         */
        public OrderOrganizationViewDO get(int i) {
            return organizations.get(i);
        }

        public OrderOrganizationViewDO add() {
            OrderOrganizationViewDO data;

            data = new OrderOrganizationViewDO();
            if (organizations == null)
                organizations = new ArrayList<OrderOrganizationViewDO>();
            organizations.add(data);

            return data;
        }

        public OrderOrganizationViewDO add(OrganizationDO organization) {
            OrderOrganizationViewDO data;
            AddressDO addr;

            data = add();
            data.setOrganizationId(organization.getId());
            data.setOrganizationName(organization.getName());
            addr = organization.getAddress();
            data.setOrganizationAddressMultipleUnit(addr.getMultipleUnit());
            data.setOrganizationAddressStreetAddress(addr.getStreetAddress());
            data.setOrganizationAddressCity(addr.getCity());
            data.setOrganizationAddressState(addr.getState());
            data.setOrganizationAddressZipCode(addr.getZipCode());
            data.setOrganizationAddressCountry(addr.getCountry());

            return data;
        }

        /**
         * Removes an organization from the list
         */
        public void remove(int i) {
            OrderOrganizationViewDO data;

            data = organizations.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(OrderOrganizationViewDO data) {
            organizations.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of organizations associated with this order
         */
        public int count() {
            if (organizations != null)
                return organizations.size();
            return 0;
        }

        /**
         * Returns a list of order organizations that are of specified typeId.
         */
        public ArrayList<OrderOrganizationViewDO> getByType(Integer typeId) {
            ArrayList<OrderOrganizationViewDO> list;

            list = null;
            if (organizations != null) {
                for (OrderOrganizationViewDO data : organizations) {
                    if (typeId.equals(data.getTypeId())) {
                        if (list == null)
                            list = new ArrayList<OrderOrganizationViewDO>();
                        list.add(data);
                    }
                }
            }
            return list;
        }
    }

    /**
     * Class to manage Order Item information
     */
    public class OrderItem {
        /**
         * Returns the item at specified index.
         */
        public OrderItemViewDO get(int i) {
            return items.get(i);
        }

        public OrderItemViewDO add() {
            OrderItemViewDO data;

            data = new OrderItemViewDO();
            if (items == null)
                items = new ArrayList<OrderItemViewDO>();
            items.add(data);

            return data;
        }

        /**
         * Removes an item from the list
         */
        public void remove(int i) {
            OrderItemViewDO data;

            data = items.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(OrderItemViewDO data) {
            items.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of items associated with this order
         */
        public int count() {
            if (items != null)
                return items.size();
            return 0;
        }
    }

    /**
     * Class to manage Order Fill (Inventory X Use) information
     */
    public class OrderFill {

        public InventoryXUseViewDO get(int i) {
            return fills.get(i);
        }

        public InventoryXUseViewDO add() {
            InventoryXUseViewDO data;

            data = new InventoryXUseViewDO();
            if (fills == null)
                fills = new ArrayList<InventoryXUseViewDO>();
            fills.add(data);

            return data;
        }

        /**
         * Removes a fill from the list
         */
        public void remove(int i) {
            InventoryXUseViewDO data;

            data = fills.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(InventoryXUseViewDO data) {
            fills.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of fills associated with this order
         */
        public int count() {
            if (fills != null)
                return fills.size();
            return 0;
        }
    }

    /**
     * Class to manage Order Receipt (Inventory X Use) information
     */
    public class OrderReceipt {

        public InventoryXPutViewDO get(int i) {
            return receipts.get(i);
        }

        /**
         * Removes a receipt from the list
         */
        public void remove(int i) {
            InventoryXPutViewDO data;

            data = receipts.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of receipts associated with this order
         */
        public int count() {
            if (receipts != null)
                return receipts.size();
            return 0;
        }
    }

    /**
     * Class to manage Order Container information
     */
    public class OrderContainer {
        public OrderContainerDO get(int i) {
            return containers.get(i);
        }

        public OrderContainerDO add() {
            OrderContainerDO data;

            data = new OrderContainerDO();
            if (containers == null)
                containers = new ArrayList<OrderContainerDO>();
            containers.add(data);

            return data;
        }

        public OrderContainerDO add(int i) {
            OrderContainerDO data;

            data = new OrderContainerDO();
            if (containers == null)
                containers = new ArrayList<OrderContainerDO>();
            containers.add(i, data);
            resetSequencesFrom(i);

            return data;
        }

        /**
         * Removes a container from the list
         */
        public void remove(int i) {
            OrderContainerDO data;

            data = containers.remove(i);
            dataObjectRemove(data.getId(), data);
            resetSequencesFrom(i);
        }

        public void remove(OrderContainerDO data) {
            containers.remove(data);
            dataObjectRemove(data.getId(), data);
            resetSequencesFrom(containers.indexOf(data));
        }

        public void move(int oldIndex, int newIndex) {
            OrderContainerDO data;

            data = containers.remove(oldIndex);

            if (newIndex >= count())
                containers.add(data);
            else
                containers.add(newIndex, data);
            if (oldIndex < newIndex)
                resetSequencesFrom(oldIndex);
            else
                resetSequencesFrom(newIndex);
        }

        /**
         * Returns the number of containers associated with this order
         */
        public int count() {
            if (containers != null)
                return containers.size();
            return 0;
        }

        private void resetSequencesFrom(int index) {
            for (int i = index; i < count(); i++ ) {
                get(i).setItemSequence(i);
            }
        }
    }

    /**
     * Class to manage Order Test information
     */
    public class OrderTest {

        public OrderTestViewDO get(int i) {
            return tests.get(i);
        }

        /**
         * Returns the number of tests associated with this order
         */
        public int count() {
            if (tests != null)
                return tests.size();
            return 0;
        }
    }

    /**
     * Class to manage Order Test Analyte information
     */
    public class OrderTestAnalyte {
        transient protected HashMap<Integer, ArrayList<OrderTestAnalyteViewDO>> map = null;

        /**
         * Returns the order test analytes at specified index.
         */
        public OrderTestAnalyteViewDO get(OrderTestViewDO test, int i) {
            mapBuild();
            return map.get(test.getId()).get(i);
        }

        /**
         * Returns the number of analytes associated with specified order test
         */
        public int count(OrderTestViewDO test) {
            ArrayList<OrderTestAnalyteViewDO> l;

            if (analytes != null) {
                mapBuild();
                l = map.get(test.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash map from analytes list
         */
        private void mapBuild() {
            if (map == null && analytes != null) {
                map = new HashMap<Integer, ArrayList<OrderTestAnalyteViewDO>>();
                for (OrderTestAnalyteViewDO data : analytes)
                    mapAdd(data);
            }
        }

        /*
         * adds a new analyte to the hash map
         */
        private void mapAdd(OrderTestAnalyteViewDO data) {
            ArrayList<OrderTestAnalyteViewDO> l;

            if (map != null) {
                l = map.get(data.getOrderTestId());
                if (l == null) {
                    l = new ArrayList<OrderTestAnalyteViewDO>();
                    map.put(data.getOrderTestId(), l);
                }
                l.add(data);
            }
        }
    }

    /**
     * Class to manage shipping external notes
     */
    public class ShippingNote {

        /**
         * Returns the order's one (1) shipping note
         */
        public NoteViewDO get() {
            return shipNote;
        }

        /**
         * Returns the editing note. For external, there is only 1 note and that
         * note can be edited regardless of whether its committed to the
         * database or not.
         */
        public NoteViewDO getEditing() {
            if (shipNote == null) {
                shipNote = new NoteViewDO();
                shipNote.setIsExternal("Y");
            }

            return shipNote;
        }

        /**
         * Removes the editing note. For external, the entire external note is
         * removed.
         */
        public void removeEditing() {
            if (shipNote != null) {
                dataObjectRemove(shipNote.getId(), shipNote);
                shipNote = null;
            }
        }
    }

    /**
     * Class to manage customer external notes
     */
    public class CustomerNote {

        /**
         * Returns the order's one (1) customer note
         */
        public NoteViewDO get() {
            return custNote;
        }

        /**
         * Returns the editing note. For external, there is only 1 note and that
         * note can be edited regardless of whether its committed to the
         * database or not.
         */
        public NoteViewDO getEditing() {
            if (custNote == null) {
                custNote = new NoteViewDO();
                custNote.setIsExternal("Y");
            }

            return custNote;
        }

        /**
         * Removes the editing note. For external, the entire external note is
         * removed.
         */
        public void removeEditing() {
            if (custNote != null) {
                dataObjectRemove(custNote.getId(), custNote);
                custNote = null;
            }
        }
    }

    /**
     * Class to manage sample external notes
     */
    public class SampleNote {

        /**
         * Returns the order's one (1) sample note
         */
        public NoteViewDO get() {
            return sampNote;
        }

        /**
         * Returns the editing note. For external, there is only 1 note and that
         * note can be edited regardless of whether its committed to the
         * database or not.
         */
        public NoteViewDO getEditing() {
            if (sampNote == null) {
                sampNote = new NoteViewDO();
                sampNote.setIsExternal("Y");
            }

            return sampNote;
        }

        /**
         * Removes the editing note. For external, the entire external note is
         * removed.
         */
        public void removeEditing() {
            if (sampNote != null) {
                dataObjectRemove(sampNote.getId(), sampNote);
                sampNote = null;
            }
        }
    }

    /**
     * Class to manage internal notes
     */
    public class InternalNote {

        /**
         * Returns the order's internal note at specified index.
         */
        public NoteViewDO get(int i) {
            return internalNotes.get(i);
        }

        /**
         * Returns the editing note. For internal, only the currently
         * uncommitted note can be edited. If no editing note currently exists,
         * one is created and returned.
         */
        public NoteViewDO getEditing() {
            NoteViewDO data;

            if (internalNotes == null)
                internalNotes = new ArrayList<NoteViewDO>(1);

            if (internalNotes.size() == 0 || (internalNotes.get(0).getId() != null)) {
                data = new NoteViewDO();
                data.setIsExternal("N");
                internalNotes.add(0, data);
            }

            return internalNotes.get(0);
        }

        /**
         * Removes the editing note. For internal, only the uncommitted note is
         * removed.
         */
        public void removeEditing() {
            NoteViewDO data;

            if (internalNotes != null && internalNotes.size() > 0) {
                data = internalNotes.get(0);
                if (data.getId() == null)
                    internalNotes.remove(0);
            }
        }

        /**
         * Returns the number of internal note(s)
         */
        public int count() {
            return (internalNotes == null) ? 0 : internalNotes.size();
        }
    }

    /**
     * Class to manage auxiliary data
     */
    public class AuxData {
        /**
         * Returns the aux data at specified index.
         */
        public AuxDataViewDO get(int i) {
            return auxilliary.get(i);
        }

        /**
         * Returns the number of aux data associated with the order
         */
        public int count() {
            if (auxilliary == null)
                return 0;
            return auxilliary.size();
        }
    }

    /**
     * Adds the specified data object to the list of objects that should be
     * removed from the database.
     */
    private void dataObjectRemove(Integer id, DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();
        if (id != null)
            removed.add(data);
    }
}