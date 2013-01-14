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
package org.openelis.local;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.ejb.Local;

import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.MCLViolationReportVO;
import org.openelis.domain.SDWISUnloadReportVO;
import org.openelis.domain.SampleItemDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.manager.TestManager;

@Local
public interface AnalysisLocal {
    public AnalysisViewDO fetchById(Integer id) throws Exception;

    public ArrayList<AnalysisViewDO> fetchBySampleId(Integer sampleId) throws Exception;

    public ArrayList<SDWISUnloadReportVO> fetchBySampleIdForSDWISUnloadReport(Integer sampleId) throws Exception;

    public ArrayList<AnalysisViewDO> fetchBySampleItemId(Integer sampleItemId) throws Exception;

    public ArrayList<AnalysisViewDO> fetchBySampleItemIds(ArrayList<Integer> sampleItemIds);

    public ArrayList<MCLViolationReportVO> fetchForMCLViolationReport(Date beginPrinted,
                                                                      Date endPrinted) throws Exception;

    public AnalysisDO add(AnalysisDO data) throws Exception;

    public AnalysisDO update(AnalysisDO data) throws Exception;

    public void updatePrintedDate(Integer id, Datetime timeStamp) throws Exception;

    public void updatePrintedDate(Set<Integer> ids, Datetime timeStamp) throws Exception;

    public void delete(AnalysisDO data) throws Exception;

    public void validate(AnalysisDO data, TestManager tm, Integer accession,
                         SampleItemDO item, boolean ignoreWarning) throws Exception;
}
