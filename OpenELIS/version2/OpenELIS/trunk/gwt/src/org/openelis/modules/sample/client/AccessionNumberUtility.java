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
package org.openelis.modules.sample.client;

import org.openelis.gwt.services.ScreenService;
import org.openelis.manager.SampleManager;

public class AccessionNumberUtility {
    
    protected static final String SAMPLE_SERVICE_URL = "org.openelis.modules.sample.server.SampleService";
    protected ScreenService service;
    
    public AccessionNumberUtility(){
        service = new ScreenService("OpenELISServlet?service="+SAMPLE_SERVICE_URL);
    }
    
    public SampleManager accessionNumberEntered(SampleManager man) throws Exception {
        SampleManager tmpMan;
        
        tmpMan = service.call("validateAccessionNumber", man.getSample());
        
        if(tmpMan != null)
            return tmpMan;
            
         return man;
    }
}

/*GWT
 * public void validateAccessionNumber(SampleDO sampleDO) throws Exception {
        service.call("validateAccessionNumber", sampleDO);
    }
 */

/* EJB
public void validateAccessionNumber(SampleDO sampleDO) throws Exception {
ValidationErrorsList errorsList;

SystemVariableLocal svl = sysVariableLocal();
ArrayList<SystemVariableDO> sysVarList;
SystemVariableDO sysVarDO;
SampleDO checkSample;

errorsList = new ValidationErrorsList();

// get system variable
sysVarList = svl.fetchByName("last_accession_number", 1);
sysVarDO = sysVarList.get(0);

// we need to set the error
if (sampleDO.getAccessionNumber().compareTo(new Integer(sysVarDO.getValue())) > 0)
    errorsList.add(new FieldErrorException("accessionNumberNotInUse",
                                           SampleMeta.getAccessionNumber()));

// check for dups
try {
    checkSample = sampleLocal().fetchByAccessionNumber(sampleDO.getAccessionNumber());

    if (checkSample != null && !checkSample.getId().equals(sampleDO.getId()))
        errorsList.add(new FieldErrorException("accessionNumberDuplicate",
                                               SampleMeta.getAccessionNumber()));

} catch (Exception e) {
    // resultnotfound exception good in this case, no error
}

if (errorsList.size() > 0)
    throw errorsList;
}
*/