package org.openelis.portal.client.resources;

import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends UIResources {

    public static final Resources INSTANCE = GWT.create(Resources.class);

    @Source("css/style.css")
    Style style();

    @Source("css/table.css")
    TableCSS portalTable();

    @Source({"css/icon.css", "org/openelis/ui/resources/css/icon.css"})
    IconCSS icon();

    @Source("css/weblink.css")
    WebLinkCSS webLinkCss();

    @Source("images/shl-logo.gif")
    ImageResource headerLogo();

    @Source("images/help.png")
    ImageResource helpImage();

    @Source("css/portalButton.css")
    PortalButton portalButton();

    @Source("css/menuButton.css")
    MenuButton menuButton();

    @Source("images/cross.png")
    ImageResource removeImage();

    @Source("images/report.png")
    ImageResource reportImage();

    @Source("images/email.png")
    ImageResource emailImage();

    @Source("images/table.png")
    ImageResource spreadsheetImage();

    @Source("images/arrow_undo.png")
    ImageResource reset();

    @Source("images/form.png")
    ImageResource formImage();

    @Source("images/status.png")
    ImageResource statusImage();

    @Source("images/logoutbuttonimage.gif")
    ImageResource logoutImage();
}
