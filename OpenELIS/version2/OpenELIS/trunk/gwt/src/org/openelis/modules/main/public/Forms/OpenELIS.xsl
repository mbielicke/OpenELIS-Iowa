<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc">
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="main" serviceUrl="OpenELISService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display  constants="OpenELISConstants">
		<panel height="100%" layout="vertical" style="AppBackground" width="100%" xsi:type="Panel">
		<panel key="topMenu" layout="horizontal" style="topMenuBar" xsi:type="Panel">
			<panel key="topBarItemHolder" layout="horizontal" xsi:type="Panel">
				<!--<widget>
					<label key="file" style="topMenuBarItem" text="{resource:getString($constants,'file')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>-->
				<widget>
					<label key="edit" style="topMenuBarItem" text="{resource:getString($constants,'edit')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="sampleManagement" style="topMenuBarItem" text="{resource:getString($constants,'sample')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="analysisManagement" style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="supplyManagement" style="topMenuBarItem" text="{resource:getString($constants,'supply')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="reports" style="topMenuBarItem" text="{resource:getString($constants,'reports')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="dataEntry" style="topMenuBarItem" text="{resource:getString($constants,'dataEntry')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="utilities" style="topMenuBarItem" text="{resource:getString($constants,'utilities')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="favorites" style="topMenuBarItem" text="{resource:getString($constants,'favorites')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="window" style="topMenuBarItem" text="{resource:getString($constants,'window')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="help" style="topMenuBarItem" text="{resource:getString($constants,'help')}" onClick="this" hover="Hover"/>
				</widget>
			</panel>
			</panel>
			
			<panel height="100%" layout="horizontal" width="100%" xsi:type="Panel">
				<panel layout="vertical" key="leftMenuPanel" xsi:type="Panel">
					<html key="headerBar" style="TitlePanel">Favorites</html>
					<panel key="leftMenuPanelContainer" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0"/>
				</panel>
				<panel layout="vertical" width="100%" xsi:type="Panel">
					<widget>
						<winbrowser key="browser" sizeToWindow="true"/>
					</widget>
				</panel>
			</panel>
		</panel>
		<!-- file panel -->
		<!--	<menuPopupPanel key="filePanel" autoHide="true" hidden="true" popup="this">
				<panel key="filePanelTable" style="topMenuContainer" layout="vertical" xsi:type="Panel" spacing="0" padding="0">
				<panel key="logoutRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" mouse="this">
					<row>-->
					<!--icon-->
					<!--<widget  style="topMenuIcon">
						<html key="logoutIcon" style="logoutIcon" xml:space="preserve"> </html>
					</widget>-->
						<!-- title and description -->
						<!--<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="logoutLabel" style="topMenuItemTitle" text="{resource:getString($constants,'logout')}"/>
							</widget>
							<widget>
								<label key="logoutDescription" wordwrap="true" style="topMenuitemDesc" text="{resource:getString($constants,'logoutDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			-->
			<!-- edit panel -->
			<menuPopupPanel key="editPanel" autoHide="true" hidden="true" popup="this">
				<panel key="editPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="cutRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" mouse="this">
					<row>
				<!--icon-->
					<widget  style="topMenuIcon">
						<html key="cutIcon" style="cutIcon" xml:space="preserve"> </html>
					</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="cutLabel" style="topMenuItemTitle" text="{resource:getString($constants,'cut')}"/>
							</widget>
							<widget>
								<label key="cutDescription" wordwrap="true" style="topMenuitemDesc" text=""/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="copyRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" mouse="this">
					<row>
					<!--icon-->
					<widget  style="topMenuIcon">
						<html key="copyIcon" style="copyIcon" xml:space="preserve"> </html>
					</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="copyLabel" style="topMenuItemTitle" text="{resource:getString($constants,'copy')}"/>
							</widget>
							<widget>
								<label key="copyDescription" wordwrap="true" style="topMenuitemDesc" value="editPanelTable:1" text=""/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
				<panel key="pasteRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" mouse="this">
				<row>
				<!--icon-->
					<widget  style="topMenuIcon">
						<html key="pasteIcon" style="pasteIcon" xml:space="preserve"> </html>
					</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="pasteLabel" style="topMenuItemTitle" text="{resource:getString($constants,'paste')}"/>
							</widget>
							<widget>
								<label key="pasteDescription" wordwrap="true" style="topMenuitemDesc" text=""/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					</panel>
			</menuPopupPanel>
			
			<!-- sample Management panel-->
			<menuPopupPanel key="sampleManagementPanel" autoHide="true" hidden="true" popup="this">
				<panel key="sampleManagementPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
					<panel key="projectRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" mouse="this">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="projectIcon" style="projectIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="projectLabel" style="topMenuItemTitle" text="{resource:getString($constants,'project')}"/>
							</widget>
							<widget>
								<label key="projectDescription" wordwrap="true" style="topMenuitemDesc" text="{resource:getString($constants,'projectDescription')}"/>
						</widget>
						</panel>
						</widget>
						</row>
					</panel>
					<panel key="releaseRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="releaseIcon" style="releaseIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="releaseLabel" style="topMenuItemTitle" text="{resource:getString($constants,'release')}"/>
							</widget>
							<widget>
								<label key="releaseDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'releaseDescription')}"/>
						</widget>
						</panel>
						</widget>
						</row>
					</panel>
					<panel key="sampleLookupRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="sampleLookupIcon" style="sampleLookupIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="sampleLookupLabel" style="topMenuItemTitle" text="{resource:getString($constants,'sampleLookup')}"/>
							</widget>
							<widget>
								<label key="sampleLookupDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'sampleLookupDescription')}"/>
						</widget>
						</panel>
						</widget>
						</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			<!-- Analysis Management panel-->
			<menuPopupPanel key="analysisManagementPanel" autoHide="true" hidden="true" popup="this">
				<panel key="analysisManagmentPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="analyteRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row style="disabled">
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="analyteIcon" style="analyteIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="analyteLabel" style="topMenuItemTitle" text="{resource:getString($constants,'analyte')}"/>
							</widget>
							<widget>
								<label key="analyteDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'analyteDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="methodRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row style="disabled">
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="methodIcon" style="methodIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel"> 
							<widget>
								<label key="methodLabel" style="topMenuItemTitle" text="{resource:getString($constants,'method')}"/>
							</widget>
							<widget>
								<label key="methodDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'methodDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="methodPanelRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row style="disabled">
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="methodPanelIcon" style="methodPanelIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="methodPanelLabel" style="topMenuItemTitle" text="{resource:getString($constants,'methodPanel')}"/>
							</widget>
							<widget>
								<label key="methodPanelDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'methodPanelDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="qaEventsRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,AnalysisModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="qaEventsIcon" style="qaEventsIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="qaEventsLabel" style="topMenuItemTitle" text="{resource:getString($constants,'qaEvents')}"/>
							</widget>
							<widget>
								<label key="qaEventsDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'qaEventsDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="resultsRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="resultsIcon" style="resultsIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="resultsLabel" style="topMenuItemTitle" text="{resource:getString($constants,'results')}"/>
							</widget>
							<widget>
								<label key="resultsDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'resultsDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<!--<row style="topMenuContainer">
					<widget  halign="center" colspan="3">
						<html value="dataEntryPanelTable:5">&lt;center&gt;&lt;img width=100% height=1px src=&quot;Images/menuspacer.png&quot;&gt;&lt;/center&gt;</html>
					</widget>
					</row>-->
					<panel key="testManagementRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="testManagementIcon" style="testManagementIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="testManagementLabel" style="topMenuItemTitle" text="{resource:getString($constants,'testManagement')}"/>
							</widget>
							<widget>
								<label key="testManagementDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'testManagementDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<!--<row style="topMenuContainer">

					<widget  style="topMenuIcon">
						<html value="dataEntryPanelTable:5">&lt;center&gt;&lt;img width=1px height=1px src=&quot;Images/1x1-trans.gif&quot;&gt;&lt;/center&gt;</html>
					</widget>
					<panel layout="horizontal" xsi:type="Panel" width="100%" height="100%"/>
					<widget  halign="center">
						<html value="dataEntryPanelTable:5">&lt;center&gt;&lt;img width=100% height=1px src=&quot;Images/menuspacer.png&quot;&gt;&lt;/center&gt;</html>
					</widget>
					</row>-->
					<panel key="testTrailerRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="testTrailerIcon" style="testTrailerIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="testTrailerLabel" style="topMenuItemTitle" text="{resource:getString($constants,'testTrailer')}"/>
							</widget>
							<widget>
								<label key="testTrailerDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'testTrailerDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="worksheetsRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="worksheetsIcon" style="worksheetsIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="worksheetsLabel" style="topMenuItemTitle" text="{resource:getString($constants,'worksheets')}"/>
							</widget>
							<widget>
								<label key="worksheetsDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'worksheetsDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			<!-- supply Management panel-->
			<menuPopupPanel key="supplyManagementPanel" autoHide="true" hidden="true" popup="this">
				<panel key="supplyManagementPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="instrumentRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
				<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="instrumentIcon" style="instrumentIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="instrumentLabel" style="topMenuItemTitle" text="{resource:getString($constants,'instrument')}"/>
							</widget>
							<widget>
								<label key="instrumentDescription" wordwrap="true,SupplyModule" style="topMenuItemDesc" text="{resource:getString($constants,'instrumentDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="inventoryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="inventoryIcon" style="inventoryIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="inventoryLabel" style="topMenuItemTitle" text="{resource:getString($constants,'inventory')}"/>
							</widget>
							<widget>
								<label key="inventoryDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'inventoryDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="orderRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="orderIcon" style="orderIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="orderLabel" style="topMenuItemTitle" text="{resource:getString($constants,'order')}"/>
							</widget>
							<widget>
								<label key="orderDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'orderDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="storageRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,SupplyModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="storageIcon" style="storageIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="storageLabel" style="topMenuItemTitle" text="{resource:getString($constants,'storage')}"/>
							</widget>
							<widget>
								<label key="storageDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'storageDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="storageUnitRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,SupplyModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="storageUnitIcon" style="storageUnitIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="storageUnitLabel" style="topMenuItemTitle" text="{resource:getString($constants,'storageUnit')}"/>
							</widget>
							<widget>
								<label key="storageUnitDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'storageUnitDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			<!-- data entry panel-->
			<menuPopupPanel key="dataEntryPanel" autoHide="true" hidden="true" popup="this">
				<panel key="dataEntryPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="fastSampleLoginRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
				<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="fastSampleLoginIcon" style="fastSampleLoginIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="fastSampleLoginLabel" style="topMenuItemTitle" text="{resource:getString($constants,'fastSampleLogin')}"/>
							</widget>
							<widget>
								<label key="fastSampleLoginDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'fastSampleLoginDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="organizationRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,DataEntryModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="organizationIcon" style="organizationIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="organizationLabel" style="topMenuItemTitle" text="{resource:getString($constants,'organization')}"/>
							</widget>
							<widget>
								<label key="organizationDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'organizationDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="patientRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="patientIcon" style="patientIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="patientLabel" style="topMenuItemTitle" text="{resource:getString($constants,'patient')}"/>
							</widget>
							<widget>
								<label key="patientDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'patientDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="personRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="personIcon" style="personIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="personLabel" style="topMenuItemTitle" text="{resource:getString($constants,'person')}"/>
							</widget>
							<widget>
								<label key="personDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'personDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="providerRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,DataEntryModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="providerIcon" style="providerIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="providerLabel" style="topMenuItemTitle" text="{resource:getString($constants,'provider')}"/>
							</widget>
							<widget>
								<label key="providerDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'providerDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="sampleLoginRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="sampleLoginIcon" style="sampleLoginIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="sampleLoginLabel" style="topMenuItemTitle" text="{resource:getString($constants,'sampleLogin')}"/>
							</widget>
							<widget>
								<label key="sampleLoginDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'sampleLoginDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			<!--reports panel -->
			<menuPopupPanel key="reportsPanel" autoHide="true" hidden="true" popup="this">
				<panel key="reportsPanelTable" style="topMenuContainer" layout="vertical" xsi:type="Panel" spacing="0" padding="0">
				<panel key="category1Row" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="category1Icon" onclick="this" mouse="this" style="category1Icon" value="reportsPanelTable:0" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="category1" style="topMenuItemTitle" onClick="this" mouse="this" value="reportsPanelTable:0" text="{resource:getString($constants,'category1')}"/>
							</widget>
							<widget>
								<label key="category1Description" wordwrap="true" style="topMenuItemDesc" onClick="this" mouse="this" value="reportsPanelTable:0" text="{resource:getString($constants,'category1Description')}"/>
							</widget>
						</panel>
						</widget>
						<!-- arrow -->
						<widget style="menuArrowDiv">
								<html key="category1Arrow" onclick="this" mouse="this" value="reportsPanelTable:0" xml:space="preserve"> </html>
							</widget>
					</row>
					</panel>
					<panel key="category2Row" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="category2Icon" onclick="this" mouse="this" style="category2Icon" value="reportsPanelTable:1" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="category2" onClick="this" style="topMenuItemTitle" mouse="this" value="reportsPanelTable:1" text="{resource:getString($constants,'category2')}"/>
							</widget>
							<widget>
								<label key="category2Description" wordwrap="true" onClick="this" style="topMenuItemDesc" mouse="this" value="reportsPanelTable:1" text="{resource:getString($constants,'category2Description')}"/>
							</widget>
						</panel>
						</widget>
						<!-- arrow -->
						<widget style="menuArrowDiv">
								<html key="category2Arrow" onclick="this" mouse="this" value="reportsPanelTable:1" xml:space="preserve"> </html>
							</widget>
					</row>
					</panel>
					<panel key="category3Row" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
			<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="category3Icon" onclick="this" mouse="this" style="category3Icon" value="reportsPanelTable:2" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="category3" onClick="this" style="topMenuItemTitle" mouse="this" value="reportsPanelTable:2" text="{resource:getString($constants,'category3')}"/>
							</widget>
							<widget>
								<label key="category3Description" wordwrap="true" onClick="this" style="topMenuItemDesc" mouse="this" value="reportsPanelTable:2" text="{resource:getString($constants,'category3Description')}"/>
							</widget>
						</panel>
						</widget>
						<!-- arrow -->
							<widget style="menuArrowDiv">
								<html key="category3Arrow" onclick="this" mouse="this" value="reportsPanelTable:2" xml:space="preserve"> </html>
							</widget>
					</row>
					</panel>
					</panel>
			</menuPopupPanel>
			
			<!--reports category subpanel 1-->
			<menuPopupPanel key="reports1SubPanel" autoHide="true" hidden="true" popup="this">
				<panel key="reports1SubPanelTable" style="topMenuContainer" layout="vertical" xsi:type="Panel" spacing="0" padding="0">
				<panel key="report1Row" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="report1" onclick="this" mouse="this" style="report1Icon" value="reports1SubPanelTable:0" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="report1" style="topMenuItemTitle" mouse="this" value="reports1SubPanelTable:0" text="{resource:getString($constants,'report1')}"/>
							</widget>
							<widget>
								<label key="report1Description" wordwrap="true" style="topMenuItemDesc" mouse="this" value="reports1SubPanelTable:0" text="{resource:getString($constants,'report1Description')}"/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="report2Row" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="report2" onclick="this" mouse="this" style="report2Icon" value="reports1SubPanelTable:1" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="report2" style="topMenuItemTitle" mouse="this" value="reports1SubPanelTable:1" text="{resource:getString($constants,'report2')}"/>
							</widget>
							<widget>
								<label key="report2Description" wordwrap="true" style="topMenuItemDesc" mouse="this" value="reports1SubPanelTable:1" text="{resource:getString($constants,'report2Description')}"/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					</panel>
			</menuPopupPanel>
			
			<!-- reports subpanel 2 -->
			<menuPopupPanel key="reports2SubPanel" autoHide="true" hidden="true" popup="this">
				<panel key="reports2SubPanelTable" style="topMenuContainer" layout="vertical" xsi:type="Panel" padding="0" spacing="0">
				<panel key="report3Row" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
				<row>
						<!--icon-->
							<widget  halign="center" style="topMenuIcon">
								<html key="report3" onclick="this" mouse="this" style="report3Icon" value="reports2SubPanelTable:0" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="report1" style="topMenuItemTitle" mouse="this" value="reports2SubPanelTable:0" text="{resource:getString($constants,'report1')}"/>
							</widget>
							<widget>
								<label key="report1Description" wordwrap="true" style="topMenuItemDesc" mouse="this" value="reports1SubPanelTable:0" text="{resource:getString($constants,'report1Description')}"/>
							</widget>
						</panel>
						</widget>
						
					</row>
					</panel>
					<panel key="repoert4Row" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="report4" onclick="this" mouse="this" style="report4Icon" value="reports2SubPanelTable:1" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="report2" style="topMenuItemTitle" mouse="this" value="reports2SubPanelTable:1" text="{resource:getString($constants,'report2')}"/>
							</widget>
							<widget>
								<label key="report1Description" wordwrap="true" style="topMenuItemDesc" mouse="this" value="reports1SubPanelTable:0" text="{resource:getString($constants,'report1Description')}"/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="report5Row" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
				<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="report5" onclick="this" mouse="this" style="report5Icon" value="reports2SubPanelTable:2" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="report3" style="topMenuItemTitle" mouse="this" value="reports2SubPanelTable:2" text="{resource:getString($constants,'report3')}"/>
							</widget>
							<widget>
								<label key="report1Description" wordwrap="true" style="topMenuItemDesc" mouse="this" value="reports1SubPanelTable:0" text="{resource:getString($constants,'report1Description')}"/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					</panel>
			</menuPopupPanel>
		
			<!-- utilities panel-->
			<menuPopupPanel key="utilitiesPanel" autoHide="true" hidden="true" popup="this">
				<panel key="utilitiesPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="auxiliaryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
				<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="auxiliaryIcon" style="auxiliaryIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="auxiliaryLabel" style="topMenuItemTitle" text="{resource:getString($constants,'auxiliary')}"/>
							</widget>
							<widget>
								<label key="auxiliaryDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'auxiliaryDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="dictionaryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,UtilitiesModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="dictionaryIcon" style="dictionaryIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="dictionaryLabel" style="topMenuItemTitle" text="{resource:getString($constants,'dictionary')}"/>
							</widget>
							<widget>
								<label key="dictionaryDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'dictionaryDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="labelRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="labelIcon" style="labelIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="labelLabel" style="topMenuItemTitle" text="{resource:getString($constants,'label')}"/>
							</widget>
							<widget>
								<label key="labelDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'labelDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="referenceTableRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="referenceTableIcon" style="referenceTableIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="referenceTableLabel" style="topMenuItemTitle" text="{resource:getString($constants,'referenceTable')}"/>
							</widget>
							<widget>
								<label key="referenceTableDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'referenceTableDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="scriptletRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="scriptletIcon" style="scriptletIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="scriptletLabel" style="topMenuItemTitle" text="{resource:getString($constants,'scriptlet')}"/>
							</widget>
							<widget>
								<label key="scriptletDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'scriptletDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="sectionRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="sectionIcon" style="sectionIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="sectionLabel" style="topMenuItemTitle" text="{resource:getString($constants,'section')}"/>
							</widget>
							<widget>
								<label key="sectionDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'sectionDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="standardNoteRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,UtilitiesModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="standardNoteIcon" style="standardNoteIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="standardNoteLabel" style="topMenuItemTitle" text="{resource:getString($constants,'standardNote')}"/>
							</widget>
							<widget>
								<label key="standardNoteDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'standardNoteDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="systemVariableRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="systemVariableIcon" style="systemVariableIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="systemVariableLabel" style="topMenuItemTitle" text="{resource:getString($constants,'systemVariable')}"/>
							</widget>
							<widget>
								<label key="systemVariableDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'systemVariableDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			<!-- favorites panel -->
			<menuPopupPanel key="favoritesPanel" autoHide="true" hidden="true" popup="this">
				<panel key="favoritesPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="favDictionaryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,UtilitiesModule" hover="Hover">
				<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="favDictionaryIcon" style="dictionaryIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="favTopDictionary" style="topMenuItemTitle" text="{resource:getString($constants,'dictionary')}"/>
							</widget>
							<widget>
								<label key="favTopDictionaryDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="favOrganizationRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,DataEntryModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="favOrganizationIcon" style="organizationIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="favTopOrganization" style="topMenuItemTitle" text="{resource:getString($constants,'organization')}"/>
							</widget>
							<widget>
								<label key="favTopOrganizationDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="favOrganizeFavoritesRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="favOrganizeFavoritesIcon" style="organizeFavoritesIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="favTopOrganizeFavorites" style="topMenuItemTitle" text="{resource:getString($constants,'organizeFavorites')}"/>
							</widget>
							<widget>
								<label key="favTopOrganizeFavoritesDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="favProvideryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,DataEntryModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="favProviderIcon" style="providerIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="favTopProvider" style="topMenuItemTitle" text="{resource:getString($constants,'provider')}"/>
							</widget>
							<widget>
								<label key="favTopProviderDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="favQaEventsRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="favQaEventsIcon" style="qaEventsIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="favTopqaEvents" style="topMenuItemTitle" text="{resource:getString($constants,'qaEvents')}"/>
							</widget>
							<widget>
								<label key="favTopQaEventsDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="favStandardNoteRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,UtilitiesModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="favStandardNoteIcon" style="standardNoteIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="favTopStandardNote" style="topMenuItemTitle" text="{resource:getString($constants,'standardNote')}"/>
							</widget>
							<widget>
								<label key="favTopStandardNoteDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="favStorageRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,SupplyModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="favStorageIcon" style="storageIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="favTopStorage" style="topMenuItemTitle" text="{resource:getString($constants,'storage')}"/>
							</widget>
							<widget>
								<label key="favTopStorageDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="favStorageUnitRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this,SupplyModule" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="favStorageUnit" style="storageUnit" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="favTopStorageUnit" style="topMenuItemTitle" text="{resource:getString($constants,'storageUnit')}"/>
							</widget>
							<widget>
								<label key="favTopStorageUnitDes" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			<!-- window panel -->
			<menuPopupPanel key="windowPanel" autoHide="true" hidden="true" popup="this">
				<panel key="windowPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0" >
				<panel key="showLeftIconRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
				<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="showLeftIcon" style="showLeftIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
						<widget>
						<check key="showLeftMenu" onClick="this" style="topMenuItemTitle" value="windowPanelTable:0" ><xsl:value-of select='resource:getString($constants,"viewFavoritesMenu")'/></check>
						</widget>
							<widget>
								<label key="showLeftMenuDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'viewFavoritesMenuDescription')}"/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
				<!--	<row>
							<widget colspan="2">
								<html onclick="this" mouse="this" value="analysisManagmentPanelTable:0">&lt;center&gt;&lt;img width=100% height=1px src=&quot;Images/menuspacer.png&quot;&gt;&lt;/center&gt;</html>
							</widget>
					</row>-->
					<panel key="closeCurrentRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="closeCurrentIcon" style="closeCurrentIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
						<widget>
						<label key="closeCurrentWindowLabel" style="topMenuItemTitle" text="{resource:getString($constants,'closeCurrentWindow')}"/>
						</widget>
						<widget>
								<label key="closeCurrentWindowDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="closeAllRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="closeAllIcon" style="closeAllIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
						<widget>
						<label key="closeAllWindowsLabel" style="topMenuItemTitle" text="{resource:getString($constants,'closeAllWindows')}"/>
						</widget>
						<widget>
							<label key="closeAllWindowsDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					</panel>
			</menuPopupPanel>
			
			<!-- help panel-->
			<menuPopupPanel key="helpPanel" autoHide="true" hidden="true" popup="this">
				<panel key="helpPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="indexRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" mouse="this" hover="Hover">
					<row>
						<!--icon-->
							<widget style="topMenuIcon">
								<html key="indexIcon" style="indexIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="indexLabel" style="topMenuItemTitle" text="{resource:getString($constants,'index')}"/>
							</widget>
							<widget>
								<label key="indexDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'indexDescription')}"/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			
	</display>
	<rpc key="display"/>
	<rpc key="query"/>
</screen>
  </xsl:template>
</xsl:stylesheet>