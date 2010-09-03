package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class SampleOrganizationManager implements RPC {

    private static final long                                 serialVersionUID = 1L;

    protected Integer                                         sampleId;
    protected ArrayList<SampleOrganizationViewDO>             organizations, deletedList;

    protected transient static SampleOrganizationManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleOrganizationManager getInstance() {
        SampleOrganizationManager som;

        som = new SampleOrganizationManager();
        som.organizations = new ArrayList<SampleOrganizationViewDO>();

        return som;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use
     * this function to load an instance of this object from database.
     */
    public static SampleOrganizationManager fetchBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }

    // service methods
    public SampleOrganizationManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleOrganizationManager update() throws Exception {
        return proxy().update(this);
    }

    public SampleOrganizationViewDO getOrganizationAt(int i) {
        return organizations.get(i);
    }

    public void setOrganizationAt(SampleOrganizationViewDO organization, int i) {
        organizations.set(i, organization);
    }

    public void addOrganization(SampleOrganizationViewDO organization) {
        if (organizations == null)
            organizations = new ArrayList<SampleOrganizationViewDO>();

        organizations.add(organization);
    }

    public void addOrganizationAt(SampleOrganizationViewDO organization, int i) {
        if (organizations == null)
            organizations = new ArrayList<SampleOrganizationViewDO>();

        organizations.add(i, organization);
    }

    public void removeOrganizationAt(int i) {
        SampleOrganizationViewDO tmp;
        if (organizations == null || i >= organizations.size())
            return;

        tmp = organizations.remove(i);

        if (deletedList == null)
            deletedList = new ArrayList<SampleOrganizationViewDO>();

        if (tmp.getId() != null)
            deletedList.add(tmp);
    }

    //
    // helper methods
    //
    public void setBillTo(SampleOrganizationViewDO billToDO) {
        setRowFor(proxy().getIdFromSystemName("org_bill_to"), billToDO);
    }

    public SampleOrganizationViewDO getFirstBillTo() {
        Integer billToId = proxy().getIdFromSystemName("org_bill_to");
        int i = 0;

        while (i < organizations.size() &&
               !billToId.equals( ((SampleOrganizationViewDO)organizations.get(i)).getTypeId()))
            i++ ;

        if (i < organizations.size())
            return (SampleOrganizationViewDO)organizations.get(i);
        else
            return null;
    }

    public void setReportTo(SampleOrganizationViewDO reportToDO) {
        setRowFor(proxy().getIdFromSystemName("org_report_to"), reportToDO);
    }

    public SampleOrganizationViewDO getFirstReportTo() {
        Integer reportToId = proxy().getIdFromSystemName("org_report_to");
        int i = 0;

        while (i < organizations.size() &&
               !reportToId.equals( ((SampleOrganizationViewDO)organizations.get(i)).getTypeId()))
            i++ ;

        if (i < organizations.size())
            return (SampleOrganizationViewDO)organizations.get(i);
        else
            return null;
    }

    public SampleOrganizationViewDO getFirstSecondaryReportTo() {
        Integer reportToId;
        int i;

        reportToId = proxy().getIdFromSystemName("org_second_report_to");
        i = 0;
        while (i < organizations.size() &&
               !reportToId.equals( ((SampleOrganizationViewDO)organizations.get(i)).getTypeId()))
            i++ ;

        if (i < organizations.size())
            return (SampleOrganizationViewDO)organizations.get(i);
        else
            return null;
    }

    public int count() {
        if (organizations == null)
            return 0;

        return organizations.size();
    }

    public void validate(String domain) throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, !domain.equals(SampleManager.WELL_DOMAIN_FLAG), errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(String domain, ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, !domain.equals(SampleManager.WELL_DOMAIN_FLAG), errorsList);
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getSampleId() {
        return sampleId;
    }

    void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    ArrayList<SampleOrganizationViewDO> getOrganizations() {
        return organizations;
    }

    void setOrganizations(ArrayList<SampleOrganizationViewDO> organizations) {
        this.organizations = organizations;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    SampleOrganizationViewDO getDeletedAt(int i) {
        return deletedList.get(i);
    }

    protected void setRowFor(Integer typeId, SampleOrganizationViewDO newOrg) {
        int rowIndex;
        rowIndex = rowIndexFor(typeId);

        if (rowIndex == -1 && newOrg != null) { // insert
            newOrg.setTypeId(typeId);
            addOrganization(newOrg);
        } else if (rowIndex != -1 && newOrg == null) { // delete
            removeOrganizationAt(rowIndex);
        } else if (rowIndex != -1 && newOrg != null) { // update
            newOrg.setTypeId(typeId);
            setOrganizationAt(newOrg, rowIndex);
        }
    }

    protected int rowIndexFor(Integer typeId) {
        int i;
        SampleOrganizationViewDO org;

        for (i = 0; i < organizations.size(); i++ ) {
            org = organizations.get(i);

            if (typeId.equals(org.getTypeId()))
                return i;
        }

        return -1;
    }

    private static SampleOrganizationManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleOrganizationManagerProxy();

        return proxy;
    }
}
