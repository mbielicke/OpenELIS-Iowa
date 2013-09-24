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
package org.openelis.modules.worksheetBuilder.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetQcChoiceVO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorksheetBuilderServiceIntAsync {

    void getColumnNames(Integer formatId, AsyncCallback<ArrayList<IdNameVO>> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchAnalysesByView(Query query, AsyncCallback<ArrayList<AnalysisViewVO>> callback);
    
    void fetchUnitsForWorksheetAutocomplete(Integer analysisId, String unitOfMeasure,
                                            AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchAnalytesByAnalysis(Integer analysisId, Integer testId, AsyncCallback<ArrayList<ResultViewDO>> callback);

    void loadTemplate(WorksheetManager1 wm, Integer testId, AsyncCallback<WorksheetQcChoiceVO> callback);

    void initializeResults(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> analyses,
                           AsyncCallback<WorksheetManager1> callback);

    void initializeResultsFromOther(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> fromAnalyses,
                                    ArrayList<WorksheetAnalysisViewDO> toAnalyses, 
                                    Integer fromWorksheetId, AsyncCallback<WorksheetManager1> callback);

    void sortItems(WorksheetManager1 wm, ArrayList<Object> keys, int direction,
                   AsyncCallback<WorksheetManager1> callback);
}