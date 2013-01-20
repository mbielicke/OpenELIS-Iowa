/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleHumanDO;
import org.openelis.gwt.common.ValidationErrorsList;

public class SampleHumanManager implements Serializable, SampleDomainInt {

    private static final long                    serialVersionUID = 1L;

    protected Integer                            sampleId;
    protected ProviderDO                         provider;
    protected PatientDO                          patient;
    protected SampleHumanDO                      human;

   // protected transient SampleHumanManagerIOInt manager;

    /**
     * Creates a new instance of this object.
     */
    public static SampleHumanManager getInstance() {
        SampleHumanManager shm;

        shm = new SampleHumanManager();

        return shm;
    }
    
    public static SampleHumanManager findBySampleId(Integer sampleId) throws Exception  {
        SampleHumanManager shm;

        shm = new SampleHumanManager();
        shm.human = new SampleHumanDO();
        shm.setSampleId(sampleId);
        
        shm.fetch();

        return shm;
    }

    public void setSampleId(Integer id) {
        sampleId = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

//    public ProviderDO getProvider() {
//        fetch();
//        if(provider == null)
//  //          provider = manager().fetchProvider(human.getProviderId());
//        
//        return provider;
//    }
//
//    public void setProvider(ProviderDO provider) {
//        this.provider = provider;
//    }
//
//    public PatientDO getPatient() {
//        fetch();
//        
//        if(patient == null)
// //           patient = manager().fetchPatient(human.getPatientId());
//        
//        return patient;
//    }

    public void setPatient(PatientDO patient) {
        this.patient = patient;
    }

    public SampleHumanDO getHuman() {
        fetch();
        return human;
    }

    //manager methods
   // public Integer update() {
   //     Integer newId = manager().update(this);
  //      human.setId(newId);
        
  //      return newId;
  //  }
    
    public SampleHumanDO fetch(){
  //      human = manager().fetch(sampleId);
        return human;
    }
    
    public SampleHumanManager add() throws Exception {
        return null;
    }
    
    public SampleHumanManager update() throws Exception {
        return null;
    }

    public void delete() throws Exception {
        // TODO Auto-generated method stub
        
    }
    
    public void validate() throws Exception {
    }

    public void validate(ValidationErrorsList errorsList) throws Exception {

    }
}
