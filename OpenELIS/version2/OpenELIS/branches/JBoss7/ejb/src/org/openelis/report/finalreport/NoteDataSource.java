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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import org.openelis.bean.AnalysisQAEventBean;
import org.openelis.bean.DictionaryBean;
import org.openelis.bean.NoteBean;
import org.openelis.bean.SampleQAEventBean;
import org.openelis.bean.TestTrailerBean;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.utils.EJBFactory;

public class NoteDataSource implements JRRewindableDataSource {

	private NoteBean nl;
    private SampleQAEventBean sl;
    private AnalysisQAEventBean al;
    private TestTrailerBean tl;
	private DictionaryBean dl;
    private String qas, notes, trailer;
    private boolean aOverride, sOverride, hasNext;
	
	private NoteDataSource() {
	}

	private NoteDataSource(Integer sampleId, Integer analysisId, Integer testTrailerId) throws Exception {
	    StringBuffer tmp;
	    ArrayList<SampleQaEventViewDO> slist;
	    ArrayList<AnalysisQaEventViewDO> alist;
	    ArrayList<NoteViewDO> nlist;
	    TestTrailerDO ttData;
	    Integer overrideId;

		try {
			javax.naming.InitialContext ctx;

			ctx = new javax.naming.InitialContext();
            /*dl = (DictionaryLocal) ctx.lookup("openelis/DictionaryBean/local");
            sl = (SampleQAEventLocal) ctx.lookup("openelis/SampleQAEventBean/local");
            nl = (NoteLocal) ctx.lookup("openelis/NoteBean/local");
			if (analysisId != null) {
	            al = (AnalysisQAEventLocal) ctx.lookup("openelis/AnalysisQAEventBean/local");
	            tl = (TestTrailerLocal) ctx.lookup("openelis/TestTrailerBean/local");
			}*/
			dl = EJBFactory.getDictionary();
            sl = EJBFactory.getSampleQAEvent();
            nl = EJBFactory.getNote();
            if (analysisId != null) {
                al = EJBFactory.getAnalysisQAEvent();
                tl = EJBFactory.getTestTrailer();
            }
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}

		sOverride = false;
		aOverride = false;
		overrideId = dl.fetchBySystemName("qaevent_override").getId();

        // for sample lookup, use the absence of analysis id
        if (analysisId == null) {
            // sample level qa events
            try {
                tmp = new StringBuffer();
                slist = sl.fetchExternalBySampleId(sampleId);
                for (SampleQaEventViewDO data: slist) {
                    if (overrideId.equals(data.getTypeId()))
                        sOverride = true;
                    if (tmp.length() > 0)
                        tmp.append(" ");
                    tmp.append(data.getQaEventReportingText());
                }
                qas = tmp.toString();
            } catch (NotFoundException e) {
                // ignore
            }

            // sample level notes
            try {
                nlist = nl.fetchByRefTableRefIdIsExt(ReferenceTable.SAMPLE, sampleId, "Y");
                notes = nlist.get(0).getText();
            } catch (NotFoundException e) {
                // ignore
            }
        } else {
            // analysis level qa events + sample qa override
            try {
                alist = al.fetchExternalByAnalysisId(analysisId);
                tmp = new StringBuffer();
                for (AnalysisQaEventViewDO data: alist) {
                    if (overrideId.equals(data.getTypeId()))
                        aOverride = true;
                    if (tmp.length() > 0)
                        tmp.append(" ");
                    tmp.append(data.getQaEventReportingText());
                }
                qas = tmp.toString();
            } catch (NotFoundException e) {
                // ignore
            }

            try {
                slist = sl.fetchBySampleId(sampleId);
                for (SampleQaEventViewDO data: slist) {
                    if (overrideId.equals(data.getTypeId())) {
                        sOverride = true;
                        break;
                    }
                }
            } catch (NotFoundException e) {
                // ignore
            }

            // analysis level notes
            try {
                nlist = nl.fetchByRefTableRefIdIsExt(ReferenceTable.ANALYSIS, analysisId, "Y");
                notes = nlist.get(0).getText();
            } catch (NotFoundException e) {
                // ignore
            }
            
            // test trailer if not override
            if (testTrailerId != null && !aOverride && !sOverride) {
                ttData = tl.fetchById(testTrailerId);
                trailer = ttData.getText();
            }
        }
        hasNext = true;
	}

	public static NoteDataSource getInstance(Integer sampleId, Integer analysisId, Integer testTrailerId) throws Exception {
		NoteDataSource ds;
	
		ds = null;
		try {
			ds = new NoteDataSource(sampleId, analysisId, testTrailerId);
		} catch (NotFoundException e) {
			ds = new NoteDataSource();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return ds;
	}

	public Object getFieldValue(JRField field) throws JRException {
	    String ret;
	    StringBuffer tmp;
	    
	    if ("styled_text".equals(field.getName())) {
	        tmp = new StringBuffer();
	        if (qas != null && qas.length() > 0) {
	            tmp.append("<style pdfFontName='Times-Bold'>")
	               .append(escape(qas))
	               .append("</style>");
	        }
	        if (notes != null && notes.length() > 0) {
                if (tmp.length() > 0)
                    tmp.append('\n');
	            tmp.append(escape(notes));
	        }
	        if (trailer != null && trailer.length() > 0) {
                if (tmp.length() > 0)
                    tmp.append('\n');
                tmp.append("<style pdfFontName='Times-Italic' size='8'>")
                   .append(escape(trailer))
                   .append("</style>");
	        }
	        ret = tmp.toString();
	    } else if ("qa_text".equals(field.getName())) {
            ret = qas;
	    } else if ("note_text".equals(field.getName())) {
	        ret = notes;
	    } else if ("trailer_text".equals(field.getName())) {
	        ret = trailer;
	    } else if ("ana_override".equals(field.getName())) {
            ret = aOverride?"Y":"N";
	    } else if ("sam_override".equals(field.getName())) {
            ret = sOverride?"Y":"N";
	    } else {
            ret = null;
	    }
	    //System.out.println(field.getName()+"="+ret);      
	    return ret;
	}

	public boolean next() throws JRException {
	    if (hasNext) {
	        hasNext = false;
	        return qas != null || notes != null || trailer != null;
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
	
    public StringBuffer escape(String text) {
        StringBuffer tmp;
            
        tmp = new StringBuffer();
        for (char c: text.toCharArray()) {
            switch (c) {
                case '<':
                    tmp.append("&lt;");
                    break;
                case '>':
                    tmp.append("&gt;");
                    break;
                case '\"':
                    tmp.append("&quot;");
                    break;
                case '\'':
                    tmp.append("&#039;");
                    break;
                case '&':
                    tmp.append("&amp;");
                    break;
                default:
                    tmp.append(c);
            }
        }
        return tmp;
    }
}