package org.openelis.web.modules.main.client.resources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

public interface Resources extends ClientBundle {
    
    public static final Resources INSTANCE = GWT.create(Resources.class);

    @Source("css/style.css")
    Style style();
    
    @Source("images/buttonbarbg.gif")
    ImageResource formTitleBG();
    

}
