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
import java.util.Iterator;

import org.openelis.bean.IOrderTestAnalyteBean;
import org.openelis.domain.Constants;
import org.openelis.domain.IOrderTestAnalyteViewDO;
import org.openelis.domain.IOrderTestViewDO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class IOrderTestAnalyteManagerProxy {
    
    public IOrderTestAnalyteManager fetchByIorderTestId(Integer id) throws Exception {
        IOrderTestAnalyteManager man;
        ArrayList<IOrderTestAnalyteViewDO> analytes;

        man = IOrderTestAnalyteManager.getInstance();
        man.setIorderTestId(id);
        analytes =  EJBFactory.getIOrderTestAnalyte().fetchByIorderTestId(id);
        man.setAnalytes(analytes);

        return man;
    }
    
    public IOrderTestAnalyteManager fetchMergedByIorderTestId(Integer id) throws Exception {
        IOrderTestAnalyteManager man;
        ArrayList<IOrderTestAnalyteViewDO> analytes;
        ArrayList<IOrderTestAnalyteViewDO> testAnalytes;

        man = IOrderTestAnalyteManager.getInstance();
        man.setIorderTestId(id);
        try {
            analytes = EJBFactory.getIOrderTestAnalyte().fetchByIorderTestId(id);
        } catch (NotFoundException e) {
            analytes = null;
        }
        try {
            testAnalytes = EJBFactory.getIOrderTestAnalyte().fetchRowAnalytesByIorderTestId(id);
        } catch (NotFoundException e) {
            testAnalytes = new ArrayList<IOrderTestAnalyteViewDO>();
        }
        man.setAnalytes(mergeAnalytes(analytes, testAnalytes));

        return man;
    }
    
    public IOrderTestAnalyteManager fetchByTestId(Integer id) throws Exception {
        IOrderTestAnalyteManager man;
        ArrayList<IOrderTestAnalyteViewDO> testAnalytes;

        man = IOrderTestAnalyteManager.getInstance();
        testAnalytes =  EJBFactory.getIOrderTestAnalyte().fetchRowAnalytesByTestId(id);
        man.setAnalytes(mergeAnalytes(null, testAnalytes));

        return man;
    }
    
    public IOrderTestAnalyteManager add(IOrderTestAnalyteManager man) throws Exception {
        int i;
        IOrderTestAnalyteBean tal;
        IOrderTestAnalyteViewDO data;

        tal = EJBFactory.getIOrderTestAnalyte();
        
        for (i = 0; i < man.count(); i++) {
            data = man.getAnalyteAt(i);
            data.setIorderTestId(man.getIorderTestId());
            tal.add(data);
        }

        return man;
    }

    public IOrderTestAnalyteManager update(IOrderTestAnalyteManager man) throws Exception {
        int i;
        IOrderTestAnalyteBean tal;
        IOrderTestAnalyteViewDO data;

        tal = EJBFactory.getIOrderTestAnalyte();

        for (i = 0; i < man.deleteCount(); i++ )
            tal.delete(man.getDeletedAt(i));        
        
        for (i = 0; i < man.count(); i++) {
            data = man.getAnalyteAt(i);
            if (data.getId() == null) {
                data.setIorderTestId(man.getIorderTestId());
                tal.add(data);
            } else {
                tal.update(data);
            }
        }
        return man;
    }

    public void validate(IOrderTestAnalyteManager man, IOrderTestViewDO test, int index) throws Exception {
        ValidationErrorsList list;
        IOrderTestAnalyteViewDO data;        
        IOrderTestAnalyteBean al;
        
        list = new ValidationErrorsList();
        al = EJBFactory.getIOrderTestAnalyte();
        for (int i = 0; i < man.count(); i++) {
            data = man.getAnalyteAt(i);
            try {
                al.validate(data, index);
            } catch (Exception e) {
                list.add(e);
            }
        }
        
        if (list.size() > 0)
            throw list;
    }
    
    private ArrayList<IOrderTestAnalyteViewDO> mergeAnalytes(ArrayList<IOrderTestAnalyteViewDO> ordTestAnaList,
                                                            ArrayList<IOrderTestAnalyteViewDO> testAnaList) {
        HashMap<Integer, IOrderTestAnalyteViewDO> map;
        IOrderTestAnalyteViewDO data;
        Iterator<IOrderTestAnalyteViewDO> iter;
        
        /*
         * this method assumes that the list that potentially has the data for one
         * or more test analytes will not be null and that the responsibility of
         * this method is just to modify it and not to create a new list if it is null          
         */
        
        map = null;
        if (ordTestAnaList != null) {
            /*
             * some analytes were added to the order from this test previously
             */
            map = new HashMap<Integer, IOrderTestAnalyteViewDO>();
            for (IOrderTestAnalyteViewDO d1: ordTestAnaList)               
                map.put(d1.getAnalyteId(), d1);
        }
                                
        for (IOrderTestAnalyteViewDO testAna : testAnaList) {
            if (map != null) {
                data = map.get(testAna.getAnalyteId());
                if (data != null) {
                    /*
                     * if an analyte from the list of test analytes is found in 
                     * the hashmap then we remove it from  hashmap so that the
                     * hashmap eventually only contains those analytes that are
                     * not present in the test anymore
                     */
                    testAna.setId(data.getId());
                    testAna.setIorderTestId(data.getIorderTestId());
                    testAna.setTestAnalyteIsReportable("Y");
                    testAna.setTestAnalyteIsPresent("Y");
                    map.remove(testAna.getAnalyteId());
                } else {
                    /*
                     * This analyte is present in the test but was not added to 
                     * the order. This could mean e.g. that the analyte was added
                     * to the test after analytes were added to the order or that
                     * the user didn't select it previously. In any case, it's marked
                     * as not reportable to prevent it from getting accidentally
                     * added to the order.  
                     */
                    testAna.setTestAnalyteIsReportable("N");
                }
            } else if (Constants.dictionary().TEST_ANALYTE_SUPLMTL.equals(testAna.getTestAnalyteTypeId())) {
                /*
                 * No analytes were added to the order from this test. So each one
                 * preserves its reportability from the test unless it's supplemental.
                 */
                testAna.setTestAnalyteIsReportable("N");
            }
        }
        
        if (map != null) {
            /*
             * these analytes are not present in the test anymore
             */
            iter = map.values().iterator();            
            while (iter.hasNext()) {
                data = iter.next();
                data.setTestAnalyteIsPresent("N");
                testAnaList.add(data);
            }
        }
        
        return testAnaList;
    }
}