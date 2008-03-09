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
				<widget>
					<label key="application" style="topMenuBarItem" text="{resource:getString($constants,'application')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="edit" style="topMenuBarItem" text="{resource:getString($constants,'edit')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="sample" style="topMenuBarItem" text="{resource:getString($constants,'sample')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="analysis" style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="inventoryOrder" style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="instrument" style="topMenuBarItem" text="{resource:getString($constants,'instrument')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="maintenance" style="topMenuBarItem" text="{resource:getString($constants,'maintenance')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="report" style="topMenuBarItem" text="{resource:getString($constants,'report')}" onClick="this" hover="Hover"/>
				</widget>
			</panel>
			</panel>
			
			<panel height="100%" layout="horizontal" width="100%" xsi:type="Panel">
				<panel layout="vertical" key="leftMenuPanel" xsi:type="Panel" width="150px">
					<panel layout="table" xsi:type="Table" style="FavoritesTitlePanel" width="150px">
					<row>
						<widget>
							<label key="headerText" text="{resource:getString($constants,'favorites')}"/>
						</widget>
						<widget halign="right">
							<appButton action="editFavs" onclick="this" style="FavHeaderButton" key="editFavsButton">
								<panel xsi:type="Panel" layout="horizontal">
						             <widget>
                						<text><xsl:value-of select='resource:getString($constants,"edit")'/></text>
							         </widget>
							    </panel>
						   </appButton>
					</widget>
				</row>
			</panel>
					<panel key="leftMenuPanelContainer" layout="vertical" style="topMenuContainer" width="150px" xsi:type="Panel" padding="0" spacing="0"/>
				</panel>
				<panel layout="vertical" width="100%" xsi:type="Panel">
					<widget>
						<winbrowser key="browser" sizeToWindow="true"/>
					</widget>
				</panel>
			</panel>
		</panel>
		<!-- application panel -->
			<menuPopupPanel key="applicationPanel" autoHide="true" hidden="true" popup="this">
				<panel key="applicationPanelTable" style="topMenuContainer" layout="vertical" xsi:type="Panel" spacing="0" padding="0">
				<panel key="preferenceRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" onPanelClick="this">
					<row>
					<!--icon-->
					<widget  style="topMenuIcon">
						<html key="preferenceIcon" style="preferenceIcon" xml:space="preserve"> </html>
					</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="preferenceLabel" style="topMenuItemTitle" text="{resource:getString($constants,'preference')}"/>
							</widget>
							<widget>
								<label key="preferenceDescription" wordwrap="true" style="topMenuitemDesc" text="{resource:getString($constants,'preferenceDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="favoritesMenuRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" onPanelClick="this">
					<row>
					<!--icon-->
					<widget  style="topMenuIcon">
						<html key="favoritesMenuIcon" style="favoritesMenuIcon" xml:space="preserve"> </html>
					</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<check key="showLeftMenu" onClick="this" style="topMenuItemTitle" value="windowPanelTable:0" ><xsl:value-of select='resource:getString($constants,"favoritesMenu")'/></check>
								<!--<label key="favoritesMenuLabel" style="topMenuItemTitle" text="{resource:getString($constants,'favoritesMenu')}"/>-->
							</widget>
							<widget>
								<label key="favoritesMenuDescription" wordwrap="true" style="topMenuitemDesc" text="{resource:getString($constants,'favoritesMenuDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="logoutRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" onPanelClick="this">
					<row>
					<!--icon-->
					<widget  style="topMenuIcon">
						<html key="logoutIcon" style="logoutIcon" xml:space="preserve"> </html>
					</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
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
			<!-- edit panel -->
			<menuPopupPanel key="editPanel" autoHide="true" hidden="true" popup="this">
				<panel key="editPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="cutRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" onPanelClick="this">
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
					<panel key="copyRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" onPanelClick="this">
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
				<panel key="pasteRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" onPanelClick="this">
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
			
			<!-- sample panel-->
			<menuPopupPanel key="samplePanel" autoHide="true" hidden="true" popup="this">
				<panel key="samplePanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
					<panel key="fullLoginRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" hover="Hover" onPanelClick="this">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="fullLoginIcon" style="fullLoginIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="fullLoginLabel" style="topMenuItemTitle" text="{resource:getString($constants,'fullLogin')}"/>
							</widget>
							<widget>
								<label key="fullLoginDescription" wordwrap="true" style="topMenuitemDesc" text="{resource:getString($constants,'fullLoginDescription')}"/>
						</widget>
						</panel>
						</widget>
						</row>
					</panel>
					<panel key="quickEntryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="quickEntryIcon" style="quickEntryIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="quickEntryLabel" style="topMenuItemTitle" text="{resource:getString($constants,'quickEntry')}"/>
							</widget>
							<widget>
								<label key="quickEntryDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'quickEntryDescription')}"/>
						</widget>
						</panel>
						</widget>
						</row>
					</panel>
					<panel key="secondEntryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="secondEntryIcon" style="secondEntryIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="secondEntryLabel" style="topMenuItemTitle" text="{resource:getString($constants,'secondEntry')}"/>
							</widget>
							<widget>
								<label key="secondEntryDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'secondEntryDescription')}"/>
						</widget>
						</panel>
						</widget>
						</row>
					</panel>
					<panel key="trackingRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="trackingIcon" style="trackingIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="trackingLabel" style="topMenuItemTitle" text="{resource:getString($constants,'tracking')}"/>
							</widget>
							<widget>
								<label key="trackingDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'trackingDescription')}"/>
						</widget>
						</panel>
						</widget>
						</row>
					</panel>
					<!-- horizontal spacer-->
					<html key="spacer">&lt;hr/&gt;</html>
					<panel key="projectRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
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
								<label key="projectDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'projectDescription')}"/>
						</widget>
						</panel>
						</widget>
						</row>
					</panel>
					<panel key="providerRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover" value="ProviderScreen">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="providerIcon" style="providerIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
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
					<panel key="organizationRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover" value="OrganizationScreen">
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
				</panel>
			</menuPopupPanel>
			
			<!-- Analysis panel-->
			<menuPopupPanel key="analysisPanel" autoHide="true" hidden="true" popup="this">
				<panel key="analysisPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="worksheetCreationRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="worksheetCreationIcon" style="worksheetCreationIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="worksheetCreationLabel" style="topMenuItemTitle" text="{resource:getString($constants,'worksheetCreation')}"/>
							</widget>
							<widget>
								<label key="worksheetCreationDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'worksheetCreationDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="worksheetCompletionRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="worksheetCompletionIcon" style="worksheetCompletionIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel"> 
							<widget>
								<label key="worksheetCompletionLabel" style="topMenuItemTitle" text="{resource:getString($constants,'worksheetCompletion')}"/>
							</widget>
							<widget>
								<label key="worksheetCompletionDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'worksheetCompletionDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="addOrCancelRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="addOrCancelIcon" style="addOrCancelIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="addOrCancelLabel" style="topMenuItemTitle" text="{resource:getString($constants,'addOrCancel')}"/>
							</widget>
							<widget>
								<label key="addOrCancelDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'addOrCancelDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="reviewAndReleaseRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="reviewAndReleaseIcon" style="reviewAndReleaseIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="reviewAndReleaseLabel" style="topMenuItemTitle" text="{resource:getString($constants,'reviewAndRelease')}"/>
							</widget>
							<widget>
								<label key="reviewAndReleaseDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'reviewAndReleaseDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="toDoRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="toDoIcon" style="toDoIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="toDoLabel" style="topMenuItemTitle" text="{resource:getString($constants,'toDo')}"/>
							</widget>
							<widget>
								<label key="toDoDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'toDoDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="labelForRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="labelForIcon" style="labelForIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="labelForLabel" style="topMenuItemTitle" text="{resource:getString($constants,'labelFor')}"/>
							</widget>
							<widget>
								<label key="labelForDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'labelForDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="storageRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
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
					<panel key="QCRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="QCIcon" style="QCIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="QCLabel" style="topMenuItemTitle" text="{resource:getString($constants,'QC')}"/>
							</widget>
							<widget>
								<label key="QCDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'QCDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			<!-- inventory Order panel-->
			<menuPopupPanel key="inventoryOrderPanel" autoHide="true" hidden="true" popup="this">
				<panel key="inventoryOrderPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="orderRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
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
					<panel key="inventoryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
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
				</panel>
			</menuPopupPanel>
			
			<!-- instrument panel-->
			<menuPopupPanel key="instrumentPanel" autoHide="true" hidden="true" popup="this">
				<panel key="instrumentTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="instrumentRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
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
								<label key="instrumentDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'instrumentDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			<!-- maintenance panel-->
			<menuPopupPanel key="maintenancePanel" autoHide="true" hidden="true" popup="this">
				<panel key="maintenancePanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="testRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
				<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="testIcon" style="testIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="testLabel" style="topMenuItemTitle" text="{resource:getString($constants,'test')}"/>
							</widget>
							<widget>
								<label key="testDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'testDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="methodRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="methodIcon" style="methodIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
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
					<panel key="panelRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="panelIcon" style="panelIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="panelLabel" style="topMenuItemTitle" text="{resource:getString($constants,'panel')}"/>
							</widget>
							<widget>
								<label key="panelDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'panelDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="QAEventRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover" value="QAEventScreen">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="QAEventIcon" style="QAEventIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="QAEventLabel" style="topMenuItemTitle" text="{resource:getString($constants,'QAEvent')}"/>
							</widget>
							<widget>
								<label key="QAEventDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'QAEventDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="labSectionRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="labSectionIcon" style="labSectionIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="labSectionLabel" style="topMenuItemTitle" text="{resource:getString($constants,'labSection')}"/>
							</widget>
							<widget>
								<label key="labSectionDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'labSectionDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<!-- horizontal spacer-->
					<html key="spacer">&lt;hr/&gt;</html>
					<panel key="analyteRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="analyteIcon" style="analyteIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
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
					<panel key="dictionaryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover" value="DictionaryScreen">
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
					<panel key="auxiliaryPromptRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="auxiliaryPromptIcon" style="auxiliaryPromptIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="auxiliaryPromptLabel" style="topMenuItemTitle" text="{resource:getString($constants,'auxiliaryPrompt')}"/>
							</widget>
							<widget>
								<label key="auxiliaryPromptDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'auxiliaryPromptDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<!-- horizontal spacer-->
					<html key="spacer">&lt;hr/&gt;</html>
					<panel key="barcodeLabelRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="barcodeLabelIcon" style="barcodeLabelIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="barcodeLabelLabel" style="topMenuItemTitle" text="{resource:getString($constants,'barcodeLabel')}"/>
							</widget>
							<widget>
								<label key="barcodeLabelDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'barcodeLabelDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="standardNoteRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
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
					<panel key="trailerForTestRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="trailerForTestIcon" style="trailerForTestIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="trailerForTestLabel" style="topMenuItemTitle" text="{resource:getString($constants,'trailerForTest')}"/>
							</widget>
							<widget>
								<label key="trailerForTestDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'trailerForTestDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<!-- horizontal spacer-->
					<html key="spacer">&lt;hr/&gt;</html>
					<panel key="storageUnitRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover" value="StorageUnitScreen">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="storageUnitIcon" style="storageUnitIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
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
					<panel key="storageLocationRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover" value="StorageLocationScreen">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="storageLocationIcon" style="storageLocationIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="storageLocationLabel" style="topMenuItemTitle" text="{resource:getString($constants,'storageLocation')}"/>
							</widget>
							<widget>
								<label key="storageLocationDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'storageLocationDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<!-- horizontal spacer-->
					<html key="spacer">&lt;hr/&gt;</html>
					<panel key="instrumentMaintRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="instrumentIcon" style="instrumentIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="instrumentLabel" style="topMenuItemTitle" text="{resource:getString($constants,'instrument')}"/>
							</widget>
							<widget>
								<label key="instrumentDescription" wordwrap="true" style="topMenuItemDesc" text="{resource:getString($constants,'instrumentMainDescription')}"/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<!-- horizontal spacer-->
					<html key="spacer">&lt;hr/&gt;</html>
					<panel key="scriptletRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
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
					<panel key="systemVariableRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
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
			
			<!-- report panel -->
			<menuPopupPanel key="reportPanel" autoHide="true" hidden="true" popup="this">
				<panel key="reportPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="finalReportRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="finalReportIcon" style="finalReportIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="finalReportLabel" style="topMenuItemTitle" text="{resource:getString($constants,'finalReport')}"/>
							</widget>
							<widget>
								<label key="finalReportDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
						</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="sampleDataExportRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="sampleDataExportIcon" style="sampleDataExportIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="sampleDataExportLabel" style="topMenuItemTitle" text="{resource:getString($constants,'sampleDataExport')}"/>
							</widget>
							<widget>
								<label key="sampleDataExportDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="loginLabelRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="loginLabelIcon" style="loginLabelIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="loginLabelLabel" style="topMenuItemTitle" text="{resource:getString($constants,'loginLabel')}"/>
							</widget>
							<widget>
								<label key="loginLabelDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<!-- horizontal spacer-->
					<html key="spacer">&lt;hr/&gt;</html>
					<panel key="referenceRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="referenceIcon" style="referenceIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="referenceLabel" style="topMenuItemTitle" text="{resource:getString($constants,'reference')}"/>
							</widget>
							<widget>
								<label key="referenceDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
						<widget style="topMenuArrowIcon">
								<html key="arrowIcon" style="arrowIcon" xml:space="preserve"> </html>
						</widget>
					</row>
					</panel>
					<panel key="summaryRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="summaryIcon" style="summaryIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="summaryLabel" style="topMenuItemTitle" text="{resource:getString($constants,'summary')}"/>
							</widget>
							<widget>
								<label key="summaryDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
						<widget style="topMenuArrowIcon">
								<html key="arrowIcon" style="arrowIcon" xml:space="preserve"> </html>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			<!-- report reference popup panel -->
			<menuPopupPanel key="reportReferencePanel" autoHide="true" hidden="true" popup="this">
				<panel key="reportReferencePanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="organizationReportRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
				<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="organizationReportIcon" style="organizationReportIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="organizationReportLabel" style="topMenuItemTitle" text="{resource:getString($constants,'organization')}"/>
							</widget>
							<widget>
								<label key="organizationReportDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="testReportRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="testReportIcon" style="testReportIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="testReportLabel" style="topMenuItemTitle" text="{resource:getString($constants,'test')}"/>
							</widget>
							<widget>
								<label key="testReportDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="qaEventReportRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="qaEventReportIcon" style="qaEventReportIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="qaEventReportLabel" style="topMenuItemTitle" text="{resource:getString($constants,'QAEvent')}"/>
							</widget>
							<widget>
								<label key="qaEventReportDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
				</panel>
			</menuPopupPanel>
			
			<!-- report summary popup panel -->
			<menuPopupPanel key="reportSummaryPanel" autoHide="true" hidden="true" popup="this">
				<panel key="reportSummaryPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<panel key="QAByOrganizationRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
				<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="QAByOrganizationIcon" style="QAByOrganizationIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="QAByOrganizationLabel" style="topMenuItemTitle" text="{resource:getString($constants,'QAByOrganization')}"/>
							</widget>
							<widget>
								<label key="QAByOrganizationDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="testCountByFacilityRow" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="testCountByFacilityIcon" style="sampleDataExportIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="sampleDataExportLabel" style="topMenuItemTitle" text="{resource:getString($constants,'testCountByFacility')}"/>
							</widget>
							<widget>
								<label key="sampleDataExportDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="6" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="loginLabelIcon" style="loginLabelIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="loginLabelLabel" style="topMenuItemTitle" text="{resource:getString($constants,'turnaround')}"/>
							</widget>
							<widget>
								<label key="loginLabelDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
							</widget>
						</panel>
						</widget>
					</row>
					</panel>
					<panel key="7" layout="table" xsi:type="Table" style="TopMenuRowContainer" onPanelClick="this" hover="Hover">
					<row>
						<!--icon-->
							<widget  style="topMenuIcon">
								<html key="referenceIcon" style="referenceIcon" xml:space="preserve"> </html>
							</widget>
						<!-- title and description -->
						<widget  style="topMenuItemMiddle">
						<panel layout="vertical" xsi:type="Panel">
							<widget>
								<label key="referenceLabel" style="topMenuItemTitle" text="{resource:getString($constants,'positiveTestCount')}"/>
							</widget>
							<widget>
								<label key="referenceDesc" wordwrap="true" style="topMenuItemDesc" text=""/>
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