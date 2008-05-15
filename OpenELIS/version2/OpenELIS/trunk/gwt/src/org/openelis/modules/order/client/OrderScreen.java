package org.openelis.modules.order.client;

import java.util.HashMap;

import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;

public class OrderScreen extends OpenELISScreenForm implements ClickListener, TabListener {
    
    private static boolean loaded = false;
    
    public static final String  INTERNAL          = "internal",
                                EXTERNAL          = "external",
                                KITS              = "kits";
    private static final HashMap XML_PATHS =
                                            new HashMap()   
                                            {  
                                                //Unnamed Block.  
                                                {  
                                                    put(INTERNAL, "/Forms/internalOrder.xsl");
                                                    put(EXTERNAL, "/Forms/externalOrder.xsl");
                                                    put(KITS, "/Forms/kitOrder.xsl");
                                                }
                                            };  
    
    public OrderScreen(DataObject[] args) {                
        super("org.openelis.modules.order.server.OrderService");
        
        HashMap hash = new HashMap();
        hash.put("xml", args[0]);
        
        getXMLData(hash);
    }

    public void onChange(Widget sender) {
        /*if (sender == atozButtons) {
            String action = atozButtons.buttonClicked.action;
            if (action.startsWith("query:")) {
                getOrganizations(action.substring(6, action.length()));
            }
        } else {
            super.onChange(sender);
        }*/
    }
    
    public void onClick(Widget sender) {
        // TODO Auto-generated method stub
        
    }
    
    public void afterDraw(boolean sucess) {
        loaded = true;
        
        setBpanel((ButtonPanel)getWidget("buttons"));
        super.afterDraw(sucess);
    }

    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        // TODO Auto-generated method stub
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        // TODO Auto-generated method stub
        
    }

}
