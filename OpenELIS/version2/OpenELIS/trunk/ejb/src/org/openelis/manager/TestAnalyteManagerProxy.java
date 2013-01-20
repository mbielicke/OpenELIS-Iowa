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
import java.util.HashMap;
import java.util.List;

import org.openelis.bean.DictionaryBean;
import org.openelis.bean.TestAnalyteBean;
import org.openelis.domain.Constants;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.TestMeta;
import org.openelis.utils.EJBFactory;

public class TestAnalyteManagerProxy {

    public TestAnalyteManager fetchByTestId(Integer testId) throws Exception {
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteManager tam;

        grid = EJBFactory.getTestAnalyte().fetchByTestId(testId);
        tam = TestAnalyteManager.getInstance();
        tam.setAnalytes(grid);
        tam.setTestId(testId);

        return tam;
    }

    public TestAnalyteManager add(TestAnalyteManager man, HashMap<Integer, Integer> idMap)
                                                                                          throws Exception {
        int i, j, so, negId;
        TestAnalyteBean al;
        ArrayList<TestAnalyteViewDO> list;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteViewDO data;        

        al = EJBFactory.getTestAnalyte();
        grid = man.getAnalytes();
        so = 0;
        negId = 0;

        for (i = 0; i < man.rowCount(); i++ ) {
            list = grid.get(i);
            for (j = 0; j < list.size(); j++ ) {
                data = list.get(j);
                if (j == 0)
                    negId = data.getId();
                data.setTestId(man.getTestId());
                data.setSortOrder( ++so);
                al.add(data);

                if (j == 0)
                    idMap.put(negId, data.getId());
            }

        }

        return man;
    }

    public TestAnalyteManager update(TestAnalyteManager man, HashMap<Integer, Integer> idMap)
                                                                                             throws Exception {
        int i, j, so, negId;
        TestAnalyteBean al;
        ArrayList<TestAnalyteViewDO> list;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteViewDO data;

        al = EJBFactory.getTestAnalyte();
        grid = man.getAnalytes();
        so = 0;
        negId = 0;

        for (i = 0; i < man.deleteCount(); i++ ) {
            al.delete(man.getDeletedAt(i));
        }

        for (i = 0; i < man.rowCount(); i++ ) {
            list = grid.get(i);
            for (j = 0; j < list.size(); j++ ) {
                data = list.get(j);
                data.setSortOrder( ++so);
                if (data.getId() == null) {
                    data.setTestId(man.getTestId());
                    al.add(data);
                } else if (data.getId() < 0) {
                    negId = data.getId();
                    data.setTestId(man.getTestId());
                    al.add(data);
                    idMap.put(negId, data.getId());
                } else {
                    al.update(data);
                }
            }

        }

        return man;
    }

    public void validate(TestAnalyteManager tam,
                         TestResultManager trm,
                         HashMap<Integer, Integer> anaResGrpMap) throws Exception {
        int i, j;
        TestAnalyteBean al;
        ValidationErrorsList list;        
        List<TestAnalyteViewDO> analist;
        TestAnalyteViewDO data;
        GridFieldErrorException exc;
        Integer rg;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        ArrayList<ArrayList<TestResultViewDO>> results;

        al = EJBFactory.getTestAnalyte();
        list = new ValidationErrorsList();
        grid = tam.getAnalytes();
        results = trm.getResults();

        for (i = 0; i < grid.size(); i++ ) {
            analist = grid.get(i);

            for (j = 0; j < analist.size(); j++ ) {
                data = analist.get(j);
                rg = data.getResultGroup();
                if (j == 0 && !DataBaseUtil.isEmpty(rg))
                    anaResGrpMap.put(data.getId(), rg);

                try {
                    al.validate(data);

                    if (rg > results.size()) {
                        exc = new GridFieldErrorException("invalidResultGroupException", i, j,
                                                          TestMeta.getAnalyteResultGroup(),
                                                          "analyteTable");
                        list.add(exc);
                    }

                    if (j > 0 && DataBaseUtil.isSame(Constants.dictionary().TEST_ANALYTE_SUPLMTL, data.getTypeId())) {
                        exc = new GridFieldErrorException("columnAnalyteSupplException", i, j,
                                                          TestMeta.getAnalyteTypeId(),
                                                          "analyteTable");
                        list.add(exc);
                    }
                } catch (Exception e) {
                    DataBaseUtil.mergeException(list, e, "analyteTable", i, j);
                }

            }
        }

        if (list.size() > 0)
            throw list;

    }
}
