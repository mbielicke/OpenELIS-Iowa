
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
  extension-element-prefixes="resource"
  version="1.0"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:service="xalan://org.openelis.gwt.server.ServiceUtils"
  xmlns:so="xalan://org.openelis.gwt.common.ModulePermission"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

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
      <HorizontalPanel style="AppBackground">
        <CollapsePanel key="favoritesCollapse" style="favorite">
          <VerticalPanel key="favoritesPanel" style="favoritesMenuContainer" visible="true" width="230px">
            <HorizontalPanel height="30px" style="FavHeader" width="100%">
              <text style="ScreenWindowLabel">Favorites</text>
              <widget halign="right">
                <appButton key="EditFavorites">
                  <AbsolutePanel style="EditSettings" />
                </appButton>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
        </CollapsePanel>
        <VerticalPanel>
          <AbsolutePanel style="topMenuBar">
            <menuPanel layout="horizontal" style="topBarItemHolder">
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'application')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem description="{resource:getString($constants,'preferenceDescription')}" enable="false" icon="preferenceIcon" key="preference" label="{resource:getString($constants,'preference')}" />
                  <menuItem description="{resource:getString($constants,'favoritesMenuDescription')}" enable="false" icon="favoritesMenuIcon" key="FavoritesMenu" label="{resource:getString($constants,'favoritesMenu')}" />
                  <menuItem description="{resource:getString($constants,'logoutDescription')}" icon="logoutIcon" key="Logout" label="{resource:getString($constants,'logout')}" />
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'label')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <code>if(UserCache.getPermission().hasModule("r_loginlabel","SELECT")){</code>
                  <menuItem description="" enable="true" icon="" key="sampleLoginLabelReport" label="{resource:getString($constants,'loginBarcode')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("r_loginlabelrep","SELECT")){</code>
                  <menuItem description="" enable="true" icon="" key="sampleLoginLabelAdditionalReport" label="{resource:getString($constants,'loginBarcodeAdd')}" />
                  <code>}</code>
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'sample')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <code>if(UserCache.getPermission().hasModule("quickentry","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'quickEntryDescription')}" enable="true" icon="quickEntryIcon" key="quickEntry" label="{resource:getString($constants,'quickEntry')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("verification","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'verificationDescription')}" enable="true" icon="QCIcon" key="verification" label="{resource:getString($constants,'verification')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("sampletracking","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'trackingDescription')}" enable="true" icon="trackingIcon" key="tracking" label="{resource:getString($constants,'tracking')}" />
                  <code>}</code>
                  <html>&lt;hr/&gt;</html>
                  <code>if(UserCache.getPermission().hasModule("sampleenvironmental","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'environmentalSampleLoginDescription')}" icon="environmentalSampleLoginIcon" key="environmentalSampleLogin" label="{resource:getString($constants,'environmentalSampleLogin')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("sampleprivatewell","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'privateWellWaterSampleLoginDescription')}" enable="true" icon="privateWellWaterSampleLoginIcon" key="privateWellWaterSampleLogin" label="{resource:getString($constants,'privateWellWaterSampleLogin')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("samplesdwis","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'sdwisSampleLoginDescription')}" enable="true" icon="sdwisSampleLoginIcon" key="sdwisSampleLogin" label="{resource:getString($constants,'sdwisSampleLogin')}" />
                  <code>}</code>
                  <menuItem description="{resource:getString($constants,'clinicalSampleLoginDescription')}" enable="false" icon="clinicalSampleLoginIcon" key="clinicalSampleLogin" label="{resource:getString($constants,'clinicalSampleLogin')}" />
                  <menuItem description="{resource:getString($constants,'newbornScreeningSampleLoginDescription')}" enable="false" icon="newbornScreeningSampleLoginIcon" key="newbornScreeningSampleLogin" label="{resource:getString($constants,'newbornScreeningSampleLogin')}" />
                  <menuItem description="{resource:getString($constants,'animalSampleLoginDescription')}" enable="false" icon="animalSampleLoginIcon" key="animalSampleLogin" label="{resource:getString($constants,'animalSampleLogin')}" />
                  <menuItem description="{resource:getString($constants,'ptSampleLoginDescription')}" enable="false" icon="ptSampleLoginIcon" key="ptSampleLogin" label="{resource:getString($constants,'ptSampleLogin')}" />
                  <html>&lt;hr/&gt;</html>
                  <code>if(UserCache.getPermission().hasModule("project","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'projectDescription')}" icon="projectIcon" key="project" label="{resource:getString($constants,'project')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("provider","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'providerDescription')}" icon="providerIcon" key="provider" label="{resource:getString($constants,'provider')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("organization","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'organizationDescription')}" icon="organizationIcon" key="organization" label="{resource:getString($constants,'organization')}" />
                  <code>}</code>
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <code>if(UserCache.getPermission().hasModule("worksheet","ADD")){</code>
                  <menuItem description="{resource:getString($constants,'worksheetCreationDescription')}" enable="true" icon="worksheetCreationIcon" key="worksheetCreation" label="{resource:getString($constants,'worksheetCreation')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("worksheet","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'worksheetCompletionDescription')}" enable="true" icon="worksheetCompletionIcon" key="worksheetCompletion" label="{resource:getString($constants,'worksheetCompletion')}" />
                  <code>}</code>
                  <menuItem description="{resource:getString($constants,'addOrCancelDescription')}" enable="false" icon="addOrCancelIcon" key="addOrCancel" label="{resource:getString($constants,'addOrCancel')}" />
                  <code>if(UserCache.getPermission().hasModule("samplecompleterelease","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'reviewAndReleaseDescription')}" enable="true" icon="reviewAndReleaseIcon" key="reviewAndRelease" label="{resource:getString($constants,'reviewAndRelease')}" />
                  <code>}</code>
                  <menuItem description="{resource:getString($constants,'toDoDescription')}" enable="true" icon="toDoIcon" key="toDo" label="{resource:getString($constants,'toDo')}" />
                  <menuItem description="{resource:getString($constants,'labelForDescription')}" enable="false" icon="labelForIcon" key="labelFor" label="{resource:getString($constants,'labelFor')}" />
                  <code>if(UserCache.getPermission().hasModule("storage","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'storageDescription')}" enable="true" icon="storageIcon" key="storage" label="{resource:getString($constants,'storage')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("qc","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'QCDescription')}" icon="QCIcon" key="QC" label="{resource:getString($constants,'QC')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("analyteparameter","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'analyteParameterDescription')}" icon="QCIcon" key="analyteParameter" label="{resource:getString($constants,'analyteParameter')}" />
                  <code>}</code>
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <code>if(UserCache.getPermission().hasModule("internalorder","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'internalOrderDescription')}" enable="true" icon="internalOrderIcon" key="internalOrder" label="{resource:getString($constants,'internalOrder')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("vendororder","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'vendorOrderDescription')}" enable="true" icon="vendorOrderIcon" key="vendorOrder" label="{resource:getString($constants,'vendorOrder')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("sendoutorder","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'kitOrderDescription')}" enable="true" icon="kitOrderIcon" key="kitOrder" label="{resource:getString($constants,'kitOrder')}" />
                  <code>}</code>
                  <html>&lt;hr/&gt;</html>
                  <code>if(UserCache.getPermission().hasModule("fillorder","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'fillOrderDescription')}" enable="true" icon="fillOrderIcon" key="fillOrder" label="{resource:getString($constants,'fillOrder')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("shipping","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'shippingDescription')}" enable="true" icon="shippingIcon" key="shipping" label="{resource:getString($constants,'shipping')}" />
                  <code>}</code>
                  <html>&lt;hr/&gt;</html>
                  <code>if(UserCache.getPermission().hasModule("buildkits","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'buildKitsDescription')}" enable="true" icon="buildKitsIcon" key="buildKits" label="{resource:getString($constants,'buildKits')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("inventorytransfer","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'inventoryTransferDescription')}" enable="true" icon="inventoryTransferIcon" key="inventoryTransfer" label="{resource:getString($constants,'inventoryTransfer')}" />
                  <code>}</code>
                  <html>&lt;hr/&gt;</html>
                  <code>if(UserCache.getPermission().hasModule("inventoryreceipt","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'inventoryReceiptDescription')}" enable="true" icon="inventoryReceiptIcon" key="inventoryReceipt" label="{resource:getString($constants,'inventoryReceipt')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("inventoryadjustment","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'inventoryAdjustmentDescription')}" enable="true" icon="inventoryAdjustmentIcon" key="inventoryAdjustment" label="{resource:getString($constants,'inventoryAdjustment')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("inventoryitem","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'inventoryItemDescription')}" enable="true" icon="inventoryItemIcon" key="inventoryItem" label="{resource:getString($constants,'inventoryItem')}" />
                  <code>}</code>
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'report')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem description="" icon="" label="{resource:getString($constants,'login')}" style="TopMenuRowContainer">
                    <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                      <menuItem description="" enable="true" icon="" key="verificationReport" label="{resource:getString($constants,'verificationReport')}" />
                      <menuItem description="" enable="false" icon="" key="testRequestFormReport" label="{resource:getString($constants,'TRFReport')}" />
                      <menuItem description="" enable="true" icon="" key="orderRequestForm" label="{resource:getString($constants,'orderRequestForm')}" />
                    </menuPanel>
                  </menuItem>
                  <menuItem description="" icon="" label="{resource:getString($constants,'reference')}" style="TopMenuRowContainer">
                    <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                      <menuItem description="" enable="true" icon="" key="testReport" label="{resource:getString($constants,'testReport')}" />
                      <menuItem description="" enable="false" icon="" key="qaEventReport" label="{resource:getString($constants,'QAEventReport')}" />
                      <menuItem description="" enable="true" icon="" key="organizationRef" label="{resource:getString($constants,'organizationRef')}" />
                      <menuItem description="" enable="true" icon="" key="organizationRelRef" label="{resource:getString($constants,'organizationRelRef')}" />
                    </menuPanel>
                  </menuItem>
                  <menuItem description="" icon="" label="{resource:getString($constants,'summary')}" style="TopMenuRowContainer">
                    <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                      <menuItem description="" enable="true" icon="" key="sampleInhouseReport" label="{resource:getString($constants,'sampleInhouseReport')}" />
                      <menuItem description="" enable="true" icon="" key="volumeReport" label="{resource:getString($constants,'volumeReport')}" />
                      <menuItem description="" enable="false" icon="" key="sampleDataExport" label="{resource:getString($constants,'sampleDataExport')}" />
                      <menuItem description="" enable="true" icon="" key="QAByOrganization" label="{resource:getString($constants,'QAByOrganization')}" />
                      <menuItem description="" enable="false" icon="" key="testCountByFacility" label="{resource:getString($constants,'testCountByFacility')}" />
                      <menuItem description="" enable="true" icon="" key="turnaround" label="{resource:getString($constants,'turnaround')}" />
                      <menuItem description="" enable="true" icon="" key="sdwisUnloadReport" label="{resource:getString($constants,'sdwisUnloadReport')}" />
                      <menuItem description="" enable="true" icon="" key="finalReport" label="{resource:getString($constants,'finalReport')}" />
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
                  <menuItem description="{resource:getString($constants,'testDescription')}" icon="testIcon" key="test" label="{resource:getString($constants,'test')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("method","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'methodDescription')}" icon="methodIcon" key="method" label="{resource:getString($constants,'method')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("panel","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'panelDescription')}" enable="true" icon="panelIcon" key="panel" label="{resource:getString($constants,'panel')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("qaevent","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'QAEventDescription')}" icon="QAEventIcon" key="QAEvent" label="{resource:getString($constants,'QAEvent')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("section","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'labSectionDescription')}" icon="labSectionIcon" key="labSection" label="{resource:getString($constants,'labSection')}" />
                  <code>}</code>
                  <html>&lt;hr/&gt;</html>
                  <code>if(UserCache.getPermission().hasModule("analyte","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'analyteDescription')}" icon="analyteIcon" key="analyte" label="{resource:getString($constants,'analyte')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("dictionary","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'dictionaryDescription')}" icon="dictionaryIcon" key="dictionary" label="{resource:getString($constants,'dictionary')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("auxiliary","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'auxiliaryPromptDescription')}" enable="true" icon="auxiliaryPromptIcon" key="auxiliaryPrompt" label="{resource:getString($constants,'auxiliaryPrompt')}" />
                  <code>}</code>
                  <html>&lt;hr/&gt;</html>
                  <code>if(UserCache.getPermission().hasModule("label","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'labelDescription')}" enable="true" icon="labelIcon" key="label" label="{resource:getString($constants,'label')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("standardnote","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'standardNoteDescription')}" icon="standardNoteIcon" key="standardNote" label="{resource:getString($constants,'standardNote')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("testtrailer","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'trailerForTestDescription')}" icon="trailerForTestIcon" key="trailerForTest" label="{resource:getString($constants,'trailerForTest')}" />
                  <code>}</code>
                  <html>&lt;hr/&gt;</html>
                  <code>if(UserCache.getPermission().hasModule("storageunit","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'storageUnitDescription')}" enable="true" icon="storageUnitIcon" key="storageUnit" label="{resource:getString($constants,'storageUnit')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("storagelocation","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'storageLocationDescription')}" enable="true" icon="storageLocationIcon" key="storageLocation" label="{resource:getString($constants,'storageLocation')}" />
                  <code>}</code>
                  <html>&lt;hr/&gt;</html>
                  <code>if(UserCache.getPermission().hasModule("instrument","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'instrumentDescription')}" enable="true" icon="instrumentIcon" key="instrument" label="{resource:getString($constants,'instrument')}" />
                  <code>}</code>
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'scriptletDescription')}" enable="false" icon="scriptletIcon" key="scriptlet" label="{resource:getString($constants,'scriptlet')}" />
                  <code>if(UserCache.getPermission().hasModule("systemvariable","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'systemVariableDescription')}" icon="systemVariableIcon" key="systemVariable" label="{resource:getString($constants,'systemVariable')}" />
                  <code>}</code>
                  <code>if(UserCache.getPermission().hasModule("pws","SELECT")){</code>
                  <menuItem description="{resource:getString($constants,'pwsInformationDescription')}" icon="sdwisSampleLoginIcon" key="pws" label="{resource:getString($constants,'pwsInformation')}" />
                  <code>}</code>
                </menuPanel>
              </menuItem>
            </menuPanel>
          </AbsolutePanel>
          <winbrowser key="browser" sizeToWindow="true" />
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
