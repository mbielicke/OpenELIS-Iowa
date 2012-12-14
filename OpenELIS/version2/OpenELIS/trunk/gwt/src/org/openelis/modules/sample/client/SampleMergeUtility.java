/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.StorageManager;
import org.openelis.manager.TestManager;

public class SampleMergeUtility {

    public static void mergeTests(SampleManager fromOrderMan, SampleManager quickEntryMan) throws Exception {
        int i, j, orderAnaIndex;
        boolean analysisFound;
        Integer testId, sequence;
        SampleItemManager fromOrderItemMan, quickEntryItemMan;
        SampleItemViewDO orderSampleItem, qeSampleItem;
        AnalysisManager orderAnaMan, qeAnaMan;        
        AnalysisViewDO orderAna, qeAna;
        ArrayList<Integer> indexes;
        HashMap<Integer, ArrayList<Integer>> sequenceMap;
        
        fromOrderItemMan = fromOrderMan.getSampleItems();
        quickEntryItemMan = quickEntryMan.getSampleItems();
        
        if (fromOrderItemMan.count() == 0) {
            for (i = 0; i < quickEntryItemMan.count(); i++) {
                orderSampleItem = fromOrderItemMan.getSampleItemAt(fromOrderItemMan.addSampleItem());
                qeSampleItem = quickEntryItemMan.getSampleItemAt(i);
                SampleDuplicateUtil.duplicateSampleItem(orderSampleItem, qeSampleItem);
                fromOrderItemMan.setSampleItemAt(orderSampleItem, i);
                orderSampleItem.setSampleId(qeSampleItem.getSampleId());
                fromOrderItemMan.setAnalysisAt(quickEntryItemMan.getAnalysisAt(i), i);
                duplicateStorage(fromOrderItemMan.getStorageAt(i) ,quickEntryItemMan.getStorageAt(i));
            }
            fromOrderMan.getSample().setNextItemSequence(quickEntryMan.getSample()
                                                                      .getNextItemSequence());
            return;
        }

        /*
         * The mapping between the test ids and the sequences of the items under
         * which they were added in the manager loaded from an order is created
         * so that there won't be a need to iterate over the items over and over
         * again for each test in the manager loaded through quick entry in
         * order to find out where the test is to be added in the resulting
         * manager.
         */
        sequenceMap = new HashMap<Integer, ArrayList<Integer>>();
        for (i = 0; i < fromOrderItemMan.count(); i++ ) {
            orderAnaMan = fromOrderItemMan.getAnalysisAt(i);
            for (j = 0; j < orderAnaMan.count(); j++ ) {
                orderAna = orderAnaMan.getAnalysisAt(j);
                /*
                 * since the data from this manager may have been showing on the
                 * screen before the merging was attempted, the test id could be
                 * null because the user may have blanked out the test of a row
                 * showing an analysis
                 */
                testId = orderAna.getTestId();
                if (testId != null) {
                    indexes = sequenceMap.get(testId);
                    if (indexes == null) {
                        indexes = new ArrayList<Integer>();
                        sequenceMap.put(testId, indexes);
                    }
                    indexes.add(i);
                }
            }
        }
        
        for (i = 0; i < quickEntryItemMan.count(); i++ ) {
            qeAnaMan = quickEntryItemMan.getAnalysisAt(i);
            
            if (i < fromOrderItemMan.count()) 
                duplicateStorage(fromOrderItemMan.getStorageAt(i), quickEntryItemMan.getStorageAt(i));
            
            for (j = 0; j < qeAnaMan.count(); j++ ) {
                qeAna = qeAnaMan.getAnalysisAt(j);
                /*
                 * since the data from this manager may have been showing on the
                 * screen before the merging was attempted, the test id could be
                 * null because the user may have blanked out the test of a row
                 * showing an analysis
                 */
                testId = qeAna.getTestId();
                if (testId == null)
                    continue;

                /*
                 * if the sequence for this test obtained from manager loaded
                 * through quick entry is the same as the one specified in the
                 * order then no action needs to be taken because the test would
                 * already have been added to the item with that sequence
                 */
                indexes = sequenceMap.get(testId);

                analysisFound = false;

                if (indexes != null) {
                    /*
                     * go through the tests added at each index 
                     */
                    for (Integer index : indexes) {
                        orderAnaMan = fromOrderItemMan.getAnalysisAt(index);
                        /*
                         * see if there's an analysis with this test id and a null
                         * or negative analysis id at this index
                         */
                        orderAnaIndex = getUnitializedByTestId(orderAnaMan, qeAna.getTestId());                        
                        
                        if (orderAnaIndex != -1) {
                            /*
                             * if such an analysis is found, then set the various
                             * parts of that analysis' manager like notes and qa
                             * events to be the ones from the analysis in the manager
                             * filled from quick entry 
                             */
                            orderAna = orderAnaMan.getAnalysisAt(orderAnaIndex);
                            setAnalysisInfo(j, orderAnaIndex, qeAnaMan, orderAnaMan);
                            orderAna.setId(qeAna.getId());
                            analysisFound = true;
                            break;
                        }
                    }
                }
                
                if ( !analysisFound) {
                    /*
                     * if the analysis isn't found at this index or if it isn't
                     * found anywhere in the manager filled from the order, then
                     * create a new analysis, add it to the sample item at this
                     * index or index zero and set the various parts of that analysis'
                     * manager like notes and qa events to be the ones from the
                     * analysis in the manager filled from quick entry                      
                     */
                    sequence = quickEntryItemMan.getSampleItemAt(i).getItemSequence();
                    if (sequence >= fromOrderItemMan.count())
                        sequence = 0;

                    orderAnaMan = fromOrderItemMan.getAnalysisAt(sequence);
                    orderAnaIndex = addAnalysis(orderAnaMan, qeAnaMan.getTestAt(j));
                    orderAna = orderAnaMan.getAnalysisAt(orderAnaIndex); 
                    setAnalysisInfo(j, orderAnaIndex, qeAnaMan, orderAnaMan);
                    orderAna.setId(qeAna.getId());
                }
            }
        }
    }


    private static void setAnalysisInfo(int qeAnaIndex, int orderAnaIndex,
                                          AnalysisManager qeAnaMan,  AnalysisManager orderAnaMan) throws Exception {
        orderAnaMan.setQAEventAt(qeAnaMan.getQAEventAt(qeAnaIndex), qeAnaIndex);
        orderAnaMan.setInternalNotes(qeAnaMan.getInternalNotesAt(qeAnaIndex), orderAnaIndex);
        orderAnaMan.setExternalNoteAt(qeAnaMan.getExternalNoteAt(qeAnaIndex), orderAnaIndex);
        orderAnaMan.setStorageAt(qeAnaMan.getStorageAt(qeAnaIndex), orderAnaIndex);
        orderAnaMan.setAnalysisUserAt(qeAnaMan.getAnalysisUserAt(qeAnaIndex), orderAnaIndex);
    }

    
    private static int getUnitializedByTestId(AnalysisManager orderAnaMan, Integer testId) {
        Integer id;
        AnalysisViewDO ana;
        
        for (int i = 0; i < orderAnaMan.count(); i++) {
            ana = orderAnaMan.getAnalysisAt(i);
            id = ana.getId();
            if ((id == null || id < 0) && DataBaseUtil.isSame(testId, ana.getTestId()))
                return i;            
        }
        return -1;
    }
    
    private static int addAnalysis(AnalysisManager orderAnaMan, TestManager testMan) throws Exception {
        int anaIndex;
        
        anaIndex = orderAnaMan.addAnalysis();

        /*
         * set the defaults e.g. section and unit and load the
         * results
         */
        orderAnaMan.setTestAt(testMan, anaIndex, false);
        
        return anaIndex;
    }
    
    private static void duplicateStorage(StorageManager orderItemStorageMan, StorageManager qeItemStorageMan) throws Exception {
        StorageViewDO orderItemStorage, qeItemStorage;
        
        for (int i = 0; i < qeItemStorageMan.count(); i++) {
            qeItemStorage = qeItemStorageMan.getStorageAt(i);
            orderItemStorage = new StorageViewDO();
            orderItemStorage.setReferenceTableId(qeItemStorage.getReferenceTableId());
            orderItemStorage.setStorageLocationId(qeItemStorage.getStorageLocationId());
            orderItemStorage.setStorageLocationName(qeItemStorage.getStorageLocationName());
            orderItemStorage.setStorageLocationLocation(qeItemStorage.getStorageLocationLocation());
            orderItemStorage.setStorageLocationParentStorageLocationName(qeItemStorage.getStorageLocationParentStorageLocationName());
            orderItemStorage.setStorageUnitDescription(qeItemStorage.getStorageUnitDescription());
            orderItemStorage.setCheckin(qeItemStorage.getCheckin());
            orderItemStorage.setCheckout(qeItemStorage.getCheckout());
            orderItemStorage.setSystemUserId(qeItemStorage.getSystemUserId());
            orderItemStorage.setUserName(qeItemStorage.getUserName());
            orderItemStorageMan.addStorage(orderItemStorage);
        }
    }
}