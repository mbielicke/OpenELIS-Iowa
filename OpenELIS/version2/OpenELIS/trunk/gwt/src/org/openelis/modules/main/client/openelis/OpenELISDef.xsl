
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
  xmlns:so="xalan://org.openelis.gwt.common.SecurityModule">

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
    <xalan:script lang="javaclass" src="xalan://org.openelis.gwt.common.SecurityModule" />
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
                <label style="topMenuBarItem" text="{resource:getString($constants,'sample')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">

<!--				
<menuItem key="fullLogin" icon="fullLoginIcon" label="{resource:getString($constants,'fullLogin')}" description="{resource:getString($constants,'fullLoginDescription')}"/>
  -->

                <menuItem key="quickEntry" description="{resource:getString($constants,'quickEntryDescription')}" enable="true" icon="quickEntryIcon" label="{resource:getString($constants,'quickEntry')}" />

<!--				
<menuItem key="secondEntry" icon="secondEntryIcon" label="{resource:getString($constants,'secondEntry')}" description="{resource:getString($constants,'secondEntryDescription')}"/>
  -->

                <menuItem key="tracking" description="{resource:getString($constants,'trackingDescription')}" enable="true" icon="trackingIcon" label="{resource:getString($constants,'tracking')}" />
                <html>&lt;hr/&gt;</html>
                <code>if(OpenELIS.security.hasModule("sampleenvironmental","SELECT")){</code>
                <menuItem key="environmentalSampleLogin" description="{resource:getString($constants,'environmentalSampleLoginDescription')}" icon="environmentalSampleLoginIcon" label="{resource:getString($constants,'environmentalSampleLogin')}" />
                <code>}</code>
                <menuItem key="clinicalSampleLogin" description="{resource:getString($constants,'clinicalSampleLoginDescription')}" enable="false" icon="clinicalSampleLoginIcon" label="{resource:getString($constants,'clinicalSampleLogin')}" />
                <menuItem key="animalSampleLogin" description="{resource:getString($constants,'animalSampleLoginDescription')}" enable="false" icon="animalSampleLoginIcon" label="{resource:getString($constants,'animalSampleLogin')}" />
                <menuItem key="newbornScreeningSampleLogin" description="{resource:getString($constants,'newbornScreeningSampleLoginDescription')}" enable="false" icon="newbornScreeningSampleLoginIcon" label="{resource:getString($constants,'newbornScreeningSampleLogin')}" />
                <menuItem key="ptSampleLogin" description="{resource:getString($constants,'ptSampleLoginDescription')}" enable="false" icon="ptSampleLoginIcon" label="{resource:getString($constants,'ptSampleLogin')}" />
                <menuItem key="sdwisSampleLogin" description="{resource:getString($constants,'sdwisSampleLoginDescription')}" enable="false" icon="sdwisSampleLoginIcon" label="{resource:getString($constants,'sdwisSampleLogin')}" />
                <menuItem key="privateWellWaterSampleLogin" description="{resource:getString($constants,'privateWellWaterSampleLoginDescription')}" enable="true" icon="privateWellWaterSampleLoginIcon" label="{resource:getString($constants,'privateWellWaterSampleLogin')}" />

<!-- 
<menuItem key="sampleManagement" icon="sampleManagementIcon" label="{resource:getString($constants,'sampleManagement')}" description="{resource:getString($constants,'sampleManagementDescription')}"/>
  -->

                <html>&lt;hr/&gt;</html>
                <menuItem key="project" description="{resource:getString($constants,'projectDescription')}" icon="projectIcon" label="{resource:getString($constants,'project')}" />
                <code>if(OpenELIS.security.hasModule("provider","SELECT")){</code>
                <menuItem key="provider" description="{resource:getString($constants,'providerDescription')}" icon="providerIcon" label="{resource:getString($constants,'provider')}" />
                <code>}</code>
                <code>if(OpenELIS.security.hasModule("organization","SELECT")){</code>
                <menuItem key="organization" description="{resource:getString($constants,'organizationDescription')}" icon="organizationIcon" label="{resource:getString($constants,'organization')}" />
                <code>}</code>
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
				<code>if(OpenELIS.security.hasModule("worksheet","ADD")){</code>
				<menuItem key="worksheetCreation" description="{resource:getString($constants,'worksheetCreationDescription')}" enable="true" icon="worksheetCreationIcon" label="{resource:getString($constants,'worksheetCreation')}"/>
				<code>}</code>
				<code>if(OpenELIS.security.hasModule("worksheet","SELECT")){</code>
				<menuItem key="worksheetCompletion" description="{resource:getString($constants,'worksheetCompletionDescription')}"	enable="false" icon="worksheetCompletionIcon" label="{resource:getString($constants,'worksheetCompletion')}"/>
				<code>}</code>
                <menuItem key="addOrCancel" description="{resource:getString($constants,'addOrCancelDescription')}" enable="false" icon="addOrCancelIcon" label="{resource:getString($constants,'addOrCancel')}" />
                <menuItem key="reviewAndRelease" description="{resource:getString($constants,'reviewAndReleaseDescription')}" enable="true" icon="reviewAndReleaseIcon" label="{resource:getString($constants,'reviewAndRelease')}" />
                <menuItem key="toDo" description="{resource:getString($constants,'toDoDescription')}" enable="false" icon="toDoIcon" label="{resource:getString($constants,'toDo')}" />
                <menuItem key="labelFor" description="{resource:getString($constants,'labelForDescription')}" enable="false" icon="labelForIcon" label="{resource:getString($constants,'labelFor')}" />
                <code>if(OpenELIS.security.hasModule("storage","SELECT")){</code>
                <menuItem key="storage" description="{resource:getString($constants,'storageDescription')}" enable="true" icon="storageIcon" label="{resource:getString($constants,'storage')}" />
                <code>}</code>
                <menuItem key="QC" description="{resource:getString($constants,'QCDescription')}" icon="QCIcon" label="{resource:getString($constants,'QC')}" />
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
                <code>if(OpenELIS.security.hasModule("order","SELECT")){</code>
                <menuItem key="internalOrder" description="{resource:getString($constants,'internalOrderDescription')}" enable="true" icon="internalOrderIcon" label="{resource:getString($constants,'internalOrder')}" />
                <code>}</code>
                <menuItem key="vendorOrder" description="{resource:getString($constants,'vendorOrderDescription')}" enable="false" icon="vendorOrderIcon" label="{resource:getString($constants,'vendorOrder')}" />
                <code>if(OpenELIS.security.hasModule("order","SELECT")){</code>
                <menuItem key="kitOrder" description="{resource:getString($constants,'kitOrderDescription')}" enable="true" icon="kitOrderIcon" label="{resource:getString($constants,'kitOrder')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <menuItem key="fillOrder" description="{resource:getString($constants,'fillOrderDescription')}" enable="false" icon="fillOrderIcon" label="{resource:getString($constants,'fillOrder')}" />
                <menuItem key="shipping" description="{resource:getString($constants,'shippingDescription')}" enable="false" icon="shippingIcon" label="{resource:getString($constants,'shipping')}" />
                <html>&lt;hr/&gt;</html>
                <menuItem key="buildKits" description="{resource:getString($constants,'buildKitsDescription')}" enable="false" icon="buildKitsIcon" label="{resource:getString($constants,'buildKits')}" />
                <menuItem key="inventoryTransfer" description="{resource:getString($constants,'inventoryTransferDescription')}" enable="false" icon="inventoryTransferIcon" label="{resource:getString($constants,'inventoryTransfer')}" />
                <html>&lt;hr/&gt;</html>
                <menuItem key="inventoryReceipt" description="{resource:getString($constants,'inventoryReceiptDescription')}" enable="false" icon="inventoryReceiptIcon" label="{resource:getString($constants,'inventoryReceipt')}" />
                <menuItem key="inventoryAdjustment" description="{resource:getString($constants,'inventoryAdjustmentDescription')}" enable="false" icon="inventoryAdjustmentIcon" label="{resource:getString($constants,'inventoryAdjustment')}" />
                <code>if(OpenELIS.security.hasModule("inventoryitem","SELECT")){</code>
                <menuItem key="inventoryItem" description="{resource:getString($constants,'inventoryItemDescription')}" enable="true" icon="inventoryItemIcon" label="{resource:getString($constants,'inventoryItem')}" />
                <code>}</code>
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'maintenance')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
                <code>if(OpenELIS.security.hasModule("test","SELECT")){</code>
                <menuItem key="test" description="{resource:getString($constants,'testDescription')}" icon="testIcon" label="{resource:getString($constants,'test')}" />
                <code>}</code>
                <menuItem key="method" description="{resource:getString($constants,'methodDescription')}" icon="methodIcon" label="{resource:getString($constants,'method')}" />
                <code>if(OpenELIS.security.hasModule("panel","SELECT")){</code>
                <menuItem key="panel" description="{resource:getString($constants,'panelDescription')}" enable="true" icon="panelIcon" label="{resource:getString($constants,'panel')}" />
                <code>}</code>
                <code>if(OpenELIS.security.hasModule("qaevent","SELECT")){</code>
                <menuItem key="QAEvent" description="{resource:getString($constants,'QAEventDescription')}" icon="QAEventIcon" label="{resource:getString($constants,'QAEvent')}" />
                <code>}</code>
                <code>if(OpenELIS.security.hasModule("section","SELECT")){</code>
                <menuItem key="labSection" description="{resource:getString($constants,'labSectionDescription')}" icon="labSectionIcon" label="{resource:getString($constants,'labSection')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(OpenELIS.security.hasModule("analyte","SELECT")){</code>
                <menuItem key="analyte" description="{resource:getString($constants,'analyteDescription')}" icon="analyteIcon" label="{resource:getString($constants,'analyte')}" />
                <code>}</code>
                <code>if(OpenELIS.security.hasModule("dictionary","SELECT")){</code>
                <menuItem key="dictionary" description="{resource:getString($constants,'dictionaryDescription')}" icon="dictionaryIcon" label="{resource:getString($constants,'dictionary')}" />
                <code>}</code>
                <code>if(OpenELIS.security.hasModule("auxiliary","SELECT")){</code>
                <menuItem key="auxiliaryPrompt" description="{resource:getString($constants,'auxiliaryPromptDescription')}" enable="true" icon="auxiliaryPromptIcon" label="{resource:getString($constants,'auxiliaryPrompt')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(OpenELIS.security.hasModule("label","SELECT")){</code>
                <menuItem key="label" description="{resource:getString($constants,'labelDescription')}" enable="true" icon="labelIcon" label="{resource:getString($constants,'label')}" />
                <code>}</code>
                <code>if(OpenELIS.security.hasModule("standardnote","SELECT")){</code>
                <menuItem key="standardNote" description="{resource:getString($constants,'standardNoteDescription')}" icon="standardNoteIcon" label="{resource:getString($constants,'standardNote')}" />
                <code>}</code>
                <code>if(OpenELIS.security.hasModule("testtrailer","SELECT")){</code>
                <menuItem key="trailerForTest" description="{resource:getString($constants,'trailerForTestDescription')}" icon="trailerForTestIcon" label="{resource:getString($constants,'trailerForTest')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(OpenELIS.security.hasModule("storageunit","SELECT")){</code>
                <menuItem key="storageUnit" description="{resource:getString($constants,'storageUnitDescription')}" enable="true" icon="storageUnitIcon" label="{resource:getString($constants,'storageUnit')}" />
                <code>}</code>
                <code>if(OpenELIS.security.hasModule("storagelocation","SELECT")){</code>
                <menuItem key="storageLocation" description="{resource:getString($constants,'storageLocationDescription')}" enable="true" icon="storageLocationIcon" label="{resource:getString($constants,'storageLocation')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <code>if(OpenELIS.security.hasModule("instrument","SELECT")){</code>
                <menuItem key="instrument" description="{resource:getString($constants,'instrumentDescription')}" enable="true" icon="instrumentIcon" label="{resource:getString($constants,'instrument')}" />
                <code>}</code>
                <html>&lt;hr/&gt;</html>
                <menuItem key="scriptlet" description="{resource:getString($constants,'scriptletDescription')}" enable="false" icon="scriptletIcon" label="{resource:getString($constants,'scriptlet')}" />
                <code>if(OpenELIS.security.hasModule("systemvariable","SELECT")){</code>
                <menuItem key="systemVariable" description="{resource:getString($constants,'systemVariableDescription')}" icon="systemVariableIcon" label="{resource:getString($constants,'systemVariable')}" />
                <code>}</code>
                <code>if(OpenELIS.security.hasModule("pws","SELECT")){</code>
                <menuItem key="pws" description="{resource:getString($constants,'pwsInformationDescription')}" icon="systemVariableIcon" label="{resource:getString($constants,'pwsInformation')}" />
                <code>}</code>
              </menuPanel>
            </menuItem>
            <menuItem>
              <menuDisplay>
                <label style="topMenuBarItem" text="{resource:getString($constants,'report')}" />
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="topMenuContainer">
                <menuItem key="finalReport" description="{resource:getString($constants,'finalReportDescription')}" enable="false" icon="finalReportIcon" label="{resource:getString($constants,'finalReport')}" />
                <menuItem key="sampleDataExport" description="{resource:getString($constants,'sampleDataExportDescription')}" enable="false" icon="sampleDataExportIcon" label="{resource:getString($constants,'sampleDataExport')}" />
                <menuItem key="loginLabel" description="{resource:getString($constants,'loginLabelDescription')}" enable="false" icon="loginLabelIcon" label="{resource:getString($constants,'loginLabel')}" />
                <html>&lt;hr/&gt;</html>
                <menuItem style="TopMenuRowContainer" description="" icon="referenceIcon" label="{resource:getString($constants,'reference')}">
                  <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                    <menuItem key="organizationRef" description="{resource:getString($constants,'organizationDescription')}" enable="false" icon="organizationIcon" label="{resource:getString($constants,'organization')}" />
                    <menuItem key="testRef" description="{resource:getString($constants,'testDescription')}" enable="false" icon="testIconRef" label="{resource:getString($constants,'test')}" />
                    <menuItem key="QAEventRef" description="{resource:getString($constants,'QAEventDescription')}" enable="false" icon="QAEventIconRef" label="{resource:getString($constants,'QAEvent')}" />
                  </menuPanel>
                </menuItem>
                <menuItem style="TopMenuRowContainer" description="" icon="summaryIcon" label="{resource:getString($constants,'summary')}">
                  <menuPanel layout="vertical" position="beside" style="topMenuContainer">
                    <menuItem key="QAByOrganization" description="{resource:getString($constants,'QAByOrganizationDescription')}" enable="false" icon="QAByOrganizationIcon" label="{resource:getString($constants,'QAByOrganization')}" />
                    <menuItem key="testCountByFacility" description="{resource:getString($constants,'testCountByFacilityDescription')}" enable="false" icon="testCountByFacilityIcon" label="{resource:getString($constants,'testCountByFacility')}" />
                    <menuItem key="turnaround" description="{resource:getString($constants,'turnaroundDescription')}" enable="false" icon="turnaroundIcon" label="{resource:getString($constants,'turnaround')}" />
                    <menuItem key="positiveTestCount" description="{resource:getString($constants,'positiveTestCountDescription')}" enable="false" icon="positiveTestCountIcon" label="{resource:getString($constants,'positiveTestCount')}" />
                  </menuPanel>
                </menuItem>
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
