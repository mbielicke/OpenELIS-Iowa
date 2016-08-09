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
import java.util.logging.Level;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for tests with serogroup column analytes. It determines the
 * result based on the value specified for serogroup.
 */
public class SerogroupResultScriptlet1 implements ScriptletInt<ScriptletObject> {

    private SerogroupResultScriptlet1Proxy proxy;

    private Integer            analysisId;

    public SerogroupResultScriptlet1(SerogroupResultScriptlet1Proxy proxy, Integer analysisId) throws Exception {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing SerogroupResultScriptlet1", null);

        proxy.log(Level.FINE, "Initialized SerogroupResultScriptlet1", null);
    }

    @Override
    public ScriptletObject run(ScriptletObject data) {
        int i, j;
        AnalysisViewDO ana;
        ResultViewDO res;
        SampleManager1 sm;
        WorksheetManager1 wm;
        WorksheetResultViewDO wrVDO;

        proxy.log(Level.FINE, "In SerogroupResultScriptlet1.run", null);
        if (data instanceof SampleSO) {
            sm = ((SampleSO)data).getManager();
            ana = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(analysisId));
            if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
                Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
                return data;
    
            /*
             * manage result changed
             */
            res = null;
            if (((SampleSO)data).getActionBefore().contains(SampleSO.Action_Before.COMPLETE)) {
                if (analysisId.equals(ana.getId())) {
                   for (i = 0; i < sm.result.count(ana); i++ ) {
                       for (j = 0; j < sm.result.count(ana, i); j++ ) {
                           res = sm.result.get(ana, i, j);
                           if ("serogroup".equals(res.getAnalyteExternalId())) {
                               setResult((SampleSO)data, ana, res);
                               break;
                           }
                       }
                   }
                }
            } else if (((SampleSO)data).getActionBefore().contains(SampleSO.Action_Before.RESULT)) {
                res = (ResultViewDO)sm.getObject(((SampleSO)data).getUid());
                if (analysisId.equals(res.getAnalysisId()) && "serogroup".equals(res.getAnalyteExternalId()))
                    setResult((SampleSO)data, ana, res);
            }
        } else if (data instanceof WorksheetSO && ((WorksheetSO)data).getActionBefore().contains(WorksheetSO.Action_Before.RESULT)) {
            wm = ((WorksheetSO)data).getManager();
            wrVDO = (WorksheetResultViewDO)wm.getObject(((WorksheetSO)data).getUid());
            setResult((WorksheetSO)data, wrVDO);
        }

        return data;
    }

    /**
     * Sets the value of result based on the value of serogroup
     */
    private void setResult(SampleSO data, AnalysisViewDO ana, ResultViewDO resSero) {
        int i, j;
        DictionaryDO dict;
        SampleManager1 sm;
        ResultViewDO res;
        ResultFormatter rf;
        TestManager tm;

        /*
         * find the values for the various analytes
         */
        sm = data.getManager();
        res = null;
        rowLoop:
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (res.getId().equals(resSero.getId())) {
                    res = sm.result.get(ana, i, 0);
                    break rowLoop;
                }
            }
        }

        try {
            dict = getDictionaryById(resSero.getValue());
            if (dict == null || dict.getRelatedEntryId() == null)
                return;

            /*
             * get the value for result
             */
            dict = getDictionaryById(dict.getRelatedEntryId());
            if (dict == null)
                return;

            /*
             * set the result
             */
            tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
            rf = tm.getFormatter();
            if (ResultHelper.formatValue(res, dict.getEntry(), ana.getUnitOfMeasureId(), rf)) {
                proxy.log(Level.FINE, "Setting the value of result as: " +
                                      dict.getEntry(), null);
                data.addRerun(res.getAnalyteExternalId());
                data.addChangedUid(Constants.uid().getResult(res.getId()));
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Sets the value of result based on the value of serogroup
     */
    private void setResult(WorksheetSO data, WorksheetResultViewDO res) {
        Integer changedCol, resultCol, seroCol;
        ArrayList<IdNameVO> worksheetColumns;
        DictionaryDO dict;
        WorksheetManager1 wm;

        wm = data.getManager();
        try {
            worksheetColumns = proxy.getColumnNames(wm.getWorksheet().getFormatId());
        } catch (Exception anyE) {
            data.setStatus(Status.FAILED);
            data.addException(new Exception("Error loading column names for format; " + anyE.getMessage()));
            return;
        }

        if (data.getChanged() == null || data.getChanged().length() <= 0)
            return;
        
        changedCol = Integer.parseInt(data.getChanged());
        resultCol = -1;
        seroCol = -1;
        for (IdNameVO col : worksheetColumns) {
            if ("final_value".equals(col.getName())) {
                resultCol = col.getId();
            } else if ("serogroup".equals(col.getName())) {
                if (changedCol.equals(col.getId() + 12))
                    seroCol = col.getId();
                else
                    return;
            }
        }
        
        if (resultCol == -1 || seroCol == -1) {
            data.setStatus(Status.FAILED);
            data.addException(new Exception("Invalid column format for this scriptlet."));
            return;
        }

        try {
            dict = getDictionaryByEntry(res.getValueAt(seroCol));
            if (dict == null || dict.getRelatedEntryId() == null)
                return;

            /*
             * get the value for result
             */
            dict = getDictionaryById(dict.getRelatedEntryId());
            if (dict == null)
                return;

            /*
             * set the result
             */
            res.setValueAt(resultCol, dict.getEntry());
            proxy.log(Level.FINE, "Setting the value of result as: " + dict.getEntry(), null);
            data.addRerun(String.valueOf(resultCol + 12));
            data.addChangedUid(Constants.uid().getResult(res.getId()));
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Returns the dictionary entry whose id's string equivalent is the passed
     * value
     */
    private DictionaryDO getDictionaryById(Integer id) throws Exception {
        if (id == null)
            return null;

        return proxy.getDictionaryById(id);
    }

    /**
     * Returns the dictionary entry whose id's string equivalent is the passed
     * value
     */
    private DictionaryDO getDictionaryById(String id) throws Exception {
        if (id == null)
            return null;

        return proxy.getDictionaryById(Integer.valueOf(id));
    }

    /**
     * Returns the dictionary entry whose value is the passed value
     */
    private DictionaryViewDO getDictionaryByEntry(String entry) throws Exception {
        ArrayList<DictionaryViewDO> dictList;
        
        if (entry != null) {
            dictList = proxy.getDictionaryByEntry(entry);
            if (dictList != null && dictList.size() > 0)
                return dictList.get(0);
        }
        
        return null;
    }
}