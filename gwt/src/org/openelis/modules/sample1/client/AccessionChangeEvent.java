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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * The event used to inform the handler that the accession number of a sample is
 * to be changed. The new accession number and id of the sample are specified
 * through accession and sampleId respectively.
 */
public class AccessionChangeEvent extends GwtEvent<AccessionChangeEvent.Handler> {

    private static Type<AccessionChangeEvent.Handler> TYPE;
    private Integer                                   accession, sampleId;
    private Exception                                 error;

    public AccessionChangeEvent(Integer accession, Integer sampleId) {
        this.accession = accession;
        this.sampleId = sampleId;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<AccessionChangeEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<AccessionChangeEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<AccessionChangeEvent.Handler>();
        }
        return TYPE;
    }

    public static interface Handler extends EventHandler {
        public void onAccessionChange(AccessionChangeEvent event);
    }

    public Integer getAccession() {
        return accession;
    }

    public Integer getSampleId() {
        return sampleId;
    }
    
    public Exception getError() {
        return error;
    }
    
    public void setError(Exception error) {
        this.error = error;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onAccessionChange(this);
    }
}