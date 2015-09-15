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

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.SecondDataEntryVO;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.util.XMLUtil;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.ReportUtil;
import org.w3c.dom.Document;

@Stateless
@SecurityDomain("openelis")
public class SecondDataEntryBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;

    @EJB
    private UserCacheBean           userCache;

    private static final SampleMeta meta = new SampleMeta();

    private static final Logger     log  = Logger.getLogger("openelis");

    public ArrayList<SecondDataEntryVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        SystemUserVO user;
        List<SecondDataEntryVO> list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.SecondDataEntryVO(" +
                          SampleMeta.getId() + ", " + SampleMeta.getAccessionNumber() + ", " +
                          SampleMeta.getHistorySystemUserId() + ", " + "''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(SampleMeta.getAccessionNumber());

        /*
         * fetched samples must be not-verified and not quick-entered anymore
         * i.e. they should have a particular domain
         */
        builder.addWhere(SampleMeta.getStatusId() + " = " +
                         Constants.dictionary().SAMPLE_NOT_VERIFIED);
        builder.addWhere(SampleMeta.getDomain() + " != " + "'" + Constants.domain().QUICKENTRY +
                         "'");

        builder.addWhere(SampleMeta.getHistoryReferenceTableId() + " = " + Constants.table().SAMPLE);

        /*
         * fetch history records where the sample was either added or updated
         */
        builder.addWhere(SampleMeta.getHistoryActivityId() + " in (" + 1 + ", " + 2 + ")");

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        for (SecondDataEntryVO h : list) {
            if (h.getHistorySystemUserId() != null) {
                user = userCache.getSystemUser(h.getHistorySystemUserId());
                if (user != null)
                    h.setHistorySystemUserLoginName(user.getLoginName());
            }
        }
        return DataBaseUtil.toArrayList(list);
    }

    public ReportStatus getFields(String systemVariableName) throws Exception {
        String url;
        ReportStatus status;
        Document doc;

        url = ReportUtil.getSystemVariableValue(systemVariableName);
        if (url == null)
            throw new InconsistencyException("System variable " + "'" + systemVariableName +
                                             "' not present.");
        /*
         * the convert the contents of the file to an xml string because the
         * front-end can only work with that and not the file itself
         */
        doc = XMLUtil.load(url);
        status = new ReportStatus();
        status.setMessage(XMLUtil.toString(doc));

        return status;
    }
}