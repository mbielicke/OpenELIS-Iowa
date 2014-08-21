
<!--
Exhibit A - UIRF Open-source Based Public Software License.
  
The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu
  
Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.
  
The Original Code is OpenELIS code.
  
The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.
  
Contributor(s): ______________________________________.
  
Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
  -->

<xsl:stylesheet
  version="1.0"
  extension-element-prefixes="resource"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:service="xalan://org.openelis.gwt.server.ServiceUtils"
  xmlns:so="xalan://org.openelis.gwt.common.ModulePermission">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="service">
    <xalan:script lang="javaclass" src="xalan://org.openelis.gwt.server.ServiceUtils" />
  </xalan:component>
  <xalan:component prefix="so">
    <xalan:script lang="javaclass" src="xalan://org.openelis.gwt.common.ModulePermission" />
  </xalan:component>
  <xsl:variable name="language">
    <xsl:value-of select="doc/locale" />
  </xsl:variable>
  <xsl:variable name="props">
    <xsl:value-of select="doc/props" />
  </xsl:variable>
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  <xsl:template match="doc">
    <screen>
      <VerticalPanel style="AppBackground">
        <AbsolutePanel style="topMenuBar">
          <menuBar>
            <menu display="{resource:getString($constants,'application')}">
              <menuItem key="preference" description="{resource:getString($constants,'preferenceDescription')}" enabled="false" icon="preferenceIcon" display="{resource:getString($constants,'preference')}" />
              <menuItem key="FavoritesMenu" description="{resource:getString($constants,'favoritesMenuDescription')}" enabled="true" icon="favoritesMenuIcon" display="{resource:getString($constants,'favoritesMenu')}" />
              <menuItem key="Logout" description="{resource:getString($constants,'logoutDescription')}" icon="logoutIcon" display="{resource:getString($constants,'logout')}" />
            </menu>
            <menu display="{resource:getString($constants,'sample')}">
				 <code>if(OpenELIS.getSystemUserPermission().hasModule("quickentry","SELECT")){</code>
                <menuItem key="quickEntry" description="{resource:getString($constants,'quickEntryDescription')}" icon="quickEntryIcon" display="{resource:getString($constants,'quickEntry')}" />
				<code>}</code>
                <menuItem key="tracking" description="{resource:getString($constants,'trackingDescription')}" icon="trackingIcon" display="{resource:getString($constants,'tracking')}" />
                <separator/>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("sampleenvironmental","SELECT")){</code>
                <menuItem key="environmentalSampleLogin" description="{resource:getString($constants,'environmentalSampleLoginDescription')}" icon="environmentalSampleLoginIcon" display="{resource:getString($constants,'environmentalSampleLogin')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("sampleprivatewell","SELECT")){</code>
                <menuItem key="privateWellWaterSampleLogin" description="{resource:getString($constants,'privateWellWaterSampleLoginDescription')}" icon="privateWellWaterSampleLoginIcon" display="{resource:getString($constants,'privateWellWaterSampleLogin')}" />
				<code>}</code>
				<code>if(OpenELIS.getSystemUserPermission().hasModule("samplesdwis","SELECT")){</code>
				<menuItem key="sdwisSampleLogin" description="{resource:getString($constants,'sdwisSampleLoginDescription')}" icon="sdwisSampleLoginIcon" display="{resource:getString($constants,'sdwisSampleLogin')}" />
                <code>}</code>
                <menuItem key="clinicalSampleLogin" description="{resource:getString($constants,'clinicalSampleLoginDescription')}" enabled="false" icon="clinicalSampleLoginIcon" display="{resource:getString($constants,'clinicalSampleLogin')}" />
                <menuItem key="newbornScreeningSampleLogin" description="{resource:getString($constants,'newbornScreeningSampleLoginDescription')}" enabled="false" icon="newbornScreeningSampleLoginIcon" display="{resource:getString($constants,'newbornScreeningSampleLogin')}" />
                <menuItem key="animalSampleLogin" description="{resource:getString($constants,'animalSampleLoginDescription')}" enabled="false" icon="animalSampleLoginIcon" display="{resource:getString($constants,'animalSampleLogin')}" />
                <menuItem key="ptSampleLogin" description="{resource:getString($constants,'ptSampleLoginDescription')}" enabled="false" icon="ptSampleLoginIcon" display="{resource:getString($constants,'ptSampleLogin')}" />
                <separator/>
                <menuItem key="project" description="{resource:getString($constants,'projectDescription')}" icon="projectIcon" display="{resource:getString($constants,'project')}" />
                <code>if(OpenELIS.getSystemUserPermission().hasModule("provider","SELECT")){</code>
                <menuItem key="provider" description="{resource:getString($constants,'providerDescription')}" icon="providerIcon" display="{resource:getString($constants,'provider')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("organization","SELECT")){</code>
                <menuItem key="organization" description="{resource:getString($constants,'organizationDescription')}" icon="organizationIcon" display="{resource:getString($constants,'organization')}" />
                <code>}</code>
            </menu>
            <menu display="{resource:getString($constants,'analysis')}">
				<code>if(OpenELIS.getSystemUserPermission().hasModule("worksheet","ADD")){</code>
				<menuItem key="worksheetCreation" description="{resource:getString($constants,'worksheetCreationDescription')}" icon="worksheetCreationIcon" display="{resource:getString($constants,'worksheetCreation')}"/>
				<code>}</code>
				<code>if(OpenELIS.getSystemUserPermission().hasModule("worksheet","SELECT")){</code>
				<menuItem key="worksheetCompletion" description="{resource:getString($constants,'worksheetCompletionDescription')}"	icon="worksheetCompletionIcon" display="{resource:getString($constants,'worksheetCompletion')}"/>
				<code>}</code>
                <menuItem key="addOrCancel" description="{resource:getString($constants,'addOrCancelDescription')}" enabled="false" icon="addOrCancelIcon" display="{resource:getString($constants,'addOrCancel')}" />
                <menuItem key="reviewAndRelease" description="{resource:getString($constants,'reviewAndReleaseDescription')}" icon="reviewAndReleaseIcon" display="{resource:getString($constants,'reviewAndRelease')}" />
                <menuItem key="toDo" description="{resource:getString($constants,'toDoDescription')}" enabled="false" icon="toDoIcon" display="{resource:getString($constants,'toDo')}" />
                <menuItem key="labelFor" description="{resource:getString($constants,'labelForDescription')}" enabled="false" icon="labelForIcon" display="{resource:getString($constants,'labelFor')}" />
                <code>if(OpenELIS.getSystemUserPermission().hasModule("storage","SELECT")){</code>
                <menuItem key="storage" description="{resource:getString($constants,'storageDescription')}" icon="storageIcon" display="{resource:getString($constants,'storage')}" />
                <code>}</code>
                <menuItem key="QC" description="{resource:getString($constants,'QCDescription')}" icon="QCIcon" display="{resource:getString($constants,'QC')}" />
            </menu>
            <menu display="{resource:getString($constants,'inventoryOrder')}">
                <code>if(OpenELIS.getSystemUserPermission().hasModule("internalorder","SELECT")){</code>
                <menuItem key="internalOrder" description="{resource:getString($constants,'internalOrderDescription')}" icon="internalOrderIcon" display="{resource:getString($constants,'internalOrder')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("vendororder","SELECT")){</code>
                <menuItem key="vendorOrder" description="{resource:getString($constants,'vendorOrderDescription')}" icon="vendorOrderIcon" display="{resource:getString($constants,'vendorOrder')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("sendoutorder","SELECT")){</code>
                <menuItem key="kitOrder" description="{resource:getString($constants,'kitOrderDescription')}" icon="kitOrderIcon" display="{resource:getString($constants,'kitOrder')}" />
                <code>}</code>
                <separator/>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("fillorder","SELECT")){</code>
                <menuItem key="fillOrder" description="{resource:getString($constants,'fillOrderDescription')}" icon="fillOrderIcon" display="{resource:getString($constants,'fillOrder')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("shipping","SELECT")){</code>
                <menuItem key="shipping" description="{resource:getString($constants,'shippingDescription')}" icon="shippingIcon" display="{resource:getString($constants,'shipping')}" />
                <code>}</code>
                <separator/>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("buildkits","SELECT")){</code>
                <menuItem key="buildKits" description="{resource:getString($constants,'buildKitsDescription')}" icon="buildKitsIcon" display="{resource:getString($constants,'buildKits')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("inventorytransfer","SELECT")){</code>
                <menuItem key="inventoryTransfer" description="{resource:getString($constants,'inventoryTransferDescription')}" icon="inventoryTransferIcon" display="{resource:getString($constants,'inventoryTransfer')}" />
                <code>}</code>
                <separator/>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("inventoryreceipt","SELECT")){</code>
                <menuItem key="inventoryReceipt" description="{resource:getString($constants,'inventoryReceiptDescription')}" icon="inventoryReceiptIcon" display="{resource:getString($constants,'inventoryReceipt')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("inventoryadjustment","SELECT")){</code>
                <menuItem key="inventoryAdjustment" description="{resource:getString($constants,'inventoryAdjustmentDescription')}" icon="inventoryAdjustmentIcon" display="{resource:getString($constants,'inventoryAdjustment')}" />                
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("inventoryitem","SELECT")){</code>
                <menuItem key="inventoryItem" description="{resource:getString($constants,'inventoryItemDescription')}" icon="inventoryItemIcon" display="{resource:getString($constants,'inventoryItem')}" />
                <code>}</code>
            </menu>
            <menu display="{resource:getString($constants,'maintenance')}">
                <code>if(OpenELIS.getSystemUserPermission().hasModule("test","SELECT")){</code>
                <menuItem key="test" description="{resource:getString($constants,'testDescription')}" icon="testIcon" display="{resource:getString($constants,'test')}" />
                <code>}</code>
                <menuItem key="method" description="{resource:getString($constants,'methodDescription')}" icon="methodIcon" display="{resource:getString($constants,'method')}" />
                <code>if(OpenELIS.getSystemUserPermission().hasModule("panel","SELECT")){</code>
                <menuItem key="panel" description="{resource:getString($constants,'panelDescription')}" icon="panelIcon" display="{resource:getString($constants,'panel')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("qaevent","SELECT")){</code>
                <menuItem key="QAEvent" description="{resource:getString($constants,'QAEventDescription')}" icon="QAEventIcon" display="{resource:getString($constants,'QAEvent')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("section","SELECT")){</code>
                <menuItem key="labSection" description="{resource:getString($constants,'labSectionDescription')}" icon="labSectionIcon" display="{resource:getString($constants,'labSection')}" />
                <code>}</code>
                <separator/>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("analyte","SELECT")){</code>
                <menuItem key="analyte" description="{resource:getString($constants,'analyteDescription')}" icon="analyteIcon" display="{resource:getString($constants,'analyte')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("dictionary","SELECT")){</code>
                <menuItem key="dictionary" description="{resource:getString($constants,'dictionaryDescription')}" icon="dictionaryIcon" display="{resource:getString($constants,'dictionary')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("auxiliary","SELECT")){</code>
                <menuItem key="auxiliaryPrompt" description="{resource:getString($constants,'auxiliaryPromptDescription')}" icon="auxiliaryPromptIcon" display="{resource:getString($constants,'auxiliaryPrompt')}" />
                <code>}</code>
                <separator/>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("label","SELECT")){</code>
                <menuItem key="label" description="{resource:getString($constants,'labelDescription')}" icon="labelIcon" display="{resource:getString($constants,'label')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("standardnote","SELECT")){</code>
                <menuItem key="standardNote" description="{resource:getString($constants,'standardNoteDescription')}" icon="standardNoteIcon" display="{resource:getString($constants,'standardNote')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("testtrailer","SELECT")){</code>
                <menuItem key="trailerForTest" description="{resource:getString($constants,'trailerForTestDescription')}" icon="trailerForTestIcon" display="{resource:getString($constants,'trailerForTest')}" />
                <code>}</code>
                <separator/>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("storageunit","SELECT")){</code>
                <menuItem key="storageUnit" description="{resource:getString($constants,'storageUnitDescription')}" icon="storageUnitIcon" display="{resource:getString($constants,'storageUnit')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("storagelocation","SELECT")){</code>
                <menuItem key="storageLocation" description="{resource:getString($constants,'storageLocationDescription')}" icon="storageLocationIcon" display="{resource:getString($constants,'storageLocation')}" />
                <code>}</code>
                <separator/>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("instrument","SELECT")){</code>
                <menuItem key="instrument" description="{resource:getString($constants,'instrumentDescription')}" icon="instrumentIcon" display="{resource:getString($constants,'instrument')}" />
                <code>}</code>
                <separator/>
                <menuItem key="scriptlet" description="{resource:getString($constants,'scriptletDescription')}" enabled="false" icon="scriptletIcon" display="{resource:getString($constants,'scriptlet')}" />
                <code>if(OpenELIS.getSystemUserPermission().hasModule("systemvariable","SELECT")){</code>
                <menuItem key="systemVariable" description="{resource:getString($constants,'systemVariableDescription')}" icon="systemVariableIcon" display="{resource:getString($constants,'systemVariable')}" />
                <code>}</code>
                <code>if(OpenELIS.getSystemUserPermission().hasModule("pws","SELECT")){</code>
                <menuItem key="pws" description="{resource:getString($constants,'pwsInformationDescription')}" icon="sdwisSampleLoginIcon" display="{resource:getString($constants,'pwsInformation')}" />
                <code>}</code>
            </menu>
           <menu display="{resource:getString($constants,'report')}">
                <menu  description="" icon="" display="{resource:getString($constants,'login')}">
	                <code>if(OpenELIS.getSystemUserPermission().hasModule("r_loginlabel","SELECT")){</code>
                    <menuItem key="sampleLoginLabelReport" description="" icon="" enabled="true" display="{resource:getString($constants,'loginBarcode')}" />
                    <code>}</code>
	                <code>if(OpenELIS.getSystemUserPermission().hasModule("r_loginlabelrep","SELECT")){</code>
                    <menuItem key="sampleLoginLabelAdditionalReport" description="" icon="" enabled="true" display="{resource:getString($constants,'loginBarcodeAdd')}" />
					<code>}</code>                    
                    <menuItem key="verificationReport" description="" icon="" enabled="true" display="{resource:getString($constants,'verificationReport')}" />
                    <menuItem key="testRequestFormReport" description="" icon="" enabled="false" display="{resource:getString($constants,'TRFReport')}" />
                </menu>
                <menu description="" icon="" display="{resource:getString($constants,'reference')}">
                    <menuItem key="testReport" description="" icon="" enabled="true" display="{resource:getString($constants,'testReport')}" />
                    <menuItem key="qaEventReport" description="" icon="" enabled="false" display="{resource:getString($constants,'QAEventReport')}" />
                    <menuItem key="organizationRef" description="" icon="" enabled="false" display="{resource:getString($constants,'organization')}" />
                </menu>
                <menu description="" icon="" display="{resource:getString($constants,'summary')}">
                    <menuItem key="sampleInhouseReport" description="" icon="" enabled="false" display="{resource:getString($constants,'sampleInhouseReport')}" />
	                <menuItem key="sampleDataExport" description="" icon="" enabled="false" display="{resource:getString($constants,'sampleDataExport')}" />
                    <menuItem key="QAByOrganization" description="" icon="" enabled="false" display="{resource:getString($constants,'QAByOrganization')}" />
                    <menuItem key="testCountByFacility" description="" icon="" enabled="false" display="{resource:getString($constants,'testCountByFacility')}" />
                    <menuItem key="turnaround" description="" icon="" enabled="false" display="{resource:getString($constants,'turnaround')}" />
                    <menuItem key="finalReport" description="" icon="" enabled="true" display="{resource:getString($constants,'finalReport')}" />
                </menu>
            </menu>
          </menuBar>
        </AbsolutePanel>
        <HorizontalPanel>
          <VerticalPanel key="favoritesPanel" height="100%" width="230px" style="favoritesMenuContainer" visible="false">
            <HorizontalPanel width="100%" height="20px" style="FavHeader">
              <text style="ScreenWindowLabel">Favorites</text>
              <widget halign="right">
                <button key="EditFavorites">
                  <AbsolutePanel style="EditSettings" />
                </button>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
          <browser key="browser" sizeToWindow="true" />
        </HorizontalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
