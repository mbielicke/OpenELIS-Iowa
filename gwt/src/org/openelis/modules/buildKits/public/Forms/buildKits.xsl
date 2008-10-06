<!--
 The contents of this file are subject to the Mozilla Public License
 Version 1.1 (the "License"); you may not use this file except in
 compliance with the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS"
 basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 License for the specific language governing rights and limitations under
 the License.
 
 The Original Code is OpenELIS code.
 
 Copyright (C) The University of Iowa.  All Rights Reserved.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:meta="xalan://org.openelis.metamap.InventoryItemMetaMap"
                xmlns:locationMeta="xalan://org.openelis.metamap.InventoryLocationMetaMap"
                xmlns:storageLocationMeta="xalan://org.openelis.metamap.StorageLocationMetaMap"                
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
  
  <xsl:template match="doc"> 
     <xsl:variable name="invItem" select="meta:new()"/>
     <xsl:variable name="location" select="meta:getInventoryLocation($invItem)"/>
     <xsl:variable name="locStorageLoc" select="locationMeta:getStorageLocation($location)"/>
     <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
     <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
   <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="BuildKits" name="{resource:getString($constants,'buildKits')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel style="WhiteContentPanel" spacing="0" padding="0">
			<VerticalPanel spacing="0">
		<!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer">
    			<buttonPanel key="buttons">
    		<!--	<xsl:call-template name="queryButton">
    				<xsl:with-param name="language">
    					<xsl:value-of select="language"/>
    				</xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="previousButton">
					<xsl:with-param name="language">
						<xsl:value-of select="language"/>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="nextButton">
					<xsl:with-param name="language">
						<xsl:value-of select="language"/>
					</xsl:with-param>
				</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>-->
    			<xsl:call-template name="addButton">
    				<xsl:with-param name="language">
    					<xsl:value-of select="language"/>
    				</xsl:with-param>
    			</xsl:call-template>
    			<!--<xsl:call-template name="updateButton">
	    			<xsl:with-param name="language">
	    				<xsl:value-of select="language"/>
	    			</xsl:with-param>
    			</xsl:call-template>-->
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
			<VerticalPanel>
				<TablePanel style="Form">
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"kit")'/>:</text>
						<widget colspan="3">
						<autoDropdown key="{meta:getName($invItem)}" cat="kitDropdown" onchange="this" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.buildKits.server.BuildKitsService" width="190px" tab="numRequested,{storageLocationMeta:getLocation($locStorageLoc)}">
							<headers>Name,Store,Dispensed Units</headers>
							<widths>135,130,110</widths>
						</autoDropdown>
						</widget>
						<widget colspan="2">
						<check key="addToExisting" onClick="this" tab="{storageLocationMeta:getLocation($locStorageLoc)},numToMake"><text style="CheckboxPrompt"><xsl:value-of select='resource:getString($constants,"addToExisting")'/></text></check>
						</widget>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"numRequested")'/>:</text>
						<textbox case="mixed" key="numRequested" width="50px" max="20" onchange="this" tab="numToMake,{meta:getName($invItem)}"/>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"numToMake")'/>:</text>
						<textbox case="mixed" key="numToMake" width="50px" max="60" alwaysDisabled="true" tab="addToExisting,numRequested"/>
						<text style="Prompt">Location:</text>
						<autoDropdown key="{storageLocationMeta:getLocation($locStorageLoc)}" cat="invLocation" autoCall="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.buildKits.server.BuildKitsService" case="mixed" width="160px" tab="{meta:getName($invItem)},addToExisting">
							<headers>Desc</headers>
							<widths>300</widths>
						</autoDropdown>
					</row>
					</TablePanel>
					<TablePanel>
					<row>
					<widget colspan="4">
						<table width="auto" key="subItemsTable" manager="this" maxRows="10" title="" showError="false" showScroll="true">
							<headers>Kit Component Name,Location,Unit,Total,On Hand</headers>
							<widths>160,177,60,60,60</widths>										
							<editors>
								<label/>
								<autoDropdown cat="componentLocation" autoCall="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.buildKits.server.BuildKitsService" case="mixed" width="150px">
									<headers>Desc</headers>
									<widths>300</widths>
								</autoDropdown>
								<label/>
								<label/>
								<label/>
							</editors>
							<fields>
      							<string required="true"/>
      							<dropdown required="true"/>
      							<number type="double" required="true"/>
      							<number type="integer" required="true"/>
      							<number type="integer" required="true"/>
							</fields>
							<sorts>false,false,false,false,false</sorts>
							<filters>false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left</colAligns>
						</table>
			<!--			<query>
							<queryTable width="auto" title="" maxRows="10" showError="false" showScroll="true">
								<headers>Item,Loc,Unit,Total,On Hand</headers>
								<widths>170,167,60,60,60</widths>
								<editors>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>		 	
								</editors>
								<fields>1,2,3,4,5</fields>
							</queryTable>
							</query>-->
					</widget>
					</row>
				</TablePanel>			
			</VerticalPanel>
		</VerticalPanel>
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	  <check key="addToExisting" required="false"/>
  	  <dropdown key="{meta:getName($invItem)}" required="true"/>
  	  <number key="numRequested" type="integer" required="true"/>
  	  <number key="numToMake" type="integer" required="true"/>
  	  <dropdown key="{storageLocationMeta:getLocation($locStorageLoc)}" required="true"/>
  	  <table key="subItemsTable"/>
	</rpc>
	<!--<rpc key="query">
	<queryString key="1"/>
	<queryString key="2"/>
	<queryString key="3"/>
	<queryString key="4"/>
	<queryString key="5"/>
  	  
	</rpc>-->
</screen>
  </xsl:template>
</xsl:stylesheet>
