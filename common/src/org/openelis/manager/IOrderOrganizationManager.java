package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.Constants;
import org.openelis.domain.IOrderOrganizationViewDO;
import org.openelis.ui.common.DataBaseUtil;

public class IOrderOrganizationManager implements Serializable {
    private static final long                                serialVersionUID = 1L;

    protected Integer                                        iorderId;
    protected ArrayList<IOrderOrganizationViewDO>            organizations, deletedList;

    protected transient static IOrderOrganizationManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static IOrderOrganizationManager getInstance() {
        IOrderOrganizationManager man;

        man = new IOrderOrganizationManager();
        man.organizations = new ArrayList<IOrderOrganizationViewDO>();

        return man;
    }

    /**
     * Creates a new instance of this object with the specified Order id. Use
     * this function to load an instance of this object from database.
     */
    public static IOrderOrganizationManager fetchByIorderId(Integer orderId) throws Exception {
        return proxy().fetchByIorderId(orderId);
    }

    // service methods
    public IOrderOrganizationManager add() throws Exception {
        return proxy().add(this);
    }

    public IOrderOrganizationManager update() throws Exception {
        return proxy().update(this);
    }

    public IOrderOrganizationViewDO getOrganizationAt(int i) {
        return organizations.get(i);
    }

    public void setOrganizationAt(IOrderOrganizationViewDO organization, int i) {
        organizations.set(i, organization);
    }

    public void addOrganization(IOrderOrganizationViewDO organization) {
        if (organizations == null)
            organizations = new ArrayList<IOrderOrganizationViewDO>();

        organizations.add(organization);
    }

    public void addOrganizationAt(IOrderOrganizationViewDO organization, int i) {
        if (organizations == null)
            organizations = new ArrayList<IOrderOrganizationViewDO>();

        organizations.add(i, organization);
    }

    public void removeOrganizationAt(int i) {
        IOrderOrganizationViewDO tmp;
        if (organizations == null || i >= organizations.size())
            return;

        tmp = organizations.remove(i);

        if (tmp.getId() != null) {
            if (deletedList == null)
                deletedList = new ArrayList<IOrderOrganizationViewDO>();
            deletedList.add(tmp);
        }
    }

    //
    // helper methods
    //
    public IOrderOrganizationViewDO getBillTo() {
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

    public IOrderOrganizationViewDO getReportTo() {
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
    Integer getIorderId() {
        return iorderId;
    }

    void setIorderId(Integer iorderId) {
        this.iorderId = iorderId;
    }

    ArrayList<IOrderOrganizationViewDO> getOrganizations() {
        return organizations;
    }

    void setOrganizations(ArrayList<IOrderOrganizationViewDO> organizations) {
        this.organizations = organizations;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    IOrderOrganizationViewDO getDeletedAt(int i) {
        return deletedList.get(i);
    }

    private static IOrderOrganizationManagerProxy proxy() {
        if (proxy == null)
            proxy = new IOrderOrganizationManagerProxy();

        return proxy;
    }
}
