
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
            <HorizontalPanel height="30px" style="topMenuBar" width="100%">
              <text style="topMenuBarItem">Favorites</text>
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
                  <menuItem description="{resource:getString($constants,'preferenceDescription')}" enable="true" icon="preferenceIcon" key="preference" label="{resource:getString($constants,'preference')}" />
                  <menuItem description="{resource:getString($constants,'logoutDescription')}" enable="true" icon="logoutIcon" key="logout" label="{resource:getString($constants,'logout')}" />
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'label')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem description="" enable="false" icon="" key="sampleLoginLabelReport" label="{resource:getString($constants,'loginBarcode')}" />
                  <menuItem description="" enable="false" icon="" key="sampleLoginLabelAdditionalReport" label="{resource:getString($constants,'loginBarcodeAdd')}" />
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'sample')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem description="{resource:getString($constants,'quickEntryDescription')}" enable="false" icon="quickEntryIcon" key="quickEntry" label="{resource:getString($constants,'quickEntry')}" />
                  <menuItem description="{resource:getString($constants,'verificationDescription')}" enable="false" icon="QCIcon" key="verification" label="{resource:getString($constants,'verification')}" />
                  <menuItem description="{resource:getString($constants,'trackingDescription')}" enable="false" icon="trackingIcon" key="tracking" label="{resource:getString($constants,'tracking')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'environmentalSampleLoginDescription')}" enable="false" icon="environmentalSampleLoginIcon" key="environmentalSampleLogin" label="{resource:getString($constants,'environmentalSampleLogin')}" />
                  <menuItem description="{resource:getString($constants,'privateWellWaterSampleLoginDescription')}" enable="false" icon="privateWellWaterSampleLoginIcon" key="privateWellWaterSampleLogin" label="{resource:getString($constants,'privateWellWaterSampleLogin')}" />
                  <menuItem description="{resource:getString($constants,'sdwisSampleLoginDescription')}" enable="false" icon="sdwisSampleLoginIcon" key="sdwisSampleLogin" label="{resource:getString($constants,'sdwisSampleLogin')}" />
                  <menuItem description="{resource:getString($constants,'clinicalSampleLoginDescription')}" enable="false" icon="clinicalSampleLoginIcon" key="clinicalSampleLogin" label="{resource:getString($constants,'clinicalSampleLogin')}" />
                  <menuItem description="{resource:getString($constants,'newbornScreeningSampleLoginDescription')}" enable="false" icon="newbornScreeningSampleLoginIcon" key="newbornScreeningSampleLogin" label="{resource:getString($constants,'newbornScreeningSampleLogin')}" />
                  <menuItem description="{resource:getString($constants,'animalSampleLoginDescription')}" enable="false" icon="animalSampleLoginIcon" key="animalSampleLogin" label="{resource:getString($constants,'animalSampleLogin')}" />
                  <menuItem description="{resource:getString($constants,'ptSampleLoginDescription')}" enable="false" icon="ptSampleLoginIcon" key="ptSampleLogin" label="{resource:getString($constants,'ptSampleLogin')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'projectDescription')}" enable="false" icon="projectIcon" key="project" label="{resource:getString($constants,'project')}" />
                  <menuItem description="{resource:getString($constants,'providerDescription')}" enable="false" icon="providerIcon" key="provider" label="{resource:getString($constants,'provider')}" />
                  <menuItem description="{resource:getString($constants,'organizationDescription')}" enable="false" icon="organizationIcon" key="organization" label="{resource:getString($constants,'organization')}" />
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem description="{resource:getString($constants,'worksheetCreationDescription')}" enable="false" icon="worksheetCreationIcon" key="worksheetCreation" label="{resource:getString($constants,'worksheetCreation')}" />
                  <menuItem description="{resource:getString($constants,'worksheetCompletionDescription')}" enable="false" icon="worksheetCompletionIcon" key="worksheetCompletion" label="{resource:getString($constants,'worksheetCompletion')}" />
                  <menuItem description="{resource:getString($constants,'addOrCancelDescription')}" enable="false" icon="addOrCancelIcon" key="addOrCancel" label="{resource:getString($constants,'manageMultipleSamples')}" />
                  <menuItem description="{resource:getString($constants,'reviewAndReleaseDescription')}" enable="false" icon="reviewAndReleaseIcon" key="reviewAndRelease" label="{resource:getString($constants,'reviewAndRelease')}" />
                  <menuItem description="{resource:getString($constants,'toDoDescription')}" enable="false" icon="toDoIcon" key="toDo" label="{resource:getString($constants,'toDo')}" />
                  <menuItem description="{resource:getString($constants,'labelForDescription')}" enable="false" icon="labelForIcon" key="labelFor" label="{resource:getString($constants,'labelFor')}" />
                  <menuItem description="{resource:getString($constants,'storageDescription')}" enable="false" icon="storageIcon" key="storage" label="{resource:getString($constants,'storage')}" />
                  <menuItem description="{resource:getString($constants,'QCDescription')}" enable="false" icon="QCIcon" key="QC" label="{resource:getString($constants,'QC')}" />
                  <menuItem description="{resource:getString($constants,'analyteParameterDescription')}" enable="false" icon="QCIcon" key="analyteParameter" label="{resource:getString($constants,'analyteParameter')}" />
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem description="{resource:getString($constants,'internalOrderDescription')}" enable="false" icon="internalOrderIcon" key="internalOrder" label="{resource:getString($constants,'internalOrder')}" />
                  <menuItem description="{resource:getString($constants,'vendorOrderDescription')}" enable="false" icon="vendorOrderIcon" key="vendorOrder" label="{resource:getString($constants,'vendorOrder')}" />
                  <menuItem description="{resource:getString($constants,'sendoutOrderDescription')}" enable="false" icon="sendoutOrderIcon" key="sendoutOrder" label="{resource:getString($constants,'sendoutOrder')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'fillOrderDescription')}" enable="false" icon="fillOrderIcon" key="fillOrder" label="{resource:getString($constants,'fillOrder')}" />
                  <menuItem description="{resource:getString($constants,'shippingDescription')}" enable="false" icon="shippingIcon" key="shipping" label="{resource:getString($constants,'shipping')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'buildKitsDescription')}" enable="false" icon="buildKitsIcon" key="buildKits" label="{resource:getString($constants,'buildKits')}" />
                  <menuItem description="{resource:getString($constants,'inventoryTransferDescription')}" enable="false" icon="inventoryTransferIcon" key="inventoryTransfer" label="{resource:getString($constants,'inventoryTransfer')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'inventoryReceiptDescription')}" enable="false" icon="inventoryReceiptIcon" key="inventoryReceipt" label="{resource:getString($constants,'inventoryReceipt')}" />
                  <menuItem description="{resource:getString($constants,'inventoryAdjustmentDescription')}" enable="false" icon="inventoryAdjustmentIcon" key="inventoryAdjustment" label="{resource:getString($constants,'inventoryAdjustment')}" />
                  <menuItem description="{resource:getString($constants,'inventoryItemDescription')}" enable="false" icon="inventoryItemIcon" key="inventoryItem" label="{resource:getString($constants,'inventoryItem')}" />
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'report')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem description="" icon="" label="{resource:getString($constants,'login')}" style="TopMenuRowContainer">
                    <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                      <menuItem description="" enable="false" icon="" key="verificationReport" label="{resource:getString($constants,'verificationReport')}" />
                      <menuItem description="" enable="false" icon="" key="testRequestFormReport" label="{resource:getString($constants,'TRFReport')}" />
                      <menuItem description="" enable="false" icon="" key="orderRequestForm" label="{resource:getString($constants,'orderRequestForm')}" />
                    </menuPanel>
                  </menuItem>
                  <menuItem description="" icon="" label="{resource:getString($constants,'reference')}" style="TopMenuRowContainer">
                    <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                      <menuItem description="" enable="false" icon="" key="testReport" label="{resource:getString($constants,'testReport')}" />
                      <menuItem description="" enable="false" icon="" key="billingReport" label="{resource:getString($constants,'billingReport')}" />
                    </menuPanel>
                  </menuItem>
                  <menuItem description="" icon="" label="{resource:getString($constants,'summary')}" style="TopMenuRowContainer">
                    <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                      <menuItem description="" enable="false" icon="" key="sampleInhouseReport" label="{resource:getString($constants,'sampleInhouseReport')}" />
                      <menuItem description="" enable="false" icon="" key="volumeReport" label="{resource:getString($constants,'volumeReport')}" />
                      <menuItem description="" enable="false" icon="" key="sampleDataExport" label="{resource:getString($constants,'sampleDataExport')}" />
                      <menuItem description="" enable="false" icon="" key="QAByOrganization" label="{resource:getString($constants,'QAByOrganization')}" />
                      <menuItem description="" enable="false" icon="" key="testCountByFacility" label="{resource:getString($constants,'testCountByFacility')}" />
                      <menuItem description="" enable="false" icon="" key="turnaround" label="{resource:getString($constants,'turnaround')}" />
                      <menuItem description="" enable="false" icon="" key="sdwisUnloadReport" label="{resource:getString($constants,'sdwisUnloadReport')}" />
                      <menuItem description="" enable="false" icon="" key="dataView" label="{resource:getString($constants,'dataView')}" />
                      <menuItem description="" enable="false" icon="" key="finalReport" label="{resource:getString($constants,'finalReport')}" />
                      <menuItem key="orderRecurrence" description="" enable="false" icon="" label="{resource:getString($constants,'orderRecurrence')}" />
                      <menuItem key="finalReportBatch" description="" enable="false" icon="" label="{resource:getString($constants,'finalReportBatch')}" />
                    </menuPanel>
                  </menuItem>
                </menuPanel>
              </menuItem>
              <menuItem>
                <menuDisplay>
                  <label style="topMenuBarItem" text="{resource:getString($constants,'maintenance')}" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem description="{resource:getString($constants,'testDescription')}" enable="false" icon="testIcon" key="test" label="{resource:getString($constants,'test')}" />
                  <menuItem description="{resource:getString($constants,'methodDescription')}" enable="false" icon="methodIcon" key="method" label="{resource:getString($constants,'method')}" />
                  <menuItem description="{resource:getString($constants,'panelDescription')}" enable="false" icon="panelIcon" key="panel" label="{resource:getString($constants,'panel')}" />
                  <menuItem description="{resource:getString($constants,'QAEventDescription')}" enable="false" icon="QAEventIcon" key="QAEvent" label="{resource:getString($constants,'QAEvent')}" />
                  <menuItem description="{resource:getString($constants,'labSectionDescription')}" enable="false" icon="labSectionIcon" key="labSection" label="{resource:getString($constants,'labSection')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'analyteDescription')}" enable="false" icon="analyteIcon" key="analyte" label="{resource:getString($constants,'analyte')}" />
                  <menuItem description="{resource:getString($constants,'dictionaryDescription')}" enable="false" icon="dictionaryIcon" key="dictionary" label="{resource:getString($constants,'dictionary')}" />
                  <menuItem description="{resource:getString($constants,'auxiliaryPromptDescription')}" enable="false" icon="auxiliaryPromptIcon" key="auxiliaryPrompt" label="{resource:getString($constants,'auxiliaryPrompt')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'labelDescription')}" enable="false" icon="labelIcon" key="label" label="{resource:getString($constants,'label')}" />
                  <menuItem description="{resource:getString($constants,'standardNoteDescription')}" enable="false" icon="standardNoteIcon" key="standardNote" label="{resource:getString($constants,'standardNote')}" />
                  <menuItem description="{resource:getString($constants,'trailerForTestDescription')}" enable="false" icon="trailerForTestIcon" key="trailerForTest" label="{resource:getString($constants,'trailerForTest')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'storageUnitDescription')}" enable="false" icon="storageUnitIcon" key="storageUnit" label="{resource:getString($constants,'storageUnit')}" />
                  <menuItem description="{resource:getString($constants,'storageLocationDescription')}" enable="false" icon="storageLocationIcon" key="storageLocation" label="{resource:getString($constants,'storageLocation')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'instrumentDescription')}" enable="false" icon="instrumentIcon" key="instrument" label="{resource:getString($constants,'instrument')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem description="{resource:getString($constants,'scriptletDescription')}" enable="false" icon="scriptletIcon" key="scriptlet" label="{resource:getString($constants,'scriptlet')}" />
                  <menuItem description="{resource:getString($constants,'systemVariableDescription')}" enable="false" icon="systemVariableIcon" key="systemVariable" label="{resource:getString($constants,'systemVariable')}" />
                  <menuItem description="{resource:getString($constants,'pwsInformationDescription')}" enable="false" icon="sdwisSampleLoginIcon" key="pws" label="{resource:getString($constants,'pwsInformation')}" />
                  <menuItem description="{resource:getString($constants,'cronDescription')}" icon="cronIcon" key="cron" label="{resource:getString($constants,'cron')}" />
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
