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
package org.openelis.modules.worksheetBuilder.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.ResultBean;
import org.openelis.bean.TestAnalyteBean;
import org.openelis.bean.TestTypeOfSampleBean;
import org.openelis.bean.WorksheetBean;
import org.openelis.bean.WorksheetManager1Bean;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetQcChoiceVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.worksheetBuilder.client.WorksheetBuilderServiceInt;

@WebServlet("/openelis/worksheetBuilder")
public class WorksheetBuilderServlet extends RemoteServlet implements WorksheetBuilderServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    ResultBean            result;
    
    @EJB
    TestAnalyteBean       testAnalyte;
    
    @EJB
    TestTypeOfSampleBean  testTypeOfSample;
    
    @EJB
    WorksheetBean         worksheet;

    @EJB
    WorksheetManager1Bean worksheetManager;

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return worksheet.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<AnalysisViewVO> fetchAnalysesByView(Query query) throws Exception {
        try {
            return worksheet.fetchAnalysesByView(query.getFields(), 0, query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdNameVO> fetchUnitsForWorksheetAutocomplete(Integer analysisId, String unitOfMeasure) throws Exception {
        try {
            return testTypeOfSample.fetchUnitsForWorksheetAutocomplete(analysisId, unitOfMeasure);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<ResultViewDO> fetchAnalytesByAnalysis(Integer analysisId, Integer testId) throws Exception {
        try {
            return worksheetManager.fetchAnalytesByAnalysis(analysisId, testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        try {
            return worksheet.getColumnNames(formatId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetQcChoiceVO loadTemplate(WorksheetManager1 wm, Integer testId) throws Exception {
        try {
            return worksheetManager.loadTemplate(wm, testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 initializeResults(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> analyses) throws Exception {
        try {
            return worksheetManager.initializeResults(wm, analyses);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 initializeResultsFromOther(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> fromAnalyses,
                                                        ArrayList<WorksheetAnalysisViewDO> toAnalyses,
                                                        Integer fromWorksheetId) throws Exception {
        try {
            return worksheetManager.initializeResultsFromOther(wm, fromAnalyses,
                                                               toAnalyses, fromWorksheetId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 sortItems(WorksheetManager1 wm, int col, int dir) throws Exception {
        try {
            return worksheetManager.sortItems(wm, col, dir);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}