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
package org.openelis.modules.scriptlet.client.ms;

import static org.openelis.modules.main.client.Logger.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;
import org.openelis.modules.analyteParameter1.client.AnalyteParameterService1;
import org.openelis.modules.qaevent.client.QaEventService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.systemvariable1.client.SystemVariableService1Impl;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * This class is used for providing the front-end functionality for the
 * "ms (Maternal Screening)" test scriptlets
 */
public class ScriptletProxy implements org.openelis.scriptlet.ms.ScriptletProxy {

    @Override
    public DictionaryDO getDictionaryById(Integer id) throws Exception {
        return DictionaryCache.getById(id);
    }

    @Override
    public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception {
        return DictionaryCache.getBySystemName(systemName);
    }
    
    @Override
    public SystemVariableDO fetchSystemVariableByName(String name) throws Exception {
        return SystemVariableService1Impl.INSTANCE.fetchByExactName(name);
    }

    @Override
    public void log(Level level, String message, Exception e) {
        logger.log(level, message, e);
    }

    @Override
    public String format(Double risk, String pattern) {
        if (risk == null)
            return null;
        return NumberFormat.getFormat(pattern).format(risk);
    }

    @Override
    public AnalyteParameterManager1 fetchParameters(Integer referenceId, Integer referenceTableId) throws Exception {
        return AnalyteParameterService1.get().fetchByReferenceIdReferenceTableId(referenceId,
                                                                                 referenceTableId);
    }

    @Override
    public Datetime addMonthsToDate(Datetime dt, int months) {
        Date date;        
        
        if (dt == null)
            return null;
        
        date = (Date)dt.getDate().clone();
        CalendarUtil.addMonthsToDate(date, months);
        return new Datetime(dt.getStartCode(), dt.getEndCode(), date);
    }
    
    @Override
    public SampleManager1 fetchByAccession(Integer accessionNumber, Load... elements) throws Exception {
        return SampleService1.get().fetchByAccession(accessionNumber, elements);
    }

    @Override
    public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                  Load... elements) throws Exception {
        return SampleService1.get().fetchByQuery(fields, first, max, elements);
    }

    @Override
    public ArrayList<QaEventDO> fetchByNames(ArrayList<String> names) throws Exception {
        return QaEventService.get().fetchByNames(names);
    }
}