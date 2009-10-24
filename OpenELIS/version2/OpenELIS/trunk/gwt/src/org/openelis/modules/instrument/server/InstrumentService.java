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
package org.openelis.modules.instrument.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameSerialNumberDO;
import org.openelis.domain.InstrumentLogDO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.DateField;
import org.openelis.gwt.common.data.deprecated.DropDownField;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.gwt.services.deprecated.AutoCompleteServiceInt;
import org.openelis.modules.instrument.client.InstrumentForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.remote.InstrumentRemote;
import org.openelis.remote.ScriptletRemote;
import org.openelis.remote.TestRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class InstrumentService implements
                              AppScreenFormServiceInt<InstrumentForm, Query<TableDataRow<Integer>>>,
                              AutoCompleteServiceInt {

    private static final int leftTableRowsPerPage = 20;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));

    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws Exception {
        List instNames;
        InstrumentRemote remote = (InstrumentRemote)EJBFactory.lookup("openelis/InstrumentBean/remote");
        try {
            instNames = remote.query(query.fields,
                                     query.page * leftTableRowsPerPage,
                                     leftTableRowsPerPage);
        } catch (LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        // fill the model with the query results
        int i = 0;
        if (query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
        while (i < instNames.size() && i < leftTableRowsPerPage) {
            IdNameSerialNumberDO resultDO = (IdNameSerialNumberDO)instNames.get(i);

            query.results.add(new TableDataRow<Integer>(resultDO.getId(),
                                                        new FieldType[] {new StringObject(resultDO.getName()),
                                                                         new StringObject(resultDO.getSerialNumber()),}));
            i++;
        }

        return query;
    }

    public InstrumentForm commitAdd(InstrumentForm rpc) throws Exception {
        InstrumentRemote remote;
        InstrumentViewDO instDO;
        Integer instId;
        List<InstrumentLogDO> logDOList;

        remote = (InstrumentRemote)EJBFactory.lookup("openelis/InstrumentBean/remote");
        instDO = getInstrumentDOFromRPC(rpc);
        logDOList = getInstrumentLogsFromRPC(rpc, null);
        try {
            instId = remote.updateInstrument(instDO, logDOList);
            instDO = remote.getInstrument(instId);
            setFieldsInRPC(rpc, instDO);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return rpc;
    }

    public InstrumentForm commitUpdate(InstrumentForm rpc) throws Exception {
        InstrumentRemote remote;
        InstrumentViewDO instDO;
        List<InstrumentLogDO> logDOList;

        remote = (InstrumentRemote)EJBFactory.lookup("openelis/InstrumentBean/remote");
        instDO = getInstrumentDOFromRPC(rpc);
        logDOList = getInstrumentLogsFromRPC(rpc, rpc.entityKey);

        try {
            remote.updateInstrument(instDO, logDOList);
            instDO = remote.getInstrument(rpc.entityKey);
            setFieldsInRPC(rpc, instDO);
        } catch (ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return rpc;
    }

    public InstrumentForm commitDelete(InstrumentForm rpc) throws Exception {
        return null;
    }

    public InstrumentForm abort(InstrumentForm rpc) throws Exception {
        InstrumentRemote remote;
        InstrumentViewDO instDO;
        List<InstrumentLogDO> logDOList;

        remote = (InstrumentRemote)EJBFactory.lookup("openelis/InstrumentBean/remote");
        instDO = remote.getInstrumentAndUnlock(rpc.entityKey,
                                               SessionManager.getSession()
                                                             .getId());
        setFieldsInRPC(rpc, instDO);

        logDOList = remote.getInstrumentLogs(rpc.entityKey);
        fillLogEntries(logDOList, rpc);

        return rpc;
    }

    public InstrumentForm fetch(InstrumentForm rpc) throws Exception {
        InstrumentRemote remote;
        InstrumentViewDO instDO;
        List<InstrumentLogDO> logDOList;

        remote = (InstrumentRemote)EJBFactory.lookup("openelis/InstrumentBean/remote");
        instDO = remote.getInstrument(rpc.entityKey);
        setFieldsInRPC(rpc, instDO);

        logDOList = remote.getInstrumentLogs(rpc.entityKey);
        fillLogEntries(logDOList, rpc);

        return rpc;
    }

    public InstrumentForm fetchForUpdate(InstrumentForm rpc) throws Exception {
        InstrumentRemote remote;
        InstrumentViewDO instDO;
        List<InstrumentLogDO> logDOList;

        remote = (InstrumentRemote)EJBFactory.lookup("openelis/InstrumentBean/remote");
        try {
            instDO = remote.getInstrumentAndLock(rpc.entityKey,
                                                 SessionManager.getSession()
                                                               .getId());
            setFieldsInRPC(rpc, instDO);
            logDOList = remote.getInstrumentLogs(rpc.entityKey);
            fillLogEntries(logDOList, rpc);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

        return rpc;
    }

    public InstrumentForm getScreen(InstrumentForm rpc) throws Exception {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/instrument.xsl");
        return rpc;
    }

    public TableDataModel getMatches(String cat,
                                     TableDataModel model,
                                     String match,
                                     HashMap<String, FieldType> params) throws Exception {
        AnalyteRemote aremote;
        TableDataModel<TableDataRow<Integer>> dataModel;
        List<IdNameDO> entries;
        List<TestMethodVO> tmlist;
        ScriptletRemote sremote;
        TestRemote tremote;

        dataModel = null;

        if ("scriptlet".equals(cat)) {
            sremote = (ScriptletRemote)EJBFactory.lookup("openelis/ScriptletBean/remote");
           // entries = sremote.findByName(match.trim() + "%",
                                                            // 10);
            //dataModel = getAutocompleteModel(entries);
        } else if ("testMethod".equals(cat)) {
            tremote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
            tmlist = tremote.fetchByName(match.trim() + "%", 10);
            dataModel = getTestMethodAutocompleteModel(tmlist);
        }

        return dataModel;
    }

    private TableDataModel<TableDataRow<Integer>> getAutocompleteModel(List<IdNameDO> entries) {
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {

            IdNameDO element = (IdNameDO)iter.next();
            Integer entryId = element.getId();
            String entryText = element.getName();

            TableDataRow<Integer> data = new TableDataRow<Integer>(entryId,
                                                                   new StringObject(entryText));
            dataModel.add(data);
        }

        return dataModel;
    }

    private TableDataModel<TableDataRow<Integer>> getTestMethodAutocompleteModel(List<TestMethodVO> entries) {
        TableDataModel<TableDataRow<Integer>> dataModel;
        TableDataRow<Integer> data;
        Integer itemId;
        TestMethodVO resultDO;
        String name, method, tdesc, mdesc;

        dataModel = new TableDataModel<TableDataRow<Integer>>();

        for (int i = 0; i < entries.size(); i++) {
            resultDO = (TestMethodVO)entries.get(i);

            itemId = resultDO.getTestId();
            name = resultDO.getTestName();
            method = resultDO.getMethodName();
            tdesc = resultDO.getTestDescription();
            mdesc = resultDO.getMethodDescription();

            data = new TableDataRow<Integer>(itemId,
                                             new FieldType[] {new StringObject(name),
                                                              new StringObject(method),
                                                              new StringObject(tdesc),
                                                              new StringObject(mdesc)});

            // add the dataset to the datamodel
            dataModel.add(data);
        }

        return dataModel;
    }

    private void setFieldsInRPC(InstrumentForm rpc, InstrumentViewDO instDO) {
        TableDataModel<TableDataRow<Integer>> model;
        Datetime dt;

        rpc.id.setValue(instDO.getId());
        rpc.name.setValue(instDO.getName());
        rpc.isActive.setValue(instDO.getIsActive());
        rpc.serialNumber.setValue(instDO.getSerialNumber());
        rpc.modelNumber.setValue(instDO.getModelNumber());
        rpc.location.setValue(instDO.getLocation());
        rpc.typeId.setValue(new TableDataRow<Integer>(instDO.getTypeId()));
        rpc.description.setValue(instDO.getDescription());

        dt = instDO.getActiveBegin();
        if (dt != null && dt.getDate() != null) {
            rpc.activeBegin.setValue(Datetime.getInstance(Datetime.YEAR,
                                                          Datetime.DAY,
                                                          dt.getDate()));
        }

        dt = instDO.getActiveEnd();
        if (dt != null && dt.getDate() != null) {
            rpc.activeEnd.setValue(Datetime.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        dt.getDate()));
        }

        model = new TableDataModel();
        if (instDO.getScriptletId() != null) {
            model.add(new TableDataRow<Integer>(instDO.getScriptletId(),
                                                new StringObject(instDO.getScriptletName())));
            rpc.scriptletId.setValue(model.get(0));
        }
        rpc.scriptletId.setModel(model);

    }

    private InstrumentViewDO getInstrumentDOFromRPC(InstrumentForm rpc) {
        InstrumentViewDO instDO;
        Datetime activeBegin, activeEnd;

        instDO = new InstrumentViewDO();

        instDO.setId(rpc.id.getValue());
        instDO.setName(rpc.name.getValue());
        instDO.setDescription(rpc.description.getValue());
        instDO.setModelNumber(rpc.modelNumber.getValue());
        instDO.setSerialNumber(rpc.serialNumber.getValue());
        instDO.setTypeId((Integer)rpc.typeId.getSelectedKey());
        instDO.setLocation(rpc.location.getValue());
        instDO.setIsActive(rpc.isActive.getValue());
        instDO.setScriptletId((Integer)rpc.scriptletId.getSelectedKey());

        activeBegin = rpc.activeBegin.getValue();
        if (activeBegin != null)
            instDO.setActiveBegin(activeBegin);

        activeEnd = rpc.activeEnd.getValue();
        if (activeEnd != null)
            instDO.setActiveEnd(activeEnd);

        return instDO;
    }

    private List<InstrumentLogDO> getInstrumentLogsFromRPC(InstrumentForm rpc,
                                                           Integer instId) {
        ArrayList<InstrumentLogDO> logDOList;
        InstrumentLogDO logDO;
        TableDataModel<TableDataRow<Integer>> model;
        TableDataRow<Integer> row;
        DropDownField<Integer> field;
        ArrayList<TableDataRow<Integer>> deletions;
        int i;
        Datetime activeBegin, activeEnd;

        model = rpc.logTable.getValue();
        logDOList = new ArrayList<InstrumentLogDO>();

        for (i = 0; i < model.size(); i++) {
            row = model.get(i);
            logDO = new InstrumentLogDO();
           // logDO.setDelete(false);
            logDO.setId(row.key);
            field = (DropDownField<Integer>)row.cells[0];
            logDO.setTypeId((Integer)field.getSelectedKey());

            logDO.setWorksheetId((Integer)row.cells[1].getValue());

            activeBegin = ((DateField)row.cells[2]).getValue();
            if (activeBegin != null)
                logDO.setEventBegin(activeBegin);

            activeEnd = ((DateField)row.cells[3]).getValue();
            if (activeEnd != null)
                logDO.setEventEnd(activeEnd);

            logDO.setText((String)row.cells[4].getValue());

            logDOList.add(logDO);
        }

        deletions = model.getDeletions();
        if (deletions != null) {
            for (i = 0; i < deletions.size(); i++) {
                row = deletions.get(i);
                logDO = new InstrumentLogDO();
                logDO.setId(row.key);
               // logDO.setDelete(true);
                logDOList.add(logDO);
            }
            deletions.clear();
        }

        return logDOList;
    }

    private void fillLogEntries(List<InstrumentLogDO> logDOList,
                                InstrumentForm form) {
        TableDataModel<TableDataRow<Integer>> tmodel;
        InstrumentLogDO logDO;
        TableDataRow<Integer> row;
        Datetime dt;

        tmodel = form.logTable.getValue();
        tmodel.clear();

        for (int i = 0; i < logDOList.size(); i++) {
            row = tmodel.createNewSet();
            logDO = logDOList.get(i);
            row.key = logDO.getId();

            row.cells[0].setValue(new TableDataRow<Integer>(logDO.getTypeId()));
            row.cells[1].setValue(logDO.getWorksheetId());

            dt = logDO.getEventBegin();
            row.cells[2].setValue(Datetime.getInstance(Datetime.YEAR,
                                                       Datetime.MINUTE,
                                                       dt.getDate()));

            dt = logDO.getEventEnd();
            if (dt != null && dt.getDate() != null) {
                row.cells[3].setValue(Datetime.getInstance(Datetime.YEAR,
                                                           Datetime.MINUTE,
                                                           dt.getDate()));
            }

            row.cells[4].setValue(logDO.getText());
            tmodel.add(row);
        }

    }

    private void setRpcErrors(ArrayList<Exception> exceptionList,
                              InstrumentForm form) {
        HashMap<String, AbstractField> map;
        String fieldName;
        int index;
        TableFieldErrorException exc;

        map = null;
        if (exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
        // we need to get the keys and look them up in the resource bundle for
        // internationalization
        for (int i = 0; i < exceptionList.size(); i++) {
            // if the error is inside the entries table
            if (exceptionList.get(i) instanceof TableFieldErrorException) {
                exc = (TableFieldErrorException)exceptionList.get(i);
                index = exc.getRowIndex();
                fieldName = exc.getFieldName();
                form.logTable.getField(index, fieldName)
                             .addError(openElisConstants.getString(exc.getMessage()));
            } else if (exceptionList.get(i) instanceof FieldErrorException) {
                map.get(((FieldErrorException)exceptionList.get(i)).getFieldName())
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                // if the error is on the entire form
            } else if (exceptionList.get(i) instanceof FormErrorException) {
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }
        }
        form.status = Form.Status.invalid;

    }
}
