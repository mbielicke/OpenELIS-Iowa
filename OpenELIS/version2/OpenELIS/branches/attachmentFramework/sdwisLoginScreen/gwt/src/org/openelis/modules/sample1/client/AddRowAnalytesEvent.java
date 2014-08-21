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
package org.openelis.modules.sample1.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.TestAnalyteViewDO;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This class is used to notify the handler that one or more row analytes are to
 * be added at the given indexes, to the results of the specified analysis
 */
public class AddRowAnalytesEvent extends GwtEvent<AddRowAnalytesEvent.Handler> {

    private static Type<AddRowAnalytesEvent.Handler> TYPE;
    private AnalysisViewDO                          analysis;
    private ArrayList<TestAnalyteViewDO>            analytes;
    private ArrayList<Integer>                      indexes;

    public AddRowAnalytesEvent(AnalysisViewDO analysis, ArrayList<TestAnalyteViewDO> analytes,
                              ArrayList<Integer> indexes) {
        this.analysis = analysis;
        this.analytes = analytes;
        this.indexes = indexes;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<AddRowAnalytesEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<AddRowAnalytesEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<AddRowAnalytesEvent.Handler>();
        }
        return TYPE;
    }

    public AnalysisViewDO getAnalysis() {
        return analysis;
    }

    public ArrayList<TestAnalyteViewDO> getAnalytes() {
        return analytes;
    }

    public ArrayList<Integer> getIndexes() {
        return indexes;
    }

    public static interface Handler extends EventHandler {
        public void onAddRowAnalytes(AddRowAnalytesEvent event);
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onAddRowAnalytes(this);
    }
}