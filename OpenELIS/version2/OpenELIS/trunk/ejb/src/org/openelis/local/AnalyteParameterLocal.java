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
import java.util.Date;

import javax.ejb.Local;

import org.openelis.domain.AnalyteParameterDO;
import org.openelis.domain.AnalyteParameterViewDO;

@Local
public interface AnalyteParameterLocal {
    public ArrayList<AnalyteParameterViewDO> fetchActiveByReferenceIdReferenceTableId(Integer referenceId, 
                                                                                      Integer referenceTableId) throws Exception;
    
    public ArrayList<AnalyteParameterViewDO> fetchByAnalyteIdReferenceIdReferenceTableId(Integer analyteId, Integer refId,
                                                                                         Integer refTableId) throws Exception;
    
    public AnalyteParameterViewDO fetchActiveByAnalyteIdReferenceIdReferenceTableId(Integer analyteId, Integer refId, 
                                                                                    Integer refTableId) throws Exception;
    
    public AnalyteParameterDO fetchById(Integer id) throws Exception;   
    
    public AnalyteParameterViewDO add(AnalyteParameterViewDO data) throws Exception;
    
    public AnalyteParameterViewDO update(AnalyteParameterViewDO data) throws Exception;  
    
    public AnalyteParameterViewDO fetchForUpdate(Integer id) throws Exception;   
    
    public AnalyteParameterViewDO abortUpdate(Integer id) throws Exception;   
    
    public void validate(AnalyteParameterViewDO data) throws Exception;
    
    public AnalyteParameterViewDO fetchForQcChartReport (Integer analyteId, Integer refId, 
                                                         Integer refTableId, Date worksheetCreatedDate) throws Exception;
}
