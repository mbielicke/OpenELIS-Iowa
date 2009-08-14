package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.SampleOrganizationDO;
import org.openelis.domain.SampleProjectDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.TableDataRow;

public class SampleOrganizationManager implements RPC {

    private static final long                          serialVersionUID = 1L;

    protected Integer                                  sampleId;

    protected ArrayList<SampleOrganizationDO>          organizations;
    protected ArrayList<SampleOrganizationDO>          deletedList;

    protected transient static SampleOrganizationManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleOrganizationManager getInstance() {
        SampleOrganizationManager som;

        som = new SampleOrganizationManager();
        som.organizations = new ArrayList<SampleOrganizationDO>();

        return som;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use this function to load an instance of this object from database.
     */
    public static SampleOrganizationManager findBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    
    public void setReportTo(SampleOrganizationDO reportToDO){
        setRowFor(proxy().getIdFromSystemName("org_report_to"), reportToDO);
    }
    
    public void setBillTo(SampleOrganizationDO billToDO){
        setRowFor(proxy().getIdFromSystemName("org_bill_to"), billToDO);
    }
    
    protected void setRowFor(Integer typeId, SampleOrganizationDO newOrg) {
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
        
        for (i = 0; i < organizations.size(); i++) {
            SampleOrganizationDO orgDO = organizations.get(i);
            
            if (typeId.equals(orgDO.getTypeId()))
                return i;
        }

        return -1;
    }

    public SampleOrganizationDO getFirstBillTo() {
        Integer billToId = proxy().getIdFromSystemName("org_bill_to");
        int i = 0;

        while (i < organizations.size() && !billToId.equals(((SampleOrganizationDO)organizations.get(i)).getTypeId()))
            i++;

        if (i < organizations.size())
            return (SampleOrganizationDO)organizations.get(i);
        else
            return null;
    }

    public SampleOrganizationDO getFirstReportTo() {
        Integer reportToId = proxy().getIdFromSystemName("org_report_to");
        int i = 0;

        while (i < organizations.size() && !reportToId.equals(((SampleOrganizationDO)organizations.get(i)).getTypeId()))
            i++;

        if (i < organizations.size())
            return (SampleOrganizationDO)organizations.get(i);
        else
            return null;
    }

    public SampleOrganizationDO getFirstSecondaryReportTo() {
        Integer reportToId = proxy().getIdFromSystemName("org_second_report_to");
        int i = 0;

        while (i < organizations.size() && !reportToId.equals(((SampleOrganizationDO)organizations.get(i)).getTypeId()))
            i++;

        if (i < organizations.size())
            return (SampleOrganizationDO)organizations.get(i);
        else
            return null;
    }

    public int count() {
        if (organizations == null)
            return 0;

        return organizations.size();
    }

    public SampleOrganizationDO getOrganizationAt(int i) {
        return organizations.get(i);
    }

    public void setOrganizationAt(SampleOrganizationDO organization, int i) {
        organizations.set(i, organization);
    }

    public void addOrganization(SampleOrganizationDO organization) {
        if (organizations == null)
            organizations = new ArrayList<SampleOrganizationDO>();

        organizations.add(organization);
    }

    public void addOrganizationAt(SampleOrganizationDO organization, int i) {
        if (organizations == null)
            organizations = new ArrayList<SampleOrganizationDO>();

        organizations.add(i, organization);
    }

    public void removeOrganizationAt(int i) {
        if (organizations == null || i >= organizations.size())
            return;

        SampleOrganizationDO tmpDO = organizations.remove(i);

        if (deletedList == null)
            deletedList = new ArrayList<SampleOrganizationDO>();

        deletedList.add(tmpDO);
    }

    // service methods
    public SampleOrganizationManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleOrganizationManager update() throws Exception {
        return proxy().update(this);
    }

    private static SampleOrganizationManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleOrganizationManagerProxy();

        return proxy;
    }

    // these are friendly methods so only managers and proxies can call this method
    ArrayList<SampleOrganizationDO> getOrganizations() {
        return organizations;
    }

    void setOrganizations(ArrayList<SampleOrganizationDO> organizations) {
        this.organizations = organizations;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    SampleOrganizationDO getDeletedAt(int i) {
        return deletedList.get(i);
    }
}
