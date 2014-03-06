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
package org.openelis.modules.worksheet1.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetQcChoiceVO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1.Load;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("worksheet1")
public interface WorksheetServiceInt1 extends XsrfProtectedService {
    public WorksheetManager1 getInstance() throws Exception;

    public WorksheetManager1 fetchById(Integer worksheetId, WorksheetManager1.Load... elements) throws Exception;

    public ArrayList<IdNameVO> query(Query query) throws Exception;

    public WorksheetManager1 fetchForUpdate(Integer worksheetId) throws Exception;

    public WorksheetManager1 unlock(Integer worksheetId, Load... elements) throws Exception;

    public WorksheetManager1 update(WorksheetManager1 wm) throws Exception;

    public ArrayList<AnalysisViewVO> fetchAnalysesByView(Query query) throws Exception;

    public ArrayList<IdNameVO> fetchUnitsForWorksheetAutocomplete(Integer analysisId,
                                                                  String unitOfMeasure) throws Exception;

    public ArrayList<ResultViewDO> fetchAnalytesByAnalysis(Integer analysisId, Integer testId) throws Exception;

    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception;

    public ArrayList<IdNameVO> getHeaderLabelsForScreen(WorksheetManager1 manager) throws Exception;

    public WorksheetQcChoiceVO loadTemplate(WorksheetManager1 wm, Integer testId) throws Exception;

    public WorksheetManager1 initializeResults(WorksheetManager1 wm,
                                               ArrayList<WorksheetAnalysisViewDO> analyses) throws Exception;

    public WorksheetManager1 initializeResultsFromOther(WorksheetManager1 wm,
                                                        ArrayList<WorksheetAnalysisViewDO> fromAnalyses,
                                                        ArrayList<WorksheetAnalysisViewDO> toAnalyses,
                                                        Integer fromWorksheetId) throws Exception;

    public WorksheetManager1 sortItems(WorksheetManager1 wm, int col, int dir) throws Exception;

    public WorksheetManager1 exportToExcel(WorksheetManager1 wm) throws Exception;

    public WorksheetManager1 importFromExcel(WorksheetManager1 wm) throws Exception;

    ReportStatus getExportToExcelStatus() throws Exception;
}