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
package org.openelis.bean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.WorksheetBuilderVO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.meta.AnalysisViewMeta;
import org.openelis.meta.WorksheetBuilderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.TurnaroundUtil;

@Stateless
@SecurityDomain("openelis")

public class WorksheetBuilderBean {

    @EJB
    DictionaryBean         dictionary;
    @EJB
    OrganizationBean       organization;
    @EJB
    SampleOrganizationBean sampleOrganization;
    @EJB
    SystemVariableBean     systemVariable;

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    private static final Logger               log = Logger.getLogger("openelis");
    private static final AnalysisViewMeta     avMeta = new AnalysisViewMeta();
    private static final WorksheetBuilderMeta wbMeta = new WorksheetBuilderMeta();
    
    public ArrayList<IdVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        List returnList = null;
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(wbMeta);
        builder.setSelect("distinct new org.openelis.domain.IdVO("+
                          WorksheetBuilderMeta.getWorksheetId()+") ");
        builder.constructWhere(fields);
        builder.setOrderBy(WorksheetBuilderMeta.getWorksheetId());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);

        builder.setQueryParams(query, fields);

        returnList = query.getResultList();
        if (returnList.isEmpty())
            throw new NotFoundException();
        returnList = (ArrayList<IdVO>) DataBaseUtil.subList(returnList, first, max);
        if (returnList == null)
            throw new LastPageException();
        return (ArrayList<IdVO>) returnList;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetBuilderVO> lookupAnalyses(ArrayList<QueryData> fields, 
                                                          int first, int max) throws Exception {
        int                      i;
        List                     list = null, returnList;
        Query                    query;
        QueryBuilderV2           builder;
        AnalysisViewVO           avVO;
        WorksheetBuilderVO     wcVO;

        builder = new QueryBuilderV2();
        builder.setMeta(avMeta);
        builder.setSelect("distinct new org.openelis.domain.AnalysisViewVO("+
                          AnalysisViewMeta.getSampleId()+", "+
                          AnalysisViewMeta.getDomain()+", "+
                          AnalysisViewMeta.getAccessionNumber()+", "+
                          AnalysisViewMeta.getReceivedDate()+", "+
                          AnalysisViewMeta.getCollectionDate()+", "+
                          AnalysisViewMeta.getCollectionTime()+", "+
                          AnalysisViewMeta.getEnteredDate()+", "+
                          AnalysisViewMeta.getPrimaryOrganizationName()+", "+
                          AnalysisViewMeta.getTodoDescription()+", "+
                          AnalysisViewMeta.getWorksheetDescription()+", "+
                          AnalysisViewMeta.getPriority()+", "+
                          AnalysisViewMeta.getTestId()+", " +
                          AnalysisViewMeta.getTestName()+", " +
                          AnalysisViewMeta.getMethodName()+", "+
                          AnalysisViewMeta.getTimeTaAverage()+", " +
                          AnalysisViewMeta.getTimeHolding()+", " +
                          AnalysisViewMeta.getTypeOfSampleId()+", "+
                          AnalysisViewMeta.getAnalysisId()+", "+
                          AnalysisViewMeta.getAnalysisStatusId()+", " +
                          AnalysisViewMeta.getSectionId()+", "+
                          AnalysisViewMeta.getSectionName()+", "+
                          AnalysisViewMeta.getAvailableDate()+", "+
                          AnalysisViewMeta.getStartedDate()+", "+
                          AnalysisViewMeta.getCompletedDate()+", "+
                          AnalysisViewMeta.getReleasedDate()+", "+
                          AnalysisViewMeta.getAnalysisResultOverride()+", " +
                          AnalysisViewMeta.getUnitOfMeasureId()+", " +
                          AnalysisViewMeta.getWorksheetFormatId()+") ");
        builder.constructWhere(fields);
        builder.setOrderBy(AnalysisViewMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);

        builder.setQueryParams(query, fields);

        returnList = new ArrayList<WorksheetBuilderVO>();
        list = DataBaseUtil.toArrayList(query.getResultList());
        if (list.isEmpty()) {
            throw new NotFoundException();
        } else {
            for (i = 0; i < list.size(); i++) {
                avVO = (AnalysisViewVO) list.get(i);
                wcVO = new WorksheetBuilderVO();
                wcVO.setSampleId(avVO.getSampleId());
                wcVO.setDomain(avVO.getDomain());
                wcVO.setAccessionNumber(avVO.getAccessionNumber());
                wcVO.setReceivedDate(avVO.getReceivedDate());
                wcVO.setCollectionDate(avVO.getCollectionDate());
                wcVO.setCollectionTime(avVO.getCollectionTime());
                wcVO.setEnteredDate(avVO.getEnteredDate());
                wcVO.setPrimaryOrganizationName(avVO.getPrimaryOrganizationName());
                wcVO.setToDoDescription(avVO.getToDoDescription());
                wcVO.setWorksheetDescription(avVO.getWorksheetDescription());
                wcVO.setPriority(avVO.getPriority());
                wcVO.setTestId(avVO.getTestId());
                wcVO.setTestName(avVO.getTestName());
                wcVO.setMethodName(avVO.getMethodName());
                wcVO.setTimeTaAverage(avVO.getTimeTaAverage());
                wcVO.setTimeHolding(avVO.getTimeHolding());
                wcVO.setTypeOfSampleId(avVO.getTypeOfSampleId());
                wcVO.setAnalysisId(avVO.getAnalysisId());
                wcVO.setAnalysisStatusId(avVO.getAnalysisStatusId());
                wcVO.setSectionId(avVO.getSectionId());
                wcVO.setSectionName(avVO.getSectionName());
                wcVO.setAvailableDate(avVO.getAvailableDate());
                wcVO.setStartedDate(avVO.getStartedDate());
                wcVO.setCompletedDate(avVO.getCompletedDate());
                wcVO.setReleasedDate(avVO.getReleasedDate());
                wcVO.setAnalysisResultOverride(avVO.getAnalysisResultOverride());
                wcVO.setUnitOfMeasureId(avVO.getUnitOfMeasureId());
                wcVO.setWorksheetFormatId(avVO.getWorksheetFormatId());

                //
                // Compute and set the number of days until the analysis is 
                // due to be completed based on when the sample was received,
                // what the tests average turnaround time is, and whether the
                // client requested a priority number of days.
                //
                if (avVO.getPriority() != null)
                    wcVO.setDueDays(TurnaroundUtil.getDueDays(avVO.getReceivedDate(), avVO.getPriority()));
                else
                    wcVO.setDueDays(TurnaroundUtil.getDueDays(avVO.getReceivedDate(), avVO.getTimeTaAverage()));
                
                //
                // Compute and set the expiration date on the analysis based
                // on the collection date and the tests definition of holding
                // hours.
                //
                wcVO.setExpireDate(TurnaroundUtil.getExpireDate(avVO.getCollectionDate(),
                                                              avVO.getCollectionTime(),
                                                              avVO.getTimeHolding()));
                returnList.add(wcVO);
            }
        }

        return (ArrayList<WorksheetBuilderVO>)returnList;
    }

    @TransactionTimeout(600)
    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        int                 i;
        AreaReference       aref;
        ArrayList<IdNameVO> columnNames;
        CellReference       cref[];
        DictionaryViewDO    formatVDO;
        FileInputStream     in;
        HSSFName            name;
        HSSFWorkbook        wb;

        columnNames = new ArrayList<IdNameVO>();
        
        try {
            formatVDO = dictionary.fetchById(formatId);
        } catch (NotFoundException nfE) {
            formatVDO = new DictionaryViewDO();
            formatVDO.setEntry("DefaultTotal");
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet format: "+anyE.getMessage());
        }

        try {
            in = new FileInputStream(getWorksheetTemplateFileName(formatVDO));
        } catch (FileNotFoundException fnfE) {
            throw new Exception("Error loading template file: "+fnfE.getMessage());
        }
        
        try {
            wb = new HSSFWorkbook(in, true);
        } catch (IOException ioE) {
            throw new Exception("Error loading workbook from template file: "+ioE.getMessage());
        }

        for (i = 0; i < wb.getNumberOfNames(); i++) {
            name = wb.getNameAt(i);
            if (name.getRefersToFormula() != null) {
                aref = new AreaReference(name.getRefersToFormula());
                cref = aref.getAllReferencedCells();
                columnNames.add(new IdNameVO(new Integer(Short.valueOf(cref[0].getCol()).intValue()), name.getNameName()));
            }
        }
        
        return columnNames;
    }
    
    private String getWorksheetTemplateFileName(DictionaryViewDO formatVDO) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String                      dirName;
        
        dirName = "";
        try {
            sysVars = systemVariable.fetchByName("worksheet_template_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: "+anyE.getMessage());
        }

        return dirName+"OEWorksheet"+formatVDO.getEntry()+".xls";
    }
}