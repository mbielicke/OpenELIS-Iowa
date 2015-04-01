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
package org.openelis.report.hivHepCTransfer;

import java.util.ArrayList;

import org.openelis.bean.AnalysisQAEventBean;
import org.openelis.bean.SampleQAEventBean;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.utils.EJBFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class QAEventDataSource implements JRRewindableDataSource {

    private SampleQAEventBean sl;
    private AnalysisQAEventBean al;
    private String qas;
    private boolean aOverride, sOverride, hasNext;
	
	private QAEventDataSource() {
	}

	private QAEventDataSource(Integer sampleId, Integer analysisId) throws Exception {
	    StringBuffer tmp;
	    ArrayList<SampleQaEventViewDO> slist;
	    ArrayList<AnalysisQaEventViewDO> alist;

		try {
            sl = EJBFactory.getSampleQAEvent();
            al = EJBFactory.getAnalysisQAEvent();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}

		sOverride = false;
		aOverride = false;

        // sample level qa events
        try {
            tmp = new StringBuffer();
            slist = sl.fetchExternalBySampleId(sampleId);
            for (SampleQaEventViewDO data: slist) {
                if (Constants.dictionary().QAEVENT_OVERRIDE.equals(data.getTypeId()))
                    sOverride = true;
                if (tmp.length() > 0)
                    tmp.append(" ");
                tmp.append(data.getQaEventReportingText());
            }
            qas = tmp.toString();
        } catch (NotFoundException e) {
            // ignore
        }

        // analysis level qa events
        try {
            alist = al.fetchExternalByAnalysisId(analysisId);
            tmp = new StringBuffer();
            for (AnalysisQaEventViewDO data: alist) {
                if (Constants.dictionary().QAEVENT_OVERRIDE.equals(data.getTypeId()))
                    aOverride = true;
                if (tmp.length() > 0)
                    tmp.append(" ");
                tmp.append(data.getQaEventReportingText());
            }
            if (qas == null)
                qas = "";
            else
                qas += " ";
            qas += tmp.toString();
        } catch (NotFoundException e) {
            // ignore
        }

        hasNext = true;
	}

	public static QAEventDataSource getInstance(Integer sampleId, Integer analysisId) throws Exception {
		QAEventDataSource ds;
	
		ds = null;
		try {
			ds = new QAEventDataSource(sampleId, analysisId);
		} catch (NotFoundException e) {
			ds = new QAEventDataSource();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return ds;
	}

	public Object getFieldValue(JRField field) throws JRException {
	    String ret;
	    
	    if ("qa_text".equals(field.getName())) {
            ret = qas;
	    } else if ("ana_override".equals(field.getName())) {
            ret = aOverride?"Y":"N";
	    } else if ("sam_override".equals(field.getName())) {
            ret = sOverride?"Y":"N";
	    } else {
            ret = null;
	    }
	    return ret;
	}

	public boolean next() throws JRException {
	    if (hasNext) {
	        hasNext = false;
	        return qas != null;
	    }
	    return false;
	}
	
    public void moveFirst() throws JRException {
        hasNext = true;
    }

    public boolean hasSampleOverride() {
	    return sOverride;
	}
	
	public boolean hasAnalysisOverride() {
	    return aOverride;
	}
	
	public boolean hasOverride() {
	    return sOverride || aOverride;
	}
}