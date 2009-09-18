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
package org.openelis.remote;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameLotNumberDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.QcViewDO;
import org.openelis.gwt.common.data.deprecated.AbstractField;

@Remote
public interface QcRemote {
    
    public QcViewDO getQc(Integer qcId);
    
    public QcViewDO getQcAndUnlock(Integer qcId, String session);
    
    public QcViewDO getQcAndLock(Integer qcId, String session)throws Exception;
    
    public Integer updateQc(QcViewDO qcDO, List<QcAnalyteViewDO> qcAnaDOList)throws Exception;
    
    public List<QcAnalyteViewDO> getQcAnalytes(Integer qcId);
    
    public List<IdNameLotNumberDO> query(ArrayList<AbstractField> fields, int first, int max) throws Exception;   
    
    public List<IdNameDO> qcAutocompleteByName(String name, int numResult);
}
