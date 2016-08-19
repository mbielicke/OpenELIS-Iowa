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
import java.util.HashSet;
import java.util.List;

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
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class SecondDataEntryBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;

    @EJB
    private UserCacheBean           userCache;

    private static final SampleMeta meta = new SampleMeta();

    public ArrayList<SecondDataEntryVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Integer prevSamId, currSamId, accession, userId;
        String domain;
        Query query;
        QueryBuilderV2 builder;
        SecondDataEntryVO data;
        List<Object[]> list;
        ArrayList<String> userNameList;
        ArrayList<SecondDataEntryVO> returnList;
        HashSet<Integer> userIds;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " + SampleMeta.ID + ", " + SampleMeta.ACCESSION_NUMBER + ", " +
                          SampleMeta.DOMAIN + ", " + SampleMeta.HISTORY_ID + ", " +
                          SampleMeta.HISTORY_SYSTEM_USER_ID);
        builder.constructWhere(fields);
        /*
         * sort by history id so that the history records are returned in the
         * order that the sample was added/updated
         */
        builder.setOrderBy(SampleMeta.ACCESSION_NUMBER + ", " + SampleMeta.HISTORY_ID);
        /*
         * fetched samples must be not-verified and not quick-entered anymore
         * i.e. they should have a particular domain
         */
        builder.addWhere(SampleMeta.STATUS_ID + " = " + Constants.dictionary().SAMPLE_NOT_VERIFIED);
        builder.addWhere(SampleMeta.DOMAIN + " != " + "'" + Constants.domain().QUICKENTRY + "'");
        builder.addWhere(SampleMeta.HISTORY_REFERENCE_TABLE_ID + " = " + Constants.table().SAMPLE);
        /*
         * fetch history records where the sample was either added or updated
         */
        builder.addWhere(SampleMeta.HISTORY_ACTIVITY_ID + " in (" + Constants.audit().ADD + ", " +
                         Constants.audit().UPDATE + ")");

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        prevSamId = null;
        returnList = new ArrayList<SecondDataEntryVO>();
        userNameList = null;
        data = null;
        /*
         * create SecondDataEntryVOs from the fetched data; there's only one VO
         * for each sample; so if a sample has been added/updated by multiple
         * users, their login names are combined into one VO
         */
        userIds = null;
        for (Object[] o : list) {
            currSamId = (Integer)o[0];
            accession = (Integer)o[1];
            domain = (String)o[2];
            userId = (Integer)o[4];

            if ( !currSamId.equals(prevSamId)) {
                userIds = new HashSet<Integer>();
                userNameList = new ArrayList<String>();
                data = new SecondDataEntryVO(currSamId, accession, domain, userNameList);
                returnList.add(data);
            }
            /*
             * the hashset makes sure that if a sample was added/updated
             * multiple times by a user, the user's login name is shown only
             * once for that sample
             */
            if (userId != null && !userIds.contains(userId)) {
                userNameList.add(userCache.getSystemUser(userId).getLoginName());
                userIds.add(userId);
            }

            prevSamId = currSamId;
        }

        return returnList;
    }
}