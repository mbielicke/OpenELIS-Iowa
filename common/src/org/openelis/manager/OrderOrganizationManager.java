package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.Constants;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.RPC;

public class OrderOrganizationManager implements RPC {

    private static final long                                serialVersionUID = 1L;

    protected Integer                                        orderId;
    protected ArrayList<OrderOrganizationViewDO>             organizations, deletedList;

    protected transient static OrderOrganizationManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static OrderOrganizationManager getInstance() {
        OrderOrganizationManager man;

        man = new OrderOrganizationManager();
        man.organizations = new ArrayList<OrderOrganizationViewDO>();

        return man;
    }

    /**
     * Creates a new instance of this object with the specified Order id. Use
     * this function to load an instance of this object from database.
     */
    public static OrderOrganizationManager fetchByOrderId(Integer orderId) throws Exception {
        return proxy().fetchByOrderId(orderId);
    }

    // service methods
    public OrderOrganizationManager add() throws Exception {
        return proxy().add(this);
    }

    public OrderOrganizationManager update() throws Exception {
        return proxy().update(this);
    }

    public OrderOrganizationViewDO getOrganizationAt(int i) {
        return organizations.get(i);
    }

    public void setOrganizationAt(OrderOrganizationViewDO organization, int i) {
        organizations.set(i, organization);
    }

    public void addOrganization(OrderOrganizationViewDO organization) {
        if (organizations == null)
            organizations = new ArrayList<OrderOrganizationViewDO>();

        organizations.add(organization);
    }

    public void addOrganizationAt(OrderOrganizationViewDO organization, int i) {
        if (organizations == null)
            organizations = new ArrayList<OrderOrganizationViewDO>();

        organizations.add(i, organization);
    }

    public void removeOrganizationAt(int i) {
        OrderOrganizationViewDO tmp;
        if (organizations == null || i >= organizations.size())
            return;

        tmp = organizations.remove(i);

        if (tmp.getId() != null) {
            if (deletedList == null)
                deletedList = new ArrayList<OrderOrganizationViewDO>();
            deletedList.add(tmp);
        }
    }

    //
    // helper methods
    //
    public OrderOrganizationViewDO getBillTo() {
        int i = 0;

        while (i < organizations.size() &&
               !DataBaseUtil.isSame(Constants.dictionary().ORG_BILL_TO,
                                    organizations.get(i).getTypeId()))
            i++ ;

        if (i < organizations.size())
            return organizations.get(i);
        else
            return null;
    }

    public OrderOrganizationViewDO getReportTo() {
        int i = 0;

        while (i < organizations.size() &&
               !DataBaseUtil.isSame(Constants.dictionary().ORG_REPORT_TO,
                                    organizations.get(i).getTypeId()))
            i++ ;

        if (i < organizations.size())
            return organizations.get(i);
        else
            return null;
    }

    public int count() {
        if (organizations == null)
            return 0;

        return organizations.size();
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getOrderId() {
        return orderId;
    }

    void setOrderId(Integer OrderId) {
        this.orderId = OrderId;
    }

    ArrayList<OrderOrganizationViewDO> getOrganizations() {
        return organizations;
    }

    void setOrganizations(ArrayList<OrderOrganizationViewDO> organizations) {
        this.organizations = organizations;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    OrderOrganizationViewDO getDeletedAt(int i) {
        return deletedList.get(i);
    }

    private static OrderOrganizationManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderOrganizationManagerProxy();

        return proxy;
    }
}
