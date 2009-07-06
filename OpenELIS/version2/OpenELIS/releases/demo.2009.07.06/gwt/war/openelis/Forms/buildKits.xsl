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
                xmlns:meta="xalan://org.openelis.metamap.InventoryItemMetaMap"
                xmlns:locationMeta="xalan://org.openelis.metamap.InventoryLocationMetaMap"
                xmlns:storageLocationMeta="xalan://org.openelis.metamap.StorageLocationMetaMap"                
                xmlns:inventoryComponentMeta="xalan://org.openelis.metamap.InventoryComponentMetaMap" 
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryItemMetaMap"/>
  </xalan:component>

  <xalan:component prefix="locationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryLocationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="storageLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.StorageLocationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryComponentMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryComponentMetaMap"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
     <xsl:variable name="invItem" select="meta:new()"/>
     <xsl:variable name="location" select="meta:getInventoryLocation($invItem)"/>
     <xsl:variable name="locStorageLoc" select="locationMeta:getStorageLocation($location)"/>
     <xsl:variable name="component" select="meta:getInventoryComponent($invItem)"/>
     <xsl:variable name="componentItem" select="inventoryComponentMeta:getInventoryItem($component)"/>
     <xsl:variable name="componentLocation" select="meta:getInventoryLocation($componentItem)"/>
     <xsl:variable name="componentLocStorageLoc" select="locationMeta:getStorageLocation($componentLocation)"/>
     <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
     <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
   <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="BuildKits" name="{resource:getString($constants,'buildKits')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<VerticalPanel spacing="0" padding="0">
		<!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer">
    			<buttonPanel key="buttons">
    			<xsl:call-template name="addButton">
    				<xsl:with-param name="language">
    					<xsl:value-of select="language"/>
    				</xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="commitButton">
    				<xsl:with-param name="language">
    					<xsl:value-of select="language"/>
    				</xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="abortButton">
    				<xsl:with-param name="language">
    					<xsl:value-of select="language"/>
    				</xsl:with-param>
    			</xsl:call-template>
				</buttonPanel>
		</AbsolutePanel>
		<!--end button panel-->
			<VerticalPanel style="WhiteContentPanel" spacing="0" padding="0">
				<TablePanel style="Form">
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"kit")'/>:</text>
						<autoComplete key="{meta:getName($invItem)}" cat="kitDropdown" onchange="this" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.buildKits.server.BuildKitsService" width="190px" tab="numRequested,{locationMeta:getExpirationDate($location)}">
							<headers>Name,Store,Dispensed Units</headers>
							<widths>135,130,110</widths>
						</autoComplete>
						<widget colspan="2">
						<check key="addToExisting" onClick="this" tab="{storageLocationMeta:getLocation($locStorageLoc)},numRequested"><text style="CheckboxPrompt"><xsl:value-of select='resource:getString($constants,"addToExisting")'/></text></check>
						</widget>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"numRequested")'/>:</text>
						<textbox case="mixed" key="numRequested" width="50px" max="20" onchange="this" tab="addToExisting,{meta:getName($invItem)}"/>
						<text style="Prompt">Location:</text>
						<autoComplete key="{storageLocationMeta:getLocation($locStorageLoc)}" cat="invLocation" autoCall="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.buildKits.server.BuildKitsService" case="mixed" width="160px" tab="{locationMeta:getLotNumber($location)},addToExisting">
							<headers>Desc</headers>
							<widths>300</widths>
						</autoComplete>
					</row>
					<row>
						<widget colspan="2">
							<HorizontalPanel/>
						</widget>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"lotNum")'/>:</text>
						<textbox case="mixed" key="{locationMeta:getLotNumber($location)}" width="100px" max="30" tab="{locationMeta:getExpirationDate($location)},{storageLocationMeta:getLocation($locStorageLoc)}"/>
					</row>
					<row>
						<widget colspan="2">
							<HorizontalPanel/>
						</widget>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"expDate")'/>:</text>
						<calendar key="{locationMeta:getExpirationDate($location)}" begin="0" end="2" tab="{meta:getName($invItem)},{locationMeta:getLotNumber($location)}"/>		
					</row>
					</TablePanel>
					<TablePanel>
					<row>
					<widget colspan="4">
						<table width="auto" key="subItemsTable" manager="this" maxRows="10" title="" showError="false" showScroll="ALWAYS">
							<headers>Kit Component Name,Location,Lot #,Unit,Total,On Hand</headers>
							<widths>160,177,80,60,60,60</widths>										
							<editors>
								<label/>
								<autoComplete cat="componentLocation" autoCall="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.buildKits.server.BuildKitsService" case="mixed" width="150px">
									<headers>Desc,Lot #,Qty</headers>
									<widths>300,65,30</widths>
								</autoComplete>
								<label/>
								<label/>
								<textbox/>
								<label/>
							</editors>
							<fields>
								<string key="{inventoryComponentMeta:getComponentId($component)}" required="true"/>
      							<dropdown key="{storageLocationMeta:getLocation($componentLocStorageLoc)}" required="true"/>
      							<string key="{locationMeta:getLotNumber($componentLocation)}" required="false"/>
      							<double key="{inventoryComponentMeta:getQuantity($component)}" required="true"/>
      							<integer key="total" required="true"/>
      							<integer key="{locationMeta:getQuantityOnhand($componentLocation)}" required="true"/>
							</fields>
							<sorts>false,false,false,false,false,false</sorts>
							<filters>false,false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left,left</colAligns>
						</table>
					</widget>
					</row>
					<row>
					<widget colspan="4" align="right">
						<appButton action="transfer" key="transferButton" onclick="this" style="Button">
							<HorizontalPanel>
								<AbsolutePanel style="RemoveRowButtonImage"/>
								<text>Transfer</text>
							</HorizontalPanel>
						</appButton>
					</widget>
					</row>
				</TablePanel>			
			</VerticalPanel>
		</VerticalPanel>
	</display>
	<rpc key="display">
  	  <check key="addToExisting" required="false"/>
  	  <dropdown key="{meta:getName($invItem)}" required="true"/>
  	  <integer key="numRequested" type="integer" required="true"/>
  	  <dropdown key="{storageLocationMeta:getLocation($locStorageLoc)}" required="true"/>
  	  <string key="{locationMeta:getLotNumber($location)}" required="false"/>
	  <date key="{locationMeta:getExpirationDate($location)}" begin="0" end="2" required="false"/>
  	  
  	  <table key="subItemsTable"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
