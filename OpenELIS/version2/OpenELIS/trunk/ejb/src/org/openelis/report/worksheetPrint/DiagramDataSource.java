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

import org.openelis.bean.WorksheetManager1Bean;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.NotFoundException;
import org.openelis.utils.EJBFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class DiagramDataSource implements JRDataSource {

    private int rowIndex;
    private ArrayList<RowObject> rowList;
    private Integer diagramCapacity, worksheetId;
    private String worksheetDescription;
	
	private DiagramDataSource(Integer worksheetId, Integer diagramCapacity) throws Exception {
	    int i, j;
	    RowObject row;
	    WorksheetAnalysisViewDO waVDO;
	    WorksheetItemDO wiDO;
	    WorksheetManager1 wman;
        WorksheetManager1Bean wm;

        if (diagramCapacity != null)
            this.diagramCapacity = diagramCapacity;
        else
            this.diagramCapacity = 500;
        
		try {
            wm = EJBFactory.getWorksheetManager1();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}

        if (worksheetId != null) {
            try {
                wman = wm.fetchById(worksheetId, WorksheetManager1.Load.DETAIL);
                this.worksheetId = wman.getWorksheet().getId();
                worksheetDescription = wman.getWorksheet().getDescription();
                
                rowList = new ArrayList<RowObject>();
                for (i = 0; i < wman.item.count(); i++) {
                    wiDO = wman.item.get(i);
                    for (j = 0; j < wman.analysis.count(wiDO); j++) {
                        waVDO = wman.analysis.get(wiDO, j);
                        row = new RowObject(wiDO, waVDO);
                        rowList.add(row);
                    }
                }
            } catch (NotFoundException e) {
                // ignore
            }
            rowIndex = -1;
        }
	}

	public static DiagramDataSource getInstance(Integer worksheetId, Integer diagramCapacity) throws Exception {
		DiagramDataSource ds;
	
		ds = null;
		try {
			ds = new DiagramDataSource(worksheetId, diagramCapacity);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return ds;
	}

	public Object getFieldValue(JRField field) throws JRException {
        Object ret;
	    RowObject row;

	    ret = null;
	    row = rowList.get(rowIndex);
	    if (row != null) {
	        if ("worksheet_id".equals(field.getName())) {
	            ret = worksheetId;
            } else if ("worksheet_description".equals(field.getName())) {
                ret = worksheetDescription; 
	        } else if ("position".equals(field.getName())) {
    	        if (row.wiDO != null)
    	            ret = row.wiDO.getPosition(); 
            } else if ("accession_number".equals(field.getName())) {
                if (row.waVDO != null)
                    ret = row.waVDO.getAccessionNumber();
    	    } else if ("well_label".equals(field.getName())) {
    	        if (row.waVDO != null) {
    	            if (row.waVDO.getAnalysisId() != null)
    	                ret = row.waVDO.getAccessionNumber();
    	            else if (row.waVDO.getQcLotId() != null)
                        ret = row.waVDO.getDescription();
    	        }
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
	
    public String getWellLabel(Integer page, Integer well) {
        Integer rowIndex;
        RowObject row;
        String ret;
        
        ret = null;
        rowIndex = (page - 1) * diagramCapacity + well - 1;
        if (rowIndex < rowList.size()) {
            row = rowList.get(rowIndex);
            if (row.waVDO != null) {
                if (row.waVDO.getAnalysisId() != null)
                    ret = row.waVDO.getAccessionNumber();
                else if (row.waVDO.getQcLotId() != null)
                    ret = row.waVDO.getDescription();
            }
        }
        
        return ret;
    }
    
    private class RowObject {
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO wiDO;

        public RowObject(WorksheetAnalysisViewDO waVDO) {
            this.waVDO = waVDO;
        }

        public RowObject(WorksheetItemDO wiDO, WorksheetAnalysisViewDO waVDO) {
            this.wiDO = wiDO;
            this.waVDO = waVDO;
        }
    }
}