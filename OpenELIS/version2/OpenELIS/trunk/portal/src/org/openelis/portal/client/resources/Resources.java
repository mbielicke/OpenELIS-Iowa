package org.openelis.portal.client.resources;

import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

public interface Resources extends UIResources {
    
    public static final Resources INSTANCE = GWT.create(Resources.class);

    @Source("css/style.css")
    Style style();
    
    @Source({"css/icon.css","org/openelis/ui/resources/css/icon.css"})
    IconCSS icons();
    
    @Source("images/shl-logo.gif")
    ImageResource headerLogo();

}
