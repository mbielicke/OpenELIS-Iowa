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
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.TestManager;

public class SampleDataBundle {
    public enum Type {
        SAMPLE_ITEM, ANALYSIS
    };

    public Type                    type;
    public SampleItemManager       sampleItemManager;
    public SampleItemViewDO        sampleItemDO;
    public AnalysisManager         analysisManager;
    public AnalysisViewDO          analysisTestDO;
    public TestManager             testManager;
    public ArrayList<TableDataRow> sectionsDropdownModel;
    public ArrayList<TableDataRow> unitsDropdownModel;
    public ArrayList<TableDataRow> samplePrepDropdownModel;

    public SampleDataBundle() {

    }

    public SampleDataBundle(SampleItemManager sampleItemManager, SampleItemViewDO sampleItemDO) {
        this(sampleItemManager, sampleItemDO, null, null, null);
        type = Type.SAMPLE_ITEM;
    }

    public SampleDataBundle(SampleItemManager sampleItemManager, SampleItemViewDO sampleItemDO,
                            AnalysisManager analysisManager, AnalysisViewDO analysisTestDO, 
                            TestManager testManager) {
        this.sampleItemManager = sampleItemManager;
        this.sampleItemDO = sampleItemDO;
        this.analysisManager = analysisManager;
        this.analysisTestDO = analysisTestDO;
        this.testManager = testManager;
        type = Type.ANALYSIS;
    }
}

