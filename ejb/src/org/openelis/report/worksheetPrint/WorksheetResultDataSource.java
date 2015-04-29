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
package org.openelis.report.worksheetPrint;

import java.util.ArrayList;

import org.openelis.bean.WorksheetAnalysisBean;
import org.openelis.bean.WorksheetQcResultBean;
import org.openelis.bean.WorksheetResultBean;
import org.openelis.domain.DataObject;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.utils.EJBFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class WorksheetResultDataSource implements JRRewindableDataSource {

    private int rowIndex;
    private ArrayList<DataObject> rowList;
	
	private WorksheetResultDataSource(Integer worksheetAnalysisId, Integer formatId) throws Exception {
        ArrayList<WorksheetQcResultViewDO> wqrList;
        ArrayList<WorksheetResultViewDO> wrList;
        WorksheetAnalysisBean wa;
        WorksheetAnalysisDO waDO;
        WorksheetQcResultBean wqr;
        WorksheetResultBean wr;

		try {
            wa = EJBFactory.getWorksheetAnalysis();
            wqr = EJBFactory.getWorksheetQcResult();
            wr = EJBFactory.getWorksheetResult();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}

        if (worksheetAnalysisId != null && formatId != null) {
            rowList = new ArrayList<DataObject>();
            try {
                waDO = wa.fetchById(worksheetAnalysisId);
                if (waDO.getAnalysisId() != null) {
                    wrList = wr.fetchByWorksheetAnalysisId(waDO.getId());
                    for (WorksheetResultViewDO data : wrList)
                        rowList.add(data);
                } else if (waDO.getQcLotId() != null) {
                    wqrList = wqr.fetchByWorksheetAnalysisId(waDO.getId());
                    for (WorksheetQcResultViewDO data : wqrList)
                        rowList.add(data);
                }
            } catch (NotFoundException e) {
                // ignore
            }
            rowIndex = -1;
        }
	}

	public static WorksheetResultDataSource getInstance(Integer worksheetId, Integer formatId) throws Exception {
		WorksheetResultDataSource ds;
	
		ds = null;
		try {
			ds = new WorksheetResultDataSource(worksheetId, formatId);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return ds;
	}

	public Object getFieldValue(JRField field) throws JRException {
        Object ret;
        DataObject row;

	    ret = null;
	    row = rowList.get(rowIndex);
	    if (row != null) {
	        if ("analyte_name".equals(field.getName())) {
	            if (row instanceof WorksheetResultViewDO)
	                ret = ((WorksheetResultViewDO)row).getAnalyteName();
	            else if (row instanceof WorksheetQcResultViewDO)
                    ret = ((WorksheetQcResultViewDO)row).getAnalyteName();
            } else if ("result_record".equals(field.getName())) {
                ret = row; 
    	    }
	    }
	    return ret;
	}

	public boolean next() throws JRException {
        boolean hasNext = false;

        if (rowList != null) {
            rowIndex++;
            hasNext = rowIndex < rowList.size();
        }

        return hasNext;
	}
    
    public void moveFirst() throws JRException {
        rowIndex = -1;
    }
}