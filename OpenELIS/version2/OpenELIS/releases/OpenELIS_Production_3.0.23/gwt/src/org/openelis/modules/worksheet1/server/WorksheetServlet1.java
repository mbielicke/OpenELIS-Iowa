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
package org.openelis.modules.worksheet1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SessionCacheBean;
import org.openelis.bean.TestTypeOfSampleBean;
import org.openelis.bean.WorksheetBean;
import org.openelis.bean.WorksheetExcelHelperBean;
import org.openelis.bean.WorksheetManager1Bean;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetQcChoiceVO;
import org.openelis.domain.WorksheetResultsTransferVO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1.Load;
import org.openelis.modules.worksheet1.client.WorksheetServiceInt1;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/worksheet1")
public class WorksheetServlet1 extends RemoteServlet implements WorksheetServiceInt1 {

    private static final long serialVersionUID = 1L;
    
    @EJB
    private SessionCacheBean           session;

    @EJB
    TestTypeOfSampleBean  testTypeOfSample;
    
    @EJB
    WorksheetBean worksheet;
    
    @EJB
    WorksheetExcelHelperBean worksheetExcelHelper;

    @EJB
    WorksheetManager1Bean worksheetManager1;

    public WorksheetManager1 getInstance() throws Exception {
        try {
            return worksheetManager1.getInstance();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 fetchById(Integer worksheetId, WorksheetManager1.Load... elements) throws Exception {
        try {
            return worksheetManager1.fetchById(worksheetId, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<WorksheetManager1> fetchByIds(ArrayList<Integer> worksheetIds,
                                                   WorksheetManager1.Load... elements) throws Exception {
        try {
            return worksheetManager1.fetchByIds(worksheetIds, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return worksheet.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<WorksheetViewDO> queryForLookup(Query query) throws Exception {
        ArrayList<IdNameVO> idVOs;
        ArrayList<Integer> ids;
        
        try {
            idVOs = worksheet.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
            ids = new ArrayList<Integer>();
            for (IdNameVO vo : idVOs)
                ids.add(vo.getId());
            return worksheet.fetchByIds(ids);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager1 fetchForUpdate(Integer worksheetId) throws Exception {
        try {
            return worksheetManager1.fetchForUpdate(worksheetId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetResultsTransferVO fetchForTransfer(Integer worksheetId) throws Exception {
        try {
            return worksheetManager1.fetchForTransfer(worksheetId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 unlock(Integer worksheetId, Load... elements) throws Exception {
        try {
            return worksheetManager1.unlock(worksheetId, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 update(WorksheetManager1 wm, WorksheetManager1.ANALYSIS_UPDATE updateFlag) throws Exception {
        try {
            return worksheetManager1.update(wm, updateFlag);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetResultsTransferVO transferResults(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> waVDOs,
                                                      ArrayList<SampleManager1> sampleMans,
                                                      boolean ignoreWarnings) throws Exception {
        try {
            return worksheetManager1.transferResults(wm, waVDOs, sampleMans, ignoreWarnings);
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
            return worksheetManager1.fetchAnalytesByAnalysis(analysisId, testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        try {
            return worksheetManager1.getColumnNames(formatId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> getHeaderLabelsForScreen(WorksheetManager1 manager) throws Exception {
        try {        
            return worksheetManager1.getHeaderLabelsForScreen(manager.getWorksheet().getFormatId());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetQcChoiceVO loadTemplate(WorksheetManager1 wm, Integer testId) throws Exception {
        try {
            return worksheetManager1.loadTemplate(wm, testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 initializeResults(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> analyses) throws Exception {
        try {
            return worksheetManager1.initializeResults(wm, analyses);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 initializeResultsFromOther(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> fromAnalyses,
                                                        ArrayList<WorksheetAnalysisViewDO> toAnalyses,
                                                        Integer fromWorksheetId) throws Exception {
        try {
            return worksheetManager1.initializeResultsFromOther(wm, fromAnalyses,
                                                               toAnalyses, fromWorksheetId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 sortItems(WorksheetManager1 wm, int col, int dir) throws Exception {
        try {
            return worksheetManager1.sortItems(wm, col, dir);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager1 exportToExcel(WorksheetManager1 manager) throws Exception {
        try {        
            return worksheetExcelHelper.exportToExcel(manager);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager1 importFromExcel(WorksheetManager1 manager) throws Exception {
        try {        
            return worksheetExcelHelper.importFromExcel(manager);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ReportStatus getExportToExcelStatus() throws Exception {
        return (ReportStatus)session.getAttribute("ExportToExcelStatus");
    }

    public ReportStatus getImportFromExcelStatus() throws Exception {
        return (ReportStatus)session.getAttribute("ImportFromExcelStatus");
    }
}