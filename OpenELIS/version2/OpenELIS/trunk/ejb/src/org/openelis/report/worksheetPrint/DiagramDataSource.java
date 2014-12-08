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
import java.util.HashMap;

import org.openelis.bean.WorksheetAnalysisBean;
import org.openelis.bean.WorksheetBean;
import org.openelis.bean.WorksheetItemBean;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetViewDO;
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
        ArrayList<WorksheetAnalysisViewDO> waList;
        ArrayList<WorksheetItemDO> wiList;
	    HashMap<Integer, Integer> waIndexMap, wiIndexMap;
	    Integer index;
	    RowObject row;
        WorksheetAnalysisBean wa;
        WorksheetBean w;
        WorksheetItemBean wi;
        WorksheetViewDO wVDO;

        if (diagramCapacity != null)
            this.diagramCapacity = diagramCapacity;
        else
            this.diagramCapacity = 500;
        
		try {
            w = EJBFactory.getWorksheet();
            wa = EJBFactory.getWorksheetAnalysis();
            wi = EJBFactory.getWorksheetItem();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}

        if (worksheetId != null) {
            try {
                wVDO = w.fetchById(worksheetId);
                this.worksheetId = wVDO.getId();
                worksheetDescription = wVDO.getDescription();
                
                waList = wa.fetchByWorksheetId(worksheetId);
                rowList = new ArrayList<RowObject>();
                wiIndexMap = new HashMap<Integer, Integer>();
                waIndexMap = new HashMap<Integer, Integer>();
                for (WorksheetAnalysisViewDO data: waList) {
                    row = new RowObject(data);
                    rowList.add(row);
                    wiIndexMap.put(data.getWorksheetItemId(), rowList.size() - 1);
                    waIndexMap.put(data.getId(), rowList.size() - 1);
                }
                
                try {
                    wiList = wi.fetchByWorksheetId(worksheetId);
                    for (WorksheetItemDO data2 : wiList) {
                        index = wiIndexMap.get(data2.getId());
                        if (index != null) {
                            row = rowList.get(index);
                            row.wiDO = data2;
                        }
                    }
                } catch (NotFoundException e) {
                    // ignore
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
	
    public String getAccessionNumber(Integer page, Integer well) {
        Integer rowIndex;
        RowObject row;
        String ret;
        
        ret = null;
        rowIndex = (page - 1) * diagramCapacity + well - 1;
        if (rowIndex < rowList.size()) {
            row = rowList.get(rowIndex);
            ret = row.waVDO.getAccessionNumber();
        }
        
        return ret;
    }
    
    private class RowObject {
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO wiDO;

        public RowObject(WorksheetAnalysisViewDO waVDO) {
            this.waVDO = waVDO;
        }
    }
}