/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.scriptlet.ms;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;

/**
 * This interface is implemented by the proxies for the "ms (Maternal Screening)"
 * test scriptlets
 */
public interface ScriptletProxy {
    public DictionaryDO getDictionaryById(Integer id) throws Exception;

    public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception;
    
    public SystemVariableDO fetchSystemVariableByName(String name) throws Exception;

    public void log(Level level, String message, Exception e);

    public String format(Double risk, String pattern);
    
    public AnalyteParameterManager1 fetchParameters(Integer referenceId, Integer referenceTableId) throws Exception;
    
    public Datetime addMonthsToDate(Datetime dt, int months);
    
    public SampleManager1 fetchByAccession(Integer accessionNumber, SampleManager1.Load... elements) throws Exception;
    
    public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                  SampleManager1.Load... elements) throws Exception;
    
    public ArrayList<QaEventDO> fetchByNames(ArrayList<String> names) throws Exception;
}
