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
package org.openelis.manager;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.QcViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalyteParameterLocal;
import org.openelis.local.QcLocal;
import org.openelis.local.TestLocal;
import org.openelis.utils.EJBFactory;

public class AnalyteParameterManagerProxy {

    public AnalyteParameterManager fetchActiveByReferenceIdReferenceTableId(Integer referenceId, Integer referenceTableId) throws Exception {
        AnalyteParameterManager m;
        ArrayList<AnalyteParameterViewDO> list;
        TestViewDO t;
        QcViewDO q;
                
        list = EJBFactory.getAnalyteParameter().fetchActiveByReferenceIdReferenceTableId(referenceId, referenceTableId);    
        m = AnalyteParameterManager.getInstance();
        m.setReferenceId(referenceId);
        m.setReferenceTableId(referenceTableId);
        
        for (AnalyteParameterViewDO data: list) 
            m.addParamater(data);
        
        switch (referenceTableId) {
            case ReferenceTable.TEST:               
                t = EJBFactory.getTest().fetchById(referenceId);
                m.setReferenceName(t.getName()+" , "+t.getMethodName());
                break;
            case ReferenceTable.QC:
                q = EJBFactory.getQc().fetchById(referenceId);
                m.setReferenceName(q.getName());
                break;
            case ReferenceTable.PROVIDER:
                break;
        }
                        
        return m;
    }
    
    public AnalyteParameterManager add(AnalyteParameterManager man) throws Exception {
        int i;
        AnalyteParameterLocal pl;
        AnalyteParameterViewDO data;
        ArrayList<AnalyteParameterViewDO> params;
        ArrayList<Integer> idList;

        pl = EJBFactory.getAnalyteParameter();
        params = man.getParameters();
        idList = new ArrayList<Integer>(); 
        for (i = 0; i < params.size(); i++) {
            data = params.get(i);
            if (data.getId() == null) {
                if ("Y".equals(data.getIsActive()) && canAddParameter(data)) {
                    /*
                     * if this is the last active DO in the list then it could
                     * mean that the previous records for this analyte were not
                     * fetched by the screen; thus we need to find out if it has
                     * any previous records in the database so that the previous
                     * record can be updated if need be
                     */
                    updatePreviousActive(data, pl, idList);                        
                    
                }
                pl.add(data);
            } 
        }
        
        for (Integer id:idList)
            pl.abortUpdate(id);        

        return man;
    }

    public AnalyteParameterManager update(AnalyteParameterManager man) throws Exception {
        AnalyteParameterLocal pl;
        AnalyteParameterViewDO data, nextData, prevData;        
        ArrayList<AnalyteParameterViewDO> params;
        ArrayList<Integer> idList;
        Datetime ab, ae;

        pl = EJBFactory.getAnalyteParameter();
        prevData = null;
        params = man.getParameters();
        idList = new ArrayList<Integer>();
        for (int i = 0; i < params.size(); i++ ) {
            data = params.get(i);
            if (data.getId() == null) {
                if ("Y".equals(data.getIsActive()) && canAddParameter(data)) {
                    /*
                     * It could have happened that when the records for this reference 
                     * id were fetched for update, a few analytes that hadn't been 
                     * added as parameters were shown as new inactive ones and
                     * since they didn't have ids they were not locked. It is possible
                     * that those were updated by some other user before the current
                     * user got the chance to do so. Thus we need to find out if
                     * this DO has any previous records in the database so that 
                     * the previous record can be updated if need be.
                     */
                    updatePreviousActive(data, pl, idList);                        
                }
                pl.add(data);
            } else {
                if (prevData != null && "Y".equals(prevData.getIsActive()) && 
                                prevData.getAnalyteId().equals(data.getAnalyteId())) {
                    /*
                     * if the previous DO was active and had the same analyte as
                     * this one then it means that the previous DO had the data
                     * for the latest active record for this analyte and we need 
                     * to update this record's active end date and active flag 
                     * if need be    
                     */
                    ab = prevData.getActiveBegin();
                    ae = data.getActiveEnd();
                    if (ae.compareTo(ab) >= 0) 
                        ae.getDate().setTime(ab.getDate().getTime() - 60000);                        
                    data.setIsActive("N");
                } else if ("Y".equals(data.getIsActive())) {
                    if (i == params.size() - 1) {
                        /*
                         * if this is the last active DO in the list then it could
                         * mean that the previous records for this analyte were 
                         * not fetched by the screen; thus we need to find out
                         * if it has any previous records in the database so that
                         * the previous record can be updated if need be   
                         */
                        if (data.isChanged())
                            updateLatestInactive(data, pl);                        
                    } else {
                        nextData = params.get(i+1);
                        if (!(nextData.getAnalyteId().equals(data.getAnalyteId())) && data.isChanged())
                            updateLatestInactive(data, pl);
                    }
                }
                pl.update(data);
            }
            prevData = data;
        }
        
        for (Integer id:idList)
            pl.abortUpdate(id);

        return man;
    }

    public AnalyteParameterManager fetchForUpdate(AnalyteParameterManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public AnalyteParameterManager abortUpdate(AnalyteParameterManager man) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public void validate(AnalyteParameterManager man) throws Exception {
        int numActive;
        boolean validateDates;
        ArrayList<AnalyteParameterViewDO> params;        
        AnalyteParameterViewDO prev, data, next;
        ValidationErrorsList errors;  
        AnalyteParameterLocal pl;        
        
        params = man.getParameters(); 
        errors = new ValidationErrorsList();
        if (params == null || params.size() == 0) {
            errors.add(new FormErrorException("recordHasNoAnalytesException"));
            throw errors;
        }
        
        pl = EJBFactory.getAnalyteParameter();
        prev = null;
        numActive = 0;
        
        for (int i = 0; i < params.size(); i++) {
            validateDates = true;
            data = params.get(i);
            if ("Y".equals(data.getIsActive())) 
                numActive++;
            
            try {
                if (data.getId() != null || canAddParameter(data)) 
                    pl.validate(data);                
                else 
                    validateDates = false;
            } catch (Exception e) {
                DataBaseUtil.mergeException(errors, e);
                validateDates = false;
            }
            
            if (!validateDates) {
                prev = data;
                continue;
            }
                            
            if ("Y".equals(data.getIsActive())) { 
                if (prev != null && "Y".equals(prev.getIsActive()) && 
                                data.getAnalyteId().equals(prev.getAnalyteId())) {
                    /*
                     * It can happen on the screen that after an analyte at the 
                     * top level has been marked active its previous analytes are 
                     * fetched and added to the tree and one of those is an active 
                     * analyte. In that case if the data is attempted to be committed,
                     * the previously active analyte will still be sent in the manager
                     * as an active one because the screen didn't get a chance to
                     * set it to inactive, but this code needs to treat it as an
                     * inactive analyte as it will be made inactive in add() or
                     * update() if validation succeeds.                           
                     */
                    validateDates(prev, data, errors);
                    prev = data;
                    continue;
                }
                if (i == params.size()-1) {
                    /*
                     * if this is the last active DO in the list then it could mean
                     * that the previous records for this analyte were not fetched
                     * by the screen; thus we need to find out if it has any previous
                     * records in the database so that we can validate its dates
                     * against those records  
                     */
                    if (data.isChanged())
                        fetchPreviousAndValidate(data, pl, errors);
                } else {
                    next = params.get(i+1);
                    if (!next.getAnalyteId().equals(data.getAnalyteId())) {
                        /*
                         * if the DO next to this one in the list is has a different
                         * analyte, then this means that, this DO is the only one
                         * in the list with its analyte then  we need to find out
                         * if this analyte has any previous records, for the reason
                         * stated in the comments above 
                         */
                        if (data.isChanged())
                            fetchPreviousAndValidate(data, pl, errors);
                    } 
                }
            } else {
                if (data.getAnalyteId().equals(prev.getAnalyteId()))
                    validateDates(prev, data, errors);
            }
            
            prev = data;
        }        
        
        if (numActive == 0)
            errors.add(new FormErrorException("recordHasNoActiveAnalytesException"));
        
        if (errors.size() > 0)
            throw errors;
    }
    
    private void fetchPreviousAndValidate(AnalyteParameterViewDO data, AnalyteParameterLocal pl,
                                          ValidationErrorsList errors) throws Exception {
        ArrayList<AnalyteParameterViewDO> results;        
        try {
            results = pl.fetchByAnalyteIdReferenceIdReferenceTableId(data.getAnalyteId(), data.getReferenceId(),
                                                                 data.getReferenceTableId());
            validateList(data, results, errors);
        } catch (NotFoundException ignE) {
            // do nothing
        }
    }
    
    private void validateList(AnalyteParameterViewDO data, ArrayList<AnalyteParameterViewDO> list,
                                     ValidationErrorsList errors) {
        AnalyteParameterViewDO prev, temp;
        
        prev = null;
        for (int i = 0; i < list.size(); i++) {
            temp = list.get(i);
            if (i == 0 && !temp.getId().equals(data.getId()))          
                validateDates(data, temp, errors);
            else if (prev != null)      
                validateDates(prev, temp, errors);            
            prev = temp;
        }
    }
    
    private void validateDates(AnalyteParameterViewDO data, AnalyteParameterViewDO prev, 
                               ValidationErrorsList errors) {
        long pbt, cbt, cet;
        
        if (data.getActiveBegin() == null || prev.getActiveBegin() == null || prev.getActiveEnd() == null)
            return;           
        
        pbt = data.getActiveBegin().getDate().getTime();
        cbt = prev.getActiveBegin().getDate().getTime();
        cet = prev.getActiveEnd().getDate().getTime();
        
        if ((pbt - cbt) <= 60000) 
            errors.add(new FormErrorException("beginDateInvalidWithParamException", prev.getAnalyteName()));
        /*
         * since the code in update() sets the end date of the previously latest
         * record, which could be "prev", to a minute before the begin date of 
         * the currently latest one, which could be "data", we don't check to see
         * if data's begin date is after prev's end date if both data and prev 
         * are marked as active; because this means that prev was not fetched to 
         * be shown on the screen and so it didn't get deactivated there but will
         * be in update()   
         */
        if (!("Y".equals(data.getIsActive()) && "Y".equals(prev.getIsActive())) && (pbt - cet) <= 0)
            errors.add(new FormErrorException("beginDateAfterPreviousEndDateException", prev.getAnalyteName()));
    }  
        
    private boolean canAddParameter(AnalyteParameterViewDO data) {
        return (!DataBaseUtil.isEmpty(data.getActiveBegin()) && !DataBaseUtil.isEmpty(data.getActiveEnd()) &&
                        !DataBaseUtil.isEmpty(data.getP1()));
    }
    
    private void updatePreviousActive(AnalyteParameterViewDO data, AnalyteParameterLocal pl,
                                      ArrayList<Integer> idList) throws Exception {
        Integer id;
        AnalyteParameterViewDO result;
        Datetime ab, ae;
        try {
            result = pl.fetchActiveByAnalyteIdReferenceIdReferenceTableId(data.getAnalyteId(),
                                                                          data.getReferenceId(),
                                                                          data.getReferenceTableId());
            id = result.getId();
            pl.fetchForUpdate(id);
            idList.add(id);
            ab = data.getActiveBegin();
            ae = result.getActiveEnd();
            if (ae.compareTo(ab) >= 0) 
                ae.getDate().setTime(ab.getDate().getTime() - 60000);                
            result.setIsActive("N");
            pl.update(result);            
        } catch (NotFoundException ignE) {
            // do nothing
        }
    }
    
    private void updateLatestInactive(AnalyteParameterViewDO data, AnalyteParameterLocal pl) throws Exception {
        AnalyteParameterViewDO result;
        ArrayList<AnalyteParameterViewDO> results;
        Datetime ab, ae;
        try {
            results = pl.fetchByAnalyteIdReferenceIdReferenceTableId(data.getAnalyteId(),
                                                                          data.getReferenceId(),
                                                                          data.getReferenceTableId());                
            result = results.get(0);
            if (data.getId().equals(result.getId()) && results.size() > 1)
                result = results.get(1);
                
            ab = data.getActiveBegin();
            ae = result.getActiveEnd();
            if (ae.compareTo(ab) >= 0) {
                ae.getDate().setTime(ab.getDate().getTime() - 60000);                
                result.setIsActive("N");
                pl.update(result);
            }
        } catch (NotFoundException ignE) {
            // do nothing
        }
    }         
}
