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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:security="xalan://org.openelis.gwt.common.SecurityUtil"
                xmlns:service="xalan://org.openelis.gwt.server.ServiceUtils"
                xmlns:so="xalan://org.openelis.gwt.common.SecurityModule"
                xmlns:fn="http://www.w3.org/2005/xpath-functions" 
                extension-element-prefixes="resource"
                version="1.0">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="security">
    <xalan:script lang="javaclass" src="xalan://org.openelis.gwt.common.SecurityUtil"/>
  </xalan:component>
  
  <xalan:component prefix="service">
    <xalan:script lang="javaclass" src="xalan://org.openelis.gwt.server.ServiceUtils"/>
  </xalan:component>
  
  <xalan:component prefix="so">
    <xalan:script lang="javaclass" src="xalan://org.openelis.gwt.common.SecurityModule"/>
  </xalan:component>
  
       <xsl:variable name="language"><xsl:value-of select="doc/locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="doc/props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
  <xsl:template match="doc">
   <xsl:variable name="security" select="service:getSecurity()"/>
<screen id="main" serviceUrl="OpenELISService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		<VerticalPanel style="AppBackground" sizeToWindow="true">
		<AbsolutePanel style="topMenuBar">
		<menuPanel layout="horizontal" xsi:type="Panel" style="topBarItemHolder" spacing="0" padding="0">
		    <menuItem>
		        <menuDisplay>
			    	  <label style="topMenuBarItem" text="{resource:getString($constants,'application')}" hover="Hover"/>
				</menuDisplay>
				  <menuPanel style="topMenuContainer" layout="vertical" xsi:type="Panel" position="below">
				    <menuItem enabled="false" key="preference" icon="preferenceIcon" label="{resource:getString($constants,'preference')}" description="{resource:getString($constants,'preferenceDescription')}"/>
				    <menuItem key="FavoritesMenu" icon="favoritesMenuIcon" label="{resource:getString($constants,'favoritesMenu')}" description="{resource:getString($constants,'favoritesMenuDescription')}"/>
				    <menuItem key="Logout" icon="logoutIcon" label="{resource:getString($constants,'logoutMenu')}" description="{resource:getString($constants,'logoutDescription')}"/>
				  </menuPanel>
		    </menuItem>
		    <menuItem>
		      <menuDisplay>					
					<label style="topMenuBarItem" text="{resource:getString($constants,'edit')}" hover="Hover"/>
			  </menuDisplay>
	            <menuPanel style="topMenuContainer" layout="vertical" position="below">
				    <menuItem enabled="false" key="cut" icon="cutIcon" label="{resource:getString($constants,'cut')}" description="{resource:getString($constants,'copyDescription')}"/>
				    <menuItem enabled="false" key="copy" icon="copyIcon" label="{resource:getString($constants,'copy')}" description="{resource:getString($constants,'copyDescription')}"/>
 					<menuItem enabled="flase" key="paste" icon="pasteIcon" label="{resource:getString($constants,'paste')}" description="{resource:getString($constants,'pasteDescription')}"/>
			    </menuPanel>
			</menuItem>
    	    <menuItem>
    	      <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'sample')}" hover="Hover" />
		      </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
<!--				<menuItem key="fullLogin" icon="fullLoginIcon" label="{resource:getString($constants,'fullLogin')}" description="{resource:getString($constants,'fullLoginDescription')}"/> -->
				    <menuItem enabled="false" key="quickEntry" icon="quickEntryIcon" label="{resource:getString($constants,'quickEntry')}" description="{resource:getString($constants,'quickEntryDescription')}"/>
<!--				<menuItem key="secondEntry" icon="secondEntryIcon" label="{resource:getString($constants,'secondEntry')}" description="{resource:getString($constants,'secondEntryDescription')}"/> -->
					<menuItem enabled="false" key="tracking" icon="trackingIcon" label="{resource:getString($constants,'tracking')}" description="{resource:getString($constants,'trackingDescription')}"/>
					<html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
					
				   <menuItem key="environmentalSampleLogin" icon="environmentalSampleLoginIcon" label="{resource:getString($constants,'environmentalSampleLogin')}" description="{resource:getString($constants,'environmentalSampleLoginDescription')}"/>
				   <menuItem key="clinicalSampleLogin" icon="clinicalSampleLoginIcon" label="{resource:getString($constants,'clinicalSampleLogin')}" description="{resource:getString($constants,'clinicalSampleLoginDescription')}"/>
				   <menuItem key="animalSampleLogin" icon="animalSampleLoginIcon" label="{resource:getString($constants,'animalSampleLogin')}" description="{resource:getString($constants,'animalSampleLoginDescription')}"/>
				   <menuItem key="newbornScreeningSampleLogin" icon="newbornScreeningSampleLoginIcon" label="{resource:getString($constants,'newbornScreeningSampleLogin')}" description="{resource:getString($constants,'newbornScreeningSampleLoginDescription')}"/>
				   <menuItem key="ptSampleLogin" icon="ptSampleLoginIcon" label="{resource:getString($constants,'ptSampleLogin')}" description="{resource:getString($constants,'ptSampleLoginDescription')}"/>
				   <menuItem key="sdwisSampleLogin" icon="sdwisSampleLoginIcon" label="{resource:getString($constants,'sdwisSampleLogin')}" description="{resource:getString($constants,'sdwisSampleLoginDescription')}"/>
				   <menuItem key="privateWellWaterSampleLogin" icon="privateWellWaterSampleLoginIcon" label="{resource:getString($constants,'privateWellWaterSampleLogin')}" description="{resource:getString($constants,'privateWellWaterSampleLoginDescription')}"/>
				   <!-- <menuItem key="sampleManagement" icon="sampleManagementIcon" label="{resource:getString($constants,'sampleManagement')}" description="{resource:getString($constants,'sampleManagementDescription')}"/>  -->
					<html panelWidth="100%" showError="false">&lt;hr/&gt;</html>

				    <menuItem key="project" icon="projectIcon" label="{resource:getString($constants,'project')}" description="{resource:getString($constants,'projectDescription')}"/>
				    <xsl:if test="security:hasModule($security,'provider','SELECT')">				  
				      <menuItem key="provider" icon="providerIcon" label="{resource:getString($constants,'provider')}" description="{resource:getString($constants,'providerDescription')}"/>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'organization','SELECT')">
				      <menuItem key="organization" icon="organizationIcon" label="{resource:getString($constants,'organization')}" description="{resource:getString($constants,'organizationDescription')}"/>
				    </xsl:if>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" hover="Hover"/>
		      </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <menuItem enabled="false" key="worksheetCreation" icon="worksheetCreationIcon" label="{resource:getString($constants,'worksheetCreation')}" description="{resource:getString($constants,'worksheetCreationDescription')}"/>
				    <menuItem enabled="false" key="worksheetCompletion" icon="worksheetCompletionIcon" label="{resource:getString($constants,'worksheetCompletion')}" description="{resource:getString($constants,'worksheetCompletionDescription')}"/>
				    <menuItem enabled="false" key="addOrCancel" icon="addOrCancelIcon" label="{resource:getString($constants,'addOrCancel')}" description="{resource:getString($constants,'addOrCancelDescription')}"/>
				    <menuItem enabled="flase" key="reviewAndRelease" icon="reviewAndReleaseIcon" label="{resource:getString($constants,'reviewAndRelease')}" description="{resource:getString($constants,'reviewAndReleaseDescription')}"/>
				    <menuItem enabled="false" key="toDo" icon="toDoIcon" label="{resource:getString($constants,'toDo')}" description="{resource:getString($constants,'toDoDescription')}"/>
				    <menuItem enabled="false" key="labelFor" icon="labelForIcon" label="{resource:getString($constants,'labelFor')}" description="{resource:getString($constants,'labelForDescription')}"/>
				    <menuItem enabled="false" key="storage" icon="storageIcon" label="{resource:getString($constants,'storage')}" description="{resource:getString($constants,'storageDescription')}"/>
				    <menuItem key="QC" icon="QCIcon" label="{resource:getString($constants,'QC')}" description="{resource:getString($constants,'QCDescription')}"/>
				</menuPanel>
			</menuItem>
	        <menuItem>  
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" hover="Hover" />
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <menuItem key="internalOrder" icon="internalOrderIcon" label="{resource:getString($constants,'internalOrder')}" description="{resource:getString($constants,'internalOrderDescription')}"/>
			        <menuItem key="vendorOrder" icon="vendorOrderIcon" label="{resource:getString($constants,'vendorOrder')}" description="{resource:getString($constants,'vendorOrderDescription')}"/>
				    <menuItem key="kitOrder" icon="kitOrderIcon" label="{resource:getString($constants,'kitOrder')}" description="{resource:getString($constants,'kitOrderDescription')}"/>
				    <html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
				    <menuItem key="fillOrder" icon="fillOrderIcon" label="{resource:getString($constants,'fillOrder')}" description="{resource:getString($constants,'fillOrderDescription')}"/>
				    <menuItem key="shipping" icon="shippingIcon" label="{resource:getString($constants,'shipping')}" description="{resource:getString($constants,'shippingDescription')}"/>
				    <html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
				    <menuItem key="buildKits" icon="buildKitsIcon" label="{resource:getString($constants,'buildKits')}" description="{resource:getString($constants,'buildKitsDescription')}"/>
				    <menuItem key="inventoryTransfer" icon="inventoryTransferIcon" label="{resource:getString($constants,'inventoryTransfer')}" description="{resource:getString($constants,'inventoryTransferDescription')}"/>
				    <html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
				    <menuItem key="inventoryReceipt" icon="inventoryReceiptIcon" label="{resource:getString($constants,'inventoryReceipt')}" description="{resource:getString($constants,'inventoryReceiptDescription')}"/>
				    <menuItem key="inventoryAdjustment" icon="inventoryAdjustmentIcon" label="{resource:getString($constants,'inventoryAdjustment')}" description="{resource:getString($constants,'inventoryAdjustmentDescription')}"/>
				    <xsl:if test="security:hasModule($security,'inventory','SELECT')">			
    				    <menuItem key="inventoryItem" icon="inventoryItemIcon" label="{resource:getString($constants,'inventoryItem')}" description="{resource:getString($constants,'inventoryItemDescription')}"/>
				    </xsl:if>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'instrument')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <menuItem enabled="false" key="instrument" icon="instrumentIcon" label="{resource:getString($constants,'instrument')}" description="{resource:getString($constants,'instrumentDescription')}"/>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'maintenance')}" hover="Hover" />
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <menuItem key="test" icon="testIcon" label="{resource:getString($constants,'test')}" description="{resource:getString($constants,'testDescription')}"/>
				    <menuItem key="method" icon="methodIcon" label="{resource:getString($constants,'method')}" description="{resource:getString($constants,'methodDescription')}"/>
				    <menuItem key="panel" icon="panelIcon" label="{resource:getString($constants,'panel')}" description="{resource:getString($constants,'panelDescription')}"/>
				    <xsl:if test="security:hasModule($security,'qaevent','SELECT')">
 				    	<menuItem key="QAEvent" icon="QAEventIcon" label="{resource:getString($constants,'QAEvent')}" description="{resource:getString($constants,'QAEventDescription')}"/>
				    </xsl:if>
				    <menuItem key="labSection" icon="labSectionIcon" label="{resource:getString($constants,'labSection')}" description="{resource:getString($constants,'labSectionDescription')}"/>
				   <html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
				    <xsl:if test="security:hasModule($security,'analyte','SELECT')">
				    	<menuItem key="analyte" icon="analyteIcon" label="{resource:getString($constants,'analyte')}" description="{resource:getString($constants,'analyteDescription')}"/>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'dictionary','SELECT')">
				    	<menuItem key="dictionary" icon="dictionaryIcon" label="{resource:getString($constants,'dictionary')}" description="{resource:getString($constants,'dictionaryDescription')}"/>
				    </xsl:if>
				    <menuItem key="auxiliaryPrompt" icon="auxiliaryPromptIcon" label="{resource:getString($constants,'auxiliaryPrompt')}" description="{resource:getString($constants,'auxiliaryPromptDescription')}"/>
					<html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
			        <xsl:if test="security:hasModule($security,'label','SELECT')">
				    	<menuItem key="label" icon="labelIcon" label="{resource:getString($constants,'label')}" description="{resource:getString($constants,'labelDescription')}"/>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'standardnote','SELECT')">			    
				    	<menuItem key="standardNote" icon="standardNoteIcon" label="{resource:getString($constants,'standardNote')}" description="{resource:getString($constants,'standardNoteDescription')}"/>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'testtrailer','SELECT')">
				        <menuItem key="trailerForTest" icon="trailerForTestIcon" label="{resource:getString($constants,'trailerForTest')}" description="{resource:getString($constants,'trailerForTestDescription')}"/>
				    </xsl:if>
					<html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
			        <xsl:if test="security:hasModule($security,'storageunit','SELECT')">
					    <menuItem key="storageUnit" icon="storageUnitIcon" label="{resource:getString($constants,'storageUnit')}" description="{resource:getString($constants,'storageUnitDescription')}"/>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'storagelocation','SELECT')">
					    <menuItem key="storageLocation" icon="storageLocationIcon" label="{resource:getString($constants,'storageLocation')}" description="{resource:getString($constants,'storageLocationDescription')}"/>
				    </xsl:if>
					<html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
				    <menuItem enabled="false" key="instrument" icon="instrumentIcon" label="{resource:getString($constants,'instrument')}" description="{resource:getString($constants,'instrumentDescription')}"/>
					<html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
				    <menuItem enabled="false" key="scriptlet" icon="scriptletIcon" label="{resource:getString($constants,'scriptlet')}" description="{resource:getString($constants,'scriptletDescription')}"/>
				    <xsl:if test="security:hasModule($security,'systemvariable','SELECT')">
					    <menuItem key="systemVariable" icon="systemVariableIcon" label="{resource:getString($constants,'systemVariable')}" description="{resource:getString($constants,'systemVariableDescription')}"/>
				    </xsl:if>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'report')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <menuItem enabled="false" key="finalReport" icon="finalReportIcon" label="{resource:getString($constants,'finalReport')}" description="{resource:getString($constants,'finalReportDescription')}"/>
				    <menuItem enabled="false" key="sampleDataExport" icon="sampleDataExportIcon" label="{resource:getString($constants,'sampleDataExport')}" description="{resource:getString($constants,'sampleDataExportDescription')}"/>
				    <menuItem enabled="false" key="loginLabel" icon="loginLabelIcon" label="{resource:getString($constants,'loginLabel')}" description="{resource:getString($constants,'loginLabelDescription')}"/>
					<html panelWidth="100%" showError="false">&lt;hr/&gt;</html>
					<menuItem style="TopMenuRowContainer" 
						      hover="Hover"
						      icon="referenceIcon"
						      label="{resource:getString($constants,'reference')}"
						      description="">					
						  <menuPanel layout="vertical" style="topMenuContainer" position="side">
				            <menuItem enabled="false" key="organizationRef" icon="organizationIcon" label="{resource:getString($constants,'organization')}" description="{resource:getString($constants,'organizationDescription')}"/>
				    <menuItem enabled="false" key="testRef" icon="testIconRef" label="{resource:getString($constants,'test')}" description="{resource:getString($constants,'testDescription')}"/>
				    <menuItem enabled="false" key="QAEventRef" icon="QAEventIconRef" label="{resource:getString($constants,'QAEvent')}" description="{resource:getString($constants,'QAEventDescription')}"/>
				  </menuPanel>
					</menuItem>
					<menuItem style="TopMenuRowContainer" 
						      hover="Hover"
						      icon="summaryIcon"
						      label="{resource:getString($constants,'summary')}"
						      description="">
							<menuPanel layout="vertical" style="topMenuContainer" position="side">
				    <menuItem enabled="false" key="QAByOrganization" icon="QAByOrganizationIcon" label="{resource:getString($constants,'QAByOrganization')}" description="{resource:getString($constants,'QAByOrganizationDescription')}"/>
				    <menuItem enabled="false" key="testCountByFacility" icon="testCountByFacilityIcon" label="{resource:getString($constants,'testCountByFacility')}" description="{resource:getString($constants,'testCountByFacilityDescription')}"/>
				    <menuItem enabled="false" key="turnaround" icon="turnaroundIcon" label="{resource:getString($constants,'turnaround')}" description="{resource:getString($constants,'turnaroundDescription')}"/>
				    <menuItem enabled="false" key="positiveTestCount" icon="positiveTestCountIcon" label="{resource:getString($constants,'positiveTestCount')}" description="{resource:getString($constants,'positiveTestCountDescription')}"/>
						</menuPanel>
					 </menuItem>
				</menuPanel>
		    </menuItem>
        </menuPanel>
       </AbsolutePanel>
       <HorizontalPanel>
          <VerticalPanel key="favoritesPanel" visible="false" width="230px" style="favoritesMenuContainer">
            <HorizontalPanel style="FavHeader" width="100%" height="20px">
                <text style="ScreenWindowLabel">Favorites</text>
              <widget halign="right">
                <appButton key="EditFavorites">
	              <AbsolutePanel style="EditSettings"/>
	            </appButton>
	          </widget>
            </HorizontalPanel>
	      </VerticalPanel>
    	<winbrowser key="browser" sizeToWindow="true"/>
		</HorizontalPanel>
	</VerticalPanel>
</screen>
  </xsl:template> 				      
   				      
</xsl:stylesheet>
