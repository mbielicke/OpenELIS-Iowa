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
package org.openelis.modules.richTextPopup.client;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.richtext.RichTextWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class RichTextPopupScreen extends OpenELISScreenForm<RichTextPopupForm,Query<TableDataRow<Integer>>> implements ClickListener{

    public HTML targetHtmlWidget;
    private RichTextWidget richText;
    
    public RichTextPopupScreen(HTML target) {
        super("org.openelis.modules.richTextPopup.server.RichTextPopupService");

        this.targetHtmlWidget = target;

        getScreen(new RichTextPopupForm());
    }
    public RichTextPopupScreen() {
        super("org.openelis.modules.richTextPopup.server.RichTextPopupService");

        getScreen(new RichTextPopupForm());
    }
        
    public void onClick(Widget sender) {

    }
    
    public void afterDraw(boolean sucess) {
        richText = (RichTextWidget)getWidget("richText");
        
        addCommandListener((ButtonPanel) getWidget("buttons"));
        ((ButtonPanel)getWidget("buttons")).addCommandListener(this);
        
        super.afterDraw(sucess);
        
        if(targetHtmlWidget.getHTML() != null)
            richText.setText(targetHtmlWidget.getHTML());
    }
    
    public void commit() {
        targetHtmlWidget.setHTML(richText.getText());
        
        window.close();
    }

    public void abort() {
        window.close();
    }
}