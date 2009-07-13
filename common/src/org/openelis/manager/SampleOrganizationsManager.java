package org.openelis.manager;

import java.util.ArrayList;
import java.util.List;

import org.openelis.domain.SampleOrganizationDO;
import org.openelis.gwt.common.RPC;

public class SampleOrganizationsManager implements RPC {

    private static final long                           serialVersionUID = 1L;

    Integer                                             sampleId;

    protected ArrayList<SampleOrganizationDO>           items;
    protected boolean                                   cached;
    protected transient SampleOrganizationsManagerIOInt manager;

    /**
     * Creates a new instance of this object.
     */
    public static SampleOrganizationsManager getInstance() {
        SampleOrganizationsManager som;

        som = new SampleOrganizationsManager();
        som.items = new ArrayList<SampleOrganizationDO>();

        return som;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use this function to load an instance of this object from database.
     */
    public static SampleOrganizationsManager findBySampleId(Integer sampleId) {
        SampleOrganizationsManager som = new SampleOrganizationsManager();
        som.items = new ArrayList<SampleOrganizationDO>();

        som.setSampleId(sampleId);
        som.fetch();

        return som;
    }

    public int count() {
        fetch();
        return items.size();
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public SampleOrganizationDO add() {
        SampleOrganizationDO newOrg = new SampleOrganizationDO();
        items.add(newOrg);

        return newOrg;
    }

    public SampleOrganizationDO getSampleOrganizationAt(int i) {
        fetch();
        return items.get(i);

    }

    public void setSampleOrganizationAt(SampleOrganizationDO sampleOrg, int i) {
        items.add(i, sampleOrg);
    }
    
    public SampleOrganizationDO getFirstBillTo(){
        fetch();
        Integer billToId = manager().getIdFromSystemName("org_bill_to");
        int i=0;
        
        while(i<items.size() && !billToId.equals(((SampleOrganizationDO)items.get(i)).getTypeId()))
            i++;
            
        if(i < items.size())
            return (SampleOrganizationDO)items.get(i);
        else
            return null;
    }
    
    public SampleOrganizationDO getFirstReportTo(){
        fetch();
        Integer reportToId = manager().getIdFromSystemName("org_report_to");
        int i=0;
        
        while(i<items.size() && !reportToId.equals(((SampleOrganizationDO)items.get(i)).getTypeId()))
            i++;
            
        if(i < items.size())
            return (SampleOrganizationDO)items.get(i);
        else
            return null;
    }
    
    public SampleOrganizationDO getFirstSecondaryReportTo(){
        fetch();
        Integer reportToId = manager().getIdFromSystemName("org_second_report_to");
        int i=0;
        
        while(i<items.size() && !reportToId.equals(((SampleOrganizationDO)items.get(i)).getTypeId()))
            i++;
            
        if(i < items.size())
            return (SampleOrganizationDO)items.get(i);
        else
            return null;
    }

    public SampleOrganizationsManagerIOInt getManager() {
        return manager;
    }

    public void setManager(SampleOrganizationsManagerIOInt manager) {
        this.manager = manager;
    }

    public void validate() {

    }

    // manager methods
    public void update() {
        manager().update(this);
    }

    protected void fetch() {
        if (cached)
            return;

        cached = true;
        List sampleOrgs = manager().fetch(sampleId);

        for (int i = 0; i < sampleOrgs.size(); i++)
            items.add((SampleOrganizationDO)sampleOrgs.get(i));
    }

    private SampleOrganizationsManagerIOInt manager() {
        if (manager == null)
            manager = ManagerFactory.getSampleOrganizationManagerIO();

        return manager;
    }
}
