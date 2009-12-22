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
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.TestWorksheetAnalyteLocal;
import org.openelis.local.TestWorksheetItemLocal;
import org.openelis.local.TestWorksheetLocal;
import org.openelis.meta.TestMeta;
import org.openelis.utilcommon.DataBaseUtil;

public class TestWorksheetManagerProxy {

    private static final TestMeta    meta = new TestMeta();
    
    private static int               typeBatch, typeTotal, typeFixed, typeDupl; 
    
    private static final Logger      log  = Logger.getLogger(TestWorksheetManagerProxy.class.getName());
    
    public TestWorksheetManagerProxy() {
        DictionaryDO data;
        DictionaryLocal dl;
        
        dl = dictLocal();
        
        try {
            data = dl.fetchBySystemName("wsheet_num_format_batch");
            typeBatch = data.getId();
        } catch (Throwable e) {
            typeBatch = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='wsheet_num_format_batch'", e);
        }
        
        try {
            data = dl.fetchBySystemName("wsheet_num_format_total");
            typeTotal = data.getId();
        } catch (Throwable e) {
            typeTotal = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='wsheet_num_format_total'", e);
        }
        
        try {
            data = dl.fetchBySystemName("pos_fixed");
            typeFixed = data.getId();
        } catch (Throwable e) {
            typeFixed = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='pos_fixed'", e);
        }
        
        try {
            data = dl.fetchBySystemName("pos_duplicate");
            typeDupl = data.getId();
        } catch (Throwable e) {
            typeDupl = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='pos_duplicate'", e);
        }
    }

    public TestWorksheetManager fetchByTestId(Integer testId) throws Exception {
        TestWorksheetLocal wl;
        TestWorksheetItemLocal il;
        TestWorksheetAnalyteLocal al;
        TestWorksheetManager twm;
        TestWorksheetViewDO data;
        ArrayList<TestWorksheetItemDO> items;
        ArrayList<TestWorksheetAnalyteViewDO> analytes;

        items = null;
        wl = worksheetLocal();
        twm = TestWorksheetManager.getInstance();
        data = null;
        analytes = null;
                
        data = wl.fetchByTestId(testId);
        
        il = itemLocal();
        al = analyteLocal();

        if (data == null) {
            data = new TestWorksheetViewDO();
        } else {
            items = il.fetchByTestWorksheetId(data.getId());        
        }
        
        analytes = al.fetchByTestId(testId);

        twm.setTestId(testId);
        twm.setWorksheet(data);
        twm.setItems(items);
        twm.setAnalytes(analytes);

        return twm;
    }

    public TestWorksheetManager add(TestWorksheetManager man, HashMap<Integer, Integer> anaIdMap)
                                                                                                 throws Exception {
        TestWorksheetLocal wl;
        TestWorksheetItemLocal il;
        TestWorksheetAnalyteLocal al;
        TestWorksheetViewDO worksheet;
        TestWorksheetItemDO item;
        TestWorksheetAnalyteViewDO analyte;
        int i;
        Integer id;

        worksheet = man.getWorksheet();
        wl = worksheetLocal();
        il = itemLocal();
        al = analyteLocal();

        //
        // This check is put here in order to distinguish between the cases
        // where
        // the TestWorksheetDO was changed on the screen and where it was not.
        // This is necessary because it is possible for the users to enter no
        // information on the screen in the fields related to the DO and
        // commit the data and the DO can't be null because then the fields
        // on the screen won't get refreshed on fetch or when the screen goes
        // into the add mode. The _changed flag will get set if any of the
        // fields was changed on the screen. The validation code won't be
        // executed
        // if the _changed flag isn't set for the same reason.
        //
        if (worksheet.isChanged()) {
            worksheet.setTestId(man.getTestId());
            wl.add(worksheet);
        }

        for (i = 0; i < man.itemCount(); i++ ) {
            item = man.getItemAt(i);
            item.setTestWorksheetId(worksheet.getId());

            il.add(item);
        }

        for (i = 0; i < man.analyteCount(); i++ ) {
            analyte = man.getAnalyteAt(i);
            id = analyte.getTestAnalyteId();
            if (id < 0)
                analyte.setTestAnalyteId(anaIdMap.get(id));
            analyte.setTestId(man.getTestId());
            al.add(analyte);
        }

        return man;
    }

    public TestWorksheetManager update(TestWorksheetManager man, HashMap<Integer, Integer> anaIdMap)
                                                                                                    throws Exception {
        TestWorksheetLocal wl;
        TestWorksheetItemLocal il;
        TestWorksheetAnalyteLocal al;
        TestWorksheetViewDO data;
        TestWorksheetItemDO item;
        TestWorksheetAnalyteViewDO analyte;
        int i;
        Integer id;

        data = man.getWorksheet();
        wl = worksheetLocal();
        il = itemLocal();
        al = analyteLocal();

        //
        // This check for the _changed flag is put here in order to distinguish
        // between the cases where the TestWorksheetDO was changed on the screen
        // and where it was not. This is necessary because it is possible for
        // the users to enter no information on the screen in the fields related
        // to the DO and commit the data. The DO can't be null because then the
        // fields on the screen won't get refreshed on fetch or when the screen
        // goes into the add mode. The _changed flag will get set if any of the
        // fields was changed on the screen. The validation code won't be
        // executed if the _changed flag isn't set for the same reason.
        //
        if (data.getId() == null && data.isChanged()) {
            data.setTestId(man.getTestId());
            wl.add(data);
        } else {
            wl.update(data);
        }

        for (i = 0; i < man.deleteItemCount(); i++ ) {
            il.delete(man.getDeletedItemAt(i));
        }

        for (i = 0; i < man.itemCount(); i++ ) {
            item = man.getItemAt(i);
            if (item.getId() == null) {
                item.setTestWorksheetId(data.getId());
                il.add(item);
            } else {
                il.update(item);
            }
        }

        for (i = 0; i < man.deleteAnalyteCount(); i++ ) {
            al.delete(man.getDeletedAnalyteAt(i));
        }

        for (i = 0; i < man.analyteCount(); i++ ) {
            analyte = man.getAnalyteAt(i);
            id = analyte.getTestAnalyteId();
            if (id < 0)
                analyte.setTestAnalyteId(anaIdMap.get(id));

            if (analyte.getId() == null) {
                analyte.setTestId(man.getTestId());
                al.add(analyte);
            } else {
                al.update(analyte);
            }
        }

        return man;
    }

    public void validate(TestWorksheetManager man) throws Exception {
        ValidationErrorsList list;
        TestWorksheetViewDO data;
        TestWorksheetLocal wl;

        data = man.getWorksheet();
        wl = worksheetLocal();
        list = new ValidationErrorsList();

        try {
            wl.validate(data);
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        validateWorksheetItems(list, man.getItems(), data);
        validateWorksheetAnalytes(list, man.getAnalytes());
        if (list.size() > 0)
            throw list;
    }

    private TestWorksheetLocal worksheetLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (TestWorksheetLocal)ctx.lookup("openelis/TestWorksheetBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private TestWorksheetItemLocal itemLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (TestWorksheetItemLocal)ctx.lookup("openelis/TestWorksheetItemBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private TestWorksheetAnalyteLocal analyteLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (TestWorksheetAnalyteLocal)ctx.lookup("openelis/TestWorksheetAnalyteBean/local");
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

    private void validateWorksheetItems(ValidationErrorsList list,
                                        List<TestWorksheetItemDO> items,
                                        TestWorksheetViewDO data) {
        Integer bc, tc, position, formatId;
        ArrayList<Integer> posList;
        int i, size;
        TestWorksheetItemDO currDO, prevDO;
        boolean checkPosition;
        
        TestWorksheetItemLocal il;
        TestWorksheetLocal wl;

        if (items == null)
            return;

        bc = null;
        tc = null;
        formatId = null;
        size = items.size();

        il = itemLocal();
        wl = worksheetLocal();

        if (data != null) {
            bc = data.getBatchCapacity();
            tc = data.getTotalCapacity();
            formatId = data.getFormatId();
        } else if (size > 0) {
            // 
            // if there's no data in TestWorksheetViewDO it means that the user didn't
            // specify any details about the kind of worksheet it will be and so
            // if there are qcs present then this is an erroneous situation and
            // the errors related to the worksheet information must be added to
            // the list of errors and since the validation for a TestWorksheetDO
            // won't be carried out unless the _changed flag is set, we set the
            // 3 fields that are required for a TestWorksheet
            //
            data = new TestWorksheetViewDO();
            data.setBatchCapacity(bc);
            data.setFormatId(formatId);
            data.setTotalCapacity(tc);
            try {
                wl.validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e);
            }
        }

        posList = new ArrayList<Integer>();
        checkPosition = false;

        prevDO = null;

        for (i = 0; i < size; i++ ) {
            currDO = items.get(i);

            if (i > 0)
                prevDO = items.get(i - 1);

            position = currDO.getPosition();
            checkPosition = true;            

            try {
                il.validate(currDO);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "worksheetTable", i);
            }

            if (position != null) {
                if (position <= 0) {
                    list.add(new TableFieldErrorException("posMoreThanZeroException", i,
                                                                   meta.getWorksheetItemPosition(),
                                                                   "worksheetTable"));
                    checkPosition = false;
                } else if (bc != null && DataBaseUtil.isSame(typeBatch,formatId) && position > bc) {
                    list.add(new TableFieldErrorException("posExcBatchCapacityException",
                                                                   i, meta.getWorksheetItemPosition(),
                                                                   "worksheetTable"));
                    checkPosition = false;
                } else if (tc != null && DataBaseUtil.isSame(typeTotal,formatId) && position > tc) {
                    list.add(new TableFieldErrorException("posExcTotalCapacityException",
                                                                   i, meta.getWorksheetItemPosition(),
                                                                   "worksheetTable"));
                    checkPosition = false;
                } else {
                    if (!posList.contains(position)) {
                        posList.add(position);
                    } else {
                        list.add(new TableFieldErrorException("duplicatePosForQCsException",
                                                                       i,
                                                                       meta.getWorksheetItemPosition(),
                                                                       "worksheetTable"));
                        checkPosition = false;
                    }
                }
            }

            if (checkPosition) {
                if (duplicateAfterFixedOrDuplicate(currDO, prevDO)) {
                    list.add(new TableFieldErrorException("duplPosAfterFixedOrDuplPosException",
                                                                   i, meta.getWorksheetItemPosition(),
                                                                   "worksheetTable"));
                }
            }

        }
    }

    private void validateWorksheetAnalytes(ValidationErrorsList list,
                                           ArrayList<TestWorksheetAnalyteViewDO> analytes) {
        TestWorksheetAnalyteViewDO data;
        Integer anaId;
        ArrayList<Integer> idlist;
        TestWorksheetAnalyteLocal al;

        if(analytes == null)
            return;
        
        idlist = new ArrayList<Integer>();
        al = analyteLocal();
        
        for (int i = 0; i < analytes.size(); i++ ) {
            data = analytes.get(i);
            anaId = data.getTestAnalyteId();

            try {
                al.validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "worksheetAnalyteTable", i);
            }            
            if (!idlist.contains(anaId)) {
                idlist.add(anaId);
            } else {
                list.add(new TableFieldErrorException("duplicateWSAnalyteException", i,
                                                               meta.getWorksheetAnalyteAnalyteId(),
                                                               "worksheetAnalyteTable"));
            }            
        }

    }

    /**
     * This method will return true if the type specified in currDO is duplicate
     * and if the type specified in prevDO is either fixed or duplicate and,
     * such that the position specified in prevDO is one less than the position
     * in currDO. The two integers, fixedId and duplId, are the ids of the
     * dictionary records that contain the entries for the fixed and duplicate
     * types respectively
     */
    private boolean duplicateAfterFixedOrDuplicate(TestWorksheetItemDO currDO,
                                                   TestWorksheetItemDO prevDO) {
        Integer ptId, ctId, ppos, cpos;

        if (prevDO == null || currDO == null)
            return false;

        ptId = prevDO.getTypeId();
        ctId = currDO.getTypeId();
        cpos = currDO.getPosition();
        ppos = prevDO.getPosition();

        if (ppos != null && cpos != null && ppos == cpos - 1) {
            if (DataBaseUtil.isSame(typeDupl,ctId) && (DataBaseUtil.isSame(typeDupl,ptId) ||
                            DataBaseUtil.isSame(typeFixed,ptId)))
                return true;
        }

        return false;
    }
}
