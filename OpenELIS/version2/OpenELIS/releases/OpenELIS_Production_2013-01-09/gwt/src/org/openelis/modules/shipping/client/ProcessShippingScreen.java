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
package org.openelis.modules.shipping.client;


import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class ProcessShippingScreen extends Screen implements HasActionHandlers<ProcessShippingScreen.Action> {

    protected TextBox barcode;
    protected ProcessShippingScreen screen; 
    
    private String    previousCode;       
    
    public enum Action {
        SHIPPING , TRACKING 
    };
    
    public ProcessShippingScreen() throws Exception {
       super((ScreenDefInt)GWT.create(ProcessShippingDef.class)); 
       screen = this;
       initialize();
       setState(State.DEFAULT);
    }       
    
    private void initialize() {
        barcode = (TextBox)def.getWidget("barcode");
        addScreenHandler(barcode, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                processShipping(event.getValue());                                                     
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                barcode.enable(true);
            }
        }); 
        
        /*
         * We add this handler here because the usual practice of setting 
         * tab = "key, key" in the xsl file, to specify the widget that gets the
         * focus when tab is pressed is not working in this case, probably because
         * both keys are for the same widget and that widget i.e. this textbox
         * is the only editable widget on the screen. So if we don't do this then
         * once the textbox loses focus, it doesn't gain focus unless it is
         * clicked into.   
         */
        barcode.addBlurHandler(new BlurHandler() {            
            public void onBlur(BlurEvent event) {
                setFocus(barcode);                
            }
        });
    }
    
    public void reset() {
        setFocus(barcode);
        barcode.setValue(null);
        previousCode = null;
        window.setDone(consts.get("done"));
    }

    public HandlerRegistration addActionHandler(ActionHandler<ProcessShippingScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
    
    private void processShipping(String code) {
        Integer id;        
        
        if (DataBaseUtil.isEmpty(code))             
            return;        
        
        /*
         * We don't allow entering a tracking number before a shipping
         * record's barcode has been entered. We also have to keep track 
         * of the previously scanned shipping record.  
         */
        if (!code.startsWith("SH")) {
            if (previousCode == null) {
                window.setError(consts.get("enterShippingBeforeTracking"));
                setFocus(barcode);
                barcode.setValue(null);
                return;
            }
            window.clearStatus();
            //
            // its a tracking number
            //
            ActionEvent.fire(screen, Action.TRACKING, code);
        } else if (!DataBaseUtil.isSame(previousCode, code)) {                   
            //                    
            // its the id of a shipping record
            //
            try {
                id = Integer.valueOf(code.substring(2));                                            
                previousCode = code;
                window.clearStatus();
                ActionEvent.fire(screen, Action.SHIPPING, id);
            } catch (NumberFormatException e) {
                window.setError(consts.get("enterValidBarcode"));                        
            }                     
        }        
        barcode.setValue(null);        
    }        
}
