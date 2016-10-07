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
package org.openelis.report.finalreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.openelis.bean.AuxDataBean;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.ui.common.NotFoundException;
import org.openelis.utils.EJBFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class SampleAuxDataDataSource implements JRRewindableDataSource {

	private ArrayList<HashMap<String, String>> rows;
	private HashMap<String, String> currentRow;
	private Iterator<HashMap<String, String>> iter;
	private AuxDataBean adb;
	
	private SampleAuxDataDataSource() {
	}

	private SampleAuxDataDataSource(Integer sampleId) throws Exception {
	    int i, mid;
	    ArrayList<AuxDataViewDO> auxData;
	    AuxDataViewDO adVDO;
		HashMap<String, String> row;
		Iterator<AuxDataViewDO> adIter;

        try {
            adb = EJBFactory.getAuxData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        try {
			auxData = adb.fetchById(sampleId, Constants.table().SAMPLE);
			adIter = auxData.iterator();
			while (adIter.hasNext()) {
			    adVDO = adIter.next();
			    if (!"Y".equals(adVDO.getIsReportable()))
			        adIter.remove();
			}
		} catch (Exception e) {
			throw e;
		}

        mid = auxData.size() / 2 + (auxData.size() % 2);
		rows = new ArrayList<HashMap<String, String>>();
        row = new HashMap<String, String>();
		for (i = 0; i < auxData.size(); i++) {
		    adVDO = auxData.get(i);
		    if (i < mid) {
		        row = new HashMap<String, String>();
		        rows.add(row);
	            row.put("analyte_name0", adVDO.getAnalyteName());
	            if (adVDO.getDictionary() != null)
	                row.put("aux_data_value0", adVDO.getDictionary());
	            else
	                row.put("aux_data_value0", adVDO.getValue());
		    } else {
		        row = rows.get(i - mid);
	            row.put("analyte_name1", adVDO.getAnalyteName());
	            if (adVDO.getDictionary() != null)
	                row.put("aux_data_value1", adVDO.getDictionary());
	            else
	                row.put("aux_data_value1", adVDO.getValue());
		    }
		}
		iter = rows.iterator();
	}

	public static SampleAuxDataDataSource getInstance(Integer sampleId) throws Exception {
		SampleAuxDataDataSource ds;
	
		ds = null;
		try {
			ds = new SampleAuxDataDataSource(sampleId);
		} catch (NotFoundException e) {
			ds = new SampleAuxDataDataSource();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return ds;
	}

	public Object getFieldValue(JRField field) throws JRException {
		return currentRow.get(field.getName());
	}

	public boolean next() throws JRException {
		if (rows != null && iter.hasNext()) {
			currentRow = iter.next();
			return true;
		}
		return false;
	}

    public void moveFirst() throws JRException {
        if (rows != null)
            iter = rows.iterator();
    }
}