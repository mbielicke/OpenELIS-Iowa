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
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.entity.Result;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultValidator;
import org.openelis.utilcommon.ResultValidator.RoundingMethod;
import org.openelis.utilcommon.ResultValidator.Type;

@Stateless
@SecurityDomain("openelis")
public class ResultBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                 manager;

    @EJB
    private DictionaryCacheBean           dictionaryCache;

    private static HashMap<Integer, Type> types;

    @PostConstruct
    public void init() {
        if (types == null) {
            types = new HashMap<Integer, Type>();

            types.put(Constants.dictionary().TEST_RES_TYPE_DICTIONARY, Type.DICTIONARY);
            types.put(Constants.dictionary().TEST_RES_TYPE_NUMERIC, Type.NUMERIC);
            types.put(Constants.dictionary().TEST_RES_TYPE_TITER, Type.TITER);
            types.put(Constants.dictionary().TEST_RES_TYPE_DATE, Type.DATE);
            types.put(Constants.dictionary().TEST_RES_TYPE_DATE_TIME, Type.DATE_TIME);
            types.put(Constants.dictionary().TEST_RES_TYPE_TIME, Type.TIME);
            types.put(Constants.dictionary().TEST_RES_TYPE_ALPHA_LOWER, Type.ALPHA_LOWER);
            types.put(Constants.dictionary().TEST_RES_TYPE_ALPHA_UPPER, Type.ALPHA_UPPER);
            types.put(Constants.dictionary().TEST_RES_TYPE_ALPHA_MIXED, Type.ALPHA_MIXED);
            types.put(Constants.dictionary().TEST_RES_TYPE_DEFAULT, Type.DEFAULT);
        }
    }

    public void fetchByAnalysisIdForDisplay(Integer analysisId,
                                            ArrayList<ArrayList<ResultViewDO>> results) throws Exception {
        int i;
        Integer j, rg;
        ResultViewDO data;
        ArrayList<ResultViewDO> ar;
        List<ResultViewDO> list;

        list = null;
        // get results by analysis id
        Query query = manager.createNamedQuery("Result.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        list = query.getResultList();

        // build the grid
        j = -1;
        ar = null;
        results.clear();

        if (list == null || list.size() == 0)
            throw new NotFoundException();

        for (i = 0; i < list.size(); i++ ) {
            data = list.get(i);

            rg = data.getRowGroup();

            if ( !DataBaseUtil.isSame(j, rg)) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(data);
                results.add(ar);
                if (rg != null)
                    j = rg;
                continue;
            }
            if ("N".equals(data.getIsColumn())) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(data);
                results.add(ar);
                continue;
            }

            ar.add(data);
        }
    }

    public ArrayList<ResultViewDO> fetchByAnalysisIds(ArrayList<Integer> analysisIds) {
        Query query;
        List<ResultViewDO> rs;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("Result.FetchByAnalysisIds");
        rs = new ArrayList<ResultViewDO>();        
        r = DataBaseUtil.createSubsetRange(analysisIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", analysisIds.subList(r.get(i), r.get(i + 1)));
            rs.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(rs);
    }

    /**
     * Fetches results for analysis that are reportable and do not have sample
     * or analysis level QA event override.
     */
    public ArrayList<ArrayList<ResultViewDO>> fetchReportableByAnalysisId(Integer sampleId,
                                                                          Integer analysisId) throws Exception {
        int i;
        ResultViewDO data;
        ArrayList<ResultViewDO> ar;
        List<ResultViewDO> list;
        ArrayList<ArrayList<ResultViewDO>> results;
        Query query;

        // get results by analysis id
        query = manager.createNamedQuery("Result.FetchForFinalReportByAnalysisId");
        query.setParameter("sid", sampleId);
        query.setParameter("aid", analysisId);
        query.setParameter("overrideid", Constants.dictionary().QAEVENT_OVERRIDE);

        list = query.getResultList();
        if (list == null || list.size() == 0)
            throw new NotFoundException();

        // build the grid
        results = new ArrayList<ArrayList<ResultViewDO>>();
        i = 0;
        ar = null;
        while (i < list.size()) {
            data = list.get(i);

            if ("N".equals(data.getIsColumn())) {
                if ("N".equals(data.getIsReportable())) {
                    do {
                        i++ ;
                    } while (i < list.size() && "Y".equals(list.get(i).getIsColumn()));
                    continue;
                }

                ar = new ArrayList<ResultViewDO>(1);
                results.add(ar);
            } else {
                if ("N".equals(data.getIsReportable())) {
                    i++ ;
                    continue;
                }
            }

            ar.add(data);
            i++ ;
        }

        return results;
    }

    public ArrayList<ResultViewDO> fetchForDataViewByAnalysisIds(ArrayList<Integer> ids) throws Exception {
        List<ResultViewDO> results;
        Query query;

        if (ids.size() == 0)
            throw new NotFoundException();

        query = manager.createNamedQuery("Result.FetchForDataViewByAnalysisIds");
        query.setParameter("ids", ids);

        results = query.getResultList();
        if (results.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(results);
    }

    public ArrayList<ResultViewDO> fetchForDataViewByAnalysisIdAndRowGroup(Integer analysisId,
                                                                           Integer rowGroup) throws Exception {
        List<ResultViewDO> list;
        Query query;

        query = manager.createNamedQuery("Result.FetchForDataViewByAnalysisIdAndRowGroup");
        query.setParameter("id", analysisId);
        query.setParameter("rowGroup", rowGroup);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ResultDO add(ResultDO data) throws Exception {
        Result entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Result();
        entity.setAnalysisId(data.getAnalysisId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setIsColumn(data.getIsColumn());
        entity.setSortOrder(data.getSortOrder());
        entity.setIsReportable(data.getIsReportable());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ResultDO update(ResultDO data) throws Exception {
        Result entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Result.class, data.getId());
        entity.setAnalysisId(data.getAnalysisId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setIsColumn(data.getIsColumn());
        entity.setSortOrder(data.getSortOrder());
        entity.setIsReportable(data.getIsReportable());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(ResultDO data) throws Exception {
        Result entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Result.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(ResultViewDO data, ResultFormatter rf, Integer accession,
                         AnalysisViewDO analysis) throws Exception {
        String test, method, value;
        ValidationErrorsList e;

        if ( !DataBaseUtil.isEmpty(data.getValue())) {
            e = new ValidationErrorsList();
            test = analysis.getTestName();
            method = analysis.getMethodName();
            if (data.getTypeId() == null) {
                e.add(new FormErrorException(Messages.get()
                                                     .result_valueInvalidException(accession,
                                                                                   test,
                                                                                   method,
                                                                                   data.getAnalyte(),
                                                                                   data.getValue())));
            } else {
                try {
                    rf.isValid(data.getTestResultId(),
                               data.getResultGroup(),
                               analysis.getUnitOfMeasureId(),
                               data.getValue());
                } catch (Exception ex) {
                    if (data.getDictionary() != null)
                        value = data.getDictionary();
                    else
                        value = data.getValue();

                    e.add(new FormErrorException(Messages.get()
                                                         .result_valueInvalidException(accession,
                                                                                       test,
                                                                                       method,
                                                                                       data.getAnalyte(),
                                                                                       value)));
                }
            }

            if (e.size() > 0)
                throw e;
        }
    }

    private void createTestResultHash(List<TestResultDO> testResultList,
                                      ArrayList<ResultValidator> resultValidators) {
        Integer rg;
        String dictEntry;
        ResultValidator rv;
        Type type;
        RoundingMethod method;
        DictionaryDO dict;

        rg = null;
        rv = null;
        try {
            for (TestResultDO data : testResultList) {
                dictEntry = null;
                method = null;

                if ( !DataBaseUtil.isSame(data.getResultGroup(), rg)) {
                    rv = new ResultValidator();
                    rg = data.getResultGroup();
                    resultValidators.add(rv);
                }

                if (DataBaseUtil.isSame(Constants.dictionary().ROUND_SIG_FIG,
                                        data.getRoundingMethodId()))
                    method = RoundingMethod.SIG_FIG;
                else if (DataBaseUtil.isSame(Constants.dictionary().ROUND_SIG_FIG_NOE,
                                             data.getRoundingMethodId()))
                    method = RoundingMethod.SIG_FIG_NOE;
                else if (DataBaseUtil.isSame(Constants.dictionary().ROUND_INT,
                                             data.getRoundingMethodId()))
                    method = RoundingMethod.INT;
                else if (DataBaseUtil.isSame(Constants.dictionary().ROUND_INT_SIG_FIG,
                                             data.getRoundingMethodId()))
                    method = RoundingMethod.INT_SIG_FIG;
                else if (DataBaseUtil.isSame(Constants.dictionary().ROUND_INT_SIG_FIG_NOE,
                                             data.getRoundingMethodId()))
                    method = RoundingMethod.INT_SIG_FIG_NOE;

                type = types.get(data.getTypeId());
                if (type == Type.DICTIONARY) {
                    dict = dictionaryCache.getById(Integer.parseInt(data.getValue()));
                    dictEntry = dict.getEntry();
                }
                rv.addResult(data.getId(),
                             data.getUnitOfMeasureId(),
                             type,
                             method,
                             data.getSignificantDigits(),
                             data.getValue(),
                             dictEntry);
            }
        } catch (Exception e) {
            resultValidators.clear();
            e.printStackTrace();
        }
    }
}