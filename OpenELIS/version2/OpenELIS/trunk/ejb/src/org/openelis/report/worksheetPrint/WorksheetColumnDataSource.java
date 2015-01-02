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

import org.openelis.bean.CategoryCacheBean;
import org.openelis.bean.DictionaryCacheBean;
import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.utils.EJBFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class WorksheetColumnDataSource implements JRRewindableDataSource {

    private int columnIndex;
    private ArrayList<DictionaryDO> columnDOs;
    private DataObject resultRecord;
	
	private WorksheetColumnDataSource(DataObject resultRecord, Integer formatId) throws Exception {
        CategoryCacheBean cc;
        CategoryCacheVO ccVO;
        DictionaryCacheBean dc;
        DictionaryDO dictDO;

		try {
            cc = EJBFactory.getCategoryCache();
            dc = EJBFactory.getDictionaryCache();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}

        if (resultRecord != null && formatId != null) {
            this.resultRecord = resultRecord;
            try {
                dictDO = dc.getById(formatId);
                ccVO = cc.getBySystemName(dictDO.getSystemName());
                columnDOs = ccVO.getDictionaryList();
            } catch (NotFoundException e) {
                // ignore
            }
            columnIndex = -1;
        }
	}

	public static WorksheetColumnDataSource getInstance(DataObject resultRecord, Integer formatId) throws Exception {
		WorksheetColumnDataSource ds;
	
		ds = null;
		try {
			ds = new WorksheetColumnDataSource(resultRecord, formatId);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return ds;
	}

	public Object getFieldValue(JRField field) throws JRException {
        Object ret;
        DictionaryDO columnDO;

	    ret = null;
	    columnDO = columnDOs.get(columnIndex);
	    if (columnDO != null) {
	        if ("analyte_name".equals(field.getName())) {
	            ret = columnDO.getEntry();
            } else if ("value".equals(field.getName())) {
                if (resultRecord instanceof WorksheetResultViewDO)
                    ret = ((WorksheetResultViewDO)resultRecord).getValueAt(columnIndex);
                else if (resultRecord instanceof WorksheetQcResultViewDO)
                    ret = ((WorksheetQcResultViewDO)resultRecord).getValueAt(columnIndex);
    	    }
	    }
	    return ret;
	}

	public boolean next() throws JRException {
        boolean hasNext = false;

        if (resultRecord != null && columnDOs != null && columnDOs.size() > 0) {
            columnIndex++;
            hasNext = columnIndex < columnDOs.size();
        }

        return hasNext;
	}
	
	public void moveFirst() throws JRException {
        columnIndex = -1;
	}
}