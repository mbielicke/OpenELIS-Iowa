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
package org.openelis.local;

import java.util.ArrayList;

import javax.ejb.Local;

import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;

@Local
public interface AuxDataLocal {
    public ArrayList<AuxDataViewDO> fetchById(Integer referenceId, Integer referenceTableId) throws Exception;
    public ArrayList<AuxDataViewDO> fetchByIds(ArrayList<Integer> referenceIds, Integer referenceTableId);
    public ArrayList<AuxDataViewDO> fetchForDataView(Integer referenceTableId, ArrayList<Integer> ids) throws Exception;
    public ArrayList<AuxDataViewDO> fetchByIdAnalyteName(Integer referenceId, Integer referenceTableId, String analyteName) throws Exception;
    public ArrayList<AuxDataViewDO> fetchByRefIdRefTableIdGroupName(Integer referenceId, Integer referenceTableId, String auxGroupName) throws Exception;
    public AuxDataDO add(AuxDataDO data) throws Exception;
    public AuxDataDO update(AuxDataDO data) throws Exception;
    public void delete(AuxDataDO data) throws Exception;
}