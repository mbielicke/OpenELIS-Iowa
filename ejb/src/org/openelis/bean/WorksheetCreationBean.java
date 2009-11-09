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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.metamap.WorksheetCreationMetaMap;
import org.openelis.remote.WorksheetCreationRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.GetPage;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class WorksheetCreationBean implements WorksheetCreationRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    private static final WorksheetCreationMetaMap WorksheetCreationMetaMap = new WorksheetCreationMetaMap();

    public WorksheetCreationBean() {
    }

    public ArrayList<WorksheetCreationVO> query(ArrayList<QueryData> fields, 
                                                int first, int max) throws Exception {
        int                 i;
        List                list = null;
        Query               query;
        QueryBuilderV2      builder;
        WorksheetCreationVO vo;

        builder = new QueryBuilderV2();
        builder.setMeta(WorksheetCreationMetaMap);
        builder.setSelect("distinct new org.openelis.domain.WorksheetCreationVO("+
                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.getId()+", "+
                          WorksheetCreationMetaMap.SAMPLE.getDomain()+", "+
                          WorksheetCreationMetaMap.SAMPLE.getAccessionNumber()+", "+
                          WorksheetCreationMetaMap.SAMPLE.getCollectionDate()+", "+
                          WorksheetCreationMetaMap.SAMPLE.getReceivedDate()+", "+
                          WorksheetCreationMetaMap.SAMPLE_ENVIRONMENTAL.getDescription()+", "+
                          WorksheetCreationMetaMap.SAMPLE_ENVIRONMENTAL.getPriority()+", "+
//                          WorksheetCreationMetaMap.SAMPLE_HUMAN.PATIENT.getLastName()+", "+
//                          WorksheetCreationMetaMap.SAMPLE_HUMAN.PATIENT.getFirstName()+", "+
//                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_PROJECT.PROJECT.getName()+", " +
                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.getId()+", " +
                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.getName()+", " +
                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.METHOD.getName()+", "+
                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.getTimeHolding()+", " +
                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.getTimeTaAverage()+", " +
                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.SECTION.getName()+", "+
                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.getPreAnalysisId()+", "+
                          WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.getStatusId()+") ");
        builder.constructWhere(fields);
        
//        builder.addWhere(WorksheetCreationMetaMap.SAMPLE.SAMPLE_PROJECT.getIsPermanent() + " = 'Y'");
        
        builder.setOrderBy(WorksheetCreationMetaMap.SAMPLE.getAccessionNumber());

        try {
            query = manager.createQuery(builder.getEJBQL());
            query.setMaxResults(first + max);

            builder.setQueryParams(query, fields);

            list = DataBaseUtil.toArrayList(query.getResultList());
            if (list.isEmpty()) {
                throw new NotFoundException();
            } else {
                for (i = 0; i < list.size(); i++) {
                    vo = (WorksheetCreationVO) list.get(i);
                    //
                    // Compute and set the number of days until the analysis is 
                    // due to be completed based on when the sample was received,
                    // what the tests average turnaround time is, and whether the
                    // client requested a priority number of days.
                    //
                    if (vo.getPriority() != null)
                        vo.setDueDays(computeDueDays(vo.getReceivedDate(), vo.getPriority()));
                    else
                        vo.setDueDays(computeDueDays(vo.getReceivedDate(), vo.getTimeTaAverage()));
                        
                    //
                    // Compute and set the expiration date on the analysis based
                    // on the collection date and the tests definition of holding
                    // hours.
                    //
                    vo.setExpireDate(computeExpireDate(vo.getCollectionDate(), vo.getTimeHolding()));
                }
            }
        } catch (Exception anyE) {
            anyE.printStackTrace();
        }

        return (ArrayList<WorksheetCreationVO>)list;
    }
    
    /*
     * Compute the number of days before the analysis is expected to be finshed
     */
    private long computeDueDays(Datetime received, int expectedDays) {
        long     due;
        Datetime now, expectedDate;
        
        now = Datetime.getInstance();
        
        expectedDate = received.add(expectedDays);
        
        due = expectedDate.getDate().getTime() - now.getDate().getTime();
        
        // convert from milliseconds to days
        due = due / 1000 / 60 / 60 / 24;
        
        return due;
    }
    
    /*
     * Computer the number of days before the sample is no longer viable for analysis
     */
    private Datetime computeExpireDate(Datetime collection, int holdingHours) {
        if (collection != null)
            return collection.add(holdingHours / 24);
        return null;
    }
}