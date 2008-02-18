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
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.client.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="main" serviceUrl="OpenELISService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display  constants="OpenELISConstants">
		<panel height="100%" layout="vertical" style="AppBackground" width="100%" xsi:type="Panel">
		<panel key="topMenu" layout="horizontal" style="topMenuBar" xsi:type="Panel" width="100%">
			<panel key="topBarItemHolder" layout="horizontal" xsi:type="Panel">
				<widget>
					<label key="file" style="topMenuBarItem" text="{resource:getString($constants,'file')}" onClick="this" hover="Hover"/>
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
					<label key="sampleManagement" style="topMenuBarItem" text="{resource:getString($constants,'sampleManagement')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="analysisManagement" style="topMenuBarItem" text="{resource:getString($constants,'analysisManagement')}" onClick="this" hover="Hover"/>
				</widget>
				<widget>
            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
          </widget>
				<widget>
					<label key="supplyManagement" style="topMenuBarItem" text="{resource:getString($constants,'supplyManagement')}" onClick="this" hover="Hover"/>
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
				<panel layout="vertical" key="leftMenuPanel" width="150px" xsi:type="Panel">
					<widget halign="center">
						<menupanel key="menuList" vertical="true"/>
					</widget>
				</panel>
				<panel layout="vertical" width="100%" xsi:type="Panel">
					<widget>
						<winbrowser key="browser" sizeToWindow="true"/>
					</widget>
				</panel>
			</panel>
		</panel>
		
		<!-- file panel -->
			<menuPopupPanel key="filePanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="filePanelTable" style="topMenuPanel" layout="table" xsi:type="Table" spacing="0" padding="0" width="100%">
				<row style="topMenuPanel">
					<!--icon-->
					<widget valign="middle" style="topMenuPanelIconPanel">
						<html key="logoutIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="filePanelTable:0">&lt;img src=&quot;Images/application_delete.png&quot;&gt;</html>
					</widget>
					<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="logoutLabel" style="topMenuPanelTitle" mouse="this" onClick="this" value="filePanelTable:0" text="{resource:getString($constants,'logout')}"/>
							</widget>
							<widget>
								<label key="logoutDescription" wordwrap="true" style="topMenuPanelDesc" mouse="this" onClick="this" value="filePanelTable:0" text="{resource:getString($constants,'logoutDescription')}"/>
						</widget>
						</panel>
					</row>
				</panel>
			</menuPopupPanel>
			
			<!-- edit panel -->
			<menuPopupPanel key="editPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="editPanelTable" layout="table" style="topMenuPanel" xsi:type="Table" width="100%" padding="0" spacing="0">
				<row style="topMenuPanel">
				<!--icon-->
					<widget valign="middle" style="topMenuPanelIconPanel">
						<html key="cutIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="editPanelTable:0">&lt;img src=&quot;Images/cut_red.png&quot;&gt;</html>
					</widget>
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="cutLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="editPanelTable:0" text="{resource:getString($constants,'cut')}"/>
							</widget>
							
						</panel>
						
					</row>
					<row style="topMenuPanel">
					<!--icon-->
					<widget valign="middle" style="topMenuPanelIconPanel">
						<html key="copyIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="editPanelTable:1">&lt;img src=&quot;Images/page_white_copy.png&quot;&gt;</html>
					</widget>
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="copyLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="editPanelTable:1" text="{resource:getString($constants,'copy')}"/>
							</widget>
							
						</panel>
						
					</row>
				<row style="topMenuPanel">
				<!--icon-->
					<widget valign="middle" style="topMenuPanelIconPanel">
						<html key="pasteIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="editPanelTable:2">&lt;img src=&quot;Images/paste_plain.png&quot;&gt;</html>
					</widget>
					<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="pasteLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="editPanelTable:2" text="{resource:getString($constants,'paste')}"/>
							</widget>
							
						</panel>
						
					</row>
					</panel>
			</menuPopupPanel>
			
			<!-- sample Management panel-->
			<menuPopupPanel key="sampleManagementPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="sampleManagementPanelTable" layout="table" style="topMenuPanel" xsi:type="Table" width="100%" padding="0" spacing="0">
				<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="projectIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="sampleManagementPanelTable:0">&#160;</html>
							</widget>
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="projectLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="sampleManagementPanelTable:0" text="{resource:getString($constants,'project')}"/>
							</widget>
							<widget>
								<label key="projectDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="sampleManagementPanelTable:0" text="{resource:getString($constants,'projectDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="releaseIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="sampleManagementPanelTable:1">&#160;</html>
							</widget>
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="releaseLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="sampleManagementPanelTable:1" text="{resource:getString($constants,'release')}"/>
							</widget>
							<widget>
								<label key="releaseDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="sampleManagementPanelTable:1" text="{resource:getString($constants,'releaseDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="sampleLookupIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="sampleManagementPanelTable:2">&lt;img src=&quot;Images/find.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="sampleLookupLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="sampleManagementPanelTable:2" text="{resource:getString($constants,'sampleLookup')}"/>
							</widget>
							<widget>
								<label key="sampleLookupDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="sampleManagementPanelTable:2" text="{resource:getString($constants,'sampleLookupDescription')}"/>
						</widget>
						</panel>
					</row>
				</panel>
			</menuPopupPanel>
			
			<!-- Analysis Management panel-->
			<menuPopupPanel key="analysisManagementPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="analysisManagmentPanelTable" layout="table" style="topMenuPanel" xsi:type="Table" width="100%" padding="0" spacing="0">
					<row>
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="analyteIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="analysisManagmentPanelTable:0">&#160;</html>
							</widget>
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="analyteLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="analysisManagmentPanelTable:0" text="{resource:getString($constants,'analyte')}"/>
							</widget>
							<widget>
								<label key="analyteDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="analysisManagmentPanelTable:0" text="{resource:getString($constants,'analyteDescription')}"/>
						</widget>
						</panel>
					</row>
					<row>
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="methodIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="analysisManagmentPanelTable:1">&#160;</html>
							</widget>
							<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px"> 
							<widget>
								<label key="methodLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="analysisManagmentPanelTable:1" text="{resource:getString($constants,'method')}"/>
							</widget>
							<widget>
								<label key="methodDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="analysisManagmentPanelTable:1" text="{resource:getString($constants,'methodDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="methodPanelIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="analysisManagmentPanelTable:2">&#160;</html>
							</widget>
							<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="methodPanelLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="analysisManagmentPanelTable:2" text="{resource:getString($constants,'methodPanel')}"/>
							</widget>
							<widget>
								<label key="methodPanelDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="analysisManagmentPanelTable:2" text="{resource:getString($constants,'methodPanelDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="qaEventsIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="analysisManagmentPanelTable:3">&#160;</html>
							</widget>
							<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="qaEventsLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="analysisManagmentPanelTable:3" text="{resource:getString($constants,'qaEvents')}"/>
							</widget>
							<widget>
								<label key="qaEventsDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="analysisManagmentPanelTable:3" text="{resource:getString($constants,'qaEventsDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="resultsIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="analysisManagmentPanelTable:4">&lt;img src=&quot;Images/map.png&quot;&gt;</html>
							</widget>
							<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="resultsLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="analysisManagmentPanelTable:4" text="{resource:getString($constants,'results')}"/>
							</widget>
							<widget>
								<label key="resultsDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="analysisManagmentPanelTable:4" text="{resource:getString($constants,'resultsDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
					<widget valign="middle" halign="center" colspan="3">
						<html value="dataEntryPanelTable:5">&lt;center&gt;&lt;img width=100% height=1px src=&quot;Images/menuspacer.png&quot;&gt;&lt;/center&gt;</html>
					</widget>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="testManagementIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="analysisManagmentPanelTable:6">&#160;</html>
							</widget>
							<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="testManagementLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="analysisManagmentPanelTable:6" text="{resource:getString($constants,'testManagement')}"/>
							</widget>
							<widget>
								<label key="testManagementDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="analysisManagmentPanelTable:6" text="{resource:getString($constants,'testManagementDescription')}"/>
						</widget>
						</panel>
					</row>
					<row>
					<!--icon-->
					<widget valign="middle" style="topMenuPanelIconPanel">
						<html value="dataEntryPanelTable:5">&lt;center&gt;&lt;img width=1px height=1px src=&quot;Images/1x1-trans.gif&quot;&gt;&lt;/center&gt;</html>
					</widget>
					<panel layout="horizontal" xsi:type="Panel" width="100%" height="100%"/>
					<widget valign="middle" halign="center">
						<html value="dataEntryPanelTable:5">&lt;center&gt;&lt;img width=100% height=1px src=&quot;Images/menuspacer.png&quot;&gt;&lt;/center&gt;</html>
					</widget>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="testTrailerIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="analysisManagmentPanelTable:8">&#160;</html>
							</widget>
							<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="testTrailerLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="analysisManagmentPanelTable:8" text="{resource:getString($constants,'testTrailer')}"/>
							</widget>
							<widget>
								<label key="testTrailerDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="analysisManagmentPanelTable:8" text="{resource:getString($constants,'testTrailerDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="worksheetsIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="analysisManagmentPanelTable:9">&lt;img src=&quot;Images/report.png&quot;&gt;</html>
							</widget>
							<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="worksheetsLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="analysisManagmentPanelTable:9" text="{resource:getString($constants,'worksheets')}"/>
							</widget>
							<widget>
								<label key="worksheetsDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="analysisManagmentPanelTable:9" text="{resource:getString($constants,'worksheetsDescription')}"/>
						</widget>
						</panel>
					</row>
				</panel>
			</menuPopupPanel>
			
			<!-- supply Management panel-->
			<menuPopupPanel key="supplyManagementPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="supplyManagementPanelTable" layout="table" style="topMenuPanel" xsi:type="Table" width="100%" padding="0" spacing="0">
				<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="instrumentIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="supplyManagementPanelTable:0">&#160;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="instrumentLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="supplyManagementPanelTable:0" text="{resource:getString($constants,'instrument')}"/>
							</widget>
							<widget>
								<label key="instrumentDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="supplyManagementPanelTable:0" text="{resource:getString($constants,'instrumentDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="inventoryIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="supplyManagementPanelTable:1">&#160;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="inventoryLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="supplyManagementPanelTable:1" text="{resource:getString($constants,'inventory')}"/>
							</widget>
							<widget>
								<label key="inventoryDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="supplyManagementPanelTable:1" text="{resource:getString($constants,'inventoryDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="orderIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="supplyManagementPanelTable:2">&lt;img src=&quot;Images/cart.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="orderLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="supplyManagementPanelTable:2" text="{resource:getString($constants,'order')}"/>
							</widget>
							<widget>
								<label key="orderDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="supplyManagementPanelTable:2" text="{resource:getString($constants,'orderDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="storageIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="supplyManagementPanelTable:3">&lt;img src=&quot;Images/box.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="storageLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="supplyManagementPanelTable:3" text="{resource:getString($constants,'storage')}"/>
							</widget>
							<widget>
								<label key="storageDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="supplyManagementPanelTable:3" text="{resource:getString($constants,'storageDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="storageUnitIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="supplyManagementPanelTable:4">&#160;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="storageUnitLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="supplyManagementPanelTable:4" text="{resource:getString($constants,'storageUnit')}"/>
							</widget>
							<widget>
								<label key="storageUnitDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="supplyManagementPanelTable:4" text="{resource:getString($constants,'storageUnitDescription')}"/>
						</widget>
						</panel>
					</row>
				</panel>
			</menuPopupPanel>
			
			<!-- data entry panel-->
			<menuPopupPanel key="dataEntryPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="dataEntryPanelTable" layout="table" style="topMenuPanel" xsi:type="Table" width="100%" padding="0" spacing="0">
				<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="fastSampleLoginIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="dataEntryPanelTable:0">&#160;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="fastSampleLoginLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="dataEntryPanelTable:0" text="{resource:getString($constants,'fastSampleLogin')}"/>
							</widget>
							<widget>
								<label key="fastSampleLoginDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="dataEntryPanelTable:0" text="{resource:getString($constants,'fastSampleLoginDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="organizationIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="dataEntryPanelTable:1">&lt;img src=&quot;Images/house.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="organizationLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="dataEntryPanelTable:1" text="{resource:getString($constants,'organization')}"/>
							</widget>
							<widget>
								<label key="organizationDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="dataEntryPanelTable:1" text="{resource:getString($constants,'organizationDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="patientIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="dataEntryPanelTable:2">&lt;img src=&quot;Images/user_red.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="patientLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="dataEntryPanelTable:2" text="{resource:getString($constants,'patient')}"/>
							</widget>
							<widget>
								<label key="patientDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="dataEntryPanelTable:2" text="{resource:getString($constants,'patientDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="personIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="dataEntryPanelTable:3">&lt;img src=&quot;Images/user.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="personLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="dataEntryPanelTable:3" text="{resource:getString($constants,'person')}"/>
							</widget>
							<widget>
								<label key="personDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="dataEntryPanelTable:3" text="{resource:getString($constants,'personDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="providerIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="dataEntryPanelTable:4">&lt;img src=&quot;Images/user_suit.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="providerLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="dataEntryPanelTable:4" text="{resource:getString($constants,'provider')}"/>
							</widget>
							<widget>
								<label key="providerDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="dataEntryPanelTable:4" text="{resource:getString($constants,'providerDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="sampleLoginIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="dataEntryPanelTable:5">&#160;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="sampleLoginLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="dataEntryPanelTable:5" text="{resource:getString($constants,'sampleLogin')}"/>
							</widget>
							<widget>
								<label key="sampleLoginDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="dataEntryPanelTable:5" text="{resource:getString($constants,'sampleLoginDescription')}"/>
						</widget>
						</panel>
					</row>
				</panel>
			</menuPopupPanel>
			
			<!--reports panel -->
			<menuPopupPanel key="reportsPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="reportsPanelTable" style="topMenuPanel" layout="table" xsi:type="Table" spacing="0" padding="0" width="100%">
					<row style="topMenuPanel">
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle">
							<widget>
								<label key="category1" style="topMenuPanelTitle" onClick="this" mouse="this" value="reportsPanelTable:0" text="{resource:getString($constants,'category1')}"/>
							</widget>
							<widget>
								<label key="category1Description" wordwrap="true" style="topMenuPanelDesc" onClick="this" mouse="this" value="reportsPanelTable:0" text="{resource:getString($constants,'category1Description')}"/>
							</widget>
						</panel>
						<!-- arrow text  -->
						<panel layout="vertical" xsi:type="Panel" width="20px">
							<widget valign="middle">
								<html key="category1Arrow" onclick="this" mouse="this" value="reportsPanelTable:0">&lt;img src=&quot;Images/resultset_next.png&quot;&gt;</html>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle">
							<widget>
								<label key="category2" onClick="this" style="topMenuPanelTitle" mouse="this" value="reportsPanelTable:1" text="{resource:getString($constants,'category2')}"/>
							</widget>
							<widget>
								<label key="category2Description" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="reportsPanelTable:1" text="{resource:getString($constants,'category2Description')}"/>
							</widget>
						</panel>
						<!-- arrow text  -->
						<panel layout="vertical" xsi:type="Panel" width="20px">
							<widget valign="middle">
								<html key="category2Arrow" onclick="this" mouse="this" value="reportsPanelTable:1">&lt;img src=&quot;Images/resultset_next.png&quot;&gt;</html>
							</widget>
						</panel>
					</row>
			<row style="topMenuPanel">
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle">
							<widget>
								<label key="category3" onClick="this" style="topMenuPanelTitle" mouse="this" value="reportsPanelTable:2" text="{resource:getString($constants,'category3')}"/>
							</widget>
							<widget>
								<label key="category3Description" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="reportsPanelTable:2" text="{resource:getString($constants,'category3Description')}"/>
							</widget>
						</panel>
						<!-- arrow text  -->
						<panel layout="vertical" xsi:type="Panel" width="20px">
							<widget valign="middle">
								<html key="category3Arrow" onclick="this" mouse="this" value="reportsPanelTable:2">&lt;img src=&quot;Images/resultset_next.png&quot;&gt;</html>
							</widget>
						</panel>
					</row>
					</panel>
			</menuPopupPanel>
			
			<!--reports category subpanel 1-->
			<menuPopupPanel key="reports1SubPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="reports1SubPanelTable" style="topMenuPanel" layout="table" xsi:type="Table" spacing="0" padding="0" width="100%">
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html onclick="this" mouse="this" style="topMenuPanelIcon" value="reports1SubPanelTable:0">&lt;img src=&quot;Images/report.png&quot;&gt;</html>
							</widget>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="report1" style="topMenuPanelTitle" mouse="this" value="reports1SubPanelTable:0" text="{resource:getString($constants,'report1')}"/>
							</widget>
							<widget>
								<label key="report1Description" wordwrap="true" style="topMenuPanelDesc" mouse="this" value="reports1SubPanelTable:0" text="{resource:getString($constants,'report1Description')}"/>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html onclick="this" mouse="this" style="topMenuPanelIcon" value="reports1SubPanelTable:1">&lt;img src=&quot;Images/report.png&quot;&gt;</html>
							</widget>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="report2" style="topMenuPanelTitle" mouse="this" value="reports1SubPanelTable:1" text="{resource:getString($constants,'report2')}"/>
							</widget>
							<widget>
								<label key="report2Description" wordwrap="true" style="topMenuPanelDesc" mouse="this" value="reports1SubPanelTable:1" text="{resource:getString($constants,'report2Description')}"/>
							</widget>
						</panel>
					</row>
					</panel>
			</menuPopupPanel>
			
			<!-- reports subpanel 2 -->
			<menuPopupPanel key="reports2SubPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="reports2SubPanelTable" style="topMenuPanel" layout="table" xsi:type="Table" width="100%" padding="0" spacing="0">
				<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" halign="center" style="topMenuPanelIconPanel">
								<html onclick="this" mouse="this" style="topMenuPanelIcon" value="reports2SubPanelTable:0">&lt;img src=&quot;Images/report.png&quot;&gt;</html>
							</widget>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="report1" style="topMenuPanelTitle" mouse="this" value="reports2SubPanelTable:0" text="{resource:getString($constants,'report1')}"/>
							</widget>
							
						</panel>
						
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html onclick="this" mouse="this" style="topMenuPanelIcon" value="reports2SubPanelTable:1">&lt;img src=&quot;Images/report.png&quot;&gt;</html>
							</widget>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="report2" style="topMenuPanelTitle" mouse="this" value="reports2SubPanelTable:1" text="{resource:getString($constants,'report2')}"/>
							</widget>
							
						</panel>
						
					</row>
				<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html onclick="this" mouse="this" style="topMenuPanelIcon" value="reports2SubPanelTable:2">&lt;img src=&quot;Images/report.png&quot;&gt;</html>
							</widget>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="report3" style="topMenuPanelTitle" mouse="this" value="reports2SubPanelTable:2" text="{resource:getString($constants,'report3')}"/>
							</widget>
							
						</panel>
						
					</row>
					</panel>
			</menuPopupPanel>
		
			<!-- utilities panel-->
			<menuPopupPanel key="utilitiesPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="utilitiesPanelTable" layout="table" style="topMenuPanel" xsi:type="Table" width="100%" padding="0" spacing="0">
				<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="auxiliaryIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="utilitiesPanelTable:0">&#160;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="auxiliaryLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="utilitiesPanelTable:0" text="{resource:getString($constants,'auxiliary')}"/>
							</widget>
							<widget>
								<label key="auxiliaryDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="utilitiesPanelTable:0" text="{resource:getString($constants,'auxiliaryDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="dictionaryIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="utilitiesPanelTable:1">&lt;img src=&quot;Images/book.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="dictionaryLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="utilitiesPanelTable:1" text="{resource:getString($constants,'dictionary')}"/>
							</widget>
							<widget>
								<label key="dictionaryDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="utilitiesPanelTable:1" text="{resource:getString($constants,'dictionaryDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="labelIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="utilitiesPanelTable:2">&#160;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="labelLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="utilitiesPanelTable:2" text="{resource:getString($constants,'label')}"/>
							</widget>
							<widget>
								<label key="labelDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="utilitiesPanelTable:2" text="{resource:getString($constants,'labelDescription')}"/>
						</widget>
						</panel>
					</row>
									<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="referenceTableIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="utilitiesPanelTable:3">&lt;img src=&quot;Images/table.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="referenceTableLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="utilitiesPanelTable:3" text="{resource:getString($constants,'referenceTable')}"/>
							</widget>
							<widget>
								<label key="referenceTableDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="utilitiesPanelTable:3" text="{resource:getString($constants,'referenceTableDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="scriptletIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="utilitiesPanelTable:4">&lt;img src=&quot;Images/script.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="scriptletLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="utilitiesPanelTable:4" text="{resource:getString($constants,'scriptlet')}"/>
							</widget>
							<widget>
								<label key="scriptletDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="utilitiesPanelTable:4" text="{resource:getString($constants,'scriptletDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="sectionIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="utilitiesPanelTable:5">&#160;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="sectionLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="utilitiesPanelTable:5" text="{resource:getString($constants,'section')}"/>
							</widget>
							<widget>
								<label key="sectionDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="utilitiesPanelTable:5" text="{resource:getString($constants,'sectionDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="standardNoteIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="utilitiesPanelTable:6">&lt;img src=&quot;Images/note.png&quot;&gt;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="standardNoteLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="utilitiesPanelTable:6" text="{resource:getString($constants,'standardNote')}"/>
							</widget>
							<widget>
								<label key="standardNoteDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="utilitiesPanelTable:6" text="{resource:getString($constants,'standardNoteDescription')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="systemVariableIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="utilitiesPanelTable:7">&#160;</html>
							</widget>
						<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="systemVariableLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="utilitiesPanelTable:7" text="{resource:getString($constants,'systemVariable')}"/>
							</widget>
							<widget>
								<label key="systemVariableDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="utilitiesPanelTable:7" text="{resource:getString($constants,'systemVariableDescription')}"/>
						</widget>
						</panel>
					</row>
				</panel>
			</menuPopupPanel>
			
			<!-- favorites panel -->
			<menuPopupPanel key="favoritesPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="favoritesPanelTable" layout="table" style="topMenuPanel" xsi:type="Table" padding="0" spacing="0" width="100%">
				<row style="topMenuPanel">
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="125px">
							<widget>
								<label key="favTopDictionary" style="topMenuPanelTitle" onClick="this" mouse="this" value="favoritesPanelTable:0" text="{resource:getString($constants,'dictionary')}"/>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="125px">
							<widget>
								<label key="favTopOrganization" style="topMenuPanelTitle" onClick="this" mouse="this" value="favoritesPanelTable:1" text="{resource:getString($constants,'organization')}"/>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="125px">
							<widget>
								<label key="favTopOrganizeFavorites" style="topMenuPanelTitle" onClick="this" mouse="this" value="favoritesPanelTable:2" text="{resource:getString($constants,'organizeFavorites')}"/>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="125px">
							<widget>
								<label key="favTopProvider" style="topMenuPanelTitle" onClick="this" mouse="this" value="favoritesPanelTable:3" text="{resource:getString($constants,'provider')}"/>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="125px">
							<widget>
								<label key="favTopqaEvents" style="topMenuPanelTitle" onClick="this" mouse="this" value="favoritesPanelTable:4" text="{resource:getString($constants,'qaEvents')}"/>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="125px">
							<widget>
								<label key="favTopStandardNote" style="topMenuPanelTitle" onClick="this" mouse="this" value="favoritesPanelTable:5" text="{resource:getString($constants,'standardNote')}"/>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="125px">
							<widget>
								<label key="favTopStorage" style="topMenuPanelTitle" onClick="this" mouse="this" value="favoritesPanelTable:6" text="{resource:getString($constants,'storage')}"/>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
						<!--  spacer -->
						<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="125px">
							<widget>
								<label key="favTopStorageUnit" style="topMenuPanelTitle" onClick="this" mouse="this" value="favoritesPanelTable:7" text="{resource:getString($constants,'storageUnit')}"/>
							</widget>
						</panel>
					</row>
				</panel>
			</menuPopupPanel>
			
			<!-- window panel -->
			<menuPopupPanel key="windowPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="windowPanelTable" layout="table" style="topMenuPanel" xsi:type="Table" padding="0" spacing="0" width="100%">
				<row style="topMenuPanel">
							<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle">
						<widget>
						<check key="showLeftMenu" onClick="this" style="topMenuPanelTitle" value="windowPanelTable:0" mouse="this"><xsl:value-of select='resource:getString($constants,"viewFavoritesMenu")'/></check>
						</widget>
							<widget>
								<label key="showLeftMenuDescription" wordwrap="true" style="topMenuPanelDesc" value="windowPanelTable:0" mouse="this" text="{resource:getString($constants,'viewFavoritesMenuDescription')}"/>
							</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
							<widget colspan="2">
								<html onclick="this" mouse="this" value="analysisManagmentPanelTable:0">&lt;center&gt;&lt;img width=100% height=1px src=&quot;Images/menuspacer.png&quot;&gt;&lt;/center&gt;</html>
							</widget>
					</row>
					<row style="topMenuPanel">
							<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle">
						<widget>
						<label key="closeCurrentWindowLabel" onClick="this" style="topMenuPanelTitle" value="windowPanelTable:2" mouse="this" text="{resource:getString($constants,'closeCurrentWindow')}"/>
						</widget>
						</panel>
					</row>
					<row style="topMenuPanel">
							<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle">
						<widget>
						<label key="closeAllWindowsLabel" onClick="this" style="topMenuPanelTitle" value="windowPanelTable:3" mouse="this" text="{resource:getString($constants,'closeAllWindows')}"/>
						</widget>
						</panel>
					</row>
					</panel>
			</menuPopupPanel>
			
			<!-- help panel-->
			<menuPopupPanel key="helpPanel" autoHide="true" hidden="true" width="200px" popup="this">
				<panel key="helpPanelTable" layout="table" style="topMenuPanel" xsi:type="Table" padding="0" spacing="0" width="100%">
					<row style="topMenuPanel">
						<!--icon-->
							<widget valign="middle" style="topMenuPanelIconPanel">
								<html key="indexIcon" onclick="this" mouse="this" style="topMenuPanelIcon" value="helpPanelTable:0">&lt;img src=&quot;Images/help.png&quot;&gt;</html>
							</widget>
							<!--  spacer -->
					<panel layout="horizontal" width="3px" xsi:type="Panel">
						</panel>
						<!-- title and description -->
						<panel layout="vertical" xsi:type="Panel" style="topMenuPanelItemMiddle" width="180px">
							<widget>
								<label key="indexLabel" onClick="this" style="topMenuPanelTitle" mouse="this" value="helpPanelTable:0" text="{resource:getString($constants,'index')}"/>
							</widget>
							<widget>
								<label key="indexDescription" wordwrap="true" onClick="this" style="topMenuPanelDesc" mouse="this" value="helpPanelTable:0" text="{resource:getString($constants,'indexDescription')}"/>
							</widget>
						</panel>
					</row>
				</panel>
			</menuPopupPanel>
			
			
	</display>
	<rpc key="display"/>
	<rpc key="query"/>
</screen>
  </xsl:template>
</xsl:stylesheet>