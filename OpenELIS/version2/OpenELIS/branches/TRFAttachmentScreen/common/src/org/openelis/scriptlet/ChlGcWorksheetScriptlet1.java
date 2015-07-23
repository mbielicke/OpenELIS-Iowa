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
package org.openelis.scriptlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.WorksheetManager1;
import org.openelis.scriptlet.WorksheetSO.Action_Before;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;

/**
 * The scriptlet for "chl-gc" and "chl-gc cbss" tests. It pools samples on the
 * worksheet, grouping four to a position by sample type. The scriptlet only
 * considers analyses in initiated status on sample items with sample type of
 * either "Cervical swab" or "Specimen from vagina". All others are left as
 * single analyses.
 */
public class ChlGcWorksheetScriptlet1 implements ScriptletInt<WorksheetSO> {

    private ChlGcWorksheetScriptlet1Proxy proxy;
    
    public static Integer                 SAMPLE_TYPE_CERVIX, SAMPLE_TYPE_VAGINAL;

    public ChlGcWorksheetScriptlet1(ChlGcWorksheetScriptlet1Proxy proxy) throws Exception {
        this.proxy = proxy;

        proxy.log(Level.FINE, "Initializing ChlGcWorksheetScriptlet1", null);

        if (SAMPLE_TYPE_CERVIX == null) {
            SAMPLE_TYPE_CERVIX = proxy.getDictionaryBySystemName("sample_type_cervix").getId();
            SAMPLE_TYPE_VAGINAL = proxy.getDictionaryBySystemName("sample_type_vaginal").getId();
        }
        
        proxy.log(Level.FINE, "Initialized ChlGcWorksheetScriptlet1", null);
    }

    @Override
    public WorksheetSO run(WorksheetSO data) {
        WorksheetDO wDO;

        proxy.log(Level.FINE, "In ChlGcWorksheetScriptlet1.run", null);
        wDO = data.getManager().getWorksheet();
        if (wDO == null || !Constants.dictionary().WORKSHEET_WORKING.equals(wDO.getStatusId()))
            return data;
    
        if (data.getActionBefore().contains(Action_Before.TEMPLATE_LOAD))
            poolAnalyses(data);
        else if (data.getActionBefore().contains(Action_Before.PRE_TRANSFER))
            requeuePools(data);
        
        return data;
    }

    /**
     * Pool the analyses based on sample type
     */
    private void poolAnalyses(WorksheetSO data) {
        int i, j, lastIndex;
        AnalysisViewDO aVDO;
        ArrayList<Integer> analysisIds;
        ArrayList<SampleManager1> sms;
        ArrayList<WorksheetAnalysisViewDO> nonPooledAnalyses, pooledAnalyses;
        HashMap<Integer, SampleItemViewDO> siVDOsByAnalysisId;
        SampleItemViewDO siVDO;
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO wiDO;
        WorksheetManager1 wm;

        wm = data.getManager();
        analysisIds = new ArrayList<Integer>();
        for (i = 0; i < wm.item.count(); i++) {
            wiDO = wm.item.get(i);
            for (j = 0; j < wm.analysis.count(wiDO); j++) {
                waVDO = wm.analysis.get(wiDO, j);
                if (waVDO.getAnalysisId() != null)
                    analysisIds.add(waVDO.getAnalysisId());
            }
        }
        
        siVDOsByAnalysisId = new HashMap<Integer, SampleItemViewDO>();
        try {
            sms = proxy.fetchSampleManagersByAnalyses(analysisIds);
            for (SampleManager1 sm : sms) {
                for (i = 0; i < sm.item.count(); i++) {
                    siVDO = sm.item.get(i);
                    for (j = 0; j < sm.analysis.count(); j++) {
                        aVDO = sm.analysis.get(j);
                        siVDOsByAnalysisId.put(aVDO.getId(), siVDO);
                    }
                }
            }
        } catch (Exception anyE) {
            data.setStatus(Status.FAILED);
            data.addException(anyE);
            return;
        }

        nonPooledAnalyses = new ArrayList<WorksheetAnalysisViewDO>();
        pooledAnalyses = new ArrayList<WorksheetAnalysisViewDO>();
        for (i = 0; i < wm.item.count(); i++) {
            wiDO = wm.item.get(i);
            for (j = 0; j < wm.analysis.count(wiDO); j++) {
                waVDO = wm.analysis.get(wiDO, j);
                if (waVDO.getAnalysisId() != null) {
                    siVDO = siVDOsByAnalysisId.get(waVDO.getAnalysisId());
                    if ((Constants.dictionary().ANALYSIS_LOGGED_IN.equals(waVDO.getStatusId()) ||
                         Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN.equals(waVDO.getStatusId()) ||
                         Constants.dictionary().ANALYSIS_INITIATED.equals(waVDO.getStatusId()) ||
                         Constants.dictionary().ANALYSIS_ERROR_INITIATED.equals(waVDO.getStatusId())) &&
                        (SAMPLE_TYPE_CERVIX.equals(siVDO.getTypeOfSampleId()) ||
                         SAMPLE_TYPE_VAGINAL.equals(siVDO.getTypeOfSampleId()))) {
                        pooledAnalyses.add(waVDO);
                    } else {
                        nonPooledAnalyses.add(waVDO);
                    }
                } else {
                    nonPooledAnalyses.add(waVDO);
                }
            }
        }

        j = 0;
        wiDO = wm.item.get(j);
        lastIndex = pooledAnalyses.size() / 4 * 4;
        try {
            for (i = 0; i < lastIndex; i++) {
                if (i != 0 && i % 4 == 0) {
                    wiDO = wm.item.get(++j);
                    if (wiDO == null)
                        wiDO = wm.item.add(j);
                }
                waVDO = pooledAnalyses.get(i);
                wm.analysis.move(waVDO, wiDO);
            }

            if (lastIndex != 0)
                j++;
            if (i < pooledAnalyses.size()) {
                for (; i < pooledAnalyses.size(); i++) {
                    wiDO = wm.item.get(j);
                    if (wiDO == null)
                        wiDO = wm.item.add(j);
                    waVDO = pooledAnalyses.get(i);
                    wm.analysis.move(waVDO, wiDO);
                    j++;
                }
            }
                
            for (i = 0; i < nonPooledAnalyses.size(); i++) {
                wiDO = wm.item.get(j);
                if (wiDO == null)
                    wiDO = wm.item.add(j);
                waVDO = nonPooledAnalyses.get(i);
                wm.analysis.move(waVDO, wiDO);
                j++;
            }

            while (j < wm.item.count())
                wm.item.remove(j);
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }
    
    /**
     * If a pooled result is "Detected", set the status of all analyses in the pool
     * to "Requeue"
     */
    private void requeuePools(WorksheetSO data) {
        int i, j, k;
        ArrayList<IdNameVO> worksheetColumns;
        Integer resultColumn;
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO wiDO;
        WorksheetManager1 wm;
        WorksheetResultViewDO wrVDO;

        resultColumn = 0;
        wm = data.getManager();
        try {
            worksheetColumns = proxy.getColumnNames(wm.getWorksheet().getFormatId());
        } catch (Exception anyE) {
            data.setStatus(Status.FAILED);
            data.addException(new Exception("Error loading column names for format; " + anyE.getMessage()));
            return;
        }
        for (IdNameVO col : worksheetColumns) {
            if ("final_value".equals(col.getName())) {
                resultColumn = col.getId() - 10;
                break;
            }
        }

        for (i = 0; i < wm.item.count(); i++) {
            wiDO = wm.item.get(i);
            if (wm.analysis.count(wiDO) > 1) {
                analyses:
                for (j = 0; j < wm.analysis.count(wiDO); j++) {
                    waVDO = wm.analysis.get(wiDO, j);
                    if (waVDO.getAnalysisId() != null && proxy.canEdit(waVDO)) {
                        for (k = 0; k < wm.result.count(waVDO); k++) {
                            wrVDO = wm.result.get(waVDO, k);
                            if ("chl_result".equals(wrVDO.getAnalyteExternalId()) ||
                                "gc_result".equals(wrVDO.getAnalyteExternalId())) {
                                if ("Detected".equals(wrVDO.getValueAt(resultColumn)) ||
                                    "Equivocal".equals(wrVDO.getValueAt(resultColumn))) {
                                    for (j = 0; j < wm.analysis.count(wiDO); j++) {
                                        waVDO = wm.analysis.get(wiDO, j);
                                        waVDO.setStatusId(Constants.dictionary().ANALYSIS_REQUEUE);
                                    }
                                    break analyses;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}