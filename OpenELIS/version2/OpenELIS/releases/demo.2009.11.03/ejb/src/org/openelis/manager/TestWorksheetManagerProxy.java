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

import javax.naming.InitialContext;

import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.CategoryLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.TestWorksheetAnalyteLocal;
import org.openelis.local.TestWorksheetItemLocal;
import org.openelis.local.TestWorksheetLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

public class TestWorksheetManagerProxy {

    private static final TestMetaMap meta = new TestMetaMap();

    public TestWorksheetManager fetchByTestId(Integer testId) throws Exception {
        TestWorksheetLocal wl;
        TestWorksheetItemLocal il;
        TestWorksheetAnalyteLocal al;
        TestWorksheetManager twm;
        TestWorksheetViewDO wsDO;
        ArrayList<TestWorksheetItemDO> items;
        ArrayList<TestWorksheetAnalyteViewDO> analytes;

        items = null;
        wl = worksheetLocal();
        twm = TestWorksheetManager.getInstance();
        wsDO = null;
        analytes = null;

        try {
            wsDO = wl.fetchByTestId(testId);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        il = itemLocal();
        al = analyteLocal();

        if (wsDO == null) {
            wsDO = new TestWorksheetViewDO();
        } else {
            try {
                items = il.fetchByTestWorksheetId(wsDO.getId());
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        
        try {
            analytes = al.fetchByTestId(testId);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        twm.setTestId(testId);
        twm.setWorksheet(wsDO);
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
        if (worksheet.getId() == null && worksheet.isChanged()) {
            worksheet.setTestId(man.getTestId());
            wl.add(worksheet);
        } else {
            wl.update(worksheet);
        }

        for (i = 0; i < man.deleteItemCount(); i++ ) {
            il.delete(man.getDeletedItemAt(i));
        }

        for (i = 0; i < man.itemCount(); i++ ) {
            item = man.getItemAt(i);
            if (item.getId() == null) {
                item.setTestWorksheetId(worksheet.getId());
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
        TestWorksheetViewDO wsDO;
        TestWorksheetLocal wl;

        wsDO = man.getWorksheet();
        wl = worksheetLocal();
        list = new ValidationErrorsList();

        try {
            wl.validate(wsDO);
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        validateWorksheetItems(list, man.getItems(), wsDO);
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

    private void validateWorksheetItems(ValidationErrorsList exceptionList,
                                        List<TestWorksheetItemDO> itemDOList,
                                        TestWorksheetViewDO worksheetDO) {
        Integer bc, tc, position, batchId, totalId, formatId, fixedId, duplId;
        ArrayList<Integer> posList;
        int i, size;
        TestWorksheetItemDO currDO, prevDO;
        boolean checkPosition;
        String sysName, name;
        TestWorksheetItemLocal il;
        TestWorksheetLocal wl;

        if (itemDOList == null)
            return;

        bc = null;
        tc = null;
        formatId = null;
        size = itemDOList.size();
        batchId = null;
        totalId = null;
        fixedId = null;
        duplId = null;
        sysName= null;

        il = itemLocal();
        wl = worksheetLocal();

        if (worksheetDO != null) {
            bc = worksheetDO.getBatchCapacity();
            tc = worksheetDO.getTotalCapacity();
            formatId = worksheetDO.getFormatId();
        } else if (size > 0) {
            // 
            // if there's no data in worksheetDO it means that the user didn't
            // specify any details about the kind of worksheet it will be and so
            // if there are qcs present then this is an erroneous situation and
            // the errors related to the worksheet information must be added to
            // the list of errors and since the validation for a TestWorksheetDO
            // won't be carried out unless the _changed flag is set, we set the
            // 3 fields that are required for a TestWorksheet
            //
            worksheetDO = new TestWorksheetViewDO();
            worksheetDO.setBatchCapacity(bc);
            worksheetDO.setFormatId(formatId);
            worksheetDO.setTotalCapacity(tc);
            try {
                wl.validate(worksheetDO);
            } catch (Exception e) {
                DataBaseUtil.mergeException(exceptionList, e);
            }
        }

        posList = new ArrayList<Integer>();
        checkPosition = false;

        try {
            batchId = (dictLocal().fetchBySystemName("batch")).getId();
            totalId = (dictLocal().fetchBySystemName("total")).getId();
            fixedId = (dictLocal().fetchBySystemName("pos_fixed")).getId();
            duplId = (dictLocal().fetchBySystemName("pos_duplicate")).getId();
        } catch(Exception e) {
            e.printStackTrace();
        }
        prevDO = null;

        for (i = 0; i < size; i++ ) {
            currDO = itemDOList.get(i);

            if (i > 0)
                prevDO = itemDOList.get(i - 1);

            position = currDO.getPosition();
            checkPosition = true;
            name = currDO.getQcName();

            if (name == null || "".equals(name)) {
                exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                                                               meta.getTestWorksheetItem()
                                                                   .getQcName(), "worksheetTable"));
            }
            if (currDO.getTypeId() == null) {
                exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                                                               meta.getTestWorksheetItem()
                                                                   .getTypeId(), "worksheetTable"));
                checkPosition = false;
            }

            try {
                il.validate(currDO);
            } catch (Exception e) {
                DataBaseUtil.mergeException(exceptionList, e, "worksheetTable", i);
            }

            if (position != null) {
                if (position <= 0) {
                    exceptionList.add(new TableFieldErrorException("posMoreThanZeroException", i,
                                                                   meta.getTestWorksheetItem()
                                                                       .getPosition(),
                                                                   "worksheetTable"));
                    checkPosition = false;
                } else if (bc != null && batchId.equals(formatId) && position > bc) {
                    exceptionList.add(new TableFieldErrorException("posExcBatchCapacityException",
                                                                   i, meta.getTestWorksheetItem()
                                                                          .getPosition(),
                                                                   "worksheetTable"));
                    checkPosition = false;
                } else if (tc != null && totalId.equals(formatId) && position > tc) {
                    exceptionList.add(new TableFieldErrorException("posExcTotalCapacityException",
                                                                   i, meta.getTestWorksheetItem()
                                                                          .getPosition(),
                                                                   "worksheetTable"));
                    checkPosition = false;
                } else {
                    if ( !posList.contains(position)) {
                        posList.add(position);
                    } else {
                        exceptionList.add(new TableFieldErrorException(
                                                                       "duplicatePosForQCsException",
                                                                       i,
                                                                       meta.getTestWorksheetItem()
                                                                           .getPosition(),
                                                                       "worksheetTable"));
                        checkPosition = false;
                    }
                }
            }

            if (checkPosition) {
                try {
                    sysName = (dictLocal().fetchById((currDO.getTypeId())).getSystemName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (position == null) {
                    if ("pos_duplicate".equals(sysName) || "".equals(sysName)) {
                        exceptionList.add(new TableFieldErrorException(
                                                                       "fixedDuplicatePosException",
                                                                       i,
                                                                       meta.getTestWorksheetItem()
                                                                           .getPosition(),
                                                                       "worksheetTable"));
                    }
                } else {
                    if (position == 1 && "pos_duplicate".equals(sysName)) {
                        exceptionList.add(new TableFieldErrorException("posOneDuplicateException",
                                                                       i,
                                                                       meta.getTestWorksheetItem()
                                                                           .getTypeId(),
                                                                       "worksheetTable"));
                    } else if ( !"pos_duplicate".equals(sysName) && !"pos_fixed".equals(sysName)) {
                        exceptionList.add(new TableFieldErrorException("posSpecifiedException", i,
                                                                       meta.getTestWorksheetItem()
                                                                           .getPosition(),
                                                                       "worksheetTable"));
                    }
                }

                if (duplicateAfterFixedOrDuplicate(currDO, prevDO, fixedId, duplId)) {
                    exceptionList.add(new TableFieldErrorException(
                                                                   "duplPosAfterFixedOrDuplPosException",
                                                                   i, meta.getTestWorksheetItem()
                                                                          .getPosition(),
                                                                   "worksheetTable"));
                }
            }

        }
    }

    private void validateWorksheetAnalytes(ValidationErrorsList exceptionList,
                                           ArrayList<TestWorksheetAnalyteViewDO> twsaDOList) {
        TestWorksheetAnalyteViewDO twsaDO;
        Integer anaId;
        ArrayList<Integer> idlist;
        TestWorksheetAnalyteLocal al;

        if(twsaDOList == null)
            return;
        
        idlist = new ArrayList<Integer>();
        al = analyteLocal();
        
        for (int i = 0; i < twsaDOList.size(); i++ ) {
            twsaDO = twsaDOList.get(i);
            anaId = twsaDO.getTestAnalyteId();

            try {
                al.validate(twsaDO);
            } catch (Exception e) {
                DataBaseUtil.mergeException(exceptionList, e, "worksheetAnalyteTable", i);
            }            
            if (!idlist.contains(anaId)) {
                idlist.add(anaId);
            } else {
                exceptionList.add(new TableFieldErrorException("duplicateWSAnalyteException", i,
                                                               meta.getTestWorksheetAnalyte()
                                                                   .getAnalyteId(),
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
                                                   TestWorksheetItemDO prevDO,
                                                   Integer fixedId,
                                                   Integer duplId) {
        Integer ptId, ctId, ppos, cpos;

        if (prevDO == null || currDO == null)
            return false;

        ptId = prevDO.getTypeId();
        ctId = currDO.getTypeId();
        cpos = currDO.getPosition();
        ppos = prevDO.getPosition();

        if (ppos != null && cpos != null && ppos == cpos - 1) {
            if (duplId.equals(ctId) && (duplId.equals(ptId) || fixedId.equals(ptId)))
                return true;
        }

        return false;
    }
}
