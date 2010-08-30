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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.TestAnalyteLocal;
import org.openelis.meta.TestMeta;

public class TestAnalyteManagerProxy {

    private static int               typeSuppl;

    private static final Logger      log  = Logger.getLogger(TestResultManagerProxy.class.getName());

    public TestAnalyteManagerProxy() {
        DictionaryDO data;
        DictionaryLocal dl;

        dl = dictLocal();

        try {
            data = dl.fetchBySystemName("test_analyte_suplmtl");
            typeSuppl = data.getId();
        } catch (Throwable e) {
            typeSuppl = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='test_analyte_suplmtl'", e);
        }
    }

    public TestAnalyteManager fetchByTestId(Integer testId) throws Exception {
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteManager tam;

        grid = local().fetchByTestId(testId);
        tam = TestAnalyteManager.getInstance();
        tam.setAnalytes(grid);
        tam.setTestId(testId);

        return tam;
    }

    public TestAnalyteManager add(TestAnalyteManager man, HashMap<Integer, Integer> idMap) throws Exception {
        TestAnalyteLocal al;
        ArrayList<TestAnalyteViewDO> list;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteViewDO data;
        int i, j, so, negId;

        al = local();
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
        TestAnalyteLocal al;
        ArrayList<TestAnalyteViewDO> list;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteViewDO data;
        int i, j, so, negId;

        al = local();
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
        TestAnalyteLocal al;
        ValidationErrorsList list;
        int i, j;
        List<TestAnalyteViewDO> analist;
        TestAnalyteViewDO data;
        GridFieldErrorException exc;
        Integer rg;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        ArrayList<ArrayList<TestResultViewDO>> results;

        al = local();
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
                    
                    if(j > 0 && DataBaseUtil.isSame(typeSuppl,data.getTypeId())) {
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

    private TestAnalyteLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (TestAnalyteLocal)ctx.lookup("openelis/TestAnalyteBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private DictionaryLocal dictLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
