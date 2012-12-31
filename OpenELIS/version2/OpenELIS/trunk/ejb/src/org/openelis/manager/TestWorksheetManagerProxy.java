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

import org.openelis.domain.Constants;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestWorksheetAnalyteLocal;
import org.openelis.local.TestWorksheetItemLocal;
import org.openelis.local.TestWorksheetLocal;
import org.openelis.meta.TestMeta;
import org.openelis.utils.EJBFactory;

public class TestWorksheetManagerProxy {

    public TestWorksheetManager fetchByTestId(Integer testId) throws Exception {
        TestWorksheetLocal wl;
        TestWorksheetItemLocal il;
        TestWorksheetAnalyteLocal al;
        TestWorksheetManager twm;
        TestWorksheetViewDO data;
        ArrayList<TestWorksheetItemDO> items;
        ArrayList<TestWorksheetAnalyteViewDO> analytes;

        items = null;
        twm = TestWorksheetManager.getInstance();
        data = null;
        analytes = null;
        wl = EJBFactory.getTestWorksheet();
        il = EJBFactory.getTestWorksheetItem();
        al = EJBFactory.getTestWorksheetAnalyte();
        data = wl.fetchByTestId(testId);

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

    public TestWorksheetManager add(TestWorksheetManager man,
                                    HashMap<Integer, Integer> anaIdMap) throws Exception {
        TestWorksheetLocal wl;
        TestWorksheetItemLocal il;
        TestWorksheetAnalyteLocal al;
        TestWorksheetViewDO worksheet;
        TestWorksheetItemDO item;
        TestWorksheetAnalyteViewDO analyte;
        int i;
        Integer id;

        worksheet = man.getWorksheet();

        wl = EJBFactory.getTestWorksheet();
        il = EJBFactory.getTestWorksheetItem();
        al = EJBFactory.getTestWorksheetAnalyte();

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
            item.setSortOrder(i + 1);
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

    public TestWorksheetManager update(TestWorksheetManager man,
                                       HashMap<Integer, Integer> anaIdMap) throws Exception {
        TestWorksheetLocal wl;
        TestWorksheetItemLocal il;
        TestWorksheetAnalyteLocal al;
        TestWorksheetViewDO data;
        TestWorksheetItemDO item;
        TestWorksheetAnalyteViewDO analyte;
        int i;
        Integer id;

        data = man.getWorksheet();
        wl = EJBFactory.getTestWorksheet();
        il = EJBFactory.getTestWorksheetItem();
        al = EJBFactory.getTestWorksheetAnalyte();

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
            item.setSortOrder(i + 1);
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
        wl = EJBFactory.getTestWorksheet();
        list = new ValidationErrorsList();

        try {
            //
            // This check is put here in order to distinguish between the cases
            // where
            // the TestWorksheetDO was changed on the screen and where it was
            // not.
            // This is necessary because it is possible for the users to enter
            // no
            // information on the screen in the fields related to the DO and
            // commit the data and since the DO can't be null because then the
            // fields
            // on the screen won't get refreshed on fetch, the validation code
            // below
            // will make error messages get displayed on the screen when there
            // was
            // no fault of the user.
            //
            if (data.isChanged() || man.itemCount() > 0 || man.analyteCount() > 0)
                wl.validate(data);
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        validateWorksheetItems(list, man.getItems(), data);
        validateWorksheetAnalytes(list, man.getAnalytes());
        if (list.size() > 0)
            throw list;
    }

    private void validateWorksheetItems(ValidationErrorsList list,
                                        List<TestWorksheetItemDO> items,
                                        TestWorksheetViewDO data) {
        int i, size;
        boolean checkPosition;
        Integer bc, tc, position;
        ArrayList<Integer> posList;
        TestWorksheetItemDO currDO, prevDO;
        TestWorksheetItemLocal il;

        if (items == null)
            return;

        size = items.size();
        il = EJBFactory.getTestWorksheetItem();

        bc = data.getSubsetCapacity();
        tc = data.getTotalCapacity();
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
                    list.add(new TableFieldErrorException("posMoreThanZeroException",
                                                          i,
                                                          TestMeta.getWorksheetItemPosition(),
                                                          "worksheetTable"));
                    checkPosition = false;
                } else if (bc != null && position > bc) {
                    list.add(new TableFieldErrorException("posExcSubsetCapacityException",
                                                          i,
                                                          TestMeta.getWorksheetItemPosition(),
                                                          "worksheetTable"));
                    checkPosition = false;
                } else if (tc != null && position > tc) {
                    list.add(new TableFieldErrorException("posExcTotalCapacityException",
                                                          i,
                                                          TestMeta.getWorksheetItemPosition(),
                                                          "worksheetTable"));
                    checkPosition = false;
                } else {
                    if ( !posList.contains(position)) {
                        posList.add(position);
                    } else {
                        list.add(new TableFieldErrorException("duplicatePosForQCsException",
                                                              i,
                                                              TestMeta.getWorksheetItemPosition(),
                                                              "worksheetTable"));
                        checkPosition = false;
                    }
                }
            }

            if (checkPosition) {
                if (duplicateAfterFixed(currDO, prevDO)) {
                    list.add(new TableFieldErrorException("duplPosAfterFixedPosException",
                                                          i,
                                                          TestMeta.getWorksheetItemPosition(),
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

        if (analytes == null)
            return;

        idlist = new ArrayList<Integer>();
        al = EJBFactory.getTestWorksheetAnalyte();

        for (int i = 0; i < analytes.size(); i++ ) {
            data = analytes.get(i);
            anaId = data.getTestAnalyteId();

            try {
                al.validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "worksheetAnalyteTable", i);
            }
            if ( !idlist.contains(anaId)) {
                idlist.add(anaId);
            } else {
                list.add(new TableFieldErrorException("duplicateWSAnalyteException",
                                                      i,
                                                      TestMeta.getWorksheetAnalyteAnalyteId(),
                                                      "worksheetAnalyteTable"));
            }
        }

    }

    /**
     * This method will return true if the type specified in currDO is duplicate
     * and if the type specified in prevDO is fixed or fixedAlways and, such
     * that the position specified in prevDO is one less than the position in
     * currDO. The three integers, typeDupl, typeFixed and typeFixedAlways, are
     * the ids of the dictionary records that contain the entries for the fixed
     * and duplicate types respectively
     */
    private boolean duplicateAfterFixed(TestWorksheetItemDO currDO,
                                        TestWorksheetItemDO prevDO) {
        Integer ptId, ctId, ppos, cpos;

        if (prevDO == null || currDO == null)
            return false;

        ptId = prevDO.getTypeId();
        ctId = currDO.getTypeId();
        cpos = currDO.getPosition();
        ppos = prevDO.getPosition();

        if (ppos != null && cpos != null && ppos == cpos - 1) {
            if (DataBaseUtil.isSame(Constants.dictionary().POS_DUPLICATE, ctId) &&
                (DataBaseUtil.isSame(Constants.dictionary().POS_FIXED, ptId) ||
                                DataBaseUtil.isSame(Constants.dictionary().POS_FIXED_ALWAYS, ptId)))
                return true;
        }

        return false;
    }
}
