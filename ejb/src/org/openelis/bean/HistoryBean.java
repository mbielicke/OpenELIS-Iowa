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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.HistoryVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.entity.History;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Stateless
@SecurityDomain("openelis")
public class HistoryBean  {

    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;

    @EJB
    private AnalysisBean            analysis;

    @EJB
    private AnalyteBean             analyte;

    @EJB
    private DictionaryCacheBean    dictionaryCache;

    @EJB
    private InventoryItemCacheBean inventoryItemCache;

    @EJB
    private MethodBean              method;

    @EJB
    private OrganizationBean        organization;

    @EJB
    private ProjectBean             project;

    @EJB
    private QaEventBean            qaevent;

    @EJB
    private SampleItemBean         sampleItem;

    @EJB
    private SectionCacheBean       sectionCache;

    @EJB
    private TestBean               test;

    @EJB
    private UserCacheBean           userCache;

    private static final Logger     log = Logger.getLogger("openelis");

    @SuppressWarnings("unchecked")
    public ArrayList<HistoryVO> fetchByReferenceIdAndTable(Integer referenceId,
                                                           Integer referenceTableId) throws Exception {
        Query query;
        SystemUserVO user;
        List<HistoryVO> list;
        HashMap<Integer, HashMap<Integer, String>> refTableMap;

        query = manager.createNamedQuery("History.FetchByReferenceIdAndTable");
        query.setParameter("referenceId", referenceId);
        query.setParameter("referenceTableId", referenceTableId);

        list = query.getResultList();
        refTableMap = new HashMap<Integer, HashMap<Integer, String>>();

        for (HistoryVO h : list) {
            if (h.getSystemUserId() != null) {
                user = userCache.getSystemUser(h.getSystemUserId());
                if (user != null)
                    h.setSystemUserLoginName(user.getLoginName());
            }
            h.setChanges(getChangesWithLabels(h.getChanges(), refTableMap));
        }
        return DataBaseUtil.toArrayList(list);
    }

    public void add(Integer referenceId, Integer referenceTableId, Integer activity,
                    String changes) throws Exception {
        History entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new History();
        entity.setReferenceId(referenceId);
        entity.setReferenceTableId(referenceTableId);
        entity.setTimestamp(new Date());
        entity.setActivityId(activity);
        entity.setSystemUserId(userCache.getId());
        entity.setChanges(changes);

        manager.persist(entity);
    }

    private String getChangesWithLabels(String changes,
                                        HashMap<Integer, HashMap<Integer, String>> refTableMap) {
        Integer refTable, refId;
        String label;
        HashMap<Integer, String> refIdMap;
        Document doc;
        Element root;
        NodeList nodes;
        Node parentNode, refIdNode, refTableNode;

        if (changes == null)
            return null;

        try {
            doc = XMLUtil.parse(changes);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to parse changes" + e);
            return null;
        }

        root = doc.getDocumentElement();
        nodes = root.getChildNodes();
        refTable = null;
        refId = null;

        for (int i = 0; i < nodes.getLength(); i++ ) {
            parentNode = nodes.item(i);

            refTableNode = parentNode.getAttributes().getNamedItem("refTable");
            refIdNode = parentNode.getFirstChild();
            if (refIdNode == null || refTableNode == null)
                continue;

            //
            // get the reference table and reference id from the changes
            //
            if ( !DataBaseUtil.isEmpty(refTableNode.getNodeValue()))
                refTable = Integer.parseInt(refTableNode.getNodeValue());

            if ( !DataBaseUtil.isEmpty(refIdNode.getNodeValue()))
                refId = Integer.parseInt(refIdNode.getNodeValue());
            /*
             * A mapping is created between reference table numbers and another
             * mapping which is between reference ids and the corresponding
             * labels for the records that the reference ids link to. Whenever
             * the label for a record from a given table is to be obtained it's
             * first looked up in the mapping and only when it's not found here,
             * is it fetched.
             */
            refIdMap = refTableMap.get(refTable);
            if (refIdMap == null) {
                refIdMap = new HashMap<Integer, String>();
                refTableMap.put(refTable, refIdMap);
            }
            label = refIdMap.get(refId);

            if (label == null) {
                label = DataBaseUtil.toString(getLabel(refTable, refId));
                refIdMap.put(refId, label);
            }
            //
            // replace the id with the label
            //
            refIdNode.setNodeValue(label);
        }

        try {
            return XMLUtil.toString(doc);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to convert the data back to xml", e);
            return null;
        }
    }

    private String getLabel(Integer table, Integer id) {
        InventoryItemDO item;
        SampleItemViewDO sitem;
        String cont;

        if (table == null || id == null)
            return null;

        try {
            if (Constants.table().ANALYSIS.equals(table)) {
                return getTestLabel(analysis.fetchById(id).getTestId());
            } else if (Constants.table().ANALYTE.equals(table)) {
                return analyte.fetchById(id).getName();
            } else if (Constants.table().DICTIONARY.equals(table)) {
                return getDictionaryLabel(id);
            } else if (Constants.table().INVENTORY_ITEM.equals(table)) {
                item = inventoryItemCache.getById(id);
                return DataBaseUtil.concatWithSeparator(item.getName(),
                                                        ", ",
                                                        getDictionaryLabel(item.getStoreId()));
            } else if (Constants.table().METHOD.equals(table)) {
                return method.fetchById(id).getName();
            } else if (Constants.table().ORGANIZATION.equals(table)) {
                return organization.fetchById(id).getName();
            } else if (Constants.table().PROJECT.equals(table)) {
                return project.fetchById(id).getName();
            } else if (Constants.table().QAEVENT.equals(table)) {
                return qaevent.fetchById(id).getName();
            } else if (Constants.table().SAMPLE_ITEM.equals(table)) {
                sitem = sampleItem.fetchById(id);
                cont = DataBaseUtil.trim(sitem.getContainer());
                return DataBaseUtil.concatWithSeparator(sitem.getItemSequence(),
                                                        " - ",
                                                        cont != null ? cont : "<>");
            } else if (Constants.table().SECTION.equals(table)) {
                return sectionCache.getById(id).getName();
            } else if (Constants.table().TEST.equals(table)) {
                return getTestLabel(id);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to look up record with reference table: " +
                                  table + " and reference id: " + id, e);
        }
        return id.toString();
    }

    private String getDictionaryLabel(Integer id) throws Exception {
        return dictionaryCache.getById(id).getEntry();
    }

    private String getTestLabel(Integer id) throws Exception {
        TestViewDO t;

        t = test.fetchById(id);
        return DataBaseUtil.concatWithSeparator(t.getName(), ", ", t.getMethodName());
    }
}