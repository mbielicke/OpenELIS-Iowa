package org.openelis.modules.main.client.resources;

import org.openelis.ui.resources.ButtonCSS;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface OpenELISResources extends ClientBundle {
	public static final OpenELISResources INSTANCE = GWT.create(OpenELISResources.class);
	
	@Source("css/style.css")
	@Import(ButtonCSS.class)
	Style style();
	
	@Source("images/topmenubg.gif")
	@ImageOptions(repeatStyle=RepeatStyle.Horizontal)
	ImageResource topMenuBG();
	
	@Source("images/app_background.png")
	@ImageOptions(repeatStyle=RepeatStyle.Both)
	ImageResource AppBackground();
	
	@Source("images/cog_edit.png")
	ImageResource EditSettings();

	@Source("images/preferencebuttonimage.gif")
	ImageResource preferenceIcon();
	
	@Source("images/logoutbuttonimage.gif")
	ImageResource logoutIcon();
	
	@Source("images/quickentrybuttonimage.gif")
	ImageResource quickEntryIcon();
	
	@Source("images/qcbuttonimage.gif")
	ImageResource QCIcon();
	
	@Source("images/fieldlookup.png")
	ImageResource trackingIcon();
	
	@Source("images/world_add.png")
	ImageResource worldAdd();
	
	@Source("images/house.png")
	ImageResource house();
	
	@Source("images/cup.png")
	ImageResource cup();
	
	@Source("images/addbuttonimage.gif")
	ImageResource add();
	
	@Source("images/providerbuttonimage.gif")
	ImageResource providerIcon();
	
	@Source("images/organizationbuttonimage.gif")
	ImageResource organizationIcon();
	
	@Source("images/worksheetcreationbuttonimage.gif")
	ImageResource worksheetCreationIcon();
	
	@Source("images/worksheetcompletionbuttonimage.gif")
	ImageResource worksheetCompletionIcon();
	
	@Source("images/addorcancelIconbuttonimage.gif")
	ImageResource addOrCancelIcon();
	
	@Source("images/reviewandreleasebuttonimage.gif")
	ImageResource reviewAndReleaseIcon();
	
	@Source("images/todobuttonimage.gif")
	ImageResource toDoIcon();
	
	@Source("images/labelforbuttonimage.gif")
	ImageResource labelForIcon();
	
	@Source("images/storagebuttonimage.gif")
	ImageResource storageIcon();
	
	@Source("images/orderButtonImage.gif")
	ImageResource orderIcon();
	
	@Source("images/fillOrderButtonImage.gif")
	ImageResource fillOrderIcon();
	
	@Source("images/shippingButtonImage.gif")
	ImageResource shippingIcon();
	
	@Source("images/inventoryReceiptButtonImage.gif")
	ImageResource inventoryReceiptIcon();
	
	@Source("images/inventorybuttonimage.gif")
	ImageResource inventoryIcon();
	
	@Source("images/testbuttonimage.gif")
	ImageResource testIcon();
	
	@Source("images/methodbuttonimage.gif")
	ImageResource methodIcon();
	
	@Source("images/calculator_link.png")
	ImageResource calculator();
	
	@Source("images/tick.png")
	ImageResource tick();
	
	@Source("images/group.png")
	ImageResource group();
	
	@Source("images/map.png")
	ImageResource map();
	
	@Source("images/dictionarybuttonimage.gif")
	ImageResource dictionaryIcon();
	
	@Source("images/lightbulb.png")
	ImageResource lightbulb();
	
	@Source("images/note.gif")
	ImageResource note();
	
	@Source("images/standardnotebuttonimagedisabled.gif")
	ImageResource noteDisabled();
	
	@Source("images/page.png")
	ImageResource page();
	
	@Source("images/package.png")
	ImageResource packageIcon();
	
	@Source("images/basket.png")
	ImageResource basket();
	
	@Source("images/instrumentbuttonimage.gif")
	ImageResource instrumentIcon();
	
	@Source("images/brick.png")
	ImageResource brick();
	
	@Source("images/history.png")
	ImageResource history();
	
	@Source("images/addrowbuttonimage.gif")
	ImageResource addRowIcon();
	
	@Source("images/addrowbuttonimagedisabled.gif")
	ImageResource addRowDisabledIcon();
	
	@Source("images/removerowbuttonimage.gif")
	ImageResource removeRowIcon();
	
	@Source("images/removerowbuttonimagedisabled.gif")
	ImageResource removeRowDisabledIcon();
	
    @Source("images/find.png")
    ImageResource findIcon();
    
    @Source("images/finddisabled.png")
    ImageResource findDisabledIcon();
    
    @Source("images/selectallbuttonimage.png")
    ImageResource selectAllIcon();
    
    @Source("images/selectallbuttonimagedisabled.png")
    ImageResource selectAllDisabledIcon();
    
    @Source("images/tablepopout.png")
    ImageResource popoutIcon();
    
    @Source("images/tablepopoutdisabled.png")
    ImageResource popoutDisabledIcon();
    
    @Source("images/fieldlookup.png")
    ImageResource fieldLookupIcon();
    
    @Source("images/fieldlookupdisabled.png")
    ImageResource fieldLookupDisabledIcon();
}
