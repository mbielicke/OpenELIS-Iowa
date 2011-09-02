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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InstrumentLogDO;
import org.openelis.domain.WorksheetCacheVO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.entity.InstrumentLog;
import org.openelis.entity.Worksheet;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.InstrumentLogLocal;
import org.openelis.local.WorksheetLocal;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetManagerProxy;
import org.openelis.meta.WorksheetCompletionMeta;
import org.openelis.remote.WorksheetRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-select")
public class WorksheetBean implements WorksheetRemote, WorksheetLocal {

	@PersistenceContext(unitName = "openelis")
    private EntityManager manager;
	
	@EJB
	private AnalysisLocal analysis;
	
    @EJB
    private DictionaryLocal dictionary;
    
    @EJB
    private InstrumentLogLocal instrumentLog;
    
    private static int logPending, logCompleted, statusComplete;

    private static final WorksheetCompletionMeta meta = new WorksheetCompletionMeta();
    
    @PostConstruct
    public void init() {
        DictionaryDO data;

        if (logPending == 0) {
            try {
                data = dictionary.fetchBySystemName("instrument_log_pending");
                logPending = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                logPending = 0;
            }
        }

        if (logCompleted == 0) {
            try {
                data = dictionary.fetchBySystemName("instrument_log_completed");
                logCompleted = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                logCompleted = 0;
            }
        }

        if (statusComplete == 0) {
            try {
                data = dictionary.fetchBySystemName("worksheet_complete");
                statusComplete = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                statusComplete = 0;
            }
        }
    }

	public WorksheetViewDO fetchById(Integer id) throws Exception {		
		Query       query;
		WorksheetViewDO data;
		
		query = manager.createNamedQuery("Worksheet.FetchById");
		query.setParameter("id", id);
		try {
		    data = (WorksheetViewDO) query.getSingleResult();
		} catch (NoResultException e) {
		    throw new NotFoundException();
		} catch (Exception e) {
		    throw new DatabaseException(e);
		}
        return data;
	}

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetViewDO> fetchByAnalysisId(Integer id) throws Exception {
        int                        i;
        Query                      query;
        ArrayList<WorksheetViewDO> data;
        SystemUserVO               user;
        WorksheetViewDO            worksheet;

        query = manager.createNamedQuery("Worksheet.FetchByAnalysisId");
        query.setParameter("id", id);
        try {
            data = (ArrayList<WorksheetViewDO>) query.getResultList();
            for (i = 0; i < data.size(); i++) {
                worksheet = data.get(i);
                if (worksheet.getSystemUserId() != null) {
                    user = EJBFactory.getUserCache().getSystemUser(worksheet.getSystemUserId());
                    if (user != null)
                        worksheet.setSystemUser(user.getLoginName());
                }
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings({"unchecked", "static-access"})
    public ArrayList<WorksheetViewDO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        int                             i, j, k;
        Integer                         analysisId, testId, methodId;
        Query                           query;
        QueryBuilderV2                  builder;
        ArrayList<AnalysisViewDO>       waList;
        ArrayList<WorksheetViewDO>      list;
        HashMap<String, AnalysisViewDO> analysisMap;
        AnalysisViewDO                  aVDO;
        SystemUserVO                    user;
        WorksheetAnalysisManager        waManager;
        WorksheetItemManager            wiManager;
        WorksheetManager                wManager;
        WorksheetManagerProxy           wManagerProxy;
        WorksheetViewDO                 worksheet;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.WorksheetViewDO("+WorksheetCompletionMeta.getId()+", "+
                          WorksheetCompletionMeta.getCreatedDate()+", "+
                          WorksheetCompletionMeta.getSystemUserId()+", "+
                          WorksheetCompletionMeta.getStatusId()+", "+
                          WorksheetCompletionMeta.getFormatId()+", "+
                          WorksheetCompletionMeta.getSubsetCapacity()+", "+
                          WorksheetCompletionMeta.getRelatedWorksheetId()+", "+
                          WorksheetCompletionMeta.getInstrumentId()+", "+
                          WorksheetCompletionMeta.getInstrumentName()+") ");
        builder.constructWhere(fields);
        builder.setOrderBy(WorksheetCompletionMeta.getId());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = (ArrayList<WorksheetViewDO>)query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        
        wManagerProxy = new WorksheetManagerProxy();
        for (i = 0; i < list.size(); i++) {
            worksheet = list.get(i);
            
            if (worksheet.getSystemUserId() != null) {
                user = EJBFactory.getUserCache().getSystemUser(worksheet.getSystemUserId());
                if (user != null)
                    worksheet.setSystemUser(user.getLoginName());
            }
            
            waList = new ArrayList<AnalysisViewDO>();
            analysisMap = new HashMap<String, AnalysisViewDO>();
            wManager = wManagerProxy.fetchById(worksheet.getId());
            wiManager = wManager.getItems();
            for (j = 0; j < wiManager.count(); j++) {
                waManager = wiManager.getWorksheetAnalysisAt(j);
                for (k = 0; k < waManager.count(); k++) {
                    analysisId = waManager.getWorksheetAnalysisAt(k).getAnalysisId();
                    if (analysisId != null) {
                        aVDO = analysis.fetchById(analysisId);
                        testId = aVDO.getTestId();
                        methodId = aVDO.getMethodId();
                        if (!analysisMap.containsKey(testId+","+methodId)) {
                            analysisMap.put(testId+","+methodId, aVDO);
                            waList.add(aVDO);
                        }
                    }
                }
            }
            
            worksheet.setTestList(waList);
        }
        
        list = DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return list;
    }

	public WorksheetViewDO add(WorksheetViewDO data){
	    InstrumentLog ilEntity;
        Worksheet     entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Worksheet();
        entity.setCreatedDate(data.getCreatedDate());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setStatusId(data.getStatusId());
        entity.setFormatId(data.getFormatId());
        entity.setSubsetCapacity(data.getSubsetCapacity());
        entity.setRelatedWorksheetId(data.getRelatedWorksheetId());
        entity.setInstrumentId(data.getInstrumentId());

        manager.persist(entity);
        data.setId(entity.getId());
        
        if (entity.getInstrumentId() != null) {
            ilEntity = new InstrumentLog();
            ilEntity.setInstrumentId(entity.getInstrumentId());
            ilEntity.setTypeId(logPending);
            ilEntity.setWorksheetId(entity.getId());
            ilEntity.setEventBegin(entity.getCreatedDate());
            manager.persist(ilEntity);
        }

        return data;
    }
    
    public WorksheetViewDO update(WorksheetViewDO data) throws Exception {
        InstrumentLog   ilEntity;
        InstrumentLogDO ilDO;
        Worksheet       entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Worksheet.class, data.getId());
        
        if (!DataBaseUtil.isDifferent(entity.getInstrumentId(), data.getInstrumentId()) &&
            data.getInstrumentId() != null) {
            try {
                ilDO = instrumentLog.fetchByInstrumentIdWorksheetId(entity.getInstrumentId(),
                                                                    entity.getId());
                ilEntity = manager.find(InstrumentLog.class, ilDO.getId());
                ilEntity.setInstrumentId(data.getInstrumentId());
            } catch (NotFoundException nfE) {
                ilEntity = new InstrumentLog();
                ilEntity.setInstrumentId(data.getInstrumentId());
                ilEntity.setTypeId(logPending);
                ilEntity.setWorksheetId(data.getId());
                ilEntity.setEventBegin(data.getCreatedDate());
            }
            if (data.getStatusId().equals(statusComplete) && ilEntity.getTypeId().equals(logPending)) {
                ilEntity.setTypeId(logCompleted);
                ilEntity.setEventEnd(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
            }
        } else {
            if (entity.getInstrumentId() == null) {
                ilEntity = new InstrumentLog();
                ilEntity.setInstrumentId(data.getInstrumentId());
                ilEntity.setWorksheetId(data.getId());
                ilEntity.setEventBegin(data.getCreatedDate());
                if (data.getStatusId().equals(statusComplete)) {
                    ilEntity.setTypeId(logCompleted);
                    ilEntity.setEventEnd(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                } else {
                    ilEntity.setTypeId(logPending);
                }
                manager.persist(ilEntity);
            } else {
                try {
                    ilDO = instrumentLog.fetchByInstrumentIdWorksheetId(entity.getInstrumentId(),
                                                                        entity.getId());
                    ilEntity = manager.find(InstrumentLog.class, ilDO.getId());
                    if (data.getInstrumentId() != null) {
                        ilEntity.setInstrumentId(data.getInstrumentId());
                        if (data.getStatusId().equals(statusComplete) &&
                            ilEntity.getTypeId().equals(logPending)) {
                            ilEntity.setTypeId(logCompleted);
                            ilEntity.setEventEnd(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                        }
                    } else {
                        manager.remove(ilEntity);
                    }
                } catch (NotFoundException nfE) {
                    if (data.getInstrumentId() != null) {
                        ilEntity = new InstrumentLog();
                        ilEntity.setInstrumentId(data.getInstrumentId());
                        ilEntity.setWorksheetId(data.getId());
                        ilEntity.setEventBegin(data.getCreatedDate());
                        if (data.getStatusId().equals(statusComplete)) {
                            ilEntity.setTypeId(logCompleted);
                            ilEntity.setEventEnd(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                        } else {
                            ilEntity.setTypeId(logPending);
                        }
                        manager.persist(ilEntity);
                    }
                }
            }
        }
        
        entity.setCreatedDate(data.getCreatedDate());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setStatusId(data.getStatusId());
        entity.setFormatId(data.getFormatId());
        entity.setSubsetCapacity(data.getSubsetCapacity());
        entity.setRelatedWorksheetId(data.getRelatedWorksheetId());
        entity.setInstrumentId(data.getInstrumentId());

        return data;
    }

    public void validate(WorksheetViewDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getCreatedDate()))
            list.add(new FieldErrorException("fieldRequiredException", WorksheetCompletionMeta.getCreatedDate()));

        if (DataBaseUtil.isEmpty(data.getSystemUserId()))
            list.add(new FieldErrorException("fieldRequiredException", WorksheetCompletionMeta.getSystemUserId()));

        if (DataBaseUtil.isEmpty(data.getStatusId()))
            list.add(new FieldErrorException("fieldRequiredException", WorksheetCompletionMeta.getStatusId()));

        if (DataBaseUtil.isEmpty(data.getFormatId()))
            list.add(new FieldErrorException("fieldRequiredException", WorksheetCompletionMeta.getFormatId()));

        if (list.size() > 0)
            throw list;
    }
}
