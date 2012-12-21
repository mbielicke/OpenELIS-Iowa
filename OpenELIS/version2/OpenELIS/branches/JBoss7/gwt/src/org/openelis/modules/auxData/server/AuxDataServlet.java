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
package org.openelis.modules.auxData.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.AuxDataBean;
import org.openelis.bean.AuxDataManagerBean;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.IdVO;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.AuxDataManager;
import org.openelis.modules.auxData.client.AuxDataServiceInt;

@WebServlet("/openelis/auxData")
public class AuxDataServlet extends AppServlet implements AuxDataServiceInt {
    
    private static final long serialVersionUID = 1L;

    @EJB
    AuxDataManagerBean auxDataManager;
    
    @EJB
    AuxDataBean        auxData;
    
    public AuxDataManager fetchById(AuxDataDO auxData) throws Exception {
        return auxDataManager.fetchById(auxData.getReferenceId(), auxData.getReferenceTableId());
    }   
    
    public ArrayList<AuxDataViewDO> fetchByRefId(AuxDataDO data) throws Exception {
        return auxData.fetchById(data.getReferenceId(), data.getReferenceTableId());
    }
    
    public IdVO getAuxGroupIdFromSystemVariable(String sysVariableKey) throws Exception {
        return auxData.fetchGroupIdBySystemVariable(sysVariableKey);
    }
}
