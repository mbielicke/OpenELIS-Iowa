
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
    <screen id="main">
      <VerticalPanel style="AppBackground">
        <AbsolutePanel style="topMenuBar">
          <menuPanel layout="horizontal" style="topBarItemHolder">
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'application')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
                <menuItem key="preference" description="{resource:getString($constants,'preferenceDescription')}" enable="false" icon="preferenceIcon" label="{resource:getString($constants,'preference')}" />
                <menuItem key="FavoritesMenu" description="{resource:getString($constants,'favoritesMenuDescription')}" enable="false" icon="favoritesMenuIcon" label="{resource:getString($constants,'favoritesMenu')}" />
                <menuItem key="Logout" description="{resource:getString($constants,'logoutDescription')}" icon="logoutIcon" label="{resource:getString($constants,'logout')}" />
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'label')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
	            <code>if(UserCache.getPermission().hasModule("r_loginlabel","SELECT")){</code>
	            <menuItem key="sampleLoginLabelReport" description="" icon="" enable="true" label="{resource:getString($constants,'loginBarcode')}" />
	            <code>}</code>
	            <code>if(UserCache.getPermission().hasModule("r_loginlabelrep","SELECT")){</code>
	            <menuItem key="sampleLoginLabelAdditionalReport" description="" icon="" enable="true" label="{resource:getString($constants,'loginBarcodeAdd')}" />
				<code>}</code>                    
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'sample')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">

				<code>if(UserCache.getPermission().hasModule("quickentry","SELECT")){</code>
                <menuItem key="quickEntry" description="{resource:getString($constants,'quickEntryDescription')}" enable="true" icon="quickEntryIcon" label="{resource:getString($constants,'quickEntry')}" />
				<code>}</code>
				<code>if(UserCache.getPermission().hasModule("verification","SELECT")){</code>
                <menuItem key="verification" description="{resource:getString($constants,'verificationDescription')}" enable="true" icon="QCIcon" label="{resource:getString($constants,'verification')}" />
                <code>}</code>
				<code>if(UserCache.getPermission().hasModule("sampletracking","SELECT")){</code>
                <menuItem key="tracking" description="{resource:getString($constants,'trackingDescription')}" enable="true" icon="trackingIcon" label="{resource:getString($constants,'tracking')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(UserCache.getPermission().hasModule("sampleenvironmental","SELECT")){</code>
                <menuItem key="environmentalSampleLogin" description="{resource:getString($constants,'environmentalSampleLoginDescription')}" icon="environmentalSampleLoginIcon" label="{resource:getString($constants,'environmentalSampleLogin')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("sampleprivatewell","SELECT")){</code>
                <menuItem key="privateWellWaterSampleLogin" description="{resource:getString($constants,'privateWellWaterSampleLoginDescription')}" enable="true" icon="privateWellWaterSampleLoginIcon" label="{resource:getString($constants,'privateWellWaterSampleLogin')}" />
				<code>}</code>
				<code>if(UserCache.getPermission().hasModule("samplesdwis","SELECT")){</code>
				<menuItem key="sdwisSampleLogin" description="{resource:getString($constants,'sdwisSampleLoginDescription')}" enable="true" icon="sdwisSampleLoginIcon" label="{resource:getString($constants,'sdwisSampleLogin')}" />
                <code>}</code>
                <menuItem key="clinicalSampleLogin" description="{resource:getString($constants,'clinicalSampleLoginDescription')}" enable="false" icon="clinicalSampleLoginIcon" label="{resource:getString($constants,'clinicalSampleLogin')}" />
                <menuItem key="newbornScreeningSampleLogin" description="{resource:getString($constants,'newbornScreeningSampleLoginDescription')}" enable="false" icon="newbornScreeningSampleLoginIcon" label="{resource:getString($constants,'newbornScreeningSampleLogin')}" />
                <menuItem key="animalSampleLogin" description="{resource:getString($constants,'animalSampleLoginDescription')}" enable="false" icon="animalSampleLoginIcon" label="{resource:getString($constants,'animalSampleLogin')}" />
                <menuItem key="ptSampleLogin" description="{resource:getString($constants,'ptSampleLoginDescription')}" enable="false" icon="ptSampleLoginIcon" label="{resource:getString($constants,'ptSampleLogin')}" />
                <html>&lt;hr/&gt;</html>
                <code>if(UserCache.getPermission().hasModule("project","SELECT")){</code>
                <menuItem key="project" description="{resource:getString($constants,'projectDescription')}" icon="projectIcon" label="{resource:getString($constants,'project')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("provider","SELECT")){</code>
                <menuItem key="provider" description="{resource:getString($constants,'providerDescription')}" icon="providerIcon" label="{resource:getString($constants,'provider')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("organization","SELECT")){</code>
                <menuItem key="organization" description="{resource:getString($constants,'organizationDescription')}" icon="organizationIcon" label="{resource:getString($constants,'organization')}" />
                <code>}</code>
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
				<code>if(UserCache.getPermission().hasModule("worksheet","ADD")){</code>
				<menuItem key="worksheetCreation" description="{resource:getString($constants,'worksheetCreationDescription')}" enable="true" icon="worksheetCreationIcon" label="{resource:getString($constants,'worksheetCreation')}"/>
				<code>}</code>
				<code>if(UserCache.getPermission().hasModule("worksheet","SELECT")){</code>
				<menuItem key="worksheetCompletion" description="{resource:getString($constants,'worksheetCompletionDescription')}"	enable="true" icon="worksheetCompletionIcon" label="{resource:getString($constants,'worksheetCompletion')}"/>
				<code>}</code>
                <menuItem key="addOrCancel" description="{resource:getString($constants,'addOrCancelDescription')}" enable="false" icon="addOrCancelIcon" label="{resource:getString($constants,'addOrCancel')}" />
                <code>if(UserCache.getPermission().hasModule("samplecompleterelease","SELECT")){</code>
                <menuItem key="reviewAndRelease" description="{resource:getString($constants,'reviewAndReleaseDescription')}" enable="true" icon="reviewAndReleaseIcon" label="{resource:getString($constants,'reviewAndRelease')}" />
                <code>}</code>
                <menuItem key="toDo" description="{resource:getString($constants,'toDoDescription')}" enable="true" icon="toDoIcon" label="{resource:getString($constants,'toDo')}" />
                <menuItem key="labelFor" description="{resource:getString($constants,'labelForDescription')}" enable="false" icon="labelForIcon" label="{resource:getString($constants,'labelFor')}" />
                <code>if(UserCache.getPermission().hasModule("storage","SELECT")){</code>
                <menuItem key="storage" description="{resource:getString($constants,'storageDescription')}" enable="true" icon="storageIcon" label="{resource:getString($constants,'storage')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("qc","SELECT")){</code>
                <menuItem key="QC" description="{resource:getString($constants,'QCDescription')}" icon="QCIcon" label="{resource:getString($constants,'QC')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("analyteparameter","SELECT")){</code>
                <menuItem key="analyteParameter" description="{resource:getString($constants,'analyteParameterDescription')}" icon="QCIcon" label="{resource:getString($constants,'analyteParameter')}" />
                <code>}</code>
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
                <code>if(UserCache.getPermission().hasModule("internalorder","SELECT")){</code>
                <menuItem key="internalOrder" description="{resource:getString($constants,'internalOrderDescription')}" enable="true" icon="internalOrderIcon" label="{resource:getString($constants,'internalOrder')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("vendororder","SELECT")){</code>
                <menuItem key="vendorOrder" description="{resource:getString($constants,'vendorOrderDescription')}" enable="true" icon="vendorOrderIcon" label="{resource:getString($constants,'vendorOrder')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("sendoutorder","SELECT")){</code>
                <menuItem key="kitOrder" description="{resource:getString($constants,'kitOrderDescription')}" enable="true" icon="kitOrderIcon" label="{resource:getString($constants,'kitOrder')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(UserCache.getPermission().hasModule("fillorder","SELECT")){</code>
                <menuItem key="fillOrder" description="{resource:getString($constants,'fillOrderDescription')}" enable="true" icon="fillOrderIcon" label="{resource:getString($constants,'fillOrder')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("shipping","SELECT")){</code>
                <menuItem key="shipping" description="{resource:getString($constants,'shippingDescription')}" enable="true" icon="shippingIcon" label="{resource:getString($constants,'shipping')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(UserCache.getPermission().hasModule("buildkits","SELECT")){</code>
                <menuItem key="buildKits" description="{resource:getString($constants,'buildKitsDescription')}" enable="true" icon="buildKitsIcon" label="{resource:getString($constants,'buildKits')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("inventorytransfer","SELECT")){</code>
                <menuItem key="inventoryTransfer" description="{resource:getString($constants,'inventoryTransferDescription')}" enable="true" icon="inventoryTransferIcon" label="{resource:getString($constants,'inventoryTransfer')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(UserCache.getPermission().hasModule("inventoryreceipt","SELECT")){</code>
                <menuItem key="inventoryReceipt" description="{resource:getString($constants,'inventoryReceiptDescription')}" enable="true" icon="inventoryReceiptIcon" label="{resource:getString($constants,'inventoryReceipt')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("inventoryadjustment","SELECT")){</code>
                <menuItem key="inventoryAdjustment" description="{resource:getString($constants,'inventoryAdjustmentDescription')}" enable="true" icon="inventoryAdjustmentIcon" label="{resource:getString($constants,'inventoryAdjustment')}" />                
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("inventoryitem","SELECT")){</code>
                <menuItem key="inventoryItem" description="{resource:getString($constants,'inventoryItemDescription')}" enable="true" icon="inventoryItemIcon" label="{resource:getString($constants,'inventoryItem')}" />
                <code>}</code>
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'report')}"/>
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
                <menuItem style="TopMenuRowContainer" description="" icon="" label="{resource:getString($constants,'login')}">
                  <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                    <menuItem key="verificationReport" description="" icon="" enable="true" label="{resource:getString($constants,'verificationReport')}" />
                    <menuItem key="testRequestFormReport" description="" icon="" enable="false" label="{resource:getString($constants,'TRFReport')}" />
                    <menuItem key="orderRequestForm" description="" icon="" enable="true" label="{resource:getString($constants,'orderRequestForm')}" />
                  </menuPanel>
                </menuItem>
                <menuItem style="TopMenuRowContainer" description="" icon="" label="{resource:getString($constants,'reference')}">
                  <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                    <menuItem key="testReport" description="" icon="" enable="true" label="{resource:getString($constants,'testReport')}" />
                    <menuItem key="qaEventReport" description="" icon="" enable="false" label="{resource:getString($constants,'QAEventReport')}" />
                    <menuItem key="organizationRef" description="" icon="" enable="true" label="{resource:getString($constants,'organizationRef')}" />
                    <menuItem key="organizationRelRef" description="" icon="" enable="true" label="{resource:getString($constants,'organizationRelRef')}" />
                  </menuPanel>
                </menuItem>
                <menuItem style="TopMenuRowContainer" description="" icon="" label="{resource:getString($constants,'summary')}">
                  <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                    <menuItem key="sampleInhouseReport" description="" icon="" enable="true" label="{resource:getString($constants,'sampleInhouseReport')}" />
                    <menuItem key="volumeReport" description="" icon="" enable="true" label="{resource:getString($constants,'volumeReport')}" />
	                <menuItem key="sampleDataExport" description="" icon="" enable="false" label="{resource:getString($constants,'sampleDataExport')}" />
                    <menuItem key="QAByOrganization" description="" icon="" enable="true" label="{resource:getString($constants,'QAByOrganization')}" />
                    <menuItem key="testCountByFacility" description="" icon="" enable="false" label="{resource:getString($constants,'testCountByFacility')}" />
                    <menuItem key="turnaround" description="" icon="" enable="true" label="{resource:getString($constants,'turnaround')}" />
                    <menuItem key="sdwisUnloadReport" description="" icon="" enable="true" label="{resource:getString($constants,'sdwisUnloadReport')}" />
                    <menuItem key="finalReport" description="" icon="" enable="true" label="{resource:getString($constants,'finalReport')}" />
                    <code>if(UserCache.getPermission().hasModule("r_final","SELECT")){</code>
                    <menuItem key="orderRecurrence" description="" icon="" enable="true" label="{resource:getString($constants,'orderRecurrence')}" />
                    <code>}</code>
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'maintenance')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
                <code>if(UserCache.getPermission().hasModule("test","SELECT")){</code>
                <menuItem key="test" description="{resource:getString($constants,'testDescription')}" icon="testIcon" label="{resource:getString($constants,'test')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("method","SELECT")){</code>
                <menuItem key="method" description="{resource:getString($constants,'methodDescription')}" icon="methodIcon" label="{resource:getString($constants,'method')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("panel","SELECT")){</code>
                <menuItem key="panel" description="{resource:getString($constants,'panelDescription')}" enable="true" icon="panelIcon" label="{resource:getString($constants,'panel')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("qaevent","SELECT")){</code>
                <menuItem key="QAEvent" description="{resource:getString($constants,'QAEventDescription')}" icon="QAEventIcon" label="{resource:getString($constants,'QAEvent')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("section","SELECT")){</code>
                <menuItem key="labSection" description="{resource:getString($constants,'labSectionDescription')}" icon="labSectionIcon" label="{resource:getString($constants,'labSection')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(UserCache.getPermission().hasModule("analyte","SELECT")){</code>
                <menuItem key="analyte" description="{resource:getString($constants,'analyteDescription')}" icon="analyteIcon" label="{resource:getString($constants,'analyte')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("dictionary","SELECT")){</code>
                <menuItem key="dictionary" description="{resource:getString($constants,'dictionaryDescription')}" icon="dictionaryIcon" label="{resource:getString($constants,'dictionary')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("auxiliary","SELECT")){</code>
                <menuItem key="auxiliaryPrompt" description="{resource:getString($constants,'auxiliaryPromptDescription')}" enable="true" icon="auxiliaryPromptIcon" label="{resource:getString($constants,'auxiliaryPrompt')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(UserCache.getPermission().hasModule("label","SELECT")){</code>
                <menuItem key="label" description="{resource:getString($constants,'labelDescription')}" enable="true" icon="labelIcon" label="{resource:getString($constants,'label')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("standardnote","SELECT")){</code>
                <menuItem key="standardNote" description="{resource:getString($constants,'standardNoteDescription')}" icon="standardNoteIcon" label="{resource:getString($constants,'standardNote')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("testtrailer","SELECT")){</code>
                <menuItem key="trailerForTest" description="{resource:getString($constants,'trailerForTestDescription')}" icon="trailerForTestIcon" label="{resource:getString($constants,'trailerForTest')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(UserCache.getPermission().hasModule("storageunit","SELECT")){</code>
                <menuItem key="storageUnit" description="{resource:getString($constants,'storageUnitDescription')}" enable="true" icon="storageUnitIcon" label="{resource:getString($constants,'storageUnit')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("storagelocation","SELECT")){</code>
                <menuItem key="storageLocation" description="{resource:getString($constants,'storageLocationDescription')}" enable="true" icon="storageLocationIcon" label="{resource:getString($constants,'storageLocation')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(UserCache.getPermission().hasModule("instrument","SELECT")){</code>
                <menuItem key="instrument" description="{resource:getString($constants,'instrumentDescription')}" enable="true" icon="instrumentIcon" label="{resource:getString($constants,'instrument')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <menuItem key="scriptlet" description="{resource:getString($constants,'scriptletDescription')}" enable="false" icon="scriptletIcon" label="{resource:getString($constants,'scriptlet')}" />
                <code>if(UserCache.getPermission().hasModule("systemvariable","SELECT")){</code>
                <menuItem key="systemVariable" description="{resource:getString($constants,'systemVariableDescription')}" icon="systemVariableIcon" label="{resource:getString($constants,'systemVariable')}" />
                <code>}</code>
                <code>if(UserCache.getPermission().hasModule("pws","SELECT")){</code>
                <menuItem key="pws" description="{resource:getString($constants,'pwsInformationDescription')}" icon="sdwisSampleLoginIcon" label="{resource:getString($constants,'pwsInformation')}" />
                <code>}</code>
              </menuPanel>
            </menuItem>
          </menuPanel>
        </AbsolutePanel>
        <HorizontalPanel>
          <VerticalPanel key="favoritesPanel" width="230px" style="favoritesMenuContainer" visible="false">
            <HorizontalPanel width="100%" height="20px" style="FavHeader">
              <text style="ScreenWindowLabel">Favorites</text>
              <widget halign="right">
                <appButton key="EditFavorites">
                  <AbsolutePanel style="EditSettings" />
                </appButton>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
          <winbrowser key="browser" sizeToWindow="true" />
        </HorizontalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
