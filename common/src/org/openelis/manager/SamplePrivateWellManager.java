package org.openelis.manager;

import org.openelis.domain.AddressDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class SamplePrivateWellManager implements RPC, SampleDomainInt {

    private static final long                           serialVersionUID = 1L;
    protected Integer sampleId;
    protected SamplePrivateWellViewDO                   privateWell;
    protected AddressDO                              reportToAddress, orgAddress, deletedAddress;

    protected transient static SamplePrivateWellManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SamplePrivateWellManager getInstance() {
        SamplePrivateWellManager spqm;

        spqm = new SamplePrivateWellManager();
        spqm.privateWell = new SamplePrivateWellViewDO();

        return spqm;
    }

    public static SamplePrivateWellManager fetchBySampleId(Integer sampleId) throws Exception {
        return proxy().fetch(sampleId);
    }

    // setters/getters
    public SamplePrivateWellViewDO getPrivateWell() {
        return privateWell;
    }
    
    public void setPrivateWell(SamplePrivateWellViewDO pwDO) {
        privateWell = pwDO;
    }
    
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    
    //address
    public void setOrganizationAddress(AddressDO addressDO) {
        if(reportToAddress != null)
            removeReportToAddress();
        
        orgAddress = addressDO;
    }
    
    public void setReportToAddress(AddressDO addressDO) {
        if (deletedAddress != null){
            addressDO.setId(deletedAddress.getId());
            privateWell.setReportToAddressId(addressDO.getId());
        }
        
        orgAddress = null;
        deletedAddress = null;
        reportToAddress = addressDO;
    }
    
    public AddressDO getAddress() {
        if(reportToAddress != null)
            return reportToAddress;
        else if(orgAddress != null)
            return orgAddress;
        else
            return new AddressDO();
    }
    
    public AddressDO getOrgAddress(){
        return orgAddress;
    }
    
    public AddressDO getReportToAddress(){
        return reportToAddress;
    }
    
    public AddressDO getDeletedAddress(){
        return deletedAddress;
    }
    
    public void removeAddress() {
        orgAddress = null;
        removeReportToAddress();
    }
    
    private void removeReportToAddress() {
        if (reportToAddress.getId() != null)
            deletedAddress = reportToAddress;
        
        reportToAddress = null;
    }
    
    // manager methods
    public SamplePrivateWellManager add() throws Exception {
        return proxy().add(this);
    }
    
    public SamplePrivateWellManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        
    }
    
    public void validate(ValidationErrorsList errorsList) throws Exception {
        
    }

    private static SamplePrivateWellManagerProxy proxy(){
        if(proxy == null) 
            proxy = new SamplePrivateWellManagerProxy();
        
        return proxy;
    }    
}
