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

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.modules.sample.client.SampleItemAnalysisTreeTab.Action;

public class AnalysisTreeUtil {

    public void updateSampleItemRow(TreeDataItem sampleItemRow){
        SampleDataBundle data;
        SampleItemViewDO itemDO;

        data = (SampleDataBundle)sampleItemRow.data;
        itemDO = data.sampleItemDO;
        sampleItemRow.cells.get(0).value = itemDO.getItemSequence() + " - " +
                                      formatTreeString(itemDO.getContainer());
        sampleItemRow.cells.get(1).value = formatTreeString(itemDO.getTypeOfSample());
    }
    
    private String formatTreeString(String val) {
        if (val == null || "".equals(val))
            return "<>";

        return val.trim();
    }
    
    private void cancelAnalysisRow(TreeDataItem treeRow) {
        /*
        SampleDataBundle bundle;
        int index;
        AnalysisViewDO anDO;

        treeRow = itemsTree.getRow(selectedIndex);

        // update the tree row
        bundle = (SampleDataBundle)treeRow.data;
        itemsTree.setCell(selectedIndex, 1, analysisCancelledId);

        // update the analysis manager
        index = bundle.analysisManager.getIndex(bundle.analysisTestDO);
        anDO = bundle.analysisManager.getAnalysisAt(index);
        bundle.analysisManager.cancelAnalysisAt(index);
        
        // cleanup the other rows
        cleanupTestsWithPrep(anDO.getId());

        // update the sample manager status boolean and the tabs.
        // then redraw the tabs to make sure this change didn't change the
        // status
        manager.setHasReleasedCancelledAnalysis(true);
        ActionEvent.fire(this, Action.REFRESH_TABS, bundle);
        */
    }
}
