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

import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.QcAnalyteLocal;
import org.openelis.local.TestResultLocal;
import org.openelis.metamap.QcMetaMap;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utilcommon.NumericRange;
import org.openelis.utilcommon.TestResultValidator;
import org.openelis.utilcommon.TiterRange;

public class QcAnalyteManagerProxy {

    private static final QcMetaMap meta = new QcMetaMap();

    public QcAnalyteManager fetchByQcId(Integer id) throws Exception {
        QcAnalyteManager cm;
        ArrayList<QcAnalyteViewDO> analytes;

        analytes = local().fetchByQcId(id);
        cm = QcAnalyteManager.getInstance();
        cm.setQcId(id);
        cm.setAnalytes(analytes);

        return cm;
    }

    public QcAnalyteManager add(QcAnalyteManager man) throws Exception {
        QcAnalyteLocal cl;
        QcAnalyteViewDO analyte;

        cl = local();
        for (int i = 0; i < man.count(); i++ ) {
            analyte = man.getAnalyteAt(i);
            analyte.setQcId(man.getQcId());
            cl.add(analyte);
        }

        return man;
    }

    public QcAnalyteManager update(QcAnalyteManager man) throws Exception {
        QcAnalyteLocal cl;
        QcAnalyteViewDO analyte;

        cl = local();
        for (int j = 0; j < man.deleteCount(); j++ )
            cl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            analyte = man.getAnalyteAt(i);

            if (analyte.getId() == null) {
                analyte.setQcId(man.getQcId());
                cl.add(analyte);
            } else {
                cl.update(analyte);
            }
        }

        return man;
    }

    public void validate(QcAnalyteManager man) throws Exception {
        ValidationErrorsList list;
        QcAnalyteViewDO resDO;
        Integer numId, dictId, titerId, typeId, entryId;
        int i;
        String value, fieldName;

        NumericRange nr;
        TiterRange tr;
        List<TiterRange> trList;
        List<NumericRange> nrList;
        List<Integer> dictList, resIdList;
        DictionaryLocal dl;
        QcAnalyteLocal rl;

        list = new ValidationErrorsList();
        value = null;
        dl = dictLocal();

        rl = local();

        dictId = (dl.fetchBySystemName("qc_analyte_dictionary")).getId();
        numId = (dl.fetchBySystemName("qc_analyte_numeric")).getId();
        titerId = (dl.fetchBySystemName("qc_analyte_titer")).getId();

        trList = new ArrayList<TiterRange>();
        nrList = new ArrayList<NumericRange>();
        dictList = new ArrayList<Integer>();

        for (i = 0; i < man.count(); i++ ) {
            dictList.clear();
            resIdList = new ArrayList<Integer>();

            resDO = man.getAnalyteAt(i);
            value = resDO.getValue();
            typeId = resDO.getTypeId();

            resIdList.add(resDO.getId());

            fieldName = meta.QC_ANALYTE.getValue();

            try {
                rl.validate(man.getAnalyteAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "QcAnalyteTable", i);
            }

            try {
                if (numId.equals(typeId)) {
                    nr = new NumericRange(value);
                    addNumericIfNoOverLap(nrList, nr);
                } else if (titerId.equals(typeId)) {
                    tr = new TiterRange(value);
                    addTiterIfNoOverLap(trList, tr);
                } else if (dictId.equals(typeId)) {
                    entryId = Integer.parseInt(value);
                    if (entryId == null)
                        throw new ParseException("illegalDictEntryException");

                    if ( !dictList.contains(entryId))
                        dictList.add(entryId);
                    else
                        throw new InconsistencyException("qcDictEntryNotUniqueException");
                }
            } catch (ParseException ex) {
                list.add(new TableFieldErrorException(ex.getMessage(), i, fieldName,
                                                      "QcAnalyteTable"));

            } catch (InconsistencyException ex) {
                list.add(new TableFieldErrorException(ex.getMessage(), i, fieldName,
                                                      "QcAnalyteTable"));

            }
        }

        if (list.size() > 0)
            throw list;
    }

    private QcAnalyteLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (QcAnalyteLocal)ctx.lookup("openelis/QcAnalyteBean/local");
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

    private void addTiterIfNoOverLap(List<TiterRange> trList, TiterRange tr)
                                                                            throws InconsistencyException {
        TiterRange lr;

        for (int i = 0; i < trList.size(); i++ ) {
            lr = trList.get(i);           
            if (lr.isOverlapping(tr))
                throw new InconsistencyException("qcTiterRangeOverlapException");
        }
        trList.add(tr);

    }

    private void addNumericIfNoOverLap(List<NumericRange> nrList, NumericRange nr)
                                                                                  throws InconsistencyException {
        NumericRange lr;

        for (int i = 0; i < nrList.size(); i++ ) {
            lr = nrList.get(i);
            if (lr.isOverlapping(nr))
                throw new InconsistencyException("qcNumRangeOverlapException");
        }
        nrList.add(nr);
    }
}
