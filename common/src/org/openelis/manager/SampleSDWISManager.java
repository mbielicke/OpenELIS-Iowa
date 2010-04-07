package org.openelis.manager;

import org.openelis.domain.PwsDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.SampleMeta;

public class SampleSDWISManager implements RPC, SampleDomainInt {
    private static final long                          serialVersionUID = 1L;
    protected Integer                                  sampleId;
    protected SampleSDWISViewDO                        sdwis;

    protected transient static SampleSDWISManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleSDWISManager getInstance() {
        SampleSDWISManager ssm;

        ssm = new SampleSDWISManager();
        ssm.sdwis = new SampleSDWISViewDO();

        return ssm;
    }

    public static SampleSDWISManager fetchBySampleId(Integer sampleId) throws Exception {
        return proxy().fetch(sampleId);
    }

    // setters/getters
    public SampleSDWISViewDO getSDWIS() {
        return sdwis;
    }

    public void setSDWIS(SampleSDWISViewDO sdwisDO) {
        sdwis = sdwisDO;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    // manager methods
    public SampleSDWISManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleSDWISManager update() throws Exception {
        return proxy().update(this);
    }

    public PwsDO validatePwsId(String pwsId) throws Exception {
        PwsDO pwsDO;
        
        try{
            pwsDO = proxy().fetchPwsByPwsId(pwsId);
            
        }catch(NotFoundException e){
            ValidationErrorsList el = new ValidationErrorsList();
            el.add(new FieldErrorException("invalidPwsException", SampleMeta.getSDWISPwsId()));
            throw el;
            
        }catch(Exception e){
            throw e;
        }
        
        return pwsDO;
    }
    
    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }
    
    private static SampleSDWISManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleSDWISManagerProxy();

        return proxy;
    }
}
