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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.AnalysisWorksheetVO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentLogDO;
import org.openelis.domain.WorksheetDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.entity.InstrumentLog;
import org.openelis.entity.Worksheet;
import org.openelis.meta.AnalysisViewMeta;
import org.openelis.meta.WorksheetMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class WorksheetBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                 manager;

    @EJB
    DictionaryBean                        dictionary;
    @EJB
    private InstrumentLogBean             instrumentLog;
    @EJB
    SystemVariableBean                    systemVariable;
    @EJB
    private UserCacheBean                 userCache;

    private static final WorksheetMeta    meta   = new WorksheetMeta();
    private static final AnalysisViewMeta avMeta = new AnalysisViewMeta();

    public WorksheetViewDO fetchById(Integer id) throws Exception {
        Query query;
        SystemUserVO user;
        WorksheetViewDO data;

        query = manager.createNamedQuery("Worksheet.FetchById");
        query.setParameter("id", id);
        try {
            data = (WorksheetViewDO)query.getSingleResult();
            if (data.getSystemUserId() != null) {
                user = userCache.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setSystemUser(user.getLoginName());
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetViewDO> fetchByIds(ArrayList<Integer> ids) {
        int i;
        ArrayList<WorksheetViewDO> data;
        Query query;
        SystemUserVO user;
        WorksheetViewDO worksheet;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("Worksheet.FetchByIds");
        data = new ArrayList<WorksheetViewDO>();
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            data.addAll(query.getResultList());
        }
        
        for (i = 0; i < data.size(); i++) {
            worksheet = data.get(i);
            if (worksheet.getSystemUserId() != null) {
                user = userCache.getSystemUser(worksheet.getSystemUserId());
                if (user != null)
                    worksheet.setSystemUser(user.getLoginName());
            }
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetViewDO> fetchByAnalysisId(Integer id) throws Exception {
        int i;
        Query query;
        ArrayList<WorksheetViewDO> data;
        SystemUserVO user;
        WorksheetViewDO worksheet;

        query = manager.createNamedQuery("Worksheet.FetchByAnalysisId");
        query.setParameter("id", id);
        try {
            data = (ArrayList<WorksheetViewDO>)query.getResultList();
            for (i = 0; i < data.size(); i++) {
                worksheet = data.get(i);
                if (worksheet.getSystemUserId() != null) {
                    user = userCache.getSystemUser(worksheet.getSystemUserId());
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

    @SuppressWarnings("unchecked")
    public ArrayList<AnalysisWorksheetVO> fetchByAnalysisIds(ArrayList<Integer> ids) throws Exception {
        int i;
        Query query;
        SystemUserVO u;
        AnalysisWorksheetVO w;
        List<AnalysisWorksheetVO> ws;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("Worksheet.FetchByAnalysisIds");
        ws = new ArrayList<AnalysisWorksheetVO>(); 
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            ws.addAll(query.getResultList());
        }

        for (i = 0; i < ws.size(); i++) {
            w = ws.get(i);
            if (w.getSystemUserId() != null) {
                u = userCache.getSystemUser(w.getSystemUserId());
                if (u != null)
                    w.setSystemUser(u.getLoginName());
            }
        }

        return DataBaseUtil.toArrayList(ws);
    }

    @SuppressWarnings({"unchecked", "static-access"})
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        ArrayList<IdNameVO> list;
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          WorksheetMeta.getId() + ", " +
                          WorksheetMeta.getDescription() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(WorksheetMeta.getId() + " desc");

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = (ArrayList<IdNameVO>)query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<AnalysisViewVO> fetchAnalysesByView(ArrayList<QueryData> fields, 
                                                         int first, int max) throws Exception {
        List list = null, newList;
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(avMeta);
        builder.setSelect("distinct new org.openelis.domain.AnalysisViewVO(" +
                          AnalysisViewMeta.getSampleId() + ", " +
                          AnalysisViewMeta.getDomain() + ", " +
                          AnalysisViewMeta.getAccessionNumber() + ", " +
                          AnalysisViewMeta.getReceivedDate() + ", " +
                          AnalysisViewMeta.getCollectionDate() + ", " +
                          AnalysisViewMeta.getCollectionTime() + ", " +
                          AnalysisViewMeta.getEnteredDate() + ", " +
                          AnalysisViewMeta.getPrimaryOrganizationName() + ", " +
                          AnalysisViewMeta.getTodoDescription() + ", " +
                          AnalysisViewMeta.getWorksheetDescription() + ", " +
                          AnalysisViewMeta.getPriority() + ", " +
                          AnalysisViewMeta.getTestId() + ", " +
                          AnalysisViewMeta.getTestName() + ", " +
                          AnalysisViewMeta.getMethodName() + ", " +
                          AnalysisViewMeta.getTimeTaAverage() + ", " +
                          AnalysisViewMeta.getTimeHolding() + ", " +
                          AnalysisViewMeta.getTypeOfSampleId() + ", " +
                          AnalysisViewMeta.getAnalysisId() + ", " +
                          AnalysisViewMeta.getAnalysisStatusId() + ", " +
                          AnalysisViewMeta.getSectionId() + ", " +
                          AnalysisViewMeta.getSectionName() + ", " +
                          AnalysisViewMeta.getAvailableDate() + ", " +
                          AnalysisViewMeta.getStartedDate() + ", " +
                          AnalysisViewMeta.getCompletedDate() + ", " +
                          AnalysisViewMeta.getReleasedDate() + ", " +
                          AnalysisViewMeta.getAnalysisResultOverride() + ", " +
                          AnalysisViewMeta.getUnitOfMeasureId() + ", " +
                          AnalysisViewMeta.getWorksheetFormatId() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(AnalysisViewMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);

        builder.setQueryParams(query, fields);

        list = DataBaseUtil.toArrayList(query.getResultList());
        if (list.isEmpty())
            throw new NotFoundException();
        
        /*
         * exclude records with patient data if the user does not have permission
         * to view them.
         */
        try {
            userCache.applyPermission("patient", ModuleFlags.SELECT);
        } catch (Exception anyE) {
            newList = new ArrayList<AnalysisViewVO>();
            for (AnalysisViewVO aVVO : (ArrayList<AnalysisViewVO>) list) {
                if (Constants.domain().CLINICAL.equals(aVVO.getDomain()) ||
                    Constants.domain().NEONATAL.equals(aVVO.getDomain()))
                    continue;
                newList.add(aVVO);
            }
            if (newList.isEmpty())
                throw new NotFoundException();
            list = newList;
        }

        return (ArrayList<AnalysisViewVO>)list;
    }

    public WorksheetDO add(WorksheetDO data) throws Exception {
        InstrumentLog ilEntity;
        Worksheet entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Worksheet();
        entity.setCreatedDate(data.getCreatedDate());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setStatusId(data.getStatusId());
        entity.setFormatId(data.getFormatId());
        entity.setSubsetCapacity(data.getSubsetCapacity());
        entity.setRelatedWorksheetId(data.getRelatedWorksheetId());
        entity.setInstrumentId(data.getInstrumentId());
        entity.setDescription(data.getDescription());

        manager.persist(entity);
        data.setId(entity.getId());

        if (entity.getInstrumentId() != null) {
            ilEntity = new InstrumentLog();
            ilEntity.setInstrumentId(entity.getInstrumentId());
            ilEntity.setTypeId(Constants.dictionary().INSTRUMENT_LOG_PENDING);
            ilEntity.setWorksheetId(entity.getId());
            ilEntity.setEventBegin(entity.getCreatedDate());
            manager.persist(ilEntity);
        }

        return data;
    }

    public WorksheetDO update(WorksheetDO data) throws Exception {
        InstrumentLog ilEntity;
        InstrumentLogDO ilDO;
        Worksheet entity;

        if (!data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Worksheet.class, data.getId());

        if (!DataBaseUtil.isDifferent(entity.getInstrumentId(), data.getInstrumentId())) {
            if (data.getInstrumentId() != null) {
                try {
                    ilDO = instrumentLog.fetchByInstrumentIdWorksheetId(entity.getInstrumentId(),
                                                                        entity.getId());
                    ilEntity = manager.find(InstrumentLog.class, ilDO.getId());
                } catch (NotFoundException nfE) {
                    ilEntity = new InstrumentLog();
                    ilEntity.setInstrumentId(data.getInstrumentId());
                    ilEntity.setTypeId(Constants.dictionary().INSTRUMENT_LOG_PENDING);
                    ilEntity.setWorksheetId(data.getId());
                    ilEntity.setEventBegin(data.getCreatedDate());
                    manager.persist(ilEntity);
                }
                if (ilEntity.getTypeId().equals(Constants.dictionary().INSTRUMENT_LOG_PENDING)) {
                    if (data.getStatusId().equals(Constants.dictionary().WORKSHEET_COMPLETE) ||
                        data.getStatusId().equals(Constants.dictionary().WORKSHEET_FAILED)) {
                        ilEntity.setTypeId(Constants.dictionary().INSTRUMENT_LOG_COMPLETED);
                        ilEntity.setEventEnd(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                    } else if (data.getStatusId().equals(Constants.dictionary().WORKSHEET_VOID)) {
                        manager.remove(ilEntity);
                    }
                }
            }
        } else {
            if (entity.getInstrumentId() == null) {
                if (!data.getStatusId().equals(Constants.dictionary().WORKSHEET_VOID)) {
                    ilEntity = new InstrumentLog();
                    ilEntity.setInstrumentId(data.getInstrumentId());
                    ilEntity.setWorksheetId(data.getId());
                    ilEntity.setEventBegin(data.getCreatedDate());
                    if (data.getStatusId().equals(Constants.dictionary().WORKSHEET_COMPLETE) ||
                        data.getStatusId().equals(Constants.dictionary().WORKSHEET_FAILED)) {
                        ilEntity.setTypeId(Constants.dictionary().INSTRUMENT_LOG_COMPLETED);
                        ilEntity.setEventEnd(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                    } else {
                        ilEntity.setTypeId(Constants.dictionary().INSTRUMENT_LOG_PENDING);
                    }
                    manager.persist(ilEntity);
                }
            } else {
                try {
                    ilDO = instrumentLog.fetchByInstrumentIdWorksheetId(entity.getInstrumentId(),
                                                                        entity.getId());
                    ilEntity = manager.find(InstrumentLog.class, ilDO.getId());
                    if (data.getInstrumentId() != null) {
                        ilEntity.setInstrumentId(data.getInstrumentId());
                        if (ilEntity.getTypeId()
                                    .equals(Constants.dictionary().INSTRUMENT_LOG_PENDING)) {
                            if (data.getStatusId()
                                    .equals(Constants.dictionary().WORKSHEET_COMPLETE) ||
                                data.getStatusId().equals(Constants.dictionary().WORKSHEET_FAILED)) {
                                ilEntity.setTypeId(Constants.dictionary().INSTRUMENT_LOG_COMPLETED);
                                ilEntity.setEventEnd(Datetime.getInstance(Datetime.YEAR,
                                                                          Datetime.MINUTE));
                            } else if (data.getStatusId()
                                           .equals(Constants.dictionary().WORKSHEET_VOID)) {
                                manager.remove(ilEntity);
                            }
                        }
                    } else {
                        manager.remove(ilEntity);
                    }
                } catch (NotFoundException nfE) {
                    if (data.getInstrumentId() != null &&
                        !data.getStatusId().equals(Constants.dictionary().WORKSHEET_VOID)) {
                        ilEntity = new InstrumentLog();
                        ilEntity.setInstrumentId(data.getInstrumentId());
                        ilEntity.setWorksheetId(data.getId());
                        ilEntity.setEventBegin(data.getCreatedDate());
                        if (data.getStatusId().equals(Constants.dictionary().WORKSHEET_COMPLETE) ||
                            data.getStatusId().equals(Constants.dictionary().WORKSHEET_FAILED)) {
                            ilEntity.setTypeId(Constants.dictionary().INSTRUMENT_LOG_COMPLETED);
                            ilEntity.setEventEnd(Datetime.getInstance(Datetime.YEAR,
                                                                      Datetime.MINUTE));
                        } else {
                            ilEntity.setTypeId(Constants.dictionary().INSTRUMENT_LOG_PENDING);
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
        entity.setDescription(data.getDescription());

        return data;
    }

    public void validate(WorksheetDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getCreatedDate()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             WorksheetMeta.getCreatedDate()));

        if (DataBaseUtil.isEmpty(data.getSystemUserId()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             WorksheetMeta.getSystemUserId()));

        if (DataBaseUtil.isEmpty(data.getStatusId()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             WorksheetMeta.getStatusId()));

        if (DataBaseUtil.isEmpty(data.getFormatId()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             WorksheetMeta.getFormatId()));

        if (list.size() > 0)
            throw list;
    }
}