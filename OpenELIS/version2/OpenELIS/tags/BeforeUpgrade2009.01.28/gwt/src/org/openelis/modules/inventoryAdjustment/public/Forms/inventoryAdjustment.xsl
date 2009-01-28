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
                xmlns:meta="xalan://org.openelis.metamap.InventoryAdjustmentMetaMap" 
                xmlns:transAdjustmentLocationMeta="xalan://org.openelis.metamap.TransAdjustmentLocationMetaMap" 
                xmlns:inventoryLocationMeta="xalan://org.openelis.metamap.InventoryLocationMetaMap" 
                xmlns:inventoryItemMeta="xalan://org.openelis.meta.InventoryItemMeta" 
                xmlns:dictionaryMeta="xalan://org.openelis.meta.DictionaryMeta" 
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryAdjustmentMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="transAdjustmentLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TransAdjustmentLocationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryLocationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMeta"/>
  </xalan:component>
  
  <xalan:component prefix="dictionaryMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.DictionaryMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
  <xsl:variable name="adj" select="meta:new()"/>
  <xsl:variable name="transAdjustmentLocation" select="meta:getTransAdjustmentLocation($adj)"/>
   <xsl:variable name="loc" select="transAdjustmentLocationMeta:getInventoryLocation($transAdjustmentLocation)"/>
   <xsl:variable name="invItem" select="inventoryLocationMeta:getInventoryItem($loc)"/>
   <xsl:variable name="dict" select="meta:getDictionary($adj)"/>
   <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
   <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
   <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Organization" name="{resource:getString($constants,'inventoryAdjustment')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<VerticalPanel spacing="0" padding="0">
		<!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer">
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton">
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
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="addButton">
    				<xsl:with-param name="language">
    					<xsl:value-of select="language"/>
    				</xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="updateButton">
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
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"adjustmentNum")'/>:</text>
						<textbox case="mixed" key="{meta:getId($adj)}" width="75px" max="20" tab="{meta:getDescription($adj)},{inventoryItemMeta:getStoreId($invItem)}"/>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
						<widget colspan="3">
							<textbox case="mixed" key="{meta:getDescription($adj)}" width="414px" max="60" tab="{meta:getAdjustmentDate($adj)},{meta:getId($adj)}"/>
						</widget>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"adjDate")'/>:</text>
						<calendar key="{meta:getAdjustmentDate($adj)}" begin="0" end="2" width="72px" tab="{meta:getSystemUserId($adj)},{meta:getDescription($adj)}"/>							
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"user")'/>:</text>
						<textbox case="mixed" key="{meta:getSystemUserId($adj)}" width="125px" tab="{inventoryItemMeta:getStoreId($invItem)},{meta:getAdjustmentDate($adj)}"/>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"store")'/>:</text>
						<dropdown key="{inventoryItemMeta:getStoreId($invItem)}" case="mixed" width="205px" tab="{meta:getId($adj)},{meta:getSystemUserId($adj)}"/>
					</row>
				</TablePanel>
				<VerticalPanel spacing="0" padding="0">
					<widget valign="top">
						<table width="auto" key="adjustmentsTable" manager="this" maxRows="14" title="" showError="false" showScroll="ALWAYS">
							<headers><xsl:value-of select='resource:getString($constants,"locationNum")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
							<xsl:value-of select='resource:getString($constants,"storageLocation")'/>, <xsl:value-of select='resource:getString($constants,"onHand")'/>, 
							<xsl:value-of select='resource:getString($constants,"physCount")'/>, <xsl:value-of select='resource:getString($constants,"adjQuan")'/></headers>
							<widths>55,205,225,65,65,65</widths>										
							<editors>
								<textbox case="mixed"/>
								<autoComplete cat="inventoryItem" autoCall="this" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService" width="189px">												
									<headers>Name,Store,Location,Lot #,Exp Date,Qty</headers>
							        <widths>130,110,160,70,70,30</widths>
								</autoComplete>
								<label/>
								<label/>
								<textbox case="mixed"/>
								<label/>
							</editors>
							<fields>
								<number key="{inventoryLocationMeta:getId($loc)}" type="integer" required="true"/>
      							<dropdown key="{inventoryItemMeta:getName($invItem)}" required="true"/>
      							<string required="false"/>
      							<number key="{inventoryLocationMeta:getQuantityOnhand($loc)}" type="integer" required="false"/>
      							<number key="{transAdjustmentLocationMeta:getPhysicalCount($transAdjustmentLocation)}" type="integer" required="true"/>
      							<number key="{transAdjustmentLocationMeta:getQuantity($transAdjustmentLocation)}" type="integer" required="false"/>
							</fields>
							<sorts>false,false,false,false,false,false</sorts>
							<filters>false,false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left,left</colAligns>
						</table>
						<query>
							<queryTable width="auto" title="" maxRows="14" showError="false" showScroll="ALWAYS">
								<headers><xsl:value-of select='resource:getString($constants,"locationNum")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
							<xsl:value-of select='resource:getString($constants,"storageLocation")'/>, <xsl:value-of select='resource:getString($constants,"onHand")'/>, 
							<xsl:value-of select='resource:getString($constants,"physCount")'/>, <xsl:value-of select='resource:getString($constants,"adjQuan")'/></headers>
								<widths>55,205,225,65,65,65</widths>										
								<editors>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<label/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>		 	
								</editors>
								<fields><xsl:value-of select='inventoryLocationMeta:getId($loc)'/>,<xsl:value-of select='inventoryItemMeta:getName($invItem)'/>,label1,
								<xsl:value-of select='inventoryLocationMeta:getQuantityOnhand($loc)'/>,<xsl:value-of select='transAdjustmentLocationMeta:getPhysicalCount($transAdjustmentLocation)'/>,
								<xsl:value-of select='transAdjustmentLocationMeta:getQuantity($transAdjustmentLocation)'/></fields>
							</queryTable>
							</query>
						</widget>
						<widget style="WhiteContentPanel" halign="center">									
							<appButton action="removeRow" onclick="this" style="Button" key="removeRowButton">
								<HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
					              </HorizontalPanel>
						            </appButton>
					            </widget>
							</VerticalPanel>
				</VerticalPanel>								
			</VerticalPanel>				
	</display>
	<rpc key="display">
  	  <number key="{meta:getId($adj)}" type="integer" required="false"/>
      <string key="{meta:getDescription($adj)}" max="60" required="true"/>
	  <date key="{meta:getAdjustmentDate($adj)}" begin="0" end="2" required="true"/>
      <string key="{meta:getSystemUserId($adj)}" required="true"/>
      <dropdown key="{inventoryItemMeta:getStoreId($invItem)}" required="true"/>
      <table key="adjustmentsTable"/>
      
      <number key="systemUserId" type="integer" required="false"/>
	</rpc>
	<rpc key="query">
  	  <queryNumber key="{meta:getId($adj)}" type="integer"/>
      <queryString key="{meta:getDescription($adj)}"/>
      <queryDate key="{meta:getAdjustmentDate($adj)}" begin="0" end="2"/>
      <queryString key="{meta:getSystemUserId($adj)}"/>
      <dropdown key="{inventoryItemMeta:getStoreId($invItem)}"/>
      
      <table key="adjustmentsTable"/>
      <queryNumber key="{inventoryLocationMeta:getId($loc)}" type="integer"/>
      <queryString key="{inventoryItemMeta:getName($invItem)}"/>
      <queryString key="label1"/>
      <queryNumber key="{inventoryLocationMeta:getQuantityOnhand($loc)}" type="integer"/>
      <queryNumber key="{transAdjustmentLocationMeta:getPhysicalCount($transAdjustmentLocation)}" type="integer"/>
      <queryNumber key="{transAdjustmentLocationMeta:getQuantity($transAdjustmentLocation)}" type="integer"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
